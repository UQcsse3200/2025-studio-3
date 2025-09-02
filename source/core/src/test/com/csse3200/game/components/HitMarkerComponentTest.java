package com.csse3200.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.RenderComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(GameExtension.class)
class HitMarkerComponentTest {

    @BeforeEach
    void beforeEach() {
    ServiceLocator.registerPhysicsService(new PhysicsService());
}

    @Test
    void shouldFlash() {
        Entity entity = createTarget();
        entity.getEvents().trigger("hitMarker", entity);
        HitMarkerComponent hitMarker = entity.getComponent(HitMarkerComponent.class);
        assertTrue(hitMarker.flashTime > 0);
    }

    Entity createTarget() {
        Entity target =
                new Entity()
                        .addComponent(new HitMarkerComponent());
        target.create();
        return target;
    }
}
