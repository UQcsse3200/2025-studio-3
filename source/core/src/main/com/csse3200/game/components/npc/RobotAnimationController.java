package com.csse3200.game.components.npc;

import com.csse3200.game.components.Component;
import com.csse3200.game.rendering.AnimationRenderComponent;

/**
 * This class listens to events relevant to a ghost entity's state and plays the animation when one
 * of the events is triggered.
 */
public class RobotAnimationController extends Component {
  AnimationRenderComponent animator;

  private boolean belowHalfHealth;

  private enum State {
    MOVE_LEFT,
    ATTACK,
    TELEPORT,
    EXPLODE,
    SHOOT,
    NONE
  }

  private State currentState;

  @Override
  public void create() {
    super.create();
    belowHalfHealth = false;
    currentState = State.NONE;
    animator = this.entity.getComponent(AnimationRenderComponent.class);
    entity.getEvents().addListener("moveLeftStart", this::animateMoveLeft);
    entity.getEvents().addListener("attackStart", this::animateAttack);
    entity.getEvents().addListener("updateHealth", this::updateHealth);
    entity.getEvents().addListener("teleportStart", this::animateTeleport);
    entity
        .getEvents()
        .addListener(
            "bomberPreExplode",
            this::animatePreExplosion); // Explosion will have to be added later.
  }

  void animatePreExplosion() {
    currentState = State.EXPLODE; // or a new state like CHARGING
    animator.startAnimation("explosion"); // e.g. flickering or glowing animation
  }

  void animateMoveLeft() {
    currentState = State.MOVE_LEFT;
    // Once teleporting task priority is fixed,
    // this may have to check if the current state is
    // TELEPORT, and if it is, wait until teleport is done to start walking.
    if (!belowHalfHealth) {
      animator.startAnimation("moveLeft");
    } else {
      animator.startAnimation("moveLeftDamaged");
    }
  }

  void animateTeleport() {
    currentState = State.TELEPORT;
    if (!belowHalfHealth) {
      animator.startAnimation("teleport");
    } else {
      animator.startAnimation("teleportDamaged");
    }
  }

  void animateAttack() {
    // Once teleporting task priority is fixed,
    // this may have to check if the current state is
    // TELEPORT, and if it is, wait until teleport is done to start attacking.
    currentState = State.ATTACK;
    if (!belowHalfHealth) {
      animator.startAnimation("attack");
    } else {
      animator.startAnimation("attackDamaged");
    }
  }

  // The gunner animation is kind of inconsistent, but this solution works
  // Gunner animations could use a second pass after the gunner targeting kinks have been ironed
  // out.
  void animateShoot() {
    currentState = State.SHOOT;
    if (!belowHalfHealth) {
      animator.startAnimation("shoot");
    } else {
      animator.startAnimation("shootDamaged");
    }
  }

  void updateHealth(int health, int maxHealth) {
    if (health <= maxHealth / 2) {
      belowHalfHealth = true;
      // Updates the animation.
      switch (currentState) {
        case MOVE_LEFT:
          animateMoveLeft();
          break;
        case TELEPORT:
          animateTeleport();
          break;
        case ATTACK:
          animateAttack();
          break;
        case EXPLODE:
          animatePreExplosion();
          break;
        case SHOOT:
          animateShoot();
          break;
      }
    }
  }
}
