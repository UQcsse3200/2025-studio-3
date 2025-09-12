package com.csse3200.game.components.hud;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles actions for the main map navigation menu.
 */
public class MainMapNavigationMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(MainMapNavigationMenuActions.class);
  private GdxGame game;

  public MainMapNavigationMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("open_settings", this::onSettings);
  }

  /** Swaps to the Profile screen. */
  private void onSettings() {
    logger.info("Entering settings screen");
    game.setScreen(GdxGame.ScreenType.SETTINGS);
  }
}
