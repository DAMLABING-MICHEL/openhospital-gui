package org.isf.accounting.manager;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.swing.JOptionPane;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItem;
import org.isf.accounting.model.BillPayment;
import org.isf.accounting.service.IoOperations;
import org.isf.priceslist.model.PriceList;
import org.isf.utils.exception.OHException;
import org.joda.time.DateTime;

public class BillBrowserManager {

	IoOperations ioOperations;

	public BillBrowserManager(){
		ioOperations = new IoOperations();
	}

	/**
	 * Returns all the stored {@link BillItem}.
	 * @return a list of {@link BillItem} or null if an error occurs.
	 */
	public ArrayList<BillItem> getItems() {
		try {
			return ioOperations.getItems(0);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the {@link BillItem} associated to the passed {@link Bill} id.
	 * @param billID the bill id.
	 * @return a list of {@link BillItem} or <code>null</code> if an error occurred.
	 */
	public ArrayList<BillItem> getItems(int billID) {
		if (billID == 0) return new ArrayList<BillItem>();
		try {
			return ioOperations.getItems(billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the stored {@link BillPayment}.
	 * @return a list of bill payments or <code>null</code> if an error occurred.
	 */
	public ArrayList<BillPayment> getPayments() {
		try {
			return ioOperations.getPayments(0);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Gets all the {@link BillPayment} for the specified {@link Bill}.
	 * @param billID the bill id.
	 * @return a list of {@link BillPayment} or <code>null</code> if an error occurred.
	 */
	public ArrayList<BillPayment> getPayments(int billID) {
		if (billID == 0) return new ArrayList<BillPayment>();
		try {
			return ioOperations.getPayments(billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Stores a new {@link Bill}.
	 * @param newBill the bill to store.
	 * @return the generated id.
	 */
	public int newBill(Bill newBill) {
		try {
			return ioOperations.newBill(newBill);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}

	}

	/**
	 * Stores a list of {@link BillItem} associated to a {@link Bill}.
	 * @param billID the bill id.
	 * @param billItems the bill items to store.
	 * @return <code>true</code> if the {@link BillItem} have been store, <code>false</code> otherwise.
	 */
	public boolean newBillItems(int billID, ArrayList<BillItem> billItems) {
		try {
			return ioOperations.newBillItems(billID, billItems);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Stores a list of {@link BillPayment} associated to a {@link Bill}.
	 * @param billID the bill id.
	 * @param payItems the bill payments.
	 * @return <code>true</code> if the payment have stored, <code>false</code> otherwise.
	 */
	public boolean newBillPayments(int billID, ArrayList<BillPayment> payItems) {
		try {
			return ioOperations.newBillPayments(billID, payItems);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Updates the specified {@link Bill}.
	 * @param updateBill the bill to update.
	 * @return <code>true</code> if the bill has been updated, <code>false</code> otherwise.
	 */
	public boolean updateBill(Bill updateBill) {
		try {
			return ioOperations.updateBill(updateBill);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Returns all {@link Bill}s for the specified patient.
	 * @param patID the patient id.
	 * @return the list of bills or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getPatientBills(int patID) {
		try {
			return ioOperations.getPatientBills(patID, null, null);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns all {@link Bill}s for the specified patient between two dates
	 * @param patID the patient id.
	 * @param fromDate
	 * @param toDate
	 * @return the list of bills or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getPatientBills(int patID, GregorianCalendar fromDate, GregorianCalendar toDate) {
		try {
			return ioOperations.getPatientBills(patID, fromDate, toDate);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns all {@link Bill}s for the specified patient's previous code.
	 * @param OPD the patient's previous code.
	 * @return the list of bills or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getPatientBills(String OPD) {
		try {
			return ioOperations.getPatientBills(OPD);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * @param patID the patient id.
	 * @return the list of pending bills or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getPendingBills(int patID) {
		try {
			return ioOperations.getPendingBills(patID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns all the pending {@link Bill}s for the specified PriceList in the specified period.
	 * @param list - the {@link PriceList}
	 * @param from - the date from
	 * @param to - the date to
	 * @return the list of pending bills or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getPendingBills(PriceList list, GregorianCalendar from, GregorianCalendar to) {
		try {
			return ioOperations.getPendingBills(list, from, to);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Get all the {@link Bill}s.
	 * @return a list of bills or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getBills() {
		try {
			return ioOperations.getBills();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Get the {@link Bill} with specified billID
	 * @param billID
	 * @return the {@link Bill} or <code>null</code> if an error occurred.
	 */
	public Bill getBill(int billID) {
		try {
			return ioOperations.getBill(billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all user ids related to a {@link BillPayment}.
	 * @return a list of user id or <code>null</code> if an error occurred.
	 */
	public ArrayList<String> getUsers() {
		try {
			return ioOperations.getUsers();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Deletes the specified {@link Bill}.
	 * @param deleteBill the bill to delete.
	 * @return <code>true</code> if the bill has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteBill(Bill deleteBill) {
		try {
			return ioOperations.deleteBill(deleteBill);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 * @param dateFrom the low date range end point, inclusive. 
	 * @param dateTo the high date range end point, inclusive.
	 * @return a list of retrieved {@link Bill}s or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getBills(dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayment}.
	 * @param dateFrom the low date range end point, inclusive. 
	 * @param dateTo the high date range end point, inclusive.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayment} or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getBillsFromPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getBillsFromPayments(dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayment}.
	 * @param billPayments the {@link BillPayment} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayment} or <code>null</code> if an error occurred.
	 */
	@Deprecated
	public ArrayList<Bill> getBills(ArrayList<BillPayment> billPayments) {
		if (billPayments.isEmpty()) return new ArrayList<Bill>();
		try {
			return ioOperations.getBills(billPayments);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the {@link BillPayment} for the specified date range.
	 * @param dateFrom low endpoint, inclusive, for the date range. 
	 * @param dateTo high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayment} for the specified date range or <code>null</code> if an error occurred.
	 */
	public ArrayList<BillPayment> getPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getPayments(dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the {@link BillPayment} associated to the passed {@link Bill} list.
	 * @param billArray the bill list.
	 * @return a list of {@link BillPayment} associated to the passed bill list or <code>null</code> if an error occurred. 
	 */
	public ArrayList<BillPayment> getPayments(ArrayList<Bill> billArray) {
		try {
			return ioOperations.getPayments(billArray);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Creates a {@link Bill} with specified {@link BillItem}
	 * @param bill
	 * @param billItems
	 * @return billID if the bill has been created, <code>0</code> otherwise
	 */
	public int newBill(Bill bill, ArrayList<BillItem> billItems) {
		int billID = newBill(bill);
		if (billID > 0) {
			if (newBillItems(billID, billItems))
				return billID;
		}
		return 0;
	}
}
