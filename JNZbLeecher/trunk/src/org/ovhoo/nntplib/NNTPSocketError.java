package org.ovhoo.nntplib;

public class NNTPSocketError extends NNTPError {
	public NNTPSocketError(String rep){
		super("NNTP Socket Error : "+ rep);
		
	}
}
