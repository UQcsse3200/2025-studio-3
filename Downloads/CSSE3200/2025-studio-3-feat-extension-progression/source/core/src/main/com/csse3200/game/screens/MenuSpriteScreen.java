package com.csse3200.game.screens;

import com.badlogic.gdx.Screen;
import com.csse3200.game.data.MenuSpriteData;

/** Adds register function. */
public interface MenuSpriteScreen extends Screen {
  /**
   * Ran at game launch to initialise screen in MapSpriteService
   *
   * @param mapSpriteData - sprite data for the map
   */
  void register(MenuSpriteData mapSpriteData);
}
