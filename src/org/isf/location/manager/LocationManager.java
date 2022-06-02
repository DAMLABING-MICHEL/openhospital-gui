package org.isf.location.manager;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.isf.location.model.Location;
import org.isf.location.service.IoOperations;
import org.isf.utils.exception.OHException;

public class LocationManager {

	private IoOperations ioOperations;
	
	public LocationManager(){
		ioOperations = new IoOperations();
	}
	
	/**
	 * return the list of all possible {@link Location}s
	 * @return the list of {@link Location}s
	 */
	public ArrayList<Location> getLocation() {
		try {
			return ioOperations.getLocation();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * return the list of all distinct cities in {@link Location}s
	 * @return the list of cities
	 */
	public ArrayList<String> getCity() {
		try {
			return ioOperations.getCity();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
}
