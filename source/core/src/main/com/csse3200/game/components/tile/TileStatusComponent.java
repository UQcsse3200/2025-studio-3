package com.csse3200.game.components.tile;

import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.components.Component;

/**
 * Component to store status of an Tile Entity in the {@link com.csse3200.game.areas.LevelGameArea}
 */
public class TileStatusComponent extends Component {
    private boolean _contains_unit;
    private int _position;
    private final AreaAPI area;

    /**
     * Constructor
     * @param area implements AreaAPI
     */
    public TileStatusComponent(AreaAPI area) {
        this.area = area;
        _contains_unit = false;
        _position = 0;
    }

    /**
     * Adds a unit to the tile
     */
    public void addUnit() {
        _contains_unit = true;
        area.spawnUnit(_position);
    }

    /**
     * Removes a unit from the tile
     */
    public void removeUnit(){
        _contains_unit = false;
        area.removeUnit(_position);
    }

    /**
     * Getter for _contains_unit
     * @return true if unit on tile
     */
    public boolean hasUnit() {
        return _contains_unit;
    }

    /**
     * Getter for tile position
     *
     * @return _position
     */
    public int get_position() {
        return _position;
    }

    /**
     * Setter for _position
     * @param position of tile
     */
    public void set_position(int position) {
        _position = position;
    }

    /**
     * Getter for the area, allows access to AreaAPI methods
     * @return area implementing AreaAPI
     */
    public AreaAPI getArea() {
        return area;
    }
}
