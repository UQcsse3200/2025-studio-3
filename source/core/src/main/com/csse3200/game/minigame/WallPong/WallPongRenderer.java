package com.csse3200.game.minigame.WallPong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;

public class WallPongRenderer {
    public static void renderScreen(){
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static void syncEntities(Entity... entities){
        for(Entity entity : entities){
            Vector2 pos = entity.getPosition();
            entity.setPosition(pos.x,pos.y);
        }
    }
}
