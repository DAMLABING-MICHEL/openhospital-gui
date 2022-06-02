package org.isf.opd.gui;

/*------------------------------------------
 * OpdBrowser - list all OPD. let the user select an opd to edit or delete
 * -----------------------------------------
 * modification history
 * 11/12/2005 - Vero, Rick  - first beta version 
 * 07/11/2006 - ross - renamed from Surgery 
 *                   - changed confirm delete message
 * 			         - version is now 1.0 
 *    12/2007 - isf bari - multilanguage version
 * 			         - version is now 1.2 
 * 21/06/2008 - ross - fixed getFilterButton method, need compare to translated string "female" to get correct filter
 *                   - displayed visitdate in the grid instead of opdDate (=system date)
 *                   - fixed "todate" bug (in case of 31/12: 31/12/2008 became 1/1/2008)
 * 			         - version is now 1.2.1 
 * 09/01/2009 - fabrizio - Column full name appears only in OPD extended. Better formatting of OPD date.
 *                         Age column justified to the right. Cosmetic changed to code style.
 * 13/02/2009 - alex - fixed variable visibility in filtering mechanism
 *------------------------------------------*/


import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.model.DiseaseType;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.opd.model.OpdRow;
import org.isf.opdchronic.gui.OpdChronicEdit;
import org.isf.patient.model.Patient;
import org.isf.utils.jobjects.BorderedPanel;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;

public class OpdBrowser extends ModalJFrame implements OpdEdit.SurgeryListener, OpdEditExtended.SurgeryListener {

	private static final long serialVersionUID = 2372745781159245861L;

	private static final String VERSION="1.2.1"; 
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private JPanel jButtonPanel = null;
	private JPanel jContainPanel = null;
//	private int pfrmWidth;
	private int pfrmHeight;
	private JButton jNewButton = null;
	private JButton jEditButton = null;
	private JButton jCloseButton = null;
	private JButton jDeleteButton = null;
	private JPanel jSelectionPanel = null;
	private JPanel dateFromPanel = null;
	private JPanel dateToPanel = null;
	private JTextField dayFrom = null;
	private JTextField monthFrom = null;
	private JTextField yearFrom = null;
	private JTextField dayTo = null;
	private JTextField monthTo = null;
	private JTextField yearTo = null;
	private JPanel jSelectionDiseasePanel = null;  //  @jve:decl-index=0:visual-constraint="232,358"
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JPanel jAgeFromPanel = null;
	private JLabel jLabel4 = null;
	private VoLimitedTextField jAgeFromTextField = null;
	private JPanel jAgeToPanel = null;
	private JLabel jLabel5 = null;
	private VoLimitedTextField jAgeToTextField = null;
	private JPanel jAgePanel = null;
	private JComboBox jDiseaseTypeBox;
	private JComboBox jDiseaseBox;
	private JComboBox jComboBoxWard;
	private JPanel sexPanel=null;
	private JPanel newPatientPanel=null;
	private ButtonGroup group=null;
	private ButtonGroup groupNewPatient=null;
	private Integer ageTo = 0;
	private Integer ageFrom = 0;
	private DiseaseType allType= new DiseaseType(MessageBundle.getMessage("angal.opd.alltype"),MessageBundle.getMessage("angal.opd.alltype"));
	//private String[] pColums = { MessageBundle.getMessage("angal.opd.datem"), MessageBundle.getMessage("angal.opd.fullname"), MessageBundle.getMessage("angal.opd.sexm"), MessageBundle.getMessage("angal.opd.agem"),MessageBundle.getMessage("angal.opd.diseasem"),MessageBundle.getMessage("angal.opd.diseasetypem"),MessageBundle.getMessage("angal.opd.patientstatus")};
	//MODIFIED : alex
	private String[] pColums = { MessageBundle.getMessage("angal.opd.datem"),
			MessageBundle.getMessage("angal.opd.patpcode"),
			MessageBundle.getMessage("angal.opd.fullname"),
			MessageBundle.getMessage("angal.opd.sexm"),
			MessageBundle.getMessage("angal.opd.agem"),
			MessageBundle.getMessage("angal.opd.diseasem"),
			MessageBundle.getMessage("angal.opd.diseasetypem"),
			MessageBundle.getMessage("angal.opd.patientstatus"),
			MessageBundle.getMessage("angal.admission.ward")
	};
	private ArrayList<OpdRow> pSur;
	private JTable jTable = null;
	private OpdBrowsingModel model;
	private int[] pColumwidth = { 20, 20, 40, 25, 25, 195, 195, 50, 50 };
	private boolean[] columnsVisible = { true, true, GeneralData.OPDEXTENDED, true, true, true, true, true, true };
	private int selectedrow;
	private Opd opd;
	private JButton filterButton = null;
	private String rowCounterText = MessageBundle.getMessage("angal.opd.count") + ": ";
	private JLabel rowCounter = null;
	private JRadioButton radioNew;
	private JRadioButton radioRea;
	private JRadioButton radioAll;
	private final JFrame myFrame;
	private JRadioButton radiom;
	private JRadioButton radiof;
	private JRadioButton radioa;
	private String chronicFilter = "all";
	// Managers
	private OpdBrowserManager manager = new OpdBrowserManager();

