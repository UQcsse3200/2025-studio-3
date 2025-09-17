package com.csse3200.game.minigame;

public class LaneManager {
  private final int numLanes = 3;
  private final float totalLaneWidth;
  private final float laneWidth;
  private final float[] laneCenters;

  public LaneManager(float screenWidth) {
    this.totalLaneWidth = screenWidth * 0.5f;
    this.laneWidth = totalLaneWidth / numLanes;
    this.laneCenters = new float[numLanes];

    float leftMargin = (screenWidth - totalLaneWidth) / 2;
    for (int i = 0; i < numLanes; i++) {
      laneCenters[i] = leftMargin + (i * laneWidth) + (laneWidth / 2);
    }
  }

  public float getLaneCenter(int lane) {

    if (lane < 0 || lane >= numLanes) {
      // Return the closest lane if out of bounds, preventing crashes
      if (lane < 0) return laneCenters[0];
      return laneCenters[numLanes - 1];
    }
    return laneCenters[lane];
  }

  public float getLaneWidth() {
    return laneWidth;
  }

  public int getNumLanes() {
    return numLanes;
  }
}
