package com.csse3200.game.components.proximityinfodisplay;

import com.csse3200.game.data.MenuSpriteData;
import com.csse3200.game.services.MenuSpriteService;
import com.csse3200.game.services.ServiceLocator;

public class LevelInteractionHandler {

  public static void handleInteraction(MenuSpriteData spriteData) {
    if (spriteData.getLocked()) {
      System.out.println(spriteData.getName() + " is locked!");
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
