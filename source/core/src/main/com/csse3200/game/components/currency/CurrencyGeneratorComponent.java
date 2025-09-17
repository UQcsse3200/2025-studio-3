package com.csse3200.game.components.currency;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Periodically spawns "sunlight" currency at random positions on the screen. The spawn interval,
 * sun value, texture path and motion parameters are configurable. Actions registered on the Stage
 * are removed in {@link #dispose()} to avoid leaks.
 */
public class CurrencyGeneratorComponent extends Component {
  private static final Logger logger = LoggerFactory.getLogger(CurrencyGeneratorComponent.class);

  /** Spawn interval in seconds */
  private final float intervalSec;

  /** Currency amount each sun grants on pick-up */
  private final int sunValue;

  /** Texture path for the sun sprite */
  private final String sunTexturePath;

  /**
   * FALL_FRAC_PER_SEC: Fraction of screen height per second used as falling speed. e.g. 0.25 -> sun
   * falls 25% of the screen height each second
   */
  private float FALL_FRAC_PER_SEC = 0.1f;

  /** Rotation speed in degrees per second. */
  private float ROT_SPEED_DPS = 100f;

  /** Sun visual size in pixels */
  private float SUN_SIZE_PX = 128f;

  /** Sun lifetime in second */
  private float SUN_LIFETIME_SEC = 20f;

  private transient Action generatorAction;
  private boolean isPaused = false;

  /** Creates a new currency generator component with default settings. */
  public CurrencyGeneratorComponent() {
    this(8f, 25, "images/normal_sunlight.png");
  }

  /**
   * Creates a new currency generator component with the specified parameters.
   *
   * @param intervalSec seconds between spawns
   * @param sunValue currency granted per sun
   * @param sunTexturePath texture path for the sun image
   */
  public CurrencyGeneratorComponent(float intervalSec, int sunValue, String sunTexturePath) {
    this.intervalSec = Math.max(0.5f, intervalSec);
    this.sunValue = Math.max(1, sunValue);
    this.sunTexturePath = sunTexturePath;
  }

  @Override
  public void create() {
    super.create();
    Stage stage =
        ServiceLocator.getRenderService() != null
            ? ServiceLocator.getRenderService().getStage()
            : null;
    if (stage == null) {
      logger.warn("RenderService or Stage is null.");
      return;
    }

    generatorAction =
        Actions.forever(
            Actions.sequence(Actions.delay(intervalSec), Actions.run(this::spawnOneSunRandom)));
    stage.addAction(generatorAction);
    logger.debug("CurrencyGenerator scheduled with interval={}s", intervalSec);
  }

  /** Spawn a sun that falls from the top to (targetX, targetY) while rotating. */
  /**
   * Spawns a sun at the specified coordinates.
   *
   * @param targetX the x coordinate to spawn at
   * @param targetY the y coordinate to spawn at
   */
  public void spawnSunAt(float targetX, float targetY) {
    ResourceService rs = ServiceLocator.getResourceService();
    Stage stage =
        ServiceLocator.getRenderService() != null
            ? ServiceLocator.getRenderService().getStage()
            : null;
    if (rs == null) {
      logger.warn("ResourceService is null. Cannot spawn sun.");
      return;
    }
    if (stage == null) {
      logger.warn("Stage is null. Cannot spawn sun.");
      return;
    }

    Texture tex = rs.getAsset(sunTexturePath, Texture.class);
    if (tex == null) {
      logger.warn("Texture '{}' not loaded.", sunTexturePath);
      return;
    }

    CurrencyInteraction sun = new CurrencyInteraction(tex, sunValue);
    sun.setSize(SUN_SIZE_PX, SUN_SIZE_PX);
    sun.setOrigin(SUN_SIZE_PX / 2f, SUN_SIZE_PX / 2f);

    float worldH = stage.getViewport().getWorldHeight();
    float startX = targetX;
    float startY = worldH + SUN_SIZE_PX;
    sun.setPosition(startX, startY);
    stage.addActor(sun);

    // speed (px/s) derived from world height fraction
    float fallSpeedPps = Math.max(1f, worldH * FALL_FRAC_PER_SEC);
    float distance = Math.max(0f, startY - targetY);
    float duration = distance / fallSpeedPps;

    // rotation
    float oneTurn = (ROT_SPEED_DPS <= 0f) ? 0f : (360f / ROT_SPEED_DPS);

    if (duration > 0f) {
      sun.addAction(
          Actions.parallel(
              Actions.moveTo(targetX, targetY, duration, Interpolation.sine),
              Actions.forever(Actions.rotateBy(360f, Math.max(0.01f, oneTurn)))));
    } else {
      sun.setPosition(targetX, targetY);
    }

    // Auto-expire if not collected to avoid screen flooding
    float expireSec = SUN_LIFETIME_SEC;
    sun.addAction(
        Actions.sequence(
            Actions.delay(expireSec),
            Actions.parallel(Actions.fadeOut(0.25f), Actions.scaleTo(0.85f, 0.85f, 0.25f)),
            Actions.run(
                () -> {
                  if (sun.hasParent()) {
                    sun.clearActions();
                    sun.clearListeners();
                    sun.remove();
                    logger.debug(
                        "Sun expired without being collected at ({}, {})", targetX, targetY);
                  }
                })));
  }

  /** Spawn at a random point within screen bounds (with padding). */
  private void spawnOneSunRandom() {
    ResourceService rs = ServiceLocator.getResourceService();
    Stage stage =
        ServiceLocator.getRenderService() != null
            ? ServiceLocator.getRenderService().getStage()
            : null;
    if (rs == null) {
      logger.warn("ResourceService is null. Skipping random sun spawn.");
      return;
    }
    if (stage == null) {
      logger.warn("Stage is null. Skipping random sun spawn.");
      return;
    }

    float padding = 32f;
    float w = stage.getViewport().getWorldWidth();
    float h = stage.getViewport().getWorldHeight();

    float targetX = MathUtils.random(padding, Math.max(padding, w - SUN_SIZE_PX - padding));
    float targetY = MathUtils.random(padding, Math.max(padding, h - SUN_SIZE_PX - padding));
    spawnSunAt(targetX, targetY);
  }

  /** Configure falling speed */
  /**
   * Sets the fall fraction per second for spawned suns.
   *
   * @param frac the fall fraction per second
   * @return this component for method chaining
   */
  public CurrencyGeneratorComponent setFallFracPerSec(float frac) {
    this.FALL_FRAC_PER_SEC = Math.max(0.01f, frac);
    return this;
  }

  /** Configure rotation speed in degrees per second. */
  /**
   * Sets the rotating speed in degrees per second for spawned suns.
   *
   * @param dps the degrees per second
   * @return this component for method chaining
   */
  public CurrencyGeneratorComponent setRotatingSpeedDps(float dps) {
    this.ROT_SPEED_DPS = Math.max(0f, dps);
    return this;
  }

  /** Configure sun visual size in pixels. */
  /**
   * Sets the size of spawned suns in pixels.
   *
   * @param px the size in pixels
   * @return this component for method chaining
   */
  public CurrencyGeneratorComponent setSunSizePx(float px) {
    this.SUN_SIZE_PX = Math.max(8f, px);
    return this;
  }

  /** Pauses the sunlight generation */
  public void pause() {
    isPaused = true;
    Stage stage =
        ServiceLocator.getRenderService() != null
            ? ServiceLocator.getRenderService().getStage()
            : null;
    if (stage != null && generatorAction != null) {
      stage.getRoot().removeAction(generatorAction);
      logger.debug("Paused CurrencyGenerator");
    }
  }

  /** Resumes the sunlight generation */
  public void resume() {
    isPaused = false;
    Stage stage =
        ServiceLocator.getRenderService() != null
            ? ServiceLocator.getRenderService().getStage()
            : null;
    if (stage != null) {
      generatorAction =
          Actions.forever(
              Actions.sequence(Actions.delay(intervalSec), Actions.run(this::spawnOneSunRandom)));
      stage.addAction(generatorAction);
      logger.debug("Resumed CurrencyGenerator");
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    Stage stage =
        ServiceLocator.getRenderService() != null
            ? ServiceLocator.getRenderService().getStage()
            : null;
    if (stage != null && generatorAction != null) {
      stage.getRoot().removeAction(generatorAction);
      logger.debug("Removed generatorAction from Stage.");
    }
    generatorAction = null;
  }
}
