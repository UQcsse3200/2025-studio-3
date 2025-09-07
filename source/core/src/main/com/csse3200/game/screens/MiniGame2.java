package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;

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

        paddletexture = new Texture("paddle.png");
        balltexture = new Texture("ball.png");

        paddle = new Entity();
        paddle.addComponent(new ColliderComponent());
        paddle.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.KinematicBody);

        ball = new Entity();
        ball.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.DynamicBody);
        ball.addComponent(new ColliderComponent());

    }
}


