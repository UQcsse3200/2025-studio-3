package com.csse3200.game.components.projectiles;

import com.csse3200.game.components.Component;
import com.csse3200.game.components.ProjectileTagComponent;
import com.csse3200.game.services.ServiceLocator;

public class LifetimeComponent extends Component{
    private final float lifetime;
    private float elapsed = 0f;

    public LifetimeComponent(float lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public void update() {
        elapsed += ServiceLocator.getTimeSource().getDeltaTime();
        if (elapsed >= lifetime && entity.getComponent(ProjectileTagComponent.class) != null) {
            entity.getEvents().trigger("despawnSlingshot", entity);
        }
    }
}
