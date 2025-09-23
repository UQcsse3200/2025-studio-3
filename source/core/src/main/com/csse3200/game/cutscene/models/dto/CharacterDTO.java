package com.csse3200.game.cutscene.models.dto;

import java.util.HashMap;
import java.util.Map;

public class CharacterDTO {
  public String id;
  public String name;
  public Map<String, String> poses = new HashMap<>();
}
