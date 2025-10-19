package com.csse3200.game.cutscene.models.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionDTO {
  private String type;
  private Map<String, Object> fields = new HashMap<>();
  private List<ActionDTO> actions;

  public ActionDTO(String type, Map<String, Object> fields, List<ActionDTO> actions) {
    this.type = type;
    this.fields = fields;
    this.actions = actions;
  }

  public ActionDTO() {}

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Map<String, Object> getFields() {
    return fields;
  }

  public void setFields(Map<String, Object> fields) {
    this.fields = fields;
  }

  public List<ActionDTO> getActions() {
    return actions;
  }

  public void setActions(List<ActionDTO> actions) {
    this.actions = actions;
  }
}
