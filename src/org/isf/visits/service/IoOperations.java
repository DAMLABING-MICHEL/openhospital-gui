package org.isf.visits.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.visits.model.Visit;

public class IoOperations {

	public Visit getVisit(int visitId) throws OHException {
		return getVisit(visitId, true, null);
	}

	public Visit getVisit(int visitId, boolean commit, DbQueryLogger existingLogger) throws OHException {
		DbQueryLogger dbQueryLogger = existingLogger != null ? existingLogger : new DbQueryLogger();
		String query = "SELECT * FROM VISITS WHERE VST_ID = ?;";
		List<Object> params = new ArrayList<Object>(1);
		params.add(visitId);
		Visit visit = null;
		try {
			ResultSet resultSet = dbQueryLogger.getDataWithParams(query, params, commit);
			if (resultSet.next())
				visit = toVisit(resultSet);
		} catch (SQLException sqle) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), sqle);
		} finally {
			if (existingLogger == null && commit)
				dbQueryLogger.releaseConnection();
		}
		return visit;
	}

	/**
	 * returns the list of all {@link Visit}s related to a patID
	 * 
	 * @param patID - the {@link Patient} ID. If <code>0</code> return the list of all {@link Visit}s
	 * @return the list of {@link Visit}s
	 * @throws OHException 
	 */
	public ArrayList<Visit> getVisits(Integer patID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Visit> visits = null;
		List<Object> parameters = Collections.<Object>singletonList(patID);
		try {
			StringBuilder query = new StringBuilder("SELECT * FROM VISITS");
			if (patID != 0) query.append(" WHERE VST_PAT_ID = ?");
			query.append(" ORDER BY VST_PAT_ID, VST_DATE");
			
			ResultSet resultSet = null;
			if (patID != 0) {
				resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			} else {
				resultSet = dbQuery.getData(query.toString(), true);
			}
			visits = new ArrayList<Visit>(resultSet.getFetchSize());
			while (resultSet.next()) {
				visits.add(toVisit(resultSet));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return visits;
	}

	public int saveVisit(Visit visit) throws OHException {
		return saveVisit(visit, true, null);
	}

	public int saveVisit(Visit visit, boolean commit, DbQueryLogger existingLogger) throws OHException {
		DbQueryLogger dbQueryLogger = existingLogger != null ? existingLogger : new DbQueryLogger();
		ArrayList<Object> parameters = new ArrayList<Object>();
		int visitID = 0;
		try {
			String query = "INSERT INTO VISITS (VST_ID, VST_PAT_ID, VST_DATE, VST_NOTE) VALUES (?, ?, ?, ?)";
			parameters.add(visit.getVisitID());
			parameters.add(visit.getPatID());
			parameters.add(convertToSQLDate(visit.getDate()));
			parameters.add(visit.getNote());
			
			ResultSet result = dbQueryLogger.setDataReturnGeneratedKeyWithParams(query, parameters, commit);

			if (result.next()) visitID = result.getInt(1);
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (commit && existingLogger == null)
				dbQueryLogger.releaseConnection();
		}
		return visitID;
	}
	
	public int updateVisit(Visit visit) throws OHException {
		return updateVisit(visit, true, null);
	}

	public int updateVisit(Visit visit, boolean commit, DbQueryLogger existingLogger) throws OHException {
		DbQueryLogger dbQueryLogger = existingLogger != null ? existingLogger : new DbQueryLogger();
		int visitId = 0;
		try {
			String query = "UPDATE VISITS SET" +
					" VST_PAT_ID = ?," +
					" VST_DATE = ?," +
					" VST_NOTE = ?" +
					" WHERE VST_ID = ?";

			List<Object> parameters = new ArrayList<Object>();
			parameters.add(visit.getPatID());
			parameters.add(convertToSQLDate(visit.getDate()));
			parameters.add(visit.getNote());
			parameters.add(visit.getVisitID());

			ResultSet resultSet = dbQueryLogger.setDataReturnGeneratedKeyWithParams(query, parameters, commit);
			if (resultSet.next())
				visitId = resultSet.getInt(1);
		} catch (SQLException sqle) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), sqle);
		} finally {
			if (commit && existingLogger == null)
				dbQueryLogger.releaseConnection();
		}
		return visitId;
	}
	
	/**
	 * Deletes the specified {@link Visit} ID
	 * 
	 * @param visitID - the {@link Visit} ID
	 * @return <code>true</code> if the visit has been deleted, <code>false</code> otherwise
	 * @throws OHException 
	 */
	public boolean deleteVisit(int visitID, DbQueryLogger dbQueryLogger) throws OHException {
		boolean result = false;
		DbQueryLogger dbQuery = dbQueryLogger;
		boolean commit = dbQuery == null;
		
		String sqlString = "DELETE FROM VISITS WHERE VST_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(visitID);
		
		try {
			if (commit)	dbQuery = new DbQueryLogger();
			result = dbQuery.setDataWithParams(sqlString, parameters, commit);
			
		} finally {
			if (commit) dbQuery.releaseConnection();
		}
		
		return result;
	}
	
	/**
	 * Deletes all {@link Visit}s related to a patID
	 * 
	 * @param patID - the {@link Patient} ID
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHException 
	 */
	public boolean deleteAllVisits(int patID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(patID);
		boolean result = true;
		try {
			String query = "DELETE FROM VISITS WHERE VST_PAT_ID = ?";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	private Visit toVisit(ResultSet resultSet) throws SQLException {
		Visit visit = new Visit();
		visit.setVisitID(resultSet.getInt("VST_ID"));
		visit.setPatID(resultSet.getInt("VST_PAT_ID"));
		{
			GregorianCalendar visitDate = new GregorianCalendar();
			visitDate.setTime(resultSet.getDate("VST_DATE"));
			visit.setDate(visitDate);
		}
		visit.setNote(resultSet.getString("VST_NOTE"));
		return visit;
	}
	
	/**
	 * return a String representing the date in format <code>yyyy-MM-dd HH:mm:ss</code>
	 * 
	 * @param datetime
	 * @return the date in format <code>yyyy-MM-dd HH:mm:ss</code>
	 */
	private String convertToSQLDate(GregorianCalendar datetime) {
		return convertToSQLDate(datetime.getTime());
	}
	
	/**
	 * return a String representing the date in format <code>yyyy-MM-dd HH:mm:ss</code>
	 * 
	 * @param datetime
	 * @return the date in format <code>yyyy-MM-dd HH:mm:ss</code>
	 */
	private String convertToSQLDate(Date datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(datetime);
	}

}
