package com.csse3200.game.components.fog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * A full-screen fog overlay that covers a grid. It fades in steps with each click, and then
 * performs a final smooth fade-out before being removed. - Always stays on top; consumes input to
 * prevent clicks on underlying elements. - Enforces uniqueness: only one fog layer is allowed on
 * the stage.
 */
public class FogOverlayActor extends Actor {
  public static final String NAME = "FogOverlay";

  // Texture
  private static Texture fogTex;

  private static Texture ensureFogTexture() {
    if (fogTex != null) return fogTex;
    String[] candidates = {
      "images/fog/fog_tile.png",
      "images/fog/fog.png",
      "fog/fog_tile.png",
      "fog.png",
      "images/fog_tile.png",
      "fog_tile.png"
    };
    for (String p : candidates) {
      FileHandle h = Gdx.files.internal(p);
      if (h.exists()) {
        Texture t = new Texture(h);
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        fogTex = t;
        return fogTex;
      }
    }
    // Fallback: generate a semi-transparent solid color texture
    Pixmap pm = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
    pm.setColor(1f, 1f, 1f, 0.2f);
    pm.fill();
    fogTex = new Texture(pm);
    pm.dispose();
    return fogTex;
  }

  // Configuration / State
  private final float dispersalSeconds;
  private final float maxAlpha;
  private int clicksLeft;
  private final int totalClicks; // Total clicks required
  private float disperseStartAlpha; // Starting alpha when the final fade-out begins

  private boolean dispersing = false;
  private float dispersalClock = 0f;
  private float alpha;

  // Appearance animation
  private final float baseX, baseY, baseW, baseH;
  private final float appearSpeedPx;

  public FogOverlayActor() {
    // --- Calculate the covered area (based on screen pixels & FogConfig)
    float screenW = Gdx.graphics.getWidth();
    float screenH = Gdx.graphics.getHeight();

    float tileWpx = screenW / (float) FogConfig.COLS;
    float tileHpx = screenH / (float) FogConfig.ROWS;
    float tilePx = Math.min(tileWpx, tileHpx);

    float gridW = tilePx * FogConfig.COLS;
    float gridH = tilePx * FogConfig.ROWS;

    float originX = (screenW - gridW) * 0.5f + FogConfig.GRID_ORIGIN_X;
    float originY = (screenH - gridH) * 0.5f + FogConfig.GRID_ORIGIN_Y;

    float fogX = originX + FogConfig.FOG_MARGIN_LEFT_PX;
    float fogY = originY + FogConfig.FOG_MARGIN_BOTTOM_PX;
    float fogW = gridW - (FogConfig.FOG_MARGIN_LEFT_PX + FogConfig.FOG_MARGIN_RIGHT_PX);
    float fogH = gridH - (FogConfig.FOG_MARGIN_TOP_PX + FogConfig.FOG_MARGIN_BOTTOM_PX);

    baseX = fogX;
    baseY = fogY;
    baseW = fogW;
    baseH = fogH;

    appearSpeedPx = FogConfig.APPEAR_SPEED_TILES_PER_SEC * tilePx;

    // Initially start with width=0 outside the right edge, expanding to the left
    float startRight = baseX + baseW + FogConfig.APPEAR_START_OFFSET_PX;
    setBounds(startRight, baseY, 0f, baseH);

    // Alpha / click logic
    this.dispersalSeconds = FogConfig.DISPERSAL_TIME;
    this.maxAlpha = FogConfig.FOG_ALPHA;
    this.alpha = maxAlpha;
    this.clicksLeft = Math.max(1, FogConfig.CLICKS_TO_CLEAR);
    this.totalClicks = this.clicksLeft;
    this.disperseStartAlpha = maxAlpha;

    setName(NAME);
    setTouchable(Touchable.enabled);
    ensureFogTexture();

    // Fade in steps with each click; trigger the final fade-out on the last click
    addListener(
        new InputListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            event.stop(); // Consume the event to prevent it from reaching underlying suns
            if (clicksLeft > 0) {
              clicksLeft--;
              if (totalClicks > 0) {
                alpha = maxAlpha * (clicksLeft / (float) totalClicks);
              }
              if (clicksLeft <= 0) {
                dispersing = true;
                dispersalClock = 0f;
                disperseStartAlpha = Math.max(0f, alpha);
              }
            }
            return true;
          }
        });
  }

  /** When added to a Stage, remove all other existing fog layers, ensuring uniqueness. */
  @Override
  protected void setStage(Stage stage) {
    super.setStage(stage);
    if (stage == null) return;
    Group root = stage.getRoot();
    for (int i = root.getChildren().size - 1; i >= 0; i--) {
      Actor a = root.getChildren().get(i);
      if (a != this && a instanceof FogOverlayActor) a.remove();
    }
    if (getName() == null) setName(NAME);
    toFront();
  }

  @Override
  public Actor hit(float x, float y, boolean touchable) {
    if (!isVisible()) return null;
    if (touchable && getTouchable() == Touchable.disabled) return null;
    return (x >= 0 && y >= 0 && x < getWidth() && y < getHeight()) ? this : null;
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    // Right-to-left appearance animation
    if (!dispersing && getWidth() < baseW) {
      float newW = Math.min(baseW, getWidth() + appearSpeedPx * delta);
      float newLeft = (baseX + baseW) - newW;
      setBounds(newLeft, baseY, newW, baseH);
    }

    // Final fade-out: from "current brightness" to 0
    if (dispersing) {
      dispersalClock += delta;
      float k = Math.min(1f, dispersalClock / Math.max(0.0001f, dispersalSeconds));
      alpha = (1f - k) * disperseStartAlpha;
      if (k >= 1f) remove();
    }

    // Keep this actor at the top
    if (getStage() != null && getParent() != null) {
      int top = getParent().getChildren().size - 1;
      if (getZIndex() != top) toFront();
    }
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    float oldA = batch.getColor().a;
    batch.setColor(1f, 1f, 1f, alpha * parentAlpha);

    float x0 = getX(), y0 = getY();
    float w = getWidth(), h = getHeight();

    float tilePx = 256f; // Pixel art tiling size
    for (float yy = y0; yy < y0 + h; yy += tilePx) {
      float drawH = Math.min(tilePx, y0 + h - yy);
      for (float xx = x0; xx < x0 + w; xx += tilePx) {
        float drawW = Math.min(tilePx, x0 + w - xx);
        batch.draw(fogTex, xx, yy, drawW, drawH);
      }
    }
    batch.setColor(1f, 1f, 1f, oldA);
  }

  /**
   * Unified entry point: removes all other fog layers and ensures only one exists. If no fog
   * exists, a new one is created.
   */
  public static FogOverlayActor spawnOnce(Stage stage) {
    FogOverlayActor existing = null;
    Group root = stage.getRoot();
    for (int i = root.getChildren().size - 1; i >= 0; i--) {
      Actor a = root.getChildren().get(i);
      if (a instanceof FogOverlayActor) {
        if (existing == null) existing = (FogOverlayActor) a;
        else a.remove();
      }
    }
    if (existing != null) {
      existing.toFront();
      return existing;
    }
    FogOverlayActor a = new FogOverlayActor();
    stage.addActor(a); // setStage will again de-duplicate and bring to front
    return a;
  }
}
