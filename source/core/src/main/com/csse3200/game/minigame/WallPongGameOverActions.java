package com.csse3200.game.minigame;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WallPongGameOverActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(WallPongGameOverActions.class);
  private GdxGame game;

  public WallPongGameOverActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("playAgain", this::onPlayAgain);
    entity.getEvents().addListener("mainMenu", this::onMainMenu);
  }

  private void onPlayAgain() {
    logger.debug("Play Again event triggered");
    game.setScreen(GdxGame.ScreenType.PADDLE_GAME);
  }

  private void onMainMenu() {
    logger.debug("Main Menu event triggered");
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }
}
