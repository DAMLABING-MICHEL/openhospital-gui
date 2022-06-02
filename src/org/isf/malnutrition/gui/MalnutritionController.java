package org.isf.malnutrition.gui;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;

import org.isf.admission.model.Admission;
import org.isf.generaldata.MessageBundle;
import org.isf.malnutrition.manager.MalnutritionManager;
import org.isf.malnutrition.model.MalnutritionVisit;
import org.isf.malnutrition.model.TfuPatient;
import org.isf.menu.gui.MainMenu;
import org.isf.patient.manager.PatientBrowserManager;
import org.joda.time.LocalDate;
import org.joda.time.Months;

/**
 * Manager for malnutrition module.
 */
public class MalnutritionController {

	private static MalnutritionController instance;
	private PatientBrowserManager patientBrowserManager = new PatientBrowserManager();
	private MalnutritionManager malnutritionManager = new MalnutritionManager();
	private List<MalnutritionVisit> visits;
	private Admission admission;
	private MalnutritionVisit admissionVisit;
	private MalnutritionVisit dischargeVisit;
	private MalnutritionVisit lastVisit;
	private TfuPatient patient;

	public static MalnutritionController getInstance() {
		if (instance == null)
			instance = new MalnutritionController();
		return instance;
	}

	private MalnutritionController() {}

	public void clearState() {
		this.admission = null;
		this.admissionVisit = null;
		this.dischargeVisit = null;
		this.visits.clear();
	}

	public void initializeState(Admission admission) {
		if (admission != null) {
			this.admission = admission;
			visits = malnutritionManager.getMalnutritionVisits(admission.getId());
			if (visits.size() > 0) {
				this.admissionVisit = findVisitByType(visits, MalnutritionVisit.ADMISSION);
				this.dischargeVisit = findVisitByType(visits, MalnutritionVisit.DISCHARGE);
			}
			if (admissionVisit == null) {
				this.patient = new TfuPatient(patientBrowserManager.getPatient(admission.getPatId()));
				admissionVisit = new MalnutritionVisit(admission, patient, MalnutritionVisit.ADMISSION);
				admissionVisit.setVisitDate(admission.getAdmDate());
			} else {
				this.patient = admissionVisit.getPatient();
			}
			if (dischargeVisit == null)
				dischargeVisit = new MalnutritionVisit(admission, patient, MalnutritionVisit.DISCHARGE);

			this.lastVisit = findPreviousVisitByType(
					admission.getPatId(),
					admissionVisit.getVisitDate(),
					MalnutritionVisit.ADMISSION);
//			admissionVisit.setReadmission(lastVisit != null);
//			dischargeVisit.setReadmission(lastVisit != null);
		}
	}

	private static MalnutritionVisit findVisitByType(List<MalnutritionVisit> malnutritionVisits, String type) {
		for (MalnutritionVisit malnutritionVisit : malnutritionVisits) {
			if (malnutritionVisit.getType().equals(type))
				return malnutritionVisit;
		}
		return null;
	}

	public boolean isAdmissionStillOpen() {
		return admission.getAdmitted() == 1;
	}

	public MalnutritionVisit getAdmissionVisit() {
		return admissionVisit;
	}

	public MalnutritionVisit getDischargeVisit() {
		return dischargeVisit;
	}

	public MalnutritionVisit getLastVisit() {
		return lastVisit;
	}

	public TfuPatient getPatient() {
		return patient;
	}

	public Admission getAdmission() {
		return admission;
	}

