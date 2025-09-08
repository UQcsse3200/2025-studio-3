package com.csse3200.game.areas;

import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.entities.Entity;

/**
 * Interface to inject LevelGameArea into components to safely have access to Entities and Components
 */
public interface AreaAPI {
    LevelGameGrid getGrid();
    Entity getSelectedUnit();
    void setSelectedUnit(Entity unit);
    void spawnUnit(int position);
    void removeUnit(int position);
    float getTileSize();
    GridPoint2 stageToWorld(GridPoint2 pos);
    GridPoint2 worldToStage(GridPoint2 pos);
}
