package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
   * @param positions positions[0] is initial world position where the effect entity is spawned, and
   *     positions[1] is destination position when movingAnimation is true (value is ignored
   *     otherwise)
   * @param scale Uniform scale applied to the effect (world units)
   * @param frameAndEffectDuration frameAndEffectDuration[0] is duration per animation frame
   *     (seconds), and frameAndEffectDuration[1] is total lifetime of the effect (seconds) when
   *     playMode == NORMAL
   * @param playMode Animation PlayMode (eg NORMAL, LOOP etc)
   * @param movingAnimation If true the effect will move from position to finalPosition
   */
  public static void spawnEffect(
      TextureAtlas atlas,
      String animatorName,
      Vector2[] positions,
      int scale,
      float[] frameAndEffectDuration,
      Animation.PlayMode playMode,
      boolean movingAnimation,
      boolean soundRequired) {

    Vector2 position = positions[0];
    Vector2 finalPosition = positions[1];
    float frameDuration = frameAndEffectDuration[0];
    float totalEffectTime = frameAndEffectDuration[1];

    if (atlas == null) {
      logger.error("Atlas not loaded");
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

    // Play sound effect for item
    if (soundRequired) {
      playSoundEffect(animatorName);
    }

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
            if (playMode == Animation.PlayMode.NORMAL
                && totalEffectTime > 0f
                && age >= totalEffectTime) {
              Gdx.app.postRunnable(effect::dispose);
              logger.info("Effect disposed");
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
            ServiceLocator.getResourceService()
                .getAsset("images/effects/buff.atlas", TextureAtlas.class),
            "buff",
            (new Vector2[] {position, bottomCorner}),
            tileSize * 3,
            (new float[] {
              0.1f, 30f
            }), // frame duration and total effect time (buff effects remain for 30 seconds)
            Animation.PlayMode.NORMAL,
            true,
            true); // allows display in bottom right to indicate effect duration

        logger.info("Created buff effect");
        break;
      case "coffee":
        // Offset effect position so effect is centred around tile spawned at (effect is scaled up
        // to cover 3x3 tiles)
        position.x = position.x - tileSize;
        position.y = position.y - tileSize;
        bottomCorner.x = bottomCorner.x - tileSize * 1.5f;

        spawnEffect(
            ServiceLocator.getResourceService()
                .getAsset("images/effects/coffee.atlas", TextureAtlas.class),
            "coffee",
            (new Vector2[] {position, bottomCorner}),
            tileSize * 3,
            (new float[] {
              0.1f, 30f
            }), // frame duration and total effect time (coffee effects remain for 30 seconds)
            Animation.PlayMode.NORMAL,
            true,
            true); // allows display in bottom right to indicate effect duration

        logger.info("Created coffee effect");
        break;
      case "emp":
        // Offset effect position so effect is centred around tile spawned at (effect is scaled up
        // to cover 3x3 tiles)
        position.x = position.x - tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService()
                .getAsset("images/effects/emp.atlas", TextureAtlas.class),
            "emp",
            (new Vector2[] {position, bottomCorner}),
            tileSize * 3,
            (new float[] {
              0.1f, 1.5f
            }), // frame duration and total effect time (emp is an instantaneous effect)
            Animation.PlayMode.NORMAL,
            false,
            true);
        logger.info("Created emp effect");
        break;
      case "grenade":
        // Offset effect position so effect is centred around tile spawned at (effect is scaled up
        // to cover 3x3 tiles)
        position.x = position.x - tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService()
                .getAsset("images/effects/grenade.atlas", TextureAtlas.class),
            "grenade",
            (new Vector2[] {position, bottomCorner}),
            tileSize * 3,
            (new float[] {
              0.1f, 1.5f
            }), // frame duration and total effect time (grenade is an instantaneous effect)
            Animation.PlayMode.NORMAL,
            false,
            true);
        logger.info("Created grenade effect");
        break;
      case "nuke":
        // Offset effect position so effect is centred around tile spawned at (effect is scaled up
        // to cover 5x5 tiles)
        position.x = position.x - 2 * tileSize;
        position.y = position.y - tileSize;
        spawnEffect(
            ServiceLocator.getResourceService()
                .getAsset("images/effects/nuke.atlas", TextureAtlas.class),
            "nuke",
            (new Vector2[] {position, bottomCorner}),
            tileSize * 5,
            (new float[] {
              0.1f, 1.5f
            }), // frame duration and total effect time (nuke is an instantaneous effect)
            Animation.PlayMode.NORMAL,
            false,
            true);
        logger.info("Created nuke effect");
        break;
      default:
        logger.error("Unknown item name");
    }
  }

  public static void playSoundEffect(String animatorName) {
    Sound effectSound =
        ServiceLocator.getResourceService()
            .getAsset("sounds/item_" + animatorName + ".mp3", Sound.class);

    if (effectSound != null) {
      float volume = ServiceLocator.getSettingsService().getSoundVolume();
      effectSound.play(volume);
      logger.info("Sound played {}", animatorName);
    }
  }
}
