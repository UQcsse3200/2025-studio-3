package com.csse3200.game.screens;

import com.csse3200.game.GdxGame;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.SlotMachineArea;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.maingame.MainGameActions;

/**
 * The game screen containing the main game.
 *
 * <p>Details on libGDX screens: https://happycoding.io/tutorials/libgdx/game-screens
 */
public class SlotMachineScreen extends MainGameScreen {

  public SlotMachineScreen(GdxGame game) {
    super(game);
  }

  @Override
  protected LevelGameArea createGameArea(TerrainFactory terrainFactory) {
    return new SlotMachineArea(terrainFactory);
  }

  @Override
  protected void configureMainGameActions(MainGameActions mainGameActions) {
    mainGameActions.setSlotMachineScreen(this);
  }
}
