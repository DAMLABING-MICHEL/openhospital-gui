package org.isf.opd.service;

import org.isf.admission.model.Admission;
import org.isf.generaldata.MessageBundle;
import org.isf.opd.model.Opd;
import org.isf.opd.model.OpdRow;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.Converters;
import org.isf.visits.model.Visit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/*----------------------------------------------------
 * (org.isf.opd.service)IoOperations - services for opd class
 * ---------------------------------------------------
 * modification history
 * 11/12/2005 - Vero, Rick  - first beta version 
 * 03/01/2008 - ross - selection for opd browser is performed on OPD_DATE_VIS instead of OPD_DATE
 *                   - selection now is less than or equal, before was only less than
 * 21/06/2008 - ross - for multilanguage version, the test for "all type" and "all disease"
 *                     must be done on the translated resource, not in english
 *                   - fix:  getSurgery() method should not add 1 day to toDate
 * 05/09/2008 - alex - added method for patient related OPD query
 * 05/01/2009 - ross - fix: in insert, referralfrom was written both in referralfrom and referralto
 * 09/01/2009 - fabrizio - Modified queried to accomodate type change of date field in Opd class.
 *                         Modified construction of queries, concatenation is performed with
 *                         StringBuilders instead than operator +. Removed some nested try-catch
 *                         blocks. Modified methods to format dates.                          
 *------------------------------------------*/

public class IoOperations {

	private org.isf.visits.service.IoOperations visitService = new org.isf.visits.service.IoOperations();

	/**
	 * return all OPDs of today or one week ago
	 *
	 * @param oneWeek - if <code>true</code> return the last week, only today otherwise.
	 * @return the list of OPDs. It could be <code>empty</code>.
	 * @throws OHException
	 */
	public ArrayList<Opd> getOpdList(boolean oneWeek, String chronicFilter) throws OHException{
		GregorianCalendar dateFrom=new GregorianCalendar();
		GregorianCalendar dateTo=new GregorianCalendar();
		if (oneWeek) dateFrom.add(GregorianCalendar.WEEK_OF_YEAR,-1);
		return getOpdList(MessageBundle.getMessage("angal.opd.alltype"),
				MessageBundle.getMessage("angal.opd.alldisease"),
				chronicFilter,
				dateFrom,
				dateTo,0,0,'A',"A");
	}

	public ArrayList<OpdRow> getOpdRows(boolean oneWeek) throws OHException{
		GregorianCalendar dateFrom=new GregorianCalendar();
		GregorianCalendar dateTo=new GregorianCalendar();
		if (oneWeek) dateFrom.add(GregorianCalendar.WEEK_OF_YEAR,-1);
		return getOpdRows(MessageBundle.getMessage("angal.opd.alltype"),
				MessageBundle.getMessage("angal.opd.alldisease"),
				MessageBundle.getMessage("angal.medicalstockward.selectaward"),
				"all",
				null, dateFrom,
				dateTo,0,0,'A',"A");
	}

	/**
	 * Return a filtered list of {@link Opd} objects without filtering for name hint and chronic state.
	 */
	public ArrayList<Opd> getOpdList(String diseaseTypeCode, String diseaseCode, String chronicFilter,
									 GregorianCalendar dateFrom, GregorianCalendar dateTo, int ageFrom,
									 int ageTo, char sex, String newPatient) throws OHException {
		return getOpdList(diseaseTypeCode, diseaseCode, "", "", chronicFilter, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient);
	}

	public ArrayList<OpdRow> getOpdRows(String diseaseTypeCode, String diseaseCode,String wardCode, String chronicFilter,
									 GregorianCalendar dateFrom, GregorianCalendar dateTo, int ageFrom,
									 int ageTo, char sex, String newPatient) throws OHException {
		return getOpdRows(diseaseTypeCode, diseaseCode,wardCode, "", chronicFilter, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient);
	}

