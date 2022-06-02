package org.isf.accounting.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.isf.accounting.gui.PatientBillEdit.PatientBillListener;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillPayment;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.Session;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.PriceList;
import org.isf.stat.manager.GenericReportBill;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.stat.manager.GenericReportUserInDate;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.ModalJFrame;
import org.joda.time.DateTime;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;

/**
 * Browsing of table BILLS
 * 
 * @author Mwithi
 * 
 */
public class BillBrowser extends ModalJFrame implements PatientBillListener {

	public void billInserted(AWTEvent event) {
		totalToday = null;
		if (GeneralData.SINGLEUSER || MainMenu.checkUserGrants("cashiersfilter")) {
			updateDataSet(dateFrom, dateTo);
		} else {
			updateDataSet();
		}
		updateTables();
		updateTotalsPeriod();
		if (event != null) {
			Bill billInserted = (Bill) event.getSource();
			if (billInserted != null) {
				int insertedId = billInserted.getId();
				for (int i = 0; i < jTableBills.getRowCount(); i++) {
					Bill aBill = (Bill) jTableBills.getModel().getValueAt(i, -1);
					if (aBill.getId() == insertedId)
							jTableBills.getSelectionModel().setSelectionInterval(i, i);
				}
			}
		}
	}
	
	private static final long serialVersionUID = 1L;
	private JTabbedPane jTabbedPaneBills;
	private JTable jTableBills;
	private JScrollPane jScrollPaneBills;
	private JTable jTablePending;
	private JScrollPane jScrollPanePending;
	private JTable jTableClosed;
	private JScrollPane jScrollPaneClosed;
	private JTable jTableToday;
	private JTable jTablePeriod;
	private JTable jTableUser;
	private JPanel jPanelRange;
	private JPanel jPanelButtons;
	private JPanel jPanelSouth;
	private JPanel jPanelTotals;
	private JButton jButtonNew;
	private JButton jButtonEdit;
	private JButton jButtonPrintReceipt;
	private JButton jButtonDelete;
	private JButton jButtonClose;
	private JButton jButtonReport;
	private JButton jButtonClearCompany;
	private JComboBox jComboUsers;
	private JMonthChooser jComboBoxMonths;
	private JYearChooser jComboBoxYears;
	private JLabel jLabelTo;
	private JLabel jLabelFrom;
	private JDateChooser jCalendarTo;
	private JDateChooser jCalendarFrom;
	private GregorianCalendar dateFrom = new GregorianCalendar();
	private GregorianCalendar dateTo = new GregorianCalendar();
	private GregorianCalendar dateToday0 = new DateTime().toDateMidnight().toGregorianCalendar();
	private GregorianCalendar dateToday24 = new DateTime().toDateMidnight().plusDays(1).toGregorianCalendar();

