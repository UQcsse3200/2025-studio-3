package com.csse3200.game.components.mainmenu;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
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
  }

  /** Start → World Map */
  private void onStart() {
    logger.info("[MainMenuActions] Starting new game");
    game.setScreen(GdxGame.ScreenType.NEW_GAME);
  }

  /** Load game (placeholder for saved states). */
  private void onLoad() {
    logger.info("[MainMenuActions] Loading game");
    game.setScreen(GdxGame.ScreenType.LOAD_GAME);
  }

  /** Exit the game. */
  private void onExit() {
    logger.info("[MainMenuActions] Exiting game");
    game.exit();
  }

  /** Open Settings screen. */
  private void onSettings() {
    logger.info("[MainMenuActions] Launching settings screen");
    //changed temp
    game.setScreen(GdxGame.ScreenType.SKILLTREE);
  }
}
