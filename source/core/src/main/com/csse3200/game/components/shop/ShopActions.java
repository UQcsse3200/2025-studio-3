package com.csse3200.game.components.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;

/**
 * Handles actions for the shop screen.
 */
public class ShopActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(ShopActions.class);
  private GdxGame game;

  public ShopActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("back", this::onBack);
    // entity.getEvents().addListener("select", this::onSelect);
    // entity.getEvents().addListener("purchase", this::onPurchase);
  }

  // private void onSelect(Item item) {
  // logger.info("Shop item selected: {}", item);
  // // Open a dialog box with the item name, description, price etc.
  // // Show two buttons, to go 'back' or to 'buy'
  // }

  // private void onPurchase(Item item) {
  // logger.info("Shop item purchased: {}", item);
  // Profile profile = ServiceLocator.getProfileService().get();
  // profile.wallet().deduct(item.getCost());
  // profile.inventory().addItem(item);

  // // Show a popup saying item has been added to inventory

  // }

  /**
   * Return to the main game.
   */
  private void onBack() {
    logger.info("Returning to main game");
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }
}
