package org.isf.pregnancy.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.isf.patient.model.Patient;
import org.isf.utils.time.TimeTools;

/**
 * @author Martin Reinstadler
 * this class represents the database table PREGNANCY. The information related to a 
 * pregnancy is encoded together with a reference to a {@link Patient} and a listo of
 * {@link PregnancyVisit}
 *
 */
public class Pregnancy {

	private int pregId= 0;
	private int patId = 0;
	/*
	 * number of pregnancies, this one included (like gravida)
	 */
	private int gravida=1;
	/*
	 * number of deliveries, regardless the delivery result type
	 */
	private int parity;
	/*
	 * 
	 */
	private int childrenAlive;
	/*
	 * last menstruation period
	 */
	private GregorianCalendar lmp= null;
	private GregorianCalendar scheduled_delivery = null;
	private GregorianCalendar real_delivery = null;
	private ArrayList<PregnancyVisit> visits= null;
	/*
	 * Y-currently pregnant, N-delivered, D-deleted
	 */
	private char active='Y';
	
	/**
	 * default contructor
	 */
	public Pregnancy() {}
	
	/**
	 * Initializes a new Preganancy instance by setting
	 * pregnancynr to -1, lmp to a new GregorianCalendar, 
	 * preg_del to 9 monts after lmp, active to true,
	 * the visits to a new List of visits
	 * @pat_id the identifier of the {@link Patient}
	 */
	public Pregnancy(int pat_id) {
		this.pregId = -1;
		this.patId = pat_id;
		this.gravida = 1;
		this.parity = 0;
		lmp = new GregorianCalendar();
		this.scheduled_delivery = (GregorianCalendar)this.lmp.clone();
		scheduled_delivery.add(2, 9);
		this.real_delivery = null;
		this.visits = new ArrayList<PregnancyVisit>();
	}
	/**
	 * Creates a new instance of Pregnancy by setting automatically 
	 * the prev_delivery 9 months after the lmpdate.
	 * @param pregnr the number of the patients pregnancy
	 * @param lmp last menstrual period
	 * @param pat_id the id of the {@link Patient}
	 * @param vis the list of {@link PregnancyVisit}
	 * @param act true if the pregnancy is still active
	 */
	public Pregnancy(int pregnr, GregorianCalendar lmpdate, int pat_id, ArrayList<PregnancyVisit> vis){
		this.lmp= lmpdate;
		this.patId = pat_id;
		this.gravida = pregnr;
		this.scheduled_delivery = (GregorianCalendar)this.lmp.clone();
		this.scheduled_delivery.add(2, 9);
		this.real_delivery = null;
		this.visits = vis;
	}
	/**
	 * @return  the id of the  {@link Patient}  
	 */
	public int getPatId() {
		return patId;
	}
	/**
	 * @param patid  the id of the  {@link Patient}  
	 */
	public void setPatId(int patid) {
		this.patId = patid;
	}
	/**
	 * @return  the number of the patients pregnancy (gravida)
	 */
	public int getGravida() {
		return gravida;
	}
	/**
	 * @param pregnancynr  the number of the patients pregnancy
	 */
	public void setGravida(int pregnancynr) {
		this.gravida = pregnancynr;
	}
	/**
	 * @return  the date of the last menstrual periode of this pregnancy as  {@link GregorianCalendar}  
	 */
	public GregorianCalendar getLmp() {
		return lmp;
	}
	/**
	 * this method sets the scheduled delivery to 9 months after the  specified date of the lmp
	 * @param lmp  the date of the last menstrual periode of this pregnancy
	 */
	public void setLmp(GregorianCalendar lmp) {
		this.lmp = lmp;
		if (this.lmp != null) {
			this.scheduled_delivery = (GregorianCalendar) this.lmp.clone();
			this.scheduled_delivery.add(2, 9);
		}
	}
	/**
	 * @return  the date of the scheduled delivery as  {@link GregorianCalendar}  
	 */
	public GregorianCalendar getScheduled_delivery() {
		return scheduled_delivery;
	}
	/**
	 * @param sched_delivery  the date of the scheduled delivery
	 */
	public void setScheduled_delivery(GregorianCalendar sched_delivery) {
		this.scheduled_delivery= sched_delivery;
	}
	/**
	 * @return the real_delivery
	 */
	public GregorianCalendar getReal_delivery() {
		return real_delivery;
	}

	/**
	 * @param real_delivery the real_delivery to set
	 */
	public void setReal_delivery(GregorianCalendar real_delivery) {
		this.real_delivery = real_delivery;
	}

	/**
	 * 
	 * @return the list of {@link PregnancyVisit}
	 */
	public ArrayList<PregnancyVisit> getVisits() {
		return visits;
	}
	/**
	 * 
	 * @param visits the list of {@link PregnancyVisit}
	 */
	public void setVisits(ArrayList<PregnancyVisit> visits) {
		this.visits = visits;
	}
	/**
	 * 
	 * @param visit a single {@link PregnancyVisit}
	 */
	public void addVisit(PregnancyVisit visit){
		this.visits.add(visit);
	}
	/**
	 * @return  the id of the pregnancy (primary key of the database table)
	 */
	public int getId(){
		return this.pregId;
	}
	/**
	 * @param id  the id of the pregnancy (primary key of the database table)
	 */
	public void setId(int id){
		this.pregId= id;
	}
	/**
	 * @return the parity
	 */
	public int getParity() {
		return parity;
	}
	/**
	 * @param parity the parity to set
	 */
	public void setParity(int parity) {
		this.parity = parity;
	}
	/**
	 * @return the childrenAlive
	 */
	public int getChildrenAlive() {
		return childrenAlive;
	}
	/**
	 * @param childrenAlive the childrenAlive to set
	 */
	public void setChildrenAlive(int childrenAlive) {
		this.childrenAlive = childrenAlive;
	}
	/**
	 * @return the active
	 */
	public char getActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(char active) {
		this.active = active;
	}
	/**
	 * @return <code>true</code> if active = 'Y'
	 */
	public boolean isActive() {
		return this.active == 'Y';
	}
	
	public int calculateGestationalAge() {
		int calculatedGA = 0;
		
		if (this.lmp == null) return calculatedGA;
		if (this.real_delivery == null)
			calculatedGA = TimeTools.getWeeksBetweenDates(this.lmp, new GregorianCalendar(), true);
		else 
			calculatedGA = TimeTools.getWeeksBetweenDates(this.lmp, this.real_delivery, true);
		return calculatedGA;
	}
}
