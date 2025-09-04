package com.csse3200.game.components.items;

/**
 * Represents a nuclear bomb item that can wipe the board of enemies.
 */
public class NukeComponent extends Item {
  public NukeComponent() {
    super("Nuke", "Wipe the map of all enemies. One time use.", "nuke", 100);
  }
}
