package com.csse3200.game.components;

import com.badlogic.gdx.Gdx;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HitMarkerComponent extends Component {
    private final Logger logger = LoggerFactory.getLogger(HitMarkerComponent.class);
    private float flashTime = 0f;

    @Override
    public void create() {
        logger.info("HitMarkerComponent created for entity: {}", entity);
        entity.getEvents().addListener("hitMarker", this::onHitMarkerStart);
    }

    @Override
    public void update() {
        TextureRenderComponent render = entity.getComponent(TextureRenderComponent.class);
        if (flashTime > 0) {
            flashTime -= Gdx.graphics.getDeltaTime();
            render.colour.set(1, 1, 1, 0.5f);
        } else {
            render.colour.set(1, 1, 1, 1); // Reset to normal colour
            flashTime = 0f; // Ensure it doesn't go negative
        }
    }

    private void onHitMarkerStart(Entity entity) {
        logger.info("Hit marker started for entity: {}", entity);
        flashTime = 0.15f; // Duration of the hit marker flash

    }

}
