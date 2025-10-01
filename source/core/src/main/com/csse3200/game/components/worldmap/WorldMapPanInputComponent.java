package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.screens.WorldMapScreen;

/**
 * Enables grab-and-drag panning of the world map camera.
 * Uses a lower priority than the Stage InputDecorator so UI can consume input first.
 */
public class WorldMapPanInputComponent extends InputComponent {
  private final WorldMapScreen screen;
  private boolean dragging = false;
  private int lastX = 0;
  private int lastY = 0;
  private int dragButton = Input.Buttons.LEFT; // default to left; also accept middle/right

  /**
   * @param screen  World map screen to pan
   * @param priority Input priority (e.g., 5). Stage typically uses 10.
   */
  public WorldMapPanInputComponent(WorldMapScreen screen, int priority) {
    super(priority);
    this.screen = screen;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    // Disable drag while the player is moving; keep view locked to center
    if (screen.isPlayerCurrentlyMoving()) {
      return false;
    }
    if (button == dragButton || button == Input.Buttons.MIDDLE || button == Input.Buttons.RIGHT) {
      dragging = true;
      lastX = screenX;
      lastY = screenY;
      screen.startManualPan(); // disable follow mode when user pans
      // Do not consume the event so UI can still receive clicks
      return false;
    }
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (screen.isPlayerCurrentlyMoving()) {
      dragging = false;
      return false;
    }
    if (!dragging) return false;
    int dx = screenX - lastX;
    int dy = screenY - lastY;
    lastX = screenX;
    lastY = screenY;
    // Dragging moves the view with the cursor
    screen.panByScreenDelta(dx, dy);
    // Do not consume to allow other drags (e.g., UI) if needed
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    if (dragging && (button == dragButton || button == Input.Buttons.MIDDLE || button == Input.Buttons.RIGHT)) {
      dragging = false;
      // Do not consume so UI gets the click/release
      return false;
    }
    return false;
  }
}

