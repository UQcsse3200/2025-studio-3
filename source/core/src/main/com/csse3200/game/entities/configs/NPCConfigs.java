package com.csse3200.game.entities.configs;

/** Defines all NPC configs to be loaded by the NPC Factory. */
public class NPCConfigs {
  public final BaseEntityConfig fastRobot = new FastRobotConfig();
  public final BaseEntityConfig standardRobot = new StandardRobotConfig();
  public final BaseEntityConfig tankyRobot = new TankyRobotConfig();
  public final BaseEntityConfig ghost = new BaseEntityConfig();
  public final GhostKingConfig ghostKing = new GhostKingConfig();
  public final BaseDefenceConfig slingshooter = new BaseDefenceConfig();
  public final BungeeRobotConfig bungeeRobot = new BungeeRobotConfig();
  public final TeleportRobotConfig teleportRobot = new TeleportRobotConfig();
  // public BaseDefenceConfig trebuchet = new BaseDefenceConfig();
  // public BaseDefenceConfig spearman = new BaseDefenceConfig();
  // public BaseDefenceConfig harpoon = new BaseDefenceConfig();
  // public BaseDefenceConfig mortar = new BaseDefenceConfig();
  public final BaseDefenceConfig forge = new BaseDefenceConfig();
}
