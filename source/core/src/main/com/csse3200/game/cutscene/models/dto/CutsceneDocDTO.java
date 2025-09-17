package com.csse3200.game.cutscene.models.dto;

import java.util.ArrayList;
import java.util.List;

public class CutsceneDocDTO {
  public Integer schemaVersion;
  public List<CharacterDTO> characters = new ArrayList<>();
  public List<BackgroundDTO> backgrounds = new ArrayList<>();
  public List<SoundDTO> sounds = new ArrayList<>();
  public CutsceneDTO cutscene;
}
