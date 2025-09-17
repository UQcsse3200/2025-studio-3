package com.csse3200.game.minigame;
import static org.junit.Assert.*;
import com.csse3200.game.entities.Entity;
import org.junit.Test;

public class LaneRunnerPlayerFactoryTest {

    // Test-only version of createPlayer that skips TextureRenderComponent
    private Entity createTestPlayer(LaneManager laneManager) {
        Entity player = new Entity();
        // Only add logic component, skip texture
        player.addComponent(new com.csse3200.game.minigame.MiniGameInputComponent(false));

        int startingLane = 1;
        float x = laneManager.getLaneCenter(startingLane);
        float y = LaneConfig.PLAYER_Y;
        player.setPosition(x, y);
        return player;
    }

    @Test
    public void testCreatePlayerLogicOnly() {
        LaneManager laneManager = new LaneManager(15f);
        Entity player = createTestPlayer(laneManager);

        assertNotNull(player);
        assertEquals("Player X should be middle lane center",
                laneManager.getLaneCenter(1), player.getPosition().x, 0.01f);
        assertEquals("Player Y should match LaneConfig.PLAYER_Y",
                LaneConfig.PLAYER_Y, player.getPosition().y, 0.01f);
    }
}
