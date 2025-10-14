package com.csse3200.game.components.lvlcompleted;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.csse3200.game.GdxGame;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.services.DialogService;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

import java.util.ArrayList;
import java.util.List;

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

    String interactKeyName =
        Input.Keys.toString(
            ServiceLocator.getSettingsService().getSettings().getInteractionButton());
    Label message =
        new Label(
            "Congratulations!\nPress " + interactKeyName + " to return to the main menu.", skin);
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
    int interactKey = ServiceLocator.getSettingsService().getSettings().getInteractionButton();
    if (Gdx.input.isKeyJustPressed(interactKey)) {
      window.setVisible(false);
      isDisplayed = false;
      // Update the current level before changing screens
      updateLevel();
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
    DialogService dialogService = ServiceLocator.getDialogService();
    displayNewEntity(dialogService);

    window.setVisible(true);
    isDisplayed = true;
  }

  /**
   * Called when level is completed, before changing back to world map screen and update's the
   * profile's current level to the following level.
   */
  public void updateLevel() {
    String currentLevel = profileService.getProfile().getCurrentLevel();
    String nextLevel = findNextLevel(currentLevel);
    profileService.getProfile().setCurrentLevel(nextLevel);
  }

  private String findNextLevel(String currentLevel) {
    String nextLevel =
        switch (currentLevel) {
          case "levelOne" -> "levelTwo";
          case "levelTwo" -> "levelThree";
          case "levelThree" -> "levelFour";
          case "levelFour" -> "levelFive";
          default -> "end";
        };
    return nextLevel;
  }

  private void displayNewEntity(DialogService dialogService) {
    String unlockedDefences = unlockEntity();
    dialogService.info(
        "Congratulations!",
        "You have unlocked the: \n"
            + unlockedDefences
            + "\n Go to the dossier to check them out!");
  }

  private String unlockEntity() {
    Profile profile = ServiceLocator.getProfileService().getProfile();
    List<String> unlockedDefences = new ArrayList<>();
    for (String key : Arsenal.ALL_DEFENCES.keySet()) {
      if (Arsenal.ALL_DEFENCES.get(key).equals(findNextLevel(profile.getCurrentLevel()))
          && !profile.getArsenal().contains(key)) {
        profile.getArsenal().unlockDefence(key);
        unlockedDefences.add(key);
      }
    }
    return String.join(" and ", unlockedDefences);
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

  public Window getWindow() {
    return window;
  }
}
