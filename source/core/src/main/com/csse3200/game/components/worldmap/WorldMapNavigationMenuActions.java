package com.csse3200.game.components.worldmap;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles actions for the main map navigation menu. */
public class WorldMapNavigationMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapNavigationMenuActions.class);
  private GdxGame game;

  /**
   * Constructor for the WorldMapNavigationMenuActions class.
   *
   * @param game The game instance.
   */
  public WorldMapNavigationMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("open_settings", this::onSettings);
    entity.getEvents().addListener("open_inventory", this::onInventory);
    entity.getEvents().addListener("open_achievements", this::onAchievements);
    entity.getEvents().addListener("open_statistics", this::onStats);
    entity.getEvents().addListener("open_dossier", this::onDossier);
    entity.getEvents().addListener("exit", this::onExit);
    entity.getEvents().addListener("main_menu", this::onMainMenu);
    entity.getEvents().addListener("quicksave", this::onQuicksave);
    entity.getEvents().addListener("savegame", this::onSave);
    entity.getEvents().addListener("loadgame", this::onLoad);
  }

  /** Swaps to the Profile screen. */
  private void onSettings() {
    logger.info("[WorldMapNavigationMenuActions] Entering settings screen");
    game.setScreen(GdxGame.ScreenType.SETTINGS);
  }

  /** Swaps to the Inventory screen. */
  private void onInventory() {
    logger.info("[WorldMapNavigationMenuActions] Opening inventory");
    game.setScreen(GdxGame.ScreenType.INVENTORY);
  }

  /** Swaps to the Achievements screen. */
  private void onAchievements() {
    logger.info("[WorldMapNavigationMenuActions] Opening achievements");
    game.setScreen(GdxGame.ScreenType.ACHIEVEMENTS);
  }

  /** Swaps to the Statistics screen. */
  private void onStats() {
    logger.info("[WorldMapNavigationMenuActions] Opening statistics");
    game.setScreen(GdxGame.ScreenType.STATISTICS);
  }

  /** Exits the game without saving. */
  private void onMainMenu() {
    logger.info("[WorldMapNavigationMenuActions] Returning to main menu");
    ServiceLocator.getDialogService()
        .warning(
            "Return to Main Menu",
            "Are you sure you want to return to the main menu? Progress will not be saved.",
            dialog -> {
              game.setScreen(GdxGame.ScreenType.MAIN_MENU);
              ServiceLocator.getProfileService().clear();
            },
            null);
  }

  /** Exits the game. */
  private void onExit() {
    logger.info("[WorldMapNavigationMenuActions] Exiting game");
    ServiceLocator.getDialogService()
        .warning(
            "Exit Game",
            "Are you sure you want to exit the game? Progress will not be saved.",
            dialog -> game.exit(),
            null);
  }

  /** Saves the game. */
  private void onSave() {
    logger.info("[WorldMapNavigationMenuActions] Opening save game screen");
    game.setScreen(GdxGame.ScreenType.SAVE_GAME);
  }

  /** Opens the dossier. */
  private void onDossier() {
    logger.info("Opening dossier");
    game.setScreen(GdxGame.ScreenType.DOSSIER);
  }

  /** Quicksaves the game. */
  private void onQuicksave() {
    logger.info("[WorldMapNavigationMenuActions] Quicksaving game");
    ServiceLocator.getProfileService().saveCurrentProfile();
    ServiceLocator.getDialogService().info("Quicksaved", "Game has been saved.");
  }

  /** Opens the load game screen. */
  private void onLoad() {
    logger.info("[WorldMapNavigationMenuActions] Opening load game screen");
    game.setScreen(GdxGame.ScreenType.LOAD_GAME);
  }
}
