package org.ovhoo.nntplib;

import java.lang.Exception;

/**
 * Base class for all nntplib exceptions
 * @author mzalim
 *
 */
public class NNTPError extends Exception{
	
	/**
	 * @serialField
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor
	 * @param message
	 */
	public NNTPError(String message){
		super(message);
	}

}
