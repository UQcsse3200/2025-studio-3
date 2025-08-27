package com.csse3200.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.csse3200.game.Achievements.Achievement;
import com.csse3200.game.Achievements.AchievementManager;
import com.csse3200.game.files.UserSettings;
import com.csse3200.game.screens.AchievementsScreen;
import com.csse3200.game.screens.MainGameScreen;
import com.csse3200.game.screens.MainMenuScreen;
import com.csse3200.game.screens.SettingsScreen;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.badlogic.gdx.Gdx.app;

/**
 * Entry point of the non-platform-specific game logic. Controls which screen is currently running.
 * The current screen triggers transitions to other screens. This works similarly to a finite state
 * machine (See the State Pattern).
 */
public class GdxGame extends Game {
  private static final Logger logger = LoggerFactory.getLogger(GdxGame.class);

  @Override
  public void create() {
    logger.info("Creating game");
    loadSettings();

    // Sets background to light yellow
    Gdx.gl.glClearColor(248f/255f, 249/255f, 178/255f, 1);

    //Initialize Achievements
    initAchievements();

    setScreen(ScreenType.MAIN_MENU);
  }

  private void initAchievements() {
    /*com.csse3200.game.Achievements.AchievementManager achievementManager =
            new com.csse3200.game.Achievements.AchievementManager();

    //register with ServiceLocator if you want global access
    com.csse3200.game.services.ServiceLocator.registerAchievementManager(achievementManager);

    // Example achievements (you’ll expand later)
    achievementManager.addAchievement(
            new com.csse3200.game.Achievements.Achievement("First move", "Made your first move", 25));
    achievementManager.addAchievement(
            new Achievement("5Secin", "loaded into the game for 5sec", 30));

*/
    AchievementManager achievementManager = new AchievementManager();
    ServiceLocator.registerAchievementManager(achievementManager);

    // Example static achievements
    achievementManager.addAchievement(
            new Achievement("First move", "Made your first move", 25)
    );
    achievementManager.addAchievement(
            new Achievement("5Secin", "Loaded into the game for 5 seconds", 30)
    );
    achievementManager.addAchievement(
            new Achievement("Explorer", "Opened the achievements menu", 10)
    );
    achievementManager.addAchievement(
            new Achievement("Persistent", "Played the game 5 times", 50)
    );
    achievementManager.addAchievement(
            new Achievement("Completionist", "Unlocked all achievements", 100)
    );

    System.out.println("DEBUG: Registered achievements count = "
            + ServiceLocator.getAchievementManager().getAllAchievements().size());

  }
  /**
   * Loads the game's settings.
   */
  private void loadSettings() {
    logger.debug("Loading game settings");
    UserSettings.Settings settings = UserSettings.get();
    UserSettings.applySettings(settings);
  }

  /**
   * Sets the game's screen to a new screen of the provided type.
   * @param screenType screen type
   */
  public void setScreen(ScreenType screenType) {
    logger.info("Setting game screen to {}", screenType);
    Screen currentScreen = getScreen();
    if (currentScreen != null) {
      currentScreen.dispose();
    }
    setScreen(newScreen(screenType));
  }

  @Override
  public void dispose() {
    logger.debug("Disposing of current screen");
    getScreen().dispose();
  }

  /**
   * Create a new screen of the provided type.
   * @param screenType screen type
   * @return new screen
   */
  private Screen newScreen(ScreenType screenType) {
    switch (screenType) {
      case MAIN_MENU:
        return new MainMenuScreen(this);
      case MAIN_GAME:
        return new MainGameScreen(this);
      case SETTINGS:
        return new SettingsScreen(this);
      case ACHIEVEMENTS:
        return new AchievementsScreen(this);
      default:
        return null;
    }
  }

  public enum ScreenType {
    MAIN_MENU, MAIN_GAME, SETTINGS, ACHIEVEMENTS
  }

  /**
   * Exit the game.
   */
  public void exit() {
    app.exit();
  }
}
