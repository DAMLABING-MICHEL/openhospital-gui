package org.isf.pregnancy.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.model.PregnancyPatient;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.patient.service.IoOperations;
import org.isf.pregnancy.model.Delivery;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.utils.db.DbQuery;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.Converters;


/**
 *@author Martin Reinstadler
 * this class performs database readings and insertions for the table:
 * PREGNANCY
 *
 */
public class IoOperationsPregnancy {
	
	/**
	 * @return a list of all female {@link PregnancyPatient}
	 */
	public ArrayList<Patient> getPregnancyPatients() {
		return getPregnancyPatients(null);
	}

	/**
	 * @param regex the searchstring (part of the patients name or surname)
	 * @return a list of all female {@link PregnancyPatient} containing the searchstring
	 */
	public ArrayList<Patient> getPregnancyPatients(String regex) {
		ArrayList<Patient> patients = null;
		IoOperations patientsOperations = new IoOperations();
		
		StringBuffer stringBfr = new StringBuffer("SELECT PAT.* ");
		stringBfr.append("FROM PATIENT PAT ");
		//stringBfr.append("(SELECT ADM_PAT_ID, ADM_WRD_ID_A FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		//stringBfr.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		stringBfr.append("WHERE (PAT.PAT_SEX = 'F' AND (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null)) ");
		if (regex != null && !regex.equals("")) {
			String s = regex.trim().toLowerCase();
			String[] s1 = s.split(" ");
			
			for (int i = 0; i < s1.length; i++) {
				//stringBfr.append("AND LOWER(CONCAT(PAT_SNAME, PAT_FNAME, PAT_CITY, PAT_ADDR, PAT_ID, PAT_NOTE)) ");
				stringBfr.append("AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME)) ");
				stringBfr.append("LIKE '%").append(s1[i]).append("%' ");
			}
		}
		
		stringBfr.append("ORDER BY PAT_NAME");
		
		DbQuery dbQuery = new DbQuery();

		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			patients = new ArrayList<Patient>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Patient patient = patientsOperations.parsePatientRecord(resultSet, false);
				patients.add(patient);
			}
			dbQuery.releaseConnection();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patients;
	}

	/**
	 * @param patid - the id of the {@link PregnancyPatient}
	 * @return the list of pregnancies given a patientcode
	 * @throws OHException 
	 */
	public ArrayList<Pregnancy> getPregnancy(int patid) throws OHException {
		ArrayList<Pregnancy> pregnancies = null;
		StringBuffer stringBfr = new StringBuffer("SELECT PREG.*");
		stringBfr.append("FROM PREGNANCY PREG ");
		stringBfr.append("WHERE PREG_PAT_ID = ?");
		stringBfr.append(" AND PREG_ACTIVE <> 'D'");
		stringBfr.append("ORDER BY PREG.PREG_GRAVIDA DESC");
		List<Object> params = Collections.<Object>singletonList(patid);
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(stringBfr.toString(), params, true);
			pregnancies = new ArrayList<Pregnancy>();
			while (resultSet.next()) {
				Pregnancy preg = toPregnancy(resultSet);
				pregnancies.add(preg);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pregnancies;
	}
	
	/**
	 * @param patid - the the id of the {@link Patient}
	 * @return the active {@link Pregnancy} for the given patid, <code>null</code> otherwise
	 * @throws OHException 
	 */
	public Pregnancy getActivePregnancy(int patid) throws OHException {
		Pregnancy pregnancy = null;
		StringBuffer stringBfr = new StringBuffer("SELECT PREG.*");
		stringBfr.append(" FROM PREGNANCY PREG ");
		stringBfr.append(" WHERE PREG_PAT_ID = ?");
		stringBfr.append(" AND PREG_ACTIVE = 'Y'");
		List<Object> parameters = Collections.<Object>singletonList(patid);
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(stringBfr.toString(), parameters, true);
			if (resultSet.next()) {
				pregnancy = toPregnancy(resultSet);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pregnancy;
	}
	
	/**
	 * 
	 * @param pregid - the id of the {@link Pregnancy}
	 * @return the {@link Pregnancy} for the given id
	 * @throws OHException 
	 */
	public Pregnancy getPregnancy_byId(int pregid) throws OHException {
		Pregnancy preg = new Pregnancy(-1);
		StringBuffer stringBfr = new StringBuffer("SELECT PREG.*");
		stringBfr.append("FROM PREGNANCY PREG ");
		stringBfr.append("WHERE PREG_ID = ? ");
		stringBfr.append(" AND PREG_ACTIVE <> 'D'");
		List<Object> parameters = Collections.<Object>singletonList(pregid);
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(stringBfr.toString(), parameters, true);

			while (resultSet.next()) {
				preg = toPregnancy(resultSet);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return preg;
	}
	
	/**
	 * @param pregnancy - the {@link Pregnancy} to close (N - Delivered)
	 * @return <code>true</code> if the data has been saved, <code>false</code> otherwise
	 * @throws OHException 
	 */
	private boolean closePregnancy(Pregnancy pregnancy, DbQueryLogger dbQuery) throws OHException {
		StringBuffer stringBfr = new StringBuffer("UPDATE PREGNANCY SET ");
		stringBfr.append(" PREG_ACTIVE = 'N' ");
		stringBfr.append(" WHERE PREG_PAT_ID = ?");
		stringBfr.append(" AND PREG_ACTIVE <> 'D' ");
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(pregnancy.getPatId());
		
		return dbQuery.setDataWithParams(stringBfr.toString(), parameters, false);
	}

	/**
	 * @param pregnancy - the {@link Pregnancy} to persist
	 * @return <code>true</code> if the data has been saved, <code>false</code> otherwise
	 * @throws OHException 
	 */
	public boolean newPregnancy(Pregnancy pregnancy) throws OHException {
		StringBuffer stringBfr = new StringBuffer("INSERT INTO PREGNANCY");
		stringBfr.append("(PREG_PAT_ID, PREG_GRAVIDA, PREG_PARITY, PREG_CHILDALIVE, PREG_LMP, PREG_CALC_DELIVERY, PREG_REAL_DELIVERY, PREG_ACTIVE)");
		stringBfr.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		ArrayList<Object> params = getParams(pregnancy);
		
		int key = -1;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			closePregnancy(pregnancy, dbQuery);
			ResultSet rs = dbQuery.setDataReturnGeneratedKeyWithParams(stringBfr.toString(), params, true);
			if (rs.first()) {
				key = rs.getInt(1);
				pregnancy.setId(key);
				return true;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return false;
	}

	/**
	 * @param pregnancyId - the id of the {@link Pregnancy}
	 * @return <code>true</code> if the record is deleted from the database
	 * @throws OHException 
	 */
	public boolean deletePregnancy(int pregnancyId) throws OHException {
		StringBuffer stringBfr = new StringBuffer("UPDATE PREGNANCY SET PREG_ACTIVE = 'D'");
		stringBfr.append(" WHERE PREG_ID = ?");
		List<Object> parameters = Collections.<Object>singletonList(pregnancyId);
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			dbQuery.setDataWithParams(stringBfr.toString(), parameters, true);
			
		} finally {
			dbQuery.releaseConnection();
		}
		return true;
	}

	/**
	 * @param preg - the {@link Pregnancy} to update
	 * @return <code>true</code> if the data has been saved, <code>false</code> otherwise
	 * @throws OHException 
	 */
	public boolean updatePregnancy(Pregnancy preg) throws OHException {
		
		StringBuffer stringBfr = new StringBuffer("UPDATE PREGNANCY SET ");
		stringBfr.append("PREG_PAT_ID = ?, ");
		stringBfr.append("PREG_GRAVIDA = ?, ");
		stringBfr.append("PREG_PARITY = ?, ");
		stringBfr.append("PREG_CHILDALIVE = ?, ");
		stringBfr.append("PREG_LMP = ?, ");
		stringBfr.append("PREG_CALC_DELIVERY = ?, ");
		stringBfr.append("PREG_REAL_DELIVERY = ?, ");
		stringBfr.append("PREG_ACTIVE = ? ");
		stringBfr.append("WHERE PREG_ID = ?");
		ArrayList<Object> params = getParams(preg);
		params.add(preg.getId());
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			return dbQuery.setDataWithParams(stringBfr.toString(), params, true);
		} finally{
			dbQuery.releaseConnection();
		}
	}
	
	/**
	 * Returns all the {@link PregnancyPatient}s (with pregnancy, patient and admission information)
	 * @return 
	 * @throws OHException
	 */
	public ArrayList<PregnancyPatient> getPregnancyAdmittedPatients() throws OHException {
		ArrayList<PregnancyPatient> pregnancies = null;
		
		StringBuilder query = new StringBuilder("SELECT * FROM PREGNANCY ");
		query.append("LEFT JOIN PATIENT PAT ON PREG_PAT_ID = PAT_ID ");
		query.append("LEFT JOIN (SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		query.append("GROUP BY PAT.PAT_ID ");
		query.append(" ORDER BY PAT_ID DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet resultSet = dbQuery.getData(query.toString(), true);
			pregnancies = new ArrayList<PregnancyPatient>();

			while (resultSet.next()) {
				PregnancyPatient adPat = buildPregnancyPatient(resultSet);
				pregnancies.add(adPat);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} catch (IOException e) {
			throw new OHException(null, e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return pregnancies;
	}
	
	public ArrayList<PregnancyPatient> getPregnancyAdmittedPatients(String searchTerms) throws OHException {
		ArrayList<PregnancyPatient> pregnancies = null;
		
		StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.*, PRG.* ");
		query.append("FROM PATIENT PAT LEFT JOIN ");
		query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		query.append("LEFT JOIN PREGNANCY PRG ON (PRG.PREG_PAT_ID = PAT.PAT_ID AND PREG_ACTIVE <> 'D') ");
		query.append("WHERE PAT.PAT_SEX = 'F' AND (PAT.PAT_DELETED='N' OR PAT.PAT_DELETED is null) ");

		List<Object> parameters = new ArrayList<Object>(); 

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			String[] terms = searchTerms.split(" ");

			for (String term:terms) {
				query.append(" AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_PCODE)) ");
				query.append("LIKE ?");
				String parameter = "%"+term+"%";
				parameters.add(parameter);
			}
		}
		
		query.append(" GROUP BY PAT_ID ");
		query.append(" ORDER BY PAT_ID DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pregnancies = new ArrayList<PregnancyPatient>();

			while (resultSet.next()) {
				PregnancyPatient adPat = buildPregnancyPatient(resultSet);
				pregnancies.add(adPat);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} catch (IOException e) {
			throw new OHException(null, e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return pregnancies;
	}
	
	private AdmittedPatient buildAdmittedPatient(ResultSet resultSet) throws SQLException, IOException {
		org.isf.patient.service.IoOperations patientService = new org.isf.patient.service.IoOperations();
		Patient patient = patientService.parsePatientRecord(resultSet, false);
		Admission admission = null;
		if (resultSet.getInt("ADM_IN") == 1)
			admission = org.isf.admission.service.IoOperations.toAdmission(resultSet);
		return new AdmittedPatient(patient, admission);
	}
	
	private PregnancyPatient buildPregnancyPatient(ResultSet resultSet) throws SQLException, IOException {
		AdmittedPatient patient = buildAdmittedPatient(resultSet);
		Pregnancy pregnancy = null;
		if (((Integer) resultSet.getInt("PREG_ID")) != null)
			pregnancy = toPregnancy(resultSet);
		return new PregnancyPatient(patient, pregnancy);
	}
	
	/**
	 * Returns the active {@link Pregnancy} for this {@link Patient}
	 * (to be closed with a delivery) 
	 * @param patient
	 * @return <code>true</code> if the patient has an ongoing pregnancy, <code>false</code> otherwise 
	 * @throws OHException
	 */
	public boolean hasActivePregnancy(Patient patient) throws OHException {
		String sql = "SELECT * FROM PREGNANCY WHERE PREG_PAT_ID = ? AND PREG_ACTIVE = 'Y'";
		DbQueryLogger dbQueryLogger = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(patient.getCode());
		try {
			ResultSet resultSet = dbQueryLogger.getDataWithParams(sql, parameters, true);
			if (resultSet.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (OHException e) {
			throw new OHException(e.getMessage());
		}
		return false;
	}
	
	/**
	 * Returns the pregnancy related to this delivery
	 * @param delivery - the {@link Delivery}
	 * @return <code>true</code> if the patient has an ongoing pregancy, <code>false</code> otherwise 
	 * @throws OHException
	 */
	public boolean hasRelatedPregnancy(Delivery delivery) throws OHException {
		String sql = "SELECT * FROM PREGNANCY WHERE PREG_ID = ?";
		DbQueryLogger dbQueryLogger = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(delivery.getPregnancy().getId());
		try {
			ResultSet resultSet = dbQueryLogger.getDataWithParams(sql, parameters, true);
			if (resultSet.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (OHException e) {
			throw new OHException(e.getMessage());
		}
		return false;
	}
	
	/**
	 * Converts a {@link ResultSet} row into an {@link Pregnancy} object.
	 * @param resultSet the result set to read.
	 * @return the converted object.
	 * @throws SQLException if an error occurs.
	 */
	public static Pregnancy toPregnancy(ResultSet resultSet) throws SQLException
	{
		Pregnancy pregnancy = new Pregnancy(0);
		pregnancy.setId(resultSet.getInt("PREG_ID"));
		pregnancy.setPatId(resultSet.getInt("PREG_PAT_ID"));
		pregnancy.setGravida(resultSet.getInt("PREG_GRAVIDA"));
		pregnancy.setParity(resultSet.getInt("PREG_PARITY"));
		pregnancy.setChildrenAlive(resultSet.getInt("PREG_CHILDALIVE"));
		pregnancy.setLmp(Converters.toCalendar(resultSet.getTimestamp("PREG_LMP")));
		pregnancy.setScheduled_delivery(Converters.toCalendar(resultSet.getTimestamp("PREG_CALC_DELIVERY")));
		pregnancy.setReal_delivery(Converters.toCalendar(resultSet.getTimestamp("PREG_REAL_DELIVERY")));
		pregnancy.setActive(resultSet.getString("PREG_ACTIVE") == null ? 'N' : resultSet.getString("PREG_ACTIVE").charAt(0));
		return pregnancy;
	}
	
	private ArrayList<Object> getParams(Pregnancy pregnancy) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(pregnancy.getPatId());
		params.add(pregnancy.getGravida());
		params.add(pregnancy.getParity());
		params.add(pregnancy.getChildrenAlive());
		params.add(pregnancy.getLmp() == null ? null : Converters.convertToSQLDate(pregnancy.getLmp()));
		params.add(pregnancy.getScheduled_delivery() == null ? null : Converters.convertToSQLDate(pregnancy.getScheduled_delivery()));
		params.add(pregnancy.getReal_delivery() == null ? null : Converters.convertToSQLDate(pregnancy.getReal_delivery()));
		params.add(String.valueOf(pregnancy.getActive()));
		return params;
	}

}
