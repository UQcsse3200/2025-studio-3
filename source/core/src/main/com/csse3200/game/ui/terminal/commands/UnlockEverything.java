package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.progression.Profile;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.WorldMapNode;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnlockEverything implements Command {
  private static final Logger logger = LoggerFactory.getLogger(UnlockEverything.class);

  /**
   * Grants the user a large number of skill points, coins, and unlocks every available level.
   *
   * @return true when successful, otherwise false
   */
  @Override
  public boolean action(ArrayList<String> args) {
    try {
      Profile profile = ServiceLocator.getProfileService().getProfile();
      profile.getWallet().addCoins(9999);
      profile.getWallet().addSkillsPoints(99);
      for (WorldMapNode node : ServiceLocator.getWorldMapService().getAllNodes()) {
        node.setUnlocked(true);
          String currentLevel = ServiceLocator.getProfileService().getProfile().getCurrentLevel();
          String nextLevel = findNextLevel(currentLevel);
          ServiceLocator.getProfileService().getProfile().setCurrentLevel(nextLevel);
      }
      ServiceLocator.getProfileService().saveCurrentProfile();
    } catch (NullPointerException e) {
      logger.debug("This service is not available on this screen.");
      return false;
    }
    return true;
  }
    private String findNextLevel(String currentLevel) {
        return switch (currentLevel) {
            case "levelOne" -> "levelTwo";
            case "levelTwo" -> "levelThree";
            case "levelThree" -> "levelFour";
            case "levelFour" -> "levelFive";
            default -> "end";
        };
    }
}
