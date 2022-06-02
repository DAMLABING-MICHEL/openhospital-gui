package org.isf.prescriber.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.isf.generaldata.MessageBundle;
import org.isf.prescriber.model.Prescriber;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;

public class IoOperations {

	/**
	 * returns the list of all {@link Prescriber}s 
	 * @return the list of all {@link Prescriber}s  
	 * @throws OHException
	 */
	public ArrayList<Prescriber> getPrescriber() throws OHException {
		ArrayList<Prescriber> lists = null;
		StringBuffer stringBfr = new StringBuffer("SELECT * FROM PRESCRIBER WHERE PRS_DELETED = 'N'");
		stringBfr.append(" ORDER BY PRS_NAME");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(),true);
			lists = new ArrayList<Prescriber>(resultSet.getFetchSize());
			while (resultSet.next()) {
				lists.add(new Prescriber(resultSet.getInt("PRS_ID"),
									 resultSet.getString("PRS_NAME")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return lists;
	}
}
