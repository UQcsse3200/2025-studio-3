package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfiniteMoney implements Command {

  private static final Logger logger = LoggerFactory.getLogger(InfiniteMoney.class);

  @Override
  /**
   * Grants the user 9999 scrap inside the level.
   *
   * @return true when successful
   */
  public boolean action(ArrayList<String> args) {
    try {
      ServiceLocator.getCurrencyService().add(9999);
    } catch (NullPointerException e) {
      logger.warn("This service is not available on this screen.");
    }
    return true;
  }
}
