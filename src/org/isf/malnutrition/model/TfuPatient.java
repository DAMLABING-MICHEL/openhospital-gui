package org.isf.malnutrition.model;

import org.isf.patient.model.Patient;

/**
 * Created by nicosalvato on 2017-02-15.
 * Contact: nicosalvato@gmail.com
 * This class is used in Malnutrition records to store patient data. The reason why a real patient is not used is that
 * TFU clerks may edit those patient data that are inserted with a wrong value by the ADT employees. To assess the
 * quality of the data recorded in the hospital ad to guarantee that everyone is hold responsible for its own work,
 * it has been chosen not to let TFU clerks override someone else's work.
 * Notice that the data recorded in this class are frozen at the {@link MalnutritionVisit} {@code visitDate} time,
 * especially the {@code monthsAtAdmissionTime} field.
 */
public class TfuPatient {

	private int id;
    private String code;
    private String name;
    private char gender;
    private int monthsAtAdmissionTime;
    private String city;
    private String address;

    public TfuPatient (int id, String code, String name, char gender, int monthsAtAdmissionTime, String city, String address) {
    	this.id = id;
        this.code = code;
        this.name = name;
        this.gender = gender;
        this.monthsAtAdmissionTime = monthsAtAdmissionTime;
        this.city = city;
        this.address = address;
    }

    public TfuPatient (Patient patient) {
        if (patient != null) {
        	this.id = patient.getCode();
            this.code = patient.getPreviousCode();
            this.name = patient.getName();
            this.gender = patient.getSex();
            this.monthsAtAdmissionTime = patient.getMonths();
            this.city = patient.getCity();
            this.address = patient.getAddress();
        }
    }
    
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getMonthsAtAdmissionTime() {
        return monthsAtAdmissionTime;
    }

    public void setMonthsAtAdmissionTime(int monthsAtAdmissionTime) {
        this.monthsAtAdmissionTime = monthsAtAdmissionTime;
    }

    public int[] getYearsAndMonthsAtAdmissionTime() {
        int[] result = new int[2];
        if (monthsAtAdmissionTime < 12) {
            result[0] = 0;
            result[1] = monthsAtAdmissionTime;
        } else {
            int yearsInMonths = monthsAtAdmissionTime / 12;
            int remain = monthsAtAdmissionTime % 12;
//            BigDecimal bd = BigDecimal.valueOf(yearsInMonths).setScale(1, BigDecimal.ROUND_DOWN);
//            String txt = bd.toPlainString();
//            int index = txt.indexOf('.');
//            result[0] = Integer.parseInt(txt.substring(0, index));
            result[0] = yearsInMonths;
//            result[1] = Integer.parseInt(txt.substring(index + 1, txt.length()));
            result[1] = remain;
        }
        return result;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
