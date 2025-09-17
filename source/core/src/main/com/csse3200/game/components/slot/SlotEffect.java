package com.csse3200.game.components.slot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.csse3200.game.areas.SlotMachineArea;
import com.csse3200.game.components.cards.CardActor;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.services.ServiceLocator;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Slot effects that apply ONLY within LevelGameArea. Supported effects: 1) SUMMON_ENEMY –
 * Punishment: spawn a wave of enemy robots in the level. 2) DESTROY_ENEMY – Reward : remove
 * ("kill") robot enemies currently present. Design rationale: - LevelGameArea exposes
 * `spawnRobot(x, y, type)` but does NOT expose a public enemy list, nor does it return the spawned
 * Entity. Therefore, precise "remove exactly what we spawned" bookkeeping is not possible here
 * without changing other files. - For DESTROY_ENEMY we use a best-effort approach: iterate over all
 * registered entities (via EntityService) and unregister those that look like "robots" by a set of
 * heuristics. This is safe and non-crashing; if the registry is not available, we log and return.
 * How to integrate: - From SlotEngine, call: SlotEffect.executeByEffect(effect,
 * levelGameAreaInstance); - This file intentionally has NO dependency on ForestGameArea.
 */
public final class SlotEffect {
  private static final Logger logger = LoggerFactory.getLogger(SlotEffect.class);

  // --- UI context bound by SlotMachineDisplay ---
  private static Stage uiStage;
  private static ScrollPane uiReelsPane;

  public static void bindUiContext(Stage stage, ScrollPane reelsPane) {
    uiStage = stage;
    uiReelsPane = reelsPane;
  }

  public static void unbindUiContext() {
    uiReelsPane = null;
    uiStage = null;
    if (ownsCardsAtlas && cardsAtlas != null) {
      cardsAtlas.dispose();
      cardsAtlas = null;
      ownsCardsAtlas = false;
    }
  }

  // Card asset
  private static final String CARDS_ATLAS_PATH = "images/ui_cards.atlas";
  private static TextureAtlas cardsAtlas;
  private static boolean ownsCardsAtlas = false;

  // Card drop params (moved from Display)
  private static final float CARD_SCREEN_W_RATIO = 0.06f;
  private static final float CARD_SLOT_W_RATIO = 0.22f;
  private static final float DROP_FADE_SEC = 0.15f;
  private static final float DROP_MOVE_SEC = 0.35f;
  private static final float DROP_PADDING_PCT = 0.02f;
  private static final float DROP_PADDING_MIN = 8f;

  private SlotEffect() {}

  /**
   * Execute a slot effect against a LevelGameArea.
   *
   * @param effect The effect chosen by the slot engine.
   * @param area The active LevelGameArea (use the current instance; inside LevelGameArea you can
   *     pass `this`).
   */
  public static void executeByEffect(SlotEngine.Effect effect, SlotMachineArea area) {
    logger.info("[SlotEffect] effect={} on {}", effect, area.getClass().getSimpleName());
    if (effect == null) {
      logger.warn("executeByEffect skipped: effect={} area={}", effect, area);
      return;
    }
    switch (effect) {
      case GAIN_METALS -> gainMetals();
      case SUMMON_ENEMY -> summonWave(area);
      case DESTROY_ENEMY -> destroyAllEnemies();
      case FREEZE_ENEMY -> freezeAllEnemies(10f);
      case DROP_SLINGSHOOTER_CARD -> dropSlingShooterCard();
      default -> logger.info("Effect {} ignored for LevelGameArea.", effect);
    }
  }

  private static void gainMetals() {
    try {
      ServiceLocator.getCurrencyService().add(100);
      logger.info("[SlotEffect] GAIN_METALS: Gained 100 metals.");
    } catch (Exception e) {
      logger.error("[SlotEffect] GAIN_METALS: Failed to add metal. {}", e.getMessage());
    }
  }

