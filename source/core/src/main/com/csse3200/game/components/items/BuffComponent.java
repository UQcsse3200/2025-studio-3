package com.csse3200.game.components.items;

/** Represents a buff item that can be used to temporarily double damage. */
public class BuffComponent extends Item {
  public BuffComponent() {
    super("Buff", "Temporarily doubles damage for 30s. One time use.", "buff", 30);
  }
}
