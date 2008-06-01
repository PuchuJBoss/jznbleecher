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
	
	public static enum statusEnum {SLEEPING, DOWNLOADING , DISABLED,WAITING,  FAILED, DOWNLOADED};
	
	private statusEnum status = statusEnum.DISABLED;
	
	private NzbSegment nzbSegment;
	private Directory destinationDir;
	private Directory tmpDir;
	private ArrayList<String> groups;
	
	public SegmentDownloader(NzbSegment nzbSegment, ArrayList<String> groups, Directory destinationDir, Directory tmpDir ){
		this.nzbSegment = nzbSegment;
		this.destinationDir = destinationDir;
		this.tmpDir = tmpDir;
		this.groups = groups;
	}
	
	public void run(){
		//System.out.println("Starting thread"+ this.nzbSegment.toString());
		//change status
		this.status = statusEnum.WAITING;
		Iterator<NNTPServer> _iter = Property.getServersList().iterator();
		while (_iter.hasNext()){
			NNTPServer _nntpServer = _iter.next();
			
			NNTP _nntp = null;
			try {
				_nntp = _nntpServer.acquire();
				this.status = statusEnum.DOWNLOADING;
				
				//System.out.println(this.groups.get(0));
				//_nntp.group(this.groups.get(0));
				_nntp.body("<"+ this.nzbSegment.getContent() +">", this.destinationDir + File.separator + this.nzbSegment.getContent());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NNTPTemporaryError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				_nntpServer.release(_nntp);
				this.status = statusEnum.FAILED;
				//System.out.println("SegmentDownloader : " + this.status);
				//System.err.println("Temporary error ");
			}
			{
				_nntpServer.release(_nntp);
				this.status = statusEnum.DOWNLOADED;
				
				
			}
		
			System.out.println("SegmentDownloader : "+ this.nzbSegment.toString() +" - "+ this.status);
			
		}
	}
	
}
