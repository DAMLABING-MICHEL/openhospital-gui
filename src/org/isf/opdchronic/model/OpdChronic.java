package org.isf.opdchronic.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by nicosalvato on 2016-08-21.
 * Contact nicosalvato@gmail.com
 */
public class OpdChronic {

    private int id;
    private int opdCode;
    private Date dateCreated;
    private Date lastUpdated;
    private Integer bodyWeight;
    private Integer fastBloodSugar;
    private BigDecimal creatinine;
    private Integer bloodPressureMin;
    private Integer bloodPressureMax;
    private boolean atrialFibrillation;
    private Integer heartRate;
    private BigDecimal hemoglobin;
    private String chestFinding;
    private Integer pO2AtRest;
    private String diseaseCodes;
    private String notes;

    public OpdChronic() {}

    public OpdChronic(int opdCode) {
        this.opdCode = opdCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getOpdCode() {
        return this.opdCode;
    }

    public void setOpdCode(int opdCode) {
        this.opdCode = opdCode;
    }

    public Integer getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(Integer bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public Integer getFastBloodSugar() {
        return fastBloodSugar;
    }

    public void setFastBloodSugar(Integer fastBloodSugar) {
        this.fastBloodSugar = fastBloodSugar;
    }

    public BigDecimal getCreatinine() {
        return creatinine;
    }

    public void setCreatinine(BigDecimal creatinine) {
        if (creatinine != null)
            this.creatinine = creatinine.round(new MathContext(3));
    }

    public Integer getBloodPressureMin() {
        return bloodPressureMin;
    }

    public void setBloodPressureMin(Integer bloodPressureMin) {
        this.bloodPressureMin = bloodPressureMin;
    }

    public Integer getBloodPressureMax() {
        return bloodPressureMax;
    }

    public void setBloodPressureMax(Integer bloodPressureMax) {
        this.bloodPressureMax = bloodPressureMax;
    }

    public boolean getAtrialFibrillation() {
        return atrialFibrillation;
    }

    public void setAtrialFibrillation(boolean atrialFibrillation) {
        this.atrialFibrillation = atrialFibrillation;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public BigDecimal getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(BigDecimal hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public String getChestFinding() {
        return chestFinding;
    }

    public void setChestFinding(String chestFinding) {
        this.chestFinding = chestFinding;
    }

    public Integer getPO2AtRest() {
        return pO2AtRest;
    }

    public String getDiseaseCodes() {
        return diseaseCodes;
    }

    public ArrayList<String> getDiseaseCodesAsList() {
        ArrayList<String> diseaseCodeList = new ArrayList<String>();
        if (diseaseCodes != null)
            diseaseCodeList = new ArrayList<String>(Arrays.asList(diseaseCodes.split("-")));
        return diseaseCodeList;
    }

    public void setDiseaseCodes(String diseaseCodes) {
        this.diseaseCodes = diseaseCodes;
    }

    public void setDiseaseCodes(List<String> diseaseCodes) {
        this.diseaseCodes = null;
        if (diseaseCodes != null && diseaseCodes.size() > 0) {
            if (diseaseCodes.size() == 1) {
                this.diseaseCodes = diseaseCodes.get(0);
            } else {
                String result = "";
                for (String code : diseaseCodes) {
                    result += code + "-";
                }
                this.diseaseCodes = result.substring(0, result.length() - 1);
            }
        }
    }

    public void setPO2AtRest(Integer pO2AtRest) {
        this.pO2AtRest = pO2AtRest;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void resetFieldByName(String fieldName) {
        if (fieldName.equals("bodyWeight"))
            this.bodyWeight = null;
        if (fieldName.equals("fastBloodSugar"))
            this.fastBloodSugar = null;
        if (fieldName.equals("creatinine"))
            this.creatinine = null;
        if (fieldName.equals("bloodPressureMin"))
            this.bloodPressureMin = null;
        if (fieldName.equals("bloodPressureMax"))
            this.bloodPressureMax = null;
        if (fieldName.equals("atrialFibrillation"))
            this.atrialFibrillation = false;
        if (fieldName.equals("heartRate"))
            this.heartRate = null;
        if (fieldName.equals("hemoglobin"))
            this.hemoglobin = null;
        if (fieldName.equals("chestFinding"))
            this.chestFinding = null;
        if (fieldName.equals("pO2AtRest"))
            this.pO2AtRest = null;
        if (fieldName.equals("diseaseCodes"))
            this.diseaseCodes = null;
        if (fieldName.equals("notes"))
            this.notes = null;
    }
}