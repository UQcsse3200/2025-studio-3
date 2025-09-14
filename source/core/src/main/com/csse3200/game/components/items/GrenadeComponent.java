package com.csse3200.game.components.items;

/** Represents a grenade item that can be used to destroy a 3x3 area. */
public class GrenadeComponent extends Item {
  public GrenadeComponent() {
    super("Grenade", "Destroy everything within a 3x3 square area. One time use.", "grenade", 30);
  }

  //  @Override
  //  protected void effectOnPlaced(Entity[] entities) {
  //
  //      Texture explosionTexture = new Texture("grenade.png");
  //      Entity explosion = new Entity().addComponent(new
  // AnimationRenderComponent(explosionTexture));
  //      AnimationRenderComponent animator =
  // explosion.getComponent(AnimationRenderComponent.class);
  //      animator.addAnimation("explosion", 0.1f);
  //      animator.startAnimation("grenade");

  // need to get item location and tile size

  // listener

  // add explosion

  // }
}
