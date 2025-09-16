package com.csse3200.game.minigame.WallPong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.PhysicsComponent;

public class WallPongRenderer {
    public static void renderScreen(){
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static void syncEntities(Entity... entities){
        for(Entity entity : entities){
            PhysicsComponent phys = entity.getComponent(PhysicsComponent.class);
            if(phys != null && phys.getBody() != null){
                Vector2 position = phys.getBody().getPosition();
                entity.setPosition(position.x, position.y);
            }
        }
    }
}
