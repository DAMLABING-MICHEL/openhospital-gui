package org.isf.therapy.service;

import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.medtype.model.MedicalType;
import org.isf.therapy.model.Therapy;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class IoOperations {

	/**
	 * Returns the list of {@link TherapyRow}s (therapies) for specified Patient ID.
	 * @param patID - the Patient ID, if <code>patID = 0</code>, all {@link TherapyRow} objects are returned.
	 * @return the list of {@link TherapyRow}s (therapies).
	 * @throws OHException
	 */
	public ArrayList<TherapyRow> getTherapyRows(int patID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(patID);
		ArrayList<TherapyRow> thRows = null;
		ResultSet resultSet;
		try {
			String query = "SELECT * FROM THERAPIES JOIN" +
					" (MEDICALDSR JOIN MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A)" +
					" ON THR_MDSR_ID = MDSR_ID WHERE THR_DATE_DELETED IS NULL";
			if (patID != 0)
				query += " AND THR_PAT_ID = ?";
			query += " ORDER BY THR_PAT_ID, THR_ID";

			if (patID != 0) {
				resultSet = dbQuery.getDataWithParams(query, parameters, true);
			} else {
				resultSet = dbQuery.getData(query, true);
			}

			thRows = new ArrayList<TherapyRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				thRows.add(parseTherapyRecord(resultSet));
			}
		} catch (Exception e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return thRows;
	}

	/**
	 * Returns all {@link TherapyRow} objects associated to an {@link org.isf.opd.model.Opd}.
	 * @param opdCode int, the {@link org.isf.opd.model.Opd} code, must be greater then 0.
	 * @param existingQueryLogger an existing instance of {@link DbQueryLogger}, can be <code>null</code>.
	 * @return ArrayList of {@link TherapyRow} objects
	 * @throws OHException
	 */
	public ArrayList<TherapyRow> getTherapyRowsByOpd(int opdCode, DbQueryLogger existingQueryLogger) throws OHException {
		DbQueryLogger dbQuery = existingQueryLogger != null ? existingQueryLogger : new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(opdCode);
		ArrayList<TherapyRow> thRows = null;
		ResultSet resultSet;
		try {
			String query = "SELECT * FROM THERAPIES JOIN" +
					" (MEDICALDSR JOIN MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A)" +
					" ON THR_MDSR_ID = MDSR_ID WHERE THR_DATE_DELETED IS NULL AND THR_OPD_ID = ?";
			resultSet = dbQuery.getDataWithParams(query, parameters, true);

			thRows = new ArrayList<TherapyRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				thRows.add(parseTherapyRecord(resultSet));
			}
		} catch (Exception e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (existingQueryLogger == null)
				dbQuery.releaseConnection();
		}
		return thRows;
	}

	/**
	 * Deletes all {@link TherapyRow}s (therapies) for specified Patient ID
	 * @param patID - the Patient ID
	 * @return <code>true</code> if the therapies have been deleted, <code>false</code> otherwise
	 * @throws OHException
	 */
	public boolean deleteAllTherapies(int patID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(patID);
		boolean result = false;
		try {
			String query = "DELETE FROM THERAPIES WHERE THR_PAT_ID = ?";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * convert passed {@link Date} to a {@link GregorianCalendar}
	 * @param aDate - the {@link Date} to convert
	 * @return {@link GregorianCalendar}
	 */
	public GregorianCalendar convertToGregorianCalendar(Timestamp aDate) {
		if (aDate == null)
			return null;
		GregorianCalendar time = new GregorianCalendar();
		time.setTime(aDate);
		return time;
	}

	/**
	 * Deletes a {@link TherapyRow} object.
	 * @param therapyRow the {@link TherapyRow} object to delete.
	 * @param existingLogger an existing instance of {@link DbQueryLogger}, can be <code>null</code>.
	 * @param autocommit boolean, set to <code>false</code> to manually manage the transaction.
	 * @throws OHException
	 */
	public void deleteTherapy(TherapyRow therapyRow, DbQueryLogger existingLogger, boolean autocommit) throws OHException {
		DbQueryLogger dbQueryLogger = existingLogger != null ? existingLogger : new DbQueryLogger();
		String query = "UPDATE THERAPIES SET THR_DATE_DELETED = ? WHERE THR_ID = ?";
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(new Timestamp(DateTime.now().toDate().getTime()));
		params.add(therapyRow.getTherapyID());
		try {
			dbQueryLogger.setDataWithParams(query, params, autocommit);
		} catch (OHException e) {
			throw new OHException("Could not set therapy as deleted", e);
		} finally {
			if (autocommit && existingLogger == null)
				dbQueryLogger.releaseConnection();
		}
	}

	public ArrayList<TherapyRow> findTherapiesByOpdCode(int opdCode) throws OHException {
		DbQueryLogger dbQueryLogger = new DbQueryLogger();
		ArrayList<TherapyRow> therapies = new ArrayList<TherapyRow>();
		String query = "SELECT * FROM THERAPIES JOIN" +
				" (MEDICALDSR JOIN MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A)" +
				" ON THR_MDSR_ID = MDSR_ID" +
				" WHERE THR_OPD_ID = ?" +
				//" AND ? < DATE(THR_ENDDATE)" +
				" AND THR_DATE_DELETED IS NULL" +
				" ORDER BY THR_STARTDATE";
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(opdCode);
		//params.add(new Timestamp(DateTime.now().withTimeAtStartOfDay().toDate().getTime()));
		try {
			ResultSet resultSet = dbQueryLogger.getDataWithParams(query, params, true);
			while (resultSet.next())
				therapies.add(parseTherapyRecord(resultSet));
			return therapies;
		} catch (SQLException e) {
			throw new OHException("Error retrieving therapies for current Opd.", e);
		} finally {
			dbQueryLogger.releaseConnection();
		}
	}

	/**
	 * Convert database Therapy table row to {@link TherapyRow} object.
	 * @param resultSet {@link ResultSet} object that results from the query.
	 * @return {@link TherapyRow} object.
	 * @throws SQLException
	 */
	public TherapyRow parseTherapyRecord(ResultSet resultSet) throws SQLException {
		TherapyRow thRow = new TherapyRow(
				resultSet.getInt("THR_ID"),
				resultSet.getInt("THR_PAT_ID"),
				convertToGregorianCalendar(resultSet.getTimestamp("THR_STARTDATE")),
				convertToGregorianCalendar(resultSet.getTimestamp("THR_ENDDATE")),
				new Medical(
						resultSet.getInt("MDSR_ID"),
						new MedicalType(
								resultSet.getString("MDSR_MDSRT_ID_A"),
								resultSet.getString("MDSRT_DESC")),
						resultSet.getString("MDSR_CODE"),
						resultSet.getString("MDSR_DESC"),
						resultSet.getDouble("MDSR_INI_STOCK_QTI"),
						resultSet.getInt("MDSR_PCS_X_PCK"),
						resultSet.getDouble("MDSR_MIN_STOCK_QTI"),
						resultSet.getDouble("MDSR_IN_QTI"),
						resultSet.getDouble("MDSR_OUT_QTI"),
						resultSet.getInt("MDSR_LOCK")),
				resultSet.getDouble("THR_QTY"),
				resultSet.getInt("THR_UNT_ID"),
				resultSet.getInt("THR_FREQINDAY"),
				resultSet.getInt("THR_FREQINPRD"),
				resultSet.getString("THR_NOTE"),
				resultSet.getBoolean("THR_NOTIFY"),
				resultSet.getBoolean("THR_SMS"));
		thRow.setOpdCode(resultSet.getInt("THR_OPD_ID"));
		thRow.setQty2(resultSet.getDouble("THR_QTY_2"));
		thRow.setQty3(resultSet.getDouble("THR_QTY_3"));
		thRow.setQty4(resultSet.getDouble("THR_QTY_4"));
		return thRow;
	}

	/**
	 * Insert a new {@link TherapyRow} (therapy) in the DB.
	 * @param thRow - the {@link TherapyRow} (therapy).
	 * @return int, the therapyID
	 * @throws OHException
	 */
	public int newTherapy(TherapyRow thRow) throws OHException {
		return saveTherapy(thRow, null, true);
	}

	/**
	 * Save a {@link Therapy} object to the databse. If a {@param existingDbQueryLogger} is passed, the caller is
	 * responsible for releasing the database connection.
	 * @param thRow {@link TherapyRow} the object containing the therapy data.
	 * @param existingDbQueryLogger {@link DbQueryLogger} an existing connection to the database.
	 * @param autocommit {@link boolean} if set to <code>false</code> allows to manage the transaction manually.
	 * @return {@link int} the ID of the Therapy created by the database.
	 * @throws OHException if something goes wrong.
	 */
	public int saveTherapy(TherapyRow thRow, DbQueryLogger existingDbQueryLogger, boolean autocommit) throws OHException {
		DbQueryLogger dbQuery = existingDbQueryLogger != null ? existingDbQueryLogger : new DbQueryLogger();
		int therapyID = 0;
		try {
			ResultSet result = dbQuery.setDataReturnGeneratedKeyWithParams(getInsertQuery(), getInsertParams(thRow), autocommit);
			if (result.next())
				therapyID = result.getInt(1);
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (autocommit && existingDbQueryLogger == null)
				dbQuery.releaseConnection();
		}
		return therapyID;
	}

	/**
	 * Updates a {@link Therapy} object. If a {@param existingDbQueryLogger} is passed, the caller is responsible
	 * for releasing the database connection.
	 * @param thRow {@link TherapyRow} the object containing the therapy data.
	 * @param existingDbQueryLogger {@link DbQueryLogger} an existing connection to the database.
	 * @param autocommit {@link boolean} if set to <code>false</code> allows to manage the transaction manually.
	 * @return {@link int} the ID of the Therapy created by the database.
	 * @throws OHException if something goes wrong.
	 */
	public void updateTherapy(TherapyRow thRow, DbQueryLogger existingDbQueryLogger, boolean autocommit) throws OHException {
		DbQueryLogger dbQuery = existingDbQueryLogger != null ? existingDbQueryLogger : new DbQueryLogger();
		boolean success = dbQuery.setDataWithParams(getUpdateQuery(), getUpdateParams(thRow), autocommit);
		if (!success)
			throw new OHException("Could not update Therapy.");
		if (autocommit && existingDbQueryLogger == null)
			dbQuery.releaseConnection();
	}

	private String getInsertQuery() {
		return "INSERT INTO THERAPIES" +
				" (THR_ID," +
				" THR_PAT_ID," +
				" THR_OPD_ID," +
				" THR_STARTDATE," +
				" THR_ENDDATE," +
				" THR_MDSR_ID," +
				" THR_QTY," +
				" THR_QTY_2," +
				" THR_QTY_3," +
				" THR_QTY_4," +
				" THR_UNT_ID," +
				" THR_FREQINDAY," +
				" THR_FREQINPRD," +
				" THR_NOTE," +
				" THR_NOTIFY," +
				" THR_SMS) VALUES" +
				" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	private ArrayList<Object> getInsertParams(TherapyRow thRow) {
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(thRow.getTherapyID());
		parameters.add(thRow.getPatID());
		parameters.add(thRow.getOpdCode());
		parameters.add(thRow.getStartDate().getTime());
		parameters.add(thRow.getEndDate().getTime());
		parameters.add(thRow.getMedical().getCode());
		parameters.add(thRow.getQty());
		parameters.add(thRow.getQty2());
		parameters.add(thRow.getQty3());
		parameters.add(thRow.getQty4());
		parameters.add(thRow.getUnitID());
		parameters.add(thRow.getFreqInDay());
		parameters.add(thRow.getFreqInPeriod());
		parameters.add(thRow.getNote());
		parameters.add(thRow.isNotify() ? 1 : 0);
		parameters.add(thRow.isSms() ? 1 : 0);
		return parameters;
	}

	private String getUpdateQuery() {
		return "UPDATE THERAPIES SET" +
				" THR_STARTDATE = ?," +
				" THR_ENDDATE = ?," +
				" THR_MDSR_ID = ?," +
				" THR_QTY = ?," +
				" THR_QTY_2 = ?," +
				" THR_QTY_3 = ?," +
				" THR_QTY_4 = ?," +
				" THR_UNT_ID = ?," +
				" THR_FREQINDAY = ?," +
				" THR_FREQINPRD = ?," +
				" THR_NOTE = ?," +
				" THR_NOTIFY = ?," +
				" THR_SMS = ?" +
				" WHERE THR_ID = ?";
	}

	private ArrayList<Object> getUpdateParams(TherapyRow thRow) {
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(thRow.getStartDate().getTime());
		parameters.add(thRow.getEndDate().getTime());
		parameters.add(thRow.getMedical().getCode());
		parameters.add(thRow.getQty());
		parameters.add(thRow.getQty2());
		parameters.add(thRow.getQty3());
		parameters.add(thRow.getQty4());
		parameters.add(thRow.getUnitID());
		parameters.add(thRow.getFreqInDay());
		parameters.add(thRow.getFreqInPeriod());
		parameters.add(thRow.getNote());
		parameters.add(thRow.isNotify() ? 1 : 0);
		parameters.add(thRow.isSms() ? 1 : 0);
		parameters.add(thRow.getTherapyID());
		return parameters;
	}
}