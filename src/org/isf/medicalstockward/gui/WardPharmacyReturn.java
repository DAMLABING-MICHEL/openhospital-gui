package org.isf.medicalstockward.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItem;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MedicalWardPrescription;
import org.isf.medicalstockward.model.MedicalWardPrescriptionDetail;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.patient.gui.SelectPatient;
import org.isf.patient.gui.SelectPatient.SelectionListener;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.model.Price;
import org.isf.utils.jobjects.TextPrompt;
import org.isf.utils.jobjects.TextPrompt.Show;
import org.isf.ward.model.Ward;

public class WardPharmacyReturn extends JDialog implements SelectionListener {

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
//		if (patientSelected.getWeight() == 0) {
//			JOptionPane.showMessageDialog(WardPharmacyNew.this, MessageBundle.getMessage("angal.medicalstockwardedit.theselectedpatienthasnoweightdefined"));
//		}
	}
	
	private static final long serialVersionUID = 1L;
	private JTextField jTextFieldPatient;
	private JTextField jTextFieldSearch;
	private JButton jButtonPickPatient;
	private JButton jButtonTrashPatient;
	private JPanel jPanelButtons;
	private JPanel jPanelNorth;
	private JButton jButtonOK;
	private JButton jButtonCancel;
	private JTable jTableMedicals;
	private JScrollPane jScrollPaneMedicals;
	private JPanel jPanelMedicalsButtons;
	private JButton jButtonRemoveMedical;

	private Patient patientSelected = null;
	private Ward wardSelected;
	private Class<?>[] medClasses = {Medical.class, Integer.class, Integer.class, Integer.class};
	private String[] medColumnNames = {	MessageBundle.getMessage("angal.medicalstockward.medical"),
			"Stock", "Return", "New Stock"};
	private Integer[] medWidth = {250, 100, 100, 100};
	private boolean[] medResizable = {true, false, false, false};
	
	//Medicals (ALL)
	private MedicalBrowsingManager medManager = new MedicalBrowsingManager();
	private ArrayList<Medical> medicals = medManager.getMedicalsSortedByCode();
	private HashMap<String, Medical> medicalMap; //map medicals by their prod_code
	private HashMap<Integer, Double> wardMap; //map quantities by their medical_id

	//Medicals (in WARD)
	private ArrayList<MedicalWardReturned> medItems = new ArrayList<MedicalWardReturned>();

	public WardPharmacyReturn(JFrame owner, Ward ward, ArrayList<MedicalWard> drugs) {
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
		getContentPane().add(getJPanelButtons(), BorderLayout.SOUTH);
		getContentPane().add(getJPanelNorth(), BorderLayout.NORTH);
		getContentPane().add(getJPanelMedicalsButtons(), BorderLayout.EAST);
		getContentPane().add(getJScrollPaneMedicals(), BorderLayout.CENTER);
		setDefaultCloseOperation(WardPharmacyReturn.DISPOSE_ON_CLOSE);
		setTitle("New Ward Pharmacy Return");
		setSize(800,600);
		setLocationRelativeTo(null);
	}

	private JPanel getJPanelNorth() {
		if (jPanelNorth == null) {
			jPanelNorth = new JPanel();
			jPanelNorth.setBorder(new EmptyBorder(5, 5, 5, 5));
			GridBagLayout gbl_jPanelNorth = new GridBagLayout();
			gbl_jPanelNorth.columnWidths = new int[]{0, 0, 0, 0};
			gbl_jPanelNorth.rowHeights = new int[]{25, 0, 0};
			gbl_jPanelNorth.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_jPanelNorth.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			jPanelNorth.setLayout(gbl_jPanelNorth);
			GridBagConstraints gbc_jTextFieldPatient = new GridBagConstraints();
			gbc_jTextFieldPatient.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldPatient.insets = new Insets(0, 0, 5, 5);
			gbc_jTextFieldPatient.gridx = 0;
			gbc_jTextFieldPatient.gridy = 0;
			jPanelNorth.add(getJTextFieldPatient(), gbc_jTextFieldPatient);
			GridBagConstraints gbc_jButtonPickPatient = new GridBagConstraints();
			gbc_jButtonPickPatient.insets = new Insets(0, 0, 5, 5);
			gbc_jButtonPickPatient.gridx = 1;
			gbc_jButtonPickPatient.gridy = 0;
			jPanelNorth.add(getJButtonPickPatient(), gbc_jButtonPickPatient);
			GridBagConstraints gbc_jButtonTrashPatient = new GridBagConstraints();
			gbc_jButtonTrashPatient.insets = new Insets(0, 0, 5, 0);
			gbc_jButtonTrashPatient.gridx = 2;
			gbc_jButtonTrashPatient.gridy = 0;
			jPanelNorth.add(getJButtonTrashPatient(), gbc_jButtonTrashPatient);
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridwidth = 3;
			gbc_textField.gridx = 0;
			gbc_textField.gridy = 1;
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
		String quantity = JOptionPane.showInputDialog(WardPharmacyReturn.this, med.toString() + " quantity:", 0);
		int qty = 0;
		if (quantity != null) {
			try {
				qty = Integer.parseInt(quantity);
				if (qty == 0)
					return 0;
				if (qty < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(WardPharmacyReturn.this, "please insert a valid value");
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
			
			int ok = JOptionPane.showConfirmDialog(WardPharmacyReturn.this, panel, "Choose a Medical", JOptionPane.YES_NO_OPTION);
			
			if (ok == JOptionPane.OK_OPTION) {
				int row = medTable.getSelectedRow();
				if (row > 0) med = medList.get(row);
			}
			return med;
		}
		return null;
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
						JOptionPane.showMessageDialog(WardPharmacyReturn.this,
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
	
	private void addItem(Medical med, Double returned) {
		if (med != null) {
			Double stock = wardMap.get(med.getCode());
			if (stock == null) stock = new Double(0);
			MedicalWardReturned item = new MedicalWardReturned(med, returned);
			item.setStock(stock);
			
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
			jTableMedicals.setBackground(Color.PINK);
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

					boolean isPatient = true;
					String description = "Return by: " + patientSelected.getName();
					int age = 0;
					float weight = 0;
					
					if (medItems.size() == 0) {
						JOptionPane.showMessageDialog(WardPharmacyReturn.this,	MessageBundle.getMessage("angal.medicalstockwardedit.pleaseselectadrug")); //$NON-NLS-1$
						return;
					}
					
					MovWardBrowserManager wardManager = new MovWardBrowserManager();
					GregorianCalendar newDate = new GregorianCalendar();
					
					ArrayList<MovementWard> dispenseMedical = new ArrayList<MovementWard>();
					for (int i = 0; i < medItems.size(); i++) {
						dispenseMedical.add(new MovementWard(
								wardSelected,
								newDate,
								isPatient,
								patientSelected,
								age,
								weight,
								description,
								medItems.get(i).getMedical(),
								-medItems.get(i).getReturned(),
								MessageBundle.getMessage("angal.medicalstockwardedit.pieces")));
					}

					Bill bill = newBillReturn();
					if (bill != null) {
						savePrescription(wardManager, bill, true);
						boolean result = wardManager.newMovementWard(dispenseMedical);
						if (result) {
							fireMovementWardInserted();
						} else {
							JOptionPane.showMessageDialog(WardPharmacyReturn.this, MessageBundle.getMessage("angal.medicalstockwardedit.thedatacouldnotbesaved"));
							return;
						}
					}
					
					dispose();
				}
				
				/**
				 * @param wardManager
				 * @param bill
				 */
				private void savePrescription(MovWardBrowserManager wardManager, Bill bill, boolean close) {
					MedicalWardPrescription prescription = new MedicalWardPrescription(new GregorianCalendar(), bill, patientSelected, wardSelected, false, null, null, null);
					ArrayList<MedicalWardPrescriptionDetail> details = new ArrayList<MedicalWardPrescriptionDetail>();
					for (MedicalWardReturned medItem : medItems) {
						MedicalWardPrescriptionDetail detail = new MedicalWardPrescriptionDetail();
						detail.setPrescription(prescription);
						detail.setMedical(medItem.getMedical());
						detail.setQuantity(medItem.getReturned());
						detail.setUnits("pieces");
						
						details.add(detail);
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
	private Bill newBillReturn() {
		PriceListManager priceMan = new PriceListManager();
		ArrayList<Price> prices = priceMan.getPrices();
		PriceList priceList = priceMan.getLists().get(0);
		
		Bill bill = null;
		ArrayList<BillItem> billItems = null;
		BillBrowserManager billMan = new BillBrowserManager();
		
		bill = new Bill();
		bill.setPatID(patientSelected.getCode());
		bill.setPatient(true);
		bill.setPatName(patientSelected.getName());
		bill.setStatus("O");
		bill.setList(true);
		bill.setListID(priceList.getId());
		bill.setListName(priceList.getName());
		
		billItems = new ArrayList<BillItem>();
		BigDecimal total = new BigDecimal(0);
		for (MedicalWardReturned medical : medItems) {
			Price price = null;
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
				billItem.setItemQuantity((int) -medical.getReturned());
				billItem.setPrice(true);
				billItem.setItemAmount(price.getPrice());
				billItem.setPriceID(price.getGroup()+price.getItem());
				
				billItems.add(billItem);
				BigDecimal pricePrice = new BigDecimal(Double.toString(price.getPrice()));
				BigDecimal returned = new BigDecimal(Double.toString(medical.getReturned())).negate();
				total = total.add(pricePrice.multiply(returned));
			}
		}
		bill.setAmount(total.doubleValue());
		bill.setBalance(total.doubleValue());
		
		System.out.println(total);
		
		if (total.abs().compareTo(new BigDecimal(500)) > 0) {
			int ok = JOptionPane.showConfirmDialog(WardPharmacyReturn.this, "The money returned will be more than 500 Birr, confirm?");
			if (ok != JOptionPane.OK_OPTION) {
				return null;
			}
		}
		
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
						dispose();
				}
			});
		}
		return jButtonCancel;
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.add(getJButtonOK());
			jPanelButtons.add(getJButtonCancel());
		}
		return jPanelButtons;
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
					SelectPatient sp = new SelectPatient(WardPharmacyReturn.this, patientSelected);
					sp.addSelectionListener(WardPharmacyReturn.this);
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
			jTextFieldPatient.setPreferredSize(new Dimension(300, 24));
			jTextFieldPatient.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {}
				
				@Override
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				     if (key == KeyEvent.VK_ENTER) {
				    	 SelectPatient sp = new SelectPatient(WardPharmacyReturn.this, jTextFieldPatient.getText());
						sp.addSelectionListener(WardPharmacyReturn.this);
						sp.pack();
						sp.setVisible(true);
				     }
				}
			});
		}
		return jTextFieldPatient;
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
			MedicalWardReturned medWard = medItems.get(r);
			int col = -1;
			double stock = medWard.getStock();
			double returned = medWard.getReturned(); 
			if (c == col) {
				return medWard;
			}
			if (c == ++col) {
				return medWard.getMedical().getDescriptionWithCode();
			}
			if (c == ++col) {
				return stock; 
			}
			if (c == ++col) {
				return returned;
			}
			if (c == ++col) {
				return stock + returned; 
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
	public class MedicalWardReturned {

		private Medical medical;
		private double stock; //quantity on hand
		private double returned; //quantity returned
		
		/**
		 * @param medical
		 * @param returned
		 */
		public MedicalWardReturned(Medical medical, Double returned) {
			this.medical = medical;
			this.returned = returned;
		}
		
		/**
		 * @param medical the medical to set
		 */
		public void setMedical(Medical medical) {
			this.medical = medical;
		}

		/**
		 * @param stock the stock to set
		 */
		public void setStock(double stock) {
			this.stock = stock;
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
		 * @return the requested
		 */
		public double getReturned() {
			return returned;
		}

		/**
		 * @param returned the requested to set
		 */
		public void setReturned(double returned) {
			this.returned = returned;
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
			MedicalWardReturned med = medItems.get(row);
			if (med.getStock() == 0)
				if (column == 2 || column == 3)
					cell.setForeground(Color.RED); // missing
			return cell;
		}
	}
}
