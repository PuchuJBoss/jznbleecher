package org.ovhoo.io;

import java.io.File;

public class Directory extends File {
	
	public Directory(String path){
		super(path);
		//verify destination dir
		if (! this.exists()){
			this.mkdirs();
		}
		else if (! this.isDirectory()){
			throw new IllegalArgumentException("Directory: the given path is not a Directory: " + this);
		}
	}

}
