package com.csse3200.minigame;

import com.csse3200.game.entities.Entity;

public class PlayerLaneManager {
    private int currentLane;
    private int[] LaneIndices ={0,1,2};
    private final LaneManager laneManager;
    private final Entity player;

    public PlayerLaneManager(Entity player, LaneManager laneManager) {
        this.player = player;
        this.laneManager = laneManager;
        this.currentLane = 1;
        movePlayertoLane(currentLane);
    }
    public void movePlayertoLane(int LaneIndex) {
        if(LaneIndex<0||LaneIndex>=LaneIndices.length){
            throw new IllegalArgumentException("Invalid lane index");
        }
        this.currentLane = LaneIndex;

        float x = laneManager.getLaneCenter(LaneIndex);
        float y= player.getPosition().y;
        player.setPosition(x, y);
    }
    public int getCurrentLane() {
        return currentLane;
    }
    //public void setCurrentLane(int Lane) {
      //  if(Lane < 0 || Lane > 2){
        //    throw new IllegalArgumentException("Invalid lane");
        //}
        //this.currentLane = Lane;
    //}
    public int[] getLaneIndices() {
        return LaneIndices;
    }
}
