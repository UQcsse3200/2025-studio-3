package com.csse3200.game.components.currency;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.csse3200.game.components.Component;
import com.csse3200.game.components.GeneratorStatsComponent;
import com.csse3200.game.entities.Entity;
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

  /** Scrap visual size in pixels */
  private float scrapSizePx = 64f;

  /** Scrap lifetime in seconds */
  private static final float SCRAP_LIFETIME_SEC = 20f;

  /** Generator action */
  private Action generatorAction;

  /** Whether the generator is paused */
  private boolean isPaused = false;

  /**
   * Creates a new currency generator component with the specified parameters.
   *
   * @param entity the furnace entity associated with the currency generator
   * @param scrapTexturePath texture path for the scrap image
   */
  public CurrencyGeneratorComponent(Entity entity, GridPoint2 stagePos, String scrapTexturePath) {
    this.intervalSec = entity.getComponent(GeneratorStatsComponent.class).getInterval();
    this.scrapValue = entity.getComponent(GeneratorStatsComponent.class).getScrapValue();
    this.targetX = stagePos.x;
    this.targetY = stagePos.y;
    this.scrapTexturePath = scrapTexturePath;
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
    scrap.setSize(scrapSizePx, scrapSizePx);
    scrap.setOrigin(scrapSizePx / 2f, scrapSizePx / 2f);

    stage.addActor(scrap);

    scrap.setPosition(this.targetX, this.targetY); // STAGE POSITIONS
    scrap.addCurrencyAnimation();
  }

  /** Configure scrap visual size in pixels. */
  /**
   * Sets the size of spawned scraps in pixels.
   *
   * @param px the size in pixels
   * @return this component for method chaining
   */
  public CurrencyGeneratorComponent setScrapSizePx(float px) {
    this.scrapSizePx = Math.max(8f, px);
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
  }

  /**
   * Checks if the generator is paused.
   *
   * @return true if the generator is paused, false otherwise
   */
  public boolean isPaused() {
    return isPaused;
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
