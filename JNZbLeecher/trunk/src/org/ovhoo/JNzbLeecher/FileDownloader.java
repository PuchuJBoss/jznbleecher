package org.ovhoo.JNzbLeecher;

import org.ovhoo.io.*;
import org.ovhoo.jNzbLeecherUI.AgentFileDownloader.AgtFileDownloaderC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import org.ovhoo.nzb.NzbFile;
import org.ovhoo.nzb.NzbSegment;
import org.yenc.YDecoder;

public class FileDownloader extends Thread{
	
	public static enum statusEnum {SLEEPING, TESTING,DECODING, FINISHED,DOWNLOADING ,STARTDW, DISABLED,WAITING,  FAILED, DOWNLOADED};
	private final int MAIN_WAIT_TIME = 500;
	
	private statusEnum status = statusEnum.DISABLED;
	private int progression;
	
	private NzbFile nzbFile;
	private Directory destinationDir;
	private Directory tmpDir;
	
	private YDecoder yencDecoder;
	
	private boolean isRunning = true;
	
	private AgtFileDownloaderC representation = null;
	
	//manage segments curently in download
	private ArrayList<SegmentDownloader> dwSegments;
	
	
	
	
		
	/**
	 * Instanciate the download thread
	 * @param nzbFile Structure with full information of file to download
	 * @param destinationDir the directory to store the final files in
	 * @param tmpDir directory used to store the temporary files
	 */
	public FileDownloader(NzbFile nzbFile, Directory destinationDir, Directory tmpDir ){
		this.nzbFile = nzbFile;
		this.destinationDir = destinationDir;
		this.tmpDir = tmpDir;
		this.dwSegments = new ArrayList<SegmentDownloader>();
		
//		Download segments
		Iterator<NzbSegment> _iter = this.nzbFile.getSegments().iterator();
		while( _iter.hasNext()){
			NzbSegment _nzbSegment = _iter.next();
			SegmentDownloader _segmentDownloader = new SegmentDownloader(this, _nzbSegment, this.nzbFile.getGroups(),this.tmpDir);
			//_segmentDownloader.start();
			
			//add to list
			dwSegments.add(_segmentDownloader);
		}
	}
	
