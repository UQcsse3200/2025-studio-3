package com.csse3200.game.minigame;

import com.badlogic.gdx.graphics.Texture;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

public class ObstacleEntity extends Entity {
    private float speed;
    private float width, height;

    public ObstacleEntity(float x, float y, float width, float height, Texture tex, float speed) {
        this.speed = speed;
        this.width = width;
        this.height = height;

        this.addComponent(new TextureRenderComponent(tex));
        this.setPosition(x, y);
        this.addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
    }

    public void update(float delta) {
        setPosition(getPosition().x, getPosition().y - speed * delta);
    }

    public boolean isOffScreen() {
        return getPosition().y + height < 0;
    }

    // Get collider bounds for collision checks
    public float getLeft() { return getPosition().x - width / 2; }
    public float getRight() { return getPosition().x + width / 2; }
    public float getTop() { return getPosition().y + height / 2; }
    public float getBottom() { return getPosition().y - height / 2; }

    public boolean collidesWith(Entity other) {
        ColliderComponent col = other.getComponent(ColliderComponent.class);
        float oX = other.getPosition().x;
        float oY = other.getPosition().y;
        float oWidth = width;   // assuming same width
        float oHeight = height; // assuming same height

        return !(getRight() < oX - oWidth / 2 ||
                getLeft() > oX + oWidth / 2 ||
                getTop() < oY - oHeight / 2 ||
                getBottom() > oY + oHeight / 2);
    }
}