package com.csse3200.game.components.worldmap;

import com.csse3200.game.input.InputComponent;
import com.csse3200.game.screens.WorldMapScreen;

/**
 * Handles mouse wheel scrolling to zoom the world map. Uses a lower priority than the Stage
 * InputDecorator so UI can consume scroll first.
 */
public class WorldMapZoomInputComponent extends InputComponent {
  private final WorldMapScreen screen;
  private float scrollAccumulator = 0f; // protects against large/frequent scroll bursts

  /**
   * @param screen The WorldMapScreen to delegate zoom changes to
   * @param priority Input priority (e.g., 5). Stage typically uses 10.
   */
  public WorldMapZoomInputComponent(WorldMapScreen screen, int priority) {
    super(priority);
    this.screen = screen;
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    // LibGDX: amountY > 0 = scroll down (away) -> zoom out; amountY < 0 -> zoom in
    if (amountY == 0f) {
      return false;
    }
    scrollAccumulator += amountY;
    int steps = (int) Math.floor(Math.abs(scrollAccumulator));
    if (steps <= 0) {
      return false;
    }
    int direction = scrollAccumulator > 0f ? +1 : -1;
    screen.stepZoom(direction * steps);
    // Remove the applied integer portion, keep fractional remainder as buffer
    scrollAccumulator -= direction * steps;
    return true;
  }
}
