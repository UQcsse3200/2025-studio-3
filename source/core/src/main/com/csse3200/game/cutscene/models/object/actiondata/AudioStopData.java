package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.AudioBus;

/**
 * Stores the data for the audio.stop event
 * @param bus     The audio bus of the sound
 * @param fadeMs  The time it takes for the music to fade out (0 for instant)
 * @param await   Wait for fade completion before continuing
 */
public record AudioStopData(
        AudioBus bus,
        int fadeMs,
        boolean await
) implements ActionData { }
