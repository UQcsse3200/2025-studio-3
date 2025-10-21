package com.csse3200.game.components.lvlcompleted;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.csse3200.game.GdxGame;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.utils.LevelType;

/** Class to create and display a window when the level is completed. */
public class LevelCompletedWindow extends UIComponent {
  private Table container;
  private boolean isDisplayed = false;
  private final ProfileService profileService = ServiceLocator.getProfileService();

  /** Creates the level completed window and sets up event listening for level completion. */
  @Override
  public void create() {
    super.create();

    // Listen for level complete event
    entity.getEvents().addListener("levelComplete", this::onLevelCompleted);

    // Create container table
    container = new Table();
    container.setFillParent(true);
    container.center();

    Label levelCompletedHeading = ui.heading("Level Completed!");
    Label message = new Label("Congratulations!", skin);

    TextButton mainMenuButton = ui.primaryButton("World Map", 250);
    mainMenuButton.addListener(
        event -> {
          if (!event.toString().equals("touchDown")) {
            return false;
          }
          navigateTo();
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

    container.add(levelCompletedHeading).pad(20f).row();
    container.add(message).pad(10f).row();
    container.add(mainMenuButton).pad(8f).row();
    container.add(quitButton).pad(8f).row();

    container.setVisible(false);
    stage.addActor(container);
  }

  /**
   * Checks the status of the level completed window and handles input to close it and return to the
   * main menu.
   */
  @Override
  public void update() {
    if (!isDisplayed) {
      return;
    }

    // Check for interact key press to navigate to the world map
    int interactKey = ServiceLocator.getSettingsService().getSettings().getInteractionButton();
    if (Gdx.input.isKeyJustPressed(interactKey)) {
      navigateTo();
    }
  }

  private void navigateTo() {
    container.setVisible(false);
    isDisplayed = false;
    updateLevel(); // Update the current level before changing screens
    Gdx.app.postRunnable(
        () -> {
          GdxGame game = (GdxGame) Gdx.app.getApplicationListener();
          game.setScreen(GdxGame.ScreenType.WORLD_MAP);
        });
  }

  /** Displays the level completed window when the level is completed. */
  private void onLevelCompleted() {
    container.setVisible(true);
    isDisplayed = true;
  }

  /** Disposes of the container when the component is disposed. */
  @Override
  public void dispose() {
    if (container != null) {
      container.remove();
    }
    super.dispose();
  }

  /**
   * Called when level is completed, before changing back to world map screen and updates the
   * profile's current level to the following level.
   */
  public void updateLevel() {
    String currentLevel = profileService.getProfile().getCurrentLevel();
    updateStatistics(currentLevel);
    
    // Mark the current level as completed
    profileService.getProfile().completeLevel(currentLevel);
    
    // Find and unlock the next level
    String nextLevel = findNextLevel(currentLevel);
    profileService.getProfile().unlockNode(nextLevel);
    profileService.getProfile().setCurrentLevel(nextLevel);
  }

  private String findNextLevel(String currentLevel) {
    return switch (currentLevel) {
      case "levelOne" -> "levelTwo";
      case "levelTwo" -> "levelThree";
      case "levelThree" -> "levelFour";
      case "levelFour" -> "levelFive";
      default -> "end";
    };
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // Stage handles drawing
  }

  public Table getContainer() {
    return container;
  }

  private void updateStatistics(String currentLevel) {
    Statistics statistics = profileService.getProfile().getStatistics();
    statistics.incrementStatistic("levelsCompleted");

    if (currentLevel.equals(LevelType.LEVEL_THREE.toKey())) {
      statistics.incrementStatistic("slotMachineCompleted");
    }
  }
}
