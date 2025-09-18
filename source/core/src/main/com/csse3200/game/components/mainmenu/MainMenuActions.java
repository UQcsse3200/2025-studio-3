package com.csse3200.game.components.mainmenu;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
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
    entity.getEvents().addListener("quickStart", this::onQuickStart); // ✅ from our branch
    entity.getEvents().addListener("minigame", this::onMiniGame); // ✅ from main
    entity.getEvents().addListener("load", this::onLoad);
    entity.getEvents().addListener("exit", this::onExit);
    entity.getEvents().addListener("settings", this::onSettings);
    entity.getEvents().addListener("worldMap", this::onWorldMap);
    entity.getEvents().addListener("Cutscene", this::onCutscene); // ✅ from main
  }

  /** Start → World Map */
  private void onStart() {
    logger.info("Start game → Opening Main Game");
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }

  /** Quick Start → Main Game directly */
  private void onQuickStart() {
    logger.info("Quick Start → Directly Launching Main Game");
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }

  /** MiniGames screen */
  private void onMiniGame() {
    logger.info("MiniGames");
    game.setScreen(GdxGame.ScreenType.MINI_GAMES);
  }

  /** Cutscene */
  private void onCutscene() {
    ServiceLocator.getCutsceneService().playCutscene("dialogue");
  }

  /** Load game (placeholder for saved states). */
  private void onLoad() {
    logger.info("Load game");
    game.setScreen(GdxGame.ScreenType.LOAD_GAME);
  }

  /** Open World Map screen */
  private void onWorldMap() {
    logger.info("Launching world map screen");
    game.setScreen(GdxGame.ScreenType.WORLD_MAP);
  }

  /** Exit the game. */
  private void onExit() {
    logger.info("Exit game");
    game.exit();
  }

  /** Open Settings screen. */
  private void onSettings() {
    logger.info("Launching settings screen");
    game.setScreen(GdxGame.ScreenType.SETTINGS);
  }
}
