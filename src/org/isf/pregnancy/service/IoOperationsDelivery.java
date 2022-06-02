package org.isf.pregnancy.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.model.User;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.model.Delivery;
import org.isf.utils.db.DbQuery;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.Converters;
public class IoOperationsDelivery {

	/**
	 * Insert one {@link Delivery} for the provided {@link Admission}
	 * @param admission - the admission for pregnancy (usually in Maternity ward)
	 * @param delivery - the delivery to be recorded
	 * @return <code>true</code> if the data has been saved, <code>false</code> otherwise
	 * @throws OHException
	 */
	public boolean insertPregnancyDelivery(Admission admission, Delivery delivery) throws OHException {
		boolean result = false;
		
		StringBuffer stringBfr = new StringBuffer("INSERT INTO PREGNANCYDELIVERY ");
		stringBfr.append("(PDEL_ADM_ID,PDEL_PREG_ID,PDEL_DIS_ID_A,PDEL_ESTIMATED_GA,PDEL_DRT_ID_A, PDEL_WEIGHT, PDEL_SEX");
		stringBfr.append(",PDEL_DATE_DEL,PDEL_ROBSON,PDEL_DLT_ID_A,PDEL_HEIGHT,PDEL_HEAD");
		stringBfr.append(",PDEL_APGAR,PDEL_CHILD_NAME,PDEL_MANAGEMENT,PDEL_COMP_APH,PDEL_COMP_PPH,PDEL_COMP_CP,PDEL_RMPT,PDEL_PMTCT_PLACE,PDEL_PMTCT_EXAM,PDEL_PMTCT_PARTNER");
		stringBfr.append(",PDEL_PA_MIN,PDEL_PA_MAX,PDEL_ANC,PDEL_MWH,PDEL_NOTE,PDEL_US_ID_A");
		stringBfr.append(") VALUES (?,?,?,?,?,?,?");
		stringBfr.append(",?,?,?,?,?");
		stringBfr.append(",?,?,?,?,?,?,?,?,?,?");
		stringBfr.append(",?,?,?,?,?,?");
		stringBfr.append(")");
		ArrayList<Object> params = getParams(admission, delivery);
		params.add(MainMenu.getUser());
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet r = dbQuery.setDataReturnGeneratedKeyWithParams(stringBfr.toString(), params, true);
			if (r.first()) {
				delivery.setId(r.getInt(1));
				result = true;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;

	}
	
	/**
	 * Update one Pregnancy Delivery case
	 * @param admission - the {@link Admission} this delivery refers to
	 * @param delivery - the delivery to be updated
	 * @return <code>true</code> if the data has been saved, <code>false</code> otherwise
	 * @throws OHException
	 */
	public boolean updateDelivery(Admission admission, Delivery delivery) throws OHException {
		boolean result = false;
		
		StringBuffer stringBfr = new StringBuffer("UPDATE PREGNANCYDELIVERY ");
		stringBfr.append("SET PDEL_ADM_ID = ?, PDEL_PREG_ID = ?, PDEL_DIS_ID_A = ?, PDEL_ESTIMATED_GA = ?, PDEL_DRT_ID_A = ?, PDEL_WEIGHT = ?, PDEL_SEX = ?");
		stringBfr.append(", PDEL_DATE_DEL = ?, PDEL_ROBSON = ?, PDEL_DLT_ID_A = ?, PDEL_HEIGHT = ?, PDEL_HEAD = ?");
		stringBfr.append(", PDEL_APGAR = ?, PDEL_CHILD_NAME = ?, PDEL_MANAGEMENT = ?, PDEL_COMP_APH = ?, PDEL_COMP_PPH = ?, PDEL_COMP_CP = ?, PDEL_RMPT = ?, PDEL_PMTCT_PLACE = ?, PDEL_PMTCT_EXAM = ?, PDEL_PMTCT_PARTNER = ?");
		stringBfr.append(", PDEL_PA_MIN = ?, PDEL_PA_MAX = ?, PDEL_ANC = ?, PDEL_MWH = ?, PDEL_NOTE = ?, PDEL_US_ID_A = ?");
		stringBfr.append(" WHERE PDEL_ID = ?");
		ArrayList<Object> params = getParams(admission, delivery);
		params.add(MainMenu.getUser());
		params.add(delivery.getId());
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			result = dbQuery.setDataWithParams(stringBfr.toString(), params, true);
			
		} finally {
			dbQuery.releaseConnection();
		}
		return result;

	}

