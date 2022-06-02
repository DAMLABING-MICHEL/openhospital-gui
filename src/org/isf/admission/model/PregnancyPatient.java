package org.isf.admission.model;

import org.isf.patient.model.*;
import org.isf.pregnancy.model.Pregnancy;

public class PregnancyPatient extends AdmittedPatient{

	private Pregnancy pregnancy;
		
	public PregnancyPatient(Patient patient, Admission admission, Pregnancy pregnancy) {
		super(patient, admission);
		this.pregnancy = pregnancy;
	}
	public PregnancyPatient(AdmittedPatient admittedPatient, Pregnancy pregnancy) {
		super(admittedPatient.getPatient(), admittedPatient.getAdmission());
		this.pregnancy = pregnancy;
	}
	public Pregnancy getPregnancy() {
		return pregnancy;
	}
	public void setPregnancy(Pregnancy pregnancy) {
		this.pregnancy = pregnancy;
	}
}
