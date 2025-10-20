package com.csse3200.game.components.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A pause menu component that displays when the game is paused. Shows a dimmed background and menu
 * options.
 */
public class PauseMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(PauseMenu.class);
  private static final float Z_INDEX = 50f;
  private static final int BUTTON_WIDTH = 200;
  private static final int BUTTON_HEIGHT = 50;
  private static final int BUTTON_SPACING = 10;
  private static final float DIM_ALPHA = 0.7f;
  private Image dimBackground;
  private Table menuTable;
  private TextButton resumeButton;
  private TextButton quitLevelButton;
  private TextButton mainMenuButton;
  private TextButton exitGameButton;
  private boolean isVisible = false;

  @Override
  public void create() {
    super.create();
    createDimBackground();
    createMenuTable();
    setVisible(false);

    // Listen for pause events to show the menu
    entity.getEvents().addListener("pause", this::handlePause);
  }

  /** Creates the dimmed background overlay */
  private void createDimBackground() {
    // Create a solid color texture for the dim background
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(0, 0, 0, DIM_ALPHA);
    pixmap.fill();
    Texture dimTexture = new Texture(pixmap);
    pixmap.dispose();

    // Create the dimmed background
    dimBackground = new Image(new TextureRegionDrawable(new TextureRegion(dimTexture)));
    dimBackground.setFillParent(true);
    dimBackground.setVisible(false);
    dimBackground.setZIndex(45);
    dimBackground.setTouchable(Touchable.enabled);

    // Add click listener to prevent clicks from going through
    dimBackground.addListener(
        new ClickListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            // Consume the event to prevent it from reaching actors below
            return true;
          }
        });

    stage.addActor(dimBackground);
  }

  /** Creates the menu table with title and buttons */
  private void createMenuTable() {
    menuTable = new Table();
    menuTable.setFillParent(true);
    menuTable.center();

    // Create title
    Label titleLabel = ui.title("Game Paused");
    whiten(titleLabel);
    menuTable.add(titleLabel).padBottom(30f).row();

    // Create buttons
    createButtons();

    // Add buttons to table
    menuTable
        .add(resumeButton)
        .size(ui.getScaledWidth(BUTTON_WIDTH), ui.getScaledHeight(BUTTON_HEIGHT))
        .padBottom(BUTTON_SPACING)
        .row();
    menuTable
        .add(quitLevelButton)
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .padBottom(BUTTON_SPACING)
        .row();
    menuTable.add(mainMenuButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).padBottom(BUTTON_SPACING).row();
    menuTable.add(exitGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
    menuTable.setVisible(false);
    // Z-order handled in show() via toFront()
    stage.addActor(menuTable);
  }

  /** Creates the menu buttons */
  private void createButtons() {
    // Resume button
    resumeButton = ui.primaryButton("Resume", BUTTON_WIDTH);
    resumeButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("[PauseMenu] Resume button clicked");
            entity.getEvents().trigger("resume");
            setVisible(false);
          }
        });

    // Quit Level button
    quitLevelButton = ui.primaryButton("Quit Level", BUTTON_WIDTH);
    quitLevelButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("[PauseMenu] Quit Level button clicked");
            entity.getEvents().trigger("quit_level");
          }
        });

    // Main Menu button
    mainMenuButton = ui.primaryButton("Main Menu", BUTTON_WIDTH);
    mainMenuButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("[PauseMenu] Main Menu button clicked");
            entity.getEvents().trigger("open_main_menu");
          }
        });

    // Exit Game button
    exitGameButton = ui.primaryButton("Exit Game", BUTTON_WIDTH);
    exitGameButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("[PauseMenu] Exit Game button clicked");
            entity.getEvents().trigger("exit_game");
          }
        });
  }

  /**
   * Sets the label's font color to white
   *
   * @param label The label to set the font color of
   */
  private void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = Color.WHITE;
    label.setStyle(st);
  }

  private void bringPauseUiToFront() {
    if (dimBackground != null && dimBackground.hasParent()) {
      dimBackground.toFront();
    }
    if (menuTable != null && menuTable.hasParent()) {
      menuTable.toFront();
    }
  }

  /** Shows the pause menu with animation */
  public void show() {
    if (isVisible) return;

    isVisible = true;
    dimBackground.setVisible(true);
    menuTable.setVisible(true);
    bringPauseUiToFront();

    // Fade in animation
    dimBackground.getColor().a = 0f;
    dimBackground.addAction(Actions.alpha(DIM_ALPHA, 0.3f));

    menuTable.getColor().a = 0f;
    menuTable.addAction(Actions.alpha(1f, 0.3f));

    logger.info("[PauseMenu] Pause menu shown");
  }

  /** Hides the pause menu with animation */
  public void hide() {
    if (!isVisible) return;
    isVisible = false;
    dimBackground.addAction(Actions.sequence(Actions.alpha(0f, 0.3f), Actions.visible(false)));
    menuTable.addAction(Actions.sequence(Actions.alpha(0f, 0.3f), Actions.visible(false)));
    logger.info("[PauseMenu] Pause menu hidden");
  }

  /** Handles pause events to show the menu */
  private void handlePause() {
    logger.info("[PauseMenu] Pause event received");
    setVisible(true);
  }

  /**
   * Sets the visibility of the pause menu
   *
   * @param visible Whether the pause menu should be visible
   */
  public void setVisible(boolean visible) {
    if (visible) {
      show();
    } else {
      hide();
    }
  }

  /**
   * Returns whether the pause menu is currently visible
   *
   * @return Whether the pause menu is currently visible
   */
  public boolean isVisible() {
    return isVisible;
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    if (dimBackground != null) {
      dimBackground.remove();
      dimBackground = null;
    }
    if (menuTable != null) {
      menuTable.remove();
      menuTable = null;
    }
    super.dispose();
  }
}
