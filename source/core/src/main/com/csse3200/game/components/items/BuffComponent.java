package com.csse3200.game.components.items;

/** Represents a buff item that can be used to temporarily double damage. */
public class BuffComponent extends Item {
  /** Creates a new buff component with default properties. */
  public BuffComponent() {
    super("Buff", "Temporarily doubles damage for 30s. One time use.", "buff", 30);
  }
}
