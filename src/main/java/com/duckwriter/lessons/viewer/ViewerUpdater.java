package com.duckwriter.lessons.viewer;

import java.awt.EventQueue;
import java.awt.Shape;
import java.awt.Image;

final class ViewerUpdater extends Object implements Runnable {

    static final int MODE_NONE    = 0x00;
    static final int MODE_MESSAGE = 0x01;
    static final int MODE_IMAGE   = 0x02;
    static final int MODE_SHAPE   = 0x04;

    private final Viewer viewer;
    private final String message;
    private final Image image;
    private final Shape shape;
    private final int mode;

    /*
     * Constructors
     */

    ViewerUpdater(
        Viewer viewer,
        String message,
        Image image,
        Shape shape,
        int mode
    ) {
        super();
        this.viewer = viewer;
        this.message = message;
        this.image = image;
        this.shape = shape;
        this.mode = mode;
    }

    ViewerUpdater(final Viewer viewer) {
        this(viewer, null, null, null, MODE_NONE);
    }

    /*
     * Static Package Accessible Methods
     */

    static int getModeMask (final int mode, final String message) {
        return (mode | (message != null ? MODE_MESSAGE : MODE_NONE));
    }

    static void dispatch(final ViewerUpdater updater) {
        EventQueue.invokeLater(updater);
    }

    /*
     * Instance Methods
     */

    void updateStatusMessage(final String message) {
        dispatch(new ViewerUpdater(this.viewer, message, null, null, MODE_MESSAGE));
    }

    void updateImage(final Image image, final String message) {
        dispatch(new ViewerUpdater(
            this.viewer,
            message,
            image,
            null,
            getModeMask(MODE_IMAGE, message)
        ));
    }

    void updateShape(final Shape shape, final String message) {
        dispatch(new ViewerUpdater(
            this.viewer,
            message,
            null,
            shape,
            getModeMask(MODE_SHAPE, message)
        ));
    }

    /*
     * Public Methods
     */

    @Override
    public void run() {

        if (EventQueue.isDispatchThread()) {

            int mask = this.mode;

            // update status message
            if ((mask & MODE_MESSAGE) == MODE_MESSAGE) {
                if (this.message != null) {
                    this.viewer.setStatusMessage(this.message);
                }
                mask = mask & ~MODE_MESSAGE;
            }

            switch (mask) {
                case MODE_IMAGE:
                    this.viewer.setImage(this.image);
                    break;
                case MODE_SHAPE:
                    this.viewer.setShape(this.shape);
                    break;
            }

        }

    }

}
