package org.isf.pregtreattype.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.isf.generaldata.MessageBundle;
import org.isf.pregtreattype.gui.PregnantTreatmentTypeEdit.PregnantTreatmentTypeListener;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.jobjects.ModalJFrame;

/**
 * Browsing of table PregnantTreatmentType
 * 
 * @author Furlanetto, Zoia, Finotto
 * 
 */

public class PregnantTreatmentTypeBrowser extends ModalJFrame implements PregnantTreatmentTypeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<PregnantTreatmentType> pPregnantTreatmentType;
	private String[] pColums = { MessageBundle.getMessage("angal.preagtreattype.code"), MessageBundle.getMessage("angal.preagtreattype.description") };
	private int[] pColumwidth = {80, 200 };
	private JPanel jContainPanel = null;
	private JPanel jButtonPanel = null;
	private JButton jNewButton = null;
	private JButton jEditButton = null;
	private JButton jCloseButton = null;
	private JButton jDeteleButton = null;
	private JTable jTable = null;
	private PregnantTreatmentTypeBrowserModel model;
	private int selectedrow;
	private PregnantTreatmentTypeBrowserManager manager = new PregnantTreatmentTypeBrowserManager();
	private PregnantTreatmentType pregnantTreatmentType = null;
	private final JFrame myFrame;
	
	
	
	
	/**
	 * This method initializes 
	 * 
	 */
	public PregnantTreatmentTypeBrowser() {
		super();
		myFrame=this;
		initialize();
		setVisible(true);
	}
	
	
	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		final int pfrmBase = 10;
        final int pfrmWidth = 5;
        final int pfrmHeight =4;
        this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase ) / 2, (screensize.height - screensize.height * pfrmHeight / pfrmBase)/2, 
                screensize.width * pfrmWidth / pfrmBase, screensize.height * pfrmHeight / pfrmBase);
		this.setTitle(MessageBundle.getMessage("angal.preagtreattype.pregnanttreatmenttypebrowsing"));
		this.setContentPane(getJContainPanel());
		//pack();	
	}
	
	
	private JPanel getJContainPanel() {
		if (jContainPanel == null) {
			jContainPanel = new JPanel();
			jContainPanel.setLayout(new BorderLayout());
			jContainPanel.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContainPanel.add(new JScrollPane(getJTable()),
					java.awt.BorderLayout.CENTER);
			validate();
		}
		return jContainPanel;
	}
	
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.add(getJNewButton(), null);
			jButtonPanel.add(getJEditButton(), null);
			jButtonPanel.add(getJDeteleButton(), null);
			jButtonPanel.add(getJCloseButton(), null);
		}
		return jButtonPanel;
	}
	
	
	private JButton getJNewButton() {
		if (jNewButton == null) {
			jNewButton = new JButton();
			jNewButton.setText(MessageBundle.getMessage("angal.preagtreattype.new"));
			jNewButton.setMnemonic(KeyEvent.VK_N);
			jNewButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					pregnantTreatmentType = new PregnantTreatmentType("","");
					PregnantTreatmentTypeEdit newrecord = new PregnantTreatmentTypeEdit(myFrame,pregnantTreatmentType, true);
					newrecord.addPregnantTreatmentTypeListener(PregnantTreatmentTypeBrowser.this);
					newrecord.setVisible(true);
				}
			});
		}
		return jNewButton;
	}
	
	/**
	 * This method initializes jEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJEditButton() {
		if (jEditButton == null) {
			jEditButton = new JButton();
			jEditButton.setText(MessageBundle.getMessage("angal.preagtreattype.edit"));
			jEditButton.setMnemonic(KeyEvent.VK_E);
			jEditButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.preagtreattype.pleaseselectarow"),
								MessageBundle.getMessage("angal.preagtreattype.stlukehospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						selectedrow = jTable.getSelectedRow();
						pregnantTreatmentType = (PregnantTreatmentType) (((PregnantTreatmentTypeBrowserModel) model)
								.getValueAt(selectedrow, -1));
						PregnantTreatmentTypeEdit newrecord = new PregnantTreatmentTypeEdit(myFrame,pregnantTreatmentType, false);
						newrecord.addPregnantTreatmentTypeListener(PregnantTreatmentTypeBrowser.this);
						newrecord.setVisible(true);
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
			jCloseButton.setText(MessageBundle.getMessage("angal.preagtreattype.close"));
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
	 * This method initializes jDeteleButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJDeteleButton() {
		if (jDeteleButton == null) {
			jDeteleButton = new JButton();
			jDeteleButton.setText(MessageBundle.getMessage("angal.preagtreattype.delete"));
			jDeteleButton.setMnemonic(KeyEvent.VK_D);
			jDeteleButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.preagtreattype.pleaseselectarow"),
								MessageBundle.getMessage("angal.preagtreattype.stlukehospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						PregnantTreatmentType dis = (PregnantTreatmentType) (((PregnantTreatmentTypeBrowserModel) model)
								.getValueAt(jTable.getSelectedRow(), -1));
						int n = JOptionPane.showConfirmDialog(null,
								MessageBundle.getMessage("angal.preagtreattype.deletetreatmenttype") + " \" "+dis.getDescription() + "\" ?",
								MessageBundle.getMessage("angal.preagtreattype.stlukehospital"), JOptionPane.YES_NO_OPTION);
						
						if ((n == JOptionPane.YES_OPTION)
								&& (manager.deletePregnantTreatmentType(dis))) {
							pPregnantTreatmentType.remove(jTable.getSelectedRow());
							model.fireTableDataChanged();
							jTable.updateUI();
						}
					}
				}
				
			});
		}
		return jDeteleButton;
	}
	
	public JTable getJTable() {
		if (jTable == null) {
			model = new PregnantTreatmentTypeBrowserModel();
			jTable = new JTable(model);
			jTable.getColumnModel().getColumn(0).setMinWidth(pColumwidth[0]);
			jTable.getColumnModel().getColumn(1).setMinWidth(pColumwidth[1]);
		}return jTable;
	}
	
	
	
class PregnantTreatmentTypeBrowserModel extends DefaultTableModel {
		
		
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		public PregnantTreatmentTypeBrowserModel() {
			PregnantTreatmentTypeBrowserManager manager = new PregnantTreatmentTypeBrowserManager();
			pPregnantTreatmentType = manager.getPregnantTreatmentType();
		}
		
		public int getRowCount() {
			if (pPregnantTreatmentType == null)
				return 0;
			return pPregnantTreatmentType.size();
		}
		
		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == 0) {
				return pPregnantTreatmentType.get(r).getCode();
			} else if (c == -1) {
				return pPregnantTreatmentType.get(r);
			} else if (c == 1) {
				return pPregnantTreatmentType.get(r).getDescription();
			}
			return null;
		}
		
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			//return super.isCellEditable(arg0, arg1);
			return false;
		}
	}




public void pregnantTreatmentTypeUpdated(AWTEvent e) {
	pPregnantTreatmentType.set(selectedrow, pregnantTreatmentType);
	((PregnantTreatmentTypeBrowserModel) jTable.getModel()).fireTableDataChanged();
	jTable.updateUI();
	if ((jTable.getRowCount() > 0) && selectedrow > -1)
		jTable.setRowSelectionInterval(selectedrow, selectedrow);
}


public void pregnantTreatmentTypeInserted(AWTEvent e) {
	pPregnantTreatmentType.add(0, pregnantTreatmentType);
	((PregnantTreatmentTypeBrowserModel) jTable.getModel()).fireTableDataChanged();
	if (jTable.getRowCount() > 0)
		jTable.setRowSelectionInterval(0, 0);
}
	
	
}
