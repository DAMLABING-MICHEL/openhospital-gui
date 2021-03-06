package org.isf.patient.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;

/**
 * A class to compose a summary of the data of a given patient
 * 
 * @author flavio
 * 
 */
public class PatientSummary {

	private Patient patient;
	
	private int maximumWidth = 350;
	private int borderTickness = 10;
	private int imageMaxWidth = 140;

	public PatientSummary(Patient patient) {
		super();
		this.patient = patient;
	}

	/**
	 * a short summary in AdmissionBrowser
	 * 
	 * @return
	 */
	public JPanel getPatientDataPanel() {
		JPanel p = new JPanel(new BorderLayout(borderTickness, borderTickness));

		p.add(getPatientTitlePanel(), BorderLayout.NORTH);
		JWhitePanel dataPanel = new JWhitePanel();
		dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
		dataPanel.add(setMyBorder(getPatientNamePanel(), MessageBundle.getMessage("angal.admission.namem")));
		dataPanel.add(setMyBorder(getPatientTaxCodePanel(), MessageBundle.getMessage("angal.admission.taxcode")));
		dataPanel.add(setMyBorder(getPatientSexPanel(), MessageBundle.getMessage("angal.admission.sexm")));
		dataPanel.add(setMyBorder(getPatientAgePanel(), MessageBundle.getMessage("angal.admission.agem")));
		dataPanel.add(setMyBorder(getPatientNotePanel(), MessageBundle.getMessage("angal.admission.patientnotes")));
		p.add(dataPanel, BorderLayout.CENTER);

		return p;
	}

