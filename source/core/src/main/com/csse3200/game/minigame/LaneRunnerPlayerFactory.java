package com.csse3200.game.minigame;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;

public class LaneRunnerPlayerFactory {
  private static final String PLAYER_TEXTURE = "images/entities/character.png";

  public static Entity createPlayer(LaneManager laneManager) {
    Entity player =
        new Entity()
            .addComponent(new TextureRenderComponent(PLAYER_TEXTURE))
            .addComponent(new MiniGameInputComponent(false));

    float scaleX = LaneConfig.OBSTACLE_WIDTH;
    float scaleY = LaneConfig.OBSTACLE_HEIGHT;
    player.setScale(scaleX, scaleY);
    int startingLane = 1;
    float x = laneManager.getLaneCenter(startingLane);
    float y = LaneConfig.PLAYER_Y;
    player.setPosition(x, y);
    return player;
  }
}
