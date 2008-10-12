package org.ovhoo.jNzbLeecherUI.AgentPrincipal;

import java.awt.Container;
import java.awt.FlowLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ovhoo.JNzbLeecher.NzbLeecher;

/**
 * Program's main frame
 * @author azalsup
 *
 */
public class AgtPrincipalP extends JFrame{
	
	
	private AgtPrincipalC controler;
	private ResourceBundle resource;
	private Container container;
	
	
	/**
	 * Main constructor
	 * @param controler : a referece to controler
	 */
	public AgtPrincipalP(AgtPrincipalC controler){
		super();
		
		this.controler = controler;
		this.resource =  ResourceBundle.getBundle("multiln",Locale.FRENCH);
		
		
	}
	
	/**
	 * Initialise the frame aspect
	 *
	 */
	public void init(){
//		change window title
		this.setTitle(resource.getString("AGT_PRINCIPAL_P.TITLE"));
		
		container = this.getContentPane();
		container.setLayout(new FlowLayout());
		
		//container.add(new JButton("button1"));
		//container.add(new JButton("button2"));
		
		this.setSize(800,800);
		//this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		
	}
	
	
	public Container getContainer(){
		return this.container;
	}
	

}
