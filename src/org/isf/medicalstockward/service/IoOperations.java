package org.isf.medicalstockward.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.accounting.model.Bill;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MedicalWardPrescription;
import org.isf.medicalstockward.model.MedicalWardPrescriptionDetail;
import org.isf.medicalstockward.model.MissingMedical;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medtype.model.MedicalType;
import org.isf.patient.model.Patient;
import org.isf.prescriber.model.Prescriber;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

/**
 * @author mwithi
 */
public class IoOperations {

	/*public ArrayList<MovementWard> getMovementWard() {
		return getMovementWard(null);
	}

	public ArrayList<MovementWard> getMovementWard(String ward) {
		ArrayList<MovementWard> mov = null;
		StringBuffer stringBfr = new StringBuffer("SELECT * FROM ((((MEDICALDSRSTOCKMOVWARD LEFT JOIN ");
		stringBfr.append("(PATIENT LEFT JOIN (SELECT MLN_PAT_ID, MLN_HEIGHT AS PAT_HEIGHT, MLN_WEIGHT AS PAT_WEIGHT FROM MALNUTRITIONCONTROL GROUP BY MLN_PAT_ID ORDER BY MLN_DATE_SUPP DESC) AS HW ON PAT_ID = HW.MLN_PAT_ID) ON MMVN_PAT_ID = PAT_ID) JOIN ");
		stringBfr.append("WARD ON MMVN_WRD_ID_A = WRD_ID_A)) JOIN ");
		stringBfr.append("MEDICALDSR ON MMVN_MDSR_ID = MDSR_ID) JOIN ");
		stringBfr.append("MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");
		if (ward != null && !ward.equals("")) {
			stringBfr.append("WHERE WRD_ID_A = '").append(ward).append("'");
		}
		dbQuery = new DbQuery();
		try {
			try {
				ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);

				mov = new ArrayList<MovementWard>(resultSet.getFetchSize());
				Patient patient;
				MovementWard movWard;
				while (resultSet.next()) {
					patient = new Patient();
					if (resultSet.getBoolean("MMVN_IS_PATIENT")) {
						patient.setCode(resultSet.getInt("PAT_ID"));
						patient.setFirstName(resultSet.getString("PAT_FNAME"));
						patient.setSecondName(resultSet.getString("PAT_SNAME"));
						patient.setAddress(resultSet.getString("PAT_ADDR"));
						patient.setBirthDate(resultSet.getDate("PAT_BDATE"));
						patient.setAge(resultSet.getInt("PAT_AGE"));
						patient.setAgetype(resultSet.getString("PAT_AGETYPE"));
						patient.setSex(resultSet.getString("PAT_SEX").charAt(0));
						patient.setCity(resultSet.getString("PAT_CITY"));
						patient.setTelephone(resultSet.getString("PAT_TELE"));
						patient.setNextKin(resultSet.getString("PAT_NEXT_KIN"));
						patient.setBloodType(resultSet.getString("PAT_BTYPE"));
						patient.setFather(resultSet.getString("PAT_FATH").charAt(0));
						patient.setFather_name(resultSet.getString("PAT_FATH_NAME"));
						patient.setMother(resultSet.getString("PAT_MOTH").charAt(0));
						patient.setMother_name(resultSet.getString("PAT_MOTH_NAME"));
						patient.setHasInsurance(resultSet.getString("PAT_ESTA").charAt(0));
						patient.setParentTogether(resultSet.getString("PAT_PTOGE").charAt(0));
						patient.setNote(resultSet.getString("PAT_NOTE"));
						patient.setHeight(resultSet.getFloat("PAT_HEIGHT"));
						patient.setWeight(resultSet.getFloat("PAT_WEIGHT"));
						patient.setLock(resultSet.getInt("PAT_LOCK"));
					}
					movWard = new MovementWard( new Ward(resultSet.getString("WRD_ID_A"), 
							resultSet.getString("WRD_NAME"),
							resultSet.getString("WRD_TELE"),
							resultSet.getString("WRD_FAX"),
							resultSet.getString("WRD_EMAIL"),
							resultSet.getInt("WRD_NBEDS"),
							resultSet.getInt("WRD_NQUA_NURS"),
							resultSet.getInt("WRD_NDOC"),
							resultSet.getInt("WRD_LOCK")),
							convertToGregorianCalendar(resultSet.getTimestamp("MMVN_DATE")),
							resultSet.getBoolean("MMVN_IS_PATIENT"),
							patient,
							resultSet.getInt("MMVN_PAT_AGE"),
							resultSet.getFloat("MMVN_PAT_WEIGHT"),
							resultSet.getString("MMVN_DESC"),
							new Medical(resultSet.getInt("MDSR_ID"),
									new MedicalType(resultSet.getString("MDSRT_ID_A"),
											resultSet.getString("MDSRT_DESC")),
											resultSet.getString("MDSR_CODE"),
											resultSet.getString("MDSR_DESC"),
											resultSet.getDouble("MDSR_INI_STOCK_QTI"),
											resultSet.getInt("MDSR_PCS_X_PCK"),
											resultSet.getDouble("MDSR_MIN_STOCK_QTI"),
											resultSet.getDouble("MDSR_IN_QTI"),
											resultSet.getDouble("MDSR_OUT_QTI"),
											resultSet.getInt("MDSR_LOCK")),
											resultSet.getDouble("MMVN_MDSR_QTY"),
											resultSet.getString("MMVN_MDSR_UNITS"));
					movWard.setCode(resultSet.getInt("MMVN_ID"));
					mov.add(movWard);

				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				dbQuery.releaseConnection();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e);
		}
		return mov;
	}*/

