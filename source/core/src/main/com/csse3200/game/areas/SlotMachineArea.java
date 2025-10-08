package com.csse3200.game.areas;

import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.components.slot.SlotMachineDisplay;
import com.csse3200.game.entities.Entity;

/**
 * Represents a dedicated game area for the slot machine level. Handles loading and unloading of
 * slot machine-specific assets, sets up the user interface (HUD), and manages display elements
 * relevant to the slot machine gameplay.
 */
public class SlotMachineArea extends LevelGameArea {

  /**
   * Creates a new SlotMachineArea with the level key.
   *
   * @param levelKey The level key for this slot machine area.
   */
  public SlotMachineArea(String levelKey) {
    super(levelKey);
  }

  @Override
  protected void displayUI() {
    // Only add Slot UI; no defence hotbar on the slot level
    Entity ui = new Entity();
    ui.addComponent(new GameAreaDisplay("Slot Machine Level"));
    ui.addComponent(new SlotMachineDisplay(this));
    spawnEntity(ui);

    createGameOverEntity();
  }
}
