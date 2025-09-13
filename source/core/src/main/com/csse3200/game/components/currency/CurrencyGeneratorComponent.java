package com.csse3200.game.components.currency;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
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

  private final float targetX;

  private final float targetY;

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

  /** Creates a new currency generator component with default settings. */
  public CurrencyGeneratorComponent() {
    this(8f, 25, "images/scrap_metal.png", new Vector2(0, 0));
  }

  /**
   * Creates a new currency generator component with the specified parameters.
   *
   * @param intervalSec seconds between spawns
   * @param sunValue currency granted per sun
   * @param sunTexturePath texture path for the sun image
   */
  public CurrencyGeneratorComponent(
      float intervalSec, int sunValue, String sunTexturePath, Vector2 position) {
    this.intervalSec = Math.max(0.5f, intervalSec);
    this.sunValue = Math.max(1, sunValue);
    this.sunTexturePath = sunTexturePath;
    this.targetX = position.x;
    this.targetY = position.y;
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
            Actions.sequence(Actions.delay(intervalSec), Actions.run(this::spawnSunAt)));
    stage.addAction(generatorAction);
    logger.debug("CurrencyGenerator scheduled with interval={}s", intervalSec);
  }

  /** Spawn a sun that falls from the top to (targetX, targetY) while rotating. */
  /** Spawns a sun at the specified coordinates. */
  public void spawnSunAt() {
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

    stage.addActor(sun);

    sun.setPosition(this.targetX, this.targetY);

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
