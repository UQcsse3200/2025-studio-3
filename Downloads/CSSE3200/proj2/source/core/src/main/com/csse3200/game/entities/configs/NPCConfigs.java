package com.csse3200.game.entities.configs;

import com.badlogic.gdx.utils.compression.lzma.Base;

/**
 * Defines all NPC configs to be loaded by the NPC Factory.
 */
public class NPCConfigs {
  public BaseEntityConfig fastRobot = new FastRobotConfig();
  public BaseEntityConfig standardRobot = new StandardRobotConfig();
  public BaseEntityConfig tankyRobot = new TankyRobotConfig();
  public BaseEntityConfig ghost = new BaseEntityConfig();
  public GhostKingConfig ghostKing = new GhostKingConfig();
  public BaseDefenceConfig slingshooter = new BaseDefenceConfig();
  // public BaseDefenceConfig trebuchet = new BaseDefenceConfig();
  // public BaseDefenceConfig spearman = new BaseDefenceConfig();
  // public BaseDefenceConfig harpoon = new BaseDefenceConfig();
  // public BaseDefenceConfig mortar = new BaseDefenceConfig();
  public BaseDefenceConfig forge = new BaseDefenceConfig();
  public BaseEntityConfig robot = new BaseEntityConfig();
  //public GhostKingConfig ghostKing = new GhostKingConfig(); // TODO remove the ghosts
}
