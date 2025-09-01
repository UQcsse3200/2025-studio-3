package com.csse3200.game.physics.components;

import com.csse3200.game.components.Component;
import com.badlogic.gdx.math.Vector2;

public class ProjectileBoundsComponent extends Component{
    private final float worldWidth;
    private final float worldHeight;

    public ProjectileBoundsComponent(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }
    @Override
    public void update(){
        Vector2 position = entity.getPosition();
        if(position.x<0||position.x>worldWidth||position.y<0||position.y>worldHeight){
            entity.dispose();
        }
    }

}
