package com.csse3200.game.cutscene.runtime.states;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Background state.
 */
public class BackgroundState {
  private Drawable image;
  private Drawable oldImage;
  private float imageOpacity;
  private float oldImageOpacity;

  /**
   * Gets the image.
   * 
   * @return the image
   */
  public Drawable getImage() {
    return image;
  }

  /**
   * Gets the old image.
   * 
   * @return the old image
   */
  public Drawable getOldImage() {
    return oldImage;
  }

  /**
   * Gets the image opacity.
   * 
   * @return the image opacity
   */
  public float getImageOpacity() {
    return imageOpacity;
  }

  /**
   * Gets the old image opacity.
   * 
   * @return the old image opacity
   */
  public float getOldImageOpacity() {
    return oldImageOpacity;
  }

  /**
   * Sets the image.
   * 
   * @param newImage the new image
   */
  public void setImage(Drawable newImage) {
    this.oldImage = this.image;
    this.image = newImage;
  }

  /**
   * Sets the image opacity.
   * 
   * @param imageOpacity the new image opacity
   */
  public void setImageOpacity(float imageOpacity) {
    this.imageOpacity = imageOpacity;
  }

  /**
   * Sets the old image opacity.
   * 
   * @param oldImageOpacity the new old image opacity
   */
  public void setOldImageOpacity(float oldImageOpacity) {
    this.oldImageOpacity = oldImageOpacity;
  }
}
