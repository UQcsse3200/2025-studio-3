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
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;

public abstract class MiniGame2 extends ScreenAdapter{
    private Entity ball;
    private Entity paddle;

    private final float ScreenWidth = Gdx.graphics.getWidth();
    private final float ScreenHeight = Gdx.graphics.getHeight();

    @Override
    public void show() {
        GameTime gameTime = new GameTime();
        ServiceLocator.registerTimeSource(new GameTime());

        if(ServiceLocator.getPhysicsService() != null){
            ServiceLocator.registerPhysicsService(new PhysicsService());
        }

        //Paddle component
        paddle = new Entity();
        paddle.addComponent(new PhysicsComponent());
        paddle.addComponent(new TextureRenderComponent("images/paddle.png"));
        TextureRenderComponent paddleRender= paddle.getComponent(TextureRenderComponent.class);
        //paddle.addComponent(paddleRender);
        paddleRender.scaleEntity();
        paddle.setScale(700f,700f);
        paddle.addComponent(new ColliderComponent().setLayer(PhysicsLayer.PADDLE));
        ColliderComponent collider_paddle = paddle.getComponent(ColliderComponent.class);
        collider_paddle.setLayer(PhysicsLayer.PADDLE);
        collider_paddle.setRestitution(1f);
        collider_paddle.setAsBox(new Vector2(paddle.getScale().x/2f,paddle.getScale().y/2f));
        paddle.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.KinematicBody);

        float paddleStartX= -(ScreenWidth / 4f);
        float paddleStartY= -(ScreenHeight/0.6f);
        PhysicsComponent paddlePhysics = paddle.getComponent(PhysicsComponent.class);
        paddle.setPosition(paddleStartX,paddleStartY);

        ServiceLocator.getEntityService().register(paddle);
        ServiceLocator.getRenderService().register(paddleRender);

        //Ball component

        ball = new Entity();
        ball.addComponent(new PhysicsComponent());
        ball.addComponent(new TextureRenderComponent("images/ball.png"));
        TextureRenderComponent ballRender= ball.getComponent(TextureRenderComponent.class);
        //ball.addComponent(ballRender);
        ballRender.scaleEntity();
        ball.setScale(400f,300f);
        ball.addComponent(new ColliderComponent().setLayer(PhysicsLayer.BALL));
        ColliderComponent collider_ball = ball.getComponent(ColliderComponent.class);
        collider_ball.setRestitution(1f);
        collider_ball.setFriction(0f);
        collider_ball.setAsBox(new Vector2(ball.getScale().x/2f,ball.getScale().y/2f));

        ball.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.DynamicBody);
        ball.getComponent(PhysicsComponent.class).getBody().setLinearVelocity(900f,900f);
        ball.getComponent(PhysicsComponent.class).getBody().setLinearDamping(0f);
        ball.getComponent(PhysicsComponent.class).getBody().setAngularDamping(0f);
        ball.addComponent(new HitboxComponent());
        ball.addComponent(new TouchAttackComponent((short)(PhysicsLayer.PADDLE | PhysicsLayer.OBSTACLE), 0f));
        Vector2 paddlePos=paddle.getPosition();
        float ballStartX = paddlePos.x;
        float ballStartY= 0.06f;
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
        ColliderComponent collider = new ColliderComponent()
                .setLayer(PhysicsLayer.OBSTACLE)
                .setFriction(0f)
                .setRestitution(1f);

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

        Vector2 paddlePos = paddlePhysics.getBody().getPosition();
        float newX = paddlePos.x;

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newX = Math.max(-(1110f+paddle.getScale().x)/2f, paddlePos.x-paddleSpeed);
        }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newX = Math.min(ScreenWidth-100f-paddle.getScale().x/2f, paddlePos.x+paddleSpeed);
        }

        paddlePhysics.getBody().setTransform(newX, paddlePos.y, 0);
        paddlePhysics.getBody().setLinearVelocity(0,0);

        ServiceLocator.getPhysicsService().getPhysics().update();
        syncEntityPositions();

        PhysicsComponent ballPhysics = ball.getComponent(PhysicsComponent.class);
        Vector2 ballPos = ballPhysics.getBody().getPosition();
        Vector2 ballVel = ballPhysics.getBody().getLinearVelocity();

        if(ballPos.x-ball.getScale().x/2f<0 && ballVel.x<0){
            ballPos.x=ball.getScale().x/2f;
            ballVel.x=-ballVel.x;
        }else if(ballPos.x+ball.getScale().x/2f>ScreenWidth && ballVel.x>0){
            ballPos.x=ScreenWidth-ball.getScale().x/2f;
            ballVel.x=-ballVel.x;
        }

        if(ballPos.y+ball.getScale().y/2f>ScreenHeight && ballVel.y>0){
            ballPos.y=ScreenHeight-ball.getScale().y/2f;
            ballVel.y=-ballVel.y;
        }

        if((ballPos.y-ball.getScale().y/2f<=paddlePos.y+paddle.getScale().y/2f) &&
                (ballPos.y-ball.getScale().y/2f>=paddlePos.y) &&
                (ballPos.x+ball.getScale().x/2f>=paddlePos.x-paddle.getScale().x/2f) &&
                (ballPos.x-ball.getScale().x/2f<=paddlePos.x+paddle.getScale().x/2f)&&
                (ballVel.y<0)){
            ballPos.y=paddlePos.y+paddle.getScale().y/2f+ball.getScale().y/2f;
            ballVel.y=-ballVel.y;
        }

        ballPhysics.getBody().setTransform(ballPos.x, ballPos.y, 0);
        ballPhysics.getBody().setLinearVelocity(ballVel);
        //end minigame if ball falls below screen
        if(ball!=null && ball.getComponent(PhysicsComponent.class).getBody().getPosition().y<0){
            endGame();
            return;
        }
        if(ServiceLocator.getEntityService()!=null){
            ServiceLocator.getEntityService().update();
        }

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


