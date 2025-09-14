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
  private final float chance; // 0..1
  private final int maxTeleports; // <=0 means unlimited
  private float[] laneYs; // may be null; we'll derive if needed

  private float timer;
  private int teleportsDone;
  private boolean lanesInit = false; // ⬅️ new

  public TeleportTask(float cooldownSec, float chance, int maxTeleports, float[] laneYs) {
    this.cooldownSec = Math.max(0.01f, cooldownSec);
    this.chance = MathUtils.clamp(chance, 0f, 1f);
    this.maxTeleports = maxTeleports;
    this.laneYs = (laneYs != null) ? laneYs.clone() : null;
  }

  @Override
  public void create() {
    timer = cooldownSec;
  }

  @Override
  public void update() {
    // ⬇️ lazily build 5 lanes if not provided
    if (!lanesInit) {
      initLanesIfNeeded();
    }

    if (laneYs == null || laneYs.length < 2) return;
    if (maxTeleports > 0 && teleportsDone >= maxTeleports) return;

    GameTime time = ServiceLocator.getTimeSource();
    float dt = (time != null) ? time.getDeltaTime() : 1f / 60f;
    timer -= dt;
    if (timer > 0f) return;

    timer = cooldownSec; // reset for next attempt
    if (MathUtils.random() > chance) return;

    Vector2 pos = entity.getPosition();
    if (pos == null) return;

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
    if (Math.abs(targetY - currentY) <= 1e-3f) return;

    entity.setPosition(pos.x, targetY);
    teleportsDone++;
  }

  // ⬇️ NEW: derive 5 lanes centered around current Y using the entity's scaled height as tile size
  private void initLanesIfNeeded() {
    lanesInit = true;
    if (laneYs != null) return;

    if (entity == null || entity.getPosition() == null) return;
    float tile = Math.max(0.01f, entity.getScale().y);
    float y0 = entity.getPosition().y;

    // Build 5 lanes: y0-2t, y0-t, y0, y0+t, y0+2t  (lane count fixed at 5)
    laneYs = new float[5];
    for (int i = 0; i < 5; i++) {
      laneYs[i] = y0 + (i - 2) * tile;
    }
  }

  // (rest of your helpers unchanged)
}
