package com.csse3200.game.components.gameover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.csse3200.game.GdxGame;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.services.DialogService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/**
 * Class to create and display a window when the game ends. This should probably be changed for a
 * custom dialog.
 */
public class GameOverWindow extends UIComponent {
  private String levelKey;

  // Initialises the game over window.
  private Window window;
  // Tracks the display status of the window.
  boolean isDisplayed = false;

  /** Creates the game over window. */
  @Override
  public void create() {
    super.create();

    // Listens for game over event
    entity.getEvents().addListener("gameOver", this::onGameOver);

    // Creates popup display.
    window = new Window("Game over.", skin);
    window.setMovable(false);
    window.setSize(500, 500);
    window.setPosition(
        (Gdx.graphics.getWidth() - window.getWidth()) / 2f,
        (Gdx.graphics.getHeight() - window.getHeight()) / 2f);

    // Adds text in the popup display.
    Label message = new Label("Game over.\n Press E to go back to main menu.", skin);
    window.add(message).pad(10).row();

    // Sets popup display to false when created.
    window.setVisible(false);
    stage.addActor(window);
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
      window.setVisible(false);
      isDisplayed = false;

      // Updates next frame to return to the main menu without crashing.
      Gdx.app.postRunnable(
          new Runnable() {
            @Override
            public void run() {
              // Gets the game.
              GdxGame game = (GdxGame) Gdx.app.getApplicationListener();
              // Switches to main menu.
              game.setScreen(GdxGame.ScreenType.WORLD_MAP);
            }
          });
    }
  }

  /** Activates the popup display when game over event is listened for. */
  private void onGameOver() {
    DialogService dialogService = ServiceLocator.getDialogService();

    displayNewEntity(dialogService);
    window.setVisible(true);
    isDisplayed = true;
  }

  private void displayNewEntity(DialogService dialogService) {
    String unlockedDefences = unlockEntity();
    dialogService.info(
        "Congratulations!",
        "You have unlocked a new entity: "
            + unlockedDefences
            + "\n Go to the dossier to check him out!");
  }

  private String unlockEntity() {
    Profile profile = ServiceLocator.getProfileService().getProfile();
    String unlockedDefences = "";
    for (String key : Arsenal.ALL_DEFENCES.keySet()) {
      if (Arsenal.ALL_DEFENCES.get(key) == levelKey && !profile.getArsenal().contains(key)) {
        profile.getArsenal().unlockDefence(key);
        unlockedDefences += key;
      }
    }
    return unlockedDefences;
  }

  /** Frees the memory. */
  @Override
  public void dispose() {
    if (window != null) {
      window.remove();
    }

    super.dispose();
  }

  /**
   * Draws a sprite batch.
   *
   * @param batch Batch to render to.
   */
  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }
}
