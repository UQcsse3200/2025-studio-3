package com.csse3200.game.cutscene.runtime.action;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.cutscene.models.object.actiondata.ChoiceData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.cutscene.runtime.states.ChoiceState;
import com.csse3200.game.cutscene.runtime.states.DialogueState;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;

import java.util.Objects;

public class ChoiceAction implements ActionState {

    public ChoiceAction(CutsceneOrchestrator orchestrator, ChoiceState choiceState,
                        DialogueState dialogueState, ChoiceData data) {
        if (!data.prompt().equals("")) {
            dialogueState.set(data.prompt(), "");
            dialogueState.setVisible(true);
        }

        choiceState.setActive(true);

        data.choices().forEach(choice -> {
            if (Objects.equals(choice.getCutsceneId(), "current")) {
//                choiceState.addChoice(); // Use button factory to create buttons
                Button button = ButtonFactory.createButton(choice.getLine());
                button.setFillParent(false);
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        orchestrator.choose(choice.getEntryBeatId());
                    }
                });
                choiceState.addChoice(button);
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
