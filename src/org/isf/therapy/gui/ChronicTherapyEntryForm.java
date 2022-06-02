package org.isf.therapy.gui;

import com.toedter.calendar.JDateChooser;
import org.isf.opdchronic.manager.OpdChronicManager;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.gui.SpringUtilities;
import org.isf.utils.jobjects.FormattedTextField;
import org.isf.utils.jobjects.IconButton;
import org.isf.utils.listeners.SelectAllOnFocus;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by nicosalvato on 2016-08-26.
 * Contact: nicosalvato@gmail.com
 */
public class ChronicTherapyEntryForm extends JDialog {

    //TODO: save VISITS and THERAPY in a signle transaction

    private static final long serialVersionUID = 1L;
    // Managers
    private MedicalBrowsingManager medBrowser = new MedicalBrowsingManager();
    private ArrayList<Medical> medArray = medBrowser.getMedicalsSortedByName();
    // Constants
    private final int oneLineComponentsHeight = 30;
    private final int visibleMedicalsRows = 5;
    private final int frequencyInDayOptions = 4;
    // Attributes
    private int opdCode;
    private TherapyRow thRow;
    private JList medicalsList;
    private JPanel dayWeeksMonthsPanel;
    private JPanel frequencyInDayPanel;
    private JPanel frequencyInPeriodPanel;
    private JPanel quantityPanel;
    private JPanel medicalsPanel;
    private JPanel therapyPanelWest;
    private JPanel therapyPanelEast;
    private JPanel startEndDatePanel;
    private JPanel nextVisitPanel;
    private JPanel startDatePanel;
    private JPanel endDatePanel;
    private JPanel notePanel;
    private JScrollPane noteScrollPane;
    private JTextArea noteTextArea;
    private JPanel iconMedicalPanel;
    private JPanel iconFrequenciesPanel;
    private JPanel iconPeriodPanel;
    private JPanel iconNotePanel;
    private JPanel frequenciesPanel;
    private JFormattedTextField qtyField1;
    private JFormattedTextField qtyField2;
    private JFormattedTextField qtyField3;
    private JFormattedTextField qtyField4;
    private ArrayList<JRadioButton> radioButtonSet;
    private JSpinner jSpinnerFreqInPeriod;
    private JDateChooser therapyStartDate;
    private GregorianCalendar therapyEndDate;
    private JSpinner jSpinnerDays = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
    private JSpinner jSpinnerWeeks = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
    private JSpinner jSpinnerMonths = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
    private JLabel endDateLabel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private final String[] radioButtonLabels = {MessageBundle.getMessage("angal.therapy.one"), MessageBundle.getMessage("angal.therapy.two"), MessageBundle.getMessage("angal.therapy.three"), MessageBundle.getMessage("angal.therapy.four")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    private JButton buttonCancel;
    private JButton okButton;
    private JButton searchButton = new JButton();
    private JPanel buttonPanel;
    private JPanel therapyPanel;
    private JTextField searchMedical = new JTextField();
    private int freqInDay;
    private int patID;
    private OpdChronicManager chronicManager = OpdChronicManager.getInstance();
    private JDateChooser nextVisitChooser = new JDateChooser(new Date());
    private JComboBox medicalBox = new JComboBox();
    private boolean edit = false;

    public ChronicTherapyEntryForm(JDialog owner, int patID, int opdCode, TherapyRow thRow) {
        super(owner, true);
        this.edit = thRow != null;
        this.thRow = edit ? thRow : new TherapyRow();
        this.patID = patID;
        this.opdCode = opdCode;
        initComponents();

        if (edit) {
            fillFormWithTherapy(thRow);
        } else {
            radioButtonSet.get(0).setSelected(true);
            endDateLabel.setText(dateFormat.format(new Date()));
        }
        pack();
        searchMedical.requestFocusInWindow();
    }

    private void fillFormWithTherapy(TherapyRow thRow) {
        medicalBox.setSelectedItem(thRow.getMedical());
        qtyField1.setValue(thRow.getQty());
        qtyField2.setValue(thRow.getQty2());
        qtyField3.setValue(thRow.getQty3());
        qtyField4.setValue(thRow.getQty4());
        radioButtonSet.get(thRow.getFreqInDay() - 1).setSelected(true);
        fillDaysWeeksMonthsFromDates(thRow.getStartDate(), thRow.getEndDate());
        noteTextArea.setText(thRow.getNote());
    }

    private void fillDaysWeeksMonthsFromDates(GregorianCalendar firstDay, GregorianCalendar lastDay) {
        Period period = new Period(new DateTime(firstDay), new DateTime(lastDay), PeriodType.standard());
        jSpinnerMonths.setValue(period.getMonths());
        jSpinnerWeeks.setValue(period.getWeeks());
        jSpinnerDays.setValue(period.getDays());
    }

    private void initComponents() {
        if (thRow == null) {
            setTitle(MessageBundle.getMessage("angal.therapy.titlenew"));
        } else {
            setTitle(MessageBundle.getMessage("angal.therapy.titleedit"));
            getContentPane().setBackground(Color.RED);
        }
        setSize(new Dimension(900, 500));
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(getTherapyPanel(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private JPanel getTherapyPanel() {
        if (therapyPanel == null) {
            therapyPanel = new JPanel();
            therapyPanel.setLayout(new GridLayout(0, 2));
            therapyPanel.add(getTherapyPanelWest());
            therapyPanel.add(getTherapyPanelEast());
        }
        return therapyPanel;
    }

    private JPanel getTherapyPanelWest() {
        if (therapyPanelWest == null) {
            therapyPanelWest = new JPanel();
            therapyPanelWest.setLayout(new BoxLayout(therapyPanelWest, BoxLayout.Y_AXIS));
            therapyPanelWest.add(getIconMedicalPanel());
            therapyPanelWest.add(getIconFrequenciesPanel());
        }
        return therapyPanelWest;
    }

    private JPanel getTherapyPanelEast() {
        if (therapyPanelEast == null) {
            therapyPanelEast = new JPanel();
            therapyPanelEast.setLayout(new BoxLayout(therapyPanelEast, BoxLayout.Y_AXIS));
            therapyPanelEast.add(getIconPeriodPanel());
            therapyPanelEast.add(getIconNotePanel());
        }
        return therapyPanelEast;
    }

    private JPanel getMedicalSelectionPanel() {
        JPanel medicalSelectionPanel = new JPanel();
        medicalSelectionPanel.setLayout(new BoxLayout(medicalSelectionPanel, BoxLayout.Y_AXIS));
        medicalSelectionPanel.add(getMedicalSearchPanel());
        medicalSelectionPanel.add(getMedicalJComboBox());
        medicalSelectionPanel.add(Box.createVerticalGlue());
        return medicalSelectionPanel;
    }

    private JPanel getMedicalSearchPanel() {
        JPanel medicalSearchPanel = new JPanel();
        medicalSearchPanel.setLayout(new BoxLayout(medicalSearchPanel, BoxLayout.X_AXIS));
        medicalSearchPanel.add(getSearchMedicalField());
        medicalSearchPanel.add(getSearchMedicalButton());
        return medicalSearchPanel;
    }

    private JTextField getSearchMedicalField() {
        searchMedical.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    searchButton.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        searchMedical.setMaximumSize(new Dimension(400, oneLineComponentsHeight));
        searchMedical.addFocusListener(new SelectAllOnFocus());
        return searchMedical;
    }

    private JButton getSearchMedicalButton() {
        searchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
        searchButton.setBorderPainted(true);
        searchButton.setActionCommand("startSearching");
        searchButton.setPreferredSize(new Dimension(30, 30));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO do this in background
                medicalBox.setModel(
                        new DefaultComboBoxModel(
                                medBrowser.findMedicalsByCodeOrDescription(
                                        searchMedical.getText())
                                        .toArray()));
            }
        });
        return searchButton;
    }

    private JComboBox getMedicalJComboBox() {
        medicalBox = new JComboBox();
        medicalBox.setMaximumSize(new Dimension(400, oneLineComponentsHeight));
        if (medArray != null && medArray.size() > 0)
            medicalBox.setModel(new DefaultComboBoxModel(medArray.toArray()));
        if (thRow != null && thRow.getMedical() != null)
            medicalBox.setSelectedItem(thRow.getMedical());
        return medicalBox;
    }

    private JPanel getDaysWeeksMonthsPanel() {
        if (dayWeeksMonthsPanel == null) {
            dayWeeksMonthsPanel = new JPanel();
            dayWeeksMonthsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            dayWeeksMonthsPanel.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.therapy.period"),
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            dayWeeksMonthsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
            dayWeeksMonthsPanel.add(getPeriodSpinners());
        }
        return dayWeeksMonthsPanel;
    }

    private JPanel getPeriodSpinners() {
        JPanel daysPanel = new JPanel();
        BoxLayout daysLayout = new BoxLayout(daysPanel, BoxLayout.Y_AXIS);
        daysPanel.setLayout(daysLayout);
        JLabel labelDays = new JLabel(MessageBundle.getMessage("angal.therapy.days"));
        labelDays.setAlignmentX(CENTER_ALIGNMENT);
        jSpinnerDays.addChangeListener(new TimeSpinnerChangeListener());
        jSpinnerDays.setAlignmentX(CENTER_ALIGNMENT);
        daysPanel.add(labelDays);
        daysPanel.add(jSpinnerDays);

        JPanel weeksPanel = new JPanel();
        BoxLayout weeksLayout = new BoxLayout(weeksPanel, BoxLayout.Y_AXIS);
        weeksPanel.setLayout(weeksLayout);
        JLabel labelWeeks = new JLabel(MessageBundle.getMessage("angal.therapy.weeks"));
        labelWeeks.setAlignmentX(CENTER_ALIGNMENT);
        jSpinnerWeeks.addChangeListener(new TimeSpinnerChangeListener());
        jSpinnerWeeks.setAlignmentX(CENTER_ALIGNMENT);
        weeksPanel.add(labelWeeks);
        weeksPanel.add(jSpinnerWeeks);

        JPanel monthsPanel = new JPanel();
        BoxLayout monthsLayout = new BoxLayout(monthsPanel, BoxLayout.Y_AXIS);
        monthsPanel.setLayout(monthsLayout);
        JLabel labelMonths = new JLabel(MessageBundle.getMessage("angal.therapy.months"));
        labelMonths.setAlignmentX(CENTER_ALIGNMENT);
        jSpinnerMonths.addChangeListener(new TimeSpinnerChangeListener());
        jSpinnerMonths.setAlignmentX(CENTER_ALIGNMENT);
        monthsPanel.add(labelMonths);
        monthsPanel.add(jSpinnerMonths);

        JPanel daysWeeksMonthsPanel = new JPanel();
        daysWeeksMonthsPanel.add(daysPanel);
        daysWeeksMonthsPanel.add(weeksPanel);
        daysWeeksMonthsPanel.add(monthsPanel);
        return daysWeeksMonthsPanel;
    }

    class TimeSpinnerChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateDates();
        }
    }

    private JPanel getMedicalsPanel() {
        if (medicalsPanel == null) {
            medicalsPanel = new JPanel();
            medicalsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            medicalsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            medicalsPanel.setLayout(new BoxLayout(medicalsPanel, BoxLayout.Y_AXIS));
            medicalsPanel.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.therapy.pharmaceutical"),
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            medicalsPanel.add(getMedicalSelectionPanel());
            medicalsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        }
        return medicalsPanel;
    }

    private JPanel getFrequencyInDayPanel() {
        if (frequencyInDayPanel == null) {
            frequencyInDayPanel = new JPanel();
            frequencyInDayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            frequencyInDayPanel.setBorder(new TitledBorder(null,
                    MessageBundle.getMessage("angal.therapy.frequencywithinday"), TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            radioButtonSet = getRadioButtonSet(frequencyInDayOptions);
            for (JRadioButton radioButton : radioButtonSet) {
                frequencyInDayPanel.add(radioButton);
            }
            frequencyInDayPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        }
        return frequencyInDayPanel;
    }

    private JPanel getFrequencyInPeriodPanel() {
        if (frequencyInPeriodPanel == null) {
            frequencyInPeriodPanel = new JPanel();
            frequencyInPeriodPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            frequencyInPeriodPanel.setBorder(new TitledBorder(null,
                    MessageBundle.getMessage("angal.therapy.frequencywithinperiod"), TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            frequencyInPeriodPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
            frequencyInPeriodPanel.add(new JLabel(MessageBundle.getMessage("angal.therapy.every")));
            frequencyInPeriodPanel.add(getSpinnerFreqInPeriod());
            frequencyInPeriodPanel.add(new JLabel(MessageBundle.getMessage("angal.therapy.daydays")));
            frequencyInPeriodPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        }
        return frequencyInPeriodPanel;
    }

    private ArrayList<JRadioButton> getRadioButtonSet(int frequencyInDayOptions) {

        radioButtonSet = new ArrayList<JRadioButton>();
        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < frequencyInDayOptions; i++) {
            JRadioButton radioButton = new JRadioButton(radioButtonLabels[i]);
            radioButton.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        getFreqInDay();
                        enableQuantityInputFields();
                    }
                }
            });
            radioButtonSet.add(radioButton);
            buttonGroup.add(radioButton);
        }

        return radioButtonSet;
    }

    private void enableQuantityInputFields() {
        int freq = getFreqInDay();
        enableQuantityField(qtyField2, freq > 1);
        enableQuantityField(qtyField3, freq > 2);
        enableQuantityField(qtyField4, freq > 3);
    }

    private void enableQuantityField(JFormattedTextField field, boolean enable) {
        field.setEnabled(enable);
        if (!enable)
            field.setText("0.0");
    }

    private JPanel getQuantityPanel() {
        //TODO add more quantity fields depending on the freq checkbox
        if (quantityPanel == null) {
            quantityPanel = new JPanel();
            quantityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            quantityPanel.setBorder(new TitledBorder(null,
                    MessageBundle.getMessage("angal.therapy.quantity"), TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            JPanel springPanel = new JPanel(new SpringLayout());
            springPanel.add(new JLabel("First Dose: "));
            qtyField1 = FormattedTextField.getDecimalField(20, true);
            springPanel.add(qtyField1);
            springPanel.add(new JLabel("Second Dose: "));
            qtyField2 = FormattedTextField.getDecimalField(20, true);
            springPanel.add(qtyField2);
            springPanel.add(new JLabel("Third Dose: "));
            qtyField3 = FormattedTextField.getDecimalField(20, true);
            springPanel.add(qtyField3);
            springPanel.add(new JLabel("Fourth Dose: "));
            qtyField4 = FormattedTextField.getDecimalField(20, true);
            springPanel.add(qtyField4);
            SpringUtilities.makeCompactGrid(springPanel,
                    4, 2,   //rows, cols
                    0, 6,   //initX, initY
                    6, 6);  //xPad, yPad
            quantityPanel.add(springPanel);
        }
        return quantityPanel;
    }

    private JSpinner getSpinnerFreqInPeriod() {
        jSpinnerFreqInPeriod = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        jSpinnerFreqInPeriod.setAlignmentX(Component.LEFT_ALIGNMENT);
        jSpinnerFreqInPeriod.setMaximumSize(new Dimension(Short.MAX_VALUE, oneLineComponentsHeight));
        return jSpinnerFreqInPeriod;
    }

    private JDateChooser getStartDate() {
        if (therapyStartDate == null) {
            therapyStartDate = new JDateChooser(thRow.getStartDate() != null ? thRow.getStartDate().getTime() : new Date());
            therapyStartDate.setLocale(new Locale(GeneralData.LANGUAGE));
            therapyStartDate.setDateFormatString(dateFormat.toPattern());
            therapyStartDate.addPropertyChangeListener("date", new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    updateDates();
                }
            });
        }
        return therapyStartDate;
    }

    private void updateDates() {
        therapyEndDate = new GregorianCalendar();
        therapyEndDate.setTime(therapyStartDate.getDate());
        therapyEndDate.add(GregorianCalendar.DAY_OF_YEAR, (Integer) jSpinnerDays.getValue());
        therapyEndDate.add(GregorianCalendar.WEEK_OF_YEAR, (Integer) jSpinnerWeeks.getValue());
        therapyEndDate.add(GregorianCalendar.MONTH, (Integer) jSpinnerMonths.getValue());
        // Update next visit date
        nextVisitChooser.setDate(therapyEndDate.getTime());
        // Update end therapy label
        endDateLabel.setText(dateFormat.format(therapyEndDate.getTime()));
    }

    private JPanel getStartEndDatePanel() {
        if (startEndDatePanel == null) {
            startEndDatePanel = new JPanel();
            startEndDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            startEndDatePanel.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.therapy.startsdashend"),
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            startEndDatePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            startEndDatePanel.add(getStartDatePanel());
            startEndDatePanel.add(getEndDatePanel());

        }
        return startEndDatePanel;
    }

    private JPanel getStartDatePanel() {
        if (startDatePanel == null) {
            startDatePanel = new JPanel();
            startDatePanel.setLayout(new BoxLayout(startDatePanel, BoxLayout.Y_AXIS));
            JLabel startLabel = new JLabel(MessageBundle.getMessage("angal.therapy.start"));
            startLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            startDatePanel.add(startLabel);
            startDatePanel.add(getStartDate());
        }
        return startDatePanel;
    }

    private JPanel getEndDatePanel() {
        endDatePanel = new JPanel();
        endDatePanel.setLayout(new BoxLayout(endDatePanel, BoxLayout.Y_AXIS));
        JLabel endDateLabel = new JLabel(MessageBundle.getMessage("angal.therapy.end"));
        endDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        endDatePanel.add(endDateLabel);
        endDatePanel.add(getEndDateField());
        return endDatePanel;
    }

    private JPanel getNotePanel() {
        if (notePanel == null) {
            notePanel = new JPanel();
            notePanel.setAlignmentY(Component.TOP_ALIGNMENT);
            notePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            notePanel.setBorder(new TitledBorder(null, MessageBundle.getMessage("angal.therapy.note"),
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            notePanel.setLayout(new BorderLayout(0, 0));
            notePanel.add(getNoteScrollPane());
        }
        return notePanel;
    }

    private JScrollPane getNoteScrollPane() {
        if (noteScrollPane == null) {
            noteScrollPane = new JScrollPane(getNoteTextArea());
            noteScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        }
        return noteScrollPane;
    }

    private JTextArea getNoteTextArea() {
        if (noteTextArea == null) {
            noteTextArea = new JTextArea();
            noteTextArea.setLineWrap(true);
        }
        return noteTextArea;
    }

    private JPanel getIconMedicalPanel() {
        if (iconMedicalPanel == null) {
            iconMedicalPanel = new JPanel();
            iconMedicalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            iconMedicalPanel.setLayout(new BoxLayout(iconMedicalPanel, BoxLayout.X_AXIS));

            JPanel iconPanel = new JPanel();
            iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS));
            IconButton iconButton = new IconButton(new ImageIcon("rsc/icons/medical_dialog.png"));
            iconButton.setAlignmentY(Component.TOP_ALIGNMENT);
            iconPanel.add(iconButton);

            iconMedicalPanel.add(iconPanel);
            iconMedicalPanel.add(getMedicalsPanel());

        }
        return iconMedicalPanel;
    }

    private JPanel getIconFrequenciesPanel() {
        if (iconFrequenciesPanel == null) {
            iconFrequenciesPanel = new JPanel();
            iconFrequenciesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            iconFrequenciesPanel.setLayout(new BoxLayout(iconFrequenciesPanel, BoxLayout.X_AXIS));

            JPanel iconPanel = new JPanel();
            iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS));
            IconButton iconButton = new IconButton(new ImageIcon("rsc/icons/clock_dialog.png"));
            iconButton.setAlignmentY(Component.TOP_ALIGNMENT);
            iconPanel.add(iconButton);
            iconFrequenciesPanel.add(iconPanel);
            iconFrequenciesPanel.add(getFrequenciesPanel());
        }
        return iconFrequenciesPanel;
    }

    private JPanel getIconPeriodPanel() {
        if (iconPeriodPanel == null) {
            iconPeriodPanel = new JPanel();
            iconPeriodPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            iconPeriodPanel.setLayout(new BoxLayout(iconPeriodPanel,BoxLayout.X_AXIS));

            JPanel iconPanel = new JPanel();
            iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS));
            IconButton iconButton = new IconButton(new ImageIcon("rsc/icons/calendar_dialog.png"));
            iconButton.setAlignmentY(Component.TOP_ALIGNMENT);
            iconPanel.add(iconButton);
            iconPeriodPanel.add(iconPanel);

            JPanel periodPanel = new JPanel();
            periodPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            periodPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            periodPanel.setLayout(new BoxLayout(periodPanel, BoxLayout.Y_AXIS));
            periodPanel.add(getDaysWeeksMonthsPanel());
            periodPanel.add(getStartEndDatePanel());
