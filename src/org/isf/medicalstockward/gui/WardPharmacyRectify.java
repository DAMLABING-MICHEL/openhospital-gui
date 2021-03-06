package org.isf.medicalstockward.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;

import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.ward.model.Ward;

public class WardPharmacyRectify extends JDialog {

	//LISTENER INTERFACE --------------------------------------------------------
    private EventListenerList movementWardListeners = new EventListenerList();
	
	public interface MovementWardListeners extends EventListener {
		public void movementInserted(AWTEvent e);
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
	//---------------------------------------------------------------------------
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField jTextFieldReason;
	private Ward wardSelected;
	private JComboBox jComboBoxMedical;
	private JLabel jLabelStockQty;
	private JSpinner jSpinnerNewQty;
	
	//Medicals (ALL)
	private MedicalBrowsingManager medManager = new MedicalBrowsingManager();
	private ArrayList<Medical> medicals = medManager.getMedicalsSortedByCode();
	private HashMap<String, Medical> medicalMap; //map medicals by their prod_code
	private HashMap<Integer, Double> wardMap; //map quantities by their medical_id
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			WardPharmacyRectify dialog = new WardPharmacyRectify();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WardPharmacyRectify() {
		initComponents();
	}
	
	/**
	 * Create the dialog.
	 */
	public WardPharmacyRectify(JFrame owner, Ward ward, ArrayList<MedicalWard> drugs) {
		
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
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel jLabelRectifyTitle = new JLabel("Rectify");
			jLabelRectifyTitle.setForeground(Color.RED);
			jLabelRectifyTitle.setFont(new Font("Tahoma", Font.PLAIN, 28));
			GridBagConstraints gbc_jLabelRectifyTitle = new GridBagConstraints();
			gbc_jLabelRectifyTitle.insets = new Insets(0, 0, 5, 5);
			gbc_jLabelRectifyTitle.gridx = 1;
			gbc_jLabelRectifyTitle.gridy = 0;
			contentPanel.add(jLabelRectifyTitle, gbc_jLabelRectifyTitle);
		}
		{
			JLabel jLabelStock = new JLabel("Stock");
			GridBagConstraints gbc_jLabelStock = new GridBagConstraints();
			gbc_jLabelStock.anchor = GridBagConstraints.SOUTH;
			gbc_jLabelStock.insets = new Insets(0, 0, 5, 0);
			gbc_jLabelStock.gridx = 2;
			gbc_jLabelStock.gridy = 1;
			contentPanel.add(jLabelStock, gbc_jLabelStock);
		}
		{
			JLabel jLabelMedical = new JLabel("Medical");
			jLabelMedical.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelMedical.setPreferredSize(new Dimension(100, 25));
			GridBagConstraints gbc_jLabelMedical = new GridBagConstraints();
			gbc_jLabelMedical.insets = new Insets(0, 0, 5, 5);
			gbc_jLabelMedical.anchor = GridBagConstraints.EAST;
			gbc_jLabelMedical.gridx = 0;
			gbc_jLabelMedical.gridy = 2;
			contentPanel.add(jLabelMedical, gbc_jLabelMedical);
		}
		{
			GridBagConstraints gbc_jComboBoxMedical = new GridBagConstraints();
			gbc_jComboBoxMedical.insets = new Insets(0, 0, 5, 5);
			gbc_jComboBoxMedical.fill = GridBagConstraints.HORIZONTAL;
			gbc_jComboBoxMedical.gridx = 1;
			gbc_jComboBoxMedical.gridy = 2;
			contentPanel.add(getJComboBoxMedical(), gbc_jComboBoxMedical);
		}
		{
			GridBagConstraints gbc_jLabelStockQty = new GridBagConstraints();
			gbc_jLabelStockQty.insets = new Insets(0, 0, 5, 0);
			gbc_jLabelStockQty.gridx = 2;
			gbc_jLabelStockQty.gridy = 2;
			contentPanel.add(getJLabelStockQty(), gbc_jLabelStockQty);
		}
		{
			JLabel jLabelNewQuantity = new JLabel("New Quantity");
			GridBagConstraints gbc_jLabelNewQuantity = new GridBagConstraints();
			gbc_jLabelNewQuantity.anchor = GridBagConstraints.EAST;
			gbc_jLabelNewQuantity.insets = new Insets(0, 0, 5, 5);
			gbc_jLabelNewQuantity.gridx = 0;
			gbc_jLabelNewQuantity.gridy = 3;
			contentPanel.add(jLabelNewQuantity, gbc_jLabelNewQuantity);
		}
		{
			GridBagConstraints gbc_jSpinnerNewQuantity = new GridBagConstraints();
			gbc_jSpinnerNewQuantity.fill = GridBagConstraints.HORIZONTAL;
			gbc_jSpinnerNewQuantity.insets = new Insets(0, 0, 5, 5);
			gbc_jSpinnerNewQuantity.gridx = 1;
			gbc_jSpinnerNewQuantity.gridy = 3;
			contentPanel.add(getJSpinnerNewQty(), gbc_jSpinnerNewQuantity);
		}
		{
			JLabel jLabelReason = new JLabel("Reason");
			GridBagConstraints gbc_jLabelReason = new GridBagConstraints();
			gbc_jLabelReason.anchor = GridBagConstraints.EAST;
			gbc_jLabelReason.insets = new Insets(0, 0, 0, 5);
			gbc_jLabelReason.gridx = 0;
			gbc_jLabelReason.gridy = 4;
			contentPanel.add(jLabelReason, gbc_jLabelReason);
		}
		{
			jTextFieldReason = new JTextField();
			GridBagConstraints gbc_jTextFieldReason = new GridBagConstraints();
			gbc_jTextFieldReason.insets = new Insets(0, 0, 0, 5);
			gbc_jTextFieldReason.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldReason.gridx = 1;
			gbc_jTextFieldReason.gridy = 4;
			contentPanel.add(jTextFieldReason, gbc_jTextFieldReason);
			jTextFieldReason.setColumns(10);
		}
		{
			JPanel jButtonPanel = new JPanel();
			jButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(jButtonPanel, BorderLayout.SOUTH);
			{
				JButton jButtonOk = new JButton("OK");
				jButtonOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						Medical med = (Medical) jComboBoxMedical.getSelectedItem();
						if (!(med instanceof Medical)) return;
						
						String reason = jTextFieldReason.getText().trim();
						if (reason.equals("")) return;
						
						Double stock = Double.parseDouble(jLabelStockQty.getText());
						Double newQty = (Double) jSpinnerNewQty.getValue();
						double quantity = stock.doubleValue() - newQty.doubleValue();
						if (quantity == 0.) return;
						
						MovWardBrowserManager wardMan = new MovWardBrowserManager();
						boolean result = wardMan.newMovementWard(new MovementWard(
								wardSelected, 
								new GregorianCalendar(), 
								false, null, 0, 0, 
								reason, 
								med, 
								quantity,
								"pieces"));
						if (!result) {
							
							return;
						}
						fireMovementWardInserted();
						dispose();
					}
				});
				jButtonPanel.add(jButtonOk);
			}
			{
				JButton jButtonCancel = new JButton("Cancel");
				jButtonCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				jButtonPanel.add(jButtonCancel);
			}
		}
		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * @return
	 */
	private JSpinner getJSpinnerNewQty() {
		if (jSpinnerNewQty == null) {
			jSpinnerNewQty = new JSpinner(new SpinnerNumberModel(0.0, null, null, 1));
			jSpinnerNewQty.setFont(new Font("Tahoma", Font.BOLD, 14));
		}
		return jSpinnerNewQty;
	}

	/**
	 * @return
	 */
	private JLabel getJLabelStockQty() {
		if (jLabelStockQty == null) {
			jLabelStockQty = new JLabel("");
			jLabelStockQty.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelStockQty.setPreferredSize(new Dimension(100, 25));
			jLabelStockQty.setFont(new Font("Tahoma", Font.BOLD, 14));
		}
		return jLabelStockQty;
	}

	/**
	 * @return
	 */
	private JComboBox getJComboBoxMedical() {
		if (jComboBoxMedical == null) {
			jComboBoxMedical = new JComboBox();
			jComboBoxMedical.addItem("");
			for (Medical med : medicals){
				jComboBoxMedical.addItem(changeDescription(med));
			}
			jComboBoxMedical.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Medical med = ((Medical) jComboBoxMedical.getSelectedItem());
						Integer code = med.getCode();
						Double qty = wardMap.get(code);
						if (qty == null) qty = new Double(0);
						jLabelStockQty.setText(qty.toString());
						jSpinnerNewQty.setValue(qty);
					} catch (ClassCastException ex) {
						jLabelStockQty.setText("");
						jSpinnerNewQty.setValue(new Double(0));
					}
				}
			});
		}
		return jComboBoxMedical;
	}

	private Medical changeDescription(Medical med) {
		med.setDescription(med.getDescriptionWithCode());
		return med;
	}
}
