package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.Input;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.screens.WorldMapScreen;

/**
 * Enables grab-and-drag panning of the world map camera.
 * This component allows the user to move the camera manually when not following the player.
 * Uses a lower priority than the Stage InputDecorator so that UI elements can consume input first.
 */
public class WorldMapPanInputComponent extends InputComponent {

  private final WorldMapScreen screen;
  private boolean dragging = false;
  private int lastX = 0;
  private int lastY = 0;

  /** Default drag button (shared by all instances). */
  private static final int DRAG_BUTTON = Input.Buttons.LEFT;

  /**
   * Constructor.
   *
   * @param screen   Reference to the world map screen to pan
   * @param priority Input handling priority (Stage typically uses 10)
   */
  public WorldMapPanInputComponent(WorldMapScreen screen, int priority) {
    super(priority);
    this.screen = screen;
  }

  /**
   * Called when the user presses a mouse button or touches the screen.
   * Starts a drag if the appropriate button is pressed.
   *
   * @return false – event is not consumed so UI can still receive clicks.
   */
  @SuppressWarnings("java:S3516") // Always returns false intentionally (design choice).
  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    // Disable drag while the player is moving; camera follows automatically in that case.
    if (screen.isPlayerCurrentlyMoving()) {
      return false;
    }

    // Start dragging if left/middle/right button is pressed.
    if (button == DRAG_BUTTON || button == Input.Buttons.MIDDLE || button == Input.Buttons.RIGHT) {
      dragging = true;
      lastX = screenX;
      lastY = screenY;
      screen.startManualPan(); // Disable follow mode when manual panning starts.
    }

    // Do not consume the event to allow UI to handle it.
    return false;
  }

  /**
   * Called continuously while the user drags the mouse or finger.
   * Moves the camera view accordingly.
   *
   * @return false – we do not consume the event to allow UI drag events as well.
   */
  @SuppressWarnings("java:S3516") // Always returns false intentionally (design choice).
  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    // Stop dragging if the player starts moving (auto-follow takes over)
    if (screen.isPlayerCurrentlyMoving()) {
      dragging = false;
      return false;
    }

    if (dragging) {
      int dx = screenX - lastX;
      int dy = screenY - lastY;
      lastX = screenX;
      lastY = screenY;
      // Move the camera view opposite to cursor motion
      screen.panByScreenDelta(dx, dy);
    }

    // Not consumed (other handlers may still process)
    return false;
  }

  /**
   * Called when the user releases a pressed mouse button or lifts the finger.
   * Ends the dragging operation if active.
   *
   * @return false – event is not consumed so UI receives release/clicks.
   */
  @SuppressWarnings("java:S3516") // Always returns false intentionally (design choice).
  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    // Stop dragging if the drag button was released.
    if (dragging
        && (button == DRAG_BUTTON
            || button == Input.Buttons.MIDDLE
            || button == Input.Buttons.RIGHT)) {
      dragging = false;
    }

    // Do not consume event (UI may also need release)
    return false;
  }
}
