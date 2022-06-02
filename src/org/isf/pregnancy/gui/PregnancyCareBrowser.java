package org.isf.pregnancy.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.isf.admission.gui.AdmissionBrowser;
import org.isf.admission.gui.AdmissionBrowser.AdmissionListener;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.model.PregnancyPatient;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patvac.gui.PatVacBrowser;
import org.isf.pregnancy.manager.PregnancyCareManager;
import org.isf.pregnancy.manager.PregnancyDeliveryManager;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.stat.manager.GenericReportDischargePatient;
import org.isf.stat.manager.PregnancyReport;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;

/**
 * 
 * @author Martin Reinstadler
 *
 */
public class PregnancyCareBrowser extends ModalJFrame implements
		PatientInsert.PatientListener, PatientInsertExtended.PatientListener,
		PregnancyEdit.PregnancyListener , AdmissionListener {

	private String[] pColums = {
			MessageBundle.getMessage("angal.admission.code"),
			MessageBundle.getMessage("angal.admission.name"),
			MessageBundle.getMessage("angal.admission.age"),
			MessageBundle.getMessage("angal.admission.address"),
			MessageBundle.getMessage("angal.admission.ward")};
	private int[] pColumwidth = { 20, 200, 20, 150, 70 };
	private String[] vColums = {
			MessageBundle.getMessage("angal.pregnancy.pregnancynumber"),
			MessageBundle.getMessage("angal.pregnancy.visitdate"),
			MessageBundle.getMessage("angal.pregnancy.visittype"),
			MessageBundle.getMessage("angal.pregnancy.visitnote") };
	private int[] vColumwidth = { 20, 40, 40, 220 };
	private static final long serialVersionUID = 1L;
	private PregnancyCareBrowser myFrame = null;
	private ArrayList<PregnancyPatient> pregnancyPatientList = new ArrayList<PregnancyPatient>();
	private ArrayList<PregnancyVisit> pregnancyvisits = null;
	private PregnancyCareManager pregMan = new PregnancyCareManager();
	private PregnancyDeliveryManager pregdelManager = new PregnancyDeliveryManager();
	private JTable patientTable = null;
	private JTable visitTable = null;
	private JButton newPatientButton = null;
	private JButton editPatientButton = null;
	private JButton deletePatientButton = null;
	private Patient patient = null;
	private PregnancyVisit pvisit;
	private JScrollPane patientScrollPane = null;
	private JScrollPane visitScrollPane = null;
	private JTextField searchPatientTextField = null;
	private ArrayList<JLabel> deltypeLabel = null;
	private ArrayList<JLabel> deltypeResLabel = null;
	private JButton jSearchButton = null;
	private String lastKey = "";
	private JButton jButtonVaccine;
	private DefaultTableModel model;
	protected boolean altKeyReleased = true;
	private HashMap<String, Ward> wardMap;

	/**
	 * Constructor called from the main menu
	 */
	public PregnancyCareBrowser() {
		setTitle(MessageBundle.getMessage("angal.pregnancy.patientsbrowser"));
		myFrame = this;
		initComponents();
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (pregnancyPatientList != null) pregnancyPatientList.clear();
				if (pregnancyvisits != null) pregnancyvisits.clear();
				dispose();
			}
		});
	}

	/**
	 * constructor for the AdmissionBrowser to see only the pregnancyvisits for
	 * the patient
	 * 
	 * @param admittedpatient
	 *            the admitted patient
	 */
	public PregnancyCareBrowser(Patient admittedpatient) {
		setTitle(MessageBundle.getMessage("angal.pregnancy.patientsbrowser"));
		myFrame = this;
		this.patient = admittedpatient;
		initComponents();
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (pregnancyPatientList != null) pregnancyPatientList.clear();
				if (pregnancyvisits != null) pregnancyvisits.clear();
				dispose();
			}
		});

	}

	/**
	 * intis the components
	 */
	private void initComponents() {
		
		if (!GeneralData.ENHANCEDSEARCH) {
			try {
		    	BusyState.setBusyState(PregnancyCareBrowser.this, true);
		    	pregnancyPatientList = pregMan.getPregnancyPatients(null);
		    } finally {
		    	BusyState.setBusyState(PregnancyCareBrowser.this, false);
		    }
		}
	    wardMap = WardBrowserManager.getWardsHashMap();
	    
		getContentPane().add(getPatientPanel(), BorderLayout.NORTH);
		getContentPane().add(getVisitPanel(), BorderLayout.CENTER);
		getContentPane().add(getPregnancyButtonPanel(), BorderLayout.SOUTH);		
	}

	private JPanel getPatientPanel() {
		JPanel dataPatientListPanel = new JPanel(new BorderLayout());
		JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dataPatientListPanel.add(navigation, BorderLayout.NORTH);
		dataPatientListPanel.add(getSearchPanel(), BorderLayout.WEST);
		dataPatientListPanel.add(getPatientScrollPane(), BorderLayout.CENTER);
		dataPatientListPanel.add(getPatientButtonPanel(), BorderLayout.EAST);
		return dataPatientListPanel;
	}

	private JPanel getVisitPanel() {
		JPanel visitListPanel = new JPanel(new BorderLayout());
		visitListPanel.add(getVisitScrollPane(), BorderLayout.NORTH);
		visitListPanel.add(getPregnancyDetailsPanel(), BorderLayout.EAST);
		return visitListPanel;
	}

	private JPanel getSearchPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPatientTextField = new JTextField(15);
		if (GeneralData.ENHANCEDSEARCH) {
			searchPatientTextField.addKeyListener(new KeyListener() {

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
			searchPatientTextField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {
					if (altKeyReleased) {
						lastKey = "";
						String s = "" + e.getKeyChar();
						if (Character.isLetterOrDigit(e.getKeyChar())) {
							lastKey = s;
						}
						filterPatient(searchPatientTextField.getText());
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
		searchPanel.add(searchPatientTextField, BorderLayout.CENTER);
		if (GeneralData.ENHANCEDSEARCH) searchPanel.add(getPatientSearchButton(), BorderLayout.EAST);
		searchPanel = setMyBorder(searchPanel, MessageBundle.getMessage("angal.admission.searchkey"));
		if (patient != null)
			searchPatientTextField.setEnabled(false);

		JPanel panelPregnantPrint = new JPanel();
		panelPregnantPrint.setLayout(new BoxLayout(panelPregnantPrint, BoxLayout.Y_AXIS));
		if (MainMenu.checkUserGrants("btnpregupddel")) panelPregnantPrint.add(getJButtonUpdateDelivery());
		panelPregnantPrint.add(getJButtonDeclarationBirth());
		panelPregnantPrint.add(getJButtonDeclarationCertificate());

		panel.add(searchPanel, BorderLayout.NORTH);
		panel.add(panelPregnantPrint, BorderLayout.SOUTH);
		
		return panel;
	}

	private JButton getJButtonUpdateDelivery() {
		JButton updateDelivery = new JButton(MessageBundle.getMessage("angal.pregnancy.updatedelivery"));
		updateDelivery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (visitTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
							MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
							MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				PregnancyVisit visit = (PregnancyVisit) visitTable.getValueAt(visitTable.getSelectedRow(), -1);
				if (visit.getType() == 0 ) {							
					//in this case, the visit ID is the admission Id
					int admission_id = visit.getVisitId();
					AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
					if(adPatient!=null){									
						patient=adPatient.getPatient();
					}
					else{
						JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
								MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
								MessageBundle.getMessage("angal.admission.editpatient"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					}									
					new AdmissionBrowser(myFrame, patient, admission_id, true);
				}
				else{
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
							MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
							MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
			}
		});
		updateDelivery.setPreferredSize(new Dimension(200, 20));
		updateDelivery.setMaximumSize(new Dimension(200, 20));
		updateDelivery.setMinimumSize(new Dimension(200, 20));
		return updateDelivery;
	}

	private JButton getJButtonDeclarationCertificate() {
		JButton declarationCertificate = new JButton(MessageBundle
				.getMessage("angal.pregnancy.declarationcertificate"));
		declarationCertificate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					PregnancyVisit visit = (PregnancyVisit) visitTable.getValueAt(visitTable.getSelectedRow(), -1);
					if (visit.getType() == 0 ) {							
						//in this case, the visit ID is the admission Id
						int admission_id = visit.getVisitId();
						int pat_id = visit.getPatID();
						new GenericReportDischargePatient(admission_id, pat_id, "certificateOfDeclaration");
						return;
					}
					else{
						JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
								MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
								MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
								JOptionPane.PLAIN_MESSAGE);
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
							MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
							MessageBundle
									.getMessage("angal.pregnancy.pleaseselectdelivery"),
							JOptionPane.PLAIN_MESSAGE);
					System.out.println("error selecting delivery");
				}
			}
		});
		declarationCertificate.setPreferredSize(new Dimension(200, 20));
		declarationCertificate.setMaximumSize(new Dimension(200, 20));
		declarationCertificate.setMinimumSize(new Dimension(200, 20));
		return declarationCertificate;
	}

	private JButton getJButtonDeclarationBirth() {
		JButton declarationBirth = new JButton(MessageBundle
				.getMessage("angal.pregnancy.declarationbirth"));
		declarationBirth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					PregnancyVisit visit = (PregnancyVisit) visitTable.getValueAt(visitTable.getSelectedRow(), -1);
					if (visit.getType() == 0 ) {
						int admission_id = visit.getVisitId();
						int pat_id = visit.getPatID();
						new GenericReportDischargePatient(admission_id, pat_id, "declarationOfBirth");
						return;
					}
					else{
						JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
								MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
								MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
								JOptionPane.PLAIN_MESSAGE);
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
							MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
							MessageBundle.getMessage("angal.pregnancy.pleaseselectdelivery"),
							JOptionPane.PLAIN_MESSAGE);
				//System.out.println("error selecting delivery");
				}
			}
		});
		declarationBirth.setPreferredSize(new Dimension(200, 20));
		declarationBirth.setMaximumSize(new Dimension(200, 20));
		declarationBirth.setMinimumSize(new Dimension(200, 20));
		return declarationBirth;
	}

	private JPanel setMyBorder(JPanel c, String title) {
		javax.swing.border.Border b2 = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(title),
				BorderFactory.createEmptyBorder(0, 0, 0, 0));
		c.setBorder(b2);
		return c;
	}

	private JButton getPatientSearchButton() {
		if (jSearchButton == null) {
			jSearchButton = new JButton();
			jSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			jSearchButton.setPreferredSize(new Dimension(20, 20));
			jSearchButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						BusyState.setBusyState(PregnancyCareBrowser.this, true);
						searchPatient();
					} finally {
						BusyState.setBusyState(PregnancyCareBrowser.this, false);
					}
				}
			});
		}
		if (patient != null)
			jSearchButton.setEnabled(false);
		return jSearchButton;
	}
	
	private void searchPatient() {
		String key = searchPatientTextField.getText();
		if (key.equals("")) {
			int ok = JOptionPane.showConfirmDialog(PregnancyCareBrowser.this, MessageBundle.getMessage("angal.admission.thiscouldretrievealargeamountofdataproceed"), MessageBundle
					.getMessage("angal.hospital"), JOptionPane.OK_CANCEL_OPTION);
			if (ok != JOptionPane.OK_OPTION)
				return;
		}
//		SelectPatient sp = new SelectPatient(PregnancyCareBrowser.this, key);
//		sp.addSelectionListener(PregnancyCareBrowser.this);
//		sp.pack();
//		sp.setVisible(true);
		pregnancyPatientList = pregMan.getPregnancyPatients(key);
		filterPatient(null);
	}

	private JPanel getPregnancyDetailsPanel() {

		JPanel panel = new JPanel();
		deltypeLabel = new ArrayList<JLabel>();
		deltypeResLabel = new ArrayList<JLabel>();
		for (int a = 0; a < 15; a++) {
			JLabel typeL = new JLabel("");
			JLabel typeR = new JLabel("");
			panel.add(typeL);
			panel.add(typeR);
			deltypeLabel.add(typeL);
			deltypeResLabel.add(typeR);
			typeL.setFont(new Font("Lucia Grande",
					0, 10));
			typeR.setFont(new Font("Lucia Grande",
					0, 10));

		}
		panel.setPreferredSize(new Dimension(180, 100));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		return panel;
	}

	private JPanel getPatientButtonPanel() {

		JPanel buttonPanel = new JPanel();
		BoxLayout layout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
		buttonPanel.setLayout(layout);
		int buttonsize = 0;
		if (MainMenu.checkUserGrants("btnpregnewpat")) {
			getButtonNewPatient();
		}
		if (MainMenu.checkUserGrants("btnpregeditpat")) {
			getButtonEditPatient();
		}
		if (MainMenu.checkUserGrants("btnpregdelpat")) {
			getButtonDeletePatient();
		}
		
		if (MainMenu.checkUserGrants("btnpregnewpat") && newPatientButton.getText().length() > buttonsize)
			buttonsize = newPatientButton.getText().length();
		if (MainMenu.checkUserGrants("btnpregeditpat") && editPatientButton.getText().length() > buttonsize)
			buttonsize = editPatientButton.getText().length();
		if (MainMenu.checkUserGrants("btnpregdelpat") && deletePatientButton.getText().length() > buttonsize)
			buttonsize = deletePatientButton.getText().length();
		
		if (MainMenu.checkUserGrants("btnpregnewpat")) {
			newPatientButton.setPreferredSize(new Dimension(180, 30));
			newPatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
			newPatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
			buttonPanel.add(newPatientButton);
		}
		if (MainMenu.checkUserGrants("btnpregeditpat")) {
			editPatientButton.setPreferredSize(new Dimension(180, 30));
			editPatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
			editPatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
			buttonPanel.add(editPatientButton);
		}
		if (MainMenu.checkUserGrants("btnpregdelpat")) {
			deletePatientButton.setPreferredSize(new Dimension(180, 30));
			deletePatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
			deletePatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
			buttonPanel.add(deletePatientButton);
		}
		// in the case the browser is opened from the admission
		return buttonPanel;
	}

	private JScrollPane getVisitScrollPane() {
		visitTable = new JTable(new PregnancyVisitBrowserModel());

		for (int i = 0; i < vColums.length; i++) {
			visitTable.getColumnModel().getColumn(i).setPreferredWidth(vColumwidth[i]);
		}

		int tableWidth = 0;
		for (int i = 0; i < vColumwidth.length; i++) {
			tableWidth += vColumwidth[i];
		}
		visitScrollPane = new JScrollPane(visitTable);
		visitScrollPane.setPreferredSize(new Dimension(tableWidth + 400, 200));
		return visitScrollPane;
	}
	
	private JTable getPatientTable() {
		if (patientTable == null) {
			model = new PregnancyPatientBrowserModel(null);
			patientTable = new JTable(model);
			patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			for (int i = 0; i < pColums.length; i++) {
				patientTable.getColumnModel().getColumn(i).setPreferredWidth(pColumwidth[i]);
			}
			
			patientTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (patientTable.getSelectedRow() >= 0) {
						AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(),-1);
						patient = adPatient.getPatient();
						filterPregnancyDetails();
						filterVisit();
					}
				}
			});
		}
		return patientTable;
	}

	private JScrollPane getPatientScrollPane() {
		patientScrollPane = new JScrollPane(getPatientTable());
		int tableWidth = 0;
		for (int i = 0; i < pColumwidth.length; i++) {
			tableWidth += pColumwidth[i];
		}
		patientScrollPane.setPreferredSize(new Dimension(tableWidth + 400, 300));
		return patientScrollPane;
	}

	private JPanel getPregnancyButtonPanel() {
		JPanel buttonPanel = new JPanel();
		if (MainMenu.checkUserGrants("btnpregnewpreg")) buttonPanel.add(getButtonNewPregnancy());
		if (MainMenu.checkUserGrants("btnpregnewpren")) buttonPanel.add(getButtonNewPrenatalVisit());
		if (MainMenu.checkUserGrants("btnpregnewpost")) buttonPanel.add(getButtonNewPostnatalVisit());
		if (MainMenu.checkUserGrants("btnpregregdel")) buttonPanel.add(getButtonDelivery());
		if (MainMenu.checkUserGrants("btnpregedit")) buttonPanel.add(getButtonVisitDetails());
//		if (MainMenu.checkUserGrants("btnadmexamination")) buttonPanel.add(getJButtonExams());
//		if (MainMenu.checkUserGrants("btnpregpatvac")) buttonPanel.add(getJButtonVaccin());
		if (MainMenu.checkUserGrants("btnpregdel")) buttonPanel.add(getButtonDeleteVisit());
		buttonPanel.add(getReportButton());
		buttonPanel.add(getButtonClose());
		return buttonPanel;
	}

	private JButton getButtonNewPatient() {
		if (newPatientButton == null) {
			newPatientButton = new JButton(MessageBundle.getMessage("angal.admission.newpatient"));
			newPatientButton.setMnemonic(KeyEvent.VK_N);
			newPatientButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {

					if (GeneralData.PATIENTEXTENDED) {
						PatientInsertExtended newrecord = new PatientInsertExtended(PregnancyCareBrowser.this, new Patient(), true);
						newrecord.addPatientListener(PregnancyCareBrowser.this);
						newrecord.setVisible(true);
					} else {
						PatientInsert newrecord = new PatientInsert(PregnancyCareBrowser.this, new Patient(), true);
						newrecord.addPatientListener(PregnancyCareBrowser.this);
						newrecord.setVisible(true);
					}

				}
			});
		}
		if (patient != null) newPatientButton.setEnabled(false);
		return newPatientButton;
	}

	private JButton getButtonEditPatient() {
		if (editPatientButton == null) {
			editPatientButton = new JButton(MessageBundle.getMessage("angal.admission.editpatient"));
			editPatientButton.setMnemonic(KeyEvent.VK_E);
			editPatientButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					if (patientTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
								MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
								MessageBundle.getMessage("angal.admission.editpatient"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					}
					
					AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
					if(adPatient!=null){
						patient=adPatient.getPatient();
					}
					else{
						patient=null;
					}
					if (GeneralData.PATIENTEXTENDED) {
						PatientInsertExtended editrecord = new PatientInsertExtended(PregnancyCareBrowser.this, patient, false);
						editrecord.addPatientListener(PregnancyCareBrowser.this);
						editrecord.setVisible(true);
					} else {
						PatientInsert editrecord = new PatientInsert(PregnancyCareBrowser.this, patient, false);
						editrecord.addPatientListener(PregnancyCareBrowser.this);
						editrecord.setVisible(true);
					}
				}
			});
		}
		if (patient != null) editPatientButton.setEnabled(false);
		return editPatientButton;
	}

	private JButton getButtonDeletePatient() {
		if (deletePatientButton == null) {
			deletePatientButton = new JButton(MessageBundle.getMessage("angal.admission.deletepatient"));
			deletePatientButton.setMnemonic(KeyEvent.VK_T);
			deletePatientButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					if (patientTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(PregnancyCareBrowser.this,
								MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
								MessageBundle.getMessage("angal.admission.deletepatient"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					}
					
					AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
					if(adPatient!=null){
						patient=adPatient.getPatient();
					}
					else{
						patient=null;
					}
					
//					patient = (Patient) patientTable.getValueAt(atientTable.getSelectedRow(), -1);
					int n = JOptionPane.showConfirmDialog(PregnancyCareBrowser.this,
							MessageBundle.getMessage("angal.admission.deletepatient") + " " + patient.getName() + "?",
							MessageBundle.getMessage("angal.admission.deletepatient"),
							JOptionPane.YES_NO_OPTION);

					if (n == JOptionPane.YES_OPTION) {
						PatientBrowserManager manager = new PatientBrowserManager();
						boolean result = manager.deletePatient(patient);
						if (result) {
							AdmissionBrowserManager abm = new AdmissionBrowserManager();
							ArrayList<Admission> patientAdmissions = abm.getAdmissions(patient);
							for (Admission elem : patientAdmissions) {
								abm.setDeleted(elem.getId());
							}
							fireMyDeletedPatient(patient);
						}
					}
				}
			});
		}
		if (patient != null) deletePatientButton.setEnabled(false);
		return deletePatientButton;
	}

	public void fireMyDeletedPatient(Patient p) {

		if (pregnancyPatientList == null) {
			filterPatient(null);
		}
		int cc = 0;
		boolean found = false;
		for (AdmittedPatient elem : pregnancyPatientList) {
			if (elem.getPatient().getCode() == p.getCode()) {
				found = true;
				break;
			}
			cc++;
		}
		if (found) {
			pregnancyPatientList.remove(cc);
			filterPatient(null);
		}
	}

	private JButton getButtonNewPrenatalVisit() {
		JButton buttonNewVisit = new JButton(MessageBundle.getMessage("angal.pregnancy.newprenatalvisit"));
		buttonNewVisit.setMnemonic(KeyEvent.VK_T);
		buttonNewVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this,
							MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle.getMessage("angal.pregnancy.newprenatalvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}

				if (pregnancyvisits.size() < 1
						|| pregnancyvisits.get(0).getType() != -1) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this,
							MessageBundle.getMessage("angal.pregnancy.pleaseinsertpregnancy"),
							MessageBundle.getMessage("angal.pregnancy.prenatalvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				
				PregnancyEdit b = new PregnancyEdit(myFrame, patient,pregnancyvisits, null, -1, false);
				b.addPregnancyListener(PregnancyCareBrowser.this);
				b.setVisible(true);

			}
		});
		return buttonNewVisit;
	}

	private JButton getButtonDeleteVisit() {
		JButton buttonDeleteVisit = new JButton(MessageBundle.getMessage("angal.pregnancy.deletevisit"));
		buttonDeleteVisit.setMnemonic(KeyEvent.VK_T);
		buttonDeleteVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this,
							MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle.getMessage("angal.pregnancy.deletevisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if (visitTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
							MessageBundle.getMessage("angal.pregnancy.pleaseselectvisit"),
							MessageBundle.getMessage("angal.pregnancy.deletevisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				PregnancyVisit visit = (PregnancyVisit) visitTable.getValueAt(visitTable.getSelectedRow(), -1);
				if (visit.getType() == 0 || visit.getType() == 10) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this,
							MessageBundle.getMessage("angal.pregnancy.pleaseselectvisit"),
							MessageBundle.getMessage("angal.pregnancy.deletevisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				int ok = JOptionPane.showConfirmDialog(PregnancyCareBrowser.this, MessageBundle.getMessage("angal.pregnancy.doyoureallywanttodeletetheselectedvisit"));
				if (ok == JOptionPane.OK_OPTION) {
					boolean result = pregMan.deletePregnancyVisitAndResults(visit.getVisitId());
					if (result && pregnancyvisits.size() == 1) {
						result = pregMan.deletePregnancy(visit.getPregnancyId());
					}
					filterVisit();
				} else return;
			}
		});
		return buttonDeleteVisit;
	}

	private JButton getReportButton() {
		JButton jButtonReport = new JButton();
		jButtonReport.setText(MessageBundle.getMessage("angal.pregnancy.report"));
		jButtonReport.setMnemonic(KeyEvent.VK_R);
		jButtonReport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
							MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle.getMessage("angal.pregnancy.report"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if (visitTable.getSelectedRow() > -1) {
					ArrayList<String> options = new ArrayList<String>();
					options.add(MessageBundle.getMessage("angal.pregnancy.singlepregnancy"));
					options.add(MessageBundle.getMessage("angal.pregnancy.allpregnancy"));
					String option = (String) JOptionPane.showInputDialog(PregnancyCareBrowser.this,
							MessageBundle.getMessage("angal.pregnancy.pleaseselectareport"),
							MessageBundle.getMessage("angal.pregnancy.report"),
							JOptionPane.INFORMATION_MESSAGE, null, options.toArray(), options.get(0));
					if (option == null)
						return;
					if (options.indexOf(option) == 0) {
						pvisit = (PregnancyVisit) visitTable.getValueAt(visitTable.getSelectedRow(), -1);
						new PregnancyReport(patient.getCode(), pvisit.getPregnancyId());

					} else if (options.indexOf(option) == 1) {
						new PregnancyReport(patient.getCode(), 0);
					}
				} else {
					if (pregnancyvisits.size() < 1) {
						JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
								MessageBundle.getMessage("angal.pregnancy.novisits"),
								MessageBundle.getMessage("angal.pregnancy.report"),
								JOptionPane.PLAIN_MESSAGE);
						return;

					}
					new PregnancyReport(patient.getCode(), 0);
				}

			}
		});
		return jButtonReport;
	}

	private JButton getButtonNewPostnatalVisit() {
		JButton buttonNewVisit = new JButton(MessageBundle.getMessage("angal.pregnancy.newpostnatalvisit"));
		buttonNewVisit.setMnemonic(KeyEvent.VK_T);
		buttonNewVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this,
							MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle.getMessage("angal.pregnancy.newpostnatalvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if (pregnancyvisits.size() < 1) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this,
							MessageBundle.getMessage("angal.pregnancy.pleaseinsertpregnancy"),
							MessageBundle.getMessage("angal.pregnancy.postnatalvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				PregnancyEdit b = new PregnancyEdit(myFrame, patient,pregnancyvisits, null, 1, false);
				b.addPregnancyListener(PregnancyCareBrowser.this);
				b.setVisible(true);

			}
		});
		return buttonNewVisit;
	}

	private JButton getButtonNewPregnancy() {
		JButton buttonNewPregnancy = new JButton(MessageBundle.getMessage("angal.pregnancy.newpregnancy"));
		buttonNewPregnancy.setMnemonic(KeyEvent.VK_T);
		buttonNewPregnancy.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this,
							MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle.getMessage("angal.pregnancy.newpregnancyvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
				patient=adPatient.getPatient();
				
				if (pregMan.hasActivePregnancy(patient)) {
					int ok = JOptionPane.showConfirmDialog(PregnancyCareBrowser.this,
						MessageBundle.getMessage("angal.pregnancy.doyouwanttorecordanewone"),
						MessageBundle.getMessage("angal.pregnancy.newdelivery"),
						JOptionPane.YES_NO_OPTION);
					if (ok != JOptionPane.YES_OPTION) return;
				}

				PregnancyEdit b = new PregnancyEdit(myFrame, patient, pregnancyvisits, null, -1, true);
				b.addPregnancyListener(PregnancyCareBrowser.this);
				b.setVisible(true);
				
			}
		});
		return buttonNewPregnancy;
	}

	private JButton getButtonVisitDetails() {
		JButton buttonEditVisit = new JButton(MessageBundle.getMessage("angal.pregnancy.visitdetails"));
		buttonEditVisit.setMnemonic(KeyEvent.VK_T);
		buttonEditVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (visitTable.getSelectedRow() < 0
						|| patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
							MessageBundle.getMessage("angal.pregnancy.pleaseselectvisit"),
							MessageBundle.getMessage("angal.pregnancy.editvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				
//				patient = (Patient) patientTable.getValueAt(
//						patientTable.getSelectedRow(), -1);
				pvisit = (PregnancyVisit) visitTable.getValueAt(visitTable.getSelectedRow(), -1);
				if (pvisit.getType() == 0) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
							MessageBundle.getMessage("angal.pregnancy.pleaseselectvisit"),
							MessageBundle.getMessage("angal.pregnancy.editvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				} else {
					PregnancyEdit b = new PregnancyEdit(myFrame, patient,pregnancyvisits, pvisit, pvisit.getType(), false);
					b.addPregnancyListener(PregnancyCareBrowser.this);
					b.setVisible(true);
				}
			}
		});
		return buttonEditVisit;
	}

	private JButton getButtonDelivery() {
		JButton buttonDelivery = new JButton(MessageBundle.getMessage("angal.pregnancy.newdelivery"));
		buttonDelivery.setMnemonic(KeyEvent.VK_T);
		buttonDelivery.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
							MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle.getMessage("angal.pregnancy.newdelivery"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
				if(adPatient!=null)
				{
					patient = adPatient.getPatient();
					Admission admission = adPatient.getAdmission();
					if (admission != null) {
//						myFrame.dispose();
						new AdmissionBrowser(myFrame, patient, admission.getId(), true);
					} else {
						new AdmissionBrowser(myFrame, patient, 0, false);
					}
				}
				else
				{
					patient=null;
				}
				

			}
		});
		if (patient != null) buttonDelivery.setEnabled(false);
		return buttonDelivery;
	}

