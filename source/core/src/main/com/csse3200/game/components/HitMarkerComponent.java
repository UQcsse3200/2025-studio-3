package com.csse3200.game.components;

import com.csse3200.game.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HitMarkerComponent extends Component {
    private final Logger logger = LoggerFactory.getLogger(HitMarkerComponent.class);

    @Override
    public void create() {
        logger.info("HitMarkerComponent created for entity: {}", entity);
        entity.getEvents().addListener("hitMarker", this::onHitMarkerStart);
    }

    private void onHitMarkerStart(Entity entity) {
        logger.info("Hit marker started for entity: {}", entity);

    }

}
