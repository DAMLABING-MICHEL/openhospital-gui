/**
 * 
 */
package org.isf.utils.db.maintenance;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.log4j.PropertyConfigurator;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.utils.db.DbSingleConn;

/**
 * @author Nanni
 * Dispense all the drugs related to the PayLater bills (before release4)
 */
public class PayLaterDispense {

	/*
	 * 2016-04-27
	 * GregorianCalendar(year, 0-11, 1-31)
	 */
	private static GregorianCalendar limitDate = new GregorianCalendar(2016, 3, 27);
	private static boolean dispense = true;
	private static boolean reopen = false;
	/**
	 * 
	 */
	public PayLaterDispense() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure(new File("./rsc/log4j.properties").getAbsolutePath());
		MovWardBrowserManager wardMan = new MovWardBrowserManager();
		BillBrowserManager billMan = new BillBrowserManager();
		
		ArrayList<Bill> pendingBills = billMan.getPendingBills(0);
		
		int payLaterBillCount = 0;
		int payLaterPrescription = 0;
		for (Bill bill : pendingBills) {
			//if (bill.getDate().after(limitDate)) continue;
			if (bill.getStatus().equals("L")) {
				int billID = bill.getId();
				GregorianCalendar dateUpdate = bill.getUpdate();
				System.out.println(++payLaterBillCount + ") " + billID);
				
				boolean ok = wardMan.dispenseMedicalPrescription(billID, dateUpdate, dispense);
				if (ok) {
					System.out.println(" --> dispensing in date " + new SimpleDateFormat("dd/MM/yyyy").format(dateUpdate.getTime()) + "...");
					payLaterPrescription++;
					if (dispense) {
						wardMan.fakeCloseMedicalPrescription(billID);
						System.out.println(" --> done!");
					} else System.out.println(" --> stop!");
				}
			}
		}
		System.out.println("Closed Prescriptions: " + payLaterPrescription);
		if (reopen) {
			try {
				Connection conn = DbSingleConn.getConnection();
				conn.createStatement().executeUpdate("UPDATE MEDICALDSRWARDPRESCRIPTION SET MWP_STATUS = 0 WHERE MWP_STATUS = 4");
				DbSingleConn.closeConnection();
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				pendingBills.clear();
			}
		}
	}
}
