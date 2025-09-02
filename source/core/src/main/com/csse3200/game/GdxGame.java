package com.csse3200.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.csse3200.game.Achievements.Achievement;
import com.csse3200.game.Achievements.AchievementManager;
import com.csse3200.game.Achievements.*;
import com.csse3200.game.persistence.UserSettings;
import com.csse3200.game.screens.LoadGameScreen;
import com.csse3200.game.screens.AchievementsScreen;
import com.csse3200.game.screens.MainGameScreen;
import com.csse3200.game.screens.MainMenuScreen;
import com.csse3200.game.screens.SettingsScreen;
import com.csse3200.game.screens.SkillTreeScreen;
import com.csse3200.game.screens.StatisticsScreen;
import com.csse3200.game.screens.ProfileScreen;
import com.csse3200.game.screens.LoadGameScreen;

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
    //AchievementManager achievementManager = new AchievementManager();
    ServiceLocator.registerAchievementManager(new AchievementManager());

/*
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
    achievementManager.addAchievement(
            new Achievement("Sharpshooter", "Hit 10 perfect moves in a row", 40)
    );
    achievementManager.addAchievement(
            new Achievement("Speedrunner", "Finished a level in under 30 seconds", 75)
    );
    achievementManager.addAchievement(
            new Achievement("Collector", "Collected 100 items total", 20)
    );
    achievementManager.addAchievement(
            new Achievement("Unstoppable", "Survived for 10 minutes without failing", 60)
    );
    achievementManager.addAchievement(
            new Achievement("Night Owl", "Played the game after midnight", 15)
    );
    achievementManager.addAchievement(
            new Achievement("Early Bird", "Played the game before 7am", 15)
    );
    achievementManager.addAchievement(
            new Achievement("Casual Gamer", "Played for 1 hour total", 25)
    );
    achievementManager.addAchievement(
            new Achievement("Marathoner", "Played for 10 hours total", 100)
    );
    achievementManager.addAchievement(
            new Achievement("Risk Taker", "Triggered a near-fail but survived", 35)
    );
    achievementManager.addAchievement(
            new Achievement("Tactician", "Used 5 different strategies in one game", 45)
    );
    achievementManager.addAchievement(
            new Achievement("The Comeback", "Recovered from near defeat to win", 80)
    );
    achievementManager.addAchievement(
            new Achievement("Legend", "Achieved the highest possible score", 200)
    );

    Achievement sharpshooter = new Achievement("Tactician", "Hit 10 perfect moves in a row", 40);

    achievementManager.addAchievement(sharpshooter);

//Unlock 1 to test
    sharpshooter.unlock();

 */

    ServiceLocator.getAchievementManager().unlock("A1");



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
      case SKILLTREE:
        return new SkillTreeScreen(this);
      case PROFILE:
        return new ProfileScreen(this);
      case LOAD_GAME:
        return new LoadGameScreen(this);
      case STATISTICS:
         return new StatisticsScreen(this);
      case ACHIEVEMENTS:
        return new AchievementsScreen(this);
      default:
        return null;
    }
  }

  public enum ScreenType {
    MAIN_MENU, MAIN_GAME, SETTINGS, SKILLTREE, PROFILE, LOAD_GAME, STATISTICS, ACHIEVEMENTS
  }

  /**
   * Exit the game.
   */
  public void exit() {
    app.exit();
  }
}
