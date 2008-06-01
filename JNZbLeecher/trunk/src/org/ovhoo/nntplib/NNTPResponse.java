package org.ovhoo.nntplib;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.File;

public class NNTPResponse {
	
	private final static int[] LONGRESPCODE =  {
		100, 215, 220, 221, 
		222, 224, 230, 231, 
		282 };
	
	
	private BufferedReader inputStream;
	
	private String response;	//The first response
	private int code;
	private ArrayList<String>  list = new ArrayList<String>();
	
	private String filepath;

	
	/**
	 * Constructor
	 * @param inputStream
	 * @throws NNTPTemporaryError 
	 */
	public NNTPResponse(BufferedReader inputStream) throws NNTPTemporaryError{
		this(inputStream, null);
	}
	/**
	 * Constructor
	 * @param inputStream
	 * @param path path of the destination file
	 * @throws NNTPTemporaryError 
	 */
	public NNTPResponse(BufferedReader inputStream, String path) throws NNTPTemporaryError{
		String _line; 
		
		
		this.filepath = path;
		this.inputStream = inputStream;
		this.readresp();
		
		//System.out.println(this.response);
		
		if (this.isLong()){
			while( true){
				_line = this.readinputline();
				if ( (_line == null) || (_line.equals("."))){
					break;
				}
//				else if("..".equals( _line.substring(0, 2) )){
//					_line = _line.substring(2);
//				}	
				
				this.list.add(_line);
			}
		}
		//System.out.print(" :::: " +this.filepath);
		//System.out.flush();
		this.toFile();
	}
	
	public boolean isLong(){
		
		for( int i= 0 ; i< NNTPResponse.LONGRESPCODE.length; i++){
			//System.out.println(this.code +" "+ NNTPResponse.LONGRESPCODE[i]);
			if (this.code == NNTPResponse.LONGRESPCODE[i] ){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Read a line from the inputStream
	 * @return return a string with the value of the read message
	 */
	protected String readinputline(){
		String _line = "";
		//System.out.println(_line);
		
		try {
			_line = this.inputStream.readLine();
		} catch (IOException e) {
			// exception catched when IO prolems encountred
			e.printStackTrace();
		}
		
		if (_line == null ){
			try {
				throw new NNTPSocketError(_line);
			} catch (NNTPSocketError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if ( _line.endsWith(NNTP.crlf)){
			return _line.substring(0 , _line.length() - 2);
		}
		//else if ( (_line. charAt(_line.length() -1) == NNTP.crlf.charAt(0) ) || (_line.charAt(_line.length() -1) == NNTP.crlf.charAt(1)) ){
		//	return _line.substring(0 , _line.length() - 1);
		//}
		else {
			return _line;
		}
	}
	
	/**
	 * get a response from the server.
	 * @return the response as string
	 * @throws NNTPTemporaryError 
	 */
	private void readresp() throws NNTPTemporaryError{
		String _rep = this.readinputline();
		//System.out.println(_rep);
		if ( _rep.startsWith("5")){
			try {
				throw new NNTPPermanentError(_rep);
			} catch (NNTPPermanentError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (_rep.startsWith("4")){
			throw new NNTPTemporaryError(_rep);	
		}
		else if ( (_rep.startsWith("1")) || (_rep.startsWith("2")) || (_rep.startsWith("3"))){
			this.response = _rep;
		}
		else {
			//this case ocure when a unknown response is given
			new NNTPProtocolError(_rep);
		}
		
		this.code = Integer.decode( _rep.substring(0, 3) );
		//System.out.println(this.code);
	}
	
	/**
	 * Return the response code
	 * @return the response code
	 */
	public int getCode(){
		return this.code;
	}
	
	/**
	 * Return a string represinting the Response
	 * @return the first kine of the response
	 */
	public String toString(){
		return this.response;
	}
	
	/**
	 * print the full response
	 */
	public void printfull(){
		System.out.println("# Full print of the response : " + this.response);
		for(int i = 0; i < this.list.size(); i++){
			System.out.println(this.list.get(i));
		}
	}
	
	/**
	 * write a file with the content of the response
	 */
	public void toFile(){
		if ( this.filepath != null ){
			this.toFile(this.filepath);
		}
	}
	
	/**
	 * Write the response in a new file
	 * @param file
	 */
	public void toFile(String filepath){
		if (! this.filepath.equals(filepath) ){
			this.deleteFile();
			this.filepath = filepath;
		}
		
		File _f = new File(this.filepath);
		// Make sure the file or directory exists and isn't write protected
		
		// If it is a directory, make sure it is empty
	    if (_f.isDirectory()) {
	    	throw new IllegalArgumentException("toFile: directoy givin when expect a file: " + this.filepath);
	    }
	    
	    //Open file
	    PrintWriter _destinationFile = null;
	    try {
			_destinationFile = new  PrintWriter(_f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    for( int i = 0; i < this.list.size(); i++){
	    	//System.out.println(this.list.get(i));
	    	_destinationFile.println(this.list.get(i));
	    }
		
	    _destinationFile.close();
	
	    
	    
	}
	
	/**
	 * Delete the file
	 */
	public void deleteFile(){
		// A File object to represent the filename
		if (this.filepath != null){
			File _f = new File(this.filepath);
	
			// Make sure the file or directory exists and isn't write protected
			if (!_f.exists())
				throw new IllegalArgumentException("Delete: no such file or directory: " + this.filepath);
	
			if (!_f.canWrite())
				throw new IllegalArgumentException("Delete: write protected: "+ this.filepath);
	
			// If it is a directory, make sure it is empty
		    if (_f.isDirectory()) {
		    	throw new IllegalArgumentException("Delete: cannot remove a directory: " + this.filepath);
		    }
	
		    // Attempt to delete it
		    boolean success = _f.delete();
	
		    if (!success) throw new IllegalArgumentException("Delete: deletion failed");
		    
		    try {
				PrintStream _stream = new PrintStream( _f);
				
				for(int i = 0; i < this.list.size(); i++) _stream.print(this.list.get(i));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
