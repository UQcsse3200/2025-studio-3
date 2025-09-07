package com.csse3200.game.minigame;

public class LaneManager {
    private final float screenWidtth;
    private final int numLanes=3;
   private final float totalLaneWidth;
   private final float LaneWidth;
   private final float[] LaneCenter;

   public LaneManager(float screenWidtth) {
       this.screenWidtth = screenWidtth;
       this.totalLaneWidth = screenWidtth * 0.5f;
       this.LaneWidth = totalLaneWidth/numLanes;
       this.LaneCenter = new float[numLanes];

       float leftMargin = (screenWidtth-totalLaneWidth)/2;
       for(int i=0;i<numLanes;i++){
          LaneCenter[i] =  leftMargin + LaneWidth/2 *i * LaneWidth;
       }
   }
   public float getLaneCenter(int laneIndex){
       if(laneIndex<0 || laneIndex>=numLanes){
           throw new IllegalArgumentException("LaneIndex out of bounds");
       }
       return LaneCenter[laneIndex];
   }
   public float getLaneWidth(){
       return LaneWidth;
   }

    public int getNumLanes() {
       return numLanes;
    }
}
