package org.isf.pregnancy.model;

import java.util.GregorianCalendar;

import org.isf.patient.model.Patient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.visits.model.Visit;

/**
 * @author Martin Reinstadler
 * this class represents the database table PREGNANCYVISIT
 * Besides the visit date, the date of the next scheduled visit, 
 * the visitnote and the type of visit it has an attribute to 
 * reference to a {@link PregnancyVisit}
 *
 */
public class PregnancyVisit extends Visit {

	private int visitType = -1;

	private GregorianCalendar nextVisitDate = null;

	private int pregnancyId = 0;
	
	private int pregnancyNr = 0;

	private int patientId = 0;

	private int visitId = 0;

	private String treatmentType = null;
	
	/**
	 * default constructor
	 */
	public PregnancyVisit() {}
	
	/**
	 * @return  the code of the {@link PregnantTreatmentType}  
	 */
	public String getTreatmenttype() {
		return treatmentType;
	}
	
	/**
	 * @param treatmenttype  the code of the {@link PregnantTreatmentType}  
	 */
	public void setTreatmenttype(String treatmenttype) {
		this.treatmentType = treatmenttype;
	}
	
	/**
	 * @return  the id of the {@link PregnancyVisit}  
	 */
	public int getVisitId() {
		return visitId;
	}
	/**
	 * @param vid  the id of the {@link PregnancyVisit}  
	 */
	public void setVisitId(int visit_id) {
		this.visitId = visit_id;
	}
	/**
	 * @return  the id of the {@link Pregnancy}  
	 */
	public int getPregnancyId() {
		return pregnancyId;
	}
	/**
	 * @return the pregnancyNr
	 */
	public int getPregnancyNr() {
		return pregnancyNr;
	}

	/**
	 * @param pregnancyNr the pregnancyNr to set
	 */
	public void setPregnancyNr(int pregnancyNr) {
		this.pregnancyNr = pregnancyNr;
	}

	/**
	 * @param pregid the id of the {@link Pregnancy}
	 */
	public void setPregnancId(int pregid) {
		this.pregnancyId = pregid;
	}
	/**
	 * 
	 * @return the id of the {@link Patient}
	 */
	public int getPatientnr() {
		return patientId;
	}
	/**
	 * @return the type of the visit (-1= prenatal, 1= postnatal)
	 */
	public int getType() {
		return visitType;
	}
	/**
	 * @param type the type of the visit (-1= prenatal, 1= postnatal)
	 */
	public void setType(int type) {
		this.visitType = type;
	}
	/**
	 * 
	 * @return the date of the next scheduled visit
	 */
	public GregorianCalendar getNextVisitdate(){
		return this.nextVisitDate;
	}
	/**
	 * 
	 * @param date the date of the next scheduled visit
	 */
	public void setNextVisitdate(GregorianCalendar date){
		this.nextVisitDate = date;
	}
	
	/**
	 * A pregnancy visit must be related to a patient. This initializes a new PregnancyVisit
	 * instance
	 * @param pat_id the identifier of the {@link Patient}
	 * @pregId the id of the {@link Pregnancy}
	 * @type the type of the visit
	 */
	public PregnancyVisit(int pat_id, int pregId, int type){
		//this.visit = new Visit();
		super();
		setNote("");
		visitType = type;
		this.pregnancyId = pregId;
		this.patientId = pat_id;
	}
}