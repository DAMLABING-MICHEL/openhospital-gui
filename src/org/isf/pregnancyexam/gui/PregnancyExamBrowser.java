package org.isf.pregnancyexam.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.isf.generaldata.MessageBundle;
import org.isf.pregnancyexam.gui.PregnancyExamEdit.PregnancyExamListener;
import org.isf.pregnancyexam.manager.PregnancyExamManager;
import org.isf.pregnancyexam.model.PregnancyExam;

public class PregnancyExamBrowser extends JFrame implements PregnancyExamListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 */
	private PregnancyExamBrowser myFrame;
	/**
	 */
	private ArrayList<PregnancyExam> exams = null;
	/**
	 */
	private PregnancyExamManager manager = null;
	/**
	 */
	private PregnancyExam exam = null;
	/**
	 */
	private JTable examtable = null;
	/**
	 */
	private int[] eColumwidth = { 120, 120 };
	/**
	 */
	private String[] headers = { MessageBundle.getMessage("angal.exa.descriptionm"),  
			MessageBundle.getMessage("angal.pregnancyexam.type")};
			
	
	
	public PregnancyExamBrowser(){
		myFrame = this;
		setTitle(MessageBundle.getMessage("angal.pregnancyexam.browser"));
		manager = new PregnancyExamManager();
		
		initComponents();
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if(exams!= null) exams.clear();

			}
		});
		
				
	}
	private void initComponents(){
		add(getExamPanel(), java.awt.BorderLayout.CENTER);
		add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
	}
	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(getButtonNewExam());
		buttonPanel.add(getButtonEditExam());
		buttonPanel.add(getButtonDeleteExam());
		buttonPanel.add(getButtonClose());
		return buttonPanel;
	}
	private JButton getButtonClose() {
		JButton buttonClose = new JButton(
				MessageBundle.getMessage("angal.pregnancy.close"));
		buttonClose.setMnemonic(KeyEvent.VK_T);
		buttonClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		return buttonClose;

	}
	
	private JButton getButtonEditExam() {
		JButton buttonClose = new JButton(
				MessageBundle.getMessage("angal.pregnancyexam.edit"));
		buttonClose.setMnemonic(KeyEvent.VK_T);
		buttonClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if(examtable.getSelectedRow()<0){
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.pregnancyexam.pleaseselectrow"),
							MessageBundle.getMessage("angal.pregnancyexam.edit"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				exam = (PregnancyExam) examtable.getValueAt(examtable.getSelectedRow(), -1);
				PregnancyExamEdit i = new PregnancyExamEdit(myFrame, exam);
				i.addExamListener(PregnancyExamBrowser.this);
				i.setVisible(true);
				//i.show();
			}
		});
		return buttonClose;

	}
	private JButton getButtonDeleteExam() {
		JButton buttonClose = new JButton(
				MessageBundle.getMessage("angal.pregnancyexam.delete"));
		buttonClose.setMnemonic(KeyEvent.VK_T);
		buttonClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if(examtable.getSelectedRow()<0){
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.pregnancyexam.pleaseselectrow"),
							MessageBundle.getMessage("angal.pregnancyexam.edit"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				exam = (PregnancyExam) examtable.getValueAt(examtable.getSelectedRow(), -1);
				manager.deletePregnancyExam(exam.getExamId());
				exams.remove(exam);
				examtable.setModel(new PregnancyExamBrowserModel());
				
			}
		});
		return buttonClose;

	}
	private JButton getButtonNewExam() {
		JButton buttonClose = new JButton(
				MessageBundle.getMessage("angal.pregnancyexam.new"));
		buttonClose.setMnemonic(KeyEvent.VK_T);
		buttonClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				PregnancyExamEdit examedit = new PregnancyExamEdit(myFrame, null);
				examedit.addExamListener(PregnancyExamBrowser.this);
				examedit.setVisible(true);
			}
		});
		return buttonClose;

	}
	private JPanel getExamPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getExamsScrollPane(), BorderLayout.CENTER);
		return panel;
		
	}
	private JScrollPane getExamsScrollPane() {
		examtable = new JTable(new PregnancyExamBrowserModel());
		for (int i = 0; i < headers.length; i++) {
			examtable.getColumnModel().getColumn(i).setPreferredWidth(
					eColumwidth[i]);
		}

		int tableWidth = 0;
		for (int i = 0; i<eColumwidth.length; i++){
			tableWidth += eColumwidth[i];
		}
		//test this
		JScrollPane examScrollPane = new JScrollPane(examtable);
		examScrollPane.setPreferredSize(new Dimension(tableWidth+200, 200));
		return examScrollPane;
	}
	
	class PregnancyExamBrowserModel extends DefaultTableModel{
		private static final long serialVersionUID = 1L;
		
		
		public PregnancyExamBrowserModel(){
			exams = manager.getPregnancyExams();
		}
		public int getRowCount() {
			if (exams == null)
				return 0;
			return exams.size();
		}

		public String getColumnName(int c) {
			return headers[c];
		}

		public int getColumnCount() {
			return headers.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return exams.get(r);
			} else if (c == 0) {
				return (exams.get(r)).getExamDesc();
						
			} 
			
			else if (c == 1) {
				int type = exams.get(r).getExamType();
				switch (type){
				
				case -1:
					return MessageBundle.getMessage("angal.pregnancyexam.prenatal");
				case -0:
					return MessageBundle.getMessage("angal.pregnancyexam.preandpostnatal");
				case 1:
					return MessageBundle.getMessage("angal.pregnancyexam.postnatal");
			
				}
			}
			
			return null;
		}
		
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
		
	}
	@Override
	public void examUpdated(AWTEvent e) {
		examtable.setModel(new PregnancyExamBrowserModel());
		try {
			if (examtable.getRowCount() > 0)
				examtable.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {
		}
	}
	@Override
	public void examInserted(AWTEvent e) {
		examtable.setModel(new PregnancyExamBrowserModel());
		try {
			if (examtable.getRowCount() > 0)
				examtable.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {
		}
		
	}


}
