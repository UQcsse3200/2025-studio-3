package com.csse3200.game.areas;

import com.csse3200.game.entities.Entity;

/**
 * Interface to inject LevelGameArea into components to safely have access to Entities and Components
 */
public interface AreaAPI {
    public LevelGameGrid getGrid();
    public Entity getSelectedUnit();
    public void setSelectedUnit(Entity unit);
    public void spawnUnit(int position);
    public void removeUnit(int position);
    public float getTileSize();
}
