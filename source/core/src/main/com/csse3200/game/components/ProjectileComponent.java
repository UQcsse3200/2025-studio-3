package com.csse3200.game.components;

/**
 * This class stores information about a projectile entity, i.e., how much damage is dealt on impact.
 */
public class ProjectileComponent extends Component {
  private final int damage;

  public ProjectileComponent(int damage) {
    this.damage = damage;
  }

  /**
   * Returns the amount of damage dealt
   * @return projectile damage value
   */
  public int getDamage() {
    return damage;
  }
}
