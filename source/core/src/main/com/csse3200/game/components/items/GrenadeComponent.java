package com.csse3200.game.components.items;

/**
 * Represents a grenade item that can be used to destroy a 3x3 area.
 */
public class GrenadeComponent extends Item {
    public GrenadeComponent() {
      super("Grenade", "Destroy everything within a 3x3 square area. One time use.", "grenade", 30);
    }
}
