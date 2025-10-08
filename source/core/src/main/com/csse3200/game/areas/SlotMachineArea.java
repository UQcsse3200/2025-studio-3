package com.csse3200.game.areas;

import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;

/**
 * Represents a dedicated game area for the slot machine level. Handles loading and unloading of
 * slot machine-specific assets, sets up the user interface (HUD), and manages display elements
 * relevant to the slot machine gameplay.
 */
public class SlotMachineArea extends LevelGameArea {
  private static final String[] SLOT_TEXTURE_ATLASES = {
    "images/entities/slotmachine/slot_frame.atlas",
    "images/entities/slotmachine/slot_reels.atlas",
    "images/entities/slotmachine/pie_filled.atlas",
  };
  private static final String[] SLOT_TEXTURES = {
    "images/entities/slotmachine/slot_reels_background.png",
  };

  /**
   * Creates a new SlotMachineArea with the level key.
   *
   * @param levelKey The level key for this slot machine area.
   */
  public SlotMachineArea(String levelKey) {
    super(levelKey);
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
}
