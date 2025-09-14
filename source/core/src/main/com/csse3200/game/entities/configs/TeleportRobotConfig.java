package com.csse3200.game.entities.configs;

public class TeleportRobotConfig extends BaseEnemyConfig {
  public float teleportCooldownSeconds = 4f;
  public float teleportChance = 1f;
  public int maxTeleports = 0; // unlimited if <=0

  public float getTeleportCooldownSeconds() {
    return teleportCooldownSeconds;
  }

  public float getTeleportChance() {
    return teleportChance;
  }

  public int getMaxTeleports() {
    return maxTeleports;
  }


}
