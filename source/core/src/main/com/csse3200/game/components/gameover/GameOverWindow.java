package com.csse3200.game.components.gameover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.csse3200.game.GdxGame;
import com.csse3200.game.ui.UIComponent;

/** Class to create and display a window when the game ends. */
public class GameOverWindow extends UIComponent {
  // Initialises the game over window.
  private Window gameOverWindow;
  // Tracks the display status of the window.
  boolean isDisplayed = false;

  /** Creates the game over window. */
  public void create() {
    super.create();

    // Listens for game over event
    entity.getEvents().addListener("gameOver", this::onGameOver);

    // Creates popup display.
    gameOverWindow = new Window("Game over.", skin);
    gameOverWindow.setMovable(false);
    gameOverWindow.setSize(500, 500);
    gameOverWindow.setPosition(
        (Gdx.graphics.getWidth() - gameOverWindow.getWidth()) / 2f,
        (Gdx.graphics.getHeight() - gameOverWindow.getHeight()) / 2f);

    // Adds text in the popup display.
    Label message = new Label("Game over.\n Press E to go back to main menu.", skin);
    gameOverWindow.add(message).pad(10).row();

    // Sets popup display to false when created.
    gameOverWindow.setVisible(false);
    stage.addActor(gameOverWindow);
  }

  /** Checks the status of the popup display */
  @Override
  public void update() {

    // Checks if popup display has been activated.
    if (!isDisplayed) {
      return;
    }

    // Press 'E' to take the player back to the main menu.
    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      // Closes popup window
      gameOverWindow.setVisible(false);
      isDisplayed = false;

      // Updates next frame to return to the main menu without crashing.
      Gdx.app.postRunnable(
          new Runnable() {
            @Override
            public void run() {
              // Gets the game.
              GdxGame game = (GdxGame) Gdx.app.getApplicationListener();
              // Switches to main menu.
              game.setScreen(GdxGame.ScreenType.MAIN_MENU);
            }
          });
    }
  }

  /** Activates the popup display when game over event is listened for. */
  private void onGameOver() {
    gameOverWindow.setVisible(true);
    isDisplayed = true;
  }

  /** Frees the memory. */
  @Override
  public void dispose() {
    if (gameOverWindow != null) {
      gameOverWindow.remove();
    }

    super.dispose();
  }

  /**
   * Draws a sprite batch.
   *
   * @param batch Batch to render to.
   */
  @Override
  protected void draw(SpriteBatch batch) {}
}
