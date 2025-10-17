package com.csse3200.game.minigame;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component for the mini game actions.
 */
public class MiniGameActions extends Component {
  private static final Logger logger =
      LoggerFactory.getLogger(MiniGameActions.class);
  private GdxGame game;

  /**
   * Creates a new MiniGameActions component.
   * 
   * @param game the game instance
   */
  public MiniGameActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("lanerunner", this::onLaneRunner);
    entity.getEvents().addListener("wallpong", this::onWallPong);
    entity.getEvents().addListener("back", this::onBack);
  }

  /** Swaps to the Lane Runner screen. */
  private void onLaneRunner() {
    logger.info("Lane Runner Minigame");
    game.setScreen(GdxGame.ScreenType.LANE_RUNNER);
  }

  /** Swaps to the Wall Pong screen. */
  private void onWallPong() {
    logger.info("Wall Pong Minigame");
    game.setScreen(GdxGame.ScreenType.PADDLE_GAME);
  }

  /** Swaps to the World Map screen. */
  private void onBack() {
    logger.info("Launching World Map screen");
    game.setScreen(GdxGame.ScreenType.WORLD_MAP);
  }
}