	/**
	 * Deletes all the delivery records related to an admission
	 * 
	 * @param admId - the id of the Admission tuple
	 * @return <code>true</code> if the data has been deleted, <code>false</code> otherwise
	 * @throws OHException
	 * @deprecated before it was used for replacing all the deliveries instead of updating them  
	 */
	public boolean deleteAllDeliveryOfAdmission(int admId) throws OHException {
		String sql = "DELETE FROM PREGNANCYDELIVERY WHERE PDEL_ADM_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(admId);
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			return dbQuery.setDataWithParams(sql, parameters, true);
		} catch (OHException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Deletes one single record of the pregnancydelivery table
	 * 
	 * @param pregdelId - the id of the single {@link Delivery}
	 * @return true if the record is deleted correctly
	 * @throws OHException 
	 */
	public boolean deleteSinglePregnancyDelivery(int pregdelId) throws OHException {
		
		String sql = "DELETE FROM PREGNANCYDELIVERY WHERE PDEL_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(pregdelId);
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			return dbQuery.setDataWithParams(sql, parameters, true);
		} catch (OHException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}


	/**
	 * 
	 * @param admission - the {@link Admission} the deliveries are related to
	 * @return a List of Deliveries related to the {@link Admission}
	 * @throws OHException 
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public ArrayList<Delivery> getDeliveriesOfAdmission(Admission admission) throws OHException {
		ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
		StringBuilder sqlString = new StringBuilder("SELECT * FROM PREGNANCYDELIVERY PDEL");
		sqlString.append(" LEFT JOIN PREGNANCY PREG ON PREG_ID = PDEL_PREG_ID");
		sqlString.append(" LEFT JOIN ADMISSION ADM ON ADM_ID = PDEL_ADM_ID");
		sqlString.append(" LEFT JOIN DISEASE DIS ON ADM_IN_DIS_ID_A = DIS_ID_A");
		sqlString.append(" LEFT JOIN DISEASETYPE DIST ON DIS_DCL_ID_A = DCL_ID_A");
		sqlString.append(" LEFT JOIN DELIVERYTYPE ON DLT_ID_A = PDEL_DLT_ID_A");
		sqlString.append(" LEFT JOIN DELIVERYRESULTTYPE ON DRT_ID_A = PDEL_DRT_ID_A");
		sqlString.append(" WHERE PDEL_ADM_ID = ?");
		sqlString.append(" ORDER BY PDEL_ID ASC");
		List<Object> parameters = Collections.<Object>singletonList(admission.getId());
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(sqlString.toString(), parameters, true);
			while (resultSet.next()) {
				Delivery del = toDelivery(resultSet);
				deliveries.add(del);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return deliveries;

	}
	

	/**
	 * 
	 * @param patId
	 *            the id of the {@link Patient}
	 * @return a {@link HashMap} with the deliveryresulttype as key and the
	 *         number of such deliveryresulttype as value
	 */
	public HashMap<String, Integer> getDeliveryCount(int patId) {
		HashMap<String, Integer> delCount = new HashMap<String, Integer>();
		StringBuffer stringBfr = new StringBuffer();
		stringBfr.append("Select ADM.*, PDEL.*, DRT.DRT_DESC ");
		stringBfr.append("FROM ADMISSION ADM RIGHT JOIN PREGNANCYDELIVERY PDEL ON ");
		stringBfr.append("PDEL.PDEL_ADM_ID = ADM.ADM_ID ");
		stringBfr.append("LEFT JOIN DELIVERYRESULTTYPE DRT ON DRT.DRT_ID_A = PDEL.PDEL_DRT_ID_A ");
		stringBfr.append("WHERE ADM.ADM_DELETED = 'N' AND ADM.ADM_PAT_ID = ");
		stringBfr.append(patId);
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			while (resultSet.next()) {
				String drtdescStr = "";
				Object drtdesc = resultSet.getObject("DRT_DESC");
				if (drtdesc == null)
					drtdescStr = MessageBundle.getMessage("angal.pregnancy.unknowndeliveryresult");
				else
					drtdescStr = drtdesc.toString();

				if (delCount.containsKey(drtdescStr)) {
					Integer val = delCount.get(drtdescStr);
					val++;
					delCount.put((String) drtdescStr, val);
				} else {
					delCount.put((String) drtdescStr, new Integer(1));
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return delCount;
	}
	/**
	 * 
	 * @param patId
	 *            the id of the {@link Patient}
	 * @return the total number of Pregnancy visits performed by the Patient
	 */
	public int selectVisitCount(int patId) {
		int visits = 0;
		StringBuffer stringBfr = new StringBuffer();
		stringBfr
				.append("SELECT COUNT(*) AS COUNT FROM PREGNANCYVISIT PVIS, PREGNANCY PREG ");
		stringBfr
				.append("WHERE PVIS.PVIS_PREG_ID = PREG.PREG_ID AND PREG.PREG_PAT_ID = ");
		stringBfr.append(patId);
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			resultSet.next();
			visits = resultSet.getInt("COUNT");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return visits;
	}
	
	private ArrayList<Object> getParams(Admission admission, Delivery delivery) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(admission.getId());
		params.add(delivery.getPregnancy().getId());
		params.add(admission.getDiseaseInId());
		params.add(delivery.getEstimatedGestationalAge());
		params.add(delivery.getDeliveryResultType().getCode());
		params.add(delivery.getWeight());
		params.add(delivery.getSex());
		params.add(Converters.convertToSQLDate(delivery.getDeliveryDate()));
		params.add(delivery.getRobsonIndex());
		params.add(delivery.getDeliveryType().getCode());
		params.add(delivery.getHeight());
		params.add(delivery.getHead());
		params.add(delivery.getApgarScore());
		params.add(delivery.getChildName());
		params.add(delivery.getManagement());
		params.add(delivery.isComplicationAPH());
		params.add(delivery.isComplicationPPH());
		params.add(delivery.isComplicationCP());
		params.add(delivery.getRemovePlacentaType());
		params.add(delivery.getHivTestPlace());
		params.add(String.valueOf(delivery.getHivTestResult()));
		params.add(String.valueOf(delivery.getHivTestResultPartner()));
		params.add(delivery.getBloodPressureMin());
		params.add(delivery.getBloodPressureMax());
		params.add(delivery.getAncVisitDone());
		params.add(String.valueOf(delivery.getMotherWaitingHomeDone()));
		params.add(delivery.getNote());
		return params;
	}
	
	private Delivery toDelivery(ResultSet resultSet) throws SQLException {
		Delivery del = new Delivery();
		del.setId(resultSet.getInt("PDEL_ID"));
		del.setPregnancy(org.isf.pregnancy.service.IoOperationsPregnancy.toPregnancy(resultSet));
		del.setAdmission(org.isf.admission.service.IoOperations.toAdmission(resultSet));
		del.setDisease(org.isf.disease.service.IoOperations.parseDiseaseRecord(resultSet));
		del.setEstimatedGestationalAge(resultSet.getInt("PDEL_ESTIMATED_GA"));
		del.setDeliveryResultType(new DeliveryResultType(resultSet.getString("DRT_ID_A"), resultSet.getString("DRT_DESC")));
		del.setWeight(resultSet.getInt("PDEL_WEIGHT"));
		del.setHeight(resultSet.getInt("PDEL_HEIGHT"));
		del.setHead(resultSet.getFloat("PDEL_HEAD"));
		del.setSex(resultSet.getString("PDEL_SEX"));
		del.setDeliveryDate(Converters.toCalendar(resultSet.getTimestamp("PDEL_DATE_DEL")));
		del.setRobsonIndex(resultSet.getString("PDEL_ROBSON"));
		del.setDeliveryType(new DeliveryType(resultSet.getString("DLT_ID_A"), resultSet.getString("DLT_DESC")));
		del.setApgarScore(resultSet.getString("PDEL_APGAR"));
		del.setChildName(resultSet.getString("PDEL_CHILD_NAME"));
		del.setManagement(resultSet.getString("PDEL_MANAGEMENT"));
		del.setComplicationAPH(resultSet.getBoolean("PDEL_COMP_APH"));
		del.setComplicationPPH(resultSet.getBoolean("PDEL_COMP_PPH"));
		del.setComplicationCP(resultSet.getBoolean("PDEL_COMP_CP"));
		del.setRemovePlacentaType(resultSet.getString("PDEL_RMPT"));
		del.setHivTestPlace(resultSet.getString("PDEL_PMTCT_PLACE"));
		del.setHivTestResult(resultSet.getString("PDEL_PMTCT_EXAM").charAt(0));
		del.setHivTestResultPartner(resultSet.getString("PDEL_PMTCT_PARTNER").charAt(0));
		del.setBloodPressureMin(resultSet.getInt("PDEL_PA_MIN"));
		del.setBloodPressureMax(resultSet.getInt("PDEL_PA_MAX"));
		del.setAncVisitDone(resultSet.getString("PDEL_ANC"));
		del.setMotherWaitingHomeDone(resultSet.getString("PDEL_MWH").charAt(0));
		del.setNote(resultSet.getString("PDEL_NOTE"));
		del.setUser(new User(resultSet.getString("PDEL_US_ID_A"),"","",""));
		return del;
	}
}
