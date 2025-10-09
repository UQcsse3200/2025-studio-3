package com.csse3200.game.cutscene.runtime.components;

public class CharacterImageData {
  private float xOffset;
  private float yOffset;
  private float rotation;
  private float scale;
  private int zIndex;

  public CharacterImageData(float xOffset, float yOffset, float rotation, float scale, int zIndex) {
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.rotation = rotation;
    this.scale = scale;
    this.zIndex = zIndex;
  }

  public float getxOffset() {
    return xOffset;
  }

  public void setxOffset(float xOffset) {
    this.xOffset = xOffset;
  }

  public float getyOffset() {
    return yOffset;
  }

  public void setyOffset(float yOffset) {
    this.yOffset = yOffset;
  }

  public int getzIndex() {
    return zIndex;
  }

  public void setzIndex(int zIndex) {
    this.zIndex = zIndex;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }
}
