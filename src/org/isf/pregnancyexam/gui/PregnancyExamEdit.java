package org.isf.pregnancyexam.gui;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;

import org.isf.exa.model.Exam;
import org.isf.generaldata.MessageBundle;
import org.isf.pregnancyexam.manager.PregnancyExamManager;
import org.isf.pregnancyexam.model.PregnancyExam;
import org.isf.utils.jobjects.VoLimitedTextField;

public class PregnancyExamEdit extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 */
	private EventListenerList examListeners = new EventListenerList();

	public interface PregnancyExamListener extends EventListener {
		public void examUpdated(AWTEvent e);

		public void examInserted(AWTEvent e);
	}

	public void addExamListener(PregnancyExamListener l) {
		examListeners.add(PregnancyExamListener.class, l);
	}

	public void removePregnancyExamListener(PregnancyExamListener listener) {
		examListeners.remove(PregnancyExamListener.class, listener);
	}

	private void firePregnancyExamInserted(PregnancyExam aExam) {
		AWTEvent event = new AWTEvent(aExam, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = examListeners
				.getListeners(PregnancyExamListener.class);
		for (int i = 0; i < listeners.length; i++)
			((PregnancyExamListener) listeners[i]).examInserted(event);
	}

	private void firePregnancyExamUpdated(PregnancyExam aExam) {
		AWTEvent event = new AWTEvent(aExam, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = examListeners
				.getListeners(PregnancyExamListener.class);
		for (int i = 0; i < listeners.length; i++)
			((PregnancyExamListener) listeners[i]).examUpdated(event);
	}

	/**
	 */
	private PregnancyExamEdit myFrame = null;
	/**
	 */
	private PregnancyExam selectedPregnancyExam = null;
	/**
	 */
	private JComboBox examtype = null;
	/**
	 */
	private ArrayList<Exam> exams = null;
	/**
	 */
	private PregnancyExamManager manager = null;
	
	
	/**
	 */
	private JPanel exampanel = null;
	/**
	 */
	private VoLimitedTextField examDesc = null;
	/**
	 */
	private VoLimitedTextField examCode = null;
	/**
	 */
	private VoLimitedTextField examDefault = null;
	/**
	 */
	private JTextArea examValues = null;
	

	/**
	 */
	private boolean isInsert = true;

	public PregnancyExamEdit(JFrame owner, PregnancyExam exam) {
		super(owner, true);
		myFrame = this;
		manager = new PregnancyExamManager();
		if (exam != null) {
			this.selectedPregnancyExam = exam;
			this.isInsert = false;
			this.setTitle(MessageBundle.getMessage("angal.pregnancyexam.edit"));
		} else {
			this.setTitle(MessageBundle.getMessage("angal.pregnancyexam.new"));
		}
		
		this.setBounds(400, 400, 450, 700);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initComponents();
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (exams != null)
					exams.clear();

			}
		});

	}

	private void initComponents() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		add(getExamPanel());
		add(getExamTypePanel());
		add(getButtonPanel());
		setLocationRelativeTo(null);
	}

	
	

	
	private JPanel getExamPanel() {
		if (exampanel == null)
			exampanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		exampanel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancyexam.details")));
		JLabel l1 = new JLabel(MessageBundle
				.getMessage("angal.pregnancyexam.code"));
		examCode = new VoLimitedTextField(10);
		examCode.setPreferredSize(new Dimension(370, 30));
		JLabel l2 = new JLabel(MessageBundle
				.getMessage("angal.pregnancyexam.name"));
		examDesc = new VoLimitedTextField(50);
		examDesc.setPreferredSize(new Dimension(370, 30));
		JLabel l3 = new JLabel(MessageBundle
				.getMessage("angal.pregnancyexam.defaultvalue"));
		examDefault = new VoLimitedTextField(50);
		examDefault.setPreferredSize(new Dimension(370, 30));
		examValues = new JTextArea();
		JLabel l4 = new JLabel(MessageBundle
				.getMessage("angal.pregnancyexam.values"));
		if(!isInsert){
			examCode.setText(selectedPregnancyExam.getExamId());
			examCode.setEnabled(false);
			examDesc.setText(selectedPregnancyExam.getExamDesc());
			examDefault.setText(selectedPregnancyExam.getExamDefault());
			examValues.setText(selectedPregnancyExam.getExamValues());
		}
		examValues.setPreferredSize(new Dimension(370, 90));
		exampanel.add(l1);
		exampanel.add(examCode);
		exampanel.add(l2);
		exampanel.add(examDesc);
		exampanel.add(l3);
		exampanel.add(examDefault);
		exampanel.add(l4);
		exampanel.add(examValues);
		exampanel.setPreferredSize(new Dimension(430, 350));
		return exampanel;

	}


	private JPanel getExamTypePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancyexam.type")));
		String[] types = new String[] {
				MessageBundle.getMessage("angal.pregnancyexam.prenatal"),
				MessageBundle.getMessage("angal.pregnancyexam.postnatal"),
				MessageBundle
						.getMessage("angal.pregnancyexam.preandpostnatal") };
		examtype = new JComboBox(types);
		if (selectedPregnancyExam != null) {
			int type = selectedPregnancyExam.getExamType();
			if (type == -1)
				examtype.setSelectedIndex(0);
			else if (type == 0)
				examtype.setSelectedIndex(2);
			else if (type == 1)
				examtype.setSelectedIndex(1);
		} else
			examtype.setSelectedIndex(0);
		examtype.setPreferredSize(new Dimension(370, 30));
		panel.add(examtype);
		panel.setPreferredSize(new Dimension(430, 80));
		return panel;
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(getButtonSave());
		buttonPanel.add(getButtonClose());
		buttonPanel.setPreferredSize(new Dimension(430, 80));
		return buttonPanel;
	}

	private JButton getButtonSave() {
		JButton buttonSave = new JButton(
				MessageBundle.getMessage("angal.pregnancy.ok"));
		buttonSave.setMnemonic(KeyEvent.VK_T);
		buttonSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				int type = -1;
				if (examtype.getSelectedIndex() == 1)
					type = 1;
				else if (examtype.getSelectedIndex() == 2)
					type = 0;
				
				
					String code = examCode.getText();
					String descr = examDesc.getText();
					String defaultv = examDefault.getText();
					String values = examValues.getText();
					if(code == null || code.length() ==0){
						return;
					}
					if(descr == null || descr.length() ==0){
						return;
					}
					if(isInsert){// insert  exam
						selectedPregnancyExam = new PregnancyExam(code, descr, type, defaultv, values);
						selectedPregnancyExam.setExamValues(values);
						if (manager.existsPregnancyExam(selectedPregnancyExam)) {
							JOptionPane.showMessageDialog(null,MessageBundle
											.getMessage("angal.pregnancyexam.examexistsalready"),
									MessageBundle.getMessage("angal.hospital"),JOptionPane.PLAIN_MESSAGE);
							return;
						}
						manager.insertPregnancyExam(selectedPregnancyExam);
						firePregnancyExamInserted(selectedPregnancyExam);
						dispose();
					}
					else{// update  exam
						selectedPregnancyExam.setExamType(type);
						selectedPregnancyExam.setExamDesc(descr);
						selectedPregnancyExam.setExamDefault(defaultv);
						selectedPregnancyExam.setExamValues(values);
						manager.updatePregnancyExam(selectedPregnancyExam);
						firePregnancyExamUpdated(selectedPregnancyExam);
						dispose();
					}
				}
			
		});
		return buttonSave;
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


}
