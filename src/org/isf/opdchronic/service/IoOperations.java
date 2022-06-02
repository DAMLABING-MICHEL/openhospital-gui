package org.isf.opdchronic.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.opd.model.Opd;
import org.isf.opdchronic.model.OpdChronic;
import org.isf.opdchronic.model.OpdChronicHistoryRow;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.Converters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nicosalvato on 2016-08-18.
 * Contact: nicosalvato@gmail.com
 */
public class IoOperations {

    private DbQueryLogger dbQuery = new DbQueryLogger();
    private Logger logger = LoggerFactory.getLogger(IoOperations.class);
    private org.isf.opd.service.IoOperations opdService = new org.isf.opd.service.IoOperations();
    private org.isf.therapy.service.IoOperations therapyService = new org.isf.therapy.service.IoOperations();
    private org.isf.visits.service.IoOperations visitService = new org.isf.visits.service.IoOperations();

    public Opd findChronicOpd(Integer patientCode, Date date, String diseaseCode) {
        String query = "SELECT *," +
                " (SELECT VST_ID FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_ID," +
                " (SELECT VST_DATE FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_DATE," +
                " (SELECT VST_PAT_ID FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_PAT_ID," +
                " (SELECT VST_NOTE FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_NOTE," +
                " (SELECT VST_ID FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_ID," +
                " (SELECT VST_DATE FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_DATE," +
                " (SELECT VST_PAT_ID FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_PAT_ID," +
                " (SELECT VST_NOTE FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_NOTE" +
                " FROM OPD WHERE OPD_PAT_ID = ?" +
                " AND OPD_DATE_VIS = ?" +
                " AND OPD_IS_CHRONIC = 1";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(patientCode);
        parameters.add(Converters.convertToSQLDateLimited(date));
        if (diseaseCode != null) {
            query += " AND (OPD_DIS_ID_A = ?";
            query += " OR OPD_DIS_ID_A_2 = ?";
            query += " OR OPD_DIS_ID_A_3 = ?)";
            parameters.add(diseaseCode);
            parameters.add(diseaseCode);
            parameters.add(diseaseCode);
        }
        query += " ORDER BY OPD_DATE DESC";

        Opd opd = null;
        try {
            opd = getOpdFromDb(query, parameters);
        } catch (OHException ohe) {
            logger.error("Could not retrieve Opd instance from database, " + ohe.getMessage());
        }
        return opd;
    }

    public ArrayList<OpdChronicHistoryRow> getOpdHistoryByPatientAndDate(int patientCode, Date date) throws OHException {
        ArrayList<OpdChronicHistoryRow> opdChronicHistoryRows = new ArrayList<OpdChronicHistoryRow>();
        String query = "SELECT *," +
                " (SELECT VST_ID FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_ID," +
                " (SELECT VST_DATE FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_DATE," +
                " (SELECT VST_PAT_ID FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_PAT_ID," +
                " (SELECT VST_NOTE FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_NOTE," +
                " (SELECT VST_ID FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_ID," +
                " (SELECT VST_DATE FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_DATE," +
                " (SELECT VST_PAT_ID FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_PAT_ID," +
                " (SELECT VST_NOTE FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_NOTE" +
                " FROM OPD" +
                " LEFT JOIN DISEASE ON OPD_DIS_ID_A = DIS_ID_A" +
                " LEFT JOIN DISEASETYPE ON DIS_DCL_ID_A = DCL_ID_A" +
                " LEFT JOIN OPD_CHRONIC ON CV_OPD_ID = OPD_ID WHERE" +
                " OPD_PAT_ID = ?" +
                " AND DATE(OPD_DATE_VIS) < ?" +
                " AND OPD_IS_CHRONIC = 1" +
                " ORDER BY OPD_DATE_VIS;";
        ArrayList<Object> params = new ArrayList<Object>(2);
        params.add(patientCode);
        params.add(Converters.convertToSQLDateLimited(date));
        try {
            ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
            while (resultSet.next()) {
                OpdChronicHistoryRow opdChronicHistoryRow = new OpdChronicHistoryRow();
                opdChronicHistoryRow.setVisitDate(resultSet.getDate("OPD_DATE_VIS"));
                opdChronicHistoryRow.setScheduledVisitDate(resultSet.getDate("SCH_VST_DATE"));
                opdChronicHistoryRow.setDiseases(getOpdHistoryDiseases(resultSet));
                opdChronicHistoryRow.setVisitParams(getOpdHistoryVisitParams(resultSet));
                opdChronicHistoryRow.setTherapy(getOpdHistoryTherapy(resultSet));
                opdChronicHistoryRow.setNotes(resultSet.getString("OPD_NOTE"));
                opdChronicHistoryRows.add(opdChronicHistoryRow);
            }
        } catch (SQLException sqle) {
            logger.error("Unable to parse opd history records, " + sqle.getMessage());
            throw new OHException("Unable to parse opd history records", sqle);
        } finally {
            dbQuery.releaseConnection();
        }
        return opdChronicHistoryRows;
    }

