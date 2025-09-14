package com.csse3200.game.services;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.AnimationRenderComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemEffectsService {
  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.services.ItemEffectsService.class);

  public static final String GRENADE = "GRENADE";
  public static final String BUFF = "BUFF";
  public static final String COFFEE = "COFFEE";
  public static final String EMP = "EMP";
  public static final String NUKE = "NUKE";

  public static final int loopDuration = 0;

  // private final ItemEffect itemEffect;

  public static void spawnEffect(
      TextureAtlas atlas,
      String animatorName,
      Vector2 position,
      int scale,
      float frameDuration,
      Animation.PlayMode playMode) {

    if (atlas == null) {
      logger.error("Atlas not loaded: {}", atlas);
      return;
    }
    AnimationRenderComponent animator = new AnimationRenderComponent(atlas);
    Entity effect = new Entity().addComponent(animator);
    //        Vector2 position = entity.getCenterPosition();
    //        effect.setPosition(position.x, position.y);
    effect.setPosition(position.x, position.y);
    effect.setScale(scale, scale);
    animator.addAnimation(animatorName, frameDuration, playMode);
    animator.startAnimation(animatorName);

    //        effect.getEvents().addListener("update", (EventListener1<Float>) dt -> {
    //            if (animator.isFinished() && playMode != Animation.PlayMode.LOOP) {
    //                effect.dispose();
    //            }
    //        });
    effect.addComponent(
        new Component() {
          float age = 0f;

          @Override
          public void update() {
            if (playMode == Animation.PlayMode.LOOP) {
              if (animator.isFinished()) {
                effect.dispose();
              }
              return;
            }

            if (loopDuration > 0f) {
              age += ServiceLocator.getTimeSource().getDeltaTime();
              if (age >= loopDuration) {
                effect.dispose();
              }
            }
          }
        });

    // animator.stopAnimation();

    // Stage stage = ServiceLocator.getRenderService().getStage();
    ServiceLocator.getEntityService().register(effect);
  }

  public void playEffect(String itemName, Vector2 position, int tileSize) {
    switch (itemName) {
      //        case "buff":
      //            spawnEffect(source, "file", "name", 0.1f, Animation.PlayMode.LOOP);
      //            break;
      //        case "coffee":
      //            spawnEffect(source, "file", "name", 0.1f, Animation.PlayMode.LOOP);
      //            break;
      //        case "emp":
      //            spawnEffect(source, "file", "name", 0.1f, Animation.PlayMode.NORMAL);
      //            break;
      case "grenade":
        spawnEffect(
            ServiceLocator.getResourceService()
                .getAsset("images/grenade.atlas", TextureAtlas.class),
            "grenade",
            position,
            tileSize * 3,
            0.1f,
            Animation.PlayMode.NORMAL);
        break;
        //       case "nuke":
        //            spawnEffect(source, "file", "name", 0.1f, Animation.PlayMode.NORMAL);
        //            break;
    }
  }
}
