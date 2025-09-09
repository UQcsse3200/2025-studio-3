package com.csse3200.game.screens;

public class Node {
  public String id;
  public String label;
  public float px; // relative X (0.0–1.0)
  public float py; // relative Y (0.0–1.0)

  public boolean completed; // true if the level is finished
  public boolean unlocked; // true if the level is available to play
  public int level; // level number (for choosing locked icon)

  public String lockReason;
}
