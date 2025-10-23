package com.csse3200.game.components.npc;

import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.Component;

/**
 * A component to watch the given entity's health, and trigger the "spawnMinion"
 * event when the health goes below a threshold fraction of the maximum.
 */
public class CarrierHealthWatcherComponent extends Component {
  private final float thresholdFrac;
  private boolean triggered = false;
  private int maxHp;

  /**
   * Initialises the health watcher component with a threshold. This threshold should be
   * between 0 and 1, and will be clamped to this if not. When the entity's health
   * falls below maxHp * thresholdFrac the minion will be spawned.
   * @param thresholdFrac The threshold. Should be between 0 and 1.
   */
  public CarrierHealthWatcherComponent(float thresholdFrac) {
    this.thresholdFrac = Math.clamp(thresholdFrac, 0f, 1f);
  }

  /**
   * Creation function. Stores the maximum health of the entity
   */
  @Override
  public void create() {
    CombatStatsComponent stats = entity.getComponent(CombatStatsComponent.class);
    maxHp = stats != null ? stats.getHealth() : 1; // fallback
  }

  /**
   * Update function. When the entity goes below half health, triggers the
   * "spawnMinion" event listener.
   */
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
