package com.csse3200.game.entities.configs;

/** Defines all NPC configs to be loaded by the NPC Factory. */
public class NPCConfigs {
  public static final BaseEntityConfig fastRobot = new FastRobotConfig();
  public static final BaseEntityConfig standardRobot = new StandardRobotConfig();
  public static final BaseEntityConfig tankyRobot = new TankyRobotConfig();
  public static final BaseEntityConfig ghost = new BaseEntityConfig();
  public static final GhostKingConfig ghostKing = new GhostKingConfig();
  public final BaseDefenceConfig slingshooter = new BaseDefenceConfig();
  // public BaseDefenceConfig trebuchet = new BaseDefenceConfig();
  // public BaseDefenceConfig spearman = new BaseDefenceConfig();
  // public BaseDefenceConfig harpoon = new BaseDefenceConfig();
  // public BaseDefenceConfig mortar = new BaseDefenceConfig();
  public static final BaseDefenceConfig forge = new BaseDefenceConfig();
  public static final BaseEntityConfig robot = new BaseEntityConfig();
  // public GhostKingConfig ghostKing = new GhostKingConfig(); // TODO remove the ghosts
}
