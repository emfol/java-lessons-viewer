package com.duckwriter.lessons.viewer;

import java.io.File;
import java.awt.Window;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuBar;
import java.awt.FileDialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public final class Viewer extends WindowAdapter
    implements Runnable, ActionListener {

    /*
     * Constants
     */

    private static final String TITLE = "Viewer - Shapes and Images";

    /*
     * Private Fields
     */

    private Frame viewerFrame;
    private File workingDirectory;
    private File selectedFile;

    /*
     * Constructors
     */

    private Viewer() {
        super();
    }

    /*
     * Private Methods
     */

    private void exit() {
        Frame frame = this.viewerFrame;
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
            this.viewerFrame = null;
        }
    }

    private void showFileDialog() {
        FileDialog dialog;
        File file, directory;
        String filePath, dirPath;
        Frame frame = this.viewerFrame;
        if (frame != null) {
            dialog = new FileDialog(
                frame,
                "Please select an image file...",
                FileDialog.LOAD
            );
            directory = this.workingDirectory;
            if (directory != null) {
                dialog.setDirectory(directory.getPath());
            }
            // displaying a file dialog blocks the calling thread
            dialog.setVisible(true);
            dirPath = dialog.getDirectory();
            filePath = dialog.getFile();
            if (filePath != null && dirPath != null) {
                directory = new File(dirPath);
                file = new File(directory, filePath);
                if (file.exists() && file.isFile()) {
                    this.selectedFile = file;
                    this.workingDirectory = directory;
                    this.refresh();
                }
            }
        }
    }

    private void showClassDialog() {
        System.out.println("A class dialog should appear...");
    }

    private void refresh() {
        // @todo: load file or class
        System.out.format("Currently Selected File: %s\n", this.selectedFile.getPath());
    }

    private MenuBar getViewerMenuBar() {

        MenuBar mbMain;
        MenuItem miOpen, miLoad, miExit;
        Menu mFile;

        // build menu items
        miOpen = new MenuItem();
        miOpen.setLabel("Open Image...");
        miOpen.setActionCommand("open-image-file");
        miOpen.addActionListener(this);
        miLoad = new MenuItem();
        miLoad.setLabel("Load Class...");
        miLoad.setActionCommand("load-class");
        miLoad.addActionListener(this);
        miExit = new MenuItem();
        miExit.setLabel("Exit");
        miExit.setActionCommand("exit");
        miExit.addActionListener(this);

        // build menu
        mFile = new Menu();
        mFile.setLabel("File");
        mFile.add(miOpen);
        mFile.add(miLoad);
        mFile.add(miExit);

        // build menu bar
        mbMain = new MenuBar();
        mbMain.add(mFile);

        return mbMain;

    }

    private Frame getViewerFrame() {

        Frame frame = this.viewerFrame;
        if (frame == null) {
            frame = new Frame(TITLE);
            frame.addWindowListener(this);
            frame.setMenuBar(this.getViewerMenuBar());
            this.viewerFrame = frame;
        }
        return frame;

    }

    /*
     * Runnable Interface
     */

    public void run() {
        Frame frame = this.getViewerFrame();
        frame.setBounds(20, 20, 480, 320);
        frame.setVisible(true);
    }

    /*
     * Action Listener Interface
     */

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("open-image-file")) {
            this.showFileDialog();
        } else if (action.equals("load-class")) {
            this.showClassDialog();
        } else if (action.equals("exit")) {
            this.exit();
        }
    }

    /*
     * Window Adapter Methods
     */

    @Override
    public void windowClosing(WindowEvent e) {
        this.exit();
    }

    /*
     * Public Static Methods
     */

    public static void main(String[] args) {
        Viewer viewer = new Viewer();
        EventQueue.invokeLater(viewer);
    }

}