	final DateFormat currentDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);

	public JTable getJTable() {
		if (jTable == null) {
			model = new OpdBrowsingModel();
			jTable = new JTable(model);
			jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			TableColumnModel columnModel = jTable.getColumnModel();
			DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
			cellRenderer.setHorizontalAlignment(JLabel.RIGHT);
			if (GeneralData.OPDEXTENDED) {
				// Show also column displaying full name.
				columnModel.getColumn(0).setMinWidth(pColumwidth[0]);
				columnModel.getColumn(1).setMinWidth(pColumwidth[1]);
				columnModel.getColumn(2).setMinWidth(pColumwidth[2]);
				columnModel.getColumn(3).setMinWidth(pColumwidth[3]);
				columnModel.getColumn(3).setMaxWidth(pColumwidth[3]);
				columnModel.getColumn(4).setMinWidth(pColumwidth[4]);
				columnModel.getColumn(4).setMaxWidth(pColumwidth[4]);
				columnModel.getColumn(5).setMinWidth(pColumwidth[5]);
				columnModel.getColumn(6).setMinWidth(pColumwidth[6]);
				columnModel.getColumn(7).setMinWidth(pColumwidth[7]);
				columnModel.getColumn(8).setMinWidth(pColumwidth[8]);
				// Justify age column to the right.
				columnModel.getColumn(4).setCellRenderer(cellRenderer);
			} else {
				columnModel.getColumn(0).setMinWidth(pColumwidth[0]);
				columnModel.getColumn(1).setMinWidth(pColumwidth[1]);
				columnModel.getColumn(1).setMaxWidth(pColumwidth[1]);
				columnModel.getColumn(2).setMinWidth(pColumwidth[2]);
				columnModel.getColumn(2).setMaxWidth(pColumwidth[2]);
				columnModel.getColumn(3).setMinWidth(pColumwidth[3]);
				columnModel.getColumn(3).setMaxWidth(pColumwidth[3]);
				columnModel.getColumn(4).setMinWidth(pColumwidth[4]);
				columnModel.getColumn(5).setMinWidth(pColumwidth[5]);
				columnModel.getColumn(6).setMinWidth(pColumwidth[6]);
				columnModel.getColumn(7).setMinWidth(pColumwidth[7]);
				// Justify age column to the right.
				columnModel.getColumn(2).setCellRenderer(cellRenderer);
			}
		}
		return jTable;
	}

	/**
	 * This method initializes 
	 * 
	 */
	public OpdBrowser() {
		super();
		myFrame=this;
		initialize();
        setVisible(true);
	}
	
	public OpdBrowser(Patient patient) {
		super();
		myFrame=this;
		initialize();
        setVisible(true);
        opd = new Opd(0,' ',-1,"0",0,"");
        OpdEditExtended editrecord = new OpdEditExtended(myFrame, opd, patient, true);
        editrecord.addSurgeryListener(OpdBrowser.this);
		editrecord.setVisible(true);
	}

