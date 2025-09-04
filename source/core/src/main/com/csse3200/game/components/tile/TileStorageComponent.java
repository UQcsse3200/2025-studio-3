package com.csse3200.game.components.tile;

import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A component that is used to track the status of the unit placed on a tile,
 * it is also used for adding and removing the units from the tile.
 */
public class TileStorageComponent extends Component {
    private static final Logger logger = LoggerFactory.getLogger(TileStorageComponent.class);
    private Entity TileUnit;

    public TileStorageComponent() {
        this.TileUnit = null;
    }

    /**
     * Gets the unit that is currently on the tile
     * @return The unit currently on the tile or null if not unit is stored
     */
    public Entity getTileUnit() {
        return this.TileUnit;
    }

    /**
     * Adds a unit to the tile
     * @param unit the unit to be added
     */
    public void addTileUnit(Entity unit) {
        if (TileUnit == null) {
            this.TileUnit = unit;
            logger.debug("Unit {} has been added to this tile", unit);
        } else {
            logger.debug("Tile has unit already.");
        }
    }

    /**
     * Removes the unit from the tile
     */
    public void removeTileUnit() {
        this.TileUnit = null;
    }
}
