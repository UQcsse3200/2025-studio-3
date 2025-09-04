package com.csse3200.game.components.tile;

import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A component that is used the hitbox calculations of the tiles
 */
public class TileHitboxComponent extends Component {
    private static final Logger logger = LoggerFactory.getLogger(TileHitboxComponent.class);
    private float maxPosX;
    private float maxPosY;
    private float minPosX;
    private float minPosY;

    /**
     * Constructor for the TileHitboxComponent
     * @param maxPosX The highest x value of the tile
     * @param maxPosY The highest y value of the tile
     * @param minPosX The lowest x value of the tile
     * @param minPosY The lowest y value of the tile
     */
    public TileHitboxComponent(float maxPosX, float maxPosY, float minPosX, float minPosY) {
        if (maxPosX <= minPosX || maxPosY <= minPosY) {
            throw new IllegalArgumentException("The max x and y values must be bigger than the min x and y values");
        }
        this.maxPosX = maxPosX;
        this.maxPosY = maxPosY;
        this.minPosX = minPosX;
        this.minPosY = minPosY;
    }

    public float getMaxPosX() {
        return maxPosX;
    }

    public float getMaxPosY() {
        return maxPosY;
    }

    public float getMinPosX() {
        return minPosX;
    }

    public float getMinPosY() {
        return minPosY;
    }

    public void setMaxPosX(float maxPosX) {
        this.maxPosX = maxPosX;
    }

    public void setMaxPosY(float maxPosY) {
        this.maxPosY = maxPosY;
    }

    public void setMinPosX(float minPosX) {
        this.minPosX = minPosX;
    }

    public void setMinPosY(float minPosY) {
        this.minPosY = minPosY;
    }

    /**
     * Checks if the provided position is in the hitbox of the tile
     * @param pos a GridPoint2 that contains the x and y of the point being checked
     * @return a boolean depending on if the given point in the hitbox(true) or not(false)
     */
    public boolean inTileHitbox(GridPoint2 pos) {
        return pos.x >= minPosX && pos.x <= maxPosX && pos.y >= minPosY && pos.y <= maxPosY;
    }
}
