package com.csse3200.game;

import static com.badlogic.gdx.Gdx.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.csse3200.game.screens.*;
import com.csse3200.game.services.*;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The main game class. */
public class GdxGame extends Game {
  private static final Logger logger = LoggerFactory.getLogger(GdxGame.class);
  private static final String[] GLOBAL_ASSETS = {
    "images/ui/dialog.png",
    "images/ui/shop-popup.png",
    "images/entities/currency/coins.png",
    "images/ui/close-icon.png",
    "images/ui/plaque.png",
    "images/entities/currency/skillpoints.png",
    "images/ui/settings-icon.png",
    "images/ui/menu-icon.png",
    "images/ui/achievement.png",
    "images/ui/pause-icon.png",
    "images/entities/placeholder.png",
    "images/ui/cursor.png",
    "images/backgrounds/bg.png",
    "images/ui/speedup1x.png",
    "images/ui/speedup15x.png",
    "images/ui/speedup2x.png",
    "images/ui/dialog_new_new.png",
    "images/ui/menu.png",
    "images/ui/menu_card.png",
    "images/ui/achievement_dialog_new.png"
  };
  private static final String[] GLOBAL_SOUNDS = {
    "sounds/achievement_unlock.mp3",
    "sounds/error.mp3",
    "sounds/dialog.mp3",
    "sounds/button_clicked.mp3"
  };
  private static final Pair<String, String> GLOBAL_FONT =
      new Pair<>("Default", "fonts/Jersey10-Regular.ttf");

  @Override
  public void create() {
    logger.info("[GdxGame] Initialising core game services.");
    setScreen(new LoadingScreen(this));
  }

  /**
   * Initializes the game after loading screen is complete. This method is called by the
   * LoadingScreen when loading is finished.
   */
  public void initializeGame() {
    logger.info("[GdxGame] Initializing game after loading screen");
    ServiceLocator.registerSettingsService(new SettingsService());
    ServiceLocator.registerProfileService(new ProfileService());
    ServiceLocator.registerGlobalResourceService(new ResourceService());

    loadGlobalAssets();

    ServiceLocator.registerDialogService(new DialogService());
    ServiceLocator.registerConfigService(new ConfigService());
    ServiceLocator.registerCutsceneService(new CutsceneService());
    ServiceLocator.registerWorldMapService(new WorldMapService());
    ServiceLocator.registerMusicService(new MusicService());

    DiscordRichPresenceService discordService = new DiscordRichPresenceService();
    discordService.initialize();
    ServiceLocator.registerDiscordRichPresenceService(discordService);
    if (discordService.isInitialized()) {
      discordService.setPresence(null);
    }

    Gdx.gl.glClearColor(0f / 255f, 0f / 255f, 0f / 255f, 1);
    setCursor();
    setScreen(ScreenType.MAIN_MENU, null);
  }

  /** Loads the game's global assets. */
  private void loadGlobalAssets() {
    logger.debug("[GdxGame] Loading global assets");
    ServiceLocator.getGlobalResourceService().loadTextures(GLOBAL_ASSETS);
    ServiceLocator.getGlobalResourceService()
        .loadFont(GLOBAL_FONT.getValue(), GLOBAL_FONT.getKey());
    ServiceLocator.getGlobalResourceService().loadSounds(GLOBAL_SOUNDS);
    ServiceLocator.getGlobalResourceService().loadAll();
  }

  /** 
   * Used for backward compatibility.
   *
   * @param screenType The type of screen to set.
   */
  public void setScreen(ScreenType screenType) {
    setScreen(screenType, null);
  }

  /**
   * Sets the game screen to the provided type.
   * 
   * @param screenType The type of screen to set.
   * @param levelKey The level key to load (only used for MAIN_GAME screen type).
   */
  public void setScreen(ScreenType screenType, String levelKey) {
    logger.info("[GdxGame] Setting game screen to {}", screenType);
    Screen currentScreen = getScreen();
    if (currentScreen != null) {
      currentScreen.dispose();
    }
    if (screenType == ScreenType.MAIN_GAME) {
      if (levelKey == null) {
        throw new IllegalArgumentException("Level key cannot be null for MAIN_GAME");
      }
      setScreen(new MainGameScreen(this, levelKey));
    } else {
      setScreen(newScreen(screenType));
    }
  }

  /** Sets the cursor to the custom cursor. */
  public void setCursor() {
    logger.info("[GdxGame] Setting cursor");
    Pixmap pixmap = new Pixmap(Gdx.files.internal("images/ui/cursor.png"));
    int xHotspot = 0;
    int yHotspot = 0;
    Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
    pixmap.dispose();
    Gdx.graphics.setCursor(cursor);
  }

  @Override
  public void render() {
    super.render();
    DiscordRichPresenceService discordService = ServiceLocator.getDiscordRichPresenceService();
    if (discordService != null && discordService.isInitialized()) {
      discordService.runCallbacks();
    }
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
      case DOSSIER:
        return new DossierScreen(this);
      case SLOT_MACHINE:
        return new MainGameScreen(this);
      case MINI_GAMES:
        return new MiniGameScreen(this);
      case PADDLE_GAME:
        return new PaddleGameScreen(this);
      case LANE_RUNNER:
        return new LaneRunnerScreen(this);
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
    WORLD_MAP,
    /** Dossier screen. */
    DOSSIER,
    /** Slot machine level */
    SLOT_MACHINE,
    /** Mini game screen */
    MINI_GAMES,
    /** Paddle game screen */
    PADDLE_GAME,
    /** Lane runner game screen */
    LANE_RUNNER
  }

  /** Exits the game. */
  public void exit() {
    ServiceLocator.getDiscordRichPresenceService().shutdown();
    ServiceLocator.deregisterGlobalResourceService();
    ServiceLocator.deregisterConfigService();
    ServiceLocator.deregisterProfileService();
    ServiceLocator.getDialogService().hideAllDialogs();
    ServiceLocator.deregisterDialogService();
    ServiceLocator.deregisterDiscordRichPresenceService();
    app.exit();
  }
}
