package com.csse3200.game.components.items;

/**
 * Represents a coffee item that can be used to temporarily boost shooting speed.
 */
public class CoffeeComponent extends Item {
    public CoffeeComponent() {
      super("Coffee", "Temporarily boosts shooting speed for 30s. One time use.", "coffee", 30);
    }
}
