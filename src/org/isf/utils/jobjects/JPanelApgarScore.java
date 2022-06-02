/**
 * 
 */
package org.isf.utils.jobjects;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Nanni
 *
 */
public class JPanelApgarScore extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int apgarScore1;
	private int apgarScore5;
	private int apgarScore10;
	private JFormattedTextField apgar1field;
	private JFormattedTextField apgar5field;
	private JFormattedTextField apgar10field;
	DecimalFormat twoDigitFormat = new DecimalFormat("00");
	
	/**
	 * converts a string of 6 digits into 3 different apgar scores
	 * @param apgarScoreStr
	 */
	public JPanelApgarScore(String apgarScoreStr) {
		setApgar(apgarScoreStr);
		initcomponents();
	}

	private void setApgar(String apgarScoreStr) {
		if (apgarScoreStr.length() == 6) {
			this.apgarScore1 = Integer.valueOf(apgarScoreStr.substring(0,2));
			this.apgarScore5 = Integer.valueOf(apgarScoreStr.substring(2,4));
			this.apgarScore10 = Integer.valueOf(apgarScoreStr.substring(4,6));
		}
	}
	
	public JPanelApgarScore(int apgarScore1, int apgarScore5, int apgarScore10) {
		super();
		this.apgarScore1 = apgarScore1;
		this.apgarScore5 = apgarScore5;
		this.apgarScore10 = apgarScore10;
		initcomponents();
	}
	
	public JPanelApgarScore() {
		super();
		initcomponents();
	}

	private void initcomponents() {
		apgar1field = FormattedTextField.getPositiveIntegerFieldMax(2, true, new Integer(apgarScore1), 10);
		apgar5field = FormattedTextField.getPositiveIntegerFieldMax(2, true, new Integer(apgarScore5), 10);
		apgar10field = FormattedTextField.getPositiveIntegerFieldMax(2, true, new Integer(apgarScore10), 10);
		
		apgar1field.setHorizontalAlignment(JTextField.CENTER);
		apgar5field.setHorizontalAlignment(JTextField.CENTER);
		apgar10field.setHorizontalAlignment(JTextField.CENTER);
		
		add(apgar1field);
		add(apgar5field);
		add(apgar10field);
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the apgarScoreStr
	 */
	public String getApgarScoreStr() {
		StringBuilder apgarScoreStr = new StringBuilder();
		apgarScoreStr.append(twoDigitFormat.format(getApgarScore1()));
		apgarScoreStr.append(twoDigitFormat.format(getApgarScore5()));
		apgarScoreStr.append(twoDigitFormat.format(getApgarScore10()));
		return apgarScoreStr.toString();
	}

	/**
	 * @return the apgarScore1
	 */
	public int getApgarScore1() {
		apgarScore1 = FormattedTextField.parseFieldValueToInteger(apgar1field).intValue();
		return apgarScore1;
	}

	/**
	 * @return the apgarScore5
	 */
	public int getApgarScore5() {
		apgarScore5 = FormattedTextField.parseFieldValueToInteger(apgar5field).intValue();
		return apgarScore5;
	}

	/**
	 * @return the apgarScore10
	 */
	public int getApgarScore10() {
		apgarScore10 = FormattedTextField.parseFieldValueToInteger(apgar10field).intValue();
		return apgarScore10;
	}

	/**
	 * @param apgarScore1 the apgarScore1 to set
	 */
	public void setApgarScore1(int apgarScore1) {
		this.apgarScore1 = apgarScore1;
	}

	/**
	 * @param apgarScore5 the apgarScore5 to set
	 */
	public void setApgarScore5(int apgarScore5) {
		this.apgarScore5 = apgarScore5;
	}

	/**
	 * @param apgarScore10 the apgarScore10 to set
	 */
	public void setApgarScore10(int apgarScore10) {
		this.apgarScore10 = apgarScore10;
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		final JPanelApgarScore apgarPanel = new JPanelApgarScore(1,5,10);
		frame.add(apgarPanel, BorderLayout.NORTH);
		
		JButton button = new JButton("Score!");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(apgarPanel.getApgarScoreStr());
				
			}
		});
		
		frame.add(button, BorderLayout.CENTER);
		
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		
	}
}
