package org.isf.disease.manager;

/*------------------------------------------
 * DiseaseBrowserManager - Class that provides gui separation from database operations and gives some
 * 						   useful logic manipulations of the dinamic data (memory)
 * -----------------------------------------
 * modification history
 * 25/01/2006 - Rick, Vero, Pupo  - first beta version 
 * 08/11/2006 - ross - added getDiseaseOpd members, and getDiseaseIpd  
 * 					   to get only opd/ipd related diseases
 *------------------------------------------*/

import org.isf.disease.model.Disease;
import org.isf.disease.service.IoOperations;
import org.isf.distype.model.DiseaseType;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * The manage class for the disease module.
 */
public class DiseaseBrowserManager {

	private IoOperations ioOperations = new IoOperations();

	/**
	 * Returns all the stored {@link Disease} with ODP flag <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @return the stored diseases with ODP flag true.
	 */
	public ArrayList<Disease> getDiseaseOpd() {
		try {
			return ioOperations.getDiseases(null,true,false,false);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all diseases, deleted ones also
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @return the stored diseases.
	 */
	public ArrayList<Disease> getDiseaseAll() {
		try {
			return ioOperations.getDiseases(null,false,false,false);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all the stored {@link Disease} with the specified typecode and flag ODP true.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param typecode the filter typecode.
	 * @return the retrieved diseases.
	 */
	public ArrayList<Disease> getDiseaseOpd(String typecode) {
		try {
			return ioOperations.getDiseases(typecode,true,false,false);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves a list of {@link Disease} objects that suite the selected {@link Patient}.
	 * @param patient {@link Patient} the patient selected.
	 * @return {@link ArrayList} of {@link Disease} objects.
     */
	public ArrayList<Disease> getDiseaseOpd(Patient patient) {
		try {
			return ioOperations.getOpdDiseases(
					patient.getSex(),
					patient.getMonthsAtDate(new GregorianCalendar()),
					null,
					false);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves a list of {@link Disease} objects that suit the selected {@link Patient} and {@link DiseaseType}.
	 * @param patient {@link Patient} the patient.
	 * @param diseaseType {@link DiseaseType} the disease type.
	 * @param chronic boolean value, if set to <code>true</code> only chronic diseases are retrieved.
     * @return {@link ArrayList} of {@link Disease} objects.
     */
	public ArrayList<Disease> getDiseaseOpd(Patient patient, DiseaseType diseaseType, boolean chronic) {
		try {
			return ioOperations.getOpdDiseases(patient.getSex(),
					patient.getMonthsAtDate(new GregorianCalendar()),
					diseaseType != null ? diseaseType.getCode() : null,
					chronic);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all the stored {@link Disease} with IPD_OUT flag <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @return the stored disease with IPD flag <code>true</code>.
	 */
	public ArrayList<Disease> getDiseaseIpdOut() {
		try {
			return ioOperations.getDiseases(null,false,false,true);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all the stored {@link Disease} with the specified typecode and the flag IPD_OUT <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param typecode the filter typecode.
	 * @return the retrieved diseases.
	 */
	public ArrayList<Disease> getDiseaseIpdOut(String typecode) {
		try {
			return ioOperations.getDiseases(typecode,false,false,true);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns all the stored {@link Disease} with IPD_IN flag <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @return the stored disease with IPD flag <code>true</code>.
	 */
	public ArrayList<Disease> getDiseaseIpdIn() {
		try {
			return ioOperations.getDiseases(null,false,true,false);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all the stored {@link Disease} with the specified typecode and the flag IPD_IN <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param typecode the filter typecode.
	 * @return the retrieved diseases.
	 */
	public ArrayList<Disease> getDiseaseIpdIn(String typecode) {
		try {
			return ioOperations.getDiseases(typecode,false,true,false);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns both OPD and IPDs diseases.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @return the stored diseases.
	 */
	public ArrayList<Disease> getDisease() {
		try {
			return ioOperations.getDiseases(null,true,true,true);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all OPD and IPDs {@link Disease} with the specified typecode.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param typecode the filter typecode.
	 * @return all the diseases with the specified typecode.
	 */
	public ArrayList<Disease> getDisease(String typecode) {
		try {
			return ioOperations.getDiseases(typecode,false,false,false);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public Disease getDiseaseByCode(String code) {
		try {
			return ioOperations.getDiseaseByCode(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Stores the specified {@link Disease}. 
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param disease the disease to store.
	 * @return <code>true</code> if the disease has been stored, <code>false</code> otherwise.
	 */
	public boolean newDisease(Disease disease) {
		try {
			return ioOperations.newDisease(disease);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Updates the specified {@link Disease}.
	 * If the disease has been updated concurrently a overwrite confirmation message is shown.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param disease the disease to update.
	 * @return <code>true</code> if the disease has been updated, <code>false</code> otherwise.
	 */
	public boolean updateDisease(Disease disease) {
		try {
			boolean modified = ioOperations.hasDiseaseModified(disease);

			if (modified) {
				String message = MessageBundle.getMessage("angal.disease.thedatahasbeenupdatedbysomeoneelse") +	MessageBundle.getMessage("angal.disease.doyouwanttooverwritethedata");
				int response = JOptionPane.showConfirmDialog(null, message, MessageBundle.getMessage("angal.disease.select"), JOptionPane.YES_NO_OPTION);
				boolean overWrite = response == JOptionPane.OK_OPTION;
				if (!overWrite) return false;
			}		
			return ioOperations.updateDisease(disease);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Mark as deleted the specified {@link Disease}.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param disease the disease to make delete.
	 * @return <code>true</code> if the disease has been marked, <code>false</code> otherwise.
	 */
	public boolean deleteDisease(Disease disease) {

		try {
			return ioOperations.deleteDisease(disease);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Check if the specified code is used by other {@link Disease}s.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param code the code to check.
	 * @return <code>true</code> if it is already used, <code>false</code> otherwise.
	 */
	public boolean codeControl(String code) {
		try {
			return ioOperations.isCodePresent(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if the specified description is used by a disease with the specified type code.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param description the description to check.
	 * @param typeCode the disease type code.
	 * @return <code>true</code> if is used, <code>false</code> otherwise.
	 */
	public boolean descriptionControl(String description,String typeCode) {
		try {
			return ioOperations.isDescriptionPresent(description,typeCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

}
