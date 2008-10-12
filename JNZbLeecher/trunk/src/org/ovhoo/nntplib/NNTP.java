package org.ovhoo.nntplib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.regex.Pattern;

/**
* An NNTP client class based on RFC 977: Network News Transfer Protocol.
* This code is an adptation of python nntlib module
**/


/**
 * An implementation of nntp client based on RFC977
 * @author azalsup
 * @version 0.1.0
 */
public class NNTP extends Socket {
	
	
	public static final int nntp_default_port =  119;	//Standard port used by NNTP servers
	public static final String crlf = "\r\n";
	
	private int port;		//the nntp port of
	private String host;	//the nntp host name
	private NNTPResponse welcomeMsg;
	
	private boolean withLogin = false;
	private String userName; //TODO use securised connection
	private String password; //TODO use securised connection
	
	private boolean serverOk = false;
	
	private BufferedReader inputStream;
	private PrintWriter outputStream;

	
	/**
	 * The default constructor, th full constructor witch give you the possibility to chose all parameteres
	 * @param host The nntp host name
	 * @param port The nntp host communication port
	 * @param login  server login
	 * @param passwd server password
	 * @throws IOException If an exception occure
	 * @throws NNTPTemporaryError 
	 * @throws NNTPPermanentError 
	 */
	public NNTP(String host, int port, String login, String passwd) throws IOException, NNTPTemporaryError, NNTPPermanentError{
		super(host, port);
		this.setHost(host);
		this.setPort(port);
		
		if ((login != "") || (passwd != "")) {
			this.withLogin = true;
			this.userName = login;
			this.password = passwd;
		}
		
		
		
		this.inputStream = new BufferedReader(new InputStreamReader(super.getInputStream()));
		this.outputStream = new PrintWriter( super.getOutputStream());
		
		this.welcomeMsg = new NNTPResponse(this.inputStream);
		
		if (this.withLogin){
			NNTPResponse _rep = this.sendCommand("authinfo user "+this.userName);
			
			if (_rep.getCode()== 381){
				if (this.password == ""){
					throw new NNTPPermanentError("Need Password");
				}
				else {
					_rep = this.sendCommand("authinfo pass "+this.password);
					if( _rep.getCode() != 281) throw new NNTPPermanentError("Wrong Password");
				}
			}
		}
		
		this.getwelcome();
		
		
	}
	
	/**
	 * The default constructor, th full constructor witch give you the possibility to chose all parameteres
	 * @param host The nntp host name
	 * @param port The nntp host communication port
	 * @throws IOException If an exception occure
	 * @throws NNTPTemporaryError 
	 */
	public NNTP(String host, int port) throws IOException, NNTPTemporaryError{
		super(host, port);
		this.setHost(host);
		this.setPort(port);
		this.inputStream = new BufferedReader(new InputStreamReader(super.getInputStream()));
		this.outputStream = new PrintWriter( super.getOutputStream());
		
		this.welcomeMsg = new NNTPResponse(this.inputStream);
	}
	
	/**
	 * A constructor who build a session with the default communication port
	 * @param host The nntp host name, used port is the default NNTP.nntp_default_port
	 * @throws IOException 
	 * @throws NNTPTemporaryError 
	 */
	public NNTP(String host) throws IOException, NNTPTemporaryError{
		this(host, NNTP.nntp_default_port);
	}
	
	/**
	 * Set the attribute this.host to a new value
	 * @param host The new value of the attribute host
	 */
	protected  void setHost(String host){
		this.host = host;
	}
	

	/**
	 * Set the attribute this.port to a new value
	 * @param port The new value of the attribute host
	 */
	protected  void setPort(int port){
		this.port = port;
	}
	
	
	/**
	 * Function who returns the welcome message 
	 * @return return a string representing the welcom message
	 */
	public String getwelcome(){
		return this.welcomeMsg.toString();
	}
	
	
	/**
	 * Internal: send one line to the server, appending CRLF.
	 * @param line : A String with the query to send
	 */
	private void putcmd(String line){
		line += NNTP.crlf;
		this.outputStream.print(line);
		this.outputStream.flush();
		
	}
	
	/**
	 * Send a command and read the response
	 * @param command The sended comand
	 * @return returned response
	 * @throws NNTPTemporaryError 
	 */
	private NNTPResponse sendCommand(String command) throws NNTPTemporaryError{
		//send the command
		this.putcmd(command);
		
		//get the response
		return new NNTPResponse(this.inputStream);
	}
	
	
	
	/**
	 * Process a NEWGROUPS command 
	 * @param date string 'yymmdd' indicating the date
	 * @param time string 'hhmmss' indicating the time
	 * @return response
	 * @throws NNTPTemporaryError 
	 */
	public NNTPResponse newgroups(String date, String time) throws NNTPTemporaryError{
		String _command = "NEWGROUPS " + date + " " + time;
		return this.sendCommand(_command);
		
	}
	
