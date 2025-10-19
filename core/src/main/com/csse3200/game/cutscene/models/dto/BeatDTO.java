package com.csse3200.game.cutscene.models.dto;

import java.util.ArrayList;
import java.util.List;

public class BeatDTO {
  private String id;
  private AdvanceDTO advance;
  private List<ActionDTO> actions = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AdvanceDTO getAdvance() {
    return advance;
  }

  public void setAdvance(AdvanceDTO advance) {
    this.advance = advance;
  }

  public List<ActionDTO> getActions() {
    return actions;
  }

  public void setActions(List<ActionDTO> actions) {
    this.actions = actions;
  }
}
