package com.csse3200.game.minigame;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

public class LaneFactory {
    public static Entity createLane(float x, float y, float width, float height, Texture texture){
        Entity lane= new Entity()
                .addComponent(new TextureRenderComponent(texture))
                .addComponent(new PhysicsComponent());
        lane.setPosition(x, y);
        lane.setScale(width/ texture.getWidth(), height/ texture.getHeight());
        return lane;

    }
}
