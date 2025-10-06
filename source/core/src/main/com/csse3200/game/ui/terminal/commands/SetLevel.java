package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.progression.Profile;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetLevel implements Command {
  private static final Logger logger = LoggerFactory.getLogger(SetLevel.class);

  @Override
  public boolean action(ArrayList<String> args) {
    if (args.isEmpty()) {
      logger.debug("Invalid arguments received for 'setLevel' command: {}", args);
      return false;
    }
    try {
      if (ServiceLocator.getConfigService().getLevelConfigs().get(args.getFirst()) == null) {
        return false;
      }
      Profile profile = ServiceLocator.getProfileService().getProfile();
      profile.setCurrentLevel(args.getFirst());
    } catch (NullPointerException e) {
      logger.debug("This service is not available on this screen.");
      return false;
    }
    return true;
  }
}
