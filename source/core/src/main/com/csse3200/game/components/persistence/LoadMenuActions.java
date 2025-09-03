package com.csse3200.game.components.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;

/**
 * Handles actions in the load menu.
 */
public class LoadMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(LoadMenuActions.class);
  private GdxGame game;

  public LoadMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("back", this::handleBack);
    entity.getEvents().addListener("loadGame", this::handleLoadGame);
  }

  /**
   * Handle going back to the main menu.
   */
  private void handleBack() {
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }

  /**
   * Handle loading a game from a savefile. Loads the profile
   * and moves to the main game screen.
   *
   * @param savefile the savefile to load
   */
  private void handleLoadGame(Savefile savefile) {
    logger.info("Loading game: " + savefile.getName());
    Persistence.load(savefile);
    game.loadMenus();
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }
}
