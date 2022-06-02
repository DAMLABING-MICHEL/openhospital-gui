package org.isf.pregnancy.manager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.isf.admission.model.Admission;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.model.Delivery;
import org.isf.pregnancy.service.IoOperationsDelivery;
import org.isf.utils.exception.OHException;

public class PregnancyDeliveryManager {

	public static final int ROBSON_CLASSES = 0;
	public static final int ROBSON_DESCRIPTIONS = 1;
	public static final int ROBSON_DESCRIPTIONS_CLASSES = 2;
	private IoOperationsDelivery ioOperationsDelivery = new IoOperationsDelivery();
	private HashMap<Integer, String> robsonMap;
	private ArrayList<String> robsonClasses;
	private ArrayList<String> robsonDescriptions;
	private ArrayList<String> robsonDescriptionsClasses;

	/**
	 * Deletes all the records related to an admission in the PregnancyAdmission
	 * and PregnancyDelivery database table
	 * 
	 * @param admId - the id of the admission
	 * @return true if the records are deleted correctly
	 */
	public boolean deleteDeliveries(int admId) {
		try {
			return ioOperationsDelivery.deleteAllDeliveryOfAdmission(admId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return false;
		
	}
	
	/**
	 * Deletes one single record of the pregnancydelivery table
	 * 
	 * @param pregdelId - the id of the single {@link Delivery}
	 * @return true if the record is deleted correctly
	 */
	public boolean deleteSingleDelivery(int pregdelId) {
		try {
			return ioOperationsDelivery.deleteSinglePregnancyDelivery(pregdelId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return false;
	}
	
	/**
	 * Insert a new Pregnancy Delivery case for the provided admission
	 * @param admission - the {@link Admission}
	 * @param delivery - the {@link Delivery}
	 * @return <code>true</code> if the delivery is inserted correctly, <code>false</code> otherwise
	 */
	public boolean insertDelivery(Admission admission, Delivery delivery){
		try {
			return ioOperationsDelivery.insertPregnancyDelivery(admission, delivery);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return false;
	}
	/**
	 * Update the provided Pregnancy Delivery case
	 * @param admission - the {@link Admission} this Delivery refers to
	 * @param delivery - the {@link Delivery} to update
	 * @return <code>true</code> if the delivery is inserted correctly, <code>false</code> otherwise
	 */
	public boolean updateDelivery(Admission admission, Delivery delivery){
		try {
			return ioOperationsDelivery.updateDelivery(admission, delivery);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return false;
	}
	/**
	 * 
	 * @param admission the {@link Admission}
	 * @return the list of Deliveries associated to the Admission
	 */
	public ArrayList<Delivery> getDeliveriesOfAdmission(Admission admission){
		try {
			return ioOperationsDelivery.getDeliveriesOfAdmission(admission);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return null;
	}
	/**
	 * 
	 * @param patId the id of the patient
	 * @return a {@link HashMap} with the {@link DeliveryResultType} as key and the count as value
	 */
	public HashMap<String, Integer> getDeliveriesOfPatient(int patId){
		return ioOperationsDelivery.getDeliveryCount(patId);
	}
	/**
	 * 
	 * @param patId the id of the {@link Patient}
	 * @return the number of visits performed by the patient
	 */
	public int patientVisitcount(int patId){
		return ioOperationsDelivery.selectVisitCount(patId);
	}
	
	/**
	 * The list of Types of Removal of Placenta
	 * @return
	 */
	public ArrayList<String> getTypesOfRemovalOfPlacenta() {
		ArrayList<String> tfp = new ArrayList<String>();
		tfp.add("NORMAL");
		tfp.add("AMTSL");
		return tfp;
	}
	
	/**
	 * The list of places where to test HIV
	 * @return
	 */
	public ArrayList<String> getHivTestPlaces() {
		ArrayList<String> hivPlaces = new ArrayList<String>();
		hivPlaces.add("Hospital");
		hivPlaces.add("Health Center");
		return hivPlaces;
	}
	
	/**
	 * The list of ANC options
	 * @return
	 */
	public ArrayList<String> getAncOptions() {
		ArrayList<String> ancOptions = new ArrayList<String>();
		ancOptions.add("Unknown");
		ancOptions.add("4+");
		ancOptions.add("1-3");
		ancOptions.add("0");
		return ancOptions;
	}
	
	/**
	 * The Robson Classification
	 * @param classes - 0 - only the classes, 1 - only descriptions, 2 - both
	 * @return
	 */
	public ArrayList<String> getRobsonClassification(int mode) {
		if (robsonMap == null) {
			robsonMap = new HashMap<Integer, String>();
			robsonMap.put(1, "Nulliparous, singleton, cephalic, ≥37 weeks, spontaneous labor");
			robsonMap.put(2, "Nulliparous, singleton, cephalic, ≥37 weeks, induced labor or cesarean section before labor");
			robsonMap.put(3, "Multiparous without previous cesarean section, singleton, cephalic, ≥37 weeks, spontaneous labor");
			robsonMap.put(4, "Multiparous without previous cesarean section, singleton, cephalic, ≥37 weeks, induced labor or caesarean section before labor");
			robsonMap.put(5, "Multiparous with prior cesarean section, singleton, cephalic, ≥37 weeks");
			robsonMap.put(6, "All nulliparous breeches");
			robsonMap.put(7, "All multiparous breeches (including previous cesarean section)");
			robsonMap.put(8, "All multiple pregnancies (including previous cesarean section)");
			robsonMap.put(9, "All pregnancies with transverse or oblique lie (including those previous cesarean section)");
			robsonMap.put(10, "Singleton, cephalic, ≤36 weeks (including previous cesarean section)");
			
			robsonClasses = new ArrayList<String>();
			robsonDescriptions = new ArrayList<String>();
			robsonDescriptionsClasses = new ArrayList<String>();
			for (Integer index : robsonMap.keySet()) {
				String key = String.valueOf(index);
				String value = robsonMap.get(index);
				
				robsonClasses.add(key);
				robsonDescriptions.add(value);
				robsonDescriptionsClasses.add(key + " - " + value);
			}
			
		}
		if (mode == ROBSON_CLASSES) return robsonClasses;
		else if (mode == ROBSON_DESCRIPTIONS) return robsonDescriptions;
		else return robsonDescriptionsClasses;
	}
	
	/**
	 * The list of Management procedures
	 * @return
	 */
	public ArrayList<String> getManagementList() {
		ArrayList<String> managementList = new ArrayList<String>();
		managementList.add("Induction");
		managementList.add("Augmentation");
		return managementList;
	}
	
	/**
	 * The list of Complications in Delivery
	 * @param delivery 
	 * @return
	 */
	public HashMap<String, Boolean> getComplicationList(Delivery delivery) {
		HashMap<String, Boolean> complicationList = new HashMap<String, Boolean>();
		complicationList.put("APH", delivery != null ? delivery.isComplicationAPH() : false);
		complicationList.put("PPH", delivery != null ? delivery.isComplicationPPH() : false);
		complicationList.put("Cord Prolapse", delivery != null ? delivery.isComplicationCP() : false);
		return complicationList;
	}
}
