package org.ovhoo.jNzbLeecherUI.AgentProgressBar;

import org.ovhoo.AbstractPACAgent.AgtPACA;
import org.ovhoo.AbstractPACAgent.AgtPACC;
import org.ovhoo.AbstractPACAgent.AgtPACP;

/**
 * Controler of the agent progresse bar
 * @author moi
 *
 */
public class AgtProgressBarC implements AgtPACC{
	
	private AgtProgressBarParentControler parent;
	private AgtProgressBarP presentation;
	
	private final int minValue = 0;
	private final int maxValue = 100;
	
	/**
	 * default constructor
	 * @param parent : the controler of the parent agent
	 */
	public AgtProgressBarC( AgtProgressBarParentControler parent){
		this.parent = parent;
		
		//create the presentation
		this.presentation = new AgtProgressBarP(this);
		
//		 min adn mex values are 0 to 100 (as percent)
		this.presentation.setMinimum(minValue);
		this.presentation.setMaximum(maxValue);
		
		//set initial value to minValue
		this.presentation.setValue(minValue);
		
	}
	
	
	public AgtPACA getAbstraction() {
		return null;
	}
	public AgtPACC getParent() {
		return (AgtPACC) parent;
	}
	public AgtPACP getPresentation() {
		return presentation;
	}
	
	/**
	 * change the value of the progress bar
	 * @param value : the new value of the preogress bar
	 */
	public void setValue(int value){
		if (value < minValue ) this.presentation.setValue(minValue);
		else if (value > maxValue) this.presentation.setValue(maxValue);
		else this.presentation.setValue(value);
		
		//this.presentation.updateUI();
	}

}
