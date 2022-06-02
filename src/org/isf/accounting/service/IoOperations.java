package org.isf.accounting.service;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItem;
import org.isf.accounting.model.BillPayment;
import org.isf.generaldata.MessageBundle;
import org.isf.priceslist.model.PriceList;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Persistence class for Accounting module.
 */
public class IoOperations {

	/**
	 * Returns all {@link Bill}s for the specified patient between two dates
	 * @param patID the patient id.
	 * @param fromDate
	 * @param toDate
	 * @return the list of bills.
	 * @throws OHException if an error occurs retrieving the bills.
	 */
	public ArrayList<Bill> getPatientBills(int patID, GregorianCalendar fromDate, GregorianCalendar toDate) throws OHException {
		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLS");
		query.append(" WHERE 1");
		if (patID != 0) {
			query.append(" AND BLL_ID_PAT = ?");
			parameters.add(patID);
		}
		if (fromDate != null) {
			query.append(" AND BLL_DATE >= ?");
			parameters.add(new Timestamp(fromDate.getTime().getTime()));
		}
		if (toDate != null) {
			query.append(" AND BLL_DATE < ?");
			parameters.add(new Timestamp(toDate.getTime().getTime()));
		}
		query.append(" ORDER BY BLL_DATE DESC");
		return fetchBillsFromDb(query.toString(), parameters);
	}
	
	/**
	 * Returns all {@link Bill}s for the specified patient's previous code.
	 * @param OPD the patient's previous code.
	 * @return the list of bills.
	 * @throws OHException if an error occurs retrieving the bills.
	 */
	public ArrayList<Bill> getPatientBills(String OPD) throws OHException {
		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLS JOIN PATIENT ON BLL_ID_PAT = PAT_ID");
		if (!OPD.equals("")) {
			query.append(" WHERE PAT_PCODE = ?");
			parameters.add(OPD);
		}
		query.append(" ORDER BY BLL_DATE DESC");
		return fetchBillsFromDb(query.toString(), parameters);
	}
	
	/**
	 * Returns all the pending {@link Bill}s for the specified PriceList in the specified period.
	 * @param list - the {@link PriceList}
	 * @param from - the date from
	 * @param to - the date to
	 * @throws OHException if an error occurs retrieving the pending bills.
	 */
	public ArrayList<Bill> getPendingBills(PriceList list, GregorianCalendar from, GregorianCalendar to) throws OHException {
		List<Object> parameters = new ArrayList<Object>(3);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLS");
		query.append(" WHERE (BLL_STATUS = 'O' OR BLL_STATUS = 'L')");
		query.append(" AND BLL_ID_LST = ?");
		parameters.add(list.getId());
		query.append(" AND DATE(BLL_DATE) BETWEEN DATE(?) AND DATE(?)");
		parameters.add(new Timestamp(from.getTime().getTime()));
		parameters.add(new Timestamp(to.getTime().getTime()));
		return fetchBillsFromDb(query.toString(), parameters);
	}
	
	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * @param patID the patient id.
	 * @return the list of pending bills.
	 * @throws OHException if an error occurs retrieving the pending bills.
	 */
	public ArrayList<Bill> getPendingBills(int patID) throws OHException {
		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLS");
		query.append(" WHERE (BLL_STATUS = 'O' OR BLL_STATUS = 'L')");
		if (patID != 0) {
			query.append(" AND BLL_ID_PAT = ?");
			parameters.add(patID);
		}
		query.append(" ORDER BY BLL_DATE DESC");
		return fetchBillsFromDb(query.toString(), parameters);
	}
	
	/**
	 * Get all the {@link Bill}s.
	 * @return a list of bills.
	 * @throws OHException if an error occurs retrieving the bills.
	 */
	public ArrayList<Bill> getBills() throws OHException {
		String query = "SELECT * FROM BILLS ORDER BY BLL_DATE DESC";
		return fetchBillsFromDb(query, null);
	}
	
