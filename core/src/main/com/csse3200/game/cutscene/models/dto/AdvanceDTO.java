package com.csse3200.game.cutscene.models.dto;

public class AdvanceDTO {
  private String mode;
  private Integer delay;
  private String signalKey;

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public Integer getDelay() {
    return delay;
  }

  public void setDelay(Integer delay) {
    this.delay = delay;
  }

  public String getSignalKey() {
    return signalKey;
  }

  public void setSignalKey(String signalKey) {
    this.signalKey = signalKey;
  }
}
