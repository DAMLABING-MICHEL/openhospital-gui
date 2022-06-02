package org.isf.pregnancy.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.isf.admission.model.PregnancyPatient;
import org.isf.generaldata.MessageBundle;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregnancyexam.model.PregnancyExam;
import org.isf.pregnancyexam.model.PregnancyExamResult;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.db.DbQuery;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.Converters;


/**
 * @author Martin Reinstadler this class performs database readings and
 *         insertions for the tables: PREGNANCY, PREGNANCYVISIT
 * 
 */
public class IoOperationsPregnancyVisit {
	/**
	 * @param pat_id
	 *            the code of the {@link PregnancyPatient}
	 * @return the list of all pregnancyvisits related to a pregnancy of the
	 *         patient
	 * @throws OHException 
	 */
	public ArrayList<PregnancyVisit> getPregnancyVisits(int pat_id) throws OHException {
		ArrayList<PregnancyVisit> pregnancyvisits = new ArrayList<PregnancyVisit>();
		StringBuffer stringBfr = new StringBuffer();
		stringBfr.append("SELECT PVIS.PVIS_ID, PREG.PREG_GRAVIDA, PREG_PAT_ID, PREG_LMP, PREG_CALC_DELIVERY,PREG_ACTIVE,PVIS_PREG_ID,");
		stringBfr.append(" PVIS_DATE,PVIS_NEXT_DATE, PVIS_PTT_ID_A, PVIS_NOTE, PVIS_TYPE");
		stringBfr.append(" FROM PREGNANCY PREG,  PREGNANCYVISIT PVIS");
		stringBfr.append(" WHERE PREG.PREG_ID= PVIS.PVIS_PREG_ID AND PREG.PREG_PAT_ID = ?");
		stringBfr.append(" UNION ALL ");
		stringBfr.append("SELECT PDEL_ADM_ID AS PVIS_ID,PREG.PREG_GRAVIDA,PREG_PAT_ID,PREG_LMP,PREG_CALC_DELIVERY,PREG_ACTIVE,PDEL_PREG_ID AS PVIS_PREG_ID,");
		stringBfr.append(" PDEL_DATE_DEL AS PVIS_DATE, PDEL_DATE_DEL AS PVIS_NEXT_DATE,PDEL_DATE_DEL AS PVIS_PTT_ID_A,PDEL_NOTE,0 AS PVIS_TYPE");
		stringBfr.append(" FROM PREGNANCY PREG, PREGNANCYDELIVERY PDEL");
		stringBfr.append(" WHERE PREG.PREG_ID = PDEL.PDEL_PREG_ID AND PREG.PREG_PAT_ID = ?");
		stringBfr.append(" GROUP BY PVIS_ID ");
		stringBfr.append(" ORDER BY PVIS_DATE DESC; ");
		List<Object> parameters = new ArrayList<Object>(); 
		parameters.add(pat_id);
		parameters.add(pat_id);
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(stringBfr.toString(), parameters, true);
			while (resultSet.next()) {
				PregnancyVisit pvisit = toPregnancyVisit(resultSet);
				pregnancyvisits.add(pvisit);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbQuery.releaseConnection();
		}

		return pregnancyvisits;
	}

