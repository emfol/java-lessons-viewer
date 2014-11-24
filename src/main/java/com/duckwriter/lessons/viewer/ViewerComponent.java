package com.duckwriter.lessons.viewer;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.ImageProducer;

/**
 *
 * @author Emanuel
 */
public class ViewerComponent extends Component {

    private static final long serialVersionUID = 1L;

    private Image image;
    private final Rectangle viewRect;

    public ViewerComponent() {
        super();
        this.viewRect = new Rectangle();
    }

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

    public void setImage(Image image) {
        this.image = image;
        this.repaint();
    }

    public void setImage(ImageProducer producer) {
        this.setImage(this.createImage(producer));
    }

    @Override
    public void paint(Graphics g) {
        Image im = this.image;
        Rectangle vw = this.getViewRect();
        if (im != null) {
            vw = this.getViewRect();
        }
    }

}
