package com.csse3200.game.components.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

public class MoveLeftComponent extends Component {
    private final float speed;

    /**
     * the speed which the projectile moves left
     * @param speed
     */
    public MoveLeftComponent(float speed) {
        this.speed = speed;
    }

    @Override
    public void update() {
        float delta = ServiceLocator.getTimeSource().getDeltaTime();
        Entity entity = getEntity();
        Vector2 pos = entity.getPosition();
        entity.setPosition(pos.x - speed * delta, pos.y);
    }
}
