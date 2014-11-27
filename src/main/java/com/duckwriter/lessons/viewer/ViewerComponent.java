package com.duckwriter.lessons.viewer;

import java.awt.Component;
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

/**
 *
 * @author Emanuel
 */
public class ViewerComponent extends Component {

    private static final long serialVersionUID = 1L;

    public static final int SHAPE_MODE_CENTRALIZED = 0;
    public static final int SHAPE_MODE_STRETCHED = 1;

    private static final int MIN_SIZE = 1;

    private Image image;
    private Shape shape;
    private int shapeMode;
    private final Dimension viewBounds;
    private final Rectangle viewRect;

    /*
     * Constructors
     */

    public ViewerComponent() {
        super();
        this.shapeMode = SHAPE_MODE_CENTRALIZED;
        this.viewBounds = new Dimension();
        this.viewRect = new Rectangle();
    }

    /*
     * Private Methods
     */

    private void adjustViewRect() {

        float k, vw, vh, iw, ih;

        vw = (float)this.viewBounds.width;
        vh = (float)this.viewBounds.height;
        iw = (float)this.viewRect.width;
        ih = (float)this.viewRect.height;

        // calculate k (scale factor)
        k = (ih / iw) < (vh / vw)
            ? vw / iw
            : vh / ih;

        // scale image
        if (k < 1.0f) {
            this.viewRect.width = (int)(k * iw);
            this.viewRect.height = (int)(k * ih);
        }

        // update rect offset
        this.viewRect.x = (this.viewBounds.width - this.viewRect.width) / 2;
        this.viewRect.y = (this.viewBounds.height - this.viewRect.height) / 2;

    }

    private void drawImage(Graphics2D g) {

        int origWidth, origHeight;

        if ((origWidth = this.image.getWidth(this)) > 0
            && (origHeight = this.image.getHeight(this)) > 0) {
            this.viewRect.setSize(origWidth, origHeight);
            this.adjustViewRect();
            g.drawImage(
                this.image,
                this.viewRect.x,
                this.viewRect.y,
                this.viewRect.width,
                this.viewRect.height,
                this
            );
        }

    }

    private void drawShapeStretched(Graphics2D g) {

        Shape tSh, oSh = this.shape;
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

    private void drawShapeCentralized(Graphics2D g) {

        Shape tSh, oSh = this.shape;
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

    private void drawShape(Graphics2D g) {

        final int mode = this.shapeMode;

        // set color
        g.setColor(new Color(0x004285f4));

        switch (mode) {
            case SHAPE_MODE_CENTRALIZED:
                this.drawShapeCentralized(g);
                break;
            case SHAPE_MODE_STRETCHED:
                this.drawShapeStretched(g);
                break;
        }

    }

    /*
     * Public Methods
     */

    public void setImage(Image image) {
        this.image = image;
        this.shape = null;
        this.repaint();
    }

    public void setShape(Shape shape) {
        this.image = null;
        this.shape = shape;
        this.repaint();
    }

    public void setShapeMode(final int shapeMode) {
        this.shapeMode = shapeMode;
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2;

        this.viewBounds.setSize(this.getWidth(), this.getHeight());
        if (this.viewBounds.width > MIN_SIZE
            && this.viewBounds.height > MIN_SIZE) {
            g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if (this.image != null) {
                this.drawImage(g2);
            } else if (this.shape != null) {
                this.drawShape(g2);
            }
        }

    }

    @Override
    public boolean imageUpdate(Image source, int flags, int x, int y, int w, int h) {

        final boolean needsUpdate = (source == this.image)
            ? super.imageUpdate(source, flags, x, y, w, h)
            : false;

        return needsUpdate;

    }

}
