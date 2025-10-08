package com.csse3200.game.cutscene.models.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CutsceneDTO {
  public String id;
  public Map<String, Object> variables = new HashMap<>();
  public List<BeatDTO> beats = new ArrayList<>();
}
