package org.isf.utils.tasks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by nicosalvato on 2016-10-11.
 * Contact: nicosalvato@gmail.com
 */
public class WaitingDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public WaitingDialog(Window win, String title, ModalityType modalityType) {
        super(win, title, modalityType);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
        });
    }
}
