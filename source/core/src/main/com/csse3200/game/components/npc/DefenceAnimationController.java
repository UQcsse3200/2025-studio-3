package com.csse3200.game.components.npc;

import com.csse3200.game.components.Component;
import com.csse3200.game.rendering.AnimationRenderComponent;

/**
 * This class listens to events relevant to a ghost entity's state and plays the animation when one
 * of the events is triggered.
 */
public class DefenceAnimationController extends Component {
  AnimationRenderComponent animator;

  @Override
  public void create() {
    super.create();
    animator = this.entity.getComponent(AnimationRenderComponent.class);
    entity.getEvents().addListener("idleStart", this::animateIdle);
    entity.getEvents().addListener("attackStart", this::animateAttack);
    entity.getEvents().addListener("attackDamaged", this::animateHit);
  }

  void animateIdle() {
    animator.startAnimation("idle");
  }

  void animateAttack() {
    animator.startAnimation("attack");
  }

  void animateHit() {
    animator.startAnimation("hit");
  }
}
