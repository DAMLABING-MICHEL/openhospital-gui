package org.isf.admission.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.isf.accounting.gui.PatientBillEdit;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItem;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.generaldata.CardPrinter;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.Session;
import org.isf.opd.gui.OpdEditExtended;
import org.isf.opd.model.Opd;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.stat.manager.GenericReportPatient;
import org.isf.therapy.gui.TherapyEdit;
import org.isf.utils.gui.SpringUtilities;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;

/**
 * This class shows a list of all known patients and for each if (and where) they are actually admitted, 
 * you can:
 *  filter patients by ward and admission status
 *  search for patient with given name 
 *  add a new patient, edit or delete an existing patient record
 *  view extended data of a selected patient 
 *  add an admission record (or modify existing admission record, or set a discharge) of a selected patient
 * 
 * release 2.2 oct-23-06
 * 
 * @author flavio
 * 
 */


/*----------------------------------------------------------
 * modification history
 * ====================
 * 23/10/06 - flavio - lastKey reset
 * 10/11/06 - ross - removed from the list the deleted patients
 *                   the list is now in alphabetical  order (modified IoOperations)
 * 12/08/08 - alessandro - Patient Extended
 * 01/01/09 - Fabrizio   - The OPD button is conditioned to the extended funcionality of OPD.
 *                         Reorganized imports.
 * 13/02/09 - Alex - Search Key extended to patient code & notes
 * 29/05/09 - Alex - fixed mnemonic keys for Admission, OPD and PatientSheet
 * 14/10/09 - Alex - optimized searchkey algorithm and cosmetic changes to the code
 * 02/12/09 - Alex - search field get focus at begin and after Patient delete/update
 * 03/12/09 - Alex - added new button for merging double registered patients histories
 * 05/12/09 - Alex - fixed exception on filter after saving admission
 * 06/12/09 - Alex - fixed exception on filter after saving admission (ALL FILTERS)
 * 06/12/09 - Alex - Cosmetic changes to GUI
 -----------------------------------------------------------*/

