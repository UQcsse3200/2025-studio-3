package com.csse3200.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.csse3200.game.persistence.UserSettings;
import com.csse3200.game.data.MenuSpriteData;
import com.csse3200.game.screens.*;
import com.csse3200.game.services.MenuSpriteService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.screens.MainGameScreen;
import com.csse3200.game.screens.MainMenuScreen;
import com.csse3200.game.screens.SettingsScreen;
import com.csse3200.game.screens.WorldMapScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.badlogic.gdx.Gdx.app;

public class GdxGame extends Game {
    private static final Logger logger = LoggerFactory.getLogger(GdxGame.class);

    @Override
    public void create() {
        logger.info("Creating game");
        loadMenus();

    // Create MenuSpriteService
    ServiceLocator.registerMenuSpriteService(new MenuSpriteService());

    loadMenus();

    // Sets background to light yellow
    Gdx.gl.glClearColor(248f/255f, 249/255f, 178/255f, 1);
        // Sets background to light yellow
        Gdx.gl.glClearColor(248f/255f, 249f/255f, 178f/255f, 1);

        setScreen(ScreenType.MAIN_MENU);
    }

  /**
   * Runs the appropriate register function to register screen sprites.
   */
  public void loadMenus() {
    for (RegisteredScreens screenType : RegisteredScreens.values()) {
      if (!contains(ScreenType.values(), screenType.name())) {
        return;
      }
      ScreenType type = ScreenType.valueOf(screenType.name());
      Screen screen = newScreen(type);
      if (screen != null) {
        if (MenuSpriteScreen.class.isAssignableFrom(screen.getClass())) {
          MenuSpriteData menuSpriteData = new MenuSpriteData(type);
          ((MenuSpriteScreen) screen).register(menuSpriteData);
        } else if (DynamicMenuSpriteScreen.class.isAssignableFrom(screen.getClass())) {
          ((DynamicMenuSpriteScreen<?>) screen).register(type);
        }
      }
    }
  }

  /**
   * Loads the game's settings.
   */
  private void loadSettings() {
    logger.debug("Loading game settings");
    UserSettings.Settings settings = UserSettings.get();
    UserSettings.applySettings(settings);
  }

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
      case SHOP:
        return new ShopScreen(this);
      case INVENTORY:
        return new InventoryScreen(this);
        case WORLD_MAP:
            return new WorldMapScreen(this);
      default:
        return null;
    }
  }

  public enum RegisteredScreens  {
  }

  public enum ScreenType {
    MAIN_MENU, MAIN_GAME, SETTINGS, SKILLTREE, PROFILE, LOAD_GAME, STATISTICS, ACHIEVEMENTS, SHOP, INVENTORY, WORLD_MAP
  }

  /**
   * Helper method to check if an enum value exists in another enum type
   */
  private boolean contains(ScreenType[] values, String name) {
    for (ScreenType type : values) {
      if (type.name().equals(name)) {
        return true;
      }
    }
    return false;
  }
    public void exit() {
        app.exit();
    }
}
