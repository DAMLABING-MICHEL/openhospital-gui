package org.isf.malnutrition.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.malnutrition.model.MalnutritionVisit;
import org.isf.utils.gui.SpringUtilities;
import org.isf.utils.jobjects.BorderedPanel;
import org.isf.utils.jobjects.FormattedTextField;
import org.isf.utils.time.Converters;

import com.toedter.calendar.JDateChooser;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;

/**
 * Created by nicosalvato on 2017-02-11.
 * Contact: nicosalvato@gmail.com
 */
public class MalnutritionVisitPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//    private MalnutritionManager manager = MalnutritionManager.getInstance();
    private JDateChooser visitDateField;
    private MalnutritionVisit malnutritionVisit;
    private static final String[] OUTCOME_OPTIONS = {
            null,
            MalnutritionVisit.CURED,
            MalnutritionVisit.NON_RESPONDER,
            MalnutritionVisit.DEFAULTER,
            MalnutritionVisit.MEDICAL_TRANSFER,
            MalnutritionVisit.TRANSFER_OUT,
            MalnutritionVisit.DEATH,
            MalnutritionVisit.SELF_DISCHARGE
//            MalnutritionVisit.UNKNOWN
    };
    private static final String[] OEADEMA_OPTIONS = {
            null,
            "0",
            "1+",
            "2+",
            "3+"
    };
    private static final String[] WH_OPTIONS = {
            null,
            "\u226460%",
            "61-70%",
            "71-75%",
            "76-80%",
            "81-85%",
            ">85%"
//            "\u2265100%"
    };
    private static final String[] KM_OPTIONS = {
            null,
            "K",
            "M",
            "MK"
    };

    MalnutritionVisitPanel(MalnutritionVisit malnutritionVisit) {
        this.malnutritionVisit = malnutritionVisit;
        this.setLayout(new BorderLayout());
        //this.setPreferredSize(new Dimension(200, 240));
        this.add(getNorhtContainer(), BorderLayout.NORTH);
        this.add(getCenterPanel(), BorderLayout.CENTER);
        this.add(getBottomPanel(), BorderLayout.SOUTH);
    }

    public MalnutritionVisit getMalnutritionVisit() {
        return this.malnutritionVisit;
    }
    
    private JPanel getNorhtContainer() {
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(getLeftPanel(), BorderLayout.WEST);
        northContainer.add(getRightPanel(), BorderLayout.CENTER);
        return northContainer;
    }

    private JPanel getLeftPanel() {
        JPanel leftPanel = new JPanel(new SpringLayout());
        leftPanel.setBorder(new EmptyBorder(15, 15, 5 ,15));
        //leftPanel.setPreferredSize(new Dimension(300, 130));
        leftPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.visitdate")));
        leftPanel.add(getVisitDateField());
        leftPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.weight")));
        leftPanel.add(getWeightField());
        leftPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.muac")));
        leftPanel.add(getMuacField());
        if (malnutritionVisit.getType().equals(MalnutritionVisit.ADMISSION)) {
            leftPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.oedema")));
            leftPanel.add(getOedemaField());
            leftPanel.add(new JLabel(""));
            leftPanel.add(new JLabel(""));
            SpringUtilities.makeGrid(leftPanel, 5, 2, 5, 10, 5, 5);
        } else if (malnutritionVisit.getType().equals(MalnutritionVisit.DISCHARGE)) {
            leftPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.minweight")));
            leftPanel.add(getMinWeightField());
            SpringUtilities.makeGrid(leftPanel, 4, 2, 5, 10, 5, 5);
        }
        
        return leftPanel;
    }

    private JPanel getRightPanel() {
        JPanel rightPanel = new JPanel(new SpringLayout());
        rightPanel.setBorder(new EmptyBorder(15, 15, 5 ,15));
        //rightPanel.setPreferredSize(new Dimension(300, 130));
        if (malnutritionVisit.getType().equals(MalnutritionVisit.ADMISSION)) {
        	rightPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.relapse")));
        	rightPanel.add(getRelapseField());
            rightPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.readmission")));
            rightPanel.add(getReadmissionField());
            rightPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.height")));
            rightPanel.add(getHeightField());
            rightPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.wh")));
            rightPanel.add(getWhField());
            rightPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.km")));
            rightPanel.add(getKmField());
            SpringUtilities.makeGrid(rightPanel, 5, 2, 5, 10, 5, 5);
        } else if (malnutritionVisit.getType().equals(MalnutritionVisit.DISCHARGE)) {
            rightPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.outcome")));
            rightPanel.add(getOutcomeField());
            rightPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.height")));
            rightPanel.add(getHeightField());
            rightPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.wh")));
            rightPanel.add(getWhField());
            rightPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.minweightdate")));
            rightPanel.add(getMinWeightDateField());
            SpringUtilities.makeGrid(rightPanel, 4, 2, 5, 10, 5, 5);
        }
        return rightPanel;
    }

    private JPanel getCenterPanel() {
    	JPanel centerPanel = new JPanel(new BorderLayout());
    	if (malnutritionVisit.getType().equals(MalnutritionVisit.DISCHARGE)) {
    		BorderedPanel.setMyBorderTick(centerPanel, MessageBundle.getMessage("angal.malnutrition.co-morbidities"), 15);
    		centerPanel.add(getHivPanel(), BorderLayout.NORTH);
    		centerPanel.add(getMorbidityPanel(), BorderLayout.CENTER);
    	} 
    	return centerPanel;
    }
    
    private JPanel getHivPanel() {
        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setVgap(10);
        panel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.hiv")));
        panel.add(getHivPositiveField());
        panel.add(getHivNegativeField());
        panel.add(getHivUnknownField());
        return panel;
    }
    
    private JPanel getMorbidityPanel() {
		JPanel morbidityPanel = new JPanel();
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0};
		morbidityPanel.setLayout(gbl_panel_1);
		tbField = new JCheckBox(MessageBundle.getMessage("angal.malnutrition.tb"));
		GridBagConstraints gbc_tbField = new GridBagConstraints();
		gbc_tbField.anchor = GridBagConstraints.WEST;
		gbc_tbField.insets = new Insets(0, 0, 10, 0);
		gbc_tbField.gridx = 0;
		gbc_tbField.gridy = 0;
		morbidityPanel.add(tbField, gbc_tbField);
		tbField.setSelected(malnutritionVisit.isTb());
		sepsisField = new JCheckBox(MessageBundle.getMessage("angal.malnutrition.sepsis"));
		GridBagConstraints gbc_sepsisField = new GridBagConstraints();
		gbc_sepsisField.anchor = GridBagConstraints.WEST;
		gbc_sepsisField.insets = new Insets(0, 0, 10, 0);
		gbc_sepsisField.gridx = 1;
		gbc_sepsisField.gridy = 0;
		morbidityPanel.add(sepsisField, gbc_sepsisField);
		sepsisField.setSelected(malnutritionVisit.isSepsis());
		anaemiaField = new JCheckBox(MessageBundle.getMessage("angal.malnutrition.anaemia"));
		GridBagConstraints gbc_anaemiaField = new GridBagConstraints();
		gbc_anaemiaField.anchor = GridBagConstraints.WEST;
		gbc_anaemiaField.insets = new Insets(0, 0, 10, 0);
		gbc_anaemiaField.gridx = 2;
		gbc_anaemiaField.gridy = 0;
		morbidityPanel.add(anaemiaField, gbc_anaemiaField);
		anaemiaField.setSelected(malnutritionVisit.isAnaemia());
		pneumoniaField = new JCheckBox(MessageBundle.getMessage("angal.malnutrition.pneumonia"));
		GridBagConstraints gbc_pneumoniaField = new GridBagConstraints();
		gbc_pneumoniaField.anchor = GridBagConstraints.WEST;
		gbc_pneumoniaField.insets = new Insets(0, 0, 10, 0);
		gbc_pneumoniaField.gridx = 3;
		gbc_pneumoniaField.gridy = 0;
		morbidityPanel.add(pneumoniaField, gbc_pneumoniaField);
		pneumoniaField.setSelected(malnutritionVisit.isPneumonia());
		malariaField = new JCheckBox(MessageBundle.getMessage("angal.malnutrition.malaria"));
		GridBagConstraints gbc_malariaField = new GridBagConstraints();
		gbc_malariaField.anchor = GridBagConstraints.WEST;
		gbc_malariaField.insets = new Insets(0, 0, 10, 0);
		gbc_malariaField.gridx = 0;
		gbc_malariaField.gridy = 1;
		morbidityPanel.add(malariaField, gbc_malariaField);
		malariaField.setSelected(malnutritionVisit.isMalaria());
		cpField = new JCheckBox(MessageBundle.getMessage("angal.malnutrition.cp"));
		GridBagConstraints gbc_cpField = new GridBagConstraints();
		gbc_cpField.anchor = GridBagConstraints.WEST;
		gbc_cpField.insets = new Insets(0, 0, 10, 0);
		gbc_cpField.gridx = 1;
		gbc_cpField.gridy = 1;
		morbidityPanel.add(cpField, gbc_cpField);
		cpField.setSelected(malnutritionVisit.isCP());
		rickettsField = new JCheckBox(MessageBundle.getMessage("angal.malnutrition.ricketts"));
		GridBagConstraints gbc_rickettsField = new GridBagConstraints();
		gbc_rickettsField.anchor = GridBagConstraints.WEST;
		gbc_rickettsField.insets = new Insets(0, 0, 10, 0);
		gbc_rickettsField.gridx = 2;
		gbc_rickettsField.gridy = 1;
		morbidityPanel.add(rickettsField, gbc_rickettsField);
		rickettsField.setSelected(malnutritionVisit.isRicketts());
		otherField = new JCheckBox(MessageBundle.getMessage("angal.malnutrition.other"));
		GridBagConstraints gbc_otherField = new GridBagConstraints();
		gbc_otherField.anchor = GridBagConstraints.WEST;
		gbc_otherField.insets = new Insets(0, 0, 10, 0);
		gbc_otherField.gridx = 3;
		gbc_otherField.gridy = 1;
		morbidityPanel.add(otherField, gbc_otherField);
		otherField.setSelected(malnutritionVisit.isOther());
    	return morbidityPanel;
    }

    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new SpringLayout());
        bottomPanel.setBorder(new EmptyBorder(15, 15, 15 ,15));
