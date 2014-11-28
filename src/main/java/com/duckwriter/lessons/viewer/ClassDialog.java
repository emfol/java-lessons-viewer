package com.duckwriter.lessons.viewer;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Panel;
import java.awt.Button;
import java.awt.Label;
import java.awt.TextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ClassDialog extends Dialog
    implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final int INPUT_SIZE = 35;
    private static final String DEFAULT_TITLE;
    private static final String ACTION_OK;
    private static final String ACTION_CANCEL;
    private static final String EMPTY_STRING;
    private static final Pattern NONBLANK_REGEX;

    static {
        DEFAULT_TITLE = "Please enter a class name...";
        ACTION_OK = "OK";
        ACTION_CANCEL = "CANCEL";
        EMPTY_STRING = "";
        NONBLANK_REGEX = Pattern.compile("\\s*(\\S+)\\s*");
    }

    private final AtomicReference<Class<?>> selectedClassRef;
    private final AtomicBoolean isBusy;
    private boolean isNotInitialized;

    private String selectedClassName;
    private TextField textField;

    ClassDialog(Frame frame, String title) {
        super(frame, title, true);
        this.selectedClassRef = new AtomicReference<Class<?>>();
        this.isBusy = new AtomicBoolean(false);
        this.isNotInitialized = true;
    }

    ClassDialog(Frame frame) {
        this(frame, DEFAULT_TITLE);
    }

    private void init() {

        if (isNotInitialized) {
            // text field
            TextField fdTx = new TextField(INPUT_SIZE);
            // button OK
            Button btOk = new Button("OK");
            btOk.setActionCommand(ACTION_OK);
            btOk.addActionListener(this);
            // button CANCEL
            Button btCn = new Button("Cancel");
            btCn.setActionCommand(ACTION_CANCEL);
            btCn.addActionListener(this);
            // panel
            Panel panel = new Panel();
            panel.add(fdTx);
            panel.add(btOk);
            panel.add(btCn);
            // prepare for exhibition
            this.add(new Label(DEFAULT_TITLE), BorderLayout.NORTH);
            this.add(panel, BorderLayout.CENTER);
            this.pack();
            // update instance variables
            this.textField = fdTx;
            this.isNotInitialized = false;
        }

        // reset internal logic
        this.selectedClassName = null;
        this.selectedClassRef.set(null);

    }

    private void loadSelectedClass() {

        final Pattern pattern = NONBLANK_REGEX;
        String className = this.selectedClassName;

        if (className != null) {
            Matcher matcher = pattern.matcher(className);
            className = matcher.matches() ? matcher.group(1) : EMPTY_STRING;
            if (!className.isEmpty()) {
                Class<?> ldCls = null;
                try {
                    ldCls = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    // TODO: Improve UI for this...
                    System.err.printf("Class \"%s\" could not be found...\n", className);
                }
                this.selectedClassRef.set(ldCls);
            }
        }

    }

    /*
     * Action Listener Methods
     */

    @Override
    public void actionPerformed(ActionEvent e) {

        String action = e.getActionCommand();
        if (action.equals(ACTION_OK)) {
            this.selectedClassName = this.textField.getText();
            this.setVisible(false);
        } else if (action.equals(ACTION_CANCEL)) {
            this.setVisible(false);
        }

    }

    /*
     * Public Methods
     */

    public Class<?> getSelectedClass() {
        return this.selectedClassRef.get();
    }

    @Override
    public void setVisible(final boolean makeVisible) {

        if (makeVisible) {
            if (this.isBusy.compareAndSet(false, true)) {
                this.init();
                super.setVisible(true); // this call blocks...
                this.loadSelectedClass();
                this.isBusy.set(false);
            }
        } else {
            if (this.isBusy.get()) {
                super.setVisible(false);
            }
        }

    }

}
