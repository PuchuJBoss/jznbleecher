package org.ovhoo.JNzbLeecher;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.ovhoo.JNzbLeecher.FileDownloader.statusEnum;
import org.ovhoo.io.Directory;
import org.ovhoo.nntplib.NNTP;
import org.ovhoo.nntplib.NNTPTemporaryError;
import org.ovhoo.nzb.NzbFile;
import org.ovhoo.nzb.NzbSegment;

public class SegmentDownloader extends Thread{
	
	public static enum statusEnum {SLEEPING, TESTING, DOWNLOADING , DISABLED,WAITING,  FAILED, DOWNLOADED, STARTDW}

	private static final long MAIN_WAIT_TIME = 500;;
	
	private statusEnum status = statusEnum.DISABLED;
	
	private NzbSegment nzbSegment;
	private Directory destinationDir;
	//private Directory tmpDir;
	private ArrayList<String> groups;
	private FileDownloader parent;
	private boolean isRunning = false;
	//private short nbTentatives = 4;
	
	private final int MaxTentatives = 4;

	private Iterator<NNTPServer> serverIterator;

	private int nbTryForCurentServ = 0;
	
	public SegmentDownloader(FileDownloader parent, NzbSegment nzbSegment, ArrayList<String> groups, Directory destinationDir ){
		this.nzbSegment = nzbSegment;
		this.destinationDir = destinationDir;
		//this.tmpDir = tmpDir;
		this.groups = groups;
		this.parent = parent;
	}
	
	public statusEnum getStatus(){
		return this.status;
	}
	
	public String getSegmentPath(){
		return this.destinationDir + File.separator + this.nzbSegment.getContent();
	}
	
	public void download(){
		NNTPServer _nntpServer = null;
		NNTP _nntp = null;
		
		this.nbTryForCurentServ ++;

		if  (this.nbTryForCurentServ < this.MaxTentatives){
			
			if (this.nbTryForCurentServ == 1){
				if (this.serverIterator.hasNext()){
					_nntpServer = this.serverIterator.next();
				}
				else {
					this.status = statusEnum.FAILED;
					return;
				}
			}
		}
		else{
			this.status = statusEnum.FAILED;
			return;
		}
		
		
		
		try {
			_nntp = _nntpServer.acquire();
			this.status = statusEnum.DOWNLOADING;
			
			//TODO gethead before download
			
			//System.out.println(this.groups.get(0));
			//_nntp.group(this.groups.get(0));
			_nntp.body("<"+ this.nzbSegment.getContent() +">", getSegmentPath());
			
			this.status = statusEnum.DOWNLOADED;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NNTPTemporaryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//_nntpServer.release(_nntp);
			//this.status = statusEnum.TESTING;
			//System.out.println("SegmentDownloader : " + this.status);
			//System.err.println("Temporary error ");
		}
		finally {
			_nntpServer.release(_nntp);
		}
	}
	
	public void startDownload(){
		this.serverIterator = Property.getServersList().iterator();
		this.nbTryForCurentServ  = 0;
		
	}
	public void run(){
		this.isRunning = true;
		this.status = statusEnum.WAITING;
		
		//SLEEPING, TESTING,  , DISABLED,  , 
		while (this.isRunning){
			switch (this.status){
			
			case WAITING : 	this.status = statusEnum.STARTDW;
			   				break;
			   				
			case STARTDW : 	this.startDownload();
							this.status = statusEnum.DOWNLOADING;
			
			case DOWNLOADING : 	this.download();
								break;

			
			case DOWNLOADED : 	isRunning = false;
								break;
								
			case FAILED : 	isRunning = false;
							break;
							
			default : break;
			}
			
			try {
				sleep(0);
				System.out.println("SegmentDownloader::Run : Status " + this.status);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
}
