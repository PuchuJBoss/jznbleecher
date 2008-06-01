package org.ovhoo.JNzbLeecher;

import java.io.IOException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import org.ovhoo.nntplib.NNTP;
import org.ovhoo.nntplib.NNTPTemporaryError;

public class NNTPServer {
	
	private String name;
	private String adress;
	private int port;
	private String userName; //TODO use securised connection
	private String password; //TODO use securised connection
	
	private static HashSet<String> nameSet = new HashSet<String>();
	private  int threadsNumber;
	private ConcurrentLinkedQueue<NNTP> slotQueue = new ConcurrentLinkedQueue<NNTP>();
	
	private static int maxSlots = 1;
	
	private Semaphore sem;
	
	
	/**
	 * the default constructor
	 * @param name server name
	 * @param adress server adress
	 * @param port server port
	 */
	public NNTPServer(String name, String adress, int port, int threadsNumber){
		this.sem = new Semaphore(threadsNumber, true);
		
		
		this.threadsNumber = threadsNumber;
		this.name = name;
		this.adress = adress;
		this.port = port;
		
		//try to register the server to avoid duplicated server adress
		this.register();
		
		for (int i=0; i< threadsNumber; i ++){
			try {
				this.slotQueue.add(new NNTP(adress, port));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NNTPTemporaryError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}
	
	/**
	 * Constructor who use the default nntp port
	 * @param name
	 * @param adress
	 */
	public NNTPServer(String name, String adress, int threadsNumber){
		this(name, adress,NNTP.nntp_default_port,threadsNumber);
	}
	
	/**
	 * register this server, whe dont wana use the same server twice
	 * @return
	 */
	public synchronized void register(){
		
		if (! NNTPServer.nameSet.add(this.adress)){
			throw new IllegalArgumentException("NNTPServer : Error duplicate server "+ this.name);
		}
		if (this.threadsNumber >  NNTPServer.maxSlots ){
			NNTPServer.maxSlots = this.threadsNumber;
		}
	}
	
	
	public NNTP acquire() throws InterruptedException{
		this.sem.acquireUninterruptibly();
		this.threadsNumber += -1;
		System.out.println(" :: Aquire Server :: "+ this.threadsNumber);
		return slotQueue.poll();
		
		
	}
	
	public void release(NNTP slot){
		
		this.threadsNumber += 1;
		System.out.println(" :: Release Server :: "+ this.threadsNumber);
		this.slotQueue.add(slot);
		this.sem.release();
		
	}
	
	public static int getMaxSlots(){
		return NNTPServer.maxSlots;
	}
	
}
