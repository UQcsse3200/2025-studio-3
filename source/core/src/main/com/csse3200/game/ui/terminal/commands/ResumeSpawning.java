package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResumeSpawning implements Command {
  private static final Logger logger = LoggerFactory.getLogger(ResumeSpawning.class);

  /**
   * Makes WaveService resume enemy spawning.
   *
   * @return true when successful, otherwise false
   */
  @Override
  public boolean action(ArrayList<String> args) {
    try {
      ServiceLocator.getWaveService().debugResumeSpawning();
    } catch (NullPointerException e) {
      logger.warn("This service is not available on this screen.");
      return false;
    }
    return true;
  }
}
