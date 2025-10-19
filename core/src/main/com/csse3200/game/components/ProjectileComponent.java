package com.csse3200.game.components;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.ProjectileFactory;
import java.util.function.Supplier;

/** Component for supplying the level area with an entity's associated projectile entity */
public class ProjectileComponent extends Component {
  private final Supplier<Entity> supplier;

  /**
   * Constructs the component's supplier for later use
   *
   * @param path the file location of the projectile's sprite
   * @param damage damage value of the projectile
   */
  public ProjectileComponent(String path, int damage) {
    this.supplier = () -> ProjectileFactory.createProjectile(path, damage);
  }

  /**
   * Gets the projectile set for this component
   *
   * @return projectile entity (from supplier)
   */
  public Entity getProjectile() {
    return supplier.get();
  }
}
