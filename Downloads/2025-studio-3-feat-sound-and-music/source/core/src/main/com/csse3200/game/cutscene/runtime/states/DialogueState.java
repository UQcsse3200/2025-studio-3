package com.csse3200.game.cutscene.runtime.states;

/** State responsible for all rendering of the dialogue box */
public class DialogueState {
  private boolean visible;
  private String speaker = "";
  private String text = "";
  private int textProgress;
  private boolean canAdvance;

  /**
   * Set the speaker and text parameters
   *
   * @param speaker The text that will show up above the dialogue
   * @param text The text that will be "spoken"
   */
  public void set(String speaker, String text) {
    this.speaker = speaker;
    this.text = text;
  }

  /** Increment the text progress by 1 character */
  public void incTextProgress() {
    textProgress = Math.max(++textProgress, text.length());
  }

  /**
   * Get the character at the current progress
   *
   * @return The last visible character
   */
  public char getCurrentCharacter() {
    return text.charAt(textProgress);
  }

  public boolean isVisible() {
    return visible;
  }

  public String getSpeaker() {
    return speaker;
  }

  public String getText() {
    return text;
  }

  public int getTextProgress() {
    return textProgress;
  }

  public boolean isCanAdvance() {
    return canAdvance;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public void setSpeaker(String speaker) {
    this.speaker = speaker;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setTextProgress(int textProgress) {
    this.textProgress = textProgress;
  }

  public void setCanAdvance(boolean canAdvance) {
    this.canAdvance = canAdvance;
  }
}
