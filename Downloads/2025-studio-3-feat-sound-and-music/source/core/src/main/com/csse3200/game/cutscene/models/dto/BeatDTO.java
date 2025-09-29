package com.csse3200.game.cutscene.models.dto;

import java.util.ArrayList;
import java.util.List;

public class BeatDTO {
  public String id;
  public AdvanceDTO advance;
  public List<ActionDTO> actions = new ArrayList<>();
}
