package org.isf.disease.gui;

/*------------------------------------------
 * LabEdit - Add/edit a Disease
 * -----------------------------------------
 * modification history
 * 25/01/2006 - Rick, Vero, Pupo - first beta version
 * 03/11/2006 - ross - added flags OPD / IPD
 * 			         - changed title, version is now 1.0 
 * 09/06/2007 - ross - when updating, now the user can change the "dis type" also
 *------------------------------------------*/
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.model.DiseaseType;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.jobjects.VoLimitedTextField;

public class DiseaseEdit extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private EventListenerList diseaseListeners = new EventListenerList();
	
	public interface DiseaseListener extends EventListener {
		public void diseaseUpdated(AWTEvent e);
		public void diseaseInserted(AWTEvent e);
	}
	
	public void addDiseaseListener(DiseaseListener l) {
		diseaseListeners.add(DiseaseListener.class, l);
	}
	
	public void removeDiseaseListener(DiseaseListener listener) {
		diseaseListeners.remove(DiseaseListener.class, listener);
	}
	
	private void fireDiseaseInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
		
		EventListener[] listeners = diseaseListeners.getListeners(DiseaseListener.class);
		for (int i = 0; i < listeners.length; i++)
			((DiseaseListener)listeners[i]).diseaseInserted(event);
	}
	private void fireDiseaseUpdated() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
		
		EventListener[] listeners = diseaseListeners.getListeners(DiseaseListener.class);
		for (int i = 0; i < listeners.length; i++)
			((DiseaseListener)listeners[i]).diseaseUpdated(event);
	}
	
	private static final String VERSION=MessageBundle.getMessage("angal.versione"); 

	private JPanel jContentPane = null;
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JLabel descLabel = null;
	private JLabel codeLabel = null;
	private JTextField descriptionTextField = null;
	private JTextField codeTextField = null;
	private VoLimitedTextField minimumAgeTextField = null;
	private VoLimitedTextField maximumAgeTextField = null;
	private JLabel typeLabel = null;
	private JComboBox typeComboBox = null;
	private Disease disease = null;
	private boolean insert = false;
	private JPanel jNewPatientPanel = null;
