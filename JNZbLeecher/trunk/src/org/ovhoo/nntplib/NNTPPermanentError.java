package org.ovhoo.nntplib;

public class NNTPPermanentError extends NNTPError {
	
	public NNTPPermanentError(String rep){
		super("NNTP Peranent Error : "+ rep);
		
	}
}
