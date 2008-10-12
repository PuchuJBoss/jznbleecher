package org.ovhoo.jNzbLeecherUI.AgentFileDownloader;

import javax.swing.JProgressBar;

import org.ovhoo.AbstractPACAgent.AgtPACA;
import org.ovhoo.AbstractPACAgent.AgtPACC;
import org.ovhoo.AbstractPACAgent.AgtPACP;
import org.ovhoo.JNzbLeecher.FileDownloader;
import org.ovhoo.jNzbLeecherUI.AgentProgressBar.AgtProgressBarC;
import org.ovhoo.jNzbLeecherUI.AgentProgressBar.AgtProgressBarParentControler;

/**
 * Controler of the agent AgentFileDownloader
 * This agent represente a file downloading thread
 * @author moi
 *
 */
public class AgtFileDownloaderC  implements AgtPACC , AgtProgressBarParentControler {
	
	private AgtFileDownloaderP presentation;
	private AgtFileDownloaderParentControler parent;
	
	private AgtProgressBarC progressBar;
	FileDownloader element;

	/**
	 * default constructor
	 * @param parent the controler of the parent agent
	 */
	public AgtFileDownloaderC(AgtFileDownloaderParentControler parent, FileDownloader element){
		this.parent = parent;
		
		//element to monitor
		this.element = element;
		this.element.setRepresentation(this);
		
		//create the presentation
		this.presentation = new AgtFileDownloaderP(this);
		
		//create the progress bar
		this.progressBar = new AgtProgressBarC(this);
		
		//add the progress bar presentation to curent pres
		this.presentation.add((JProgressBar)this.progressBar.getPresentation());
	}
	
	public AgtPACA getAbstraction() {
		return null;
	}

	public AgtPACC getParent() {
		return (AgtPACC) this.parent;
	}

	public AgtPACP getPresentation() {
		return this.presentation;
	}
	
	public void setProgression(int progression){
		this.progressBar.setValue(progression);
	}

}
