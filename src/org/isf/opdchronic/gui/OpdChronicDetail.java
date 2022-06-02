package org.isf.opdchronic.gui;

import org.isf.opdchronic.manager.OpdChronicManager;
import org.isf.opdchronic.model.OpdChronic;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.gui.SpringUtilities;
import org.isf.utils.jobjects.FormattedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * Created by nicosalvato on 2016-08-21.
 * Contact: nicosalvato@gmail.com
 */
public class OpdChronicDetail extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final String VERSION = "1.0";
    private JPanel mainPanel;
    private JPanel paramsPanel;
    private JPanel buttonsPanel = new JPanel();
    private ArrayList<String> diseaseCodes;
    private OpdChronicManager opdChronicManager = OpdChronicManager.getInstance();
    private int opdCode;
    private OpdChronic opdChronic;
    // FIELDS
    private JFormattedTextField bodyWeightField;
    private JFormattedTextField fastBloodSugarField;
    private JFormattedTextField creatinineField;
    private JFormattedTextField bloodPressureMinField;
    private JFormattedTextField bloodPressureMaxField;
    private JCheckBox atrialFibrillationField;
    private JFormattedTextField heartRateField;
    private JFormattedTextField hemoglobinField;
    private JTextArea chestFindingField;
    private JFormattedTextField pO2AtRestField;
    private JTextArea notesField;


    public OpdChronicDetail(JDialog owner, ArrayList<String> diseaseCodes, int opdCode) {
        super(owner, true);
        this.diseaseCodes = diseaseCodes;
        this.opdCode = opdCode;
        this.opdChronic = opdChronicManager.getCurrentVisit(opdCode);
        initialize(opdChronic != null);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initialize(boolean edit) {
        setMinimumSize(this.getSize());
        if (edit) {
            this.setTitle(MessageBundle.getMessage("angal.opdchronic.editvisitpaneltitle") +
                    " (" + VERSION + ")");
        } else {
            this.setTitle(MessageBundle.getMessage("angal.opdchronic.newvisitpaneltitle") +
                    " (" + VERSION + ")");
        }
        this.paramsPanel = getParamsPanel();
        this.buttonsPanel = getButtonsPanel();
        this.setContentPane(getMainPanel());
        pack();
    }

    private JPanel getMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(getParamsPanel(), BorderLayout.NORTH);
        mainPanel.add(getButtonsPanel(), BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel getParamsPanel() {
        paramsPanel = new JPanel(new FlowLayout());
        JPanel springPanel = new JPanel(new SpringLayout());
        ArrayList<String> fields = opdChronicManager.getFieldsByDisease(diseaseCodes);
        PanelField panelField;
        for (String field: fields) {
            panelField = addFieldByName(field);
            if (panelField != null) {
                JLabel label = panelField.getLabel();
                JComponent textField = panelField.getField();
                label.setLabelFor(textField);
                springPanel.add(label);
                springPanel.add(textField);
            }
        }
        notesField = getTextArea(opdChronic != null ? opdChronic.getNotes() : null);
        PanelField notesArea = getTextAreaPanel(notesField, "Notes: ");
        springPanel.add(notesArea.getLabel());
        springPanel.add(notesArea.getField());
        SpringUtilities.makeCompactGrid(springPanel,
                fields.size() + 1, 2,   //rows, cols
                6, 6,               //initX, initY
                6, 6);              //xPad, yPad
        paramsPanel.add(springPanel);
        return paramsPanel;
    }

    private PanelField addFieldByName(String fieldName) {
        //TODO: i18n
        if (fieldName.equals("bodyWeight")) {
            bodyWeightField = FormattedTextField.getIntegerField(20, true, opdChronic != null ? opdChronic.getBodyWeight() : null);
            bodyWeightField.addKeyListener(new SaveOnEnterListener());

            return new PanelField(getLabel("BW: "), bodyWeightField);
        } else if (fieldName.equals("fastBloodSugar")) {
            fastBloodSugarField = FormattedTextField.getIntegerField(20, true, opdChronic != null ? opdChronic.getFastBloodSugar() : null);
            fastBloodSugarField.addKeyListener(new SaveOnEnterListener());
            return new PanelField(getLabel("FBS: "), fastBloodSugarField);
        } else if (fieldName.equals("creatinine")) {
            creatinineField = FormattedTextField.getDecimalField(20, true, opdChronic != null ? opdChronic.getCreatinine() : null);
            creatinineField.addKeyListener(new SaveOnEnterListener());
            return new PanelField(getLabel("Creatinine: "), creatinineField);
        } else if (fieldName.equals("bloodPressureMin")) {
            bloodPressureMinField = FormattedTextField.getIntegerField(20, true, opdChronic != null ? opdChronic.getBloodPressureMin() : null);
            bloodPressureMinField.addKeyListener(new SaveOnEnterListener());
            return new PanelField(getLabel("BP Min: "), bloodPressureMinField);
        } else if (fieldName.equals("bloodPressureMax")) {
            bloodPressureMaxField = FormattedTextField.getIntegerField(20, true, opdChronic != null ? opdChronic.getBloodPressureMax() : null);
            bloodPressureMaxField.addKeyListener(new SaveOnEnterListener());
            return new PanelField(getLabel("BP Max: "), bloodPressureMaxField);
        } else if (fieldName.equals("atrialFibrillation")) {
            atrialFibrillationField = FormattedTextField.getBooleanField(opdChronic != null && opdChronic.getAtrialFibrillation());
            atrialFibrillationField.addKeyListener(new SaveOnEnterListener());
            return new PanelField(getLabel("AF: "), atrialFibrillationField);
        } else if (fieldName.equals("heartRate")) {
            heartRateField = FormattedTextField.getIntegerField(20, true, opdChronic != null ? opdChronic.getHeartRate() : null);
            heartRateField.addKeyListener(new SaveOnEnterListener());
            return new PanelField(getLabel("Heart Rate: "), heartRateField);
        } else if (fieldName.equals("hemoglobin")) {
            hemoglobinField = FormattedTextField.getDecimalField(20, true, opdChronic != null ? opdChronic.getHemoglobin() : null);
            hemoglobinField.addKeyListener(new SaveOnEnterListener());
            return new PanelField(getLabel("Hb: "), hemoglobinField);
        } else if (fieldName.equals("chestFinding")) {
            chestFindingField = getTextArea(opdChronic != null ? opdChronic.getNotes() : null);
            return getTextAreaPanel(chestFindingField, "Chest Finding: ");
        } else if (fieldName.equals("pO2AtRest")) {
            pO2AtRestField = FormattedTextField.getIntegerField(20, true, opdChronic != null ? opdChronic.getPO2AtRest() : null);
            pO2AtRestField.addKeyListener(new SaveOnEnterListener());
            return new PanelField(getLabel("PO2 at rest: "), pO2AtRestField);
        } else {
            return null;
        }
    }

    private JLabel getLabel(String labelText) {
        return new JLabel(labelText, JLabel.TRAILING);
    }

    private PanelField getTextAreaPanel(JTextArea field, String label) {
        JScrollPane textAreaContainer = new JScrollPane(field);
        textAreaContainer.setVerticalScrollBar(new JScrollBar());
        textAreaContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        textAreaContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textAreaContainer.validate();
        return new PanelField(getLabel(label), textAreaContainer);
    }

    private JPanel getButtonsPanel() {
        buttonsPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton(MessageBundle.getMessage("angal.opdchronic.visitpanelsave"));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChronicVisit();
                closeVisitWindow();
            }
        });
        JButton cancelButton = new JButton(MessageBundle.getMessage("angal.opdchronic.visitpanelcancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeVisitWindow();
            }
        });
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        return buttonsPanel;
    }

    private void closeVisitWindow() {
        diseaseCodes.clear();
        dispose();
    }

    private void saveChronicVisit() {
        OpdChronic opdChronic = opdChronicManager.getCurrentVisit(opdCode);
        if (opdChronic == null)
            opdChronic = new OpdChronic(opdCode);
        opdChronic.setDiseaseCodes(diseaseCodes);
        if (bodyWeightField != null)
            opdChronic.setBodyWeight(FormattedTextField.parseFieldValueToInteger(bodyWeightField));
        if (fastBloodSugarField != null)
            opdChronic.setFastBloodSugar(FormattedTextField.parseFieldValueToInteger(fastBloodSugarField));
        if (creatinineField != null)
            opdChronic.setCreatinine(FormattedTextField.parseFieldValueToBigDecimal(creatinineField));
        if (bloodPressureMinField != null)
            opdChronic.setBloodPressureMin(FormattedTextField.parseFieldValueToInteger(bloodPressureMinField));
        if (bloodPressureMaxField != null)
            opdChronic.setBloodPressureMax(FormattedTextField.parseFieldValueToInteger(bloodPressureMaxField));
        if (atrialFibrillationField != null)
            opdChronic.setAtrialFibrillation(atrialFibrillationField.isSelected());
        if (heartRateField != null)
            opdChronic.setHeartRate(FormattedTextField.parseFieldValueToInteger(heartRateField));
        if (hemoglobinField != null)
            opdChronic.setHemoglobin(FormattedTextField.parseFieldValueToBigDecimal(hemoglobinField));
        if (chestFindingField != null)
            opdChronic.setChestFinding(chestFindingField.getText().isEmpty() ?
                    null : chestFindingField.getText());
        if (pO2AtRestField != null)
            opdChronic.setPO2AtRest(FormattedTextField.parseFieldValueToInteger(pO2AtRestField));
        if (notesField != null)
            opdChronic.setNotes(notesField.getText().isEmpty() ?
                    null : notesField.getText());
        opdChronicManager.saveCurrentVisit(opdChronic);
    }

    public JTextArea getTextArea(String value) {
        JTextArea field = new JTextArea(4, 20);
        field.setLineWrap(true);
        field.setWrapStyleWord(true);
        if (value != null) field.append(value);
        return field;
    }

    class SaveOnEnterListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                saveChronicVisit();
                closeVisitWindow();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}

class PanelField {
    private JLabel label;
    private JComponent field;

    PanelField(JLabel label, JComponent field) {
        this.label = label;
        this.field = field;
    }

    JLabel getLabel() { return label; }
    JComponent getField() { return field; }
}
