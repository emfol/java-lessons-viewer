package com.duckwriter.lessons.viewer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.Shape;
import java.awt.image.ImageProducer;

final class ClassImageLoader extends Object
    implements Runnable {

    private static final String REQUEST_MESSAGE = "Please inform a class name...";
    private static final String LOADING_MESSAGE = "Loading...";
    private static final String ERROR_MESSAGE = "!!! Error loading informed class name...";
    private static final String UNSUPPORTED_ERROR = "!!! Class not supported...";
    private static final String EMPTY_MESSAGE = "";

    private final AtomicBoolean isRunning;
    private final Viewer viewer;
    private ClassDialog classDialog;

    ClassImageLoader(Viewer viewer) {
        this.isRunning = new AtomicBoolean(false);
        this.viewer = viewer;
    }

    private Object createObject(Class<?> cls) {
        Object obj = null;
        try {
            obj = cls.newInstance();
        } catch (ReflectiveOperationException e) {
            System.err.println("Error while creating class instance...");
        }
        return obj;
    }

    private Class<?> getClassFromDialog() {

        ClassDialog dialog = this.classDialog;

        if (dialog == null) {
            dialog = new ClassDialog(this.viewer.getFrame());
            this.classDialog = dialog;
        }

        // displaying a file dialog blocks the calling thread
        dialog.setVisible(true);

        return dialog.getSelectedClass();

    }

    @Override
    public void run() {

        if (this.isRunning.compareAndSet(false, true)) {

            final ViewerUpdater updater = new ViewerUpdater(this.viewer);
            Object obj;
            Class<?> cls;

            updater.updateStatusMessage(REQUEST_MESSAGE);
            cls = this.getClassFromDialog();

            if (cls == null) {
                updater.updateStatusMessage(EMPTY_MESSAGE);
            } else {
                updater.updateStatusMessage(LOADING_MESSAGE);
                obj = this.createObject(cls);
                if (obj == null) {
                    updater.updateStatusMessage(ERROR_MESSAGE);
                } else if (obj instanceof Shape) {
                    updater.updateShape((Shape)obj, EMPTY_MESSAGE);
                } else if (obj instanceof ImageProducer) {
                    updater.updateImageProducer((ImageProducer)obj, EMPTY_MESSAGE);
                } else {
                    updater.updateStatusMessage(UNSUPPORTED_ERROR);
                }
            }

            this.isRunning.set(false);

        }

    }

}
