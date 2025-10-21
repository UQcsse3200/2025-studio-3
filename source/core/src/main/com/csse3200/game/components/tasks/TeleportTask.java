package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.ai.tasks.TaskRunner;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;

/**
 * Teleport behaviour component for enemies. After a cooldown, roll a chance and (if successful)
 * play teleport animation, then teleport to a different lane Y. Keeps X constant; chooses Y from
 * the provided laneYs[].
 */
public class TeleportTask extends DefaultTask implements PriorityTask {
  private final float cooldownSec;
  private final float chance;
  private final int maxTeleports;
  private final float[] laneYs;

  // Priority during teleporting is higher than walking but lower than attacks
  private final int teleportPriority = 50;

  // Timing and state
  private float timer;
  private int teleportsDone;

  private enum State {
    NOT_TELEPORTING,
    DISAPPEARING,
    REAPPEARING
  }

  private State currentState;
  private float animTimer = 0.5f;
  private final float teleportAnimTime = 2f; // Duration to hold animation before teleport happens

  /**
   * @param cooldownSec seconds between teleport attempts
   * @param chance probability (0..1) to teleport when cooldown elapses
   * @param maxTeleports max number of teleports (0 = unlimited)
   * @param laneYs array of lane Y positions; must contain at least 2 distinct values
   */
  public TeleportTask(float cooldownSec, float chance, int maxTeleports, float[] laneYs) {
    this.cooldownSec = Math.max(0.01f, cooldownSec);
    this.chance = MathUtils.clamp(chance, 0f, 1f);
    this.maxTeleports = maxTeleports;
    this.laneYs = laneYs != null ? laneYs.clone() : null;
  }

  @Override
  public void create(TaskRunner taskRunner) {
    super.create(taskRunner);
    this.timer = cooldownSec;
  }

  @Override
  public void start() {
    super.start();
    timer = cooldownSec;
    teleportsDone = 0;
    currentState = State.NOT_TELEPORTING;
  }

  /**
   * Return priority dynamically: - High priority during the teleport animation phase - High
   * priority when ready to teleport - -1 otherwise (lets MoveLeftTask run)
   */
  @Override
  public int getPriority() {
    if (maxTeleports > 0 && teleportsDone >= maxTeleports) {
      return -1;
    }
    if (currentState != State.NOT_TELEPORTING) {
      return teleportPriority;
    }
    return readyToTeleport() ? teleportPriority : -1;
  }

  @Override
  public void update() {
    if (maxTeleports > 0 && teleportsDone >= maxTeleports) {
      return;
    }

    if (currentState == State.NOT_TELEPORTING) {
      // Update will only be called if ready to teleport, otherwise TeleportTask has
      // a priority of -1. Therefore, if we aren't teleporting, we should start teleporting.
      currentState = State.DISAPPEARING;
      owner.getEntity().getEvents().trigger("teleportDisappearStart");
    } else if (currentState == State.DISAPPEARING) {
      AnimationRenderComponent animator =
          owner.getEntity().getComponent(AnimationRenderComponent.class);
      // If the animator is null, something went wrong, and we should skip ahead.
      // Otherwise, we wait until the disappear animation is done to teleport.
      if (animator == null || animator.isFinished()) {
        // The start animation is finished.
        currentState = State.REAPPEARING;
        performTeleport();
        owner.getEntity().getEvents().trigger("teleportReappearStart");
      }
    } else if (currentState == State.REAPPEARING) {
      AnimationRenderComponent animator =
          owner.getEntity().getComponent(AnimationRenderComponent.class);
      // If the animator is null, something went wrong, and we should skip ahead.
      // Otherwise, we wait until the reappear animation is done to reset state.
      if (animator == null || animator.isFinished()) {
        // The teleport is finished.
        currentState = State.NOT_TELEPORTING;
      }
    }
  }

  /** Check if teleport conditions are met (cooldown elapsed, chance succeeded, etc.). */
  private boolean readyToTeleport() {
    if (laneYs == null || laneYs.length < 2) return false;
    if (maxTeleports > 0 && teleportsDone >= maxTeleports) return false;

    GameTime time = ServiceLocator.getTimeSource();
    float dt = (time != null) ? time.getDeltaTime() : 1f / 60f;
    timer -= dt;

    if (timer > 0f) return false;
    return MathUtils.random() <= chance;
  }

  /** Actually performs the teleport after animation finishes. */
  private void performTeleport() {
    var entity = owner.getEntity();
    if (entity == null) return;

    Vector2 pos = entity.getPosition();
    if (pos == null) return;

    float currentY = pos.y;
    float targetY = pickDifferentLaneY(currentY);
    if (Math.abs(targetY - currentY) <= 1e-3f) return;

    entity.setPosition(pos.x, targetY);
    teleportsDone++;
    timer = cooldownSec;
  }

  /** Picks a lane Y from laneYs that is different from currentY. */
  private float pickDifferentLaneY(float currentY) {
    float targetY = currentY;
    int attempts = 6;
    while (attempts-- > 0) {
      float candidate = laneYs[MathUtils.random(0, laneYs.length - 1)];
      if (Math.abs(candidate - currentY) > 1e-3f) {
        targetY = candidate;
        break;
      }
    }
    return targetY;
  }
}
