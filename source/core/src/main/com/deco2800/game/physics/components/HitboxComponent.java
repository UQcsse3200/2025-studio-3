package com.deco2800.game.physics.components;

/**
 * Physics comp
 */
public class HitboxComponent extends ColliderComponent {
  @Override
  public void create() {
    setSensor(true);
    super.create();
  }
}
