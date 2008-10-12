package org.ovhoo.jNzbLeecherUI.AgentFileDownloader;

import java.util.ResourceBundle;

import javax.swing.JPanel;

import org.ovhoo.AbstractPACAgent.AgtPACC;
import org.ovhoo.AbstractPACAgent.AgtPACP;

/**
 * Presentation of a File downloader agent
 * @author moi
 *
 */
public class AgtFileDownloaderP extends JPanel implements AgtPACP{
	
	private AgtFileDownloaderC controler;
	private ResourceBundle resource;
	
	public AgtFileDownloaderP( AgtFileDownloaderC controler){
		this.controler = controler;
	}

	public AgtPACC getControler() {
		return controler;
	}

}
