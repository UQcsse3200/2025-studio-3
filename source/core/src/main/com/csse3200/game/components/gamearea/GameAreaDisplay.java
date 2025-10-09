package com.csse3200.game.components.gamearea;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.csse3200.game.ui.UIComponent;

/** Displays the name of the current game area. */
public class GameAreaDisplay extends UIComponent {
  private String gameAreaName = "";
  private Label title;

  public GameAreaDisplay(String gameAreaName) {
    this.gameAreaName = gameAreaName;
  }

  @Override
  public void create() {
    super.create();
    entity.getEvents().addListener("pause", this::handlePause);
    entity.getEvents().addListener("resume", this::handleResume);
    addActors();
  }

  private void addActors() {
    title = ui.title(this.gameAreaName);
    stage.addActor(title);
  }

  @Override
  public void draw(SpriteBatch batch) {
    float width = stage.getViewport().getWorldWidth();
    float height = stage.getViewport().getWorldHeight();
    float offsetX = 0.015f * width;
    float offsetY = 0.08f * height;

    title.setPosition(offsetX, height - offsetY);
  }

  /** Handles pause events to dim the HUD */
  private void handlePause() {
    if (title != null) {
      title.getColor().a = 0.3f; // Dim to 30% opacity
    }
  }

  /** Handles resume events to restore the HUD */
  private void handleResume() {
    if (title != null) {
      title.getColor().a = 1.0f; // Restore to 100% opacity
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    title.remove();
  }
}
