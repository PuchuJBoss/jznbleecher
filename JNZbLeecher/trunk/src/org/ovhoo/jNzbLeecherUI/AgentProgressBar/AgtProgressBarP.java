package org.ovhoo.jNzbLeecherUI.AgentProgressBar;

import java.util.ResourceBundle;

import javax.swing.JProgressBar;

import org.ovhoo.AbstractPACAgent.AgtPACC;
import org.ovhoo.AbstractPACAgent.AgtPACP;

/**
 * Presentation of the agent progress bar
 * @author moi
 *
 */
public class AgtProgressBarP extends JProgressBar implements AgtPACP{
	private AgtProgressBarC controler;
	private ResourceBundle resource;
	
	
	
	/**
	 * Constructeur of the class
	 * @param controler the controler of this presentation
	 */
	public AgtProgressBarP( AgtProgressBarC controler){
		//create a horisontal progress bar
		super();
		
		//affect the controler
		this.controler = controler;
	}

	public AgtPACC getControler() {
		return controler;
	}
	
}
