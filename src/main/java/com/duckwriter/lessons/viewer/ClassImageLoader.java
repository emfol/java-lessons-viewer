package com.duckwriter.lessons.viewer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.Shape;
import java.awt.Image;
import java.awt.ImageProducer;

final class ClassImageLoader extends Object
    implements Runnable {

    private static final String REQUEST_MESSAGE = "Please inform a class name...";
    private static final String EMPTY_MESSAGE = "";

    private final AtomicBoolean isRunning;
    private final Viewer viewer;
    private ClassDialog classDialog;

    ClassImageLoader(Viewer viewer) {
        this.isRunning = new AtomicBoolean(false);
        this.viewer = viewer;
    }

    private Object createInstance(Class<?> cls) {

    }

    private Class<?> loadClass() {

        ClassDialog dialog = this.classDialog;

        if (dialog == null) {
            dialog = new ClassDialog(this.frame);
            dialog.addExpectedClass(Shape.class);
            dialog.addExpectedClass(ImageProducer.class);
            this.classDialog = dialog;
        }

        // displaying a file dialog blocks the calling thread
        dialog.setVisible(true);

        return dialog.getSelectedClass();

    }

    @Override
    public void run() {

        Object inst;
        Class<?> cls;

        if (this.isRunning.compareAndSet(false, true)) {
            ViewerEvent.dispatch(this.viewer, REQUEST_MESSAGE);
            cls = this.loadClass();
            if (cls != null) {
                inst = this.createInstance(cls);
                if (inst instanceof Image) {

                } else if (inst instanceof ImageProducer) {

                }
            }
            ViewerEvent.dispatch(this.viewer, EMPTY_MESSAGE);
            this.isRunning.set(false);
        }

    }

}
