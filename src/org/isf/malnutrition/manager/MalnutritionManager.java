package org.isf.malnutrition.manager;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;

import org.isf.admission.model.Admission;
import org.isf.generaldata.MessageBundle;
import org.isf.malnutrition.model.MalnutritionVisit;
import org.isf.malnutrition.service.IoOperation;
import org.isf.utils.exception.OHException;

/**
 * Manager for malnutrition module.
 */
public class MalnutritionManager {
	
	public MalnutritionManager() {}
	
	private IoOperation ioOperation = new IoOperation();

	/**
	 * Retrieves all the {@link MalnutritionVisit} associated to the given admission id.
	 * In case of wrong parameters an error message is shown and <code>null</code> value is returned.
	 * In case of error a message error is shown and an empty list is returned.
	 * @param admissionId the admission id to use as filter.
	 * @return all the retrieved malnutrition or <code>null</code> if the specified admission id is <code>null</code>.
	 */
	public List<MalnutritionVisit> getMalnutritionVisits(int admissionId){
		List<MalnutritionVisit> malnutritionVisits = new ArrayList<MalnutritionVisit>();
		if(admissionId == 0) {
			JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.malnutrition.nonameselected"));
			return null;
		}
		try {
			malnutritionVisits = ioOperation.getMalnutritionVisits(admissionId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return malnutritionVisits;
	}
	
	/**
	 * Saves {@link MalnutritionVisit} instances. The malnutritionVisit object is updated with the generated id.
	 * In case of wrong parameters an error message is shown and <code>false</code> value is returned.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param malnutritionVisits the malnutritionVisit to store.
	 * @return the ID of the {@link MalnutritionVisit} record inserted, 0 otherwise.
	 */
	public boolean saveMalnutritionVisits(List<MalnutritionVisit> malnutritionVisits){

		if(malnutritionVisits == null || malnutritionVisits.size() == 0){
			JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.selectarow"));
			return false;
		}
		boolean result = false;
		try {
			result = ioOperation.saveMalnutritionVisits(malnutritionVisits);
			result &= ioOperation.setMalnutrition(malnutritionVisits.get(0).getAdmissionId());
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, "Could not save the Malnutrition Visit instance: " + e.getMessage());
		}
		return result;
	}

	/**
	 * Deletes the specified {@link MalnutritionVisit}.
	 * In case of wrong parameters an error message is shown and <code>false</code> value is returned.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param admissionId the ID of the {@link Admission} instance to which the visit referes.
	 * @return <code>true</code> if the malnutritionVisit has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteMalnutrition(int admissionId){
		boolean result = false;
		try {
			result = ioOperation.deleteMalnutritionVisit(admissionId);
			result &= ioOperation.unsetMalnutrition(admissionId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, "Could not delete Malnutrition Visit instance: " + e.getMessage());
		}
		return result;
	}
	
	
	/**
	 * TODO: Description Needed
	 * @param patientId
	 * @param visitDate
	 * @param visitType
	 * @return
	 */
	public MalnutritionVisit findPreviousVisitByType(int patientId, GregorianCalendar visitDate, String visitType) {
		try {
			 return ioOperation.findPreviousVisitByType(patientId, visitDate, visitType);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, "Could not retrieve data from the database: " + e.getMessage());
		}
		return null;
	}

}
