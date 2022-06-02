package org.isf.utils.jobjects;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

/**
 * Created by nicosalvato on 2017-02-06.
 * Contact: nicosalvato@gmail.com
 */
public class BorderedPanel {

    // Set a specific border+title to a panel
    public static JPanel setMyBorder(JPanel c, String title) {
        javax.swing.border.Border b2 = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title), BorderFactory
                        .createEmptyBorder(0, 0, 0, 0));
        c.setBorder(b2);
        return c;
    }
    
    // Set a specific border+title+align to a panel
    public static JPanel setMyBorderAlign(JPanel c, String title, int alignment) {
        c.setBorder(BorderFactory.createTitledBorder(c.getBorder(), title, alignment, TitledBorder.TOP));
        return c;
    }
    
    // Set a specific border+title+tickness to a panel
    public static JPanel setMyBorderTick(JPanel c, String title, int tickness) {
        javax.swing.border.Border b2 = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title), BorderFactory
                        .createEmptyBorder(tickness, tickness, tickness, tickness));
        c.setBorder(b2);
        return c;
    }
    
    // Set a specific border+title+matte to a panel
    public static JPanel setMyMatteBorder(JPanel c, String title) {
        c.setBorder(new TitledBorder(new MatteBorder(1, 20, 1, 1, new Color(153, 180, 209)), title,
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        return c;
    }
    
    // Set a specific color to a border
    public static void setMyBorderColor(JPanel c, Color color) {
    	if (c != null) {
    		Border border = c.getBorder();
    		if (border instanceof TitledBorder) ((TitledBorder) border).setTitleColor(color);
    	}
    }
}
