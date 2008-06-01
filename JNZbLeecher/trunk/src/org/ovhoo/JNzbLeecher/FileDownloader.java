package org.ovhoo.JNzbLeecher;

import org.ovhoo.io.*;

import java.util.Iterator;
import java.util.concurrent.Semaphore;
import org.ovhoo.nzb.NzbFile;
import org.ovhoo.nzb.NzbSegment;

public class FileDownloader extends Thread{
	
	public static enum statusEnum {SLEEPING, DOWNLOADING , DISABLED,WAITING,  FAILED, DOWNLOADED};
	
	private statusEnum status = statusEnum.DISABLED;
	
	private NzbFile nzbFile;
	private Directory destinationDir;
	private Directory tmpDir;
	
		
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
	}
	
	/**
	 * run the thread
	 */
	public void run(){
		
		//Change status
		this.status = statusEnum.WAITING;
		
		try {
			Property.maxSimuDLSem.acquire();
			//Change status
			this.status = statusEnum.DOWNLOADING;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Download segments
		Iterator<NzbSegment> _iter = this.nzbFile.getSegments().iterator();
		while( _iter.hasNext()){
			NzbSegment _nzbSegment = _iter.next();
			SegmentDownloader _segmentDownloader = new SegmentDownloader(_nzbSegment, this.nzbFile.getGroups(),this.destinationDir, this.tmpDir);
			_segmentDownloader.start();
		}
		
		
		//Normaly the download is finiched
		//Change status
		//System.out.println("Starting thread"+ this.nzbFile.toString());
		
		Property.maxSimuDLSem.release();
		//Change status
		this.status = statusEnum.DOWNLOADED;
	}
	
	public int downloadStatus(){
		return this.nzbFile.downloadStatus();
	}
}
