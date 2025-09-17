package com.csse3200.game.entities.configs;

/** Defines all NPC configs to be loaded by the NPC Factory. */
public class NPCConfigs {
  public final BaseEnemyConfig fastRobot = new BaseEnemyConfig();
  public final BaseEnemyConfig standardRobot = new BaseEnemyConfig();
  public final BaseEnemyConfig tankyRobot = new BaseEnemyConfig();
  public final BaseEnemyConfig bungeeRobot = new BaseEnemyConfig();
  public final BaseEntityConfig ghost = new BaseEntityConfig();
  public final GhostKingConfig ghostKing = new GhostKingConfig();
  public final BaseDefenderConfig slingshooter = new BaseDefenderConfig();

  public TeleportRobotConfig teleportRobot = new TeleportRobotConfig();
  // public BaseDefenceConfig trebuchet = new BaseDefenceConfig();
  // public BaseDefenceConfig spearman = new BaseDefenceConfig();
  // public BaseDefenceConfig harpoon = new BaseDefenceConfig();
  // public BaseDefenceConfig mortar = new BaseDefenceConfig();
  public final BaseGeneratorConfig furnace = new BaseGeneratorConfig();
}
