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

    public TileHitboxComponent(float maxPosX, float maxPosY, float minPosX, float minPosY) {
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

    public boolean inTileHitbox(GridPoint2 pos) {
        return pos.x >= minPosX && pos.x <= maxPosX && pos.y >= minPosY && pos.y <= maxPosY;
    }
}
