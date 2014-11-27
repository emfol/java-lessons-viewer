package com.duckwriter.lessons.viewer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageProducer;

/**
 *
 * @author Emanuel
 */
public class ViewerComponent extends Component {

    private static final long serialVersionUID = 1L;

    private static final int MIN_SIZE = 1;

    private Image image;
    private Shape shape;
    private final Dimension viewBounds;
    private final Rectangle viewRect;

    /*
     * Constructors
     */

    public ViewerComponent() {
        super();
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

    private void drawShape(Graphics2D g) {

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

    @Override
    public void paint(Graphics g) {

        this.viewBounds.setSize(this.getWidth(), this.getHeight());
        if (this.viewBounds.width > MIN_SIZE
            && this.viewBounds.height > MIN_SIZE) {
            if (this.image != null) {
                this.drawImage((Graphics2D)g);
            } else if (this.shape != null) {
                this.drawShape((Graphics2D)g);
            }
        }

    }

    @Override
    public boolean imageUpdate(Image source, int flags, int x, int y, int w, int h) {

        final boolean needsUpdate = source == this.image
            ? super.imageUpdate(source, flags, x, y, w, h)
            : false;

        return needsUpdate;

    }

}
