package com.csse3200.game.cutscene.models.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionDTO {
    public String type;
    public Map<String, Object> fields = new HashMap<>();
    public List<ActionDTO> actions;
}
