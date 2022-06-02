package org.isf.opdchronic.manager;

import org.isf.opdchronic.model.OpdChronic;
import org.isf.opdchronic.model.OpdChronicHistoryRow;
import org.isf.opdchronic.service.IoOperations;
import org.isf.opd.model.Opd;
import org.isf.patient.model.Patient;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.exception.OHException;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.*;

/**
 * Created by nicosalvato on 2016-08-18.
 * Contact: nicosalvato@gmail.com
 */
public class OpdChronicManager {

    private Logger logger = LoggerFactory.getLogger(OpdChronicManager.class);
    private IoOperations chronicService = new IoOperations();
    private static final HashMap<String, ArrayList<String>> DISEASES_FIELDS = new HashMap<String, ArrayList<String>>();
    private static final List<String> fields = new ArrayList<String>();
    private static OpdChronicManager instance;
    private OpdChronic currentVisit;
    private ArrayList<TherapyRow> therapies = new ArrayList<TherapyRow>();
    private ArrayList<TherapyRow> removedTherapies = new ArrayList<TherapyRow>();
    private org.isf.therapy.service.IoOperations therapyService = new org.isf.therapy.service.IoOperations();

    private OpdChronicManager() {
        initializeFields();
        initializeDiseasesFields();
    }

    public static synchronized OpdChronicManager getInstance() {
        if (instance == null) {
            instance = new OpdChronicManager();
        }
        return instance;
    }

    public void addTherapy(TherapyRow therapyRow) {
        this.therapies.add(therapyRow);
    }

    public boolean removeTherapy(TherapyRow therapyRow) {
        // If the removed therapy is already persisted in the database, I track it to set it
        // as deleted when persisting objects to the database.
        if (therapyRow.getTherapyID() > 0)
            this.removedTherapies.add(therapyRow);
        return therapies.remove(therapyRow);
    }

    public ArrayList<TherapyRow> getTherapies() {
        return this.therapies;
    }

    public ArrayList<TherapyRow> getTherapies(int opdCode) {
        try {
            this.therapies = therapyService.findTherapiesByOpdCode(opdCode);
        } catch (OHException e) {
            this.therapies.clear();
        }
        return this.therapies;
    }

    public void clearTherapies() {
        this.therapies.clear();
    }

    public Date getNextVisitDate(Opd opd) {
        Date therapiesNextVisitDate = getNextVisitDateFromTherapies();
        if (therapiesNextVisitDate != null) {
            if (opd != null && opd.getNextVisit() != null) {
                return opd.getNextVisit().getDate().after(therapiesNextVisitDate) ?
                        opd.getNextVisit().getDate().getTime() : therapiesNextVisitDate;
            } else {
                return therapiesNextVisitDate;
            }
        } else {
            if (opd != null && opd.getNextVisit() != null) {
                return opd.getNextVisit().getDate().getTime();
            } else {
                return null;
            }
        }
    }

    /**
     * Computes a candidate for next visit date by choosing the greatest end date among the therapies associated to the
     * Opd.
     * @return a {@link Date} representing a possible candidate for being next visit date.
     */
    public Date getNextVisitDateFromTherapies() {
        if (this.therapies == null || this.therapies.size() == 0)
            return null;

        GregorianCalendar date = null;
        for (TherapyRow therapy : therapies) {
            GregorianCalendar currentDate = therapy.getEndDate();
            date = (date == null || date.before(currentDate)) ? currentDate : date;
        }
        return date.getTime();
    }

    private void initializeFields() {
        fields.add("bodyWeight");
        fields.add("bloodPressureMin");
        fields.add("bloodPressureMax");
        fields.add("creatinine");
        fields.add("fastBloodSugar");
        fields.add("hemoglobin");
        fields.add("heartRate");
        fields.add("atrialFibrillation");
        fields.add("pO2AtRest");
        fields.add("chestFinding");
    }