//        bottomPanel.setPreferredSize(new Dimension(300, 50));
        if (malnutritionVisit.getType().equals(MalnutritionVisit.ADMISSION)) {
        	bottomPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.fromothertfp")));
            bottomPanel.add(getFromOtherTfpField());
            bottomPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.fromotherotp")));
            bottomPanel.add(getFromOtherOtpField());
            bottomPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.referredby")));
            bottomPanel.add(getReferredByField());
            
        } else if (malnutritionVisit.getType().equals(MalnutritionVisit.DISCHARGE)) {
        	bottomPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.transfertoothertfp")));
        	bottomPanel.add(getTransferOtherOTPField());
        	bottomPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.medicaltransfer")));
        	bottomPanel.add(getmedicalTransferField());
            bottomPanel.add(new JLabel(MessageBundle.getMessage("angal.malnutrition.remarks")));
            bottomPanel.add(getRemarksField());
        }
        SpringUtilities.makeCompactGrid(bottomPanel, 3, 2, 5, 10, 5, 5);
        return bottomPanel;
    }

    private JDateChooser getVisitDateField() {
        Date defaultDate = new Date();
        if (malnutritionVisit.getVisitDate() != null)
            defaultDate = Converters.toDate(malnutritionVisit.getVisitDate());
        else if (malnutritionVisit.getType().equals(MalnutritionVisit.ADMISSION))
            defaultDate = Converters.toDate(malnutritionVisit.getAdmission().getAdmDate());
        else if (malnutritionVisit.getType().equals(MalnutritionVisit.DISCHARGE))
            defaultDate = Converters.toDate(malnutritionVisit.getAdmission().getDisDate());

        visitDateField = new JDateChooser(defaultDate, "dd/MM/yy");
        visitDateField.setLocale(new Locale(GeneralData.LANGUAGE));
        visitDateField.setDateFormatString("dd/MM/yyyy");
        visitDateField.setMaxSelectableDate(malnutritionVisit.getAdmission().getDisDate() != null ?
                Converters.toDate(malnutritionVisit.getAdmission().getDisDate()) : new Date());
        visitDateField.setMinSelectableDate(Converters.toDate(malnutritionVisit.getAdmission().getAdmDate()));
        return visitDateField;
    }

    void addPropertyChangeListenerToDateField(PropertyChangeListener propertyChangeListener) {
        visitDateField.getDateEditor().addPropertyChangeListener(propertyChangeListener);
    }

    void setMinWeightDateFieldMinSelectableValue(Date date) {
        minWeightDateField.setMinSelectableDate(date);
    }

    void setVisitDateFieldMinSelectableValue(Date date) {
        visitDateField.setMinSelectableDate(date);
    }

    void setMinWeightDateFieldMaxSelectableValue(Date date) {
        minWeightDateField.setMaxSelectableDate(date);
    }

    private JCheckBox readmissionField;
    private JCheckBox getReadmissionField() {
        readmissionField = new JCheckBox();
        readmissionField.setSelected(Boolean.TRUE.equals(malnutritionVisit.isReadmission()));
        return readmissionField;
    }

    private JFormattedTextField weightField;
    private JFormattedTextField getWeightField() {
        weightField = FormattedTextField.getDecimalField(10, true, malnutritionVisit.getWeight());
//        weightField.addPropertyChangeListener(new ComputeWhPropertyChangeListener());
        return weightField;
    }

    private JFormattedTextField heightField;
    private JFormattedTextField getHeightField() {
        heightField = FormattedTextField.getDecimalField(10, true, malnutritionVisit.getHeight());
//        heightField.addPropertyChangeListener(new ComputeWhPropertyChangeListener());
        return heightField;
    }

    private JFormattedTextField muacField;
    private JFormattedTextField getMuacField() {
        muacField = FormattedTextField.getDecimalField(10, true, malnutritionVisit.getMuac());
        return muacField;
    }

    private JComboBox whField;
    private JComboBox getWhField() {
        whField = new JComboBox(WH_OPTIONS);
        whField.setSelectedItem(convertWhToGuiOption(malnutritionVisit.getWh()));
        return whField;
    }

    String convertWhToGuiOption(Integer whSup) {
        Integer[] supValues = new Integer[] {null, 60, 70, 75, 80, 85, 99};
        return WH_OPTIONS[Arrays.asList(supValues).indexOf(whSup)];
    }

    private JComboBox oedemaField;
    private JComboBox getOedemaField() {
        oedemaField = new JComboBox(OEADEMA_OPTIONS);
        oedemaField.setSelectedItem(convertOedemaToString(malnutritionVisit.getOedema()));
        return oedemaField;
    }
    
    private JCheckBox relapseField;
    private JCheckBox getRelapseField() {
    	relapseField = new JCheckBox();
    	relapseField.setSelected(Boolean.TRUE.equals(malnutritionVisit.getRelapse()));
        return relapseField;
    }

    private String convertOedemaToString(Integer oedema) {
        String formattedValue = null;
        if (oedema != null) {
            formattedValue = oedema > 0 ? oedema + "+" : oedema + "";
        }
        return formattedValue;
    }

    private JComboBox kmField;
    private JComboBox getKmField() {
        kmField = new JComboBox(KM_OPTIONS);
        kmField.setSelectedItem(malnutritionVisit.getKm());
        return kmField;
    }

    private JTextField referredbyField;
    private JTextField getReferredByField() {
        referredbyField = new JTextField(10);
        referredbyField.setText(malnutritionVisit.getReferredBy());
        return referredbyField;
    }

    private JComboBox outcomeField;
    private JComboBox getOutcomeField() {
        outcomeField = new JComboBox(OUTCOME_OPTIONS);
        outcomeField.setSelectedItem(malnutritionVisit.getOutcome());
        return outcomeField;
    }

    void addItemListenerToOutcomeField(ItemListener itemListener) {
        outcomeField.addItemListener(itemListener);
    }

    private JFormattedTextField minWeightField;
    private JFormattedTextField getMinWeightField() {
        minWeightField = FormattedTextField.getDecimalField(10, true, malnutritionVisit.getMinWeight());
        return minWeightField;
    }

    private JDateChooser minWeightDateField;
    private JDateChooser getMinWeightDateField() {
        minWeightDateField = new JDateChooser(malnutritionVisit.getMinWeightDate() != null ?
                Converters.toDate(malnutritionVisit.getMinWeightDate()) : null,
                "dd/MM/yy");
        minWeightDateField.setLocale(new Locale(GeneralData.LANGUAGE));
        minWeightDateField.setDateFormatString("dd/MM/yyyy");
        minWeightDateField.setMaxSelectableDate(getVisitDateFieldValue() != null ?
                Converters.toDate(getVisitDateFieldValue()) : new Date());
        if (malnutritionVisit.getAdmission() != null && malnutritionVisit.getAdmission().getAdmDate() != null)
            minWeightDateField.setMinSelectableDate(Converters.toDate(malnutritionVisit.getAdmission().getAdmDate()));
        return minWeightDateField;
    }

    private ButtonGroup hivGroup = new ButtonGroup();
    private JRadioButton hivPositiveField;
    private JRadioButton hivNegativeField;
    private JRadioButton hivUnknownField;
    private JRadioButton getHivPositiveField() {
        hivPositiveField = new JRadioButton(MessageBundle.getMessage("angal.malnutrition.yes"));
        hivGroup.add(hivPositiveField);
        hivPositiveField.setSelected(malnutritionVisit.getHiv() != null && malnutritionVisit.getHiv().equals("Y"));
        return hivPositiveField;
    }
    private JRadioButton getHivNegativeField() {
        hivNegativeField = new JRadioButton(MessageBundle.getMessage("angal.malnutrition.no"));
        hivGroup.add(hivNegativeField);
        hivNegativeField.setSelected(malnutritionVisit.getHiv() != null && malnutritionVisit.getHiv().equals("N"));
        return hivNegativeField;
    }
    private JRadioButton getHivUnknownField() {
        hivUnknownField = new JRadioButton(MessageBundle.getMessage("angal.malnutrition.unknown"));
        hivGroup.add(hivUnknownField);
        hivUnknownField.setSelected(malnutritionVisit.getHiv() == null || malnutritionVisit.getHiv().equals("U"));
        return hivUnknownField;
    }
    
    private JCheckBox tbField;
    private JCheckBox sepsisField;
    private JCheckBox anaemiaField;
    private JCheckBox pneumoniaField;
    private JCheckBox malariaField;
    private JCheckBox cpField;
    private JCheckBox rickettsField;
    private JCheckBox otherField;
    private JTextField remarksField;
    private JTextField getRemarksField() {
        remarksField = new JTextField(malnutritionVisit.getRemarks());
        return remarksField;
    }

    private JCheckBox fromOtherTFPField;
    private JCheckBox getFromOtherTfpField() {
        fromOtherTFPField = new JCheckBox();
        fromOtherTFPField.setSelected(malnutritionVisit.isFromOtherTFP());
        fromOtherTFPField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox thisCheckBox = (JCheckBox) e.getSource();
				if (thisCheckBox.isSelected()) {
					fromOtherTFPField.setSelected(true);
					fromOtherOTPField.setSelected(false);
				}
				else fromOtherTFPField.setSelected(false);
			}
		});
        return fromOtherTFPField;
    }
    
    private JCheckBox fromOtherOTPField;
    private JCheckBox getFromOtherOtpField() {
        fromOtherOTPField = new JCheckBox();
        fromOtherOTPField.setSelected(malnutritionVisit.isFromOtherOTP());
        fromOtherOTPField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox thisCheckBox = (JCheckBox) e.getSource();
				if (thisCheckBox.isSelected()) {
					fromOtherOTPField.setSelected(true);
					fromOtherTFPField.setSelected(false);
				}
				else fromOtherOTPField.setSelected(false);
			}
		});
        return fromOtherOTPField;
    }
    
    private JCheckBox transferToOtherOTPField;
    private JCheckBox getTransferOtherOTPField() {
    	transferToOtherOTPField = new JCheckBox();
    	transferToOtherOTPField.setSelected(malnutritionVisit.isTransferToOtherOTP());
    	transferToOtherOTPField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox thisCheckBox = (JCheckBox) e.getSource();
				if (thisCheckBox.isSelected()) {
					transferToOtherOTPField.setSelected(true);
					medicalTransferField.setSelected(false);
				}
				else transferToOtherOTPField.setSelected(false);
			}
		});
        return transferToOtherOTPField;
    }
    
    private JCheckBox medicalTransferField;
    private JCheckBox getmedicalTransferField() {
    	medicalTransferField = new JCheckBox();
    	medicalTransferField.setSelected(malnutritionVisit.isMedicalTransfer());
    	medicalTransferField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox thisCheckBox = (JCheckBox) e.getSource();
				if (thisCheckBox.isSelected()) {
					medicalTransferField.setSelected(true);
					transferToOtherOTPField.setSelected(false);
				}
				else medicalTransferField.setSelected(false);
			}
		});
        return medicalTransferField;
    }

    public GregorianCalendar getVisitDateFieldValue() {
        return Converters.toCalendar(visitDateField.getDate());
    }

    public GregorianCalendar getMinWeightDateFieldValue() {
        return Converters.toCalendar(minWeightDateField.getDate());
    }
    
    public boolean isRelapseFieldChecked() {
        return relapseField.isSelected();
    }

    public boolean isReadmissionFieldChecked() {
        return readmissionField.isSelected();
    }

    public BigDecimal getWeightFieldValue() {
        return FormattedTextField.parseFieldValueToBigDecimal(weightField);
    }

    public BigDecimal getHeightFieldValue() {
        return FormattedTextField.parseFieldValueToBigDecimal(heightField);
    }

    public BigDecimal getMuacFieldValue() {
        return FormattedTextField.parseFieldValueToBigDecimal(muacField);
    }

    /**
     * The values returned by this method are meaningful if thought as a superior limits of the intervals reported in
     * WH_OPTIONS array. Original values were: [<=60%, <=70%, <=75%, <=80%, >80%, >=85%, >=100%]
     * Now changed to: [<=60%, 61-70%, 71-75%, 76-80%, 81-85%, >85%]
     * @return {@code Integer} representing a superior limit to W/H value.
     */
    public Integer getWhFieldValue() {
        String selectedWh = (String) whField.getSelectedItem();
        try {
            return parseWhValue(selectedWh);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    static Integer parseWhValue(String input) throws NumberFormatException {
        Integer whValue = null;
        if (input != null && !input.isEmpty()) {
            if (input.startsWith(">"))
                whValue = 99;
            else
                whValue = Integer.valueOf(input.substring(input.length() - 3, input.length() - 1));
        }
        return whValue;
    }

    public void setWhFieldValue(Integer value) {
        whField.setSelectedItem(convertWhToGuiOption(value));
    }

    public Integer getOedemaFieldValue() {
        String oedemaStringValue = (String) oedemaField.getSelectedItem();
        Integer oedemaIntegerValue = null;
        if (oedemaStringValue != null) {
            try {
                oedemaIntegerValue = oedemaStringValue.length() > 1 ?
                        Integer.valueOf(oedemaStringValue.substring(0, 1)) : 0;
            } catch (NumberFormatException nfe) {
                // Leave the value to null
            }
        }
        return oedemaIntegerValue;
    }

    public String getKmFieldValue() {
        return (String) kmField.getSelectedItem();
    }

    public String getReferredByFieldValue() {
        return referredbyField.getText();
    }

    public String getOutcomeFieldValue() {
        return (String) outcomeField.getSelectedItem();
    }

    public BigDecimal getMinWeightFieldValue() {
        return FormattedTextField.parseFieldValueToBigDecimal(minWeightField);
    }

    public String getHivFieldValue() {
        String isHiv = "U";
        if (hivPositiveField.isSelected())
        	isHiv = "Y";
        else if (hivNegativeField.isSelected())
        	isHiv = "N";
        return isHiv;
    }
    
    public Boolean getTbFieldValue() {
		return tbField.isSelected();
	}
    
    public Boolean getSepsisFieldValue() {
		return sepsisField.isSelected();
	}
    
    public Boolean getAnaemiaFieldValue() {
		return anaemiaField.isSelected();
	}
    
    public Boolean getPneumoniaFieldValue() {
		return pneumoniaField.isSelected();
	}
    
    public Boolean getMalariaFieldValue() {
		return malariaField.isSelected();
	}
    
    public Boolean getCPFieldValue() {
		return cpField.isSelected();
	}
    
    public Boolean getRickettsFieldValue() {
		return rickettsField.isSelected();
	}
    
    public Boolean getOtherFieldValue() {
		return otherField.isSelected();
	}
    
    public String getRemarksFieldValue() {
        return remarksField.getText();
    }
    
    public boolean getFromOtherOTPFieldValue() {
        return fromOtherOTPField.isSelected();
    }

    public boolean getFromOtherTFPFieldValue() {
        return fromOtherTFPField.isSelected();
    }
    
    public boolean getTransferToOtherOTPFieldValue() {
    	return transferToOtherOTPField.isSelected();
    }
    
    public boolean getMedicalTransferFieldValue() {
    	return medicalTransferField.isSelected();
    }
}
