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

    public TileStorageComponent(AreaAPI area) {
        this.area = area;
        this.containsUnit = false;
        this.position = 0;
    }

    /**
     * Adds a unit to the tile
     */
    public void addTileUnit() {
        if (!containsUnit) {
            this.containsUnit = true;
            area.spawnUnit(position);
        }
    }

    /**
     * Removes the unit from the tile
     */
    public void removeTileUnit() {
        this.containsUnit = false;
        area.removeUnit(position);
    }

    public boolean hasUnit() {
        return containsUnit;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public AreaAPI getArea() {
        return area;
    }
}
