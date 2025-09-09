package com.csse3200.game.cutscene.runtime;

import com.csse3200.game.cutscene.runtime.states.DialogueState;

public class OrchestratorState {
    private DialogueState dialogueState;

    public OrchestratorState() {
        this.dialogueState = new DialogueState();
    }

    public DialogueState getDialogueState() {
        return dialogueState;
    }
}