	/**
	 * this methode aquire a download slot
	 */
	public void aquire(){
		try {
			Property.maxSimuDLSem.acquire();
			//Change status
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * starting download
	 */
	public void startDownload(){
		//start downloads
		Iterator<SegmentDownloader> _dwIter = dwSegments.iterator();
		while((_dwIter.hasNext()) && (isRunning)){
			SegmentDownloader _segmentDownloader = _dwIter.next();
			_segmentDownloader.start();
		}
	}
	
	/**
	 * wait for download to finish
	 */
	public void downloading(){
		try {
			sleep(MAIN_WAIT_TIME);
			updateStatus();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void decode(){
		System.out.println("FileDownloader:: File successfully downloaded" + this.nzbFile.getSubject()); 
		System.out.println("FileDownloader:: decoding");
		
		//use yenc decoder to decode segments
		Iterator<SegmentDownloader> _dwIter = dwSegments.iterator();
		while(_dwIter.hasNext()){
			SegmentDownloader _segmentDownloader = _dwIter.next();
			String _segmentPath = _segmentDownloader.getSegmentPath();
			try {
				System.err.println(_segmentPath);
				System.out.println(yencDecoder.decode(_segmentPath, destinationDir.getAbsolutePath()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("FileDownloader :: Error while decoding " +_segmentPath);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * run the thread
	 */
	public void run(){
		
		this.status = statusEnum.SLEEPING;
		
		while (this.isRunning){
			switch (this.status){
			
			case SLEEPING : this.status = statusEnum.WAITING;
							break;
							
			case WAITING : this.aquire();
						   this.status = statusEnum.STARTDW;
						   break;
						   
			case STARTDW : 	this.startDownload();
							this.status = statusEnum.DOWNLOADING;
							break;
			
			case DOWNLOADING : this.downloading();
								//status is updated bu the procedure downloading
							   break;
							   
			case DOWNLOADED: this.decode();
							this.status = statusEnum.DECODING;
							break;
			
			case DECODING : this.status = statusEnum.FINISHED;
							break;
			
			case FINISHED : isRunning = false;
							//deleteTempFiles(); 
							break;
			case FAILED : 	isRunning = false;
							//deleteTempFiles(); 
							break;
							
			
							
			case TESTING : break;
			case DISABLED : break;
				
			}
			
			this.updateRepresentation();
			
			try {
				sleep(MAIN_WAIT_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		System.out.println("FileDownloader::run <-");;
		
	}
	
	/**
	 * run the thread
	 */
	public void run1(){
		
		//Change status
		this.status = statusEnum.WAITING;
		this.isRunning = true;
		
		try {
			Property.maxSimuDLSem.acquire();
			//Change status
			this.status = statusEnum.DOWNLOADING;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//start downloads
		Iterator<SegmentDownloader> _dwIter = dwSegments.iterator();
		while((_dwIter.hasNext()) && (isRunning)){
			SegmentDownloader _segmentDownloader = _dwIter.next();
			_segmentDownloader.start();
		}
		
//		exit thread
		if ( isRunning == false ) return;
		
		try {
			sleep(MAIN_WAIT_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while ( getStatus() != statusEnum.DOWNLOADED &&
				getStatus() != statusEnum.FAILED && isRunning){
			try {
				sleep(MAIN_WAIT_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				updateStatus();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//exit thread
		if ( isRunning == false ) return;
		
		//Normaly the download is finiched
		//Change status
		//System.out.println("Starting thread"+ this.nzbFile.toString());
		
		Property.maxSimuDLSem.release();
		//Change status
		//this.status = statusEnum.DOWNLOADED;
		if(this.status == statusEnum.DOWNLOADED){
			System.out.println("FileDownloader:: File successfully downloaded" + this.nzbFile.getSubject()); 
			System.out.println("FileDownloader:: decoding");
			//use yenc decoder to decode segments
			_dwIter = dwSegments.iterator();
			while(_dwIter.hasNext()){
				SegmentDownloader _segmentDownloader = _dwIter.next();
				String _segmentPath = _segmentDownloader.getSegmentPath();
				try {
					System.err.println(_segmentPath);
					yencDecoder.decode(_segmentPath, destinationDir.getAbsolutePath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("FileDownloader :: Error while decoding " +_segmentPath);
					e.printStackTrace();
				}
			}
		}
		else{
			System.out.println("FileDownloader:: File failed to downloaded" + this.nzbFile.getSubject()); 
		}
		
		
		deleteTempFiles();
		System.out.println("FileDownloader::run <-");
	}
	
	public statusEnum getStatus(){		
		return status;
	}
	
	public void setStatus( statusEnum status){
		this.status = status;
	}
	
	public void updateStatus() throws Exception{
		int _nbSegments = dwSegments.size();

		int _nbSegmentsSleeping = 0;
		int _nbSegmentsTesting = 0;
		int _nbSegmentsDownloading = 0;
		int _nbSegmentsDisabled = 0;
		int _nbSegmentsWaiting = 0;
		int _nbSegmentsFailed = 0;
		int _nbSegmentsDownloaded = 0;

		
		Iterator<SegmentDownloader> _dwIter = dwSegments.iterator();
		while(_dwIter.hasNext()){
			SegmentDownloader _segmentDownloader = _dwIter.next();
			
			if (_segmentDownloader.getStatus() == SegmentDownloader.statusEnum.SLEEPING ){
				_nbSegmentsSleeping ++;
			}
			else if (_segmentDownloader.getStatus() == SegmentDownloader.statusEnum.TESTING ){
				_nbSegmentsTesting ++;
			}
			else if (_segmentDownloader.getStatus() == SegmentDownloader.statusEnum.DOWNLOADING ){
				_nbSegmentsDownloading ++;
			}
			else if (_segmentDownloader.getStatus() == SegmentDownloader.statusEnum.DISABLED ){
				_nbSegmentsDisabled ++;
			}
			else if (_segmentDownloader.getStatus() == SegmentDownloader.statusEnum.WAITING ){
				_nbSegmentsWaiting ++;
			}
			else if (_segmentDownloader.getStatus() == SegmentDownloader.statusEnum.FAILED ){
				_nbSegmentsFailed ++;
			}
			else if (_segmentDownloader.getStatus() == SegmentDownloader.statusEnum.DOWNLOADED ){
				_nbSegmentsDownloaded ++;
			}
			else throw new Exception("Unknown segment state");
			
		}
		
		if ( _nbSegmentsFailed > 0){
			status = statusEnum.FAILED;
		}
		else if ( _nbSegmentsDownloaded == _nbSegments){
			status = statusEnum.DOWNLOADED;
		}
		else if ( _nbSegmentsDownloading > 0){
			status = statusEnum.DOWNLOADING;
		}
		else if ( _nbSegmentsWaiting > 0){
			status = statusEnum.WAITING;
		}
		else if ( _nbSegmentsSleeping > 0){
			status = statusEnum.SLEEPING;
		}
		else if ( _nbSegmentsTesting > 0){
			status = statusEnum.TESTING;
		}
		else if ( _nbSegmentsDisabled > 0){
			status = statusEnum.DISABLED;
		}
		else status = statusEnum.DISABLED;
		
		
		//update progresssion
		this.progression = (( 100 *_nbSegmentsDownloaded)/_nbSegments);
		this.updateRepresentation();
		
		System.out.println("FileDownloader::updateStatus :" + status.toString());
		
		//System.out.println(_nbSegmentsSleeping);
		//System.out.println(_nbSegmentsTesting);
		System.out.println(_nbSegmentsDownloading);
		//System.out.println(_nbSegmentsDisabled);
		//System.out.println(_nbSegmentsWaiting);
		//System.out.println(_nbSegmentsFailed);
		//System.out.println(_nbSegmentsDownloaded);
		
		
		
	}
	
	public int downloadStatus(){
		return this.nzbFile.downloadStatus();
	}
	
	
	public void deleteTempFiles(){

		Iterator<SegmentDownloader> _dwIter = dwSegments.iterator();
		while(_dwIter.hasNext()){
			SegmentDownloader _segmentDownloader = _dwIter.next();
			
			String _filePath = _segmentDownloader.getSegmentPath();
			
			File _tempFile = new File(_filePath);
			
			//Make sure the file or directory exists and isn't write protected
			if (!_tempFile.exists())
				throw new IllegalArgumentException("FileDownloader::deleteTempFiles: no such file or directory: " + _filePath);
	
			if (!_tempFile.canWrite())
				throw new IllegalArgumentException("FileDownloader::deleteTempFiles: write protected: "+ _filePath);
	
			// If it is a directory, make sure it is empty
		    if (_tempFile.isDirectory()) {
		    	throw new IllegalArgumentException("FileDownloader::deleteTempFiles: cannot remove a directory: " + _filePath);
		    }
	
		    // Attempt to delete it
		    boolean _success = _tempFile.delete();
	
		    if (!_success) throw new IllegalArgumentException("FileDownloader::deleteTempFiles: deletion failed");
			
		}
	}
	
	/**
	 * define the graphical representaion of this class
	 * @param representation
	 */
	public void setRepresentation(AgtFileDownloaderC representation){
		this.representation = representation;
	}
	
	/**
	 * update the represntation
	 */
	public void updateRepresentation(){
		if (this.representation != null){
			this.representation.setProgression(this.getProgression());
		}
	}
	
	/**
	 * return the progression as percent
	 * @return between 0 and 100
	 */
	public int getProgression(){
		return this.progression;
	}
}
