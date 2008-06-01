package org.ovhoo.nntplib;

public class NNTPProtocolError extends NNTPError {
	
	public NNTPProtocolError(String rep){
		super("Protocole unexpected : "+ rep);
		
	}

}
