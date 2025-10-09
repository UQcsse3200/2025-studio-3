package com.csse3200.game.components;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

/**
 * Component that causes the entity to deal AOE (area-of-effect) damage to nearby entities upon
 * death.
 *
 * <p>When the entity with this component dies, it checks surrounding tiles within a given radius
 * and applies damage to any other entities in that area. This can be used for enemy bombers that
 * explode on death, damaging nearby defences.
 */
public class BomberDeathExplodeComponent extends Component {
  /** Damage dealt to each entity caught in the explosion. */
  private final int explosionDamage;

  /** Radius of the explosion measured in tiles. */
  private final float explosionRadiusTiles;

  /** Conversion factor: number of world units per tile (used for radius calculation). */
  float tileSize = 1f; // currently unused, but kept for clarity and future refactors

  float worldRadius;

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
   * when it triggers the "entityDeath" event, the {@link #explode()} method is called.
   */
  @Override
  public void create() {
    super.create();
    System.out.println("[BomberExplosion] Component created for entity " + getEntity().getId());
    getEntity().getEvents().addListener("entityDeath", this::onDeath);
  }

  /**
   * Handles what happens when the entity dies: first trigger the pre-explosion animation event,
   * then schedule or directly call explode().
   */
  private void onDeath() {
    getEntity().getEvents().trigger("bomberPreExplode");

    //    // Schedule explosion after 0.5 seconds
    //    Timer.schedule(
    //        new Timer.Task() {
    //          @Override
    //          public void run() {
    //            explode();
    //          }
    //        },
    //        0.5f);
  }

  /**
   * Triggers an explosion centered at the bomber's current tile position.
   *
   * <p>The method:
   *
   * <ol>
   *   <li>Finds the bomber's tile position using the game grid.
   *   <li>Iterates over all entities in the game world.
   *   <li>Skips itself, ignores entities with no position, and checks tile distance.
   *   <li>If within the explosion radius, subtracts health and triggers death handling on that
   *       entity.
   * </ol>
   */
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

    System.out.println("[BomberExplosion] Tile center row=" + centerRow + " col=" + centerCol);

    for (Entity target : ServiceLocator.getEntityService().getEntities()) {

      // Skip the bomber itself.
      if (target == entity) continue;

      // Skip entities with no position set.
      Vector2 pos = target.getPosition();
      if (pos == null) continue;

      // Convert target position to tile coordinates.
      int targetCol = Math.round(pos.x / tileWidth);
      int targetRow = Math.round(pos.y / tileHeight);

      // Calculate distance between target and bomber in tile space.
      int dCol = Math.abs(targetCol - centerCol);
      int dRow = Math.abs(targetRow - centerRow);

      if (dCol <= explosionRadiusTiles && dRow <= explosionRadiusTiles) {

        CombatStatsComponent combat = target.getComponent(CombatStatsComponent.class);
        if (combat == null) continue;

        System.out.println(
            "[BomberExplosion] Hitting target "
                + target.getId()
                + " at row="
                + targetRow
                + ", col="
                + targetCol);

        combat.setHealth(combat.getHealth() - explosionDamage);
        combat.handleDeath();
      }
    }
  }
}