	/**
	 * 
	 * @param visit - the {@link PregnancyVisit} to save
	 * @return the if of the inserted tuple
	 * @throws OHException 
	 */
	public int insertPregnancyVisit(PregnancyVisit visit) throws OHException {
		StringBuilder sqlString = new StringBuilder("INSERT INTO PREGNANCYVISIT");
		sqlString.append("(PVIS_PREG_ID, PVIS_DATE, PVIS_NEXT_DATE, PVIS_PTT_ID_A, PVIS_NOTE, PVIS_TYPE)");
		sqlString.append(" VALUES");
		sqlString.append(" (?,?,?,?,?,?)");
		ArrayList<Object> params = getParams(visit);
		
		int key = -1;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet rs = dbQuery.setDataReturnGeneratedKeyWithParams(sqlString.toString(), params, true);
			if (rs.first()) {
				key = rs.getInt(1);
				visit.setVisitID(key);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (OHException e) {
			throw new OHException(e.getMessage());
		} finally {
			dbQuery.releaseConnection();
		}
		return key;
	}
	
	/**
	 * inserts a set of {@link PregnancyExamResult} in the database
	 * @param visitid the id of the {@link PregnancyVisit}
	 * @param examresults the list of {@link PregnancyExamResult}
	 * @return true if the examresults are inserted correctly
	 */
	public boolean insertExamResult(int visitid,
			ArrayList<PregnancyExamResult> examresults) {
		StringBuffer stringBfr = new StringBuffer(
				"INSERT INTO PREGNANCYEXAMRESULT (	PEXRES_PVIS_ID , 	PEXRES_PREGEX_ID , PEXRES_OUTCOME) VALUES ");
		for (int a = 0; a < examresults.size() - 1; a++) {
			PregnancyExamResult ex = examresults.get(a);
			stringBfr.append(" (" + visitid + " , '" + ex.getExamCode()
					+ "' , '" + ex.getOutcome() + "'), ");
		}
		PregnancyExamResult ex = examresults.get(examresults.size() - 1);
		stringBfr.append(" (" + visitid + " , '" + ex.getExamCode() + "' , '"
				+ ex.getOutcome() + "');");
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param visitid
	 *            the id of the {@link PregnancyVisit}
	 * @param examCode
	 *            the id of the {@link PregnancyExam}
	 * @param outcome
	 *            the result of the {@link PregnancyExamResult}
	 * @return true if the tuple is updated correctly
	 */
	public boolean updateExamResult(int visitid, String examCode, String outcome) {

		StringBuffer stringBfr = new StringBuffer(
				"UPDATE PREGNANCYEXAMRESULT SET PEXRES_OUTCOME= '"
						+ outcome + "' ");
		stringBfr.append("WHERE PEXRES_PVIS_ID = " + visitid
				+ " AND PEXRES_PREGEX_ID= '" + examCode + "'");
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	/**
	 * 
	 * @param visitId - the id of the {@link PregnancyVisit}
	 * @return <code>true</code> if all the {@link PregnancyExamResult} for 
	 * the specified visit are deleted, <code>false</code> otherwise or 
	 * no records have been found
	 * @throws OHException 
	 */
	private boolean deletePregnancyExamResults(int visitId, DbQueryLogger dbQuery) throws OHException {
		String sql = "DELETE FROM PREGNANCYEXAMRESULT WHERE PEXRES_PVIS_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(visitId);
		return dbQuery.setDataWithParams(sql, parameters, false);
	}
	
	/**
	 * 
	 * @param visitId the id of the {@link PregnancyVisit}
	 * @return a {@link HashMap} wit the id of the {@link PregnancyExam} as key and the
	 *  {@link PregnancyExamResult} asvalue;
	 */
	public HashMap<String, PregnancyExamResult> getExamResults(int visitId) {
		HashMap<String, PregnancyExamResult> result = new HashMap<String, PregnancyExamResult>();
		StringBuffer stringBfr = new StringBuffer(
				"SELECT * FROM PREGNANCYEXAMRESULT ");
		stringBfr.append("WHERE PEXRES_PVIS_ID= " + visitId);
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			while (resultSet.next()) {
				PregnancyExamResult res = new PregnancyExamResult();
				res.setVisitid(resultSet.getInt("PEXRES_PVIS_ID"));
				res.setExamCode(resultSet.getString("PEXRES_PREGEX_ID"));
				res.setOutcome(resultSet.getString("PEXRES_OUTCOME"));
				res.setPregnancyExamResultId(resultSet.getInt("PEXRES_ID"));
				result.put(res.getExamCode(), res);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param visitId the id of the {@link PregnancyVisit}
	 * @param date new date of the {@link PregnancyVisit}
	 * @param nextdate the scheduled first next date of the {@link PregnancyVisit}
	 * @param treatmenttype the id of the {@link PregnantTreatmentType}
	 * @param note the note of the visit
	 * @param visittype the type of the visit
	 * @return true if the tuple is updated correctly
	 */
	public boolean updatePregnancyVisit(int visitId, GregorianCalendar date, GregorianCalendar nextdate,
			String treatmenttype, String note, int visittype) {
		DbQuery dbQuery = new DbQuery();
		Connection conn;
		try {

			conn = dbQuery.getConnection();
			String query = "UPDATE PREGNANCYVISIT SET PVIS_DATE = ?, PVIS_NEXT_DATE = ?,"
					+ " PVIS_PTT_ID_A = ? , PVIS_NOTE = ? ," +
					" PVIS_TYPE =?  WHERE PVIS_ID = ?  ";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setTimestamp(1, new java.sql.Timestamp(date.getTime()
					.getTime()));
			if (nextdate != null)
				pstmt.setTimestamp(2, new java.sql.Timestamp(nextdate.getTime()
						.getTime()));
			else
				pstmt.setNull(2, java.sql.Types.NULL);
			if (treatmenttype != null)
				pstmt.setString(3, treatmenttype);
			else
				pstmt.setNull(3, java.sql.Types.NULL);
			if (note != null)
				pstmt.setString(4, note);
			else
				pstmt.setNull(4, java.sql.Types.NULL);
			
			pstmt.setInt(5, visittype);
			pstmt.setInt(6, visitId);
			pstmt.executeUpdate();
			return true;

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return false;

	}

	/**
	 * 
	 * @param visitid - the id of the {@link PregnancyVisit}
	 * @return <code>true</code> if the visit is deleted correctly, <code>false</code> otherwise
	 * @throws OHException 
	 */
	public boolean deleteVisit(int visitid) throws OHException {
		String sqlString = "DELETE FROM PREGNANCYVISIT WHERE PVIS_ID = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(visitid);
		try {
			deletePregnancyExamResults(visitid, dbQuery);
			return dbQuery.setDataWithParams(sqlString, parameters, true);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	//////////////////////////////
	public HashMap<String, Integer> getCountCPN(int year) throws OHException, ParseException{
		String query  = "SELECT count(OPD_ID) as TOTAL  from opd where  OPD_DATE BETWEEN ? AND ?";
		String query1 = "SELECT count(PVIS_ID) as TOTAL  from pregnancyvisit where  PVIS_DATE BETWEEN ? AND ? and PVIS_PTT_ID_A =?";
		String query2 = "SELECT count(PVIS_ID) as TOTAL  from pregnancyvisit where  PVIS_DATE BETWEEN ? AND ? and PVIS_PTT_ID_A != ?";
		List<Object> parameters = new ArrayList<Object>(2);
		List<Object> parameters1 = new ArrayList<Object>(3);
		List<Object> parameters2 = new ArrayList<Object>(3);
		
		DateFormat dateFromat = new SimpleDateFormat("dd/MM/yyyy");
		Date dateFrom = new Date();
		Date dateTo = new Date();
		dateFrom = dateFromat.parse("01/01/"+year);
		dateTo = dateFromat.parse("31/12/"+year);
		String stringDateFrom = convertToSQLDateLimited(dateFrom);
		String stringDateTo = convertToSQLDateLimited(dateTo);
		
		parameters.add(stringDateFrom);
		parameters.add(stringDateTo);
		parameters1.add(stringDateFrom);
		parameters1.add(stringDateTo);
		parameters2.add(stringDateFrom);
		parameters2.add(stringDateTo);
		parameters1.add("N");
		parameters2.add("N");
		
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
//			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
//			if (resultSet.next()) {
//				results.put("total",resultSet.getInt("TOTAL"));
//			}
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalCpnN",resultSet.getInt("TOTAL"));
			}
			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
			if (resultSet.next()) {
				results.put("totalCpnR",resultSet.getInt("TOTAL"));
			}			
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return results;
	}
	private String convertToSQLDateLimited(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	/**
	 * Converts a {@link ResultSet} row into an {@link PregnancyVisit} object.
	 * @param resultSet the result set to read.
	 * @return the converted object.
	 * @throws SQLException if an error occurs.
	 */
	public static PregnancyVisit toPregnancyVisit(ResultSet resultSet) throws SQLException
	{
		PregnancyVisit pregnancyVisit = new PregnancyVisit();
		pregnancyVisit.setVisitId(resultSet.getInt("PVIS_ID"));
		pregnancyVisit.setPregnancId(resultSet.getInt("PVIS_PREG_ID"));
		pregnancyVisit.setPregnancyNr(resultSet.getInt("PREG_GRAVIDA"));
		pregnancyVisit.setDate(Converters.toCalendar(resultSet.getTimestamp("PVIS_DATE")));
		pregnancyVisit.setNextVisitdate(Converters.toCalendar(resultSet.getTimestamp("PVIS_NEXT_DATE")));
		pregnancyVisit.setTreatmenttype(resultSet.getString("PVIS_PTT_ID_A"));
		pregnancyVisit.setNote(resultSet.getString("PVIS_NOTE"));
		pregnancyVisit.setType(resultSet.getInt("PVIS_TYPE"));
		return pregnancyVisit;
	}
	
	private ArrayList<Object> getParams(PregnancyVisit visit) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(visit.getPregnancyId());
		params.add(Converters.convertToSQLDate(visit.getDate()));
		params.add(Converters.convertToSQLDate(visit.getNextVisitdate()));
		params.add(visit.getTreatmenttype());
		params.add(visit.getNote());
		params.add(visit.getType());
		return params;
	}
}
