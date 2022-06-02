package org.isf.utils.listeners;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Created by nicosalvato on 2016-09-14.
 * Contact: nicosalvato@gmail.com
 */
public class SelectAllOnFocus extends FocusAdapter {
    public void focusGained(FocusEvent evt) {
        SwingUtilities.invokeLater(new SelectRunnable(evt));
    }

    class SelectRunnable implements Runnable {

        private FocusEvent event;
        SelectRunnable(FocusEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            ((JTextField) event.getSource()).selectAll();
        }
    }
}
