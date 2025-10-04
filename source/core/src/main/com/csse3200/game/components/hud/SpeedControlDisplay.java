package com.csse3200.game.components.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HUD component that lets the player cycle gameplay speed between 1.0x, 1.5x and 2.0x. Uses
 * GameTime timeScale to speed up time-dependent systems.
 */
public class SpeedControlDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(SpeedControlDisplay.class);
  private static final float Z_INDEX = 40f;
  private static final int BUTTON_SIZE = 60;

  private final float[] speeds = new float[] {1.0f, 1.5f, 2.0f};
  private final String[] speedImages =
      new String[] {
        "images/ui/speedup1x.png", "images/ui/speedup15x.png", "images/ui/speedup2x.png"
      };
  private int speedIndex = 0;

  private ImageButton speedButton;
  private Label tooltip;
  private Label badgeLabel;

  @Override
  public void create() {
    super.create();
    addActors();
    updateSpeedFromTimeSource();
  }

  private void addActors() {
    // Create speed button
    Texture speedTex =
        ServiceLocator.getGlobalResourceService().getAsset(speedImages[speedIndex], Texture.class);
    speedButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(speedTex)));
    speedButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
    speedButton.setOrigin(BUTTON_SIZE / 2f, BUTTON_SIZE / 2f);

    // Position near top-right, to the left of PauseButton
    float x = stage.getWidth() - BUTTON_SIZE - 20f - (BUTTON_SIZE + 12f);
    float y = stage.getHeight() - BUTTON_SIZE - 20f;
    speedButton.setPosition(x, y);
    speedButton.setVisible(true);
    stage.addActor(speedButton);

    // Add click listener
    speedButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            cycleSpeed();
          }
        });

    createTooltip();
    createBadge();
    addHoverEffect();
  }

  private void cycleSpeed() {
    speedIndex = (speedIndex + 1) % speeds.length;
    float newScale = speeds[speedIndex];
    logger.info("[SpeedControl] Setting time scale to {}x", newScale);
    ServiceLocator.getTimeSource().setTimeScale(newScale);
    updateButtonTexture();
    badgeLabel.setText(formatSpeedLabel(newScale));
    updatePosition();
    // Optional: broadcast event for other interested UI/components
    entity.getEvents().trigger("speed_changed");
  }

  private void updateSpeedFromTimeSource() {
    float ts =
        ServiceLocator.getTimeSource().getDeltaTime()
            / ServiceLocator.getTimeSource().getRawDeltaTime();
    // map ts to nearest of speeds
    float nearest = speeds[0];
    int nearestIdx = 0;
    for (int i = 0; i < speeds.length; i++) {
      if (Math.abs(speeds[i] - ts) < Math.abs(nearest - ts)) {
        nearest = speeds[i];
        nearestIdx = i;
      }
    }
    speedIndex = nearestIdx;
    if (badgeLabel != null) {
      badgeLabel.setText(formatSpeedLabel(nearest));
      updateButtonTexture();
      updatePosition();
    }
  }

  /** Creates the tooltip for the speed button */
  private void createTooltip() {
    tooltip = new Label("Speed", skin);
    whiten(tooltip);
    tooltip.setFontScale(0.8f);

    // Position tooltip below the button
    float x = stage.getWidth() - BUTTON_SIZE - 20f - (BUTTON_SIZE + 12f);
    float y = stage.getHeight() - BUTTON_SIZE - 20f;
    tooltip.setPosition(x + (BUTTON_SIZE - tooltip.getPrefWidth()) / 2f, y - 20f);
    tooltip.setVisible(false);
    tooltip.setZIndex(60);
    stage.addActor(tooltip);
  }

  /** Creates the speed badge label */
  private void createBadge() {
    badgeLabel = new Label(formatSpeedLabel(speeds[speedIndex]), skin);
    whiten(badgeLabel);
    badgeLabel.setFontScale(0.8f);

    float x = stage.getWidth() - BUTTON_SIZE - 20f - (BUTTON_SIZE + 12f);
    float y = stage.getHeight() - BUTTON_SIZE - 20f;
    badgeLabel.setPosition(
        x + (BUTTON_SIZE - badgeLabel.getPrefWidth()) / 2f, y + BUTTON_SIZE - 2f);
    badgeLabel.setZIndex(60);
    stage.addActor(badgeLabel);
  }

  /** Adds hover effect to the speed button */
  private void addHoverEffect() {
    speedButton.addListener(
        new InputListener() {
          @Override
          public void enter(
              InputEvent event,
              float x,
              float y,
              int pointer,
              com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
            if (tooltip != null) {
              tooltip.setVisible(true);
            }
          }

          @Override
          public void exit(
              InputEvent event,
              float x,
              float y,
              int pointer,
              com.badlogic.gdx.scenes.scene2d.Actor toActor) {
            if (tooltip != null) {
              tooltip.setVisible(false);
            }
          }
        });
  }

  /** Updates the button texture to match current speed */
  private void updateButtonTexture() {
    if (speedButton != null) {
      Texture speedTex =
          ServiceLocator.getGlobalResourceService()
              .getAsset(speedImages[speedIndex], Texture.class);
      speedButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(speedTex));
    }
  }

  /**
   * Sets the label's font color to white
   *
   * @param label The label to set the font color of
   */
  private void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = com.badlogic.gdx.graphics.Color.WHITE;
    label.setStyle(st);
  }

  private String formatSpeedLabel(float scale) {
    if (Math.abs(scale - Math.round(scale)) < 0.001f) {
      return Math.round(scale) + "x";
    }
    return String.format("%.1fx", scale);
  }

  @Override
  public void update() {
    super.update();
    updatePosition();
  }

  /** Updates button, tooltip and badge position when window is resized */
  private void updatePosition() {
    if (speedButton != null) {
      float x = stage.getWidth() - BUTTON_SIZE - 20f - (BUTTON_SIZE + 12f);
      float y = stage.getHeight() - BUTTON_SIZE - 20f;
      speedButton.setPosition(x, y);

      // Update tooltip position relative to button
      if (tooltip != null) {
        tooltip.setPosition(x + (BUTTON_SIZE - tooltip.getPrefWidth()) / 2f, y - 20f);
      }

      // Update badge position relative to button
      if (badgeLabel != null) {
        badgeLabel.setPosition(
            x + (BUTTON_SIZE - badgeLabel.getPrefWidth()) / 2f, y + BUTTON_SIZE - 2f);
      }
    }
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Draw handled by stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    if (speedButton != null) {
      speedButton.remove();
      speedButton = null;
    }
    if (tooltip != null) {
      tooltip.remove();
      tooltip = null;
    }
    if (badgeLabel != null) {
      badgeLabel.remove();
      badgeLabel = null;
    }
    super.dispose();
  }
}
