package org.ovhoo.nntplib;

public class NNTPGroup {
	
	
	private String name;		//Server name
	private int number; 		//Number of articles
	private String first;		//First article number
	private String last;		//Last article number
	
	
	/**
	 * Complete constructor
	 * @param name server name
	 * @param number number of articles
	 * @param first first article number
	 * @param last last article number
	 */
	public NNTPGroup(String name, int number, String first, String last){
		this.name = name;
		this.number = number;
		this.first = first;
		this.last = last;
	}
	
	public NNTPGroup(String name){
		this.name = name;
	}
	
	public String toString(){
		return this.name +" "+ String.valueOf(this.number) +" "+ this.first +" "+ this.last;
	}

}
