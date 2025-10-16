package com.csse3200.game.minigame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaneManager {
  private static final Logger logger = LoggerFactory.getLogger(LaneManager.class);  
  private static final int NUM_LANES = 3;
  private final float totalLaneWidth;
  private final float laneWidth;
  private final float[] laneCenters;

  public LaneManager(float screenWidth) {
    // Use 80% of screen width for lanes, centered
    this.totalLaneWidth = screenWidth * 0.8f;
    this.laneWidth = totalLaneWidth / NUM_LANES;
    this.laneCenters = new float[NUM_LANES];

    // Center the lanes on screen
    float leftMargin = (screenWidth - totalLaneWidth) / 2;
    for (int i = 0; i < NUM_LANES; i++) {
      laneCenters[i] = leftMargin + (i * laneWidth) + (laneWidth / 2);
    }

    logger.info("LaneManager created with totalLaneWidth: {}, laneWidth: {}, laneCenters: {}", totalLaneWidth, laneWidth, laneCenters);
    logger.info("Screen width: {}", screenWidth);
  }

  public float getLaneCenter(int lane) {

    if (lane < 0 || lane >= NUM_LANES) {
      // Return the closest lane if out of bounds, preventing crashes
      if (lane < 0) return laneCenters[0];
      return laneCenters[NUM_LANES - 1];
    }
    return laneCenters[lane];
  }

  public float getLaneWidth() {
    return laneWidth;
  }

  public int getNumLanes() {
    return NUM_LANES;
  }
}
