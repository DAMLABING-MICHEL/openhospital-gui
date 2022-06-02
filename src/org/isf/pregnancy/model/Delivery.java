package org.isf.pregnancy.model;

import java.util.GregorianCalendar;

import org.isf.admission.model.Admission;
import org.isf.disease.model.Disease;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.menu.model.User;

public class Delivery {

	private int id;
	private Pregnancy pregnancy;
	private Admission admission;
	private Disease disease;
	private int estimatedGestationalAge;
	private GregorianCalendar deliverydate;
	private String robsonIndex;
	private DeliveryType deliveryType;
	private DeliveryResultType deliveryResultType;
	private int weight;
	private int height;
	private float head;
	private String sex;
	private String apgarScore;
	private String childName;
	private String removePlacentaType;
	private String management;
	private boolean complicationAPH;
	private boolean complicationPPH;
	private boolean complicationCP;
	private String hivTestPlace;
	private char hivTestResult;
	private char hivTestResultPartner;
	private int bloodPressureMin;
	private int bloodPressureMax;
	private String ancVisitDone;
	private char motherWaitingHomeDone;
	private String note;
	private User user;
	
	public Delivery(){
		deliverydate = new GregorianCalendar();
		sex = "F";
		hivTestPlace = "Unknown";
		hivTestResult = 'U';
		hivTestResultPartner = 'U';
		ancVisitDone = "U";
		motherWaitingHomeDone = 'N';
	}
	/**
	 * @return  the date of the delivery
	 */
	public GregorianCalendar getDeliveryDate() {
		return deliverydate;
	}
	/**
	 * @param date  the date of the delivery
	 */
	public void setDeliveryDate(GregorianCalendar date) {
		this.deliverydate = date;
	}
	/**
	 * @return  the sex of the newborn child 'F' or 'M'
	 */
	public String getSex() {
		return sex;
	}
	/**
	 * @param sex   the sex of the newborn child 'F' or 'M'
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}
	/**
	 * @return   the weight of the newborn child
	 */
	public int getWeight() {
		return weight;
	}
	/**
	 * @param weight  the weight of the newborn child
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	/**
	 * @return  the id of the record in the database
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id  the id of the record in the database
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the pregnancy
	 */
	public Pregnancy getPregnancy() {
		return pregnancy;
	}
	/**
	 * @param pregnancy the pregnancy to set
	 */
	public void setPregnancy(Pregnancy pregnancy) {
		this.pregnancy = pregnancy;
	}
	/**
	 * @return the admission
	 */
	public Admission getAdmission() {
		return admission;
	}
	/**
	 * @param admission the admission to set
	 */
	public void setAdmission(Admission admission) {
		this.admission = admission;
	}
	/**
	 * @return the disease
	 */
	public Disease getDisease() {
		return disease;
	}
	/**
	 * @param disease the disease to set
	 */
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	/**
	 * @return the gestationalAge
	 */
	public int getEstimatedGestationalAge() {
		return estimatedGestationalAge;
	}
	/**
	 * @param estimatedGestationalAge the estimatedGestationalAge to set
	 */
	public void setEstimatedGestationalAge(int estimatedGestationalAge) {
		this.estimatedGestationalAge = estimatedGestationalAge;
	}
	/**
	 * @return the deliverydate
	 */
	public GregorianCalendar getDeliverydate() {
		return deliverydate;
	}
	/**
	 * @param deliverydate the deliverydate to set
	 */
	public void setDeliverydate(GregorianCalendar deliverydate) {
		this.deliverydate = deliverydate;
	}
	/**
	 * @return the robsonIndex
	 */
	public String getRobsonIndex() {
		return robsonIndex;
	}
	/**
	 * @param robsonIndex the robsonIndex to set
	 */
	public void setRobsonIndex(String robsonIndex) {
		this.robsonIndex = robsonIndex;
	}
	/**
	 * @return the deliveryType
	 */
	public DeliveryType getDeliveryType() {
		return deliveryType;
	}
	/**
	 * @param deliveryType the deliveryType to set
	 */
	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}
	/**
	 * @return the deliveryResultType
	 */
	public DeliveryResultType getDeliveryResultType() {
		return deliveryResultType;
	}
	/**
	 * @param deliveryResultType the deliveryResultType to set
	 */
	public void setDeliveryResultType(DeliveryResultType deliveryResultType) {
		this.deliveryResultType = deliveryResultType;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return the head
	 */
	public float getHead() {
		return head;
	}
	/**
	 * @param head the head to set
	 */
	public void setHead(float head) {
		this.head = head;
	}
	/**
	 * @return the apgarScore
	 */
	public String getApgarScore() {
		return apgarScore;
	}
	/**
	 * @param apgarScore the apgarScore to set
	 */
	public void setApgarScore(String apgarScore) {
		this.apgarScore = apgarScore;
	}
	/**
	 * @return the childName
	 */
	public String getChildName() {
		return childName;
	}
	/**
	 * @param childName the childName to set
	 */
	public void setChildName(String childName) {
		this.childName = childName;
	}
	/**
	 * @return the removePlacentaType
	 */
	public String getRemovePlacentaType() {
		return removePlacentaType;
	}
	/**
	 * @param removePlacentaType the removePlacentaType to set
	 */
	public void setRemovePlacentaType(String removePlacentaType) {
		this.removePlacentaType = removePlacentaType;
	}
	/**
	 * @return the management
	 */
	public String getManagement() {
		return management;
	}
	/**
	 * @param management the management to set
	 */
	public void setManagement(String management) {
		this.management = management;
	}
	/**
	 * @return the complicationAPH
	 */
	public boolean isComplicationAPH() {
		return complicationAPH;
	}
	/**
	 * @param complicationAPH the complicationAPH to set
	 */
	public void setComplicationAPH(boolean complicationAPH) {
		this.complicationAPH = complicationAPH;
	}
	/**
	 * @return the complicationPPH
	 */
	public boolean isComplicationPPH() {
		return complicationPPH;
	}
	/**
	 * @param complicationPPH the complicationPPH to set
	 */
	public void setComplicationPPH(boolean complicationPPH) {
		this.complicationPPH = complicationPPH;
	}
	/**
	 * @return the complicationCP
	 */
	public boolean isComplicationCP() {
		return complicationCP;
	}
	/**
	 * @param complicationCP the complicationCP to set
	 */
	public void setComplicationCP(boolean complicationCP) {
		this.complicationCP = complicationCP;
	}
	/**
	 * @return the hivTestPlace
	 */
	public String getHivTestPlace() {
		return hivTestPlace;
	}
	/**
	 * @param hivTestPlace the hivTestPlace to set
	 */
	public void setHivTestPlace(String hivTestPlace) {
		this.hivTestPlace = hivTestPlace;
	}
	/**
	 * @return the hivTestResult
	 */
	public char getHivTestResult() {
		return hivTestResult;
	}
	/**
	 * @param hivTestResult the hivTestResult to set
	 */
	public void setHivTestResult(char hivTestResult) {
		this.hivTestResult = hivTestResult;
	}
	/**
	 * @return the hivTestResultPartner
	 */
	public char getHivTestResultPartner() {
		return hivTestResultPartner;
	}
	/**
	 * @param hivTestResultPartner the hivTestResultPartner to set
	 */
	public void setHivTestResultPartner(char hivTestResultPartner) {
		this.hivTestResultPartner = hivTestResultPartner;
	}
	/**
	 * @return the bloddPressureMin
	 */
	public int getBloodPressureMin() {
		return bloodPressureMin;
	}
	/**
	 * @param bloodPressureMin the bloddPressureMin to set
	 */
	public void setBloodPressureMin(int bloodPressureMin) {
		this.bloodPressureMin = bloodPressureMin;
	}
	/**
	 * @return the bloodPressureMax
	 */
	public int getBloodPressureMax() {
		return bloodPressureMax;
	}
	/**
	 * @param bloodPressureMax the bloodPressureMax to set
	 */
	public void setBloodPressureMax(int bloodPressureMax) {
		this.bloodPressureMax = bloodPressureMax;
	}
	/**
	 * @return the ancVisitDone
	 */
	public String getAncVisitDone() {
		return ancVisitDone;
	}
	/**
	 * @param ancVisitDone the ancVisitDone to set
	 */
	public void setAncVisitDone(String ancVisitDone) {
		this.ancVisitDone = ancVisitDone;
	}
	/**
	 * @return the motherWaitingHomeDone
	 */
	public char getMotherWaitingHomeDone() {
		return motherWaitingHomeDone;
	}
	/**
	 * @param motherWaitingHomeDone the motherWaitingHomeDone to set
	 */
	public void setMotherWaitingHomeDone(char motherWaitingHomeDone) {
		this.motherWaitingHomeDone = motherWaitingHomeDone;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