    private void initializeDiseasesFields() {
        // Diabetes
        ArrayList<String> dmFields = new ArrayList<String>();
        dmFields.add("bodyWeight");
        dmFields.add("bloodPressureMin");
        dmFields.add("bloodPressureMax");
        dmFields.add("creatinine");
        dmFields.add("fastBloodSugar");
        DISEASES_FIELDS.put("333", dmFields);
        DISEASES_FIELDS.put("334", dmFields);
        DISEASES_FIELDS.put("335", dmFields);
        DISEASES_FIELDS.put("336", dmFields);
        DISEASES_FIELDS.put("337", dmFields);
        DISEASES_FIELDS.put("338", dmFields);
        DISEASES_FIELDS.put("339", dmFields);
        DISEASES_FIELDS.put("340", dmFields);
        DISEASES_FIELDS.put("341", dmFields);
        DISEASES_FIELDS.put("342", dmFields);
        DISEASES_FIELDS.put("343", dmFields);
        DISEASES_FIELDS.put("344", dmFields);
        DISEASES_FIELDS.put("345", dmFields);
        DISEASES_FIELDS.put("346", dmFields);
        DISEASES_FIELDS.put("347", dmFields);
        DISEASES_FIELDS.put("348", dmFields);
        DISEASES_FIELDS.put("349", dmFields);
        DISEASES_FIELDS.put("350", dmFields);
        DISEASES_FIELDS.put("351", dmFields);
        DISEASES_FIELDS.put("352", dmFields);
        DISEASES_FIELDS.put("353", dmFields);
        DISEASES_FIELDS.put("354", dmFields);
        DISEASES_FIELDS.put("355", dmFields);
        DISEASES_FIELDS.put("356", dmFields);
        DISEASES_FIELDS.put("357", dmFields);
        DISEASES_FIELDS.put("358", dmFields);
        DISEASES_FIELDS.put("359", dmFields);
        DISEASES_FIELDS.put("360", dmFields);
        DISEASES_FIELDS.put("361", dmFields);
        DISEASES_FIELDS.put("362", dmFields);
        DISEASES_FIELDS.put("363", dmFields);
        DISEASES_FIELDS.put("364", dmFields);
        DISEASES_FIELDS.put("365", dmFields);
        DISEASES_FIELDS.put("366", dmFields);
        DISEASES_FIELDS.put("367", dmFields);
        DISEASES_FIELDS.put("368", dmFields);
        DISEASES_FIELDS.put("369", dmFields);
        DISEASES_FIELDS.put("370", dmFields);
        DISEASES_FIELDS.put("371", dmFields);
        DISEASES_FIELDS.put("372", dmFields);
        DISEASES_FIELDS.put("373", dmFields);
        DISEASES_FIELDS.put("374", dmFields);
        DISEASES_FIELDS.put("375", dmFields);
        DISEASES_FIELDS.put("376", dmFields);
        DISEASES_FIELDS.put("377", dmFields);
        DISEASES_FIELDS.put("378", dmFields);
        DISEASES_FIELDS.put("379", dmFields);
        DISEASES_FIELDS.put("380", dmFields);
        DISEASES_FIELDS.put("381", dmFields);
        DISEASES_FIELDS.put("382", dmFields);
        DISEASES_FIELDS.put("383", dmFields);
        DISEASES_FIELDS.put("384", dmFields);
        DISEASES_FIELDS.put("385", dmFields);
        // Hyper Tension (HT)
        ArrayList<String> htFields = new ArrayList<String>();
        htFields.add("bloodPressureMin");
        htFields.add("bloodPressureMax");
        htFields.add("creatinine");
        DISEASES_FIELDS.put("569", htFields);
        DISEASES_FIELDS.put("571", htFields);
        DISEASES_FIELDS.put("572", htFields);
        // Chronic Liver Disease (CLD)
        ArrayList<String> cldFields = new ArrayList<String>();
        cldFields.add("bodyWeight");
        cldFields.add("bloodPressureMin");
        cldFields.add("bloodPressureMax");
        cldFields.add("creatinine");
        DISEASES_FIELDS.put("800", cldFields);
        DISEASES_FIELDS.put("803", cldFields);
        //Chronic Renal Failure (CRF)
        ArrayList<String> crfFields = new ArrayList<String>();
        crfFields.add("bodyWeight");
        crfFields.add("bloodPressureMin");
        crfFields.add("bloodPressureMax");
        crfFields.add("creatinine");
        crfFields.add("hemoglobin");
        DISEASES_FIELDS.put("952", crfFields);
        DISEASES_FIELDS.put("953", crfFields);
        // Chronic Heart Failure (CHF)
        ArrayList<String> chfFields = new ArrayList<String>();
        chfFields.add("bodyWeight");
        chfFields.add("bloodPressureMin");
        chfFields.add("bloodPressureMax");
        chfFields.add("creatinine");
        chfFields.add("hemoglobin");
        chfFields.add("heartRate");
        chfFields.add("atrialFibrillation");
        DISEASES_FIELDS.put("577", chfFields);
        DISEASES_FIELDS.put("600", chfFields);
        // Asthma
        ArrayList<String> asthmaFields = new ArrayList<String>();
        asthmaFields.add("pO2AtRest");
        asthmaFields.add("chestFinding");
        DISEASES_FIELDS.put("649", asthmaFields);
    }

    public ArrayList<String> getFieldsByDisease(String diseaseCode) {
        return DISEASES_FIELDS.get(diseaseCode) != null ? DISEASES_FIELDS.get(diseaseCode) : new ArrayList<String>();
    }

    public ArrayList<String> getFieldsByDisease(ArrayList<String> diseases) {
        LinkedHashSet<String> allFields = new LinkedHashSet<String>(); // Using Set since it does not allows duplicates
        if (diseases != null && diseases.size() > 0) {
            for (String diseaseCode : diseases) {
                allFields.addAll(getFieldsByDisease(diseaseCode));
            }
        }
        return new ArrayList<String>(allFields);
    }

