package com.csse3200.game.areas;

import com.csse3200.game.entities.Entity;

public interface AreaAPI {
    public LevelGameGrid getGrid();
    public Entity getSelectedUnit();
    public void setSelectedUnit(Entity unit);
    public void spawnUnit(int position);
    public void removeUnit(int position);
}
