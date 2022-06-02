package org.isf.pregnancy.manager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.isf.admission.model.PregnancyPatient;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.model.Delivery;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregnancy.service.IoOperationsPregnancy;
import org.isf.pregnancy.service.IoOperationsPregnancyVisit;
import org.isf.pregnancyexam.manager.PregnancyExamManager;
import org.isf.pregnancyexam.model.PregnancyExam;
import org.isf.pregnancyexam.model.PregnancyExamResult;
import org.isf.utils.exception.OHException;

/**
 * Martin Reinstadler
 * This class manages the IO operations by calling the various methods from them. 
 * The GUI instantiates only this class and no IoOperaitions class for simplicity
 *
 */
public class PregnancyCareManager {

	private IoOperationsPregnancy ioOperations = new IoOperationsPregnancy();
	private IoOperationsPregnancyVisit ioOperationsVisit = new IoOperationsPregnancyVisit();
	private PregnancyExamManager exaMan = new PregnancyExamManager(); 
	
	/**
	 * 
	 * Returns all the {@link PregnancyPatient}s (with pregnancy, patient and admission information)
	 * @return a list of {@link Patient}s  
	 */
	public ArrayList<PregnancyPatient> getPregnancyPatients(){
		try {
			return ioOperations.getPregnancyAdmittedPatients();
		} catch (OHException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param regex the searchkey
	 * @return a list of female {@link Patient} 
	 */
	public ArrayList<PregnancyPatient> getPregnancyPatients(String regex){
		try {
			return ioOperations.getPregnancyAdmittedPatients(regex);
		} catch (OHException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	 
	/**
	 * 
	 * @param patid the od of the {@link Patient}
	 * @return a list of the patients pregnancies
	 */
	public ArrayList<Pregnancy>getPatientsPregnancies(int patid){
		try {
			return ioOperations.getPregnancy(patid);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	/**
	 * 
	 * @param pregid the id of the {@link Pregnancy}
	 * @return the pregnancy for the given id
	 */
	public Pregnancy getPregnancy(int pregid){
		try {
			return ioOperations.getPregnancy_byId(pregid);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * @param patientid - the id of the {@link PregnancyPatient}
	 * @return a list of visits the patient has performed during her live
	 */
	public ArrayList<PregnancyVisit> getPregnancyVisits(int patientid){
		try {
			return ioOperationsVisit.getPregnancyVisits(patientid);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * @param visittype - the type of the {@link PregnancyVisit}, which can be
	 * prenatal(-1) or postnatal(1)
	 * @return the list of exams previously specified for this type
	 */
	public ArrayList<PregnancyExam> getVisitExams_byVisitType(int visittype){
			return exaMan.getPregnancyExams(visittype);
		
	}
	
	/**
	 * Inserts a new {@link Pregnancy} in the database
	 * @param pregnancy - the {@link Pregnancy} to insert
	 * @return  <code>true</code> if the data has been inserted, <code>false</code> otherwise 
	 */
	public boolean newPregnancy(Pregnancy pregnancy){
		try {
			return ioOperations.newPregnancy(pregnancy);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	/**
	 * Inserts a new {@link PregnancyVisit} in the database
	 * @param visit the {@link PregnancyVisit} to be inserted
	 * @return the id of the new {@link PregnancyVisit}
	 * 
	 */
	public int newVisit(PregnancyVisit visit){
		try {
			int key = ioOperationsVisit.insertPregnancyVisit(visit);
			return key;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return -1;
		}
	}
	/**
	 * 
	 * @param visitid the id of the {@link PregnancyVisit}
	 * @param examresults the list of {@link PregnancyExamResult} with outcomes
	 * @return true if the tuples are inserted correctly
	 */
	public boolean newExamOutcomes(int visitid, ArrayList<PregnancyExamResult> examresults){
			return ioOperationsVisit.insertExamResult(visitid, examresults);
	}
	/**
	 * 
	 * @param preg the {@link Pregnancy} to be updated
	 * @return true if the tuple is updated correctly
	 */
	public boolean updatePregnancy(Pregnancy preg){
		try {
			return ioOperations.updatePregnancy(preg);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	/**
	 * 
	 * @param visit the {@link PregnancyVisit} to be updated
	 * @return true if the tuple is updated correctly
	 */
	public boolean updateVisit(PregnancyVisit visit){
		return ioOperationsVisit.updatePregnancyVisit(visit.getVisitId(), visit.getDate(), visit.getNextVisitdate(),
				visit.getTreatmenttype(), visit.getNote(), visit.getType());
	}
	/**
	 * 
	 * @param visitid the id of the visit
	 * @param examresult the {@link PregnancyExamResult} to be updated
	 * @return true if the tuple is updated correctly
	 */
	public boolean updateExamResult(int visitid, PregnancyExamResult examresult){
			return ioOperationsVisit.updateExamResult(visitid, examresult.getExamCode(), examresult.getOutcome());
	}
	/**
	 * 
	 * @param visitid - the id of the {@link PregnancyVisit}
	 * @return <code>true</code> if the visit and the related examresults are deleted correctly
	 */
	public boolean deletePregnancyVisitAndResults(int visitid){
		try {
			return ioOperationsVisit.deleteVisit(visitid);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	/**
	 * deletes a Pregnancy from the database, but leaves Admission records untouched
	 * @param pregid the id of the {@link Pregnancy}
	 * @return true if the pregnancy is deleted correctly
	 */
	public boolean deletePregnancy(int pregid){
		try {
			//ioOperations.deleteAllDeliveryOfPregnancy(pregid);
			//ioOperations.deletePregnancyAdmission(pregid);
			return ioOperations.deletePregnancy(pregid);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * 
	 * @param visitid the {@link PregnancyVisit}
	 * @return the Hashmap with the examcode as key and the Examresult as value
	 */
	public HashMap<String, PregnancyExamResult> getExamResults(int visitid){
			return ioOperationsVisit.getExamResults(visitid);
	}
	
//	/**
//	 * 
//	 * @param admId the id of the {@link Admission}
//	 * @return the {@link Admission} with the specified id
//	 */
//	public Admission getAdmission(int admId){
//		if(ioOperationsAdmission == null)
//			ioOperationsAdmission = new IoOperationsDelivery();
//		return ioOperationsAdmission.getAdmission(admId);
//	}
	
	public ArrayList<PregnancyPatient> getPregnancyAdmittedPatients(String regex) {
		try {
			return ioOperations.getPregnancyAdmittedPatients(regex);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<PregnancyPatient> getPregnancyAdmittedPatients() {
		try {
			return ioOperations.getPregnancyAdmittedPatients();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * @param patient the {@link Patient}
	 * @return true if the patient has an ongoing pregnancy (without delivery)
	 */
	public boolean hasActivePregnancy(Patient patient) {
		try {
			return ioOperations.hasActivePregnancy(patient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * @param patient the {@link Patient}
	 * @return true if the patient has an ongoing pregnancy (without delivery)
	 */
	public boolean hasRelatedPregnancy(Delivery delivery) {
		try {
			return ioOperations.hasRelatedPregnancy(delivery);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * @param patid - the the id of the {@link Patient}
	 * @return the active {@link Pregnancy} for the given patid, <code>null</code> otherwise
	 */
	public Pregnancy getActivePregnancy(int patid) {
		try {
			return ioOperations.getActivePregnancy(patid);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

}
