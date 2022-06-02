/**
 * MedicalWardPrescription.java - 06/apr/2014
 */
package org.isf.medicalstockward.model;

import java.util.GregorianCalendar;

import org.isf.accounting.model.Bill;
import org.isf.patient.model.Patient;
import org.isf.prescriber.model.Prescriber;
import org.isf.ward.model.Ward;

/**
 * @author Mwithi
 *
 */
public class MedicalWardPrescription implements Comparable<MedicalWardPrescription> {

	private int id;
	private GregorianCalendar date;
	private Patient patient;
	private Ward ward;
	private Bill bill;
	private int status;
	private boolean ipd; //false = opd, true = ipd
	private String ipdWard; //wardID if ipd = true, null otherwise
	private String diseaseOpd; //diseaseID if ipd = false, null otherwise
	private Prescriber prescriber;
	private int opdCode;
	
	/**
	 * 
	 */
	public MedicalWardPrescription() {}
	
	/**
	 * @param bill
	 * @param patient
	 * @param ward
	 * @param ipd
	 * @param ipdWard
	 * @param diseaseOpd
	 * @param prescriber
	 */
	public MedicalWardPrescription(GregorianCalendar date, Bill bill, Patient patient, Ward ward, boolean ipd, String ipdWard, String diseaseOpd, Prescriber prescriber) {
		super();
		this.date = date;
		this.patient = patient;
		this.ward = ward;
		this.bill = bill;
		this.status = 0;
		this.ipd = ipd;
		this.ipdWard = ipdWard;
		this.diseaseOpd = diseaseOpd;
		this.prescriber = prescriber;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the date
	 */
	public GregorianCalendar getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * @return the billID
	 */
	public Bill getBill() {
		return bill;
	}

	/**
	 * @param bill the billID to set
	 */
	public void setBill(Bill bill) {
		this.bill = bill;
	}

	/**
	 * @return the ward
	 */
	public Ward getWard() {
		return ward;
	}

	/**
	 * @param ward the ward to set
	 */
	public void setWard(Ward ward) {
		this.ward = ward;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * @return the ipd
	 */
	public boolean isIpd() {
		return ipd;
	}

	/**
	 * @param ipd the ipd to set
	 */
	public void setIpd(boolean ipd) {
		this.ipd = ipd;
	}

	/**
	 * @return the ipdWard
	 */
	public String getIpdWard() {
		return ipdWard;
	}

	/**
	 * @param ipdWard the ipdWard to set
	 */
	public void setIpdWard(String ipdWard) {
		this.ipdWard = ipdWard;
	}

	/**
	 * @return the diseaseOpd
	 */
	public String getDiseaseOpd() {
		return diseaseOpd;
	}

	/**
	 * @param diseaseOpd the diseaseOpd to set
	 */
	public void setDiseaseOpd(String diseaseOpd) {
		this.diseaseOpd = diseaseOpd;
	}
	
	/**
	 * @return the prescriber
	 */
	public Prescriber getPrescriber() {
		return prescriber;
	}

	/**
	 * @param prescriber the prescriber to set
	 */
	public void setPrescriber(Prescriber prescriber) {
		this.prescriber = prescriber;
	}

	public int getOpdCode() {
		return this.opdCode;
	}

	public void setOpdCode(int opdCode) {
		this.opdCode = opdCode;
	}

	@Override
	public int compareTo(MedicalWardPrescription o) {
		return this.bill.getId() - o.getBill().getId();
	}
}
