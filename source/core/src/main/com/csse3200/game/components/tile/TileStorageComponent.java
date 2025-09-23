package com.csse3200.game.components.tile;

import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

/**
 * A component that is used to track the status of the unit placed on a tile, it is also used for
 * adding and removing the units from the tile.
 */
public class TileStorageComponent extends Component {
  private int position;
  private final AreaAPI area;

  public TileStorageComponent(AreaAPI area) {
    this.area = area;
    this.position = 0;
  }

  /**
   * Sets the unit of a tile
   *
   * @param unit the unit being added to the tile
   */
  public boolean setTileUnit(Entity unit) {
    // returns false if already occupied
    return area.getGrid().placeOccupantIndex(position, unit);
  }

  /**
   * Gets the unit that a tile stores
   *
   * @return the unit of a tile
   */
  public Entity getTileUnit() {
    return area.getGrid().getOccupantIndex(position);
  }

  /** Removes the unit from the tile */
  public void removeTileUnit() {
    area.getGrid().clearOccupantIndex(position);
  }

  /**
   * Checks if a unit is being stored by this tile
   *
   * @return a boolean value for if the tile has a unit
   */
  public boolean hasUnit() {
    return area.getGrid().isOccupiedIndex(position);
  }

  /**
   * Gets the position of the tile
   *
   * @return the tile's position
   */
  public int getPosition() {
    return position;
  }

  /**
   * Sets the position of the tile
   *
   * @param position the position for the tile to be set at
   */
  public void setPosition(int position) {
    this.position = position;
  }

  public AreaAPI getArea() {
    return area;
  }
}
