package com.csse3200.game.screens;

import com.csse3200.game.GdxGame;

import net.dermetfan.utils.Pair;

/**
 * Represents a node on the world map that can be registered dynamically.
 */
public class WorldMapNode {
  private String label;
  private Pair<Float, Float> coordinates;
  private boolean completed;
  private boolean unlocked;
  private GdxGame.ScreenType targetScreen;
  private String nodeTexture;
  private String lockReason;

  /**
   * Creates a new world map node.
   * 
   * @param label The label of the node
   * @param coordinates The coordinates of the node
   * @param completed Whether the node is completed
   * @param unlocked Whether the node is unlocked
   * @param targetScreen The target screen of the node
   * @param nodeTexture The texture of the node
   * @param lockReason The lock reason of the node
   */
  public WorldMapNode(String label, Pair<Float, Float> coordinates, boolean completed, boolean unlocked, GdxGame.ScreenType targetScreen, String nodeTexture, String lockReason) {
    this.label = label;
    this.coordinates = coordinates;
    this.completed = completed;
    this.unlocked = unlocked;
    this.targetScreen = targetScreen;
    this.nodeTexture = nodeTexture;
    this.lockReason = lockReason;
  }

  /**
   * Sets the label of the node.
   * 
   * @param label The label of the node
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Sets the position of the node.
   * 
   * @param x The x coordinate of the node (0.0-1.0)
   * @param y The y coordinate of the node (0.0-1.0)
   */
  public void setPosition(float x, float y) {
    this.coordinates = new Pair<>(x, y);
  }

  /**
   * Sets the completed state of the node.
   * 
   * @param completed Whether the node is completed
   */
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  /**
   * Sets the unlocked state of the node.
   * 
   * @param unlocked Whether the node is unlocked
   */
  public void setUnlocked(boolean unlocked) {
    this.unlocked = unlocked;
  }

  /**
   * Sets the target screen of the node.
   * 
   * @param screenType The target screen of the node
   */
  public void setTargetScreen(GdxGame.ScreenType screenType) {
    this.targetScreen = screenType;
  }

  /**
   * Sets the texture of the node.
   * 
   * @param texture The texture of the node
   */
  public void setNodeTexture(String texture) {
    this.nodeTexture = texture;
  }

  /**
   * Sets the lock reason of the node.
   * 
   * @param reason The lock reason of the node
   */
  public void setLockReason(String reason) {
    this.lockReason = reason;
  }

  /**
   * Gets the label of the node.
   * 
   * @return The label of the node
   */
  public String getLabel() {
    return label;
  }

  /**
   * Gets the x position of the node.
   * 
   * @return The xposition of the node
   */
  public float getPositionX() {
    return coordinates.getKey();
  }

  /**
   * Gets the y position of the node.
   * 
   * @return The y position of the node
   */
  public float getPositionY() {
    return coordinates.getValue();
  }

  /**
   * Gets the position of the node.
   * 
   * @return The position of the node
   */
  public Pair<Float, Float> getPosition() {
    return coordinates;
  }

  /**
   * Gets the completed state of the node.
   * 
   * @return The completed state of the node
   */
  public boolean isCompleted() {
    return completed;
  }

  /**
   * Gets the unlocked state of the node.
   * 
   * @return The unlocked state of the node
   */
  public boolean isUnlocked() {
    return unlocked;
  }

  /**
   * Gets the target screen of the node.
   * 
   * @return The target screen of the node
   */
  public GdxGame.ScreenType getTargetScreen() {
    return targetScreen;
  }

  /**
   * Gets the texture of the node.
   * 
   * @return The texture of the node
   */
  public String getNodeTexture() {
    return nodeTexture;
  }

  /**
   * Gets the lock reason of the node.
   * 
   * @return The lock reason of the node
   */
  public String getLockReason() {
    return lockReason;
  }
}
