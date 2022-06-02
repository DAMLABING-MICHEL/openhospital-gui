package org.isf.stat.reportlauncher.gui;

/*--------------------------------------------------------
 * ReportLauncher - lancia tutti i report che come parametri hanno
 * 					anno e mese
 * 					la classe prevede l'inizializzazione attraverso 
 *                  anno, mese, nome del report (senza .jasper)
 *---------------------------------------------------------
 * modification history
 * 01/01/2006 - rick - prima versione. lancia HMIS1081 e HMIS1081 
 * 11/11/2006 - ross - resa barbaramente generica (ad angal)
 * 16/11/2014 - eppesuig - show WAIT_CURSOR during generateReport()
 *-----------------------------------------------------------------*/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.stat.manager.GenericReportMY;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.VoDateTextField;
import org.isf.xmpp.gui.CommunicationFrame;
import org.isf.xmpp.manager.Interaction;

public class ReportLauncher extends ModalJFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pfrmExactWidth = 500;
	private int pfrmExactHeight = 165;
	private int pfrmBordX;
	private int pfrmBordY;
	private JPanel jPanel = null;
	private JPanel jButtonPanel = null;
	private JButton jCloseButton = null;
	private JPanel jContentPanel = null;
	private JButton jOkButton = null;
	private JButton jCSVButton = null;
	private JPanel jMonthPanel = null;
	private JLabel jMonthLabel = null;
	private JComboBox jMonthComboBox = null;
	private JLabel jYearLabel = null;
	private JComboBox jYearComboBox = null;
	private JLabel jFromDateLabel = null;
	private JLabel jToDateLabel = null;
	private VoDateTextField jToDateField = null;
	private VoDateTextField jFromDateField = null;
	private JTextField jToDateAndTimeField = null;
	private JTextField jFromDateAndTimeField = null;
	
	
	private JLabel jRptLabel = null;
	private JComboBox jRptComboBox = null;
	
	private final int BUNDLE = 0;
	private final int FILENAME = 1;
	private final int TYPE = 2;
	
	private String[][] reportMatrix = {
//		{"angal.stat.registeredpatient", 				"OH001_RegisteredPatients", 					"twodates"},
		{"angal.stat.registeredpatientbyprovenance", 	"OH002_RegisteredPatientsByProvenance", 		"twodates"},
		{"angal.stat.registeredpatientbyageandsex", 	"OH003_RegisteredPatientsByAgeAndSex", 			"twodates"},
		{"OH004 - Incomes All by PriceCodes", 			"OH004_IncomesAllByPriceCodes", 				"twodates"},
		{"OH005 - Incomes All by Categories", 			"OH005_IncomesAllByCategories", 				"twodates"},
		{"OH006 - Incomes OPD by PriceCodes", 			"OH006_IncomesOpdByPriceCodes", 				"twodates"},
		{"OH007 - Incomes IPD by PriceCodes", 			"OH007_IncomesIpdByPriceCodes", 				"twodates"},
		{"OH008A - Incomes Drugs by Amount",		    "OH008A_IncomesDrugsByAmount", 					"twodates"},
		{"OH008B - Incomes Drugs by Code", 			    "OH008B_IncomesDrugsByCode", 					"twodates"},
		{"angal.stat.inpatientreport", 					"OH009_InPatientReport", 						"twodates"},
		{"angal.stat.outpatientreport", 				"OH010_OutPatientReport", 						"twodates"},
		{"OH011 - Admission Total Income", 				"ADM_cost_betweentwodates_report", 				"twodates"},
		{"OH012 - OPD Average Income by Age",			"OPD_AVG_COST_AGE", 							"twodates"},
		{"OH013 - Admission - Prescriptions - Bills",	"PatientAdmissionPrescriptionBillReport", 		"twodates"},
		{"OH014 - Admissions Report",					"IPD_ADMISSION", 								"twodates"},
		{"OH015 - Discharges Report",					"IPD_DISCHARGE", 								"twodates"},
		{"OH016 - Registered Patients",					"PATIENT", 										"twodates"},
		{"OH017 - Exemption Report",					"OH017_ExemptionReport",						"twodates"},
		{"OH018 - Gyne FOC",							"OH018_GyneFOC",								"twodates"},
		{"OH019 - Company & Private Patients",			"OH019_PrivatePatientsBill",					"twodates"},
		{"OH020 - (New!) Admission AVG Cost",			"OH020_AdmissionAverageCost",					"twodates_java"},
		{"OH021 - (New!) OPD AVG Cost",					"OH021_OPDAverageCost",							"twodates_java"},
		{"OH022 - Chronic OPD Report",					"ChronicOpdReport",								"twodates"},
		{"OH023A - Revenues, Incomes & Receivables",	"OH023_BillsReport",							"twodates"},
		{"OH023B - Revenues, Incomes & Receivables (Monthly)",	"OH023_BillsReport_monthly",			"twodates"},
		{"OH024 - Incomes & Exemptions",				"OH024_Incomes_and_Exemptions",					"twodates"},
		{"OH025 - TFU Zonal Report",					"WolissoTfuZonalReport",						"twodates"},
//		{"angal.stat.morbidityandmortalityinipd", 		"FMOH_Ver102_IPD", 								"twodates"},
//		{"angal.stat.pageonecensusinfo", 				"hmis108_cover", 								"twodates"},
//		{"angal.stat.pageonereferrals", 				"hmis108_referrals", 							"twodates"},
//		{"angal.stat.pageoneoperations", 				"hmis108_operations", 							"twodates"},
//		{"angal.stat.inpatientdiagnosisin", 			"hmis108_adm_by_diagnosis_in", 					"twodates"},
//		{"angal.stat.inpatientdiagnosisout", 			"hmis108_adm_by_diagnosis_out", 				"twodates"},
//		{"angal.stat.opdattendance", 					"hmis105_opd_attendance", 						"twodates"},
//		{"angal.stat.opdreferrals", 					"hmis105_opd_referrals", 						"twodates"},
//		{"angal.stat.opdbydiagnosis", 					"hmis105_opd_by_diagnosis", 					"twodates"},
//		{"angal.stat.labmonthlyformatted", 				"hmis055b_lab_monthly_formatted", 				"twodates"},
//		{"angal.stat.weeklyepidemsurveil", 				"hmis033_weekly_epid_surv", 					"twodates"},
//		{"angal.stat.weeklyepidemsurveilunder5", 		"hmis033_weekly_epid_surv_under_5", 			"twodates"},
//		{"angal.stat.weeklyepidemsurveilover5", 		"hmis033_weekly_epid_surv_over_5", 				"twodates"},
		{"OH026 - PregnancyCare Report", 				"PregnancyCareReport", 							"twodates"}
	};
	
	private JComboBox shareWith=null;//nicola
	Interaction userOh=null;

	
	
