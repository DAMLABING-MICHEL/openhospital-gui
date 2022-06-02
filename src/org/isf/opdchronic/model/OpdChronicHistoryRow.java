package org.isf.opdchronic.model;

import java.util.Date;

/**
 * Created by nicosalvato on 2016-10-08.
 * Contact: nicosalvato@gmail.com
 */
public class OpdChronicHistoryRow {

    private Date visitDate;
    private Date scheduledVisitDate;
    private String diseases;
    private String visitParams;
    private String therapy;
    private String notes;

    public OpdChronicHistoryRow() {}

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date date) {
        this.visitDate = date;
    }

    public Date getScheduledVisitDate() {
        return scheduledVisitDate;
    }

    public void setScheduledVisitDate(Date scheduledVisitDate) {
        this.scheduledVisitDate = scheduledVisitDate;
    }

    public String getDiseases() {
        return diseases;
    }

    public void setDiseases(String diseases) {
        this.diseases = diseases;
    }

    public String getVisitParams() {
        return visitParams;
    }

    public void setVisitParams(String params) {
        this.visitParams = params;
    }

    public String getTherapy() {
        return therapy;
    }

    public void setTherapy(String therapy) {
        this.therapy = therapy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
