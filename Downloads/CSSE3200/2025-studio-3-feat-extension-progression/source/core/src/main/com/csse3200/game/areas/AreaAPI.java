package com.csse3200.game.areas;

import com.csse3200.game.entities.Entity;

/**
 * Interface to inject LevelGameArea into components to safely have access to Entities and
 * Components
 */
public interface AreaAPI {
  /**
   * Gets the game grid for this area.
   *
   * @return the level game grid
   */
  public LevelGameGrid getGrid();

  /**
   * Gets the currently selected unit.
   *
   * @return the selected entity, or null if none selected
   */
  public Entity getSelectedUnit();

  /**
   * Sets the currently selected unit.
   *
   * @param unit the entity to select
   */
  public void setSelectedUnit(Entity unit);

  /**
   * Spawns a unit at the specified position.
   *
   * @param position the position to spawn the unit at
   */
  public void spawnUnit(int position);

  /**
   * Removes a unit from the specified position.
   *
   * @param position the position to remove the unit from
   */
  public void removeUnit(int position);

  /**
   * Gets the size of tiles in this area.
   *
   * @return the tile size
   */
  public float getTileSize();
}
