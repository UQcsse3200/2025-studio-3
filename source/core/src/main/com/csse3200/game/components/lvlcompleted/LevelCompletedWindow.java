package com.csse3200.game.components.lvlcompleted;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
    window.setModal(true);
    window.setSize(500, 500);
    window.setPosition(
        (Gdx.graphics.getWidth() - window.getWidth()) / 2f,
        (Gdx.graphics.getHeight() - window.getHeight()) / 2f);

    String interactKeyName =
        Input.Keys.toString(
            ServiceLocator.getSettingsService().getSettings().getInteractionButton());
    Label message =
        new Label(
            "Congratulations!\nPress " + interactKeyName + " to return to the main menu.", skin);
    window.add(message).pad(10).row();

    // Capture keyboard 'interact' while the window is visible (more reliable than global polling)
    window.addListener(
        new InputListener() {
          @Override
          public boolean keyDown(InputEvent event, int keycode) {
            int interactKey =
                ServiceLocator.getSettingsService().getSettings().getInteractionButton();
            if (keycode == interactKey && isDisplayed) {
              returnToWorldMap();
              return true;
            }
            return false;
          }
        });

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
    int interactKey = ServiceLocator.getSettingsService().getSettings().getInteractionButton();
    if (Gdx.input.isKeyJustPressed(interactKey)) {
      returnToWorldMap();
    }
  }

  /** Displays the level completed window when the level is completed. */
  private void onLevelCompleted() {
    window.setVisible(true);
    isDisplayed = true;
    if (stage != null) {
      stage.setKeyboardFocus(window);
    }
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

  /** Return to world map screen safely. */
  private void returnToWorldMap() {
    window.setVisible(false);
    isDisplayed = false;
    Gdx.app.postRunnable(
        () -> {
          GdxGame game = (GdxGame) Gdx.app.getApplicationListener();
          game.setScreen(GdxGame.ScreenType.WORLD_MAP);
        });
  }

  public Window getWindow() {
    return window;
  }
}
