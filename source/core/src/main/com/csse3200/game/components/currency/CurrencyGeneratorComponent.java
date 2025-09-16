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
 * Periodically spawns "Scrap Metal" currency at random positions on the screen. The spawn interval,
 * scrap value, texture path and motion parameters are configurable. Actions registered on the Stage
 * are removed in {@link #dispose()} to avoid leaks.
 */
public class CurrencyGeneratorComponent extends Component {
  private static final Logger logger = LoggerFactory.getLogger(CurrencyGeneratorComponent.class);

  /** Spawn interval in seconds */
  private final float intervalSec;

  /** Currency amount each scrap grants on pick-up */
  private final int scrapValue;

  /** Texture path for the scrap sprite */
  private final String scrapTexturePath;

  private final float targetX;

  private final float targetY;

  /**
   * FALL_FRAC_PER_SEC: Fraction of screen height per second used as falling speed. e.g. 0.25 ->
   * scrap falls 25% of the screen height each second
   */
  private float FALL_FRAC_PER_SEC = 0.1f;

  /** Rotation speed in degrees per second. */
  private float ROT_SPEED_DPS = 100f;

  /** Scrap visual size in pixels */
  private float SCRAP_SIZE_PX = 64f;

  /** Scrap lifetime in second */
  private float SCRAP_LIFETIME_SEC = 20f;

  private transient Action generatorAction;

  /**
   * Creates a new currency generator component with the specified parameters.
   *
   * @param intervalSec seconds between spawns
   * @param scrapValue currency granted per scrap
   * @param scrapTexturePath texture path for the scrap image
   */
  public CurrencyGeneratorComponent(
      float intervalSec, int scrapValue, String scrapTexturePath, Vector2 position) {
    this.intervalSec = Math.max(0.5f, intervalSec);
    this.scrapValue = Math.max(1, scrapValue);
    this.scrapTexturePath = scrapTexturePath;
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
            Actions.sequence(Actions.delay(intervalSec), Actions.run(this::spawnScrapAt)));
    stage.addAction(generatorAction);
    logger.debug("CurrencyGenerator scheduled with interval={}s", intervalSec);
  }

  /** Spawn a scrap that falls from the top to (targetX, targetY) while rotating. */
  /** Spawns a scrap at the specified coordinates. */
  public void spawnScrapAt() {
    ResourceService rs = ServiceLocator.getResourceService();
    Stage stage =
        ServiceLocator.getRenderService() != null
            ? ServiceLocator.getRenderService().getStage()
            : null;
    if (rs == null) {
      logger.warn("ResourceService is null. Cannot spawn scrap.");
      return;
    }
    if (stage == null) {
      logger.warn("Stage is null. Cannot spawn scrap.");
      return;
    }

    Texture tex = rs.getAsset(scrapTexturePath, Texture.class);
    if (tex == null) {
      logger.warn("Texture '{}' not loaded.", scrapTexturePath);
      return;
    }

    CurrencyInteraction scrap = new CurrencyInteraction(tex, scrapValue);
    scrap.setSize(SCRAP_SIZE_PX, SCRAP_SIZE_PX);
    scrap.setOrigin(SCRAP_SIZE_PX / 2f, SCRAP_SIZE_PX / 2f);

    stage.addActor(scrap);

    scrap.setPosition(this.targetX, this.targetY);

    // Auto-expire if not collected to avoid screen flooding
    float expireSec = SCRAP_LIFETIME_SEC;
    scrap.addAction(
        Actions.sequence(
            Actions.delay(expireSec),
            Actions.parallel(Actions.fadeOut(0.25f), Actions.scaleTo(0.85f, 0.85f, 0.25f)),
            Actions.run(
                () -> {
                  if (scrap.hasParent()) {
                    scrap.clearActions();
                    scrap.clearListeners();
                    scrap.remove();
                    logger.debug(
                        "Scrap expired without being collected at ({}, {})", targetX, targetY);
                  }
                })));
  }

  /** Configure scrap visual size in pixels. */
  /**
   * Sets the size of spawned scraps in pixels.
   *
   * @param px the size in pixels
   * @return this component for method chaining
   */
  public CurrencyGeneratorComponent setScrapSizePx(float px) {
    this.SCRAP_SIZE_PX = Math.max(8f, px);
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
