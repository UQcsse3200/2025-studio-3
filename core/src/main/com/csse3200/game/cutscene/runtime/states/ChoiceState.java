package com.csse3200.game.cutscene.runtime.states;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import java.util.ArrayList;
import java.util.List;

public class ChoiceState {
  private boolean active;
  private List<Button> choices;

  public ChoiceState() {
    this.active = false;
    this.choices = new ArrayList<>();
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setChoices(List<Button> choices) {
    this.choices = choices;
  }

  public void addChoice(Button button) {
    this.choices.add(button);
  }

  public void removeChoice(Button button) {
    this.choices.remove(button);
  }

  public void clearChoices() {
    this.choices.clear();
  }

  public List<Button> getChoices() {
    return choices;
  }

  public boolean isActive() {
    return active;
  }
}