	/**
	 * Get all {@link MovementWard}s with the specified criteria.
	 * @param wardId the ward id.
	 * @param dateFrom the lower bound for the movement date range.
	 * @param dateTo the upper bound for the movement date range.
	 * @return the retrieved movements.
	 * @throws OHException if an error occurs retrieving the movements.
	 */
	public ArrayList<MovementWard> getWardMovements(String wardId, GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {

		ArrayList<MovementWard> movements = null;

		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();

		query.append("SELECT * FROM ((((MEDICALDSRSTOCKMOVWARD LEFT JOIN ");
		query.append("(PATIENT LEFT JOIN (SELECT MLN_PAT_ID, MLN_HEIGHT AS PAT_HEIGHT, MLN_WEIGHT AS PAT_WEIGHT FROM MALNUTRITIONCONTROL GROUP BY MLN_PAT_ID ORDER BY MLN_DATE_SUPP DESC) AS HW ON PAT_ID = HW.MLN_PAT_ID) ON MMVN_PAT_ID = PAT_ID) JOIN ");
		query.append("WARD ON MMVN_WRD_ID_A = WRD_ID_A)) JOIN ");
		query.append("MEDICALDSR ON MMVN_MDSR_ID = MDSR_ID) JOIN ");
		query.append("MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");

		if (wardId!=null || dateFrom!=null || dateTo!=null) query.append("WHERE ");

		if (wardId != null && !wardId.equals("")) {
			if (parameters.size()!=0) query.append("AND ");
			parameters.add(wardId);
			query.append("WRD_ID_A = ? ");
		}

		if ((dateFrom != null) && (dateTo != null)) {
			if (parameters.size()!=0) query.append("AND ");
			query.append("MMVN_DATE > ? AND MMVN_DATE < ? ");
			parameters.add(toTimestamp(dateFrom));
			parameters.add(toTimestamp(dateTo));
		}

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			movements = new ArrayList<MovementWard>(resultSet.getFetchSize());

			while (resultSet.next()) {
				MovementWard movementWard = toMovementWard(resultSet);
				movements.add(movementWard);
			}


		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return movements;
	}

	/**
	 * Extracts a {@link MovementWard} from the current {@link ResultSet} row.
	 * @param resultSet the resultset.
	 * @return the extracted movement ward.
	 * @throws SQLException if an error occurs during the extraction.
	 */
	protected MovementWard toMovementWard(ResultSet resultSet) throws SQLException {
		Patient patient;
		if (resultSet.getBoolean("MMVN_IS_PATIENT")) {
			patient = toPatient(resultSet);
		} else patient = new Patient();

		Ward ward = toWard(resultSet);

		Medical medical = toMedical(resultSet);
		MovementWard movementWard = new MovementWard(ward,
				toCalendar(resultSet.getTimestamp("MMVN_DATE")),
				resultSet.getBoolean("MMVN_IS_PATIENT"),
				patient,
				resultSet.getInt("MMVN_PAT_AGE"),
				resultSet.getFloat("MMVN_PAT_WEIGHT"),
				resultSet.getString("MMVN_DESC"),
				medical,
				resultSet.getDouble("MMVN_MDSR_QTY"),
				resultSet.getString("MMVN_MDSR_UNITS"));

		movementWard.setCode(resultSet.getInt("MMVN_ID"));
		return movementWard;
	}

	/**
	 * Extracts a {@link Patient} from the current {@link ResultSet} row.
	 * @param resultSet the resultset.
	 * @return the extracted patient.
	 * @throws SQLException if an error occurs during the extraction.
	 */
	protected Patient toPatient(ResultSet resultSet) throws SQLException
	{
		Patient patient = new Patient();
		patient.setCode(resultSet.getInt("PAT_ID"));
		patient.setFirstName(resultSet.getString("PAT_FNAME"));
		patient.setSecondName(resultSet.getString("PAT_SNAME"));
		patient.setAddress(resultSet.getString("PAT_ADDR"));
		patient.setBirthDate(resultSet.getDate("PAT_BDATE"));
		patient.setAge(resultSet.getInt("PAT_AGE"));
		patient.setAgetype(resultSet.getString("PAT_AGETYPE"));
		patient.setSex(resultSet.getString("PAT_SEX").charAt(0));
		patient.setCity(resultSet.getString("PAT_CITY"));
		patient.setTelephone(resultSet.getString("PAT_TELE"));
		patient.setNextKin(resultSet.getString("PAT_NEXT_KIN"));
		patient.setBloodType(resultSet.getString("PAT_BTYPE"));
		patient.setFather(resultSet.getString("PAT_FATH").charAt(0));
		patient.setFather_name(resultSet.getString("PAT_FATH_NAME"));
		patient.setMother(resultSet.getString("PAT_MOTH").charAt(0));
		patient.setMother_name(resultSet.getString("PAT_MOTH_NAME"));
		patient.setHasInsurance(resultSet.getString("PAT_ESTA").charAt(0));
		patient.setParentTogether(resultSet.getString("PAT_PTOGE").charAt(0));
		patient.setNote(resultSet.getString("PAT_NOTE"));
		patient.setHeight(resultSet.getFloat("PAT_HEIGHT"));
		patient.setWeight(resultSet.getFloat("PAT_WEIGHT"));
		patient.setLock(resultSet.getInt("PAT_LOCK"));
		return patient;
	}

	/**
	 * Extracts a {@link Ward} from the current {@link ResultSet} row.
	 * @param resultSet the result set.
	 * @return the extracted ward.
	 * @throws SQLException if an error occurs during the extraction.
	 */
	protected Ward toWard(ResultSet resultSet) throws SQLException
	{
		Ward ward = new Ward(resultSet.getString("WRD_ID_A"),
				resultSet.getString("WRD_NAME"),
				resultSet.getString("WRD_TELE"),
				resultSet.getString("WRD_FAX"),
				resultSet.getString("WRD_EMAIL"),
				resultSet.getInt("WRD_NBEDS"),
				resultSet.getInt("WRD_NQUA_NURS"),
				resultSet.getInt("WRD_NDOC"),
				resultSet.getInt("WRD_MIN_AGE"),
				resultSet.getInt("WRD_MAX_AGE"),
				resultSet.getInt("WRD_LOCK"));
		return ward;
	}

	/**
	 * Extracts a {@link Medical} from the current {@link ResultSet} row.
	 * @param resultSet the result set.
	 * @return the extracted medical.
	 * @throws SQLException if an error occurs during the extraction.
	 */
	protected Medical toMedical(ResultSet resultSet) throws SQLException
	{
		MedicalType medicalType = toMedicalType(resultSet);
		Medical medical = new Medical(resultSet.getInt("MDSR_ID"),
				medicalType,
				resultSet.getString("MDSR_CODE"),
				resultSet.getString("MDSR_DESC"),
				resultSet.getDouble("MDSR_INI_STOCK_QTI"),
				resultSet.getInt("MDSR_PCS_X_PCK"),
				resultSet.getDouble("MDSR_MIN_STOCK_QTI"),
				resultSet.getDouble("MDSR_IN_QTI"),
				resultSet.getDouble("MDSR_OUT_QTI"),
				resultSet.getInt("MDSR_LOCK"));
		return medical;
	}
	
	/**
	 * Extracts a {@link MissingMedical} from the current {@link ResultSet} row.
	 * @param resultSet the result set.
	 * @return the extracted medical.
	 * @throws SQLException if an error occurs during the extraction.
	 */
	protected MissingMedical toMissingMedical(ResultSet resultSet) throws SQLException {
		Medical medical = toMedical(resultSet);
		Ward ward = toWard(resultSet);
		MissingMedical missing = new MissingMedical();
		missing.setDate(toCalendar(resultSet.getTimestamp("WMS_DATE")));
		missing.setMedical(medical);
		missing.setQty(resultSet.getDouble("WMS_MDSR_QTY"));
		missing.setUnits(resultSet.getString("WMS_MSDR_UNITS"));
		missing.setWard(ward);
		return missing;
	}

	/**
	 * Extract a {@link MedicalType} from the current {@link ResultSet} row.
	 * @param resultSet the result set.
	 * @return the extracted medical type.
	 * @throws SQLException if an error occurs during the extraction.
	 */
	protected MedicalType toMedicalType(ResultSet resultSet) throws SQLException
	{
		MedicalType medicalType = new MedicalType(resultSet.getString("MDSRT_ID_A"),
				resultSet.getString("MDSRT_DESC"));
		return medicalType;
	}
	
	/**
	 * Extract a {@link Prescriber} from the current {@link ResultSet} row.
	 * @param resultSet the result set.
	 * @return the extracted Prescriber.
	 * @throws SQLException if an error occurs during the extraction.
	 */
	protected Prescriber toPrescriber(ResultSet resultSet) throws SQLException
	{
		Prescriber prescriber = new Prescriber(resultSet.getInt("PRS_ID"),
				resultSet.getString("PRS_NAME"));
		return prescriber;
	}

	/**
	 * Gets the current quantity for the specified {@link Medical}.
	 * @param ward if specified medical are filtered by the {@link Ward}.
	 * @param medical the medical to check.
	 * @return the total quantity.
	 * @throws OHException if an error occurs retrieving the quantity.
	 */
	public int getCurrentQuantity(Ward ward, Medical medical) throws OHException {

		List<Object> parameters = new ArrayList<Object>(2);
		StringBuilder query = new StringBuilder();

		query.append("SELECT SUM(MMV_QTY) MAIN FROM MEDICALDSRSTOCKMOV M WHERE MMV_MMVT_ID_A = 'discharge' AND MMV_MDSR_ID = ? ");
		parameters.add(medical.getCode());

		if (ward!=null) {
			parameters.add(ward.getCode());
			query.append(" AND MMV_WRD_ID_A = ?");
		}

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			resultSet.next();
			int mainQuantity = resultSet.getInt("MAIN");

			query = new StringBuilder();
			parameters.clear();

			query.append("SELECT SUM(MMVN_MDSR_QTY) DISCHARGE FROM MEDICALDSRSTOCKMOVWARD WHERE MMVN_MDSR_ID = ?");
			parameters.add(medical.getCode());

			if (ward!=null) {
				parameters.add(ward.getCode());
				query.append(" AND MMV_WRD_ID_A = ?");
			}

			resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			resultSet.next();
			int dischargeQuantity = resultSet.getInt("DISCHARGE");

			resultSet.close();
			return mainQuantity - dischargeQuantity;

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Stores the specified {@link Movement}.
	 * @param movement the movement to store.
	 * @return <code>true</code> if has been stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs.
	 */
	public boolean newMovementWard(MovementWard movement) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			boolean stored = newMovementWard(dbQuery, movement);
			dbQuery.commit();
			return stored;
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Stores the specified {@link Movement} list.
	 * @param movements the movement to store.
	 * @return <code>true</code> if the movements have been stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs.
	 */
	public boolean newMovementWard(ArrayList<MovementWard> movements) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			for (MovementWard movement:movements) {

				boolean inserted = newMovementWard(dbQuery, movement);
				if (!inserted) {
					dbQuery.rollback();
					return false;
				}
			}
			dbQuery.commit();
			return true;
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Stores the specified {@link MovementWard}.
	 * @param dbQuery the {@link DbQueryLogger} to use.
	 * @param movement the movement ward to store.
	 * @return <code>true</code> if has been stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs.
	 */
	protected boolean newMovementWard(DbQueryLogger dbQuery, MovementWard movement) throws OHException {
		String query = "INSERT INTO MEDICALDSRSTOCKMOVWARD (MMVN_WRD_ID_A, MMVN_DATE, MMVN_IS_PATIENT, MMVN_PAT_ID, MMVN_PAT_AGE, MMVN_PAT_WEIGHT, MMVN_DESC, MMVN_MDSR_ID, MMVN_MDSR_QTY, MMVN_MDSR_UNITS) VALUES (?,?,?,?,?,?,?,?,?,?)";
		List<Object> parameters = new ArrayList<Object>(10);

		parameters.add(movement.getWard().getCode());
		parameters.add(toTimestamp(movement.getDate() == null ? new GregorianCalendar() : movement.getDate()));
		parameters.add(movement.isPatient());
		if (movement.isPatient()) {
			parameters.add(movement.getPatient().getCode());
			parameters.add(movement.getPatient().getAge());
			parameters.add(movement.getPatient().getWeight());
		} else {
			parameters.add("0");
			parameters.add(0);
			parameters.add(0);
		}
		parameters.add(movement.getDescription());
		parameters.add(movement.getMedical().getCode().toString());
		parameters.add(movement.getQuantity());
		parameters.add(movement.getUnits());

		boolean inserted = dbQuery.setDataWithParams(query, parameters, false);

		if (inserted) {
			boolean updated = updateStockWardQuantity(dbQuery, movement);
			return updated;
		}
		return false;
	}

	/**
	 * Updates the quantity for the specified movement ward.
	 * N.B. To allow rectifies, if the couple ward-medical is not in the table it performs an INSERT.
	 * @param dbQuery the {@link DbQueryLogger} to use.
	 * @param movement the movement ward to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the update.
	 */
	protected boolean updateStockWardQuantity(DbQueryLogger dbQuery, MovementWard movement) throws OHException {

		List<Object> parameters = new ArrayList<Object>(3);
		String query = "INSERT INTO MEDICALDSRWARD (MDSRWRD_WRD_ID_A, MDSRWRD_MDSR_ID, MDSRWRD_IN_QTI, MDSRWRD_OUT_QTI) " +
			"VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE MDSRWRD_OUT_QTI = MDSRWRD_OUT_QTI + ?";
		parameters.add(movement.getWard().getCode());
		parameters.add(movement.getMedical().getCode());
		parameters.add(0);
		parameters.add(movement.getQuantity());
		parameters.add(movement.getQuantity());

		boolean updated = dbQuery.setDataWithParams(query, parameters, false);

		if (!updated) dbQuery.rollback();

		return updated;
	}

	/**
	 * Updates the specified {@link MovementWard}.
	 * @param movement the movement ward to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the update.
	 */
	public boolean updateMovementWard(MovementWard movement) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			List<Object> parameters = new ArrayList<Object>(11);
			String query = "UPDATE MEDICALDSRSTOCKMOVWARD SET MMVN_WRD_ID_A = ?, " +
					"MMVN_DATE = ?, " +
					"MMVN_IS_PATIENT = ?, " +
					"MMVN_PAT_ID = ?, " +
					"MMVN_PAT_AGE = ?, " +
					"MMVN_PAT_WEIGHT = ?, " +
					"MMVN_DESC = ?, " +
					"MMVN_MDSR_ID = ?, " +
					"MMVN_MDSR_QTY = ?, " +
					"MMVN_MDSR_UNITS = ? " +
					"WHERE MMVN_ID = ?";

			parameters.add(movement.getWard().getCode());
			parameters.add(toTimestamp(movement.getDate()));
			parameters.add(movement.isPatient());
			if (movement.isPatient()) parameters.add(movement.getPatient().getCode().toString());
			else parameters.add("0");
			parameters.add( movement.getAge());
			parameters.add(movement.getWeight());
			parameters.add(movement.getDescription());
			parameters.add(movement.getMedical().getCode().toString());
			parameters.add(movement.getQuantity());
			parameters.add(movement.getUnits());
			parameters.add(movement.getCode());

			boolean updated = dbQuery.setDataWithParams(query, parameters, true);
			return updated;

		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Deletes the specified {@link MovementWard}.
	 * @param movement the movement ward to delete.
	 * @return <code>true</code> if the movement has been deleted, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the delete.
	 */
	public boolean deleteMovementWard(MovementWard movement) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			List<Object> parameters = Collections.<Object>singletonList(movement.getCode());
			String query = "DELETE FROM MEDICALDSRSTOCKMOVWARD WHERE MMVN_ID = ?";

			boolean deleted = dbQuery.setDataWithParams(query, parameters, true);

			return deleted;

		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Gets all the {@link Medical}s associated to specified {@link Ward}.
	 * @param wardId the ward id.
	 * @return the retrieved medicals.
	 * @throws OHException if an error occurs during the medical retrieving.
	 */
	public ArrayList<MedicalWard> getMedicalsWard(String wardId) throws OHException {

		ArrayList<MedicalWard> medicalWards = new ArrayList<MedicalWard>();

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = Collections.<Object>singletonList(wardId);
			StringBuilder query = new StringBuilder("SELECT MEDICALDSR.*, MDSRT_ID_A, MDSRT_DESC, MDSRWRD_WRD_ID_A, MDSRWRD_IN_QTI - MDSRWRD_OUT_QTI AS QTY ");
			query.append("FROM (MEDICALDSRWARD JOIN MEDICALDSR ON MDSRWRD_MDSR_ID = MDSR_ID) JOIN MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");
			query.append("WHERE MDSRWRD_WRD_ID_A = ? ");
			query.append("ORDER BY MDSR_DESC");


			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			while (resultSet.next()) {
				Medical medical = toMedical(resultSet);
				MedicalWard medicalWard = new MedicalWard(medical, resultSet.getDouble("QTY"));
				medicalWards.add(medicalWard);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}

		return medicalWards;
	}

	/**
	 * Converts a {@link GregorianCalendar} to a {@link Timestamp}.
	 * @param calendar the calendar to convert.
	 * @return the converted value or <code>null</code> if the passed value is <code>null</code>.
	 */
	protected Timestamp toTimestamp(GregorianCalendar calendar)
	{
		if (calendar == null) return null;
		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 * Converts the specified {@link java.sql.Date} to a {@link GregorianCalendar}.
	 * @param date the date to convert.
	 * @return the converted date.
	 */
	protected GregorianCalendar toCalendar(Date date){
		if (date == null) return null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}
	
	/**
	 * Converts the specified {@link java.sql.Timestamp} to a {@link GregorianCalendar}.
	 * @param timestamp the timestamp to convert.
	 * @return the converted timestamp.
	 */
	public GregorianCalendar toCalendar(Timestamp timestamp) {
		if (timestamp == null) return null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(timestamp);
		return calendar;
	}
	
	/**
	 * Store the supplied list of {@link Medical}s and quantities for future analysis
	 * @param missingMedical - the list of missing Medicals
	 * @param ward - the ward where the medicals are missing
	 * @return <code>true</code> if the list has been saved, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the medical retrieving.
	 */
	public boolean storeMissingMedicals(ArrayList<MedicalWard> missingMedical, Ward ward) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			for (MedicalWard medical : missingMedical) {

				boolean inserted = newMissingMedical(dbQuery, medical, ward);
				if (!inserted) {
					dbQuery.rollback();
					return false;
				}
			}
			dbQuery.commit();
			return true;
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Store the missing {@link Medical}s and its quantity for future analysis
	 * @param ward - the ward where the medical is missing
	 * @param medical - the missing medical
	 * @return <code>true</code> if the list has been saved, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the medical retrieving.
	 */
	private boolean newMissingMedical(DbQueryLogger dbQuery, MedicalWard medical, Ward ward) throws OHException {
		String query = "INSERT INTO MEDICALDSRWARDMISSING (WMS_DATE, WMS_WRD_ID_A, WMS_MDSR_ID, WMS_MDSR_QTY, WMS_MSDR_UNITS) VALUES (?,?,?,?,?)";
		List<Object> parameters = new ArrayList<Object>(5);

		parameters.add(toTimestamp(new GregorianCalendar()));
		parameters.add(ward.getCode());
		parameters.add(medical.getMedical().getCode().toString());
		parameters.add(medical.getQty());
		parameters.add("pieces");

		boolean inserted = dbQuery.setDataWithParams(query, parameters, false);

		return inserted;
	}
	
	/**
	 * Gets all missing {@link MissingMedical}s associated to specified {@link Ward}.
	 * @param wardId the ward id.
	 * @param dateTo 
	 * @param dateFrom 
	 * @return the retrieved medicals.
	 * @throws OHException if an error occurs during the medical retrieving.
	 */
	public ArrayList<MissingMedical> getMissingMedicals(String wardId, GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {

		ArrayList<MissingMedical> missingMedicals = new ArrayList<MissingMedical>();

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = new ArrayList<Object>(3);
			StringBuilder query = new StringBuilder("SELECT * FROM MEDICALDSRWARDMISSING ");
			query.append("JOIN MEDICALDSR ON WMS_MDSR_ID = MDSR_ID ");
			query.append("JOIN MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");
			query.append("JOIN WARD ON WMS_WRD_ID_A = WRD_ID_A ");
			query.append("WHERE WMS_WRD_ID_A = ? ");
			query.append("AND DATE(WMS_DATE) BETWEEN DATE(?) AND DATE(?) ");
			query.append("ORDER BY WMS_DATE DESC ");
			
			parameters.add(wardId);
			parameters.add(toDate(dateFrom));
			parameters.add(toDate(dateTo));

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			while (resultSet.next()) {
				MissingMedical medical = toMissingMedical(resultSet);
				missingMedicals.add(medical);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}

		return missingMedicals;
	}
	
	/**
	 * Store the specified {@link MedicalWardPrescription}
	 * @param prescription
	 * @param details 
	 * @return
	 * @throws OHException
	 */
	public boolean storePrescription(MedicalWardPrescription prescription, ArrayList<MedicalWardPrescriptionDetail> details) throws OHException  {
		String query = "INSERT INTO MEDICALDSRWARDPRESCRIPTION (" +
				"MWP_DATE, " +
				"MWP_PAT_ID, " +
				"MWP_WRD_ID_A, " +
				"MWP_BLL_ID, " +
				"MWP_IPD, " +
				"MWP_WRD_ID_B, " +
				"MWP_DIS_ID_A, " +
				"MWP_PRS_ID, " +
				"MWP_OPD_ID) " +
				"VALUES (?,?,?,?,?,?,?,?,?)";
		List<Object> parameters = new ArrayList<Object>(6);
		Bill bill = prescription.getBill();
		Prescriber prescriber = prescription.getPrescriber();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		parameters.add(toTimestamp(prescription.getDate()));
		parameters.add(prescription.getPatient().getCode());
		parameters.add(prescription.getWard().getCode());
		parameters.add(bill == null ? null : bill.getId());
		parameters.add(prescription.isIpd());
		parameters.add(prescription.getIpdWard());
		parameters.add(prescription.getDiseaseOpd());
		parameters.add(prescriber != null ?  prescriber.getId() : null);
		parameters.add(prescription.getOpdCode() > 0 ? prescription.getOpdCode() : null);
		
		try {
			boolean result = true;
			int prescrID;
			ResultSet resultSet = dbQuery.setDataReturnGeneratedKeyWithParams(query, parameters, false);
			if (resultSet.next()) {
				prescrID = resultSet.getInt(1);
				prescription.setId(prescrID);
			}
			else return false;
			
			query = "INSERT INTO MEDICALDSRWARDPRESCRIPTIONDETAIL (MWPD_MWP_ID, MWPD_MDSR_ID, MWPD_MDSR_QTY, MWPD_MDSR_UNITS) VALUES (?,?,?,?)";
			for (MedicalWardPrescriptionDetail detail : details) {
				parameters = new ArrayList<Object>(4);
				parameters.add(prescrID);
				parameters.add(detail.getMedical().getCode());
				parameters.add(detail.getQuantity());
				parameters.add(detail.getUnits());
				
				result = result && dbQuery.setDataWithParams(query, parameters, false);
			}
			
			if (!result) dbQuery.rollback();
			else dbQuery.commit();
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return true;
	}
	
	/**
	 * Retrieves the {@link MedicalWardPrescription} related to provided billId
	 * @param billId
	 * @return
	 * @throws OHException
	 */
	public MedicalWardPrescription getPrescription(int billId) throws OHException {
		MedicalWardPrescription prescription = null;
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(billId);
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT *, MLN_HEIGHT AS PAT_HEIGHT, MLN_WEIGHT AS PAT_WEIGHT FROM MEDICALDSRWARDPRESCRIPTION ");
		query.append("LEFT JOIN BILLS ON MWP_BLL_ID = BLL_ID ");
		query.append("JOIN WARD ON MWP_WRD_ID_A = WRD_ID_A ");
		query.append("JOIN PATIENT ON MWP_PAT_ID = PAT_ID ");
		query.append("LEFT JOIN MALNUTRITIONCONTROL ON MWP_PAT_ID = MLN_PAT_ID ");
		query.append("LEFT JOIN PRESCRIBER ON MWP_PRS_ID = PRS_ID ");
		query.append("WHERE MWP_BLL_ID = ? ");
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			while (resultSet.next()) {
				prescription = toMedicalWardPrescription(resultSet);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return prescription;
		
	}
	
	/**
	 * Retrieves {@link MedicalWardPrescription} for the specified {@link Ward} and dates
	 * @param wardSelected - the {@link Ward} selected
	 * @param toDate 
	 * @param fromDate 
	 * @return the list of {@link MedicalWardPrescription}
	 * @throws OHException
	 */
	public ArrayList<MedicalWardPrescription> getPrescriptions(Ward wardSelected, GregorianCalendar fromDate, GregorianCalendar toDate) throws OHException  {
		ArrayList<MedicalWardPrescription> pending = new ArrayList<MedicalWardPrescription>();
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = new ArrayList<Object>();
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT *, MLN_HEIGHT AS PAT_HEIGHT, MLN_WEIGHT AS PAT_WEIGHT FROM MEDICALDSRWARDPRESCRIPTION ");
		query.append("LEFT JOIN BILLS ON MWP_BLL_ID = BLL_ID ");
		query.append("JOIN WARD ON MWP_WRD_ID_A = WRD_ID_A ");
		query.append("JOIN PATIENT ON MWP_PAT_ID = PAT_ID ");
		query.append("LEFT JOIN MALNUTRITIONCONTROL ON MWP_PAT_ID = MLN_PAT_ID ");
		query.append("LEFT JOIN PRESCRIBER ON MWP_PRS_ID = PRS_ID ");
		query.append("WHERE MWP_WRD_ID_A = ? ");
		query.append("AND MWP_DATE BETWEEN ? AND ? ");
		
		parameters.add(wardSelected.getCode());
		parameters.add(toTimestamp(fromDate));
		parameters.add(toTimestamp(toDate));
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			while (resultSet.next()) {
				MedicalWardPrescription medical = toMedicalWardPrescription(resultSet);
				pending.add(medical);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pending;
	}
	
	/**
	 * Retrieves pending {@link MedicalWardPrescription} for the specified {@link Ward} and dates
	 * @param wardSelected - the {@link Ward} selected
	 * @param toDate 
	 * @param fromDate 
	 * @return the list of pending {@link MedicalWardPrescription}
	 * @throws OHException
	 */
	public ArrayList<MedicalWardPrescription> getPendingPrescriptions(Ward wardSelected, GregorianCalendar fromDate, GregorianCalendar toDate) throws OHException  {
		ArrayList<MedicalWardPrescription> pending = new ArrayList<MedicalWardPrescription>();
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = new ArrayList<Object>();
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT *, MLN_HEIGHT AS PAT_HEIGHT, MLN_WEIGHT AS PAT_WEIGHT FROM MEDICALDSRWARDPRESCRIPTION ");
		query.append("LEFT JOIN BILLS ON MWP_BLL_ID = BLL_ID ");
		query.append("JOIN WARD ON MWP_WRD_ID_A = WRD_ID_A ");
		query.append("JOIN PATIENT ON MWP_PAT_ID = PAT_ID ");
		query.append("LEFT JOIN MALNUTRITIONCONTROL ON MWP_PAT_ID = MLN_PAT_ID ");
		query.append("LEFT JOIN PRESCRIBER ON MWP_PRS_ID = PRS_ID ");
		query.append("WHERE MWP_WRD_ID_A = ? ");
		query.append("AND MWP_DATE BETWEEN ? AND ? ");
		query.append("AND MWP_STATUS = 0");
		
		parameters.add(wardSelected.getCode());
		parameters.add(toTimestamp(fromDate));
		parameters.add(toTimestamp(toDate));
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			while (resultSet.next()) {
				MedicalWardPrescription medical = toMedicalWardPrescription(resultSet);
				pending.add(medical);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pending;
	}
	
	/**
	 * Retrieves the {@link MedicalWardPrescriptionDetail}s of a {@link MedicalWardPrescription}
	 * @param prescription
	 * @return
	 * @throws OHException
	 */
	public ArrayList<MedicalWardPrescriptionDetail> getPrescriptionDetail(MedicalWardPrescription prescription) throws OHException  {
		ArrayList<MedicalWardPrescriptionDetail> details = new ArrayList<MedicalWardPrescriptionDetail>();
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(prescription.getId());
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM MEDICALDSRWARDPRESCRIPTIONDETAIL ");
		query.append("JOIN MEDICALDSR ON MWPD_MDSR_ID = MDSR_ID ");
		query.append("JOIN MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");
		query.append("WHERE MWPD_MWP_ID = ? ");
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			while (resultSet.next()) {
				MedicalWardPrescriptionDetail detail = toMedicalWardPrescriptionDetail(resultSet);
				details.add(detail);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return details;
	}
	
	/**
	 * Sets a {@link MedicalWardPrescription} as 'done' (status = 1);
	 * @param prescription
	 * @throws OHException
	 */
	public void closeMedicalPrescription(MedicalWardPrescription prescription) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(prescription.getId());
		
		StringBuilder query = new StringBuilder();
		query.append("UPDATE MEDICALDSRWARDPRESCRIPTION ");
		query.append("SET MWP_STATUS = 1 ");
		query.append("WHERE MWP_ID = ?");
		
		try {
			dbQuery.setDataWithParams(query.toString(), parameters, true);
			
		} finally {
			dbQuery.releaseConnection();
		}
	}
	
	/**
	 * Sets a {@link MedicalWardPrescription} as 'done' (status = 1);
	 * @param billID - the bill the prescription refers to
	 * @throws OHException
	 */
	public void closeMedicalPrescription(int billID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(billID);
		
		StringBuilder query = new StringBuilder();
		query.append("UPDATE MEDICALDSRWARDPRESCRIPTION ");
		query.append("SET MWP_STATUS = 1 ");
		query.append("WHERE MWP_BLL_ID = ?");
		
		try {
			dbQuery.setDataWithParams(query.toString(), parameters, true);
			
		} finally {
			dbQuery.releaseConnection();
		}
	}
	
	/**
	 * Sets a {@link MedicalWardPrescription} as 'cancelled' (status = 2);
	 * @param billID - the bill the prescription refers to
	 * @throws OHException
	 */
	public void deleteMedicalPrescription(int billID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = Collections.<Object>singletonList(billID);
		
		StringBuilder query = new StringBuilder();
		query.append("UPDATE MEDICALDSRWARDPRESCRIPTION ");
		query.append("SET MWP_STATUS = 2 ");
		query.append("WHERE MWP_BLL_ID = ?");
		
		try {
			dbQuery.setDataWithParams(query.toString(), parameters, true);
			
		} finally {
			dbQuery.releaseConnection();
		}
	}
	
	/**
	 * Extracts {@link MedicalWardPrescriptionDetail} from {@link ResultSet}
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private MedicalWardPrescriptionDetail toMedicalWardPrescriptionDetail(ResultSet resultSet) throws SQLException {
		MedicalWardPrescriptionDetail detail = new MedicalWardPrescriptionDetail();
		
		detail.setMedical(toMedical(resultSet));
		detail.setQuantity(resultSet.getDouble("MWPD_MDSR_QTY"));
		detail.setUnits(resultSet.getString("MWPD_MDSR_UNITS"));
		return detail;
	}
	
	/**
	 * Extracts {@link MedicalWardPrescription} from {@link ResultSet}
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private MedicalWardPrescription toMedicalWardPrescription(ResultSet resultSet) throws SQLException {
		MedicalWardPrescription prescription = new MedicalWardPrescription();
		org.isf.accounting.service.IoOperations billOp = new org.isf.accounting.service.IoOperations();
		Bill bill = billOp.toBill(resultSet);
		Patient patient = toPatient(resultSet);
		Prescriber prescriber = toPrescriber(resultSet);
		
		prescription.setDate(toCalendar(resultSet.getTimestamp("MWP_DATE")));
		prescription.setBill(bill);
		prescription.setId(resultSet.getInt("MWP_ID"));
		prescription.setStatus(resultSet.getInt("MWP_STATUS"));
		prescription.setPatient(patient);
		prescription.setWard(toWard(resultSet));
		prescription.setPrescriber(prescriber);
		prescription.setOpdCode(resultSet.getInt("MWP_OPD_ID"));
		return prescription;
	}
	
	/**
	 * Converts a {@link GregorianCalendar} to a {@link Date}.
	 * @param calendar the calendar to convert.
	 * @return the converted value or <code>null</code> if the passed value is <code>null</code>.
	 */
	protected java.sql.Timestamp toDate(GregorianCalendar calendar)
	{
		if (calendar == null) return null;
		return new java.sql.Timestamp(calendar.getTimeInMillis());
	}
}
