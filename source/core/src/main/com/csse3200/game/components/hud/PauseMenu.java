package com.csse3200.game.components.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
  private TextButton settingsButton;
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

    // Check if the game is already paused (e.g., returning from settings)
    if (entity.getComponent(com.csse3200.game.components.maingame.MainGameActions.class) != null)
    {
      // Check if the main game screen is paused
      com.csse3200.game.screens.MainGameScreen mainGameScreen =
          entity
              .getComponent(com.csse3200.game.components.maingame.MainGameActions.class)
              .getMainGameScreen();
      if (mainGameScreen != null && mainGameScreen.isPaused()) {
        show();
      }
    }
  }

  /** Creates the dimmed background overlay */
  private void createDimBackground() {
    // Create a solid color texture for the dim background
    com.badlogic.gdx.graphics.Pixmap pixmap =
        new com.badlogic.gdx.graphics.Pixmap(
            1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
    pixmap.setColor(0, 0, 0, DIM_ALPHA);
    pixmap.fill();
    Texture dimTexture = new Texture(pixmap);
    pixmap.dispose();

    // Create the dimmed background
    dimBackground = new Image(new TextureRegionDrawable(new TextureRegion(dimTexture)));
    dimBackground.setFillParent(true);
    dimBackground.setVisible(false);
    dimBackground.setZIndex(45);
    dimBackground.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);

    // Add click listener to prevent clicks from going through
    dimBackground.addListener(
        new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
          @Override
          public boolean touchDown(
              com.badlogic.gdx.scenes.scene2d.InputEvent event,
              float x,
              float y,
              int pointer,
              int button) {
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
    Label titleLabel = new Label("Game Paused", skin);
    titleLabel.setFontScale(2.0f);
    whiten(titleLabel);
    menuTable.add(titleLabel).padBottom(30f).row();

    // Create buttons
    createButtons();

    // Add buttons to table
    menuTable.add(resumeButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).padBottom(BUTTON_SPACING).row();
    menuTable.add(settingsButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).padBottom(BUTTON_SPACING).row();
    menuTable
        .add(quitLevelButton)
        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
        .padBottom(BUTTON_SPACING)
        .row();
    menuTable.add(mainMenuButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).padBottom(BUTTON_SPACING).row();
    menuTable.add(exitGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).row();
    menuTable.setVisible(false);
    menuTable.setZIndex(10); // Set Z-index to be above the dimmed background
    stage.addActor(menuTable);
  }

  /** Creates the menu buttons */
  private void createButtons() {
    // Resume button
    resumeButton = new TextButton("Resume", skin);
    resumeButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("Resume button clicked");
            entity.getEvents().trigger("resume_game");
          }
        });

    // Settings button
    settingsButton = new TextButton("Settings", skin);
    settingsButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("Settings button clicked");
            entity.getEvents().trigger("open_settings");
          }
        });

    // Quit Level button
    quitLevelButton = new TextButton("Quit Level", skin);
    quitLevelButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("Quit Level button clicked");
            entity.getEvents().trigger("quit_level");
          }
        });

    // Main Menu button
    mainMenuButton = new TextButton("Main Menu", skin);
    mainMenuButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("Main Menu button clicked");
            entity.getEvents().trigger("main_menu");
          }
        });

    // Exit Game button
    exitGameButton = new TextButton("Exit Game", skin);
    exitGameButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("Exit Game button clicked");
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

  /** Shows the pause menu with animation */
  public void show() {
    if (isVisible) return;

    isVisible = true;
    dimBackground.setVisible(true);
    menuTable.setVisible(true);

    // Fade in animation
    dimBackground.getColor().a = 0f;
    dimBackground.addAction(Actions.alpha(DIM_ALPHA, 0.3f));

    menuTable.getColor().a = 0f;
    menuTable.addAction(Actions.alpha(1f, 0.3f));

    logger.info("Pause menu shown");
  }

  /** Hides the pause menu with animation */
  public void hide() {
    if (!isVisible) return;
    isVisible = false;
    dimBackground.addAction(Actions.sequence(Actions.alpha(0f, 0.3f), Actions.visible(false)));
    menuTable.addAction(Actions.sequence(Actions.alpha(0f, 0.3f), Actions.visible(false)));
    logger.info("Pause menu hidden");
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
  public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
    // Draw is handled by the stage
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
