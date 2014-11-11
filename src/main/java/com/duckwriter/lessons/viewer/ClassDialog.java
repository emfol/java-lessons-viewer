package com.duckwriter.lessons.viewer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Panel;
import java.awt.Button;
import java.awt.TextField;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ClassDialog extends Dialog
    implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_TITLE;
    private static final String ACTION_OK;
    private static final String ACTION_CANCEL;
    private static final String EMPTY_STRING;
    private static final Pattern NONBLANK_REGEX;

    static {
        DEFAULT_TITLE = "Please inform a class name...";
        ACTION_OK = "OK";
        ACTION_CANCEL = "CANCEL";
        EMPTY_STRING = "";
        NONBLANK_REGEX = Pattern.compile("\\S+");
    }

    private final AtomicBoolean initState;
    private final AtomicBoolean visibilityState;
    private final Set<Class<?>> expectedClasses;

    private Class<?> previousSelectedClass;
    private volatile Class<?> selectedClass;

    private TextField textField;

    ClassDialog(Frame frame, String title) {
        super(frame, title, true);
        this.initState = new AtomicBoolean(false);
        this.visibilityState = new AtomicBoolean(false);
        this.expectedClasses = new HashSet<Class<?>>();
    }

    ClassDialog(Frame frame) {
        this(frame, DEFAULT_TITLE);
    }

    private void init() {

        if (this.initState.compareAndSet(false, true)) {
            // text field
            TextField fdTx = new TextField(35);
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
            this.add(panel, BorderLayout.CENTER);
            this.pack();
            // update instance variables
            this.textField = fdTx;
        }

        // save selected class and reset it
        if (this.selectedClass != null) {
            this.previousSelectedClass = this.selectedClass;
            this.selectedClass = null;
        }

        // set text field initial value
        this.textField.setText(
            this.previousSelectedClass != null
                ? this.previousSelectedClass.getName()
                : EMPTY_STRING
        );

    }

    private void loadClassByName(String className) {

        Set<Class<?>> expClsSet;
        Class<?> selCls = null, ldCls = null;

        if (!className.isEmpty()) {
            try {
                ldCls = Class.forName(className);
            } catch (ClassNotFoundException e) {
                // TODO: Improve UI for this...
                System.out.format("Class \"%s\" could not be found...\n", className);
            }
        }

        if (ldCls != null) {
            expClsSet = this.expectedClasses;
            if (expClsSet.isEmpty()) {
                selCls = ldCls;
            } else {
                for (Class<?> expCls : expClsSet) {
                    if (expCls.isAssignableFrom(ldCls)) {
                        selCls = ldCls;
                        break;
                    }
                }
            }
        }

        this.selectedClass = selCls;

    }

    /*
     * Action Listener Methods
     */

    public void actionPerformed(ActionEvent e) {

        String text, action;
        Matcher matcher;

        action = e.getActionCommand();
        if (action.equals(ACTION_OK)) {
            text = this.textField.getText();
            matcher = NONBLANK_REGEX.matcher(text);
            text = matcher.matches() ? matcher.group() : EMPTY_STRING;
            this.loadClassByName(text);
            this.setVisible(false);
        } else if (action.equals(ACTION_CANCEL)) {
            this.setVisible(false);
        }

    }

    /*
     * Public Methods
     */

    public void addExpectedClass(Class<?> expected) {
        if (!this.visibilityState.get()) {
            this.expectedClasses.add(expected);
        }
    }

    public Class<?> getSelectedClass() {
        return this.selectedClass;
    }

    @Override
    public void setVisible(boolean visibility) {
        boolean expected = !visibility;
        if (this.visibilityState.compareAndSet(expected, visibility)) {
            if (visibility) {
                this.init();
            }
            super.setVisible(visibility);
        }
    }

}
