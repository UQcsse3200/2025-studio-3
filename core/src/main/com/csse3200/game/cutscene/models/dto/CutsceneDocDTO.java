package com.csse3200.game.cutscene.models.dto;

import java.util.ArrayList;
import java.util.List;

public class CutsceneDocDTO {
  private Integer schemaVersion;
  private List<CharacterDTO> characters = new ArrayList<>();
  private List<BackgroundDTO> backgrounds = new ArrayList<>();
  private List<SoundDTO> sounds = new ArrayList<>();
  private CutsceneDTO cutscene;

  public Integer getSchemaVersion() {
    return schemaVersion;
  }

  public void setSchemaVersion(Integer schemaVersion) {
    this.schemaVersion = schemaVersion;
  }

  public List<CharacterDTO> getCharacters() {
    return characters;
  }

  public void setCharacters(List<CharacterDTO> characters) {
    this.characters = characters;
  }

  public List<BackgroundDTO> getBackgrounds() {
    return backgrounds;
  }

  public void setBackgrounds(List<BackgroundDTO> backgrounds) {
    this.backgrounds = backgrounds;
  }

  public List<SoundDTO> getSounds() {
    return sounds;
  }

  public void setSounds(List<SoundDTO> sounds) {
    this.sounds = sounds;
  }

  public CutsceneDTO getCutscene() {
    return cutscene;
  }

  public void setCutscene(CutsceneDTO cutscene) {
    this.cutscene = cutscene;
  }
}