//            periodPanel.add(getNextVisitPanel());
            iconPeriodPanel.add(periodPanel);
        }
        return iconPeriodPanel;
    }

    private JPanel getIconNotePanel() {
        if (iconNotePanel == null) {
            iconNotePanel = new JPanel();
            iconNotePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            iconNotePanel.setLayout(new BoxLayout(iconNotePanel,
                    BoxLayout.X_AXIS));

            JPanel iconPanel = new JPanel();
            iconPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS));
            iconPanel.add(new IconButton(new ImageIcon("rsc/icons/list_dialog.png")));
            iconNotePanel.add(iconPanel);
            iconNotePanel.add(getNotePanel());
        }
        return iconNotePanel;
    }

    private JPanel getFrequenciesPanel() {
        if (frequenciesPanel == null) {
            frequenciesPanel = new JPanel();
            frequenciesPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            frequenciesPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            frequenciesPanel.setLayout(new BoxLayout(frequenciesPanel, BoxLayout.Y_AXIS));
            frequenciesPanel.add(getFrequencyInPeriodPanel());
            frequenciesPanel.add(getFrequencyInDayPanel());
            frequenciesPanel.add(getQuantityPanel());
        }
        return frequenciesPanel;
    }

    private JLabel getEndDateField() {
        if (endDateLabel == null) {
            endDateLabel = new JLabel("");
            endDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            updateDates();
        }
        return endDateLabel;
    }

    private JButton getButtonCancel() {
        if (buttonCancel == null) {
            buttonCancel = new JButton(MessageBundle.getMessage("angal.therapy.cancel"));
            buttonCancel.setMnemonic(KeyEvent.VK_N);
            buttonCancel.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    dispose();
                }
            });
        }
        return buttonCancel;
    }

    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton(MessageBundle.getMessage("angal.therapy.okm"));
            okButton.setMnemonic(KeyEvent.VK_O);
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    if ((Integer) jSpinnerDays.getValue() == 0 &&
                            (Integer) jSpinnerWeeks.getValue() == 0 &&
                            (Integer) jSpinnerMonths.getValue() == 0) {
                        // Therapy period must be greater then zero
                        JOptionPane.showMessageDialog(ChronicTherapyEntryForm.this,
                                "Please set a valid period for the therapy.",
                                MessageBundle.getMessage("angal.therapy.warning"),
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (createNewTherapy())
                        dispose();
                }
            });
        }
        return okButton;
    }

    private boolean createNewTherapy() {
        Medical medical = (Medical) medicalBox.getSelectedItem();
        if (medical == null) {
            JOptionPane.showMessageDialog(
                    ChronicTherapyEntryForm.this,
                    MessageBundle.getMessage("angal.therapy.selectapharmaceutical"),
                    MessageBundle.getMessage("angal.therapy.warning"),
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        Double qty = FormattedTextField.parseFieldValueToDouble(qtyField1);
        if (qty == 0.) {
            JOptionPane.showMessageDialog(
                    ChronicTherapyEntryForm.this,
                    MessageBundle.getMessage("angal.therapy.pleaseinsertaquantitygreaterthanzero"),
                    MessageBundle.getMessage("angal.therapy.warning"),
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.setTime(therapyStartDate.getDate());
        GregorianCalendar endDate = therapyEndDate;
        int freqInDay = getFreqInDay();
        int freqInPeriod = Integer.parseInt(jSpinnerFreqInPeriod.getValue().toString());
        String note = noteTextArea.getText();

        thRow.setPatID(patID);
        thRow.setOpdCode(opdCode);
        thRow.setStartDate(startDate);
        thRow.setEndDate(endDate);
        thRow.setMedical(medical);
        thRow.setQty(qty);
        thRow.setQty2(FormattedTextField.parseFieldValueToDouble(qtyField2));
        thRow.setQty3(FormattedTextField.parseFieldValueToDouble(qtyField3));
        thRow.setQty4(FormattedTextField.parseFieldValueToDouble(qtyField4));
        thRow.setUnitID(0); //TODO: UoM table
        thRow.setFreqInDay(freqInDay);
        thRow.setFreqInPeriod(freqInPeriod);
        thRow.setNote(note);
        thRow.setNotify(false);
        thRow.setSms(false);

        GregorianCalendar nextVisitCalendar = new GregorianCalendar();
        nextVisitCalendar.setTime(nextVisitChooser.getDate());
        if (!edit)
            chronicManager.addTherapy(thRow);
        return true;
    }

    private void closeWindow() {
        dispose();
    }

    private int getFreqInDay() {
        for (JRadioButton button : radioButtonSet) {
            if (button.isSelected()) {
                freqInDay = radioButtonSet.indexOf(button) + 1;
            }
        }
        return freqInDay;
    }

    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getOkButton());
            buttonPanel.add(getButtonCancel());
        }
        return buttonPanel;
    }
}
