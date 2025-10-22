package com.csse3200.game.components;

import com.csse3200.game.entities.ProjectileType;

/**
 * A component used to tag a projectile entity with its type, to allow the game to handle different
 * projectile behaviours.
 */
public class ProjectileTagComponent extends Component {
  private final ProjectileType type;

  /**
   * Creates a new {@code ProjectileTagComponent} with the specified projectile type.
   *
   * @param type The {@link ProjectileType} this projectile belongs to.
   */
  public ProjectileTagComponent(ProjectileType type) {
    this.type = type;
  }

  /**
   * Gets the projectile's type.
   *
   * @return The {@link ProjectileType} of this projectile.
   */
  public ProjectileType getType() {
    return type;
  }
}
