package com.csse3200.game.components.gameover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.csse3200.game.ui.UIComponent;

public class GameOverPopupDisplay extends UIComponent {
  private Window popupDisplay;
  boolean isDisplayed = false;

  // public GameOverPopupDisplay() {}

  /** Creates the game over window. */
  public void create() {
    super.create();

    // Listens for game over event
    entity.getEvents().addListener("gameOver", this::onGameOver);

    // Creates popup display.
    popupDisplay = new Window("Game over.", skin);
    popupDisplay.setMovable(false);
    popupDisplay.setSize(500, 500);
    popupDisplay.setPosition(
        (Gdx.graphics.getWidth() - popupDisplay.getWidth()) / 2f,
        (Gdx.graphics.getHeight() - popupDisplay.getHeight()) / 2f);

    // Adds text in the popup display.
    Label message = new Label("Game over.\n Press E to go back to main menu.", skin);
    popupDisplay.add(message).pad(10).row();

    // Sets popup display to false when created.
    popupDisplay.setVisible(false);
    stage.addActor(popupDisplay);
  }

  /** Checks the status of the popup display */
  @Override
  public void update() {

    // Checks if popup display has been activated.
    if (!isDisplayed) {
      return;
    }

    // Press 'E' to take you back to the main menu.
    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      popupDisplay.setVisible(false);
      isDisplayed = false;
      // return to main menu
    }
  }

  /** Activates the popup display when game over event is listened for. */
  private void onGameOver() {
    popupDisplay.setVisible(true);
    isDisplayed = true;
  }

  /** Frees the memory. */
  @Override
  public void dispose() {
    if (popupDisplay != null) {
      popupDisplay.remove();
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
