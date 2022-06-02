package org.isf.malnutrition.service;

import org.isf.admission.model.Admission;
import org.isf.admission.service.IoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.malnutrition.model.MalnutritionVisit;
import org.isf.malnutrition.model.TfuPatient;
import org.isf.menu.gui.MainMenu;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.Converters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Persistence class for the malnutrition module.
 */
public class IoOperation {

	private DbQueryLogger dbQueryLogger = new DbQueryLogger();
	private IoOperations admissionService = new IoOperations();

	/**
	 * Returns all the available {@link MalnutritionVisit} for the specified admission id.
	 * @param admissionId the admission id
	 * @return the retrieved malnutrition.
	 * @throws OHException if an error occurs retrieving the malnutrition list.
	 */
	public ArrayList<MalnutritionVisit> getMalnutritionVisits(int admissionId) throws OHException{

		ArrayList<MalnutritionVisit> malnutritionVisits = new ArrayList<MalnutritionVisit>();
		try{
			String query = "SELECT * FROM MALNUTRITIONCONTROL" +
					" JOIN ADMISSION ON MLN_ADM_ID = ADM_ID" +
					" JOIN PATIENT ON ADM_PAT_ID = PAT_ID" +
					" WHERE MLN_ADM_ID = ?" +
					" AND MLN_DATE_DELETED IS NULL" +
					" ORDER BY MLN_DATE_VIS DESC, MLN_ID DESC";
			List<Object> parameters = Collections.<Object>singletonList(admissionId);
			ResultSet resultSet = dbQueryLogger.getDataWithParams(query, parameters, true);

			malnutritionVisits = new ArrayList<MalnutritionVisit>(resultSet.getFetchSize());

			while (resultSet.next())
				malnutritionVisits.add(parseDbRecord(resultSet));

		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQueryLogger.releaseConnection();
		}	
		return malnutritionVisits;
	}

