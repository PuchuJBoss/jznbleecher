package org.ovhoo.io;

import java.io.File;

public class RFile extends File {
	public RFile(String path){
		super(path);
		
		//Verify if the file exists
		if (! this.exists()){
			throw new IllegalArgumentException("RFile: no such file or directory: " + this);
			
		}
		else if(! this.isFile()){
			throw new IllegalArgumentException("RFile: no sutch file: " + this);
		}
		else if(! this.canRead()){
			throw new IllegalArgumentException("RFile: canot read this file: " + this);
		}
	}
}

