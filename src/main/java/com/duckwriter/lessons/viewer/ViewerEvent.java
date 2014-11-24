package com.duckwriter.lessons.viewer;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.image.ImageProducer;

final class ViewerEvent extends Object implements Runnable {

    private final Viewer viewer;
    private final String statusMessage;
    private final Image image;
    private final ImageProducer imageProducer;

    ViewerEvent(Viewer viewer, String statusMessage, Image image, ImageProducer imageProducer) {
        super();
        this.viewer = viewer;
        this.statusMessage = statusMessage;
        this.image = image;
        this.imageProducer = imageProducer;
    }

    /*
     * Static Package Accessible Methods
     */

    static void dispatch(Viewer viewer, String statusMessage) {
        final ViewerEvent e = new ViewerEvent(viewer, statusMessage, null, null);
        EventQueue.invokeLater(e);
    }

    static void dispatch(Viewer viewer, String statusMessage, Image image) {
        final ViewerEvent e = new ViewerEvent(viewer, statusMessage, image, null);
        EventQueue.invokeLater(e);
    }

    static void ViewerEvent(Viewer viewer, String statusMessage, ImageProducer imageProducer) {
        final ViewerEvent e = new ViewerEvent(viewer, statusMessage, null, imageProducer);
        EventQueue.invokeLater(e);
    }

    /*
     * Public Methods
     */

    @Override
    public void run() {

        if (EventQueue.isDispatchThread()) {

            // update status message
            if (this.statusMessage != null) {
                this.viewer.setStatusMessage(this.statusMessage);
            }

            // update image
            if (this.image != null) {
                this.viewer.setImage(this.image);
            } else if (this.imageProducer != null) {
                this.viewer.setImage(this.imageProducer);
            }

        }

    }

}
