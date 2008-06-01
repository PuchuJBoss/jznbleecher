package org.ovhoo.nzb;

import java.io.IOException;
import java.util.ArrayList;


import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Implementation of a nzb content handler
 * @author azalsup
 *
 */
public class NzbHandler implements ContentHandler{
	
	private ArrayList<NzbFile> files = new ArrayList<NzbFile>(); //List of nzb files
	
	private int nbNzb = 0;
	//private boolean eNzb = false;
	
	private NzbFile tmpFile ;	//Store the last found file
	private ArrayList<String> tmpGroups = null; //new  ArrayList<String>();
	private ArrayList<NzbSegment> tmpSegments = null; //new  ArrayList<NzbSegment>();
	
	private String tmpGroup;
	private NzbSegment tmpSegment;
	      	
	
	@Override
	public void characters(char[] ch, int start, int lenght) throws SAXException {
		//System.out.println("#PCDATA : " + new String(ch, start, lenght));
		if ( this.tmpGroup != null){
			this.tmpGroup = new String(ch, start, lenght);
		}
		else if ( this.tmpSegment != null){
			this.tmpSegment.setContent(new String(ch, start, lenght));
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		String _e_name = qName.toLowerCase();
		
		if (_e_name.equals("nzb")){
			
		}
		else if (_e_name.equals("file")){
			this.files.add(this.tmpFile);
			this.tmpFile = null;
		}
		else if (_e_name.equals("groups")){
			this.tmpFile.addGroups(this.tmpGroups);
			this.tmpGroups = null;
		}
		else if (_e_name.equals("group")){
			this.tmpGroups.add(this.tmpGroup);
			this.tmpGroup = null;
		}
		else if (_e_name.equals("segments")){
			this.tmpFile.addSegments(this.tmpSegments);
			this.tmpSegments = null;
		}
		else if (_e_name.equals("segment")){
			this.tmpSegments.add(this.tmpSegment);
			this.tmpSegment = null;
		}
		
		
	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		
		// We compare only lower case words
		String _e_name = qName.toLowerCase();
		
		if (_e_name.equals("nzb")){
			//this.eNzb = true;
			this.nbNzb += 1;
		}
		else if(_e_name.equals("file")){
			this.tmpFile = new NzbFile(atts.getValue("subject"), atts.getValue("date"), atts.getValue("poster"));	
		}
		else if(_e_name.equals("groups")){
			this.tmpGroups = new ArrayList<String>();
			
		}
		else if(_e_name.equals("group")){
			this.tmpGroup = new String();
			
		}
		else if(_e_name.equals("segments")){
			this.tmpSegments = new ArrayList<NzbSegment>();
		}
		else if(_e_name.equals("segment")){
			this.tmpSegment = new NzbSegment(atts.getValue("bytes") , atts.getValue("number"), this.tmpFile);
		}
	
	}
	
	

	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * For internal use only, show the structure of the nzb file
	 */
	public void testPrint(){
		for ( int i = 0; i < this.files.size(); i++){
			System.out.println(this.files.get(i));
		}
	}
	
	/**
	 * Return this.files
	 * @return the array list of files founded in the nzb
	 */
	public ArrayList<NzbFile> getFiles(){
		return this.files;
	}
	
	public static void main(String [] args) throws SAXException{
		
		NzbHandler myHandler = new NzbHandler();
		
		XMLReader myXmlReader = XMLReaderFactory.createXMLReader();
		myXmlReader.setContentHandler(myHandler);			
		
        try {
        	
        	myXmlReader.parse("/home/moi/Bureau/two.and.a.half.men.s01e01.ws.dvdrip.xvid.nzb");
        	//myXmlReader.parse("/home/moi/Bureau/Stephane Guillon .nzb");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// one more test
		myHandler.testPrint();
		
	}
	
}
