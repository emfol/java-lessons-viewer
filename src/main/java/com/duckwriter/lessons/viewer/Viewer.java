package com.duckwriter.lessons.viewer;

import java.awt.Window;
import java.awt.Frame;
import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public final class Viewer extends WindowAdapter
    implements Runnable {

    /*
     * Constants
     */

    private static final String TITLE = "Viewer - Shapes and Images";

    /*
     * Constructors
     */

    private Viewer() {
        super();
    }

    /*
     * Runnable Interface
     */

    public void run() {
        Frame frame = new Frame(TITLE);
        frame.addWindowListener(this);
        frame.setBounds(10, 10, 480, 320);
        frame.setVisible(true);
    }

    /*
     * Window Adapter Methods
     */

    @Override
    public void windowClosing(WindowEvent e) {
        Window w = e.getWindow();
        w.setVisible(false);
        w.dispose();
    }

    /*
     * Public Static Methods
     */

    public static void main(String[] args) {
        Viewer viewer = new Viewer();
        EventQueue.invokeLater(viewer);
    }

}
