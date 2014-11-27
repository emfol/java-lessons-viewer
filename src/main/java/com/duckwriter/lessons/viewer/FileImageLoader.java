package com.duckwriter.lessons.viewer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.Image;
import java.awt.FileDialog;
import java.awt.Toolkit;

final class FileImageLoader extends Object
    implements Runnable, FilenameFilter {

    private static final String REQUEST_MESSAGE;
    private static final String LOADING_MESSAGE;
    private static final String ERROR_MESSAGE;
    private static final String EMPTY_MESSAGE;
    private static final Pattern FILENAME_REGEX;

    static {
        REQUEST_MESSAGE = "Please select an image file...";
        LOADING_MESSAGE = "Loading...";
        ERROR_MESSAGE = "!!! Error loading selected file...";
        EMPTY_MESSAGE = "";
        FILENAME_REGEX = Pattern.compile(
            ".+\\.(?:jpe?g|png|gif)$",
            Pattern.CASE_INSENSITIVE
        );
    }

    private static Matcher getFilenameMatcher(final String filename) {
        final Pattern p = FILENAME_REGEX;
        return p.matcher(filename);
    }

    /*
     * Instance Variables
     */

    private final AtomicBoolean isRunning;
    private final Viewer viewer;
    private FileDialog fileDialog;

    /*
     * Constructors
     */

    FileImageLoader(Viewer viewer) {
        super();
        this.isRunning = new AtomicBoolean(false);
        this.viewer = viewer;
    }

    /*
     * Private Methods
     */

    private Image load(File file) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return toolkit.createImage(file.getAbsolutePath());
    }

    private File getFile() {

        FileDialog dialog = this.fileDialog;
        File fileEnt, dirEnt, selEnt = null;
        String filePath, dirPath;

        if (dialog == null) {
            dialog = new FileDialog(
                this.viewer.getFrame(),
                REQUEST_MESSAGE,
                FileDialog.LOAD
            );
            dialog.setFilenameFilter(this);
            this.fileDialog = dialog;
        }

        // displaying a file dialog blocks the calling thread
        dialog.setVisible(true);

        if ((dirPath = dialog.getDirectory()) != null
            && (filePath = dialog.getFile()) != null) {
            dirEnt = new File(dirPath);
            fileEnt = new File(dirEnt, filePath);
            if (fileEnt.exists() && fileEnt.isFile()) {
                selEnt = fileEnt;
            }
        }

        return selEnt;

    }

    /*
     * Public Methods
     */

    @Override
    public boolean accept(File dir, String name) {
        return (getFilenameMatcher(name)).matches();
    }

    @Override
    public void run() {

        if (this.isRunning.compareAndSet(false, true)) {

            final ViewerUpdater updater = new ViewerUpdater(this.viewer);
            File file;
            Image image;

            updater.updateStatusMessage(REQUEST_MESSAGE);
            file = this.getFile();

            if (file == null) {
                updater.updateStatusMessage(EMPTY_MESSAGE);
            } else {
                updater.updateStatusMessage(LOADING_MESSAGE);
                image = this.load(file);
                updater.updateImage(
                    image,
                    image != null ? EMPTY_MESSAGE: ERROR_MESSAGE
                );
            }

            this.isRunning.set(false);

        }

    }

}