//	private JLabel inludeOpdLabel = null;
//	private JLabel inludeIpdLabel = null;
	private JCheckBox includeOpdCheckBox  = null;
	private JCheckBox includeIpdInCheckBox  = null;
	private JCheckBox includeIpdOutCheckBox  = null;
	private JCheckBox isFemaleCheckBox  = null;
	private JCheckBox isMaleCheckBox  = null;
	private JCheckBox isChronicCheckBox  = null;
	

	private String lastDescription;

	/**
	 * 
	 * This is the default constructor; we pass the arraylist and the selectedrow
	 * because we need to update them
	 */
	public DiseaseEdit(JFrame parent,Disease old,boolean inserting) {
		super(parent,true);
		insert = inserting;
		disease = old;		//disease will be used for every operation
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
//		Toolkit kit = Toolkit.getDefaultToolkit();
//		Dimension screensize = kit.getScreenSize();
//		pfrmBordX = (screensize.width - (screensize.width / pfrmBase * pfrmWidth)) / 2;
//		pfrmBordY = (screensize.height - (screensize.height / pfrmBase * pfrmHeight)) / 2;
//		this.setBounds(pfrmBordX,pfrmBordY,screensize.width / pfrmBase * pfrmWidth,screensize.height / pfrmBase * pfrmHeight);
		this.setContentPane(getJContentPane());
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.disease.newdisease")+VERSION+")");
		} else {
			this.setTitle(MessageBundle.getMessage("angal.disease.editdisease")+VERSION+")");
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getDataPanel(), java.awt.BorderLayout.CENTER);  // Generated
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);  // Generated
		}
		return jContentPane;
	}
	
	/**
	 * This method initializes dataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDataPanel() {
		if (dataPanel == null) {
			dataPanel = new JPanel();
			GridBagLayout gbl_dataPanel = new GridBagLayout();
			gbl_dataPanel.columnWidths = new int[]{544, 0};
			gbl_dataPanel.rowHeights = new int[]{31, 31, 31, 31, 31, 31, 31, 31, 0};
			gbl_dataPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_dataPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			dataPanel.setLayout(gbl_dataPanel);
			typeLabel = new JLabel(MessageBundle.getMessage("angal.disease.type"));
			GridBagConstraints gbc_typeLabel = new GridBagConstraints();
			gbc_typeLabel.fill = GridBagConstraints.VERTICAL;
			gbc_typeLabel.insets = new Insets(5, 5, 5, 5);
			gbc_typeLabel.gridx = 0;
			gbc_typeLabel.gridy = 0;
			dataPanel.add(typeLabel, gbc_typeLabel);  // Generated
			GridBagConstraints gbc_typeComboBox = new GridBagConstraints();
			gbc_typeComboBox.fill = GridBagConstraints.BOTH;
			gbc_typeComboBox.insets = new Insets(5, 5, 5, 5);
			gbc_typeComboBox.gridx = 0;
			gbc_typeComboBox.gridy = 1;
			dataPanel.add(getTypeComboBox(), gbc_typeComboBox);  // Generated
			codeLabel = new JLabel(MessageBundle.getMessage("angal.disease.code"));
			GridBagConstraints gbc_codeLabel = new GridBagConstraints();
			gbc_codeLabel.fill = GridBagConstraints.VERTICAL;
			gbc_codeLabel.insets = new Insets(5, 5, 5, 5);
			gbc_codeLabel.gridx = 0;
			gbc_codeLabel.gridy = 2;
			dataPanel.add(codeLabel, gbc_codeLabel);  // Generated
			GridBagConstraints gbc_codeTextField = new GridBagConstraints();
			gbc_codeTextField.fill = GridBagConstraints.BOTH;
			gbc_codeTextField.insets = new Insets(0, 0, 5, 5);
			gbc_codeTextField.gridx = 0;
			gbc_codeTextField.gridy = 3;
			dataPanel.add(getCodeTextField(), gbc_codeTextField);  // Generated
			descLabel = new JLabel(MessageBundle.getMessage("angal.disease.description"));
			GridBagConstraints gbc_descLabel = new GridBagConstraints();
			gbc_descLabel.fill = GridBagConstraints.VERTICAL;
			gbc_descLabel.insets = new Insets(5, 5, 5, 5);
			gbc_descLabel.gridx = 0;
			gbc_descLabel.gridy = 4;
			dataPanel.add(descLabel, gbc_descLabel);  // Generated
			GridBagConstraints gbc_descTextField = new GridBagConstraints();
			gbc_descTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_descTextField.insets = new Insets(0, 0, 5, 5);
			gbc_descTextField.gridx = 0;
			gbc_descTextField.gridy = 5;
			dataPanel.add(getDescriptionTextField(), gbc_descTextField);  // Generated
			GridBagConstraints gbc_MinimumAgeLabel = new GridBagConstraints();
			gbc_MinimumAgeLabel.fill = GridBagConstraints.VERTICAL;
			gbc_MinimumAgeLabel.insets = new Insets(0, 0, 5, 5);
			gbc_MinimumAgeLabel.gridx = 0;
			gbc_MinimumAgeLabel.gridy = 6;
			JLabel label = new JLabel("Minimum Age (months)");
			dataPanel.add(label, gbc_MinimumAgeLabel);
			GridBagConstraints gbc_minimumAgeTextField = new GridBagConstraints();
			gbc_minimumAgeTextField.fill = GridBagConstraints.BOTH;
			gbc_minimumAgeTextField.insets = new Insets(0, 0, 5, 5);
			gbc_minimumAgeTextField.gridx = 0;
			gbc_minimumAgeTextField.gridy = 7;
			dataPanel.add(getMinimumAgeTextField(), gbc_minimumAgeTextField);  // Generated
			GridBagConstraints gbc_MaximumAgeLabel = new GridBagConstraints();
			gbc_MaximumAgeLabel.fill = GridBagConstraints.VERTICAL;
			gbc_MaximumAgeLabel.insets = new Insets(0, 0, 5, 5);
			gbc_MaximumAgeLabel.gridx = 0;
			gbc_MaximumAgeLabel.gridy = 8;
			JLabel label_1 = new JLabel("Maximum Age (months)");
			dataPanel.add(label_1, gbc_MaximumAgeLabel);
			GridBagConstraints gbc_maximumAgeTextField = new GridBagConstraints();
			gbc_maximumAgeTextField.fill = GridBagConstraints.BOTH;
			gbc_maximumAgeTextField.insets = new Insets(0, 0, 5, 5);
			gbc_maximumAgeTextField.gridx = 0;
			gbc_maximumAgeTextField.gridy = 9;
			dataPanel.add(getMaximumAgeTextField(), gbc_maximumAgeTextField);  // Generated
			GridBagConstraints gbc_jNewPatientPanel = new GridBagConstraints();
			gbc_jNewPatientPanel.fill = GridBagConstraints.BOTH;
			gbc_jNewPatientPanel.insets = new Insets(0, 0, 0, 5);
			gbc_jNewPatientPanel.gridx = 0;
			gbc_jNewPatientPanel.gridy = 10;
			dataPanel.add(getJFlagsPanel(), gbc_jNewPatientPanel);
		}
		return dataPanel;
	}
	
	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton(), null);  // Generated
			buttonPanel.add(getCancelButton(), null);  // Generated
		}
		return buttonPanel;
	}
	
	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(MessageBundle.getMessage("angal.disease.cancel"));  // Generated
			cancelButton.setMnemonic(KeyEvent.VK_C);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}
	
	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(MessageBundle.getMessage("angal.disease.ok"));  // Generated
			okButton.setMnemonic(KeyEvent.VK_O);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					DiseaseBrowserManager manager = new DiseaseBrowserManager();
					String diseaseDesc = descriptionTextField.getText();
					int minimumAge;
					int maximumAge;
					if (insert){
						String key = codeTextField.getText().trim();
						if (key.equals("")){
							JOptionPane.showMessageDialog(				
									null,
									MessageBundle.getMessage("angal.disease.pleaseinsertacode"),
									MessageBundle.getMessage("angal.hospital"),
									JOptionPane.PLAIN_MESSAGE);
							return;
						}	
						if (key.length()>10){
							JOptionPane.showMessageDialog(				
									null,
									MessageBundle.getMessage("angal.disease.codetoolongmaxchars"),
									MessageBundle.getMessage("angal.hospital"),
									JOptionPane.PLAIN_MESSAGE);
							
							return;	
						}
						
						if (manager.codeControl(key)){
							JOptionPane.showMessageDialog(				
									null,
									MessageBundle.getMessage("angal.disease.codealreadyinuse"),
									MessageBundle.getMessage("angal.hospital"),
									JOptionPane.PLAIN_MESSAGE);
							
							return;	
						}
					}
					if (diseaseDesc.equals("")){
						JOptionPane.showMessageDialog(				
								null,
								MessageBundle.getMessage("angal.disease.pleaseinsertavaliddescription"),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;	
					}
					
					try {
						minimumAge = Integer.parseInt(minimumAgeTextField.getText());
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(				
								null,
								"Please insert a valid minimum age",
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;	
					}
					try {
						maximumAge = Integer.parseInt(maximumAgeTextField.getText());
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(				
								null,
								"Please insert a valid maximum age",
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;	
					}

					//if inserting or description has changed on updating
					if (lastDescription == null || !lastDescription.equals(diseaseDesc)) {
						if (manager.descriptionControl(descriptionTextField.getText(),
								((DiseaseType)typeComboBox.getSelectedItem()).getCode())){
							JOptionPane.showMessageDialog(				
									null,
									MessageBundle.getMessage("angal.disease.diseasealreadypresent"),
									MessageBundle.getMessage("angal.hospital"),
									JOptionPane.PLAIN_MESSAGE);
							
							return;	
						}
					}
					
					disease.setType((DiseaseType)typeComboBox.getSelectedItem());
					disease.setDescription(descriptionTextField.getText());
					disease.setCode(codeTextField.getText().trim().toUpperCase());
					disease.setOpdInclude(includeOpdCheckBox.isSelected());
					disease.setIpdInInclude(includeIpdInCheckBox.isSelected());
					disease.setIpdOutInclude(includeIpdOutCheckBox.isSelected());
					disease.setMinimumAge(minimumAge);
					disease.setMaximumAge(maximumAge);
					disease.setMale(isMaleCheckBox.isSelected());
					disease.setFemale(isFemaleCheckBox.isSelected());
					disease.setChronic(isChronicCheckBox.isSelected());
					
					boolean result = false;
					if (insert) {      // inserting
						result = manager.newDisease(disease);
						if (result) {
							fireDiseaseInserted();
						}
					} else {                          // updating
						result = manager.updateDisease(disease);
						if (result) {
							fireDiseaseUpdated();
						}
					}
					if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.disease.thedatacouldnotbesaved"));
					else  dispose();
				}
			});
		}
		return okButton;
	}
	
	/**
	 * This method initializes descriptionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDescriptionTextField() {
		if (descriptionTextField == null) {
			descriptionTextField = new JTextField();
			if (!insert) {
				lastDescription = disease.getDescription();
				descriptionTextField.setText(lastDescription);
			}
		}
		return descriptionTextField;
	}
	
	private VoLimitedTextField getMinimumAgeTextField() {
		if (minimumAgeTextField == null) {
			minimumAgeTextField = new VoLimitedTextField(3, "0", 5);
			if (!insert) {
				minimumAgeTextField.setText(String.valueOf(disease.getMinimumMonths()));
			}
		}
		return minimumAgeTextField;
	}
	
	private VoLimitedTextField getMaximumAgeTextField() {
		if (maximumAgeTextField == null) {
			maximumAgeTextField = new VoLimitedTextField(3, "0", 5);
			if (!insert) {
				maximumAgeTextField.setText(String.valueOf(disease.getMaximumMonths()));
			}
		}
		return maximumAgeTextField;
	}
	
	/**
	 * This method initializes codeTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCodeTextField() {
		if (codeTextField == null) {
			codeTextField = new VoLimitedTextField(10);
			if (!insert) {
				codeTextField.setText(disease.getCode());
				codeTextField.setEnabled(false);
			}
		}
		return codeTextField;
	}

	
	//private JLabel inludeOpdLabel = null;
	//private JCheckBox includeOpdCheckBox  = null;

	
	public JPanel getJFlagsPanel() {
		if (jNewPatientPanel == null){
			jNewPatientPanel = new JPanel();
			includeOpdCheckBox = new JCheckBox(MessageBundle.getMessage("angal.disease.opd"));
			includeIpdInCheckBox = new JCheckBox(MessageBundle.getMessage("angal.disease.ipdin"));
			includeIpdOutCheckBox = new JCheckBox(MessageBundle.getMessage("angal.disease.ipdout"));
			isMaleCheckBox = new JCheckBox("Male");
			isFemaleCheckBox = new JCheckBox("Female");
			isChronicCheckBox = new JCheckBox("Chronic");
			jNewPatientPanel.add(includeOpdCheckBox);
			jNewPatientPanel.add(includeIpdInCheckBox);
			jNewPatientPanel.add(includeIpdOutCheckBox);
			jNewPatientPanel.add(isMaleCheckBox);
			jNewPatientPanel.add(isFemaleCheckBox);
			jNewPatientPanel.add(isChronicCheckBox);
			if(!insert){
				if (disease.getOpdInclude()) includeOpdCheckBox.setSelected(true);
				if (disease.getIpdInInclude()) includeIpdInCheckBox.setSelected(true);
				if (disease.getIpdOutInclude()) includeIpdOutCheckBox.setSelected(true);
				if (disease.isMale()) isMaleCheckBox.setSelected(true);
				if (disease.isFemale()) isFemaleCheckBox.setSelected(true);
				if (disease.isChronic()) isChronicCheckBox.setSelected(true);
			}
		}
		return jNewPatientPanel;
	}

	
	
	/**
	 * This method initializes typeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getTypeComboBox() {
		if (typeComboBox == null) {
			typeComboBox = new JComboBox();
			if (insert) {
				DiseaseTypeBrowserManager manager = new DiseaseTypeBrowserManager();
				ArrayList<DiseaseType> types = manager.getDiseaseType();
				for (DiseaseType elem : types) {
					typeComboBox.addItem(elem);
				}
			} else {
				DiseaseType selectedDiseaseType=null;
				DiseaseTypeBrowserManager manager = new DiseaseTypeBrowserManager();
				ArrayList<DiseaseType> types = manager.getDiseaseType();
				for (DiseaseType elem : types) {
					typeComboBox.addItem(elem);
					if (disease.getType().equals(elem)) {
						selectedDiseaseType = elem;
					}
				}
				if (selectedDiseaseType!=null)
					typeComboBox.setSelectedItem(selectedDiseaseType);
				//typeComboBox.setEnabled(false);
			}
			
		}
		return typeComboBox;
	}
	
	
}  //  @jve:decl-index=0:visual-constraint="82,7"