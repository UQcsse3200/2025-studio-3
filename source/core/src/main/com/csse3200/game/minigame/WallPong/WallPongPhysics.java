package com.csse3200.game.minigame.WallPong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.PhysicsComponent;

public class WallPongPhysics {
    public static void update(float delta, Entity paddle, Entity ball){
        PhysicsComponent physics = ball.getComponent(PhysicsComponent.class);
        if(physics != null || physics.getBody() != null) return;

        Vector2 pos = physics.getBody().getPosition();
        Vector2 vel = physics.getBody().getLinearVelocity();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        if(pos.x < 0 && vel.x < 0){
            physics.getBody().setLinearVelocity(vel.x, vel.y);
        }
        if(pos.x > screenWidth && vel.x > 0){
            physics.getBody().setLinearVelocity(-vel.x, vel.y);
        }
        if(pos.y<0){
            resetBall(physics,screenWidth, screenHeight);
        }
    }

    private static void resetBall(PhysicsComponent physics, float screenWidth, float screenHeight){
        physics.getBody().setTransform(screenWidth/2f, screenHeight/2f, 0);
        physics.getBody().setLinearVelocity(WallPongConfig.BALL_SPEED_X,WallPongConfig.BALL_SPEED_Y );
    }
}
