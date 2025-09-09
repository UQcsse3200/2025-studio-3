package com.csse3200.game.components.proximityinfodisplay;

import com.csse3200.game.data.MenuSpriteData;
import com.csse3200.game.services.MenuSpriteService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelInteractionHandler {
  private static final Logger logger = LoggerFactory.getLogger(LevelInteractionHandler.class);

  public static void handleInteraction(MenuSpriteData spriteData) {
    
    if (spriteData.getLocked()) {
      logger.info("{} is locked!", spriteData.getName());
    } else {
      try {
        // Use the MenuSpriteService to trigger the level opening
        MenuSpriteService menuService = ServiceLocator.getMenuSpriteService();
        if (menuService != null) {
          menuService.getMenuSprites().stream()
              .filter(s -> s == spriteData)
              .findFirst()
              .ifPresent(
                  s -> {
                    try {
                      s.enter(null); // pass null since your friend’s code doesn’t use GdxGame
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                  });
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