	/**
	 * Get the {@link Bill} with specified billID.
	 * @param billID Bill ID.
	 * @return the {@link Bill}.
	 * @throws OHException if an error occurs retrieving the bill.
	 */
	public Bill getBill(int billID) throws OHException {
		Bill bill = null;
		String query = "SELECT * FROM BILLS WHERE BLL_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(billID);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			while (resultSet.next()) {
				bill = toBill(resultSet);
			}
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return bill;
	}

	/**
	 * Returns all user ids related to a {@link BillPayment}.
	 * @return a list of user id.
	 * @throws OHException if an error occurs retrieving the users list.
	 */
	public ArrayList<String> getUsers() throws OHException {
		ArrayList<String> userIds = null;
		String query = "SELECT BLP_USR_ID_A FROM BILLPAYMENTS GROUP BY BLP_USR_ID_A ORDER BY BLP_USR_ID_A ASC";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getData(query,true);
			userIds = new ArrayList<String>(resultSet.getFetchSize());
			while (resultSet.next()) {
				userIds.add(resultSet.getString("BLP_USR_ID_A"));
			}			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return userIds;
	}

	/**
	 * Returns the {@link BillItem} associated to the specified {@link Bill} id or all 
	 * the stored {@link BillItem} if no id is provided. 
	 * @param billID the bill id or <code>0</code>.
	 * @return a list of {@link BillItem} associated to the bill id or all the stored bill items.
	 * @throws OHException if an error occurs retrieving the bill items.
	 */
	public ArrayList<BillItem> getItems(int billID) throws OHException {
		ArrayList<BillItem> billItems = null;

		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLITEMS"); 
		if (billID != 0) {
			query.append(" WHERE BLI_ID_BILL = ?");
			parameters.add(billID);
		}
		query.append(" ORDER BY BLI_ID ASC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			billItems = new ArrayList<BillItem>(resultSet.getFetchSize());
			while (resultSet.next()) {
				billItems.add(new BillItem(resultSet.getInt("BLI_ID"),
						resultSet.getInt("BLI_ID_BILL"),
						resultSet.getBoolean("BLI_IS_PRICE"),
						resultSet.getString("BLI_ID_PRICE"),
						resultSet.getString("BLI_ITEM_DESC"),
						resultSet.getDouble("BLI_ITEM_AMOUNT"),
						resultSet.getInt("BLI_QTY")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return billItems;
	}

	/**
	 * Retrieves all the {@link BillPayment} for the specified date range.
	 * @param dateFrom low endpoint, inclusive, for the date range. 
	 * @param dateTo high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayment} for the specified date range.
	 * @throws OHException if an error occurs retrieving the bill payments.
	 */
	public ArrayList<BillPayment> getPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {
		ArrayList<BillPayment> payments = null;
		StringBuilder query = new StringBuilder("SELECT * FROM BILLPAYMENTS");
		query.append(" WHERE DATE(BLP_DATE) BETWEEN ? AND ?");
		query.append(" ORDER BY BLP_ID_BILL, BLP_DATE ASC");
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			payments = new ArrayList<BillPayment>(resultSet.getFetchSize());
			while (resultSet.next()) {
				payments.add(new BillPayment(resultSet.getInt("BLP_ID"),
						resultSet.getInt("BLP_ID_BILL"),
						convertToGregorianCalendar(resultSet.getTimestamp("BLP_DATE")),
						resultSet.getDouble("BLP_AMOUNT"),
						resultSet.getString("BLP_USR_ID_A")));
			}
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return payments;
	}

	/**
	 * Retrieves all the {@link BillPayment} for the specified {@link Bill} id, or all 
	 * the stored {@link BillPayment} if no id is indicated.
	 * @param billID the bill id or <code>0</code>.
	 * @return the list of bill payments.
	 * @throws OHException if an error occurs retrieving the bill payments.
	 */
	public ArrayList<BillPayment> getPayments(int billID) throws OHException {
		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLPAYMENTS");
		if (billID != 0) {
			query.append(" WHERE BLP_ID_BILL = ?");
			parameters.add(billID);
		}
		query.append(" ORDER BY BLP_ID_BILL, BLP_DATE ASC");
		return fetchBillPaymentsFromDb(query.toString(), parameters);
	}

	private ArrayList<BillPayment> fetchBillPaymentsFromDb(String query, List<Object> parameters) throws OHException {
		ArrayList<BillPayment> payments = null;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			payments = new ArrayList<BillPayment>(resultSet.getFetchSize());
			while (resultSet.next()) {
				payments.add(new BillPayment(resultSet.getInt("BLP_ID"),
						resultSet.getInt("BLP_ID_BILL"),
						convertToGregorianCalendar(resultSet.getTimestamp("BLP_DATE")),
						resultSet.getDouble("BLP_AMOUNT"),
						resultSet.getString("BLP_USR_ID_A")));
			}
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return payments;
	}

	/**
	 * Converts the specified {@link Timestamp} to a {@link GregorianCalendar} instance.
	 * @param aDate the date to convert.
	 * @return the corresponding GregorianCalendar value or <code>null</code> if the input value is <code>null</code>.
	 */
	public GregorianCalendar convertToGregorianCalendar(Timestamp aDate) {
		if (aDate == null)
			return null;
		GregorianCalendar time = new GregorianCalendar();
		time.setTime(aDate);
		return time;
	}

	/**
	 * Stores a new {@link Bill}.
	 * @param newBill the bill to store.
	 * @return the generated {@link Bill} id.
	 * @throws OHException if an error occurs storing the bill.
	 */
	public int newBill(Bill newBill) throws OHException {
		int billID;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			String query = "INSERT INTO BILLS (" +
					"BLL_DATE, BLL_UPDATE, BLL_IS_LST, BLL_ID_LST, BLL_LST_NAME, BLL_IS_PAT, BLL_ID_PAT, BLL_PAT_NAME, BLL_STATUS, BLL_AMOUNT, BLL_BALANCE, BLL_USR_ID_A, BLL_ADM_ID) "+
					"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

			List<Object> parameters = new ArrayList<Object>(11);
			parameters.add(new java.sql.Timestamp(newBill.getDate().getTime().getTime()));
			parameters.add(new java.sql.Timestamp(newBill.getUpdate().getTime().getTime()));
			parameters.add(newBill.isList());
			parameters.add(newBill.getListID());
			parameters.add(newBill.getListName());
			parameters.add(newBill.isPatient());
			parameters.add(newBill.getPatID());
			parameters.add(newBill.getPatName());
			parameters.add(newBill.getStatus());
			parameters.add(newBill.getAmount());
			parameters.add(newBill.getBalance());
			parameters.add(newBill.getUser());
			parameters.add(newBill.getAdmID());
			ResultSet result = dbQuery.setDataReturnGeneratedKeyWithParams(query, parameters, true);

			if (result.next())
				billID = result.getInt(1);
			else return 0;

			return billID;

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Stores a list of {@link BillItem} associated to a {@link Bill}.
	 * @param billID the bill id.
	 * @param billItems the bill items to store.
	 * @return <code>true</code> if the {@link BillItem} have been store, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the store operation.
	 */
	public boolean newBillItems(int billID, ArrayList<BillItem> billItems) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		try{
			//With this INSERT and UPDATE processes are equals
			String query = "DELETE FROM BILLITEMS WHERE BLI_ID_BILL = ?";
			List<Object> parameters = Collections.<Object>singletonList(billID);
			dbQuery.setDataWithParams(query, parameters, false);

			query = "INSERT INTO BILLITEMS (" +
					"BLI_ID_BILL, BLI_IS_PRICE, BLI_ID_PRICE, BLI_ITEM_DESC, BLI_ITEM_AMOUNT, BLI_QTY) "+
					"VALUES (?,?,?,?,?,?)";

			for (BillItem item : billItems) {

				parameters = new ArrayList<Object>(6);
				parameters.add(billID);
				parameters.add(item.isPrice());
				parameters.add(item.getPriceID());
				parameters.add(item.getItemDescription());
				parameters.add(item.getItemAmount());
				parameters.add(item.getItemQuantity());
				//System.out.println(pstmt.toString());
				result = result && dbQuery.setDataWithParams(query, parameters, false);
			}
			
			if (result) {
				dbQuery.commit();
			}
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Stores a list of {@link BillPayment} associated to a {@link Bill}.
	 * @param billID the bill id.
	 * @param payItems the bill payments.
	 * @return <code>true</code> if the payment have stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the store procedure.
	 */
	public boolean newBillPayments(int billID, ArrayList<BillPayment> payItems) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		try{

			//With this INSERT and UPDATE processes are equals
			String query = "DELETE FROM BILLPAYMENTS WHERE BLP_ID_BILL = ?";
			List<Object> parameters = Collections.<Object>singletonList(billID);
			dbQuery.setDataWithParams(query, parameters, false);

			query = "INSERT INTO BILLPAYMENTS (" +
					"BLP_ID_BILL, BLP_DATE, BLP_AMOUNT, BLP_USR_ID_A) " +
					"VALUES (?,?,?,?)";

			for (BillPayment item : payItems) {
				parameters = new ArrayList<Object>(4);
				parameters.add(billID);
				parameters.add(new java.sql.Timestamp(item.getDate().getTime().getTime()));
				parameters.add(item.getAmount());
				parameters.add(item.getUser());
				//System.out.println(pstmt.toString());
				result = result && dbQuery.setDataWithParams(query, parameters, false);
			}

			if (result) {
				dbQuery.commit();
			}
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Updates the specified {@link Bill}.
	 * @param updateBill the bill to update.
	 * @return <code>true</code> if the bill has been updated, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the update.
	 */
	public boolean updateBill(Bill updateBill) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{

			String query = "UPDATE BILLS SET " +
					"BLL_DATE = ?, " +
					"BLL_UPDATE = ?, " +
					"BLL_IS_LST = ?, " +
					"BLL_ID_LST = ?, " +
					"BLL_LST_NAME = ?, " +
					"BLL_IS_PAT = ?, " +
					"BLL_ID_PAT = ?, " +
					"BLL_PAT_NAME = ?, " +
					"BLL_STATUS = ?, " +
					"BLL_AMOUNT = ?, " +
					"BLL_BALANCE = ?, " +
					"BLL_USR_ID_A = ? " +
					"WHERE BLL_ID = ?";

			List<Object> parameters = new ArrayList<Object>(12);

			parameters.add(new java.sql.Timestamp(updateBill.getDate().getTimeInMillis()));
			parameters.add(new java.sql.Timestamp(updateBill.getUpdate().getTimeInMillis()));
			parameters.add(updateBill.isList());
			parameters.add(updateBill.getListID());
			parameters.add(updateBill.getListName());
			parameters.add(updateBill.isPatient());
			parameters.add(updateBill.getPatID());
			parameters.add(updateBill.getPatName());
			parameters.add(updateBill.getStatus());
			parameters.add(updateBill.getAmount());
			parameters.add(updateBill.getBalance());
			parameters.add(updateBill.getUser());
			parameters.add(updateBill.getId());

			//System.out.println(pstmt.toString());
			dbQuery.setDataWithParams(query, parameters, true);

			return true;

		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Deletes the specified {@link Bill}.
	 * @param deleteBill the bill to delete.
	 * @return <code>true</code> if the bill has been deleted, <code>false</code> otherwise.
	 * @throws OHException if an error occurs deleting the bill.
	 */
	public boolean deleteBill(Bill deleteBill) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		try{
			List<Object> parameters = Collections.<Object>singletonList(deleteBill.getId());
			
//			String query = "DELETE FROM BILLS WHERE BLL_ID = ?";
//			dbQuery.setDataWithParams(query, parameters, false);
//
//			query = "DELETE FROM BILLPAYMENTS WHERE BLP_ID_BILL = ?";
//			dbQuery.setDataWithParams(query, parameters, false);
//
//			query = "DELETE FROM BILLITEMS WHERE BLI_ID_BILL = ?";
//			dbQuery.setDataWithParams(query, parameters, false);
			
			String query = "UPDATE BILLS SET BLL_STATUS = 'D' WHERE BLL_ID = ?";
			dbQuery.setDataWithParams(query, parameters, false);

		} finally {
			dbQuery.commit();
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 * @param dateFrom the low date range end point, inclusive. 
	 * @param dateTo the high date range end point, inclusive.
	 * @return a list of retrieved {@link Bill}s.
	 * @throws OHException if an error occurs retrieving the bill list.
	 */
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {
		String query = "SELECT * FROM BILLS WHERE DATE(BLL_DATE) BETWEEN ? AND ?";
		List<Object> parameters = new ArrayList<Object>(2);
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		return fetchBillsFromDb(query, parameters);
	}
	
	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayment}.
	 * @param dateFrom the low date range end point, inclusive. 
	 * @param dateTo the high date range end point, inclusive.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayment}.
	 * @throws OHException if an error occurs retrieving the bill list.
	 */
	public ArrayList<Bill> getBillsFromPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {
		String query = "SELECT * FROM BILLS LEFT JOIN BILLPAYMENTS ON BLL_ID = BLP_ID_BILL WHERE DATE(BLP_DATE) BETWEEN ? AND ? ORDER BY BLL_ID DESC";
		List<Object> parameters = new ArrayList<Object>(2);
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		return fetchBillsFromDb(query, parameters);
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayment}.
	 * @param payments the {@link BillPayment} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayment}.
	 * @throws OHException if an error occurs retrieving the bill list.
	 */
	@Deprecated
	public ArrayList<Bill> getBills(ArrayList<BillPayment> payments) throws OHException {
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("");
		for (int i = 0; i < payments.size(); i++) {
			BillPayment payment = payments.get(i);
			if (i == payments.size() - 1) {
				query.append("?");
				parameters.add(payment.getBillID());
			} else {
				query.append("?, ");
				parameters.add(payment.getBillID());
			}
		}
		query.append(")");
		return fetchBillsFromDb(query.toString(), parameters);
	}

	private ArrayList<Bill> fetchBillsFromDb(String query, List<Object> parameters) throws OHException {
		ArrayList<Bill> bills = null;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet;
			if (parameters != null)
				resultSet = dbQuery.getDataWithParams(query, parameters, true);
			else
				resultSet = dbQuery.getData(query, true);
			bills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bills.add(bill);
			}
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return bills;
	}

	/**
	 * #BILLBROWSER_NEW
	 * Compute amount and balance total for a set of {@link Bill} objects.
	 * @param filters a <code>Map</code> containing the filters.
	 * @return <code>ArrayList<BigDecimal></code>.
	 * @throws OHException if soething goes wrong.
     */
	public ArrayList<BigDecimal> getBillsTotalInPeriod(Map<String, Object> filters) throws OHException {
		List<Object> parameters = new ArrayList<Object>();
		String query = "SELECT SUM(BLL_AMOUNT - BLL_BALANCE) AS AMOUNT, SUM(BLL_BALANCE) AS BALANCE FROM BILLS" +
				" WHERE DATE(BLL_DATE) BETWEEN ? AND ?" +
				" AND BLL_STATUS <> 'D'";
		parameters.add(new Timestamp(((Date) filters.get("dateFrom")).getTime()));
		parameters.add(new Timestamp(((Date) filters.get("dateTo")).getTime()));
		return fetchTotalsFromDb(query, parameters);
	}

	/**
	 * #BILLBROWSER_NEW
	 * Compute amount and balance total for a set of {@link Bill} objects.
	 * @param filters a <code>Map</code> containing the filters.
	 * @return <code>ArrayList<BigDecimal></code>.
	 * @throws OHException if soething goes wrong.
	 */
	public ArrayList<BigDecimal> getBillsTotalToday(Map<String, Object> filters) throws OHException {
		List<Object> parameters = new ArrayList<Object>();
		String query = "SELECT SUM(BLL_AMOUNT - BLL_BALANCE) AS AMOUNT, SUM(BLL_BALANCE) AS BALANCE FROM BILLS" +
				" WHERE BLL_STATUS <> 'D'" +
				" AND DATE(BLL_DATE) BETWEEN ? AND ?";
		long today = DateTime.now().withTimeAtStartOfDay().toDate().getTime();
		parameters.add(new Timestamp(today));
		parameters.add(new Timestamp(today));
		return fetchTotalsFromDb(query, parameters);
	}

	/**
	 * #BILLBROWSER_NEW
	 * @param user <code>String</code> containing a username.
	 * @param dateFrom <code>Timestamp</code> of starting date.
	 * @param dateTo <code>Timestamp</code> of end date.
	 * @return <code>BigDecimal</code>.
	 * @throws OHException if something goes wrong.
     */
	public BigDecimal getBillsTotalInPeriodByUser(String user, Timestamp dateFrom, Timestamp dateTo) throws OHException {
		List<Object> parameters = new ArrayList<Object>();
		String query = "SELECT SUM(BLL_AMOUNT - BLL_BALANCE) AS AMOUNT FROM BILLS" +
				" WHERE DATE(BLL_DATE) BETWEEN ? AND ?" +
				" AND BLL_STATUS <> 'D'";
		parameters.add(dateFrom);
		parameters.add(dateTo);
		if (!user.equals("ALL")) {
			query += " AND BLL_USR_ID_A = ?";
			parameters.add(user);
		}
		BigDecimal total = null;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			while (resultSet.next()) {
				total = new BigDecimal(resultSet.getDouble("AMOUNT"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return total;
	}

	/**
	 * #BILLBROWSER_NEW
	 * @param query <code>String</code> containing the query.
	 * @param parameters <code>List<Object></code> containing query parameters.
	 * @return <code>ArrayList<BigDecimal></code> results.
	 * @throws OHException if something goes wrong.
     */
	private ArrayList<BigDecimal> fetchTotalsFromDb(String query, List<Object> parameters) throws OHException {
		ArrayList<BigDecimal> totals = new ArrayList<BigDecimal>(2);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			while (resultSet.next()) {
				totals.add(new BigDecimal(resultSet.getDouble("AMOUNT")));
				totals.add(new BigDecimal(resultSet.getDouble("BALANCE")));
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return totals;
	}

	/**
	 * Retrieves all the {@link BillPayment} associated to the passed {@link Bill} list.
	 * @param bills the bill list.
	 * @return a list of {@link BillPayment} associated to the passed bill list.
	 * @throws OHException if an error occurs retrieving the payments.
	 */
	public ArrayList<BillPayment> getPayments(ArrayList<Bill> bills) throws OHException {
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT * FROM BILLPAYMENTS WHERE BLP_ID_BILL IN (''");
		if (bills!=null) {
			for (Bill bill:bills) {
				query.append(", ?");
				parameters.add(bill.getId());
			}
		}
		query.append(")");
		return fetchBillPaymentsFromDb(query.toString(), parameters);
	}
	
	public Bill toBill(ResultSet resultSet) throws SQLException {
		Bill bill = new Bill(resultSet.getInt("BLL_ID"),
				convertToGregorianCalendar(resultSet.getTimestamp("BLL_DATE")),
				convertToGregorianCalendar(resultSet.getTimestamp("BLL_UPDATE")),
				resultSet.getBoolean("BLL_IS_LST"),
				resultSet.getInt("BLL_ID_LST"),
				resultSet.getString("BLL_LST_NAME"),
				resultSet.getBoolean("BLL_IS_PAT"),
				resultSet.getInt("BLL_ID_PAT"),
				resultSet.getString("BLL_PAT_NAME"),
				resultSet.getString("BLL_STATUS"),
				resultSet.getDouble("BLL_AMOUNT"),
				resultSet.getDouble("BLL_BALANCE"),
				resultSet.getString("BLL_USR_ID_A"),
				resultSet.getInt("BLL_ADM_ID"));
		return bill;
	}
}
