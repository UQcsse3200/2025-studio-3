package com.csse3200.game.components.currency;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.csse3200.game.components.Component;
import com.csse3200.game.components.GeneratorStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.progression.skilltree.Skill;
import com.csse3200.game.services.GameStateService;
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

  /** Generator action */
  private Action generatorAction;

  /** Whether the generator is paused */
  private boolean isPaused = false;

  /** Stage the generator action was scheduled against. */
  private Stage scheduledStage;

  /** Tracks whether the action is currently scheduled on the stage. */
  private boolean actionScheduled = false;

  /** Listener for responding to global freeze state changes. */
  private GameStateService.FreezeListener freezeListener;

  /**
   * Creates a new currency generator component with the specified parameters.
   *
   * @param entity the furnace entity associated with the currency generator
   * @param scrapTexturePath texture path for the scrap image
   */
  public CurrencyGeneratorComponent(Entity entity, GridPoint2 stagePos, String scrapTexturePath) {
    int interval = entity.getComponent(GeneratorStatsComponent.class).getInterval();
    // adjust interval value with currency generation skill upgrade
    if (ServiceLocator.getProfileService() != null) {
      float scrapUpgrade =
          ServiceLocator.getProfileService()
              .getProfile()
              .getSkillset()
              .getUpgradeValue(Skill.StatType.CURRENCY_GEN);
      interval = (int) Math.floor(interval / scrapUpgrade);
    }
    this.intervalSec = interval;
    this.scrapValue = entity.getComponent(GeneratorStatsComponent.class).getScrapValue();
    this.targetX = stagePos.x;
    this.targetY = stagePos.y;
    this.scrapTexturePath = scrapTexturePath;
  }

  @Override
  public void create() {
    super.create();
    if (getStage() == null) {
      logger.warn("RenderService or Stage is null.");
      return;
    }

    GameStateService gameStateService = ServiceLocator.getGameStateService();
    if (gameStateService != null) {
      freezeListener =
          frozen -> {
            if (frozen) {
              pause();
            } else {
              resume();
            }
          };
      gameStateService.registerFreezeListener(freezeListener);
    }

    if (gameStateService != null && gameStateService.isFrozen()) {
      pause();
    } else {
      resume();
    }
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
    Stage stage = scheduledStage != null ? scheduledStage : getStage();
    if (stage != null && generatorAction != null && actionScheduled) {
      stage.getRoot().removeAction(generatorAction);
    }
    actionScheduled = false;
    generatorAction = null; // discard pooled action to avoid invalid reuse
    scheduledStage = null;
    if (!isPaused) {
      isPaused = true;
      logger.debug("Paused CurrencyGenerator");
    }
  }

  /** Resumes the sunlight generation */
  public void resume() {
    Stage stage = getStage();
    if (stage == null) {
      logger.warn("Stage unavailable; cannot resume CurrencyGenerator");
      return;
    }
    if (generatorAction == null) {
      generatorAction = buildGeneratorAction();
    }
    if (!actionScheduled) {
      stage.getRoot().addAction(generatorAction);
      scheduledStage = stage;
      actionScheduled = true;
    }
    if (isPaused) {
      logger.debug("Resumed CurrencyGenerator");
    }
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
    if (freezeListener != null) {
      GameStateService gameStateService = ServiceLocator.getGameStateService();
      if (gameStateService != null) {
        gameStateService.unregisterFreezeListener(freezeListener);
      }
      freezeListener = null;
    }
    pause();
  }

  /** Build a new, safe-to-add generator action instance. */
  private Action buildGeneratorAction() {
    return Actions.forever(
        Actions.sequence(Actions.delay(intervalSec), Actions.run(this::spawnScrapAt)));
  }

  private Stage getStage() {
    return ServiceLocator.getRenderService() != null
        ? ServiceLocator.getRenderService().getStage()
        : null;
  }
}
