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
        TextureRenderComponent paddleRender= paddle.getComponent(TextureRenderComponent.class);
        paddle.addComponent(paddleRender);
        paddleRender.scaleEntity();
        paddle.setScale(700f,700f);
        paddle.addComponent(new ColliderComponent().setLayer(PhysicsLayer.PADDLE));
        ColliderComponent collider_paddle = paddle.getComponent(ColliderComponent.class);
        collider_paddle.setRestitution(1f);
        paddle.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.KinematicBody);

        float paddleStartX= -(ScreenWidth / 4f);
        float paddleStartY= -(ScreenHeight/1f);
        PhysicsComponent paddlePhysics = paddle.getComponent(PhysicsComponent.class);
        paddle.setPosition(paddleStartX,paddleStartY);

        ServiceLocator.getEntityService().register(paddle);
        ServiceLocator.getRenderService().register(paddleRender);

        //Ball component

        ball = new Entity();
        ball.addComponent(new PhysicsComponent());
        ball.addComponent(new TextureRenderComponent("images/ball.png"));
        TextureRenderComponent ballRender= ball.getComponent(TextureRenderComponent.class);
        ball.addComponent(ballRender);
        ballRender.scaleEntity();
        ball.setScale(200f,200f);
        ball.addComponent(new ColliderComponent().setLayer(PhysicsLayer.BALL));
        ColliderComponent collider_ball = ball.getComponent(ColliderComponent.class);
        collider_ball.setRestitution(1f);
        collider_ball.setFriction(0f);
        ball.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.DynamicBody);
        ball.getComponent(PhysicsComponent.class).getBody().setLinearVelocity(50f,50f);
        ball.getComponent(PhysicsComponent.class).getBody().setLinearDamping(0f);
        ball.getComponent(PhysicsComponent.class).getBody().setAngularDamping(0f);

        float ballStartX = paddleStartX;
        float ballStartY= 80f;
        PhysicsComponent ballPhysics = ball.getComponent(PhysicsComponent.class);
        ball.setPosition(ballStartX,ballStartY);
        ServiceLocator.getEntityService().register(ball);
        ServiceLocator.getRenderService().register(ballRender);
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
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float paddleSpeed = 500f*Gdx.graphics.getDeltaTime();
        PhysicsComponent paddlePhysics = paddle.getComponent(PhysicsComponent.class);

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
          //  paddlePhysics.getBody().setLinearVelocity(-paddleSpeed, 0);
            paddle.setPosition(Math.min(0,paddle.getPosition().x-paddleSpeed),paddle.getPosition().y);
        }if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            //paddlePhysics.getBody().setLinearVelocity(paddleSpeed, 0);
            float maxX = ScreenWidth-paddle.getScale().x;
            paddle.setPosition(Math.max(maxX,paddle.getPosition().x+ paddleSpeed),paddle.getPosition().y);
        }
        else{
            paddlePhysics.getBody().setLinearVelocity(0, 0);
        }

        syncEntityPositions();

        //end minigame if ball falls below screen
        if(ball.getComponent(PhysicsComponent.class).getBody().getPosition().y<0){
            endGame();
        }

        ServiceLocator.getEntityService().update();
    }
    private void syncEntityPositions(){
        Vector2 paddlePos = paddle.getComponent(PhysicsComponent.class).getBody().getPosition();
        paddle.setPosition(paddlePos.x,paddlePos.y);

        Vector2 ballPos = ball.getComponent(PhysicsComponent.class).getBody().getPosition();
        ball.setPosition(ballPos.x,ballPos.y);
    }

    private void endGame(){
        System.out.println("Game Over ! Ending Minigame .... ");
        dispose();

    }

    @Override
    public void dispose() {
        if(paddle!=null){
            ServiceLocator.getEntityService().unregister(paddle);
            paddle=null;
        }
        if(ball!=null){
            ServiceLocator.getEntityService().unregister(ball);
            ball=null;
        }
    }
}


