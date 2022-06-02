package org.isf.prescriber.manager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.isf.prescriber.model.Prescriber;
import org.isf.prescriber.service.IoOperations;
import org.isf.utils.exception.OHException;

public class PrescriberManager {

	IoOperations ioOperations;
	
	public PrescriberManager(){
		ioOperations=new IoOperations();
	}
	
	/**
	 * returns the list of all {@link Prescriber}s 
	 * @return the list of all {@link Prescriber}s  
	 */
	public ArrayList<Prescriber> getPrescriber() {
		try {
			return ioOperations.getPrescriber();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * returns the {@link HashMap} of all {@link Prescriber}s 
	 * @return the {@link HashMap} of all {@link Prescriber}s  
	 */
	public HashMap<Integer, String> getHashMap() {
		ArrayList<Prescriber> list = getPrescriber();
		HashMap<Integer, String> prsMap = new HashMap<Integer, String>();
		for (Prescriber prs : list) {
			prsMap.put(prs.getId(), prs.getName());
		}
		return prsMap;
	}
}
