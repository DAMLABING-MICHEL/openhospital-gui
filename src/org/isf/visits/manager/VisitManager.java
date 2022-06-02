/**
 * 
 */
package org.isf.visits.manager;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.isf.generaldata.MessageBundle;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.visits.model.Visit;
import org.isf.visits.service.IoOperations;
import org.joda.time.DateTime;

/**
 * @author Mwithi
 *
 */
public class VisitManager {
	
	private IoOperations ioOperations = new IoOperations();
	
	/**
	 * returns the list of all {@link Visit}s related to a patID
	 * 
	 * @param patID - the {@link Patient} ID. If <code>0</code> return the list of all {@link Visit}s
	 * @return the list of {@link Visit}s
	 */
	public ArrayList<Visit> getVisits(int patID) {
		try {
			return ioOperations.getVisits(patID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * insert a new {@link Visit} for related Patient
	 * 
	 * @param visit - the {@link Visit}
	 * @return the visitID
	 */
	public int newVisit(Visit visit) {
		try {
			return ioOperations.saveVisit(visit);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	/**
	 * inserts or replaces all {@link Visit}s related to a patID
	 * 
	 * @param patID - the {@link Patient} ID
	 * @param visits - the list of {@link Visit}s related to patID. 
	 * @return <code>true</code> if the list has been replaced, <code>false</code> otherwise
	 * @throws OHException 
	 */
	public boolean newVisits(ArrayList<Visit> visits) {
		if (!visits.isEmpty()) {
			DateTime now = new DateTime();
			PatientBrowserManager patMan = new PatientBrowserManager();
		try {
				int patID = visits.get(0).getPatID();
				ioOperations.deleteAllVisits(patID);

				for (Visit visit : visits) {
					
					int visitID = ioOperations.saveVisit(visit);
					if (visitID == 0) return false;
					
					visit.setVisitID(visitID);
				}
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
		return true;
	}
	
	/**
	 * deletes all {@link Visit}s related to a patID
	 * 
	 * @param patID - the {@link Patient} ID
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHException 
	 */
	public boolean deleteAllVisits(int patID) {
		try {
			return ioOperations.deleteAllVisits(patID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	private String prepareSmsFromVisit(Visit visit) {
		
		String note = visit.getNote();
		StringBuilder sb = new StringBuilder(MessageBundle.getMessage("angal.visit.reminderm")).append(": ");
		sb.append(visit.toString());
		if (note != null && !note.equals("")) {
			sb.append(" - ").append(note);
		}
		if (sb.toString().length() > 160) {
		    return sb.toString().substring(0, 160);
		}
		return sb.toString();
	}
}
