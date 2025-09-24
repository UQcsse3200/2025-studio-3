package com.csse3200.game.components.slot;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.SlotMachineArea;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.services.ServiceLocator;
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
    private static final String CARDS_ATLAS_PATH = "images/ui_cards/ui_cards.atlas";
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

    // Current level instance
    private static LevelGameArea currentArea;

    // External query of the number of cards on the field
    public static int getActiveCardCount() {
        return SlotCardEntity.getActiveCardCount();
    }
    /**
     * Execute a slot effect against a LevelGameArea.
     *
     * @param effect The effect chosen by the slot engine.
     * @param area The active LevelGameArea (use the current instance; inside LevelGameArea you can
     *     pass `this`).
     */
    public static void executeByEffect(SlotEngine.Effect effect, SlotMachineArea area) {
        logger.info("[SlotEffect] effect={} on {}", effect, area.getClass().getSimpleName());
        currentArea = (area instanceof LevelGameArea) ? (LevelGameArea) area : null;

        if (effect == null) {
            logger.warn("executeByEffect skipped: effect={} area={}", effect, area);
            return;
        }
        switch (effect) {
            case SUMMON_ENEMY -> summonWave(area);
            case DESTROY_ENEMY -> destroyAllEnemies();
            case DROP_SLINGSHOOTER_CARD -> dropSlingShooterCard();
            default -> logger.info("Effect {} ignored for LevelGameArea.", effect);
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
            area.spawnRobot(10, 0, RobotFactory.RobotType.FAST);
            area.spawnRobot(10, 1, RobotFactory.RobotType.FAST);
            area.spawnRobot(10, 2, RobotFactory.RobotType.FAST);
            area.spawnRobot(10, 3, RobotFactory.RobotType.FAST);
            area.spawnRobot(10, 4, RobotFactory.RobotType.FAST);
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

    /**
     * On triple-hit, create a draggable card and drop it.
     *
     *The actual drop and count are handed over to the SlotCardEntity implementation; the original implementation is retained as a comment.
     */
    private static void dropSlingShooterCard() {
        // ===== New logic: forward to card module, then return =====
        if (uiStage != null && uiReelsPane != null && currentArea != null) {
            SlotCardEntity.dropSlingShooterCard(uiStage, uiReelsPane, currentArea);
            return;
        }
    }

    private static boolean isEnemy(Entity e) {
        HitboxComponent hb = e.getComponent(HitboxComponent.class);
        return hb != null && PhysicsLayer.contains(hb.getLayer(), PhysicsLayer.ENEMY);
    }
}
