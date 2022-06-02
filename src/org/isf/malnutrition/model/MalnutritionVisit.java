package org.isf.malnutrition.model;

import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

public class MalnutritionVisit {

	private int id;
	public static final String ADMISSION = "admission";
	public static final String DISCHARGE = "discharge";
	public static final String HOSPITALIZATION = "hospitalization";
	public static final String CURED = "CURED";
	public static final String NON_RESPONDER = "NON-RESPONDER";
	public static final String DEFAULTER = "DEFAULTER";
	public static final String MEDICAL_TRANSFER = "MEDICAL_TRANSFER";
	public static final String TRANSFER_OUT = "TRANSFER_OUT";
	public static final String DEATH = "DEATH";
	public static final String UNKNOWN = "UNKNOWN";
	public static final String SELF_DISCHARGE = "SELF_DISCHARGE";
	private String type; //One of ADMISSION, HOSPITALIZATION, DISCHARGE
	private String extCode; //Mandatory code for government official logbook
	private GregorianCalendar dateCreated;
	private GregorianCalendar lastUpdated;
	private GregorianCalendar visitDate;
	private String user;
	private Admission admission;
	private TfuPatient patient;
	private Integer ageInMonthsAtVisitTime;
	private BigDecimal weight;
	private BigDecimal height;
	private BigDecimal minWeight;
	private GregorianCalendar minWeightDate;
	private BigDecimal muac;
	/**
	 * The WH field value stored in the database doesn't reflect the actual W/H value measured by clinicians. Given
	 * patient height, W/H value represents the ratio (percent) between the patient weight and a target weight (for
	 * that height value) reported on a table provided by health ministry. This ratio may fall int the following
	 * intervals: <= 60, <= 70, <= 75, <= 80, > 80, >= 85, >= 100. The corresponding stored values for WH field are:
	 * 60, 70, 75, 80, 84, 99, 100.
	 * Note: intervals have changed according to Yonas and Anna's request. Now the corresponding stered values will be:
	 * 60, 70, 75, 80, 85, 99.
	 */
	private Integer wh;
	private Integer oedema;
	private String km;
	private boolean readmission;
	private boolean relapse;
	private String referredBy;
	private String outcome;
	private String remarks;
	private boolean tb;
	private String hiv;
	private boolean sepsis;
	private boolean anaemia;
	private boolean pneumonia;
	private boolean malaria;
	private boolean CP;
	private boolean ricketts;
	private boolean other;
	private boolean followUp;
	private String closestHealthCenter;
	private boolean isFromOtherTFP;
	private boolean isFromOtherOTP;
	private boolean isTransferToOtherOTP;
	private boolean isMedicalTransfer;

	public MalnutritionVisit() {}

