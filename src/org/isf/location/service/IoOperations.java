package org.isf.location.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.isf.generaldata.MessageBundle;
import org.isf.location.model.Location;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;

public class IoOperations {

	/**
	 * return the list of all possible {@link Location}s
	 * @return the list of {@link Location}s
	 * @throws OHException
	 */
	public ArrayList<Location> getLocation() throws OHException {
		ArrayList<Location> list = null;
		StringBuffer stringBfr = new StringBuffer("SELECT * FROM LOCATION");
		stringBfr.append(" ORDER BY LOC_CITY ASC, LOC_ADDRESS ASC");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			list = new ArrayList<Location>(resultSet.getFetchSize());
			while (resultSet.next()) {
				list.add(new Location(resultSet.getString("LOC_CITY"),
									 resultSet.getString("LOC_ADDRESS")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return list;
	}
	
	/**
	 * return the list of all distinct cities in {@link Location}s
	 * @return the list of cities
	 * @throws OHException
	 */
	public ArrayList<String> getCity() throws OHException {
		ArrayList<String> list = null;
		StringBuffer stringBfr = new StringBuffer("SELECT DISTINCT(LOC_CITY) FROM LOCATION");
		stringBfr.append(" ORDER BY LOC_CITY ASC");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			list = new ArrayList<String>(resultSet.getFetchSize());
			while (resultSet.next()) {
				list.add(resultSet.getString("LOC_CITY"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return list;
	}
}
