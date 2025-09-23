package com.csse3200.game.cutscene.runtime.states;

import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.csse3200.game.cutscene.models.object.Character;
import com.csse3200.game.cutscene.models.object.Position;

/** Character state. */
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

  /**
   * Creates a new character state.
   *
   * @param character the character
   */
  public CharacterState(Character character) {
    this.character = character;
  }

  /**
   * Gets the character.
   *
   * @return the character
   */
  public Character getCharacter() {
    return character;
  }

  /**
   * Sets the character.
   *
   * @param character the character
   */
  public void setCharacter(Character character) {
    this.character = character;
  }

  /**
   * Gets the x offset.
   *
   * @return the x offset
   */
  public float getxOffset() {
    return xOffset;
  }

  /**
   * Sets the x offset.
   *
   * @param xOffset the x offset
   */
  public void setxOffset(float xOffset) {
    this.xOffset = xOffset;
  }

  /**
   * Gets the y offset.
   *
   * @return the y offset
   */
  public float getyOffset() {
    return yOffset;
  }

  /**
   * Sets the y offset.
   *
   * @param yOffset the y offset
   */
  public void setyOffset(float yOffset) {
    this.yOffset = yOffset;
  }

  /**
   * Gets the scale.
   *
   * @return the scale
   */
  public float getScale() {
    return scale;
  }

  /**
   * Sets the scale.
   *
   * @param scale the scale
   */
  public void setScale(float scale) {
    this.scale = scale;
  }

  /**
   * Gets the on screen.
   *
   * @return the on screen
   */
  public boolean isOnScreen() {
    return onScreen;
  }

  /**
   * Sets the on screen.
   *
   * @param onScreen the on screen
   */
  public void setOnScreen(boolean onScreen) {
    this.onScreen = onScreen;
  }

  /**
   * Gets the texture.
   *
   * @return the texture
   */
  public SpriteDrawable getTexture() {
    return texture;
  }

  /**
   * Sets the texture.
   *
   * @param texture the texture
   */
  public void setTexture(SpriteDrawable texture) {
    this.texture = texture;
  }

  /**
   * Gets the rotation.
   *
   * @return the rotation
   */
  public float getRotation() {
    return rotation;
  }

  /**
   * Sets the rotation.
   *
   * @param rotation the rotation
   */
  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  /**
   * Gets the opacity.
   *
   * @return the opacity
   */
  public float getOpacity() {
    return opacity;
  }

  /**
   * Sets the opacity.
   *
   * @param opacity the opacity
   */
  public void setOpacity(float opacity) {
    this.opacity = opacity;
  }

  /**
   * Gets the position.
   *
   * @return the position
   */
  public Position getPosition() {
    return position;
  }

  /**
   * Sets the position.
   *
   * @param position the position
   */
  public void setPosition(Position position) {
    this.position = position;
  }
}
