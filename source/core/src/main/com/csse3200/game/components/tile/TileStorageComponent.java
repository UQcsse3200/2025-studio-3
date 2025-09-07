package com.csse3200.game.components.tile;

import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A component that is used to track the status of the unit placed on a tile,
 * it is also used for adding and removing the units from the tile.
 */
public class TileStorageComponent extends Component {
    private boolean containsUnit;
    private int position;
    private final AreaAPI area;
    private Entity tileUnit;

    public TileStorageComponent(AreaAPI area) {
        this.area = area;
        this.containsUnit = false;
        this.position = 0;
        this.tileUnit = null;
    }

    /**
     * Triggers the spawning process for a unit
     */
    public void triggerSpawnUnit() {
        if (!containsUnit) {
            this.containsUnit = true;
            area.spawnUnit(position);
        }
    }

    /**
     * Sets the unit of a tile
     *
     * @param unit the unit being added to the tile
     */
    public void setTileUnit(Entity unit) {
        if (!containsUnit) {
            this.tileUnit = unit;
        }
    }

    /**
     * Gets the unit that a tile stores
     *
     * @return the unit of a tile
     */
    public Entity getTileUnit() {
        return this.tileUnit;
    }

    /**
     * Removes the unit from the tile
     */
    public void removeTileUnit() {
        this.containsUnit = false;
        area.removeUnit(position);
    }

    /**
     * Checks if a unit is being stored by this tile
     *
     * @return a boolean value for if the tile has a unit
     */
    public boolean hasUnit() {
        return containsUnit;
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
