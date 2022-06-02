package org.isf.utils.jobjects;

import org.isf.utils.listeners.SelectAllOnFocus;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by nicosalvato on 2017-02-04.
 * Contact: nicosalvato@gmail.com
 */
public class FormattedTextField {

    public static JFormattedTextField getDecimalField(int columns) {
        return getDecimalField(columns, null, false);
    }

    public static JFormattedTextField getDecimalField(int columns, boolean selectAllOnFocus) {
        return getDecimalField(columns, null, selectAllOnFocus);
    }

    public static JFormattedTextField getDecimalField(int columns, String name, boolean selectAllOnFocus) {
        return getDecimalField(columns, name, selectAllOnFocus, null);
    }

    public static JFormattedTextField getDecimalField(int columns, boolean selectAllOnFocus, BigDecimal value) {
        return getDecimalField(columns, null, selectAllOnFocus, value);
    }

    public static JFormattedTextField getDecimalField(int columns, String name, boolean selectAllOnFocus, BigDecimal value) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(1);
        JFormattedTextField field = new JFormattedTextField(format);
        field.setColumns(columns);

        if (name != null)
            field.setName(name);

        if (selectAllOnFocus)
            field.addFocusListener(new SelectAllOnFocus());

        field.setValue(0);
        if (value != null)
            field.setValue(value);

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                JFormattedTextField source = (JFormattedTextField) e.getSource();
                if (source.getText() == null || source.getText().trim().isEmpty()) {
                    source.setValue(0.0);
                }
            }
        });

        return field;
    }

    public static Double parseFieldValueToDouble(JFormattedTextField field) {
        if (field.getValue() instanceof Long)
            return ((Long) field.getValue()).doubleValue();
        if (field.getValue() instanceof Integer)
            return ((Integer) field.getValue()).doubleValue();
        if (field.getValue() instanceof BigDecimal)
            return ((BigDecimal) field.getValue()).doubleValue();

        return (Double) field.getValue();
    }

    public static BigDecimal parseFieldValueToBigDecimal(JFormattedTextField field) {
        return new BigDecimal(parseFieldValueToDouble(field)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static JFormattedTextField getIntegerField(int columns, boolean selectAllOnFocus, Integer value) {
        JFormattedTextField field = new JFormattedTextField(NumberFormat.getIntegerInstance());
        field.setColumns(columns);

        if (selectAllOnFocus)
            field.addFocusListener(new SelectAllOnFocus());

        field.setValue(0);
        if (value != null)
            field.setValue(value);

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                JFormattedTextField source = (JFormattedTextField) e.getSource();
                if (source.getText() == null || source.getText().trim().isEmpty()) {
                    source.setValue(0);
                }
            }
        });

        return field;
    }
    
    public static JFormattedTextField getPositiveIntegerField(int columns, boolean selectAllOnFocus, Integer value) {
        return getPositiveIntegerFieldMax(columns,selectAllOnFocus,value,null);
    }
    
    public static JFormattedTextField getPositiveIntegerFieldMax(int columns, boolean selectAllOnFocus, Integer value, final Integer maxValue) {
        JFormattedTextField field = new JFormattedTextField(NumberFormat.getIntegerInstance());
        field.setColumns(columns);

        if (selectAllOnFocus)
            field.addFocusListener(new SelectAllOnFocus());

        field.setValue(0);
        if (value != null)
            field.setValue(value);

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                JFormattedTextField source = (JFormattedTextField) e.getSource();
                if (source.getText() == null || source.getText().trim().isEmpty()
                		|| Integer.parseInt(source.getText()) < 0 ) {
                    source.setValue(0);
                }
                if (maxValue != null && Integer.parseInt(source.getText()) > maxValue.intValue()) {
                	source.setValue(maxValue.intValue());
                }
            }
        });

        return field;
    }

    public static Integer parseFieldValueToInteger(JFormattedTextField field) {
        if (field.getValue() instanceof Long)
            return ((Long) field.getValue()).intValue();
        return (Integer) field.getValue();
    }

    public static JCheckBox getBooleanField(boolean checked) {
        JCheckBox field = new JCheckBox();
        field.setSelected(checked);
        return field;
    }
}
