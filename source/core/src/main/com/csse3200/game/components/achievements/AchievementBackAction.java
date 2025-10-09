package com.csse3200.game.components.achievements;

import com.csse3200.game.GdxGame;

public class AchievementBackAction {

  private final GdxGame game;

  public AchievementBackAction(GdxGame game) {
    this.game = game;
  }

  /** Handles navigation back to the World Map Screen. */
  public void backMenu() {
    game.setScreen(GdxGame.ScreenType.WORLD_MAP);
  }
}
