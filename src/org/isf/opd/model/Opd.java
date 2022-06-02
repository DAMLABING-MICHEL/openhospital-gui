package org.isf.opd.model;


/*------------------------------------------
 * Opd - model for OPD
 * -----------------------------------------
 * modification history
 * 11/12/2005 - Vero, Rick  pupo
 * 21/11/2006 - ross - renamed from Surgery 
 *                   - added visit date, disease 2, diseas3
 *                   - disease is not mandatory if re-attendance
 * 			         - version is now 1.0 
 * 12/06/2008 - ross - added referral from / to
 * 16/06/2008 - ross - added patient detail
 * 05/09/2008 - alex - added fullname e notefield
 * 09/01/2009 - fabrizio - date field modified to type Date
 *------------------------------------------*/

import org.isf.utils.time.TimeTools;
import org.isf.visits.model.Visit;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Opd {
	/**
	 * @author Vero, Rick, Pupo
	 */

	private int code;
	private Date date;
	private GregorianCalendar visitDate;

	private String user;
	private int patientCode;
	private String fullName; //ADDED : alex
	private String firstName;
	private String secondName;
	private int age;
	private char sex;
	private String address;
	private String city;
	private String nextKin;
	private String note; //ADDED: Alex
	private String ward; //ADDED: Nicola
	private int year;
	private String disease;
	private String disease2;
	private String disease3;
	private int lock;
	private String diseaseType;
	private String diseaseDesc;
	private String diseaseTypeDesc;
	private String newPatient;	//n=NEW R=REATTENDANCE

	private String referralFrom;	//R=referral from another unit; null=no referral from
	private String referralTo;		//R=referral to another unit; null=no referral to
	private boolean isChronic;
	private Visit scheduledVisit; //Indicates whether an OPD episode was schedule, used for chronic opd
	private Visit nextVisit; //Indicates whether an OPD episode was schedule, used for chronic opd


	public String getNewPatient() {
		return newPatient;
	}

	public void setNewPatient(String newPatient) {
		this.newPatient = newPatient;
	}

	/**
	 * @param aYear
	 * @param aSex
	 * @param aAge
	 * @param aDisease
	 * @param aLock
	 */

	public Opd(int aYear, char aSex, int aAge, String aDisease, int aLock, String aWard) {
		year=aYear;
		sex=aSex;
		age=aAge;
		disease=aDisease;
		lock=aLock;
		ward=aWard;
	}
	//ADDED: Alex
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/////////////////////

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getpatientCode() {
		return patientCode;
	}
	public void setPatientCode(int patientCode) {
		this.patientCode = patientCode;
	}

	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	public String getfirstName() {
		return firstName;
	}

	public void setfirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getsecondName() {
		return secondName;
	}

	public void setsecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getnextKin() {
		return nextKin;
	}

	public void setnextKin(String nextKin) {
		this.nextKin = nextKin;
	}

	public String getcity() {
		return city;
	}

	public void setcity(String city) {
		this.city = city;
	}

	public String getaddress() {
		return address;
	}

	public void setaddress(String address) {
		this.address = address;
	}

	public String getReferralTo() {
		return referralTo;
	}

	public void setReferralTo(String referralTo) {
		this.referralTo = referralTo;
	}

	public String getReferralFrom() {
		return referralFrom;
	}

	public void setReferralFrom(String referralFrom) {
		this.referralFrom = referralFrom;
	}


	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDisease() {
		return disease;
	}
	public String getDisease2() {
		return disease2;
	}
	public String getDisease3() {
		return disease3;
	}
	public void setDisease(String disease) {
		this.disease = disease;
		if (disease!=null) {
			if (disease.equals("")) {
				this.disease=null;
			}
		}
	}
	public void setDisease2(String disease) {
		this.disease2 = disease;
		if (disease!=null) {
			if (disease.equals("")) {
				this.disease2=null;
			}
		}
	}
	public void setDisease3(String disease) {
		this.disease3 = disease;
		if (disease!=null) {
			if (disease.equals("")) {
				this.disease3=null;
			}
		}
	}
	public int getLock() {
		return lock;
	}
	public void setLock(int lock) {
		this.lock = lock;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public GregorianCalendar getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(GregorianCalendar visDate) {
		this.visitDate = visDate;
	}

	public char getSex() {
		return sex;
	}
	public void setSex(char sex) {
		this.sex = sex;
	}
	public String getWard() {
		return ward;
	}
	public void setWard(String ward) {
		this.ward = ward;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getDiseaseDesc() {
		return diseaseDesc;
	}

	public void setDiseaseDesc(String diseaseDesc) {
		this.diseaseDesc = diseaseDesc;
	}

	public String getDiseaseTypeDesc() {
		return diseaseTypeDesc;
	}

	public void setDiseaseTypeDesc(String diseaseTypeDesc) {
		this.diseaseTypeDesc = diseaseTypeDesc;
	}

	public String getDiseaseType() {
		return diseaseType;
	}

	public void setDiseaseType(String diseaseType) {
		this.diseaseType = diseaseType;
	}

	public void setIsChronic(boolean isChronic) { this.isChronic = isChronic; }

	public boolean getIsChronic() { return this.isChronic; }

	public Visit getScheduledVisit() {
		return this.scheduledVisit;
	}

	public void setScheduledVisit(Visit visit) {
		this.scheduledVisit = visit;
	}

	public Visit getNextVisit() {
		return this.nextVisit;
	}

	public void setNextVisit(Visit visit) {
		this.nextVisit = visit;
	}

	public List<String> getDiseases() {
		List<String> diseases = new ArrayList<String>();
		if (disease != null)
			diseases.add(disease);
		if (disease2 != null)
			diseases.add(disease2);
		if (disease3 != null)
			diseases.add(disease3);
		return diseases;
	}
	
	@Override
	public String toString() {
		return code + " - " + TimeTools.formatDateTime(visitDate, "dd-MM-yyyy") + " - " + disease;
	}
}