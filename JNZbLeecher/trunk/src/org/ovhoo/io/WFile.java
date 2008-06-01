package org.ovhoo.io;

import java.io.File;

public class WFile extends File {
	public WFile(String path){
		super(path);
		
		//Verify if the file exists
		if (! this.exists()){
			throw new IllegalArgumentException("WFile: no such file or directory: " + this);
			
		}
		else if(! this.isFile()){
			throw new IllegalArgumentException("WFile: no sutch file: " + this);
		}
		else if(! this.canWrite()){
			throw new IllegalArgumentException("WFile: canot read this file: " + this);
		}
	}
}
