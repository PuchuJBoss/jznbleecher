package org.ovhoo.nzb;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class NzbParser {
	
	private ArrayList<NzbFile> files;
	private String nzbFilePath;
	private NzbHandler nzbHandler;
	private XMLReader nzbReader;
	
	/**
	 * Default constructor
	 * @param nzbFilePath The nzb file path
	 */
	public NzbParser(String nzbFilePath){
		this.nzbFilePath = nzbFilePath;		
		
	}
	
	/**
	 * Parse the nzb file
	 */
	public void parse(){
		
		this.files = null;
		this.nzbHandler = new NzbHandler();
		
		try {
			this.nzbReader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.nzbReader.setContentHandler(this.nzbHandler);
		
		try {
			this.nzbReader.parse(this.nzbFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.files = this.nzbHandler.getFiles();
		
	}
	
	/**
	 * get the files reference from the nzb file
	 * @return
	 */
	public ArrayList<NzbFile> getFiles(){
		if ( this.files == null){
			this.parse();
		}
		
		return this.files;
	}
	
	

}
