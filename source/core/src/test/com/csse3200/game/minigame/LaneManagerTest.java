package com.csse3200.game.minigame;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LaneManagerTest {
     @Test
    void testLaneCenters(){
         LaneManager manager=new LaneManager(15f);
         assertEquals(manager.getLaneWidth(),15f*0.5f/3,0.01f);
         assertEquals(manager.getLaneCenter(0),manager.getLaneCenter(-1));
         assertEquals(manager.getLaneCenter(2),manager.getLaneCenter(5));
     }

}
