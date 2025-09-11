package com.csse3200.game.components.tasks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.HitboxComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO : integrate with attack system team

/**
 * Allows an entity to attack the closest target entity from a list of potential targets. This task
 * runs when there is a visible target within the entities range of attack
 */
public class RobotAttackTask extends RobotTargetDetectionTasks {
  private static final Logger logger = LoggerFactory.getLogger(RobotAttackTask.class);
  private static final float TIME_BETWEEN_ATTACKS = 4f; // seconds
  private float timeLeft = 0f;

  /**
   * Creates an attack task
   *
   * @param attackRange the maximum distance the entity can find a target to attack
   */
  public RobotAttackTask(float attackRange, short targetLayer) {
    super(attackRange, targetLayer);
  }

  /**
   * Starts the attack task. The closest visible target within the entity's attack range is found
   * and ATTACK LOGIC BEGINS.
   */

  @Override
  public void start() {
    super.start();
    Entity target = getNearestVisibleTarget();

    if (target == null) {
      return;
    }
    timeLeft = TIME_BETWEEN_ATTACKS;
    this.owner.getEntity().getEvents().trigger("attackStart");
  }

  /**
   * Updates the task each game frame
   */
  @Override
  public void update() {
    Entity target = getNearestVisibleTarget();

    if (target == null) {
      stop();
      return;
    }
    if (timeLeft - Gdx.graphics.getDeltaTime() > 0) {
      timeLeft -= Gdx.graphics.getDeltaTime();
      return;
    }
    //    logger.info("Attacking target: " + target);
    Fixture meFixture = owner.getEntity().getComponent(HitboxComponent.class).getFixture();
    Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();
    this.owner.getEntity().getEvents().trigger("collisionStart", meFixture, targetFixture);
  }

  /**
   * Stops the attack
   */
  @Override
  public void stop() {
    super.stop();
  }
}
