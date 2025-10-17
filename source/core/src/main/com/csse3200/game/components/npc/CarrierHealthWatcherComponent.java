package com.csse3200.game.components.npc;

import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.Component;

public class CarrierHealthWatcherComponent extends Component {
  private final float thresholdFrac;
  private boolean triggered = false;
  private int maxHp;

  public CarrierHealthWatcherComponent(float thresholdFrac) {
    this.thresholdFrac = Math.max(0f, Math.min(1f, thresholdFrac));
  }

  @Override
  public void create() {
    CombatStatsComponent stats = entity.getComponent(CombatStatsComponent.class);
    maxHp = stats != null ? stats.getHealth() : 1; // fallback
  }

  @Override
  public void update() {
    if (triggered) return;
    CombatStatsComponent stats = entity.getComponent(CombatStatsComponent.class);
    if (stats == null) return;

    int cur = stats.getHealth();
    if (cur <= maxHp * thresholdFrac) {
      triggered = true;
      entity.getEvents().trigger("spawnMinion");
    }
  }
}
