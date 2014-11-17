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

    private Image image;
    private final Rectangle viewRect;

    public ViewerComponent() {
        super();
        this.viewRect = new Rectangle();
    }

    public static boolean adjustViewRect(Rectangle viewRect, int imageWidth, int imageHeight) {
        boolean result = false;
        float scl, vrW, vrH, imW, imH;
        if (viewRect.width > 0 && viewRect.height > 0
            && imageWidth > 0 && imageHeight > 0) {
            vrW = (float)viewRect.width;
            vrH = (float)viewRect.height;
            imW = (float)imageWidth;
            imH = (float)imageHeight;
            scl = (imH / imW < vrH / vrW) ? vrW / imW : vrH / imH;
            // scale image
            imageWidth = (int)(scl * imW);
            imageHeight = (int)(scl * imH);
            // update rectangle
            viewRect.x = (viewRect.width - imageWidth) / 2;
            viewRect.y = (viewRect.height - imageHeight) / 2;
            viewRect.width = imageWidth;
            viewRect.height = imageHeight;
            result = true;
        }
        return result;
    }

    private Rectangle getViewRect() {
        Image im = this.image;
        Rectangle vr = null;
        if (im != null) {
            this.viewRect.width = this.getWidth();
            this.viewRect.height = this.getHeight();
            if (adjustViewRect(this.viewRect, im.getWidth(this), im.getHeight(this))) {
                vr = this.viewRect;
            }
        }
        return vr;
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
