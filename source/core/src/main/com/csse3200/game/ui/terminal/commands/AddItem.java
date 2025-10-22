package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddItem implements Command {
  private static final Logger logger = LoggerFactory.getLogger(AddItem.class);

  /**
   * Adds item given as first argument to inventory.
   *
   * @return true when successful, otherwise false
   */
  @Override
  public boolean action(ArrayList<String> args) {
    try {
      if (args.isEmpty()) {
        logger.warn("Invalid arguments received for 'addItem' command: {}", args);
        return false;
      }
      ServiceLocator.getProfileService().getProfile().getInventory().addItem(args.getFirst());
    } catch (NullPointerException e) {
      logger.warn("This service is not available on this screen.");
      return false;
    }
    return true;
  }
}
