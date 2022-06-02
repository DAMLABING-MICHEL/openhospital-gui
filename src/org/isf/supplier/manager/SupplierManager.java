package org.isf.supplier.manager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.IoOperations;
import org.isf.utils.exception.OHException;

public class SupplierManager {

	IoOperations ioOperations;
	
	public SupplierManager(){
		ioOperations=new IoOperations();
	}
	
	/**
	 * returns the list of all {@link Supplier}s 
	 * @param all - if <code>true</code> it will returns deleted ones also
	 * @return the list of all {@link Supplier}s  
	 */
	public ArrayList<Supplier> getSupplier(boolean all) {
		try {
			return ioOperations.getSupplier(all);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * returns the {@link HashMap} of all {@link Supplier}s 
	 * @param all - if <code>true</code> it will returns deleted ones also
	 * @return the {@link HashMap} of all {@link Supplier}s  
	 */
	public HashMap<Integer, String> getHashMap(boolean all) {
		ArrayList<Supplier> supList = getSupplier(all);
		HashMap<Integer, String> supMap = new HashMap<Integer, String>();
		for (Supplier sup : supList) {
			supMap.put(sup.getSupId(), sup.getSupName());
		}
		return supMap;
	}
}
