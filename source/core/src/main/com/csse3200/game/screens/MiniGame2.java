package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;

public abstract class MiniGame2 extends ScreenAdapter{
    private Entity ball;
    private Entity paddle;

    private final float ScreenWidth = Gdx.graphics.getWidth();
    private final float ScreenHeight = Gdx.graphics.getHeight();

    @Override
    public void show() {
        //Paddle component

        paddle = new Entity();
        paddle.addComponent(new PhysicsComponent());
        paddle.addComponent(new TextureRenderComponent("images/paddle.png"));
        paddle.getComponent(TextureRenderComponent.class).scaleEntity();
        paddle.addComponent(new ColliderComponent().setLayer(PhysicsLayer.PADDLE));
        ColliderComponent collider_paddle = paddle.getComponent(ColliderComponent.class);
        collider_paddle.setRestitution(1f);
        paddle.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.KinematicBody);

        float paddleStartX= ScreenWidth /2f;
        float paddleStartY= 50f;
        PhysicsComponent paddlePhysics = paddle.getComponent(PhysicsComponent.class);
        paddle.setPosition(paddleStartX,paddleStartY);



        ServiceLocator.getEntityService().register(paddle);

        //Ball component

        ball = new Entity();
        ball.addComponent(new PhysicsComponent());
        ball.addComponent(new TextureRenderComponent("images/ball.png"));
        ball.getComponent(TextureRenderComponent.class).scaleEntity();
        ball.addComponent(new ColliderComponent().setLayer(PhysicsLayer.BALL));
        ColliderComponent collider_ball = ball.getComponent(ColliderComponent.class);
        collider_ball.setRestitution(1f);
        collider_ball.setFriction(0f);
        ball.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.DynamicBody);
        ball.getComponent(PhysicsComponent.class).getBody().setLinearVelocity(50f,50f);
        ball.getComponent(PhysicsComponent.class).getBody().setLinearDamping(0f);
        ball.getComponent(PhysicsComponent.class).getBody().setAngularDamping(0f);

        float ballStartX = paddleStartX;
        float ballStartY= 60f;
        PhysicsComponent ballPhysics = ball.getComponent(PhysicsComponent.class);
        ball.setPosition(ballStartX,ballStartY);
        ServiceLocator.getEntityService().register(ball);

        //wall component

        float thickness = 10f;

        createbbwall(thickness/2f, ScreenHeight/2f, thickness, ScreenHeight);
        createbbwall(ScreenWidth-thickness/2f, ScreenHeight/2f, thickness, ScreenHeight);
        createbbwall(ScreenWidth/2f, ScreenHeight - thickness/2f,ScreenWidth, thickness);


    }

    private void createbbwall(float x, float y, float width, float height) {
        Entity bbwall = new Entity();
        bbwall.addComponent(new PhysicsComponent());
        ColliderComponent collider = new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE);
        bbwall.addComponent(collider);

        bbwall.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.StaticBody);

        bbwall.getComponent(ColliderComponent.class).setAsBox(new Vector2(width/2f,height/2f));
        bbwall.getComponent(PhysicsComponent.class).getBody().setTransform(x,y,0);

        ServiceLocator.getEntityService().register(bbwall);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float paddleSpeed = 75f;
        PhysicsComponent paddlePhysics = paddle.getComponent(PhysicsComponent.class);

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            paddlePhysics.getBody().setLinearVelocity(-paddleSpeed, 0);
        }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            paddlePhysics.getBody().setLinearVelocity(paddleSpeed, 0);
        }else{
            paddlePhysics.getBody().setLinearVelocity(0, 0);
        }

        //end minigame if ball falls below screen
        if(ball.getComponent(PhysicsComponent.class).getBody().getPosition().y<0){
            endGame();
        }

        ServiceLocator.getEntityService().update();
    }


    private void endGame(){
        System.out.println("Game Over ! Ending Minigame .... ");
        dispose();

    }

    @Override
    public void dispose() {

    }
}


