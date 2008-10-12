package org.ovhoo.JNzbLeecher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import org.ovhoo.io.*;
import org.ovhoo.nntplib.NNTP;
import org.ovhoo.nzb.NzbFile;
import org.ovhoo.nzb.NzbParser;
import org.ovhoo.nzb.NzbSegment;

public class NzbLeecher extends Thread{
	
	private RFile nzbFile;
	private Directory destinationDir;
	private Directory tmpDir;
	private NzbParser nzbParser;
	
	
	private static Semaphore runSem = new Semaphore(1);
	private boolean isRunning = false;
	
	//private DLQueue<NzbFile> filesDLQueue = new DLQueue<NzbFile>();
	private DLQueue<FileDownloader> fileDownloaderQueue = new DLQueue<FileDownloader>();
	private ArrayList<FileDownloader> fileDownloaderList= new ArrayList<FileDownloader>();
	
	/**
	 * Instanciate this class giving the nzbFile as File
	 * @param nzbFile The nzb File
	 */
	public NzbLeecher(RFile nzbFile, Directory destinationDir, Directory tmpDir){
		super();
		this.nzbFile = nzbFile;
		this.destinationDir = destinationDir;
		this.tmpDir = tmpDir;
		
		//Initiate the thread
		this.init();
	}
	
	/**
	 * Methode called to initiate this thread
	 */
	public void init(){
		new Property(this.nzbFile);
		
		//Once all servers are listed
		//System.out.println(NNTPServer.getMaxSlots());
		Property.maxSimuDLSem = new Semaphore(NNTPServer.getMaxSlots());
		
		//parse .nzb file
		this.nzbParser = new NzbParser(this.nzbFile.toString());
		
		//build the dl file queue
		Iterator<NzbFile> _iter = this.nzbParser.getFiles().iterator();
		while(_iter.hasNext()){
			NzbFile _nzbFile = _iter.next();
			FileDownloader _fileDownloader = new FileDownloader(_nzbFile,this.destinationDir, this.tmpDir);
			this.fileDownloaderQueue.add(_fileDownloader);
			this.fileDownloaderList.add(_fileDownloader );
		}
		System.out.println("NzbLeecher::init <-");
		
	}
	
	/**
	 * Instanciate this class with the nzbFile complete path
	 * @param nzbFilePath
	 */
	public NzbLeecher(String nzbFilePath, String destinationDir, String tmpDir){
		
		try{
			this.nzbFile = new RFile(nzbFilePath);
			this.destinationDir = new Directory(destinationDir);
			this.tmpDir  = new Directory(tmpDir);
			
		}
		catch (IllegalArgumentException e){
			System.out.println("Error : Initialising the thread NzbLeecher");			
		}
		
		
		
		//Initiate the thread
		this.init();
	}
	

	public void run(){
		
		while( (this.isRunning ) && !this.fileDownloaderQueue.isEmpty() ){
			
			if ( Property.maxSimuDLSem.availablePermits() > 0 ){
				FileDownloader _fileDownloader = this.fileDownloaderQueue.poll();
				_fileDownloader.start();
			}
			
		}
		
		//exit thread
		if ( isRunning == false ) return;
		
		
		
		while (isRunning){
			Iterator<FileDownloader> _dwIter = fileDownloaderList.iterator();
			short _nbDownloaded = 0;
			short _nbFailed = 0;
			
			while (_dwIter.hasNext()){
				FileDownloader _filetDownloader = _dwIter.next();
				if (_filetDownloader.getStatus() == FileDownloader.statusEnum.FAILED) {
					_nbFailed ++;
				}
				else if (_filetDownloader.getStatus() == FileDownloader.statusEnum.DOWNLOADED) {
					_nbDownloaded ++;
				}
			}
			
			if ( _nbFailed > 0 ){
				isRunning = false;
				System.out.println("NzbLeecher::run <- Failed");
			}
			else if ( _nbDownloaded == fileDownloaderList.size() ){
				isRunning = false;
				System.out.println("NzbLeecher::run <- Downloaded");
			}
			
			
		}
		System.out.println("NzbLeecher::run <-");
		
	}
	
	public void start(){
		try {
			NzbLeecher.runSem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.isRunning = true;
		super.start();
		
	}
	
	public ArrayList<FileDownloader> getFileList(){
		return fileDownloaderList;
	}
	
	public static void main(String [] args) throws IOException{
		
		System.out.println("Starting NzbLeecher");
		String _nzbPath = "/home/moi/Bureau/sample.nzb";
		//String _nzbPath = "/home/moi/test.nzb";
		
		//test NzbLeecher
		NzbLeecher _mynzbLeecher = new NzbLeecher(_nzbPath, "/home/moi/TestNzb", "/tmp/JNzbLeecher");
		_mynzbLeecher.start();
		
		/*
		//parse the nzb file
		NzbParser _nzbParser = new NzbParser(_nzbPath);
		ArrayList<NzbFile> _files =_nzbParser.getFiles();
		
		// open a nntp connection
		NNTP _nntp = new NNTP("news.free.fr");
		System.out.println(_nntp.getwelcome());
		
		//get files from usent
		for( int i=0; i < _files.size(); i++){
			String _groupName = _files.get(i).getGroups().get(0);
			System.out.println(_groupName);
			_nntp.group(_groupName);
			ArrayList<NzbSegment> _segments = _files.get(i).getSegments();
			
			for(int j = 0; j < _segments.size(); j ++){
				String _id = "<"+ _segments.get(j).getContent() +">";
				//String _id = _segments.get(j).getContent();
				String _destination ="/tmp/JNzbLeecher/" + i + j + ".tmp";
				
				_nntp.body(_id, _destination);
				
			}
		}*/
		
		
	}
	
}
