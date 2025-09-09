package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.Beat;

/**
 * Stores the data for the goto event
 * @param cutsceneId  The id of the cutscene to go to (current for current cutscene)
 * @param beatId      The id of the beat to jump to
 */
public record GotoData(
        String cutsceneId,
        String beatId
) implements ActionData { }
