package org.ovhoo.JNzbLeecher;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.ovhoo.io.RFile;

public class Property {
	public static Semaphore maxSimuDLSem = new Semaphore(4);
	
	private static ArrayList<NNTPServer> serversList = new ArrayList<NNTPServer>();
	
	public Property(RFile confFile){
		//Server list
		Property.serversList.add(new NNTPServer("test","news.free.fr",4));
	}
	
	public static ArrayList<NNTPServer> getServersList(){
		return Property.serversList;
	}

}
