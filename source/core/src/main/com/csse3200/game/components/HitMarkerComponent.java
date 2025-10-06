package com.csse3200.game.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.rendering.RenderComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HitMarkerComponent extends Component {
  private final Logger logger = LoggerFactory.getLogger(HitMarkerComponent.class);
  protected static final float FLASH_DURATION = 0.13f;
  protected static final Color FLASH_COLOUR = new Color(0.8f, 1f, 0, 0.2f);
  protected float flashTime = 0f;
  protected RenderComponent render;

  @Override
  public void create() {
    //    logger.info("HitMarkerComponent created for entity: {}", entity);
    entity.getEvents().addListener("hitMarker", this::onHitMarkerStart);
    render = entity.getComponent(TextureRenderComponent.class);
    if (render == null) {
      render = entity.getComponent(AnimationRenderComponent.class);
    }
  }

  @Override
  public void update() {
    if (render == null) {
      return;
    }

    if (flashTime > 0f) {
      flashTime -= Gdx.graphics.getDeltaTime();
      render.setColour(FLASH_COLOUR);
      return;
    }
    render.setColour(Color.WHITE); // Reset to normal colour
  }

  private void onHitMarkerStart(Entity entity) {
    //    logger.info("Hit marker started for entity: {}", entity);
    flashTime = FLASH_DURATION;
  }
}