	/**
	 * create and returns a JWhitePanel with all patient's informations
	 * 
	 * @return
	 */
	public JWhitePanel getPatientCompleteSummary() {

		JWhitePanel p = new JWhitePanel();
		p.setLayout(new BorderLayout(borderTickness,borderTickness));

		p.add(getPatientCard(), BorderLayout.NORTH);
		
		JWhitePanel dataPanel = new JWhitePanel();
		dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));

		dataPanel.add(setMyBorder(getPatientTaxCodePanel(), MessageBundle.getMessage("angal.admission.taxcode")));
		dataPanel.add(getPatientAddressAndCityPanel());
		dataPanel.add(setMyBorder(getPatientParentNewsPanel(), MessageBundle.getMessage("angal.admission.parents")));
		dataPanel.add(getPatientKinAndTelephonePanel());
		dataPanel.add(getPatientBloodAndEcoPanel());
		
		p.add(dataPanel, BorderLayout.CENTER);
		p.add(setMyBorder(getPatientNotePanel(), MessageBundle.getMessage("angal.admission.patientnotes")), BorderLayout.SOUTH);
		
		Dimension dim = p.getPreferredSize();
		p.setMaximumSize(new Dimension(maximumWidth, dim.height));

		return p;
	}

	private JWhitePanel getPatientAddressAndCityPanel() {
		JWhitePanel dataPanel = new JWhitePanel();
		dataPanel.setLayout(new java.awt.GridLayout(1, 2));;
		dataPanel.add(setMyBorder(getPatientAddressPanel(), MessageBundle.getMessage("angal.admission.addressm")));
		dataPanel.add(setMyBorder(getPatientCityPanel(), MessageBundle.getMessage("angal.admission.city")));
		return dataPanel;
	}

	private JWhitePanel getPatientBloodAndEcoPanel() {
		JWhitePanel dataPanel = new JWhitePanel();
		dataPanel.setLayout(new java.awt.GridLayout(1, 2));
		dataPanel.add(setMyBorder(getPatientBloodTypePanel(), MessageBundle.getMessage("angal.admission.bloodtype")));
		dataPanel.add(setMyBorder(getPatientEcoStatusPanel(), MessageBundle.getMessage("angal.admission.insurance")));
		return dataPanel;
	}

	private JWhitePanel getPatientKinAndTelephonePanel() {
		JWhitePanel dataPanel = new JWhitePanel();
		dataPanel.setLayout(new java.awt.GridLayout(1, 2));
		dataPanel.add(setMyBorder(getPatientKinPanel(), MessageBundle.getMessage("angal.admission.nextkin")));
		dataPanel.add(setMyBorder(getPatientTelephonePanel(), MessageBundle.getMessage("angal.admission.telephone")));
		return dataPanel;
	}

	final int insetSize = 5;

	private JWhitePanel getPatientTitlePanel() {
		JLabel l = new JLabel(MessageBundle.getMessage("angal.admission.patientsummary") + " (" + MessageBundle.getMessage("angal.patient.code") + ": " + patient.getCode() + ")");
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.CENTER, insetSize, insetSize));
		lP.add(l);
		return lP;
	}
	
	private Image scaleImage(int maxDim, Image photo) {
		double scale = (double) maxDim / (double) photo.getHeight(null);
		if (photo.getWidth(null) > photo.getHeight(null))
		{
			scale = (double) maxDim / (double) photo.getWidth(null);
		}
		int scaledW = (int) (scale * photo.getWidth(null));
		int scaledH = (int) (scale * photo.getHeight(null));
		
		return photo.getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);
	}
	
	private JWhitePanel getPatientCard() {
		JWhitePanel cardPanel = new JWhitePanel();
		cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.X_AXIS));
		cardPanel.setBorder(BorderFactory.createEmptyBorder(insetSize, insetSize, insetSize, insetSize));
		
		JWhitePanel patientData = new JWhitePanel();
		patientData.setLayout(new BoxLayout(patientData, BoxLayout.Y_AXIS));
		patientData.setBorder(BorderFactory.createEmptyBorder(insetSize, insetSize, insetSize, insetSize));
		
		if (patient == null) patient = new Patient();
		Integer code = patient.getCode();
		String previousCode = patient.getPreviousCode();
		JLabel patientCode = new JLabel();
		StringBuilder patientCodeString = new StringBuilder();
		if (code != null) 
			patientCodeString.append(MessageBundle.getMessage("angal.patient.code")).append(": ").append(code.toString());
		if (previousCode != null && !previousCode.equals(""))
			patientCodeString.append(" (").append(previousCode).append(")");
		patientCode.setText(patientCodeString.toString());
		JLabel patientName = new JLabel(MessageBundle.getMessage("angal.patient.name") + ": " + filtra(patient.getName()));
		JLabel patientAge = new JLabel(MessageBundle.getMessage("angal.patient.age") + ": " +
				(patient.getMonths() < 24 ?
						filtra(patient.getMonths()) + " " + MessageBundle.getMessage("angal.patient.months") :
						filtra(patient.getAge())  + " " + MessageBundle.getMessage("angal.patient.years")));
		JLabel patientSex = new JLabel(MessageBundle.getMessage("angal.patient.sex") + ": " + patient.getSex());
		JLabel patientTOB = new JLabel(MessageBundle.getMessage("angal.patient.tobm") + ": " + filtra(patient.getBloodType()));
		
		JLabel patientPhoto = new JLabel();
		Image photo = patient.getPhoto();
		
		if (photo != null) {
			patientPhoto.setIcon(new ImageIcon(scaleImage(imageMaxWidth, photo)));
		} else {
			try {
				Image noPhotoImage = ImageIO.read(new File("rsc/images/nophoto.png"));
				patientPhoto.setIcon(new ImageIcon(scaleImage(imageMaxWidth, noPhotoImage)));
			} catch (IOException ioe) {
				System.out.println("rsc/images/nophoto.png is missing...");
			}
		}
		
		patientData.add(patientCode);
		patientData.add(Box.createVerticalStrut(insetSize));
		patientData.add(patientName);
		patientData.add(patientAge);
		patientData.add(patientSex);
		patientData.add(Box.createVerticalGlue());
		patientData.add(patientTOB);
		
		cardPanel.add(patientPhoto);
		cardPanel.add(Box.createHorizontalStrut(insetSize));
		cardPanel.add(patientData);
		return cardPanel;
	}

	private String filtra(String string) {
		if (string == null) return " ";
		if (string.equalsIgnoreCase("Unknown")) return " ";
		return string;
	}
	
	private String filtra(int number) {
		if (number == 0) return " ";
		return String.valueOf(number);
	}
	
	private JWhitePanel getPatientNamePanel() {
		JLabel l = new JLabel(patient.getSecondName() + " " + patient.getFirstName());
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	private JWhitePanel getPatientTaxCodePanel() {
		JLabel l = new JLabel(patient.getTaxCode() + " ");
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	private JWhitePanel getPatientKinPanel() {
		JLabel l = null;
		if (patient.getNextKin() == null || patient.getNextKin().equalsIgnoreCase("")) {
			//l = new JLabel(MessageBundle.getMessage("angal.admission.unknown"));
			l = new JLabel(" ");
		} else {
			l = new JLabel(patient.getNextKin());
		}
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	private JWhitePanel getPatientTelephonePanel() {
		JLabel l = null;
		if (patient.getTelephone() == null || patient.getTelephone().equalsIgnoreCase("")) {
			//l = new JLabel(MessageBundle.getMessage("angal.admission.unknown"));
			l = new JLabel(" ");
		} else {
			l = new JLabel("" + patient.getTelephone());
		}
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}
	
	private JWhitePanel getPatientAddressPanel() {
		JLabel l = null;
		if (patient.getAddress() == null || patient.getAddress().equalsIgnoreCase("")) {
			//l = new JLabel(MessageBundle.getMessage("angal.admission.unknown"));
			l = new JLabel(" ");
		} else {
			l = new JLabel("" + patient.getAddress());
		}
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	private JWhitePanel getPatientCityPanel() {
		JLabel l = null;
		if (patient.getCity() == null || patient.getCity().equalsIgnoreCase("")) {
			//l = new JLabel(MessageBundle.getMessage("angal.admission.unknown"));
			l = new JLabel(" ");
		} else {
			l = new JLabel("" + patient.getCity());
		}
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	// Panel for Blood Type
	private JWhitePanel getPatientBloodTypePanel() {
		JLabel l = null;
		String c = new String(patient.getBloodType());
		if (c == null || c.equalsIgnoreCase("Unknown")) {
			l = new JLabel(MessageBundle.getMessage("angal.admission.bloodtypeisunknown"));
			l = new JLabel(" ");
		} else {
			// l = new
			// JLabel(MessageBundle.getMessage("angal.admission.bloodtypeis")+c);
			l = new JLabel(c); // Added - Bundle is not necessary here
		}
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	private JWhitePanel getPatientEcoStatusPanel() {
		JLabel l = null;
		char c = patient.getHasInsurance();
		if (c == 'Y') {
			l = new JLabel(MessageBundle.getMessage("angal.admission.hasinsuranceyes"));
		} else if (c == 'N') {
			l = new JLabel(MessageBundle.getMessage("angal.admission.hasinsuranceno"));
		} else {
			l = new JLabel(MessageBundle.getMessage("angal.admission.unknown"));
			l = new JLabel(" ");
		}
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	private JWhitePanel getPatientAgePanel() {
		JLabel l = new JLabel("" + patient.getAge());
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	private JWhitePanel getPatientSexPanel() {
		JLabel l = new JLabel((patient.getSex() == 'F' ? MessageBundle.getMessage("angal.admission.female") : MessageBundle.getMessage("angal.admission.male")));
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	private JWhitePanel getPatientParentNewsPanel() {
		StringBuffer labelBfr = new StringBuffer("<html>");
		if (patient.getMother() == 'A')
			labelBfr.append(MessageBundle.getMessage("angal.admission.motherisalive"));
		else if (patient.getMother() == 'D')
			labelBfr.append(MessageBundle.getMessage("angal.admission.motherisdead"));;
		// added
			labelBfr.append((patient.getMother_name() == null || patient.getMother_name().compareTo("") == 0 ? "<BR>" : "(" + patient.getMother_name() + ")<BR>"));
		if (patient.getFather() == 'A')
			labelBfr.append(MessageBundle.getMessage("angal.admission.fatherisalive"));
		else if (patient.getFather() == 'D')
			labelBfr.append(MessageBundle.getMessage("angal.admission.fatherisdead"));
		// added
		labelBfr.append((patient.getFather_name() == null || patient.getFather_name().compareTo("") == 0 ? "<BR>" : "(" + patient.getFather_name() + ")<BR>"));
		if (patient.getParentTogether() == 'Y')
			labelBfr.append(MessageBundle.getMessage("angal.admission.parentslivetoghether"));
		else if (patient.getParentTogether() == 'N')
			labelBfr.append(MessageBundle.getMessage("angal.admission.parentsnotlivingtogether"));
		else 
			labelBfr.append("<BR>");
		labelBfr.append("</html>");
		JLabel l = new JLabel(labelBfr.toString());
		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new FlowLayout(FlowLayout.LEFT, insetSize, insetSize));
		lP.add(l);
		return lP;
	}

	// alex: modified with scroolbar
	private JWhitePanel getPatientNotePanel() {
		JTextArea textArea = new JTextArea(3, 40);
		textArea.setText(patient.getNote());
		textArea.setEditable(false);
		textArea.setLineWrap(true);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JWhitePanel lP = new JWhitePanel();
		lP.setLayout(new BorderLayout());
		lP.add(scrollPane, BorderLayout.CENTER);

		return lP;
	}

	private JWhitePanel setMyBorder(JWhitePanel c, String title) {
		javax.swing.border.Border b1 = BorderFactory.createLineBorder(Color.lightGray);
		javax.swing.border.Border b2 = BorderFactory.createTitledBorder(b1, title, javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP);
		c.setBorder(b2);
		return c;
	}
	
	class JWhitePanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public JWhitePanel() {
			super();
			setBackground(Color.WHITE);
		}
	}
}
