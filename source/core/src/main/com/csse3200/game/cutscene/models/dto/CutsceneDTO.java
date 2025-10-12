package com.csse3200.game.cutscene.models.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CutsceneDTO {
  private String id;
  private Map<String, Object> variables = new HashMap<>();
  private List<BeatDTO> beats = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, Object> variables) {
    this.variables = variables;
  }

  public List<BeatDTO> getBeats() {
    return beats;
  }

  public void setBeats(List<BeatDTO> beats) {
    this.beats = beats;
  }
}
