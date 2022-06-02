package org.isf.medicalstockward.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableModel;

import org.isf.accounting.model.Bill;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWardPrescription;
import org.isf.medicalstockward.model.MedicalWardPrescriptionDetail;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.patient.model.Patient;

public class WardPharmacyDispense extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//LISTENER INTERFACE --------------------------------------------------------
    private EventListenerList movementWardListeners = new EventListenerList();
	
	public interface MovementWardListeners extends EventListener {
		public void prescriptionUpdated(AWTEvent e);
	}
	
	public void addMovementWardListener(MovementWardListeners l) {
		movementWardListeners.add(MovementWardListeners.class, l);
	}
	
	public void removeMovementWardListener(MovementWardListeners listener) {
		movementWardListeners.remove(MovementWardListeners.class, listener);
	}
	
	private void firePrescriptionUpdated() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
		
		EventListener[] listeners = movementWardListeners.getListeners(MovementWardListeners.class);
		for (int i = 0; i < listeners.length; i++)
			((MovementWardListeners)listeners[i]).prescriptionUpdated(event);
	}
	//---------------------------------------------------------------------------
	
	private final JPanel contentPanel = new JPanel();
	private JLabel jLabelPatientID;
	private JLabel jLabelBillId;
	private JTextField jTextFieldPatientID;
	private JTextField jTextFieldBillID;
	private JTextField jTextFieldPatientName;
	private JLabel jLabelAmount;
	private JTextField jTextFieldAmount;
	private JLabel jLabelStatus;
	private JScrollPane jScrollPane;
	private JTable jTableMedical;
	private boolean paid;
	private boolean cancelled;
	private boolean later;
	private boolean done;
	private boolean unclaimed;
	private Patient patient;
	private Bill bill;
	private MedicalWardPrescription prescription;
	
	private String[] columsMedical = {"Medical", "Quantity", "Units"};
	private boolean[] columsResizableMedical = { true, false, false };
	private int[] columWidthMedical = { 200, 80, 50 };
	
	private ArrayList<MovementWard> medicals = new ArrayList<MovementWard>();
	private MovWardBrowserManager wardMan = new MovWardBrowserManager();

	

	public WardPharmacyDispense(JFrame owner, MedicalWardPrescription prescription) {
		super(owner, true);
		this.prescription = prescription;
		patient = prescription.getPatient();
		bill = prescription.getBill();
		String status = bill.getStatus(); 
		cancelled = status.equals("D");
		paid = status.equals("C");
		later = status.equals("L");
		unclaimed = status.equals("X");
		done = prescription.getStatus() == 1;
		ArrayList<MedicalWardPrescriptionDetail> details = wardMan.getPrescriptionDetail(prescription);
		for (MedicalWardPrescriptionDetail detail : details) {
			medicals.add(new MovementWard(
					prescription.getWard(),
					new GregorianCalendar(),
					true, patient, patient.getAge(),patient.getWeight(),patient.getName(),
					detail.getMedical(), detail.getQuantity(), detail.getUnits())
			);
		}
		initComponents();
	}
	
	/**
	 * Create the dialog.
	 * @return 
	 */
	public void initComponents() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			jLabelPatientID = new JLabel("Patiend ID");
			GridBagConstraints gbc_jLabelPatientID = new GridBagConstraints();
			gbc_jLabelPatientID.anchor = GridBagConstraints.EAST;
			gbc_jLabelPatientID.insets = new Insets(0, 0, 5, 5);
			gbc_jLabelPatientID.gridx = 0;
			gbc_jLabelPatientID.gridy = 0;
			contentPanel.add(jLabelPatientID, gbc_jLabelPatientID);
		}
		{
			jTextFieldPatientID = new JTextField();
			jTextFieldPatientID.setEditable(false);
			jTextFieldPatientID.setText(String.valueOf(patient.getCode()));
			GridBagConstraints gbc_jTextFieldPatientID = new GridBagConstraints();
			gbc_jTextFieldPatientID.anchor = GridBagConstraints.WEST;
			gbc_jTextFieldPatientID.insets = new Insets(0, 0, 5, 5);
			gbc_jTextFieldPatientID.gridx = 1;
			gbc_jTextFieldPatientID.gridy = 0;
			contentPanel.add(jTextFieldPatientID, gbc_jTextFieldPatientID);
			jTextFieldPatientID.setColumns(10);
		}
		{
			jTextFieldPatientName = new JTextField();
			jTextFieldPatientName.setEditable(false);
			jTextFieldPatientName.setText(patient.getName());
			GridBagConstraints gbc_jTextFieldPatientName = new GridBagConstraints();
			gbc_jTextFieldPatientName.gridwidth = 3;
			gbc_jTextFieldPatientName.insets = new Insets(0, 0, 5, 5);
			gbc_jTextFieldPatientName.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldPatientName.gridx = 2;
			gbc_jTextFieldPatientName.gridy = 0;
			contentPanel.add(jTextFieldPatientName, gbc_jTextFieldPatientName);
			jTextFieldPatientName.setColumns(10);
		}
		{
			jLabelBillId = new JLabel("Bill ID");
			GridBagConstraints gbc_jLabelBillId = new GridBagConstraints();
			gbc_jLabelBillId.anchor = GridBagConstraints.EAST;
			gbc_jLabelBillId.insets = new Insets(0, 0, 0, 5);
			gbc_jLabelBillId.gridx = 0;
			gbc_jLabelBillId.gridy = 1;
			contentPanel.add(jLabelBillId, gbc_jLabelBillId);
		}
		{
			jTextFieldBillID = new JTextField();
			jTextFieldBillID.setEditable(false);
			jTextFieldBillID.setText(String.valueOf(bill.getId()));
			GridBagConstraints gbc_jTextFieldBillID = new GridBagConstraints();
			gbc_jTextFieldBillID.insets = new Insets(0, 0, 0, 5);
			gbc_jTextFieldBillID.anchor = GridBagConstraints.WEST;
			gbc_jTextFieldBillID.gridx = 1;
			gbc_jTextFieldBillID.gridy = 1;
			contentPanel.add(jTextFieldBillID, gbc_jTextFieldBillID);
			jTextFieldBillID.setColumns(10);
		}
		{
			jLabelAmount = new JLabel("Amount");
			GridBagConstraints gbc_jLabelAmount = new GridBagConstraints();
			gbc_jLabelAmount.insets = new Insets(0, 0, 0, 5);
			gbc_jLabelAmount.anchor = GridBagConstraints.EAST;
			gbc_jLabelAmount.gridx = 2;
			gbc_jLabelAmount.gridy = 1;
			contentPanel.add(jLabelAmount, gbc_jLabelAmount);
		}
		{
			jTextFieldAmount = new JTextField();
			jTextFieldAmount.setEditable(false);
			jTextFieldAmount.setText(String.valueOf(bill.getAmount()));
			GridBagConstraints gbc_jTextFieldAmount = new GridBagConstraints();
			gbc_jTextFieldAmount.insets = new Insets(0, 0, 0, 5);
			gbc_jTextFieldAmount.anchor = GridBagConstraints.WEST;
			gbc_jTextFieldAmount.gridx = 3;
			gbc_jTextFieldAmount.gridy = 1;
			contentPanel.add(jTextFieldAmount, gbc_jTextFieldAmount);
			jTextFieldAmount.setColumns(10);
		}
		{
			if (paid) {
				jLabelStatus = new JLabel("PAID");
				jLabelStatus.setForeground(new Color(0, 128, 0));
			} else if (later){
				jLabelStatus = new JLabel("LATER");
				jLabelStatus.setForeground(new Color(0, 0, 100));
			} else if (cancelled){
				jLabelStatus = new JLabel("CANCELLED");
				jLabelStatus.setForeground(new Color(255, 0, 0));
			} else if (unclaimed){
				jLabelStatus = new JLabel("UNCLAIMED");
				jLabelStatus.setForeground(new Color(0, 200, 200));
			} else {
				jLabelStatus = new JLabel("NOT PAID");
				jLabelStatus.setForeground(new Color(100, 0, 0));
			} 
			jLabelStatus.setFont(new Font("Tahoma", Font.BOLD, 14));
			GridBagConstraints gbc_jLabelStatus = new GridBagConstraints();
			gbc_jLabelStatus.gridx = 4;
			gbc_jLabelStatus.gridy = 1;
			contentPanel.add(jLabelStatus, gbc_jLabelStatus);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						boolean result = wardMan.newMovementWard(medicals);
						if (result) {
							wardMan.closeMedicalPrescription(prescription);
							firePrescriptionUpdated();
						} else {
							JOptionPane.showMessageDialog(WardPharmacyDispense.this, MessageBundle.getMessage("angal.medicalstockwardedit.thedatacouldnotbesaved"));
							return;
						}
						dispose();
					}
				});
				if ((paid || later) && !done) buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton();
				if (paid || done) cancelButton.setText("Close");
				else cancelButton.setText("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		{
			jScrollPane = new JScrollPane();
			getContentPane().add(jScrollPane, BorderLayout.CENTER);
			{
				jTableMedical = new JTable(new MedicalModel());
				for (int i = 0; i < columWidthMedical.length; i++) {
					jTableMedical.getColumnModel().getColumn(i).setMinWidth(columWidthMedical[i]);
					if (!columsResizableMedical[i])
						jTableMedical.getColumnModel().getColumn(i).setMaxWidth(columWidthMedical[i]);
				}
				jScrollPane.setViewportView(jTableMedical);
			}
		}
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	class MedicalModel extends DefaultTableModel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MedicalModel() {}
		
		
		public int getRowCount() {
			return medicals.size();
		}

		public Object getValueAt(int r, int c) {
			MovementWard mov = medicals.get(r);
			if (c == -1) {
				return mov;
			}
			if (c == 0) {
				return mov.getMedical().getDescriptionWithCode();
			}
			if (c == 1) {
				return mov.getQuantity();
			}
			if (c == 2) {
				return mov.getUnits();
			}
			return null;
		}

		public String getColumnName(int c) {
			return columsMedical[c];
		}

		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 1) return Double.class;
			return String.class;
		}

		public int getColumnCount() {
			return columsMedical.length;
		}

		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}
}
