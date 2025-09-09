package com.csse3200.game.cutscene.runtime.states;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class BackgroundState {
    private Drawable image;
    private Drawable oldImage;
    private float imageOpacity;
    private float oldImageOpacity;

    public Drawable getImage() {
        return image;
    }

    public Drawable getOldImage() {
        return oldImage;
    }

    public float getImageOpacity() {
        return imageOpacity;
    }

    public float getOldImageOpacity() {
        return oldImageOpacity;
    }

    public void setImage(Drawable newImage) {
        this.oldImage = this.image;
        this.image = newImage;
    }

    public void setImageOpacity(float imageOpacity) {
        this.imageOpacity = imageOpacity;
    }

    public void setOldImageOpacity(float oldImageOpacity) {
        this.oldImageOpacity = oldImageOpacity;
    }
}