	/**
	 * Process a NEWNEWS command
	 * @param group group name or '*'
	 * @param date string 'yymmdd' indicating the date
	 * @param time string 'hhmmss' indicating the time
	 * @return NNTPResponse with the list of messages id
	 * @throws NNTPTemporaryError 
	 */
	public NNTPResponse newnews( String group, String date, String time) throws NNTPTemporaryError{
		String _command = "NEWNEWS " + group +" "+ date + " " + time;
		return this.sendCommand(_command);
	}
	
	
	/**
	 * Process a LIST command
	 * @return NNTPResponse with the list of (group, last, first, flag) (strings)
	 * @throws NNTPTemporaryError 
	 */
	public NNTPResponse list() throws NNTPTemporaryError{
		String _command = "LIST";
		return this.sendCommand(_command);
	}
	
	
	
	/**
	 * Get descriptions for a range of groups.
	 * @param group_pattern pattern of group to get destcription of
	 * @return NNTPResponse
	 */
	public void descriptions(String group_pattern){
		Pattern line_pat = Pattern.compile("^(?P<group>[^ \t]+)[ \t]+(.*)$");
		//TODO Implement this function only if nececesary
	}
	
	/**
	 * Process a GROUP command
	 * @param group the group name
	 * @return a structure NNTPGroup with all the informations about the group
	 * @throws NNTPTemporaryError 
	 */
	public NNTPGroup group( String group) throws NNTPTemporaryError{
		String _command = "GROUP "+ group;
		
		NNTPResponse _response =  this.sendCommand(_command);
		if ( _response.getCode() != 211){
			new NNTPReplyError(_response.toString());
		}
		
		String[] _tmp =  _response.toString().split(" ");
		
		if (! _tmp[4].equals(group) ){
			System.out.println("WARNING : Nom du groupe ne correspond pas");
			System.out.println("Demande : " + group);
			System.out.println("Obtenu : " + _tmp[4]);
		}
		
		return new NNTPGroup(group, Integer.decode(_tmp[1]), _tmp[2], _tmp[3]);
	}
	
	/**
	 * Process a stat command
	 * @param id article number or message id
	 * @return
	 * @throws NNTPTemporaryError 
	 */
	public NNTPStatResponse stat(String id) throws NNTPTemporaryError{
		
		String _command = "STAT " + id; 
		//send the command
		this.putcmd(_command);
		
		//get the response
		return new NNTPStatResponse(this.inputStream);
	}
	
	/**
	 * Process a next command
	 * @return
	 * @throws NNTPTemporaryError 
	 */
	public NNTPStatResponse next() throws NNTPTemporaryError{
		
		String _command = "NEXT"; 
		//send the command
		this.putcmd(_command);
		
		//get the response
		return new NNTPStatResponse(this.inputStream);
	}
	
	/**
	 * Process a last command
	 * @return
	 * @throws NNTPTemporaryError 
	 */
	public NNTPStatResponse last() throws NNTPTemporaryError{
		
		String _command = "LAST"; 
		//send the command
		this.putcmd(_command);
		
		//get the response
		return new NNTPStatResponse(this.inputStream);
	}
	
	/**
	 * Process a head command
	 * @param id article number or message id
	 * @return
	 * @throws NNTPTemporaryError 
	 */
	public NNTPStatResponse head(String id, String filepath) throws NNTPTemporaryError{
		
		String _command = "HEAD " + id; 
		//send the command
		this.putcmd(_command);
		
		//get the response
		return new NNTPStatResponse(this.inputStream, filepath);
	}
	
	/**
	 * Process a body command
	 * @param id article number or message id
	 * @return
	 * @throws NNTPTemporaryError 
	 */
	public NNTPStatResponse body(String id, String filepath) throws NNTPTemporaryError{
		
		String _command = "BODY " + id; 
		//send the command
		//System.out.println(id);
		this.putcmd(_command);
		
		
		//get the response
		return new NNTPStatResponse(this.inputStream, filepath);
	}
	
	/**
	 * Process a article command
	 * @param id article number or message id
	 * @return
	 * @throws NNTPTemporaryError 
	 */
	public NNTPStatResponse article(String id, String filepath) throws NNTPTemporaryError{
		
		String _command = "ARTICLE " + id; 
		//send the command
		this.putcmd(_command);
		
		//get the response
		return new NNTPStatResponse(this.inputStream, filepath);
	}
	
	/**
	 * Process a QUIT command and close the socket
	 * @throws NNTPTemporaryError 
	 */
	public void quit() throws NNTPTemporaryError{
		String _command = "QUIT"; 
		NNTPResponse _rep = this.sendCommand(_command);
		
		try {
			this.inputStream.close();
			this.outputStream.close();
			super.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	/**
	 * The main entry of the program
	 * @param args command line arguments
	 * @throws NNTPTemporaryError 
	 */
	public static void main(String[] args) throws NNTPTemporaryError{
		
		NNTP _mynntp;
		try {
			_mynntp = new NNTP("news.free.fr");
			System.out.println(_mynntp.getwelcome());
			//System.out.println(_mynntp.newgroups("080107", "000000"));
			//System.out.println(_mynntp.newnews("alt.binaries.sounds.mp3","070107", "000000"));
			//System.out.println(_mynntp.list());
			//System.out.println(_mynntp.group("alt.binaries.sounds.mp3"));
			
			//print full list
			//_mynntp.list().printfull();
			System.out.println("GROUP "+ _mynntp.group("alt.butt.harp"));
			_mynntp.stat("3047").printfull();
			_mynntp.head("3047", null).printfull();
			_mynntp.body("3047", null).printfull();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		
		
		
		
		
	}
}

