package com.csse3200.game.minigame.WallPong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.ServiceLocator;

public class WallPongGame extends ScreenAdapter {

    private Entity paddle;
    private Entity ball;

    private float ScreenWidth;
    private float ScreenHeight;

    @Override
    public void show(){
        ScreenWidth = Gdx.graphics.getWidth();
        ScreenHeight = Gdx.graphics.getHeight();

        paddle=WallPongEntities.createPaddle();
        ball=WallPongEntities.createBall();

        float thickness=10f;
        createWall(thickness/2f,ScreenHeight/2f,thickness,ScreenHeight);
        createWall(ScreenWidth-thickness/2f,ScreenHeight/2f,thickness,ScreenHeight);
        createWall(ScreenWidth/2f,ScreenHeight- thickness/2f,ScreenWidth,thickness);
    }

    private void createWall(float x,float y,float width,float height) {
        Entity wall = new Entity();
        wall.addComponent(new PhysicsComponent());
        ColliderComponent collider = new ColliderComponent()
                .setLayer(PhysicsLayer.OBSTACLE)
                .setFriction(0f)
                .setRestitution(1f);
        wall.addComponent(collider);

        wall.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.StaticBody);
        wall.getComponent(ColliderComponent.class);
        wall.getComponent(PhysicsComponent.class).getBody().setTransform(x, y, 0);

        ServiceLocator.getEntityService().register(wall);
    }
    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


}
