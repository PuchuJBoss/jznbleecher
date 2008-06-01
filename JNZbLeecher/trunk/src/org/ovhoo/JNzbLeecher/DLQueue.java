package org.ovhoo.JNzbLeecher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A DL Queue
 * @author moi
 *
 */
public class DLQueue<T> extends ConcurrentLinkedQueue<T> {

	public DLQueue(){
		super();
		
	}
	

	
	public static void main( String [] args){
		DLQueue<String> ll = new DLQueue<String>();
		ll.add("String");
		ll.add(new String("hh"));
	}

}
