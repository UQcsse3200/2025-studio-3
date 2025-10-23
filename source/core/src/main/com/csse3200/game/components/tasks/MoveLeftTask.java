package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.ai.tasks.Task;
import com.csse3200.game.physics.components.PhysicsComponent;

/**
 * Moves to the left at a given movement speed. Also triggers the moveLeft animation.
 * This task has low priority so will only be called if all other tasks (attack, teleport etc.)
 * are invalid.
 */
public class MoveLeftTask extends DefaultTask implements PriorityTask {
  private final float moveSpeed;
  private Vector2 startPos;
  private MovementTask movementTask;
  private Task currentTask;

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
    // Triggers animation
    owner.getEntity().getEvents().trigger("moveLeftStart");
  }

  @Override
  public void update() {
    PhysicsComponent phys = owner.getEntity().getComponent(PhysicsComponent.class);
    if (phys == null || phys.getBody() == null) return;

    // Horizontal-only: move left, never allow vertical drift
    phys.getBody().setLinearVelocity(-moveSpeed, 0f);
  }

  /** This was used to switch between moving and waiting when this was wanderTask,
   * but is no longer being used. Consider deleting */
  public void swapTask(Task newTask) {
    if (currentTask != null) {
      currentTask.stop();
    }
    currentTask = newTask;
    currentTask.start();
  }

  // The following are left-overs from box boy. They are no longer being used.

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
