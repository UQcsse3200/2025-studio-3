package com.csse3200.game.components.hud;

import com.badlogic.gdx.graphics.Texture;
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

/** A pause button component that displays a pause icon. When clicked, it triggers a pause event. */
public class PauseButton extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(PauseButton.class);
  private static final float Z_INDEX = 40f;
  private static final int BUTTON_SIZE = 60;
  private ImageButton pauseButtonComponent;
  private Label pauseTooltip;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Adds the pause button to the stage */
  private void addActors() {
    // Create pause button
    Texture pauseTexture =
        ServiceLocator.getGlobalResourceService().getAsset("images/pause-icon.png", Texture.class);
    pauseButtonComponent =
        new ImageButton(new TextureRegionDrawable(new TextureRegion(pauseTexture)));
    pauseButtonComponent.setSize(BUTTON_SIZE, BUTTON_SIZE);
    pauseButtonComponent.setOrigin(BUTTON_SIZE / 2f, BUTTON_SIZE / 2f);

    // Position in top right of screen
    float x = stage.getWidth() - BUTTON_SIZE - 20f;  // 20f padding from right
    float y = stage.getHeight() - BUTTON_SIZE - 20f;  // 20f padding from top
    pauseButtonComponent.setPosition(x, y);
    pauseButtonComponent.setVisible(true);
    stage.addActor(pauseButtonComponent);

    // Add click listener
    pauseButtonComponent.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("Pause button clicked");
            entity.getEvents().trigger("pause_game");
          }
        });

    createTooltip();
    addHoverEffect();
  }

  /** Creates the tooltip for the pause button */
  private void createTooltip() {
    pauseTooltip = new Label("Pause", skin);
    whiten(pauseTooltip);
    pauseTooltip.setFontScale(0.8f);

    // Position tooltip below the button
    float x = stage.getWidth() - BUTTON_SIZE - 20f;  // 20f padding from right
    float y = stage.getHeight() - BUTTON_SIZE - 20f;  // 20f padding from top
    pauseTooltip.setPosition(x + (BUTTON_SIZE - pauseTooltip.getPrefWidth()) / 2f, y - 20f);
    pauseTooltip.setVisible(false);
    pauseTooltip.setZIndex(60);
    stage.addActor(pauseTooltip);
  }

  /** Adds hover effect to the pause button */
  private void addHoverEffect() {
    pauseButtonComponent.addListener(
        new InputListener() {
          @Override
          public void enter(
              InputEvent event,
              float x,
              float y,
              int pointer,
              com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
            if (pauseTooltip != null) {
              pauseTooltip.setVisible(true);
            }
          }

          @Override
          public void exit(
              InputEvent event,
              float x,
              float y,
              int pointer,
              com.badlogic.gdx.scenes.scene2d.Actor toActor) {
            if (pauseTooltip != null) {
              pauseTooltip.setVisible(false);
            }
          }
        });
  }

  @Override
  public void update() {
    super.update();
    updatePosition();
  }

  /** Updates button and tooltip position when window is resized */
  private void updatePosition() {
    if (pauseButtonComponent != null) {
      float x = stage.getWidth() - BUTTON_SIZE - 20f;  // 20f padding from right
      float y = stage.getHeight() - BUTTON_SIZE - 20f;  // 20f padding from top
      pauseButtonComponent.setPosition(x, y);

      // Update tooltip position relative to button
      if (pauseTooltip != null) {
        pauseTooltip.setPosition(x + (BUTTON_SIZE - pauseTooltip.getPrefWidth()) / 2f, y - 20f);
      }
    }
  }

  @Override
  public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
    // Draw is handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
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

  /**
   * Moves the pause button and tooltip behind the dimmed background when paused
   *
   * @param paused Whether the game is paused
   */
  public void setPaused(boolean paused) {
    if (paused) {
      pauseButtonComponent.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
      pauseButtonComponent.getColor().a = 0.5f; // 50% opacity
      if (pauseTooltip != null) {
        pauseTooltip.setZIndex(40);
        pauseTooltip.setVisible(false); // Hide tooltip when paused
      }
    } else {
      pauseButtonComponent.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
      pauseButtonComponent.getColor().a = 1.0f; // 100% opacity
      if (pauseTooltip != null) {
        pauseTooltip.setZIndex(60); // Show tooltip when not paused
      }
    }
  }

  @Override
  public void dispose() {
    if (pauseButtonComponent != null) {
      pauseButtonComponent.remove();
      pauseButtonComponent = null;
    }
    if (pauseTooltip != null) {
      pauseTooltip.remove();
      pauseTooltip = null;
    }
    super.dispose();
  }
}
