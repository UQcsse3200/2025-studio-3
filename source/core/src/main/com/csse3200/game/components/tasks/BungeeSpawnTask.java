package com.csse3200.game.components.tasks;

import com.badlogic.gdx.audio.Sound;
import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ServiceLocator;

/**
 * Spawning behaviour component for bungee robots. Upon spawning, play teleport animation, and don't
 * allow other tasks to run. Once teleport is complete, set priority low and do not run again.
 */
public class BungeeSpawnTask extends DefaultTask implements PriorityTask {
  // Priority during spawn is higher than attack to stop bungee from moving or
  // attacking during the teleport animation.
  private static final int SPAWN_PRIORITY = 200;

  // Stores whether the task is finished
  private boolean finished;

  @Override
  public void start() {
    super.start();
    // Starts the teleport animation
    this.finished = false;
    this.owner.getEntity().getEvents().trigger("teleportReappearStart");
    // Plays the teleport sound
    Sound teleportSound =
        ServiceLocator.getResourceService().getAsset("sounds/teleport_end.mp3", Sound.class);
    teleportSound.play(ServiceLocator.getSettingsService().getSoundVolume() * 0.4f);
  }

  /**
   * Return priority dynamically: - High priority during the teleport animation -1 otherwise (lets
   * MoveLeftTask and RobotAttackTask run)
   */
  @Override
  public int getPriority() {
    if (finished) {
      return -1;
    } else {
      return SPAWN_PRIORITY;
    }
  }

  @Override
  public void update() {
    if (finished) {
      return;
    }

    AnimationRenderComponent animator =
        owner.getEntity().getComponent(AnimationRenderComponent.class);
    // If the animator is null, something went wrong, and we should skip ahead.
    // Otherwise, we wait until the teleport animation is done and then set finished
    if (animator == null || animator.isFinished()) {
      // The animation is finished. Setting finished to true will lower the task priority
      this.finished = true;
    }
  }
}
