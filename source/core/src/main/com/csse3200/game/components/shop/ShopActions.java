package com.csse3200.game.components.shop;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles actions for the shop screen. */
public class ShopActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(ShopActions.class);
  private GdxGame game;

  public ShopActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("back", this::onBack);
  }

  /** Return to the main game. */
  private void onBack() {
    logger.info("Returning to profile");
    game.setScreen(GdxGame.ScreenType.PROFILE);
  }
}