  /**
   * Spawn a simple wave of robots (positions/types tuned to your existing map layout). You can
   * freely tweak the layout below to match your level. Uses LevelGameArea.spawnRobot(x, y, type),
   * where: - (x, y) are "grid-like" positions consistent with your current usage, - type is one of
   * {"tanky", "standard", "fast"} per your RobotFactory.
   */
  private static void summonWave(SlotMachineArea area) {
    try {
      area.spawnRobot(10, 1, RobotFactory.RobotType.FAST);
      area.spawnRobot(10, 2, RobotFactory.RobotType.FAST);
      area.spawnRobot(10, 3, RobotFactory.RobotType.FAST);
      area.spawnRobot(10, 4, RobotFactory.RobotType.FAST);
      area.spawnRobot(10, 5, RobotFactory.RobotType.FAST);

      logger.info("[SlotEffect][Level] SUMMON_ENEMY: spawned a wave of robots.");
    } catch (Exception e) {
      logger.error("[SlotEffect][Level] SUMMON_ENEMY failed: {}", e.getMessage(), e);
    }
  }

  /**
   * Remove all enemy entities from the game world. Rule: An entity is considered an enemy if it has
   * a HitboxComponent with PhysicsLayer.ENEMY. Uses reflection to safely iterate through
   * EntityService's registered entities.
   */
  private static void destroyAllEnemies() {
    try {
      EntityService es = ServiceLocator.getEntityService();
      if (es == null) {
        logger.warn("[SlotEffect] DESTROY_ENEMY: EntityService unavailable.");
        return;
      }

      com.badlogic.gdx.utils.Array<Entity> all = getAllEntitiesUnsafe(es);
      if (all == null || all.size == 0) {
        logger.info("[SlotEffect] DESTROY_ENEMY: no entities registered.");
        return;
      }

      int removed = 0;
      for (int i = all.size - 1; i >= 0; i--) {
        Entity e = all.get(i);
        if (!isEnemy(e)) {
          continue;
        }
        if (removeEntitySilently(es, e)) {
          removed++;
        }
      }
      logger.info("[SlotEffect] DESTROY_ENEMY: removed {} enemies.", removed);
    } catch (Exception e) {
      logger.error("[SlotEffect] DESTROY_ENEMY failed: {}", e.getMessage(), e);
    }
  }

  private static boolean removeEntitySilently(EntityService es, Entity e) {
    try {
      es.unregister(e);
      e.dispose();
      return true;
    } catch (Exception ex) {
      logger.error("[SlotEffect] Failed to remove enemy: {}", ex.getMessage(), ex);
      return false;
    }
  }

  private static void freezeAllEnemies(float durationSec) {
    EntityService es = ServiceLocator.getEntityService();
    if (es == null) {
      logger.warn("[SlotEffect] FREEZE_ENEMY skipped: no EntityService");
      return;
    }

    com.badlogic.gdx.utils.Array<Entity> all = getAllEntitiesUnsafe(es);

    int applied = 0;
    for (Entity e : all) {
      if (!isEnemy(e)) continue;
      if (applyFreeze(e, durationSec)) {
        applied++;
      }
    }
    logger.info("[SlotEffect] FREEZE_ENEMY: applied to {} enemies for {}s", applied, durationSec);
  }

  @SuppressWarnings({"unchecked", "java:S3011"})
  private static com.badlogic.gdx.utils.Array<Entity> getAllEntitiesUnsafe(EntityService es) {
    try {
      java.lang.reflect.Field fEntities = EntityService.class.getDeclaredField("entities");
      fEntities.setAccessible(true);
      return (com.badlogic.gdx.utils.Array<Entity>) fEntities.get(es);
    } catch (Exception ex) {
      logger.error("[SlotEffect] FREEZE_ENEMY getAllEntities failed: {}", ex.getMessage(), ex);
      return new com.badlogic.gdx.utils.Array<>();
    }
  }