    private String getOpdHistoryDiseases(ResultSet resultSet) throws OHException {
        String result = "";
        String query = "SELECT DIS_DESC FROM DISEASE WHERE DIS_ID_A = ?;";
        ArrayList<Object> params = new ArrayList<Object>(1);
        try {
            ArrayList<String> diseaseCodes = new ArrayList<String>();
            String disease1 = resultSet.getString("OPD_DIS_ID_A");
            if (disease1 != null && !disease1.isEmpty())
                diseaseCodes.add(disease1);
            String disease2 = resultSet.getString("OPD_DIS_ID_A_2");
            if (disease2 != null && !disease2.isEmpty())
                diseaseCodes.add(disease2);
            String disease3 = resultSet.getString("OPD_DIS_ID_A_3");
            if (disease3 != null && !disease3.isEmpty())
                diseaseCodes.add(disease3);

            for (String disease : diseaseCodes) {
                params.clear();
                params.add(disease);
                ResultSet rs = dbQuery.getDataWithParams(query, params, true);
                if (rs.first()) {
                    String diseaseDesc = rs.getString("DIS_DESC");
                    result += diseaseDesc + "\n";
                }
            }
        } catch (SQLException sqle) {
            throw new OHException("Error retrieving Opd history diseases data", sqle);
        }
        return result.substring(0, result.length() - 1);
    }

    private String getOpdHistoryVisitParams(ResultSet resultSet) throws OHException {
        String result = "";
        try {
            OpdChronic opdChronic = parseChronicVisitRecord(resultSet);
            result += opdChronic.getBodyWeight() != null ?  "BW = " + opdChronic.getBodyWeight() + "\n" : "";
            result += opdChronic.getCreatinine() != null ? "CREATININE = " + opdChronic.getCreatinine() + "\n" : "";
            result += opdChronic.getBloodPressureMin() != null ? "BP Min = " + opdChronic.getBloodPressureMin() + "\n" : "";
            result += opdChronic.getBloodPressureMax() != null ? "BP Max = " + opdChronic.getBloodPressureMax() + "\n" : "";
            result += opdChronic.getChestFinding() != null ? "CF = " + opdChronic.getChestFinding() + "\n" : "";
            result += opdChronic.getHeartRate() != null ? "HR = " + opdChronic.getHeartRate() + "\n" : "";
            result += opdChronic.getFastBloodSugar() != null ? "FBS = " + opdChronic.getFastBloodSugar() + "\n" : "";
            result += opdChronic.getHemoglobin() != null ? "HB = " + opdChronic.getHemoglobin() + "\n" : "";
            result += opdChronic.getPO2AtRest() != null ? "PO2 = " + opdChronic.getPO2AtRest() + "\n" : "";
            result += opdChronic.getAtrialFibrillation() ? "AF found\n" : "";
        } catch (SQLException sqle) {
            throw new OHException("Error retrieving Opd history visits data", sqle);
        }
        return result.isEmpty() ? "" : result.substring(0, result.length() - 1);
    }

    private String getOpdHistoryTherapy(ResultSet resultSet) throws OHException {
        String result = "";
        try {
            int opdCode = resultSet.getInt("OPD_ID");
            ArrayList<TherapyRow> therapies = therapyService.getTherapyRowsByOpd(opdCode, dbQuery);
            for (TherapyRow therapy : therapies) {
                result += therapy.describe() + "\n";
            }
        } catch (SQLException sqle) {
            throw new OHException("Error retrieving Opd history therapies data", sqle);
        }
        return result;
    }

