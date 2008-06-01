package org.ovhoo.nntplib;

import java.io.BufferedReader;

public class NNTPStatResponse extends NNTPResponse {
	private int number;
	private String id;
	
	
	/**
	 * 
	 * @param inputStream
	 * @throws NNTPTemporaryError 
	 */
	public NNTPStatResponse(BufferedReader inputStream) throws NNTPTemporaryError{
		this(inputStream, null);
	}
	
	/**
	 * The default constructor
	 * @param number the article number
	 * @param id the article id
	 * @throws NNTPTemporaryError 
	 */
	public NNTPStatResponse(BufferedReader inputStream, String filepath) throws NNTPTemporaryError{
		super(inputStream, filepath);
		
		if ( this.getCode()/10 != 22 ){
			try {
				throw new NNTPReplyError(super.toString());
			} catch (NNTPReplyError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//System.out.println(super.toString());
		//System.out.flush();
		String [] _tmp = super.toString().split(" ");
		
		this.number = Integer.decode(_tmp[1]);	
		this.id = _tmp[2];
	}
	
	

}
