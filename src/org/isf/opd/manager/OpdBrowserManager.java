package org.isf.opd.manager;


import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import org.isf.admission.model.Admission;
import org.isf.generaldata.MessageBundle;
import org.isf.opd.model.Opd;
import org.isf.opd.model.OpdRow;
import org.isf.opd.service.IoOperations;
import org.isf.utils.exception.OHException;


/**
 * @author Vero
 * 
 */
public class OpdBrowserManager {
	
	private IoOperations ioOperations = new IoOperations();
	
	/**
	 * return all OPDs of today or one week ago
	 * 
	 * @param oneWeek - if <code>true</code> return the last week, only today otherwise.
	 * @return the list of OPDs. It could be <code>null</code>.
	 */
	public ArrayList<Opd> getOpd(boolean oneWeek){
		return getOpd(oneWeek, "all");
	}

	public ArrayList<Opd> getOpd(boolean oneWeek, String chronicFilter){
		try {
			return ioOperations.getOpdList(oneWeek, chronicFilter);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Opd> getOpd(String diseaseTypeCode,String diseaseCode, GregorianCalendar dateFrom,GregorianCalendar dateTo,int ageFrom, int ageTo,char sex,String newPatient) {
		return getOpd(diseaseTypeCode, diseaseCode, "", "", "", dateFrom, dateTo, ageFrom, ageTo, sex, newPatient);
	}

	public ArrayList<Opd> getOpd(String diseaseTypeCode, String diseaseCode, String patientNameHint,
								 String opdCodeHint, String chronicFilter, GregorianCalendar dateFrom,
								 GregorianCalendar dateTo, int ageFrom, int ageTo, char sex, String newPatient) {
		try {
			return ioOperations.getOpdList(diseaseTypeCode, diseaseCode, patientNameHint, opdCodeHint, chronicFilter, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Return a list of {@link OpdRow} objects providing also the PAT_PCODE table field
	 * @param oneWeek boolean
	 * @param chronicFilter chronic filter value (either 'chronic', 'non-chronic' or 'all')
     * @return a list of {@link OpdRow objects}
     */
	public ArrayList<OpdRow> getOpdRows(boolean oneWeek){
		try {
			return ioOperations.getOpdRows(oneWeek);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<OpdRow> getOpdRows(String diseaseTypeCode, String diseaseCode, String wardCode,
								 String opdCodeHint, String chronicFilter, GregorianCalendar dateFrom,
								 GregorianCalendar dateTo, int ageFrom, int ageTo, char sex, String newPatient) {
		try {
			return ioOperations.getOpdRows(diseaseTypeCode, diseaseCode, wardCode, opdCodeHint, chronicFilter, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public String getPatPCode(int patientId) {
		try {
			return ioOperations.getPatPCode(patientId);
		} catch (OHException ohe) {
			return "";
		}
	}
	
	/**
	 * Returns all {@link Opd}s associated to specified patient ID
	 * @param patientCode - the patient ID
	 * @return the list of {@link Opd}s associated to specified patient ID.
	 * 		   the whole list of {@link Opd}s if <code>0</code> is passed.
	 */
	public ArrayList<Opd> getOpdList(int patientCode) {
		try {
			return ioOperations.getOpdList(patientCode);
		} catch (OHException e) {
			return null;
		}
	}

	/**
	 * insert a new item in the db
	 * 
	 * @param opd {@link Opd}
	 * @return <code>true</code> if the item has been inserted
	 */
	public int newOpd(Opd opd) {
		try {
			return ioOperations.newOpd(opd);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	/**
	 * Updates the specified {@link Opd} object.
	 * @param opd - the {@link Opd} object to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 */
	public boolean updateOpd(Opd opd){
		try {
			boolean recordUpdated = ioOperations.hasOpdModified(opd);

			if (!recordUpdated) { 
				// it was not updated
				return ioOperations.updateOpd(opd);
			} else { 
				// it was updated by someone else
				String message = MessageBundle.getMessage("angal.admission.thedatahasbeenupdatedbysomeoneelse")	+ MessageBundle.getMessage("angal.admission.doyouwanttooverwritethedata");
				int response = JOptionPane.showConfirmDialog(null, message, MessageBundle.getMessage("angal.admission.select"), JOptionPane.YES_NO_OPTION);
				boolean overWrite = response== JOptionPane.OK_OPTION;

				if (overWrite) {
					// the user has confirmed he wants to overwrite the record
					return ioOperations.updateOpd(opd);
				}
			}
			return false;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Delete an {@link Opd} from the db
	 * @param opd - the {@link Opd} to delete
	 * @return <code>true</code> if the item has been deleted. <code>false</code> otherwise.
	 */
	public boolean deleteOpd(Opd opd) {
		try {
			return ioOperations.deleteOpd(opd);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Returns the max progressive number within specified year or within current year if <code>0</code>.
	 * 
	 * @param year
	 * @return <code>int</code> - the progressive number in the year
	 */
	public int getProgYear(int year) {
		try {
			return ioOperations.getProgYear(year);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}
	
	/**
	 * Return the last Opd in time associated with specified patient ID.
	 * @param patientCode - the patient ID
	 * @return last Opd associated with specified patient ID or <code>null</code>
	 */
	public Opd getLastOpd(int patientCode) {
		try {
			return ioOperations.getLastOpd(patientCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Return the next Opd after the specified one, for the same patient
	 * @param opd
	 * @return
	 */
	public Opd getNextOpd(Opd opd) {
		try {
			return ioOperations.getNextOpd(opd);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Return the next Opd after the specified Admission, for the same patient
	 * @param adm
	 * @return
	 */
	public Opd getNextOpd(Admission adm) {
		try {
			return ioOperations.getNextOpd(adm);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
}
