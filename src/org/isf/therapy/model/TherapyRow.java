package org.isf.therapy.model;

import java.util.GregorianCalendar;

import org.isf.medicals.model.Medical;

/**
 * 
 * @author Mwithi
 * 
 * Bean to collect data from DB table THERAPIES
 *
 */
public class TherapyRow {
	
	private int therapyID;
	private int patID;
	private int opdCode;
	private GregorianCalendar startDate;
	private GregorianCalendar endDate;
	private Medical medical;
	private Double qty;
	private Double qty2;
	private Double qty3;
	private Double qty4;
	private int unitID;
	private int freqInDay;
	private int freqInPeriod;
	private String note;
	private boolean notify;
	private boolean sms;
	
	/**
	 * @param therapyID
	 * @param patID
	 * @param startDate
	 * @param endDate
	 * @param medical
	 * @param qty
	 * @param unitID
	 * @param freqInDay
	 * @param freqInPeriod
	 * @param note
	 * @param notify
	 * @param sms
	 */
	public TherapyRow(int therapyID, int patID, 
			GregorianCalendar startDate, GregorianCalendar endDate,
			Medical medical, Double qty, int unitID, int freqInDay,
			int freqInPeriod, String note, boolean notify, boolean sms) {
		super();
		this.therapyID = therapyID;
		this.patID = patID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.medical = medical;
		this.qty = qty;
		this.unitID = unitID;
		this.freqInDay = freqInDay;
		this.freqInPeriod = freqInPeriod;
		this.note = note;
		this.notify = notify;
		this.sms = sms;
	}

	public TherapyRow() {}
	
	public int getTherapyID() {
		return therapyID;
	}

	public void setTherapyID(int therapyID) {
		this.therapyID = therapyID;
	}

	public int getPatID() {
		return patID;
	}

	public void setPatID(int patID) {
		this.patID = patID;
	}

	public int getOpdCode() {
		return this.opdCode;
	}

	public void setOpdCode(int opdCode) {
		this.opdCode = opdCode;
	}

	public GregorianCalendar getStartDate() {
		return startDate;
	}

	public void setStartDate(GregorianCalendar startDate) {
		this.startDate = startDate;
	}

	public GregorianCalendar getEndDate() {
		return endDate;
	}

	public void setEndDate(GregorianCalendar endDate) {
		this.endDate = endDate;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public Double getQty2() {
		return qty2;
	}

	public void setQty2(Double qty2) {
		this.qty2 = qty2;
	}

	public Double getQty3() {
		return qty3;
	}

	public void setQty3(Double qty3) {
		this.qty3 = qty3;
	}

	public Double getQty4() {
		return qty4;
	}

	public void setQty4(Double qty4) {
		this.qty4 = qty4;
	}

	public int getUnitID() {
		return unitID;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}

	public int getFreqInDay() {
		return freqInDay;
	}

	public void setFreqInDay(int freqInDay) {
		this.freqInDay = freqInDay;
	}

	public int getFreqInPeriod() {
		return freqInPeriod;
	}

	public void setFreqInPeriod(int freqInPeriod) {
		this.freqInPeriod = freqInPeriod;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}

	public boolean isSms() {
		return sms;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}
	
	public String toString() {
		String string = medical.toString() + " - " + this.unitID + " " + String.valueOf(this.qty) + "/" + this.freqInDay + "/" + this.freqInPeriod; 
		return string;
	}

	public String describe() {
		String description = "";
		if (medical != null) {
			description += medical.getDescriptionWithCode();
			switch (freqInDay) {
				case 1:
					description += ", once a day";
					break;
				case 2:
					description += ", twice a day";
					break;
				case 3:
					description += ", three times a day";
					break;
				case 4:
					description += ", four times a day";
					break;
			}
			description += " (" +
					(qty > 0.0 ? qty : "") +
					(qty3 > 0.0 ? ", " + qty3 : "") +
					(qty2 > 0.0 ? ", " + qty2 : "") +
					(qty4 > 0.0 ? ", " + qty4 : "") +
					")";

			switch (freqInPeriod) {
				case 1:
					description += ", every day.";
					break;
				default:
					description += ", every " + freqInPeriod + " days.";
			}
		}
		return description;
	}
}
