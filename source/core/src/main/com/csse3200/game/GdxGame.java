package com.csse3200.game;

import static com.badlogic.gdx.Gdx.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.csse3200.game.data.MenuSpriteData;
import com.csse3200.game.persistence.UserSettings;
import com.csse3200.game.screens.*;
import com.csse3200.game.screens.MainGameScreen;
import com.csse3200.game.screens.MainMenuScreen;
import com.csse3200.game.screens.NewGameScreen;
import com.csse3200.game.screens.SaveGameScreen;
import com.csse3200.game.screens.SettingsScreen;
import com.csse3200.game.screens.WorldMapScreen;
import com.csse3200.game.services.CutsceneService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The main game class. */
public class GdxGame extends Game {
  private static final Logger logger = LoggerFactory.getLogger(GdxGame.class);
  private static final String[] GLOBAL_ASSETS = {
    "images/dialog.png",
    "images/shop-popup.png",
    "images/coins.png",
    "images/close-icon.png",
    "images/plaque.png",
    "images/skillpoints.png",
    "images/settings-icon.png",
    "images/menu-icon.png",
    "images/achievement.png",
    "images/pause-icon.png",
    "images/placeholder.png"
  };

    @Override
    public void create() {
        logger.info("Creating game");
        loadMenus();

        ServiceLocator.registerCutsceneService(new CutsceneService());


    loadMenus();

    // Sets background to light yellow
    Gdx.gl.glClearColor(248f/255f, 249/255f, 178/255f, 1);
        // Sets background to light yellow
        Gdx.gl.glClearColor(248f/255f, 249f/255f, 178f/255f, 1);

        setScreen(ScreenType.MAIN_MENU);
    }

  /**
   * Initializes the game after loading screen is complete. This method is called by the
   * LoadingScreen when loading is finished.
   */
  public void initializeGame() {
    logger.info("[GdxGame] Initializing game after loading screen");

    //  Game-dependent services
    ServiceLocator.registerProfileService(new ProfileService());
    ServiceLocator.registerGlobalResourceService(new ResourceService());
    ServiceLocator.registerDialogService(new DialogService());
    ServiceLocator.registerMenuSpriteService(new MenuSpriteService());
    ServiceLocator.registerConfigService(new ConfigService());

    // Game-dependent data
    loadGlobalAssets();
    loadSettings();
    loadScreens();

    Gdx.gl.glClearColor(215f / 255f, 215f / 255f, 215f / 255f, 1);
    setScreen(ScreenType.MAIN_MENU);
  }

  /** Runs the appropriate register function to register screen sprites. */
  private void loadScreens() {
    for (ScreenType screenType : ScreenType.values()) {
      Screen screen = newScreen(screenType);
      if (screen != null) {
        if (MenuSpriteScreen.class.isAssignableFrom(screen.getClass())) {
          MenuSpriteData menuSpriteData = new MenuSpriteData(screenType);
          ((MenuSpriteScreen) screen).register(menuSpriteData);
        } else if (DynamicMenuSpriteScreen.class.isAssignableFrom(screen.getClass())) {
          ((DynamicMenuSpriteScreen<?>) screen).register(screenType);
        } else {
          screen.dispose();
        }
      }
    }
  }

  /** Loads the game's global assets. */
  private void loadGlobalAssets() {
    logger.debug("[GdxGame] Loading global assets");
    ServiceLocator.getGlobalResourceService().loadTextures(GLOBAL_ASSETS);
    ServiceLocator.getGlobalResourceService().loadAll();
  }

  /** Loads the game's settings. */
  private void loadSettings() {
    logger.debug("[GdxGame] Loading game settings");
    UserSettings.Settings settings = UserSettings.get();
    UserSettings.applySettings(settings);
  }

  /** Sets the game screen to the provided type. */
  public void setScreen(ScreenType screenType) {
    logger.info("[GdxGame] Setting game screen to {}", screenType);
    Screen currentScreen = getScreen();
    if (currentScreen != null) {
      currentScreen.dispose();
    }
    setScreen(newScreen(screenType));
  }

  @Override
  public void dispose() {
    logger.debug("[GdxGame] Disposing of current screen");
    getScreen().dispose();
  }

  /**
   * Create a new screen of the provided type.
   *
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
      case LOAD_GAME:
        return new LoadGameScreen(this);
      case NEW_GAME:
        return new NewGameScreen(this);
      case SAVE_GAME:
        return new SaveGameScreen(this);
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

  /** Enum for all screens. */
  public enum ScreenType {
    /** Main menu screen. */
    MAIN_MENU,
    /** Main game screen. */
    MAIN_GAME,
    /** Settings screen. */
    SETTINGS,
    /** Skill tree screen. */
    SKILLTREE,
    /** Load game screen. */
    LOAD_GAME,
    /** New game screen. */
    NEW_GAME,
    /** Save game screen. */
    SAVE_GAME,
    /** Statistics screen. */
    STATISTICS,
    /** Achievements screen. */
    ACHIEVEMENTS,
    /** Shop screen. */
    SHOP,
    /** Inventory screen. */
    INVENTORY,
    /** World map screen. */
    WORLD_MAP
  }

  /** Exits the game. */
  public void exit() {
    ServiceLocator.deregisterGlobalResourceService();
    ServiceLocator.deregisterMenuSpriteService();
    ServiceLocator.deregisterConfigService();
    ServiceLocator.deregisterProfileService();
    ServiceLocator.getDialogService().hideAllDialogs();
    ServiceLocator.deregisterDialogService();
    app.exit();
  }
}
