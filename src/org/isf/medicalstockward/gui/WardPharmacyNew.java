package org.isf.medicalstockward.gui;

import org.apache.log4j.PropertyConfigurator;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItem;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MedicalWardPrescription;
import org.isf.medicalstockward.model.MedicalWardPrescriptionDetail;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.menu.gui.MainMenu;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.patient.gui.SelectPatient;
import org.isf.patient.gui.SelectPatient.SelectionListener;
import org.isf.patient.model.Patient;
import org.isf.prescriber.manager.PrescriberManager;
import org.isf.prescriber.model.Prescriber;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.Price;
import org.isf.priceslist.model.PriceList;
import org.isf.utils.jobjects.RequestFocusListener;
import org.isf.utils.jobjects.TextPrompt;
import org.isf.utils.jobjects.TextPrompt.Show;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class WardPharmacyNew extends JDialog implements SelectionListener {

	//LISTENER INTERFACE --------------------------------------------------------
    private EventListenerList movementWardListeners = new EventListenerList();
	
	public interface MovementWardListeners extends EventListener {
		public void movementUpdated(AWTEvent e);
		public void movementInserted(AWTEvent e);
		public void missingInserted(AWTEvent e);
		public void prescriptionInserted(AWTEvent e);
	}
	
	public void addMovementWardListener(MovementWardListeners l) {
		movementWardListeners.add(MovementWardListeners.class, l);
	}
	
	public void removeMovementWardListener(MovementWardListeners listener) {
		movementWardListeners.remove(MovementWardListeners.class, listener);
	}
	
	private void fireMovementWardInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
		
		EventListener[] listeners = movementWardListeners.getListeners(MovementWardListeners.class);
		for (int i = 0; i < listeners.length; i++)
			((MovementWardListeners)listeners[i]).movementInserted(event);
	}
	
	private void fireMissingWardInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
		
		EventListener[] listeners = movementWardListeners.getListeners(MovementWardListeners.class);
		for (int i = 0; i < listeners.length; i++)
			((MovementWardListeners)listeners[i]).missingInserted(event);
	}
	
	private void firePrescriptionInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
		
		EventListener[] listeners = movementWardListeners.getListeners(MovementWardListeners.class);
		for (int i = 0; i < listeners.length; i++)
			((MovementWardListeners)listeners[i]).prescriptionInserted(event);
	}
	//---------------------------------------------------------------------------
	
	public void patientSelected(Patient patient) {
		patientSelected = patient;
		jTextFieldPatient.setText(patientSelected.getName());
		jTextFieldPatient.setEditable(false);
		jButtonPickPatient.setText(MessageBundle.getMessage("angal.medicalstockwardedit.changepatient")); //$NON-NLS-1$
		jButtonPickPatient.setToolTipText(MessageBundle.getMessage("angal.medicalstockwardedit.changethepatientassociatedwiththismovement")); //$NON-NLS-1$
		jButtonTrashPatient.setEnabled(true);
		AdmissionBrowserManager admMan = new AdmissionBrowserManager();
		Admission adm = admMan.getCurrentAdmission(patient);
		if (adm != null) {
			WardBrowserManager wardMan = new WardBrowserManager();
			wardIPD = wardMan.getWard(adm.getWardId());
			admID = adm.getId();
			String description = wardIPD.getDescription();
			
			if (description.equalsIgnoreCase("EMERGENCY")) {
				setEmergency(true);
				setIPD(null);
				setOPD(false);
			} else {
				setIPD(wardIPD);
				setOPD(false);
				setEmergency(false);
			}
		} else {
			OpdBrowserManager opdMan = new OpdBrowserManager();
			opdList = opdMan.getOpdList(patient.getCode());
			if (opdList == null) opdList = new ArrayList<Opd>();
			getJListOpd();
			setOPD(true);
			setIPD(null);
			setEmergency(false);
		}
		getJComboBoxDiseaseOpd();
		getJComboBoxDiseaseEmergency();
//		if (patientSelected.getWeight() == 0) {
//			JOptionPane.showMessageDialog(WardPharmacyNew.this, MessageBundle.getMessage("angal.medicalstockwardedit.theselectedpatienthasnoweightdefined"));
//		}
	}
	
	private void setEmergency(boolean state) {
		jCheckBoxEmergency.setSelected(state);
		jCheckBoxEmergency.setEnabled(state);
		jComboBoxDiseaseEmergency.setEnabled(state);
		jComboBoxDiseaseEmergency.setSelectedIndex(0);
	}
	
	private void setIPD(Ward ward) {
		if (ward != null) {
			jCheckBoxIpd.setEnabled(true);
			jCheckBoxIpd.setSelected(true);
			jLabelIPDStatus.setText(ward.getDescription());
//			jCheckBoxFree.setEnabled(true);
//			jCheckBoxFree.setSelected(ward.isFree());
//			jCheckBoxFree.setEnabled(false);
		} else {
			jCheckBoxIpd.setEnabled(false);
			jCheckBoxIpd.setSelected(false);
			jLabelIPDStatus.setText("");
//			jCheckBoxFree.setSelected(false);
//			jCheckBoxFree.setEnabled(false);
		}
	}
	
	private void setOPD(boolean state) {
		jCheckBoxOpd.setEnabled(state);
		jCheckBoxOpd.setSelected(state);
		jComboBoxDiseaseOpd.setEnabled(state);
		jComboBoxDiseaseOpd.setSelectedIndex(0);
		jCheckBoxReattendance.setEnabled(state);
	}
	
	private static final long serialVersionUID = 1L;
	private JTextField jTextFieldPatient;
	private JTextField jTextFieldSearch;
	private JComboBox jComboBoxDiseaseOpd;
	private JComboBox jComboBoxDiseaseEmergency;
	private JButton jButtonPickPatient;
	private JButton jButtonTrashPatient;
	private JPanel jPanelButtons;
	private JPanel jPanelNorth;
	private JButton jButtonOK;
	private JButton jButtonCancel;
	private JRadioButton jRadioPatient;
	private JTable jTableMedicals;
	private JScrollPane jScrollPaneMedicals;
	private JPanel jPanelMedicalsButtons;
	private JButton jButtonRemoveMedical;
	private static final Dimension PatientDimension = new Dimension(300,30);
	private JRadioButton jRadioUse;
	private JTextField jTextFieldUse;
	private JCheckBox jCheckBoxReattendance;
	private JCheckBox jCheckBoxOpd;
	private JComboBox jComboBoxPrescriber;

	private Patient patientSelected = null;
	private Ward wardSelected;
	private Class<?>[] medClasses = {Medical.class, Integer.class, Integer.class, Integer.class, Integer.class};
	private String[] medColumnNames = {
			MessageBundle.getMessage("angal.medicalstockward.medical"),
			"Stock", "Requested", "Given", "Missing"};
	private Integer[] medWidth = {250, 100, 100, 100, 100};
	private boolean[] medResizable = {true, false, false, false, false};
	
	//Medicals (ALL)
	private MedicalBrowsingManager medManager = new MedicalBrowsingManager();
	private ArrayList<Medical> medicals = medManager.getMedicalsSortedByCode();
	private HashMap<String, Medical> medicalMap; //map medicals by their prod_code
	private HashMap<Integer, Double> wardMap; //map quantities by their medical_id
	
	//Diseases (OPD)
	private DiseaseBrowserManager disMan = new DiseaseBrowserManager();
	private ArrayList<Disease> diseases = disMan.getDiseaseOpd();
	private ArrayList<Opd> opdList = new ArrayList<Opd>();
	private JList jListOpd;
	private DefaultListModel listModel = new DefaultListModel();
	private Opd selectedOpd;
	private JScrollPane jScrollPaneOpd;
	
	//Prescribers (ALL)
	private PrescriberManager prsMan = new PrescriberManager();
	private ArrayList<Prescriber> prescribers = prsMan.getPrescriber();

	//Medicals (in WARD)
	private ArrayList<MedicalWardStatus> medItems = new ArrayList<MedicalWardStatus>();
	private JCheckBox jCheckBoxIpd;
	private JLabel jLabelIPDStatus;
	private JCheckBox jCheckBoxEmergency;
