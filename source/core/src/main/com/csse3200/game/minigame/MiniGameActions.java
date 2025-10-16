package com.csse3200.game.minigame;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiniGameActions extends Component {
  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.minigame.MiniGameActions.class);
  private GdxGame game;

  public MiniGameActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("lanerunner", this::onLaneRunner);
    entity.getEvents().addListener("wallpong", this::onWallPong);
    entity.getEvents().addListener("back", this::onBack);
  }

  /** Swaps to the Main Game screen. */
  private void onLaneRunner() {
    logger.info("Lane Runner Mini game");

    game.setScreen(GdxGame.ScreenType.LANE_RUNNER);
  }

  private void onWallPong() {
    logger.info("Brick Breaker Mini game");

    game.setScreen(GdxGame.ScreenType.PADDLE_GAME);
  }

  /** Swaps to the World Map screen. */
  private void onBack() {
    logger.info("Launching World Map screen");
    game.setScreen(GdxGame.ScreenType.WORLD_MAP);
  }
}
