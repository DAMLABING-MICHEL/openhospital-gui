package org.isf.sms.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.sms.model.Sms;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
// Generated 31-gen-2014 15.39.04 by Hibernate Tools 3.4.0.CR1
import java.util.Collections;
import java.util.Date;

/**
 * @see org.isf.sms.model.Sms
 * @author Mwithi
 */
public class IoOperations {

	/**
	 * 
	 */
	public IoOperations() {}
	
	/**
	 * Save or Update a {@link Sms}
	 * @param supplier - the {@link Sms} to save or update
	 * return <code>true</code> if data has been saved, <code>false</code> otherwise. 
	 * @throws OHException 
	 */
	public boolean saveOrUpdate(
			Sms sms) throws OHException 
	{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		
		try {
			if (sms.getSmsId() == 0)
			{
				String query = "INSERT INTO SMS (SMS_DATE, SMS_DATE_SCHED, SMS_NUMBER, SMS_TEXT, SMS_DATE_SENT, SMS_USER, SMS_MOD, SMS_MOD_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				ArrayList<Object> params = new ArrayList<Object>();
				params.add(sms.getSmsDate());
				params.add(sms.getSmsDateSched());
				params.add(sms.getSmsNumber());
				params.add(sms.getSmsText());
				params.add(sms.getSmsDateSent());
				params.add(sms.getSmsUser());
				params.add(sms.getModule());
				params.add(sms.getModuleID());
				result = dbQuery.setDataWithParams(query, params, true);			
			}
			else
			{			
				String query = "UPDATE SMS SET SMS_DATE = ?, SMS_DATE_SCHED = ?, SMS_NUMBER = ?, SMS_TEXT = ?, SMS_DATE_SENT = ?, SMS_USER = ?, SMS_MOD = ?, SMS_MOD_ID = ? WHERE SMS_ID = ?";
				ArrayList<Object> params = new ArrayList<Object>();
				params.add(sms.getSmsDate());
				params.add(sms.getSmsDateSched());
				params.add(sms.getSmsNumber());
				params.add(sms.getSmsText());
				params.add(sms.getSmsDateSent());
				params.add(sms.getSmsUser());
				params.add(sms.getModule());
				params.add(sms.getModuleID());
				params.add(sms.getSmsId());
				ResultSet resultSet = dbQuery.setDataReturnGeneratedKeyWithParams(query, params, true);
				if(resultSet.first()){
					int smsId = resultSet.getInt("SMS_ID");
					sms.setSmsId(smsId);
					result = true;
				}
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;	
	}
	
	/**
	 * Returns a {@link Sms} with specified ID
	 * @param ID - sms ID
	 * @return sms - the sms with specified ID
	 * @throws OHException 
	 */
	public Sms getByID(
			int ID) throws OHException 
	{
		DbQueryLogger dbQuery = new DbQueryLogger();
		String query = "SELECT * FROM SMS WHERE SMS_ID = ?";
		List<Object> params = Collections.<Object>singletonList(ID);
		Sms sms = null;
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			if(resultSet != null) sms = toSMS(resultSet);
		} catch (SQLException e) {					
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return sms;
	}
	
	/**
	 * Returns the list of all {@link Sms}s, sent and not sent, between the two dates
	 * @return smsList - the list of {@link Sms}s
	 * @throws OHException 
	 */
	public ArrayList<Sms> getAll(
			Date dateFrom, 
			Date dateTo) throws OHException 
	{
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Sms> smsList = new ArrayList<Sms>();
		ArrayList<Object> params = new ArrayList<Object>();
		
		String query = "SELECT * FROM SMS" +
						" WHERE DATE(SMS_DATE_SCHED) BETWEEN ? AND ?" +
						" ORDER BY SMS_DATE_SCHED ASC";
		params.add(new Timestamp(dateFrom.getTime()));
		params.add(new Timestamp(dateTo.getTime()));
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			while(resultSet.next()) {
				Sms sms = toSMS(resultSet);
				smsList.add(sms);
			}
		} catch (SQLException e) {					
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return smsList;
	}
	
	/**
	 * Returns the list of not sent {@link Sms}s between the two dates
	 * @return smsList - the list of {@link Sms}s
	 * @throws OHException 
	 */
	public List<Sms> getList(
			Date dateFrom, 
			Date dateTo) throws OHException 
	{
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Sms> smsList = new ArrayList<Sms>();
		ArrayList<Object> params = new ArrayList<Object>();
		
		String query = "SELECT * FROM SMS" +
						" WHERE DATE(SMS_DATE_SCHED) BETWEEN ? AND ?" +
						" AND SMS_DATE_SENT IS NULL " +
						" ORDER BY SMS_DATE_SCHED ASC";
		params.add(new Timestamp(dateFrom.getTime()));
		params.add(new Timestamp(dateTo.getTime()));
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			while(resultSet.next()) {
				Sms sms = toSMS(resultSet);
				smsList.add(sms);
			}
		} catch (SQLException e) {					
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return smsList;
	}
	
	/**
	 * Returns the list of not sent {@link Sms}s
	 * @return smsList - the list of {@link Sms}s
	 * @throws OHException 
	 */
	public ArrayList<Sms> getList() throws OHException 
	{
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Sms> smsList = new ArrayList<Sms>();
		ArrayList<Object> params = new ArrayList<Object>();
		
		String query = "SELECT * FROM SMS" +
						" WHERE SMS_DATE_SENT IS NULL " +
						" ORDER BY SMS_DATE_SCHED ASC";
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			while(resultSet.next()) {
				Sms sms = toSMS(resultSet);
				smsList.add(sms);
			}
		} catch (SQLException e) {					
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return smsList;
	}

	/**
	 * Delete the specified {@link Sms}
	 * @param sms - the {@link Sms} to delete
	 * @throws OHException 
	 */
	public boolean deleteSMS(
			Sms sms) throws OHException 
	{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		List<Object> params = Collections.<Object>singletonList(sms.getSmsId());
		
		String query = "DELETE FROM SMS WHERE SMS_ID = ?";
		
		try {
			result = dbQuery.setDataWithParams(query, params, true);

		} catch (OHException e) {					
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Delete the specified {@link Sms}s if not already sent
	 * @param module - the module name which generated the {@link Sms}s
	 * @param moduleID - the module ID within its generated {@link Sms}s
	 * @throws OHException 
	 */
	public boolean deleteByModuleModuleID(
			String module, 
			String moduleID) throws OHException 
	{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		ArrayList<Object> params = new ArrayList<Object>();
        		
		try {
			String query = "DELETE FROM SMS" +
					" WHERE SMS_MOD = ? AND SMS_MOD_ID = ?" +
					" AND SMS_DATE_SENT IS NULL";
			params.add(module);
			params.add(moduleID);
			result = dbQuery.setDataWithParams(query, params, true);
			
		}  catch (OHException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		
        return result;
	}
	
	protected Sms toSMS(ResultSet resultSet) throws SQLException {
		Sms sms = new Sms();
		
		sms.setSmsId(resultSet.getInt("SMS_ID"));
		sms.setSmsDate(resultSet.getTimestamp("SMS_DATE"));
		sms.setSmsDateSched(resultSet.getTimestamp("SMS_DATE_SCHED"));
		sms.setSmsDateSent(resultSet.getTimestamp("SMS_DATE_SENT"));
		sms.setSmsText(resultSet.getString("SMS_TEXT"));
		sms.setSmsNumber(resultSet.getString("SMS_NUMBER"));
		sms.setSmsUser(resultSet.getString("SMS_USER"));
		sms.setModule(resultSet.getString("SMS_MOD"));
		sms.setModuleID(resultSet.getString("SMS_MOD_ID"));
		
		return sms;
	}
}
