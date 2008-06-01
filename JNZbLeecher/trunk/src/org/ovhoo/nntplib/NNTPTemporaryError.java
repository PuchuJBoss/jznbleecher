package org.ovhoo.nntplib;

public class NNTPTemporaryError extends NNTPError {
	
	
	public NNTPTemporaryError(String rep){
		super("NNTP Temporary Error : "+ rep);
		
	}

}
