package org.isf.medicalstock.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.PropertyConfigurator;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medstockmovtype.manager.MedicaldsrstockmovTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.supplier.manager.SupplierManager;
import org.isf.supplier.model.Supplier;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.RequestFocusListener;
import org.isf.utils.jobjects.TextPrompt;
import org.isf.utils.jobjects.TextPrompt.Show;

import com.toedter.calendar.JDateChooser;

public class MovStockMultipleCharging extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DATE_FORMAT_DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
	private static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
	private static final int CODE_COLUMN_WIDTH = 100;
	
	private JPanel mainPanel;
	private JTextField jTextFieldReference;
	private JTextField jTextFieldSearch;
	private JComboBox jComboBoxChargeType;
	private JDateChooser jDateChooser;
	private JComboBox jComboBoxSupplier;
	private JTable jTableMovements;
	private final String[] columnNames = { 
		"Code", 
		"Description", 
		"Qty/Packet", 
		"Qty", 
		"Unit/Pack", 
		"Total", 
		"Lot No.", 
		"Expiring Date", 
		"Cost", 
		"Total" };
	private final Class[] columnClasses = { String.class, String.class, Integer.class, Integer.class, String.class, Integer.class, String.class, String.class, Double.class, Double.class };
	private boolean[] columnEditable = { true, false, false, true, true, false, !GeneralData.AUTOMATICLOT, true, true, false };
	private int[] columnWidth = { 50, 100, 70, 50, 70, 50, 50, 80, 50, 80 };
	private boolean[] columnResizable = { false, true, false, false, false, false, false, false, false, false };
	private boolean[] columnVisible = { true, true, true, true, true, true, !GeneralData.AUTOMATICLOT, true, GeneralData.LOTWITHCOST, GeneralData.LOTWITHCOST };
	private int[] columnAlignment = { SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER,
			SwingConstants.CENTER, SwingConstants.RIGHT, SwingConstants.RIGHT };
	private boolean[] columnBold = { false, false, false, false, false, true, false, false, false, true };
	private HashMap<String, Medical> medicalMap;
	private ArrayList<Integer> units;
	private JTableModel model;
	private String[] qtyOption = new String[] { 
		"units", 
		"packets" };
	private JComboBox comboBox = new JComboBox(qtyOption);
	private final int UNITS = 0;
	private final int PACKETS = 1;
	private int optionSelected = UNITS;

	/**
	 * Launch the application.
	 * TODO: externalize strings
	 */
	public static void main(String[] args) {
		try {
			PropertyConfigurator.configure(new File("./rsc/log4j.properties").getAbsolutePath());
			GeneralData.getGeneralData();
			new MovStockMultipleCharging(new JFrame());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isAutomaticLot() {
		return GeneralData.AUTOMATICLOT;
	}

	/**
	 * Create the dialog.
	 */
	public MovStockMultipleCharging(JFrame owner) {
		super(owner, true);
		initialize();
		initcomponents();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void initialize() {
		MedicalBrowsingManager medMan = new MedicalBrowsingManager();
		ArrayList<Medical> medicals = medMan.getMedicalsSortedByCode();

		medicalMap = new HashMap<String, Medical>();
		for (Medical med : medicals) {
			medicalMap.put(med.getProd_code(), med);
		}
		units = new ArrayList<Integer>();
	}
	
	private void initcomponents() {
		setTitle(MessageBundle.getMessage("angal.medicalstock.stockmovementinserting"));
		add(getJPanelHeader(), BorderLayout.NORTH);
		add(getJMainPanel(), BorderLayout.CENTER);
		add(getJButtonPane(), BorderLayout.SOUTH);
		setPreferredSize(new Dimension(800, 600));
		pack();
		setLocationRelativeTo(null);
	}

	private JPanel getJPanelHeader() {
		JPanel headerPanel = new JPanel();
		getContentPane().add(headerPanel, BorderLayout.NORTH);
		GridBagLayout gbl_headerPanel = new GridBagLayout();
		gbl_headerPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_headerPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_headerPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_headerPanel.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		headerPanel.setLayout(gbl_headerPanel);
		{
			JLabel jLabelDate = new JLabel("Date");
			GridBagConstraints gbc_jLabelDate = new GridBagConstraints();
			gbc_jLabelDate.anchor = GridBagConstraints.WEST;
			gbc_jLabelDate.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDate.gridx = 0;
			gbc_jLabelDate.gridy = 0;
			headerPanel.add(jLabelDate, gbc_jLabelDate);
		}
		{
			GridBagConstraints gbc_dateChooser = new GridBagConstraints();
			gbc_dateChooser.anchor = GridBagConstraints.WEST;
			gbc_dateChooser.insets = new Insets(5, 0, 5, 5);
			gbc_dateChooser.fill = GridBagConstraints.VERTICAL;
			gbc_dateChooser.gridx = 1;
			gbc_dateChooser.gridy = 0;
			headerPanel.add(getJDateChooser(), gbc_dateChooser);
		}
		{
			JLabel jLabelReferenceNo = new JLabel("Reference No.");
			GridBagConstraints gbc_jLabelReferenceNo = new GridBagConstraints();
			gbc_jLabelReferenceNo.anchor = GridBagConstraints.EAST;
			gbc_jLabelReferenceNo.insets = new Insets(5, 0, 5, 5);
			gbc_jLabelReferenceNo.gridx = 2;
			gbc_jLabelReferenceNo.gridy = 0;
			headerPanel.add(jLabelReferenceNo, gbc_jLabelReferenceNo);
		}
		{
			jTextFieldReference = new JTextField();
			GridBagConstraints gbc_jTextFieldReference = new GridBagConstraints();
			gbc_jTextFieldReference.insets = new Insets(5, 0, 5, 0);
			gbc_jTextFieldReference.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldReference.gridx = 3;
			gbc_jTextFieldReference.gridy = 0;
			headerPanel.add(jTextFieldReference, gbc_jTextFieldReference);
			jTextFieldReference.setColumns(10);
		}
		{
			JLabel jLabelChargeType = new JLabel("Charge Type");
			GridBagConstraints gbc_jLabelChargeType = new GridBagConstraints();
			gbc_jLabelChargeType.anchor = GridBagConstraints.EAST;
			gbc_jLabelChargeType.insets = new Insets(0, 5, 5, 5);
			gbc_jLabelChargeType.gridx = 0;
			gbc_jLabelChargeType.gridy = 1;
			headerPanel.add(jLabelChargeType, gbc_jLabelChargeType);
		}
		{
			GridBagConstraints gbc_jComboBoxChargeType = new GridBagConstraints();
			gbc_jComboBoxChargeType.anchor = GridBagConstraints.WEST;
			gbc_jComboBoxChargeType.insets = new Insets(0, 0, 5, 5);
			gbc_jComboBoxChargeType.gridx = 1;
			gbc_jComboBoxChargeType.gridy = 1;
			headerPanel.add(getJComboBoxChargeType(), gbc_jComboBoxChargeType);
		}
		{
			JLabel jLabelSupplier = new JLabel("Supplier");
			GridBagConstraints gbc_jLabelSupplier = new GridBagConstraints();
			gbc_jLabelSupplier.anchor = GridBagConstraints.WEST;
			gbc_jLabelSupplier.insets = new Insets(0, 5, 0, 5);
			gbc_jLabelSupplier.gridx = 0;
			gbc_jLabelSupplier.gridy = 3;
			headerPanel.add(jLabelSupplier, gbc_jLabelSupplier);
		}
		{
			GridBagConstraints gbc_jComboBoxSupplier = new GridBagConstraints();
			gbc_jComboBoxSupplier.anchor = GridBagConstraints.WEST;
			gbc_jComboBoxSupplier.insets = new Insets(0, 0, 0, 5);
			gbc_jComboBoxSupplier.gridx = 1;
			gbc_jComboBoxSupplier.gridy = 3;
			headerPanel.add(getJComboBoxSupplier(), gbc_jComboBoxSupplier);
		}
		return headerPanel;
	}

	private JPanel getJButtonPane() {
		JPanel buttonPane = new JPanel();
		//buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		{
			JButton deleteButton = new JButton("remove Item");
			deleteButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int row = jTableMovements.getSelectedRow();
					if (row > -1) model.removeItem(row);
				}
			});
			buttonPane.add(deleteButton);
		}
		{
			JButton saveButton = new JButton("Save");
			saveButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						BusyState.setBusyState(MovStockMultipleCharging.this, true);
						if (!checkAndPrepareMovements()) {
							return;
						}
						if (!save()) {
							return;
						}
					} finally {
						BusyState.setBusyState(MovStockMultipleCharging.this, false);
					}
					dispose();
				}
			});
			buttonPane.add(saveButton);
		}
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			buttonPane.add(cancelButton);
		}
		return buttonPane;
	}

	private JPanel getJMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			mainPanel.add(getJTextFieldSearch(), BorderLayout.NORTH);
			mainPanel.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return mainPanel;
	}

	private JScrollPane getJScrollPane() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(getJTable());
		scrollPane.setPreferredSize(new Dimension(400, 450));
		return scrollPane;
	}

	private JTable getJTable() {
		if (jTableMovements == null) {
			model = new JTableModel();
			jTableMovements = new JTable(model);
			jTableMovements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jTableMovements.setRowHeight(24);
			jTableMovements.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						int row = jTableMovements.getSelectedRow();
						model.removeItem(row);
					}
				}
			});

			for (int i = 0; i < columnNames.length; i++) {
				jTableMovements.getColumnModel().getColumn(i).setCellRenderer(new EnabledTableCellRenderer());
				jTableMovements.getColumnModel().getColumn(i).setMinWidth(columnWidth[i]);
				if (!columnResizable[i]) {
					jTableMovements.getColumnModel().getColumn(i).setResizable(columnResizable[i]);
					jTableMovements.getColumnModel().getColumn(i).setMaxWidth(columnWidth[i]);
				}
				if (!columnVisible[i]) {
					jTableMovements.getColumnModel().getColumn(i).setMinWidth(0);
					jTableMovements.getColumnModel().getColumn(i).setMaxWidth(0);
					jTableMovements.getColumnModel().getColumn(i).setWidth(0);
				}
			}

			TableColumn qtyOptionColumn = jTableMovements.getColumnModel().getColumn(4);
			qtyOptionColumn.setCellEditor(new DefaultCellEditor(comboBox));
			
			TableColumn costColumn = jTableMovements.getColumnModel().getColumn(8);
			costColumn.setCellRenderer(new DecimalFormatRenderer());
			
			TableColumn totalColumn = jTableMovements.getColumnModel().getColumn(9);
			totalColumn.setCellRenderer(new DecimalFormatRenderer());
			
			comboBox.setSelectedIndex(optionSelected);
		}
		return jTableMovements;
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
						
						med = chooseMedical(text.toLowerCase());
					}
						
					if (med != null) {	
						
						// Quantity
						int qty = askQuantity(med);
						if (qty == 0)
							return;

						// Lot (PreparationDate && ExpiringDate)
						Lot lot = null;
						if (isAutomaticLot()) {
							GregorianCalendar preparationDate = new GregorianCalendar();
							GregorianCalendar expiringDate = askExpiringDate();
							lot = new Lot("", preparationDate, expiringDate);
							// Cost
							double cost;
							if (GeneralData.LOTWITHCOST) {
								cost = askCost();
								if (cost == 0.) {
									double total = askTotalCost();
									//if (total == 0.) return;
									cost = total / qty;
								}
								lot.setCost(cost);
							}
						} else {
							lot = chooseLot(med);
							if (lot == null) {
								lot = askLot();
								if (lot == null) {
									return;
								}
								// Cost
								double cost;
								if (GeneralData.LOTWITHCOST) {
									cost = askCost();
									if (cost == 0.) {
										double total = askTotalCost();
										//if (total == 0.) return;
										cost = total / qty;
									}
									lot.setCost(cost);
								}
							}
						}

						// Date
						GregorianCalendar date = new GregorianCalendar();
						date.setTime(jDateChooser.getDate());
						
						// RefNo
						String refNo = jTextFieldReference.getText();
						
						Movement movement = new Movement(med, (MovementType) jComboBoxChargeType.getSelectedItem(), null, lot, date, qty, "", refNo);
						model.addItem(movement);

						units.add(PACKETS);

						jTextFieldSearch.setText("");
						jTextFieldSearch.requestFocus();
					}
				}
			});
		}
		return jTextFieldSearch;
	}

	private JDateChooser getJDateChooser() {
		if (jDateChooser == null) {
			jDateChooser = new JDateChooser(new Date());
			jDateChooser.setDateFormatString(DATE_FORMAT_DD_MM_YYYY_HH_MM_SS);
			jDateChooser.setPreferredSize(new Dimension(150, 24));
		}
		return jDateChooser;
	}

	private JComboBox getJComboBoxChargeType() {
		if (jComboBoxChargeType == null) {
			jComboBoxChargeType = new JComboBox();
			MedicaldsrstockmovTypeBrowserManager movMan = new MedicaldsrstockmovTypeBrowserManager();
			ArrayList<MovementType> movTypes = movMan.getMedicaldsrstockmovType();
			for (MovementType movType : movTypes) {
				if (movType.getType().equals("+"))
					jComboBoxChargeType.addItem(movType);
			}
		}
		return jComboBoxChargeType;
	}

	protected double askCost() {
		String input = JOptionPane.showInputDialog(MovStockMultipleCharging.this, "Unit cost:", 0.);
		double cost = 0.;
		if (input != null) {
			try {
				cost = Double.parseDouble(input);
				if (cost < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "please insert a valid value");
			}
		}
		return cost;
	}
	
	protected double askTotalCost() {
		String input = JOptionPane.showInputDialog(MovStockMultipleCharging.this, "Total cost:", 0.);
		double total = 0.;
		if (input != null) {
			try {
				total = Double.parseDouble(input);
				if (total < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "please insert a valid value");
			}
		}
		return total;
	}

	protected Lot askLot() {
		GregorianCalendar preparationDate = new GregorianCalendar();
		GregorianCalendar expiringDate = new GregorianCalendar();
		Lot lot = null;

		JTextField lotNameTextField = new JTextField(15);
		lotNameTextField.addAncestorListener(new RequestFocusListener());
		if (isAutomaticLot())
			lotNameTextField.setEnabled(false);
		TextPrompt suggestion = new TextPrompt("LOT CODE", lotNameTextField);
		{
			suggestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
			suggestion.setForeground(Color.GRAY);
			suggestion.setHorizontalAlignment(JLabel.CENTER);
			suggestion.changeAlpha(0.5f);
			suggestion.changeStyle(Font.BOLD + Font.ITALIC);
		}
		JDateChooser preparationDateChooser = new JDateChooser(new Date());
		{
			preparationDateChooser.setDateFormatString(DATE_FORMAT_DD_MM_YYYY);
		}
		JDateChooser expireDateChooser = new JDateChooser(new Date());
		{
			expireDateChooser.setDateFormatString(DATE_FORMAT_DD_MM_YYYY);
		}
		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("Lot No.:"));
		panel.add(lotNameTextField);
		panel.add(new JLabel("Preparation Date:"));
		panel.add(preparationDateChooser);
		panel.add(new JLabel("Expiring Date:"));
		panel.add(expireDateChooser);

		int ok = JOptionPane.showConfirmDialog(MovStockMultipleCharging.this, panel, "Lot informations", JOptionPane.OK_CANCEL_OPTION);

		if (ok == JOptionPane.OK_OPTION) {
			String lotName = lotNameTextField.getText();
			expiringDate.setTime(expireDateChooser.getDate());
			preparationDate.setTime(preparationDateChooser.getDate());
			lot = new Lot(lotName, preparationDate, expiringDate);
		}
		return lot;
	}
	
	protected Medical chooseMedical(String text) {
		ArrayList<Medical> medList = new ArrayList<Medical>();
		for (Medical aMed : medicalMap.values()) {
			if (aMed.getDescriptionWithCode().toLowerCase().contains(text))
				medList.add(aMed);
		}
		Collections.sort(medList);
		Medical med = null;
		
		if (!medList.isEmpty()) {
			JTable medTable = new JTable(new StockMedModel(medList));
			medTable.getColumnModel().getColumn(0).setMaxWidth(CODE_COLUMN_WIDTH);
			medTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JPanel panel = new JPanel();
			panel.add(new JScrollPane(medTable));
			
			int ok = JOptionPane.showConfirmDialog(MovStockMultipleCharging.this, panel, "Choose a Medical", JOptionPane.YES_NO_OPTION);
			
			if (ok == JOptionPane.OK_OPTION) {
				int row = medTable.getSelectedRow();
				med = medList.get(row);
			}
			return med;
		}
		return null;
	}

	protected Lot chooseLot(Medical med) {
		MovStockInsertingManager movBrowser = new MovStockInsertingManager();
		ArrayList<Lot> lots = movBrowser.getLotByMedical(med);
		Lot lot = null;
		if (!lots.isEmpty()) {
			JTable lotTable = new JTable(new StockMovModel(lots));
			lotTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel("Use an existing lot?"), BorderLayout.NORTH);
			panel.add(new JScrollPane(lotTable), BorderLayout.CENTER);
			
			int ok = JOptionPane.showConfirmDialog(MovStockMultipleCharging.this, panel, "Existing lots", JOptionPane.YES_NO_OPTION);

			if (ok == JOptionPane.OK_OPTION) {
				int row = lotTable.getSelectedRow();
				lot = lots.get(row);
			}
			return lot;
			
		}
		return lot;
	}

	protected GregorianCalendar askExpiringDate() {
		GregorianCalendar date = new GregorianCalendar();
		JDateChooser expireDateChooser = new JDateChooser(new Date());
		{
			expireDateChooser.setDateFormatString(DATE_FORMAT_DD_MM_YYYY);
		}
		JPanel panel = new JPanel(new GridLayout(1, 2));
		panel.add(new JLabel("Expiring Date:"));
		panel.add(expireDateChooser);

		int ok = JOptionPane.showConfirmDialog(MovStockMultipleCharging.this, panel, "Expiring Date:", JOptionPane.OK_CANCEL_OPTION);

		if (ok == JOptionPane.OK_OPTION) {
			date.setTime(expireDateChooser.getDate());
		}
		return date;
	}

	protected int askQuantity(Medical med) {
		String quantity = JOptionPane.showInputDialog(MovStockMultipleCharging.this, med.getDescriptionWithCode() + " quantity:", 0);
		int qty = 0;
		if (quantity != null) {
			try {
				qty = Integer.parseInt(quantity);
				if (qty == 0)
					return 0;
				if (qty < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "please insert a valid value");
			}
		}
		return qty;
	}

	private JComboBox getJComboBoxSupplier() {
		if (jComboBoxSupplier == null) {
			jComboBoxSupplier = new JComboBox();
			jComboBoxSupplier.addItem("");
			SupplierManager supMan = new SupplierManager();
			ArrayList<Supplier> suppliers = supMan.getSupplier(false);
			for (Supplier sup : suppliers) {
				jComboBoxSupplier.addItem(sup);
			}
		}
		return jComboBoxSupplier;
	}

	public class JTableModel extends AbstractTableModel {

		private ArrayList<Movement> movements;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public JTableModel() {
			movements = new ArrayList<Movement>();
		}
		
		public ArrayList<Movement> getMovements() {
			return movements;
		}

		public void removeItem(int row) {
			movements.remove(row);
			units.remove(row);
			fireTableDataChanged();
		}

		public void addItem(Movement movement) {
			movements.add(movement);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return movements.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnClasses[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnEditable[columnIndex];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int r, int c) {
			Movement movement = movements.get(r);
			Medical medical = movement.getMedical();
			Lot lot = movement.getLot();
			String lotName = lot.getCode();
			int qty = movement.getQuantity();
			int ppp = medical.getPcsperpck().intValue();
			int option = units.get(r);
			int total = option == UNITS ? qty : (ppp == 0 ? qty : ppp * qty);
			double cost = lot.getCost();
			if (c == -1) {
				return movement;
			} else if (c == 0) {
				return medical.getProd_code();
			} else if (c == 1) {
				return medical.getDescription();
			} else if (c == 2) {
				return ppp;
			} else if (c == 3) {
				return qty;
			} else if (c == 4) {
				return qtyOption[option];
			} else if (c == 5) {
				return total;
			} else if (c == 6) {
				return lotName.equals("") ? "AUTO" : lotName;
			} else if (c == 7) {
				return format(lot.getDueDate());
			} else if (c == 8) {
				return cost;
			} else if (c == 9) {
				return cost * qty;
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
		 * int)
		 */
		@Override
		public void setValueAt(Object value, int r, int c) {
			Movement movement = movements.get(r);
			Lot lot = movement.getLot();
			if (c == 0) {
				String key = String.valueOf(value);
				if (medicalMap.containsKey(key)) {
					movement.setMedical(medicalMap.get(key));
					movements.set(r, movement);
				}
			} else if (c == 3) {
				movement.setQuantity((Integer) value);
			} else if (c == 4) {
				units.set(r, comboBox.getSelectedIndex());
			} else if (c == 6) {
				lot.setCode((String) value);
			} else if (c == 7) {
				try {
					lot.setDueDate(convertToDate((String) value));
				} catch (ParseException e) {
				}
			} else if (c == 8) {
				lot.setCost((Double) value);
			}
			movements.set(r, movement);
			fireTableDataChanged();
		}
	}
	
	private boolean checkAndPrepareMovements() {
		boolean ok = true;
		MovStockInsertingManager manager = new MovStockInsertingManager();
		
		// Check the Date
		GregorianCalendar thisDate = new GregorianCalendar();
		thisDate.setTime(jDateChooser.getDate());
		GregorianCalendar lastDate = manager.getLastMovementDate();
		if (lastDate != null && thisDate.compareTo(lastDate) < 0) {
			JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "date before last movement (" + format(lastDate) + ") not allowed");
			return false;
		}
		
		// Check the RefNo
		String refNo = jTextFieldReference.getText();
		if (refNo.equals("")) {
			JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "please insert a reference number");
			return false;
		} else if (manager.refNoExists(refNo)) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "the inserted reference number already exists");
				return false;
		}
		
		// Check supplier
		Object supplier = jComboBoxSupplier.getSelectedItem();
		if (supplier instanceof String) {
			JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "please select a Supplier");
			return false;
		}
		
		// Check and set all movements
		ArrayList<Movement> movements = model.getMovements();
		for (int i = 0; i < movements.size(); i++) {
			Movement mov = movements.get(i);
			Lot lot = mov.getLot();
			GregorianCalendar expiringDate = mov.getLot().getDueDate();
			if (expiringDate.compareTo(thisDate) < 0) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "expiring date in the past not allowed");
				jTableMovements.getSelectionModel().setSelectionInterval(i, i);
				return false;
			}
			if (GeneralData.LOTWITHCOST) {
				Double cost = lot.getCost();
				if (cost == null || cost.doubleValue() <= 0.) {
					JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "zero costs not allowed");
					jTableMovements.getSelectionModel().setSelectionInterval(i, i);
					return false;
				}
			}
			mov.setDate(thisDate);
			mov.setRefNo(refNo);
			mov.setType((MovementType) jComboBoxChargeType.getSelectedItem());
			mov.setOrigin(((Supplier) jComboBoxSupplier.getSelectedItem()).getSupId().toString());
			mov.getLot().setPreparationDate(thisDate);
		}
		return ok;
	}
	
	private boolean save() {
		boolean ok = true;
		MovStockInsertingManager movManager = new MovStockInsertingManager();
		ArrayList<Movement> movements = model.getMovements();
		if (movements.isEmpty()) {
			JOptionPane.showMessageDialog(MovStockMultipleCharging.this, "no elements to save");
			return false;
		}
		
		int movSize = movements.size();
		int index = movManager.newMultipleChargingMovements(movements);
		
		if (index < movSize) {
			jTableMovements.getSelectionModel().setSelectionInterval(index, index);
			ok = false;
		}
		return ok;
	}

	public String format(GregorianCalendar gc) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
		return sdf.format(gc.getTime());
	}

	public GregorianCalendar convertToDate(String string) throws ParseException {
		GregorianCalendar date = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
		date.setTime(sdf.parse(string));
		return date;
	}

	class EnabledTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setHorizontalAlignment(columnAlignment[column]);
			if (!columnEditable[column]) {
				cell.setBackground(Color.LIGHT_GRAY);
			}
			if (columnBold[column]) { 
				cell.setFont(new Font(null, Font.BOLD, 12));
			}
			return cell;
		}
	}
	
	class DecimalFormatRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final DecimalFormat formatter = new DecimalFormat("#,##0.00");

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			// First format the cell value as required
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			value = formatter.format((Number) value);
			setHorizontalAlignment(columnAlignment[column]);
			if (!columnEditable[column]) {
				cell.setBackground(Color.LIGHT_GRAY);
			}
			if (columnBold[column]) { 
				cell.setFont(new Font(null, Font.BOLD, 12));
			}
			// And pass it on to parent class
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
	
	class StockMovModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<Lot> lotList;

		public StockMovModel(ArrayList<Lot> lots) {
			lotList = lots;
		}

		public int getRowCount() {
			if (lotList == null)
				return 0;
			return lotList.size();
		}

		public String getColumnName(int c) {
			if (c == 0) {
				return MessageBundle.getMessage("angal.medicalstock.lotid");
			}
			if (c == 1) {
				return MessageBundle.getMessage("angal.medicalstock.prepdate");
			}
			if (c == 2) {
				return MessageBundle.getMessage("angal.medicalstock.duedate");
			}
			if (c == 3) {
				return MessageBundle.getMessage("angal.medicalstock.quantity");
			}
			if (GeneralData.LOTWITHCOST) {
				if (c == 4) {
					return "Cost";
				}
			}
			return "";
		}

		public int getColumnCount() {
			if (GeneralData.LOTWITHCOST) return 5;
			return 4;
		}

		public Object getValueAt(int r, int c) {
			Lot lot = lotList.get(r);
			if (c == -1) {
				return lot;
			} else if (c == 0) {
				return lot.getCode();
			} else if (c == 1) {
				return format(lot.getPreparationDate());
			} else if (c == 2) {
				return format(lot.getDueDate());
			} else if (c == 3) {
				return lot.getQuantity();
			} else if (c == 4) {
				return lot.getCost();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
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
}
