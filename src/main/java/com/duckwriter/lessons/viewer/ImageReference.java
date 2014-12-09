package com.duckwriter.lessons.viewer;

import java.awt.Image;

final class ImageReference extends Object {

    final Image image;
    int status;

    ImageReference(Image image) {
        this.image = image;
        this.status = 0;
    }

}