//	private JButton getJButtonExams() {
//		if (jButtonExams == null) {
//			
//			jButtonExams = new JButton(MessageBundle.getMessage("angal.opd.exams"));
//			
//			jButtonExams.setMnemonic(KeyEvent.VK_E);
//			jButtonExams.addActionListener(new ActionListener() {
//				
//				public void actionPerformed(ActionEvent e) {
//					if (patientTable.getSelectedRow() < 0) {
//						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
//								MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
//						return;
//					}
//					AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
//					Patient pat = adPatient.getPatient();
//					
//					
//					LabBrowser dialog = new LabBrowser(pat);
//					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//					dialog.pack();
//					dialog.setLocationRelativeTo(null);
//					dialog.setVisible(true);
//				}
//			});
//		}
//		return jButtonExams;
//	}
	
	private JButton getButtonVaccine() {
		if (jButtonVaccine == null) {
			jButtonVaccine = new JButton(MessageBundle.getMessage("angal.pregnancy.vaccine"));
			jButtonVaccine.setMnemonic(KeyEvent.VK_E);
			jButtonVaccine.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (patientTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(PregnancyCareBrowser.this, 
								MessageBundle.getMessage("angal.common.pleaseselectarow"),
								MessageBundle.getMessage("angal.admission.editpatient"), 
								JOptionPane.PLAIN_MESSAGE);
						return;
					}
					AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
					Patient pat = adPatient.getPatient();
					
					
					PatVacBrowser dialog = new PatVacBrowser(pat);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.pack();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
				}
			});
		}
		return jButtonVaccine;
	}
	
	private JButton getButtonClose() {
		JButton buttonClose = new JButton(
				MessageBundle.getMessage("angal.pregnancy.close"));
		buttonClose.setMnemonic(KeyEvent.VK_T);
		buttonClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (pregnancyPatientList != null) pregnancyPatientList.clear();
				if (pregnancyvisits != null) pregnancyvisits.clear();
				dispose();
			}
		});
		return buttonClose;

	}

	class PregnancyVisitBrowserModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		ArrayList<PregnancyVisit> visits = new ArrayList<PregnancyVisit>();

		public PregnancyVisitBrowserModel() {
			if (patient != null) {
				if (pregnancyvisits != null) {
					try {
						BusyState.setBusyState(PregnancyCareBrowser.this, true);
						pregnancyvisits = pregMan.getPregnancyVisits(patient.getCode());
					} finally {
						BusyState.setBusyState(PregnancyCareBrowser.this, false);
					}
				} else {
					pregnancyvisits = new ArrayList<PregnancyVisit>();
				}
				for (int a = 0; a < pregnancyvisits.size(); a++) {
					visits.add(pregnancyvisits.get(a));
				}
			}

		}

		public int getRowCount() {
			if (pregnancyvisits == null)
				return 0;
			return pregnancyvisits.size();
		}

		public String getColumnName(int c) {
			return vColums[c];
		}

		public int getColumnCount() {
			return vColums.length;
		}

		public Object getValueAt(int r, int c) {
			PregnancyVisit pregVis = pregnancyvisits.get(r);
			if (c == -1) {
				return pregVis;
			} else if (c == 0) {
				return pregVis.getPregnancyNr();

			} else if (c == 1) {

				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				return sdf.format(pregVis.getDate().getTime());

			} else if (c == 2) {
				
				int type = pregVis.getType();
				switch (type) {

					case -1:
						return MessageBundle.getMessage("angal.pregnancy.prenatalvisit");
					case 0:
						return MessageBundle.getMessage("angal.pregnancy.delivery");
					case 1:
						return MessageBundle.getMessage("angal.pregnancy.postnatalvisit");
					case 10:
						return MessageBundle.getMessage("angal.pregnancy.abortvisit");
				}
				return pregVis.getType();
			} else if (c == 3) {
				return pregVis.getNote();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

	}

	class PregnancyPatientBrowserModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		ArrayList<AdmittedPatient> admittedPatientList = new ArrayList<AdmittedPatient>();

		public PregnancyPatientBrowserModel(String key) {
			
			for (AdmittedPatient ap : pregnancyPatientList) {
				
				String s = "";
				if (key != null)
					s = key + lastKey;
				s.trim();
				String[] tokens = s.split(" ");

				if (!s.equals("")) {
					String name = ap.getPatient().getSearchString();
					int a = 0;
					for (int j = 0; j < tokens.length; j++) {
						String token = tokens[j].toLowerCase();
						if (name.contains(token)) {
							a++;
						}
					}
					if (a == tokens.length)
						admittedPatientList.add(ap);
				} else
					admittedPatientList.add(ap);
			}
		}

		public int getRowCount() {
			if (admittedPatientList == null)
				return 0;
			return admittedPatientList.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			AdmittedPatient admittedPatient = admittedPatientList.get(r);
			Admission admission = admittedPatient.getAdmission();
			if (c == -1) {
				return admittedPatient;
			} else if (c == 0) {
				return (admittedPatient).getPatient().getPreviousCode() + "";
			} else if (c == 1) {
				return (admittedPatient).getPatient().getSecondName() + " "
						+ admittedPatient.getPatient().getFirstName();
			} else if (c == 2) {
				return admittedPatient.getPatient().getAge();

			} else if (c == 3) {
				return admittedPatient.getPatient().getCity() + " "
						+ admittedPatient.getPatient().getAddress();
			} else if (c == 4) {
				if (admission != null)
					return wardMap.get(admission.getWardId());
				else return "";
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	@Override
	public void patientUpdated(AWTEvent e) {
		Patient u = (Patient) e.getSource();
		
		//remember selected row
		int row = patientTable.getSelectedRow();
//		row = patientTable.convertRowIndexToModel(patientTable.getSelectedRow());
		
		if (pregnancyPatientList == null) {
			lastKey = "";
			filterPatient(null);
		}
		for (int i = 0; i < pregnancyPatientList.size(); i++) {
			if ((pregnancyPatientList.get(i).getPatient().getCode()).equals(u.getCode())) {
				Admission admission = pregnancyPatientList.get(i).getAdmission();
				Pregnancy pregnancy = pregnancyPatientList.get(i).getPregnancy();
				pregnancyPatientList.set(i, new PregnancyPatient(u, admission, pregnancy));
				break;
			}
		}
		lastKey = "";
		filterPatient(null);
		try {
			patientTable.setRowSelectionInterval(row, row);
		} catch (Exception e1) {
		}
		searchPatientTextField.requestFocus();

	}

	@Override
	public void patientInserted(AWTEvent e) {
		Patient u = (Patient) e.getSource();
		if (pregnancyPatientList == null) {
//			pregnancyPatientList.add(0, u);
			pregnancyPatientList=new ArrayList<PregnancyPatient>();
			pregnancyPatientList.add(0, new PregnancyPatient(u, null, null));
		} else {
			pregnancyPatientList.add(0, new PregnancyPatient(u, null, null));
//			pregnancyPatientList.add(0, u);
			lastKey = "";
			filterPatient(null);
		}
		try {
			if (patientTable.getRowCount() > 0)
				patientTable.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {
		}
		searchPatientTextField.requestFocus();

	}

	private void filterPatient(String key) {
		patientTable.setModel(new PregnancyPatientBrowserModel(key));
	}

	/**
	 * fill the pregnancy details with abortions count and deliveries count
	 */
	private void filterPregnancyDetails() {
		//TODO the abort count and the delivery details
		for (int a = 0; a < deltypeLabel.size(); a++) {
			deltypeLabel.get(a).setText("");
			deltypeResLabel.get(a).setText("");
		}
		HashMap<String, Integer> deliveries = pregdelManager.getDeliveriesOfPatient(patient.getCode());
		Set<String> keys = deliveries.keySet();
		Object[] keyArray = keys.toArray();
		for (int a = 0; a < keys.size(); a++) {
				String deltype = keyArray[a].toString();
				if (deltype.equals("unknown")) 
					deltype = MessageBundle.getMessage("angal.pregnancy.unknowndeliveryresult");
				if (deltype.length() > 23)
					deltype = deltype.substring(0, 22);
			    deltypeLabel.get(a).setText("  " + deltype + ": ");
			    deltypeResLabel.get(a).setText(deliveries.get(keyArray[a]).toString());
				
			} 
	}

	private void filterVisit() {
		visitTable.setModel(new PregnancyVisitBrowserModel());
		try {
			if (visitTable.getRowCount() > 0)
				visitTable.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {

		}
	}

	@Override
	public void pregnancyUpdated(AWTEvent e) {
		int selectedrow = visitTable.getSelectedRow();
		filterVisit();
		try {
			if (visitTable.getRowCount() > 0)
				visitTable.setRowSelectionInterval(selectedrow, selectedrow);
		} catch (Exception e1) {
		}
	}

	@Override
	public void pregnancyInserted(AWTEvent e) {
		PregnancyVisit v = (PregnancyVisit) e.getSource();
		if (pregnancyvisits == null)
			pregnancyvisits = new ArrayList<PregnancyVisit>();
		pregnancyvisits.add(0, v);
		filterVisit();

	}

	@Override
	public void admissionUpdated(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void admissionInserted(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}
}
