package com.duckwriter.lessons.viewer;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class ViewerComponent extends Canvas {

    /* Class Members */

    private static final long serialVersionUID = 1L;

    private static final int MIN_SIZE = 1;
    private static final Color FG_COLOR = new Color(0x004285f4);

    /* Instance Members */

    private volatile ImageReference imageRef;
    private volatile Shape shapeRef;
    private volatile boolean preserveAspect;
    private volatile boolean needsRefresh;
    private final Dimension viewBounds;
    private final Rectangle imageRect;

    /*
     * Constructors
     */

    public ViewerComponent() {
        super();
        this.imageRef = null;
        this.shapeRef = null;
        this.preserveAspect = true;
        this.needsRefresh = true;
        this.viewBounds = new Dimension();
        this.imageRect = new Rectangle();
    }

    /*
     * Private Methods
     */

    private void adjustImageRect() {

        float k, vw, vh, iw, ih;

        vw = (float)this.viewBounds.width;
        vh = (float)this.viewBounds.height;
        iw = (float)this.imageRect.width;
        ih = (float)this.imageRect.height;

        // calculate k (scale factor)
        k = (ih / iw) < (vh / vw)
            ? vw / iw
            : vh / ih;

        // scale image
        if (k < 1.0f) {
            this.imageRect.width = (int)(k * iw);
            this.imageRect.height = (int)(k * ih);
        }

        // update rect offset
        this.imageRect.x = (this.viewBounds.width - this.imageRect.width) / 2;
        this.imageRect.y = (this.viewBounds.height - this.imageRect.height) / 2;

    }

    private void drawImage(final Graphics2D g, final Image image) {

        int origWidth, origHeight;

        if (this.preserveAspect) {

            if (this.imageRect.width < 0 || this.imageRect.height < 0) {
                if ((origWidth = image.getWidth(this)) > 0
                    && (origHeight = image.getHeight(this)) > 0) {
                    this.imageRect.setSize(origWidth, origHeight);
                    this.adjustImageRect();
                }
            }

            if (this.imageRect.width > 0 && this.imageRect.height > 0) {
                g.drawImage(image,
                    this.imageRect.x,
                    this.imageRect.y,
                    this.imageRect.width,
                    this.imageRect.height,
                    this
                );
            }

        } else {

            g.drawImage(
                image,
                0, 0,
                this.viewBounds.width,
                this.viewBounds.height,
                this
            );

        }

    }

    private void drawShapeStretched(final Graphics2D g, final Shape oSh) {

        Shape tSh;
        Rectangle2D shapeBounds = oSh.getBounds2D();
        AffineTransform matrix = new AffineTransform();
        double origWidth, origHeight, frameWidth, frameHeight;

        origWidth = (double)this.viewBounds.width;
        origHeight = (double)this.viewBounds.height;
        frameWidth = origWidth * 0.75;
        frameHeight = origHeight * 0.75;

        matrix.translate(
            (origWidth - frameWidth) * 0.5,
            (origHeight - frameHeight) * 0.5
        );
        matrix.scale(
            frameWidth / shapeBounds.getWidth(),
            frameHeight / shapeBounds.getHeight()
        );

        tSh = matrix.createTransformedShape(oSh);

        g.fill(tSh);

    }

    private void drawShapeCentralized(final Graphics2D g, final Shape oSh) {

        Shape tSh;
        Rectangle2D shapeBounds = oSh.getBounds2D();
        AffineTransform matrix = new AffineTransform();
        double scale, origWidth, origHeight,
            shapeWidth, shapeHeight, frameWidth, frameHeight;

        origWidth = (double)this.viewBounds.width;
        origHeight = (double)this.viewBounds.height;
        frameWidth = origWidth * 0.75;
        frameHeight = origHeight * 0.75;
        shapeWidth = shapeBounds.getWidth();
        shapeHeight = shapeBounds.getHeight();

        if (shapeHeight / shapeWidth < frameHeight / frameWidth) {
            scale = frameWidth / shapeWidth;
        } else {
            scale = frameHeight / shapeHeight;
        }

        matrix.translate(
            (origWidth - (shapeWidth * scale)) * 0.5,
            (origHeight - (shapeHeight * scale)) * 0.5
        );
        matrix.scale(scale, scale);

        tSh = matrix.createTransformedShape(oSh);

        g.fill(tSh);

    }

    private void drawShape(final Graphics2D g, final Shape shape) {

        // set color
        g.setColor(FG_COLOR);

        if (this.preserveAspect) {
            this.drawShapeCentralized(g, shape);
        } else {
            this.drawShapeStretched(g, shape);
        }

    }

    /*
     * Public Methods
     */

    public void setImage(Image image) {
        this.imageRef = new ImageReference(image);
        this.shapeRef = null;
        this.needsRefresh = true;
        this.repaint();
    }

    public void setShape(Shape shape) {
        this.imageRef = null;
        this.shapeRef = shape;
        this.needsRefresh = true;
        this.repaint();
    }

    public void setPreserveAspect(boolean preserveAspect) {
        this.preserveAspect = preserveAspect;
        this.needsRefresh = true;
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2;
        ImageReference iRef;
        Shape sRef;
        int width, height;
        boolean viewResized = false;

        if ((width = this.getWidth()) > MIN_SIZE
            && (height = this.getHeight()) > MIN_SIZE) {

            // check for resize...
            if (width != this.viewBounds.width
                || height != this.viewBounds.height) {
                this.viewBounds.setSize(width, height);
                viewResized = true;
            }

            // check if view needs to be cleared...
            if (viewResized || this.needsRefresh) {
                g.setClip(0, 0, width, height);
                g.clearRect(0, 0, width, height);
                this.needsRefresh = false;
            }

            // setup graphics context...
            g2 = (Graphics2D)g;
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

            if ((iRef = this.imageRef) != null) {

                // should invalidate image rect?
                if (viewResized || iRef.status == 0) {
                    this.imageRect.setSize(-1, -1);
                    iRef.status = 1; // flag image is not fresh
                }

                // try drawing image...
                this.drawImage(g2, iRef.image);

            } else if ((sRef = this.shapeRef) != null) {

                // simply draw shape...
                this.drawShape(g2, sRef);

            }

        }

    }

    @Override
    public void update(Graphics g) {
        this.paint(g);
    }

    @Override
    public boolean imageUpdate(Image source, int flags, int x, int y, int w, int h) {
        final ImageReference iRef = this.imageRef;
        return ((iRef != null && source == iRef.image)
            ? super.imageUpdate(source, flags, x, y, w, h)
            : false
        );
    }

}
