package com.csse3200.game.minigame;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

public class LaneRunnerPlayerFactory {
    private static final String PLAYER_TEXTURE="images/box_boy.png";

    public static Entity createPlayer(){
        Entity player = new Entity()
                .addComponent(new TextureRenderComponent(PLAYER_TEXTURE))
                //.addComponent(new PhysicsComponent())
                //.addComponent(new ColliderComponent())
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PLAYER));

       // PhysicsUtils.setScaledCollider(player,0.6f,0.3f);
      //  player.getComponent(ColliderComponent.class).setDensity(1.5f);
        player.getComponent(TextureRenderComponent.class).scaleEntity();
        return player;
    }
    public static String[] getTextures(){
        return new String[]{PLAYER_TEXTURE};
    }
}