//	private JCheckBox jCheckBoxFree;
	private JLabel jLabelEmergencyStatus;
	private Ward wardIPD;
	private Integer admID;
	private Disease diseaseOPD;

	private final String IMPROVED_DISCHARGE_TYPE_CODE = "I";
	private final String EMERGENCY_DISEASE = "999";
	

	public WardPharmacyNew(JFrame owner, Ward ward, ArrayList<MedicalWard> drugs) {
		super(owner, true);
		wardMap = new HashMap<Integer, Double>();
		for (MedicalWard medWard : drugs) {
			wardMap.put(medWard.getMedical().getCode(), medWard.getQty());
		}
		medicalMap = new HashMap<String, Medical>();
		for (Medical med : medicals) {
			medicalMap.put(med.getProd_code(), med);
		}
		wardSelected = ward;
		initComponents();
		jTextFieldPatient.addAncestorListener(new RequestFocusListener());
	}

	private void initComponents() {
		BorderLayout borderLayout = new BorderLayout();
		getContentPane().setLayout(borderLayout);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setResizeWeight(1.0);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(getJPanelButtons(), BorderLayout.SOUTH);
		mainPanel.add(getJPanelNorth(), BorderLayout.NORTH);
		mainPanel.add(getJPanelMedicalsButtons(), BorderLayout.EAST);
		mainPanel.add(getJScrollPaneMedicals(), BorderLayout.CENTER);
		
		JPanel sidePanel = new JPanel();
		sidePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		JLabel label = new JLabel("Patient's OPD");
		label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		sidePanel.add(label);
		sidePanel.add(getJScrollPaneOpd());
		
		splitPane.add(mainPanel);
		splitPane.add(sidePanel);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(MessageBundle.getMessage("angal.medicalstockwardedit.title"));
		setSize(950,600);
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent e) {
				//to free memory
				medicals.clear();
				diseases.clear();
				opdList.clear();
				prescribers.clear();
				medItems.clear();
				dispose();
			}			
		});
	}

	private JPanel getJPanelNorth() {
		if (jPanelNorth == null) {
			jPanelNorth = new JPanel();
			jPanelNorth.setBorder(new EmptyBorder(5, 5, 5, 5));
			GridBagLayout gbl_jPanelNorth = new GridBagLayout();
			gbl_jPanelNorth.columnWidths = new int[]{0, 0, 0, 0, 0};
			gbl_jPanelNorth.rowHeights = new int[]{25, 20, 0, 0, 21, 0, 0};
			gbl_jPanelNorth.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_jPanelNorth.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			jPanelNorth.setLayout(gbl_jPanelNorth);
			GridBagConstraints gbc_jRadioPatient = new GridBagConstraints();
			gbc_jRadioPatient.anchor = GridBagConstraints.WEST;
			gbc_jRadioPatient.insets = new Insets(0, 0, 5, 5);
			gbc_jRadioPatient.gridx = 0;
			gbc_jRadioPatient.gridy = 0;
			jPanelNorth.add(getJRadioPatient(), gbc_jRadioPatient);
			GridBagConstraints gbc_jTextFieldPatient = new GridBagConstraints();
			gbc_jTextFieldPatient.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldPatient.insets = new Insets(0, 0, 5, 5);
			gbc_jTextFieldPatient.gridx = 1;
			gbc_jTextFieldPatient.gridy = 0;
			jPanelNorth.add(getJTextFieldPatient(), gbc_jTextFieldPatient);
			GridBagConstraints gbc_jButtonPickPatient = new GridBagConstraints();
			gbc_jButtonPickPatient.anchor = GridBagConstraints.WEST;
			gbc_jButtonPickPatient.insets = new Insets(0, 0, 5, 5);
			gbc_jButtonPickPatient.gridx = 2;
			gbc_jButtonPickPatient.gridy = 0;
			jPanelNorth.add(getJButtonPickPatient(), gbc_jButtonPickPatient);
			GridBagConstraints gbc_jButtonTrashPatient = new GridBagConstraints();
			gbc_jButtonTrashPatient.insets = new Insets(0, 0, 5, 0);
			gbc_jButtonTrashPatient.gridx = 3;
			gbc_jButtonTrashPatient.gridy = 0;
			jPanelNorth.add(getJButtonTrashPatient(), gbc_jButtonTrashPatient);
			GridBagConstraints gbc_jCheckBoxOpd = new GridBagConstraints();
			gbc_jCheckBoxOpd.anchor = GridBagConstraints.WEST;
			gbc_jCheckBoxOpd.insets = new Insets(0, 0, 5, 5);
			gbc_jCheckBoxOpd.gridx = 0;
			gbc_jCheckBoxOpd.gridy = 1;
			jPanelNorth.add(getJCheckBoxOpd(), gbc_jCheckBoxOpd);
			GridBagConstraints gbc_jComboBoxDisease = new GridBagConstraints();
			gbc_jComboBoxDisease.fill = GridBagConstraints.HORIZONTAL;
			gbc_jComboBoxDisease.insets = new Insets(0, 0, 5, 5);
			gbc_jComboBoxDisease.gridx = 1;
			gbc_jComboBoxDisease.gridy = 1;
			jPanelNorth.add(getJComboBoxDiseaseOpd(), gbc_jComboBoxDisease);
			GridBagConstraints gbc_jCheckBoxReattendance = new GridBagConstraints();
			gbc_jCheckBoxReattendance.anchor = GridBagConstraints.WEST;
			gbc_jCheckBoxReattendance.insets = new Insets(0, 0, 5, 5);
			gbc_jCheckBoxReattendance.gridx = 2;
			gbc_jCheckBoxReattendance.gridy = 1;
			jPanelNorth.add(getJCheckBoxReattendance(), gbc_jCheckBoxReattendance);
			GridBagConstraints gbc_jCheckBoxIpd = new GridBagConstraints();
			gbc_jCheckBoxIpd.anchor = GridBagConstraints.WEST;
			gbc_jCheckBoxIpd.insets = new Insets(0, 0, 5, 5);
			gbc_jCheckBoxIpd.gridx = 0;
			gbc_jCheckBoxIpd.gridy = 2;
			jPanelNorth.add(getJCheckBoxIpd(), gbc_jCheckBoxIpd);
			GridBagConstraints gbc_jLabelIPDStatus = new GridBagConstraints();
			gbc_jLabelIPDStatus.anchor = GridBagConstraints.WEST;
			gbc_jLabelIPDStatus.insets = new Insets(0, 0, 5, 5);
			gbc_jLabelIPDStatus.gridx = 1;
			gbc_jLabelIPDStatus.gridy = 2;
			jPanelNorth.add(getJLabelIPDStatus(), gbc_jLabelIPDStatus);
//			GridBagConstraints gbc_jCheckBoxFree = new GridBagConstraints();
//			gbc_jCheckBoxFree.anchor = GridBagConstraints.WEST;
//			gbc_jCheckBoxFree.insets = new Insets(0, 0, 5, 5);
//			gbc_jCheckBoxFree.gridx = 2;
//			gbc_jCheckBoxFree.gridy = 2;
//			jPanelNorth.add(getJCheckBoxFree(), gbc_jCheckBoxFree);
			GridBagConstraints gbc_jCheckBoxEmergency = new GridBagConstraints();
			gbc_jCheckBoxEmergency.insets = new Insets(0, 0, 5, 5);
			gbc_jCheckBoxEmergency.gridx = 0;
			gbc_jCheckBoxEmergency.gridy = 3;
			jPanelNorth.add(getJCheckBoxEmergency(), gbc_jCheckBoxEmergency);
			GridBagConstraints gbc_jComboBoxDiseaseEmergency = new GridBagConstraints();
			gbc_jComboBoxDiseaseEmergency.fill = GridBagConstraints.HORIZONTAL;
			gbc_jComboBoxDiseaseEmergency.insets = new Insets(0, 0, 5, 5);
			gbc_jComboBoxDiseaseEmergency.gridx = 1;
			gbc_jComboBoxDiseaseEmergency.gridy = 3;
			jPanelNorth.add(getJComboBoxDiseaseEmergency(), gbc_jComboBoxDiseaseEmergency);
			GridBagConstraints gbc_jLabelEmergencyStatus = new GridBagConstraints();
			gbc_jLabelEmergencyStatus.insets = new Insets(0, 0, 5, 5);
			gbc_jLabelEmergencyStatus.gridx = 1;
			gbc_jLabelEmergencyStatus.gridy = 3;
			jPanelNorth.add(getJLabelEmergencyStatus(), gbc_jLabelEmergencyStatus);
			GridBagConstraints gbc_jRadioUse = new GridBagConstraints();
			gbc_jRadioUse.anchor = GridBagConstraints.WEST;
			gbc_jRadioUse.insets = new Insets(0, 0, 5, 5);
			gbc_jRadioUse.gridx = 0;
			gbc_jRadioUse.gridy = 4;
			jPanelNorth.add(getJRadioUse(), gbc_jRadioUse);
			ButtonGroup group = new ButtonGroup();
			group.add(jRadioPatient);
			group.add(jRadioUse);
			ButtonGroup groupPatient = new ButtonGroup();
			groupPatient.add(jCheckBoxOpd);
			groupPatient.add(jCheckBoxIpd);
			groupPatient.add(jCheckBoxEmergency);
			GridBagConstraints gbc_jTextFieldUse = new GridBagConstraints();
			gbc_jTextFieldUse.insets = new Insets(0, 0, 5, 5);
			gbc_jTextFieldUse.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldUse.gridx = 1;
			gbc_jTextFieldUse.gridy = 4;
			jPanelNorth.add(getJTextFieldUse(), gbc_jTextFieldUse);
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridwidth = 4;
			gbc_textField.gridx = 0;
			gbc_textField.gridy = 5;
			jPanelNorth.add(getJTextFieldSearch(), gbc_textField);
		}
		return jPanelNorth;
	}
	
	private JTextField getJTextFieldSearch() {
		if (jTextFieldSearch == null) {
			jTextFieldSearch = new JTextField();
			jTextFieldSearch.setPreferredSize(new Dimension(300, 30));
			jTextFieldSearch.setHorizontalAlignment(SwingConstants.LEFT);
			
			jTextFieldSearch.setColumns(10);
			TextPrompt suggestion = new TextPrompt("Type a code or a description and press ENTER", jTextFieldSearch, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
			jTextFieldSearch.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					
					String text = jTextFieldSearch.getText();
					Medical med = null;
					
					if (medicalMap.containsKey(text)) {
						// Medical found
						med = medicalMap.get(text);
					} else {
						
						med = chooseMedical(text);
					}
					
					if (med != null) {	
						for (int i = 0; i < medItems.size(); i++) {
							if (medItems.get(i).getMedical().getCode() == med.getCode()) {
								jTableMedicals.getSelectionModel().setSelectionInterval(i, i);
								return;
							}
						}
						// Quantity
						int qty = askQuantity(med);
						if (qty == 0)
							return;
						
						addItem(med, new Double(qty));
						jTextFieldSearch.setText("");
					}
				}
			});
		}
		return jTextFieldSearch;
	}
	
	protected int askQuantity(Medical med) {
		String quantity = JOptionPane.showInputDialog(WardPharmacyNew.this, med.getDescriptionWithCode() + " quantity:", 0);
		int qty = 0;
		if (quantity != null) {
			try {
				qty = Integer.parseInt(quantity);
				if (qty == 0)
					return 0;
				if (qty < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(WardPharmacyNew.this, "please insert a valid value");
				qty = 0;
			}
		}
		return qty;
	}
	
	protected Medical chooseMedical(String text) {
		ArrayList<Medical> medList = new ArrayList<Medical>();
		for (Medical aMed : medicalMap.values()) {
			if (aMed.getDescriptionWithCode().contains(text))
				medList.add(aMed);
		}
		Collections.sort(medList);
		Medical med = null;
		
		if (!medList.isEmpty()) {
			JTable medTable = new JTable(new StockMedModel(medList));
			medTable.getColumnModel().getColumn(0).setMaxWidth(100);
			medTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			medTable.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				@Override
				public void mousePressed(MouseEvent e) {}
				
				@Override
				public void mouseExited(MouseEvent e) {}
				
				@Override
				public void mouseEntered(MouseEvent e) {}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2 && !e.isConsumed()) {
						e.consume();
						//JTable thisTable = (JTable) e.getSource();
						//AncestorListener[] alist = thisTable.getAncestorListeners();
						//ContainerListener[] clist = thisTable.getContainerListeners();
						//TODO find a way to perform a OK_OPTION 
					}
				}
			});
			JPanel panel = new JPanel();
			panel.add(new JScrollPane(medTable));
			
			int ok = JOptionPane.showConfirmDialog(WardPharmacyNew.this, panel, "Choose a Medical", JOptionPane.YES_NO_OPTION);
			
			if (ok == JOptionPane.OK_OPTION) {
				int row = medTable.getSelectedRow();
				med = medList.get(row);
			}
			return med;
		}
		return null;
	}
	
	private JTextField getJTextFieldUse() {
		if (jTextFieldUse == null) {
			jTextFieldUse = new JTextField();
			jTextFieldUse.setColumns(10);
		}
		return jTextFieldUse;
	}

	private JRadioButton getJRadioUse() {
		if (jRadioUse == null) {
			jRadioUse = new JRadioButton(MessageBundle.getMessage("angal.medicalstockwardedit.internaluse"));
			jRadioUse.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					jTextFieldPatient.setEnabled(false);
					jButtonPickPatient.setEnabled(false);
					jButtonTrashPatient.setEnabled(false);
					jTextFieldUse.setEnabled(true);
					if (GeneralData.FORCEDISEASECODE) {
						jComboBoxDiseaseOpd.setEnabled(false);
						jComboBoxDiseaseEmergency.setEnabled(false);
					}
				}
			});
		}
		return jRadioUse;
	}

	public double round(double input, double step) {
		return Math.round(input / step) * step;
	}
	
	private JButton getJButtonRemoveMedical() {
		if (jButtonRemoveMedical == null) {
			jButtonRemoveMedical = new JButton();
			jButtonRemoveMedical.setText(MessageBundle.getMessage("angal.medicalstockwardedit.removeitem")); //$NON-NLS-1$
			jButtonRemoveMedical.setIcon(new ImageIcon("rsc/icons/delete_button.png")); //$NON-NLS-1$
			jButtonRemoveMedical.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (jTableMedicals.getSelectedRow() < 0) { 
						JOptionPane.showMessageDialog(WardPharmacyNew.this,
								MessageBundle.getMessage("angal.medicalstockwardedit.pleaseselectanitem"), //$NON-NLS-1$
								"Error", //$NON-NLS-1$
								JOptionPane.WARNING_MESSAGE);
					} else {
						removeItem(jTableMedicals.getSelectedRow());
					}
				}
			});
		}
		return jButtonRemoveMedical;
	}
	
	private void addItem(Medical med, Double requested) {
		if (med != null) {
			Double stock = wardMap.get(med.getCode());
			if (stock == null) stock = new Double(0);
			double given = 0;
			if (requested <= stock) {
				given = requested;
			} else {
				given = stock;
			}
			double missing = requested - given;
			
			MedicalWardStatus item = new MedicalWardStatus(med, requested);
			item.setStock(stock);
			item.setGiven(given);
			item.setMissing(missing);
			
			medItems.add(item);
			jTableMedicals.updateUI();
		}
	}
	
	private void removeItem(int row) {
		if (row != -1) {
			medItems.remove(row);
			int index = row;
			if (row == jTableMedicals.getRowCount()) index = --row;
			jTableMedicals.getSelectionModel().setSelectionInterval(index, index);
			jTableMedicals.updateUI();
		}
		
	}
	
	private JPanel getJPanelMedicalsButtons() {
		if (jPanelMedicalsButtons == null) {
			jPanelMedicalsButtons = new JPanel();
			jPanelMedicalsButtons.setLayout(new BoxLayout(jPanelMedicalsButtons, BoxLayout.Y_AXIS));
			jPanelMedicalsButtons.add(getJButtonRemoveMedical());
		}
		return jPanelMedicalsButtons;
	}
	
	private JScrollPane getJScrollPaneOpd() {
		if (jScrollPaneOpd == null) {
			jScrollPaneOpd = new JScrollPane();
			jScrollPaneOpd.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPaneOpd.setViewportBorder(new LineBorder(SystemColor.activeCaption, 1, true));
			jScrollPaneOpd.setBorder(null);
			jScrollPaneOpd.setViewportView(getJListOpd());
			jScrollPaneOpd.setPreferredSize(new Dimension(150,600));
		}
		return jScrollPaneOpd;
	}

	private JScrollPane getJScrollPaneMedicals() {
		if (jScrollPaneMedicals == null) {
			jScrollPaneMedicals = new JScrollPane();
			jScrollPaneMedicals.setViewportView(getJTableMedicals());
		}
		return jScrollPaneMedicals;
	}

	private JTable getJTableMedicals() {
		if (jTableMedicals == null) {
			jTableMedicals = new JTable();
			jTableMedicals.setModel(new MedicalTableModel());
			jTableMedicals.setRowHeight(24);
			jTableMedicals.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jTableMedicals.setDefaultRenderer(Integer.class, new ColorTableCellRenderer());
			for (int i = 0; i < medWidth.length; i++) {
				jTableMedicals.getColumnModel().getColumn(i).setPreferredWidth(medWidth[i]);
				if (!medResizable[i]) jTableMedicals.getColumnModel().getColumn(i).setMaxWidth(medWidth[i]);
			}
		}
		return jTableMedicals;
	}

	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("Confirm"); //$NON-NLS-1$
			jButtonOK.setMnemonic(KeyEvent.VK_C);
			jButtonOK.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					boolean isPatient;
					String description;
					int age = 0;
					float weight = 0;
					
					if (jRadioPatient.isSelected()) {
						if (patientSelected == null) {
							JOptionPane.showMessageDialog(WardPharmacyNew.this,	MessageBundle.getMessage("angal.medicalstockwardedit.pleaseselectapatient")); //$NON-NLS-1$
							return;
						}
						description = patientSelected.getName();
						age = patientSelected.getAge();
						weight = patientSelected.getWeight();
						isPatient = true;
					} else {
						if (jTextFieldUse.getText().compareTo("") == 0) {
							JOptionPane.showMessageDialog(WardPharmacyNew.this,	MessageBundle.getMessage("angal.medicalstockwardedit.pleaseinsertadescriptionfortheinternaluse")); //$NON-NLS-1$
							jTextFieldUse.requestFocus();
							return;
						}
						description = jTextFieldUse.getText();
						isPatient = false;
					}