    private String parseDiseaseCode(String diseaseDesc) {
        // Get disease code from description, assuming description has format 'DIS_CODE - DISEASE_DESCRIPTION'
        String code = "";
        try {
            code = diseaseDesc.substring(0, diseaseDesc.indexOf("-") - 1);
        } catch (Exception ex) {
            logger.error("Unable to parse disease code from disease description <" +
                    diseaseDesc + ">: " + ex.getMessage());
        }
        return code;
    }

    public boolean isDiseaseChronicByDesc(String diseaseDesc) {
        return isDiseaseChronicByCode(parseDiseaseCode(diseaseDesc));
    }

    public boolean isDiseaseChronicByCode(String diseaseCode) {
        return chronicService.isDiseaseChronic(diseaseCode);
    }

    public boolean chronicDiseasesSelected(Collection<String> diseaseDescList) {
        for (String diseaseDesc : diseaseDescList) {
            if (isDiseaseChronicByDesc(diseaseDesc))
                return true;
        }
        return false;
    }

    public Opd findChronicOpdByPatientAndDateAndDisease(Patient patient, Date date, String diseaseDesc) {
        Opd chronicOpd = null;
        String diseaseCode = parseDiseaseCode(diseaseDesc);
        if (diseaseCode.isEmpty())
            return null;
        if (isDiseaseChronicByCode(diseaseCode)) {
            chronicOpd = chronicService.findChronicOpd(patient.getCode(), date, diseaseCode);
        }
        return chronicOpd;
    }

    /**
     * Checks if a OpdChronic exists for the selected opd.
     * @param opdCode the ID of the OPD.
     * @return {@link OpdChronic} instance, {@code null} otherwise.
     */
    public OpdChronic getCurrentVisit(int opdCode) {
        if (currentVisit == null && opdCode > 0)
            currentVisit = chronicService.findVisitByOpdCode(opdCode);
        return currentVisit;
    }

    /**
     * Method that reset the OpdChronic fields that are inappropriate for the selected diseases.
     * @param diseaseCodes the diseases selected in the OpdChronicEdit interface.
     */
    public boolean resetInappropriateVisitFields(int opdCode, List<String> diseaseCodes) {
        // Whenever the list of selected diseases changes, the visit params unrelated to the selected diseases are reset
        getCurrentVisit(opdCode);
        boolean resetPerformed = false;
        if (currentVisit != null) {
            ArrayList<String> visitDiseases = currentVisit.getDiseaseCodesAsList();
            if (diseaseCodes.size() != visitDiseases.size() || !diseaseCodes.containsAll(visitDiseases)) {
                List<String> fieldsToReset = new ArrayList<String>(fields);
                for (String code : diseaseCodes) {
                    List<String> diseaseFields = DISEASES_FIELDS.get(code);
                    if (diseaseFields != null)
                        fieldsToReset.removeAll(diseaseFields);
                }
                for (String field : fieldsToReset) {
                    currentVisit.resetFieldByName(field);
                }
                resetPerformed = true;
            }
            currentVisit.setDiseaseCodes(diseaseCodes);
        }
        return resetPerformed;
    }

    public void clearState() {
        clearCurrentVisit();
        clearTherapies();
    }

    public void saveCurrentVisit(OpdChronic visit) {
        this.currentVisit = visit;
    }

    public void clearCurrentVisit() {
        this.currentVisit = null;
    }

    public boolean saveChronicOpd(Opd opd) {
        try {
            boolean success = chronicService.persistToDb(opd, currentVisit, therapies, removedTherapies);
            if (success)
                clearState();
            return success;
        } catch (OHException ohe) {
            logger.error("Could not persist chronic opd, visit and therapy to the database, " + ohe.getMessage());
            return false;
        }
    }

    public Opd getLastChronicOpd(int patientCode, Date date) {
        try {
            return chronicService.getLastChronicOpd(patientCode, date);
        } catch (OHException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return null;
        }
    }

    public List<OpdChronicHistoryRow> getOpdHistoryByPatientAndDate(int patientCode, Date date) {
        try {
            return chronicService.getOpdHistoryByPatientAndDate(patientCode, date);
        } catch (OHException ohe) {
            JOptionPane.showMessageDialog(null, ohe.getMessage());
            return null;
        }
    }

    public String onSchedule(DateTime date1, DateTime date2) {
        String onTime = "";
        if (date1 != null && date2 != null) {
            int days = Days.daysBetween(date1, date2).getDays();
            if (days == 0)
                onTime = "on time";
            else if (days < 0)
                onTime = Math.abs(days) + " day" + (Math.abs(days) > 1 ? "s" : "") + " early";
            else
                onTime = days + " day" + (days > 1 ? "s" : "") + " late";
        }
        return onTime;
    }
}
