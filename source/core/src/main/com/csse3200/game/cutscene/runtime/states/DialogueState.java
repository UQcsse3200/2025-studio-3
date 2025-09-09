package com.csse3200.game.cutscene.runtime.states;

/**
 * State responsible for all rendering of the dialogue box
 */
public class DialogueState {
    boolean visible;
    String speaker = "";
    String text = "";
    int textProgress;
    boolean canAdvance;

    /**
     * Set the speaker and text parameters
     * @param speaker The text that will show up above the dialogue
     * @param text    The text that will be "spoken"
     */
    public void set(String speaker, String text) {
        this.speaker = speaker;
        this.text = text;
    }

    /**
     * Increment the text progress by 1 character
     */
    public void incTextProgress() {
        textProgress = Math.max(++textProgress, text.length());
    }

    /**
     * Get the character at the current progress
     * @return The last visible character
     */
    public char getCurrentCharacter() {
        return text.charAt(textProgress);
    }
}
