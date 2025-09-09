package com.csse3200.game.minigame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;

public class LaneManager {
    private final int numLanes;
   private final float totalLaneWidth;
   private final float LaneWidth;
   private final float[] LaneCenter;

   public LaneManager(float screenWidth) {
       this.numLanes=LaneConfig.NUM_LANES;
       this.totalLaneWidth = screenWidth * 0.5f;
       this.LaneWidth = totalLaneWidth/numLanes;
       this.LaneCenter = new float[numLanes];

       float leftMargin = (screenWidth-totalLaneWidth)/2;
       for(int i=0;i<numLanes;i++){
          LaneCenter[i] =  leftMargin + (i*LaneWidth)+(LaneWidth/2);
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
  public void createLaneVisuals(Stage stage, Texture laneTex){
       for(int i=0;i<numLanes;i++){
           Image lane=new Image(new TextureRegionDrawable(new TextureRegion(laneTex)));
           lane.setFillParent(false);
           lane.setSize(LaneWidth,LaneConfig.LANE_HEIGHT);
           lane.setPosition(LaneCenter[i]-LaneWidth/2,LaneConfig.LANE_Y);
           stage.addActor(lane);
       }
  }
    /*public void createLaneEntities(EntityService entityService,Texture laneTex) {
        for(int i=0;i<numLanes;i++){
            float x = LaneCenter[i];
            float y = LaneConfig.LANE_Y;
            Entity lane=LaneFactory.createLane(x,y,LaneWidth, LaneConfig.LANE_HEIGHT,laneTex);
            entityService.register(lane);
        }*/
    }

