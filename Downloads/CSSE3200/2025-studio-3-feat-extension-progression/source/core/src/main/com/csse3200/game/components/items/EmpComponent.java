package com.csse3200.game.components.items;

/** Represents an EMP item that can be used to temporarily disable enemies within a 3x3 area. */
public class EmpComponent extends Item {
  public EmpComponent() {
    super("EMP", "Shock everything within a 3x3 square area. One time use.", "emp", 30);
  }
}