    public boolean isDiseaseChronic(String diseaseCode) {
        String query = "SELECT DIS_IS_CHRONIC FROM DISEASE WHERE DIS_ID_A = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(diseaseCode);
        try {
            ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
            if (resultSet.first()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException sqle) {
            logger.error("Unable to parse DIS_ID_CHRONIC field from DISEASE table record, " + sqle.getMessage());
        } catch (OHException ohe) {
            logger.error("Could not retrieve field DIS_IS_CHRONIC from database, " + ohe.getMessage());
        }
        return false;
    }

    public OpdChronic findVisitByOpdCode(int code) {
        String query = "SELECT * FROM OPD_CHRONIC WHERE CV_OPD_ID = ?";
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(code);
        OpdChronic opdChronic = null;
        try {
            opdChronic = getChronicVisitFromDb(query, parameters);
        } catch (OHException ohe) {
            logger.error("Could not retrieve chronic visit from database, " + ohe.getMessage());
        }
        return opdChronic;
    }

    private Opd getOpdFromDb(String query, List<Object> params) throws OHException {
        try {
            ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
            if (resultSet.next()) {
                return opdService.parseOpdRecord(resultSet);
            }
        } catch (SQLException e) {
            throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
        } finally {
            dbQuery.releaseConnection();
        }
        return null;
    }

    private OpdChronic getChronicVisitFromDb(String query, List<Object> params) throws OHException {
        try {
            ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
            if (resultSet.next()) {
                return parseChronicVisitRecord(resultSet);
            }
        } catch (SQLException e) {
            throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
        } finally {
            dbQuery.releaseConnection();
        }
        return null;
    }

    public boolean persistToDb(Opd chronicOpd, OpdChronic opdChronic, ArrayList<TherapyRow> therapies,
                               ArrayList<TherapyRow> removedTherapies) throws OHException {
        int opdCode;
        try {
            if (chronicOpd.getCode() > 0) {
                // Editing an existing Opd
                if (chronicOpd.getScheduledVisit() != null && chronicOpd.getScheduledVisit().getVisitID() > 0)
                    visitService.updateVisit(chronicOpd.getScheduledVisit(), false, dbQuery);

                if (chronicOpd.getNextVisit() != null) {
                    if (chronicOpd.getNextVisit().getVisitID() > 0)
                        visitService.updateVisit(chronicOpd.getNextVisit(), false, dbQuery);
                    else
                        chronicOpd.getNextVisit().setVisitID(visitService.saveVisit(chronicOpd.getNextVisit(), false, dbQuery));
                }
                opdService.updateOpd(chronicOpd, false, dbQuery);
                opdCode = chronicOpd.getCode();
            } else {
                // Saving a brand new Opd
                if (chronicOpd.getNextVisit() != null)
                    chronicOpd.getNextVisit().setVisitID(visitService.saveVisit(chronicOpd.getNextVisit(), false, dbQuery));
                opdCode = opdService.saveOpd(chronicOpd, false, dbQuery);
            }
            if (opdChronic != null) {
                opdChronic.setOpdCode(opdCode);
                if (opdChronic.getId() > 0)   // Editing an existing visit
                    updateVisit(opdChronic);
                else
                    saveVisit(opdChronic);
            }
            if (therapies != null && therapies.size() > 0) {
                for (TherapyRow therapyRow : therapies) {
                    therapyRow.setOpdCode(opdCode);
                    if (therapyRow.getTherapyID() > 0)
                        therapyService.updateTherapy(therapyRow, dbQuery, false);
                    else
                        therapyService.saveTherapy(therapyRow, dbQuery, false);
                }
            }
            if (removedTherapies != null && removedTherapies.size() > 0) {
                for (TherapyRow therapyRow : removedTherapies) {
                    therapyService.deleteTherapy(therapyRow, dbQuery, false);
                }
            }
            dbQuery.commit();
            return true;
        } catch (OHException ohe) {
            logger.error("Could not persist chronic opd, visit and therapy, " + ohe.getMessage());
            return false;
        } finally {
            dbQuery.releaseConnection();
        }
    }

    Date now = new Date();
    public void saveVisit(OpdChronic visit) throws OHException {
        String query = "INSERT INTO OPD_CHRONIC (" +
                "CV_OPD_ID, " +
                "CV_DATE_CREATED, " +
                "CV_LAST_UPDATED, " +
                "CV_DISEASE_CODES, " +
                "CV_BW, " +
                "CV_FBS, " +
                "CV_CREATININE, " +
                "CV_BP_MIN, " +
                "CV_BP_MAX, " +
                "CV_AF, " +
                "CV_HR, " +
                "CV_HB, " +
                "CV_CF, " +
                "CV_PO2, " +
                "CV_NOTES) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(visit.getOpdCode());
        params.add(Converters.convertToSQLDate(now));
        params.add(Converters.convertToSQLDate(now));
        params.add(visit.getDiseaseCodes());
        params.add(visit.getBodyWeight());
        params.add(visit.getFastBloodSugar());
        params.add(visit.getCreatinine());
        params.add(visit.getBloodPressureMin());
        params.add(visit.getBloodPressureMax());
        params.add(visit.getAtrialFibrillation());
        params.add(visit.getHeartRate());
        params.add(visit.getHemoglobin());
        params.add(visit.getChestFinding());
        params.add(visit.getPO2AtRest());
        params.add(visit.getNotes());
        dbQuery.setDataWithParams(query, params, false);
    }

    public void updateVisit(OpdChronic visit) throws OHException {
        String query = "UPDATE OPD_CHRONIC SET" +
                " CV_LAST_UPDATED = ?," +
                " CV_DISEASE_CODES = ?," +
                " CV_BW = ?," +
                " CV_FBS = ?," +
                " CV_CREATININE = ?," +
                " CV_BP_MIN = ?," +
                " CV_BP_MAX = ?," +
                " CV_AF = ?," +
                " CV_HR = ?," +
                " CV_HB = ?," +
                " CV_CF = ?," +
                " CV_PO2 = ?," +
                " CV_NOTES = ?" +
                " WHERE CV_ID = ?;";
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(Converters.convertToSQLDate(now));
        params.add(visit.getDiseaseCodes());
        params.add(visit.getBodyWeight());
        params.add(visit.getFastBloodSugar());
        params.add(visit.getCreatinine());
        params.add(visit.getBloodPressureMin());
        params.add(visit.getBloodPressureMax());
        params.add(visit.getAtrialFibrillation());
        params.add(visit.getHeartRate());
        params.add(visit.getHemoglobin());
        params.add(visit.getChestFinding());
        params.add(visit.getPO2AtRest());
        params.add(visit.getNotes());
        params.add(visit.getId());
        dbQuery.setDataWithParams(query, params, false);
    }

    public Opd getLastChronicOpd(int patientCode, Date date) throws OHException {
        String query = "SELECT *," +
                " (SELECT VST_ID FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_ID," +
                " (SELECT VST_DATE FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_DATE," +
                " (SELECT VST_PAT_ID FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_PAT_ID," +
                " (SELECT VST_NOTE FROM VISITS WHERE VST_ID = OPD_SCHEDULED_VISIT_ID) AS SCH_VST_NOTE," +
                " (SELECT VST_ID FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_ID," +
                " (SELECT VST_DATE FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_DATE," +
                " (SELECT VST_PAT_ID FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_PAT_ID," +
                " (SELECT VST_NOTE FROM VISITS WHERE VST_ID = OPD_NEXT_VISIT_ID) AS NXT_VST_NOTE" +
                " FROM OPD WHERE OPD_PAT_ID = ?" +
                " AND DATE(OPD_DATE_VIS) < ?" +
                " AND OPD_IS_CHRONIC = 1" +
                " ORDER BY OPD_DATE DESC;";
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(patientCode);
        params.add(new Timestamp(date.getTime()));
        ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
        try {
            if (resultSet.first())
                return opdService.parseOpdRecord(resultSet, false, true);
        } catch (SQLException sqle) {
            throw new OHException("Could not parse Opd object when retrieving last chronic Opd, " + sqle.getMessage());
        }
        return null;
    }

    public OpdChronic parseChronicVisitRecord(ResultSet resultSet) throws SQLException {
        OpdChronic opdChronic = new OpdChronic();
        opdChronic.setId(resultSet.getInt("CV_ID"));
        opdChronic.setOpdCode(resultSet.getInt("CV_OPD_ID"));
        opdChronic.setDateCreated(resultSet.getDate("CV_DATE_CREATED"));
        opdChronic.setLastUpdated(resultSet.getDate("CV_LAST_UPDATED"));
        opdChronic.setDiseaseCodes(resultSet.getString("CV_DISEASE_CODES"));
        opdChronic.setBodyWeight(resultSet.getInt("CV_BW") > 0 ? resultSet.getInt("CV_BW") : null);
        opdChronic.setFastBloodSugar(resultSet.getInt("CV_FBS") > 0 ? resultSet.getInt("CV_FBS") : null);
        opdChronic.setCreatinine(resultSet.getBigDecimal("CV_CREATININE"));
        opdChronic.setBloodPressureMin(resultSet.getInt("CV_BP_MIN") > 0 ? resultSet.getInt("CV_BP_MIN") : null);
        opdChronic.setBloodPressureMax(resultSet.getInt("CV_BP_MAX")> 0 ? resultSet.getInt("CV_BP_MAX") : null);
        opdChronic.setAtrialFibrillation(resultSet.getBoolean("CV_AF"));
        opdChronic.setHeartRate(resultSet.getInt("CV_HR")> 0 ? resultSet.getInt("CV_HR") : null);
        opdChronic.setHemoglobin(resultSet.getBigDecimal("CV_HB"));
        opdChronic.setChestFinding(resultSet.getString("CV_CF"));
        opdChronic.setPO2AtRest(resultSet.getInt("CV_PO2") > 0 ? resultSet.getInt("CV_PO2") : null);
        opdChronic.setNotes(resultSet.getString("CV_NOTES"));
        return opdChronic;
    }

    public boolean isOpdDisease(String diseaseCode) throws OHException {
        String query = "SELECT * FROM DISEASE WHERE DIS_OPD_INCLUDE = 1 AND DIS_iD_A = ?";
        ArrayList<Object> params = new ArrayList<Object>(1);
        params.add(diseaseCode);
        ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
        try {
            return resultSet.first();
        } catch (SQLException sqle) {
            throw new OHException("Could not retrieve info on whether the disease code <"
                    + diseaseCode +"> is an Opd disease", sqle);
        }
    }
}
