/**
 * MissingMedical.java - 05/apr/2014
 */
package org.isf.medicalstockward.model;

import java.util.GregorianCalendar;

import org.isf.medicals.model.Medical;
import org.isf.ward.model.Ward;

/**
 * @author Mwithi
 *
 */
public class MissingMedical {

	private GregorianCalendar date;
	private Ward ward;
	private Medical medical;
	private double qty;
	private String units;
	
	/**
	 * 
	 */
	public MissingMedical() {}

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
	 * @return the qty
	 */
	public double getQty() {
		return qty;
	}

	/**
	 * @param qty the qty to set
	 */
	public void setQty(double qty) {
		this.qty = qty;
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
