package com.csse3200.game.components.tile;

import com.badlogic.gdx.Input;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Input handler for tiles for mouse input.
 * This input handler uses touch input.
 */
public class TileInputComponent extends InputComponent {

    private static final Logger logger = LoggerFactory.getLogger(TileInputComponent.class);

    public TileInputComponent() {
        super(5);
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
        logger.info("Click handler started");
        TileStatusComponent tileStatus = entity.getComponent(TileStatusComponent.class);
        Entity selected_unit = tileStatus.getArea().getSelectedUnit();

        // Need to check if click is within bounds of the tile and then do the below

        switch (button) {
            case Input.Buttons.LEFT:
                logger.info("left Click processed");
                if (!tileStatus.hasUnit() && selected_unit != null) {
                    logger.info("Attempting to place unit");
                    tileStatus.addUnit();
                }
                return true;

            case Input.Buttons.RIGHT:
                logger.info("right Click processed");
                if (tileStatus.hasUnit()) {
                    logger.info("Attempting to remove unit");
                    tileStatus.removeUnit();
                }
                return true;
            default:
                return false;
        }
    }

}
