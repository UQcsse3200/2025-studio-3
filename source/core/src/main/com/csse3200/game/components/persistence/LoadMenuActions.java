package com.csse3200.game.components.persistence;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles actions in the load menu. */
public class LoadMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(LoadMenuActions.class);
  private GdxGame game;

  /**
   * Constructor for the LoadMenuActions class.
   *
   * @param game The game instance.
   */
  public LoadMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("back", this::handleBack);
    entity.getEvents().addListener("loadGame", this::handleLoadGame);
  }

  /** Handle going back to the main menu. */
  private void handleBack() {
    ServiceLocator.deregisterConfigService();
    if (Persistence.profile() == null) {
      game.setScreen(GdxGame.ScreenType.MAIN_MENU);
    } else {
      game.setScreen(GdxGame.ScreenType.MAIN_GAME);
    }
  }

  /**
   * Handle loading a game from a savefile. Loads the profile and moves to the main game screen.
   *
   * @param savefile the savefile to load
   */
  private void handleLoadGame(Savefile savefile) {
    logger.info("Loading game: {}", savefile.getName());
    Persistence.load(savefile);
    ServiceLocator.registerConfigService(new ConfigService());
    game.loadScreens();
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }
}
