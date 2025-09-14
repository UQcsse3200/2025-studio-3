package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.csse3200.game.components.Component;

public class ObstacleComponent extends Component {
    private float speed;
    private boolean isAlive;
    private float screenBottom;


    public ObstacleComponent(float speed) {
        this.speed = speed;
        this.isAlive = true;
        this.screenBottom = 0.2f;

    }

    @Override
    public void update() {
        if (!isAlive) {
            return;
        }
        moveDown();
    }
    private void moveDown() {
        float currentY = entity.getPosition().y;
        float deltaTime = Gdx.graphics.getDeltaTime();
        float newY = currentY - (speed * deltaTime);
        entity.setPosition(entity.getPosition().x, newY);
    }

    private boolean isOffScreen() {
        return entity.getPosition().y < screenBottom;
    }
    private void setSpeed(float speed) {
        this.speed = speed;
    }
    private float getSpeed() {
        return speed;
    }
    public void destroy() {
        isAlive = false;
    }
    public boolean isAlive() {
        return isAlive;
    }
    public void activate() {
        isAlive = true;
    }
    public void setScreenBottom(float screenBottom) {
        this.screenBottom = screenBottom;
    }
}
