package com.csse3200.game.components;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.ProjectileFactory;
import java.util.function.Supplier;

// TODO - documentation
public class ProjectileComponent extends Component {
  private final Supplier<Entity> supplier;

  public ProjectileComponent(String path, int damage) {
    this.supplier = () -> ProjectileFactory.createProjectile(path, damage);
  }

  public Entity getProjectile() {
    return supplier.get();
  }
}
