package com.csse3200.game.components.fog;

/** Fog layer configuration (parameters only, for easy fine-tuning and alignment) */
public class FogConfig {
  // Grid dimensions
  public static final int ROWS = 5;
  public static final int COLS = 12;

  // Grid to stage coordinate conversion (adjust these 4 values if position/size doesn't align with
  // the grid)
  public static final float TILE_W = 1.0f;
  public static final float TILE_H = 1.0f;
  public static final float GRID_ORIGIN_X = 0f;
  public static final float GRID_ORIGIN_Y = -20f;

  // Fog layer behavior
  public static final float FOG_ALPHA = 0.9f; // Initial opacity
  public static final int CLICKS_TO_CLEAR = 10; // Clicks required to clear (fades gradually)
  public static final float DISPERSAL_TIME = 1f; // Final fade-out time (seconds)

  // Edge margin (in pixels, fine-tune based on screen display)
  public static final float FOG_MARGIN_LEFT_PX = 0f;
  public static final float FOG_MARGIN_RIGHT_PX = 0f;
  public static final float FOG_MARGIN_TOP_PX = 110f;
  public static final float FOG_MARGIN_BOTTOM_PX = 0f;

  // Right-to-left appearance speed (how many columns are covered per second)
  public static final float APPEAR_SPEED_TILES_PER_SEC = 2f;

  // Start offset in pixels from the right edge of the grid (0 means flush with the edge)
  public static final float APPEAR_START_OFFSET_PX = 0f;
}
