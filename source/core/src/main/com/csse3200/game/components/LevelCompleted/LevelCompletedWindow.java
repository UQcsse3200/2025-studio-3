package com.csse3200.game.components.LevelCompleted;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.csse3200.game.GdxGame;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/** Class to create and display a window when the level is completed. */
public class LevelCompletedWindow extends UIComponent {
  private Window window;
  private boolean isDisplayed = false;
  private final ProfileService profileService = ServiceLocator.getProfileService();

  /** Creates the level completed window and sets up event listening for level completion. */
  @Override
  public void create() {
    super.create();

    // Listen for level complete event
    entity.getEvents().addListener("levelComplete", this::onLevelCompleted);

    // Create popup window
    window = new Window("Level Completed!", skin);
    window.setMovable(false);
    window.setSize(500, 500);
    window.setPosition(
        (Gdx.graphics.getWidth() - window.getWidth()) / 2f,
        (Gdx.graphics.getHeight() - window.getHeight()) / 2f);

    Label message = new Label("Congratulations!\nPress E to return to the main menu.", skin);
    window.add(message).pad(10).row();

    window.setVisible(false);
    stage.addActor(window);
  }

  /**
   * Checks the status of the level completed window and handles input to close it and return to the
   * main menu.
   */
  @Override
  public void update() {
    if (!isDisplayed) return;

    // Check for 'E' key press to close window and return to main menu
    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      window.setVisible(false);
      isDisplayed = false;
      // Unlock the next level before changing screens
      String currentLevel = profileService.getProfile().getCurrentLevel();
      String nextLevel = getNextLevel(currentLevel);
      profileService.getProfile().setCurrentLevel(nextLevel);
      // Return to main menu (world map) safely
      Gdx.app.postRunnable(
          () -> {
            GdxGame game = (GdxGame) Gdx.app.getApplicationListener();
            game.setScreen(GdxGame.ScreenType.WORLD_MAP);
          });
    }
  }

  /** Displays the level completed window when the level is completed. */
  private void onLevelCompleted() {
    window.setVisible(true);
    isDisplayed = true;
  }

  /** Disposes of the window when the component is disposed. */
  @Override
  public void dispose() {
    if (window != null) {
      window.remove();
    }
    super.dispose();
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // Stage handles drawing
  }

  protected String getNextLevel(String level) {
    return switch (level) {
      case "levelOne" -> "levelTwo";
      case "levelTwo" -> "levelThree";
      case "levelThree" -> "levelFour";
      case "levelFour" -> "levelFive";
      default -> "error";
    };
  }

  public Window getWindow() {
    return window;
  }
}
