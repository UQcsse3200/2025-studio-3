package com.csse3200.game.components.tasks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.ServiceLocator;

/**
 * Allows an entity to attack the closest target entity from a list of potential targets. This task
 * runs when there is a visible target within the entities range of attack
 */
public class RobotAttackTask extends RobotTargetDetectionTasks {
  private static final float TIME_BETWEEN_ATTACKS = 2f; // seconds
  private static final float TIME_BETWEEN_ATTACK_SOUNDS = 0.5f; // seconds
  private float timeLeft = 0f;
  private float soundTimeLeft = 0f;

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
    soundTimeLeft = TIME_BETWEEN_ATTACK_SOUNDS;
    this.owner.getEntity().getEvents().trigger("attackStart");
    PhysicsComponent phys = owner.getEntity().getComponent(PhysicsComponent.class);
    if (phys == null || phys.getBody() == null) return;
    phys.getBody().setLinearVelocity(0f, 0f);
  }

  /** Updates the task each game frame */
  @Override
  public void update() {
    Entity target = getNearestVisibleTarget();

    if (target == null) {
      stop();
      return;
    }
    float delta = Gdx.graphics.getDeltaTime();
    timeLeft -= delta;
    soundTimeLeft -= delta;

    // play sound every 0.5s regardless of attack timing
    if (soundTimeLeft - Gdx.graphics.getDeltaTime() < 0) {
      Sound attackSound =
          ServiceLocator.getResourceService().getAsset("sounds/robot-attack.mp3", Sound.class);
      attackSound.play(ServiceLocator.getSettingsService().getSoundVolume() * 0.3f);
      soundTimeLeft = TIME_BETWEEN_ATTACK_SOUNDS;
    }
    if (timeLeft < 0) {
      Fixture meFixture = owner.getEntity().getComponent(HitboxComponent.class).getFixture();
      Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();
      if (meFixture == null || targetFixture == null) {
        return;
      }

      if (target.getDeathFlag()) {
        return;
      }
      this.owner.getEntity().getEvents().trigger("collisionStart", meFixture, targetFixture);
      timeLeft = TIME_BETWEEN_ATTACKS;
    }
  }
}
