package org.ovhoo.jNzbLeecherUI.AgentPrincipal;

import java.awt.Container;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ovhoo.JNzbLeecher.FileDownloader;
import org.ovhoo.JNzbLeecher.NzbLeecher;
import org.ovhoo.jNzbLeecherUI.AgentFileDownloader.AgtFileDownloaderC;
import org.ovhoo.jNzbLeecherUI.AgentFileDownloader.AgtFileDownloaderParentControler;
import org.ovhoo.nzb.NzbFile;

/**
 * Main controler using the PAC architecture
 * @author moi
 *
 */
public class AgtPrincipalC implements AgtFileDownloaderParentControler  {
	
	
	private AgtPrincipalP presentation;
	private Container container;
	
	/**
	 * Main constructor
	 *
	 */
	public AgtPrincipalC(){
		this.presentation = new AgtPrincipalP(this);
		
		this.presentation.init();
		
		//get the fram container
		this.container = this.presentation.getContainer();
				
		
		//create a layout manager
		JPanel _panel = new JPanel();
		BoxLayout _layoutManager  = new BoxLayout(_panel, BoxLayout.Y_AXIS); // boxlayout is stupid
 		
		_panel.setLayout(_layoutManager);
		
		
		
		

		
		//String _nzbPath = "/home/moi/Bureau/sample.nzb";
		String _nzbPath = "sample.nzb";
		
		//test NzbLeecher
		NzbLeecher _mynzbLeecher = new NzbLeecher(_nzbPath, "DL", "tmp");
		
		//get File list
		ArrayList<FileDownloader> _FileToDownloadList = _mynzbLeecher.getFileList();
		
		Iterator<FileDownloader> _iter = _FileToDownloadList.iterator();
		while(_iter.hasNext()){
			FileDownloader _fileDownloader = _iter.next();
			AgtFileDownloaderC _fileDwIHM = new AgtFileDownloaderC(this, _fileDownloader);
			_panel.add((JPanel)_fileDwIHM.getPresentation());
		}
		
		_mynzbLeecher.start();
		_panel.setSize(800,800);
		
		
		
		this.container.add(_panel);
		this.presentation.pack();
		//this.presentation.setSize(800,800);
		
		
		this.presentation.setVisible(true);
	}
	
	public static void main(String args[]) throws InterruptedException{
		System.out.println("Starting NzbLeecher UI agent principal");
		
		AgtPrincipalC _agentPrin = new AgtPrincipalC();
	}
}