	public boolean saveMalnutritionVisits(MalnutritionVisitEdit malnutritionVisitEdit,
										  MalnutritionVisitPanel admissionPanel,
										  MalnutritionVisitPanel dischargePanel) {

		if (!checkPatientOverriddenFields(malnutritionVisitEdit))
			return false;

		if (!checkMalnutritionVisitFields(admissionPanel))
			return false;

		if (!isAdmissionStillOpen() && (!checkMalnutritionVisitFields(dischargePanel) ||
				!checkVisitDates(admissionPanel.getVisitDateFieldValue(),
						dischargePanel.getVisitDateFieldValue()))) {
			return false;
		}

		TfuPatient patient = admissionVisit.getPatient();
		patient.setName(malnutritionVisitEdit.getPatientNameFieldValue());
		patient.setGender(malnutritionVisitEdit.getPatientGenderFieldValue());
		patient.setCity(malnutritionVisitEdit.getPatientCityFieldValue());
		patient.setAddress(malnutritionVisitEdit.getPatientAddressFieldValue());
		patient.setMonthsAtAdmissionTime(computeMonthsFromYearsAndMonths(
				malnutritionVisitEdit.getPatientYearsFieldValue(),
				malnutritionVisitEdit.getPatientMonthsFieldValue()));
		admissionVisit.setPatient(patient);
		admissionVisit.setClosestHealthCenter(malnutritionVisitEdit.getClosestHealthCenterFieldValue());
		
		admissionVisit.setType("admission");
		admissionVisit.setExtCode(malnutritionVisitEdit.getExternalCodeFieldValue());
		admissionVisit.setVisitDate(admissionPanel.getVisitDateFieldValue());
		admissionVisit.setUser(MainMenu.getUser());
		admissionVisit.setAdmission(getAdmission());
		admissionVisit.setAgeInMonthsAtVisitTime(patient.getMonthsAtAdmissionTime());
		admissionVisit.setWeight(admissionPanel.getWeightFieldValue());
		admissionVisit.setHeight(admissionPanel.getHeightFieldValue());
		admissionVisit.setMuac(admissionPanel.getMuacFieldValue());
		admissionVisit.setWh(admissionPanel.getWhFieldValue());
		admissionVisit.setOedema(admissionPanel.getOedemaFieldValue());
		admissionVisit.setRelapse(admissionPanel.isRelapseFieldChecked());
		admissionVisit.setReadmission(admissionPanel.isReadmissionFieldChecked());
		admissionVisit.setKm(admissionPanel.getKmFieldValue());
		admissionVisit.setReferredBy(admissionPanel.getReferredByFieldValue());
		admissionVisit.setFromOtherOTP(admissionPanel.getFromOtherOTPFieldValue());
		admissionVisit.setFromOtherTFP(admissionPanel.getFromOtherTFPFieldValue());
		
		if (!isAdmissionStillOpen()) {
			patient.setMonthsAtAdmissionTime(patient.getMonthsAtAdmissionTime() +
					Months.monthsBetween(LocalDate.fromCalendarFields(
					admissionPanel.getVisitDateFieldValue()),
					LocalDate.fromCalendarFields(dischargePanel.getVisitDateFieldValue())).getMonths());
			dischargeVisit.setPatient(patient);
			dischargeVisit.setFollowUp(malnutritionVisitEdit.getFollowUpFieldValue());
			dischargeVisit.setType("discharge");
			dischargeVisit.setUser(MainMenu.getUser());
			dischargeVisit.setAdmission(getAdmission());
			dischargeVisit.setOutcome(dischargePanel.getOutcomeFieldValue());
			dischargeVisit.setExtCode(malnutritionVisitEdit.getExternalCodeFieldValue());
			dischargeVisit.setClosestHealthCenter(malnutritionVisitEdit.getClosestHealthCenterFieldValue());
			dischargeVisit.setVisitDate(dischargePanel.getVisitDateFieldValue());
			dischargeVisit.setWeight(dischargePanel.getWeightFieldValue());
			dischargeVisit.setHeight(dischargePanel.getHeightFieldValue());
			dischargeVisit.setMuac(dischargePanel.getMuacFieldValue());
			dischargeVisit.setWh(dischargePanel.getWhFieldValue());
			dischargeVisit.setMinWeight(dischargePanel.getMinWeightFieldValue());
			dischargeVisit.setMinWeightDate(dischargePanel.getMinWeightDateFieldValue());
			dischargeVisit.setTb(dischargePanel.getTbFieldValue());
			dischargeVisit.setHiv(dischargePanel.getHivFieldValue());
			dischargeVisit.setSepsis(dischargePanel.getSepsisFieldValue());
			dischargeVisit.setAnaemia(dischargePanel.getAnaemiaFieldValue());
			dischargeVisit.setPneumonia(dischargePanel.getPneumoniaFieldValue());
			dischargeVisit.setMalaria(dischargePanel.getMalariaFieldValue());
			dischargeVisit.setCP(dischargePanel.getCPFieldValue());
			dischargeVisit.setRicketts(dischargePanel.getRickettsFieldValue());
			dischargeVisit.setOther(dischargePanel.getOtherFieldValue());
			dischargeVisit.setTransferToOtherOTP(dischargePanel.getTransferToOtherOTPFieldValue());
			dischargeVisit.setMedicalTransfer(dischargePanel.getMedicalTransferFieldValue());
			dischargeVisit.setRemarks(dischargePanel.getRemarksFieldValue());
		}

		visits.clear();
		visits.add(0, admissionVisit);
		if (!isAdmissionStillOpen())
			visits.add(dischargeVisit);
		return malnutritionManager.saveMalnutritionVisits(visits);
	}

	int computeMonthsFromYearsAndMonths(int years, int months) {
		return years * 12 + months;
	}

