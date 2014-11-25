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

    public static void adjustViewRect(Rectangle viewRect, int imageWidth, int imageHeight) {

        float k, vw, vh, iw, ih;

        vw = (float)viewRect.width;
        vh = (float)viewRect.height;
        iw = (float)imageWidth;
        ih = (float)imageHeight;

        // calculate scale factor
        k = (ih / iw < vh / vw) ? vw / iw : vh / ih;

        // scale image
        if (k < 1.0f) {
            imageWidth = (int)(k * iw);
            imageHeight = (int)(k * ih);
        }
        // update rectangle
        viewRect.x = (viewRect.width - imageWidth) / 2;
        viewRect.y = (viewRect.height - imageHeight) / 2;
        viewRect.width = imageWidth;
        viewRect.height = imageHeight;

    }

    private Rectangle getViewRect() {
        Rectangle result = null;
        int viewWidth, viewHeight, imageWidth, imageHeight;
        if (this.image != null
            && (imageWidth = this.image.getWidth(this)) > 0
            && (imageHeight = this.image.getHeight(this)) > 0
            && (viewWidth = this.getWidth()) > 0
            && (viewHeight = this.getHeight()) > 0
        ) {
            this.viewRect.width = viewWidth;
            this.viewRect.height = viewHeight;
            adjustViewRect(this.viewRect, imageWidth, imageHeight);
            result = this.viewRect;
        }
        return result;
    }

    private void drawImage(Graphics2D g) {

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

    public void setImage(ImageProducer producer) {
        this.setImage(this.createImage(producer));
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

}
