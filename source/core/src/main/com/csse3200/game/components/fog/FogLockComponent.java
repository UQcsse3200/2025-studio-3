package com.csse3200.game.components.fog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.input.InputComponent;

public class FogLockComponent extends InputComponent {
  private final Stage stage;

  // The clickable fog area (screen coordinates, top-left origin)
  private final Rectangle fogAreaScreenTL = new Rectangle();

  // === New: Whitelist area (e.g., the top-right "Exit" button area, screen coordinates, top-left
  // origin)
  private final Rectangle whitelistScreenTL = new Rectangle();
  private boolean whitelistEnabled = false;

  // Whether to allow the ESC key to pass through
  private boolean allowEsc = true;

  private boolean active = false;

  public FogLockComponent(Stage stage) {
    super(10_000);
    this.stage = stage;
  }

  /** Sets the fog area using screen coordinates (top-left origin). */
  public void setFogAreaScreenTL(float x, float yFromTop, float w, float h) {
    fogAreaScreenTL.set(x, yFromTop, w, h);
  }

  /** Converts stage bounds to screen coordinates (top-left origin) and sets the fog area. */
  public void setFogAreaFromStageBounds(float stageX, float stageY, float w, float h) {
    if (stage == null || stage.getViewport() == null) {
      setFogAreaScreenTL(stageX, stageY, w, h);
      return;
    }
    Vector3 p0 = new Vector3(stageX, stageY, 0);
    Vector3 p1 = new Vector3(stageX + w, stageY + h, 0);
    stage.getViewport().project(p0);
    stage.getViewport().project(p1);
    float screenH = Gdx.graphics.getHeight();
    float x = p0.x;
    float yTop = screenH - p1.y;
    float width = Math.abs(p1.x - p0.x);
    float height = Math.abs(p1.y - p0.y);
    fogAreaScreenTL.set(x, yTop, width, height);
  }

  /** === New: Sets the "Exit button" whitelist screen area (top-left origin). */
  public void setWhitelistScreenTL(float x, float yFromTop, float w, float h) {
    whitelistScreenTL.set(x, yFromTop, w, h);
    whitelistEnabled = true;
  }

  /** === New: Configures whether the ESC key is allowed to pass through. */
  public void setAllowEsc(boolean allow) {
    this.allowEsc = allow;
  }

  public void activate() {
    active = true;
  }

  public void deactivate() {
    active = false;
    if (stage != null) {
      stage.cancelTouchFocus();
      stage.unfocusAll();
    }
  }

  public boolean isActive() {
    return active;
  }

  // Keyboard input handling
  @Override
  public boolean keyDown(int keycode) {
    if (!active) return false;
    if (allowEsc && keycode == Input.Keys.ESCAPE) {
      // Pass the ESC key directly to the Stage and prevent other game inputs from receiving it
      if (stage != null) stage.keyDown(keycode);
      return true;
    }
    return true; // Intercept all other keys
  }

  @Override
  public boolean keyTyped(char character) {
    return active;
  }

  @Override
  public boolean keyUp(int keycode) {
    if (!active) return false;
    if (allowEsc && keycode == Input.Keys.ESCAPE) {
      if (stage != null) stage.keyUp(keycode);
      return true;
    }
    return true;
  }

  @Override
  public boolean scrolled(float ax, float ay) {
    return active;
  }

  @Override
  public boolean mouseMoved(int x, int y) {
    return active;
  }

  // Mouse input handling
  @Override
  public boolean touchDown(int sx, int sy, int pointer, int button) {
    if (!active) return false;

    // Whitelist area (e.g., top-right exit button) - always pass to the Stage
    if (whitelistEnabled && whitelistScreenTL.contains(sx, sy)) {
      if (stage != null) stage.touchDown(sx, sy, pointer, button);
      return true;
    }

    // Fog area: Pass to the Stage (for fog dispersal)
    if (fogAreaScreenTL.contains(sx, sy)) {
      if (stage != null) stage.touchDown(sx, sy, pointer, button);
      return true;
    }
    return true; // Disable other areas
  }

  @Override
  public boolean touchDragged(int sx, int sy, int pointer) {
    if (!active) return false;
    if (stage != null) stage.touchDragged(sx, sy, pointer);
    return true;
  }

  @Override
  public boolean touchUp(int sx, int sy, int pointer, int button) {
    if (!active) return false;
    if (stage != null) stage.touchUp(sx, sy, pointer, button);
    return true;
  }
}
