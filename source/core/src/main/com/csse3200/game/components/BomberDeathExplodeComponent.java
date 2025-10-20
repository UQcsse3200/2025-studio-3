package com.csse3200.game.components;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

/**
 * Component that causes the entity to deal AOE (area-of-effect) damage to nearby entities upon
 * death, and start the explosion animation when health drops below 30%.
 */
public class BomberDeathExplodeComponent extends Component {
  private final int explosionDamage;
  private final float explosionRadiusTiles;

  float tileSize = 1f;
  float worldRadius;

  private static final float EXPLOSION_THRESHOLD = 0.1f; // 10% HP

  public BomberDeathExplodeComponent(int explosionDamage, float explosionRadiusTiles) {
    this.explosionDamage = explosionDamage;
    this.explosionRadiusTiles = explosionRadiusTiles;
    this.worldRadius = explosionRadiusTiles * tileSize;
  }

  @Override
  public void create() {
    super.create();
    System.out.println("[BomberExplosion] Component created for entity " + getEntity().getId());
    entity.getEvents().addListener("updateHealth", this::onHealthUpdate);
    entity.getEvents().addListener("entityDeath", this::onDeath);
  }

  /** When HP drops below 30%, start playing the explosion animation. */
  private void onHealthUpdate(int currentHealth, int maxHealth) {
    if (currentHealth <= maxHealth * EXPLOSION_THRESHOLD) {
      // Play explosion animation early (visual warning)
      entity.getEvents().trigger("bomberPreExplode");
      System.out.println(
          "[BomberExplosion] Explosion animation started early for " + entity.getId());
    }
  }

  /** On death, apply AOE damage immediately. */
  private void onDeath() {
    System.out.println("[BomberExplosion] Bomber died, triggering damage.");
    explode();
  }

  /** Performs AOE explosion damage around the bomber’s position. */
  private void explode() {
    System.out.println("[BomberExplosion] Triggered for " + entity.getId());

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

    for (Entity target : ServiceLocator.getEntityService().getEntities()) {
      if (target == entity) continue;

      Vector2 pos = target.getPosition();
      if (pos == null) continue;

      int targetCol = Math.round(pos.x / tileWidth);
      int targetRow = Math.round(pos.y / tileHeight);

      int dCol = Math.abs(targetCol - centerCol);
      int dRow = Math.abs(targetRow - centerRow);

      if (dCol <= explosionRadiusTiles && dRow <= explosionRadiusTiles) {
        DefenderStatsComponent defence = target.getComponent(DefenderStatsComponent.class);
        if (defence != null) {
          defence.setHealth(defence.getHealth() - explosionDamage);
          defence.handleDeath();
          System.out.println(
              "[BomberExplosion] Damaged target "
                  + target.getId()
                  + " at row="
                  + targetRow
                  + ", col="
                  + targetCol);
        }
      }
    }
  }
}
