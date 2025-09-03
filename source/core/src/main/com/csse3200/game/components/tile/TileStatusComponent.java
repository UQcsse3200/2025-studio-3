package com.csse3200.game.components.tile;

import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.components.Component;

public class TileStatusComponent extends Component {
    private boolean _contains_unit;
    private int _position;
    private final AreaAPI area;

    public TileStatusComponent(AreaAPI area) {
        this.area = area;
        _contains_unit = false;
        _position = 0;
    }
    public void addUnit() {
        _contains_unit = true;
        area.spawnUnit(_position);
    }
    public void removeUnit(){
        _contains_unit = false;
        area.removeUnit(_position);
    }

    public boolean hasUnit() {
        return _contains_unit;
    }

    public int get_position() {
        return _position;
    }

    public void set_position(int position) {
        _position = position;
    }

    public AreaAPI getArea() {
        return area;
    }
}
