package com.csse3200.game.cutscene.runtime.states;

import com.csse3200.game.cutscene.models.object.actiondata.DialogueShowData;
import com.csse3200.game.cutscene.runtime.ActionState;

public class DialogueShowAction implements ActionState {
    private final DialogueState dialogueState;
    private final String speaker;
    private final String text;
    private final boolean await;
    private int charsShown;
    private int nextCharMsCountdown;
    private boolean done;

    public DialogueShowAction(DialogueState dialogueState, DialogueShowData dialogueShowData) {
        this.dialogueState = dialogueState;
        this.speaker = dialogueShowData.character().getName();
        this.text = dialogueShowData.text();
        this.await = dialogueShowData.await();
        this.charsShown = 0;
        this.nextCharMsCountdown = 0;
        this.done = false;

        this.dialogueState.set(speaker, "");
    }

    /**
     * Runs on every game tick to progress logic
     *
     * @param dtMs The delta time in milliseconds
     */
    @Override
    public void tick(int dtMs) {
        if (nextCharMsCountdown > 0) {
            nextCharMsCountdown -= dtMs;
        } else if (text.length() >= charsShown) {
            dialogueState.set(speaker, text.substring(0, charsShown));
            nextCharMsCountdown = switch(text.charAt(Math.max(charsShown - 1, 0))) {
                case ',' -> 300;
                case '.' -> 1000;
                case '-' -> 500;
                default -> 20;
            };
            charsShown++;
        } else {
            done = true;
        }
    }

    /**
     * @return True if the action is blocking till completion (false if async)
     */
    @Override
    public boolean blocking() {
        return await;
    }

    /**
     * @return True if the action is completed (can be disposed of)
     */
    @Override
    public boolean done() {
        return done;
    }
}
