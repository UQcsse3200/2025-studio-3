package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
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
      Animation.PlayMode playMode,
      boolean movingAnimation,
      Vector2 finalPosition,
      float totalEffectTime) {

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
          float ageAfterArrival = 0f;
          boolean arrived = false;

          @Override
          public void update() {
            float dt = ServiceLocator.getTimeSource().getDeltaTime();
            age += dt;

            //            if (playMode == Animation.PlayMode.LOOP) {
            //              if (animator.isFinished()) {
            //                effect.dispose();
            //              }
            //              return;
            //            }

            if (movingAnimation && !arrived) {
              // Moving phase
              if (age <= 1f) {
                return;
              }

              // Move linearly to a lower corner
              float t = Math.min((age - 1f), 1f);
              float x = position.x + (finalPosition.x - position.x) * t;
              float y = position.y + (finalPosition.y - position.y) * t;
              effect.setPosition(x, y);

              if (t >= 1f) {
                arrived = true;
                ageAfterArrival = age;
              } else {
                return;
              }
            }
            if (playMode == Animation.PlayMode.NORMAL) {
              // logger.info("still there");
              //              if (animator.isFinished()) {
              //                logger.info("finished");
              //                effect.dispose();
              //              } else
              if (totalEffectTime > 0f && age >= totalEffectTime) {
                // ageAfterArrival += dt;
                // logger.info("age after arrival: " + ageAfterArrival);
                // if (ageAfterArrival >= (totalEffectTime - age)) {
                logger.info("finished 2");
                // effect.dispose();
                Gdx.app.postRunnable(effect::dispose);
              }
            }
          }
        });

    //            if (loopDuration > 0f) {
    //              age += ServiceLocator.getTimeSource().getDeltaTime();
    //              if (age >= loopDuration) {
    //                  effect.dispose();
    // return;
    //              }
    //            }

    //            if (playMode == Animation.PlayMode.NORMAL && animator.isFinished()) {
    //              effect.dispose();
    //            }

    // animator.stopAnimation();

    // Stage stage = ServiceLocator.getRenderService().getStage();
    ServiceLocator.getEntityService().register(effect);
  }

  public void playEffect(String itemName, Vector2 position, int tileSize, Vector2 bottomCorner) {
    switch (itemName) {
      case "buff":
        position.x = position.x - tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService().getAsset("images/buff.atlas", TextureAtlas.class),
            "buff",
            position,
            tileSize * 3,
            0.1f,
            Animation.PlayMode.NORMAL,
            true,
            bottomCorner,
            30f);
        break;
      case "coffee":
        position.x = position.x - tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService().getAsset("images/coffee.atlas", TextureAtlas.class),
            "coffee",
            position,
            tileSize * 3,
            0.1f,
            Animation.PlayMode.NORMAL,
            true,
            bottomCorner,
            30f);
        break;
      case "emp":
        position.x = position.x - tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService().getAsset("images/emp.atlas", TextureAtlas.class),
            "emp",
            position,
            tileSize * 3,
            0.1f,
            Animation.PlayMode.NORMAL,
            false,
            bottomCorner,
            1.5f);
        break;
      case "grenade":
        position.x = position.x - tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService()
                .getAsset("images/grenade.atlas", TextureAtlas.class),
            "grenade",
            position,
            tileSize * 3,
            0.1f,
            Animation.PlayMode.NORMAL,
            false,
            bottomCorner,
            1.5f);
        break;
      case "nuke":
        position.x = position.x - 2 * tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService().getAsset("images/nuke.atlas", TextureAtlas.class),
            "nuke",
            position,
            tileSize * 5,
            0.1f,
            Animation.PlayMode.NORMAL,
            false,
            bottomCorner,
            1.5f);
        break;
    }
  }
}
