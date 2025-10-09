package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorDialogue implements Command {

  private static final Logger logger = LoggerFactory.getLogger(ErrorDialogue.class);

  @Override
  /**
   * Creates a placeholder error dialogue box.
   *
   * @return true when successful
   */
  public boolean action(ArrayList<String> args) {
    try {
      ServiceLocator.getDialogService().error("Debug!", "If you see this error, things work.");
    } catch (NullPointerException e) {
      logger.debug("This service is not available on this screen.");
    }
    return true;
  }
}
