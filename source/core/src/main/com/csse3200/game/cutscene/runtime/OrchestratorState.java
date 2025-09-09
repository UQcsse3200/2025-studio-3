package com.csse3200.game.cutscene.runtime;

import com.badlogic.gdx.graphics.Color;
import com.csse3200.game.cutscene.runtime.components.CutsceneHudComponent;
import com.csse3200.game.cutscene.runtime.states.BackgroundState;
import com.csse3200.game.cutscene.runtime.states.DialogueState;

public class OrchestratorState {
    private DialogueState dialogueState;
    private BackgroundState backgroundState;


    public OrchestratorState() {
        this.dialogueState = new DialogueState();
        this.dialogueState.setVisible(false);
        this.backgroundState = new BackgroundState();
        this.backgroundState.setImage(CutsceneHudComponent.loadImage(Color.BLACK));
    }

    public DialogueState getDialogueState() {
        return dialogueState;
    }

    public BackgroundState getBackgroundState() {
        return backgroundState;
    }
}
