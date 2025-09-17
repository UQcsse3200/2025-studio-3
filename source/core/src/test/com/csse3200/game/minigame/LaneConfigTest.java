package com.csse3200.game.minigame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

 class LaneConfigTest {

    @Test
    void testLaneConstants(){
        assertEquals(3,LaneConfig.NUM_LANES);
        assertTrue(LaneConfig.LANE_WIDTH* LaneConfig.NUM_LANES <= LaneConfig.SCREEN_WIDTH);
    }
    @Test
    void testObstacleConstants(){
        assertTrue(LaneConfig.OBSTACLE_WIDTH>0);
        assertTrue(LaneConfig.OBSTACLE_HEIGHT>0);
        assertEquals(10,LaneConfig.OBSTACLE_MAX_COUNT);

    }

}
