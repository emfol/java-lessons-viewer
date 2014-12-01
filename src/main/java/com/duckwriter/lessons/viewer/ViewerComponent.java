package com.duckwriter.lessons.viewer;

import java.util.concurrent.atomic.AtomicReference;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.RenderingHints;
import java.awt.CheckboxMenuItem;
import java.awt.PopupMenu;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ViewerComponent extends Component
    implements MouseListener, ItemListener {

    /* Class Members */

    private static final long serialVersionUID = 1L;

    public static final int SHAPE_MODE_CENTRALIZED = 0;
    public static final int SHAPE_MODE_STRETCHED = 1;

    private static final int MIN_SIZE = 1;
    private static final Color FG_COLOR = new Color(0x004285f4);
    private static final String ITEM_LABEL_CENTRALIZED = "Centralized Mode";
    private static final String ITEM_LABEL_STRETCHED = "Stretched Mode";

    /* Instance Members */

    private final AtomicReference<Image> imageRef;
    private final AtomicReference<Shape> shapeRef;
    private final Dimension viewBounds;
    private final Rectangle viewRect;
    private PopupMenu popupMenu;
    private volatile int shapeMode;
    private volatile boolean popupTriggered;
    private volatile boolean initialized;

    /*
     * Constructors
     */

    public ViewerComponent() {
        super();
        this.imageRef = new AtomicReference<Image>();
        this.shapeRef = new AtomicReference<Shape>();
        this.viewBounds = new Dimension();
        this.viewRect = new Rectangle();
        this.popupMenu = null;
        this.shapeMode = SHAPE_MODE_CENTRALIZED;
        this.popupTriggered = false;
        this.initialized = false;
        // settings
        this.setBackground(SystemColor.window);
    }

    /*
     * Private Methods
     */

    private PopupMenu getPopupMenu() {

        final int mode = this.shapeMode;
        PopupMenu menu = this.popupMenu;
        CheckboxMenuItem menuItem;

        if (menu == null) {

            // initialize popup menu
            menu = new PopupMenu();

            menuItem = new CheckboxMenuItem(
                ITEM_LABEL_CENTRALIZED,
                mode == SHAPE_MODE_CENTRALIZED
            );
            menuItem.addItemListener(this);
            menu.add(menuItem);
            menuItem = new CheckboxMenuItem(
                ITEM_LABEL_STRETCHED,
                mode == SHAPE_MODE_STRETCHED
            );
            menuItem.addItemListener(this);
            menu.add(menuItem);

            // add?
            this.add(menu);

            // save reference
            this.popupMenu = menu;

        }

        return menu;

    }

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

    private void drawImage(final Graphics2D g, final Image image) {

        int origWidth, origHeight;

        if ((origWidth = image.getWidth(this)) > 0
            && (origHeight = image.getHeight(this)) > 0) {
            this.viewRect.setSize(origWidth, origHeight);
            this.adjustViewRect();
            g.drawImage(
                image,
                this.viewRect.x,
                this.viewRect.y,
                this.viewRect.width,
                this.viewRect.height,
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

        final int mode = this.shapeMode;

        // set color
        g.setColor(FG_COLOR);

        switch (mode) {
            case SHAPE_MODE_CENTRALIZED:
                this.drawShapeCentralized(g, shape);
                break;
            case SHAPE_MODE_STRETCHED:
                this.drawShapeStretched(g, shape);
                break;
        }

    }

    /*
     * Public Methods
     */

    public void setImage(final Image image) {
        this.imageRef.set(image);
        this.shapeRef.set(null);
        this.repaint();
    }

    public void setShape(final Shape shape) {
        this.imageRef.set(null);
        this.shapeRef.set(shape);
        this.repaint();
    }

    public void setShapeMode(final int shapeMode) {
        this.shapeMode = shapeMode;
    }

    @Override
    public void paint(Graphics g) {

        Image image;
        Shape shape;
        Graphics2D g2;

        // temporary... where should I put this?
        if (!this.initialized) {
            this.addMouseListener(this);
            this.initialized = true;
        }

        this.viewBounds.setSize(this.getWidth(), this.getHeight());
        if (this.viewBounds.width > MIN_SIZE
            && this.viewBounds.height > MIN_SIZE) {
            g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if ((image = this.imageRef.get()) != null) {
                this.drawImage(g2, image);
            } else if ((shape = this.shapeRef.get()) != null) {
                this.drawShape(g2, shape);
            }
        }

    }

    @Override
    public boolean imageUpdate(Image source, int flags, int x, int y, int w, int h) {

        final Image image = this.imageRef.get();
        final boolean needsUpdate = (source == image)
            ? super.imageUpdate(source, flags, x, y, w, h)
            : false;

        return needsUpdate;

    }

    /* MouseListener Interface */

    @Override
    public void mouseEntered(MouseEvent e) {
        // nothing...
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // nothing...
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.popupTriggered = e.isPopupTrigger();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.popupTriggered || e.isPopupTrigger()) {
            (this.getPopupMenu()).show(this, e.getX(), e.getY());
            this.popupTriggered = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // nothing...
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO...
    }

}