	private JButton jButtonToday;
	private JButton jButtonRefresh;
	private JTextField jTextFieldSearchBill;
	private JTextField jTextFieldSearchPatient;
	private JLabel jLabelSearchBill;
	private JLabel jLabelSearchPatient;
	private JLabel jLabelSearchByOPD;
	private JTextField jTextFieldSearchByOPD;
	
//	private String status;
	private String[] columsNames = {"Cashier", "List",
			MessageBundle.getMessage("angal.billbrowser.id"),
			MessageBundle.getMessage("angal.billbrowser.date"), 
			MessageBundle.getMessage("angal.billbrowser.patientID"),
			MessageBundle.getMessage("angal.billbrowser.patient"), 
			MessageBundle.getMessage("angal.billbrowser.amount"), 
			MessageBundle.getMessage("angal.billbrowser.lastpayment"), 
			MessageBundle.getMessage("angal.billbrowser.status"), 
			MessageBundle.getMessage("angal.billbrowser.balance")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	private int[] columsWidth = {50, 100, 50, 150, 50, 50, 100, 150, 50, 100};
	private boolean[] columnsVisible = {MainMenu.checkUserGrants("cashiersfilter"), true, true, true, true, true, true, true, true, true };
	private int[] maxWidth = {50, 100, 150, 150, 150, 200, 100, 150, 50, 100};
	private boolean[] columsResizable = {false, false, false, false, false, true, false, false, false, false};
	private Class<?>[] columsClasses = {String.class, String.class, Integer.class, String.class, String.class, String.class, Double.class, String.class, String.class, Double.class};
	private boolean[] alignCenter = {true, false, true, true, true, false, false, true, true, false};
	private boolean[] boldCenter = {false, false, true, false, false, false, false, false, false, false};
	
//	private final int TabbedWidth = 800;
//	private final int TabbedHeight = 400;
//	private final int TotalWidth = TabbedWidth / 2;
//	private final int TotalHeight = 20;
	
	private BigDecimal totalToday;
	private BigDecimal balanceToday;
	private BigDecimal totalPeriod;
	private BigDecimal balancePeriod;
	private BigDecimal userToday;
	private BigDecimal userPeriod;
	private int month;
	private int year;
	
	//Bills & Payments
	private BillBrowserManager billManager = new BillBrowserManager();
	private ArrayList<Bill> billPeriod = new ArrayList<Bill>();
	private HashMap<Integer, Bill> mapBill = new HashMap<Integer, Bill>();
	private ArrayList<BillPayment> paymentsPeriod = new ArrayList<BillPayment>();
	private ArrayList<Bill> billFromPayments = new ArrayList<Bill>();
	
	//Users
	private String user = MainMenu.getUser();
	private ArrayList<String> users = billManager.getUsers();
	
	public BillBrowser() {
		initComponents();
		updateDataSet();
		updateTables();
		updateTotalsPeriod();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initComponents() {
		getContentPane().add(getJPanelRange(), BorderLayout.NORTH);
		getContentPane().add(getJTabbedPaneBills(), BorderLayout.CENTER);
		getContentPane().add(getJPanelSouth(), BorderLayout.SOUTH);
		setTitle(MessageBundle.getMessage("angal.billbrowser.title")); //$NON-NLS-1$
		setMinimumSize(new Dimension(800,500));
		addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent e) {
				//to free memory
				billPeriod.clear();
				paymentsPeriod.clear();
				billFromPayments.clear();
				mapBill.clear();
				users.clear();
				dispose();
			}			
		});
		pack();
	}

	private JPanel getJPanelSouth() {
		if (jPanelSouth == null) {
			jPanelSouth = new JPanel();
			jPanelSouth.setLayout(new BoxLayout(jPanelSouth, BoxLayout.X_AXIS));
			jPanelSouth.add(getJPanelTotals());
			jPanelSouth.add(getJPanelButtons());
		}
		return jPanelSouth;
	}

	private JPanel getJPanelTotals() {
		if (jPanelTotals == null) {
			jPanelTotals = new JPanel();
			jPanelTotals.setLayout(new BoxLayout(jPanelTotals, BoxLayout.Y_AXIS));
			jPanelTotals.add(getJTableToday());
			jPanelTotals.add(getJTablePeriod());
			if (!GeneralData.SINGLEUSER) jPanelTotals.add(getJTableUser());
		}
		return jPanelTotals;
	}

	private JLabel getJLabelTo() {
		if (jLabelTo == null) {
			jLabelTo = new JLabel();
			jLabelTo.setText(MessageBundle.getMessage("angal.billbrowser.to")); //$NON-NLS-1$
		}
		return jLabelTo;
	}

	private JDateChooser getJCalendarFrom() {
		if (jCalendarFrom == null) {
			dateFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
			dateFrom.set(GregorianCalendar.MINUTE, 0);
			dateFrom.set(GregorianCalendar.SECOND, 0);
			jCalendarFrom = new JDateChooser(dateFrom.getTime()); // Calendar
			jCalendarFrom.setLocale(new Locale(GeneralData.LANGUAGE));
			jCalendarFrom.setDateFormatString("dd/MM/yy"); //$NON-NLS-1$
			jCalendarFrom.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$

				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarFrom.setDate((Date) evt.getNewValue());
					dateFrom.setTime((Date) evt.getNewValue());
					dateFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
					dateFrom.set(GregorianCalendar.MINUTE, 0);
					dateFrom.set(GregorianCalendar.SECOND, 0);
					jButtonToday.setEnabled(true);
					updateDataSet(dateFrom, dateTo);
					updateTotalsPeriod();
					updateTables();
				}
			});
		}			
		return jCalendarFrom;
	}

	private JDateChooser getJCalendarTo() {
		if (jCalendarTo == null) {
			dateTo.set(GregorianCalendar.HOUR_OF_DAY, 23);
			dateTo.set(GregorianCalendar.MINUTE, 59);
			dateTo.set(GregorianCalendar.SECOND, 59);
			jCalendarTo = new JDateChooser(dateTo.getTime()); // Calendar
			jCalendarTo.setLocale(new Locale(GeneralData.LANGUAGE));
			jCalendarTo.setDateFormatString("dd/MM/yy"); //$NON-NLS-1$
			jCalendarTo.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$
				
				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarTo.setDate((Date) evt.getNewValue());
					dateTo.setTime((Date) evt.getNewValue());
					dateTo.set(GregorianCalendar.HOUR_OF_DAY, 23);
					dateTo.set(GregorianCalendar.MINUTE, 59);
					dateTo.set(GregorianCalendar.SECOND, 59);
					jButtonToday.setEnabled(true);
					updateDataSet(dateFrom, dateTo);
					updateTotalsPeriod();
					updateTables();
				}
			});
		}
		return jCalendarTo;
	}
	
	private JLabel getJLabelFrom() {
		if (jLabelFrom == null) {
			jLabelFrom = new JLabel();
			jLabelFrom.setText(MessageBundle.getMessage("angal.billbrowser.from")); //$NON-NLS-1$
		}
		return jLabelFrom;
	}
	
	private JButton getJButtonClearCompany() {
		if (jButtonClearCompany == null) {
			jButtonClearCompany = new JButton();
			jButtonClearCompany.setText(MessageBundle.getMessage("angal.billbrowser.clearcompany")); //$NON-NLS-1$
			jButtonClearCompany.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					Icon iconOpen = new ImageIcon("rsc/icons/open.png"); //$NON-NLS-1$
					Icon iconPatient = new ImageIcon("rsc/icons/patient_dialog.png"); //$NON-NLS-1$
					Icon iconOK = new ImageIcon("rsc/icons/ok_dialog.png"); //$NON-NLS-1$
					PriceList company;
					int month;
					int year;
					
					/*
					 * Ask Company and Period (Month - Year)
					 */
					JPanel closeCompanyPanel = new JPanel();
					closeCompanyPanel.setLayout(new BorderLayout());
					
					JComboBox companyComboBox = new JComboBox();
					PriceListManager listMan = new PriceListManager();
					ArrayList<PriceList> companyList = listMan.getLists();
					Collections.sort(companyList, PriceList.NameComparator);
					for (PriceList list : companyList) {
						companyComboBox.addItem(list);
					}
					
					JPanel monthYearPanel = new JPanel();
					monthYearPanel.setLayout(new BoxLayout(monthYearPanel, BoxLayout.X_AXIS));
					JMonthChooser monthChooser = new JMonthChooser();
					monthChooser.setLocale(new Locale(GeneralData.LANGUAGE));
					JYearChooser yearChooser = new JYearChooser();
					yearChooser.setLocale(new Locale(GeneralData.LANGUAGE));
					monthYearPanel.add(monthChooser);
					monthYearPanel.add(yearChooser);
					
					closeCompanyPanel.add(companyComboBox, BorderLayout.NORTH);
					closeCompanyPanel.add(monthYearPanel, BorderLayout.SOUTH);
					
			        int r = JOptionPane.showConfirmDialog(BillBrowser.this, 
			        		closeCompanyPanel, 
			        		MessageBundle.getMessage("angal.billbrowser.clearcompany"), 
			        		JOptionPane.OK_CANCEL_OPTION, 
			        		JOptionPane.PLAIN_MESSAGE,
			        		iconOpen);

			        if (r == JOptionPane.OK_OPTION) {
			        	company = (PriceList) companyComboBox.getSelectedItem();
			        	month = monthChooser.getMonth();
			        	year = yearChooser.getYear();
			        } 
			        else return;
			        
					GregorianCalendar thisMonthFrom = (GregorianCalendar) dateFrom.clone();
					GregorianCalendar thisMonthTo = (GregorianCalendar) dateTo.clone();
					thisMonthFrom.set(GregorianCalendar.YEAR, year);
					thisMonthFrom.set(GregorianCalendar.MONTH, month);
					thisMonthFrom.set(GregorianCalendar.DAY_OF_MONTH, 1);
					thisMonthTo.set(GregorianCalendar.YEAR, year);
					thisMonthTo.set(GregorianCalendar.MONTH, month);
					thisMonthTo.set(GregorianCalendar.DAY_OF_MONTH, thisMonthFrom.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
					
					ArrayList<Bill> pendingBills = new ArrayList<Bill>();
					try {
						BusyState.setBusyState(BillBrowser.this, true);
						pendingBills = billManager.getPendingBills(company, thisMonthFrom, thisMonthTo);
					} finally {
						BusyState.setBusyState(BillBrowser.this, false);
					}	
					
					if (pendingBills.isEmpty()) {
						JOptionPane.showMessageDialog(BillBrowser.this, 
								MessageBundle.getMessage("angal.billbrowser.nopendingbillsfound"));
						return;
					}
					
					/*
					 * Confirm closing Company pending Bills with Date
					 */
					JDateChooser jDatePayment = new JDateChooser(new Date());
					jDatePayment.setLocale(new Locale(GeneralData.LANGUAGE));
					
					//TOTALS (on the top)
					JPanel totalsPanel = new JPanel(new BorderLayout());
					CompanyTotalTableModel totalModel = new CompanyTotalTableModel();
					JTable totalsTable = new JTable(totalModel);
					totalsTable.setRowSelectionAllowed(false);
					totalsTable.setGridColor(Color.WHITE);
					totalsPanel.add(totalsTable);
					
					//BILLS LIST
					CompanyBillTableModel companyModel = new CompanyBillTableModel(pendingBills);
					companyModel.addCompanyBillListener(totalModel);
					JTable companyPendingBills = new JTable(companyModel);
					companyPendingBills.getColumnModel().getColumn(0).setMaxWidth(40);
					companyPendingBills.getColumnModel().getColumn(1).setMinWidth(200);
					companyPendingBills.setAutoCreateColumnsFromModel(true);
					companyPendingBills.setGridColor(Color.WHITE);
					JScrollPane jScrollList = new JScrollPane(companyPendingBills);
					
					//CONTROL PANEL
					JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					JCheckBox selectAllCheckBox = new JCheckBox("Select All");
					selectAllCheckBox.addActionListener(new SelectAllActionListener(companyModel));
					controlPanel.add(selectAllCheckBox);
					
					JPanel listPanel = new JPanel(new BorderLayout());
					listPanel.add(totalsPanel, BorderLayout.NORTH);
					listPanel.add(jScrollList, BorderLayout.CENTER);
					listPanel.add(controlPanel, BorderLayout.SOUTH);
					
					JPanel datePanel = new JPanel(new BorderLayout());
					datePanel.add(new JLabel("<html><b>Date of clearing:</b></html>"), BorderLayout.NORTH);
					datePanel.add(jDatePayment, BorderLayout.SOUTH);
					
					closeCompanyPanel.removeAll();
					closeCompanyPanel.add(listPanel, BorderLayout.CENTER);
					closeCompanyPanel.add(datePanel, BorderLayout.SOUTH);
					
					r = JOptionPane.showConfirmDialog(BillBrowser.this, 
							closeCompanyPanel, 
			        		company.getName(), 
			        		JOptionPane.OK_CANCEL_OPTION, 
			        		JOptionPane.PLAIN_MESSAGE,
			        		iconPatient);

			        if (r == JOptionPane.OK_OPTION) {
			        	
			        	r = JOptionPane.showConfirmDialog(BillBrowser.this, 
			        			MessageBundle.getMessage("angal.billbrowser.doyoureallywanttoproceed"), 
			        			MessageBundle.getMessage("angal.billbrowser.confirm"), 
			        			JOptionPane.OK_CANCEL_OPTION, 
			        			JOptionPane.WARNING_MESSAGE);
			        	
			        	if (r == JOptionPane.OK_OPTION) {
			        		try {
			        			BusyState.setBusyState(BillBrowser.this, true);
			        			
			        			ArrayList<Bill> updatedBills = new ArrayList<Bill>();
			        			Boolean[] selectedBills = companyModel.getSelected();
				        		String user = MainMenu.getUser();
				        		GregorianCalendar date = new GregorianCalendar();
			        			date.setTime(jDatePayment.getDate());
				        		for (int i=0; i < pendingBills.size(); i++) {
				        			Bill bill = pendingBills.get(i);
				        			boolean billSelected = selectedBills[i];
				        			if (billSelected) {
					        			int billID = bill.getId();
					        			ArrayList<BillPayment> payments = new ArrayList<BillPayment>();
					        			if (bill.getBalance() != 0) {
					        				BillPayment payment = new BillPayment(0,
					        						billID,
					        						date,
					        						bill.getBalance(),
					        						user);
					        				payments.add(payment);
					        				if (billManager.newBillPayments(billID, payments)) {
					        					bill.setStatus("C");
					        					bill.setUser(user);
					        					bill.setUpdate(date);
					        					bill.setBalance(new Double(0));
					        					if (billManager.updateBill(bill)) {
					        						updatedBills.add(bill);
					        					}
					        				}
					        			}
				        			}
				        		}
				        		
				        		String[] closedBillsList = new String[updatedBills.size()];
								for (int i = 0; i < updatedBills.size(); i++) {
									Bill bill = updatedBills.get(i);
									closedBillsList[i] = toHtmlTable(bill);
								}
				        		
				        		JOptionPane.showMessageDialog(BillBrowser.this, 
				        				new JScrollPane(new JList(closedBillsList)),
				        				MessageBundle.getMessage("angal.billbrowser.closed"),
				        				JOptionPane.INFORMATION_MESSAGE,
				        				iconOK);
				        		
				        		updateDataSet(dateFrom, dateTo);
								updateTotalsPeriod();
								updateTables();
			        		} finally {
			        			BusyState.setBusyState(BillBrowser.this, false);
			        		}
			        	} 
			        	else return;
			        } 
			        else return;
				}
			});
		}
		
		return jButtonClearCompany;
	}
	
	private JButton getJButtonReport() {
		if (jButtonReport == null) {
			jButtonReport = new JButton();
			jButtonReport.setText(MessageBundle.getMessage("angal.billbrowser.report")); //$NON-NLS-1$
			jButtonReport.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					
					ArrayList<String> options = new ArrayList<String>();
					options.add(MessageBundle.getMessage("angal.billbrowser.todayclosure") + " New!");
					options.add(MessageBundle.getMessage("angal.billbrowser.todayclosure"));
					options.add(MessageBundle.getMessage("angal.billbrowser.today"));
					options.add(MessageBundle.getMessage("angal.billbrowser.thismonth"));
					options.add(MessageBundle.getMessage("angal.billbrowser.othermonth"));
					options.add(MessageBundle.getMessage("angal.billbrowser.otherperiod"));
					
					Icon icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$
					String option = (String) JOptionPane.showInputDialog(BillBrowser.this, 
							MessageBundle.getMessage("angal.billbrowser.pleaseselectareport"), 
							MessageBundle.getMessage("angal.billbrowser.report"), 
							JOptionPane.INFORMATION_MESSAGE, 
							icon, 
							options.toArray(), 
							options.get(0));
					
					if (option == null) return;
					
					String from = null;
					String to = null;
					
					int i = 0;
					
					if (options.indexOf(option) == i) {
						UserBrowsingManager sessionManager = new UserBrowsingManager();
						ArrayList<Session> sessions;
						String user = MainMenu.getUser();
						if (MainMenu.checkUserGrants("cashiersfilter")) {
							user = (String) jComboUsers.getSelectedItem();
						}
						if (user.equals("ALL")) {
							sessions = sessionManager.getSessionByUser(80);
						} else {
							sessions = sessionManager.getSessionByUser(user, 40);
						}
						
						JList jList = new JList(sessions.toArray());
						jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
						jList.setFont(new Font("Arial", Font.PLAIN, 20));
						JScrollPane scrollPane = new JScrollPane(jList);
						int r = JOptionPane.showConfirmDialog(
			                    BillBrowser.this,
			                    scrollPane,
			                    "Session",
			                    JOptionPane.OK_CANCEL_OPTION, 
				        		JOptionPane.PLAIN_MESSAGE);
						
						if (r != JOptionPane.OK_OPTION) return;

						int[] indexes = jList.getSelectedIndices();
						GregorianCalendar login = ((Session) jList.getModel().getElementAt(indexes[indexes.length - 1])).getLogin();
						GregorianCalendar logout = ((Session) jList.getModel().getElementAt(indexes[0])).getLogout();
						if (logout == null) logout = new GregorianCalendar();
						
						from = formatDateTimeReport(login);
						to = formatDateTimeReport(logout);

						//For testing
						//from = "31/12/2018 14:41:03";
						//to = "30/04/2019 15:19:18";
						
						//System.out.println("User: " + selectedSession.getUser());
						//System.out.println("From: " + from);
						//System.out.println("To: " + to);
						try {
							BusyState.setBusyState(BillBrowser.this, true);
							if (user.equals("ALL")) {
								new GenericReportUserInDate(from, to, "BillsReportUserInDate2", false);
							} else {
								new GenericReportUserInDate(from, to, user, "BillsReportUserInDate2", false);
							} 
						} finally {
							BusyState.setBusyState(BillBrowser.this, false);
						}
						return;
					}
					if (options.indexOf(option) == ++i) {
						UserBrowsingManager sessionManager = new UserBrowsingManager();
						ArrayList<Session> sessions;
						String user = MainMenu.getUser();
						if (MainMenu.checkUserGrants("cashiersfilter")) {
							user = (String) jComboUsers.getSelectedItem();
						}
						if (user.equals("ALL")) {
							sessions = sessionManager.getSessionByUser(80);
						} else {
							sessions = sessionManager.getSessionByUser(user, 40);
						}
						
						JList jList = new JList(sessions.toArray());
						jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
						jList.setFont(new Font("Arial", Font.PLAIN, 20));
						JScrollPane scrollPane = new JScrollPane(jList);
						int r = JOptionPane.showConfirmDialog(
			                    BillBrowser.this,
			                    scrollPane,
			                    "Session",
			                    JOptionPane.OK_CANCEL_OPTION, 
				        		JOptionPane.PLAIN_MESSAGE);
						
						if (r != JOptionPane.OK_OPTION) return;

						int[] indexes = jList.getSelectedIndices();
						GregorianCalendar login = ((Session) jList.getModel().getElementAt(indexes[indexes.length - 1])).getLogin();
						GregorianCalendar logout = ((Session) jList.getModel().getElementAt(indexes[0])).getLogout();
						if (logout == null) logout = new GregorianCalendar();
						
						from = formatDateTimeReport(login);
						to = formatDateTimeReport(logout);
						
						//System.out.println("User: " + selectedSession.getUser());
						//System.out.println("From: " + from);
						//System.out.println("To: " + to);
						try {
							BusyState.setBusyState(BillBrowser.this, true);
							if (user.equals("ALL")) {
								new GenericReportUserInDate(from, to, "BillsReportUserInDate", false);
							} else {
								new GenericReportUserInDate(from, to, user, "BillsReportUserInDate", false);
							} 
						} finally {
							BusyState.setBusyState(BillBrowser.this, false);
						}
						return;
					}
					if (options.indexOf(option) == ++i) {
						
						from = formatDateTimeReport(dateToday0);
						to = formatDateTimeReport(dateToday24);
					}
					if (options.indexOf(option) == ++i) {
						
						month = jComboBoxMonths.getMonth();
						GregorianCalendar thisMonthFrom = dateFrom;
						GregorianCalendar thisMonthTo = dateTo;
						thisMonthFrom.set(GregorianCalendar.MONTH, month);
						thisMonthFrom.set(GregorianCalendar.DAY_OF_MONTH, 1);
						thisMonthTo.set(GregorianCalendar.MONTH, month);
						thisMonthTo.set(GregorianCalendar.DAY_OF_MONTH, dateFrom.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
						from = formatDateTimeReport(thisMonthFrom);
						to = formatDateTimeReport(thisMonthTo);
					}
					if (options.indexOf(option) == ++i) {
						
						icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$
						
						int month;
						int year;
						JPanel monthYearPanel = new JPanel();
						JMonthChooser monthChooser = new JMonthChooser();
						monthChooser.setLocale(new Locale(GeneralData.LANGUAGE));
						JYearChooser yearChooser = new JYearChooser();
						yearChooser.setLocale(new Locale(GeneralData.LANGUAGE));
						monthYearPanel.add(monthChooser);
						monthYearPanel.add(yearChooser);
						
				        int r = JOptionPane.showConfirmDialog(BillBrowser.this, 
				        		monthYearPanel, 
				        		MessageBundle.getMessage("angal.billbrowser.month"), 
				        		JOptionPane.OK_CANCEL_OPTION, 
				        		JOptionPane.PLAIN_MESSAGE,
				        		icon);

				        if (r == JOptionPane.OK_OPTION) {
				        	month = monthChooser.getMonth();
				        	year = yearChooser.getYear();
				        } else {
				            return;
				        }
				        
						GregorianCalendar thisMonthFrom = dateFrom;
						GregorianCalendar thisMonthTo = dateTo;
						thisMonthFrom.set(GregorianCalendar.YEAR, year);
						thisMonthFrom.set(GregorianCalendar.MONTH, month);
						thisMonthFrom.set(GregorianCalendar.DAY_OF_MONTH, 1);
						thisMonthTo.set(GregorianCalendar.YEAR, year);
						thisMonthTo.set(GregorianCalendar.MONTH, month);
						thisMonthTo.set(GregorianCalendar.DAY_OF_MONTH, dateFrom.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
						from = formatDateTimeReport(thisMonthFrom);
						to = formatDateTimeReport(thisMonthTo);
					}
					if (options.indexOf(option) == ++i) {
						
						GregorianCalendar thisDateFrom = new GregorianCalendar();
						GregorianCalendar thisDateTo = new GregorianCalendar();
						
						JPanel periodPanel = new JPanel();
						JDateChooser fromDateChooser = new JDateChooser(dateFrom.getTime());
						JDateChooser toDateChooser = new JDateChooser(dateTo.getTime());
						periodPanel.add(fromDateChooser);
						periodPanel.add(toDateChooser);
						
						int r = JOptionPane.showConfirmDialog(BillBrowser.this, 
								periodPanel, 
				        		MessageBundle.getMessage("angal.billbrowser.period"), 
				        		JOptionPane.OK_CANCEL_OPTION, 
				        		JOptionPane.PLAIN_MESSAGE,
				        		icon);

				        if (r == JOptionPane.OK_OPTION) {
				        	thisDateFrom.setTime(fromDateChooser.getDate());
				        	thisDateTo.setTime(toDateChooser.getDate());
				        } else {
				            return;
				        }
						
				        from = formatDateTimeReport(thisDateFrom);
						to = formatDateTimeReport(thisDateTo);
					}

					options = new ArrayList<String>();
					options.add(MessageBundle.getMessage("angal.billbrowser.shortreportonlybaddebts"));
					options.add(MessageBundle.getMessage("angal.billbrowser.fullreportallbills"));
										
					icon = new ImageIcon("rsc/icons/list_dialog.png"); //$NON-NLS-1$
					option = (String) JOptionPane.showInputDialog(BillBrowser.this, 
							MessageBundle.getMessage("angal.billbrowser.pleaseselectareport"), 
							MessageBundle.getMessage("angal.billbrowser.report"), 
							JOptionPane.INFORMATION_MESSAGE, 
							icon, 
							options.toArray(), 
							options.get(0));
					
					if (option == null) return;
					
					if (options.indexOf(option) == 0) {
						try {
							BusyState.setBusyState(BillBrowser.this, true);
							new GenericReportFromDateToDate(from, to, GeneralData.BILLSREPORT, GeneralData.BILLSREPORT, false);
						} finally {
							BusyState.setBusyState(BillBrowser.this, false);
						}
					}
					if (options.indexOf(option) == 1) {
						try {
							BusyState.setBusyState(BillBrowser.this, true);
							new GenericReportFromDateToDate(from, to, GeneralData.BILLSREPORT, GeneralData.BILLSREPORT, false);
						} finally {
							BusyState.setBusyState(BillBrowser.this, false);
						}
					}
				}
			});
		}
		return jButtonReport;
	}
	
	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText(MessageBundle.getMessage("angal.billbrowser.close")); //$NON-NLS-1$
			jButtonClose.setMnemonic(KeyEvent.VK_C);
			jButtonClose.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					//to free memory
					billPeriod.clear();
					paymentsPeriod.clear();
					billFromPayments.clear();
					mapBill.clear();
					users.clear();
					dispose();
				}
			});
		}
		return jButtonClose;
	}

	private JButton getJButtonEdit() {
		if (jButtonEdit == null) {
			jButtonEdit = new JButton();
			jButtonEdit.setText(MessageBundle.getMessage("angal.billbrowser.editbill")); //$NON-NLS-1$
			jButtonEdit.setMnemonic(KeyEvent.VK_E);
			jButtonEdit.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (jScrollPaneBills.isShowing()) {
						int rowSelected = jTableBills.getSelectedRow();
						if (rowSelected == -1) {
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.billbrowser.pleaseselectabillfirst"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.billbrowser.title"), //$NON-NLS-1$
									JOptionPane.PLAIN_MESSAGE);
							return;
						}
						Bill editBill = (Bill)jTableBills.getValueAt(rowSelected, -1);
						String status = editBill.getStatus();
						if (MainMenu.checkUserGrants("editclosedbills") 
								|| status.equals("O") 
								|| status.equals("L")
								|| status.equals("X")) { //$NON-NLS-1$
							PatientBillEdit pbe = new PatientBillEdit(BillBrowser.this, editBill, null, false);
							pbe.addPatientBillListener(BillBrowser.this);
							pbe.setVisible(true);
						} else {
							/*JOptionPane.showMessageDialog(BillBrowser.this,
									MessageBundle.getMessage("angal.billbrowser.billalreadyclosed"),  //$NON-NLS-1$
									MessageBundle.getMessage("angal.hospital"),  //$NON-NLS-1$
									JOptionPane.CANCEL_OPTION);*/
							new GenericReportBill(editBill.getId(), GeneralData.PATIENTBILL);
						}
					}
					
					if (jScrollPanePending != null && jScrollPanePending.isShowing()) {
						int rowSelected = jTablePending.getSelectedRow();
						Bill editBill = (Bill)jTablePending.getValueAt(rowSelected, -1);
						PatientBillEdit pbe = new PatientBillEdit(BillBrowser.this, editBill, null, false);
						pbe.addPatientBillListener(BillBrowser.this);
						pbe.setVisible(true);
					}
					if (jScrollPaneClosed != null && jScrollPaneClosed.isShowing()) {
						int rowSelected = jTableClosed.getSelectedRow();
						Bill editBill = (Bill)jTableClosed.getValueAt(rowSelected, -1);
						new GenericReportBill(editBill.getId(), GeneralData.PATIENTBILL);
					}
					
				}
			});
		}
		return jButtonEdit;
	}
	
	private JButton getJButtonPrintReceipt() {
		if (jButtonPrintReceipt == null) {
			jButtonPrintReceipt = new JButton();
			jButtonPrintReceipt.setText(MessageBundle.getMessage("angal.billbrowser.receipt")); //$NON-NLS-1$
			jButtonPrintReceipt.setMnemonic(KeyEvent.VK_R);
			jButtonPrintReceipt.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						if (jScrollPaneBills.isShowing()) {
							int rowSelected = jTableBills.getSelectedRow();
							Bill selectedBill = (Bill)jTableBills.getValueAt(rowSelected, -1);
							String status = selectedBill.getStatus();
							if (status.equals("C")) { //$NON-NLS-1$
								new GenericReportBill(selectedBill.getId(), GeneralData.PATIENTBILL, false, true);
							} else {
								if (status.equals("D")) {
									JOptionPane.showMessageDialog(BillBrowser.this,
											"Bill Deleted",  //$NON-NLS-1$
											MessageBundle.getMessage("angal.hospital"),  //$NON-NLS-1$
											JOptionPane.CANCEL_OPTION);
									
								} else {
									JOptionPane.showMessageDialog(BillBrowser.this,
										"Bill not yet closed",  //$NON-NLS-1$
										MessageBundle.getMessage("angal.hospital"),  //$NON-NLS-1$
										JOptionPane.CANCEL_OPTION);
								}
							}
						}
						if (jScrollPanePending != null && jScrollPanePending.isShowing()) {
							int rowSelected = jTablePending.getSelectedRow();
							Bill editBill = (Bill)jTablePending.getValueAt(rowSelected, -1);
							PatientBillEdit pbe = new PatientBillEdit(BillBrowser.this, editBill, null, false);
							pbe.addPatientBillListener(BillBrowser.this);
							pbe.setVisible(true);
						}
						if (jScrollPaneClosed != null && jScrollPaneClosed.isShowing()) {
							int rowSelected = jTableClosed.getSelectedRow();
							Bill editBill = (Bill)jTableClosed.getValueAt(rowSelected, -1);
							new GenericReportBill(editBill.getId(), GeneralData.PATIENTBILL);
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.billbrowser.pleaseselectabillfirst"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.billbrowser.title"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE);
					}
				}
			});
		}
		return jButtonPrintReceipt;
	}

	private JButton getJButtonNew() {
		if (jButtonNew == null) {
			jButtonNew = new JButton();
			jButtonNew.setText(MessageBundle.getMessage("angal.billbrowser.newbill")); //$NON-NLS-1$
			jButtonNew.setMnemonic(KeyEvent.VK_N);
			jButtonNew.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					PatientBillEdit newBill = new PatientBillEdit(BillBrowser.this, new Bill(), null, true);
					newBill.addPatientBillListener(BillBrowser.this);
					newBill.setVisible(true);
				}
				
			});
		}
		return jButtonNew;
	}
	
	private JButton getJButtonDelete() {
		if (jButtonDelete == null) {
			jButtonDelete = new JButton();
			jButtonDelete.setText(MessageBundle.getMessage("angal.billbrowser.deletebill")); //$NON-NLS-1$
			jButtonDelete.setMnemonic(KeyEvent.VK_D);
			jButtonDelete.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						Bill deleteBill = null;
						int ok = JOptionPane.NO_OPTION;
						if (jScrollPaneBills.isShowing()) {
							int rowSelected = jTableBills.getSelectedRow();
							deleteBill = (Bill)jTableBills.getValueAt(rowSelected, -1);
							ok = JOptionPane.showConfirmDialog(null, 
									MessageBundle.getMessage("angal.billbrowser.doyoureallywanttodeletetheselectedbill"),  //$NON-NLS-1$
									MessageBundle.getMessage("angal.billbrowser.delete"), //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION);
							
						}
						if (jScrollPanePending != null && jScrollPanePending.isShowing()) {
							int rowSelected = jTablePending.getSelectedRow();
							deleteBill = (Bill)jTablePending.getValueAt(rowSelected, -1);
							ok = JOptionPane.showConfirmDialog(null, 
									MessageBundle.getMessage("angal.billbrowser.doyoureallywanttodeletetheselectedbill"),  //$NON-NLS-1$
									MessageBundle.getMessage("angal.billbrowser.delete"), //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION);
						}
						if (jScrollPaneClosed != null && jScrollPaneClosed.isShowing()) {
							int rowSelected = jTableClosed.getSelectedRow();
							deleteBill = (Bill)jTableClosed.getValueAt(rowSelected, -1);
							ok = JOptionPane.showConfirmDialog(null, 
									MessageBundle.getMessage("angal.billbrowser.doyoureallywanttodeletetheselectedbill"),  //$NON-NLS-1$
									MessageBundle.getMessage("angal.billbrowser.delete"), //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION);
						}
						if (ok == JOptionPane.YES_OPTION) {
							boolean deleted = billManager.deleteBill(deleteBill);
							if (deleted) {
								MovWardBrowserManager wardMan = new MovWardBrowserManager();
								wardMan.deleteMedicalPrescription(deleteBill.getId());
							}
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.billbrowser.pleaseselectabillfirst"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE);
					}
					billInserted(null);
				}
			});
		}
		return jButtonDelete;
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.setLayout(new BoxLayout(jPanelButtons, BoxLayout.Y_AXIS));
			
			JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if (MainMenu.checkUserGrants("btnbillnew")) firstRow.add(getJButtonNew());
			if (MainMenu.checkUserGrants("btnbilledit")) firstRow.add(getJButtonEdit());
			if (MainMenu.checkUserGrants("btnbilldelete")) firstRow.add(getJButtonDelete());
			if (MainMenu.checkUserGrants("btnbillreceipt") && GeneralData.RECEIPTPRINTER) firstRow.add(getJButtonPrintReceipt());
			if (MainMenu.checkUserGrants("btnbillreport")) firstRow.add(getJButtonReport());
			firstRow.add(getJButtonClose());
			
			JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if (MainMenu.checkUserGrants("btnbillclearcompany")) secondRow.add(getJButtonClearCompany());
			
			jPanelButtons.add(firstRow);
			jPanelButtons.add(secondRow);
		}
		return jPanelButtons;
	}

	private JPanel getJPanelRange() {
		if (jPanelRange == null) {
			jPanelRange = new JPanel();
			if (GeneralData.SINGLEUSER) {
				jPanelRange.add(getJComboUsers());
				jPanelRange.add(getJButtonToday());
				jPanelRange.add(getJLabelFrom());
				jPanelRange.add(getJCalendarFrom());
				jPanelRange.add(getJLabelTo());
				jPanelRange.add(getJCalendarTo());
				jPanelRange.add(getJComboMonths());
				jPanelRange.add(getJComboYears());
				jPanelRange.add(getJLabelSearchBill());
				jPanelRange.add(getJTextFieldSearchBill());
				jPanelRange.add(getJLabelSearchPatient());
				jPanelRange.add(getJTextFieldSearchPatient());
				jPanelRange.add(getJLabelSearchByOPD());
				jPanelRange.add(getJTextFieldSearchByOPD());
			} else if (MainMenu.checkUserGrants("cashiersfilter")) {
				jPanelRange.add(getJComboUsers());
				jPanelRange.add(getJButtonToday());
				jPanelRange.add(getJLabelFrom());
				jPanelRange.add(getJCalendarFrom());
				jPanelRange.add(getJLabelTo());
				jPanelRange.add(getJCalendarTo());
				jPanelRange.add(getJComboMonths());
				jPanelRange.add(getJComboYears());
				jPanelRange.add(getJLabelSearchBill());
				jPanelRange.add(getJTextFieldSearchBill());
				jPanelRange.add(getJLabelSearchPatient());
				jPanelRange.add(getJTextFieldSearchPatient());
				jPanelRange.add(getJLabelSearchByOPD());
				jPanelRange.add(getJTextFieldSearchByOPD());
			} else {
				jPanelRange.setLayout(new FlowLayout(FlowLayout.RIGHT));
				jPanelRange.add(getJLabelSearchBill());
				jPanelRange.add(getJTextFieldSearchBill());
				jPanelRange.add(getJLabelSearchPatient());
				jPanelRange.add(getJTextFieldSearchPatient());
				jPanelRange.add(getJLabelSearchByOPD());
				jPanelRange.add(getJTextFieldSearchByOPD());
				jPanelRange.add(getJButtonRefresh());
			}
		}
		return jPanelRange;
	}
	
	private JLabel getJLabelSearchBill() {
		if (jLabelSearchBill == null) {
			jLabelSearchBill = new JLabel("Search Bill");
		}
		return jLabelSearchBill;
	}
	
	private JLabel getJLabelSearchByOPD() {
		if (jLabelSearchByOPD == null) {
			jLabelSearchByOPD = new JLabel("OPD");
		}
		return jLabelSearchByOPD;
	}
	
	private JTextField getJTextFieldSearchByOPD() {
		if (jTextFieldSearchByOPD == null) {
			jTextFieldSearchByOPD = new JTextField(10);
			jTextFieldSearchByOPD.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {}
				
				@Override
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				    if (key == KeyEvent.VK_ENTER) {
				    	String pcode = jTextFieldSearchByOPD.getText();
						jTableBills.setModel(new BillTableModel("ALL", 0, pcode));
				    }
				}
			});
		}
		return jTextFieldSearchByOPD;
	}
	
	private JLabel getJLabelSearchPatient() {
		if (jLabelSearchPatient == null) {
			jLabelSearchPatient = new JLabel("Search Patient");
		}
		return jLabelSearchPatient;
	}
	
	private JTextField getJTextFieldSearchPatient() {
		if (jTextFieldSearchPatient == null) {
			jTextFieldSearchPatient = new JTextField(10);
			jTextFieldSearchPatient.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {}
				
				@Override
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				    if (key == KeyEvent.VK_ENTER) {
				    	String code = jTextFieldSearchPatient.getText();
				    	try {
							int patID = Integer.parseInt(code);
							jTableBills.setModel(new BillTableModel("ALL", 0, patID));
						} catch (NumberFormatException e1) {}
				    }
				}
			});
		}
		return jTextFieldSearchPatient;
	}
	
	private JTextField getJTextFieldSearchBill() {
		if (jTextFieldSearchBill == null) {
			jTextFieldSearchBill = new JTextField(10);
			jTextFieldSearchBill.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {}
				
				@Override
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				    if (key == KeyEvent.VK_ENTER) {
				    	String code = jTextFieldSearchBill.getText();
				    	try {
							int billID = Integer.parseInt(code);
							jTableBills.setModel(new BillTableModel("ALL", billID, 0));
						} catch (NumberFormatException e1) {}
				    }
				}
			});
		}
		return jTextFieldSearchBill;
	}
	
	private JButton getJButtonRefresh() {
		if (jButtonRefresh == null) {
			jButtonRefresh = new JButton("Refresh");
			jButtonRefresh.setIcon(new ImageIcon("rsc/icons/clock_button.png"));
			jButtonRefresh.setMnemonic(KeyEvent.VK_F5);
			jButtonRefresh.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					jTextFieldSearchBill.setText("");
					jTextFieldSearchPatient.setText("");
					totalToday = null;
					updateDataSet();
					updateTables();
				}
			});
		}
		return jButtonRefresh;
	}

	private JComboBox getJComboUsers() {
		if (jComboUsers == null) {
			jComboUsers = new JComboBox();
			jComboUsers.addItem("ALL");
			for (String user : users) {
				
				jComboUsers.addItem(user);
			}
			
			jComboUsers.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					user = (String) jComboUsers.getSelectedItem();
					jTableUser.setValueAt("<html><b>"+user+" today</b></html>", 0, 0);
					jTableUser.setValueAt("<html><b>"+user+" period</b></html>", 0, 2);
					updateTotalsPeriod();
					if (user.equals("ALL"))
						updateTables();
					else updateTables(user);
					
				}
			});
		}
		return jComboUsers;
	}
	
	private JButton getJButtonToday() {
		if (jButtonToday == null) {
			jButtonToday = new JButton();
			jButtonToday.setText(MessageBundle.getMessage("angal.billbrowser.today")); //$NON-NLS-1$
			jButtonToday.setMnemonic(KeyEvent.VK_T);
			jButtonToday.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					dateFrom.setTime(dateToday0.getTime());
					dateTo.setTime(dateToday24.getTime());
					
					jCalendarFrom.setDate(dateFrom.getTime());
					jCalendarTo.setDate(dateTo.getTime());
					
					jButtonToday.setEnabled(false);
				}
			});
			jButtonToday.setEnabled(false);
		}
		return jButtonToday;
	}

	private JMonthChooser getJComboMonths() {
		if (jComboBoxMonths == null) {
			jComboBoxMonths = new JMonthChooser();
			jComboBoxMonths.setLocale(new Locale(GeneralData.LANGUAGE));
			jComboBoxMonths.addPropertyChangeListener("month", new PropertyChangeListener() { //$NON-NLS-1$

				public void propertyChange(PropertyChangeEvent evt) {
					month = jComboBoxMonths.getMonth();
					dateFrom.set(GregorianCalendar.MONTH, month);
					dateFrom.set(GregorianCalendar.DAY_OF_MONTH, 1);
					dateTo.set(GregorianCalendar.MONTH, month);
					dateTo.set(GregorianCalendar.DAY_OF_MONTH, dateFrom.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
					
					jCalendarFrom.setDate(dateFrom.getTime());
					jCalendarTo.setDate(dateTo.getTime());
				}
			});
		}
		return jComboBoxMonths;
	}

	private JYearChooser getJComboYears() {
		if (jComboBoxYears == null) {
			jComboBoxYears = new JYearChooser();
			jComboBoxYears.setLocale(new Locale(GeneralData.LANGUAGE));
			jComboBoxYears.addPropertyChangeListener("year", new PropertyChangeListener() { //$NON-NLS-1$

				public void propertyChange(PropertyChangeEvent evt) {
					year = jComboBoxYears.getYear();
					dateFrom.set(GregorianCalendar.YEAR, year);
					dateFrom.set(GregorianCalendar.MONTH, 1);
					dateFrom.set(GregorianCalendar.DAY_OF_YEAR, 1);
					dateTo.set(GregorianCalendar.YEAR, year);
					dateTo.set(GregorianCalendar.MONTH, 12);
					dateTo.set(GregorianCalendar.DAY_OF_YEAR, dateFrom.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
					jCalendarFrom.setDate(dateFrom.getTime());
					jCalendarTo.setDate(dateTo.getTime());
				}
			});
		}
		return jComboBoxYears;
	}

	private JScrollPane getJScrollPaneClosed() {
		if (jScrollPaneClosed == null) {
			jScrollPaneClosed = new JScrollPane();
			jScrollPaneClosed.setViewportView(getJTableClosed());
		}
		return jScrollPaneClosed;
	}

	private JTable getJTableClosed() {
		if (jTableClosed == null) {
			jTableClosed = new JTable();
			BillTableModel model = new BillTableModel("C");
			jTableClosed.setModel(model); //$NON-NLS-1$
			for (int i=0;i<model.getColumnCount(); i++){
				jTableClosed.getColumnModel().getColumn(i).setMinWidth(columsWidth[i]);
				if (!columsResizable[i]) jTableClosed.getColumnModel().getColumn(i).setMaxWidth(maxWidth[i]);
				if (!columnsVisible[i]) {
					jTableClosed.getColumnModel().getColumn(i).setMinWidth(0);
					jTableClosed.getColumnModel().getColumn(i).setMaxWidth(0);
					jTableClosed.getColumnModel().getColumn(i).setWidth(0);
				}
				if (alignCenter[i]) {
					jTableClosed.getColumnModel().getColumn(i).setCellRenderer(new StringCenterTableCellRenderer());
					if (boldCenter[i]) {
						jTableClosed.getColumnModel().getColumn(i).setCellRenderer(new CenterBoldTableCellRenderer());
					}
				}
			}
			jTableClosed.setAutoCreateColumnsFromModel(false);
			jTableClosed.setDefaultRenderer(String.class, new StringTableCellRenderer());
			jTableClosed.setDefaultRenderer(Integer.class, new IntegerTableCellRenderer());
			jTableClosed.setDefaultRenderer(Double.class, new DoubleTableCellRenderer());
		}
		return jTableClosed;
	}

	private JScrollPane getJScrollPanePending() {
		if (jScrollPanePending == null) {
			jScrollPanePending = new JScrollPane();
			jScrollPanePending.setViewportView(getJTablePending());
		}
		return jScrollPanePending;
	}

	private JTable getJTablePending() {
		if (jTablePending == null) {
			jTablePending = new JTable();
			BillTableModel model = new BillTableModel("O");
			jTablePending.setModel(model); //$NON-NLS-1$
			for (int i=0;i<model.getColumnCount(); i++){
				jTablePending.getColumnModel().getColumn(i).setMinWidth(columsWidth[i]);
				if (!columsResizable[i]) jTablePending.getColumnModel().getColumn(i).setMaxWidth(maxWidth[i]);
				if (!columnsVisible[i]) {
					jTablePending.getColumnModel().getColumn(i).setMinWidth(0);
					jTablePending.getColumnModel().getColumn(i).setMaxWidth(0);
					jTablePending.getColumnModel().getColumn(i).setWidth(0);
				}
				if (alignCenter[i]) {
					jTablePending.getColumnModel().getColumn(i).setCellRenderer(new StringCenterTableCellRenderer());
					if (boldCenter[i]) {
						jTablePending.getColumnModel().getColumn(i).setCellRenderer(new CenterBoldTableCellRenderer());
					}
				}
			}
			jTablePending.setAutoCreateColumnsFromModel(false);
			jTablePending.setDefaultRenderer(String.class, new StringTableCellRenderer());
			jTablePending.setDefaultRenderer(Integer.class, new IntegerTableCellRenderer());
			jTablePending.setDefaultRenderer(Double.class, new DoubleTableCellRenderer());
		}
		return jTablePending;
	}

	private JScrollPane getJScrollPaneBills() {
		if (jScrollPaneBills == null) {
			jScrollPaneBills = new JScrollPane();
			jScrollPaneBills.setViewportView(getJTableBills());
		}
		return jScrollPaneBills;
	}

	private JTable getJTableBills() {
		if (jTableBills == null) {
			jTableBills = new JTable();
			BillTableModel model = new BillTableModel("ALL");
			jTableBills.setModel(model); //$NON-NLS-1$
			for (int i=0;i<model.getColumnCount(); i++){
				jTableBills.getColumnModel().getColumn(i).setMinWidth(columsWidth[i]);
				if (!columsResizable[i]) jTableBills.getColumnModel().getColumn(i).setMaxWidth(maxWidth[i]);
				if (!columnsVisible[i]) {
					jTableBills.getColumnModel().getColumn(i).setMinWidth(0);
					jTableBills.getColumnModel().getColumn(i).setMaxWidth(0);
					jTableBills.getColumnModel().getColumn(i).setWidth(0);
				}
				if (alignCenter[i]) {
					jTableBills.getColumnModel().getColumn(i).setCellRenderer(new StringCenterTableCellRenderer());
					if (boldCenter[i]) {
						jTableBills.getColumnModel().getColumn(i).setCellRenderer(new CenterBoldTableCellRenderer());
					}
				}
			}
			jTableBills.setAutoCreateColumnsFromModel(false);
			jTableBills.setDefaultRenderer(String.class, new StringTableCellRenderer());
			jTableBills.setDefaultRenderer(Integer.class, new IntegerTableCellRenderer());
			jTableBills.setDefaultRenderer(Double.class, new DoubleTableCellRenderer());
		}
		return jTableBills;
	}

	private JTabbedPane getJTabbedPaneBills() {
		if (jTabbedPaneBills == null) {
			jTabbedPaneBills = new JTabbedPane();
			jTabbedPaneBills.addTab(MessageBundle.getMessage("angal.billbrowser.bills"), getJScrollPaneBills()); //$NON-NLS-1$
			if (GeneralData.SINGLEUSER || MainMenu.checkUserGrants("cashiersfilter")) {
				jTabbedPaneBills.addTab(MessageBundle.getMessage("angal.billbrowser.pending"), getJScrollPanePending()); //$NON-NLS-1$
				jTabbedPaneBills.addTab(MessageBundle.getMessage("angal.billbrowser.closed"), getJScrollPaneClosed()); //$NON-NLS-1$
			}
		}
		return jTabbedPaneBills;
	}

	private JTable getJTableToday() {
		if (jTableToday == null) {
			jTableToday = new JTable();
			jTableToday.setModel(new DefaultTableModel(new Object[][] {{"<html><b>"+MessageBundle.getMessage("angal.billbrowser.todaym")+"</b></html>", totalToday, "<html><b>"+MessageBundle.getMessage("angal.billbrowser.notpaid")+"</b></html>", balanceToday}}, new String[] {"","","",""}) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, JLabel.class, Double.class};
	
				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTableToday.setRowSelectionAllowed(false);
			jTableToday.setGridColor(Color.WHITE);

		}
		return jTableToday;
	}
	
	private JTable getJTablePeriod() {
		if (jTablePeriod == null) {
			jTablePeriod = new JTable();
			jTablePeriod.setModel(new DefaultTableModel(new Object[][] {{"<html><b>"+MessageBundle.getMessage("angal.billbrowser.periodm")+"</b></html>", totalPeriod, "<html><b>"+MessageBundle.getMessage("angal.billbrowser.notpaid")+"</b></html>", balancePeriod}}, new String[] {"","","",""}) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, JLabel.class, Double.class};
	
				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTablePeriod.setRowSelectionAllowed(false);
			jTablePeriod.setGridColor(Color.WHITE);

		}
		return jTablePeriod;
	}
	
	private JTable getJTableUser() {
		if (jTableUser == null) {
			jTableUser = new JTable();
			jTableUser.setModel(new DefaultTableModel(new Object[][] {{"<html><b>"+user+" today</b></html>", userToday, "<html><b>"+user+ " period"+"</b></html>", userPeriod}}, new String[] {"","","",""}) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, JLabel.class, Double.class};
	
				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTableUser.setRowSelectionAllowed(false);
			jTableUser.setGridColor(Color.WHITE);

		}
		return jTableUser;
	}
	
	private void updateTables() {
		jTableBills.setModel(new BillTableModel("ALL")); //$NON-NLS-1$
		if (GeneralData.SINGLEUSER || MainMenu.checkUserGrants("cashiersfilter")) {
			jTablePending.setModel(new BillTableModel("O")); //$NON-NLS-1$
			jTableClosed.setModel(new BillTableModel("C")); //$NON-NLS-1$
		}
	}
	
	private void updateTables(String user) {
		jTableBills.setModel(new BillTableModel(user, "ALL")); //$NON-NLS-1$
		if (GeneralData.SINGLEUSER || MainMenu.checkUserGrants("cashiersfilter")) {
			jTablePending.setModel(new BillTableModel("O")); //$NON-NLS-1$
			jTableClosed.setModel(new BillTableModel("C")); //$NON-NLS-1$
		}
	}
	
	private void updateDataSet() {
//		System.out.println(formatDateTime(new DateTime().minusMonths(5).toDateMidnight().toGregorianCalendar()));
//		System.out.println(formatDateTime(new DateTime().toDateMidnight().plusDays(1).toGregorianCalendar()));
		updateDataSet(dateToday0, dateToday24);
		
	}
	
	private void updateDataSet(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		
		/*
		 * Bills in the period
		 */
		billPeriod = billManager.getBills(dateFrom, dateTo);
		
		/*
		 * Payments in the period
		 */
		paymentsPeriod = billManager.getPayments(dateFrom, dateTo);
		
		/*
		 * Bills that may be not in the period but with payments in the period
		 */
		billFromPayments = billManager.getBillsFromPayments(dateFrom, dateTo);
		
		/*
		 * Today Totals (singleton)
		 */
		updateTotalsToday();
	}
	
	private void updateTotalsToday() {
		if (totalToday == null) {
			
			totalToday = new BigDecimal(0);
			balanceToday = new BigDecimal(0);
			
			//Bills in today contribution for Not Paid Today (balance)
			for (Bill bill : billPeriod) {
				String status = bill.getStatus();
				if (!status.equals("D") && !status.equals("X")) {
					BigDecimal balance = new BigDecimal(Double.toString(bill.getBalance()));
					balanceToday = balanceToday.add(balance);
				}
			}
			
			//Needed to exclude payments of deleted bills
			ArrayList<Integer> notDeletedBills = new ArrayList<Integer>();
			for (Bill bill : billFromPayments) {
				String status = bill.getStatus();
				if (!status.equals("D")) {
					notDeletedBills.add(bill.getId());
				}
			}
			
			//Payments in today contribution for Paid Today (total)
			for (BillPayment payment : paymentsPeriod) {
				if (notDeletedBills.contains(payment.getBillID())) {
					BigDecimal payAmount = new BigDecimal(Double.toString(payment.getAmount()));
					totalToday = totalToday.add(payAmount);
				}
			}
			
			jTableToday.setValueAt(totalToday, 0, 1);
			jTableToday.setValueAt(balanceToday, 0, 3);
		} 
	}
	
	private void updateTotalsPeriod() {
		
		totalPeriod = new BigDecimal(0);
		balancePeriod = new BigDecimal(0);
		userPeriod = new BigDecimal(0);
		userToday = new BigDecimal(0);
		
		//Bills in range contribution for Not Paid (balance)
		for (Bill bill : billPeriod) {
			String status = bill.getStatus();
			if (!status.equals("D") && !status.equals("X")) {
				BigDecimal balance = new BigDecimal(Double.toString(bill.getBalance()));
				balancePeriod = balancePeriod.add(balance);
			}
		}
		
		//Needed to exclude payments of deleted bills
		ArrayList<Integer> notDeletedBills = new ArrayList<Integer>();
		for (Bill bill : billFromPayments) {
			String status = bill.getStatus();
			if (!status.equals("D")) {
				notDeletedBills.add(bill.getId());
			}
		}
		
		//Payments in range contribution for Paid Period (total)
		for (BillPayment payment : paymentsPeriod) {
			if (notDeletedBills.contains(payment.getBillID())) {
				BigDecimal payAmount = new BigDecimal(Double.toString(payment.getAmount()));
				GregorianCalendar payDate = payment.getDate();
				totalPeriod = totalPeriod.add(payAmount);
				
				String payUser = payment.getUser();
				if (!GeneralData.SINGLEUSER && payUser.equals(user)) {
					userPeriod = userPeriod.add(payAmount);
					
					if (payDate.after(dateToday0) && payDate.before(dateToday24)) 
						userToday = userToday.add(payAmount);
				}
			}
		}
		
		jTablePeriod.setValueAt(totalPeriod, 0, 1);
		jTablePeriod.setValueAt(balancePeriod, 0, 3);
		if (jTableUser != null) {
			jTableUser.setValueAt(userToday, 0, 1);
			jTableUser.setValueAt(userPeriod, 0, 3);
		}
	}
	
	public class BillTableModel extends DefaultTableModel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<Bill> tableArray = new ArrayList<Bill>();
		
		/*
		 * All Bills
		 */
		private ArrayList<Bill> billAll = new ArrayList<Bill>();
		
		public BillTableModel(String user, String status) {
			loadData(user, status);
		}
		
		public BillTableModel(String status) {
			loadData(status, 0, 0);
		}
		
		public BillTableModel(String status, int billID, int patID) {
			loadData(status, billID, patID);
		}
		
		public BillTableModel(String status, int billID, String OPD) {
			loadData(status, billID, OPD);
		}
		
		private void loadData(String status, int billID, String OPD) {
			
			tableArray.clear();
			mapBill.clear();
			
			if (billID != 0) { //filter on billID, no period taken in consideration
				Bill bill = billManager.getBill(billID);
				billPeriod.clear();
				billFromPayments.clear();
				if (bill != null) billPeriod.add(bill);
			} else if (!OPD.equals("")) { //filter on patID, no period taken in consideration
				ArrayList<Bill> bills = billManager.getPatientBills(OPD);
				billPeriod.clear();
				billFromPayments.clear();
				billPeriod.addAll(bills);
			}
			
			mapping(null, status);
		}
		
		private void loadData(String user, String status) {
			
			tableArray.clear();
			mapBill.clear();
			mapping(user, status);
			
		}
		
		private void loadData(String status, int billID, int patID) {
			
			tableArray.clear();
			mapBill.clear();
			
			if (billID != 0) { //filter on billID, no period taken in consideration
				Bill bill = billManager.getBill(billID);
				billPeriod.clear();
				billFromPayments.clear();
				if (bill != null) billPeriod.add(bill);
			} else if (patID != 0) { //filter on patID, no period taken in consideration
				ArrayList<Bill> bills = billManager.getPatientBills(patID);
				billPeriod.clear();
				billFromPayments.clear();
				billPeriod.addAll(bills);
			}
			
			mapping(null, status);
		}
		
		private void mapping(String user, String status) {
			
			/*
			 * Mappings Bills in the period 
			 */
			for (Bill bill : billPeriod) {
				if (user == null || bill.getUser().equals(user)) {
					billAll.add(bill);
					mapBill.put(bill.getId(), bill);
				}
			}
			
			/*
			 * Merging the two bills lists
			 */
			for (Bill bill : billFromPayments) {
				if (user == null || bill.getUser().equals(user)) {
					if (mapBill.get(bill.getId()) == null)
						billAll.add(bill);
				}
			}
			
			if (status.equals("O")) {

				/*
				 * Gets ALL pending bills since begin ("O" + "L")
				 */
				tableArray = billManager.getPendingBills(0);
				
			} else if (status.equals("ALL")) {
				
				Collections.sort(billAll);
				tableArray = billAll;

			} else if (status.equals("C")) {
				for (Bill bill : billPeriod) {
					
					if (bill.getStatus().equals(status)) 
						tableArray.add(bill);
				}
			}
			
			Collections.sort(tableArray, Collections.reverseOrder());
		}
		
		public Class<?> getColumnClass(int columnIndex) {
			return columsClasses[columnIndex];
		}

		public int getColumnCount() {
			int c = 0;
			for (int i = 0; i < columnsVisible.length; i++) {
				if (columnsVisible[i]) {
					c++;
				}
			}
			return c;
		}
		
		public String getColumnName(int columnIndex) {
			return columsNames[columnIndex];
		}

		public int getRowCount() {
			if (tableArray == null)
				return 0;
			return tableArray.size();
		}
		
		//["List", "BillID", "Date", "PatID", "Patient", "Amount", "Update", "Status", "Balance"};

		public Object getValueAt(int r, int c) {
			int index = -1;
			Bill thisBill = tableArray.get(r);
			if (c == index) {
				return thisBill;
			}
			if (c == ++index) {
				return thisBill.getUser();
			}
			if (c == ++index) {
				return thisBill.getListName();
			}
			if (c == ++index) {
				return thisBill.getId();
			}
			if (c == ++index) {
				return formatDateTime(thisBill.getDate());
			}
			if (c == ++index) {
				int patID = thisBill.getPatID();
				return patID == 0 ? "" : String.valueOf(patID);
			}
			if (c == ++index) {
				return thisBill.getPatName();
			}
			if (c == ++index) {
				return thisBill.getAmount();
			}
			if (c == ++index) {
				return formatDateTime(thisBill.getUpdate());
			}
			if (c == ++index) {
				return thisBill.getStatus();
			}
			if (c == ++index) {
				return thisBill.getBalance();
			}
			return null;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
		
		/** 
	     * This method converts a column number in the table
	     * to the right number of the datas.
	     */
	    protected int getNumber(int col) {
	    	// right number to return
	        int n = col;    
	        int i = 0;
	        do {
	            if (!columnsVisible[i]) {
	            	n++;
	            }
	            i++;
	        } while (i < n);
	        // If we are on an invisible column, 
	        // we have to go one step further
	        while (!columnsVisible[n]) {
	        	n++;
	        }
	        return n;
	    }

	}
	
	public String formatDate(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");  //$NON-NLS-1$
		return format.format(time.getTime());
	}
	
	public String formatDateTime(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");  //$NON-NLS-1$
		return format.format(time.getTime());
	}
	
	public String formatDateTimeReport(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //$NON-NLS-1$
		return format.format(time.getTime());
	}

	class StringTableCellRenderer extends DefaultTableCellRenderer {  
	   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {  
		   
			Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			cell.setForeground(Color.BLACK);
			if (((String)table.getValueAt(row, 8)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (!((String)table.getValueAt(row, 1)).equals("Basic")) { //$NON-NLS-1$
				cell.setForeground(Color.BLUE);
			}
			if (((String)table.getValueAt(row, 8)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			return cell;
	   }
	}
	
	class StringCenterTableCellRenderer extends DefaultTableCellRenderer {  
		   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {  
		   
			Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(CENTER);
			if (((String)table.getValueAt(row, 8)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (!((String)table.getValueAt(row, 1)).equals("Basic")) { //$NON-NLS-1$
				cell.setForeground(Color.BLUE);
			}
			if (((String)table.getValueAt(row, 8)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			return cell;
	   }
	}
	
	class IntegerTableCellRenderer extends DefaultTableCellRenderer {  
		   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {  
		   
			Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			cell.setForeground(Color.BLACK);
			cell.setFont(new Font(null, Font.BOLD, 12));
			setHorizontalAlignment(CENTER);
			if (((String)table.getValueAt(row, 8)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (!((String)table.getValueAt(row, 1)).equals("Basic")) { //$NON-NLS-1$
				cell.setForeground(Color.BLUE);
			}
			if (((String)table.getValueAt(row, 8)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			return cell;
	   }
	}
	
	class DoubleTableCellRenderer extends DefaultTableCellRenderer {  
		   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {  
		   
			Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(RIGHT);
			if (((String)table.getValueAt(row, 8)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (!((String)table.getValueAt(row, 1)).equals("Basic")) { //$NON-NLS-1$
				cell.setForeground(Color.BLUE);
			}
			if (((String)table.getValueAt(row, 8)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			return cell;
	   }
	}
	
	class DoubleLeftTableCellRenderer extends DefaultTableCellRenderer {  
		   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {  
		   
			Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			setHorizontalAlignment(LEFT);
			return cell;
	   }
	}
	
	class CenterBoldTableCellRenderer extends DefaultTableCellRenderer {  
		   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {  
		   
			Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(CENTER);
			cell.setFont(new Font(null, Font.BOLD, 12));
			if (((String)table.getValueAt(row, 8)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (!((String)table.getValueAt(row, 1)).equals("Basic")) { //$NON-NLS-1$
				cell.setForeground(Color.BLUE);
			}
			if (((String)table.getValueAt(row, 8)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			return cell;
	   }
	}
	
	private String toHtmlTable(Bill bill) {
		StringBuilder string = new StringBuilder("<html><table><tr>");
		string.append("<td width=\"30px\">").append(bill.getId()).append("</td>");
		string.append("<td width=\"30px\">").append(bill.getPatID()).append("</td>");
		string.append("<td width=\"200px\">").append(bill.getPatName()).append("</td>");
		string.append("<td width=\"30px\">").append(bill.getAmount()).append("</td>");
		string.append("<td width=\"30px\">").append(bill.getBalance()).append("</td>");
		string.append("<td width=\"10px\">").append(bill.getStatus()).append("</td>");
		string.append("</tr></table></html>");
		return string.toString();
	}
	
	public class CompanyTotalTableModel extends AbstractTableModel implements CompanyBillListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BigDecimal totalAmount;
		private BigDecimal totalBalance;
		Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, JLabel.class, Double.class};
		
		/**
		 * @param totalAmount
		 * @param totalBalance
		 */
		public CompanyTotalTableModel(BigDecimal totalAmount, BigDecimal totalBalance) {
			super();
			this.totalAmount = totalAmount;
			this.totalBalance = totalBalance;
		}
		
		public CompanyTotalTableModel() {
			super();
			this.totalAmount = new BigDecimal(0);
			this.totalBalance = new BigDecimal(0);
		}

		@Override
		public int getRowCount() {
			return 1;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}
		
		public Class<?> getColumnClass(int columnIndex) {
			return types[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) return "<html><b>"+MessageBundle.getMessage("angal.billbrowser.amount")+"</b></html>";
			if (columnIndex == 1) return this.totalAmount;
			if (columnIndex == 2) return "<html><b>"+MessageBundle.getMessage("angal.billbrowser.balance")+"</b></html>";
			if (columnIndex == 3) return this.totalBalance;
			return null;
		}

		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}

		public void setTotalBalance(BigDecimal totalBalance) {
			this.totalBalance = totalBalance;
		}

		@Override
		public void totalsUpdated(BigDecimal totalAmount, BigDecimal totalBalance) {
			this.totalAmount = totalAmount;
			this.totalBalance = totalBalance;
			fireTableDataChanged();
		}
		
	}
	
	public class CompanyBillTableModel extends AbstractTableModel {
		
		private EventListenerList companyBillListener = new EventListenerList();
		
		public void addCompanyBillListener(CompanyBillListener l) {
			companyBillListener.add(CompanyBillListener.class, l);
			
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<Bill> pendingBills = new ArrayList<Bill>();
		private Boolean[] selected;
		private BigDecimal totalAmount;
		private BigDecimal totalBalance;
		
		public CompanyBillTableModel(ArrayList<Bill> pendingBills, BigDecimal totalAmount, BigDecimal totalBalance) {
			this.pendingBills = pendingBills;
			this.totalAmount = totalAmount;
			this.totalBalance = totalBalance;
			this.selected = new Boolean[pendingBills.size()];
			Arrays.fill(this.selected, false);
			updateTotals();
		}
		
		public CompanyBillTableModel(ArrayList<Bill> pendingBills) {
			this.pendingBills = pendingBills;
			this.totalAmount = new BigDecimal(0);
			this.totalBalance = new BigDecimal(0);
			this.selected = new Boolean[pendingBills.size()];
			Arrays.fill(this.selected, false);
			updateTotals();
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0) return Boolean.class;
			return String.class;
		}

		@Override
		public int getRowCount() {
			if (pendingBills == null) return 0;
			return pendingBills.size();
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == 0) return true;
			return false;
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			int index = -1;
			Bill thisBill = pendingBills.get(row);
			if (column == index) {
				return thisBill;
			}
			if (column == ++index) {
				return selected[row];
			}
			if (column == ++index) {
				return toHtmlTable(thisBill);
			}
			return null;
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			
			Boolean value = (Boolean) aValue;
			if (columnIndex == 0) {
				this.selected[rowIndex] = value;
			} 
			fireTableUpdated();
			fireTableCellUpdated(rowIndex, columnIndex);
		}
		
		private void fireTableUpdated() {
			EventListener[] listeners = companyBillListener.getListeners(CompanyBillListener.class);
			for (int i = 0; i < listeners.length; i++)
				((CompanyBillListener)listeners[i]).totalsUpdated(this.totalAmount, this.totalBalance);
			
		}

		private void updateTotals() {
			totalAmount = new BigDecimal(0);
			totalBalance = new BigDecimal(0);
			for (int i = 0; i < pendingBills.size(); i++) {
				Bill bill = pendingBills.get(i);
				if (selected[i]) {
					totalAmount = totalAmount.add(new BigDecimal(bill.getAmount()));
					totalBalance = totalBalance.add(new BigDecimal(bill.getBalance()));
				}
			}
			fireTableUpdated();
//			System.out.println("totalAmount: " + totalAmount.toString());
//			System.out.println("totalBalance: " + totalBalance.toString());
		}
		
		@Override
		public void fireTableCellUpdated(int row, int column) {
			updateTotals();
			super.fireTableCellUpdated(row, column);
		}

		public ArrayList<Bill> getPendingBills() {
			return pendingBills;
		}

		public Boolean[] getSelected() {
			return selected;
		}

		public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public BigDecimal getTotalBalance() {
			return totalBalance;
		}
		
		public void selectAll(boolean all) {
			Arrays.fill(this.selected, all);
			updateTotals();
			fireTableDataChanged();
		}
	}
	
	public class SelectAllActionListener implements ActionListener {
		
		CompanyBillTableModel companyModel;

		public SelectAllActionListener(CompanyBillTableModel companyModel) {
			this.companyModel = companyModel;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox jCheckBox = (JCheckBox) e.getSource();
			if (jCheckBox.isSelected()) companyModel.selectAll(true);
			else companyModel.selectAll(false);
		}

	}
	
	public interface CompanyBillListener extends EventListener {

		public void totalsUpdated(BigDecimal totalAmount, BigDecimal totalBalance);
	}
	
}
