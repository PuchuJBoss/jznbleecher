package org.ovhoo.nzb;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Save information about a file to download
 * @author azalsup
 *
 */
public class NzbFile {
	
	private String subject;
	private String date;
	private String poster;
	private ArrayList<String> groups = new ArrayList<String>();
	private ArrayList<NzbSegment> segments = new ArrayList<NzbSegment>();
	
	
	/**
	 * The default constructor
	 * @param subject the article subject
	 * @param date article publication date
	 * @param poster the article poster name
	 */
	public NzbFile(String subject, String date, String poster){
		this.subject = subject;
		this.date = date;
		this.poster = poster;
		
	}
	
	/**
	 * Add a new group to the groups list
	 * @param group the new group name to add
	 */
	public void addGroup( String group){
		this.groups.add(group);
	}
	/**
	 * Add  groups to the groups list
	 * @param groups list of groups to add
	 */
	public void addGroups( ArrayList<String> groups){
		
		for( int i = 0 ; i < groups.size(); i ++) this.addGroup(groups.get(i));
	}
			
	/**
	 * Add a new segment to the segments list
	 * @param seg the new segment  to add
	 */
	public void addSegment( NzbSegment seg){
		this.segments.add(seg);
	}
	
	/**
	 * Add a new group to the groups list
	 * @param segs segments to add
	 */
	public void addSegments( ArrayList<NzbSegment> segs){
		for( int i = 0 ; i < segs.size(); i ++) this.addSegment(segs.get(i));
	}
	
	/**
	 * Return a String representing the class
	 */
	public String toString(){
		String _ret = "NzbFile Instance";
		_ret += " subject=\""+  this.subject.toString() +"\"";
		_ret += " poster=\""+  this.poster.toString() +"\"";
		_ret += " date=\""+  this.date.toString() +"\"\n";
		
		//print su-belements
		_ret += "# # List of groups\n";
		
		for(int i = 0; i < this.groups.size(); i++) _ret+= "  # "+ this.groups.get(i) +"\n";
		
		_ret += "# # List of segments\n";
		for(int i = 0; i < this.segments.size(); i++) _ret+= "  # "+ this.segments.get(i) +"\n";
		
		return _ret;
	}
	
	/**
	 * returns the segments arrayList
	 * @return
	 */
	public ArrayList<NzbSegment> getSegments(){
		return this.segments;
	}
	
	/**
	 * return the groups arrayList
	 * @return
	 */
	public ArrayList<String> getGroups(){
		return this.groups;
	}
	
	/**
	 * returns the subject
	 * @return
	 */
	public String getSubject(){
		return this.subject;
	}
	
	/**returns article date
	 * 
	 * @return
	 */
	public String getDate(){
		return this.date;
	}
	
	/**
	 * return poster
	 * @return
	 */
	public String getPoster(){
		return this.poster;
	}
	
	/**
	 * Return download advancement
	 * @return (Nomber of downloaded segments) / number of segments => < 1
	 */
	public int downloadStatus(){
		
		int _downloadedNb = 0, _notDownloaded = 0;
		
		Iterator<NzbSegment> _iter = this.segments.iterator();
		while (_iter.hasNext()){
			NzbSegment _tmpSeg = _iter.next();
			if ( _tmpSeg.getDownloaded()){
				_downloadedNb += 1;
			}
			else{
				_notDownloaded += 1;
			}
		}
		
		return (_downloadedNb/(_downloadedNb + _notDownloaded));
		
	}
}
