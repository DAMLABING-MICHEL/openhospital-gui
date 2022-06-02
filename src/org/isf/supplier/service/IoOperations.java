package org.isf.supplier.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.isf.generaldata.MessageBundle;
import org.isf.supplier.model.Supplier;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;

public class IoOperations {

	/**
	 * returns the list of all {@link Supplier}s 
	 * @param all - if <code>true</code> it will returns deleted ones also
	 * @return the list of all {@link Supplier}s  
	 * @throws OHException
	 */
	public ArrayList<Supplier> getSupplier(boolean all) throws OHException {
		ArrayList<Supplier> lists = null;
		StringBuffer stringBfr = new StringBuffer("SELECT * FROM SUPPLIER");
		if (!all) stringBfr.append(" WHERE SUP_DELETED = 'N'"); 
		stringBfr.append(" ORDER BY SUP_NAME");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(),true);
			lists = new ArrayList<Supplier>(resultSet.getFetchSize());
			while (resultSet.next()) {
				lists.add(new Supplier(resultSet.getInt("SUP_ID"),
									 resultSet.getString("SUP_NAME"),
									 resultSet.getString("SUP_ADDRESS"),
									 resultSet.getString("SUP_TAXCODE"),
									 resultSet.getString("SUP_PHONE"),
									 resultSet.getString("SUP_FAX"),
									 resultSet.getString("SUP_EMAIL"),
									 resultSet.getString("SUP_NOTE")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return lists;
	}
}
