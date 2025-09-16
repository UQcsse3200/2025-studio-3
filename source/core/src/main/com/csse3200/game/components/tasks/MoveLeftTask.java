package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.ai.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wander around by moving a random position within a range of the starting position. Wait a little
 * bit between movements. Requires an entity with a PhysicsMovementComponent.
 */
public class MoveLeftTask extends DefaultTask implements PriorityTask {
  private static final Logger logger = LoggerFactory.getLogger(MoveLeftTask.class);

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

    // Tells the enemy robot to move left at a set speed.
    movementTask = new MovementTask(new Vector2(0, startPos.y), moveSpeed);
    movementTask.create(owner);

    movementTask.start();
    currentTask = movementTask;

    this.owner.getEntity().getEvents().trigger("moveLeftStart");
  }

  @Override
  public void update() {
    // do nothing
  }

  // This was used to switch between moving and waiting when this was wanderTask.
  // We might use this to implement attacking.
  private void swapTask(Task newTask) {
    if (currentTask != null) {
      currentTask.stop();
    }
    currentTask = newTask;
    currentTask.start();
  }
}