//					if (medItems.size() == 0) {
//						JOptionPane.showMessageDialog(WardPharmacyNew.this,	MessageBundle.getMessage("angal.medicalstockwardedit.pleaseselectadrug")); //$NON-NLS-1$
//						return;
//					}
					if (jComboBoxPrescriber.getSelectedItem() instanceof Prescriber == false) {
						JOptionPane.showMessageDialog(WardPharmacyNew.this,	MessageBundle.getMessage("Please select a prescriber")); //$NON-NLS-1$
						return;
					}
					
					MovWardBrowserManager wardManager = new MovWardBrowserManager();
					GregorianCalendar newDate = new GregorianCalendar();
					
					ArrayList<MovementWard> dispenseMedical = new ArrayList<MovementWard>();
					ArrayList<MedicalWard> missingMedical = new ArrayList<MedicalWard>();
					for (int i = 0; i < medItems.size(); i++) {
						if (medItems.get(i).getGiven() > 0) {
							dispenseMedical.add(new MovementWard(
									wardSelected,
									newDate,
									isPatient,
									patientSelected,
									age,
									weight,
									description,
									medItems.get(i).getMedical(),
									medItems.get(i).getGiven(),
									MessageBundle.getMessage("angal.medicalstockwardedit.pieces")));
						}
						if (medItems.get(i).getMissing() > 0) {
							missingMedical.add(new MedicalWard(
									medItems.get(i).getMedical(),
									medItems.get(i).getMissing()));
						}
					}
					
					if (isPatient) {
						/////// OPD ///////
						if (jCheckBoxOpd.isSelected()) {
							int opdCode = 0;
							if (GeneralData.FORCEDISEASECODE && 
									jComboBoxDiseaseOpd.getSelectedItem() instanceof Disease == false) {
								JOptionPane.showMessageDialog(WardPharmacyNew.this,	MessageBundle.getMessage("angal.medicalstockwardedit.pleaseselectadisease")); //$NON-NLS-1$
								return;
							}
							if (GeneralData.AUTOMATICOPD) {
								if (medItems.size() == 0) {
									int ok = JOptionPane.showConfirmDialog(WardPharmacyNew.this, "This operation will only close the OPD case for this patient (no prescription), confirm?"); //$NON-NLS-1$
									if (ok != JOptionPane.OK_OPTION) return;
								}
								//TODO: opd and prescription should be saved in the same transaction
								if (selectedOpd != null) {
									GregorianCalendar today = TimeTools.today(false);
									if (selectedOpd.getVisitDate().compareTo(today) < 0) {
										int ok = JOptionPane.showConfirmDialog(WardPharmacyNew.this, "This prescription will be associated to an old OPD case (" +
													selectedOpd.toString() + "), confirm?"); //$NON-NLS-1$
										if (ok != JOptionPane.OK_OPTION) return;
									}
									opdCode = selectedOpd.getCode();
									
								} else {
									Disease selectedDisease = (Disease) jComboBoxDiseaseOpd.getSelectedItem();
									GregorianCalendar today = TimeTools.today(false);
									for (Opd opd : opdList) {
										if (opd.getVisitDate().compareTo(today) >= 0) {
											if (selectedDisease.getCode().equals(opd.getDisease())) {
												int ok = JOptionPane.showConfirmDialog(WardPharmacyNew.this, "The selected disease is already associated to an OPD case (" +
														opd.toString() + "), confirm?"); //$NON-NLS-1$
												if (ok != JOptionPane.OK_OPTION) {
													return;
												} else {
													jListOpd.setSelectedIndex(opdList.indexOf(opd));
													selectedOpd = opd;
													opdCode = selectedOpd.getCode();
													diseaseOPD = (Disease) jComboBoxDiseaseOpd.getSelectedItem();
												}
												break;
											}
										}
									}
									if (opdCode == 0) opdCode = newOpd((Disease) jComboBoxDiseaseOpd.getSelectedItem());
								}
							}
							if (!dispenseMedical.isEmpty()) {
								//TODO: wouldn't it be better to save the prescription anyway?
								if (GeneralData.AUTOMATICBILL) {
									Bill bill = newBill();
									if (bill != null) {
										savePrescription(wardManager, bill, bill.getStatus().equals("C"), opdCode);
									} else {
										return;
									}
								}
							} 
						}
						/////// IPD //////
						if (jCheckBoxIpd.isSelected()) { // && !jCheckBoxFree.isSelected()) {
							if (!dispenseMedical.isEmpty()) {
								if (GeneralData.AUTOMATICBILL) {
									Bill bill = newBill();
									if (bill != null) {
										savePrescription(wardManager, bill, bill.getStatus().equals("C"));
									} else {
										return;
									}
								}
							} 
						}
					    /////// EMERGENCY //////
						if (jCheckBoxEmergency.isSelected()) {
							Object object = jComboBoxDiseaseEmergency.getSelectedItem();
							if (GeneralData.FORCEDISEASECODE && jComboBoxDiseaseEmergency.getSelectedItem() instanceof Disease) {
								Disease disease =  (Disease) object;
								
								if (disease.getCode().equals(EMERGENCY_DISEASE)) {
									if (medItems.size() == 0) {
										//Empty prescription: cannot either record the prescription nor close the Emergency
										JOptionPane.showMessageDialog(WardPharmacyNew.this,	"Please add a drug to continue the Emergency case or\nselect the final disease to close the case"); //$NON-NLS-1$
										return;
									} else {
										//Continue: record the prescription (after) and the OPD
									}
								} else {
									if (medItems.size() == 0) { 
										//ask if to close the Emergency 
										int ok = JOptionPane.showConfirmDialog(WardPharmacyNew.this, "This will only close the Emergency case for this patient (no prescription), confirm?"); //$NON-NLS-1$
										if (ok != JOptionPane.OK_OPTION) return;
									}
									//record the precription (after) and close the Emergency with OPD
									AdmissionBrowserManager admMan = new AdmissionBrowserManager();
									Admission admission = admMan.getCurrentAdmission(patientSelected);
									admission.setDisDate(new GregorianCalendar());
									admission.setDisType(IMPROVED_DISCHARGE_TYPE_CODE);
									admission.setDiseaseOutId1(disease.getCode());
									admission.setAdmitted(0);
									admMan.updateAdmission(admission);
									if (GeneralData.AUTOMATICOPD) newOpd(disease);
								}
							} else {
								JOptionPane.showMessageDialog(WardPharmacyNew.this,	MessageBundle.getMessage("angal.medicalstockwardedit.pleaseselectadisease")); //$NON-NLS-1$
								return;
							}
							if (!dispenseMedical.isEmpty()) {
								if (GeneralData.AUTOMATICBILL) {
									Bill bill = newBill();
									if (bill != null) {
										savePrescription(wardManager, bill, bill.getStatus().equals("C"));
										/*boolean result = false;
									
										result = wardManager.newMovementWard(dispenseMedical); //DISPENSE
										if (result) {
											fireMovementWardInserted();
										} else {
											JOptionPane.showMessageDialog(WardPharmacyNew.this, MessageBundle.getMessage("angal.medicalstockwardedit.thedatacouldnotbesaved"));
											return;
										}*/
									} else {
										return;
									}
								}
							} 
						}
					} else {
					    /////// INTERNAL USE //////
						boolean result = false;
						if (!dispenseMedical.isEmpty()) {
							result = wardManager.newMovementWard(dispenseMedical); //DISPENSE
							if (result) {
								fireMovementWardInserted();
							} else {
								JOptionPane.showMessageDialog(WardPharmacyNew.this, MessageBundle.getMessage("angal.medicalstockwardedit.thedatacouldnotbesaved"));
								return;
							}
						}
					}
					
					/////// MISSING //////
					boolean result = false;
					if (!missingMedical.isEmpty()) {
						result = wardManager.storeMissingMedicals(missingMedical, wardSelected); //SAVE MISSING
						if (result) {
							fireMissingWardInserted();
						} else {
							JOptionPane.showMessageDialog(WardPharmacyNew.this, MessageBundle.getMessage("angal.medicalstockwardedit.thedatacouldnotbesaved"));
						}
					}
					dispose();
				}
				
				private void savePrescription(MovWardBrowserManager wardManager, Bill bill, boolean close) {
					savePrescription(wardManager, bill, close, 0);
				}

				private void savePrescription(MovWardBrowserManager wardManager, Bill bill, boolean close, int opdCode) {
					boolean ipd = jCheckBoxIpd.isSelected();
					boolean opd = jCheckBoxOpd.isSelected();
					boolean emergency = jCheckBoxEmergency.isSelected();
					MedicalWardPrescription prescription = new MedicalWardPrescription(
							new GregorianCalendar(),
							bill,
							patientSelected,
							wardSelected,
							ipd,
							ipd || emergency ? wardIPD.getCode() : null,
							opd ? diseaseOPD.getCode() : null,
							(Prescriber) jComboBoxPrescriber.getSelectedItem()
					);
					prescription.setOpdCode(opdCode);
					ArrayList<MedicalWardPrescriptionDetail> details = new ArrayList<MedicalWardPrescriptionDetail>();
					for (MedicalWardStatus medItem : medItems) {
						if (medItem.getGiven() > 0) {
							MedicalWardPrescriptionDetail detail = new MedicalWardPrescriptionDetail();
							detail.setPrescription(prescription);
							detail.setMedical(medItem.getMedical());
							detail.setQuantity(medItem.getGiven());
							detail.setUnits("pieces");
							
							details.add(detail);
						}
					}
					boolean result = wardManager.storePrescription(prescription, details);
					if (result) {
						if (close) wardManager.closeMedicalPrescription(prescription);
						firePrescriptionInserted();
					}
				}
			});
		}
		return jButtonOK;
	}
	
	/**
	 * 
	 */
	private Bill newBill() {
		PriceListManager priceMan = new PriceListManager();
		ArrayList<Price> prices = priceMan.getPrices();
		PriceList priceList = priceMan.getLists().get(0);
		BillBrowserManager billMan = new BillBrowserManager();
		
		
		//Check if is Free
		boolean isStaff = patientSelected.getPreviousCode().startsWith("0");
		boolean isFree = false;
		if (!isStaff) { //CHECK THE PRICE CODE
			Bill lastBill = null;
			ArrayList<Bill> listBill = billMan.getPendingBills(patientSelected.getCode());
			if (!listBill.isEmpty()) lastBill = listBill.get(0);
			if (lastBill != null) {
				ArrayList<BillItem> lastBillItems = billMan.getItems(lastBill.getId());
				for (BillItem item : lastBillItems) {
					String description = item.getItemDescription();
					if (description.contains("P1 -") || 
						description.contains("P2 -") ||
						description.contains("N1 -") ||
						description.contains("N2 -") ||
						description.contains("D1 -") ||
						description.contains("D2 -") ||
						description.contains("D7 -") ||
						description.contains("PS1 -") ||
						description.contains("PS2 -") ||
						description.contains("PS3 -") ||
						description.contains("PS4 -") ||
						description.contains("POR1 -") ||
						description.contains("POR2 -") ||
						description.contains("POR3 -") ||
						description.contains("POR4 -") ||
						description.contains("PSS1 -") ||
						description.contains("MWA -")
					) isFree = true;
					else if (description.contains("D3 -") ||
						description.contains("D4 -") ||
						description.contains("D5 -") ||
						description.contains("D6 -") ||
						description.contains("M1 -") ||
						description.contains("M2 -") ||
						description.contains("MOR1 -") ||
						description.contains("MOR2 -") ||
						description.contains("MOR3 -") ||
						description.contains("OR1 -") ||
						description.contains("OR2 -") ||
						description.contains("OR3 -") ||
						description.contains("OR4 -") ||
						description.contains("S1 -") ||
						description.contains("S2 -") ||
						description.contains("S3 -") ||
						description.contains("S4 -") ||
						description.contains("SG1 -") ||
						description.contains("SG2 -") ||
						description.contains("SG3 -") ||
						description.contains("SM1 -") ||
						description.contains("SM2 -") ||
						description.contains("SM3 -") ||
						description.contains("SM4 -") ||
						description.contains("SS1 -")
					) isFree = false;
				}
			}
		}
		
		//create the bill
		Bill bill = new Bill();
		bill.setPatID(patientSelected.getCode());
		bill.setPatient(true);
		bill.setPatName(patientSelected.getName());
		bill.setStatus("X");
		bill.setUser(MainMenu.getUser());
		bill.setList(true);
		bill.setListID(priceList.getId());
		bill.setListName(priceList.getName());
		bill.setAdmID(admID);
		
		//create the items
		ArrayList<BillItem> billItems = new ArrayList<BillItem>();
		BigDecimal total = new BigDecimal(0);
		for (MedicalWardStatus medical : medItems) {
			Price price = null;
			if (medical.getGiven() > 0) {
				for (Price thisPrice : prices) {
					if (thisPrice.getGroup().equals("MED")) {
						if (thisPrice.getDesc().equals(medical.getMedical().getDescriptionWithCode())) {
							price = thisPrice;
						}
					}
				}
				if (price != null) {
					
					BillItem billItem = new BillItem();
					billItem.setItemDescription(price.getDesc());
					billItem.setItemQuantity((int) medical.getGiven());
					billItem.setPrice(true);
					billItem.setItemAmount(price.getPrice());
					billItem.setPriceID(price.getGroup()+price.getItem());
					
					billItems.add(billItem);
					BigDecimal pricePrice = new BigDecimal(Double.toString(price.getPrice()));
					BigDecimal given = new BigDecimal(Double.toString(medical.getGiven()));
					total = total.add(pricePrice.multiply(given));
				}
			}
		}
		bill.setAmount(total.doubleValue());
		
		if (total.compareTo(new BigDecimal(500)) > 0) {
			int ok = JOptionPane.showConfirmDialog(WardPharmacyNew.this, "The bill is higher than 500 Birr, confirm?");
			if (ok != JOptionPane.OK_OPTION) {
				return null;
			}
		}
		
		if (isStaff) { //if staff exempt all
			
			BillItem billItem = new BillItem();
			billItem.setItemDescription("STAFF");
			billItem.setItemQuantity(1);
			billItem.setPrice(false);
			billItem.setItemAmount(-total.doubleValue());
			billItem.setPriceID("STAFF");
			billItems.add(billItem);
//			bill.setStatus("C");
			
		} else if (isFree) { //if free exempt all
			
			BillItem billItem = new BillItem();
			billItem.setItemDescription("EXEMPT");
			billItem.setItemQuantity(1);
			billItem.setPrice(false);
			billItem.setItemAmount(-total.doubleValue());
			billItem.setPriceID("EXE");
			billItems.add(billItem);
//			bill.setStatus("C");
			
		} else {
			bill.setBalance(total.doubleValue());
		}
		
//		if (total.compareTo(new BigDecimal(0)) == 0)
//			bill.setStatus("C"); //Free drugs
		
		//store the bill
		int billID = billMan.newBill(bill, billItems);
		if (billID > 0) {
			bill.setId(billID);
			return bill;
		} else return null;
	}
	
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText(MessageBundle.getMessage("angal.medicalstockwardedit.Cancel")); //$NON-NLS-1$
			jButtonCancel.setMnemonic(KeyEvent.VK_C);
			jButtonCancel.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
						//to free memory
						medicals.clear();
						diseases.clear();
						opdList.clear();
						prescribers.clear();
						medItems.clear();
						dispose();
				}
			});
		}
		return jButtonCancel;
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.add(getJComboBoxPrescriber());
			jPanelButtons.add(getJButtonOK());
			jPanelButtons.add(getJButtonCancel());
		}
		return jPanelButtons;
	}
	
	private JList getJListOpd() {
		if (jListOpd == null) {
			jListOpd = new JList(listModel);
			jListOpd.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jListOpd.setFont(new Font("Tahoma",Font.PLAIN,14));
		}
		listModel.removeAllElements();
		if (!opdList.isEmpty()) populateJListOpd();
		jListOpd.addListSelectionListener(new ListSelectionListener() {

			@Override
		    public void valueChanged(ListSelectionEvent e)
		    {
	        	if (!e.getValueIsAdjusting()) {
	        		int index = jListOpd.getSelectedIndex();
					if (index >= 0) {
						selectedOpd = opdList.get(jListOpd.getSelectedIndex());
						Disease disease = disMan.getDiseaseByCode(selectedOpd.getDisease());
						jComboBoxDiseaseOpd.setSelectedItem(disease);
						jCheckBoxReattendance.setSelected(selectedOpd.getNewPatient().equals("N") ? false : true);
					}
					//jListOpd.setSelectedIndex(index);
	        	}
		    }
		});
		return jListOpd;
	}

	private JComboBox getJComboBoxDiseaseOpd() {
		if (jComboBoxDiseaseOpd == null) {
			jComboBoxDiseaseOpd = new JComboBox();
		}
		jComboBoxDiseaseOpd.removeAllItems();
		populateDiseasesComboBox(jComboBoxDiseaseOpd, diseases);
		jComboBoxDiseaseOpd.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String disease = String.valueOf(jComboBoxDiseaseOpd.getSelectedItem());
				if (e.getStateChange() == ItemEvent.SELECTED && !disease.startsWith("<") && patientSelected != null) {
					jListOpd.clearSelection();
					selectedOpd = null;
				}
			}
		});
		return jComboBoxDiseaseOpd;
	}

	private JComboBox getJComboBoxDiseaseEmergency() {
		if (jComboBoxDiseaseEmergency == null) {
			jComboBoxDiseaseEmergency = new JComboBox();
		}
		jComboBoxDiseaseEmergency.removeAllItems();
		populateDiseasesComboBox(jComboBoxDiseaseEmergency, diseases);
		return jComboBoxDiseaseEmergency;
	}
	
	private void populateJListOpd() {
		StringBuilder label = new StringBuilder();
		GregorianCalendar today = TimeTools.today(false);
		
		for (Opd opd : opdList) {
			label = new StringBuilder();
			
			if (opd.getVisitDate().compareTo(today) >= 0) 
				label.append("Today");
			else 
				label.append(TimeTools.formatDateTime(opd.getVisitDate(),"yyyy-MM-dd"));
			
			label.append(" - ").append(opd.getDisease());
			listModel.addElement(label.toString());
		}
	}
	
	private void populateDiseasesComboBox(JComboBox comboBox, ArrayList<Disease> diseases) {
		comboBox.addItem("<please select a disease code>");
		for (Disease disease : diseases) {
			if (patientSelected != null) {
				boolean ok = true;
				if (!disease.isMale() && patientSelected.getSex() == 'M') ok = false; //skip female diseases for male patients
				if (!disease.isFemale() && patientSelected.getSex() == 'F') ok = false; //skip male diseases for female patients
				if (disease.getMinimumMonths() != 0 && patientSelected.getMonthsAtDate(new GregorianCalendar()) < disease.getMinimumMonths()) ok = false; //skip diseases for young patients
				if (disease.getMaximumMonths() != 0 && patientSelected.getMonthsAtDate(new GregorianCalendar()) > disease.getMaximumMonths()) ok = false; //skip diseases for old patients
				if (ok) comboBox.addItem(disease);
			} else {
				comboBox.addItem(disease);
			}
		}
	}

	private JRadioButton getJRadioPatient() {
		if (jRadioPatient == null) {
			jRadioPatient = new JRadioButton(MessageBundle.getMessage("angal.medicalstockwardedit.patient"));
			jRadioPatient.setSelected(true);
			jRadioPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					jTextFieldUse.setEnabled(false);
					jTextFieldPatient.setEnabled(true);
					jButtonPickPatient.setEnabled(true);
					if (patientSelected != null) jButtonTrashPatient.setEnabled(true);
					if (GeneralData.FORCEDISEASECODE) {
						jComboBoxDiseaseOpd.setEnabled(true);
						jComboBoxDiseaseEmergency.setEnabled(true);
					}
				}
			});
		}
		return jRadioPatient;
	}

	private JButton getJButtonTrashPatient() {
		if (jButtonTrashPatient == null) {
			jButtonTrashPatient = new JButton();
			jButtonTrashPatient.setMnemonic(KeyEvent.VK_R);
			jButtonTrashPatient.setPreferredSize(new Dimension(25,25));
			jButtonTrashPatient.setIcon(new ImageIcon("rsc/icons/remove_patient_button.png")); //$NON-NLS-1$
			jButtonTrashPatient.setToolTipText(MessageBundle.getMessage("angal.medicalstockwardedit.tooltip.removepatientassociationwiththismovement")); //$NON-NLS-1$
			jButtonTrashPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					
					patientSelected = null;
					jTextFieldPatient.setText(""); //$NON-NLS-1$
					jTextFieldPatient.setEditable(true);
					jButtonPickPatient.setText(MessageBundle.getMessage("angal.medicalstockwardedit.pickpatient"));
					jButtonPickPatient.setToolTipText(MessageBundle.getMessage("angal.medicalstockwardedit.tooltip.associateapatientwiththismovement")); //$NON-NLS-1$
					jButtonTrashPatient.setEnabled(false);
					setOPD(true);
					setIPD(null);
					setEmergency(false);
					getJComboBoxDiseaseOpd();
					getJComboBoxDiseaseEmergency();
				}
			});
			jButtonTrashPatient.setEnabled(false);
		}
		return jButtonTrashPatient;
	}

	private JButton getJButtonPickPatient() {
		if (jButtonPickPatient == null) {
			jButtonPickPatient = new JButton();
			jButtonPickPatient.setText(MessageBundle.getMessage("angal.medicalstockwardedit.pickpatient"));
			jButtonPickPatient.setMnemonic(KeyEvent.VK_P);
			jButtonPickPatient.setIcon(new ImageIcon("rsc/icons/pick_patient_button.png")); //$NON-NLS-1$
			jButtonPickPatient.setToolTipText(MessageBundle.getMessage("angal.medicalstockwardedit.tooltip.associateapatientwiththismovement"));
			jButtonPickPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					SelectPatient sp = new SelectPatient(WardPharmacyNew.this, patientSelected);
					sp.addSelectionListener(WardPharmacyNew.this);
					sp.pack();
					sp.setVisible(true);
				}
			});
		}
		return jButtonPickPatient;
	}

	private JTextField getJTextFieldPatient() {
		if (jTextFieldPatient == null) {
			jTextFieldPatient = new JTextField();
			jTextFieldPatient.setText(""); //$NON-NLS-1$
			jTextFieldPatient.setPreferredSize(PatientDimension);
			jTextFieldPatient.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {}
				
				@Override
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				     if (key == KeyEvent.VK_ENTER) {
				    	 SelectPatient sp = new SelectPatient(WardPharmacyNew.this, jTextFieldPatient.getText());
						sp.addSelectionListener(WardPharmacyNew.this);
						sp.pack();
						sp.setVisible(true);
				     }
				}
			});
		}
		return jTextFieldPatient;
	}
	
	private JCheckBox getJCheckBoxReattendance() {
		if (jCheckBoxReattendance == null) {
			jCheckBoxReattendance = new JCheckBox("Re-Attendance");
		}
		return jCheckBoxReattendance;
	}
	
	private JCheckBox getJCheckBoxOpd() {
		if (jCheckBoxOpd == null) {
			jCheckBoxOpd = new JCheckBox("OPD");
			jCheckBoxOpd.setSelected(true);
		}
		return jCheckBoxOpd;
	}
	
	/**
	 * 
	 */
	private int newOpd(Disease disease) {
		diseaseOPD = disease;
	
		GregorianCalendar now = new GregorianCalendar();
		OpdBrowserManager opdMan = new OpdBrowserManager();
		Opd opd = new Opd(0, ' ', -1, "0", 0,"");

		opd.setNote("");
		opd.setPatientCode(patientSelected.getCode());
		opd.setFullName(patientSelected.getName());
		opd.setNewPatient(jCheckBoxReattendance.isSelected() ? "R" : "N");
		opd.setReferralFrom("");
		opd.setReferralTo("");
		opd.setAge(patientSelected.getAge());
		opd.setSex(patientSelected.getSex());

		opd.setfirstName(patientSelected.getFirstName());
		opd.setsecondName(patientSelected.getSecondName());
		opd.setaddress(patientSelected.getAddress());
		opd.setcity(patientSelected.getCity());
		opd.setnextKin(patientSelected.getNextKin());

		opd.setDisease(disease.getCode());
		opd.setDiseaseType(disease.getType().getCode());
		opd.setDiseaseDesc(disease.getDescription());
		opd.setDiseaseTypeDesc(disease.getType().getDescription());

		opd.setVisitDate(now);
		opd.setYear(opdMan.getProgYear(now.get(Calendar.YEAR)) + 1);
		
		opd.setUser(MainMenu.getUser());

		return opdMan.newOpd(opd);
	}
	
	class MedicalTableModel extends DefaultTableModel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MedicalTableModel() {}
		

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return medClasses[columnIndex];
		}

		public int getColumnCount() {
			return medClasses.length;
		}
		
		public int getRowCount() {
			if (medItems == null)
				return 0;
			return medItems.size();
		}
		
		public Object getValueAt(int r, int c) {
			MedicalWardStatus medWard = medItems.get(r);
			int col = -1;
			if (c == col) {
				return medWard;
			}
			if (c == ++col) {
				return medWard.getMedical().getDescriptionWithCode();
			}
			if (c == ++col) {
				return medWard.getStock(); 
			}
			if (c == ++col) {
				return medWard.getRequested(); 
			}
			if (c == ++col) {
				return medWard.getGiven(); 
			}
			if (c == ++col) {
				return medWard.getMissing(); 
			}
			return null;
		}
		
		public boolean isCellEditable(int r, int c) {
//			if (c == 1) return true;
			return false;
		}
		
		public void setValueAt(Object item, int r, int c) {
//			double value = (Double) item;
//			if (c == 1) {
//				if (value <= qtyArray.get(r))
//					medItems.get(r).setQty((Double)item);
//			}
		}

		public String getColumnName(int columnIndex) {
			return medColumnNames[columnIndex];
		}

	}
	
	/**
	 * @author Mwithi
	 *
	 */
	public class MedicalWardStatus {

		private Medical medical;
		private double stock; //quantity on hand
		private double requested; //quantity on prescription
		private double given; //actual quantity sold
		private double missing; //quantity missing
		private boolean present; //false = missing
		
		/**
		 * @param medical
		 * @param requested
		 */
		public MedicalWardStatus(Medical medical, Double requested) {
			this.medical = medical;
			this.requested = requested;
		}
		
		/**
		 * @return the medical
		 */
		public Medical getMedical() {
			return medical;
		}

		/**
		 * @return the stock
		 */
		public double getStock() {
			return stock;
		}

		/**
		 * @param stock the stock to set
		 */
		public void setStock(double stock) {
			this.stock = stock;
			if (stock > 0) this.present = true;
			else this.present = false;
		}

		/**
		 * @return the present
		 */
		public boolean isPresent() {
			return present;
		}

		/**
		 * @param status the present to set
		 */
		public void setStatus(boolean status) {
			this.present = status;
		}

		/**
		 * @return the requested
		 */
		public double getRequested() {
			return requested;
		}

		/**
		 * @param requested the requested to set
		 */
		public void setRequested(double requested) {
			this.requested = requested;
		}

		/**
		 * @return the given
		 */
		public double getGiven() {
			return given;
		}

		/**
		 * @param given the given to set
		 */
		public void setGiven(double given) {
			this.given = given;
		}

		/**
		 * @return the missing
		 */
		public double getMissing() {
			return missing;
		}

		/**
		 * @param missing the missing to set
		 */
		public void setMissing(double missing) {
			this.missing = missing;
			if (missing == 0) this.present = true;
			else this.present = false;
		}

		/**
		 * @param present the present to set
		 */
		public void setPresent(boolean present) {
			this.present = present;
		}
	}
	
	class StockMedModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<Medical> medList;

		public StockMedModel(ArrayList<Medical> meds) {
			medList = meds;
		}

		public int getRowCount() {
			if (medList == null)
				return 0;
			return medList.size();
		}

		public String getColumnName(int c) {
			if (c == 0) {
				return MessageBundle.getMessage("angal.medicals.code");
			}
			if (c == 1) {
				return MessageBundle.getMessage("angal.medicals.description");
			}
			return "";
		}

		public int getColumnCount() {
			return 2;
		}

		public Object getValueAt(int r, int c) {
			Medical med = medList.get(r);
			if (c == -1) {
				return med;
			} else if (c == 0) {
				return med.getProd_code();
			} else if (c == 1) {
				return med.getDescription();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}
	
	class ColorTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(RIGHT);
			MedicalWardStatus med = medItems.get(row);
			if (!med.isPresent())
				if (column == 3 || column == 4)
					cell.setForeground(Color.RED); // missing
			return cell;
		}
	}
	private JCheckBox getJCheckBoxIpd() {
		if (jCheckBoxIpd == null) {
			jCheckBoxIpd = new JCheckBox("IPD");
		}
		return jCheckBoxIpd;
	}
	private JLabel getJLabelIPDStatus() {
		if (jLabelIPDStatus == null) {
			jLabelIPDStatus = new JLabel("");
		}
		return jLabelIPDStatus;
	}
	private JCheckBox getJCheckBoxEmergency() {
		if (jCheckBoxEmergency == null) {
			jCheckBoxEmergency = new JCheckBox("EMERGENCY");
		}
		return jCheckBoxEmergency;
	}
