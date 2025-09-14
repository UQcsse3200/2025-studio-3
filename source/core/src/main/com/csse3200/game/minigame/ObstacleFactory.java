package com.csse3200.game.minigame;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

public class ObstacleFactory {
    private static final String ObstacleTexture = "images/heart.png";

    public static Entity createObstacle(float x, float y,float speed){
        Entity obstacle = new Entity()
                .addComponent(new ObstacleComponent(speed))
            .addComponent(new TextureRenderComponent(ObstacleTexture))
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));

        obstacle.setPosition(x, y);
        setObstaclescale(obstacle);
        return obstacle;
    }
    private static void setObstaclescale(Entity obstacle) {

        TextureRenderComponent textureComponent = obstacle.getComponent(TextureRenderComponent.class);

        if (textureComponent != null&&textureComponent.getTexture() != null) {
            Texture texture = textureComponent.getTexture();
            float scaleX=LaneConfig.OBSTACLE_WIDTH/texture.getWidth();
            float scaleY=LaneConfig.OBSTACLE_HEIGHT/texture.getHeight();
            obstacle.setScale(scaleX, scaleY);
        }
    }
    public static Entity slowObstacle(float x, float y) {
        float slowSpeed =LaneConfig.OBSTACLE_BASE_SPEED*0.6f;
        return createObstacle(x, y, slowSpeed);
    }
    public static Entity fastObstacle(float x, float y) {
        float fastSpeed =LaneConfig.OBSTACLE_BASE_SPEED*1.4f;
        return createObstacle(x, y, fastSpeed);
    }
}