	public MalnutritionVisit findPreviousVisitByType(int patientId, GregorianCalendar visitDate, String visitType) throws OHException {
		String query = "SELECT * FROM MALNUTRITIONCONTROL" +
				" JOIN ADMISSION ON MLN_ADM_ID = ADM_ID" +
				" WHERE ADM_PAT_ID = ?" +
				" AND DATE(MLN_DATE_VIS) < ?" +
				" AND MLN_TYPE = ?" +
				" AND MLN_DATE_DELETED IS NULL" +
				" ORDER BY MLN_DATE_VIS DESC, MLN_ID DESC";
		List<Object> params = new ArrayList<Object>(2);
		params.add(patientId);
		params.add(Converters.convertToSQLDateLimited(visitDate));
		params.add(visitType);
		try {
			ResultSet resultSet = dbQueryLogger.getDataWithParams(query, params, true);
			if (resultSet.next())
				return parseDbRecord(resultSet);
		} catch (SQLException sqle) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), sqle);
		} finally {
			dbQueryLogger.releaseConnection();
		}
		return null;
	}

	public boolean saveMalnutritionVisits(List<MalnutritionVisit> malnutritionVisits) throws OHException {
		try {
			for (MalnutritionVisit malnutritionVisit : malnutritionVisits) {
				if (malnutritionVisit.getId() == 0)
					saveMalnutritionVisit(malnutritionVisit, false, dbQueryLogger);
				else
					updateMalnutritionVisit(malnutritionVisit, false, dbQueryLogger);
			}
			dbQueryLogger.commit();
		} catch (OHException ohe) {
			throw new OHException(ohe.getMessage());
		} finally {
			dbQueryLogger.releaseConnection();
		}
		return true;
	}

	public int saveMalnutritionVisit(MalnutritionVisit malnutritionVisit) throws OHException {
		return saveMalnutritionVisit(malnutritionVisit, true, null);
	}

	/**
	 * Persists a {@link MalnutritionVisit} record to the database.
	 * @param malnutritionVisit the {@link MalnutritionVisit} object.
	 * @param commit boolean value, set to {@link false} to manage transaction manually.
	 * @param existingLogger an instance of {@link DbQueryLogger}, pass it to manage transaction manually.
	 * @return int the id of the newly inserted record, 0 in case of error.
	 * @throws OHException if something goes wrong.
     */
	public int saveMalnutritionVisit(MalnutritionVisit malnutritionVisit, boolean commit, DbQueryLogger existingLogger) throws OHException{
		DbQueryLogger dbQuery = existingLogger != null ? existingLogger : dbQueryLogger;
		int malnutritionVisitId = 0;
		try{
			String query = "INSERT INTO MALNUTRITIONCONTROL" +
					" (MLN_TYPE," +
					" MLN_EXT_CODE," +
					" MLN_DATE_CREATED," +
					" MLN_LAST_UPDATED," +
					" MLN_DATE_VIS," +
					" MLN_US_ID_A," +
					" MLN_ADM_ID," +
					" MLN_PAT_AGE," +
					" MLN_WEIGHT," +
					" MLN_HEIGHT," +
					" MLN_MIN_WEIGHT," +
					" MLN_MIN_WEIGHT_DATE," +
					" MLN_MUAC," +
					" MLN_WH," +
					" MLN_OEDEMA," +
					" MLN_RELAPSE," +
					" MLN_KM," +
					" MLN_READMISSION," +
					" MLN_REFERRED_BY," +
					" MLN_OUTCOME," +
					" MLN_TB," +
					" MLN_HIV," +
					" MLN_SEPSIS," +
					" MLN_ANAEMIA," +
					" MLN_PNEUMONIA," +
					" MLN_MALARIA," +
					" MLN_CP," +
					" MLN_RICKETTS," +
					" MLN_OTHER," +
					" MLN_REMARKS," +
					" MLN_FOLLOWUP," +
					" MLN_CLOSEST_HC," +
					" MLN_FROM_OTHER_OTP," +
					" MLN_FROM_OTHER_TFP," +
					" MLN_TO_OTHER_TFP," +
					" MLN_MEDICAL_TRASNFER," +
					" MLN_PAT_ID," +
					" MLN_PAT_PCODE," +
					" MLN_PAT_NAME," +
					" MLN_PAT_GENDER," +
					" MLN_PAT_MONTHS," +
					" MLN_PAT_CITY," +
					" MLN_PAT_ADDR)" +
			" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			ResultSet resultSet = dbQuery.setDataReturnGeneratedKeyWithParams(query, getParams(malnutritionVisit, false), commit);
			if (resultSet.first())
				malnutritionVisitId = resultSet.getInt(1);

		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			if (commit && existingLogger == null)
				dbQuery.releaseConnection();
		}
		return malnutritionVisitId;
	}

	public boolean updateMalnutritionVisit(MalnutritionVisit malnutritionVisit) throws OHException {
		return updateMalnutritionVisit(malnutritionVisit, true, null);
	}

	/**
	 * Updates a {@link MalnutritionVisit} record.
	 * @param malnutritionVisit the {@link MalnutritionVisit} instance.
	 * @param commit boolean value, set to {@link false} to manage transaction manually.
	 * @param existingLogger an instance of {@link DbQueryLogger}, pass it to manage transaction manually.
	 * @return boolean {@code true} if the update is successful.
	 * @throws OHException if something goes wrong.
     */
	public boolean updateMalnutritionVisit(MalnutritionVisit malnutritionVisit, boolean commit, DbQueryLogger existingLogger) throws OHException {
		DbQueryLogger dbQuery = existingLogger != null ? existingLogger : dbQueryLogger;
		boolean result;
		try{
			String query = "UPDATE MALNUTRITIONCONTROL SET" +
					" MLN_TYPE = ?," +
					" MLN_EXT_CODE = ?," +
					" MLN_LAST_UPDATED = ?," +
					" MLN_DATE_VIS = ?," +
					" MLN_US_ID_A = ?," +
					" MLN_ADM_ID = ?," +
					" MLN_PAT_AGE = ?," +
					" MLN_WEIGHT = ?," +
					" MLN_HEIGHT = ?," +
					" MLN_MIN_WEIGHT = ?," +
					" MLN_MIN_WEIGHT_DATE = ?," +
					" MLN_MUAC = ?," +
					" MLN_WH = ?," +
					" MLN_OEDEMA = ?," +
					" MLN_RELAPSE = ?," +
					" MLN_KM = ?," +
					" MLN_READMISSION = ?," +
					" MLN_REFERRED_BY = ?," +
					" MLN_OUTCOME = ?," +
					" MLN_TB = ?," +
					" MLN_HIV = ?," +
					" MLN_SEPSIS = ?," +
					" MLN_ANAEMIA = ?," +
					" MLN_PNEUMONIA = ?," +
					" MLN_MALARIA = ?," +
					" MLN_CP = ?," +
					" MLN_RICKETTS = ?," +
					" MLN_OTHER = ?," +
					" MLN_REMARKS = ?," +
					" MLN_FOLLOWUP = ?," +
					" MLN_CLOSEST_HC = ?," +
					" MLN_FROM_OTHER_OTP = ?," +
					" MLN_FROM_OTHER_TFP = ?," +
					" MLN_TO_OTHER_TFP = ?," +
					" MLN_MEDICAL_TRASNFER = ?," +
					" MLN_PAT_ID = ?," +
					" MLN_PAT_PCODE = ?," +
					" MLN_PAT_NAME = ?," +
					" MLN_PAT_GENDER = ?," +
					" MLN_PAT_MONTHS = ?," +
					" MLN_PAT_CITY = ?," +
					" MLN_PAT_ADDR = ?" +
					" WHERE MLN_ID = ?";

			result = dbQuery.setDataWithParams(query, getParams(malnutritionVisit, true), commit);
		} finally{
			if (commit && existingLogger == null)
				dbQuery.releaseConnection();
		}
		return result;
	}

	private List<Object> getParams(MalnutritionVisit malnutritionVisit, boolean update) {
		List<Object> params = new ArrayList<Object>();
		params.add(malnutritionVisit.getType());
		params.add(malnutritionVisit.getExtCode());
		if (!update) params.add(Converters.convertToSQLDate(new GregorianCalendar()));
		params.add(Converters.convertToSQLDate(new GregorianCalendar()));
		params.add(Converters.convertToSQLDateLimited(malnutritionVisit.getVisitDate()));
		params.add(MainMenu.getUser());
		params.add(malnutritionVisit.getAdmissionId());
		params.add(malnutritionVisit.getAgeInMonthsAtVisitTime());
		params.add(malnutritionVisit.getWeight());
		params.add(malnutritionVisit.getHeight());
		params.add(malnutritionVisit.getMinWeight());
		params.add(Converters.convertToSQLDateLimited(malnutritionVisit.getMinWeightDate()));
		params.add(malnutritionVisit.getMuac());
		params.add(malnutritionVisit.getWh());
		params.add(malnutritionVisit.getOedema());
		params.add(malnutritionVisit.getRelapse());
		params.add(malnutritionVisit.getKm());
		params.add(malnutritionVisit.isReadmission());
		params.add(malnutritionVisit.getReferredBy());
		params.add(malnutritionVisit.getOutcome());
		params.add(malnutritionVisit.isTb());
		params.add(malnutritionVisit.getHiv() == null ? "U" : malnutritionVisit.getHiv());
		params.add(malnutritionVisit.isSepsis());
		params.add(malnutritionVisit.isAnaemia());
		params.add(malnutritionVisit.isPneumonia());
		params.add(malnutritionVisit.isMalaria());
		params.add(malnutritionVisit.isCP());
		params.add(malnutritionVisit.isRicketts());
		params.add(malnutritionVisit.isOther());
		params.add(malnutritionVisit.getRemarks());
		params.add(malnutritionVisit.isFollowUp());
		params.add(malnutritionVisit.getClosestHealthCenter());
		params.add(malnutritionVisit.isFromOtherOTP());
		params.add(malnutritionVisit.isFromOtherTFP());
		params.add(malnutritionVisit.isTransferToOtherOTP());
		params.add(malnutritionVisit.isMedicalTransfer());
		//Patient
		params.add(malnutritionVisit.getPatient().getId());
		params.add(malnutritionVisit.getPatient().getCode());
		params.add(malnutritionVisit.getPatient().getName());
		params.add(String.valueOf(malnutritionVisit.getPatient().getGender()));
		params.add(malnutritionVisit.getPatient().getMonthsAtAdmissionTime());
		params.add(malnutritionVisit.getPatient().getCity());
		params.add(malnutritionVisit.getPatient().getAddress());
		if (update) params.add(malnutritionVisit.getId());

		return params;
	}

	
	/**
	 * Deletes all malnutrition rows associated with an {@link Admission}.
	 * @param admissionId the ID of the {@link Admission} to which the malnutrition visit refers.
	 * @return <code>true</code> if the malnutritionVisit has been deleted, <code>false</code> otherwise.
	 * @throws OHException if an error occurs deleting the specified malnutritionVisit.
	 */
	public boolean deleteMalnutritionVisit(int admissionId) throws OHException {
		boolean result = false;
		try {
			String query = "UPDATE MALNUTRITIONCONTROL SET MLN_DATE_DELETED = ? WHERE MLN_ADM_ID = ?";
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(Converters.convertToSQLDate(new Date()));
			parameters.add(admissionId);
			result = dbQueryLogger.setDataWithParams(query, parameters, true);
		} finally{
			dbQueryLogger.releaseConnection();
		}
		return result;	
	}

	/**
	 * Parse database record into {@link MalnutritionVisit} object.
	 * @param resultSet the ResulstSet instance representing the query result
	 * @return a {@link MalnutritionVisit} instance.
	 * @throws SQLException in case a parsing error occurs.
     */
	public MalnutritionVisit parseDbRecord(ResultSet resultSet) throws SQLException {
		MalnutritionVisit malnutritionVisit = new MalnutritionVisit();
		malnutritionVisit.setId(resultSet.getInt("MLN_ID"));
		malnutritionVisit.setType(resultSet.getString("MLN_TYPE"));
		malnutritionVisit.setExtCode(resultSet.getString("MLN_EXT_CODE"));
		malnutritionVisit.setDateCreated(Converters.toCalendar(resultSet.getDate("MLN_DATE_CREATED")));
		malnutritionVisit.setLastUpdated(Converters.toCalendar(resultSet.getDate("MLN_LAST_UPDATED")));
		malnutritionVisit.setVisitDate(Converters.toCalendar(resultSet.getDate("MLN_DATE_VIS")));
		malnutritionVisit.setUser(resultSet.getString("MLN_US_ID_A"));
		malnutritionVisit.setAdmission(admissionService.toAdmission(resultSet));
		malnutritionVisit.setPatient(new TfuPatient(
				resultSet.getInt("MLN_PAT_ID"),
				resultSet.getString("MLN_PAT_PCODE"),
				resultSet.getString("MLN_PAT_NAME"),
				resultSet.getString("MLN_PAT_GENDER").charAt(0),
				resultSet.getInt("MLN_PAT_MONTHS"),
				resultSet.getString("MLN_PAT_CITY"),
				resultSet.getString("MLN_PAT_ADDR")
		));
		malnutritionVisit.setAgeInMonthsAtVisitTime(resultSet.getInt("MLN_PAT_AGE"));
		malnutritionVisit.setWeight(resultSet.getBigDecimal("MLN_WEIGHT"));
		malnutritionVisit.setHeight(resultSet.getBigDecimal("MLN_HEIGHT"));
		malnutritionVisit.setMinWeight(resultSet.getBigDecimal("MLN_MIN_WEIGHT"));
		malnutritionVisit.setMinWeightDate(Converters.toCalendar(resultSet.getDate("MLN_MIN_WEIGHT_DATE")));
		malnutritionVisit.setMuac(resultSet.getBigDecimal("MLN_MUAC"));
		malnutritionVisit.setWh(getNullableInteger(resultSet, "MLN_WH"));
		malnutritionVisit.setOedema(getNullableInteger(resultSet, "MLN_OEDEMA"));
		malnutritionVisit.setRelapse(resultSet.getBoolean("MLN_RELAPSE"));
		malnutritionVisit.setKm(resultSet.getString("MLN_KM"));
		malnutritionVisit.setReadmission(resultSet.getBoolean("MLN_READMISSION"));
		malnutritionVisit.setReferredBy(resultSet.getString("MLN_REFERRED_BY"));
		malnutritionVisit.setOutcome(resultSet.getString("MLN_OUTCOME"));
		malnutritionVisit.setRemarks(resultSet.getString("MLN_REMARKS"));
		// The following is to avoid getBoolean to return FALSE even if the value in the db is NULL
		malnutritionVisit.setTb(resultSet.getBoolean("MLN_TB"));
		malnutritionVisit.setHiv(resultSet.getString("MLN_HIV"));
		malnutritionVisit.setSepsis(resultSet.getBoolean("MLN_SEPSIS"));
		malnutritionVisit.setAnaemia(resultSet.getBoolean("MLN_ANAEMIA"));
		malnutritionVisit.setPneumonia(resultSet.getBoolean("MLN_PNEUMONIA"));
		malnutritionVisit.setMalaria(resultSet.getBoolean("MLN_MALARIA"));
		malnutritionVisit.setCP(resultSet.getBoolean("MLN_CP"));
		malnutritionVisit.setRicketts(resultSet.getBoolean("MLN_RICKETTS"));
		malnutritionVisit.setOther(resultSet.getBoolean("MLN_OTHER"));
		malnutritionVisit.setFollowUp(resultSet.getBoolean("MLN_FOLLOWUP"));
		malnutritionVisit.setClosestHealthCenter(resultSet.getString("MLN_CLOSEST_HC"));
		malnutritionVisit.setFromOtherTFP(resultSet.getBoolean("MLN_FROM_OTHER_TFP"));
		malnutritionVisit.setFromOtherOTP(resultSet.getBoolean("MLN_FROM_OTHER_OTP"));
		malnutritionVisit.setTransferToOtherOTP(resultSet.getBoolean("MLN_TO_OTHER_TFP"));
		malnutritionVisit.setMedicalTransfer(resultSet.getBoolean("MLN_MEDICAL_TRASNFER"));
		return malnutritionVisit;
	}

	Integer getNullableInteger(ResultSet resultSet, String fieldName) throws SQLException {
		String stringValue = resultSet.getString(fieldName);
		Integer result = null;
		if (stringValue != null) {
			try {
				result = Integer.valueOf(stringValue);
			} catch (NumberFormatException nfe) {
				// Do nothing, leaves the result to null
			}
		}
		return result;
	}
	
	/**
	 * Set the type of the admission to 'M' - Malnutrition
	 * @param admID - the {@link Admission}'s ID
	 * @return <code>true</code> if the type of admission has been set, <code>false</code> otherwise
	 * @throws OHException 
	 */
	public boolean setMalnutrition(int admID) throws OHException {
		boolean result = false;
		String query = "UPDATE ADMISSION SET ADM_TYPE = 'M' WHERE ADM_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(admID);

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			result = dbQuery.setDataWithParams(query, parameters, true);
		} catch (OHException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	/**
	 * Set the type of the admission to 'N' - Normal
	 * @param admissionId - the {@link Admission}'s ID
	 * @return <code>true</code> if the type of admission has been set, <code>false</code> otherwise
	 * @throws OHException 
	 */
	public boolean unsetMalnutrition(int admissionId) throws OHException {
		boolean result = false;
		String query = "UPDATE ADMISSION SET ADM_TYPE = 'N' WHERE ADM_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(admissionId);

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			result = dbQuery.setDataWithParams(query, parameters, true);
		} catch (OHException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
}
