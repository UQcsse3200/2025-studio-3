package com.csse3200.game.cutscene.runtime.states;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

import java.util.List;

public class ChoiceState {
    private boolean active;
    private List<Button> choices;

    public ChoiceState(boolean active, List<Button> choices) {
        this.active = active;
        this.choices = choices;
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

    public boolean isActive() {
        return active;
    }
}
