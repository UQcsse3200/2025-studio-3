package com.csse3200.game.components;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that causes the entity to deal AOE (area-of-effect) damage to nearby entities upon
 * death, and start the explosion animation when health drops below 30%.
 */
public class BomberDeathExplodeComponent extends Component {
  private final int explosionDamage;
  private final float explosionRadiusTiles;

  float tileSize = 1f;
  float worldRadius;
  boolean triggered = false;

  private static final Logger logger = LoggerFactory.getLogger(BomberDeathExplodeComponent.class);

  /**
   * Creates a BomberDeathExplodeComponent.
   *
   * @param explosionDamage amount of damage to apply to each entity within the explosion radius
   * @param explosionRadiusTiles radius of the explosion in tiles
   */
  public BomberDeathExplodeComponent(int explosionDamage, float explosionRadiusTiles) {
    this.explosionDamage = explosionDamage;
    this.explosionRadiusTiles = explosionRadiusTiles;
    this.worldRadius = explosionRadiusTiles * tileSize;
  }

  /**
   * Called when the component is created. Registers an event listener on the owning entity so that
   * when it triggers the "entityDeath" event.
   */
  @Override
  public void create() {
    super.create();
    entity.getEvents().addListener("updateHealth", this::onHealthUpdate);
    entity.getEvents().addListener("entityDeath", this::onDeath);
  }

  /** Called when health changes. If health hits zero, trigger explosion animation. */
  private void onHealthUpdate(int currentHealth, int maxHealth) {
    if (triggered) {
      return; // Already triggered
    }
    if (currentHealth <= maxHealth * 0.3f) {
      // Trigger the explosion animation on death moment
      entity.getEvents().trigger("bomberPreExplode");
      triggered = true;
    }
  }

  /** On death event (entityDeath) — start the animation if not already started. */
  private void onDeath() {
    // If not already triggered, trigger it
    entity.getEvents().trigger("bomberPreExplode");

    if (triggered) explode();
  }

  /** Performs AOE explosion damage around the bomber’s position. */
  private void explode() {
    logger.info("[BomberExplosion] Triggered for {}", entity.getId());

    Vector2 center = entity.getPosition();
    if (center == null) return;

    final int rows = 5;
    final int cols = 10;
    final float gridHeight = 450f;
    final float gridWidth = 990f;

    float tileWidth = gridWidth / cols;
    float tileHeight = gridHeight / rows;

    int centerCol = (int) Math.floor(center.x / tileWidth);
    int centerRow = (int) Math.floor(center.y / tileHeight);

    logger.info("[BomberExplosion] Tile center row= {} col= {}", centerRow, centerCol);

    for (Entity target : ServiceLocator.getEntityService().getEntities()) {
      if (target == entity) continue;

      Vector2 pos = target.getPosition();
      if (pos == null) continue;

      int targetCol = Math.round(pos.x / tileWidth);
      int targetRow = Math.round(pos.y / tileHeight);

      int dCol = Math.abs(targetCol - centerCol);
      int dRow = Math.abs(targetRow - centerRow);

      if (dCol <= explosionRadiusTiles && dRow <= explosionRadiusTiles) {
        logger.info("Defence explode", target.getId());
        DefenderStatsComponent defence = target.getComponent(DefenderStatsComponent.class);
        if (defence != null) {
          defence.setHealth(defence.getHealth() - explosionDamage);
          defence.handleDeath();
        }
      }
    }
  }
}
