package com.csse3200.game.cutscene.models.object.actiondata;

/**
 * Stores the data for the dialogue.hide event
 * @param await Wait for the dialogue menu to disappear before continuing
 */
public record DialogueHideData(
        boolean await
) implements ActionData { }
