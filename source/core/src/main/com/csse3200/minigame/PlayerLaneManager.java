package com.csse3200.minigame;

public class PlayerLaneManager {
    private int currentLane;
    private int[] LaneIndices ={0,1,2};

    public PlayerLaneManager(int startingLane) {
        if(startingLane < 0 || startingLane > 2){
            throw new IllegalArgumentException("Invalid starting lane");
        }
        this.currentLane = startingLane;
    }
    public int getCurrentLane() {
        return currentLane;
    }
    public void setCurrentLane(int Lane) {
        if(Lane < 0 || Lane > 2){
            throw new IllegalArgumentException("Invalid lane");
        }
        this.currentLane = Lane;
    }
    public int[] getLaneIndices() {
        return LaneIndices;
    }
}
