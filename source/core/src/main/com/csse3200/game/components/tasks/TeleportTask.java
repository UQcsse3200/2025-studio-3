package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;

/**
 * Teleport behaviour component for enemies. Every fixed cooldown, roll a chance and (if successful)
 * jump to a different lane Y. Keeps X constant; chooses Y from the provided laneYs[].
 */
public class TeleportTask extends Component {
  private final float cooldownSec;
  private final float chance;
  private final int maxTeleports;
  private final float[] laneYs;

  private float timer;
  private int teleportsDone;

  /**
   * @param cooldownSec constant seconds between teleport attempts
   * @param chance probability 0..1 when cooldown elapses
   * @param laneYs array of lane Y positions; must contain at least 2 distinct values
   */
  public TeleportTask(float cooldownSec, float chance, int maxTeleports, float[] laneYs) {
    this.cooldownSec = Math.max(0.01f, cooldownSec);
    this.chance = MathUtils.clamp(chance, 0f, 1f);
    this.maxTeleports = maxTeleports;
    this.laneYs = laneYs != null ? laneYs.clone() : null;
  }

  @Override
  public void create() {
    timer = cooldownSec;
  }

  @Override
  public void update() {
    if (laneYs == null || laneYs.length < 2) return;
    if (maxTeleports > 0 && teleportsDone >= maxTeleports) return;

    GameTime time = ServiceLocator.getTimeSource();
    float dt = (time != null) ? time.getDeltaTime() : 1f / 60f;
    timer -= dt;
    if (timer > 0f) return;

    timer = cooldownSec; // reset for next attempt
    if (MathUtils.random() > chance) return;

    // Get current position from the entity.
    Vector2 pos = entity.getPosition();
    if (pos == null) return;

    // Choose a different lane Y
    float currentY = pos.y;
    float targetY = currentY;
    int attempts = 6;
    while (attempts-- > 0) {
      float cand = laneYs[MathUtils.random(0, laneYs.length - 1)];
      if (Math.abs(cand - currentY) > 1e-3f) {
        targetY = cand;
        break;
      }
    }
    if (Math.abs(targetY - currentY) <= 1e-3f) return; // no different lane found

    // Teleport: keep X, change Y.
    entity.setPosition(pos.x, targetY);
    teleportsDone++;
  }

  /**
   * Returns true if no other entity is already occupying roughly (x,targetY). Uses the entity's
   * scaled height as a proxy for tile size to derive tolerances.
   */
}
