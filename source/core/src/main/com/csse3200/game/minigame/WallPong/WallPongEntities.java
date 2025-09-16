package com.csse3200.game.minigame.WallPong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;

public class WallPongEntities {

    public static Entity createPaddle(){
        float ScreenWidth= Gdx.graphics.getWidth();
        float ScreenHeight= Gdx.graphics.getHeight();

        Entity paddle = new Entity();
        paddle.addComponent(new PhysicsComponent());
        paddle.addComponent(new TextureRenderComponent("images/paddle.png"));
        TextureRenderComponent paddleRender= paddle.getComponent(TextureRenderComponent.class);
        paddleRender.scaleEntity();

        paddle.setScale(WallPongConfig.PADDLE_WIDTH,WallPongConfig.PADDLE_HEIGHT);
        paddle.addComponent(new ColliderComponent().setLayer(PhysicsLayer.PADDLE));
        ColliderComponent collider_paddle = paddle.getComponent(ColliderComponent.class);
        collider_paddle.setRestitution(1f);
        collider_paddle.setAsBox(new Vector2(1f,0.2f));
        paddle.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.KinematicBody);

        float paddleStartX= -(ScreenWidth / 4f);
        float paddleStartY= -(ScreenHeight/0.6f);
        PhysicsComponent paddlePhysics = paddle.getComponent(PhysicsComponent.class);
        paddle.setPosition(paddleStartX,paddleStartY);

        ServiceLocator.getEntityService().register(paddle);
        ServiceLocator.getRenderService().register(paddleRender);
        return paddle;
    }
}
