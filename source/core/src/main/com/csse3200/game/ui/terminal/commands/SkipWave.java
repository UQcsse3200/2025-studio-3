package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkipWave implements Command {
  private static final Logger logger = LoggerFactory.getLogger(SkipWave.class);

  @Override
  public boolean action(ArrayList<String> args) {
    try {
      ServiceLocator.getWaveService().endWave();
    } catch (NullPointerException e) {
      logger.debug("This service is not available on this screen.");
      return false;
    }
    return true;
  }
}
