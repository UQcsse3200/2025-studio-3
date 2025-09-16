package com.csse3200.game.minigame.WallPong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;

public class WallPongEntities {

    public static Entity createPaddle(){
        /*float ScreenWidth= Gdx.graphics.getWidth();
        float ScreenHeight= Gdx.graphics.getHeight();*/

        Entity paddle = new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new TextureRenderComponent("images/paddle.png"))
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.PADDLE));

        paddle.setScale(WallPongConfig.PADDLE_WIDTH,WallPongConfig.PADDLE_HEIGHT);

        ColliderComponent collider = paddle.getComponent(ColliderComponent.class);
        collider.setAsBox(new Vector2(WallPongConfig.PADDLE_WIDTH/2f,WallPongConfig.PADDLE_HEIGHT/2f));
        collider.setRestitution(1f);

        paddle.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.KinematicBody);

        float startX = Gdx.graphics.getWidth()/2f;
        float startY = 50f;
        paddle.setPosition(startX,startY);

        return paddle;
    }

    public static Entity createBall(){
        Entity ball = new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new TextureRenderComponent("images/ball.png"))
                .addComponent(new ColliderComponent().setLayer(PhysicsLayer.BALL))
                        .addComponent(new HitboxComponent())
                                .addComponent(new TouchAttackComponent((short)(PhysicsLayer.PADDLE | PhysicsLayer.OBSTACLE), 0f));

        ball.setScale(WallPongConfig.BALL_RADIUS*2f, WallPongConfig.BALL_RADIUS*2f);

        ColliderComponent collider = ball.getComponent(ColliderComponent.class);
        collider.setAsBox(new Vector2(WallPongConfig.BALL_RADIUS, WallPongConfig.BALL_RADIUS));
        collider.setRestitution(1f);
        collider.setFriction(0f);

        PhysicsComponent physics = ball.getComponent(PhysicsComponent.class);
        physics.setBodyType(BodyDef.BodyType.DynamicBody);

        ServiceLocator.getEntityService().register(ball);
        return ball;
    }
}
