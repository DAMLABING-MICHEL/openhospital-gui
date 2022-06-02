package org.isf.admission.gui;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;

import org.apache.log4j.PropertyConfigurator;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.malnutrition.manager.MalnutritionManager;
import org.isf.malnutrition.model.MalnutritionVisit;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.patient.gui.PatientSummary;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.gui.PregnancyCareBrowser;
import org.isf.pregnancy.gui.PregnancyEdit;
import org.isf.pregnancy.manager.PregnancyCareManager;
import org.isf.pregnancy.manager.PregnancyDeliveryManager;
import org.isf.pregnancy.model.Delivery;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.jobjects.BorderedPanel;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.FormattedTextField;
import org.isf.utils.jobjects.JDateAndTimeChooserDialog;
import org.isf.utils.jobjects.JPanelApgarScore;
import org.isf.utils.jobjects.ShadowBorder;
import org.isf.utils.jobjects.VoDateTextField;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.utils.time.Converters;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.isf.xmpp.gui.CommunicationFrame;
import org.isf.xmpp.manager.Interaction;

import com.toedter.calendar.JDateChooser;

/**
 * This class shows essential patient data and allows to create an admission
 * record or modify an existing one
 * 
 * release 2.5 nov-10-06
 * 
 * @author flavio
 * 
 */

/*----------------------------------------------------------
 * modification history
 * ====================
 * 23/10/06 - flavio - borders set to not resizable
 *                     changed Disease IN (/OUT) into Dignosis IN (/OUT)
 *                     
 * 10/11/06 - ross - added RememberDate for admission Date
 * 				   - only diseses with flag In Patient (IPD) are displayed
 *                 - on Insert. in edit all are displayed
 *                 - the correct way should be to display the IPD + the one aready registered
 * 18/08/08 - Alex/Andrea - Calendar added
 * 13/02/09 - Alex - Cosmetic changes to UI
 * 10/01/11 - Claudia - insert ward beds availability 
 * 01/01/11 - Alex - GUI and code reengineering
 * 29/12/11 - Nicola - insert alert IN/OUT patient for communication module
 * 18/08/12 - Martin modify the delivery tab- complete restructuring
 -----------------------------------------------------------*/
