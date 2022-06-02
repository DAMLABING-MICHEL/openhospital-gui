package org.isf.malnutrition.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import org.isf.admission.model.Admission;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.gui.SpringUtilities;
import org.isf.utils.jobjects.BorderedPanel;
import org.isf.utils.jobjects.FormattedTextField;

/**
 * Created by nicosalvato on 2017-02-10.
 * Contact: nicosalvato@gmail.com
 */
public class MalnutritionVisitEdit extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Managers
    private MalnutritionController manager = MalnutritionController.getInstance();
    // Fields
    private JTextField closestHealthCenterField;
    private MalnutritionVisitPanel admissionPanel;
    private MalnutritionVisitPanel dischargePanel;

    public MalnutritionVisitEdit(JFrame owner, Admission admission) {
        super(owner, true);
        manager.initializeState(admission);
        this.setTitle(MessageBundle.getMessage("angal.malnutrition.malnutritionvisitedittitle"));
        this.setContentPane(getMainPanel());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void closeWindow() {
        manager.clearState();
        dispose();
    }

    private JPanel getMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(getNorthContainer(), BorderLayout.NORTH);
        mainPanel.add(getVisitsPanel(), BorderLayout.CENTER);
        mainPanel.add(getButtonPanel(), BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel getNorthContainer() {
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(getExtCodeAndFollowUpdPanel(), BorderLayout.NORTH);
        northContainer.add(getPatientPanel(), BorderLayout.CENTER);
        return northContainer;
    }

    private JTextField externalCodeField;
    private JCheckBox followUpField;
    private JPanel getExtCodeAndFollowUpdPanel() {
        JPanel container = new JPanel(new BorderLayout());
        JPanel extCodePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        extCodePanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.externalcode")));
        externalCodeField = new JTextField(20);
        externalCodeField.setText(manager.getAdmissionVisit().getExtCode());
        extCodePanel.add(externalCodeField);
        JPanel followUpPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        followUpPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.followup")));
        followUpField = new JCheckBox();
        followUpField.setSelected(manager.getDischargeVisit().isFollowUp());
        followUpField.setEnabled(!manager.isAdmissionStillOpen());
        followUpPanel.add(followUpField);
        container.add(extCodePanel, BorderLayout.WEST);
        container.add(followUpPanel, BorderLayout.EAST);
        return container;
    }

    private JTextField patientNameField;
    private JComboBox patientGenderField;
    private JTextField patientCityField;
    private JTextField patientAddressField;
    private JFormattedTextField patientMonthsField;
    private JFormattedTextField patientYearsField;
    private JPanel getPatientPanel() {
        JPanel patientPanel = new JPanel(new FlowLayout());
//        patientPanel.setPreferredSize(new Dimension());
        BorderedPanel.setMyBorder(patientPanel, MessageBundle.getMessage("angal.malnutrition.patientareatitle"));
        JPanel patientParamsPanel = new JPanel(new SpringLayout());
        // Previous Code
        patientParamsPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.patientopdnumber")));
        JTextField pCode = new JTextField(manager.getPatient().getCode(), 20);
        pCode.setEnabled(false);
        patientParamsPanel.add(pCode);
        // Name
        patientParamsPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.patientname")));
        patientNameField = new JTextField(manager.getPatient().getName(), 20);
        patientParamsPanel.add(patientNameField);
        // Age
        patientParamsPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.patientage")));
        JPanel agePanel = new JPanel(new SpringLayout());
        int[] yearsAndMonths = manager.getPatient().getYearsAndMonthsAtAdmissionTime();
        agePanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.patientyears")));
        patientYearsField = FormattedTextField.getIntegerField(4, true, yearsAndMonths[0]);
        agePanel.add(patientYearsField);
        agePanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.patientmonths")));
        patientMonthsField = FormattedTextField.getIntegerField(4, true, yearsAndMonths[1]);
        agePanel.add(patientMonthsField);
        patientParamsPanel.add(agePanel);
        SpringUtilities.makeCompactGrid(agePanel, 1, 4, 0, 0, 5, 0);
        //Gender
        patientParamsPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.patientgender")));
        patientGenderField = new JComboBox();
        patientGenderField.addItem('M');
        patientGenderField.addItem('F');
        patientGenderField.setSelectedItem(manager.getPatient().getGender());
        patientParamsPanel.add(patientGenderField);
        // Woreda
        patientParamsPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.patientworeda")));
        patientCityField = new JTextField(manager.getPatient().getCity(), 15);
        patientParamsPanel.add(patientCityField);
        // Kebele
        patientParamsPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.patientkebele")));
        patientAddressField = new JTextField(manager.getPatient().getAddress());
        patientParamsPanel.add(patientAddressField);
        // Closest Health Center
        patientParamsPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.closesthc")));
        String closestHealthCenter = null;
        if (manager.getAdmissionVisit().getId() > 0)
            closestHealthCenter = manager.getAdmissionVisit().getClosestHealthCenter();
        else if (manager.getLastVisit() != null)
            closestHealthCenter = manager.getLastVisit().getClosestHealthCenter();
        closestHealthCenterField = new JTextField(closestHealthCenter);
        patientParamsPanel.add(closestHealthCenterField);
        //Empty label to avoid SpringLayout exception
        patientParamsPanel.add(new JLabel(""));
        patientParamsPanel.add(new JLabel(""));

        SpringUtilities.makeCompactGrid(patientParamsPanel, 4, 4, 5, 5, 10, 5);
        patientPanel.add(patientParamsPanel);
        return patientPanel;
    }

    private JTabbedPane getVisitsPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(MessageBundle.getMessage("angal.malnutrition.admissiontabtitle"), getAdmissionPanel());
        //TODO: add HOSPITALIZATION panel if configured.
        tabbedPane.addTab(MessageBundle.getMessage("angal.malnutrition.dischargetabtitle"), getDischargePanel());
        tabbedPane.setEnabledAt(1, !manager.isAdmissionStillOpen());
        return tabbedPane;
    }

    private JPanel getAdmissionPanel() {
        admissionPanel = new MalnutritionVisitPanel(manager.getAdmissionVisit());
        admissionPanel.addPropertyChangeListenerToDateField(new MinWeightDateChangeListener(MinWeightDateChangeListener.MIN));
        return admissionPanel;
    }

    private JPanel getDischargePanel() {
        if (!manager.isAdmissionStillOpen()) {
            dischargePanel = new MalnutritionVisitPanel(manager.getDischargeVisit());
            dischargePanel.addPropertyChangeListenerToDateField(new MinWeightDateChangeListener(MinWeightDateChangeListener.MAX));
        }
        return dischargePanel;
    }

    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(getSaveButton());
        buttonPanel.add(getDeleteButton());
        buttonPanel.add(getCancelButton());
        return buttonPanel;
    }

    private JButton getSaveButton() {
        //TODO: i18n
        JButton saveButton = new JButton(MessageBundle.getMessage("angal.common.ok"));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (manager.saveMalnutritionVisits(MalnutritionVisitEdit.this, admissionPanel, dischargePanel))
                    closeWindow();
            }
        });
        return saveButton;
    }

    private JButton getCancelButton() {
        JButton cancelButton = new JButton(MessageBundle.getMessage("angal.common.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });
        return cancelButton;
    }

    private JButton getDeleteButton() {
        JButton deleteButton = new JButton(MessageBundle.getMessage("angal.common.delete"));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MalnutritionVisitEdit.this,
                        MessageBundle.getMessage("angal.malnutrition.doyoureallywanttodelete"),
                        MessageBundle.getMessage("angal.common.confirm"),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.OK_OPTION) {
                    if (manager.deleteMalnutrition(manager.getAdmission().getId()))
                        closeWindow();
                }
            }
        });
        deleteButton.setEnabled(manager.isEditMode());
        return deleteButton;
    }

    public String getExternalCodeFieldValue() {
        return externalCodeField.getText();
    }

    public String getClosestHealthCenterFieldValue() {
        return closestHealthCenterField.getText();
    }

    public int getPatientYearsFieldValue() {
        return FormattedTextField.parseFieldValueToInteger(patientYearsField);
    }

    public int getPatientMonthsFieldValue() {
        return FormattedTextField.parseFieldValueToInteger(patientMonthsField);
    }

    public String getPatientNameFieldValue() {
        return patientNameField.getText();
    }

    public char getPatientGenderFieldValue() {
        return (patientGenderField.getSelectedItem() + "").charAt(0);
    }

    public String getPatientCityFieldValue() {
        return patientCityField.getText();
    }

    public String getPatientAddressFieldValue() {
        return patientAddressField.getText();
    }

    public boolean getFollowUpFieldValue() {
        return followUpField.isSelected();
    }

    class MinWeightDateChangeListener implements PropertyChangeListener {

        private static final String MIN = "min";
        private static final String MAX = "max";
        private String limit;

        public MinWeightDateChangeListener(String limit) {
            this.limit = limit;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("date".equals(evt.getPropertyName())) {
                if (limit.equals(MIN) && dischargePanel != null) {
                    dischargePanel.setMinWeightDateFieldMinSelectableValue((Date) evt.getNewValue());
                    dischargePanel.setVisitDateFieldMinSelectableValue((Date) evt.getNewValue());
                } else if (limit.equals(MAX) && dischargePanel != null) {
                    dischargePanel.setMinWeightDateFieldMaxSelectableValue((Date) evt.getNewValue());
                }
            }
        }
    }
}
