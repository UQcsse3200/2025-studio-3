package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

public abstract class MiniGame2 implements Screen {
    private SpriteBatch batch;
    private Texture paddletexture;
    private Texture balltexture;

    private Entity ball;
    private Entity paddle;

    private final float ScreenWidth = Gdx.graphics.getWidth();
    private final float ScreenHeight = Gdx.graphics.getHeight();

    @Override
    public void show() {
        batch = new SpriteBatch();

        paddletexture = new Texture("images/paddle.png");
        balltexture = new Texture("images/ball.png");

        paddle = new Entity();
        paddle.addComponent(new TextureRenderComponent(balltexture));
        paddle.getComponent(TextureRenderComponent.class).scaleEntity();
        paddle.addComponent(new PhysicsComponent());
        paddle.addComponent(new ColliderComponent().setLayer(PhysicsLayer.PADDLE));
        paddle.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.KinematicBody);

        ball = new Entity();
        ball.addComponent(new TextureRenderComponent(balltexture));
        ball.getComponent(TextureRenderComponent.class).scaleEntity();
        ball.addComponent(new PhysicsComponent());
        ball.addComponent(new ColliderComponent().setLayer(PhysicsLayer.BALL));
        ball.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.DynamicBody);
    }
}


