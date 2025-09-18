package com.csse3200.game.cutscene.runtime.states;

import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.csse3200.game.cutscene.models.object.Character;
import com.csse3200.game.cutscene.models.object.Position;

public class CharacterState {
  private Character character;
  private float xOffset; // in percentage of sprite width
  private float yOffset; // in percentage of sprite height
  private float scale;
  private float rotation;
  private float opacity;
  private boolean onScreen;
  private SpriteDrawable texture;
  private Position position;

  public CharacterState(Character character) {
    this.character = character;
  }

  public Character getCharacter() {
    return character;
  }

  public void setCharacter(Character character) {
    this.character = character;
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

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  public boolean isOnScreen() {
    return onScreen;
  }

  public void setOnScreen(boolean onScreen) {
    this.onScreen = onScreen;
  }

  public SpriteDrawable getTexture() {
    return texture;
  }

  public void setTexture(SpriteDrawable texture) {
    this.texture = texture;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public float getOpacity() {
    return opacity;
  }

  public void setOpacity(float opacity) {
    this.opacity = opacity;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }
}
