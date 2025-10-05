package com.csse3200.game.components;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

/**
 * Component that causes the entity to deal AOE damage to nearby entities upon death.
 * <p>
 * This version uses a temporary, very large radius and high damage for easy visual confirmation.
 * Prints out debug messages when triggered and when hitting targets.
 */
public class BomberDeathExplodeComponent extends Component {
    private final int explosionDamage;
    private final float explosionRadiusTiles;

    float tileSize = 1f; // assuming 1 tile = 1 world unit
    float worldRadius ;

    public BomberDeathExplodeComponent(int explosionDamage, float explosionRadiusTiles) {
        this.explosionDamage = explosionDamage;
        this.explosionRadiusTiles = explosionRadiusTiles;
         worldRadius = explosionRadiusTiles * tileSize;
    }

    @Override
    public void create() {
        super.create();
        System.out.println("[BomberExplosion] Component created for entity " + getEntity().getId());
        getEntity().getEvents().addListener("entityDeath", this::explode);
    }

    private void explode() {
        System.out.println("[BomberExplosion] Triggered for " + entity.getId());

        Vector2 center = entity.getPosition();
        if (center == null) return;

        // Tile/grid constants (same as LevelGameArea)
        final int rows = 5;
        final int cols = 10;
        final float gridHeight = 450f;
        final float gridWidth = 990f;
        float tileWidth = gridWidth / cols;   // ≈ 99
        float tileHeight = gridHeight / rows; // ≈ 90

        // Convert bomber world position → tile row/col
        int centerCol = Math.round(center.x / tileWidth);
        int centerRow = Math.round(center.y / tileHeight);

        System.out.println("[BomberExplosion] Tile center row=" + centerRow + " col=" + centerCol);

        for (Entity target : ServiceLocator.getEntityService().getEntities()) {
            if (target == entity) continue;

            Vector2 pos = target.getPosition();
            if (pos == null) continue;

            int targetCol = Math.round(pos.x / tileWidth);
            int targetRow = Math.round(pos.y / tileHeight);

            int dCol = Math.abs(targetCol - centerCol);
            int dRow = Math.abs(targetRow - centerRow);

            // Check if within 3x3 tile square
            if (dCol <= explosionRadiusTiles && dRow <= explosionRadiusTiles) {
                CombatStatsComponent combat = target.getComponent(CombatStatsComponent.class);
                if (combat == null) continue;

                System.out.println("[BomberExplosion] Hitting target " + target.getId() +
                        " at row=" + targetRow + ", col=" + targetCol);

                combat.setHealth(combat.getHealth() - explosionDamage);
                combat.handleDeath();
            }
        }
    }
}