public class AdmissionBrowser extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private EventListenerList admissionListeners = new EventListenerList();

	public interface AdmissionListener extends EventListener {
		public void admissionUpdated(AWTEvent e);
		public void admissionInserted(AWTEvent e);
	}

	public void addAdmissionListener(AdmissionListener l) {
		admissionListeners.add(AdmissionListener.class, l);
	}

	public void removeAdmissionListener(AdmissionListener listener) {
		admissionListeners.remove(AdmissionListener.class, listener);
	}

	private void fireAdmissionInserted(Admission anAdmission) {
		AWTEvent event = new AWTEvent(anAdmission, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = admissionListeners.getListeners(AdmissionListener.class);
		for (int i = 0; i < listeners.length; i++)
			((AdmissionListener) listeners[i]).admissionInserted(event);
	}

	private void fireAdmissionUpdated(Admission anAdmission) {
		AWTEvent event = new AWTEvent(anAdmission, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = admissionListeners.getListeners(AdmissionListener.class);
		for (int i = 0; i < listeners.length; i++)
			((AdmissionListener) listeners[i]).admissionUpdated(event);
	}

	private Patient patient = null;

	private boolean editing = false;
	
	private Admission admission = null;

	private PatientSummary ps = null;
	
	private JTextArea textArea = null;

	private JTabbedPane jTabbedPaneAdmission;

	private JPanel jPanelAdmission;

	private JPanel jPanelOperation;

	private JPanel jPanelDelivery;
	
	private int pregnancyTabIndex;
	
	private JPanel jContentPane = null;
	
	// enable is if patient is female
	private boolean enablePregnancy = false;

	// viewing is if you set ward to pregnancy
	private boolean viewingPregnancy = false;

	private GregorianCalendar visitDate = null;

	private float weight = 0.0f;

	private VoLimitedTextField weightField = null;

	private JDateChooser visitDateFieldCal = null; // Calendar

	private JComboBox treatmTypeBox = null;

	private final int preferredWidthDates = 110;
	
	private final int preferredWidthDiagnosis = 550;
	
	private final int preferredWidthTypes = 220;
	
	private final int preferredWidthTransfusionSpinner = 55;

	private final int preferredHeightLine = 24;
	
	private GregorianCalendar deliveryDate = null;

	private VoDateTextField deliveryDateField = null;

	private JDateChooser deliveryDateFieldCal = null;
	
	private JButton deliveryDateAndTimeButton;

	private JComboBox deliveryTypeBox = null;

	private JComboBox managementBox = null;
	
	private JComboBox typeRemovePlacentaBox = null;
	
	private JComboBox ancBox = null;
	
	private JComboBox hivTestPlaceBox = null;

	private JComboBox deliveryResultTypeBox = null;
	
	private ArrayList<PregnantTreatmentType> treatmTypeList = null;

	private ArrayList<DeliveryType> deliveryTypeList = null;

	private ArrayList<DeliveryResultType> deliveryResultTypeList = null;

	private GregorianCalendar ctrl1Date = null;

	private GregorianCalendar ctrl2Date = null;

	private GregorianCalendar abortDate = null;

	private JDateChooser ctrl1DateFieldCal = null;

	private JDateChooser ctrl2DateFieldCal = null;

	private JDateChooser abortDateFieldCal = null;
	
	private JComboBox wardBox;
	
	private JButton jButtonTransfer;

	private ArrayList<Ward> wardList = null;

	// save value during a switch
	private Ward saveWard = null;

	private String saveYProg = null;

	private JTextField yProgTextField = null;

	private JTextField FHUTextField = null;

	private JPanel wardPanel;

	private JPanel fhuPanel;

	private JPanel yearProgPanel;
	
	private JComboBox diseaseInBox;
	
	private DiseaseBrowserManager dbm = new DiseaseBrowserManager();

	private ArrayList<Disease> diseaseInList = dbm.getDiseaseIpdIn();

	private ArrayList<Disease> diseaseOutList = dbm.getDiseaseIpdOut();

	private JCheckBox malnuCheck;

	private JPanel diseaseInPanel;

	private JPanel malnuPanel;
	
	private GregorianCalendar dateIn = null;

	private JDateChooser dateInFieldCal = null;

	private JComboBox admTypeBox = null;

	private ArrayList<AdmissionType> admTypeList = null;

	private DateFormat currentDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, new Locale(GeneralData.LANGUAGE));

	private JPanel admissionDatePanel;

	private JPanel admissionTypePanel;
	
	private JComboBox diseaseOut1Box = null;
	
	private JComboBox diseaseOut2Box = null;
	
	private JComboBox diseaseOut3Box = null;

	private JPanel diseaseOutPanel;
	
	private JPanel diseaseOut1Panel;
	
	private JPanel diseaseOut2Panel;
	
	private JPanel diseaseOut3Panel;
	
	private JComboBox operationBox = null;

	private JRadioButton operationResultRadioP = null;

	private JRadioButton operationResultRadioN = null;

	private JRadioButton operationResultRadioU = null;

	private ArrayList<Operation> operationList = null;
	
	private GregorianCalendar operationDate = null;

	private JDateChooser operationDateFieldCal = null;

	private VoDateTextField operationDateField = null;

	private float trsfUnit = 0.0f;

	private JSpinner trsfUnitField = null;
	
	private GregorianCalendar dateOut = null;

	private JDateChooser dateOutFieldCal = null;

	private JComboBox disTypeBox = null;
	
	private ArrayList<DischargeType> disTypeList = null;

	private JPanel dischargeDatePanel;

	private JPanel dischargeTypePanel;
	
	private JPanel bedDaysPanel;

	private JPanel buttonPanel = null;

	private JLabel labelRequiredFields;

	private JButton closeButton = null;
	
	private JButton newPrenatalVisitButton = null;
	
	private JButton newPostnatalVisitButton = null;
	
	private JButton seePregnancyVisitButton = null;

	private JButton saveButton = null;

	private JPanel operationDatePanel;

	private JPanel transfusionPanel;

	private JPanel operationPanel;

	private JPanel resultPanel;

	private JPanel visitDatePanel;

	private JPanel weightPanel;

	private JPanel treatmentPanel;
	
	private JPanel deliveryDatePanel;

	private JPanel deliveryTypePanel;
	
	private JPanel robsonIndexPanel;
	
	private JPanel managementPanel;
	
	private JPanel complicationsPanel;
	
	private JPanel typeRemovePlacentaPanel;
	
	private JPanel hivTestStatusPanel;
	
	private JPanel hivTestStatusPartnerPanel;
	
	private JPanel bloodPressurePanel;
	
	private JPanel ancPanel;
	
	private JPanel motherWaitingHomePanel;

	private JPanel deliveryResultTypePanel;

	private JPanel control1DatePanel;

	private JPanel control2DatePanel;

	private JPanel abortDatePanel;
	
	private JPanel newBorn1Panel;

	private JPanel sexPanel1;
	
	private JRadioButton sex1Female;
	
	private JRadioButton sex1Male;
	
	private JPanel weightAndHeightPanel1;
	
	private VoLimitedTextField weight1TextField;
	
	private VoLimitedTextField height1TextField;
	
	private VoLimitedTextField bloodPressureMinField;
	
	private VoLimitedTextField bloodPressureMaxField;
	
	private JPanel delrestype1;
	
	private JPanelApgarScore apgar1Panel;
	
	private JPanelApgarScore apgar2Panel;
	
	private JPanelApgarScore apgar3Panel;
	
	private JComboBox delrestypeBox1;
	
	private JPanel newBorn2Panel;
	
	private JPanel sexPanel2;
	
	private JRadioButton sex2Female;
	
	private JRadioButton sex2Male;
	
	private JPanel weightAndHeightPanel2;
	
	private VoLimitedTextField weight2TextField;
	
	private VoLimitedTextField height2TextField;
	
	private JPanel delrestype2;
	
	private JComboBox delrestypeBox2;
	
	private JPanel newBorn3Panel;
	
	private JPanel sexPanel3;
	
	private JRadioButton sex3Female;
	
	private JRadioButton sex3Male;
	
	private JPanel weightAndHeightPanel3;
	
	private VoLimitedTextField weight3TextField;
	
	private VoLimitedTextField height3TextField;
	
	private JPanel delrestype3;
	
	private JComboBox delrestypeBox3;

	private VoLimitedTextField bedDaysTextField;
	
	private JComboBox shareWith=null;
	
	private JCheckBox newborn2EnableCheckbox;
	
	private JCheckBox newborn3EnableCheckBox;
	
	private JPanel panelGravida;
	
	private JPanel panelParity;
	
	private JPanel panelAbort;

	private JRadioButton hivPositive;

	private JRadioButton hivNegative;

	private JRadioButton hivUnknown;

	private JRadioButton hivPartnerPositive;

	private JRadioButton hivPartnerNegative;

	private JRadioButton hivPartnerUnknown;

	private JRadioButton mwhYes;

	private JRadioButton mwhNo;

	private VoLimitedTextField textFieldGravida;

	private VoLimitedTextField textFieldParity;

	private VoLimitedTextField textFieldAbort;
	
	private ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
	
	private Pregnancy pregnancy;
	
	private Ward transferWard;
	
	private JComboBox robsonIndexComboBox;
	
	private final String LABEL_HEIGHT = "H (cm)";

	private final String LABEL_WEIGHT = "W (g)";

	private final String EMERGENCY_WARD_CODE = "Q";

	private final int BED_DAYS_THRESHOLD = 15;
	
	private boolean deleteNewBorn2;
	
	private boolean deleteNewBorn3;
	
	private JPanel childName1Panel;
	private VoLimitedTextField childName1;
	private JPanel childName2Panel;
	private VoLimitedTextField childName2;
	private JPanel childName3Panel;
	private VoLimitedTextField childName3;

	private AdmissionBrowserManager admMan = new AdmissionBrowserManager();
	private DeliveryResultTypeBrowserManager drtbm = new DeliveryResultTypeBrowserManager();
	private PregnancyDeliveryManager deliveryManager = new PregnancyDeliveryManager();
	private PregnancyCareManager pregnancyManager = new PregnancyCareManager(); 
	
	private AdmissionBrowser myFrame = null;

	private JPanel estimatedGestationalAgePanel;

	private JFormattedTextField estimatedGestationalAgeTextField;

	private JCheckBox compCheckBoxs[];

	private HashMap<String, Boolean> complicationsHashMap;

	private boolean lmpChecked = false;

	/*
	 * from AdmittedPatientBrowser
	 */
	public AdmissionBrowser(JFrame parentFrame, AdmittedPatient admPatient, boolean editing) {
		super(parentFrame, (editing ? MessageBundle.getMessage("angal.admission.editadmissionrecord") : MessageBundle.getMessage("angal.admission.newadmission")), true);
		addAdmissionListener((AdmissionListener) parentFrame);
		this.editing = editing;
		myFrame = this;
		patient = admPatient.getPatient();
		if (("" + patient.getSex()).equalsIgnoreCase("F")) {
			enablePregnancy = true;
		}
		ps = new PatientSummary(patient);
		if (editing) {
			admission = admMan.getCurrentAdmission(patient);
			if (admission.getWardId().equalsIgnoreCase("M")) {
				viewingPregnancy = true;
				deliveries = deliveryManager.getDeliveriesOfAdmission(admission);
				if (deliveries != null && deliveries.size() > 0 && pregnancyManager.hasRelatedPregnancy(deliveries.get(0))) //already delivered
					pregnancy = pregnancyManager.getPregnancy(deliveries.get(0).getPregnancy().getId()); //get closed pregnancy (N - delievered)
				else if (pregnancyManager.hasActivePregnancy(patient)) { //pregnant?
					pregnancy = pregnancyManager.getActivePregnancy(patient.getCode()); //get active pregnancy
				}
				dateIn = admission.getAdmDate();
			} 
		} else {
			admission = new Admission();
			dateIn = new GregorianCalendar(); //RememberDates.getLastAdmInDateGregorian();
			if (pregnancyManager.hasActivePregnancy(patient))
				pregnancy = pregnancyManager.getActivePregnancy(patient.getCode());
		}
		
		initialize(parentFrame);
		
		this.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent e) {
				//to free memory
				if (diseaseInList != null) diseaseInList.clear();
				if (diseaseOutList != null) diseaseOutList.clear();
				dispose();
			}			
		});
	}

	/*
	 * from PatientDataBrowser
	 */
	public AdmissionBrowser(JFrame parentFrame, JFrame parentParentFrame, Patient aPatient, Admission anAdmission) {
		super(parentFrame, MessageBundle.getMessage("angal.admission.editadmissionrecord"), true);
		addAdmissionListener((AdmissionListener) parentParentFrame);
		addAdmissionListener((AdmissionListener) parentFrame);
		myFrame = this;
		this.editing = true;
		patient = aPatient;
		if (("" + patient.getSex()).equalsIgnoreCase("F")) {
			enablePregnancy = true;
		}
		ps = new PatientSummary(patient);

		admission = admMan.getAdmission(anAdmission.getId());
		if (admission.getWardId().equalsIgnoreCase("M")) {
			viewingPregnancy = true;
			deliveries = deliveryManager.getDeliveriesOfAdmission(admission);
			if (deliveries != null && deliveries.size() > 0 && pregnancyManager.hasRelatedPregnancy(deliveries.get(0))) //already delivered
				pregnancy = pregnancyManager.getPregnancy(deliveries.get(0).getPregnancy().getId()); //get closed pregnancy (N - delievered)
			else if (pregnancyManager.hasActivePregnancy(patient)) { //pregnant?
				pregnancy = pregnancyManager.getActivePregnancy(patient.getCode()); //get active pregnancy
			}
		} 
		
		if (editing) {
			dateIn = admission.getAdmDate();
		} else {
			dateIn = new GregorianCalendar(); //RememberDates.getLastAdmInDateGregorian();
		}
		
		initialize(parentFrame);
		
		this.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent e) {
				//to free memory
				if (diseaseInList != null) diseaseInList.clear();
				if (diseaseOutList != null) diseaseOutList.clear();
				dispose();
			}			
		});
		
	}

	/*
	 * Entering from PregnancyCareBrowser
	 */
	public AdmissionBrowser(JFrame parentFrame, Patient pregPatient, int admId, boolean editing) {
		super(parentFrame, MessageBundle.getMessage("angal.admission.editadmissionrecord"), true);
		addAdmissionListener((AdmissionListener) parentFrame);
		myFrame = this;
		this.editing = editing;
		patient = pregPatient;
		ps = new PatientSummary(patient);
		AdmissionBrowserManager abm = new AdmissionBrowserManager();
		enablePregnancy = true;
		viewingPregnancy = true;
		if (editing){
			admission = abm.getAdmission(admId);
			deliveries = deliveryManager.getDeliveriesOfAdmission(admission);
			if (deliveries != null && deliveries.size() > 0 && pregnancyManager.hasRelatedPregnancy(deliveries.get(0))) //already delivered
				pregnancy = pregnancyManager.getPregnancy(deliveries.get(0).getPregnancy().getId()); //get closed pregnancy (N - delievered)
			else if (pregnancyManager.hasActivePregnancy(patient)) { //pregnant?
				pregnancy = pregnancyManager.getActivePregnancy(patient.getCode()); //get active pregnancy
			}
			dateIn = admission.getAdmDate();
		}else{
			admission = new Admission();
			admission.setWardId("M");
			int nextProg = admMan.getNextYProg("M");
			admission.setYProg(nextProg);
			saveYProg = new Integer(nextProg).toString();
			dateIn = new GregorianCalendar();
		}
		initialize(parentFrame);
		if (editing) jTabbedPaneAdmission.setSelectedIndex(2);
	}
	
	private void initialize(JFrame parent) {

		this.add(getJContentPane(), BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getDataPanel(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private JPanel getDataPanel() {
		JPanel data = new JPanel();
		data.setLayout(new BorderLayout());
		//data.add(getPatientDataPanel(), java.awt.BorderLayout.WEST);  //rimosso per lo schermo del netbook
		data.add(getJTabbedPaneAdmission(), java.awt.BorderLayout.CENTER);
		return data;
	}

	private JPanel getPatientDataPanel() {
		JPanel data = new JPanel();
		data.add(ps.getPatientCompleteSummary());
		return data;
	}

	private JTabbedPane getJTabbedPaneAdmission() {
		if (jTabbedPaneAdmission == null) {
			jTabbedPaneAdmission = new JTabbedPane();
			jTabbedPaneAdmission.addTab(MessageBundle.getMessage("angal.admission.admissionanddischarge"), getAdmissionTab());
			jTabbedPaneAdmission.addTab(MessageBundle.getMessage("angal.admission.operation"), getOperationTab());
			if (enablePregnancy) {
				jTabbedPaneAdmission.addTab(MessageBundle.getMessage("angal.admission.delivery"), getDeliveryTab());
				pregnancyTabIndex = jTabbedPaneAdmission.getTabCount() - 1;
				if (!viewingPregnancy) {
					jTabbedPaneAdmission.setEnabledAt(pregnancyTabIndex, false);
				}
			}
			jTabbedPaneAdmission.addTab("Note", getJPanelNote());
		}
		return jTabbedPaneAdmission;
	}

	private JPanel getAdmissionTab() {
		if (jPanelAdmission == null) {
			jPanelAdmission = new JPanel();

			GroupLayout layout = new GroupLayout(jPanelAdmission);
			jPanelAdmission.setLayout(layout);
			
//			layout.setAutoCreateGaps(true);
//			layout.setAutoCreateContainerGaps(true);

			layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(LEADING)
					.addComponent(getDiseaseInPanel())
					.addComponent(getDiseaseOutPanel())
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(LEADING)
								.addComponent(getWardPanel())
								.addComponent(getAdmissionDatePanel())
								.addComponent(getDischargeDatePanel())
						)
						.addGroup(layout.createParallelGroup(LEADING)
								.addComponent(getFHUPanel())
								.addComponent(getAdmissionTypePanel())
								.addComponent(getBedDaysPanel())
						)
						.addGroup(layout.createParallelGroup(LEADING)
								.addComponent(getProgYearPanel())
								.addComponent(getMalnutritionPanel())
								.addComponent(getDischargeTypePanel())
								.addComponent(getJLabelRequiredFields())
						)
					)
				)
			);

			layout.setVerticalGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(BASELINE)
							.addComponent(getWardPanel())
							.addComponent(getFHUPanel())
							.addComponent(getProgYearPanel())
					)
					.addGroup(layout.createParallelGroup(BASELINE)
							.addComponent(getAdmissionDatePanel())
							.addComponent(getAdmissionTypePanel())
							.addComponent(getMalnutritionPanel())
					)
					.addComponent(getDiseaseInPanel())
					.addGroup(layout.createParallelGroup(BASELINE)
							.addComponent(getDischargeDatePanel())
							.addComponent(getBedDaysPanel())
							.addComponent(getDischargeTypePanel())
					)
					.addComponent(getDiseaseOutPanel())
					.addComponent(getJLabelRequiredFields())
			);
		}
		return jPanelAdmission;
	}

	private JPanel getOperationTab() {
		if (jPanelOperation == null) {
			jPanelOperation = new JPanel();
			
			GroupLayout layout = new GroupLayout(jPanelOperation);
			jPanelOperation.setLayout(layout);
			
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			
			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(LEADING)
						.addComponent(getOperationDatePanel(), GroupLayout.PREFERRED_SIZE, preferredWidthDates, GroupLayout.PREFERRED_SIZE)
						.addComponent(getOperationPanel(), GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(layout.createSequentialGroup()
							.addComponent(getOperationResultPanel())
							.addComponent(getTransfusionPanel())
						)
					)
			);
			
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addComponent(getOperationDatePanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(getOperationPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(layout.createParallelGroup(BASELINE)
							.addComponent(getOperationResultPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getTransfusionPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					)
			);
		}
		return jPanelOperation;
	}

	private JPanel getDeliveryTab() {
		if (jPanelDelivery == null) {
			jPanelDelivery = new JPanel();
			if (GeneralData.PREGNANCYCARE){
				
				GroupLayout layout = new GroupLayout(jPanelDelivery);
				jPanelDelivery.setLayout(layout);
				layout.setAutoCreateGaps(true);
				layout.setAutoCreateContainerGaps(true);
				
				layout.setHorizontalGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(getDeliveryDatePanel())
										.addComponent(getEstimatedGestationalAge())
										.addComponent(getRobsonIndexPanel())
										.addComponent(getDeliveryTypePanel())
								)
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(LEADING)
//												.addComponent(getnewPrenatalVisitButton(), Alignment.CENTER)
												.addComponent(getJPanelGravida())
												.addComponent(getNewborn1Panel())
										)
										.addGroup(layout.createParallelGroup(LEADING)
//												.addComponent(getnewPostnatalVisitButton(), Alignment.CENTER)
												.addComponent(getJPanelParity())
												.addComponent(getNewborn2EnableCheckbox())
												.addComponent(getNewborn2Panel())
										)
										.addGroup(layout.createParallelGroup(LEADING)
//												.addComponent(getSeePregnancyVisitsButton(),Alignment.CENTER)
												.addComponent(getJPanelAbort())
												.addComponent(getNewborn3EnableCheckbox())
												.addComponent(getNewborn3Panel())
										)
								)
								.addGroup(layout.createSequentialGroup()
										.addComponent(getManagementPanel())
										.addComponent(getComplicationsPanel())
								)
								.addGroup(layout.createSequentialGroup()
										.addComponent(getTypeOfRemovalOfPlacentaPanel())
										.addComponent(getHIVTestStatusPanel())
										.addComponent(getHIVTestStatusPartnerPanel())
								)
								.addGroup(layout.createSequentialGroup()
										.addComponent(getBloodPressurePanel())
										.addComponent(getANCPanel())
										.addComponent(getMotherWaitingHomePanel())
								)
						)
				);
				
				layout.setVerticalGroup(layout.createSequentialGroup()
//						.addGroup(layout.createParallelGroup(BASELINE)
//								.addComponent(getnewPrenatalVisitButton())
//								.addComponent(getnewPostnatalVisitButton())
//								.addComponent(getSeePregnancyVisitsButton())
//						)
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(getJPanelGravida(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getJPanelParity(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getJPanelAbort(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addGroup(layout.createParallelGroup()
								.addComponent(getDeliveryDatePanel())
								.addComponent(getEstimatedGestationalAge())
								.addComponent(getRobsonIndexPanel())
								.addComponent(getDeliveryTypePanel())	
						)
						.addGroup(layout.createParallelGroup(Alignment.TRAILING)
								.addComponent(getNewborn2EnableCheckbox())
								.addComponent(getNewborn3EnableCheckbox())
						)
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(getNewborn1Panel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getNewborn2Panel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getNewborn3Panel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(getTypeOfRemovalOfPlacentaPanel())
								.addComponent(getHIVTestStatusPanel())
								.addComponent(getHIVTestStatusPartnerPanel())
						)
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(getManagementPanel())
								.addComponent(getComplicationsPanel())
						)
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(getBloodPressurePanel())
								.addComponent(getANCPanel())
								.addComponent(getMotherWaitingHomePanel())
						)
						
				);
			}
			else
			{
				GroupLayout layout = new GroupLayout(jPanelDelivery);
				jPanelDelivery.setLayout(layout);
				layout.setAutoCreateGaps(true);
				layout.setAutoCreateContainerGaps(true);
				layout.setHorizontalGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(LEADING)
								.addComponent(getVisitDatePanel(), GroupLayout.PREFERRED_SIZE, preferredWidthDates, GroupLayout.PREFERRED_SIZE)
								.addComponent(getDeliveryDatePanel(), GroupLayout.PREFERRED_SIZE, preferredWidthDates, GroupLayout.PREFERRED_SIZE)
						)
						.addGroup(layout.createParallelGroup(LEADING)
								.addComponent(getWeightPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getDeliveryTypePanel())
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(getTreatmentPanel())
								.addComponent(getDeliveryResultTypePanel())
								.addComponent(getControl1DatePanel(), GroupLayout.PREFERRED_SIZE, preferredWidthDates, GroupLayout.PREFERRED_SIZE)
								.addComponent(getControl2DatePanel(), GroupLayout.PREFERRED_SIZE, preferredWidthDates, GroupLayout.PREFERRED_SIZE)
								.addComponent(getAbortDatePanel(), GroupLayout.PREFERRED_SIZE, preferredWidthDates, GroupLayout.PREFERRED_SIZE)
						)
				);
				
				layout.setVerticalGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(getVisitDatePanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getWeightPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getTreatmentPanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(getDeliveryDatePanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getDeliveryTypePanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getDeliveryResultTypePanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addGroup(layout.createParallelGroup()
								.addComponent(getControl1DatePanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addGroup(layout.createParallelGroup()
								.addComponent(getControl2DatePanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
						.addGroup(layout.createParallelGroup()
								.addComponent(getAbortDatePanel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						)
				);
				layout.linkSize(SwingConstants.VERTICAL, getDeliveryDatePanel(), getDeliveryTypePanel(), getDeliveryResultTypePanel());
			}
		}
		return jPanelDelivery;
	}
	
	private JPanel getNewborn1Panel(){
		if (newBorn1Panel== null){
			newBorn1Panel= new JPanel();
			newBorn1Panel.setLayout(new BoxLayout(newBorn1Panel, BoxLayout.PAGE_AXIS));
			newBorn1Panel.add(getNewborn1SexPanel());
			newBorn1Panel.add(getNewborn1WeightAndHeightPanel());
			newBorn1Panel.add(getNewborn1DeliveryResultTypePanel());
			newBorn1Panel.add(getNewborn1ApgarScore());
			newBorn1Panel.add(getNewborn1Name());
		}
		
		newBorn1Panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newborn1details")));
		return newBorn1Panel;
	}
	
	private JPanel getNewborn1SexPanel(){
		if (sexPanel1== null){
			sexPanel1 = new JPanel();
			sex1Female = new JRadioButton("F");
			sex1Male = new JRadioButton("M");
			ButtonGroup resultGroup = new ButtonGroup();
			resultGroup.add(sex1Female);
			resultGroup.add(sex1Male);
			if (deliveries != null && deliveries.size() > 0){
				if (deliveries.get(0).getSex().equals("M"))
					sex1Male.setSelected(true);
				else
					sex1Female.setSelected(true);
			}
				
			sexPanel1.add(sex1Female);
			sexPanel1.add(sex1Male);
			
		}
		return sexPanel1;
	}
	
	private JPanel getNewborn1WeightAndHeightPanel(){
		if (weightAndHeightPanel1 == null) {
			weightAndHeightPanel1 = new JPanel();
			
			weight1TextField = new VoLimitedTextField(6, 6);
			weight1TextField.setHorizontalAlignment(JTextField.CENTER);
			height1TextField = new VoLimitedTextField(6, 6);
			height1TextField.setHorizontalAlignment(JTextField.CENTER);
			if (deliveries != null && deliveries.size() > 0){
				weight1TextField.setText(String.valueOf(deliveries.get(0).getWeight()));
				height1TextField.setText(String.valueOf(deliveries.get(0).getHeight()));
			}
			weightAndHeightPanel1.add(new JLabel(LABEL_WEIGHT));
			weightAndHeightPanel1.add(weight1TextField);
			weightAndHeightPanel1.add(new JLabel(LABEL_HEIGHT));
			weightAndHeightPanel1.add(height1TextField);
			//weightAndHeightPanel1.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.weightandheight")));
			BorderedPanel.setMyBorderAlign(weightAndHeightPanel1, MessageBundle.getMessage("angal.admission.weightandheight"), TitledBorder.CENTER);
		}
		return weightAndHeightPanel1;
	}
	
	private JPanel getNewborn1DeliveryResultTypePanel(){
		if (delrestype1 == null) {
			delrestype1 = new JPanel();
			delrestypeBox1 = new JComboBox();
			delrestypeBox1.addItem("");
			if (deliveryResultTypeList == null)
				deliveryResultTypeList = drtbm.getDeliveryResultType();
			for (DeliveryResultType elem : deliveryResultTypeList) {
				if (elem.getDescription().length()>21)
					elem.setDescription(elem.getDescription().substring(0, 20));
				delrestypeBox1.addItem(elem);
				if (deliveries != null && deliveries.size() > 0 
						&& deliveries.get(0).getDeliveryResultType()!= null 
						&& deliveries.get(0).getDeliveryResultType().getCode().equalsIgnoreCase(elem.getCode())) {
					delrestypeBox1.setSelectedItem(elem);
				}
				
				
			}
			delrestype1.add(delrestypeBox1);
			//delrestype1.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.deliveryresultype")));
			BorderedPanel.setMyBorderAlign(delrestype1, MessageBundle.getMessage("angal.admission.deliveryresultype"), TitledBorder.CENTER);
		}
		return delrestype1;
	}
	
	private JPanel getNewborn1ApgarScore(){
		if (apgar1Panel == null) {
			if (deliveries != null && deliveries.size() > 0) {
				apgar1Panel = new JPanelApgarScore(deliveries.get(0).getApgarScore());
			} else apgar1Panel = new JPanelApgarScore();
			BorderedPanel.setMyBorderAlign(apgar1Panel, MessageBundle.getMessage("angal.admission.apgar"), TitledBorder.CENTER);
		}
		return apgar1Panel;
	}
	
	private JPanel getNewborn1Name() {
		if (childName1Panel == null) {
			childName1Panel = new JPanel();
			childName1 = new VoLimitedTextField(45,15);
			childName1.setHorizontalAlignment(JTextField.CENTER);
			
			if (deliveries != null && deliveries.size() > 0){
				childName1.setText(deliveries.get(0).getChildName());
			}
			
			childName1Panel.add(childName1);
			//childName1Panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.apgar")));
			BorderedPanel.setMyBorderAlign(childName1Panel, MessageBundle.getMessage("angal.admission.childname"), TitledBorder.CENTER);
		}
		return childName1Panel;
	}
	
	private JPanel getNewborn2Panel(){
		if (newBorn2Panel== null){
			newBorn2Panel= new JPanel();
			newBorn2Panel.setLayout(new BoxLayout(newBorn2Panel, BoxLayout.PAGE_AXIS));
			newBorn2Panel.add(getNewborn2SexPanel());
			newBorn2Panel.add(getNewborn2WeightAndHeightPanel());
			newBorn2Panel.add(getNewborn2DeliveryResultTypePanel());
			newBorn2Panel.add(getNewborn2ApgarScore());
			newBorn2Panel.add(getNewborn2Name());
//			newBorn2Panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newborn2details")));
			BorderedPanel.setMyBorderAlign(newBorn2Panel, MessageBundle.getMessage("angal.admission.newborn2details"), TitledBorder.LEFT);
		}
		if (!newborn2EnableCheckbox.isSelected())
			enableDisableFields(newBorn2Panel, false);		
		return newBorn2Panel;
	}
	
	private JPanel getNewborn2SexPanel(){
		if (sexPanel2== null){
			sexPanel2 = new JPanel();
			sex2Female = new JRadioButton("F");
			sex2Male = new JRadioButton("M");
			ButtonGroup resultGroup = new ButtonGroup();
			resultGroup.add(sex2Female);
			resultGroup.add(sex2Male);
			if (deliveries != null && deliveries.size() > 1){
				if (deliveries.get(1).getSex().equals("M"))
					sex2Male.setSelected(true);
				else
					sex2Female.setSelected(true);
			}
			sexPanel2.add(sex2Female);
			sexPanel2.add(sex2Male);
			
		}
		return sexPanel2;
	}
	
	private JPanel getNewborn2WeightAndHeightPanel(){
		if (weightAndHeightPanel2 == null) {
			weightAndHeightPanel2 = new JPanel();
			
			weight2TextField = new VoLimitedTextField(6, 6);
			height2TextField = new VoLimitedTextField(6, 6);
			weight2TextField.setHorizontalAlignment(JTextField.CENTER);
			height2TextField.setHorizontalAlignment(JTextField.CENTER);
			if (deliveries != null && deliveries.size() > 1){
				weight2TextField.setText(String.valueOf(deliveries.get(1).getWeight()));
				height2TextField.setText(String.valueOf(deliveries.get(1).getHeight()));
			}
			weightAndHeightPanel2.add(new JLabel(LABEL_WEIGHT));
			weightAndHeightPanel2.add(weight2TextField);
			weightAndHeightPanel2.add(new JLabel(LABEL_HEIGHT));
			weightAndHeightPanel2.add(height2TextField);
			//weightAndHeightPanel2.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.weightandheight")));
			BorderedPanel.setMyBorderAlign(weightAndHeightPanel2, MessageBundle.getMessage("angal.admission.weightandheight"), TitledBorder.CENTER);
		}
		return weightAndHeightPanel2;
	}
	
	private JPanel getNewborn2DeliveryResultTypePanel(){
		if (delrestype2 == null) {
			delrestype2 = new JPanel();
			delrestypeBox2 = new JComboBox();
			delrestypeBox2.addItem("");
			if (deliveryResultTypeList == null)
				deliveryResultTypeList = drtbm.getDeliveryResultType();
			for (DeliveryResultType elem : deliveryResultTypeList) {
				if (elem.getDescription().length()>21)
					elem.setDescription(elem.getDescription().substring(0, 20));
				delrestypeBox2.addItem(elem);
				if (deliveries != null && deliveries.size() > 1 
						&& deliveries.get(1).getDeliveryResultType() != null 
						&& deliveries.get(1).getDeliveryResultType().getCode().equalsIgnoreCase(elem.getCode())) {
					delrestypeBox2.setSelectedItem(elem);
				}
				
			}
			delrestype2.add(delrestypeBox2);
			//delrestype2.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.deliveryresultype")));
			BorderedPanel.setMyBorderAlign(delrestype2, MessageBundle.getMessage("angal.admission.deliveryresultype"), TitledBorder.CENTER);
		}
		return delrestype2;
	}
	
	private JPanel getNewborn2ApgarScore(){
		if (apgar2Panel == null) {
			if (deliveries != null && deliveries.size() > 1) {
				apgar2Panel = new JPanelApgarScore(deliveries.get(1).getApgarScore());
			} else apgar2Panel = new JPanelApgarScore();
			BorderedPanel.setMyBorderAlign(apgar2Panel, MessageBundle.getMessage("angal.admission.apgar"), TitledBorder.CENTER);
		}
		return apgar2Panel;
	}
	
	private JPanel getNewborn2Name() {
		if (childName2Panel == null) {
			childName2Panel = new JPanel();
			childName2 = new VoLimitedTextField(45,15);
			childName2.setHorizontalAlignment(JTextField.CENTER);
			
			if (deliveries != null && deliveries.size() > 1){
				childName2.setText(deliveries.get(1).getChildName());
			}
			
			childName2Panel.add(childName2);
			BorderedPanel.setMyBorderAlign(childName2Panel, MessageBundle.getMessage("angal.admission.childname"), TitledBorder.CENTER);
		}
		return childName2Panel;
	}
	
	private JPanel getNewborn3Panel(){
		if (newBorn3Panel== null){
			newBorn3Panel= new JPanel();
			newBorn3Panel.setLayout(new BoxLayout(newBorn3Panel, BoxLayout.PAGE_AXIS));
			newBorn3Panel.add(getNewborn3SexPanel());
			newBorn3Panel.add(getNewborn3WeightAndHeightPanel());
			newBorn3Panel.add(getNewborn3DeliveryResultTypePanel());
			newBorn3Panel.add(getNewborn3ApgarScore());
			newBorn3Panel.add(getNewborn3Name());
//			newBorn3Panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newborn3details")));
			BorderedPanel.setMyBorderAlign(newBorn3Panel, MessageBundle.getMessage("angal.admission.newborn3details"), TitledBorder.LEFT);
		}
		if (!newborn3EnableCheckBox.isSelected())
			enableDisableFields(newBorn3Panel, false);
		return newBorn3Panel;
	}
	
	private JPanel getNewborn3SexPanel(){
		if (sexPanel3== null){
			sexPanel3 = new JPanel();
			sex3Female = new JRadioButton("F");
			sex3Male = new JRadioButton("M");
			ButtonGroup resultGroup = new ButtonGroup();
			resultGroup.add(sex3Female);
			resultGroup.add(sex3Male);
			if (deliveries != null && deliveries.size() > 2){
				if (deliveries.get(2).getSex().equals("M"))
					sex3Male.setSelected(true);
				else
					sex3Female.setSelected(true);
			}
			sexPanel3.add(sex3Female);
			sexPanel3.add(sex3Male);
			
		}
		
		return sexPanel3;
	}
	
	private JPanel getNewborn3WeightAndHeightPanel(){
		if (weightAndHeightPanel3 == null) {
			weightAndHeightPanel3 = new JPanel();
			
			weight3TextField = new VoLimitedTextField(6, 6);
			height3TextField = new VoLimitedTextField(6, 6);
			weight3TextField.setHorizontalAlignment(JTextField.CENTER);
			height3TextField.setHorizontalAlignment(JTextField.CENTER);
			if (deliveries != null && deliveries.size() > 2){
				weight3TextField.setText(String.valueOf(deliveries.get(2).getWeight()));
				height3TextField.setText(String.valueOf(deliveries.get(2).getHeight()));
			}
			weightAndHeightPanel3.add(new JLabel(LABEL_WEIGHT));
			weightAndHeightPanel3.add(weight3TextField);
			weightAndHeightPanel3.add(new JLabel(LABEL_HEIGHT));
			weightAndHeightPanel3.add(height3TextField);
			//weightAndHeightPanel3.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.weightandheight")));
			BorderedPanel.setMyBorderAlign(weightAndHeightPanel3, MessageBundle.getMessage("angal.admission.weightandheight"), TitledBorder.CENTER);
		}
		
		return weightAndHeightPanel3;
	}
	
	private JPanel getNewborn3DeliveryResultTypePanel(){
		if (delrestype3 == null) {
			delrestype3 = new JPanel();
			delrestypeBox3 = new JComboBox();
			delrestypeBox3.addItem("");
			if (deliveryResultTypeList == null)
				deliveryResultTypeList = drtbm.getDeliveryResultType();
			for (DeliveryResultType elem : deliveryResultTypeList) {
				if (elem.getDescription().length()>21)
					elem.setDescription(elem.getDescription().substring(0, 20));
				delrestypeBox3.addItem(elem);
				if (deliveries != null && deliveries.size() > 2 
						&& deliveries.get(2).getDeliveryResultType()!= null 
						&& deliveries.get(2).getDeliveryResultType().getCode().equalsIgnoreCase(elem.getCode())) {
					delrestypeBox3.setSelectedItem(elem);
				}
			}
			
			delrestype3.add(delrestypeBox3);
			//delrestype3.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.deliveryresultype")));
			BorderedPanel.setMyBorderAlign(delrestype3, MessageBundle.getMessage("angal.admission.deliveryresultype"), TitledBorder.CENTER);
		}
		return delrestype3;
	}
	
	private JPanel getNewborn3ApgarScore(){
		if (apgar3Panel == null) {
			if (deliveries != null && deliveries.size() > 2) {
				apgar3Panel = new JPanelApgarScore(deliveries.get(2).getApgarScore());
			} else apgar3Panel = new JPanelApgarScore();
			BorderedPanel.setMyBorderAlign(apgar3Panel, MessageBundle.getMessage("angal.admission.apgar"), TitledBorder.CENTER);
		}
		return apgar3Panel;
	}
	
	private JPanel getNewborn3Name() {
		if (childName3Panel == null) {
			childName3Panel = new JPanel();
			childName3 = new VoLimitedTextField(45,15);
			childName3.setHorizontalAlignment(JTextField.CENTER);
			
			if (deliveries != null && deliveries.size() > 2){
				childName3.setText(deliveries.get(2).getChildName());
			}
			
			childName3Panel.add(childName3);
			BorderedPanel.setMyBorderAlign(childName3Panel, MessageBundle.getMessage("angal.admission.childname"), TitledBorder.CENTER);
		}
		return childName3Panel;
	}
	
	private JPanel getJPanelGravida() {
		if (panelGravida == null) {
			panelGravida = new JPanel();
			BorderedPanel.setMyBorderAlign(panelGravida, "Gravida", TitledBorder.CENTER);
			textFieldGravida = new VoLimitedTextField(2,5);
			textFieldGravida.setHorizontalAlignment(JTextField.CENTER);
			
			if (GeneralData.PREGNANCYCARE && pregnancy != null) {
				textFieldGravida.setText(String.valueOf(pregnancy.getGravida()));
			} 
			else textFieldGravida.setText("0");
			textFieldGravida.setInputVerifier(new InputVerifier() {
				
				@Override
				public boolean verify(JComponent input) {
					VoLimitedTextField thisInput = (VoLimitedTextField) input;
					int gravida = 0;
					boolean ok = true;
					try {
						gravida = Integer.parseInt(thisInput.getText());
						if (gravida < 0) ok = false;
					} catch (NumberFormatException ne) {
						ok = false;
					}
					if (!ok) {
						JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidgravida"));
						return false;
					}
					int parity;
					try {
						parity = Integer.parseInt(textFieldParity.getText());
					} catch (NumberFormatException ne) {
						textFieldParity.setText("0");
						parity = 0;
					}
					textFieldAbort.setText(String.valueOf(gravida - 1 - parity));
					return true;
				}
			});
			panelGravida.add(textFieldGravida);
		}
		return panelGravida;
	}
	
	private JPanel getJPanelParity() {
		if (panelParity == null) {
			panelParity = new JPanel();
			BorderedPanel.setMyBorderAlign(panelParity, "Parity", TitledBorder.CENTER);
			textFieldParity = new VoLimitedTextField(2,5);
			textFieldParity.setHorizontalAlignment(JTextField.CENTER);
			
			if (GeneralData.PREGNANCYCARE && pregnancy != null) {
				textFieldParity.setText(String.valueOf(pregnancy.getParity()));
			} 
			else textFieldParity.setText("0");
			textFieldParity.setInputVerifier(new InputVerifier() {
				
				@Override
				public boolean verify(JComponent input) {
					VoLimitedTextField thisInput = (VoLimitedTextField) input;
					int gravida;
					try {
						gravida = Integer.parseInt(textFieldGravida.getText());
					} catch (NumberFormatException ne) {
						textFieldGravida.setText("0");
						gravida = 0;
					}
					int parity = 0;
					boolean ok = true;
					try {
						parity = Integer.parseInt(thisInput.getText());
						if (parity < 0 || parity > gravida) ok = false;
					} catch (NumberFormatException ne) {
						ok = false;
					}
					if (!ok) {
						JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidparity"));
						textFieldParity.setText("0");
						return false;
					}
					textFieldAbort.setText(String.valueOf(gravida - 1 - parity));
					return true;
				}
			});
			panelParity.add(textFieldParity);
		}
		return panelParity;
	}
	
	private JPanel getJPanelAbort() {
		if (panelAbort == null) {
			panelAbort = new JPanel();
			BorderedPanel.setMyBorderAlign(panelAbort, "Aborts", TitledBorder.CENTER);
			textFieldAbort = new VoLimitedTextField(2,5);
			textFieldAbort.setHorizontalAlignment(JTextField.CENTER);
			
			if (GeneralData.PREGNANCYCARE && pregnancy != null) {
				textFieldAbort.setText(String.valueOf(pregnancy.getGravida() - 1 - pregnancy.getParity()));
			} 
			else textFieldAbort.setText("0");
			textFieldAbort.setEditable(false);
			panelAbort.add(textFieldAbort);
		}
		return panelAbort;
	}
	
	private JButton getnewPrenatalVisitButton(){
		if (newPrenatalVisitButton== null){
			newPrenatalVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.newprenatalvisit"));
			newPrenatalVisitButton.setIcon(new ImageIcon("rsc/icons/plus_button.png"));
			newPrenatalVisitButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int visitcount = deliveryManager.patientVisitcount(patient.getCode());
					if (visitcount <1){
						JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.pregnancy.nopregnancy"));
						return;

					}
					((JFrame)myFrame.getParent()).setAlwaysOnTop(false);
					((JFrame)myFrame.getParent()).toBack();
					myFrame.dispose();
					PregnancyEdit newPregnancyVisit = new PregnancyEdit(((JFrame)myFrame.getParent()), patient, -1);
					newPregnancyVisit.setVisible(true);
					newPregnancyVisit.toFront();
					newPregnancyVisit.requestFocus();
					
					
					
				}
			});
			
		}
		return newPrenatalVisitButton;
	}
	
	private JButton getnewPostnatalVisitButton(){
		if (newPostnatalVisitButton== null){
			newPostnatalVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.newpostnatalvisit"));
			newPostnatalVisitButton.setIcon(new ImageIcon("rsc/icons/plus_button.png"));
			newPostnatalVisitButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int visitcount = deliveryManager.patientVisitcount(patient.getCode());
					if (visitcount <1){
						JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.pregnancy.nopregnancy"));
						return;

					}
					((JFrame)myFrame.getParent()).setAlwaysOnTop(false);
					((JFrame)myFrame.getParent()).toBack();
					myFrame.dispose();
					PregnancyEdit newPregnancyVisit = new PregnancyEdit(((JFrame)myFrame.getParent()), patient, 1);
					newPregnancyVisit.setVisible(true);
					newPregnancyVisit.toFront();
					newPregnancyVisit.requestFocus();
					
					
				}
			});
			
		}
		return newPostnatalVisitButton;
	}
	private JButton getSeePregnancyVisitsButton(){
		if (seePregnancyVisitButton== null){
			seePregnancyVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.seepregnancyvisits"));
			seePregnancyVisitButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			seePregnancyVisitButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					((JFrame)myFrame.getParent()).setAlwaysOnTop(false);
					((JFrame)myFrame.getParent()).toBack();
					myFrame.dispose();
					PregnancyCareBrowser pregnancyCareBrowser = new PregnancyCareBrowser(patient);
					pregnancyCareBrowser.setVisible(true);
					pregnancyCareBrowser.toFront();
					pregnancyCareBrowser.requestFocus();
				}
			});
			
		}
		return seePregnancyVisitButton;
	}
	private JCheckBox getNewborn2EnableCheckbox(){
		if (newborn2EnableCheckbox== null){
			newborn2EnableCheckbox = new JCheckBox();
			newborn2EnableCheckbox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (newborn2EnableCheckbox.isSelected()) {
						enableDisableFields(newBorn2Panel, true);
						deleteNewBorn2 = false;
					} else {
						if (deliveries.size() > 1) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, "NewBorn 2 will be removed");
						}
						enableDisableFields(newBorn2Panel, false);
						deleteNewBorn2 = true;
					}
				}
			});
		}
		if (deliveries!= null && deliveries.size() > 1){
			newborn2EnableCheckbox.setSelected(true);
		}
		return newborn2EnableCheckbox;
	}
	
	private JCheckBox getNewborn3EnableCheckbox(){
		if (newborn3EnableCheckBox== null){
			newborn3EnableCheckBox = new JCheckBox();
			newborn3EnableCheckBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (newborn3EnableCheckBox.isSelected()) {
						enableDisableFields(newBorn3Panel, true);
						deleteNewBorn3 = false;
					} else {
						if (deliveries.size() > 2) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, "NewBorn 3 will be removed");
						}
						enableDisableFields(newBorn3Panel, false);
						deleteNewBorn3 = true;
					}
				}
			});
		}
		if (deliveries!= null && deliveries.size() > 2){
			newborn3EnableCheckBox.setSelected(true);
		}
		return newborn3EnableCheckBox;
	}
	
	private JButton getDeliveryDateAndTimeButton() {
		if (deliveryDateAndTimeButton == null) {
			deliveryDateAndTimeButton = new JButton(""); //$NON-NLS-1$
			deliveryDateAndTimeButton.setIcon(new ImageIcon("./rsc/icons/clock_button.png")); //$NON-NLS-1$
			deliveryDateAndTimeButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					JDateAndTimeChooserDialog schedDate = new JDateAndTimeChooserDialog(AdmissionBrowser.this, deliveryDateFieldCal.getDate());
					schedDate.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					schedDate.setVisible(true);
					
					Date date = schedDate.getDate();
					
					if (date != null) {
						
						deliveryDateFieldCal.setDate(date);
						updateGestationalAge(date);
					} else {
						return;
					}
				}
			});
		}
		return deliveryDateAndTimeButton;
	}
	
	protected void updateGestationalAge(Date date) {
		if (lmpChecked) return;
		if (pregnancy.getLmp() == null) {
			JOptionPane.showMessageDialog(AdmissionBrowser.this,
					MessageBundle.getMessage("angal.admission.lmpmissingnotpossibletocalculatethegestationalage"), 
					MessageBundle.getMessage("angal.admission.lmpmissing"),
					JOptionPane.WARNING_MESSAGE);
		} else {
			estimatedGestationalAgeTextField.setText(String.valueOf(TimeTools.getWeeksBetweenDates(pregnancy.getLmp(), Converters.toCalendar(date), true)));
		}
		lmpChecked  = true;
	}

	private JPanel getDeliveryDatePanel() {
		if (deliveryDatePanel == null) {
			deliveryDatePanel = new JPanel();
			
			Date myDate = null;
			if (editing) {
				if (GeneralData.PREGNANCYCARE && deliveries != null && deliveries.size() > 0 && deliveries.get(0).getDeliveryDate() != null){
					deliveryDate = deliveries.get(0).getDeliveryDate();
					myDate= deliveryDate.getTime();
				} else if (admission.getDeliveryDate() != null) {
					deliveryDate = admission.getDeliveryDate();
					myDate = deliveryDate.getTime();
				}
			}
			
			deliveryDateFieldCal = new JDateChooser(myDate);
			deliveryDateFieldCal.setLocale(new Locale(GeneralData.LANGUAGE));
			deliveryDateFieldCal.setDateFormatString("dd/MM/yyyy HH:mm:ss");
			deliveryDateFieldCal.addPropertyChangeListener("date", new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Date newDate = (Date) evt.getNewValue();
					deliveryDateFieldCal.setDate(newDate);
					updateGestationalAge(newDate);
				}
			});
			
			deliveryDatePanel.add(deliveryDateFieldCal);
			deliveryDatePanel.add(getDeliveryDateAndTimeButton());
			//deliveryDatePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.deliverydate")));
			BorderedPanel.setMyBorderAlign(deliveryDatePanel, MessageBundle.getMessage("angal.admission.deliverydate"), TitledBorder.CENTER);
		}
		return deliveryDatePanel;
	}

	private JPanel getDeliveryTypePanel() {
		if (deliveryTypePanel == null) {
			deliveryTypePanel = new JPanel();
			
			DeliveryTypeBrowserManager dtbm = new DeliveryTypeBrowserManager();
			deliveryTypeBox = new JComboBox();
			deliveryTypeBox.addItem("");
			deliveryTypeList = dtbm.getDeliveryType();
			for (DeliveryType elem : deliveryTypeList) {
				deliveryTypeBox.addItem(elem);
				if (editing) {
					if (GeneralData.PREGNANCYCARE){
						if (deliveries != null && deliveries.size() > 0 
								&& deliveries.get(0).getDeliveryType() != null 
								&& deliveries.get(0).getDeliveryType().getCode().equalsIgnoreCase(elem.getCode()))
						deliveryTypeBox.setSelectedItem(elem);
						
					}
					else if (admission.getDeliveryTypeId() != null && admission.getDeliveryTypeId().equalsIgnoreCase(elem.getCode())) {
						deliveryTypeBox.setSelectedItem(elem);
					}
				}
			}
			deliveryTypePanel.add(deliveryTypeBox);
			//deliveryTypePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.deliverytype")));
			BorderedPanel.setMyBorderAlign(deliveryTypePanel, MessageBundle.getMessage("angal.admission.deliverytype"), TitledBorder.CENTER);
		}
		return deliveryTypePanel;
	}
	
	private JPanel getEstimatedGestationalAge() {
		if (estimatedGestationalAgePanel == null) {
			estimatedGestationalAgePanel = new JPanel();
			
//			if (editing) {
				if (GeneralData.PREGNANCYCARE && deliveries != null && deliveries.size() > 0 && deliveries.get(0).getEstimatedGestationalAge() != 0){
					estimatedGestationalAgeTextField = FormattedTextField.getPositiveIntegerField(2, true, new Integer(deliveries.get(0).getEstimatedGestationalAge()));
				} else {
					if (GeneralData.PREGNANCYCARE && pregnancy != null) {
						int calculatedGA = pregnancy.calculateGestationalAge(); 
						estimatedGestationalAgeTextField = FormattedTextField.getPositiveIntegerField(2, true, new Integer(calculatedGA));
					} else
						estimatedGestationalAgeTextField = FormattedTextField.getPositiveIntegerField(2, true, new Integer(0));
				}
//			}
			estimatedGestationalAgeTextField.setHorizontalAlignment(JTextField.CENTER);
			estimatedGestationalAgePanel.add(estimatedGestationalAgeTextField);
			BorderedPanel.setMyBorderAlign(estimatedGestationalAgePanel, MessageBundle.getMessage("angal.admission.gestationalageabbr"), TitledBorder.CENTER);
		}
		return estimatedGestationalAgePanel;
	}
	
	private JPanel getRobsonIndexPanel() {
		if (robsonIndexPanel == null) {
			robsonIndexPanel = new JPanel();
			
			robsonIndexComboBox = new JComboBox();
			robsonIndexComboBox.addItem("");
			for (String elem : deliveryManager.getRobsonClassification(deliveryManager.ROBSON_CLASSES)) {
				robsonIndexComboBox.addItem(elem);
				if (editing) {
					if (GeneralData.PREGNANCYCARE){
						if (deliveries != null && deliveries.size() > 0 
								&& deliveries.get(0).getRobsonIndex() != null 
								&& deliveries.get(0).getRobsonIndex().equals(elem))
							robsonIndexComboBox.setSelectedItem(elem);
					}
				}
			}
			
			robsonIndexPanel.add(robsonIndexComboBox);
			BorderedPanel.setMyBorderAlign(robsonIndexPanel, MessageBundle.getMessage("angal.admission.robson"), TitledBorder.CENTER);
		}
		return robsonIndexPanel;
	}
	
	private JPanel getManagementPanel(){
		if (managementPanel == null) {
			managementPanel = new JPanel();
			managementBox = new JComboBox();
			managementBox.addItem("");
			for (String elem : deliveryManager.getManagementList()) {
				managementBox.addItem(elem);
				if (editing) {
					if (GeneralData.PREGNANCYCARE){
						if (deliveries != null && deliveries.size() > 0 
								&& deliveries.get(0).getManagement() != null 
								&& deliveries.get(0).getManagement().equals(elem))
							managementBox.setSelectedItem(elem);
					}
				}
			}
			managementPanel.add(managementBox);
			//managementPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.management")));
			BorderedPanel.setMyBorderAlign(managementPanel, MessageBundle.getMessage("angal.admission.management"), TitledBorder.CENTER);
		}
		return managementPanel;
	}
	
	private JPanel getComplicationsPanel(){
		if (complicationsPanel == null) {
			complicationsPanel = new JPanel();
			
			complicationsHashMap = deliveryManager.getComplicationList(null);
			if (editing) {
				if (GeneralData.PREGNANCYCARE){
					if (deliveries != null && deliveries.size() > 0) {
						complicationsHashMap = deliveryManager.getComplicationList(deliveries.get(0));
					}
				}
			} 
			
			compCheckBoxs = new JCheckBox[complicationsHashMap.keySet().size()];
			Object[] keys = complicationsHashMap.keySet().toArray();
			
			for (int i = 0; i < keys.length ; i++) {
				compCheckBoxs[i] = new JCheckBox();
				compCheckBoxs[i].setText((String) keys[i]);
				compCheckBoxs[i].setSelected(complicationsHashMap.get((String) keys[i]));
				compCheckBoxs[i].addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JCheckBox thisCheckBox = (JCheckBox) e.getSource();
						complicationsHashMap.put(thisCheckBox.getText(), thisCheckBox.isSelected());
					}
				});
				complicationsPanel.add(compCheckBoxs[i]);
			}
			//complicationsPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.complications")));
			BorderedPanel.setMyBorderAlign(complicationsPanel, MessageBundle.getMessage("angal.admission.complications"), TitledBorder.CENTER);
		}
		return complicationsPanel;
	}
	
	private JPanel getTypeOfRemovalOfPlacentaPanel(){
		if (typeRemovePlacentaPanel == null) {
			typeRemovePlacentaPanel = new JPanel();
			typeRemovePlacentaBox = new JComboBox();
			typeRemovePlacentaBox.addItem("");
			for (String elem : deliveryManager.getTypesOfRemovalOfPlacenta()) {
				typeRemovePlacentaBox.addItem(elem);
				if (editing) {
					if (GeneralData.PREGNANCYCARE){
						if (deliveries != null && deliveries.size() > 0 
								&& deliveries.get(0).getRemovePlacentaType() != null 
								&& deliveries.get(0).getRemovePlacentaType().equals(elem))
							typeRemovePlacentaBox.setSelectedItem(elem);
					}
				}
			}
			typeRemovePlacentaPanel.add(typeRemovePlacentaBox);
			//typeRemovePlacentaPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.typeofremovalofplacenta")));
			BorderedPanel.setMyBorderAlign(typeRemovePlacentaPanel, MessageBundle.getMessage("angal.admission.typeofremovalofplacenta"), TitledBorder.CENTER);
		}
		return typeRemovePlacentaPanel;
	}
	
	private JPanel getHIVTestStatusPanel(){
		if (hivTestStatusPanel == null) {
			hivTestStatusPanel = new JPanel();
			
			hivTestStatusPanel.add(new JLabel("Place"));
			{
				hivTestPlaceBox = new JComboBox();
				hivTestPlaceBox.addItem("");
				for (String elem : deliveryManager.getHivTestPlaces()) {
					hivTestPlaceBox.addItem(elem);
					if (editing) {
						if (GeneralData.PREGNANCYCARE){
							if (deliveries != null && deliveries.size() > 0 
									&& deliveries.get(0).getHivTestPlace() != null 
									&& deliveries.get(0).getHivTestPlace().equals(elem))
								hivTestPlaceBox.setSelectedItem(elem);
						}
					}
				}
				hivTestStatusPanel.add(hivTestPlaceBox);	
			}
			hivTestStatusPanel.add(new JLabel("Result"));
			{
				hivPositive = new JRadioButton("P");
				hivNegative = new JRadioButton("N");
				hivUnknown = new JRadioButton("U");
				ButtonGroup resultGroup = new ButtonGroup();
				resultGroup.add(hivPositive);
				resultGroup.add(hivNegative);
				resultGroup.add(hivUnknown);
				hivUnknown.setSelected(true);
				
				if (deliveries != null && deliveries.size() > 0){
					char hivTestResult = deliveries.get(0).getHivTestResult();
					if (hivTestResult == 'P')
						hivPositive.setSelected(true);
					else if (hivTestResult == 'N')
						hivNegative.setSelected(true);
					else 
						hivUnknown.setSelected(true);
				}
				hivTestStatusPanel.add(hivPositive);
				hivTestStatusPanel.add(hivNegative);
				hivTestStatusPanel.add(hivUnknown);
			}
			//hivTestStatusPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.hivstatus")));
			BorderedPanel.setMyBorderAlign(hivTestStatusPanel, MessageBundle.getMessage("angal.admission.hivstatus"), TitledBorder.CENTER);
		}
		return hivTestStatusPanel;
	}
	
	private JPanel getHIVTestStatusPartnerPanel(){
		if (hivTestStatusPartnerPanel == null) {
			hivTestStatusPartnerPanel = new JPanel();
			
			hivTestStatusPartnerPanel.add(new JLabel("Partner"));
			{
				hivPartnerPositive = new JRadioButton("P");
				hivPartnerNegative = new JRadioButton("N");
				hivPartnerUnknown = new JRadioButton("U");
				ButtonGroup resultGroup = new ButtonGroup();
				resultGroup.add(hivPartnerPositive);
				resultGroup.add(hivPartnerNegative);
				resultGroup.add(hivPartnerUnknown);
				hivPartnerUnknown.setSelected(true);
				
				if (deliveries != null && deliveries.size() > 0){
					char hivTestResult = deliveries.get(0).getHivTestResultPartner();
					if (hivTestResult == 'P')
						hivPartnerPositive.setSelected(true);
					else if (hivTestResult == 'N')
						hivPartnerNegative.setSelected(true);
					else 
						hivPartnerUnknown.setSelected(true);
				}
				hivTestStatusPartnerPanel.add(hivPartnerPositive);
				hivTestStatusPartnerPanel.add(hivPartnerNegative);
				hivTestStatusPartnerPanel.add(hivPartnerUnknown);
			}
			//hivTestStatusPartnerPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.hivstatuspartner")));
			BorderedPanel.setMyBorderAlign(hivTestStatusPartnerPanel, MessageBundle.getMessage("angal.admission.hivstatuspartner"), TitledBorder.CENTER);
		}
		return hivTestStatusPartnerPanel;
	}
	
	private JPanel getBloodPressurePanel(){
		if (bloodPressurePanel == null) {
			bloodPressurePanel = new JPanel();
			
			bloodPressureMinField = new VoLimitedTextField(3, 3);
			bloodPressureMinField.setHorizontalAlignment(JTextField.CENTER);
			bloodPressureMaxField = new VoLimitedTextField(3, 3);
			bloodPressureMaxField.setHorizontalAlignment(JTextField.CENTER);
			if (deliveries != null && deliveries.size() > 0){
				bloodPressureMinField.setText(new Integer(deliveries.get(0).getBloodPressureMin()).toString());
				bloodPressureMaxField.setText(new Integer(deliveries.get(0).getBloodPressureMax()).toString());
			}
			bloodPressurePanel.add(bloodPressureMinField);
			bloodPressurePanel.add(new JLabel("/"));
			bloodPressurePanel.add(bloodPressureMaxField);
			//bloodPressurePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.weightandheight")));
			BorderedPanel.setMyBorderAlign(bloodPressurePanel, MessageBundle.getMessage("angal.admission.bloodpressureabbr"), TitledBorder.CENTER);
		}
		return bloodPressurePanel;
	}
	
	private JPanel getANCPanel(){
		if (ancPanel == null) {
			ancPanel = new JPanel();
			
			ancBox = new JComboBox();
			for (String elem : deliveryManager.getAncOptions()) {
				ancBox.addItem(elem);
				if (editing) {
					if (GeneralData.PREGNANCYCARE){
						if (deliveries != null && deliveries.size() > 0 
								&& deliveries.get(0).getAncVisitDone() != null 
								&& deliveries.get(0).getAncVisitDone().equals(elem))
							ancBox.setSelectedItem(elem);
					}
				}
			}
			ancPanel.add(ancBox);
			//ancPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.ancdone")));
			BorderedPanel.setMyBorderAlign(ancPanel, MessageBundle.getMessage("angal.admission.ancdone"), TitledBorder.CENTER);
		}
		return ancPanel;
	}
	
	private JPanel getMotherWaitingHomePanel(){
		if (motherWaitingHomePanel == null) {
			motherWaitingHomePanel = new JPanel();
			
			{
				mwhYes = new JRadioButton("Y");
				mwhNo = new JRadioButton("N");
				ButtonGroup resultGroup = new ButtonGroup();
				resultGroup.add(mwhYes);
				resultGroup.add(mwhNo);
				
				if (deliveries != null && deliveries.size() > 0){
					char hivTestResult = deliveries.get(0).getMotherWaitingHomeDone();
					if (hivTestResult == 'Y')
						mwhYes.setSelected(true);
					else
						mwhNo.setSelected(true);
				}
				motherWaitingHomePanel.add(mwhYes);
				motherWaitingHomePanel.add(mwhNo);
			}
			
			//motherWaitingHomePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.motherwaitinghome")));
			BorderedPanel.setMyBorderAlign(motherWaitingHomePanel, MessageBundle.getMessage("angal.admission.motherwaitinghome"), TitledBorder.CENTER);
		}
		return motherWaitingHomePanel;
	}
	
	private void enableDisableFields(JPanel panel, boolean enabled){
		for(int a=0; a< panel.getComponentCount(); a++){
			if (panel.getComponent(a).getClass().equals(JPanel.class)
					|| JPanel.class.isAssignableFrom(panel.getComponent(a).getClass())) {
				enableDisableFields((JPanel)panel.getComponent(a), enabled);
			}
			panel.getComponent(a).setEnabled(enabled);
		}
		if (enabled) BorderedPanel.setMyBorderColor(panel, Color.DARK_GRAY);
		else BorderedPanel.setMyBorderColor(panel, Color.GRAY);
	}
	
	private JScrollPane getJPanelNote() {

		JScrollPane scrollPane = new JScrollPane(getJTextAreaNote());
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 50, 0, 50), // external
				new ShadowBorder(5, Color.LIGHT_GRAY))); // internal
		scrollPane.addAncestorListener(new AncestorListener() {

			public void ancestorRemoved(AncestorEvent event) {
			}

			public void ancestorMoved(AncestorEvent event) {
			}

			public void ancestorAdded(AncestorEvent event) {
				textArea.requestFocus();
			}
		});
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		scrollPane.setPreferredSize(new Dimension(screensize.width/2, screensize.height/2));
		return scrollPane;
	}

	private JTextArea getJTextAreaNote() {
		if (textArea == null) {
			textArea = new JTextArea();
			if (editing && admission.getNote() != null) {
				textArea.setText(admission.getNote());
			}
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setMargin(new Insets(10, 10, 10, 10));
		}
		return textArea;
	}

	private JPanel getTreatmentPanel() {
		if (treatmentPanel == null) {
			treatmentPanel = new JPanel();
			
			PregnantTreatmentTypeBrowserManager abm = new PregnantTreatmentTypeBrowserManager();
			treatmTypeBox = new JComboBox();
			treatmTypeBox.addItem("");
			treatmTypeList = abm.getPregnantTreatmentType();
			for (PregnantTreatmentType elem : treatmTypeList) {
				treatmTypeBox.addItem(elem);
				if (editing) {
					if (admission.getPregTreatmentType() != null && admission.getPregTreatmentType().equalsIgnoreCase(elem.getCode())) {
						treatmTypeBox.setSelectedItem(elem);
					}
				}
			}
			
			treatmentPanel.add(treatmTypeBox);
			treatmentPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.treatmenttype")));
		}
		return treatmentPanel;
	}

	private JPanel getWeightPanel() {
		if (weightPanel == null) {
			weightPanel = new JPanel();
			
			weightField = new VoLimitedTextField(6, 6);
			if (editing && admission.getWeight() != null) {
				weight = admission.getWeight().floatValue();
				weightField.setText(String.valueOf(weight));
			}
			
			weightPanel.add(weightField);
			weightPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.weight")));
		}
		return weightPanel;
	}

	private JPanel getVisitDatePanel() {
		if (visitDatePanel == null) {
			visitDatePanel = new JPanel();
			
			Date myDate = null;
			if (editing && admission.getVisitDate() != null) {
				visitDate = admission.getVisitDate();
				myDate = visitDate.getTime();
			} else {
				visitDate = new GregorianCalendar();
			}
			visitDateFieldCal = new JDateChooser(myDate, "dd/MM/yy"); // Calendar
			visitDateFieldCal.setLocale(new Locale(GeneralData.LANGUAGE));
			visitDateFieldCal.setDateFormatString("dd/MM/yy");
	
			visitDatePanel.add(visitDateFieldCal); // Calendar
			visitDatePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.visitdate")));
		}
		return visitDatePanel;
	}

	private JPanel getDeliveryResultTypePanel() {
		if (deliveryResultTypePanel == null) {
			deliveryResultTypePanel = new JPanel();
			
			deliveryResultTypeBox = new JComboBox();
			deliveryResultTypeBox.addItem("");
			deliveryResultTypeList = drtbm.getDeliveryResultType();
			for (DeliveryResultType elem : deliveryResultTypeList) {
				deliveryResultTypeBox.addItem(elem);
				if (editing) {
					if (admission.getDeliveryResultId() != null && admission.getDeliveryResultId().equalsIgnoreCase(elem.getCode())) {
						deliveryResultTypeBox.setSelectedItem(elem);
					}
				}
			}
			
			deliveryResultTypePanel.add(deliveryResultTypeBox);
			//deliveryResultTypePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.deliveryresultype")));
			BorderedPanel.setMyBorderAlign(deliveryResultTypePanel, MessageBundle.getMessage("angal.admission.deliveryresultype"), TitledBorder.CENTER);
		}
		return deliveryResultTypePanel;
	}

	private JPanel getAbortDatePanel() {
		if (abortDatePanel == null) {
			abortDatePanel = new JPanel();
//			Date myDate = null;
			if (editing && admission.getAbortDate() != null) {
				abortDate = admission.getAbortDate();
				abortDateFieldCal = new JDateChooser(abortDate.getTime());
//				myDate = abortDate.getTime();
			} else {
				abortDateFieldCal = new JDateChooser();
			}
//			abortDateFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			abortDateFieldCal.setLocale(new Locale(GeneralData.LANGUAGE));
			abortDateFieldCal.setDateFormatString("dd/MM/yy");
	
			abortDatePanel.add(abortDateFieldCal);
			abortDatePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.abortdate")));
		}
		return abortDatePanel;
	}

	private JPanel getControl1DatePanel() {
		if (control1DatePanel == null) {
			control1DatePanel = new JPanel();
			
			Date myDate = null;
			if (editing && admission.getCtrlDate1() != null) {
				ctrl1Date = admission.getCtrlDate1();
				myDate = ctrl1Date.getTime();
			} 
			ctrl1DateFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			ctrl1DateFieldCal.setLocale(new Locale(GeneralData.LANGUAGE));
			ctrl1DateFieldCal.setDateFormatString("dd/MM/yy");

			control1DatePanel.add(ctrl1DateFieldCal);
			control1DatePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.controln1date")));
		}
		return control1DatePanel;
	}
	
	private JPanel getControl2DatePanel() {
		if (control2DatePanel == null) {
			control2DatePanel = new JPanel();
			
			Date myDate = null;
			if (editing && admission.getCtrlDate2() != null) {
				ctrl2Date = admission.getCtrlDate2();
				myDate = ctrl2Date.getTime();
			} 
			ctrl2DateFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			ctrl2DateFieldCal.setLocale(new Locale(GeneralData.LANGUAGE));
			ctrl2DateFieldCal.setDateFormatString("dd/MM/yy");

			control2DatePanel.add(ctrl2DateFieldCal);
			control2DatePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.controln2date")));
		}
		return control2DatePanel;
	}

	private JPanel getProgYearPanel() {
		if (yearProgPanel == null) {
			yearProgPanel = new JPanel();
//			String yProg = "";
//			if (!GeneralData.WARDIPDNUMBER) yProg = "" + admMan.getNextYProg();
			
			if (saveYProg != null) {
				yProgTextField = new JTextField(saveYProg);
				
			}
//			//if the window is launched from pregnancy care
//			else if (!editing && admission.getWardId().equals("M")){
//				AdmissionBrowserManager abm = new AdmissionBrowserManager();
//				int nextProg = abm.getNextYProg("M");
//				yProgTextField = new JTextField("" + nextProg);
//
//				// get default selected warn default beds number
//				int nBeds = (((Ward) wardBox.getSelectedItem()).getBeds()).intValue();
//				int usedBeds = abm.getUsedWardBed("M");
//				int freeBeds = nBeds - usedBeds;
//				if (freeBeds <= 0)
//					JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.wardwithnobedsavailable"));
//
//			}

			else if (editing) {
				yProgTextField = new JTextField("" + admission.getYProg());
			} else {
				yProgTextField = new JTextField("" + admMan.getNextYProg());
			}
			yProgTextField.setColumns(11);
			
			yearProgPanel.add(yProgTextField);
			yearProgPanel.setBorder(BorderFactory.createTitledBorder("IP Number")); //MessageBundle.getMessage("angal.admission.progressiveinyear")));
			
		}
		return yearProgPanel;
	}

	private JPanel getFHUPanel() {
		if (fhuPanel == null) {
			fhuPanel = new JPanel();
			
			if (editing) {
				FHUTextField = new JTextField(admission.getFHU());
			} else {
				FHUTextField = new JTextField();
			}
			FHUTextField.setColumns(20);
			
			fhuPanel.add(FHUTextField);
			fhuPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.fromhealthunit")));
			
		}
		return fhuPanel;
	}

	private JPanel getWardPanel() {
		if (wardPanel == null) {
			wardPanel = new JPanel();
			
			WardBrowserManager wbm = new WardBrowserManager();
			wardBox = new JComboBox();
			wardBox.addItem("");
			wardList = wbm.getWards();
			for (Ward elem : wardList) {
				if (editing) {
					if (elem.getBeds() > 0) {
						wardBox.addItem(elem);
						if (admission.getWardId().equalsIgnoreCase(elem.getCode())) {
							wardBox.setSelectedItem(elem);
						}
					}
					wardBox.setEnabled(false);
				} else {
					/*
					 * if patient is a male you don't see pregnancy case
					 * EMERGENCY ward excluded from the list
					 */
					if (elem.isFemale() && !enablePregnancy || elem.getCode().equals(EMERGENCY_WARD_CODE)) {
						continue;
					} else {
						if (elem.getBeds() > 0)
							wardBox.addItem(elem);
					}
					if (saveWard != null) {
						if (saveWard.getCode().equalsIgnoreCase(elem.getCode())) {
							wardBox.setSelectedItem(elem);
						}
					}
				}

			}
			wardBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (GeneralData.WARDIPDNUMBER) {
						if (wardBox.getSelectedIndex() == 0) {
							yProgTextField.setText("");
							return;
						} else {
							String wardId = ((Ward) wardBox.getSelectedItem()).getCode();
							if (wardId.equalsIgnoreCase(admission.getWardId())) {
								yProgTextField.setText("" + admission.getYProg());
							} else {
								AdmissionBrowserManager abm = new AdmissionBrowserManager();
								int nextProg = abm.getNextYProg(wardId);
								yProgTextField.setText("" + nextProg);
							}
						}
					}
					
					if (wardBox.getSelectedIndex() != 0) {
						Ward ward = (Ward) wardBox.getSelectedItem();
						String wardId = ward.getCode();
						int nBeds = ward.getBeds().intValue();
						int usedBeds = admMan.getUsedWardBed(wardId);
						int freeBeds = nBeds - usedBeds;
						if (freeBeds <= 0)
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.wardwithnobedsavailable"));
					}

					// switch panel
					if (((Ward) wardBox.getSelectedItem()).getCode().equalsIgnoreCase("M")) {
						if (!viewingPregnancy) {
							saveWard = (Ward) wardBox.getSelectedItem();
							saveYProg = yProgTextField.getText();
							viewingPregnancy = true;
							jTabbedPaneAdmission.setEnabledAt(pregnancyTabIndex, true);
							validate();
							repaint();
						}
					} else {
						if (viewingPregnancy) {
							saveWard = (Ward) wardBox.getSelectedItem();
							saveYProg = yProgTextField.getText();
							viewingPregnancy = false;
							jTabbedPaneAdmission.setEnabledAt(pregnancyTabIndex, false);
							validate();
							repaint();
						}
					}

				}
			});
			
			wardPanel.add(wardBox);
			if (editing) wardPanel.add(getJButtonTransfer());
			wardPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.ward")));
		}
		return wardPanel;
	}

	private JButton getJButtonTransfer() {
		if (jButtonTransfer == null) {
			jButtonTransfer = new JButton("Transfer");
			jButtonTransfer.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					ArrayList<Ward> transferWardList = new ArrayList<Ward>();
					for (Ward ward : wardList) {
						if (ward.getCode().equals(admission.getWardId())) continue;
						if (ward.isFemale() && !enablePregnancy || ward.getCode().equals(EMERGENCY_WARD_CODE)) {
							continue;
						} else {
							if (ward.getBeds() > 0)
								transferWardList.add(ward);
						}
					}
					transferWard = (Ward) JOptionPane.showInputDialog(AdmissionBrowser.this,
							"Transfer to ward", //$NON-NLS-1$
		                    "Transfer", //$NON-NLS-1$
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    transferWardList.toArray(),
		                    "");
					if (transferWard == null) return;
					
					/*
					 * Just change the ward and keep the IP number
					 */
					int YProg = admission.getYProg();
					wardBox.setSelectedItem(transferWard);
					admission.setWardId(transferWard.getCode());
					yProgTextField.setText(String.valueOf(YProg));
				}
			});
		}
		return jButtonTransfer;
	}

	private JPanel getDiseaseInPanel() {
		if (diseaseInPanel == null) {
			diseaseInPanel = new JPanel();
			diseaseInPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			
			diseaseInPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.diagnosisinstar")));
			diseaseInPanel.add(Box.createHorizontalStrut(50));
			
			diseaseInBox = new JComboBox();
			diseaseInBox.setPreferredSize(new Dimension(preferredWidthDiagnosis, preferredHeightLine));
			
		}	
		
		Disease found = null;
		String diseaseIn = admission.getDiseaseInId();
		diseaseInBox.removeAllItems();
		diseaseInBox.addItem("");
		for (Disease elem : diseaseInList) {
			boolean ok = true;
			if (!elem.isMale() && patient.getSex() == 'M') ok = false; //skip female diseases for male patients
			if (!elem.isFemale() && patient.getSex() == 'F') ok = false; //skip male diseases for female patients
			if (elem.getMinimumMonths() != 0 && patient.getMonthsAtDate((GregorianCalendar) dateIn.clone()) < elem.getMinimumMonths()) ok = false; //skip diseases for young patients
			if (elem.getMaximumMonths() != 0 && patient.getMonthsAtDate((GregorianCalendar) dateIn.clone()) > elem.getMaximumMonths()) ok = false; //skip diseases for old patients
			if (ok) diseaseInBox.addItem(elem);
			
			//search for saved DiseaseIn
			if (editing && found == null && diseaseIn != null && diseaseIn.equalsIgnoreCase(elem.getCode())) {
				diseaseInBox.setSelectedItem(elem);
				found = elem;
			}
		}
		
		
		if (editing && found == null && diseaseIn != null) {
			
			//Not found: search among all diseases
			ArrayList<Disease> diseaseAllList = dbm.getDiseaseAll();
			for (Disease elem : diseaseAllList) {
				if (diseaseIn.equalsIgnoreCase(elem.getCode())) {
					diseaseInBox.addItem(elem);
					diseaseInBox.setSelectedItem(elem);
					found = elem;
				}
			}
			
			if (found == null) {
				//Still not found
				diseaseInBox.addItem(MessageBundle.getMessage("angal.admission.no") + admission.getDiseaseOutId1() + " " + MessageBundle.getMessage("angal.admission.notfoundasinpatientdisease"));
				diseaseInBox.setSelectedIndex(diseaseInBox.getItemCount() - 1);
			}
		}
		
		diseaseInPanel.add(diseaseInBox);
		
		return diseaseInPanel;
	}

	/**
	 * @return
	 */
	private JPanel getMalnutritionPanel() {
		if (malnuPanel == null) {
			malnuPanel = new JPanel();
			
			malnuCheck = new JCheckBox();
			if (editing && admission.getType().equalsIgnoreCase("M")) {
				malnuCheck.setSelected(true);
			} else {
				malnuCheck.setSelected(false);
			}
			
			malnuCheck.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox thisCheckBox = (JCheckBox) e.getSource();
					if (!thisCheckBox.isSelected()) {
						MalnutritionManager malnutritionManager = new MalnutritionManager();
						List<MalnutritionVisit> visits = malnutritionManager.getMalnutritionVisits(admission.getId());
						if (visits.isEmpty()) thisCheckBox.setSelected(false);
						else {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.malnutrition.malnutritiondatastillpresentpleasedeletefirst"));
							thisCheckBox.setSelected(true);
						}
					} 
					else thisCheckBox.setSelected(true);
				}
			});
			
			malnuPanel.add(malnuCheck);
			malnuPanel.add(new JLabel(MessageBundle.getMessage("angal.admission.malnutrition")), BorderLayout.CENTER);
			
		}
		return malnuPanel;
	}

	private JPanel getAdmissionTypePanel() {
		if (admissionTypePanel == null) {
			admissionTypePanel = new JPanel();
			
			admTypeBox = new JComboBox();
			admTypeBox.setPreferredSize(new Dimension(preferredWidthTypes, preferredHeightLine));
			admTypeBox.addItem("");
			admTypeList = admMan.getAdmissionType();
			for (AdmissionType elem : admTypeList) {
				admTypeBox.addItem(elem);
				if (editing) {
					if (admission.getAdmType().equalsIgnoreCase(elem.getCode())) {
						admTypeBox.setSelectedItem(elem);
					}
				}
			}
			
			admissionTypePanel.add(admTypeBox);
			admissionTypePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.admissiontype")));
				
		}
		return admissionTypePanel;
	}

	private JPanel getAdmissionDatePanel() {
		if (admissionDatePanel == null) {
			admissionDatePanel = new JPanel();
			
//			dateInFieldCal = new JDateChooser(dateIn.getTime(), "dd/MM/yy"); // Calendar
			dateInFieldCal = new JDateChooser(dateIn.getTime());
			dateInFieldCal.setLocale(new Locale(GeneralData.LANGUAGE));
			dateInFieldCal.setDateFormatString("dd/MM/yy HH:mm:ss");
			dateInFieldCal.addPropertyChangeListener("date", new PropertyChangeListener() {
				
				public void propertyChange(PropertyChangeEvent evt) {
					Date newValue = (Date) evt.getNewValue();
					if (newValue.before(patient.getBirthDate())) {
						JOptionPane.showMessageDialog(AdmissionBrowser.this, "The patient was not yet born at the selected date!!!");
						dateInFieldCal.setDate((Date) evt.getOldValue());
						return;
					}
					dateInFieldCal.setDate(newValue);
					dateIn.setTime(newValue);
					updateBedDays();
					getDiseaseInPanel();
					getDiseaseOut1Panel();
					getDiseaseOut2Panel();
					getDiseaseOut3Panel();
				}
			});
			
			admissionDatePanel.add(dateInFieldCal);
			admissionDatePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.admissiondate")));
		}
		return admissionDatePanel;
	}

	private JPanel getDiseaseOutPanel() {
		if (diseaseOutPanel == null) {
			diseaseOutPanel = new JPanel();
			diseaseOutPanel.setLayout(new BoxLayout(diseaseOutPanel, BoxLayout.Y_AXIS));
			diseaseOutPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.diagnosisout")));
			diseaseOutPanel.add(getDiseaseOut1Panel());
			diseaseOutPanel.add(getDiseaseOut2Panel());
			diseaseOutPanel.add(getDiseaseOut3Panel());
		}
		return diseaseOutPanel;
	}

	/**
	 * @return
	 */
	private JPanel getDiseaseOut1Panel() {
		if (diseaseOut1Panel == null) {
			diseaseOut1Panel = new JPanel();
			diseaseOut1Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			
			JLabel label = new JLabel(MessageBundle.getMessage("angal.admission.number1"), SwingConstants.RIGHT);
			label.setPreferredSize(new Dimension(50, 50));
			label.setHorizontalTextPosition(SwingConstants.RIGHT);
			
			diseaseOut1Panel.add(label);
			
			diseaseOut1Box = new JComboBox();
			diseaseOut1Box.setPreferredSize(new Dimension(preferredWidthDiagnosis, preferredHeightLine));
		}	
		
		Disease found = null;
		diseaseOut1Box.removeAllItems();
		diseaseOut1Box.addItem("");
		String diseaseOut1 = admission.getDiseaseOutId1();
		for (Disease elem : diseaseOutList) {
			boolean ok = true;
			if (!elem.isMale() && patient.getSex() == 'M') ok = false; //skip female diseases for male patients
			if (!elem.isFemale() && patient.getSex() == 'F') ok = false; //skip male diseases for female patients
			if (elem.getMinimumMonths() != 0 && patient.getMonthsAtDate((GregorianCalendar) dateIn.clone()) < elem.getMinimumMonths()) ok = false; //skip diseases for young patients
			if (elem.getMaximumMonths() != 0 && patient.getMonthsAtDate((GregorianCalendar) dateIn.clone()) > elem.getMaximumMonths()) ok = false; //skip diseases for old patients
			if (ok) diseaseOut1Box.addItem(elem);
			
			//search for saved diseaseOut1
			if (editing && found == null && diseaseOut1 != null && diseaseOut1.equalsIgnoreCase(elem.getCode())) {
				diseaseOut1Box.setSelectedItem(elem);
				found = elem;
			}
		}
		
		
		if (editing && found == null && diseaseOut1 != null) {
			
			//Not found: select among all diseases
			ArrayList<Disease> diseaseAllList = dbm.getDiseaseAll();
			for (Disease elem : diseaseAllList) {
				if (diseaseOut1.equalsIgnoreCase(elem.getCode())) {
					diseaseOut1Box.addItem(elem);
					diseaseOut1Box.setSelectedItem(elem);
					found = elem;
				}
			}
			
			if (found == null) {
				//Still not found
				diseaseOut1Box.addItem(MessageBundle.getMessage("angal.admission.no") + admission.getDiseaseOutId1() + " " + MessageBundle.getMessage("angal.admission.notfoundasinpatientdisease"));
				diseaseOut1Box.setSelectedIndex(diseaseOut1Box.getItemCount() - 1);
			}
		}
		
		diseaseOut1Panel.add(diseaseOut1Box);
		
		return diseaseOut1Panel;
	}

	private JPanel getDiseaseOut2Panel() {
		
		if (diseaseOut2Panel == null) {
			diseaseOut2Panel = new JPanel();
			diseaseOut2Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			
			JLabel label = new JLabel(MessageBundle.getMessage("angal.admission.number2"), SwingConstants.RIGHT);
			label.setPreferredSize(new Dimension(50, 50));
			label.setHorizontalTextPosition(SwingConstants.RIGHT);
			
			diseaseOut2Panel.add(label);
			
			diseaseOut2Box = new JComboBox();
			diseaseOut2Box.setPreferredSize(new Dimension(preferredWidthDiagnosis, preferredHeightLine));
		}	
		
		Disease found = null;
		String diseaseOut2 = admission.getDiseaseOutId2();
		diseaseOut2Box.removeAllItems();
		diseaseOut2Box.addItem("");
		for (Disease elem : diseaseOutList) {
			boolean ok = true;
			if (!elem.isMale() && patient.getSex() == 'M') ok = false; //skip female diseases for male patients
			if (!elem.isFemale() && patient.getSex() == 'F') ok = false; //skip male diseases for female patients
			if (elem.getMinimumMonths() != 0 && patient.getMonthsAtDate((GregorianCalendar) dateIn.clone()) < elem.getMinimumMonths()) ok = false; //skip diseases for young patients
			if (elem.getMaximumMonths() != 0 && patient.getMonthsAtDate((GregorianCalendar) dateIn.clone()) > elem.getMaximumMonths()) ok = false; //skip diseases for old patients
			if (ok) diseaseOut2Box.addItem(elem);
			
			//Search for saved disaseOut2
			if (editing && found == null && diseaseOut2 != null&& diseaseOut2.equalsIgnoreCase(elem.getCode())) {
				diseaseOut2Box.setSelectedItem(elem);
				found = elem;
			}
		}
		
		if (editing && found == null && diseaseOut2 != null) {
			
			//Not found: select among all diseases
			ArrayList<Disease> diseaseAllList = dbm.getDiseaseAll();
			for (Disease elem : diseaseAllList) {
				if (diseaseOut2.equalsIgnoreCase(elem.getCode())) {
					diseaseOut2Box.addItem(elem);
					diseaseOut2Box.setSelectedItem(elem);
					found = elem;
				}
			}
			
			if (found == null) {
				//Still not found
				diseaseOut2Box.addItem(MessageBundle.getMessage("angal.admission.no") + admission.getDiseaseOutId1() + " " + MessageBundle.getMessage("angal.admission.notfoundasinpatientdisease"));
				diseaseOut2Box.setSelectedIndex(diseaseOut2Box.getItemCount() - 1);
			}
		}
		
		diseaseOut2Panel.add(diseaseOut2Box);
		
		return diseaseOut2Panel;
	}

	private JPanel getDiseaseOut3Panel() {
		
		if (diseaseOut3Panel == null) {
			diseaseOut3Panel = new JPanel();
			diseaseOut3Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			
			JLabel label = new JLabel(MessageBundle.getMessage("angal.admission.number3"), SwingConstants.RIGHT);
			label.setPreferredSize(new Dimension(50, 50));
			
			diseaseOut3Panel.add(label);
			
			diseaseOut3Box = new JComboBox();
			diseaseOut3Box.setPreferredSize(new Dimension(preferredWidthDiagnosis, preferredHeightLine));
		}	
		
		Disease found = null;
		String diseaseOut3 = admission.getDiseaseOutId3();
		diseaseOut3Box.removeAllItems();
		diseaseOut3Box.addItem("");
		for (Disease elem : diseaseOutList) {
			boolean ok = true;
			if (!elem.isMale() && patient.getSex() == 'M') ok = false; //skip female diseases for male patients
			if (!elem.isFemale() && patient.getSex() == 'F') ok = false; //skip male diseases for female patients
			if (elem.getMinimumMonths() != 0 && patient.getMonthsAtDate((GregorianCalendar) dateIn.clone()) < elem.getMinimumMonths()) ok = false; //skip diseases for young patients
			if (elem.getMaximumMonths() != 0 && patient.getMonthsAtDate((GregorianCalendar) dateIn.clone()) > elem.getMaximumMonths()) ok = false; //skip diseases for old patients
			if (ok) diseaseOut3Box.addItem(elem);
			
			if (editing && found == null && diseaseOut3 != null && diseaseOut3.equalsIgnoreCase(elem.getCode())) {
				diseaseOut3Box.setSelectedItem(elem);
				found = elem;
			}
		}
		
		if (editing && found == null && diseaseOut3 != null) {
			
			//Not found: select among all diseases
			ArrayList<Disease> diseaseAllList = dbm.getDiseaseAll();
			for (Disease elem : diseaseAllList) {
				if (diseaseOut3.equalsIgnoreCase(elem.getCode())) {
					diseaseOut3Box.addItem(elem);
					diseaseOut3Box.setSelectedItem(elem);
					found = elem;
				}
			}
			
			if (found == null) {
				//Still not found
				diseaseOut3Box.addItem(MessageBundle.getMessage("angal.admission.no") + admission.getDiseaseOutId1() + " " + MessageBundle.getMessage("angal.admission.notfoundasinpatientdisease"));
				diseaseOut3Box.setSelectedIndex(diseaseOut3Box.getItemCount() - 1);
			}
		}

		diseaseOut3Panel.add(diseaseOut3Box);

		return diseaseOut3Panel;
	}

	/*
	 * simply an utility
	 */
	private JRadioButton getRadioButton(String label, char mn, boolean active) {
		JRadioButton rb = new JRadioButton(label);
		rb.setMnemonic(KeyEvent.VK_A + (mn - 'A'));
		rb.setSelected(active);
		rb.setName(label);
		return rb;
	}

	/*
	 * admission sheet: 5th row: insert select operation type and result
	 */
	private JPanel getOperationPanel() {
		if (operationPanel == null) {
			operationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
			OperationBrowserManager obm = new OperationBrowserManager();
			operationBox = new JComboBox();
			operationBox.addItem("");
			operationList = obm.getOperation();
			for (Operation elem : operationList) {
				operationBox.addItem(elem);
				if (editing) {
					if (admission.getOperationId() != null && admission.getOperationId().equalsIgnoreCase(elem.getCode())) {
						operationBox.setSelectedItem(elem);
					}
				}
			}
			operationBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (operationBox.getSelectedIndex() == 0) {
						// operationDateField.setText("");
						operationDateFieldCal.setDate(null);
					} else {
						/*
						 * if (!operationDateField.getText().equals("")){ // leave
						 * old date value }
						 */
						if (operationDateFieldCal.getDate() != null) {
							// leave old date value
						}
	
						else {
							// set today date
							operationDateFieldCal.setDate((new GregorianCalendar()).getTime());
						}
					}
				}
			});

			operationPanel.add(operationBox);
			operationPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.operationtype")));
		}
		return operationPanel;
	}

	/**
	 * @return
	 */
	private JPanel getOperationResultPanel() {
		if (resultPanel == null) {
			resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			
			operationResultRadioP = getRadioButton(MessageBundle.getMessage("angal.admission.positive"), 'P', false);
			operationResultRadioN = getRadioButton(MessageBundle.getMessage("angal.admission.negative"), 'N', false);
			operationResultRadioU = getRadioButton(MessageBundle.getMessage("angal.admission.unknown"), 'U', true);
			
			ButtonGroup resultGroup = new ButtonGroup();
			resultGroup.add(operationResultRadioP);
			resultGroup.add(operationResultRadioN);
			resultGroup.add(operationResultRadioU);
			
			if (editing) {
				if (admission.getOpResult() != null) {
					if (admission.getOpResult().equalsIgnoreCase("P"))
						operationResultRadioP.setSelected(true);
					else 
						operationResultRadioN.setSelected(true);
				}
			} 
	
			resultPanel.add(operationResultRadioP);
			resultPanel.add(operationResultRadioN);
			resultPanel.add(operationResultRadioU);
	
			resultPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.operationresult")));
		}
		return resultPanel;
	}

	/*
	 * admission sheet: 6th row: insert operation date and transusional unit
	 */
	private JPanel getOperationDatePanel() {
		if (operationDatePanel == null) {
			operationDatePanel = new JPanel();
			
			Date myDate = null;
			if (editing && admission.getOpDate() != null) {
				operationDate = admission.getOpDate();
				myDate = operationDate.getTime();
			}
			operationDateFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			operationDateFieldCal.setLocale(new Locale(GeneralData.LANGUAGE));
			operationDateFieldCal.setDateFormatString("dd/MM/yy");
			
			operationDatePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.operationdate")));
			operationDatePanel.add(operationDateFieldCal);
		}
		return operationDatePanel;
	}
	
	private JPanel getTransfusionPanel() {
		if (transfusionPanel == null) {
			transfusionPanel = new JPanel();
			
			float start = 0;
			float min = 0;
			float step = (float) 1.;
			
			SpinnerModel model = new SpinnerNumberModel(start, min, null, step);
			trsfUnitField = new JSpinner(model);
			trsfUnitField.setPreferredSize(new Dimension(preferredWidthTransfusionSpinner, preferredHeightLine));
			
			if (editing && admission.getTransUnit() != null) {
				trsfUnit = admission.getTransUnit().floatValue();
				trsfUnitField.setValue(trsfUnit);
			}
			
			transfusionPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.transfusionalunit")));
			transfusionPanel.add(trsfUnitField);
		}
		return transfusionPanel;
	}

	private JPanel getDischargeTypePanel() {
		if (dischargeTypePanel == null) {
			dischargeTypePanel = new JPanel();
			
			disTypeBox = new JComboBox();
			disTypeBox.setPreferredSize(new Dimension(preferredWidthTypes, preferredHeightLine));
			disTypeBox.addItem("");
			disTypeList = admMan.getDischargeType();
			for (DischargeType elem : disTypeList) {
				disTypeBox.addItem(elem);
				if (editing) {
					if (admission.getDisType() != null && admission.getDisType().equalsIgnoreCase(elem.getCode())) {
						disTypeBox.setSelectedItem(elem);
					}
				}
			}
			
			dischargeTypePanel.add(disTypeBox);
			dischargeTypePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.dischargetype")));
		}
		return dischargeTypePanel;
	}
	
	private JPanel getBedDaysPanel() {
		if (bedDaysPanel == null) {
			bedDaysPanel = new JPanel();
			
			bedDaysTextField  = new VoLimitedTextField(10, 10);
			bedDaysTextField.setEditable(false);
			
			bedDaysPanel.add(bedDaysTextField);
			bedDaysPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.beddays")));
		}
		return bedDaysPanel;
	}
	
	private void updateBedDays() {
		try {
			Date admission = dateInFieldCal.getDate();
			Date discharge = dateOutFieldCal.getDate();
			int bedDays = TimeTools.getDaysBetweenDates(admission, discharge, false);
			if (bedDays == 0) bedDays++;
			bedDaysTextField.setText(String.valueOf(bedDays));
		} catch (Exception e) {
			bedDaysTextField.setText("");
		}
	}

	private JPanel getDischargeDatePanel() {
		if (dischargeDatePanel == null) {
			dischargeDatePanel = new JPanel();
			
			Date myDate = null;
			if (editing && admission.getDisDate() != null) {
				dateOut = admission.getDisDate();
				myDate = dateOut.getTime();
			}
			dateOutFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			dateOutFieldCal.setLocale(new Locale(GeneralData.LANGUAGE));
			dateOutFieldCal.setDateFormatString("dd/MM/yy HH:mm:ss");
			dateOutFieldCal.addPropertyChangeListener("date", new PropertyChangeListener() {
				
				public void propertyChange(PropertyChangeEvent evt) {
					updateBedDays();
				}
			});
			
			
			dischargeDatePanel.add(dateOutFieldCal);
			dischargeDatePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.dischargedate")));
			
		}
		return dischargeDatePanel;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getSaveButton());
			buttonPanel.add(getCloseButton());
			
			if (GeneralData.XMPPMODULEENABLED){
			Interaction share= new Interaction();
			Collection<String> contacts = share.getContactOnline();
			contacts.add("-- Share alert with: nobody --");
			shareWith = new JComboBox(contacts.toArray());
			shareWith.setSelectedItem("-- Share alert with: nobody --");
			buttonPanel.add(shareWith);
			}
		}
		return buttonPanel;
	}

	/**
	 * @return
	 */
	private JLabel getJLabelRequiredFields() {
		if (labelRequiredFields == null) {
			labelRequiredFields = new JLabel(MessageBundle.getMessage("angal.admission.indicatesrequiredfields"));
		}
		return labelRequiredFields;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText(MessageBundle.getMessage("angal.admission.close"));
			closeButton.setMnemonic(KeyEvent.VK_C);
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return closeButton;
	}
	
	private JButton getSaveButton() {

		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setText(MessageBundle.getMessage("angal.admission.save"));
			saveButton.setMnemonic(KeyEvent.VK_S);
			saveButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					
					try {
						/*
						 * During save, add a wait cursor to the window and disable all widgets it contains.
						 * They are enabled back in the <code>finally</code> block
						 * instead of enabling them before every single <code>return</code>.
						 */
						BusyState.setBusyState(AdmissionBrowser.this, true);

						/*
						 * Initialize AdmissionBrowserManager
						 */
						AdmissionBrowserManager abm = new AdmissionBrowserManager();
						ArrayList<Admission> admList = abm.getAdmissions(patient);

						/*
						 * Today GregorianCalendar
						 */
						GregorianCalendar today = new GregorianCalendar();
						
						/*
						 * Date Format for displaying
						 */
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						
						/*
						 * is it an admission update or a discharge? if we have a
						 * valid discharge date isDischarge will be true
						 */
						boolean isDischarge = false;

						/*
						 * set if ward pregnancy is selected
						 */
						boolean isPregnancy = false;
						
						char sex = patient.getSex();
						int months = patient.getMonthsAtDate((GregorianCalendar) dateIn.clone());
	
						// get ward id (not null)
						if (wardBox.getSelectedIndex() == 0) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectavalidward"));
							return;
						} else {
							Ward ward = (Ward) wardBox.getSelectedItem();
							if (ward.getCode().equals(EMERGENCY_WARD_CODE)) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "The patient cannot stay in EMERGENCY, please transfer to a different ward");
								return;
							}
							if (ward.getMinimumMonths() != 0 && months < ward.getMinimumMonths()) { 
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "The patient is too young for the selected ward (at the date of admission)");
								return;
							}
							if (ward.getMaximumMonths() != 0 && months > ward.getMaximumMonths()) { 
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "The patient is too old for the selected ward (at the date of admission)");
								return;
							}
							admission.setWardId(ward.getCode());
						}
						
						if (admission.getWardId().equalsIgnoreCase("M")) {
							isPregnancy = true;
						}

						// get disease in id ( it can be null)
						if (diseaseInBox.getSelectedIndex() == 0) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectavaliddiseasein"));
							return;
						} else {
							try {
								Disease diseaseIn = (Disease) diseaseInBox.getSelectedItem();
								if (!diseaseIn.isMale() && sex == 'M') {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, "Selected disease IN is not related to female patients");
									return;
								} 
								if (!diseaseIn.isFemale() && sex == 'F') {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, "Selected disease IN is not related to male patients");
									return;
								} 
								if (diseaseIn.getMinimumMonths() != 0 && months < diseaseIn.getMinimumMonths()) {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, "the patient is too young for selected disease IN");
									return;
								}
								if (diseaseIn.getMaximumMonths() != 0 && months > diseaseIn.getMaximumMonths()) {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, "the patient is too old for selected disease IN");
									return;
								}
								admission.setDiseaseInId(diseaseIn.getCode());
							} catch (IndexOutOfBoundsException e1) {
								/*
								 * Workaround in case a fake-disease is selected (ie
								 * when previous disease has been deleted)
								 */
								admission.setDiseaseInId(null);
							}
						}
	
						// get disease out id ( it can be null)
						int disease1index = diseaseOut1Box.getSelectedIndex();
						if (disease1index == 0) {
							admission.setDiseaseOutId1(null);
						} else {
							Disease diseaseOut1 = (Disease) diseaseOut1Box.getSelectedItem();
							if (!diseaseOut1.isMale() && sex == 'M') {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "Selected disease IN is not related to female patients");
								return;
							} 
							if (!diseaseOut1.isFemale() && sex == 'F') {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "Selected disease OUT 1 is not related to male patients");
								return;
							} 
							if (diseaseOut1.getMinimumMonths() != 0 && months < diseaseOut1.getMinimumMonths()) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "the patient is too young for selected disease OUT 1");
								return;
							} 
							if (diseaseOut1.getMaximumMonths() != 0 && months > diseaseOut1.getMaximumMonths()) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "the patient is too old for selected disease OUT 1");
								return;
							} 
							admission.setDiseaseOutId1(diseaseOut1.getCode());
						}
	
						// get disease out id 2 ( it can be null)
						int disease2index = diseaseOut2Box.getSelectedIndex();
						if (disease2index == 0) {
							admission.setDiseaseOutId2(null);
						} else {
							Disease diseaseOut2 = (Disease) diseaseOut2Box.getSelectedItem();
							if (!diseaseOut2.isMale() && sex == 'M') {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "Selected disease IN is not related to female patients");
								return;
							} 
							if (!diseaseOut2.isFemale() && sex == 'F') {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "Selected disease OUT 1 is not related to male patients");
								return;
							} 
							if (diseaseOut2.getMinimumMonths() != 0 && months < diseaseOut2.getMinimumMonths()) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "the patient is too young for selected disease OUT 2");
								return;
							}
							if (diseaseOut2.getMaximumMonths() != 0 && months > diseaseOut2.getMaximumMonths()) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "the patient is too old for selected disease OUT 2");
								return;
							}
							admission.setDiseaseOutId2(diseaseOut2.getCode());
						}
	
						// get disease out id 3 ( it can be null)
						int disease3index = diseaseOut3Box.getSelectedIndex();
						if (disease3index == 0) {
							admission.setDiseaseOutId3(null);
						} else {
							Disease diseaseOut3 = (Disease) diseaseOut3Box.getSelectedItem();
							if (!diseaseOut3.isMale() && sex == 'M') {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "Selected disease IN is not related to female patients");
								return;
							} 
							if (!diseaseOut3.isFemale() && sex == 'F') {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "Selected disease OUT 1 is not related to male patients");
								return;
							} 
							if (diseaseOut3.getMinimumMonths() != 0 && months < diseaseOut3.getMinimumMonths()) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "the patient is too young for selected disease OUT 3");
								return;
							}
							if (diseaseOut3.getMaximumMonths() != 0 && months > diseaseOut3.getMaximumMonths()) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "the patient is too old for selected disease OUT 3");
								return;
							}
							admission.setDiseaseOutId3(diseaseOut3.getCode());
						}
	
						// get year prog ( not null)
						try {
							int x = Integer.parseInt(yProgTextField.getText());
							if (x < 0) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, "Please insert a valid IP Number");//MessageBundle.getMessage("angal.admission.pleaseinsertacorrectprogressiveid"));
								return;
							} else {
								admission.setYProg(x);
							}
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, "Please insert a valid IP Number"); //MessageBundle.getMessage("angal.admission.pleaseinsertacorrectprogressiveid"));
							return;
						}
	
						// get FHU (it can be null)
						String s = FHUTextField.getText();
						if (s.equals("")) {
							admission.setFHU(null);
						} else {
							admission.setFHU(FHUTextField.getText());
						}
	
						// check and get date in (not null)
						String d = currentDateFormat.format(dateInFieldCal.getDate());
	
						try {
							dateIn.setTime(dateInFieldCal.getDate());
							
							if (dateIn.after(today)) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.futuredatenotallowed"));
								dateInFieldCal.setDate(today.getTime());
								return;
							}
							if (dateIn.before(today)) {
								// check for invalid date
								for (Admission ad : admList) {
									if (editing && ad.getId() == admission.getId()) {
										continue;
									}
									if ((ad.getAdmDate().before(dateIn) || ad.getAdmDate().compareTo(dateIn) == 0) 
											&& (ad.getDisDate() != null && ad.getDisDate().after(dateIn))) {
										JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.ininserteddatepatientwasalreadyadmitted"));
										dateInFieldCal.setDate(today.getTime());
										return;
									}
								}
							}
							// updateDisplay
							dateInFieldCal.setDate(dateIn.getTime());
							admission.setAdmDate(dateIn);
							//RememberDates.setLastAdmInDate(dateIn);
	
						} catch (IllegalArgumentException iae) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidadmissiondate"));
							return;
						}

						// get admission type (not null)
						if (admTypeBox.getSelectedIndex() == 0) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectavalidadmissiondate"));
							return;
						} else {
							admission.setAdmType(admTypeList.get(admTypeBox.getSelectedIndex() - 1).getCode());
						}

						// check and get date out (it can be null)
						// if set date out, isDischarge is set
						
						Date date = dateOutFieldCal.getDate();
						if (date != null) {
							dateOut = new GregorianCalendar();
							dateOut.setTime(date);
						} else dateOut = null;
						if (dateOut == null) {
							// only if we are editing the last admission
							// or if it is a new admission
							// no if we are editing an old admission
							Admission last = null;
							if (admList.size() > 0) {
								last = admList.get(admList.size() - 1);
							} else {
								last = admission;
							}
							if (!editing || (editing && admission.getId() == last.getId())) {
								// ok
							} else {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertadischargedate") + MessageBundle.getMessage("angal.admission.youareeditinganoldadmission"));
								return;

							}
							admission.setDisDate(null);
						} else {
							try {
								// date control
								if (dateOut.before(dateIn)) {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.dischargedatemustbeafteradmissiondate"));
									return;
								}
								if (dateOut.after(today)) {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.futuredatenotallowed"));
									return;
								} else {
									// check for invalid date
									boolean invalidDate = false;
									Date invalidStart = new Date();
									Date invalidEnd = new Date();
									for (Admission ad : admList) {
										// case current admission : let it be
										if (editing && ad.getId() == admission.getId()) {
											continue;
										}
										// found an open admission
										// only if i close my own first of it
										if (ad.getDisDate() == null) {
											if (!dateOut.after(ad.getAdmDate()))
												;// ok
											else {
												JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.intheselecteddatepatientwasadmittedagain"));
												return;
											}
										}
										// general case
										else {
											// DateIn >= adOut
											if (dateIn.after(ad.getDisDate()) || dateIn.equals(ad.getDisDate())) {
												// ok
											}
											// dateOut <= adIn
											else if (dateOut.before(ad.getAdmDate()) || dateOut.equals(ad.getAdmDate())) {
												// ok
											} else {
												invalidDate = true;
												invalidStart = ad.getAdmDate().getTime();
												invalidEnd = ad.getDisDate().getTime();
												break;
											}
										}
									}
									if (invalidDate) {
										JOptionPane.showMessageDialog(AdmissionBrowser.this,
												MessageBundle.getMessage("angal.admission.invalidadmissionperiod") + MessageBundle.getMessage("angal.admission.theadmissionbetween") + " "
														+ sdf.format(invalidStart) + " " + MessageBundle.getMessage("angal.admission.and") + " " + sdf.format(invalidEnd) + " "
														+ MessageBundle.getMessage("angal.admission.alreadyexists"));
										dateOutFieldCal.setDate(null);
										return;
									}

								}

								// updateDisplay
								admission.setDisDate(dateOut);
								isDischarge = true;
								int bedDays = Integer.parseInt(bedDaysTextField.getText());
								if (bedDays > BED_DAYS_THRESHOLD) {
									int ok = JOptionPane.showConfirmDialog(AdmissionBrowser.this, "The admission exceed "+BED_DAYS_THRESHOLD+"days. Confirm?");
									if (ok != JOptionPane.OK_OPTION) return;
								}
							} catch (IllegalArgumentException iae) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavaliddischargedate"));
								return;
							}
						}

						// get operation ( it can be null)
						if (operationBox.getSelectedIndex() == 0) {
							admission.setOperationId(null);
						} else {
							admission.setOperationId(operationList.get(operationBox.getSelectedIndex() - 1).getCode());
						}

						// get operation date (may be null)
						date = operationDateFieldCal.getDate();
						if (date != null) {
							operationDate.setTime(date);
						} else operationDate = null;
						
						if (operationDate == null) {
							admission.setOpDate(null);
						} else {
							try {
								GregorianCalendar limit;
								if (admission.getDisDate() == null) {
									limit = today;
								} else {
									limit = admission.getDisDate();
								}

								if (operationDate.before(dateIn) || operationDate.after(limit)) {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidvisitdate"));
									return;
								}

								admission.setOpDate(operationDate);
							} catch (IllegalArgumentException iae) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidvisitdate"));
								operationDateField.setText(sdf.format(operationDate.getTime()));
								return;
							}
						}// else

						// get operation result (can be null)
						if (operationResultRadioN.isSelected()) {
							admission.setOpResult("N");
						} else if (operationResultRadioP.isSelected()) {
							admission.setOpResult("P");
						} else {
							admission.setOpResult(null);
						}

						// get discharge type (it can be null)
						// if isDischarge, null value not allowed
						if (disTypeBox.getSelectedIndex() == 0) {
							if (isDischarge) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectavaliddischargetype"));
								return;
							} else {
								admission.setDisType(null);
							}
						} else {
							if (dateOut == null) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertadischargedate"));
								return;
							}
							if (isDischarge) {
								admission.setDisType(disTypeList.get(disTypeBox.getSelectedIndex() - 1).getCode());
							} else {
								admission.setDisType(null);
							}
						}

						// get the disease out n.1 (it can be null)
						// if isDischarge, null value not allowed
						if (admission.getDiseaseOutId1() == null) {
							if (isDischarge) {
								int yes = JOptionPane.showConfirmDialog(null, MessageBundle.getMessage("angal.admission.diagnosisoutsameasdiagnosisin"));
								if (yes == JOptionPane.YES_OPTION) {
									if (diseaseOutList.contains(diseaseInBox.getSelectedItem()))
										admission.setDiseaseOutId1(admission.getDiseaseInId());
									else {
										JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectavaliddiagnosisout"));
										return;
									}
								} else {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseselectatleastfirstdiagnosisout"));
									return;
								}
							}
						} else {
							if (admission.getDisDate() == null) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertadischargedate"));
								return;
							}
						}
	
						// field notes
						if (textArea.getText().equals("")) {
							admission.setNote(null);
						} else {
							admission.setNote(textArea.getText());
						}
	
						// get transfusional unit (it can be null)
						try {
								float f = (Float) trsfUnitField.getValue();
								admission.setTransUnit(new Float(f));
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidunitvalue"));
							return;
						}

						// fields for pregnancy status
						ArrayList<Delivery> newDeliveries = new ArrayList<Delivery>();
						if (isPregnancy) 
						{
							//the user wants to register a delivery
							if (isDelivery()) 
							{
								// start and limit date for control
								GregorianCalendar start = admission.getAdmDate();
								GregorianCalendar limit;
								if (admission.getDisDate() == null) 
								{
									limit = today;
								} else 
								{
									limit = admission.getDisDate();
								}
								

								if (validateDeliveryData(today, start, limit))
								{
									newDeliveries = getNewBornData();
									for (int a = 0; a < newDeliveries.size(); a++) 
									{
										newDeliveries.get(a).setEstimatedGestationalAge(Integer.valueOf(estimatedGestationalAgeTextField.getText()));
										if (robsonIndexComboBox.getSelectedIndex()==0){
											newDeliveries.get(a).setRobsonIndex(null);
										} else {
											newDeliveries.get(a).setRobsonIndex((String) robsonIndexComboBox.getSelectedItem());
										}
										if (deliveryTypeBox.getSelectedIndex()==0){
											newDeliveries.get(a).setDeliveryType(null);
										} else {
											newDeliveries.get(a).setDeliveryType((DeliveryType) deliveryTypeBox.getSelectedItem());
										}
										newDeliveries.get(a).setDeliveryDate(deliveryDate);
										if (typeRemovePlacentaBox.getSelectedIndex()==0){
											newDeliveries.get(a).setRemovePlacentaType(null);
										} else {
											newDeliveries.get(a).setRemovePlacentaType((String) typeRemovePlacentaBox.getSelectedItem());
										}
										if (hivTestPlaceBox.getSelectedIndex()==0){
											newDeliveries.get(a).setHivTestPlace(null);
										} else {
											newDeliveries.get(a).setHivTestPlace((String) hivTestPlaceBox.getSelectedItem());
										}
										if (hivPositive.isSelected()) newDeliveries.get(a).setHivTestResult('P');
										else if (hivNegative.isSelected()) newDeliveries.get(a).setHivTestResult('N');
										if (hivPartnerPositive.isSelected()) newDeliveries.get(a).setHivTestResultPartner('P');
										else if (hivPartnerNegative.isSelected()) newDeliveries.get(a).setHivTestResultPartner('N');
										if (managementBox.getSelectedIndex()==0){
											newDeliveries.get(a).setManagement(null);
										} else {
											newDeliveries.get(a).setManagement((String) managementBox.getSelectedItem());
										}
										newDeliveries.get(a).setComplicationAPH(complicationsHashMap.get("APH"));
										newDeliveries.get(a).setComplicationPPH(complicationsHashMap.get("PPH"));
										newDeliveries.get(a).setComplicationCP(complicationsHashMap.get("Cord Prolapse"));
										newDeliveries.get(a).setBloodPressureMin(Integer.parseInt(bloodPressureMinField.getText()));
										newDeliveries.get(a).setBloodPressureMax(Integer.parseInt(bloodPressureMaxField.getText()));
										newDeliveries.get(a).setAncVisitDone((String) ancBox.getSelectedItem());
										if (mwhYes.isSelected()) newDeliveries.get(a).setMotherWaitingHomeDone('Y');
										else newDeliveries.get(a).setMotherWaitingHomeDone('N');
									}
								} 
								else return;
							}
						}
	

						// set not editable fields
						admission.setPatId(patient.getCode());

						if (admission.getDisDate() == null) {
							admission.setAdmitted(1);
						} else {
							admission.setAdmitted(0);
						}

						if (malnuCheck.isSelected()) {
							admission.setType("M");
						} 
						admission.setDeleted("N");

						// IOoperation result
						boolean result = false;
						
						// ready to save...
						if (isPregnancy) {
							if (pregnancy == null) {
								pregnancy = getPregnancyValues();
								result = pregnancyManager.newPregnancy(pregnancy);
							} else {
								int gravida = Integer.parseInt(textFieldGravida.getText());
								int parity = Integer.parseInt(textFieldParity.getText());
								pregnancy.setGravida(gravida);
								pregnancy.setParity(parity);
								pregnancy.setReal_delivery(Converters.toCalendar(deliveryDateFieldCal.getDate()));
								pregnancy.setActive('N'); //delivered
								result = pregnancyManager.updatePregnancy(pregnancy);
							}
							if (result && isDelivery()) {
								if (deliveries.size() > 1 && deleteNewBorn2) deliveryManager.deleteSingleDelivery(deliveries.get(1).getId());
								if (deliveries.size() > 2 && deleteNewBorn3) deliveryManager.deleteSingleDelivery(deliveries.get(2).getId());
								for (int a = 0; a < newDeliveries.size(); a++) {
									newDeliveries.get(a).setPregnancy(pregnancy);
									if (deliveries.size() > a) deliveryManager.updateDelivery(admission, newDeliveries.get(a));
									else deliveryManager.insertDelivery(admission, newDeliveries.get(a));
								}
							} else {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.problemsoccuredwhilesavingpregnancydata"));
								return;
							}
						}
						
						if (!editing && !isDischarge) {
							int newKey = admMan.newAdmissionReturnKey(admission);
							if (newKey > 0) {
								result = true;
								admission.setId(newKey);
								fireAdmissionInserted(admission);
								if (GeneralData.XMPPMODULEENABLED) {
									CommunicationFrame frame= (CommunicationFrame)CommunicationFrame.getFrame();
									frame.sendMessage("new patient admission: "+patient.getName()+" in "+((Ward)wardBox.getSelectedItem()).getDescription(), (String)shareWith.getSelectedItem(), false);
								}
								dispose();
							}
						} else if (!editing && isDischarge) {
							result = admMan.newAdmission(admission);
							if (result) {
								fireAdmissionUpdated(admission);
								dispose();
							}
						} else {
							result = admMan.updateAdmission(admission);
							if (result) {
								fireAdmissionUpdated(admission);
								if (GeneralData.XMPPMODULEENABLED) {
									CommunicationFrame frame= (CommunicationFrame)CommunicationFrame.getFrame();
									frame.sendMessage("discharged patient: "+patient.getName()+" for "+((DischargeType)disTypeBox.getSelectedItem()).getDescription() , (String)shareWith.getSelectedItem(), false);
								}
								dispose();
							}
						}

						if (!result) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.thedatacouldnotbesaved"));
						} else {
							dispose();
						}
					} finally {
						BusyState.setBusyState(AdmissionBrowser.this, false);
					}
				}

				private Pregnancy getPregnancyValues() {
					int gravida = Integer.parseInt(textFieldGravida.getText());
					int parity = Integer.parseInt(textFieldParity.getText());
					Pregnancy preg = new Pregnancy();
					preg.setPatId(patient.getCode());
					preg.setGravida(gravida);
					preg.setParity(parity);
					preg.setChildrenAlive(gravida-parity);
					preg.setReal_delivery(Converters.toCalendar(deliveryDateFieldCal.getDate()));
					preg.setActive('N');
					return preg;
				}
				
				private boolean isDelivery() {
					if (deliveryDateFieldCal.getDate() != null
							|| deliveryTypeBox.getSelectedIndex() != 0)
						return true;
					return false;
				}

				/*
				 * Validate as much fields as possible before to save the Delivery data
				 */
				private boolean validateDeliveryData(GregorianCalendar today, GregorianCalendar start, GregorianCalendar limit) {
					if (deliveryDateFieldCal.getDate() == null) {
						JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
						deliveryDateFieldCal.requestFocusInWindow();
						return false;
					} else {
						deliveryDate = new GregorianCalendar(); 
						deliveryDate.setTime(deliveryDateFieldCal.getDate());
					}
					if (deliveryTypeBox.getSelectedIndex() == 0) {
						JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertadeliverytype"));
						deliveryTypeBox.requestFocusInWindow();
						return false;
					}
					if (!validateBloodPressure()) {
						JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidbloodpressure"));
						bloodPressureMinField.requestFocusInWindow();
						return false;
					}
					if (!mwhYes.isSelected() && !mwhNo.isSelected()) {
						JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleasesetmotherwaitinghome"));
						return false;
					}
					if (GeneralData.PREGNANCYCARE) 
					{
						/************************
						 * NEW BORN 1
						 ***********************/
						float f; //to perform several controls
						if (!sex1Female.isSelected() && !sex1Male.isSelected()) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidsexfornewborn"));
							return false;
						}
						try {
							f = Float.parseFloat(weight1TextField.getText());
							if (f < 0.0f) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
								weight1TextField.requestFocusInWindow();
								return false;
							}
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
							weight1TextField.requestFocusInWindow();
							return false;
						}
						try {
							f = Float.parseFloat(height1TextField.getText());
							if (f < 0.0f) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidheightvalue"));
								height1TextField.requestFocusInWindow();
								return false;
							}
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidheightvalue"));
							height1TextField.requestFocusInWindow();
							return false;
						}
						if (delrestypeBox1.getSelectedIndex() == 0) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertadeliveryresulttype"));
							delrestypeBox1.requestFocusInWindow();
							return false;
						}
