package org.isf.distype.service;

import org.isf.distype.model.DiseaseType;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Persistence class for the DisType module.
 */
public class IoOperation {

	/**
	 * Returns all the stored {@link DiseaseType}s.
	 * @return a list of disease type.
	 * @throws OHException if an error occurs retrieving the diseases list.
	 */
	public ArrayList<DiseaseType> getDiseaseTypes(boolean chronicTypesOnly) throws OHException {
		ArrayList<DiseaseType> diseaseTypes = null;
		String query = "SELECT * FROM DISEASETYPE DT";
		if (chronicTypesOnly) {
			query += " WHERE EXISTS (SELECT 1 FROM DISEASE D WHERE D.DIS_IS_CHRONIC IS TRUE" +
					" AND DIS_OPD_INCLUDE IS TRUE" +
					" AND D.DIS_DCL_ID_A = DT.DCL_ID_A)";
		}
		query += " ORDER BY DCL_DESC";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getData(query,true);
			diseaseTypes = new ArrayList<DiseaseType>(resultSet.getFetchSize());
			while (resultSet.next()) {
				diseaseTypes.add(new DiseaseType(resultSet.getString("DCL_ID_A"), resultSet.getString("DCL_DESC")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return diseaseTypes;
	}

	/**
	 * Retruns a {@link DiseaseType} object given its code.
	 * @param code a String containing the code of the {@link DiseaseType}.
	 * @return {@link DiseaseType} object.
	 * @throws OHException if something goes wrong.
     */
	public DiseaseType getDiseaseTypeByCode(String code) throws OHException {
		String query = "SELECT * FROM DISEASETYPE WHERE DCL_ID_A = ?;";
		List<Object> params = new ArrayList<Object>(1);
		params.add(code);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			if (resultSet.first())
				return new DiseaseType(resultSet.getString("DCL_ID_A"), resultSet.getString("DCL_DESC"));
			else
				return null;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
	}
	/**
	 * Updates the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to update.
	 * @return <code>true</code> if the disease type has been updated, false otherwise.
	 * @throws OHException if an error occurs during the update operation.
	 */
	public boolean updateDiseaseType(DiseaseType diseaseType) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = new ArrayList<Object>(3);
			parameters.add(diseaseType.getCode());
			parameters.add(diseaseType.getDescription());
			parameters.add(diseaseType.getCode());
			String query = "update DISEASETYPE set DCL_ID_A=?, DCL_DESC=? where DCL_ID_A=?";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Store the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to store.
	 * @return <code>true</code> if the {@link DiseaseType} has been stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the store operation.
	 */
	public boolean newDiseaseType(DiseaseType diseaseType) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(diseaseType.getCode());
			parameters.add(diseaseType.getDescription());
			String query = "insert into DISEASETYPE (DCL_ID_A,DCL_DESC) values (?,?)";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Deletes the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to remove.
	 * @return <code>true</code> if the disease has been removed, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the delete procedure.
	 */
	public boolean deleteDiseaseType(DiseaseType diseaseType) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = Collections.<Object>singletonList(diseaseType.getCode());
			String query = "delete from DISEASETYPE where DCL_ID_A = ?";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Checks if the specified code is already used by any {@link DiseaseType}.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, false otherwise.
	 * @throws OHException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHException{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean present=false;
		try{
			List<Object> parameters = Collections.<Object>singletonList(code);
			String query = "SELECT DCL_ID_A FROM DISEASETYPE where DCL_ID_A = ?";
			ResultSet set = dbQuery.getDataWithParams(query, parameters, true);
			present = set.first();
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return present;
	}
}
