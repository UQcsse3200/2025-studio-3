package com.csse3200.game.components.slot;

import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;
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

  private SlotEffect() {}

  /**
   * Execute a slot effect against a LevelGameArea.
   *
   * @param effect The effect chosen by the slot engine.
   * @param area The active LevelGameArea (use the current instance; inside LevelGameArea you can
   *     pass `this`).
   */
  public static void executeByEffect(SlotEngine.Effect effect, LevelGameArea area) {
    logger.info("[SlotEffect] effect={} on {}", effect, area.getClass().getSimpleName());
    if (effect == null) {
      logger.warn("executeByEffect skipped: effect={} area={}", effect, area);
      return;
    }
    switch (effect) {
      case GAIN_METALS -> gainMetals();
      case GAIN_COINS -> gainCoins();
      case SUMMON_ENEMY -> summonWave(area);
      case DESTROY_ENEMY -> destroyAllEnemies();
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

  private static void gainCoins() {
    try {
      Persistence.profile().wallet().addCoins(50);
      logger.info("[SlotEffect] GAIN_COINS: Gained 50 coins.");
    } catch (Exception e) {
      logger.error("[SlotEffect] GAIN_COINS: Failed to add coins. {}", e.getMessage());
    }
  }

  /**
   * Spawn a simple wave of robots (positions/types tuned to your existing map layout). You can
   * freely tweak the layout below to match your level. Uses LevelGameArea.spawnRobot(x, y, type),
   * where: - (x, y) are "grid-like" positions consistent with your current usage, - type is one of
   * {"tanky", "standard", "fast"} per your RobotFactory.
   */
  private static void summonWave(LevelGameArea area) {
    try {
      area.spawnRobot(10, 1, "fast");
      area.spawnRobot(10, 2, "fast");
      area.spawnRobot(10, 3, "fast");
      area.spawnRobot(10, 4, "fast");

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

      // Access the internal LibGDX Array<Entity> from EntityService
      Field fEntities = EntityService.class.getDeclaredField("entities");
      fEntities.setAccessible(true);
      Object gdxArray = fEntities.get(es);
      if (gdxArray == null) {
        logger.info("[SlotEffect] DESTROY_ENEMY: no entities registered.");
        return;
      }

      // LibGDX Array has fields: int size, Object[] items
      Class<?> arrCls = gdxArray.getClass();
      Field fSize = arrCls.getDeclaredField("size");
      Field fItems = arrCls.getDeclaredField("items");
      fSize.setAccessible(true);
      fItems.setAccessible(true);

      int size = (int) fSize.get(gdxArray);
      Object[] items = (Object[]) fItems.get(gdxArray);

      int removed = 0;
      for (int i = 0; i < size; i++) {
        Object o = items[i];
        if (!(o instanceof Entity e)) continue;

        HitboxComponent hb = e.getComponent(HitboxComponent.class);
        if (hb != null && hb.getLayer() == PhysicsLayer.ENEMY) {
          try {
            es.unregister(e);
            e.dispose();
            removed++;
          } catch (Exception ex) {
            logger.error("[SlotEffect] Failed to remove enemy: {}", ex.getMessage(), ex);
          }
        }
      }
      logger.info("[SlotEffect] DESTROY_ENEMY: removed {} enemies.", removed);
    } catch (Throwable t) {
      logger.error("[SlotEffect] DESTROY_ENEMY failed: {}", t.getMessage(), t);
    }
  }
}
