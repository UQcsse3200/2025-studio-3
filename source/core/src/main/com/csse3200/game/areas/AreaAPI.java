package com.csse3200.game.areas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
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

  /**
   * Converts stage to world coords
   *
   * @param pos the stage coords as GridPoint2
   * @return the world coords as GridPoint2
   */
  GridPoint2 stageToWorld(GridPoint2 pos);

  /**
   * Converts world to stage coords
   *
   * @param pos the world coords as GridPoint2
   * @return the stage coords as GridPoint2
   */
  GridPoint2 worldToStage(GridPoint2 pos);

  /**
   * Begins a drag operation with the given texture.
   *
   * @param texture the texture to show while dragging
   */
  default void beginDrag(Texture texture) {}

  /** Cancels an ongoing drag operation. */
  default void cancelDrag() {}

  /**
   * Checks if a character is currently selected.
   *
   * @return true if a character is selected, false otherwise
   */
  boolean isCharacterSelected();

  /**
   * Sets the character selection status.
   *
   * @param selected true to indicate a character is selected, false otherwise
   */
  void setIsCharacterSelected(boolean selected);
}
