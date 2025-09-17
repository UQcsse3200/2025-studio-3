package com.csse3200.game.components.slot;

public final class SlotSymbols {
  private SlotSymbols() {}

  public static final int SLINGSHOOTER = 8;

  public static boolean isTriple(int a, int b, int c, int symbolId) {
    return a == symbolId && b == symbolId && c == symbolId;
  }
}