//	private final JFrame myFrame;
	
	/**
	 * This is the default constructor
	 */
	public ReportLauncher() {
		super();
//		myFrame = this;
		this.setResizable(true);
		initialize();
		setVisible(true);
	}

	/**
	 * This method initializes this	
	 * 	
	 * @return void	
	 */
	private void initialize() {
		this.setTitle(MessageBundle.getMessage("angal.stat.reportlauncher"));
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		pfrmBordX = (screensize.width / 3) - (pfrmExactWidth / 2);
		pfrmBordY = (screensize.height / 3) - (pfrmExactHeight / 2);
		this.setBounds(pfrmBordX,pfrmBordY,pfrmExactWidth,pfrmExactHeight);
		this.setContentPane(getJPanel());
		selectAction();
		pack();
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJButtonPanel(), BorderLayout.SOUTH);
			jPanel.add(getJContentPanel(), BorderLayout.CENTER);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.setLayout(new FlowLayout());
			if(GeneralData.XMPPMODULEENABLED)
				jButtonPanel.add(getComboShareReport(),null);
			jButtonPanel.add(getJOkButton(), null);
			jButtonPanel.add(getJCSVButton(), null);
			jButtonPanel.add(getJCloseButton(), null);
			
		}
		return jButtonPanel;
	}

	private JComboBox getComboShareReport() {
		userOh= new Interaction();
		Collection<String> contacts = userOh.getContactOnline();
		contacts.add("-- Share report with : Nobody --");
		shareWith = new JComboBox(contacts.toArray());
		shareWith.setSelectedItem("-- Share report with : Nobody --");
		return shareWith;
	}

	/**
	 * This method initializes jCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton.setText(MessageBundle.getMessage("angal.stat.close"));
			jCloseButton.setMnemonic(KeyEvent.VK_C);
			jCloseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
		}
		return jCloseButton;
	}

	/**
	 * This method initializes jContentPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPanel() {
		if (jContentPanel == null) {
			
			jContentPanel = new JPanel();
			jContentPanel.setLayout(new BorderLayout());
			
			JPanel rep1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

			rep1.add(getJParameterSelectionPanel());
			rep1 = setMyBorder(rep1, MessageBundle.getMessage("angal.stat.parametersselectionframe") + " ");
			
			jContentPanel.add(rep1, BorderLayout.NORTH);
			//jContentPanel.add(rep2, BorderLayout.SOUTH);
			
				
		}
		return jContentPanel;
	}

	
	
	private JPanel getJParameterSelectionPanel() {

		if (jMonthPanel == null) {

			jMonthPanel = new JPanel();
			jMonthPanel.setLayout(new FlowLayout());
			
			//final DateFormat dtf = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);
			//String dt = dtf.format(new java.util.Date());
			//Integer month = Integer.parseInt(dt.substring(3, 5));
			//Integer year = 2000 + Integer.parseInt(dt.substring(6, 8));

			java.util.GregorianCalendar gc = new java.util.GregorianCalendar();
			Integer month=gc.get(Calendar.MONTH);
			Integer year = gc.get(Calendar.YEAR);

			//System.out.println("m="+month +",y="+ year);
			
			jRptLabel = new JLabel();
			jRptLabel.setText(MessageBundle.getMessage("angal.stat.report"));
			
			
			jRptComboBox = new JComboBox();
			for (int i=0;i<reportMatrix.length;i++)
				jRptComboBox.addItem(MessageBundle.getMessage(reportMatrix[i][BUNDLE]));
			
			jRptComboBox.addActionListener(new ActionListener() {   
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (e.getActionCommand()!= null) {
						if (e.getActionCommand().equalsIgnoreCase("comboBoxChanged")) {
							selectAction();
						}
					}
				}
			});
			
			
			jMonthLabel = new JLabel();
			jMonthLabel.setText("        " + MessageBundle.getMessage("angal.stat.month"));
			
			jMonthComboBox = new JComboBox();
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.january"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.february"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.march"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.april"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.may"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.june"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.july"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.august"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.september"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.october"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.november"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.december"));

			jMonthComboBox.setSelectedIndex(month);

			jYearLabel = new JLabel();
			jYearLabel.setText("        " + MessageBundle.getMessage("angal.stat.year"));
			jYearComboBox = new JComboBox();

			for (int i=0;i<4;i++){
				jYearComboBox.addItem((year-i)+"");
			}
			
			jFromDateLabel = new JLabel();
			jFromDateLabel.setText(MessageBundle.getMessage("angal.stat.fromdate"));
			GregorianCalendar defaultDate = new GregorianCalendar();
			defaultDate.add(GregorianCalendar.DAY_OF_MONTH, -8);
			jFromDateField = new VoDateTextField("dd/mm/yyyy", defaultDate, 10);
			jToDateLabel = new JLabel();
			jToDateLabel.setText(MessageBundle.getMessage("angal.stat.todate"));
			defaultDate.add(GregorianCalendar.DAY_OF_MONTH, 7);
			jToDateField = new VoDateTextField("dd/mm/yyyy", defaultDate, 10);
			jToDateLabel.setVisible(false);
			jToDateField.setVisible(false);
			jFromDateLabel.setVisible(false);
			jFromDateField.setVisible(false);
			jFromDateAndTimeField = new JTextField(20);
			jToDateAndTimeField = new JTextField(20);
			jFromDateAndTimeField.setVisible(false);
			jToDateAndTimeField.setVisible(false);
			
			//jMonthPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
			jMonthPanel.add(jRptLabel, null);
			jMonthPanel.add(jRptComboBox, null);
			jMonthPanel.add(jMonthLabel, null);
			jMonthPanel.add(jMonthComboBox, null);
			jMonthPanel.add(jYearLabel, null);
			jMonthPanel.add(jYearComboBox, null);
			jMonthPanel.add(jFromDateLabel, null);
			jMonthPanel.add(jFromDateField, null);
			jMonthPanel.add(jFromDateAndTimeField, null);
			jMonthPanel.add(jToDateLabel, null);
			jMonthPanel.add(jToDateField, null);
			jMonthPanel.add(jToDateAndTimeField, null);
		}
		return jMonthPanel;
	}


	protected void selectAction() {
		String sParType="";
		int rptIndex=jRptComboBox.getSelectedIndex();
		sParType = reportMatrix[rptIndex][TYPE];
		if (sParType.equalsIgnoreCase("twodates")) {
			jMonthComboBox.setVisible(false);
			jMonthLabel.setVisible(false);
			jYearComboBox.setVisible(false);
			jYearLabel.setVisible(false);
			jFromDateLabel.setVisible(true);
			jFromDateField.setVisible(true);
			jToDateLabel.setVisible(true);
			jToDateField.setVisible(true);
			jFromDateAndTimeField.setVisible(false);
			jToDateAndTimeField.setVisible(false);
		}
		if (sParType.equalsIgnoreCase("twodatesandtime")) {
			jMonthComboBox.setVisible(false);
			jMonthLabel.setVisible(false);
			jYearComboBox.setVisible(false);
			jYearLabel.setVisible(false);
			jFromDateLabel.setVisible(true);
			jToDateLabel.setVisible(true);
			jFromDateField.setVisible(false);
			jToDateField.setVisible(false);
			jFromDateAndTimeField.setVisible(true);
			jToDateAndTimeField.setVisible(true);
		}
		if (sParType.equalsIgnoreCase("twodatesfrommonthyear")) {
			jMonthComboBox.setVisible(true);
			jMonthLabel.setVisible(true);
			jYearComboBox.setVisible(true);
			jYearLabel.setVisible(true);
			jFromDateLabel.setVisible(false);
			jFromDateField.setVisible(false);
			jToDateLabel.setVisible(false);
			jToDateField.setVisible(false);
			jFromDateAndTimeField.setVisible(false);
			jToDateAndTimeField.setVisible(false);
		}
		if (sParType.equalsIgnoreCase("monthyear")) {
			jMonthComboBox.setVisible(true);
			jMonthLabel.setVisible(true);
			jYearComboBox.setVisible(true);
			jYearLabel.setVisible(true);
			jFromDateLabel.setVisible(false);
			jFromDateField.setVisible(false);
			jToDateLabel.setVisible(false);
			jToDateField.setVisible(false);
			jFromDateAndTimeField.setVisible(false);
			jToDateAndTimeField.setVisible(false);
		}
	}

	private JButton getJOkButton() {
		if (jOkButton == null) {
			jOkButton = new JButton();
			jOkButton.setBounds(new Rectangle(15, 15, 91, 31));
			jOkButton.setText(MessageBundle.getMessage("angal.stat.launchreport"));
			jOkButton.addActionListener(new ActionListener() {   
				public void actionPerformed(ActionEvent e) {
					try {
						BusyState.setBusyState(ReportLauncher.this, true);
						generateReport(false);
					} finally {
						BusyState.setBusyState(ReportLauncher.this, false);
					}
				}
			});
		}
		return jOkButton;
	}
	
	private JButton getJCSVButton() {
		if (jCSVButton == null) {
			jCSVButton = new JButton();
			jCSVButton.setBounds(new Rectangle(15, 15, 91, 31));
			jCSVButton.setText("Excel");
			jCSVButton.addActionListener(new ActionListener() {   
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						BusyState.setBusyState(ReportLauncher.this, true);
						generateReport(true);
					} finally {
						BusyState.setBusyState(ReportLauncher.this, false);
					}
				}
			});
		}
		return jCSVButton;
	}
	
	protected void generateReport(boolean toCSV) {
		   
		int rptIndex=jRptComboBox.getSelectedIndex();
		Integer month = jMonthComboBox.getSelectedIndex()+1;
		Integer year = (Integer.parseInt((String)jYearComboBox.getSelectedItem()));
		String fromDate=jFromDateField.getText().trim();
		String toDate=jToDateField.getText().trim();
		String fromDateAndTime=jFromDateAndTimeField.getText().trim();
		String toDateAndTime=jToDateAndTimeField.getText().trim();
		
		if (rptIndex>=0) {
			String sParType = reportMatrix[rptIndex][TYPE];
			if (sParType.equalsIgnoreCase("twodates")) {
				new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], MessageBundle.getMessage(reportMatrix[rptIndex][BUNDLE]), toCSV);
				if (GeneralData.XMPPMODULEENABLED) {
					String user= (String)shareWith.getSelectedItem();
					CommunicationFrame frame= (CommunicationFrame)CommunicationFrame.getFrame();
					frame.sendMessage("011100100110010101110000011011110111001001110100 "+fromDate+" "+toDate+" "+reportMatrix[rptIndex][FILENAME],
							user, false);
				}
			}
			if (sParType.equalsIgnoreCase("twodatesandtime")) {
				new GenericReportFromDateToDate(fromDateAndTime, toDateAndTime, reportMatrix[rptIndex][FILENAME], MessageBundle.getMessage(reportMatrix[rptIndex][BUNDLE]), toCSV);
				if (GeneralData.XMPPMODULEENABLED) {
					String user= (String)shareWith.getSelectedItem();
					CommunicationFrame frame= (CommunicationFrame)CommunicationFrame.getFrame();
					frame.sendMessage("011100100110010101110000011011110111001001110100 "+fromDate+" "+toDate+" "+reportMatrix[rptIndex][FILENAME],
							user, false);
				}
			}
			if (sParType.equalsIgnoreCase("twodatesfrommonthyear")) {
				GregorianCalendar d = new GregorianCalendar();
				d.set(GregorianCalendar.DAY_OF_MONTH,1 );
				d.set(GregorianCalendar.MONTH, month-1);
				d.set(GregorianCalendar.YEAR, year);
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
				fromDate = sdf.format(d.getTime());
				d.set(GregorianCalendar.DAY_OF_MONTH, d.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
				toDate = sdf.format(d.getTime());
				new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], MessageBundle.getMessage(reportMatrix[rptIndex][BUNDLE]), toCSV);
				if (GeneralData.XMPPMODULEENABLED) {
					String user= (String)shareWith.getSelectedItem();
					CommunicationFrame frame= (CommunicationFrame)CommunicationFrame.getFrame();
					frame.sendMessage("011100100110010101110000011011110111001001110100 "+fromDate+" "+toDate+" "+reportMatrix[rptIndex][FILENAME],
							user, false);
				}
			}
			if (sParType.equalsIgnoreCase("monthyear")) {
				new GenericReportMY(month, year, reportMatrix[rptIndex][FILENAME], MessageBundle.getMessage(reportMatrix[rptIndex][BUNDLE]), toCSV);
				if (GeneralData.XMPPMODULEENABLED) {
					String user= (String)shareWith.getSelectedItem();
					CommunicationFrame frame= (CommunicationFrame)CommunicationFrame.getFrame();
					frame.sendMessage("011100100110010101110000011011110111001001110100 "+month+" "+year+" "+reportMatrix[rptIndex][FILENAME],
							user, false);
				}
			}
			if (sParType.equalsIgnoreCase("twodates_java")) {
				String sReportName = reportMatrix[rptIndex][FILENAME];
				
		        Object[] obj={fromDate,toDate,sReportName,toCSV};
		        Class<?> params[] = new Class[obj.length];
		        for (int i = 0; i < obj.length; i++) {
		            if (obj[i] instanceof Boolean) {
		                params[i] = Boolean.TYPE;
		            } else if (obj[i] instanceof String) {
		                params[i] = String.class;
		            }
		        }
		        
		        try {
		        	
					String methoName = sReportName; // methodname to be invoked
			        String className = sReportName; // Class name
			        Class<?> cls = Class.forName("org.isf.stat.java."+className);
			        Object _instance = cls.newInstance();
			        Method myMethod = cls.getDeclaredMethod(methoName, params);
			        myMethod.invoke(_instance, obj);
				
		        } catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				//new OH020_AdmissionAverageCost(fromDate, toDate, reportMatrix[rptIndex][FILENAME], toCSV);
				if (GeneralData.XMPPMODULEENABLED) {
					String user= (String)shareWith.getSelectedItem();
					CommunicationFrame frame= (CommunicationFrame)CommunicationFrame.getFrame();
					frame.sendMessage("011100100110010101110000011011110111001001110100 "+fromDate+" "+toDate+" "+reportMatrix[rptIndex][FILENAME],
							user, false);
				}
			}
		}
	
		
	}

	/*
	 * set a specific border+title to a panel
	 */
	private JPanel setMyBorder(JPanel c, String title) {
		javax.swing.border.Border b2 = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(title), BorderFactory
						.createEmptyBorder(0, 0, 0, 0));
		c.setBorder(b2);
		return c;
	}

}  
