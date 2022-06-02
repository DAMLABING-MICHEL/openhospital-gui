package org.isf.pregnancy.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.manager.PregnancyCareManager;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregnancyexam.model.PregnancyExam;
import org.isf.pregnancyexam.model.PregnancyExamResult;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.jobjects.BorderedPanel;
import org.isf.utils.jobjects.JDateAndTimeChooserDialog;
import org.isf.utils.time.Converters;
import org.isf.visits.model.Visit;

import com.toedter.calendar.JDateChooser;

//public class PregnancyEdit extends JDialog 
public class PregnancyEdit extends JDialog {

	private static final long serialVersionUID = 1L;

	private EventListenerList pregnancyListeners = new EventListenerList();

	public interface PregnancyListener extends EventListener {
		public void pregnancyUpdated(AWTEvent e);

		public void pregnancyInserted(AWTEvent e);
	}

	public void addPregnancyListener(PregnancyListener l) {
		pregnancyListeners.add(PregnancyListener.class, l);
	}

	public void removePregnancyListener(PregnancyListener listener) {
		pregnancyListeners.remove(PregnancyListener.class, listener);
	}

	private void firePregnancyInserted(PregnancyVisit aVisit) {
		AWTEvent event = new AWTEvent(aVisit, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 *  
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = pregnancyListeners
				.getListeners(PregnancyListener.class);
		for (int i = 0; i < listeners.length; i++)
			((PregnancyListener) listeners[i]).pregnancyInserted(event);
	}

	private void firePregnancyUpdated(PregnancyVisit aVisit) {
		AWTEvent event = new AWTEvent(aVisit, AWTEvent.RESERVED_ID_MAX + 1) {

			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = pregnancyListeners
				.getListeners(PregnancyListener.class);
		for (int i = 0; i < listeners.length; i++)
			((PregnancyListener) listeners[i]).pregnancyUpdated(event);
	}

	private String[] vColums = {
			MessageBundle.getMessage("angal.pregnancyexam.code"),
			MessageBundle.getMessage("angal.pregnancyexam.name"),
			MessageBundle.getMessage("angal.pregnancy.examresult") };

	private JTable examTable = null;

	private int[] vColumwidth = { 10, 120, 90 };
	private PregnancyEdit myFrame;
	private ArrayList<PregnancyVisit> pregnancyvisits = null;
	private ArrayList<Pregnancy> patientsPregnancies = null;

	private ArrayList<PregnancyExam> pregnancyexams = null;
	private ArrayList<PregnantTreatmentType> treatmTypeList = null;

	private HashMap<String, PregnancyExamResult> examoutcomes = null;

	private ArrayList<Visit> generalvisits = null;

	private Patient pPatient = null;
	private Pregnancy pregnancy = null;
	private PregnancyCareManager pregManager = new PregnancyCareManager();
	private PatientBrowserManager patientManager = null;
	private PregnancyVisit pregnancyvisit = null;
	private JDateChooser lmpDateChooser = null;
	private JDateChooser scheduledDeliveryDateChooser = null;
	private JDateChooser realDeliveryDateChooser = null;
	private JDateChooser visitDateChooser = null;
	private JDateChooser nextvisitDateChooser = null;
	private JComboBox pregnancyNrBox = null;
	private JComboBox parityBox = null;
	private JComboBox childrenAliveBox = null;
	private JComboBox bloodgroupBox = null;
	private JTextArea notearea = null;
	private JScrollPane examscrollpane = null;
	private JComboBox treatmTypeBox = null;
	private GregorianCalendar today;
	private boolean newVisit = true;
	private boolean newPregnancy = true;
	private boolean fromAdmission = false;
	private JButton nextVisitDateAndTimeButton;
	private JButton visitDateAndTimeButton;
	private JPanel parityPanel;
	private JPanel childrenAlivePanel;
	private JPanel calculatedGestationalAgePanel;
	private JTextField calculatedGestationalAgeTextField;
	

	public PregnancyEdit(JFrame owner, Patient patient,
			ArrayList<PregnancyVisit> visits, PregnancyVisit selectedvisit,
			int visittype, boolean insertpregnancy) {
		super(owner, true);
		//super();
		this.pPatient = patient;
		this.pregnancyvisits = visits;
		this.newPregnancy = insertpregnancy;
		this.patientsPregnancies = pregManager.getPatientsPregnancies(patient.getCode());
		if (selectedvisit != null) {
			newVisit = false;
			this.pregnancyvisit = selectedvisit;
			this.pregnancy = pregManager.getPregnancy(this.pregnancyvisit.getPregnancyId());
			examoutcomes = pregManager.getExamResults(pregnancyvisit.getVisitId());
			pregnancy = pregManager.getPregnancy(pregnancyvisit.getPregnancyId());

		} else {
			pregnancyvisit = new PregnancyVisit(pPatient.getCode(), 0, visittype);
			if (!newPregnancy) {
				pregnancy = pregManager.getPregnancy(pregnancyvisits.get(0).getPregnancyId());
				pregnancyvisit.setPregnancId(pregnancy.getId());
				pregnancyvisit.setPregnancyNr(pregnancy.getGravida());

			} else {
				pregnancy = new Pregnancy(pPatient.getCode());
				

			}
			examoutcomes = new HashMap<String, PregnancyExamResult>();
		}

		this.pregnancyexams = pregManager.getVisitExams_byVisitType(visittype);

		myFrame = this;
		today = new GregorianCalendar();
		this.setResizable(false);
		initComponents();
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (generalvisits != null) generalvisits.clear();
				if (treatmTypeList != null) treatmTypeList.clear();
				if (examoutcomes != null) examoutcomes.clear();
				if (patientsPregnancies != null) patientsPregnancies.clear();
				dispose();
			}
		});

	}

	// from AdmissionBrowser
	public PregnancyEdit(JFrame owner, Patient patient, int visittype) {
		//super(owner, true);
		super();
		this.pPatient = patient;
		this.pregnancyvisits = pregManager.getPregnancyVisits(patient.getCode());
		this.newPregnancy = false;
		this.fromAdmission = true;
		this.patientsPregnancies = pregManager.getPatientsPregnancies(patient.getCode());
		pregnancyvisit = new PregnancyVisit(pPatient.getCode(), 0, visittype);
		pregnancy = pregManager.getPregnancy(pregnancyvisits.get(0).getPregnancyId());
		pregnancyvisit.setPregnancId(pregnancy.getId());
		pregnancyvisit.setPregnancyNr(pregnancy.getGravida());
		examoutcomes = new HashMap<String, PregnancyExamResult>();
		this.pregnancyexams = pregManager.getVisitExams_byVisitType(visittype);
		this.newVisit = true;
		myFrame = this;
		today = new GregorianCalendar();
		this.setResizable(false);
		initComponents();
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (generalvisits != null) generalvisits.clear();
				if (treatmTypeList != null) treatmTypeList.clear();
				if (examoutcomes != null) examoutcomes.clear();
				if (patientsPregnancies != null) patientsPregnancies.clear();
				dispose();
			}
		});

	}

	private void initComponents() {
		setTitle(MessageBundle.getMessage("angal.pregnancy.pregnancybrowser"));
		this.setBounds(200, 200, 750, 650);
		getContentPane().add(getDataPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		
	}

	private JPanel getDataPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getPatientPanel(), java.awt.BorderLayout.NORTH);
		panel.add(getPregnancyDetailsPanel(), java.awt.BorderLayout.CENTER);
		panel.add(getAdditionalPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel getAdditionalPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getNextVisitDatePanel(), BorderLayout.CENTER);
		panel.add(getNotePanel(), BorderLayout.EAST);
		return panel;
	}

	private JPanel getPregnancyDetailsPanel() {
		JPanel data = new JPanel(new BorderLayout());

		data.add(getExamsScrollPane(), BorderLayout.CENTER);
		data.add(getPregnancyPanel(), java.awt.BorderLayout.EAST);
		return data;
	}

	private JPanel getPatientPanel() {
		
		JPanel panel = new JPanel();
		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 315, 200, 100, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowHeights = new int[] { 30, 0 };
		gbl_panel.rowWeights = new double[] { 0.0 };
		panel.setLayout(gbl_panel);
		
		
		GridBagConstraints gbc_visitDatePanel = new GridBagConstraints();
		gbc_visitDatePanel.fill = GridBagConstraints.BOTH;
		gbc_visitDatePanel.gridx = 0;
		gbc_visitDatePanel.gridy = 0;
		panel.add(getVisitDatePanel(), gbc_visitDatePanel);
		
		GridBagConstraints gbc_BloodgroupPanel = new GridBagConstraints();
		gbc_BloodgroupPanel.fill = GridBagConstraints.BOTH;
		gbc_BloodgroupPanel.gridx = 1;
		gbc_BloodgroupPanel.gridy = 0;
		panel.add(getBloodgroupPanel(), gbc_BloodgroupPanel);
		
		GridBagConstraints gbc_TreatmentPanel = new GridBagConstraints();
		gbc_TreatmentPanel.fill = GridBagConstraints.BOTH;
		gbc_TreatmentPanel.gridx = 2;
		gbc_TreatmentPanel.gridy = 0;
		panel.add(getTreatmentPanel(), gbc_TreatmentPanel);
		
//		panel.add(getVisitDatePanel());
//		panel.add(getBloodgroupPanel());
//		panel.add(getTreatmentPanel());
//		panel.setPreferredSize(new Dimension(740, 100));
		return panel;
	}

	private JPanel getPregnancyPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(getPregnancyNrPanel());
		panel.add(getParityPanel());
		panel.add(getChildrenAlivePanel());
		panel.add(getLmpDatePanel());
		panel.add(getCalculatedGestationalAgePanel());
		if (pregnancyvisit.getType() == -1) {
			panel.add(getScheduledDeliveryDatePanel());
		} else {
			if (pregnancy.getReal_delivery() == null)
				panel.add(getScheduledDeliveryDatePanel());
			else 
				panel.add(getRealDeliveryDatePanel());
		}
		panel.setPreferredSize(new Dimension(230, 300));
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.pregnancydetails")));
		return panel;
	}

	private JPanel getExamsScrollPane() {
		Object[][] data = new Object[pregnancyexams.size()][3];
		for (int a = 0; a < pregnancyexams.size(); a++) {
			String id = pregnancyexams.get(a).getExamId();
			data[a][0] = id;
			data[a][1] = pregnancyexams.get(a).getExamDesc();
			String def = pregnancyexams.get(a).getExamDefault();
			if (examoutcomes.containsKey(id)) {
				data[a][2] = examoutcomes.get(id).getOutcome();
			} else if (def != null && def.length() > 0) {
				data[a][2] = def;
			}
		}

		DefaultTableModel model = new DefaultTableModel(data, vColums);

		examTable = new JTable(model) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			// Determine editor to be used by row
			public TableCellEditor getCellEditor(int row, int column) {

				if (column == 2) {
					
					if (pregnancyexams.get(row).getExamValues().length() > 0) {
						
					String[] arr = pregnancyexams.get(row).getExamValues()
							.split(";");
					ArrayList<String> val = new ArrayList<String>();
					val.add("");
					for (int a = 0; a < arr.length; a++) {
						val.add(arr[a].trim());
					}

					JComboBox cellcombo = new JComboBox(val.toArray());
					DefaultCellEditor celleditor = new DefaultCellEditor(
							cellcombo);
					return celleditor;
					}
					else return super.getCellEditor(row, column);
				}
				return cellEditor;
			}
		};

		for (int i = 0; i < vColums.length; i++) {
			examTable.getColumnModel().getColumn(i)
					.setPreferredWidth(vColumwidth[i]);
		}

		int tableWidth = 0;
		for (int i = 0; i < vColumwidth.length; i++) {
			tableWidth += vColumwidth[i];
		}
		examscrollpane = new JScrollPane(examTable);

		examscrollpane.setPreferredSize(new Dimension(tableWidth + 280, 280));
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(examscrollpane);
		if (pregnancyvisit.getType() == -1)
			panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.prenatalexam")));
		else
			panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.postnatalexam")));
		return panel;
	}
	
	private JPanel getNotePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		notearea = new JTextArea();
		notearea.setLineWrap(true);
		notearea.setText(pregnancyvisit.getNote());
		panel.setPreferredSize(new Dimension(400, 100));

		panel.add(notearea, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancy.pregnancynote")));
		return panel;
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(getButtonSave());
		buttonPanel.add(getButtonClose());
		return buttonPanel;
	}

	private JButton getButtonSave() {
		JButton buttonSave = new JButton(
				MessageBundle.getMessage("angal.pregnancy.ok"));
		buttonSave.setMnemonic(KeyEvent.VK_T);
		buttonSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				ArrayList<PregnancyExamResult> nonLabResultsToInsert = new ArrayList<PregnancyExamResult>();
				pregnancy.setGravida((Integer) pregnancyNrBox.getSelectedItem());
				pregnancy.setParity((Integer) parityBox.getSelectedItem());
				pregnancy.setChildrenAlive((Integer) childrenAliveBox.getSelectedItem());
				pregnancy.setLmp((GregorianCalendar) lmpDateChooser.getCalendar());
				pregnancy.setScheduled_delivery(scheduledDeliveryDateChooser == null ? pregnancy.getScheduled_delivery() : (GregorianCalendar) scheduledDeliveryDateChooser.getCalendar());
				pregnancy.setReal_delivery(realDeliveryDateChooser == null ? pregnancy.getReal_delivery() : (GregorianCalendar) realDeliveryDateChooser.getCalendar());
				int origpregnr = pregnancyvisit.getPregnancyNr();
				for (int v = 0; v < pregnancyvisits.size(); v++) {
					if (pregnancyvisits.get(v).getPregnancyNr() == origpregnr)
						pregnancyvisits.get(v).setPregnancyNr(
								pregnancy.getGravida());
				}
				Date visitDate = visitDateChooser.getDate();
				if (visitDate == null) {
					JOptionPane.showMessageDialog(PregnancyEdit.this,
							MessageBundle.getMessage("angal.pregedit.pleaseinsertadate"),
							"",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				pregnancyvisit.setNote(notearea.getText());
				pregnancyvisit.setDate(visitDate);
				
				GregorianCalendar nextVistDate = null;
				if (nextvisitDateChooser.getDate() != null) {
					nextVistDate = new GregorianCalendar();
					nextVistDate.setTime(nextvisitDateChooser.getDate());
					if (nextVistDate.before(visitDate)) {
						JOptionPane.showMessageDialog(PregnancyEdit.this,
								MessageBundle.getMessage("angal.pregedit.notpasseddate"),
								"",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				pregnancyvisit.setNextVisitdate(nextVistDate);

				if (treatmTypeBox.getSelectedIndex() > 0) {
					pregnancyvisit.setTreatmenttype(((PregnantTreatmentType) treatmTypeBox.getSelectedItem()).getCode());
				} else
					pregnancyvisit.setTreatmenttype(null);
				if (!bloodgroupBox.getSelectedItem().toString().equals(pPatient.getBloodType())) {
					patientManager = new PatientBrowserManager();
					pPatient.setBloodType(bloodgroupBox.getSelectedItem().toString());
					patientManager.updatePatient(pPatient);
				}

				for (int a = 0; a < pregnancyexams.size(); a++) {
					PregnancyExam exam = pregnancyexams.get(a);
					String outcome = (String) examTable.getModel().getValueAt(a, 2);
					if (examoutcomes.containsKey(exam.getExamId())) {
						PregnancyExamResult r = examoutcomes.get(exam.getExamId());
						r.setOutcome(outcome);
						pregManager.updateExamResult(pregnancyvisit.getVisitId(), r);
					} else {
						if (outcome != null && outcome.length() > 0) {
							PregnancyExamResult r = new PregnancyExamResult(pregnancyvisit.getVisitId(), exam.getExamId(), outcome);
							nonLabResultsToInsert.add(r);
						}
					}
				}
				if (newVisit) {

					if (newPregnancy) {
						pregManager.newPregnancy(pregnancy);
					} else
						pregManager.updatePregnancy(pregnancy);
					pregnancyvisit.setPregnancId(pregnancy.getId());
					int insertid = pregManager.newVisit(pregnancyvisit);
					pregnancyvisit.setVisitId(insertid);

					if (nonLabResultsToInsert.size() > 0)
						pregManager.newExamOutcomes(pregnancyvisit.getVisitId(),
								nonLabResultsToInsert);
					firePregnancyInserted(pregnancyvisit);

				} else {
					pregManager.updatePregnancy(pregnancy);
					pregManager.updateVisit(pregnancyvisit);

					if (nonLabResultsToInsert.size() > 0)
						pregManager.newExamOutcomes(pregnancyvisit.getVisitId(),
								nonLabResultsToInsert);
					firePregnancyUpdated(pregnancyvisit);
				}
				dispose();
			}
		});
		return buttonSave;

	}
	
	private JPanel getChildrenAlivePanel() {
		if (childrenAlivePanel == null) {
			childrenAlivePanel = new JPanel(new BorderLayout());
			childrenAlivePanel.add(getChildrenAliveBox());
			childrenAlivePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.childrenalive")));
			childrenAlivePanel.setPreferredSize(new Dimension(200, 60));
		}
		return childrenAlivePanel;
	}
	
	private JComboBox getChildrenAliveBox() {
		if (childrenAliveBox == null) {
			childrenAliveBox = new JComboBox();
		}
		int startNumber = (Integer) pregnancyNrBox.getItemAt(0) - 1;
		int endNumber = (Integer) pregnancyNrBox.getSelectedItem() - 1;
		int selectedChildrenAlive = 0;
		
		if (newPregnancy) {
			if (patientsPregnancies.size() > 0)
				startNumber = patientsPregnancies.get(0).getChildrenAlive();
		} else {
			Pregnancy previouspreg = null;
			Pregnancy preg = null;
			Pregnancy nextpreg = null;
			for (int v = 0; v < patientsPregnancies.size(); v++) {
				preg = patientsPregnancies.get(v);
				if (pregnancyvisit.getPregnancyNr() == preg.getGravida()) {
					selectedChildrenAlive = preg.getChildrenAlive();
					if (v > 0)
						nextpreg = patientsPregnancies.get(v - 1);
					if (patientsPregnancies.size() > v + 1)
						previouspreg = patientsPregnancies.get(v + 1);
					break;
				}
			}
			if (previouspreg != null)
				startNumber = previouspreg.getGravida() - 1;
			if (nextpreg != null)
				endNumber = (Integer) nextpreg.getChildrenAlive();
		}
		
		childrenAliveBox.removeAllItems();
		for (int a = startNumber; a <= endNumber; a++) {
			childrenAliveBox.addItem(a);
		}
		childrenAliveBox.setSelectedItem(selectedChildrenAlive);
		return childrenAliveBox;
	}

	private JPanel getParityPanel() {
		if (parityPanel == null) {
			parityPanel = new JPanel(new BorderLayout());
			parityPanel.add(getParityBox());
			parityPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.parity")));
			parityPanel.setPreferredSize(new Dimension(200, 60));
		}
		return parityPanel;
	}

	private JComboBox getParityBox() {
		if (parityBox == null) {
			parityBox = new JComboBox();
		}

		int startNumber = (Integer) pregnancyNrBox.getItemAt(0) - 1;
		int endNumber = (Integer) pregnancyNrBox.getSelectedItem() - 1;
		int selectedParity = 0;
		
		if (newPregnancy) {
			if (patientsPregnancies.size() > 0)
				startNumber = patientsPregnancies.get(0).getParity();
		} else {
			Pregnancy previouspreg = null;
			Pregnancy preg = null;
			Pregnancy nextpreg = null;
			for (int v = 0; v < patientsPregnancies.size(); v++) {
				preg = patientsPregnancies.get(v);
				if (pregnancyvisit.getPregnancyNr() == preg.getGravida()) {
					selectedParity = preg.getParity();
					if (v > 0)
						nextpreg = patientsPregnancies.get(v - 1);
					if (patientsPregnancies.size() > v + 1)
						previouspreg = patientsPregnancies.get(v + 1);
					break;
				}
			}
			if (previouspreg != null)
				startNumber = previouspreg.getGravida() - 1;
			if (nextpreg != null)
				endNumber = (Integer) nextpreg.getParity() - 1;
		}
		
		parityBox.removeAllItems();
		for (int a = startNumber; a <= endNumber; a++) {
			parityBox.addItem(a);
		}
		parityBox.setSelectedItem(selectedParity);
		return parityBox;
	}

	private JPanel getPregnancyNrPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		int startNumber = 1;
		int endNumber = 20;
		ArrayList<Integer> possiblePregnancyNumber = new ArrayList<Integer>();
		if (newPregnancy) {
			if (patientsPregnancies.size() > 0)
				startNumber = patientsPregnancies.get(0).getGravida() + 1;
		} else {
			Pregnancy previouspreg = null;
			Pregnancy preg = null;
			Pregnancy nextpreg = null;
			for (int v = 0; v < patientsPregnancies.size(); v++) {
				preg = patientsPregnancies.get(v);
				if (pregnancyvisit.getPregnancyNr() == preg.getGravida()) {
					if (v > 0)
						nextpreg = patientsPregnancies.get(v - 1);
					if (patientsPregnancies.size() > v + 1)
						previouspreg = patientsPregnancies.get(v + 1);
					break;
				}
			}
			if (previouspreg != null)
				startNumber = previouspreg.getGravida() + 1;
			if (nextpreg != null)
				endNumber = nextpreg.getGravida() - 1;

		}
		for (int a = startNumber; a <= endNumber; a++) {
			possiblePregnancyNumber.add(a);
		}

		pregnancyNrBox = new JComboBox(possiblePregnancyNumber.toArray());
		if (fromAdmission || (newVisit && !newPregnancy)) {
			pregnancyNrBox.setSelectedItem(pregnancyvisit.getPregnancyNr());
			pregnancyNrBox.setEnabled(false);

		} else if (!newVisit && !newPregnancy)
			pregnancyNrBox.setSelectedItem(pregnancyvisit.getPregnancyNr());
		
		pregnancyNrBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				getParityBox();
				getChildrenAliveBox();
			}
		});
		panel.add(pregnancyNrBox);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.pregnancynumber")));
		panel.setPreferredSize(new Dimension(200, 60));
		return panel;
	}

	private JPanel getBloodgroupPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		String[] bloodgroups = { MessageBundle.getMessage("angal.patient.bloodtype.unknown"), "0+", "A+", "B+", "AB+", "0-", "A-", "B-", "AB-" };
		bloodgroupBox = new JComboBox(bloodgroups);
		bloodgroupBox.setSelectedIndex(0);
		if (pPatient.getBloodType() != null
				&& pPatient.getBloodType().length() > 0) {
			for (int a = 1; a < bloodgroups.length; a++) {
				if (bloodgroups[a].equals(pPatient.getBloodType())) {
					bloodgroupBox.setSelectedItem(bloodgroups[a]);
					break;
				}
			}
		}
		bloodgroupBox.setPreferredSize(new Dimension(155, 20));
		panel.add(bloodgroupBox, BorderLayout.WEST);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.bloodtype")));
		panel.setPreferredSize(new Dimension(175, 60));
		return panel;
	}

	private JPanel getVisitDatePanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setPreferredSize(new Dimension(315, 30));
		Date myDate = null;
		if (pregnancyvisit.getDate() != null) {
			myDate = pregnancyvisit.getDate().getTime();
		}
		visitDateChooser = new JDateChooser(myDate, "dd/MM/yy HH:mm:ss");
		visitDateChooser.setMaxSelectableDate(today.getTime());
		visitDateChooser.setLocale(new Locale(GeneralData.LANGUAGE));
		visitDateChooser.setDateFormatString("dd/MM/yy HH:mm:ss");
		panel.add(visitDateChooser);
		panel.add(getVisitDateAndTimeButton());
		
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.visitdate")));
		return panel;
	}
	
	private JButton getVisitDateAndTimeButton() {
		if (visitDateAndTimeButton == null) {
			visitDateAndTimeButton = new JButton(""); //$NON-NLS-1$
			visitDateAndTimeButton.setIcon(new ImageIcon("./rsc/icons/clock_button.png")); //$NON-NLS-1$
			visitDateAndTimeButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					JDateAndTimeChooserDialog schedDate = new JDateAndTimeChooserDialog(PregnancyEdit.this, visitDateChooser.getDate());
					schedDate.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					schedDate.setVisible(true);
					
					Date date = schedDate.getDate();
					
					if (date != null) {
						
						visitDateChooser.setDate(date);
						
					} else {
						return;
					}
				}
			});
		}
		return visitDateAndTimeButton;
	}

	private JPanel getNextVisitDatePanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Date myDate = null;
		if (pregnancyvisit.getNextVisitdate() != null) {
			myDate = pregnancyvisit.getNextVisitdate().getTime();
		}
		nextvisitDateChooser = new JDateChooser(myDate);
		nextvisitDateChooser.setLocale(new Locale(GeneralData.LANGUAGE));
		nextvisitDateChooser.setDateFormatString("dd/MM/yy HH:mm:ss");
		panel.add(nextvisitDateChooser);
		panel.add(getNextVisitDateAndTimeButton());
		
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.nextvisitdate")));
		return panel;
	}
	
	private JButton getNextVisitDateAndTimeButton() {
		if (nextVisitDateAndTimeButton == null) {
			nextVisitDateAndTimeButton = new JButton(""); //$NON-NLS-1$
			nextVisitDateAndTimeButton.setIcon(new ImageIcon("./rsc/icons/clock_button.png")); //$NON-NLS-1$
			nextVisitDateAndTimeButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					JDateAndTimeChooserDialog schedDate = new JDateAndTimeChooserDialog(PregnancyEdit.this, nextvisitDateChooser.getDate());
					schedDate.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					schedDate.setVisible(true);
					
					Date date = schedDate.getDate();
					
					if (date != null) {
						
						nextvisitDateChooser.setDate(date);
						
					} else {
						return;
					}
				}
			});
		}
		return nextVisitDateAndTimeButton;
	}
	
	private JPanel getTreatmentPanel() {
		treatmTypeBox = new JComboBox();
		treatmTypeBox.addItem("");
		JPanel treatmentPanel = new JPanel();
		PregnantTreatmentTypeBrowserManager abm = new PregnantTreatmentTypeBrowserManager();
		treatmTypeList = abm.getPregnantTreatmentType();
		for (PregnantTreatmentType elem : treatmTypeList) {
			treatmTypeBox.addItem(elem);
			if (pregnancyvisit.getTreatmenttype() != null
					&& pregnancyvisit.getTreatmenttype().equals(elem.getCode()))
				treatmTypeBox.setSelectedItem(elem);
		}
		GridBagLayout gbl_treatmentPanel = new GridBagLayout();
		gbl_treatmentPanel.columnWidths = new int[] {142, 0};
		gbl_treatmentPanel.rowHeights = new int[]{22, 0};
		gbl_treatmentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_treatmentPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		treatmentPanel.setLayout(gbl_treatmentPanel);
		treatmentPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.treatmenttype")));
		
		GridBagConstraints gbc_treatmTypeBox = new GridBagConstraints();
		gbc_treatmTypeBox.fill = GridBagConstraints.BOTH;
		gbc_treatmTypeBox.gridx = 0;
		gbc_treatmTypeBox.gridy = 0;
		treatmentPanel.add(treatmTypeBox, gbc_treatmTypeBox);
		return treatmentPanel;
	}

	private JPanel getLmpDatePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		lmpDateChooser = new JDateChooser();
		lmpDateChooser.setMaxSelectableDate(today.getTime());
		lmpDateChooser.setLocale(new Locale(GeneralData.LANGUAGE));
		lmpDateChooser.setDateFormatString("dd/MM/yy");
		if (pregnancy.getLmp() != null) lmpDateChooser.setDate(pregnancy.getLmp().getTime());
		if (pregnancyvisit.getType() == 1 || fromAdmission) {
			lmpDateChooser.setEnabled(false);
			
		} else {
			lmpDateChooser.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (scheduledDeliveryDateChooser != null) {
						Calendar c = lmpDateChooser.getCalendar();
						pregnancy.setLmp(Converters.toCalendar(lmpDateChooser.getDate()));
						calculatedGestationalAgeTextField.setText(String.valueOf((pregnancy.calculateGestationalAge())));
						if (c != null) {
							c.add(2, 9);
							scheduledDeliveryDateChooser.setDate(c.getTime());
						}
					}
				}
			});
		}
		
		panel.add(lmpDateChooser);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.lmpdate")));
		panel.setPreferredSize(new Dimension(200, 60));
		return panel;
	}
	
	private JPanel getCalculatedGestationalAgePanel() {
		if (calculatedGestationalAgePanel == null) {
			calculatedGestationalAgePanel = new JPanel(new BorderLayout());
			calculatedGestationalAgeTextField = new JTextField();
			calculatedGestationalAgeTextField.setHorizontalAlignment(JTextField.CENTER);
			calculatedGestationalAgeTextField.setEditable(false);
			calculatedGestationalAgeTextField.setText(String.valueOf(pregnancy.calculateGestationalAge()));
			calculatedGestationalAgePanel.add(calculatedGestationalAgeTextField);
			calculatedGestationalAgePanel.setPreferredSize(new Dimension(200, 60));
			BorderedPanel.setMyBorderAlign(calculatedGestationalAgePanel, MessageBundle.getMessage("angal.pregnancy.gestationalageabbr"), TitledBorder.CENTER);
		}
		return calculatedGestationalAgePanel;
	}

	private JPanel getScheduledDeliveryDatePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		scheduledDeliveryDateChooser = new JDateChooser();
		scheduledDeliveryDateChooser.setMinSelectableDate(today.getTime());
		scheduledDeliveryDateChooser.setLocale(new Locale(GeneralData.LANGUAGE));
		scheduledDeliveryDateChooser.setDateFormatString("dd/MM/yy");
		if (pregnancy.getScheduled_delivery() != null) scheduledDeliveryDateChooser.setDate(pregnancy.getScheduled_delivery().getTime());
		if (fromAdmission || pregnancyvisit.getType() == 1) {
			scheduledDeliveryDateChooser.setEnabled(false);

		}
		panel.add(scheduledDeliveryDateChooser);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancy.scheduleddelivery")));
		panel.setPreferredSize(new Dimension(200, 60));
		return panel;
	}
	
	private JPanel getRealDeliveryDatePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		realDeliveryDateChooser = new JDateChooser();
		realDeliveryDateChooser.setLocale(new Locale(GeneralData.LANGUAGE));
		realDeliveryDateChooser.setDateFormatString("dd/MM/yy");
		if (pregnancy.getReal_delivery() != null) realDeliveryDateChooser.setDate(pregnancy.getReal_delivery().getTime());
		realDeliveryDateChooser.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (lmpDateChooser != null) {
					Calendar c = realDeliveryDateChooser.getCalendar();
					pregnancy.setReal_delivery(Converters.toCalendar(realDeliveryDateChooser.getDate()));
					calculatedGestationalAgeTextField.setText(String.valueOf((pregnancy.calculateGestationalAge())));
				}
			}
		});
		
		
		
		panel.add(realDeliveryDateChooser);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.pregnancy.realdelivery")));
		panel.setPreferredSize(new Dimension(200, 60));
		return panel;
	}

	private JButton getButtonClose() {
		JButton buttonClose = new JButton(
				MessageBundle.getMessage("angal.pregnancy.close"));
		buttonClose.setMnemonic(KeyEvent.VK_T);
		buttonClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		return buttonClose;

	}

}
