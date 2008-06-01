package org.ovhoo.nzb;

/**
 * Save information about nzb segments
 * @author moi
 *
 */
public class NzbSegment {
	
	private int bytes;
	private int number;
	private String content;
	private boolean downloaded = false;
	private NzbFile file;
	
	/**
	 * The default constructor
	 * @param bytes size of the segment
	 * @param number article number in the usenet group
	 * @param content article id usent id
	 * @param file the file witch this segment of part of
	 */
	public NzbSegment(int bytes, int number, String content, NzbFile file){
		this.bytes = bytes;
		this.number = number;
		this.content = content;
		this.file = file;
	}
	
	/**
	 * Another constructor with string parameters
	 * @param sBytes
	 * @param sNumber
	 * @param file the file witch this segment of part of
	 */
	public NzbSegment(String sBytes, String sNumber, NzbFile file){
		this(Integer.decode(sBytes), Integer.decode(sNumber), file);
	}
	/**
	 * Constructor without content initialisation
	 * @param bytes
	 * @param number
	 * @param file the file witch this segment of part of
	 */
	public NzbSegment(int bytes, int number, NzbFile file){
		this(bytes, number, "", file);
	}
	
	
	/**
	 * Update the content
	 * @param content
	 */
	public void addContent(String content){
		this.content = new String( this.content + content);
		
	}
	
	/**
	 * Change the value of this.content
	 * @param content the new content value
	 */
	public void setContent(String content){
		this.content = content;
	}
	
	/**
	 * Return the content
	 * @return
	 */
	public String getContent(){
		return this.content;
	}
	
	/**
	 * Return this.bytes
	 * @return
	 */
	public int getNumber(){
		return this.bytes;
	}
	
	/**
	 * 
	 */
	public String toString(){
		String _ret = "NzbSegment Instance";
		_ret += " content=\""+  this.content +"\"";
		_ret += " number=\""+  this.number +"\"";
		_ret += " bytes=\""+  this.bytes +"\"\n";
		
		return _ret;
	}
	
	/**
	 * Change the value of the attribute downloaded
	 * @param status
	 */
	public void setDownloaded(boolean status){
		this.downloaded = status;
	}
	
	/**
	 * Returns the value of the attribute downloaded
	 * @return true if has bee downloaded false if not
	 */
	public boolean getDownloaded(){
		return this.downloaded;
	}
	
	/**
	 * Return the file witch this segment is part of
	 * @return
	 */
	public NzbFile getFile(){
		return this.file;
	}

}

