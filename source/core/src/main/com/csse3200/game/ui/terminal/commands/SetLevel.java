package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.progression.Profile;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetLevel implements Command {
  private static final Logger logger = LoggerFactory.getLogger(SetLevel.class);

  /**
   * Sets the internal variable for current level. First argument must be a string corresponding to
   * the name of the level Levels are named levelOne, levelTwo etc.
   *
   * @return true when successful, false if unsuccessful
   */
  @Override
  public boolean action(ArrayList<String> args) {
    if (args.isEmpty()) {
      logger.warn("Invalid arguments received for 'setLevel' command: {}", args);
      return false;
    }
    try {
      if (ServiceLocator.getConfigService().getLevelConfigs().get(args.getFirst()) == null) {
        return false;
      }
      Profile profile = ServiceLocator.getProfileService().getProfile();
      profile.setCurrentLevel(args.getFirst());
    } catch (NullPointerException e) {
      logger.warn("This service is not available on this screen.");
      return false;
    }
    return true;
  }
}
