package org.isf.utils.db.maintenance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MedicalWardPrescription;
import org.isf.medicalstockward.model.MedicalWardPrescriptionDetail;
import org.isf.medicalstockward.model.MissingMedical;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.patient.model.Patient;
import org.isf.serviceprinting.print.MedicalWardForPrint;
import org.isf.serviceprinting.print.MovementForPrint;
import org.isf.serviceprinting.print.MovementWardForPrint;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.joda.time.DateTime;

public class MovWardBrowserManager {

	IoOperations ioOperations;

	public MovWardBrowserManager(){
		ioOperations=new IoOperations();
	}

	/**
	 * Gets all the {@link MovementWard}s.
	 * If an error occurs a message error is shown and the <code>null</code> value is returned.
	 * @return all the retrieved movements ward.
	 */
	public ArrayList<MovementWard> getMovementWard() {
		try {
			return ioOperations.getWardMovements(null, null, null);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Gets all the {@link MedicalWard}s associated to the specified ward.
	 * If an error occurs a message error is shown and the <code>null</code> value is returned.
	 * @param wardId the ward id.
	 * @return the retrieved medicals.
	 */
	public ArrayList<MedicalWard> getMedicalsWard(String wardId) {
		try {
			return ioOperations.getMedicalsWard(wardId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Gets all the movement ward with the specified criteria.
	 * If an error occurs a message error is shown and the <code>null</code> value is returned.
	 * @param wardId the ward id.
	 * @param dateFrom the lower bound for the movement date range.
	 * @param dateTo the upper bound for the movement date range.
	 * @return all the retrieved movements.
	 */
	public ArrayList<MovementWard> getMovementWard(String wardId, GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getWardMovements(wardId, dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Persists the specified movement.
	 * If an error occurs a message error is shown and the <code>false</code> value is returned.
	 * @param newMovement the movement to persist.
	 * @return <code>true</code> if the movement has been persisted, <code>false</code> otherwise.
	 */
	public boolean newMovementWard(MovementWard newMovement) {
		try {
			return ioOperations.newMovementWard(newMovement);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Persists the specified movements.
	 * If an error occurs a message error is shown and the <code>false</code> value is returned.
	 * @param newMovements the movements to persist.
	 * @return <code>true</code> if the movements have been persisted, <code>false</code> otherwise.
	 */
	public boolean newMovementWard(ArrayList<MovementWard> newMovements) {
		try {
			return ioOperations.newMovementWard(newMovements);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Updates the specified {@link MovementWard}.
	 * If an error occurs a message error is shown and the <code>false</code> value is returned.
	 * @param updateMovement the movement ward to update.
	 * @return <code>true</code> if the movement has been updated, <code>false</code> otherwise.
	 */
	public boolean updateMovementWard(MovementWard updateMovement) {
		try {
			return ioOperations.updateMovementWard(updateMovement);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Deletes the specified {@link MovementWard}.
	 * If an error occurs a message error is shown and the <code>false</code> value is returned.
	 * @param movement the movement to delete.
	 * @return <code>true</code> if the movement has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteMovementWard(MovementWard movement) {
		try {
			return ioOperations.deleteMovementWard(movement);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Gets the current quantity for the specified {@link Medical}.
	 * If an error occurs a message error is shown and the <code>0</code> value is returned.
	 * @param ward the medical ward, or <code>null</code>.
	 * @param medical the medical.
	 * @return the total quantity.
	 */
	public int getCurrentQuantity(Ward ward, Medical medical) {
		try {
			return ioOperations.getCurrentQuantity(ward, medical);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	/**
	 * Store the supplied list of {@link Medical}s and quantities for future analysis
	 * @param missingMedical
	 * @param ward
	 * @return <code>true</code> if the list has been saved, <code>false</code> otherwise.
	 */
	public boolean storeMissingMedicals(ArrayList<MedicalWard> missingMedical, Ward ward) {
		try {
			return ioOperations.storeMissingMedicals(missingMedical, ward);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Gets all missing {@link MissingMedical}s associated to specified {@link Ward}.
	 * @param wardId the ward id.
	 * @param dateTo 
	 * @param dateFrom 
	 * @return the retrieved medicals.
	 */
	public ArrayList<MissingMedical> getMissingMedicals(String wardId, GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getMissingMedicals(wardId, dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Stores the {@link MedicalWardPrescription} and its {@link MedicalWardPrescriptionDetail}s
	 * @param prescription
	 * @param details
	 * @return <code>true</code> if the prescription has been store, <code>false</code> otherwise
	 */
	public boolean storePrescription(MedicalWardPrescription prescription, ArrayList<MedicalWardPrescriptionDetail> details) {
		try {
			return ioOperations.storePrescription(prescription, details);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Retrieves {@link MedicalWardPrescription} for the specified {@link Ward}
	 * within the specified range of dates
	 * @param wardSelected
	 * @param fromDate
	 * @param toDate
	 * @return the list of {@link MedicalWardPrescription}
	 */
	public ArrayList<MedicalWardPrescription> getPrescriptions(Ward wardSelected, GregorianCalendar fromDate, GregorianCalendar toDate) {
		try {
			return ioOperations.getPrescriptions(wardSelected, fromDate, toDate);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Retrieves pending {@link MedicalWardPrescription} for the specified {@link Ward} and today
	 * @param wardSelected
	 * @return the list of {@link MedicalWardPrescription}
	 */
	public ArrayList<MedicalWardPrescription> getPendingPrescriptions(Ward wardSelected) {
		GregorianCalendar fromDate = new DateTime().toDateMidnight().toGregorianCalendar();
		GregorianCalendar toDate = new DateTime().toDateMidnight().plusDays(1).toGregorianCalendar();
		return getPendingPrescriptions(wardSelected, fromDate, toDate);
	}
	
	/**
	 * Retrieves pending {@link MedicalWardPrescription} for the specified {@link Ward}
	 * within the specified range of dates
	 * @param wardSelected
	 * @param fromDate
	 * @param toDate
	 * @return the list of pending {@link MedicalWardPrescription}
	 */
	public ArrayList<MedicalWardPrescription> getPendingPrescriptions(Ward wardSelected, GregorianCalendar fromDate, GregorianCalendar toDate) {
		try {
			return ioOperations.getPendingPrescriptions(wardSelected, fromDate, toDate);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Retrieves the {@link MedicalWardPrescriptionDetail}s of a {@link MedicalWardPrescription}
	 * @param prescription
	 * @return the {@link MedicalWardPrescriptionDetail}
	 */
	public ArrayList<MedicalWardPrescriptionDetail> getPrescriptionDetail(MedicalWardPrescription prescription) {
		try {
			return ioOperations.getPrescriptionDetail(prescription);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Dispense a medical prescription related to a Bill
	 * 
	 * @param billId
	 * @return
	 */
	public boolean dispenseMedicalPrescription(int billId) {
		try {
			MedicalWardPrescription prescription = ioOperations.getPrescription(billId);
			if (prescription == null || prescription.getStatus() != 0) return false;
			Patient patient = prescription.getPatient();
			ArrayList<MedicalWardPrescriptionDetail> details = ioOperations.getPrescriptionDetail(prescription);
			ArrayList<MovementWard> movementsWard = new ArrayList<MovementWard>();
			for (MedicalWardPrescriptionDetail detail : details) {
				movementsWard.add(new MovementWard(
						prescription.getWard(),
						new GregorianCalendar(),
						true, patient, patient.getAge(),patient.getWeight(),patient.getName(),
						detail.getMedical(), detail.getQuantity(), detail.getUnits())
				);
			}
			MovWardBrowserManager wardMan = new MovWardBrowserManager();
			return wardMan.newMovementWard(movementsWard);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Dispense a medical prescription related to a Bill
	 * 
	 * @param billId
	 * @return
	 */
	public boolean dispenseMedicalPrescription(int billId, GregorianCalendar date, boolean proceed) {
		MovWardBrowserManager wardMan = new MovWardBrowserManager();
		try {
			MedicalWardPrescription prescription = ioOperations.getPrescription(billId);
			if (prescription == null || prescription.getStatus() != 0) return false;
			Patient patient = prescription.getPatient();
			ArrayList<MedicalWardPrescriptionDetail> details = ioOperations.getPrescriptionDetail(prescription);
			ArrayList<MovementWard> movementsWard = new ArrayList<MovementWard>();
			for (MedicalWardPrescriptionDetail detail : details) {
				movementsWard.add(new MovementWard(
						prescription.getWard(),
						date,
						true, patient, patient.getAge(),patient.getWeight(),patient.getName(),
						detail.getMedical(), detail.getQuantity(), detail.getUnits())
				);
			}
			if (proceed) {
				return wardMan.newMovementWard(movementsWard);
			} else return true;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Sets a {@link MedicalWardPrescription} as 'done' (status = 1);
	 * @param prescription
	 */
	public void closeMedicalPrescription(MedicalWardPrescription prescription) {
		try {
			ioOperations.closeMedicalPrescription(prescription);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	/**
	 * Sets the {@link MedicalWardPrescription} as 'done' (status = 1);
	 * @param billID - the bill the prescription refers to
	 */
	public void closeMedicalPrescription(int billID) {
		try {
			ioOperations.closeMedicalPrescription(billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	/**
	 * Sets the {@link MedicalWardPrescription} as 'fake-done' (status = 4);
	 * @param billID - the bill the prescription refers to
	 */
	public void fakeCloseMedicalPrescription(int billID) {
		try {
			ioOperations.fakeCloseMedicalPrescription(billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	/**
	 * Sets the {@link MedicalWardPrescription} as 'cancelled' (status = 2);
	 * @param billID - the bill the prescription refers to
	 */
	public void deleteMedicalPrescription(int billID) {
		try {
			ioOperations.deleteMedicalPrescription(billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	/**
	 * Sets the {@link MedicalWardPrescription} as 'fake-cancelled' (status = 5);
	 * @param billID - the bill the prescription refers to
	 */
	public void fakeDeleteMedicalPrescription(int billID) {
		try {
			ioOperations.fakeDeleteMedicalPrescription(billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	public ArrayList<MovementWardForPrint> convertMovementWardForPrint(ArrayList<MovementWard> wardOutcomes) {
		ArrayList<MovementWardForPrint> movPrint = new ArrayList<MovementWardForPrint>();
		for (MovementWard mov : wardOutcomes) {
			movPrint.add(new MovementWardForPrint(mov));
		}
		Collections.sort(movPrint, new ComparatorMovementWardForPrint());
		return movPrint;
	}

	public ArrayList<MovementForPrint> convertMovementForPrint(ArrayList<Movement> wardIncomes) {
		ArrayList<MovementForPrint> movPrint = new ArrayList<MovementForPrint>();
		for (Movement mov : wardIncomes) {
			movPrint.add(new MovementForPrint(mov));
		}
		Collections.sort(movPrint, new ComparatorMovementForPrint());
		return movPrint;
	}

	public ArrayList<MedicalWardForPrint> convertWardDrugs(Ward wardSelected, ArrayList<MedicalWard> wardDrugs) {
		ArrayList<MedicalWardForPrint> drugPrint = new ArrayList<MedicalWardForPrint>();
		for (MedicalWard mov : wardDrugs) {
			drugPrint.add(new MedicalWardForPrint(mov, wardSelected));
		}
		Collections.sort(drugPrint);
		return drugPrint;
	}
	
	public ArrayList<MovementForPrint> convertWardMissing(Ward wardSelected, ArrayList<MissingMedical> wardMissing) {
		ArrayList<MovementForPrint> movPrint = new ArrayList<MovementForPrint>();
		for (MissingMedical mov : wardMissing) {
			movPrint.add(new MovementForPrint(mov));
		}
		Collections.sort(movPrint, new ComparatorMovementForPrint());
		return movPrint;
	}
	
	class ComparatorMovementWardForPrint implements Comparator<MovementWardForPrint>  {
		@Override
		public int compare(MovementWardForPrint o1, MovementWardForPrint o2) {
			int byDate = o2.getDate().compareTo(o1.getDate());
			if (byDate == 0) {
				return o1.getDescription().compareTo(o2.getDescription());
			} else {
				return byDate;
			}
		}
	}
	
	class ComparatorMovementForPrint implements Comparator<MovementForPrint>  {
		@Override
		public int compare(MovementForPrint o1, MovementForPrint o2) {
			int byDate = o2.getDate().compareTo(o1.getDate());
			if (byDate == 0) {
				return o1.getDescription().compareTo(o2.getDescription());
			} else {
				return byDate;
			}
		}
	}
}
