package com.csse3200.game.components;

import com.badlogic.gdx.Input;
import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.input.InputComponent;

/**
 * Input handler for inventory units for mouse input.
 * This input handler uses touch input.
 */
public class InventoryUnitInputComponent extends InputComponent {

    private final AreaAPI area;
    public InventoryUnitInputComponent(AreaAPI area) {
        super(5);
        this.area = area;
    }

    /**
     * Action on mouse click on entity
     *
     * @param screenX The x coordinate, origin is in the upper left corner
     * @param screenY The y coordinate, origin is in the upper left corner
     * @param pointer the pointer for the event.
     * @param button the button
     * @return true if action taken, otherwise false
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        // Need to check if click is within bounds of the tile and then do the below

        switch (button) {
            case Input.Buttons.LEFT:
                area.setSelectedUnit(entity);
                return true;
            case Input.Buttons.RIGHT:
                area.setSelectedUnit(null);
                return true;
            default:
                return false;
        }
    }

}