//	private boolean userHasOnlyChronicOpdPermission() {
//		return MainMenu.checkUserGrants("btnchropd") &&
//				!(
//						MainMenu.checkUserGrants("btnopdnew") ||
//						MainMenu.checkUserGrants("btnopdedit")  ||
//						MainMenu.checkUserGrants("btnopddel")
//				);
//	}

	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			if (MainMenu.checkUserGrants("btnopdnew")) jButtonPanel.add(getJNewButton(), null);
			if (MainMenu.checkUserGrants("btnopdedit")) jButtonPanel.add(getJEditButton(), null);
			if (MainMenu.checkUserGrants("btnopddel")) jButtonPanel.add(getJDeleteButton(), null);
			// Chronic module
			if (MainMenu.checkUserGrants("btnchropd")) {
				jButtonPanel.add(getChronicButton("new"), null);
				jButtonPanel.add(getChronicButton("edit"), null);
				jButtonPanel.add(getChronicButton("delete"), null);
			}
			jButtonPanel.add(getJCloseButton(), null);
		}
		return jButtonPanel;
	}

	/**
	 * Chronic Opd button.
	 * @return {@link JButton} that opens the Chronic Opd panel.
	 */
	private JButton getChronicButton(String type) {
		JButton chronicButton = new JButton(MessageBundle.getMessage("angal.opdchronic." + type));
		chronicButton.addActionListener(new ChronicButtonListener(type));
		return chronicButton;
	}

	class ChronicButtonListener implements ActionListener {
		private String type;

		ChronicButtonListener(String type) {
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedRow = jTable.getSelectedRow();
			if (type.equals("new")) {
				opd = new Opd(0,' ',-1,"0",0,"");
				openChronicOpdForm();
			} else if (type.equals("edit")) {
				if (selectedRow < 0) {
					JOptionPane.showMessageDialog(OpdBrowser.this,
							MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.opdchronic." + type),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				opd = ((OpdRow) jTable.getValueAt(selectedRow, -1)).getOpd();
				openChronicOpdForm();
			} else if (type.equals("delete")) {
				if (selectedRow < 0) {
					JOptionPane.showMessageDialog(OpdBrowser.this,
							MessageBundle.getMessage("angal.admission.pleaseselectarow"),
							MessageBundle.getMessage("angal.opdchronic." + type),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				Opd opd = ((OpdRow) jTable.getValueAt(selectedRow, -1)).getOpd();
				confirmDelete(opd);
			}
		}
	}

	private void openChronicOpdForm() {
		OpdChronicEdit chronicGui = new OpdChronicEdit(myFrame, opd);
		chronicGui.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		chronicGui.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				updateOpdTable();
			}
		});
		chronicGui.setVisible(true);
	}

	private void confirmDelete(Opd opd) {
		String dt = opd.getVisitDate() != null ?
				currentDateFormat.format(opd.getVisitDate().getTime()) : "[not specified]";
		int n = JOptionPane.showConfirmDialog(null,
				MessageBundle.getMessage("angal.opd.deletefollowingopd") + "\n" +
						"  "  + MessageBundle.getMessage("angal.opd.opdname") + " = " + opd.getFullName() + "\n" +
						"  "  + MessageBundle.getMessage("angal.opd.registrationdate") + " = " + dateFormat.format(opd.getDate()) + "\n" +
						"  "  + MessageBundle.getMessage("angal.opd.disease") + " = " + ((opd.getDiseaseDesc() == null) ?
						"  [" + MessageBundle.getMessage("angal.opd.notspecified") + "]" : opd.getDiseaseDesc()) + "\n" +
						"  "  + MessageBundle.getMessage("angal.opd.age") + " = " + opd.getAge()+ "\n" +
						"  Gender = " + opd.getSex() + "\n" +
						"  "  + MessageBundle.getMessage("angal.opd.visitdate") + " = " + dt + "\n" +
						"Confirm?",
				MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);

		if ((n == JOptionPane.YES_OPTION)
				&& (manager.deleteOpd(opd))) {
			pSur.remove(pSur.size() - jTable.getSelectedRow() - 1);
			model.fireTableDataChanged();
			jTable.updateUI();
		}
	}
	
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
        final int pfrmBase = 20;
        final int pfrmWidth = 17;
        final int pfrmHeight = 12;
        this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase ) / 2,
        		(screensize.height - screensize.height * pfrmHeight / pfrmBase)/2, 
                screensize.width * pfrmWidth / pfrmBase+50,
                screensize.height * pfrmHeight / pfrmBase+20);
		this.setTitle(MessageBundle.getMessage("angal.opd.opdoutpatientdepartment")+"("+VERSION+")");
		//this.setSize(new java.awt.Dimension(pfrmWidth,pfrmHeight));
		this.setContentPane(getJContainPanel());
		rowCounter.setText(rowCounterText + pSur.size());
		validate();
		//pack();
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * This method initializes containPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContainPanel() {
		if (jContainPanel == null) {
			jContainPanel = new JPanel();
			jContainPanel.setLayout(new BorderLayout());
			jContainPanel.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContainPanel.add(getJSelectionPanel(), java.awt.BorderLayout.WEST);
			jContainPanel.add(new JScrollPane(getJTable()),	java.awt.BorderLayout.CENTER);
			validate();
			//pack();
		}
		return jContainPanel;
	}
	
	/**
	 * This method initializes jNewButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJNewButton() {
		if (jNewButton == null) {
			jNewButton = new JButton();
			jNewButton.setText(MessageBundle.getMessage("angal.opd.new"));
			jNewButton.setMnemonic(KeyEvent.VK_N);
			jNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					opd = new Opd(0,' ',-1,"0",0,"");
					if (GeneralData.OPDEXTENDED) {
						OpdEditExtended newrecord = new OpdEditExtended(myFrame, opd, true);
						newrecord.addSurgeryListener(OpdBrowser.this);
						newrecord.setVisible(true);
					} else {
						OpdEdit newrecord = new OpdEdit(myFrame, opd, true);
						newrecord.addSurgeryListener(OpdBrowser.this);
						newrecord.setVisible(true);
					}
				}
			});
		}
		return jNewButton;
	}
	
	public void NewOpd() {
		jNewButton.doClick();
	}
	
	/**
	 * This method initializes jEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJEditButton() {
		if (jEditButton == null) {
			jEditButton = new JButton();
			jEditButton.setText(MessageBundle.getMessage("angal.opd.edit"));
			jEditButton.setMnemonic(KeyEvent.VK_E);
			jEditButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.opd.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						selectedrow = jTable.getSelectedRow();
						opd = ((OpdRow)(((OpdBrowsingModel) model).getValueAt(selectedrow, -1))).getOpd();
						if (GeneralData.OPDEXTENDED) {
							OpdEditExtended editrecord = new OpdEditExtended(myFrame, opd, false);
							editrecord.addSurgeryListener(OpdBrowser.this);
							editrecord.setVisible(true);
						} else {
							OpdEdit editrecord = new OpdEdit(myFrame, opd, false);
							editrecord.addSurgeryListener(OpdBrowser.this);
							editrecord.setVisible(true);
						}
					}
				}
			});
		}
		return jEditButton;
	}
	
	/**
	 * This method initializes jCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton.setText(MessageBundle.getMessage("angal.opd.close"));
            jCloseButton.setMnemonic(KeyEvent.VK_C);
			jCloseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
		}
		return jCloseButton;
	}
	
	/**
	 * This method initializes jDeleteButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJDeleteButton() {
		if (jDeleteButton == null) {
			jDeleteButton = new JButton();
			jDeleteButton.setText(MessageBundle.getMessage("angal.opd.delete"));
			jDeleteButton.setMnemonic(KeyEvent.VK_D);
			jDeleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.opd.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						Opd opd = ((OpdRow) (((OpdBrowsingModel) model).getValueAt(jTable.getSelectedRow(), -1))).getOpd();
						String dt="[not specified]";
						try {
							dt = currentDateFormat.format(opd.getVisitDate().getTime());
						}
						catch (Exception ex){
						}
		
						
						int n = JOptionPane.showConfirmDialog(null,
								MessageBundle.getMessage("angal.opd.deletefollowingopd") +
								"\n"+MessageBundle.getMessage("angal.opd.registrationdate")+"="+dateFormat.format(opd.getDate()) + 
								"\n"+MessageBundle.getMessage("angal.opd.disease")+"= "+ ((opd.getDiseaseDesc()==null)? "["+MessageBundle.getMessage("angal.opd.notspecified")+"]": opd.getDiseaseDesc()) + 
								"\n"+MessageBundle.getMessage("angal.opd.age")+"="+ opd.getAge()+", "+"Sex="+" " +opd.getSex()+
								"\n"+MessageBundle.getMessage("angal.opd.visitdate")+"=" + dt +
								"\n ?",
								MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);
						
						if ((n == JOptionPane.YES_OPTION)
								&& (manager.deleteOpd(opd))) {
							pSur.remove(pSur.size() - jTable.getSelectedRow()
									- 1);
							model.fireTableDataChanged();
							jTable.updateUI();
						}
					}
				}
				
			});
		}
		return jDeleteButton;
	}
	
	/**
	 * This method initializes jSelectionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJSelectionPanel() {
		if (jSelectionPanel == null) {
			JPanel filterButtonPanel = new JPanel();
			filterButtonPanel.add(getFilterButton());
			filterButtonPanel.add(getResetButton());
			jSelectionPanel = new JPanel();
			jSelectionPanel.setLayout(new BorderLayout());
			jSelectionPanel.setPreferredSize(new Dimension(300, pfrmHeight));
			jSelectionPanel.add(getOpdCodePanel(), BorderLayout.NORTH);
			jSelectionPanel.add(getOtherFiltersPanel(), BorderLayout.CENTER);
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.add(filterButtonPanel);
			p.add(getRowCounter());
			jSelectionPanel.add(p, BorderLayout.SOUTH);
		}
		return jSelectionPanel;
	}

	private JPanel opdCodePanel;
	private String opdCodeHint;
	private JTextField opdCodeFilter;
	private JPanel getOpdCodePanel() {
		if (opdCodePanel == null) {
			opdCodePanel = new JPanel();
			opdCodePanel = BorderedPanel.setMyBorder(opdCodePanel, "OPD Code");
			opdCodePanel.setLayout(new BoxLayout(opdCodePanel, BoxLayout.Y_AXIS));
			opdCodeFilter = new JTextField(9);
			opdCodeFilter.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {}

				@Override
				public void focusLost(FocusEvent e) {
					opdCodeHint = ((JTextField) e.getSource()).getText();
				}
			});
			opdCodeFilter.addKeyListener(new SearchOnEnterListener());
			opdCodePanel.add(opdCodeFilter, BorderLayout.NORTH);
		}
		return opdCodePanel;
	}

	private JPanel getOtherFiltersPanel() {
		JPanel panel = new JPanel();
		panel = BorderedPanel.setMyBorder(panel, "Other Filters");
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// Filter by Disease panel
		JPanel diseaseLabelPanel = new JPanel();
		diseaseLabelPanel.add(new JLabel(MessageBundle.getMessage("angal.opd.selectadisease")), null);
		panel.add(diseaseLabelPanel);
		panel.add(getJSelectionDiseasePanel());
		panel.add(Box.createVerticalGlue(), null);

		// Filter by Sex
		panel.add(getSexPanel(), null);
		// Filter chronic patients
		panel.add(getChronicPanel());
		// Filter by date
		panel.add(getDateFromPanel());
		panel.add(getDateToPanel());
		panel.add(Box.createVerticalGlue(), null);
		// Filter by Age
		panel.add(getJAgePanel(), null);
		panel.add(Box.createVerticalGlue(), null);
		// Filter by Patient Type panel
		JPanel newPatientLabelPanel = new JPanel();
		newPatientLabelPanel.add(new JLabel(MessageBundle.getMessage("angal.opd.patienttype")));
		panel.add(newPatientLabelPanel, null);
		panel.add(getNewPatientPanel(), null);
		return panel;
	}
	
	private JLabel getRowCounter() {
		if (rowCounter == null) {
			rowCounter = new JLabel();
			rowCounter.setAlignmentX(Box.CENTER_ALIGNMENT);
		}
		return rowCounter;
	}

	private JPanel getDateFromPanel() {
		if (dateFromPanel == null) {
			dateFromPanel = new JPanel();
			dateFromPanel.add(new JLabel(MessageBundle.getMessage("angal.opd.datefrom")), null);
			dayFrom = new JTextField(2);
			dayFrom.setDocument(new DocumentoLimitato(2));
			dayFrom.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					if (dayFrom.getText().length() != 0) {
						if (dayFrom.getText().length() == 1) {
							String typed = dayFrom.getText();
							dayFrom.setText("0" + typed);
						}
						if (!isValidDay(dayFrom.getText()))
							dayFrom.setText("1");
					}
				}
				
				public void focusGained(FocusEvent e) {
				}
			});
			monthFrom = new JTextField(2);
			monthFrom.setDocument(new DocumentoLimitato(2));
			monthFrom.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					if (monthFrom.getText().length() != 0) {
						if (monthFrom.getText().length() == 1) {
							String typed = monthFrom.getText();
							monthFrom.setText("0" + typed);
						}
						if (!isValidMonth(monthFrom.getText()))
							monthFrom.setText("1");
					}
				}
				
				public void focusGained(FocusEvent e) {
				}
			});
			yearFrom = new JTextField(4);
			yearFrom.setDocument(new DocumentoLimitato(4));
			yearFrom.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					if (yearFrom.getText().length() == 4) {
						if (!isValidYear(yearFrom.getText()))
							yearFrom.setText("2006");
					} else
						yearFrom.setText("2006");
				}
				
				public void focusGained(FocusEvent e) {
				}
			});
			dateFromPanel.add(dayFrom);
			dateFromPanel.add(monthFrom);
			dateFromPanel.add(yearFrom);
			GregorianCalendar now = new GregorianCalendar();
			if (!GeneralData.ENHANCEDSEARCH) now.add(GregorianCalendar.WEEK_OF_YEAR, -1);
			//now.roll(GregorianCalendar.WEEK_OF_YEAR, false);
			dayFrom.setText(String.valueOf(now
					.get(GregorianCalendar.DAY_OF_MONTH)));
			monthFrom.setText(String
					.valueOf(now.get(GregorianCalendar.MONTH) + 1));
			yearFrom.setText(String.valueOf(now.get(GregorianCalendar.YEAR)));
		}
		return dateFromPanel;
	}
	
	public class DocumentoLimitato extends DefaultStyledDocument {
		
		private static final long serialVersionUID = -5098766139884585921L;
		
		private final int NUMERO_MASSIMO_CARATTERI;
		
		public DocumentoLimitato(int numeroMassimoCaratteri) {
			NUMERO_MASSIMO_CARATTERI = numeroMassimoCaratteri;
		}
		
		public void insertString(int off, String text, AttributeSet att)
		throws BadLocationException {
			int numeroCaratteriNelDocumento = getLength();
			int lunghezzaNuovoTesto = text.length();
			if (numeroCaratteriNelDocumento + lunghezzaNuovoTesto > NUMERO_MASSIMO_CARATTERI) {
				int numeroCaratteriInseribili = NUMERO_MASSIMO_CARATTERI
				- numeroCaratteriNelDocumento;
				if (numeroCaratteriInseribili > 0) {
					String parteNuovoTesto = text.substring(0,
							numeroCaratteriInseribili);
					super.insertString(off, parteNuovoTesto, att);
				}
			} else {
				super.insertString(off, text, att);
			}
		}
	}
	
	
	private JPanel getDateToPanel() {
		if (dateToPanel == null) {
			dateToPanel = new JPanel();
			dateToPanel.add(new JLabel(MessageBundle.getMessage("angal.opd.dateto")), null);
			dayTo = new JTextField(2);
			dayTo.setDocument(new DocumentoLimitato(2));
			dayTo.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					if (dayTo.getText().length() != 0) {
						if (dayTo.getText().length() == 1) {
							String typed = dayTo.getText();
							dayTo.setText("0" + typed);
						}
						if (!isValidDay(dayTo.getText()))
							dayTo.setText("1");
					}
				}
				
				public void focusGained(FocusEvent e) {
				}
			});
			monthTo = new JTextField(2);
			monthTo.setDocument(new DocumentoLimitato(2));
			monthTo.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					if (monthTo.getText().length() != 0) {
						if (monthTo.getText().length() == 1) {
							String typed = monthTo.getText();
							monthTo.setText("0" + typed);
						}
						if (!isValidMonth(monthTo.getText()))
							monthTo.setText("1");
					}
				}
				
				public void focusGained(FocusEvent e) {
				}
			});
			yearTo = new JTextField(4);
			yearTo.setDocument(new DocumentoLimitato(4));
			yearTo.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					if (yearTo.getText().length() == 4) {
						if (!isValidYear(yearTo.getText()))
							yearTo.setText("2006");
					} else
						yearTo.setText("2006");
				}
				
				public void focusGained(FocusEvent e) {
				}
			});
			dateToPanel.add(dayTo);
			dateToPanel.add(monthTo);
			dateToPanel.add(yearTo);
			GregorianCalendar now = new GregorianCalendar();
			dayTo.setText(String.valueOf(now
					.get(GregorianCalendar.DAY_OF_MONTH)));
			monthTo.setText(String
					.valueOf(now.get(GregorianCalendar.MONTH) + 1));
			yearTo.setText(String.valueOf(now.get(GregorianCalendar.YEAR)));
			
		}
		return dateToPanel;
	}
	/**
	 * 
	 * @param day 
	 * 48 == '0'
	 * 57 == '9'
	 * @return
	 */
	private boolean isValidDay(String day) {		
		byte[] typed = day.getBytes();
		if (typed[0] < 48 || typed[0] > 57 || typed[1] < 48 || typed[1] > 57) {
			return false;
		}
		int num = Integer.valueOf(day);
		return !(num < 1 || num > 31);
	}
	
	private boolean isValidMonth(String month) {
		byte[] typed = month.getBytes();
		if (typed[0] < 48 || typed[0] > 57 || typed[1] < 48 || typed[1] > 57) {
			return false;
		}
		int num = Integer.valueOf(month);
		return !(num < 1 || num > 12);
	}
	
	private boolean isValidYear(String year) {
		byte[] typed = year.getBytes();
		return !(typed[0] < 48 || typed[0] > 57 || typed[1] < 48 || typed[1] > 57
				|| typed[2] < 48 || typed[2] > 57 || typed[3] < 48
				|| typed[3] > 57);
	}
	
	private GregorianCalendar getDateFrom() {
		return new GregorianCalendar(Integer.valueOf(yearFrom.getText()),
									 Integer.valueOf(monthFrom.getText()) - 1, 
									 Integer.valueOf(dayFrom.getText()));
	}
	
	private GregorianCalendar getDateTo() {
		return new GregorianCalendar(Integer.valueOf(yearTo.getText()), 
									 Integer.valueOf(monthTo.getText()) - 1, 
									 Integer.valueOf(dayTo.getText()));
	}

	private char getGender() {
		if (radioa.isSelected())
			return 'A';
		else if (radiom.isSelected())
			return 'M';
		else
			return 'F';
	}

	private String getPatientAttendance() {
		if(radioAll.isSelected())
			return "A";
		else if(radioNew.isSelected())
			return "N";
		else
			return "R";
	}

	
	
	
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	public JComboBox getDiseaseTypeBox() {
		if (jDiseaseTypeBox == null) {
			jDiseaseTypeBox = new JComboBox();
			jDiseaseTypeBox.setMaximumSize(new Dimension(300,50));
			
			DiseaseTypeBrowserManager manager = new DiseaseTypeBrowserManager();
			ArrayList<DiseaseType> types = manager.getDiseaseType();
			
			jDiseaseTypeBox.addItem(allType);
			
			for (DiseaseType elem : types) {
				jDiseaseTypeBox.addItem(elem);
			}
			
			jDiseaseTypeBox.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					//System.out.println("passato");
					jDiseaseBox.removeAllItems();
					getDiseaseBox();
				}
			});					
		}
		
		return jDiseaseTypeBox;
	}
	
	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	public JComboBox getDiseaseBox() {
		if (jDiseaseBox == null) {
			jDiseaseBox = new JComboBox();
			jDiseaseBox.setMaximumSize(new Dimension(300, 50));
			
		};
		DiseaseBrowserManager manager = new DiseaseBrowserManager();
		ArrayList<Disease> diseases;
		if (((DiseaseType)jDiseaseTypeBox.getSelectedItem()).getDescription().equals(MessageBundle.getMessage("angal.opd.alltype"))){
			diseases = manager.getDiseaseOpd();
		}else{
			diseases = manager.getDiseaseOpd(((DiseaseType)jDiseaseTypeBox.getSelectedItem()).getCode());
		};
		Disease allDisease = new Disease(MessageBundle.getMessage("angal.opd.alldisease"), MessageBundle.getMessage("angal.opd.alldisease"), allType, 0, 0, true, true, false, 0);
		jDiseaseBox.addItem(allDisease);
		for (Disease elem : diseases) {
			jDiseaseBox.addItem(elem);
		}		
		return jDiseaseBox;
	}
	
	private JComboBox getJComboBoxWard() {

		if (jComboBoxWard == null) {
			jComboBoxWard = new JComboBox();
			WardBrowserManager wardManager = new WardBrowserManager();
			ArrayList<Ward> wardList = wardManager.getWardsOPD();
			jComboBoxWard.addItem(MessageBundle.getMessage("angal.medicalstockward.selectaward"));
			for (Ward ward : wardList) {
				
				jComboBoxWard.addItem(ward);

			}
			jComboBoxWard.setBorder(null);
			jComboBoxWard.setBounds(15, 14, 122, 24);
			jComboBoxWard.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				
				}
			});
		}
		return jComboBoxWard;
	}
	
	/**
	 * This method initializes sexPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JPanel getSexPanel() {
		if (sexPanel == null) {
			sexPanel = new JPanel();
			group=new ButtonGroup();
			radiom= new JRadioButton(MessageBundle.getMessage("angal.opd.male"));
			radiof= new JRadioButton(MessageBundle.getMessage("angal.opd.female"));
			radioa= new JRadioButton(MessageBundle.getMessage("angal.opd.all"));
			radioa.setSelected(true);
			group.add(radiom);
			group.add(radiof);
			group.add(radioa);
			sexPanel.add(radiom);
			sexPanel.add(radiof);
			sexPanel.add(radioa);

		}
		return sexPanel;
	}

	private JRadioButton chronic;
	private JRadioButton nonChronic;
	private JRadioButton chronicAndNonChronic;
	private JPanel getChronicPanel() {
		JPanel chronicPanel = new JPanel();
		ButtonGroup chronicGroup = new ButtonGroup();
		chronic = new JRadioButton(MessageBundle.getMessage("angal.opdchronic.chronicradio"));
		chronic.setName("chronic");
		chronic.addItemListener(new ChronicRadioListener());
		nonChronic = new JRadioButton(MessageBundle.getMessage("angal.opdchronic.nonchronicradio"));
		nonChronic.setName("non-chronic");
		nonChronic.addItemListener(new ChronicRadioListener());
		chronicAndNonChronic = new JRadioButton(MessageBundle.getMessage("angal.opdchronic.allradio"));
		chronicAndNonChronic.setName("all");
		chronicAndNonChronic.addItemListener(new ChronicRadioListener());
		chronicAndNonChronic.setSelected(true);
		chronicGroup.add(chronic);
		chronicGroup.add(nonChronic);
		chronicGroup.add(chronicAndNonChronic);
		chronicPanel.add(chronic);
		chronicPanel.add(nonChronic);
		chronicPanel.add(chronicAndNonChronic);
		return chronicPanel;
	}

	class ChronicRadioListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				chronicFilter = ((JRadioButton) e.getSource()).getName();
			}
		}
	}
	
	public JPanel getNewPatientPanel() {
		if (newPatientPanel == null) {
			newPatientPanel = new JPanel();
			groupNewPatient=new ButtonGroup();
			radioNew= new JRadioButton(MessageBundle.getMessage("angal.opd.new"));
			radioRea= new JRadioButton(MessageBundle.getMessage("angal.opd.reattendance"));
			radioAll= new JRadioButton(MessageBundle.getMessage("angal.opd.all"));
			radioAll.setSelected(true);
			groupNewPatient.add(radioAll);
			groupNewPatient.add(radioNew);
			groupNewPatient.add(radioRea);
			newPatientPanel.add(radioAll);
			newPatientPanel.add(radioNew);
			newPatientPanel.add(radioRea);
		}
		return newPatientPanel;
	}
	
	
	/**
	 * This method initializes jSelectionDiseasePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJSelectionDiseasePanel() {
		if (jSelectionDiseasePanel == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("    ");
			jLabel3 = new JLabel();
			jLabel3.setText("    ");
			jSelectionDiseasePanel = new JPanel();
			jSelectionDiseasePanel.setLayout(new BoxLayout(jSelectionDiseasePanel,BoxLayout.Y_AXIS));
			jSelectionDiseasePanel.add(getDiseaseTypeBox(), null);
			jSelectionDiseasePanel.add(jLabel2, null);
			jSelectionDiseasePanel.add(getDiseaseBox(), null);
			jSelectionDiseasePanel.add(jLabel3, null);
			jSelectionDiseasePanel.add(getJComboBoxWard(), null);
		}
		return jSelectionDiseasePanel;
	}

	/**
	 * This method initializes jAgePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJAgeFromPanel() {
		if (jAgeFromPanel == null) {
			jLabel4 = new JLabel();
			jLabel4.setText(MessageBundle.getMessage("angal.opd.agefrom"));
			jAgeFromPanel = new JPanel();
			jAgeFromPanel.add(jLabel4, null);
			jAgeFromPanel.add(getJAgeFromTextField(), null);
		}
		return jAgeFromPanel;
	}
	
	/**
	 * This method initializes jAgeFromTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private VoLimitedTextField getJAgeFromTextField() {
		if (jAgeFromTextField == null) {
			jAgeFromTextField = new VoLimitedTextField(3, 2);
			jAgeFromTextField.setText("0");
			jAgeFromTextField.setMinimumSize(new Dimension(100, 50));
			ageFrom=0;
			jAgeFromTextField.addFocusListener(new AgeFocusListener("ageFrom"));
		}
		return jAgeFromTextField;
	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJAgeToPanel() {
		if (jAgeToPanel == null) {
			jLabel5 = new JLabel();
			jLabel5.setText(MessageBundle.getMessage("angal.opd.ageto"));
			jAgeToPanel = new JPanel();
			jAgeToPanel.add(jLabel5, null);
			jAgeToPanel.add(getJAgeToTextField(), null);
		}
		return jAgeToPanel;
	}
	
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private VoLimitedTextField getJAgeToTextField() {
		if (jAgeToTextField == null) {
			jAgeToTextField = new VoLimitedTextField(3, 2);
			jAgeToTextField.setText("0");
			jAgeToTextField.setMaximumSize(new Dimension(100, 50));
			ageTo=0;
			jAgeToTextField.addFocusListener(new AgeFocusListener("ageTo"));
		}
		return jAgeToTextField;
	}

	/**
	 * This method initializes jAgePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJAgePanel() {
		if (jAgePanel == null) {
			jAgePanel = new JPanel();
//			jAgePanel.setLayout(new BoxLayout(getJAgePanel(),BoxLayout.Y_AXIS));
			jAgePanel.setLayout(new FlowLayout());
			jAgePanel.add(getJAgeFromPanel(), null);
			jAgePanel.add(getJAgeToPanel(), null);
		}
		return jAgePanel;
	}
	
	class OpdBrowsingModel extends DefaultTableModel {
		
		private static final long serialVersionUID = -9129145534999353730L;
		
		public OpdBrowsingModel(String diseaseTypeCode, String diseaseCode, String wardCode,
								String opdCodeHint, String chronicFilter, GregorianCalendar dateFrom,
								GregorianCalendar dateTo, int ageFrom, int ageTo, char sex, String newPatient) {
			pSur = manager.getOpdRows(diseaseTypeCode, diseaseCode, wardCode, opdCodeHint, chronicFilter, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient);
		}
		
		public OpdBrowsingModel() {
			pSur = manager.getOpdRows(!GeneralData.ENHANCEDSEARCH);
		}
		
		public int getRowCount() {
			if (pSur == null)
				return 0;
			return pSur.size();
		}
		
		public String getColumnName(int c) {
			return pColums[getNumber(c)];
		}
		
		public int getColumnCount() {
			int c = 0;
			for (int i = 0; i < columnsVisible.length; i++) {
				if (columnsVisible[i]) {
					c++;
				}
			}
			return c;
		}
		
		public Object getValueAt(int r, int c) {
			OpdRow opdRow = pSur.get(pSur.size() - r - 1);
			if (c == -1) {
				//System.out.println(MessageBundle.getMessage("angal.opd.passolariga")+ String.valueOf(pSur.size() - r - 1));
				return opdRow;
			} else if (getNumber(c) == 0) {
				String sVisitDate;
				if (opdRow.getOpd().getVisitDate() == null) {
					sVisitDate = "";
				} else {
					sVisitDate = dateFormat.format(opdRow.getOpd().getVisitDate().getTime());
				}
				return sVisitDate;
			} else if (getNumber(c) == 1) {
				return opdRow.getPatPCode(); //manager.getPatPCode();
			} else if (getNumber(c) == 2) {
				return opdRow.getOpd().getFullName(); //MODIFIED: alex
			} else if (getNumber(c) == 3) {
				return opdRow.getOpd().getSex();
			} else if (getNumber(c) == 4) {
				return opdRow.getOpd().getAge();
			} else if (getNumber(c) == 5) {
				return opdRow.getOpd().getDiseaseDesc();
			} else if (getNumber(c) == 6) {
				return opdRow.getOpd().getDiseaseTypeDesc();
			} else if (getNumber(c) == 7) {
				String patientStatus;
				if (opdRow.getOpd().getNewPatient().equals("N")){
					patientStatus = MessageBundle.getMessage("angal.opd.new");
				} else {
					patientStatus = MessageBundle.getMessage("angal.opd.reattendance");
				}
				return patientStatus;
			}else if (getNumber(c) == 8) {
				ArrayList<Ward> wardL;
				WardBrowserManager wardManager = new WardBrowserManager();
				wardL = wardManager.getWards();
				Ward ward2 = null;
				for (Ward ward : wardL) {
					
					if (ward.getCode().equalsIgnoreCase(opdRow.getOpd().getWard()))
						return ward;

				}
				 
			}
			
			return null;
		}//"DATE", "PROG YEAR", "SEX", "AGE","DISEASE","DISEASE TYPE"};
		
		
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			// return super.isCellEditable(arg0, arg1);
			return false;
		}
		
		/** 
	     * This method converts a column number in the table
	     * to the right number of the datas.
	     */
	    protected int getNumber(int col) {
	    	// right number to return
	        int n = col;    
	        int i = 0;
	        do {
	            if (!columnsVisible[i]) {
	            	n++;
	            }
	            i++;
	        } while (i < n);
	        // If we are on an invisible column, 
	        // we have to go one step further
	        while (!columnsVisible[n]) {
	        	n++;
	        }
	        return n;
	    }
	}
	
	
	public void surgeryUpdated(AWTEvent e) {
		pSur.set(pSur.size() - selectedrow - 1, new OpdRow(opd, manager.getPatPCode(opd.getpatientCode())));
		//System.out.println("riga->" + selectedrow);
		((OpdBrowsingModel) jTable.getModel()).fireTableDataChanged();
		jTable.updateUI();
		if ((jTable.getRowCount() > 0) && selectedrow > -1)
			jTable.setRowSelectionInterval(selectedrow, selectedrow);
		rowCounter.setText(rowCounterText + pSur.size());
	}
	
	public void surgeryInserted(AWTEvent e) {
		pSur.add(pSur.size(), new OpdRow(opd, manager.getPatPCode(opd.getpatientCode())));
		((OpdBrowsingModel) jTable.getModel()).fireTableDataChanged();
		if (jTable.getRowCount() > 0)
			jTable.setRowSelectionInterval(0, 0);
		rowCounter.setText(rowCounterText + pSur.size());
	}
	
	private JButton getFilterButton() {
		if (filterButton == null) {
			filterButton = new JButton(MessageBundle.getMessage("angal.opd.search"));
            filterButton.setMnemonic(KeyEvent.VK_S);
			filterButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if(ageFrom > ageTo){
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.agefrommustbelowerthanageto"));
						jAgeFromTextField.setText(ageTo.toString());
						ageFrom = ageTo;
						return;
					}
					updateOpdTable();
				}
				
			});
		}
		return filterButton;
	}

	private JButton resetButton;
	private JButton getResetButton() {
		if(resetButton == null) {
			resetButton = new JButton("Reset");
			resetButton.setMnemonic(KeyEvent.VK_R);
			resetButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					resetAllFilters();
				}
			});
		}
		return resetButton;
	}

	private void resetAllFilters() {
		jDiseaseTypeBox.setSelectedIndex(0);
		jDiseaseBox.setSelectedIndex(0);
		jComboBoxWard.setSelectedIndex(0);
		if (opdCodeFilter != null)
			opdCodeFilter.setText("");
		if (chronicAndNonChronic != null)
			chronicAndNonChronic.setSelected(true);
		opdCodeHint = null;
		Calendar now = Calendar.getInstance();
		yearFrom.setText(now.get(Calendar.YEAR) + "");
		monthFrom.setText(now.get(Calendar.MONTH) + 1  + "");
		dayFrom.setText(now.get(Calendar.DAY_OF_MONTH)+ "");
		yearTo.setText(now.get(Calendar.YEAR) + "");
		monthTo.setText(now.get(Calendar.MONTH) + 1 + "");
		dayTo.setText(now.get(Calendar.DAY_OF_MONTH) + "");
		jAgeFromTextField.setText("0");
		ageFrom = 0;
		jAgeToTextField.setText("0");
		ageTo = 0;
		radioa.setSelected(true);
		radioAll.setSelected(true);
	}

	private void updateOpdTable() {
		try {
			String wardId;
			BusyState.setBusyState(OpdBrowser.this, true);
			if  (jComboBoxWard.getSelectedIndex() != 0) 
			{
				Ward ward = (Ward) jComboBoxWard.getSelectedItem();
				wardId = ward.getCode();
			}else 
			{
				wardId	= MessageBundle.getMessage("angal.medicalstockward.selectaward");
			}
			model = new OpdBrowsingModel(
					((DiseaseType) jDiseaseTypeBox.getSelectedItem()).getCode(),
					((Disease) jDiseaseBox.getSelectedItem()).getCode(),
					wardId,
					opdCodeHint,
					chronicFilter,
					getDateFrom(),
					getDateTo(),
					ageFrom,
					ageTo,
					getGender(),
					getPatientAttendance());
			jTable.updateUI();
			rowCounter.setText(rowCounterText + pSur.size());
		} finally {
			BusyState.setBusyState(OpdBrowser.this, false);
		}
	}

	class AgeFocusListener implements FocusListener {

		private String field;
		VoLimitedTextField textField;

		AgeFocusListener(String field) {
			this.field = field;
		}

		@Override 
		public void focusGained(FocusEvent e) {}

		@Override
		public void focusLost(FocusEvent e) {
			try {
				textField = (VoLimitedTextField) e.getSource();
				Integer age = Integer.parseInt(textField.getText());
				if ((age < 0)||(age > 200)) {
					textField.setText("");
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.insertvalidage"));
				} else {
					setField(field, age);
				}
			} catch (NumberFormatException ex) {
				textField.setText("0");
				setField(field, 0);
			}
		}
		 private void setField(String field, Integer age) {
			 if (field.equals("ageFrom"))
				 ageFrom = age;
			 else
				 ageTo = age;
		 }
	}

	class SearchOnEnterListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				opdCodeHint = ((JTextField) e.getSource()).getText();
				updateOpdTable();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	}

}