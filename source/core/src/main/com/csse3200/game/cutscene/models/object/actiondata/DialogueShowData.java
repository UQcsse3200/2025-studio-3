package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.Character;

/**
 * Stores the data for the dialogue.show event
 *
 * @param character The character that "says" the text
 * @param text The text that is being "said"
 * @param await Wait for the text to finish displaying before continuing
 */
public record DialogueShowData(Character character, String text, boolean await)
    implements ActionData {}
