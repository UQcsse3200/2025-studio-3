package com.csse3200.game.components.items;

/** Represents a buff item that can be used to temporarily double damage. */
public class BuffComponent extends Item {
  /** Creates a new buff component with default properties. */
  public BuffComponent() {
    super("Buff", "Temporarily doubles damage for 30s. One time use.", "buff", 30);
  }

  //  @Override
  //  protected void effectOnPlaced(Entity[] entities) {

  //      Texture explosionTexture = new Texture("grenade.png");
  //      Entity explosion = new Entity().addComponent(new
  // AnimationRenderComponent(explosionTexture));
  //      AnimationRenderComponent animator =
  // explosion.getComponent(AnimationRenderComponent.class);
  //      animator.addAnimation("explosion", 0.1f);
  //      animator.startAnimation("grenade");

  // listener

  // add explosion

}
