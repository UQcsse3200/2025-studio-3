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
    entity.getEvents().addListener("resetShop", this::onResetShop);
  }

  /** Return to the world map. */
  private void onBack() {
    logger.info("Returning to world map");
    game.setScreen(GdxGame.ScreenType.WORLD_MAP);
  }

  /** Reset the shop screen to show updated sold status. */
  private void onResetShop() {
    logger.info("Resetting shop screen");
    game.setScreen(GdxGame.ScreenType.SHOP);
  }
}
