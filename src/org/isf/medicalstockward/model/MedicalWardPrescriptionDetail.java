package org.isf.medicalstockward.model;

import org.isf.medicals.model.Medical;

public class MedicalWardPrescriptionDetail {

	private MedicalWardPrescription prescription;
	private Medical medical;
	private double quantity;
	private String units;
	
	public MedicalWardPrescriptionDetail() {}
	
	/**
	 * @param prescription
	 * @param medical
	 * @param quantity
	 * @param units
	 */
	public MedicalWardPrescriptionDetail(MedicalWardPrescription prescription, Medical medical, double quantity, String units) {
		super();
		this.prescription = prescription;
		this.medical = medical;
		this.quantity = quantity;
		this.units = units;
	}

	/**
	 * @return the prescription
	 */
	public MedicalWardPrescription getPrescription() {
		return prescription;
	}

	/**
	 * @param prescription the prescription to set
	 */
	public void setPrescription(MedicalWardPrescription prescription) {
		this.prescription = prescription;
	}

	/**
	 * @return the medical
	 */
	public Medical getMedical() {
		return medical;
	}

	/**
	 * @param medical the medical to set
	 */
	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	/**
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(String units) {
		this.units = units;
	}
}