//						if (apgarScore1.getText().equals("")) {
//							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertanapgarscore"));
//							apgarScore1.requestFocusInWindow();
//							return false;
//						}
//						if (!validateApgar(apgarScore1.getText())) {
//							JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidapgarscore"));
//							apgarScore1.requestFocusInWindow();
//							return false;
//						}
						/************************
						 * NEW BORN 2
						 ***********************/
						if (newborn2EnableCheckbox.isSelected()){// a second newborn
							if (!sex2Female.isSelected() && !sex2Male.isSelected()) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidsexfornewborn") + " 2");
								return false;
							}
							try {
								f = Float.parseFloat(weight2TextField.getText());
								if (f < 0.0f) {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
									return false;
								}
							} catch (NumberFormatException e) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
								return false;
							}
							try {
								f = Float.parseFloat(height2TextField.getText());
								if (f < 0.0f) {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidheightvalue"));
									return false;
								}
							} catch (NumberFormatException e) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidheightvalue"));
								return false;
							}
							if (delrestypeBox2.getSelectedIndex() == 0) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertadeliveryresulttype") + " 2");
								return false;
							}
//							if (apgarScore2.getText().equals("")) {
//								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertanapgarscore") + " 2");
//								apgarScore2.requestFocusInWindow();
//								return false;
//							}
//							if (!validateApgar(apgarScore2.getText())) {
//								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidapgarscore") + " 2");
//								apgarScore2.requestFocusInWindow();
//								return false;
//							}
						}
						/************************
						 * NEW BORN 3
						 ***********************/
						if (newborn3EnableCheckBox.isSelected()){// a second newborn
							if (!sex3Female.isSelected() && !sex3Male.isSelected()) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidsexfornewborn") + " 3");
								return false;
							}
							try {
								f = Float.parseFloat(weight3TextField.getText());
								if (f < 0.0f) {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
									return false;
								}
							} catch (NumberFormatException e) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
								return false;
							}
							try {
								f = Float.parseFloat(height3TextField.getText());
								if (f < 0.0f) {
									JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidheightvalue"));
									return false;
								}
							} catch (NumberFormatException e) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertavalidheightvalue"));
								return false;
							}
							if (delrestypeBox3.getSelectedIndex() == 0) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this, MessageBundle.getMessage("angal.admission.pleaseinsertadeliveryresulttype") + " 3");
								return false;
							}
						}
					}
					else
					{
						oldValidateAndSettings(today, start, limit);
					}
					return true;
				}

				private boolean validateBloodPressure() {
					int i;
					try {
						i = Integer.parseInt(bloodPressureMinField.getText());
						if (i < 0) return false;
						i = Integer.parseInt(bloodPressureMaxField.getText());
						if (i < 0) return false;
					} catch (NumberFormatException e) {
						return false;
					}
					return true;
				}

				private void oldValidateAndSettings(GregorianCalendar today, GregorianCalendar start, GregorianCalendar limit) {
					String d; //to perform several controls
					// get weight (it can be null)
					try {
						if (weightField.getText().equals("")) {
							admission.setWeight(null);
						} else {
							float f = Float.parseFloat(weightField.getText());
							if (f < 0.0f) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this,
										MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
								return;
							} else {
								admission.setWeight(new Float(f));
							}
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(AdmissionBrowser.this,
								MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
						return;
					}

					// get treatment type(may be null)
					if (treatmTypeBox.getSelectedIndex() == 0) {
						admission.setPregTreatmentType(null);
					} else {
						admission.setPregTreatmentType(treatmTypeList.get(treatmTypeBox.getSelectedIndex() - 1).getCode());
					}

					// get delivery date
					if (deliveryDateFieldCal.getDate() != null) {
						d = currentDateFormat.format(deliveryDateFieldCal.getDate());
					} 
					else d = "";

					if (d.equals("")) {
						admission.setDeliveryDate(null);
					} else {
						try {
							Date date = currentDateFormat.parse(d);
							deliveryDate = TimeTools.getServerDateTime();
							deliveryDate.setTime(date);

							if (deliveryDate.before(start) || deliveryDate.after(limit)) 
							{
								JOptionPane.showMessageDialog(AdmissionBrowser.this,
										MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
								deliveryDateFieldCal.setDate(null);
								return;
							}

							// updateDisplay
							d = currentDateFormat.format(date);
							deliveryDateFieldCal.setDate(currentDateFormat.parse(d));
							admission.setDeliveryDate(deliveryDate);

						} catch (ParseException pe) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this,
									MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
							deliveryDateField.setText("");// CONTROLLARE
							return;
						} catch (IllegalArgumentException iae) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this,
									MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
							deliveryDateField.setText("");// CONTROLLARE
							return;
						}
					}

					// get delivery type
					if (deliveryTypeBox.getSelectedIndex() == 0) {
						admission.setDeliveryTypeId(null);
					} else {
						admission.setDeliveryTypeId(deliveryTypeList.get(deliveryTypeBox.getSelectedIndex() - 1).getCode());
					}

					// get delivery result type
					if (deliveryResultTypeBox.getSelectedIndex() == 0) {
						admission.setDeliveryResultId(null);
					} else {
						admission.setDeliveryResultId(deliveryResultTypeList.get(deliveryResultTypeBox.getSelectedIndex() - 1).getCode());
					}

					// get ctrl1 date
					if (ctrl1DateFieldCal.getDate() != null) {
						d = currentDateFormat.format(ctrl1DateFieldCal.getDate());
					} else
						d = "";

					if (d.equals("")) {
						admission.setCtrlDate1(null);
					} else {
						try {
							Date date = currentDateFormat.parse(d);
							ctrl1Date = TimeTools.getServerDateTime();
							ctrl1Date.setTime(date);

							// date control
							if (admission.getDeliveryDate() == null) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this,
										MessageBundle.getMessage("angal.admission.controln1datenodeliverydatefound"));
								return;
							}
							if (ctrl1Date.before(deliveryDate)
									|| ctrl1Date.after(limit)) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this,
										MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln1date"));
								return;
							}

							// updateDisplay
							d = currentDateFormat.format(date);
							ctrl1DateFieldCal.setDate(currentDateFormat.parse(d));
							admission.setCtrlDate1(ctrl1Date);

						} catch (ParseException pe) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this,
									MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln1date"));
							ctrl1DateFieldCal.setDate(null);
							return;
						} catch (IllegalArgumentException iae) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this,
									MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln1date"));
							ctrl1DateFieldCal.setDate(null);
							return;
						}
					}

					// get ctrl2 date
					if (ctrl2DateFieldCal.getDate() != null) {
						d = currentDateFormat.format(ctrl2DateFieldCal
								.getDate());
					} else
						d = "";

					if (d.equals("")) {
						admission.setCtrlDate2(null);
					} else {
						if (admission.getCtrlDate1() == null) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this,
									MessageBundle.getMessage("angal.admission.controldaten2controldaten1notfound"));
							ctrl2DateFieldCal.setDate(null);
							return;
						}
						try {
							Date date = currentDateFormat.parse(d);
							ctrl2Date = TimeTools.getServerDateTime();
							ctrl2Date.setTime(date);
							if (ctrl2Date.before(ctrl1Date)
									|| ctrl2Date.after(limit)) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this,
										MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln2date"));
								return;
							}

							// updateDisplay
							d = currentDateFormat.format(date);
							ctrl2DateFieldCal.setDate(currentDateFormat
									.parse(d));
							admission.setCtrlDate2(ctrl2Date);

						} catch (ParseException pe) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this,
									MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln2date"));
							ctrl2DateFieldCal.setDate(null);
							return;
						} catch (IllegalArgumentException iae) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this,
									MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln2date"));
							ctrl2DateFieldCal.setDate(null);
							return;
						}
					}

					// get abort date
					if (abortDateFieldCal.getDate() != null) {
						d = currentDateFormat.format(abortDateFieldCal.getDate());
					} else
						d = "";

					if (d.equals("")) {
						admission.setAbortDate(null);
					} else {
						try {
							Date date = currentDateFormat.parse(d);
							abortDate = TimeTools.getServerDateTime();
							abortDate.setTime(date);
							if (ctrl2Date != null
									&& abortDate.before(ctrl2Date)
									|| ctrl1Date != null
									&& abortDate.before(ctrl1Date)
									|| abortDate.before(visitDate)
									|| abortDate.after(limit)) {
								JOptionPane.showMessageDialog(AdmissionBrowser.this,
										MessageBundle.getMessage("angal.admission.pleaseinsertavalidabortdate"));
								return;
							}

							// updateDisplay
							d = currentDateFormat.format(date);
							abortDateFieldCal.setDate(currentDateFormat.parse(d));
							admission.setAbortDate(abortDate);

						} catch (ParseException pe) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this,
									MessageBundle.getMessage("angal.admission.pleaseinsertavalidabortdate"));
							abortDateFieldCal.setDate(null);
							return;
						} catch (IllegalArgumentException iae) {
							JOptionPane.showMessageDialog(AdmissionBrowser.this,
									MessageBundle.getMessage("angal.admission.pleaseinsertavalidabortdate"));
							abortDateFieldCal.setDate(null);
							return;
						}
					}
				}

				/*
				 * Collect all data from each newborn
				 */
				private ArrayList<Delivery> getNewBornData() {
					ArrayList<Delivery> newDeliveries = new ArrayList<Delivery>();
					Delivery delivery1 = new Delivery();
					if (deliveries != null && deliveries.size() > 0) delivery1.setId(deliveries.get(0).getId());
					delivery1.setSex(sex1Female.isSelected()?"F":"M");
					delivery1.setDeliveryResultType(deliveryResultTypeList.get(delrestypeBox1.getSelectedIndex()-1));
					delivery1.setWeight(Integer.parseInt(weight1TextField.getText()));
					delivery1.setHeight(Integer.parseInt(height1TextField.getText()));
					delivery1.setApgarScore(apgar1Panel.getApgarScoreStr());
					delivery1.setChildName(childName1.getText().trim());
					newDeliveries.add(delivery1);
					if (newborn2EnableCheckbox.isSelected()){// a second newborn
						Delivery delivery2 = new Delivery();
						if (deliveries != null && deliveries.size() > 1) delivery2.setId(deliveries.get(1).getId());
						delivery2.setSex(sex2Female.isSelected()?"F":"M");
						delivery2.setDeliveryResultType(deliveryResultTypeList.get(delrestypeBox2.getSelectedIndex()-1));
						delivery2.setWeight(Integer.parseInt(weight2TextField.getText()));
						delivery2.setHeight(Integer.parseInt(height2TextField.getText()));
						delivery2.setApgarScore(apgar2Panel.getApgarScoreStr());
						delivery2.setChildName(childName2.getText().trim());
						newDeliveries.add(delivery2);
					}
					if (newborn3EnableCheckBox.isSelected()){// a third newborn
						Delivery delivery3 = new Delivery();
						if (deliveries != null && deliveries.size() > 2) delivery3.setId(deliveries.get(2).getId());
						delivery3.setSex(sex3Female.isSelected()?"F":"M");
						delivery3.setDeliveryResultType(deliveryResultTypeList.get(delrestypeBox3.getSelectedIndex()-1));
						delivery3.setWeight(Integer.parseInt(weight3TextField.getText()));
						delivery3.setHeight(Integer.parseInt(height3TextField.getText()));
						delivery3.setApgarScore(apgar3Panel.getApgarScoreStr());
						delivery3.setChildName(childName3.getText().trim());
						newDeliveries.add(delivery3);
					}
					return newDeliveries;
				}
			});
		}
		return saveButton;
	}
	
	/**
	 * for testing
	 * @param args
	 */
	public static void main(String[] args) {

		PropertyConfigurator.configure(new File("./rsc/log4j.properties").getAbsolutePath());
		GeneralData.getGeneralData();
		JFrame frame = new JFrame();
		AdmissionBrowserManager admMan = new AdmissionBrowserManager();
		PatientBrowserManager patMan = new PatientBrowserManager();
		Patient patient = patMan.getPatient(142542);
		ArrayList<Admission> admission = admMan.getAdmissions(patient);
		frame.add(new AdmissionBrowser(null, null, patient, admission.get(0)));
		frame.setVisible(true);
		frame.pack();
	}

}// class
