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

/**
 * ItemEffectsService is a centralised helper for spawning animations for Items used in-level.
 *
 * <p>Effects are implemented as an Entity with an AnimationRenderComponent.
 */
public class ItemEffectsService {
  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.services.ItemEffectsService.class);

  /**
   * Spawns the animation effect for the particular Item.
   *
   * @param atlas Texture atlas that contains the frames for the animation
   * @param animatorName Name of the animation.
   * @param position Initial world position where the effect entity is spawned
   * @param scale Uniform scale applied to the effect (world units)
   * @param frameDuration Duration per animation frame (seconds)
   * @param playMode Animation PlayMode (eg NORMAL, LOOP etc)
   * @param movingAnimation If true the effect will move from position to finalPosition
   * @param finalPosition Destination position when movingAnimation is true (ignored otherwise)
   * @param totalEffectTime Total lifetime of the effect (seconds) when paylMode == NORMAL
   */
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

    // Create the render component and effect 'entity'
    AnimationRenderComponent animator = new AnimationRenderComponent(atlas);
    Entity effect = new Entity().addComponent(animator);

    // Set up initial effect spawning parameters
    effect.setPosition(position.x, position.y);
    effect.setScale(scale, scale);

    // Configure and start the animation
    animator.addAnimation(animatorName, frameDuration, playMode);
    animator.startAnimation(animatorName);
    logger.info("Started animation {}", animatorName);

    // Attach component to handle optional movement and timed disposal
    effect.addComponent(
        new Component() {
          float age = 0f; // total time since effect spawned
          boolean arrived = false; // whether effect has arrived at the finalPosition (if relevant)

          @Override
          public void update() {
            float dt = ServiceLocator.getTimeSource().getDeltaTime();
            age += dt;

            // Optional movement phase
            if (movingAnimation && !arrived) {
              // Movement phase starts after 1 second at initial position
              if (age <= 1f) {
                logger.info("Moving animation {} - waiting phase", animatorName);
                return;
              }

              // Move linearly to the lower right corner over 1 second duration
              logger.info("Moving animation {} - movement phase", animatorName);
              float t = Math.min((age - 1f), 1f);
              float x = position.x + (finalPosition.x - position.x) * t;
              float y = position.y + (finalPosition.y - position.y) * t;
              effect.setPosition(x, y);

              // Once 1 second is finished, effect has arrived at finalPosition
              if (t >= 1f) {
                logger.info("Moving animation {} - movement complete", animatorName);
                arrived = true;
              } else {
                return;
              }
            }

            // Dispose effect after effect duration exceeded
            if (playMode == Animation.PlayMode.NORMAL) {
              if (totalEffectTime > 0f && age >= totalEffectTime) {
                Gdx.app.postRunnable(effect::dispose);
                // Trigger Defence listener to stop double attack speed/double damage
                // if (Objects.equals(animatorName, "emp")) {
                // [].getEvents().trigger("stopDoubleDamage");
                // }
                // if (Objects.equals(animatorName, "coffee")) {
                // [].getEvents().trigger("stopDoubleSpeed");
                // }
                logger.info("Effect disposed");
              }
            }
          }
        });

    // Register the effect entity
    ServiceLocator.getEntityService().register(effect);
    logger.info("Spawned effect {}", animatorName);
  }

  /**
   * Handles call to play a particular effect and then calls to spawn effect.
   *
   * @param itemName Effect name
   * @param position World position at which the item was placed (centre of tile)
   * @param tileSize Tile size of the level used to scale the effect
   * @param bottomCorner Target position (bottom right corner) for moving effects
   */
  public void playEffect(String itemName, Vector2 position, int tileSize, Vector2 bottomCorner) {
    switch (itemName) {
      case "buff":
        // Offset effect position so effect is centred around tile spawned at (effect is scaled up
        // to cover 3x3 tiles)
        position.x = position.x - tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService().getAsset("images/buff.atlas", TextureAtlas.class),
            "buff",
            position,
            tileSize * 3,
            0.1f,
            Animation.PlayMode.NORMAL,
            true, // allows display in bottom right to indicate effect duration
            bottomCorner,
            30f); // buff effects remain for 30 seconds

        // Trigger Defence listener to start double damage
        // [].getEvents().trigger("startDoubleDamage");
        logger.info("Created buff effect");
        break;
      case "coffee":
        // Offset effect position so effect is centred around tile spawned at (effect is scaled up
        // to cover 3x3 tiles)
        position.x = position.x - tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService().getAsset("images/coffee.atlas", TextureAtlas.class),
            "coffee",
            position,
            tileSize * 3,
            0.1f,
            Animation.PlayMode.NORMAL,
            true, // allows display in bottom right to indicate effect duration
            bottomCorner,
            30f); // coffee effects remain for 30 seconds

        // Trigger Defence listener to begin double attack speed
        // [].getEvents().trigger("startDoubleSpeed");
        logger.info("Created coffee effect");
        break;
      case "emp":
        // Offset effect position so effect is centred around tile spawned at (effect is scaled up
        // to cover 3x3 tiles)
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
            1.5f); // emp is an instantaneous effect
        logger.info("Created emp effect");
        break;
      case "grenade":
        // Offset effect position so effect is centred around tile spawned at (effect is scaled up
        // to cover 3x3 tiles)
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
            1.5f); // grenade is an instantaneous effect
        logger.info("Created grenade effect");
        break;
      case "nuke":
        // Offset effect position so effect is centred around tile spawned at (effect is scaled up
        // to cover 5x5 tiles)
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
            1.5f); // nuke is an instantaneous effect
        logger.info("Created nuke effect");
        break;
    }
  }
}
