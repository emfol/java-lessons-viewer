package com.duckwriter.lessons.viewer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuBar;
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

    private final AtomicBoolean isRunning;
    private final Frame frame;
    private final ViewerComponent viewerComponent;
    private final Label statusComponent;
    private boolean initialized;


    /*
     * Constructors
     */

    private Viewer() {
        super();
        this.isRunning = new AtomicBoolean(false);
        this.frame = new Frame();
        this.viewerComponent = new ViewerComponent();
        this.statusComponent = new Label();
        this.initialized = false;
    }

    /*
     * Private Methods
     */

    private void stop() {
        if (this.isRunning.compareAndSet(true, false)) {
            this.frame.setVisible(false);
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

    private MenuBar createMenuBar() {

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
        if (!this.initialized) {
            this.frame.setTitle(TITLE);
            this.frame.addWindowListener(this);
            this.frame.setMenuBar(this.createMenuBar());
            this.frame.add(this.viewerComponent, BorderLayout.CENTER);
            this.frame.add(this.statusComponent, BorderLayout.SOUTH);
            this.frame.setBounds(20, 20, 480, 320);
            this.initialized = true;
        }
    }

    /*
     * Package Accessible Methods
     */

    Frame getFrame() {
        return this.frame;
    }

    void setStatusMessage(final String statusMessage) {
        if (this.isRunning.get()) {
            this.statusComponent.setText(statusMessage);
        }
    }

    void setImage(final Image image) {
        if (this.isRunning.get()) {
            this.viewerComponent.setImage(image);
        }
    }

    void setImage(final ImageProducer image) {
        if (this.isRunning.get()) {
            this.viewerComponent.setImage(image);
        }
    }

    /*
     * Public Methods
     */

    /* Runnable Interface */

    @Override
    public void run() {
        if (this.isRunning.compareAndSet(false, true)) {
            this.init();
            this.frame.setVisible(true);
        }
    }

    /* Action Listener Interface */

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("open-image-file")) {
            this.showFileDialog();
        } else if (action.equals("load-class")) {
            this.showClassDialog();
        } else if (action.equals("exit")) {
            this.stop();
        }
    }

    /* Window Adapter Methods */

    @Override
    public void windowClosing(WindowEvent e) {
        this.stop();
    }

    /* Entry Point */

    public static void main(String[] args) {
        Viewer viewer = new Viewer();
        EventQueue.invokeLater(viewer);
    }

}
