package org.ovhoo.nntplib;

public class NNTPReplyError extends NNTPError {
	public NNTPReplyError(String rep){
		super("NNTP Reply  Error : "+ rep);
		
	}
}