  private static boolean applyFreeze(Entity e, float durationSec) {
    try {
      PhysicsMovementComponent pm = e.getComponent(PhysicsMovementComponent.class);
      PhysicsComponent pc = e.getComponent(PhysicsComponent.class);

      if (pm != null) pm.setMoving(false);
      if (pc != null && pc.getBody() != null) {
        pc.getBody().setLinearVelocity(0f, 0f);
        pc.getBody().setAngularVelocity(0f);
      }

      com.badlogic.gdx.utils.Timer.schedule(
          new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
              try {
                if (pm != null) pm.setMoving(true);
                if (pc != null && pc.getBody() != null) {
                  pc.getBody().setLinearVelocity(0f, 0f);
                  pc.getBody().setAngularVelocity(0f);
                }
              } catch (Exception ex) {
                logger.error("[SlotEffect] FREEZE_ENEMY unfreeze failed: {}", ex.getMessage(), ex);
              }
            }
          },
          durationSec);

      return true;
    } catch (Exception ex) {
      logger.error("[SlotEffect] FREEZE_ENEMY freeze failed: {}", ex.getMessage(), ex);
      return false;
    }
  }

  private static void dropSlingShooterCard() {
    if (uiStage == null || uiReelsPane == null) {
      logger.error("[SlotEffect] DROP_SLINGSHOOTER_CARD: UI context not bound");
      return;
    }
    if (cardsAtlas == null) {
      try {
        cardsAtlas =
            ServiceLocator.getResourceService().getAsset(CARDS_ATLAS_PATH, TextureAtlas.class);
      } catch (Exception ignore) {
        cardsAtlas = null;
      }
      if (cardsAtlas == null) {
        if (!Gdx.files.internal(CARDS_ATLAS_PATH).exists()) {
          logger.error("[SlotEffect] Card atlas not found: {}", CARDS_ATLAS_PATH);
          return;
        }
        cardsAtlas = new TextureAtlas(Gdx.files.internal(CARDS_ATLAS_PATH));
        ownsCardsAtlas = true;
      }
    }
    if (cardsAtlas.findRegion("Card_SlingShooter") == null) {
      logger.error("[SlotEffect] Region 'Card_SlingShooter' not found in {}", CARDS_ATLAS_PATH);
      return;
    }
    Vector2 slotTopCenter =
        uiReelsPane.localToStageCoordinates(
            new Vector2(uiReelsPane.getWidth() * 0.5f, uiReelsPane.getHeight()));
    Vector2 slotBottomCenter =
        uiReelsPane.localToStageCoordinates(new Vector2(uiReelsPane.getWidth() * 0.5f, 0f));

    float stageW = uiStage.getWidth();
    float stageH = uiStage.getHeight();
    float padding = Math.max(DROP_PADDING_MIN, stageH * DROP_PADDING_PCT);

    CardActor card = CardActor.fromAtlas(uiStage, cardsAtlas, "Card_SlingShooter");
    float desiredWpx =
        Math.min(stageW * CARD_SCREEN_W_RATIO, uiReelsPane.getWidth() * CARD_SLOT_W_RATIO);
    card.setPercentWidth(desiredWpx / stageW);
    card.act(0f);

    float startCx = slotTopCenter.x;
    float startCy = slotTopCenter.y + card.getHeight() / 2f + padding;
    float targetCx = slotBottomCenter.x;
    float targetCy =
        Math.max(slotBottomCenter.y - card.getHeight() / 2f - padding, card.getHeight() / 2f + 4f);

    card.setPosition(startCx - card.getWidth() / 2f, startCy - card.getHeight() / 2f);
    card.getColor().a = 0f;
    uiStage.addActor(card);
    card.toFront();

    float targetX = targetCx - card.getWidth() / 2f;
    float targetY = targetCy - card.getHeight() / 2f;

    card.addAction(
        Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(DROP_FADE_SEC),
                Actions.moveTo(targetX, targetY, DROP_MOVE_SEC, Interpolation.sine)),
            Actions.run(
                () -> {
                  float px = targetCx / stageW;
                  float py = targetCy / stageH;
                  card.setPercentPosition(px, py);
                })));
  }

  private static boolean isEnemy(Entity e) {
    HitboxComponent hb = e.getComponent(HitboxComponent.class);
    return hb != null && PhysicsLayer.contains(hb.getLayer(), PhysicsLayer.ENEMY);
  }
}
