package com.csse3200.game.entities.configs;

/** Defines all NPC configs to be loaded by the NPC Factory. */
public class NPCConfigs {
  public BaseEnemyConfig fastRobot = new BaseEnemyConfig();
  public BaseEnemyConfig standardRobot = new BaseEnemyConfig();
  public BaseEnemyConfig tankyRobot = new BaseEnemyConfig();
  public final BaseEntityConfig ghost = new BaseEntityConfig();
  public final GhostKingConfig ghostKing = new GhostKingConfig();
  public final BaseDefenceConfig slingshooter = new BaseDefenceConfig();
  public TeleportRobotConfig teleportRobot = new TeleportRobotConfig();
  public BaseEnemyConfig bungeeRobot = new BaseEnemyConfig();
  public BaseEnemyConfig gunnerRobot = new BaseEnemyConfig();
  // public BaseDefenceConfig trebuchet = new BaseDefenceConfig();
  // public BaseDefenceConfig spearman = new BaseDefenceConfig();
  // public BaseDefenceConfig harpoon = new BaseDefenceConfig();
  // public BaseDefenceConfig mortar = new BaseDefenceConfig();
  public final BaseDefenceConfig forge = new BaseDefenceConfig();
}
