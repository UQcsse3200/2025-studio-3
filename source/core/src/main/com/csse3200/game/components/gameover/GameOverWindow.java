package com.csse3200.game.components.gameover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.csse3200.game.GdxGame;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/**
 * Class to create and display a window when the game ends. This should probably be changed for a
 * custom dialog.
 */
public class GameOverWindow extends UIComponent {
  // Initialises the game over window.
  private Table container;

  // Tracks the display status of the window.
  boolean isDisplayed = false;

  /** Creates the game over window. */
  @Override
  public void create() {
    super.create();

    // Listens for game over event
    entity.getEvents().addListener("gameOver", this::onGameOver);

    container = new Table();
    container.setFillParent(true);
    container.center();

    Label gameOverHeading = ui.heading("Game Over!");
    Label message = ui.text("You have failed to complete the level");

    TextButton mainMenuButton = ui.primaryButton("World Map", 250);
    mainMenuButton.addListener(
        event -> {
          if (!event.toString().equals("touchDown")) {
            return false;
          }
          navigateTo(GdxGame.ScreenType.WORLD_MAP);
          return true;
        });

    TextButton quitButton = ui.primaryButton("Quit Game", 250);
    quitButton.addListener(
        event -> {
          if (!event.toString().equals("touchDown")) {
            return false;
          }
          Gdx.app.exit();
          return true;
        });

    container.add(gameOverHeading).pad(20f).row();
    container.add(message).pad(10f).row();
    container.add(mainMenuButton).pad(8f).row();
    container.add(quitButton).pad(8f).row();
    container.setVisible(false);
    stage.addActor(container);
  }

  /** Checks the status of the popup display */
  @Override
  public void update() {

    // Checks if popup display has been activated.
    if (!isDisplayed) {
      return;
    }

    // Press 'E' to take the player back to the main menu.
    int interactKey = ServiceLocator.getSettingsService().getSettings().getInteractionButton();
    if (Gdx.input.isKeyJustPressed(interactKey)) {
      navigateTo(GdxGame.ScreenType.WORLD_MAP);
    }
  }

  /** Activates the popup display when game over event is listened for. */
  private void onGameOver() {
    container.setVisible(true);
    isDisplayed = true;
  }

  private void navigateTo(GdxGame.ScreenType target) {
    container.setVisible(false);
    isDisplayed = false;
    Gdx.app.postRunnable(
        () -> {
          GdxGame game = (GdxGame) Gdx.app.getApplicationListener();
          game.setScreen(target);
        });
  }
  
  /** Frees the memory. */
  @Override
  public void dispose() {
    if (container != null) {
      container.remove();
    }

    super.dispose();
  }
}
