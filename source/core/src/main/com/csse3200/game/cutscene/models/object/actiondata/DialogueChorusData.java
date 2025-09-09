package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.Character;

import java.util.List;

/**
 * Stores the data for the dialogue.chorus event
 * @param characters  A list of characters that "say" the text
 * @param text        The text that is being "said"
 * @param await       Wait for the text to finish displaying before continuing
 */
public record DialogueChorusData(
        List<Character> characters,
        String text,
        boolean await
) implements ActionData { }
