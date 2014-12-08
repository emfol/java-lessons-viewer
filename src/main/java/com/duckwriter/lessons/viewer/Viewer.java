package com.duckwriter.lessons.viewer;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Shape;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.CheckboxMenuItem;
import java.awt.MenuBar;
import java.awt.Label;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.duckwriter.util.dispatch.DispatchQueue;

public final class Viewer extends WindowAdapter
    implements Runnable, ActionListener, ItemListener {

    /*
     * Constants
     */

    private static final String TITLE = "Viewer - Images and Drawable Classes";
    private static final String CACHE_DISPATCHQUEUE = "dispatchQueue";
    private static final String CACHE_CLASSLOADER = "classLoader";
    private static final String CACHE_FILELOADER = "fileLoader";
    private static final String COMMAND_LOADFILE = "load-file";
    private static final String COMMAND_LOADCLASS = "load-class";
    private static final String COMMAND_EXIT = "exit";

    /*
     * Private Fields
     */

    private final AtomicBoolean isRunning;
    private final Map<String, Object> cache;
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
        this.cache = new HashMap<String, Object>();
        this.initialized = false;
    }

    /*
     * Private Methods
     */

    private void stop() {
        if (this.isRunning.compareAndSet(true, false)) {
            DispatchQueue queue = (DispatchQueue)this.cache.get(CACHE_DISPATCHQUEUE);
            if (queue != null) {
                // kill dispatch thread
                queue.stop();
            }
            this.frame.setVisible(false);
            this.cache.clear();
            System.exit(0);
        }
    }

    private MenuBar createMenuBar() {

        MenuBar mbMain;
        MenuItem miOpen, miLoad, miExit;
        CheckboxMenuItem miChkAspect;
        Menu mFile, mView;

        // build menu items
        miOpen = new MenuItem("Load Image...");
        miOpen.setActionCommand(COMMAND_LOADFILE);
        miOpen.addActionListener(this);
        miLoad = new MenuItem("Load Class...");
        miLoad.setActionCommand(COMMAND_LOADCLASS);
        miLoad.addActionListener(this);
        miExit = new MenuItem("Exit");
        miExit.setActionCommand(COMMAND_EXIT);
        miExit.addActionListener(this);
        miChkAspect = new CheckboxMenuItem("Preserve Aspect Ratio", true);
        miChkAspect.addItemListener(this);

        // build file menu
        mFile = new Menu("File");
        mFile.add(miOpen);
        mFile.add(miLoad);
        mFile.add(miExit);

        // build view menu
        mView = new Menu("View");
        mView.add(miChkAspect);

        // build menu bar
        mbMain = new MenuBar();
        mbMain.add(mFile);
        mbMain.add(mView);

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

    private DispatchQueue getDispatchQueue() {
        DispatchQueue dispatchQueue = (DispatchQueue)this.cache.get(CACHE_DISPATCHQUEUE);
        if (dispatchQueue == null) {
            dispatchQueue = DispatchQueue.createDispatchQueue();
            this.cache.put(CACHE_DISPATCHQUEUE, dispatchQueue);
        }
        return dispatchQueue;
    }

    public void showFileDialog() {
        if (this.isRunning.get()) {
            FileImageLoader fileLoader = (FileImageLoader)this.cache.get(CACHE_FILELOADER);
            if (fileLoader == null) {
                fileLoader = new FileImageLoader(this);
                this.cache.put(CACHE_FILELOADER, fileLoader);
            }
            (this.getDispatchQueue()).dispatch(fileLoader);
        }
    }

    public void showClassDialog() {
        if (this.isRunning.get()) {
            ClassImageLoader classLoader = (ClassImageLoader)this.cache.get(CACHE_CLASSLOADER);
            if (classLoader == null) {
                classLoader = new ClassImageLoader(this);
                this.cache.put(CACHE_CLASSLOADER, classLoader);
            }
            (this.getDispatchQueue()).dispatch(classLoader);
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

    void setShape(final Shape shape) {
        if (this.isRunning.get()) {
            this.viewerComponent.setShape(shape);
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
        if (action.equals(COMMAND_LOADFILE)) {
            this.showFileDialog();
        } else if (action.equals(COMMAND_LOADCLASS)) {
            this.showClassDialog();
        } else if (action.equals(COMMAND_EXIT)) {
            this.stop();
        }
    }

    /* Item Listener Interface */

    @Override
    public void itemStateChanged(ItemEvent e) {
        final int state = e.getStateChange();
        this.viewerComponent.setPreserveAspect(state == ItemEvent.SELECTED);
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
