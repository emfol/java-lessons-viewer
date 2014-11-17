package com.duckwriter.lessons.viewer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.awt.Shape;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuBar;
import java.awt.FileDialog;
import java.awt.EventQueue;
import java.awt.image.ImageProducer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public final class Viewer extends WindowAdapter
    implements Runnable, ActionListener {

    /*
     * Constants
     */

    private static final String CACHE_KEY_FILEDIALOG = "fileDialog";
    private static final String CACHE_KEY_CLASSDIALOG = "classDialog";
    private static final String CACHE_KEY_DIRECTORY = "directory";
    private static final String TITLE = "Viewer - Images and Shapes";

    /*
     * Private Fields
     */

    private final AtomicBoolean initState;
    private final Frame frame;
    private final Map<String, Object> cache;
    private File selectedFile;
    private Class<?> selectedClass;

    /*
     * Constructors
     */

    private Viewer() {
        super();
        this.initState = new AtomicBoolean(false);
        this.frame = new Frame();
        this.cache = new HashMap<String, Object>();
    }

    /*
     * Private Methods
     */

    private void exit() {
        this.frame.setVisible(false);
        this.frame.dispose();
        this.cache.clear();
    }

    private void showFileDialog() {

        FileDialog fileDialog;
        File file, directory;
        String filePath, dirPath;

        fileDialog = (FileDialog)this.cache.get(CACHE_KEY_FILEDIALOG);
        if (fileDialog == null) {
            fileDialog = new FileDialog(
                this.frame,
                "Please select an image file...",
                FileDialog.LOAD
            );
            this.cache.put(CACHE_KEY_FILEDIALOG, fileDialog);
        }

        // check for previous directory
        directory = (File)this.cache.get(CACHE_KEY_DIRECTORY);
        if (directory != null) {
            fileDialog.setDirectory(directory.getPath());
        }

        // displaying a file dialog blocks the calling thread
        fileDialog.setVisible(true);
        dirPath = fileDialog.getDirectory();
        filePath = fileDialog.getFile();
        if (filePath != null && dirPath != null) {
            directory = new File(dirPath);
            file = new File(directory, filePath);
            if (file.exists() && file.isFile()) {
                this.cache.put(CACHE_KEY_DIRECTORY, directory);
                this.selectedFile = file;
                this.selectedClass = null;
                this.refresh();
            }
        }

    }

    private void showClassDialog() {

        ClassDialog classDialog;
        Class<?> selCls;

        classDialog = (ClassDialog)this.cache.get(CACHE_KEY_CLASSDIALOG);
        if (classDialog == null) {
            classDialog = new ClassDialog(this.frame);
            classDialog.addExpectedClass(Shape.class);
            classDialog.addExpectedClass(ImageProducer.class);
            this.cache.put(CACHE_KEY_CLASSDIALOG, classDialog);
        }
        classDialog.setVisible(true);
        selCls = classDialog.getSelectedClass();
        if (selCls != null) {
            this.selectedClass = selCls;
            this.selectedFile = null;
            this.refresh();
        }

    }

    private void refresh() {
        // TODO: load file or class
        System.out.format(
            "Currently Selected File: %s\n",
            this.selectedFile != null
                ? this.selectedFile.getPath()
                : "None..."
        );
        System.out.format(
            "Currently Selected Class: %s\n",
            this.selectedClass != null
                ? this.selectedClass
                : "None..."
        );
    }

    private MenuBar buildMenuBar() {

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

    private void init() {
        if (this.initState.compareAndSet(false, true)) {
            this.frame.setTitle(TITLE);
            this.frame.addWindowListener(this);
            this.frame.setMenuBar(this.buildMenuBar());
            this.frame.setBounds(20, 20, 480, 320);
            this.frame.setVisible(true);
        }
    }

    /*
     * Runnable Interface
     */

    @Override
    public void run() {
        if (EventQueue.isDispatchThread()) {
            this.init();
        }
    }

    /*
     * Action Listener Interface
     */

    @Override
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
