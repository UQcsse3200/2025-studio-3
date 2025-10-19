package com.csse3200.game.cutscene.models.dto;

import java.util.HashMap;
import java.util.Map;

public class CharacterDTO {
  private String id;
  private String name;
  private Map<String, String> poses = new HashMap<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getPoses() {
    return poses;
  }

  public void setPoses(Map<String, String> poses) {
    this.poses = poses;
  }
}