//	private JCheckBox getJCheckBoxFree() {
//		if (jCheckBoxFree == null) {
//			jCheckBoxFree = new JCheckBox("Free");
//		}
//		return jCheckBoxFree;
//	}
	private JLabel getJLabelEmergencyStatus() {
		if (jLabelEmergencyStatus == null) {
			jLabelEmergencyStatus = new JLabel("");
		}
		return jLabelEmergencyStatus;
	}
	private JComboBox getJComboBoxPrescriber() {
		if (jComboBoxPrescriber == null) {
			jComboBoxPrescriber = new JComboBox();
			jComboBoxPrescriber.addItem("<please select a prescriber>");
			for (Prescriber prs : prescribers) {
				jComboBoxPrescriber.addItem(prs);
			}
		}
		return jComboBoxPrescriber;
	}
	
	static public void main(String[] args) {
		PropertyConfigurator.configure(new File("./rsc/log4j.properties").getAbsolutePath());
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GeneralData.ENHANCEDSEARCH = true;
				SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
				JFrame frame = new JFrame();
				WardBrowserManager wardMan = new WardBrowserManager();
				Ward ward = wardMan.getWard("D");
				ArrayList<MedicalWard> drugs = new ArrayList<MedicalWard>();
				WardPharmacyNew editor = new WardPharmacyNew(frame, ward, drugs);
				editor.setVisible(true);
			}
		});
	}
	
}