public class AdmittedPatientBrowser extends ModalJFrame implements
		PatientInsert.PatientListener,// AdmissionBrowser.AdmissionListener,
		PatientInsertExtended.PatientListener, AdmissionBrowser.AdmissionListener, //by Alex
		PatientDataBrowser.DeleteAdmissionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//TODO: Fix The filtering (it has to be done by the database!!!).
	//TODO: Put some date limit, and a button that retrieves everything if the user needs it.
	//TODO: Show patPCode in table.
	private String[] patientClassItems = {
			MessageBundle.getMessage("angal.admission.all"),
			MessageBundle.getMessage("angal.admission.admitted"),
			MessageBundle.getMessage("angal.admission.notadmitted"),
//			"Recently Discharged"
	};
	private JComboBox patientClassBox = new JComboBox(patientClassItems);
	private JCheckBox wardCheck[] = null;
	private JTextField searchString = null;
	private JButton jSearchButton = null;
	private JButton buttonBill = null;
	private String lastKey = "";
	private ArrayList<Ward> wardList = null;
	private JLabel rowCounter = null;
	private String rowCounterText = MessageBundle.getMessage("angal.admission.count");
	private ArrayList<AdmittedPatient> pPatient = new ArrayList<AdmittedPatient>();
	private String informations = MessageBundle.getMessage("angal.admission.city") + " / " + MessageBundle.getMessage("angal.admission.addressm") + " / " + MessageBundle.getMessage("angal.admission.telephone") + " / " + MessageBundle.getMessage("angal.patient.note");
	private String[] pColums = { MessageBundle.getMessage("angal.admission.code"), MessageBundle.getMessage("angal.admission.name"), MessageBundle.getMessage("angal.admission.age"), MessageBundle.getMessage("angal.admission.sex"), informations, MessageBundle.getMessage("angal.admission.ward") };
	private int[] pColumwidth = { 100, 200, 80, 50, 120, 100 };
	private boolean[] pColumResizable = {false, false, false, false, true, false};
	private AdmittedPatient patient;
	private JTable table;
	private JScrollPane scrollPane;
	private AdmittedPatientBrowser myFrame;
	private AdmissionBrowserManager manager = new AdmissionBrowserManager();
	protected boolean altKeyReleased = true;

	private final String EMERGENCY_WARD_CODE = "Q";
	private final String ADMISSION_TYPE_NORMAL = "N";
	private final String TRANSFER_ADM_TYPE_EMERGENCY_CODE = "E";
	private final String EMERGENCY_DIAGNOSIS_CODE = "EM";

	public void fireMyDeletedPatient(Patient p){
				
		int cc = 0;
		boolean found = false;
		for (AdmittedPatient elem : pPatient) {
			if (elem.getPatient().getCode() == p.getCode()) {
				found = true;
				break;
			}
			cc++;
		}
		if (found){
			pPatient.remove(cc);
			lastKey = "";
			filterPatient(null);
		}
	}
	
	/*
	 * manage PatientDataBrowser messages
	 */
	public void deleteAdmissionUpdated(AWTEvent e) {
		Admission adm = (Admission) e.getSource();
		
		//remember selected row
		int row = table.getSelectedRow();
		
		for (AdmittedPatient elem : pPatient) {
			if (elem.getPatient().getCode() == adm.getPatId()) {
				//found same patient in the list
				Admission elemAdm = elem.getAdmission();
				if (elemAdm != null) {
					//the patient is admitted
					if (elemAdm.getId() == adm.getId())
						//same admission --> delete
						elem.setAdmission(null);	
				}
				break;
			}
		}
		lastKey = "";
		filterPatient(null);
		try {
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(row, row);
		} catch (Exception e1) {
		}
		
	}

	/*
	 * manage AdmissionBrowser messages
	 */
	public void admissionInserted(AWTEvent e) {
		Admission adm = (Admission) e.getSource();
		admissionInserted(adm);
	}

	public void admissionInserted(Admission adm) {
		//remember selected row
		int row = table.getSelectedRow();
		int patId = adm.getPatId();
		Patient patient = null;

		for (AdmittedPatient elem : pPatient) {
			if (elem.getPatient().getCode() == patId) {
				//found same patient in the list
				patient = elem.getPatient();
				elem.setAdmission(adm);
				break;
			}
		}
		lastKey = "";
		filterPatient(null);
		try {
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(row, row);
		} catch (Exception e1) {
		}

		if (CardPrinter.PRINT_AFTER_NEW_ADMISSION && patient != null) {
			printPatientAdmission(patient);
		}

		if (adm.getWardId().equals(EMERGENCY_WARD_CODE)) {
			JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.emergencybutton.admissionsuccessful") + ": " + adm.getYProg(),
					MessageBundle.getMessage("angal.admission.emergencybutton"), JOptionPane.INFORMATION_MESSAGE);
		} else if (GeneralData.AUTOMATICBILL) {
//			newBillAdmission(patient);
			buttonBill.doClick();
		}
	}

	/*
	 * param contains info about patient admission,
	 * ward can varying or patient may be discharged
	 * 
	 */
	public void admissionUpdated(AWTEvent e) {
		Admission adm = (Admission) e.getSource();
		
		//remember selected row
		int row = table.getSelectedRow();
		int admId = adm.getId();
		int patId = adm.getPatId();
		
		for (AdmittedPatient elem : pPatient) {
			if (elem.getPatient().getCode() == patId) {
				//found same patient in the list
				Admission elemAdm = elem.getAdmission();
				if (adm.getDisDate() != null) {
					//is a discharge
					if (elemAdm != null) {
						//the patient is not discharged
						if (elemAdm.getId() == admId)
							//same admission --> discharge
							elem.setAdmission(null);
					}
				} else {
					//is not a discharge --> patient admitted
					elem.setAdmission(adm);
				}
				break;
			}
		}
		lastKey = "";
		filterPatient(null);
		try {
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(row, row);
			
		} catch (Exception e1) {
		}
	}

	/*
	 * manage PatientEdit messages
	 * 
	 * mind PatientEdit return a patient patientInserted create a new
	 * AdmittedPatient for table
	 */
	public void patientInserted(AWTEvent e) {
		Patient u = (Patient) e.getSource();
		pPatient.add(0, new AdmittedPatient(u, null));
		lastKey = "";
		filterPatient(null);
		try {
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {
		}
		searchString.requestFocus();
		rowCounter.setText(rowCounterText + ": " + pPatient.size());
		
		if (CardPrinter.PRINT_AFTER_NEW_PATIENT) {
			printPatientCard(u);
		}
		
		if (GeneralData.AUTOMATICBILL) {
			newBillCard(u);
		}
	}

	private void newBillCard(Patient u) {
		int age = u.getAge();
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar time17pm = new GregorianCalendar();
		GregorianCalendar time8am = new GregorianCalendar();
		
		time17pm.set(GregorianCalendar.HOUR_OF_DAY, 17);
		time17pm.set(GregorianCalendar.MINUTE, 0);
		time17pm.set(GregorianCalendar.SECOND, 0);
		
		time8am.set(GregorianCalendar.HOUR_OF_DAY, 8);
		time8am.set(GregorianCalendar.MINUTE, 0);
		time8am.set(GregorianCalendar.SECOND, 0);
		
		PriceListManager priceMan = new PriceListManager();
		ArrayList<Price> prices = priceMan.getPrices();
		PriceList priceList = priceMan.getLists().get(0);
		
		Price under5Price = null;
		Price over5Price = null;
		Price adultPrice = null;
		Price nightUnder5Price = null;
		Price nightOver5Price = null;
		Price nightAdultPrice = null;
		Price consultationAdult = null;
		Price consultationUnder5 = null;
		Price consultationOver5 = null;
				
		for (Price price : prices) {
			if (price.getGroup().equals("OTH")) { 
				if (price.getDesc().startsWith("C1 -"))
					adultPrice = price;
				else if (price.getDesc().startsWith("MC1 -"))
					over5Price = price;
				else if (price.getDesc().startsWith("PC1 -"))
					under5Price = price;
				else if (price.getDesc().startsWith("EMG -"))
					nightAdultPrice = price;
				else if (price.getDesc().startsWith("MEMG -"))
					nightOver5Price = price;
				else if (price.getDesc().startsWith("PEMG -"))
					nightUnder5Price = price;
				else if (price.getDesc().startsWith("C2 -"))
					consultationAdult = price;
				else if (price.getDesc().startsWith("MC2 -"))
					consultationOver5 = price;
				else if (price.getDesc().startsWith("PC2 -"))
					consultationUnder5 = price;
			}
		}
		
		//create the bill
		Bill bill = new Bill();
		bill.setPatID(u.getCode());
		bill.setPatient(true);
		bill.setPatName(u.getName());
		bill.setStatus("O");
		bill.setList(true);
		bill.setListID(priceList.getId());
		bill.setListName(priceList.getName());
		
		//create the items
		BigDecimal total = new BigDecimal(0);
		ArrayList<BillItem> billItems = new ArrayList<BillItem>();
		if (age > 10) {
			BillItem billItem = new BillItem();
			billItem.setItemDescription(adultPrice.getDesc());
			billItem.setItemQuantity(1);
			billItem.setPrice(true);
			billItem.setItemAmount(adultPrice.getPrice());
			billItem.setPriceID(adultPrice.getGroup()+adultPrice.getItem());
			billItems.add(billItem);
			total = total.add(new BigDecimal(Double.toString(adultPrice.getPrice())));
		}
		if (age >= 6 && age <= 10) {
			BillItem billItem = new BillItem();
			billItem.setItemDescription(over5Price.getDesc());
			billItem.setItemQuantity(1);
			billItem.setPrice(true);
			billItem.setItemAmount(over5Price.getPrice());
			billItem.setPriceID(over5Price.getGroup()+over5Price.getItem());
			billItems.add(billItem);
			total = total.add(new BigDecimal(Double.toString(over5Price.getPrice())));
		}
		if (age <= 5) {
			BillItem billItem = new BillItem();
			billItem.setItemDescription(under5Price.getDesc());
			billItem.setItemQuantity(1);
			billItem.setPrice(true);
			billItem.setItemAmount(under5Price.getPrice());
			billItem.setPriceID(under5Price.getGroup()+under5Price.getItem());
			billItems.add(billItem);
			total = total.add(new BigDecimal(Double.toString(under5Price.getPrice())));
		}
		if (now.after(time17pm) || now.before(time8am) || 
				now.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY ||
				now.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY) {
			// Night time
			if (age > 10) {
				BillItem billItem = new BillItem();
				billItem.setItemDescription(nightAdultPrice.getDesc());
				billItem.setItemQuantity(1);
				billItem.setPrice(true);
				billItem.setItemAmount(nightAdultPrice.getPrice());
				billItem.setPriceID(nightAdultPrice.getGroup()+nightAdultPrice.getItem());
				billItems.add(billItem);
				total = total.add(new BigDecimal(Double.toString(nightAdultPrice.getPrice())));
			}
			if (age >= 6 && age <= 10) {
				BillItem billItem = new BillItem();
				billItem.setItemDescription(nightOver5Price.getDesc());
				billItem.setItemQuantity(1);
				billItem.setPrice(true);
				billItem.setItemAmount(nightOver5Price.getPrice());
				billItem.setPriceID(nightOver5Price.getGroup()+nightOver5Price.getItem());
				billItems.add(billItem);
				total = total.add(new BigDecimal(Double.toString(nightOver5Price.getPrice())));
			}
			if (age <= 5) {
				BillItem billItem = new BillItem();
				billItem.setItemDescription(nightUnder5Price.getDesc());
				billItem.setItemQuantity(1);
				billItem.setPrice(true);
				billItem.setItemAmount(nightUnder5Price.getPrice());
				billItem.setPriceID(nightUnder5Price.getGroup()+nightUnder5Price.getItem());
				billItems.add(billItem);
				total = total.add(new BigDecimal(Double.toString(nightUnder5Price.getPrice())));
			}
		} else {
			// Day Time
			if (age > 10) {
				BillItem billItem = new BillItem();
				billItem.setItemDescription(consultationAdult.getDesc());
				billItem.setItemQuantity(1);
				billItem.setPrice(true);
				billItem.setItemAmount(consultationAdult.getPrice());
				billItem.setPriceID(consultationAdult.getGroup()+consultationAdult.getItem());
				billItems.add(billItem);
				total = total.add(new BigDecimal(Double.toString(consultationAdult.getPrice())));
			}
			if (age >= 6 && age <= 10) {
				BillItem billItem = new BillItem();
				billItem.setItemDescription(consultationOver5.getDesc());
				billItem.setItemQuantity(1);
				billItem.setPrice(true);
				billItem.setItemAmount(consultationOver5.getPrice());
				billItem.setPriceID(consultationOver5.getGroup()+consultationOver5.getItem());
				billItems.add(billItem);
				total = total.add(new BigDecimal(Double.toString(consultationOver5.getPrice())));
			}
			if (age <= 5) {
				BillItem billItem = new BillItem();
				billItem.setItemDescription(consultationUnder5.getDesc());
				billItem.setItemQuantity(1);
				billItem.setPrice(true);
				billItem.setItemAmount(consultationUnder5.getPrice());
				billItem.setPriceID(consultationUnder5.getGroup()+consultationUnder5.getItem());
				billItems.add(billItem);
				total = total.add(new BigDecimal(Double.toString(consultationUnder5.getPrice())));
			}
		}
		bill.setAmount(total.doubleValue());
		
		
		//Edit the bill for confirm
		PatientBillEdit editBill = new PatientBillEdit(AdmittedPatientBrowser.this, bill, billItems, u);
		editBill.setVisible(true);
	}
	
	private void updateBillAdmission(Patient u) {
		PriceListManager priceMan = new PriceListManager();
		PriceList priceList = priceMan.getLists().get(0);
		
		//create the bill
		Bill bill = new Bill();
		bill.setPatID(u.getCode());
		bill.setPatient(true);
		bill.setPatName(u.getName());
		bill.setStatus("O");
		bill.setList(true);
		bill.setListID(priceList.getId());
		bill.setListName(priceList.getName());
		
		//Edit the bill for confirm
		PatientBillEdit editBill = new PatientBillEdit(AdmittedPatientBrowser.this, bill, new ArrayList<BillItem>(), u);
		editBill.setVisible(true);
	}
	
	private void newBillAdmission(Patient u) {
		PriceListManager priceMan = new PriceListManager();
		PriceList priceList = priceMan.getLists().get(0);
		
		//create the bill
		Bill bill = new Bill();
		bill.setPatID(u.getCode());
		bill.setPatient(true);
		bill.setPatName(u.getName());
		bill.setStatus("O");
		bill.setList(true);
		bill.setListID(priceList.getId());
		bill.setListName(priceList.getName());
		
		//Edit the bill for confirm
		PatientBillEdit editBill = new PatientBillEdit(AdmittedPatientBrowser.this, bill, new ArrayList<BillItem>(), u);
		editBill.setVisible(true);
	}

	/**
	 * @param u
	 * @throws HeadlessException
	 */
	private void printPatientCard(Patient u) throws HeadlessException {
		int option = 0;
		if (!CardPrinter.PRINT_WITHOUT_ASK) {
			option = JOptionPane.showConfirmDialog(AdmittedPatientBrowser.this, "Print OPD form?");
		} 
		if (option == JOptionPane.YES_OPTION)
			new GenericReportPatient(u.getCode(), CardPrinter.CARD_FILE, true, CardPrinter.USE_DEFAULT_PRINTER);
	}
	
	/**
	 * @param u
	 * @throws HeadlessException
	 */
	private void printPatientAdmission(Patient u) throws HeadlessException {
		int option = 0;
		if (!CardPrinter.PRINT_WITHOUT_ASK) {
			option = JOptionPane.showConfirmDialog(AdmittedPatientBrowser.this, "Print Admission form?");
		} 
		if (option == JOptionPane.YES_OPTION)
			new GenericReportPatient(u.getCode(), CardPrinter.ADMISSION_FILE, true, CardPrinter.USE_DEFAULT_PRINTER);
	}

	public void patientUpdated(AWTEvent e) {
		
		Patient u = (Patient) e.getSource();
		
		//remember selected row
		int row = table.getSelectedRow();
		
		for (int i = 0; i < pPatient.size(); i++) {
			if ((pPatient.get(i).getPatient().getCode()).equals(u.getCode())) {
				Admission admission = pPatient.get(i).getAdmission();
				pPatient.remove(i);
				pPatient.add(i, new AdmittedPatient(u, admission));
				break;
			}
		}
		lastKey = "";
		filterPatient(null);
		try {
			table.setRowSelectionInterval(row,row);
		} catch (Exception e1) {
		}
		
		searchString.requestFocus();
		rowCounter.setText(rowCounterText + ": " + pPatient.size());
	}

	public AdmittedPatientBrowser() {

		setTitle(MessageBundle.getMessage("angal.admission.patientsbrowser"));
		myFrame = this;

		if (!GeneralData.ENHANCEDSEARCH) {
			//Load the whole list of patients
		    try {
		    	BusyState.setBusyState(AdmittedPatientBrowser.this, true);
		    	pPatient = manager.getAdmittedPatients(null);
		    } finally {
		    	BusyState.setBusyState(AdmittedPatientBrowser.this, false);
		    }
		}
		
		CardPrinter.getCardPrinter();
		
		initComponents();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		rowCounter.setText(rowCounterText + ": " + pPatient.size());
		searchString.requestFocus();

		myFrame.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent e) {
				//to free memory
				if (pPatient != null) pPatient.clear();
				if (wardList != null) wardList.clear();
				dispose();
			}			
		});
	}

	private void initComponents() {
		add(getDataAndControlPanel(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);
	}

	private JPanel getDataAndControlPanel() {
		JPanel dataAndControlPanel = new JPanel(new BorderLayout());
		dataAndControlPanel.add(getControlPanel(), BorderLayout.WEST);
		dataAndControlPanel.add(getScrollPane(), BorderLayout.CENTER);
		return dataAndControlPanel;
	}
	
	/*
	 * panel with filtering controls
	 */
//	private JDateChooser dateFromField;
//	private JDateChooser dateToField;
	private JRadioButton tfuVisitYes;
	private JRadioButton tfuVisitNo;
	private JRadioButton tfuVisitAll;
	private JPanel getControlPanel() {

		JPanel mainPanel = new JPanel(new BorderLayout());

		patientClassBox = new JComboBox(patientClassItems);
		if (!GeneralData.ENHANCEDSEARCH) {
			patientClassBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					lastKey = "";
					filterPatient(null);
				}
			});
		}
		JPanel northPanel = new JPanel(new FlowLayout());
		northPanel.add(patientClassBox);
		northPanel = setMyBorder(northPanel, MessageBundle.getMessage("angal.admission.admissionstatus"));

		mainPanel.add(northPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		if (MainMenu.checkUserGrants("btndatamalnut")) {
			JPanel tfuPanel  = new JPanel(new BorderLayout());
			JPanel tfuFiltersPanel = new JPanel(new SpringLayout());
//			dateFromField = new JDateChooser(new Date(), "dd/MM/yy");
//			tfuFiltersPanel.add(new JLabel("From: "));
//			tfuFiltersPanel.add(dateFromField);
//			dateToField = new JDateChooser(new Date(), "dd/MM/yy");
//			tfuFiltersPanel.add(new JLabel("To: "));
//			tfuFiltersPanel.add(dateToField);
			JPanel radioPanel = new JPanel(new FlowLayout());
			ButtonGroup tfuVisitGroup = new ButtonGroup();
			tfuVisitYes = new JRadioButton("Yes");
			tfuVisitGroup.add(tfuVisitYes);
			radioPanel.add(tfuVisitYes);
			tfuVisitNo = new JRadioButton("No");
			tfuVisitGroup.add(tfuVisitNo);
			radioPanel.add(tfuVisitNo);
			tfuVisitAll = new JRadioButton("All", true);
			tfuVisitGroup.add(tfuVisitAll);
			radioPanel.add(tfuVisitAll);
			tfuFiltersPanel.add(new JLabel("TFU Visit:"));
			tfuFiltersPanel.add(radioPanel);
			SpringUtilities.makeCompactGrid(tfuFiltersPanel, 1, 2, 0, 0, 0, 0);
			tfuPanel.add(tfuFiltersPanel);
			tfuPanel = setMyBorder(tfuPanel, "TFU Filter");
			tfuPanel.add(tfuFiltersPanel);
			centerPanel.add(tfuPanel);
		}

		if (wardList == null) {
			WardBrowserManager wbm = new WardBrowserManager();
			ArrayList<Ward> wardWithBeds = wbm.getWards();

			wardList = new ArrayList<Ward>();
			for (Ward elem : wardWithBeds) {

				if (elem.getBeds() > 0)
					wardList.add(elem);
			}
		}

		JPanel wardsPanel = new JPanel();
		wardsPanel.setLayout(new BoxLayout(wardsPanel, BoxLayout.Y_AXIS));

		JPanel checkPanel[] = new JPanel[wardList.size()];
		wardCheck = new JCheckBox[wardList.size()];

		for (int i = 0; i < wardList.size(); i++) {
			checkPanel[i] = new JPanel(new BorderLayout());
			wardCheck[i] = new JCheckBox();
			wardCheck[i].setSelected(true);
			if (!GeneralData.ENHANCEDSEARCH) {
				wardCheck[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						lastKey = "";
						filterPatient(null);
					}
				});
			}
			checkPanel[i].add(wardCheck[i], BorderLayout.WEST);
			checkPanel[i].add(new JLabel(wardList.get(i).getDescription()),
					BorderLayout.CENTER);
			checkPanel[i].setPreferredSize(new Dimension(200, 20));
			checkPanel[i].setMaximumSize(new Dimension(200, 20));
			checkPanel[i].setMinimumSize(new Dimension(200, 20));
			wardsPanel.add(checkPanel[i], null);
		}
		wardsPanel = setMyBorder(wardsPanel, MessageBundle.getMessage("angal.admission.wards"));
		centerPanel.add(wardsPanel);

		rowCounter = new JLabel(rowCounterText + ": ");
		centerPanel.add(rowCounter);
		
		mainPanel.add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new BorderLayout());

		searchString = new JTextField();
		searchString.setColumns(15);
		if (GeneralData.ENHANCEDSEARCH) {
			searchString.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				     if (key == KeyEvent.VK_ENTER) {
				    	 jSearchButton.doClick();
				     }
				}
	
				public void keyReleased(KeyEvent e) {
				}
	
				public void keyTyped(KeyEvent e) {
				}
			});
		} else {
			searchString.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {
					if (altKeyReleased) {
						lastKey = "";
						String s = "" + e.getKeyChar();
						if (Character.isLetterOrDigit(e.getKeyChar())) {
							lastKey = s;
						}
						filterPatient(searchString.getText());
					}
				}
	
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
					if (key == KeyEvent.VK_ALT)
						altKeyReleased = false;
				}
	
				public void keyReleased(KeyEvent e) {
					altKeyReleased = true;
				}
			});
		}
		southPanel.add(searchString, BorderLayout.CENTER);
		if (GeneralData.ENHANCEDSEARCH) southPanel.add(getJSearchButton(), BorderLayout.EAST);
		southPanel = setMyBorder(southPanel, MessageBundle.getMessage("angal.admission.searchkey"));
		mainPanel.add(southPanel, BorderLayout.SOUTH);

		return mainPanel;
	}

	private JScrollPane getScrollPane() {
		table = new JTable(new AdmittedPatientBrowserModel(null));
		table.setAutoCreateColumnsFromModel(false);
		
		for (int i=0;i<pColums.length; i++){
			table.getColumnModel().getColumn(i).setMinWidth(pColumwidth[i]);
			if (!pColumResizable[i]) table.getColumnModel().getColumn(i).setMaxWidth(pColumwidth[i]);
		}
		
		table.getColumnModel().getColumn(0).setCellRenderer(new CenterTableCellRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(new CenterTableCellRenderer());
		table.getColumnModel().getColumn(3).setCellRenderer(new CenterTableCellRenderer());

		int tableWidth = 0;
		for (int i = 0; i<pColumwidth.length; i++){
			tableWidth += pColumwidth[i];
		}
		
		scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(tableWidth+200, 200));
		return scrollPane;
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		if (MainMenu.checkUserGrants("btnadmnew")) buttonPanel.add(getButtonNew());
		if (MainMenu.checkUserGrants("btnadmedit")) buttonPanel.add(getButtonEdit());
		if (MainMenu.checkUserGrants("btnadmemergency")) buttonPanel.add(getButtonEmergency());
		if (MainMenu.checkUserGrants("btnadmdel")) buttonPanel.add(getButtonDel());
		if (MainMenu.checkUserGrants("btnadmadm")) buttonPanel.add(getButtonAdmission());
		if (GeneralData.OPDEXTENDED && MainMenu.checkUserGrants("btnadmopd")) buttonPanel.add(getButtonOpd());
		if (MainMenu.checkUserGrants("btnadmbill")) buttonPanel.add(getButtonBill());
		if (MainMenu.checkUserGrants("data")) buttonPanel.add(getButtonData());
		if (MainMenu.checkUserGrants("btnadmpatientfolder")) buttonPanel.add(getButtonPatientFolderBrowser());
		if (MainMenu.checkUserGrants("btnadmtherapy")) buttonPanel.add(getButtonTherapy());
		if (GeneralData.MERGEFUNCTION && MainMenu.checkUserGrants("btnadmmer")) buttonPanel.add(getButtonMerge());
		if (!MainMenu.checkUserGrants("btndatamalnut")) buttonPanel.add(getJButtonReport());
		buttonPanel.add(getButtonClose());
		return buttonPanel;
	}

	private JButton getButtonNew() {
		JButton buttonNew = new JButton(MessageBundle.getMessage("angal.admission.newpatient"));
		buttonNew.setMnemonic(KeyEvent.VK_N);
		buttonNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				if (GeneralData.PATIENTEXTENDED) {
					PatientInsertExtended newrecord = new PatientInsertExtended(AdmittedPatientBrowser.this, new Patient(), true);
					newrecord.addPatientListener(AdmittedPatientBrowser.this);
					newrecord.setVisible(true);
				} else {
					PatientInsert newrecord = new PatientInsert(AdmittedPatientBrowser.this, new Patient(), true);
					newrecord.addPatientListener(AdmittedPatientBrowser.this);
					newrecord.setVisible(true);
				}
				
			}
		});
		return buttonNew;
	}

	private JButton getButtonEdit() {
		JButton buttonEdit = new JButton(MessageBundle.getMessage("angal.admission.editpatient"));
		buttonEdit.setMnemonic(KeyEvent.VK_E);
		buttonEdit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				if (GeneralData.PATIENTEXTENDED) {
					
					PatientInsertExtended editrecord = new PatientInsertExtended(AdmittedPatientBrowser.this, patient.getPatient(), false);
					editrecord.addPatientListener(AdmittedPatientBrowser.this);
					editrecord.setVisible(true);
				} else {
					PatientInsert editrecord = new PatientInsert(AdmittedPatientBrowser.this, patient.getPatient(), false);
					editrecord.addPatientListener(AdmittedPatientBrowser.this);
					editrecord.setVisible(true);
				}
			}
		});
		return buttonEdit;
	}

	private JButton getButtonEmergency() {
		JButton emergencyButton = new JButton(MessageBundle.getMessage("angal.admission.emergency"));
		emergencyButton.setMnemonic(KeyEvent.VK_X);
		emergencyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.emergencybutton"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				Admission admission = patient.getAdmission();
				if (admission != null && admission.getAdmitted() == 1) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.emergencybutton.alreadyadmitted"),
							MessageBundle.getMessage("angal.admission.emergencybutton"), JOptionPane.WARNING_MESSAGE);
					return;
				} else {
					int ok = JOptionPane.showConfirmDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.emergencybuttonconfirm"));
					if (ok == JOptionPane.OK_OPTION) {
						// Create new Admission in Emergency
						Admission emergencyAdmission = new Admission();
						emergencyAdmission.setType(ADMISSION_TYPE_NORMAL);
						emergencyAdmission.setAdmType(TRANSFER_ADM_TYPE_EMERGENCY_CODE);
						emergencyAdmission.setYProg(manager.getNextYProg());
						emergencyAdmission.setAdmDate(new GregorianCalendar());
						emergencyAdmission.setPatId(patient.getPatient().getCode());
						emergencyAdmission.setWardId(EMERGENCY_WARD_CODE);
						emergencyAdmission.setDeleted("N");
						emergencyAdmission.setAdmitted(1);
						emergencyAdmission.setDiseaseInId(EMERGENCY_DIAGNOSIS_CODE);
						emergencyAdmission.setTransUnit(new Float(0));
						if (manager.newAdmission(emergencyAdmission)) {
							admissionInserted(emergencyAdmission);
						}
					} else {
						return;
					}
				}
			}
		});
		return emergencyButton;
	}

	private JButton getButtonDel() {
		JButton buttonDel = new JButton(MessageBundle.getMessage("angal.admission.deletepatient"));
		buttonDel.setMnemonic(KeyEvent.VK_T);
		buttonDel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.deletepatient"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				Patient pat = patient.getPatient();
				
				int n = JOptionPane.showConfirmDialog(null,
						MessageBundle.getMessage("angal.admission.deletepatient") + " " +pat.getName() + "?",
						MessageBundle.getMessage("angal.admission.deletepatient"), JOptionPane.YES_NO_OPTION);
				
				if (n == JOptionPane.YES_OPTION){
					PatientBrowserManager manager = new PatientBrowserManager();
					boolean result = false;
					try {
						BusyState.setBusyState(AdmittedPatientBrowser.this, true);
						result = manager.deletePatient(pat);
					} finally {
						BusyState.setBusyState(AdmittedPatientBrowser.this, false);
					}
					if (result){
						AdmissionBrowserManager abm = new AdmissionBrowserManager();
						ArrayList<Admission> patientAdmissions = abm.getAdmissions(pat);
						for (Admission elem : patientAdmissions){
							abm.setDeleted(elem.getId());
						}
						fireMyDeletedPatient(pat);
					}
				}					
			}
		});
		return buttonDel;
	}

	private JButton getButtonAdmission() {
		JButton buttonAdmission = new JButton(MessageBundle.getMessage("angal.admission.admission"));
		buttonAdmission.setMnemonic(KeyEvent.VK_A);
		buttonAdmission.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.admission"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				if (patient.getAdmission() != null) {
					// edit previous admission or dismission
					new AdmissionBrowser(myFrame, patient, true);
				} else {
					// new admission
					new AdmissionBrowser(myFrame, patient, false);
				}
			}
		});
		return buttonAdmission;
	}

	private JButton getButtonOpd() {
		JButton buttonOpd = new JButton(MessageBundle.getMessage("angal.admission.opd"));
		buttonOpd.setMnemonic(KeyEvent.VK_O);
		buttonOpd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.opd"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				if (patient  != null) {
					Opd opd = new Opd(0,' ',-1,"0",0,"");
					OpdEditExtended newrecord = new OpdEditExtended(myFrame, opd, patient.getPatient(), true);
					newrecord.setVisible(true);
					
				} /*else {
					//new OpdBrowser(true);
				}*/
			}
		});
		return buttonOpd;
	}
	
	private JButton getButtonBill() {
		buttonBill = new JButton(MessageBundle.getMessage("angal.admission.bill"));
		buttonBill.setMnemonic(KeyEvent.VK_B);
		buttonBill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.bill"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				if (patient  != null) {
					Patient pat = patient.getPatient();
					BillBrowserManager billManager = new BillBrowserManager();
					ArrayList<Bill> patientPendingBills = billManager.getPendingBills(pat.getCode());
					if (patientPendingBills.isEmpty()) {
						new PatientBillEdit(AdmittedPatientBrowser.this, pat);
						//dispose();
					} else {
						if (patientPendingBills.size() == 1) {
							int ok = JOptionPane.showConfirmDialog(AdmittedPatientBrowser.this, "This patient has a pending bill, start a new bill?",
									MessageBundle.getMessage("angal.admission.bill"), JOptionPane.YES_NO_CANCEL_OPTION);
							
							if (ok == JOptionPane.YES_OPTION) {
								new PatientBillEdit(AdmittedPatientBrowser.this, pat);
							} else if (ok == JOptionPane.NO_OPTION) {
								PatientBillEdit pbe = new PatientBillEdit(AdmittedPatientBrowser.this, patientPendingBills.get(0), pat, false);
								pbe.setVisible(true);
							} else return;
						} else {
							JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, "There are more than one pending bill for this patient, please check in Bill Manager",
									MessageBundle.getMessage("angal.admission.bill"), JOptionPane.WARNING_MESSAGE);
							return;
						}
					} 
				} /*else {
					//new OpdBrowser(true);
				}*/
			}
		});
		return buttonBill;
	}

	//TODO: add date filters
	private JButton getButtonData() {
		JButton buttonData = new JButton(MessageBundle.getMessage("angal.admission.data"));
		buttonData.setMnemonic(KeyEvent.VK_D);
		buttonData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.data"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				PatientDataBrowser pdb = new PatientDataBrowser(myFrame, patient.getPatient());
				pdb.addDeleteAdmissionListener(myFrame);
				pdb.showAsModal(AdmittedPatientBrowser.this);
			}
		});
		return buttonData;
	}

	private JButton getButtonPatientFolderBrowser() {
		JButton buttonPatientFolderBrowser = new JButton(MessageBundle.getMessage("angal.admission.patientfolder"));
		buttonPatientFolderBrowser.setMnemonic(KeyEvent.VK_S);
		buttonPatientFolderBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.patientfolder"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				new PatientFolderBrowser(myFrame, patient.getPatient()).showAsModal(AdmittedPatientBrowser.this);
			}
		});
		return buttonPatientFolderBrowser;
	}

	private JButton getButtonTherapy() {
		JButton buttonTherapy = new JButton(MessageBundle.getMessage("angal.admission.therapy"));
		buttonTherapy.setMnemonic(KeyEvent.VK_T);
		buttonTherapy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.therapy"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				TherapyEdit therapy = new TherapyEdit(AdmittedPatientBrowser.this, patient.getPatient(), patient.getAdmission() != null);
				therapy.setLocationRelativeTo(null);
				therapy.setVisible(true);
				
			}
		});
		return buttonTherapy;
	}

	private JButton getButtonMerge() {
		JButton buttonMerge = new JButton(MessageBundle.getMessage("angal.admission.merge"));
		buttonMerge.setMnemonic(KeyEvent.VK_M);
		buttonMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRowCount() != 2) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseselecttwopatients"),
							MessageBundle.getMessage("angal.admission.merge"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				int[] indexes = table.getSelectedRows();
				
				Patient mergedPatient;
				Patient patient1 = ((AdmittedPatient)table.getValueAt(indexes[0], -1)).getPatient();
				Patient patient2 = ((AdmittedPatient)table.getValueAt(indexes[1], -1)).getPatient();
				
				//MergePatient mergedPatient = new MergePatient(patient1, patient2);
				
				if (patient1.getSex() != patient2.getSex()) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.selectedpatientshavedifferentsex"),
							MessageBundle.getMessage("angal.admission.merge"), JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				//Select most recent patient
				if (patient1.getCode() > patient2.getCode()) { 
					mergedPatient = patient1;
				}
				else { 
					mergedPatient = patient2;
					patient2 = patient1;
				}
				//System.out.println("mergedPatient: " + mergedPatient.getCode());

				//ASK CONFIRMATION
				int ok = JOptionPane.showConfirmDialog(null, 
						MessageBundle.getMessage("angal.admission.withthisoperationthepatient")+"\n"+MessageBundle.getMessage("angal.admission.code")+": "+
						patient2.getCode() + " " + patient2.getName() + " " + patient2.getAge() + " " + patient2.getAddress() +"\n"+
						MessageBundle.getMessage("angal.admission.willbedeletedandhisherhistorytransferedtothepatient")+"\n"+MessageBundle.getMessage("angal.admission.code")+": "+
						mergedPatient.getCode() + " " + mergedPatient.getName() + " " + mergedPatient.getAge() + " " + mergedPatient.getAddress() +"\n"+
						MessageBundle.getMessage("angal.admission.continue"),
						MessageBundle.getMessage("angal.admission.merge"), 
						JOptionPane.YES_NO_OPTION);
				if (ok != JOptionPane.YES_OPTION) return;
				
				if (mergedPatient.getName().toUpperCase().compareTo(
						patient2.getName().toUpperCase()) != 0) {
					String[] names = {mergedPatient.getName(), patient2.getName()};
					String whichName = (String) JOptionPane.showInputDialog(null, 
							MessageBundle.getMessage("angal.admission.pleaseselectthefinalname"), 
							MessageBundle.getMessage("angal.admission.differentnames"), 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							names, 
							null);
					if (whichName == null) return;
					if (whichName.compareTo(names[1]) == 0) {
						//patient2 name selected
						mergedPatient.setFirstName(patient2.getFirstName());
						mergedPatient.setSecondName(patient2.getSecondName());
					}
				}
				if (mergedPatient.getBirthDate() != null &&
						mergedPatient.getAgetype().compareTo("") == 0) {
					//mergedPatient only Age
					Date bdate2 = patient2.getBirthDate();
					int age2 = patient2.getAge();
					String ageType2 = patient2.getAgetype();
					if (bdate2 != null) {
						//patient2 has BirthDate
						mergedPatient.setAge(age2);
						mergedPatient.setBirthDate(bdate2);
					}
					if (bdate2 != null && ageType2.compareTo("") != 0) {
						//patient2 has AgeType 
						mergedPatient.setAge(age2);
						mergedPatient.setAgetype(ageType2);
					}
				}
				
				if (mergedPatient.getAddress().compareTo("") == 0)
					mergedPatient.setAddress(patient2.getAddress());
				
				if (mergedPatient.getCity().compareTo("") == 0)
					mergedPatient.setCity(patient2.getCity());
				
				if (mergedPatient.getNextKin().compareTo("") == 0)
					mergedPatient.setNextKin(patient2.getNextKin());
				
				if (mergedPatient.getTelephone().compareTo("") == 0)
					mergedPatient.setTelephone(patient2.getTelephone());
				
				if (mergedPatient.getMother_name().compareTo("") == 0)
					mergedPatient.setMother_name(patient2.getMother_name());
				
				if (mergedPatient.getMother() == 'U')
					mergedPatient.setMother(patient2.getMother());
				
				if (mergedPatient.getFather_name().compareTo("") == 0)
					mergedPatient.setFather_name(patient2.getFather_name());
				
				if (mergedPatient.getFather() == 'U')
					mergedPatient.setFather(patient2.getFather());
				
				if (mergedPatient.getBloodType().compareTo("") == 0)
					mergedPatient.setBloodType(patient2.getBloodType());
				
				if (mergedPatient.getHasInsurance() == 'U')
					mergedPatient.setHasInsurance(patient2.getHasInsurance());
				
				if (mergedPatient.getParentTogether() == 'U')
					mergedPatient.setParentTogether(patient2.getParentTogether());
				
				if (mergedPatient.getNote().compareTo("") == 0)
					mergedPatient.setNote(patient2.getNote());
				else {
					String note = mergedPatient.getNote();
					mergedPatient.setNote(patient2.getNote()+"\n\n"+note);
				}

				PatientBrowserManager patManager = new PatientBrowserManager();
				if (patManager.mergePatientHistory(mergedPatient, patient2)) {
					fireMyDeletedPatient(patient2);
				}
			}
		});
		return buttonMerge;
	}
	
	private JButton jButtonReport;
	private JButton getJButtonReport() {
		if (jButtonReport == null) {
			jButtonReport = new JButton();
			jButtonReport.setText(MessageBundle.getMessage("angal.billbrowser.report")); //$NON-NLS-1$
			jButtonReport.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					
					ArrayList<String> options = new ArrayList<String>();
					options.add("My Registered Patient");
					options.add("My Admissions");
					options.add("My Discharges");
					
					Icon icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$
					String option = (String) JOptionPane.showInputDialog(AdmittedPatientBrowser.this, 
							"Please select a report", 
							"Report", 
							JOptionPane.INFORMATION_MESSAGE, 
							icon, 
							options.toArray(), 
							options.get(0));
					
					if (option == null) return;
					
					String from = null;
					String to = null;
					
					int i = 0;
					
					UserBrowsingManager sessionManager = new UserBrowsingManager();
					String user = MainMenu.getUser();
					ArrayList<Session> sessions;
					if (user.equals("admin")) {
						sessions = sessionManager.getSessionByUser(80);
					} else {
						sessions = sessionManager.getSessionByUser(user, 40);
					}
					
					JList jList = new JList(sessions.toArray());
					jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
					jList.setFont(new Font("Arial", Font.PLAIN, 20));
					JScrollPane scrollPane = new JScrollPane(jList);
					int r = JOptionPane.showConfirmDialog(
							AdmittedPatientBrowser.this,
		                    scrollPane,
		                    "Session",
		                    JOptionPane.OK_CANCEL_OPTION, 
			        		JOptionPane.PLAIN_MESSAGE);
					
					if (r != JOptionPane.OK_OPTION)	return;
					
					int[] indexes = jList.getSelectedIndices();
					GregorianCalendar login = ((Session) jList.getModel().getElementAt(indexes[indexes.length - 1])).getLogin();
					GregorianCalendar logout = ((Session) jList.getModel().getElementAt(indexes[0])).getLogout();
					if (logout == null) logout = new GregorianCalendar();
					
					from = formatDateTimeReport(login);
					to = formatDateTimeReport(logout);
					
					//System.out.println("First index: " + indexes[indexes.length - 1] + " - From: " + from);
					//System.out.println("Last index: " + indexes[0] + " - To: " + to);
					
					if (options.indexOf(option) == i) {
						
						new GenericReportFromDateToDate(from, to, "patient_report", "patient_report", false);
						return;
					}
					if (options.indexOf(option) == ++i) {
						
						new GenericReportFromDateToDate(from, to, "AdmissionUserInDate", "AdmissionUserInDate", false);
						return;
					}
					if (options.indexOf(option) == ++i) {
						
						new GenericReportFromDateToDate(from, to, "DischargeUserInDate", "DischargeUserInDate", false);
						return;
					}
				}
			});
		}
		return jButtonReport;
	}

	private JButton getButtonClose() {
		JButton buttonClose = new JButton(MessageBundle.getMessage("angal.admission.close"));
		buttonClose.setMnemonic(KeyEvent.VK_C);
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//to free Memory
				if (pPatient != null) pPatient.clear();
				if (wardList != null) wardList.clear();
				dispose();
			}
		});
		return buttonClose;
	}
	
	public String formatDateTimeReport(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  //$NON-NLS-1$
		return format.format(time.getTime());
	}
	
	private void filterPatient(String key) {
		table.setModel(new AdmittedPatientBrowserModel(key));
		rowCounter.setText(rowCounterText + ": " + table.getRowCount());
		searchString.requestFocus();
	}
	
	private void searchPatient() {
		String key = searchString.getText();
		if (key.equals("")) {
			int ok = JOptionPane.showConfirmDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.thiscouldretrievealargeamountofdataproceed"), MessageBundle
					.getMessage("angal.hospital"), JOptionPane.OK_CANCEL_OPTION);
			if (ok != JOptionPane.OK_OPTION)
				return;
		}
		if (MainMenu.checkUserGrants("btndatamalnut")) {
			String malnutrition = "All";
			if (tfuVisitYes.isSelected()) malnutrition = "Y";
			else if (tfuVisitNo.isSelected()) malnutrition = "N";
			pPatient = manager.getAdmittedPatients(key, malnutrition);
		} else {
			pPatient = manager.getAdmittedPatients(key);
		}
		filterPatient(null);
	}
	
	private JButton getJSearchButton() {
		if (jSearchButton == null) {
			jSearchButton = new JButton();
			jSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			jSearchButton.setPreferredSize(new Dimension(20, 20));
			jSearchButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					try {
						BusyState.setBusyState(AdmittedPatientBrowser.this, true);
						searchPatient();
					} finally {
						BusyState.setBusyState(AdmittedPatientBrowser.this, false);
					}
				}
			});
		}
		return jSearchButton;
	}
	
	private JPanel setMyBorder(JPanel c, String title) {
		javax.swing.border.Border b2 = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(title), BorderFactory
						.createEmptyBorder(0, 0, 0, 0));
		c.setBorder(b2);
		return c;
	}

	class AdmittedPatientBrowserModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		ArrayList<AdmittedPatient> patientList = new ArrayList<AdmittedPatient>();
		
		public AdmittedPatientBrowserModel(String key) {
			for (AdmittedPatient ap : pPatient) {
				Admission adm = ap.getAdmission();
				// if not admitted stripes admitted
				if (((String) patientClassBox.getSelectedItem())
						.equals(patientClassItems[2])) {
					if (adm != null)
						continue;
				}
				// if admitted stripes not admitted
				else if (((String) patientClassBox.getSelectedItem())
						.equals(patientClassItems[1])) {
					if (adm == null)
						continue;
				}

				// if all or admitted filters not matching ward
				if (!((String) patientClassBox.getSelectedItem())
						.equals(patientClassItems[2])) {
					if (adm != null) {
						int cc = -1;
						for (int j = 0; j < wardList.size(); j++) {
							if (adm.getWardId().equalsIgnoreCase(
									wardList.get(j).getCode())) {
								cc = j;
								break;
							}
						}
						if (!wardCheck[cc].isSelected())
							continue;
					}
				}

				if (key != null) {
					
					String s = key + lastKey;
					s.trim();
					String[] tokens = s.split(" ");

					if (!s.equals("")) {
						String name = ap.getPatient().getSearchString();
						int a = 0;
						for (int j = 0; j < tokens.length ; j++) {
							String token = tokens[j].toLowerCase();
							if (name.contains(token)) {
								a++;
							}
						}
						if (a == tokens.length) patientList.add(ap);
					} else patientList.add(ap);
				} else patientList.add(ap);
			}
		}

		public int getRowCount() {
			if (patientList == null)
				return 0;
			return patientList.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			AdmittedPatient admPat = patientList.get(r);
			Patient patient = admPat.getPatient();
			Admission admission = admPat.getAdmission();
			if (c == -1) {
				return admPat;
			} else if (c == 0) {
				return patient.getCode();
			} else if (c == 1) {
				return patient.getName();
			} else if (c == 2) {
				return patient.getFormattedAge();
			} else if (c == 3) {
				return patient.getSex();
			} else if (c == 4) {
				return patient.getInformations();
			} else if (c == 5) {
				if (admission == null) {
					return new String("");
				} else {
					for (int i = 0; i < wardList.size(); i++) {
						if (wardList.get(i).getCode()
								.equalsIgnoreCase(admission.getWardId())) {
							return wardList.get(i).getDescription();
						}
					}
					return new String("?");
				}
			}

			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}
	
	
	class CenterTableCellRenderer extends DefaultTableCellRenderer {  
		   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {  
		   
			Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(CENTER);	   
			return cell;
	   }
	}

}