	public boolean isEditMode() {
		return admissionVisit.getId() > 0 || dischargeVisit.getId() > 0;
	}

	public MalnutritionVisit findPreviousVisitByType(int paitentId, GregorianCalendar visitDate, String visitType) {
		if (visitDate == null)
			visitDate = new GregorianCalendar();
		return malnutritionManager.findPreviousVisitByType(paitentId, visitDate, visitType);
	}
	
	public boolean deleteMalnutrition(int admissionId){
		return malnutritionManager.deleteMalnutrition(admissionId);
	}

	public boolean checkMalnutritionVisitFields(MalnutritionVisitPanel panel) {
		MalnutritionVisit malnutritionVisit = panel.getMalnutritionVisit();

		if (!malnutritionVisit.getType().equals(MalnutritionVisit.DISCHARGE) ||
				panel.getOutcomeFieldValue() == null ||
				!panel.getOutcomeFieldValue().equals(MalnutritionVisit.UNKNOWN)) {

			if (malnutritionVisit.getType().equals(MalnutritionVisit.DISCHARGE) && panel.getOutcomeFieldValue() == null) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.invaliddischargeoutcome"));
				return false;
			}

			if (panel.getVisitDateFieldValue() == null) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.invalid" +
						malnutritionVisit.getType() + "visitdate"));
				return false;
			}
//		if (panel.getVisitDateFieldValue().getTimeInMillis() - malnutritionVisit.getPatient().getBirthDate().getTime() < 0) {
//			JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.patientnotyetborn"));
//			return false;
//		}
			if (panel.getVisitDateFieldValue().getTimeInMillis() - new GregorianCalendar().getTimeInMillis() > 0) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.visitinthefuturenotallowed"));
				return false;
			}
			if (panel.getWeightFieldValue() == null || panel.getWeightFieldValue().equals(new BigDecimal(0))) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.invalid" +
						malnutritionVisit.getType() + "weight"));
				return false;
			}
			if (panel.getHeightFieldValue() == null || panel.getHeightFieldValue().equals(new BigDecimal(0))) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.invalid" +
						malnutritionVisit.getType() + "height"));
				return false;
			}
			if (panel.getWhFieldValue() == null) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.invalid" +
						malnutritionVisit.getType() + "wh"));
				return false;
			}
			if (malnutritionVisit.getType().equals(MalnutritionVisit.ADMISSION) && panel.getKmFieldValue() == null) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.invalidadmissionkm"));
				return false;
			}
		}
		return true;
	}

	public boolean checkVisitDates(GregorianCalendar admissionDate, GregorianCalendar dischargeDate) {
		if (dischargeDate.getTimeInMillis() - admissionDate.getTimeInMillis() < 0) {
			JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.dischargedatebeforeadmissiondate"));
			return false;
		}
		return true;
	}

	public boolean checkPatientOverriddenFields(MalnutritionVisitEdit malnutritionVisitEdit) {
		if (malnutritionVisitEdit.getPatientNameFieldValue() == null ||
				malnutritionVisitEdit.getPatientNameFieldValue().isEmpty()) {
			JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.patientnamenull"));
			return false;
		}
		if (malnutritionVisitEdit.getPatientMonthsFieldValue() == 0 &&
				malnutritionVisitEdit.getPatientYearsFieldValue() == 0) {
			JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.malnutrition.patientagetnull"));
			return false;
		}
		return true;
	}

//	WhReference whReference = WhReference.getInstance();
//	public Integer computeWhValue(BigDecimal patientHeight, BigDecimal patientWeight) {
//		Integer result = null;
//		BigDecimal referenceWeight = whReference.getWhTable().get(patientHeight);
//		if (referenceWeight != null) {
//			BigDecimal weightRatio = patientWeight.divide(referenceWeight, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
//			if (weightRatio.compareTo(new BigDecimal(60)) <= 0)
//				result = 60;
//			else if(weightRatio.compareTo(new BigDecimal(70)) < 0) // Because who did the table messed up the ratio values!!!
//				result = 70;
//			else if(weightRatio.compareTo(new BigDecimal(75)) <= 0)
//				result = 75;
//			else if(weightRatio.compareTo(new BigDecimal(80)) <= 0)
//				result = 80;
//			else if(weightRatio.compareTo(new BigDecimal(85)) < 0)
//				result = 84;
//			else if(weightRatio.compareTo(new BigDecimal(100)) < 0)
//				result = 99;
//			else if(weightRatio.compareTo(new BigDecimal(100)) >= 0)
//				result = 100;
//		}
//		return result;
//	}
}
