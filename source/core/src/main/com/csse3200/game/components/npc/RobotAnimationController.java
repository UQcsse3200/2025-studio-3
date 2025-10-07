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
  }

  void animateMoveLeft() {
    currentState = State.MOVE_LEFT;
    if (!belowHalfHealth) {
      animator.startAnimation("moveLeft");
    } else {
      animator.startAnimation("moveLeftDamaged");
    }
  }

  void animateAttack() {
    currentState = State.ATTACK;
    if (!belowHalfHealth) {
      animator.startAnimation("attack");
    } else {
      animator.startAnimation("attackDamaged");
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
        case ATTACK:
          animateAttack();
          break;
      }
    }
  }
}
