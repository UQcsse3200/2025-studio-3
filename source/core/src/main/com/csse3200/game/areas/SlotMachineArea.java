package com.csse3200.game.areas;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.components.gameover.GameOverWindow;
import com.csse3200.game.components.slot.SlotMachineDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;

/**
 * Represents a dedicated game area for the slot machine level. Handles loading and unloading of
 * slot machine-specific assets, sets up the user interface (HUD), and manages display elements
 * relevant to the slot machine gameplay.
 */
public class SlotMachineArea extends LevelGameArea {
  private static final String[] SLOT_TEXTURE_ATLASES = {
    "images/slot_frame.atlas", "images/slot_reels.atlas",
  };
  private static final String[] SLOT_TEXTURES = {
    "images/slot_reels_background.png",
  };

  /**
   * Creates a new SlotMachineArea with the given TerrainFactory.
   *
   * @param terrainFactory The factory used to generate terrain for this area.
   */
  public SlotMachineArea(TerrainFactory terrainFactory) {
    super(terrainFactory);
  }

  /** Initializes the slot machine area by loading assets and adding the HUD. */
  @Override
  public void create() {
    loadSlotAssets();
    super.create();
  }

  /** Unloads slot machine assets and disposes of the area. */
  @Override
  public void dispose() {
    unloadSlotAssets();
    super.dispose();
  }

  @Override
  protected void displayUI() {
    Entity ui = new Entity();
    ui.addComponent(new GameAreaDisplay("Slot Machine Level"));
    ui.addComponent(new SlotMachineDisplay(this));
    spawnEntity(ui);

    // Creates a game over entity to handle the game over window UI
    this.gameOverEntity = new Entity();
    gameOverEntity.addComponent(new GameOverWindow());
    spawnEntity(this.gameOverEntity);
  }

  /** Loads all textures and atlases required for the slot machine. */
  private void loadSlotAssets() {
    ResourceService rs = ServiceLocator.getResourceService();
    rs.loadTextureAtlases(SLOT_TEXTURE_ATLASES);
    rs.loadTextures(SLOT_TEXTURES);
    rs.loadAll();
  }

  /** Unloads all slot machine textures and atlases to free memory. */
  private void unloadSlotAssets() {
    ResourceService rs = ServiceLocator.getResourceService();
    if (rs != null) {
      rs.unloadAssets(SLOT_TEXTURE_ATLASES);
      rs.unloadAssets(SLOT_TEXTURES);
    }
  }

  @Override
  protected void spawnScrap(Vector2 targetPos, int spawnInterval, int scrapValue) {
    // Slot level does not use the currency system; ignore requests to spawn scrap.
  }
}
