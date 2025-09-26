package com.csse3200.game.cutscene.runtime.action;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.csse3200.game.cutscene.models.object.actiondata.ChoiceData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.states.ChoiceState;
import com.csse3200.game.cutscene.runtime.states.DialogueState;

import java.util.Objects;

public class ChoiceAction implements ActionState {

    public ChoiceAction(ChoiceState choiceState, DialogueState dialogueState, ChoiceData data) {
        dialogueState.set(data.prompt(), "");
        dialogueState.setVisible(true);

        data.choices().forEach(choice -> {
            if (Objects.equals(choice.getCutsceneId(), "current")) {
//                choiceState.addChoice(); // Use button factory to create buttons
            }
        });
    }

    /**
     * Runs on every game tick to progress logic
     *
     * @param dtMs The delta time in milliseconds
     */
    @Override
    public void tick(int dtMs) {

    }

    /**
     * Triggered on skip, will fast track any logic to its final state
     */
    @Override
    public void skip() {

    }

    /**
     * @return True if the action is blocking till completion (false if async)
     */
    @Override
    public boolean blocking() {
        return false;
    }

    /**
     * @return True if the action is completed (can be disposed of)
     */
    @Override
    public boolean done() {
        return false;
    }
}
