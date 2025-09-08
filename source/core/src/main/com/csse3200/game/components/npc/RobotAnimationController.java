package com.csse3200.game.components.npc;

import com.csse3200.game.components.Component;
import com.csse3200.game.rendering.AnimationRenderComponent;

/**
 * This class listens to events relevant to a ghost entity's state and plays the animation when one
 * of the events is triggered.
 */
public class RobotAnimationController extends Component {
  AnimationRenderComponent animator;

  @Override
  public void create() {
    super.create();
    animator = this.entity.getComponent(AnimationRenderComponent.class);
    entity.getEvents().addListener("moveLeftStart", this::animateMoveLeft);
    entity.getEvents().addListener("attackStart", this::animateMoveLeft);
    entity.getEvents().addListener("chaseStart", this::animateChase);
  }

  void animateMoveLeft() {
    animator.startAnimation("chill");
  }

  void animateChase() {
    animator.startAnimation("angry");
  }
}
