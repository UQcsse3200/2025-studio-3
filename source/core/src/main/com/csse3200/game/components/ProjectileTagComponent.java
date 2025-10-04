package com.csse3200.game.components;

import com.csse3200.game.entities.ProjectileType;

public class ProjectileTagComponent extends Component {
  private final ProjectileType type;

  public ProjectileTagComponent(ProjectileType type) {
    this.type = type;
  }

  public ProjectileType getType() {
    return type;
  }
}
