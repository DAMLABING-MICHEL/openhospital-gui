package org.isf.opdchronic.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.model.DiseaseType;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.menu.gui.MainMenu;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.opdchronic.manager.OpdChronicManager;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.therapy.gui.ChronicTherapyEntryForm;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.jobjects.BorderedPanel;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.utils.tasks.SwingBackgroundTask;
import org.isf.utils.tasks.SwingBackgroundTaskExecutor;
import org.isf.utils.tasks.SwingUiRenderer;
import org.isf.utils.tasks.WaitingDialog;
import org.isf.utils.time.RememberDates;
import org.isf.visits.model.Visit;
import org.joda.time.DateTime;

/**
 * Created by nicosalvato on 2016-08-18.
 * Contact: nicosalvato@gmail.com
 */
public class OpdChronicEdit extends JDialog implements PatientInsertExtended.PatientListener, PatientInsert.PatientListener {

    // Constants
    private static final String VERSION = "1.0";
    private static final long serialVersionUID = 1L;
    private static final String LAST_OPD_LABEL = "<html><i>" +
            MessageBundle.getMessage("angal.opd.lastopdvisitm") + ":</i></html>";
    private static final String LAST_OPD_NOTE_LABEL = "<html><i>" +
            MessageBundle.getMessage("angal.opd.lastopdnote") + ":</i></html>";
    private static final String LAST_OPD_SCHEDULED_VISIT_LABEL = "<html><i>" +
            MessageBundle.getMessage("angal.opd.scheduledvisit") + "</i></html>:";
    //Components
    private JPanel jPanelMain = null;
    private JPanel jPanelNorth;
    private JPanel jPanelCentral;
    private JPanel jPanelData = null;
    private JPanel jPanelButtons = null;
    private JLabel jLabelDate = null;
    private JLabel jLabelDiseaseType1 = null;
    private JLabel jLabelDisease1 = null;
    private JLabel jLabelDis2 = null;
    private JLabel jLabelDis3 = null;
    private JComboBox diseaseTypeBox = null;
    private JComboBox diseaseBox1 = null;
    private JComboBox diseaseBox2 = null;
    private JComboBox diseaseBox3 = null;
    private JLabel jLabelAge = null;
    private JLabel jLabelSex = null;
    private JDateChooser opdDateFieldCal = null;
    private JButton okButton = null;
    private JButton opdHistoryButton;
    private JButton cancelButton = null;
    private JCheckBox rePatientCheckBox = null;
    private JCheckBox newPatientCheckBox = null;
    private JCheckBox referralToCheckBox = null;
    private JCheckBox referralFromCheckBox = null;
    private JPanel jPanelSex = null;
    private ButtonGroup group=null;
    private JLabel jLabelfirstName = null;
    private JLabel jLabelsecondName = null;
    private JLabel jLabeladdress = null;
    private JLabel jLabelcity = null;
    private JLabel jLabelnextKin = null;
    private JPanel jPanelPatient = null;
    private VoLimitedTextField jFieldFirstName= null;
    private VoLimitedTextField jFieldSecondName= null;
    private VoLimitedTextField jFieldAddress= null;
    private VoLimitedTextField jFieldCity= null;
    private VoLimitedTextField jFieldNextKin= null;
    private VoLimitedTextField jFieldAge = null;
    private VoLimitedTextField patientSearchField;
    private JComboBox patientSearchBox;
    private JLabel jSearchLabel = null;
    private JRadioButton radiof;
    private JRadioButton radiom;
    private JButton jPatientEditButton = null;
    private JButton patientSearchButton = null;
    private JLabel jLabelLastOpdVisit = null;
    private JLabel jFieldLastOpdVisit = null;
    private JLabel jLabelLastOpdScheduledVisit = null;
    private JLabel jFieldLastOpdScheduledVisit = null;
    private JPanel jNotePanel = null;
    private JScrollPane jNoteScrollPane = null;
    private JTextArea jNoteTextArea = null;
    private JPanel jPatientNotePanel = null;
    private JScrollPane jPatientScrollNote = null;
    private JTextArea jPatientNote = null;
    private JPanel jOpdNumberPanel = null;
    private JTextField jOpdNumField = null;
    private JLabel jOpdNumLabel = null;
    private JDateChooser nextVisitDateCal = null;
    // Class variables
    private GregorianCalendar dateOpd = null;
    private DateFormat currentDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);
    private Opd opd;
    private boolean insert;
    private DiseaseType allType= new DiseaseType(MessageBundle.getMessage("angal.opd.alltype"),MessageBundle.getMessage("angal.opd.alltype"));
    private Patient opdPatient = null;
    private JTable therapyTable;
    private JButton deleteTherapyButton;
    private JButton editTherapyButton;
    private TherapyTableModel therapyTableModel;
    private Opd lastOpd;
    // Managers and Arrays
    private DiseaseTypeBrowserManager typeManager = new DiseaseTypeBrowserManager();
    private ArrayList<DiseaseType> types = typeManager.getDiseaseType(true);
    private ArrayList<Disease> diseasesOPD;
    private ArrayList<Disease> diseasesOPDByType;
    private OpdBrowserManager opdManager = new OpdBrowserManager();
    private PatientBrowserManager patBrowser = new PatientBrowserManager();
    private OpdChronicManager chronicManager = OpdChronicManager.getInstance();
    private ArrayList<Patient> patients = new ArrayList<Patient>();
    private Disease lastOPDDisease1;
    private JButton visitButton;
    private JButton therapyButton;
    private DiseaseBrowserManager diseaseManager = new DiseaseBrowserManager();
    private DiseaseTypeBrowserManager diseaseTypeManager = new DiseaseTypeBrowserManager();
	private boolean warningDiseaseDeleted;
    private static String selectPatientMessage = MessageBundle.getMessage("angal.opd.selectapatient");
    private static String newPatientMessage = MessageBundle.getMessage("angal.opd.newpatient");

    public OpdChronicEdit(JFrame owner, Opd opd) {
        super(owner, true);
        this.opd = opd;
        this.insert = opd.getpatientCode() == 0;
        if (!insert) {
            this.opdPatient = patBrowser.getPatient(opd.getpatientCode());
            chronicManager.getTherapies(opd.getCode());
        }
        initialize(owner);
    }

    private void initialize(JFrame owner) {
        this.setContentPane(getMainPanel());
        pack();
        setMinimumSize(this.getSize());
        setLocationRelativeTo(owner);
        setWindowTitle(insert);
        if (insert) {
            patientSearchField.requestFocusInWindow();
        } else {
            jNoteTextArea.requestFocusInWindow();
        }
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        setPatient();
        enableComponents();
        setLastChronicOpd();
        patientSearchButton.addActionListener(new SearchPatientListener());
        patientSearchBox.addActionListener(new PatientSearchBoxListener(selectPatientMessage, newPatientMessage));
    }

    private void setWindowTitle(boolean insert) {
        if (insert) {
            this.setTitle(MessageBundle.getMessage("angal.opd.newchronicopdregistration") + " ("+VERSION+")");
        } else {
            this.setTitle(MessageBundle.getMessage("angal.opd.editchronicopdregistration") + " ("+VERSION+")");
        }
    }

    private void closeWindow() {
        clearMemory();
        chronicManager.clearCurrentVisit();
        chronicManager.clearTherapies();
        dispose();
    }

    private void clearMemory() {
        if (patients != null) patients.clear();
        if (diseasesOPD != null) diseasesOPD.clear();
        if (diseasesOPDByType != null) diseasesOPDByType.clear();
        types.clear();
        patientSearchBox.removeAllItems();
        diseaseTypeBox.removeAllItems();
        diseaseBox1.removeAllItems();
        diseaseBox2.removeAllItems();
        diseaseBox3.removeAllItems();
        chronicManager.clearState();
    }

    private ArrayList<Disease> diseasesAll;
    private void getDiseases() {
        if (opdPatient != null) {
        	diseasesAll = diseaseManager.getDiseaseAll();
            DiseaseType diseaseType = diseaseTypeBox.getSelectedItem().equals(allType) ?
                    null : (DiseaseType) diseaseTypeBox.getSelectedItem();
            if (diseasesOPD != null)
                diseasesOPD.clear();
            diseasesOPD = diseaseManager.getDiseaseOpd(opdPatient);
            if (diseasesOPDByType != null)
                diseasesOPDByType.clear();
            // The first disease combo box provides just chronic diseases
            diseasesOPDByType = diseaseManager.getDiseaseOpd(opdPatient, diseaseType, true);
        }
    }

    private void setPatient() {
        if (opdPatient != null) {
            chronicManager.clearCurrentVisit();
            jFieldAge.setText(String.valueOf(opdPatient.getFormattedAge()));
            jFieldFirstName.setText(opdPatient.getFirstName());
            jFieldAddress.setText(opdPatient.getAddress());
            jFieldCity.setText(opdPatient.getCity());
            jFieldSecondName.setText(opdPatient.getSecondName());
            jFieldNextKin.setText(opdPatient.getNextKin());
            jPatientNote.setText(opdPatient.getNote());
            BorderedPanel.setMyMatteBorder(jPanelPatient, MessageBundle.getMessage("angal.opd.patient") + " (code: " + opdPatient.getCode() + ")");
            radiom.setSelected(opdPatient.getSex() == 'M');
            radiof.setSelected(opdPatient.getSex() == 'F');
            jOpdNumField.setText(opdPatient.getPreviousCode());
            getDiseases();
            getDiseaseBox();
            getDiseaseBox2();
            getDiseaseBox3();
            enableComponents();
        }
    }

    private void resetPatient() {
        if (jPanelPatient != null) {
            jFieldAge.setText("");
            jFieldFirstName.setText("");
            jFieldAddress.setText("");
            jFieldCity.setText("");
            jFieldSecondName.setText("");
            jFieldNextKin.setText("");
            jPatientNote.setText("");
            BorderedPanel.setMyMatteBorder(jPanelPatient, MessageBundle.getMessage("angal.opd.patient"));
        }
        opdPatient = null;
        getPatientSearchBox();
        jOpdNumField.setText("");
        radiom.setSelected(true);
        enableComponents();
    }

    private JButton getOpdHistoryButton() {
        if (opdHistoryButton == null) {
            opdHistoryButton = new JButton("History");
            opdHistoryButton.setEnabled(false);
            opdHistoryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    OpdChronicHistoryPanel opdChronicHistoryPanel = new OpdChronicHistoryPanel(
                            OpdChronicEdit.this,
                            opdPatient.getCode(),
                            opdDateFieldCal.getDate());
                    opdChronicHistoryPanel.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    opdChronicHistoryPanel.setVisible(true);
                }
            });
        }
        return opdHistoryButton;
    }

    private void setLastChronicOpd() {
        if (opdPatient != null) {
            lastOpd = chronicManager.getLastChronicOpd(opdPatient.getCode(), opdDateFieldCal.getDate());
            opdHistoryButton.setEnabled(lastOpd != null);
            if (lastOpd == null) {
                newPatientCheckBox.setSelected(true);
                rePatientCheckBox.setSelected(false);
                clearLastOpdFields();
            } else {
                lastOPDDisease1 = lastOpd.getDisease() != null ?
                        diseaseManager.getDiseaseByCode(lastOpd.getDisease()) : null;
                Disease lastOPDDisease2 = lastOpd.getDisease2() != null ?
                        diseaseManager.getDiseaseByCode(lastOpd.getDisease2()) : null;
                Disease lastOPDDisease3 = lastOpd.getDisease3() != null ?
                        diseaseManager.getDiseaseByCode(lastOpd.getDisease3()) : null;

                StringBuilder lastOPDDisease = new StringBuilder();
                lastOPDDisease.append(currentDateFormat.format(lastOpd.getVisitDate().getTime())).append(" (");
                if (lastOPDDisease1 != null) {
                    setAttendance();
                    lastOPDDisease.append(lastOPDDisease1.getDescription());
                }
                if (lastOPDDisease2 != null) lastOPDDisease.append(", ").append(lastOPDDisease2.getDescription());
                if (lastOPDDisease3 != null) lastOPDDisease.append(", ").append(lastOPDDisease3.getDescription());
                lastOPDDisease.append(")");

                jLabelLastOpdVisit.setText(LAST_OPD_LABEL);
                jFieldLastOpdVisit.setText(lastOPDDisease.toString());
                jLabelLastOpdScheduledVisit.setText(LAST_OPD_SCHEDULED_VISIT_LABEL);
                String dateInfo = "";
                if (lastOpd.getNextVisit() != null) {
                    dateInfo += currentDateFormat.format(lastOpd.getNextVisit().getDate().getTime()) + "";
                    if (opdDateFieldCal.getDate() != null)
                        dateInfo += " (" + chronicManager.onSchedule(new DateTime(lastOpd.getNextVisit().getDate().getTime()),
                                new DateTime(opdDateFieldCal.getDate())) + ")";
                    jFieldLastOpdScheduledVisit.setText(dateInfo);
                }
            }
        }
    }

    private void setAttendance() {
        Object selectedObject = diseaseBox1.getSelectedItem();
        if (selectedObject instanceof Disease) {
            Disease disease = (Disease) selectedObject;
            boolean isReAttended = lastOPDDisease1 != null && disease.getCode().equals(lastOPDDisease1.getCode());
            rePatientCheckBox.setSelected(isReAttended);
            newPatientCheckBox.setSelected(!isReAttended);
        }
    }

    private JPanel getJPanelNorth() {
        if (jPanelNorth == null) {
            String referralTo, referralFrom;
            jPanelNorth = new JPanel(new FlowLayout());
            rePatientCheckBox = new JCheckBox(MessageBundle.getMessage("angal.opd.reattendance"));
            newPatientCheckBox = new JCheckBox(MessageBundle.getMessage("angal.opd.newattendance"));
            newPatientCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    rePatientCheckBox.setSelected(!newPatientCheckBox.isSelected());
                }
            });
            rePatientCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    newPatientCheckBox.setSelected(!rePatientCheckBox.isSelected());
                }
            });
            jPanelNorth.add(rePatientCheckBox);
            jPanelNorth.add(newPatientCheckBox);
            referralFromCheckBox = new JCheckBox(MessageBundle.getMessage("angal.opd.referral.from"));
            jPanelNorth.add(referralFromCheckBox);
            referralToCheckBox = new JCheckBox(MessageBundle.getMessage("angal.opd.referral.to"));
            jPanelNorth.add(referralToCheckBox);
            if (!insert){
                boolean isNewPatient = opd.getNewPatient().equals("N");
                newPatientCheckBox.setSelected(isNewPatient);
                rePatientCheckBox.setSelected(!isNewPatient);
                referralFrom = opd.getReferralFrom() != null ? opd.getReferralFrom() : "";
                referralFromCheckBox.setSelected(referralFrom.equals("R"));
                referralTo = opd.getReferralTo() != null ? opd.getReferralTo() : "";
                referralToCheckBox.setSelected(referralTo.equals("R"));
            }
        }
        return jPanelNorth;
    }

    private JPanel getCentralPanel() {
        if (jPanelCentral == null) {
            jPanelCentral = new JPanel();
            jPanelCentral.setLayout(new BoxLayout(jPanelCentral, BoxLayout.Y_AXIS));
            jPanelCentral.add(getDataPanel());
            jPanelCentral.add(Box.createVerticalStrut(10));
            jPanelCentral.add(getJPanelPatient());
        }
        return jPanelCentral;
    }

    private JPanel getMainPanel() {
        if (jPanelMain == null) {
            jPanelMain = new JPanel();
            jPanelMain.setLayout(new BorderLayout());
            jPanelMain.add(getJPanelNorth(), BorderLayout.NORTH);
            jPanelMain.add(getEastPanel(), BorderLayout.EAST);
            jPanelMain.add(getCentralPanel(), BorderLayout.CENTER);
            jPanelMain.add(getJButtonPanel(), BorderLayout.SOUTH);
        }
        jPanelMain.setSize(new Dimension(700, 500));
        return jPanelMain;
    }

    private JPanel getDataPanel() {
        if (jPanelData == null) {
            jPanelData = new JPanel();
        }
        GridBagLayout gbl_jPanelData = new GridBagLayout();
        gbl_jPanelData.columnWidths = new int[] {80, 40, 20, 80, 20};
        gbl_jPanelData.rowHeights = new int[] {20, 20, 20, 20, 20, 20, 20, 20};
        gbl_jPanelData.columnWeights = new double[] {0.0, 0.1, 0.0, 1.0, 0.0};
        gbl_jPanelData.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        jPanelData.setLayout(gbl_jPanelData);
        // Attendance date
        jLabelDate= new JLabel(MessageBundle.getMessage("angal.opd.attendancedate"));
        GridBagConstraints gbc_jLabelDate = new GridBagConstraints();
        gbc_jLabelDate.fill = GridBagConstraints.VERTICAL;
        gbc_jLabelDate.anchor = GridBagConstraints.WEST;
        gbc_jLabelDate.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelDate.gridy = 0;
        gbc_jLabelDate.gridx = 0;
        jPanelData.add(jLabelDate, gbc_jLabelDate);
        GridBagConstraints gbc_DateFieldCal = new GridBagConstraints();
        gbc_DateFieldCal.fill = GridBagConstraints.HORIZONTAL;
        gbc_DateFieldCal.insets = new Insets(5, 5, 5, 5);
        gbc_DateFieldCal.gridy = 0;
        gbc_DateFieldCal.gridx = 1;
        jPanelData.add(getOpdDateFieldCal(), gbc_DateFieldCal);
        // Opd Number
        GridBagConstraints gbc_jOpdNumberPanel = new GridBagConstraints();
        gbc_jOpdNumberPanel.gridwidth = 1;
        gbc_jOpdNumberPanel.fill = GridBagConstraints.BOTH;
        gbc_jOpdNumberPanel.insets = new Insets(5, 5, 5, 5);
        gbc_jOpdNumberPanel.gridy = 0;
        gbc_jOpdNumberPanel.gridx = 3;
        jPanelData.add(getJOpdNumberPanel(), gbc_jOpdNumberPanel);
        // Patient Search
        jSearchLabel = new JLabel(MessageBundle.getMessage("angal.opd.search"));
        GridBagConstraints gbc_jSearchLabel = new GridBagConstraints();
        gbc_jSearchLabel.fill = GridBagConstraints.VERTICAL;
        gbc_jSearchLabel.anchor = GridBagConstraints.WEST;
        gbc_jSearchLabel.insets = new Insets(5, 5, 5, 5);
        gbc_jSearchLabel.gridy = 1;
        gbc_jSearchLabel.gridx = 0;
        jPanelData.add(jSearchLabel, gbc_jSearchLabel);
        GridBagConstraints gbc_jTextPatientSrc = new GridBagConstraints();
        gbc_jTextPatientSrc.fill = GridBagConstraints.HORIZONTAL;
        gbc_jTextPatientSrc.insets = new Insets(5, 5, 5, 5);
        gbc_jTextPatientSrc.gridy = 1;
        gbc_jTextPatientSrc.gridx = 1;
        jPanelData.add(getPatientSearchField(), gbc_jTextPatientSrc);
        GridBagConstraints gbc_jSearchButton = new GridBagConstraints();
        gbc_jSearchButton.insets = new Insets(5, 5, 5, 5);
        gbc_jSearchButton.gridy = 1;
        gbc_jSearchButton.gridx = 2;
        jPanelData.add(getPatientSearchButton(), gbc_jSearchButton);
        GridBagConstraints gbc_jSearchBox = new GridBagConstraints();
        gbc_jSearchBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_jSearchBox.insets = new Insets(5, 5, 5, 5);
        gbc_jSearchBox.gridy = 1;
        gbc_jSearchBox.gridx = 3;
        jPanelData.add(getPatientSearchBox(), gbc_jSearchBox);
        GridBagConstraints gbc_jPatientEditButton = new GridBagConstraints();
        gbc_jPatientEditButton.insets = new Insets(5, 5, 5, 5);
        gbc_jPatientEditButton.gridy = 1;
        gbc_jPatientEditButton.gridx = 4;
        jPanelData.add(getJPatientEditButton(), gbc_jPatientEditButton);

        jLabelDiseaseType1 = new JLabel(MessageBundle.getMessage("angal.opd.diseasetype"));
        GridBagConstraints gbc_jLabelDiseaseType1 = new GridBagConstraints();
        gbc_jLabelDiseaseType1.fill = GridBagConstraints.VERTICAL;
        gbc_jLabelDiseaseType1.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelDiseaseType1.anchor = GridBagConstraints.WEST;
        gbc_jLabelDiseaseType1.gridy = 2;
        gbc_jLabelDiseaseType1.gridx = 0;
        jPanelData.add(jLabelDiseaseType1, gbc_jLabelDiseaseType1);
        GridBagConstraints gbc_jLabelDiseaseTypeBox = new GridBagConstraints();
        gbc_jLabelDiseaseTypeBox.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelDiseaseTypeBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_jLabelDiseaseTypeBox.gridwidth = 4;
        gbc_jLabelDiseaseTypeBox.gridy = 2;
        gbc_jLabelDiseaseTypeBox.gridx = 1;
        jPanelData.add(getDiseaseTypeBox(), gbc_jLabelDiseaseTypeBox);

        jLabelDisease1 = new JLabel(MessageBundle.getMessage("angal.opd.diagnosis"));
        GridBagConstraints gbc_jLabelDisease1 = new GridBagConstraints();
        gbc_jLabelDisease1.fill = GridBagConstraints.VERTICAL;
        gbc_jLabelDisease1.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelDisease1.anchor = GridBagConstraints.WEST;
        gbc_jLabelDisease1.gridy = 3;
        gbc_jLabelDisease1.gridx = 0;
        jPanelData.add(jLabelDisease1, gbc_jLabelDisease1);
        GridBagConstraints gbc_jLabelDiseaseBox = new GridBagConstraints();
        gbc_jLabelDiseaseBox.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelDiseaseBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_jLabelDiseaseBox.gridwidth = 4;
        gbc_jLabelDiseaseBox.gridy = 3;
        gbc_jLabelDiseaseBox.gridx = 1;
        jPanelData.add(getDiseaseBox(), gbc_jLabelDiseaseBox);

        jLabelDis2 = new JLabel(MessageBundle.getMessage("angal.opd.diagnosisnfulllist"));
        GridBagConstraints gbc_jLabelDis2 = new GridBagConstraints();
        gbc_jLabelDis2.fill = GridBagConstraints.VERTICAL;
        gbc_jLabelDis2.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelDis2.anchor = GridBagConstraints.WEST;
        gbc_jLabelDis2.gridy = 4;
        gbc_jLabelDis2.gridx = 0;
        jPanelData.add(jLabelDis2, gbc_jLabelDis2);
        GridBagConstraints gbc_jLabelDisBox2 = new GridBagConstraints();
        gbc_jLabelDisBox2.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelDisBox2.fill = GridBagConstraints.HORIZONTAL;
        gbc_jLabelDisBox2.gridwidth = 4;
        gbc_jLabelDisBox2.gridy = 4;
        gbc_jLabelDisBox2.gridx = 1;
        jPanelData.add(getDiseaseBox2(), gbc_jLabelDisBox2);

        jLabelDis3 = new JLabel(MessageBundle.getMessage("angal.opd.diagnosisnfulllist3"));
        GridBagConstraints gbc_jLabelDis3 = new GridBagConstraints();
        gbc_jLabelDis3.fill = GridBagConstraints.VERTICAL;
        gbc_jLabelDis3.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelDis3.anchor = GridBagConstraints.WEST;
        gbc_jLabelDis3.gridy = 5;
        gbc_jLabelDis3.gridx = 0;
        jPanelData.add(jLabelDis3, gbc_jLabelDis3);
        GridBagConstraints gbc_jLabelDisBox3 = new GridBagConstraints();
        gbc_jLabelDisBox3.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelDisBox3.fill = GridBagConstraints.HORIZONTAL;
        gbc_jLabelDisBox3.gridwidth = 4;
        gbc_jLabelDisBox3.gridy = 5;
        gbc_jLabelDisBox3.gridx = 1;
        jPanelData.add(getDiseaseBox3(), gbc_jLabelDisBox3);

        jLabelLastOpdVisit = new JLabel(" ");
        jLabelLastOpdVisit.setForeground(Color.RED);
        GridBagConstraints gbc_jLabelLastOpdVisit = new GridBagConstraints();
        gbc_jLabelLastOpdVisit.fill = GridBagConstraints.VERTICAL;
        gbc_jLabelLastOpdVisit.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelLastOpdVisit.anchor = GridBagConstraints.EAST;
        gbc_jLabelLastOpdVisit.gridy = 6;
        gbc_jLabelLastOpdVisit.gridx = 0;
        jPanelData.add(jLabelLastOpdVisit, gbc_jLabelLastOpdVisit);
        jFieldLastOpdVisit = new JLabel(" ");
        jFieldLastOpdVisit.setFocusable(false);
        GridBagConstraints gbc_jFieldLastOpdVisit = new GridBagConstraints();
        gbc_jFieldLastOpdVisit.insets = new Insets(5, 5, 5, 5);
        gbc_jFieldLastOpdVisit.fill = GridBagConstraints.HORIZONTAL;
        gbc_jFieldLastOpdVisit.gridwidth = 4;
        gbc_jFieldLastOpdVisit.gridy = 6;
        gbc_jFieldLastOpdVisit.gridx = 1;
        jPanelData.add(jFieldLastOpdVisit, gbc_jFieldLastOpdVisit);

        jLabelLastOpdScheduledVisit = new JLabel(" ");
        jLabelLastOpdScheduledVisit.setForeground(Color.RED);
        GridBagConstraints gbc_jLabelLastOpdScheduledVisit = new GridBagConstraints();
        gbc_jLabelLastOpdScheduledVisit.fill = GridBagConstraints.VERTICAL;
        gbc_jLabelLastOpdScheduledVisit.insets = new Insets(5, 5, 5, 5);
        gbc_jLabelLastOpdScheduledVisit.anchor = GridBagConstraints.EAST;
        gbc_jLabelLastOpdScheduledVisit.gridy = 7;
        gbc_jLabelLastOpdScheduledVisit.gridx = 0;
        jPanelData.add(jLabelLastOpdScheduledVisit, gbc_jLabelLastOpdScheduledVisit);
        jFieldLastOpdScheduledVisit = new JLabel(" ");
        jFieldLastOpdScheduledVisit.setFocusable(false);
        GridBagConstraints gbc_jFieldLastOpdScheduledVisit = new GridBagConstraints();
        gbc_jFieldLastOpdScheduledVisit.insets = new Insets(5, 5, 5, 5);
        gbc_jFieldLastOpdScheduledVisit.fill = GridBagConstraints.HORIZONTAL;
        gbc_jFieldLastOpdScheduledVisit.gridwidth = 4;
        gbc_jFieldLastOpdScheduledVisit.gridy = 7;
        gbc_jFieldLastOpdScheduledVisit.gridx = 1;
        jPanelData.add(jFieldLastOpdScheduledVisit, gbc_jFieldLastOpdScheduledVisit);
        return jPanelData;
    }

    private JDateChooser getOpdDateFieldCal() {
        if (opdDateFieldCal == null) {
            String d = "";
            java.util.Date myDate;
            if (insert) {
                if (RememberDates.getLastOpdVisitDate() == null)
                    dateOpd = new GregorianCalendar();
                else
                    dateOpd = RememberDates.getLastOpdVisitDateGregorian();
            }
            else {
                dateOpd = opd.getVisitDate();
            }
            if (dateOpd != null) {
                myDate = dateOpd.getTime();
                d = currentDateFormat.format(myDate);
            }
            try {
                opdDateFieldCal = new JDateChooser(currentDateFormat.parse(d), "dd/MM/yy");
                opdDateFieldCal.setLocale(new Locale(GeneralData.LANGUAGE));
                opdDateFieldCal.setDateFormatString("dd/MM/yy");
                opdDateFieldCal.setMaxSelectableDate(new Date());
                opdDateFieldCal.addPropertyChangeListener("date", new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        Date eventDate = (Date) evt.getNewValue();
                        if (opdPatient != null && eventDate.before(opdPatient.getBirthDate())) {
                            JOptionPane.showMessageDialog(OpdChronicEdit.this, MessageBundle.getMessage("angal.opd.patientnotyetborn"));
                            opdDateFieldCal.setDate((Date) evt.getOldValue());
                            return;
                        }
                        if (eventDate.after(new Date())) {
                            JOptionPane.showMessageDialog(OpdChronicEdit.this, MessageBundle.getMessage("angal.opd.futuredatenotallowed"));
                            opdDateFieldCal.setDate((Date) evt.getOldValue());
                            return;
                        }
                        opdDateFieldCal.setDate(eventDate);
                        dateOpd.setTime(eventDate);
                        setLastChronicOpd();
                        getNextVisitDateCal();
                    }
                });
            } catch (ParseException e) {
                // Do nothing
            }
            opdDateFieldCal.setPreferredSize(new Dimension(10,24));
        }
        return opdDateFieldCal;
    }

    private JPanel getNextVisitPanel() {
        JPanel panel = new JPanel();
        panel = BorderedPanel.setMyBorder(panel, "Next Visit");
        panel.setLayout(new BorderLayout(0, 0));
        panel.setPreferredSize(new Dimension(300, 60));
        panel.add(getNextVisitDateCal());
        return panel;
    }

    private JDateChooser getNextVisitDateCal() {
        if (nextVisitDateCal == null) {
            nextVisitDateCal = new JDateChooser();
            nextVisitDateCal.setLocale(new Locale(GeneralData.LANGUAGE));
            nextVisitDateCal.setDateFormatString("dd/MM/yy");
            Date minDate = !insert && opd.getVisitDate() != null ? opd.getVisitDate().getTime() : new Date();
            nextVisitDateCal.setMinSelectableDate(minDate);
        }
        Date nextVisitDate = chronicManager.getNextVisitDate(opd);
        nextVisitDateCal.setDate(nextVisitDate);
        return nextVisitDateCal;
    }

    private void clearAllOpdFields() {
        diseaseTypeBox.setSelectedItem(allType);
        diseaseBox1.setSelectedItem(null);
        diseaseBox2.setSelectedItem(null);
        diseaseBox3.setSelectedItem(null);
        jNoteTextArea.setText("");
        chronicManager.clearTherapies();
        therapyTableModel.fireTableDataChanged();
        therapyTable.updateUI();
        selectedDiseases.clear();
    }

    private JPanel getJOpdNumberPanel() {
        if (jOpdNumberPanel == null) {
            jOpdNumberPanel = new JPanel();

            jOpdNumLabel = new JLabel();
            jOpdNumLabel.setText(MessageBundle.getMessage("angal.opd.opdnumber"));

            jOpdNumField = new JTextField(10);
            jOpdNumField.setEditable(false);
            jOpdNumField.setFocusable(false);
            jOpdNumField.setText(getOpdNum());

            jOpdNumberPanel.add(jOpdNumLabel);
            jOpdNumberPanel.add(jOpdNumField);
        }
        return jOpdNumberPanel;
    }

    private String getOpdNum() {
        if (!insert)
            return "" + opd.getYear();
        GregorianCalendar date = new GregorianCalendar();
        opd.setYear(opdManager.getProgYear(date.get(Calendar.YEAR))+1);
        return "";
    }

    private JPanel getEastPanel() {
        JPanel eastPanel = new JPanel();
        GridBagLayout gbl_eastPanel = new GridBagLayout();
        eastPanel.setLayout(gbl_eastPanel);
        GridBagConstraints gbc_nextVisitPanel = new GridBagConstraints();
        gbc_nextVisitPanel.fill = GridBagConstraints.VERTICAL;
        gbc_nextVisitPanel.anchor = GridBagConstraints.WEST;
        gbc_nextVisitPanel.gridy = 0;
        gbc_nextVisitPanel.gridx = 0;
        eastPanel.add(getNextVisitPanel(), gbc_nextVisitPanel);
        GridBagConstraints gbc_notePanel = new GridBagConstraints();
        gbc_notePanel.fill = GridBagConstraints.VERTICAL;
        gbc_notePanel.anchor = GridBagConstraints.WEST;
        gbc_notePanel.gridy = 1;
        gbc_notePanel.gridx = 0;
        eastPanel.add(getJNotePanel(), gbc_notePanel);
        GridBagConstraints gbc_therapyPanel = new GridBagConstraints();
        gbc_therapyPanel.fill = GridBagConstraints.VERTICAL;
        gbc_therapyPanel.anchor = GridBagConstraints.WEST;
        gbc_therapyPanel.gridy = 2;
        gbc_therapyPanel.gridx = 0;
        eastPanel.add(getTherapyPanel(), gbc_therapyPanel);
        return eastPanel;
    }

    private JPanel getJNotePanel() {
        if (jNotePanel == null) {
            jNotePanel = new JPanel();
            jNotePanel = BorderedPanel.setMyBorder(jNotePanel, MessageBundle.getMessage("angal.opd.noteandsymptom"));
            jNoteScrollPane = new JScrollPane(getOpdNotesTextArea());
            jNoteScrollPane.setVerticalScrollBar(new JScrollBar());
            jNoteScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jNoteScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            jNoteScrollPane.validate();
            jNoteScrollPane.setPreferredSize(new Dimension(300, 230));
            jNotePanel.setLayout(new BorderLayout(0, 0));
            jNotePanel.setPreferredSize(new Dimension(300, 270));
            jNotePanel.add(jNoteScrollPane);
        }
        return jNotePanel;
    }

    private JPanel getTherapyPanel() {
        JPanel therapyPanel = new JPanel();
        therapyPanel.setLayout(new BoxLayout(therapyPanel, BoxLayout.Y_AXIS));
        BorderedPanel.setMyBorder(therapyPanel, "Therapy");
        therapyPanel.add(getTherapyTablePanel());
        therapyPanel.add(getTableButtonsPanel());
        return therapyPanel;
    }

    private JScrollPane getTherapyTablePanel() {
        JScrollPane therapyPanel = new JScrollPane();
        therapyPanel.setPreferredSize(new Dimension(285, 270));
        therapyPanel.setViewportView(getTherapyTable());
        return therapyPanel;
    }

    private JPanel getTableButtonsPanel() {
        JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableButtonsPanel.add(getEditTherapyButton());
        tableButtonsPanel.add(getDeleteTherapyButton());
        return tableButtonsPanel;
    }

    private JButton getEditTherapyButton() {
        editTherapyButton = new JButton("Edit");
        editTherapyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TherapyRow therapy = therapyTableModel.getSelectedTherapy(therapyTable.getSelectedRow());
                openChronicTherapyForm(
                        OpdChronicEdit.this,
                        opdPatient.getCode(),
                        opd.getCode(),
                        therapy);
            }
        });
        editTherapyButton.setEnabled(false);
        return editTherapyButton;
    }

    private JButton getDeleteTherapyButton() {
        deleteTherapyButton = new JButton("Delete");
        deleteTherapyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TherapyRow therapy = therapyTableModel.getSelectedTherapy(therapyTable.getSelectedRow());
                chronicManager.removeTherapy(therapy);
                therapyTableModel.fireTableDataChanged();
            }
        });
        deleteTherapyButton.setEnabled(false);
        return deleteTherapyButton;
    }

    private JTable getTherapyTable() {
        if (therapyTable == null) {
            therapyTable = new JTable();
            therapyTableModel = new TherapyTableModel();
            therapyTable.setModel(therapyTableModel);
            therapyTable.setRowHeight(24);
            therapyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            ListSelectionModel listSelectionModel = therapyTable.getSelectionModel();
            listSelectionModel.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    editTherapyButton.setEnabled(!lsm.isSelectionEmpty());
                    deleteTherapyButton.setEnabled(!lsm.isSelectionEmpty());
                }
            });
        }
        return therapyTable;
    }

    class TherapyTableModel extends DefaultTableModel {

        private static final long serialVersionUID = 1L;
        private final Class<?>[] COL_CLASSES = { Medical.class };
        private final String[] COL_NAMES = { MessageBundle.getMessage("angal.medicalstockward.medical") };
        private ArrayList<TherapyRow> therapies;

        public TherapyTableModel() {
            super();
            this.therapies = chronicManager.getTherapies();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return COL_CLASSES[columnIndex];
        }

        public int getColumnCount() {
            return COL_CLASSES.length;
        }

        public int getRowCount() {
            return therapies != null ? therapies.size() : 0;
        }

        public Object getValueAt(int r, int c) {
            TherapyRow therapy = therapies.get(r);
            switch(c) {
                case 0:
                    return therapy.getMedical().getDescription();
                default:
                    return null;
            }
        }

        public TherapyRow getSelectedTherapy(int r) {
            return therapies.get(r);
        }

        public boolean isCellEditable(int r, int c) {
            return false;
        }

        public void setValueAt(Object item, int r, int c) {}

        public String getColumnName(int columnIndex) {
            return COL_NAMES[columnIndex];
        }
    }

    private JTextArea getOpdNotesTextArea() {
        if (jNoteTextArea == null) {
            jNoteTextArea = new JTextArea(15, 20);
            jNoteTextArea.setAutoscrolls(true);
            jNoteTextArea.setWrapStyleWord(true);
            jNoteTextArea.setLineWrap(true);
            if (!insert)
                jNoteTextArea.setText(opd.getNote());
        }
        return jNoteTextArea;
    }

    private JComboBox getDiseaseTypeBox() {
        if (diseaseTypeBox == null) {
            diseaseTypeBox = new JComboBox();
            types.add(0, allType);
        }
        diseaseTypeBox.setMaximumSize(new Dimension(400, 50));
        diseaseTypeBox.setModel(new DefaultComboBoxModel(types.toArray()));
        if (opd.getDiseaseType() != null)
            diseaseTypeBox.setSelectedItem(diseaseTypeManager.getDiseaseTypeByCode(opd.getDiseaseType()));
        else
            diseaseTypeBox.setSelectedIndex(0);
        diseaseTypeBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    getDiseases();
                    getDiseaseBox();
                }
            }
        });
        return diseaseTypeBox;
    }

    private JComboBox getDiseaseBox() {
        if (diseaseBox1 == null) {
            diseaseBox1 = new JComboBox();
            diseaseBox1.setMaximumSize(new Dimension(400, 50));
            diseaseBox1.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
            diseaseBox1.addItemListener(db1listener);
        }
        diseaseBox1.removeAllItems();
        diseaseBox1.addItem("");

        if (diseasesOPDByType != null && diseasesOPDByType.size() > 0) {
        	Disease elem2 = null;
        	for (Disease elem : diseasesOPDByType) {
        		diseaseBox1.addItem(elem);
    			if(!insert && opd.getDisease() != null){
    				if(opd.getDisease().equals(elem.getCode())){
    					elem2 = elem;}
    			}
    		}
        
	        if (!insert) {
				if (elem2 != null) {
					diseaseBox1.setSelectedItem(elem2);
				} else { //try in the cancelled diseases
					if (opd.getDisease() != null) {
						for (Disease elem : diseasesAll) {
							if (opd.getDisease().compareTo(elem.getCode()) == 0) {
								if (!warningDiseaseDeleted) JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.disease1mayhavebeencanceled"));
								diseaseBox1.addItem(elem);
								diseaseBox1.setSelectedItem(elem);
								warningDiseaseDeleted = true;
							}
						}
					}
				}
			}
        }
        diseaseBox1.setEnabled(opdPatient != null);
        return diseaseBox1;
    }

    private JComboBox getDiseaseBox2() {
        if (diseaseBox2 == null) {
            diseaseBox2 = new JComboBox();
            diseaseBox2.setMaximumSize(new Dimension(400, 50));
            diseaseBox2.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
            diseaseBox2.addItemListener(db2listener);
        }
        diseaseBox2.removeItemListener(db2listener);
        diseaseBox2.removeAllItems();
        diseaseBox2.addItem("");
        
        if (diseasesOPD != null && diseasesOPD.size() > 0) {
	        Disease elem2 = null;
	    	for (Disease elem : diseasesOPD) {
	    		diseaseBox2.addItem(elem);
				if(!insert && opd.getDisease2() != null){
					if(opd.getDisease2().equals(elem.getCode())){
						elem2 = elem;}
				}
			}
	        
	        if (!insert) {
				if (elem2 != null) {
					diseaseBox2.setSelectedItem(elem2);
				} else { //try in the cancelled diseases
					if (opd.getDisease2() != null) {
						for (Disease elem : diseasesAll) {
							if (opd.getDisease2().compareTo(elem.getCode()) == 0) {
								if (!warningDiseaseDeleted) JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.disease2mayhavebeencanceled"));
								diseaseBox2.addItem(elem);
								diseaseBox2.setSelectedItem(elem);
								warningDiseaseDeleted = true;
							}
						}
					}
				}
			}
        }
        diseaseBox2.addItemListener(db2listener);
        diseaseBox2.setEnabled(opdPatient != null);
        return diseaseBox2;
    }

    private JComboBox getDiseaseBox3() {
        if (diseaseBox3 == null) {
            diseaseBox3 = new JComboBox();
            diseaseBox3.setMaximumSize(new Dimension(400, 50));
            diseaseBox3.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
            diseaseBox3.addItemListener(db3listener);
        }
        diseaseBox3.removeItemListener(db3listener);
        diseaseBox3.removeAllItems();
        diseaseBox3.addItem("");

        if (diseasesOPD != null && diseasesOPD.size() > 0) {
	        Disease elem2 = null;
	    	for (Disease elem : diseasesOPD) {
	    		diseaseBox3.addItem(elem);
				if(!insert && opd.getDisease3() != null){
					if(opd.getDisease3().equals(elem.getCode())){
						elem2 = elem;}
				}
			}
	        
	        if (!insert) {
				if (elem2 != null) {
					diseaseBox2.setSelectedItem(elem2);
				} else { //try in the cancelled diseases
					if (opd.getDisease3() != null) {
						for (Disease elem : diseasesAll) {
							if (opd.getDisease3().compareTo(elem.getCode()) == 0) {
								if (!warningDiseaseDeleted) JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.disease3mayhavebeencanceled"));
								diseaseBox3.addItem(elem);
								diseaseBox3.setSelectedItem(elem);
								warningDiseaseDeleted = true;
							}
						}
					}
				}
			}
        }
        diseaseBox3.addItemListener(db3listener);
        diseaseBox3.setEnabled(opdPatient != null);
        return diseaseBox3;
    }

    private VoLimitedTextField getPatientSearchField() {
        if (patientSearchField == null) {
            patientSearchField = new VoLimitedTextField(12,12);
            patientSearchField.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_ENTER) {
                        patientSearchButton.doClick();
                    }
                }
                public void keyReleased(KeyEvent e) {
                }
                public void keyTyped(KeyEvent e) {
                }
            });
        }
        return patientSearchField;
    }

    private JButton getPatientSearchButton() {
        if (patientSearchButton == null) {
            patientSearchButton = new JButton();
            patientSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
            patientSearchButton.setBorderPainted(false);
            patientSearchButton.setActionCommand("startSearching");
            patientSearchButton.setPreferredSize(new Dimension(20, 20));
        }
        return patientSearchButton;
    }

    private void clearLastOpdFields() {
        jLabelLastOpdVisit.setText("");
        jFieldLastOpdVisit.setText("");
        jLabelLastOpdScheduledVisit.setText("");
        jFieldLastOpdScheduledVisit.setText("");
    }

    private boolean opdIsDuplicatingExistingChronicOpd() {
        if (insert && opdPatient != null && diseaseBox1.getSelectedItem() != null && opdDateFieldCal.getDate() != null) {
            // If a chronic OPD for the same patient is found in the same day, it is assumed that the
            // OPD was already inserted by the pharmacists.
            String diseaseDesc = String.valueOf(diseaseBox1.getSelectedItem());
            Opd existingChronicOpd = chronicManager.findChronicOpdByPatientAndDateAndDisease(
                    opdPatient,
                    opdDateFieldCal.getDate(),
                    diseaseDesc);
            if (existingChronicOpd != null) {
                opd = existingChronicOpd;
                return true;
            }
        }
        return false;
    }

    private JComboBox getPatientSearchBox() {
        if (patientSearchBox == null) {
            patientSearchBox = new JComboBox();
            patientSearchBox.setPreferredSize(new Dimension(250, 24));
            patientSearchBox.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
        }
        patientSearchBox.removeAllItems();
        if (opdPatient != null) {
            patientSearchBox.addItem(opdPatient);
            patientSearchBox.setEnabled(false);
            patientSearchField.setEnabled(false);
            return patientSearchBox;
        } else {
            patientSearchBox.addItem(selectPatientMessage);
            patientSearchBox.addItem(newPatientMessage);
        }
        return patientSearchBox;
    }

    class PatientSearchBoxListener implements ActionListener {

        private String selectPatientMessage;
        private String newPatientMessage;
        public PatientSearchBoxListener(String selectPatientMessage, String newPatientMessage) {
            this.selectPatientMessage = selectPatientMessage;
            this.newPatientMessage = newPatientMessage;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (patientSearchBox.getSelectedItem() != null) {
                if (patientSearchBox.getSelectedItem().toString().equals(this.newPatientMessage)) {
                    if (GeneralData.PATIENTEXTENDED) {
                        patientSearchBox.setSelectedItem(this.selectPatientMessage);
                        PatientInsertExtended newRecord = new PatientInsertExtended(OpdChronicEdit.this, new Patient(), true);
                        newRecord.addPatientListener(OpdChronicEdit.this);
                        newRecord.setVisible(true);
                    } else {
                        PatientInsert newRecord = new PatientInsert(OpdChronicEdit.this, new Patient(), true);
                        newRecord.addPatientListener(OpdChronicEdit.this);
                        newRecord.setVisible(true);
                    }
                } else if (patientSearchBox.getSelectedItem().toString().equals(this.selectPatientMessage)) {
                    jPatientEditButton.setEnabled(false);
                } else {
                    opdPatient = (Patient) patientSearchBox.getSelectedItem();
                    setPatient();
                    setLastChronicOpd();
                    jPatientEditButton.setEnabled(true);
                }
            }
        }
    }

    private JButton getJPatientEditButton() {
        if (jPatientEditButton == null) {
            jPatientEditButton = new JButton();
            jPatientEditButton.setIcon(new ImageIcon("rsc/icons/edit_button.png"));
            jPatientEditButton.setBorderPainted(false);
            jPatientEditButton.setPreferredSize(new Dimension(20, 20));
            jPatientEditButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (opdPatient != null) {
                        if (GeneralData.PATIENTEXTENDED) {
                            PatientInsertExtended editRecord = new PatientInsertExtended(OpdChronicEdit.this, opdPatient, false);
                            editRecord.addPatientListener(OpdChronicEdit.this);
                            editRecord.setVisible(true);
                        } else {
                            PatientInsert editRecord = new PatientInsert(OpdChronicEdit.this, opdPatient, false);
                            editRecord.addPatientListener(OpdChronicEdit.this);
                            editRecord.setVisible(true);
                        }
                    }
                }
            });
            if (!insert) jPatientEditButton.setEnabled(false);
        }
        return jPatientEditButton;
    }

    private JPanel getJPanelPatient() {
        if (jPanelPatient == null){
            jPanelPatient = new JPanel();
            GridBagLayout gbl_jPanelPatient = new GridBagLayout();
            gbl_jPanelPatient.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
            gbl_jPanelPatient.columnWeights = new double[]{0.0, 1.0, 1.0};
            gbl_jPanelPatient.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            jPanelPatient.setLayout(gbl_jPanelPatient);
            BorderedPanel.setMyMatteBorder(jPanelPatient, MessageBundle.getMessage("angal.opd.patient"));

            jLabelfirstName = new JLabel();
            jLabelfirstName.setText(MessageBundle.getMessage("angal.opd.first.name") + "\t");
            GridBagConstraints gbc_jLabelfirstName = new GridBagConstraints();
            gbc_jLabelfirstName.fill = GridBagConstraints.BOTH;
            gbc_jLabelfirstName.insets = new Insets(5, 5, 5, 5);
            gbc_jLabelfirstName.gridx = 0;
            gbc_jLabelfirstName.gridy = 0;
            jPanelPatient.add(jLabelfirstName, gbc_jLabelfirstName);
            jFieldFirstName= new VoLimitedTextField(50,20);
            jFieldFirstName.setEditable(false);
            jFieldFirstName.setFocusable(false);
            GridBagConstraints gbc_jFieldFirstName = new GridBagConstraints();
            gbc_jFieldFirstName.insets = new Insets(5, 5, 5, 5);
            gbc_jFieldFirstName.fill = GridBagConstraints.BOTH;
            gbc_jFieldFirstName.gridx = 1;
            gbc_jFieldFirstName.gridy = 0;
            jPanelPatient.add(jFieldFirstName, gbc_jFieldFirstName);
            jLabelsecondName = new JLabel();
            jLabelsecondName.setText(MessageBundle.getMessage("angal.opd.second.name") + "\t");
            GridBagConstraints gbc_jLabelsecondName = new GridBagConstraints();
            gbc_jLabelsecondName.insets = new Insets(5, 5, 5, 5);
            gbc_jLabelsecondName.fill = GridBagConstraints.BOTH;
            gbc_jLabelsecondName.gridx = 0;
            gbc_jLabelsecondName.gridy = 1;
            jPanelPatient.add(jLabelsecondName, gbc_jLabelsecondName);
            jFieldSecondName= new VoLimitedTextField(50,20);
            jFieldSecondName.setEditable(false);
            jFieldSecondName.setFocusable(false);
            GridBagConstraints gbc_jFieldSecondName = new GridBagConstraints();
            gbc_jFieldSecondName.fill = GridBagConstraints.BOTH;
            gbc_jFieldSecondName.insets = new Insets(5, 5, 5, 5);
            gbc_jFieldSecondName.gridx = 1;
            gbc_jFieldSecondName.gridy = 1;
            jPanelPatient.add(jFieldSecondName, gbc_jFieldSecondName);
            jLabeladdress  = new JLabel();
            jLabeladdress.setText(MessageBundle.getMessage("angal.opd.address"));
            GridBagConstraints gbc_jLabeladdress = new GridBagConstraints();
            gbc_jLabeladdress.fill = GridBagConstraints.BOTH;
            gbc_jLabeladdress.insets = new Insets(5, 5, 5, 5);
            gbc_jLabeladdress.gridx = 0;
            gbc_jLabeladdress.gridy = 2;
            jPanelPatient.add(jLabeladdress, gbc_jLabeladdress);
            jFieldAddress= new VoLimitedTextField(50,20);
            jFieldAddress.setEditable(false);
            jFieldAddress.setFocusable(false);
            GridBagConstraints gbc_jFieldAddress = new GridBagConstraints();
            gbc_jFieldAddress.fill = GridBagConstraints.BOTH;
            gbc_jFieldAddress.insets = new Insets(5, 5, 5, 5);
            gbc_jFieldAddress.gridx = 1;
            gbc_jFieldAddress.gridy = 2;
            jPanelPatient.add(jFieldAddress, gbc_jFieldAddress);
            jLabelcity = new JLabel();
            jLabelcity.setText(MessageBundle.getMessage("angal.opd.city"));
            GridBagConstraints gbc_jLabelcity = new GridBagConstraints();
            gbc_jLabelcity.fill = GridBagConstraints.BOTH;
            gbc_jLabelcity.insets = new Insets(5, 5, 5, 5);
            gbc_jLabelcity.gridx = 0;
            gbc_jLabelcity.gridy = 3;
            jPanelPatient.add(jLabelcity, gbc_jLabelcity );
            jFieldCity= new VoLimitedTextField(50,20);
            jFieldCity.setEditable(false);
            jFieldCity.setFocusable(false);
            GridBagConstraints gbc_jFieldCity = new GridBagConstraints();
            gbc_jFieldCity.fill = GridBagConstraints.BOTH;
            gbc_jFieldCity.insets = new Insets(5, 5, 5, 5);
            gbc_jFieldCity.gridx = 1;
            gbc_jFieldCity.gridy = 3;
            jPanelPatient.add(jFieldCity, gbc_jFieldCity);
            jLabelnextKin = new JLabel();
            jLabelnextKin.setText(MessageBundle.getMessage("angal.opd.nextkin"));
            GridBagConstraints gbc_jLabelnextKin = new GridBagConstraints();
            gbc_jLabelnextKin.fill = GridBagConstraints.BOTH;
            gbc_jLabelnextKin.insets = new Insets(5, 5, 5, 5);
            gbc_jLabelnextKin.gridx = 0;
            gbc_jLabelnextKin.gridy = 4;
            jPanelPatient.add(jLabelnextKin, gbc_jLabelnextKin);
            jFieldNextKin= new VoLimitedTextField(50,20);
            jFieldNextKin.setEditable(false);
            jFieldNextKin.setFocusable(false);
            GridBagConstraints gbc_jFieldNextKin = new GridBagConstraints();
            gbc_jFieldNextKin.fill = GridBagConstraints.BOTH;
            gbc_jFieldNextKin.insets = new Insets(5, 5, 5, 5);
            gbc_jFieldNextKin.gridx = 1;
            gbc_jFieldNextKin.gridy = 4;
            jPanelPatient.add(jFieldNextKin, gbc_jFieldNextKin);
            jLabelAge = new JLabel();
            jLabelAge.setText(MessageBundle.getMessage("angal.opd.age"));
            GridBagConstraints gbc_jLabelAge = new GridBagConstraints();
            gbc_jLabelAge.fill = GridBagConstraints.BOTH;
            gbc_jLabelAge.insets = new Insets(5, 5, 5, 5);
            gbc_jLabelAge.gridx = 0;
            gbc_jLabelAge.gridy = 5;
            jPanelPatient.add(jLabelAge, gbc_jLabelAge);
            jFieldAge = new VoLimitedTextField(50,20);
            jFieldAge.setEditable(false);
            jFieldAge.setFocusable(false);
            GridBagConstraints gbc_jFieldAge = new GridBagConstraints();
            gbc_jFieldAge.fill = GridBagConstraints.BOTH;
            gbc_jFieldAge.insets = new Insets(5, 5, 5, 5);
            gbc_jFieldAge.gridx = 1;
            gbc_jFieldAge.gridy = 5;
            jPanelPatient.add(jFieldAge, gbc_jFieldAge);
            jLabelSex = new JLabel();
            jLabelSex.setText(MessageBundle.getMessage("angal.opd.sex"));
            GridBagConstraints gbc_jLabelSex = new GridBagConstraints();
            gbc_jLabelSex.fill = GridBagConstraints.HORIZONTAL;
            gbc_jLabelSex.insets = new Insets(5, 5, 5, 5);
            gbc_jLabelSex.gridx = 0;
            gbc_jLabelSex.gridy = 6;
            jPanelPatient.add(jLabelSex, gbc_jLabelSex);
            radiom= new JRadioButton(MessageBundle.getMessage("angal.opd.male"));
            radiof= new JRadioButton(MessageBundle.getMessage("angal.opd.female"));
            jPanelSex = new JPanel();
            jPanelSex.add(radiom);
            jPanelSex.add(radiof);
            GridBagConstraints gbc_jPanelSex = new GridBagConstraints();
            gbc_jPanelSex.insets = new Insets(5, 5, 5, 5);
            gbc_jPanelSex.fill = GridBagConstraints.HORIZONTAL;
            gbc_jPanelSex.gridx = 1;
            gbc_jPanelSex.gridy = 6;
            jPanelPatient.add(jPanelSex, gbc_jPanelSex);
            GridBagConstraints gbc_jPatientNote = new GridBagConstraints();
            gbc_jPatientNote.fill = GridBagConstraints.BOTH;
            gbc_jPatientNote.insets = new Insets(5, 5, 5, 5);
            gbc_jPatientNote.gridx = 2;
            gbc_jPatientNote.gridy = 0;
            gbc_jPatientNote.gridheight = 7;
            jPanelPatient.add(getJPatientNote(), gbc_jPatientNote);

            group = new ButtonGroup();
            group.add(radiom);
            group.add(radiof);
            radiom.setSelected(true);
            radiom.setEnabled(false);
            radiof.setEnabled(false);
            radiom.setFocusable(false);
            radiof.setFocusable(false);
        }
        return jPanelPatient;
    }

    private JPanel getJPatientNote() {
        if (jPatientNotePanel == null) {
            jPatientNotePanel = new JPanel(new BorderLayout());
            jPatientScrollNote = new JScrollPane(getJPatientNoteArea());
            jPatientScrollNote.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jPatientScrollNote.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            jPatientScrollNote.setAutoscrolls(false);
            jPatientScrollNote.validate();
            jPatientNotePanel.add(jPatientScrollNote, BorderLayout.CENTER);
        }
        return jPatientNotePanel;
    }

    private JTextArea getJPatientNoteArea() {
        if (jPatientNote == null) {
            jPatientNote = new JTextArea(15, 15);
            jPatientNote.setLineWrap(true);
            jPatientNote.setEditable(false);
            jPatientNote.setFocusable(false);
            if (!insert)
                jPatientNote.setText(opdPatient.getNote());
        }
        return jPatientNote;
    }

    /**
     * This method initializes jPanelButtons
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJButtonPanel() {
        if (jPanelButtons == null) {
            jPanelButtons = new JPanel();
            jPanelButtons.add(getOkButton(), null);
            jPanelButtons.add(getVisitButton(), null);
            jPanelButtons.add(getTherapyButton(), null);
            jPanelButtons.add(getOpdHistoryButton(), null);
            jPanelButtons.add(getCancelButton(), null);
        }
        return jPanelButtons;
    }

    private JButton getVisitButton() {
        visitButton = new JButton(MessageBundle.getMessage("angal.opdchronic.visit"));
        visitButton.setMnemonic(KeyEvent.VK_V);
        visitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpdChronicDetail opdChronicDetail = new OpdChronicDetail(
                        OpdChronicEdit.this,
                        getSelectedDiseases(),
                        opd.getCode()
                );
                opdChronicDetail.setVisible(true);
            }
        });
        return visitButton;
    }

    private JButton getTherapyButton() {
        therapyButton = new JButton(MessageBundle.getMessage("angal.opdchronic.newtherapybutton"));
        therapyButton.setMnemonic(KeyEvent.VK_T);
        therapyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openChronicTherapyForm(
                        OpdChronicEdit.this,
                        opdPatient.getCode(),
                        opd.getCode(),
                        null);
            }
        });
        return therapyButton;
    }

    private void openChronicTherapyForm(JDialog owner, int patId, int opdCode, TherapyRow therapyRow) {
        ChronicTherapyEntryForm chronicTherapyEntryForm = new ChronicTherapyEntryForm(
                owner,
                patId,
                opdCode,
                therapyRow);
        chronicTherapyEntryForm.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        chronicTherapyEntryForm.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                therapyTableModel.fireTableDataChanged();
                getNextVisitDateCal();
            }
        });
        chronicTherapyEntryForm.setVisible(true);
    }

    private ArrayList<String> getSelectedDiseases() {
        LinkedHashSet<String> selectedDiseaseCodes = new LinkedHashSet<String>();
        if (diseaseBox1.getSelectedIndex() > 0)
            selectedDiseaseCodes.add(((Disease) diseaseBox1.getSelectedItem()).getCode());
        if (diseaseBox2.getSelectedIndex() > 0)
            selectedDiseaseCodes.add(((Disease) diseaseBox2.getSelectedItem()).getCode());
        if (diseaseBox3.getSelectedIndex() > 0)
            selectedDiseaseCodes.add(((Disease) diseaseBox3.getSelectedItem()).getCode());
        return new ArrayList<String>(selectedDiseaseCodes);
    }

    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton(MessageBundle.getMessage("angal.opd.ok"));
            okButton.setMnemonic(KeyEvent.VK_O);
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (chronicManager.resetInappropriateVisitFields(opd.getCode(), getSelectedDiseases())
                            && getSelectedDiseases().size() > 0) {
                        // If a visit was already inserted and diagnosis selection changed, prompt the visit panel so thaht
                        // the user can review the parameters.
                        JOptionPane.showMessageDialog(OpdChronicEdit.this, "Changing the diagnosis will affect the visit " +
                                "parameters. Please click on Visit button to review them.");
                        visitButton.doClick();
                    } else {
                        // Search for a chronic Opd associated to the patient, for the selected disease, in the selected
                        // date. If found, we're assuming that the Opd was already inserted by pharmacists,
                        // so we're editing that Opd instead of inserting a new one.
                        if (opdIsDuplicatingExistingChronicOpd()) {
                            int r = JOptionPane.showConfirmDialog(OpdChronicEdit.this,
                                    "An Opd for the same patient and same disease already exist in the selected date." +
                                            "\nClick OK to merge the data you inserted into the existing Opd." +
                                            "\nClick CANCEL to exit without saving (you can edit the existing Opd from the Opd list).",
                                    "Merge data into existing Opd",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.PLAIN_MESSAGE,
                                    new ImageIcon("rsc/icons/info_button.png"));

                            if (r == JOptionPane.OK_OPTION) {
                                saveOpd();
                            }
                        } else {
                            saveOpd();
                        }
                    }
                }
               }
            );
        }
        return okButton;
    }

    private void saveOpd() {
        Disease disease = null, disease2 = null, disease3 = null;
        DiseaseType diseaseType = null;

        if (opdPatient == null) {
            JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.pleaseselectapatient"));
            return;
        }
        if (diseaseBox1.getSelectedIndex() < 0) {
            //TODO i18n
            JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.pleaseselectamaindiagnosis"));
            return;
        }
        // Update User
        opd.setUser(MainMenu.getUser());

        // Disease Type
        if (diseaseTypeBox.getSelectedIndex() > 0) {
            diseaseType = (DiseaseType) diseaseTypeBox.getSelectedItem();
            opd.setDiseaseType(diseaseType.getCode());
            opd.setDiseaseTypeDesc(diseaseType.getDescription());
        }
        //disease
        if (diseaseBox1.getSelectedIndex() > 0) {
            disease = (Disease) diseaseBox1.getSelectedItem();
            opd.setDisease(disease.getCode());
        }
        //disease2
        if (diseaseBox2.getSelectedIndex() > 0) {
            disease2 = (Disease) diseaseBox2.getSelectedItem();
            opd.setDisease2(disease2.getCode());
        } else {
            opd.setDisease2(null);
        }
        //disease3
        if (diseaseBox3.getSelectedIndex() > 0) {
            disease3 = (Disease) diseaseBox3.getSelectedItem();
            opd.setDisease3(disease3.getCode());
        } else {
            opd.setDisease3(null);
        }
        //Check double diseases
        if (disease2 != null && disease == disease2) {
            JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.duplicatediseasesnotallowed"));
            return;
        }
        if (disease3 != null && disease == disease3) {
            JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.duplicatediseasesnotallowed"));
            return;
        }
        if (disease2 != null && disease3 != null && disease2 == disease3) {
            JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.duplicatediseasesnotallowed"));
            return;
        }
        // Check if visit date is set
        if (opdDateFieldCal.getDate() == null) {
            JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.pleaseinsertattendancedate"));
            return;
        }
        String d = currentDateFormat.format(opdDateFieldCal.getDate());
        if (d.equals("")) {
            JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.pleaseinsertattendancedate"));
            return;
        }
        opd.setNote(jNoteTextArea.getText());
        opd.setPatientCode(opdPatient.getCode()); //ADDED : alex
        opd.setFullName(opdPatient.getName());
        opd.setNewPatient(newPatientCheckBox.isSelected() ? "N" : "R");
        opd.setReferralFrom(referralFromCheckBox.isSelected() ? "R" : "");
        opd.setReferralTo(referralToCheckBox.isSelected() ? "R" : "");
        opd.setAge(opdPatient.getAge());
        opd.setSex(opdPatient.getSex());
        opd.setfirstName(opdPatient.getFirstName());
        opd.setsecondName(opdPatient.getSecondName());
        opd.setaddress(opdPatient.getAddress());
        opd.setcity(opdPatient.getCity());
        opd.setnextKin(opdPatient.getNextKin());
        opd.setDiseaseDesc(disease != null ? disease.getDescription() : null);
        if (diseaseType != null) {
            opd.setDiseaseType(diseaseType.getCode());
            opd.setDiseaseTypeDesc(diseaseType.getDescription());
        }
        GregorianCalendar visitDate = new GregorianCalendar();
        visitDate.setTime(opdDateFieldCal.getDate());
        opd.setVisitDate(visitDate);
        opd.setIsChronic(opdIsChronic());
        if (insert) {
            GregorianCalendar date = new GregorianCalendar();
            opd.setYear(opdManager.getProgYear(date.get(Calendar.YEAR)) + 1);
            RememberDates.setLastOpdVisitDate(visitDate);
        }
        if (nextVisitDateCal.getDate() != null) {
            if (opd.getNextVisit() != null && opd.getNextVisit().getVisitID() > 0) {
                opd.getNextVisit().getDate().setTime(nextVisitDateCal.getDate());
            } else {
                Visit nextVisitDate = new Visit();
                nextVisitDate.setPatID(opd.getpatientCode());
                nextVisitDate.getDate().setTime(nextVisitDateCal.getDate());
                opd.setNextVisit(nextVisitDate);
            }
        }
        if (lastOpd != null)
            opd.setScheduledVisit(lastOpd.getNextVisit());
        if (chronicManager.saveChronicOpd(opd))
            dispose();
        else
            JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.thedatacouldnotbesaved"));
    }

    /**
     * This method initializes cancelButton
     *
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton(MessageBundle.getMessage("angal.opd.cancel"));
            cancelButton.setMnemonic(KeyEvent.VK_C);
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: fire a window closing event instead
                    closeWindow();
                }
            });
        }
        return cancelButton;
    }

    private HashMap<String, String> selectedDiseases = new HashMap<String, String>(3);
    class ChronicVisitListener implements ItemListener {
        private String item;
        public ChronicVisitListener(String itemName) {
            this.item = itemName;
        }
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String diseaseDesc = String.valueOf(((JComboBox) e.getSource()).getSelectedItem());
                if (!diseaseDesc.isEmpty() && !diseaseDesc.equals("null")) {
                    selectedDiseases.put(item, diseaseDesc);
                }
            }
            enableComponents();
            setAttendance();
        }
    }
    ChronicVisitListener db1listener = new ChronicVisitListener("db1");
    ChronicVisitListener db2listener = new ChronicVisitListener("db2");
    ChronicVisitListener db3listener = new ChronicVisitListener("db3");


    //@Override
    public void patientInserted(AWTEvent e) {
        opdPatient = (Patient) e.getSource();
        setPatient();
        patientSearchBox.addItem(opdPatient);
        patientSearchBox.setSelectedItem(opdPatient);
    }

    private void enableComponents() {
        boolean diseaseCombosEnabled = opdPatient != null;
        diseaseTypeBox.setEnabled(diseaseCombosEnabled);
        diseaseBox1.setEnabled(diseaseCombosEnabled);
        if (diseaseBox2 != null) diseaseBox2.setEnabled(diseaseCombosEnabled);
        if (diseaseBox3 != null) diseaseBox3.setEnabled(diseaseCombosEnabled);
        if (jPatientEditButton != null) jPatientEditButton.setEnabled(diseaseCombosEnabled);
        if (therapyButton != null) therapyButton.setEnabled(opdIsChronic());
        if (visitButton != null) visitButton.setEnabled(opdIsChronic());
    }

    private boolean opdIsChronic() {
        return selectedDiseases.size() > 0 &&
                chronicManager.chronicDiseasesSelected(selectedDiseases.values());
    }

    //@Override
    public void patientUpdated(AWTEvent e) {
        setPatient();
    }

    private EventListenerList surgeryListeners = new EventListenerList();

    public interface SurgeryListener extends EventListener {
        void surgeryUpdated(AWTEvent e);
        void surgeryInserted(AWTEvent e);
    }

    public void addSurgeryListener(SurgeryListener l) {
        surgeryListeners.add(SurgeryListener.class, l);
    }

    public void removeSurgeryListener(SurgeryListener listener) {
        surgeryListeners.remove(SurgeryListener.class, listener);
    }

    private void fireSurgeryInserted() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
            private static final long serialVersionUID = 1L;
        };
        EventListener[] listeners = surgeryListeners.getListeners(SurgeryListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((SurgeryListener)listeners[i]).surgeryInserted(event);
    }
    private void fireSurgeryUpdated() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
            private static final long serialVersionUID = 1L;
        };
        EventListener[] listeners = surgeryListeners.getListeners(SurgeryListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((SurgeryListener)listeners[i]).surgeryUpdated(event);
    }

    // BACKGROUND TASK CLASSES
    class SearchPatientListener implements ActionListener {

        private static final int MIN_HINT_LENGTH = 3;

        @Override
        public void actionPerformed(ActionEvent evt) {
            String queryParam = patientSearchField.getText() != null ? patientSearchField.getText().trim() : "";
            if ("startSearching".equalsIgnoreCase(evt.getActionCommand())) {
                resetPatient();
                clearAllOpdFields();
                clearLastOpdFields();
                if (queryParam.isEmpty()) {
                    getPatientSearchBox();
                } else if (queryParam.length() < MIN_HINT_LENGTH) {
                    JOptionPane.showMessageDialog(null, "Please type at least " + MIN_HINT_LENGTH + " characters.");
                    getPatientSearchBox();
                }
                else {
                    startSearching(evt, queryParam);
                    updatePatientSearchBox();
                    patientSearchField.requestFocus();
                }
            } else if ("stopSearching".equalsIgnoreCase(evt.getActionCommand())) {
                stopSearching();
            }
        }

        private void updatePatientSearchBox() {
            patientSearchBox.setModel(new DefaultComboBoxModel(patients.toArray()));
            if (patientSearchBox.getItemCount() > 0)
                patientSearchBox.setSelectedIndex(0);
        }

        public void startSearching(ActionEvent evt, String params) {
            BackgroundTask task = new BackgroundTask(params);
            UiRenderer renderer = new UiRenderer(evt);
            SwingBackgroundTaskExecutor.getInstance().execute(task, renderer);
        }

        public void stopSearching() {
            SwingBackgroundTaskExecutor exec = SwingBackgroundTaskExecutor.getInstance();
            exec.cancel();
        }
    }

    class BackgroundTask implements SwingBackgroundTask {

        private String query;

        public BackgroundTask(String query) {
            this.query = query;
        }

        @Override
        public Object doInBackground() throws Exception {
            patients = patBrowser.getPatients(query, true);
            return null;
        }

        @Override
        public Object getNextResultChunk() {
            return null;
        }

        @Override
        public int getProgress() {
            return 0;
        }
    }

    class UiRenderer implements SwingUiRenderer {

        private WaitingDialog dialog;
        private ActionEvent actionEvent;

        public UiRenderer(ActionEvent actionEvent) {
            this.actionEvent = actionEvent;
        }

        @Override
        public void start() {
            Window win = SwingUtilities.getWindowAncestor((AbstractButton) actionEvent.getSource());
            dialog = new WaitingDialog(win, "Waiting", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setUndecorated(true);
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(progressBar, BorderLayout.CENTER);
            panel.add(new JLabel("Please wait......."), BorderLayout.PAGE_START);
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new SearchPatientListener());
            cancel.setActionCommand("stopSearching");
            cancel.setVisible(true);
            panel.add(cancel, BorderLayout.SOUTH);
            dialog.add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(win);
            dialog.setVisible(true);
        }

        @Override
        public void processIntermediateResults(List intermediateResults, int progress) {}

        @Override
        public void done(Object result) {}

        @Override
        public void cancel() {
            this.dialog.dispose();
        }
    }
}