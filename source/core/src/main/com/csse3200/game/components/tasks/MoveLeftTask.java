package com.csse3200.game.components.tasks;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.ai.tasks.Task;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ServiceLocator;

/**
 * Wander around by moving a random position within a range of the starting position. Wait a little
 * bit between movements. Requires an entity with a PhysicsMovementComponent.
 */
public class MoveLeftTask extends DefaultTask implements PriorityTask {
  private final float moveSpeed;
  private Vector2 startPos;
  private MovementTask movementTask;
  private Task currentTask;
  private float animationNumLoops = 0f; // for playing sounds

  /**
   * @param moveSpeed The speed that the entity will move left at.
   */
  public MoveLeftTask(float moveSpeed) {
    this.moveSpeed = moveSpeed;
  }

  @Override
  public int getPriority() {
    return 1; // Low priority task
  }

  @Override
  public void start() {
    super.start();
    startPos = owner.getEntity().getPosition();
    owner.getEntity().getEvents().trigger("moveLeftStart");
  }

  @Override
  public void update() {
    PhysicsComponent phys = owner.getEntity().getComponent(PhysicsComponent.class);
    if (phys == null || phys.getBody() == null) return;

    // Horizontal-only: move left, never allow vertical drift
    phys.getBody().setLinearVelocity(-moveSpeed, 0f);

    // play footstep sounds
    float playTime =
        owner
            .getEntity()
            .getComponent(AnimationRenderComponent.class)
            .getCurrentAnimationPlayTime();
    // Play a footstep sound every 0.4 seconds based on the animation’s elapsed play time
    // 0.4 picked based on sprite sheet and animation speed
    if (playTime > animationNumLoops * 0.4f) {
      Sound stepSound =
          ServiceLocator.getResourceService().getAsset("sounds/robot_footstep.mp3", Sound.class);
      stepSound.play(1.0f);
      animationNumLoops++;
    }
  }

  // This was used to switch between moving and waiting when this was wanderTask.
  // We might use this to implement attacking.
  public void swapTask(Task newTask) {
    if (currentTask != null) {
      currentTask.stop();
    }
    currentTask = newTask;
    currentTask.start();
  }

  public Task getCurrentTask() {
    return currentTask;
  }

  public Vector2 getStartPos() {
    return startPos;
  }

  public float getMoveSpeed() {
    return moveSpeed;
  }

  public MovementTask getMovementTask() {
    return movementTask;
  }
}
