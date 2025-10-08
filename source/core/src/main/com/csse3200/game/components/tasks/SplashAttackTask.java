package com.csse3200.game.components.tasks;

import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

public class SplashAttackTask extends TargetDetectionTasks implements PriorityTask {
  private final float splashCooldown;
  private float timeSinceLastSplash;

  public SplashAttackTask(float attackRange, float splashCooldown) {
    super(attackRange);
    this.splashCooldown = splashCooldown;
    this.timeSinceLastSplash = splashCooldown;
  }

  @Override
  public void start() {
    super.start();
    owner.getEntity().getEvents().trigger("splashAttack");
    timeSinceLastSplash = 0f;
    stop();
  }

  @Override
  public int getPriority() {
    timeSinceLastSplash += ServiceLocator.getTimeSource().getDeltaTime();

    if (timeSinceLastSplash < splashCooldown) {
      return -1;
    }
    return super.getPriority();
  }

  @Override
  public int getActivePriority(float distance, Entity target) {
    return -1;
  }

  @Override
  public int getInactivePriority(float distance, Entity target) {
    if (target != null) {
      return 10;
    }
    return -1;
  }
}
