package com.csse3200.game.entities.configs;

import com.badlogic.gdx.utils.compression.lzma.Base;

/**
 * Defines all NPC configs to be loaded by the NPC Factory.
 */
public class NPCConfigs {
  public BaseEntityConfig ghost = new BaseEntityConfig();
  public GhostKingConfig ghostKing = new GhostKingConfig();
  public BaseEntityConfig robot = new BaseEntityConfig();
}
