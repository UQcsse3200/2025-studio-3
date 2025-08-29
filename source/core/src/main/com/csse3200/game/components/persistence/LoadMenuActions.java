package com.csse3200.game.components.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.context.Persistence;

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

  private void handleBack() {
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }

  private void handleLoadGame(Persistence.Savefile savefile) {
    logger.info("Loading game: " + savefile.name);
    Persistence.load(savefile);
    // Load game logic here
  }
}