	/**
	 * {@link MalnutritionVisit} constructor.
	 * @param admission {@link Admission} instance.
	 * @param patient {@link Patient} instance.
	 * @param type one of {@code MalnutritionVisit.ADMISSION}, {@code MalnutritionVisit.DISCHARGE},
	 * {@code MalnutritionVisit.HOSPITALIZATION}.
     */
	public MalnutritionVisit(Admission admission, TfuPatient patient, String type) {
		this.admission = admission;
		this.patient = patient;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExtCode() {
		return extCode;
	}

	public void setExtCode(String extCode) {
		this.extCode = extCode;
	}

	public GregorianCalendar getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(GregorianCalendar dateCreated) {
		this.dateCreated = dateCreated;
	}

	public GregorianCalendar getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(GregorianCalendar lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public GregorianCalendar getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(GregorianCalendar visitDate) {
		this.visitDate = visitDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Admission getAdmission() {
		return admission;
	}

	public void setAdmission(Admission admission) {
		this.admission = admission;
	}

	public int getAdmissionId() {
		if (admission != null)
			return admission.getId();
		return 0;
	}

	public TfuPatient getPatient() {
		return patient;
	}

	public void setPatient(TfuPatient patient) {
		this.patient = patient;
	}

	public Integer getAgeInMonthsAtVisitTime() {
		return ageInMonthsAtVisitTime;
	}

	public void setAgeInMonthsAtVisitTime(Integer ageInMonthsAtVisitTime) {
		this.ageInMonthsAtVisitTime = ageInMonthsAtVisitTime;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public BigDecimal getMinWeight() {
		return minWeight;
	}

	public void setMinWeight(BigDecimal minWeight) {
		this.minWeight = minWeight;
	}

	public GregorianCalendar getMinWeightDate() {
		return minWeightDate;
	}

	public void setMinWeightDate(GregorianCalendar minWeightDate) {
		this.minWeightDate = minWeightDate;
	}

	public BigDecimal getMuac() {
		return muac;
	}

	public void setMuac(BigDecimal muac) {
		this.muac = muac;
	}

	public Integer getWh() {
		return wh;
	}

	public void setWh(Integer wh) {
		this.wh = wh;
	}

	public Integer getOedema() {
		return oedema;
	}

	public void setOedema(Integer oedema) {
		this.oedema = oedema;
	}

	public String getKm() {
		return km;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public Boolean isReadmission() {
		return readmission;
	}

	public void setReadmission(Boolean readmission) {
		this.readmission = readmission;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Boolean isTb() {
		return tb;
	}

	public String getHiv() {
		return hiv;
	}

	public void setHiv(String hiv) {
		this.hiv = hiv;
	}

	public boolean isFollowUp() {
		return followUp;
	}

	public void setFollowUp(boolean followUp) {
		this.followUp = followUp;
	}

	public String getClosestHealthCenter() {
		return closestHealthCenter;
	}

	public void setClosestHealthCenter(String closestHealthCenter) {
		this.closestHealthCenter = closestHealthCenter;
	}

	public boolean getRelapse() {
		return relapse;
	}

	public void setRelapse(Boolean relapse) {
		this.relapse = relapse;
	}

	public boolean isSepsis() {
		return sepsis;
	}

	public void setSepsis(boolean sepsis) {
		this.sepsis = sepsis;
	}

	public boolean isAnaemia() {
		return anaemia;
	}

	public void setAnaemia(boolean anaemia) {
		this.anaemia = anaemia;
	}

	public boolean isPneumonia() {
		return pneumonia;
	}

	public void setPneumonia(boolean pneumonia) {
		this.pneumonia = pneumonia;
	}

	public boolean isMalaria() {
		return malaria;
	}

	public void setMalaria(boolean malaria) {
		this.malaria = malaria;
	}

	public boolean isCP() {
		return CP;
	}

	public void setCP(boolean cP) {
		CP = cP;
	}

	public boolean isRicketts() {
		return ricketts;
	}

	public void setRicketts(boolean ricketts) {
		this.ricketts = ricketts;
	}

	public boolean isOther() {
		return other;
	}

	public void setOther(boolean other) {
		this.other = other;
	}

	public boolean isFromOtherTFP() {
		return isFromOtherTFP;
	}

	public void setFromOtherTFP(boolean isFromOtherTFP) {
		this.isFromOtherTFP = isFromOtherTFP;
	}

	public boolean isFromOtherOTP() {
		return isFromOtherOTP;
	}

	public void setFromOtherOTP(boolean isFromOtherOTP) {
		this.isFromOtherOTP = isFromOtherOTP;
	}

	public boolean isTransferToOtherOTP() {
		return isTransferToOtherOTP;
	}

	public void setTransferToOtherOTP(boolean isTransferToOtherOTP) {
		this.isTransferToOtherOTP = isTransferToOtherOTP;
	}

	public boolean isMedicalTransfer() {
		return isMedicalTransfer;
	}

	public void setMedicalTransfer(boolean isMedicalTransfer) {
		this.isMedicalTransfer = isMedicalTransfer;
	}

	public Boolean getReadmission() {
		return readmission;
	}

	public void setTb(boolean tb) {
		this.tb = tb;
	}
}
