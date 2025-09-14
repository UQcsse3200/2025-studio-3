package com.csse3200.game.minigame;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;

public class LaneRunnerPlayerFactory {
    private static final String PLAYER_TEXTURE="images/box_boy.png";

    public static Entity createPlayer(LaneManager laneManager){
        Entity player = new Entity()
                .addComponent(new TextureRenderComponent(PLAYER_TEXTURE))
                .addComponent(new MiniGameInputComponent());
        player.setScale(64f,64f);
        int startingLane = 1;
        return player;
    }
}
