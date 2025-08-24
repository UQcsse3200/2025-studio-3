package com.csse3200.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HitMarkerComponent extends Component {
    private final Logger logger = LoggerFactory.getLogger(HitMarkerComponent.class);
    private static final float FLASH_DURATION = 0.13f;
    private static final Color FLASH_COLOUR = new Color(0.8f, 0, 0, 0.9f);
    private float flashTime = 0f;

    @Override
    public void create() {
        logger.info("HitMarkerComponent created for entity: {}", entity);
        entity.getEvents().addListener("hitMarker", this::onHitMarkerStart);
    }

    @Override
    public void update() {
        TextureRenderComponent render = entity.getComponent(TextureRenderComponent.class);
        if (flashTime > 0f) {
            flashTime -= Gdx.graphics.getDeltaTime();
            render.colour.set(FLASH_COLOUR);
            return;
        }
        render.colour.set(Color.WHITE); // Reset to normal colour
    }

    private void onHitMarkerStart(Entity entity) {
        logger.info("Hit marker started for entity: {}", entity);
        flashTime = FLASH_DURATION;

    }

}
