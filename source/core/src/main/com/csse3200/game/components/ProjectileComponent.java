package com.csse3200.game.components;

import com.csse3200.game.entities.Entity;

// TODO - documentation
public class ProjectileComponent extends Component {
    private final Entity projectile;

    public ProjectileComponent(Entity projectile) {
        this.projectile = projectile;
    }

    public Entity getProjectile() {
        return projectile;
    }
}
