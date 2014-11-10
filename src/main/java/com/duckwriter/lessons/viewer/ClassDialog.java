package com.duckwriter.lessons.viewer;

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

    private static final String DEFAULT_TITLE;
    private static final Pattern NONBLANK_REGEX;

    static {
        DEFAULT_TITLE = "Please inform a class name...";
        NONBLANK_REGEX = Pattern.compile(
            "\\S+",
            Pattern.UNICODE_CHARACTER_CLASS
        );
    }

    private boolean isNotInitialized;
    private TextField textField;
    private String selectedClass;

    ClassDialog(Frame frame, String title) {
        super(frame, title, true);
        this.isNotInitialized = true;
    }

    ClassDialog(Frame frame) {
        this(frame, DEFAULT_TITLE);
    }

    private void init() {

        TextField fdTx;
        Button btOk, btCn;
        Panel panel;

        fdTx = new TextField();
        fdTx.setColumns(45);

        btOk = new Button();
        btOk.setLabel("OK");
        btOk.setActionCommand("ok");
        btOk.addActionListener(this);

        btCn = new Button();
        btCn.setLabel("Cancel");
        btCn.setActionCommand("cancel");
        btCn.addActionListener(this);

        panel = new Panel();
        panel.add(fdTx);
        panel.add(btOk);
        panel.add(btCn);

        // prepare for exibition
        this.add(panel, BorderLayout.CENTER);
        this.pack();

        // update instance variables
        this.textField = fdTx;
        this.isNotInitialized = false;

    }

    /*
     * Action Listener Methods
     */

    public void actionPerformed(ActionEvent e) {

        String text, action;
        Matcher matcher;

        action = e.getActionCommand();
        if (action.equals("ok")) {
            text = this.textField.getText();
            if (text != null) {
                if (text.isEmpty()) {
                    text = null;
                } else {
                    matcher = NONBLANK_REGEX.matcher(text);
                    text = matcher.matches() ? matcher.group() : null;
                }
            }
            this.selectedClass = text;
            this.setVisible(false);
        } else if (action.equals("cancel")) {
            this.setVisible(false);
        }

    }

    /*
     * Public Methods
     */

    public String getSelectedClass() {
        return this.selectedClass;
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.isNotInitialized) {
            this.init();
        }
        super.setVisible(visible);
    }

}
