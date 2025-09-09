package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.Background;
import com.csse3200.game.cutscene.models.object.Transition;

/**
 * Stores data for the background.set event
 * @param background  The background object containing background info (image & id)
 * @param transition  How the background will transition into existence (Transition enum)
 * @param duration    How long the transition will take
 * @param await       Wait for transition to complete before continuing
 */
public record BackgroundSetData(
        Background background,
        Transition transition,
        int duration,
        boolean await
) implements ActionData { }