	/**
	 * Returns a filtered list of {@link Opd} objects.
	 * @param diseaseTypeCode String, the {@link org.isf.distype.model.DiseaseType} code.
	 * @param diseaseCode String, the {@link org.isf.disease.model.Disease} code.
	 * @param patientNameHint String, the beginning of a patient name.
	 * @param chronicFilter String, chronic filter option.
	 * @param dateFrom GregorialCalendar, defines the starting of {@link Opd} date range.
	 * @param dateTo GregorialCalendar, defines the end of {@link Opd} date range.
	 * @param ageFrom int, min {@link org.isf.patient.model.Patient} age.
	 * @param ageTo int, max {@link org.isf.patient.model.Patient} age.
	 * @param sex char, patient gender, can be one of M (male), F (female), A (any).
	 * @param newPatient String, tells whether the {@link Opd} refers to a new {@link org.isf.patient.model.Patient}
	 *                   or not, can be one of A (any), N (new) or R.
	 * @return ArrayList of {@link Opd} objects.
	 * @throws OHException
	 */
	public ArrayList<Opd> getOpdList(String diseaseTypeCode, String diseaseCode, String patientNameHint, String opdCodeHint,
										String chronicFilter, GregorianCalendar dateFrom, GregorianCalendar dateTo,
										int ageFrom, int ageTo, char sex, String newPatient) throws OHException{
		ArrayList<Opd> opdList = null;
		List<Object> parameters = new ArrayList<Object>();

		String query = "SELECT * FROM OPD LEFT JOIN DISEASE ON OPD_DIS_ID_A = DIS_ID_A" +
				" LEFT JOIN DISEASETYPE ON DIS_DCL_ID_A = DCL_ID_A";
		if (opdCodeHint != null && !opdCodeHint.isEmpty()) {
			query += " JOIN PATIENT ON PAT_ID = OPD_PAT_ID WHERE PAT_PCODE LIKE ?";
			parameters.add(opdCodeHint + "%");
		} else {
			query += " WHERE 1";
		}
		if (!(diseaseTypeCode.equals(MessageBundle.getMessage("angal.opd.alltype")))) {
			query += " AND DIS_DCL_ID_A = ?";
			parameters.add(diseaseTypeCode);
		}
		if(!diseaseCode.equals(MessageBundle.getMessage("angal.opd.alldisease"))) {
			query += " AND DIS_ID_A = ?";
			parameters.add(diseaseCode);
		}
		if (patientNameHint != null && !patientNameHint.isEmpty()) {
			query += " AND OPD_PAT_FULLNAME LIKE ?";
			parameters.add(patientNameHint + "%");
		}
		if (chronicFilter.equals("chronic"))
			query += " AND OPD_IS_CHRONIC = 1";
		else if (chronicFilter.equals("non-chronic"))
			query += " AND OPD_IS_CHRONIC = 0";

		if (ageFrom != 0 || ageTo != 0) {
			query += " AND OPD_AGE BETWEEN ? AND ?";
			parameters.add(ageFrom);
			parameters.add(ageTo);
		}
		if (sex != 'A') {
			query += " AND OPD_SEX = ?";
			parameters.add(String.valueOf(sex));
		}
		if (!newPatient.equals("A")) {
			query += " AND OPD_NEW_PAT = ?";
			parameters.add(newPatient);
		}
		String stringDateFrom = Converters.convertToSQLDateLimited(dateFrom);
		String stringDateTo = Converters.convertToSQLDateLimited(dateTo);
		query += " AND OPD_DATE_VIS BETWEEN ? AND ?";
		parameters.add(stringDateFrom);
		parameters.add(stringDateTo);
		query += " ORDER BY OPD_DATE_VIS DESC";

		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			opdList = new ArrayList<Opd>(resultSet.getFetchSize());
			while (resultSet.next()) {
				opdList.add(parseOpdRecord(resultSet,true,false));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return opdList;
	}

	public ArrayList<OpdRow> getOpdRows(String diseaseTypeCode, String diseaseCode,String wardCode, String opdCodeHint, 
										String chronicFilter, GregorianCalendar dateFrom, GregorianCalendar dateTo,
										int ageFrom, int ageTo, char sex, String newPatient) throws OHException{
		ArrayList<OpdRow> opdList = null;
		List<Object> parameters = new ArrayList<Object>();

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
				" LEFT JOIN PATIENT ON OPD_PAT_ID = PAT_ID" +
				" LEFT JOIN DISEASE ON OPD_DIS_ID_A = DIS_ID_A" +
//				" LEFT JOIN WARD ON OPD_WRD_ID_A = WRD_ID_A" +
				" LEFT JOIN DISEASETYPE ON DIS_DCL_ID_A = DCL_ID_A WHERE 1";
		if (opdCodeHint != null && !opdCodeHint.isEmpty()) {
			query += " AND PAT_PCODE LIKE ?";
			parameters.add(opdCodeHint + "%");
		} else {
			if (!(diseaseTypeCode.equals(MessageBundle.getMessage("angal.opd.alltype")))) {
				query += " AND DIS_DCL_ID_A = ?";
				parameters.add(diseaseTypeCode);
			}
			if (!diseaseCode.equals(MessageBundle.getMessage("angal.opd.alldisease"))) {
				query += " AND DIS_ID_A = ?";
				parameters.add(diseaseCode);
			}
//			if (!wardCode.equals(MessageBundle.getMessage("angal.medicalstockward.selectaward"))) {
//				query += " AND WRD_ID_A = ?";
//				parameters.add(wardCode);
//			}
			if (chronicFilter.equals("chronic"))
				query += " AND OPD_IS_CHRONIC = 1";
			else if (chronicFilter.equals("non-chronic"))
				query += " AND OPD_IS_CHRONIC = 0";

			if (ageFrom != 0 || ageTo != 0) {
				query += " AND OPD_AGE BETWEEN ? AND ?";
				parameters.add(ageFrom);
				parameters.add(ageTo);
			}
			if (sex != 'A') {
				query += " AND OPD_SEX = ?";
				parameters.add(String.valueOf(sex));
			}
			if (!newPatient.equals("A")) {
				query += " AND OPD_NEW_PAT = ?";
				parameters.add(newPatient);
			}
			String stringDateFrom = Converters.convertToSQLDateLimited(dateFrom);
			String stringDateTo = Converters.convertToSQLDateLimited(dateTo);
			query += " AND OPD_DATE_VIS BETWEEN ? AND ?";
			parameters.add(stringDateFrom);
			parameters.add(stringDateTo);
		}
		query += " ORDER BY OPD_DATE_VIS ASC;";

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			opdList = new ArrayList<OpdRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				opdList.add(new OpdRow(parseOpdRecord(resultSet), resultSet.getString("PAT_PCODE")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return opdList;
	}

	/**
	 * returns all {@link Opd}s associated to specified patient ID
	 *
	 * @param patID - the patient ID
	 * @return the list of {@link Opd}s associated to specified patient ID.
	 * 		   the whole list of {@link Opd}s if <code>0</code> is passed.
	 * @throws OHException
	 */
	public ArrayList<Opd> getOpdList(int patID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Opd> opdList = null;
		ResultSet resultSet;

		if (patID == 0) {
			String sqlString = "SELECT * FROM OPD ORDER BY OPD_DATE_VIS DESC, OPD_DATE DESC";
			resultSet = dbQuery.getData(sqlString, true);
		} else {
			String sqlString = "SELECT * FROM OPD WHERE OPD_PAT_ID = ? ORDER BY OPD_DATE_VIS DESC, OPD_DATE DESC";
			List<Object> parameters = Collections.<Object>singletonList(patID);
			resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
		}
		try {
			opdList = new ArrayList<Opd>(resultSet.getFetchSize());
			System.out.println("view opd list" +opdList);
			while (resultSet.next()) {
				opdList.add(parseOpdRecord(resultSet, false, false));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}

		return opdList;
	}

	/**
	 * Insert a new item in the db committing the query.
	 * @param opd {@link Opd}
	 * @return <code>true</code> if the item has been inserted
	 * @throws OHException
	 */
	public int newOpd(Opd opd) throws OHException{
		return saveOpd(opd, true, null);
	}

	/**
	 * Insert a new {@link Opd} in the database, supporting jdbc transactions.
	 * @param opd {@link Opd} instance to be saved.
	 * @param commit <code>boolean</code>, if true the query is committed, set to false to manage transactions manually.
	 * @param existingLogger pass an existing dbQueryLogger if one already in use.
	 * @return <code>int</code> the opd code (auto increment value in the database)
	 * @throws OHException if something goes wrong.
	 */
	public int saveOpd(Opd opd, boolean commit, DbQueryLogger existingLogger) throws OHException {
		int opdCode = 0;
		DbQueryLogger dbQueryLogger = existingLogger != null ? existingLogger : new DbQueryLogger();

		String query = "INSERT INTO OPD (" +
				" OPD_US_ID_A," +
				" OPD_DATE," +
				" OPD_PROG_YEAR," +
				" OPD_SEX, OPD_AGE," +
				" OPD_DIS_ID_A, OPD_DIS_ID_A_2, OPD_DIS_ID_A_3," +
				" OPD_NEW_PAT," +
				" OPD_DATE_VIS," +
				" OPD_REFERRAL_FROM," +
				" OPD_REFERRAL_TO," +
				" OPD_NOTE," +
				" OPD_PAT_ID," +
				" OPD_PAT_FULLNAME," +
				" OPD_PAT_FNAME," +
				" OPD_PAT_SNAME," +
				" OPD_PAT_NEXT_KIN," +
				" OPD_PAT_ADDR," +
				" OPD_PAT_CITY," +
				" OPD_IS_CHRONIC," +
				" OPD_SCHEDULED_VISIT_ID," +
				" OPD_NEXT_VISIT_ID)" +
//				" OPD_WRD_ID_A)" +
				
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

		Date visitDate = (opd.getVisitDate() == null ? null : new Date(opd.getVisitDate().getTimeInMillis()));
		GregorianCalendar now = new GregorianCalendar();
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(String.valueOf(opd.getUser()));
		parameters.add(Converters.convertToSQLDate(now));
		parameters.add(opd.getYear());
		parameters.add(String.valueOf(opd.getSex()));
		parameters.add(opd.getAge());
		parameters.add(opd.getDisease());
		parameters.add(opd.getDisease2());
		parameters.add(opd.getDisease3());
		parameters.add(opd.getNewPatient());
		parameters.add(Converters.convertToSQLDateLimited(visitDate));
		parameters.add(opd.getReferralFrom());
		parameters.add(opd.getReferralTo());
		parameters.add(sanitize(opd.getNote()));
		parameters.add(opd.getpatientCode());
		parameters.add(opd.getFullName());
		parameters.add(opd.getfirstName());
		parameters.add(opd.getsecondName());
		parameters.add(opd.getnextKin());
		parameters.add(opd.getaddress());
		parameters.add(opd.getcity());
		parameters.add(opd.getIsChronic());
		parameters.add(opd.getScheduledVisit() != null ? opd.getScheduledVisit().getVisitID() : null);
		parameters.add(opd.getNextVisit() != null ? opd.getNextVisit().getVisitID() : null);
//		parameters.add(opd.getWard());

		ResultSet r = dbQueryLogger.setDataReturnGeneratedKeyWithParams(query, parameters, commit);
		try {
			if(r.first()){
				opdCode = r.getInt(1);
//				opd.setCode(opdCode);
				opd.setDate(now.getTime());
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			if (commit)
				dbQueryLogger.releaseConnection();
		}
		return opdCode;
	}

	/**
	 * Checks if the specified {@link Opd} has been modified.
	 * @param opd - the {@link Opd} to check.
	 * @return <code>true</code> if has been modified, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the check.
	 */
	public boolean hasOpdModified(Opd opd) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;

		// we establish if someone else has updated/deleted the record since the last read
		String query = "SELECT OPD_LOCK FROM OPD WHERE OPD_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(opd.getCode());

		try {
			// we use manual commit of the transaction
			ResultSet resultSet =  dbQuery.getDataWithParams(query, parameters, true);
			if (resultSet.first()) {
				// ok the record is present, it was not deleted
				result = resultSet.getInt("OPD_LOCK") != opd.getLock();
			} else {
				throw new OHException(MessageBundle.getMessage("angal.sql.couldntfindthedataithasprobablybeendeleted"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Modify an {@link Opd} in the db.
	 * @param opd {@link Opd}
	 * @return <code>true</code> if the item has been updated. <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean updateOpd(Opd opd) throws OHException {
		return updateOpd(opd, true, null);
	}

	public boolean updateOpd(Opd opd, boolean commit, DbQueryLogger existingLogger) throws OHException {
		DbQueryLogger dbQueryLogger = existingLogger != null ? existingLogger : new DbQueryLogger();
		boolean result = false;
		Date visitDate = (opd.getVisitDate() == null ? null : new Date(opd.getVisitDate().getTimeInMillis()));
		try {
			String query = "UPDATE OPD SET" +
					" OPD_US_ID_A = ?," +
					" OPD_SEX = ?," +
//					" OPD_WRD_ID_A = ?," +
					" OPD_AGE = ?," +
					" OPD_DIS_ID_A = ?," +
					" OPD_DIS_ID_A_2 = ?," +
					" OPD_DIS_ID_A_3 = ?," +
					" OPD_NEW_PAT = ?," +
					" OPD_DATE_VIS = ?," +
					" OPD_REFERRAL_FROM = ?," +
					" OPD_REFERRAL_TO = ?," +
					" OPD_NOTE = ?," +
					" OPD_PAT_ID = ?," +
					" OPD_PAT_FULLNAME = ?," +
					" OPD_PAT_FNAME = ?," +
					" OPD_PAT_SNAME = ?," +
					" OPD_PAT_NEXT_KIN = ?," +
					" OPD_PAT_ADDR = ?," +
					" OPD_PAT_CITY = ?," +
					" OPD_IS_CHRONIC = ?," +
					" OPD_SCHEDULED_VISIT_ID = ?," +
					" OPD_NEXT_VISIT_ID = ?," +
					" OPD_LOCK = OPD_LOCK + 1" +
					" WHERE OPD_ID = ?";

			List<Object> parameters = new ArrayList<Object>();
			parameters.add(String.valueOf(opd.getUser()));
			parameters.add(String.valueOf(opd.getSex()));
//			parameters.add(opd.getWard());
			parameters.add(opd.getAge());
			parameters.add(opd.getDisease());
			parameters.add(opd.getDisease2());
			parameters.add(opd.getDisease3());
			parameters.add(opd.getNewPatient());
			parameters.add(Converters.convertToSQLDateLimited(visitDate));
			parameters.add(opd.getReferralFrom());
			parameters.add(opd.getReferralTo());
			parameters.add(sanitize(opd.getNote()));
			parameters.add(opd.getpatientCode());
			parameters.add(opd.getFullName());
			parameters.add(opd.getfirstName());
			parameters.add(opd.getsecondName());
			parameters.add(opd.getnextKin());
			parameters.add(opd.getaddress());
			parameters.add(opd.getcity());
			parameters.add(opd.getIsChronic());
			parameters.add(opd.getScheduledVisit() != null ? opd.getScheduledVisit().getVisitID() : null);
			parameters.add(opd.getNextVisit() != null ? opd.getNextVisit().getVisitID() : null);
			parameters.add(opd.getCode());

			result = dbQueryLogger.setDataWithParams(query, parameters, commit);
			if (result) opd.setLock(opd.getLock() + 1);
		} finally {
			if (commit && existingLogger == null)
				dbQueryLogger.releaseConnection();
		}
		return result;
	}

	/**
	 * delete an {@link Opd} from the db
	 *
	 * @param opd - the {@link Opd} to delete
	 * @return <code>true</code> if the item has been deleted. <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteOpd(Opd opd) throws OHException {
		//TODO: evaluate the possibility of adding some checks before deleting an Opd
		//TODO: eventually, the deleting could set a DELETED status in the db instead of erasing the record
		boolean result = true;
		String sqlString = "DELETE FROM OPD WHERE OPD_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(opd.getCode());
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			
			result &= dbQuery.setDataWithParams(sqlString, parameters, false);
			
			if (opd.getNextVisit() != null)
				result &= visitService.deleteVisit(opd.getNextVisit().getVisitID(), dbQuery);
			if (opd.getScheduledVisit() != null)
				result &= visitService.deleteVisit(opd.getScheduledVisit().getVisitID(), dbQuery);
			
			if (result) dbQuery.commit();
			
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Returns the max progressive number within specified year or within current year if <code>0</code>.
	 *
	 * @param year
	 * @return <code>int</code> - the progressive number in the year
	 * @throws OHException
	 */
	public int getProgYear(int year) throws OHException {
		int progYear=0;
		DbQueryLogger dbQuery = new DbQueryLogger();
		ResultSet resultSet;
		StringBuilder sqlString = new StringBuilder("SELECT MAX(OPD_PROG_YEAR) FROM OPD");

		if (year == 0) {
			resultSet = dbQuery.getData(sqlString.toString(), true);
		} else {
			sqlString.append(" WHERE YEAR(OPD_DATE) = ?");
			List<Object> parameters = Collections.<Object>singletonList(year);
			resultSet = dbQuery.getDataWithParams(sqlString.toString(), parameters, true);
		}

		try {
			resultSet.next();
			progYear = resultSet.getInt("MAX(OPD_PROG_YEAR)");
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return progYear;
	}

	/**
	 * return the last Opd in time associated with specified patient ID.
	 *
	 * @param patID - the patient ID
	 * @return last Opd associated with specified patient ID or <code>null</code>
	 * @throws OHException
	 */
	public Opd getLastOpd(int patID) throws OHException {
		String sqlString = "SELECT * FROM OPD WHERE OPD_PAT_ID = ? ORDER BY OPD_DATE DESC";
		List<Object> parameters = Collections.<Object>singletonList(patID);
		DbQueryLogger dbQuery = new DbQueryLogger();
		Opd opd = null;
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
			if (resultSet.next()) {
				opd = parseOpdRecord(resultSet, false, false);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return opd;
	}
	
	/**
	 * return the next Opd in time associated with specified patient ID.
	 *
	 * @param opd
	 * @return next Opd associated with specified patient ID or <code>null</code>
	 * @throws OHException
	 */
	public Opd getNextOpd(Opd opd) throws OHException {
		String sqlString = "SELECT * FROM OPD WHERE OPD_PAT_ID = ? AND OPD_ID > ? LIMIT 1";
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(opd.getpatientCode());
		parameters.add(opd.getCode());
		DbQueryLogger dbQuery = new DbQueryLogger();
		Opd nextOpd = null;
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
			if (resultSet.next()) {
				nextOpd = parseOpdRecord(resultSet, false, false);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return nextOpd;
	}
	
	/**
	 * return the next Opd in time following the specified Admission.
	 *
	 * @param adm
	 * @return next Opd associated with specified patient ID or <code>null</code>
	 * @throws OHException
	 */
	public Opd getNextOpd(Admission adm) throws OHException {
		String sqlString = "SELECT * FROM OPD WHERE OPD_PAT_ID = ? AND OPD_DATE_VIS >= DATE(?) LIMIT 1";
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(adm.getPatId());
		parameters.add(new Timestamp(adm.getDisDate().getTime().getTime()));
		DbQueryLogger dbQuery = new DbQueryLogger();
		Opd nextOpd = null;
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
			if (resultSet.next()) {
				nextOpd = parseOpdRecord(resultSet, false, false);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return nextOpd;
	}
	
	/**
	 * Sanitize the given {@link String} value.
	 * This method is maintained only for backward compatibility.
	 * @param value the value to sanitize.
	 * @return the sanitized value or <code>null</code> if the passed value is <code>null</code>.
	 */
	protected String sanitize(String value)
	{
		if (value == null) return null;
		return value.trim().replaceAll("'", "''");
	}

	public Opd parseOpdRecord(ResultSet resultSet) throws SQLException, OHException {
		return parseOpdRecord(resultSet, true, true);
	}

	public Opd parseOpdRecord(ResultSet resultSet, boolean includeDiseaseInfo, boolean includeVisits) throws SQLException, OHException {
		Opd opd = new Opd(resultSet.getInt("OPD_PROG_YEAR"),
				resultSet.getString("OPD_SEX").charAt(0),
				resultSet.getInt("OPD_AGE"),
				resultSet.getString("OPD_DIS_ID_A"),
				resultSet.getInt("OPD_LOCK"),
				null
//				resultSet.getString("OPD_WRD_ID_A")
		);
		opd.setUser(resultSet.getString("OPD_US_ID_A"));
		opd.setCode(resultSet.getInt("OPD_ID"));
		Date date = new Date();
		date.setTime(resultSet.getTimestamp("OPD_DATE").getTime());
		opd.setDate(date);
		GregorianCalendar visitDate = new GregorianCalendar();
		visitDate.setTime(resultSet.getDate("OPD_DATE_VIS"));
		opd.setVisitDate(visitDate);
		opd.setDisease2(resultSet.getString("OPD_DIS_ID_A_2"));
		opd.setDisease3(resultSet.getString("OPD_DIS_ID_A_3"));
		if (includeDiseaseInfo) {
			opd.setDiseaseType(resultSet.getString("DIS_DCL_ID_A"));
			opd.setDiseaseDesc(resultSet.getString("DIS_DESC"));
			opd.setDiseaseTypeDesc(resultSet.getString("DCL_DESC"));
		}
		opd.setNewPatient(resultSet.getString("OPD_NEW_PAT"));
		opd.setReferralFrom(resultSet.getString("OPD_REFERRAL_FROM"));
		opd.setReferralTo(resultSet.getString("OPD_REFERRAL_TO"));
		opd.setPatientCode(resultSet.getInt("OPD_PAT_ID"));
		opd.setFullName(resultSet.getString("OPD_PAT_FULLNAME"));
		opd.setNote(resultSet.getString("OPD_NOTE"));
		opd.setfirstName(resultSet.getString("OPD_PAT_FNAME"));
		opd.setsecondName(resultSet.getString("OPD_PAT_SNAME"));
		opd.setnextKin(resultSet.getString("OPD_PAT_NEXT_KIN"));
		opd.setaddress(resultSet.getString("OPD_PAT_ADDR"));
		opd.setcity(resultSet.getString("OPD_PAT_CITY"));
		opd.setIsChronic(resultSet.getBoolean("OPD_IS_CHRONIC"));
		if (includeVisits) {
			if (resultSet.getInt("SCH_VST_ID") > 0) {
				Visit scheduledVisit = new Visit();
				scheduledVisit.setVisitID(resultSet.getInt("SCH_VST_ID"));
				scheduledVisit.setPatID(resultSet.getInt("SCH_VST_PAT_ID"));
				scheduledVisit.setDate(resultSet.getDate("SCH_VST_DATE"));
				scheduledVisit.setNote(resultSet.getString("SCH_VST_NOTE"));
				opd.setScheduledVisit(scheduledVisit);
			}
			if (resultSet.getInt("NXT_VST_ID") > 0) {
				Visit nextVisit = new Visit();
				nextVisit.setVisitID(resultSet.getInt("NXT_VST_ID"));
				nextVisit.setPatID(resultSet.getInt("NXT_VST_PAT_ID"));
				nextVisit.setDate(resultSet.getDate("NXT_VST_DATE"));
				nextVisit.setNote(resultSet.getString("NXT_VST_NOTE"));
				opd.setNextVisit(nextVisit);
			}
		}
		return opd;
	}

	public String getPatPCode(int patientId) throws OHException {
		String query = "SELECT PAT_PCODE FROM PATIENT WHERE PAT_ID = ?";
		ArrayList<Object> params = new ArrayList<Object>(1);
		params.add(patientId);
		DbQueryLogger dbQuery = new DbQueryLogger();
		String patPCode = "";
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			if (resultSet.next())
				patPCode = resultSet.getString("PAT_PCODE");
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return patPCode;
	}
}

