package com.csse3200.game.components.mainmenu;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(MainMenuActions.class);
  private GdxGame game;

  public MainMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("start", this::onStart);
    entity.getEvents().addListener("load", this::onLoad);
    entity.getEvents().addListener("exit", this::onExit);
    entity.getEvents().addListener("settings", this::onSettings);
    entity.getEvents().addListener("worldMap", this::onWorldMap);
  }

  /** Swaps to the Main Game screen. */
  private void onStart() {
    logger.info("Start game");
    ServiceLocator.registerConfigService(new ConfigService());
    Persistence.load();
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }

  /** Intended for loading a saved game state. Load functionality is not actually implemented. */
  private void onLoad() {
    logger.info("Load game");
    game.setScreen(GdxGame.ScreenType.LOAD_GAME);
  }

  private void onWorldMap() {
    logger.info("Launching world map screen");
    game.setScreen(GdxGame.ScreenType.WORLD_MAP);
  }

  /** Exits the game. */
  private void onExit() {
    logger.info("Exit game");
    game.exit();
  }

  /** Swaps to the Settings screen. */
  private void onSettings() {
    logger.info("Launching settings screen");
    game.setScreen(GdxGame.ScreenType.SETTINGS);
  }
}
