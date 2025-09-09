package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.AudioBus;
import com.csse3200.game.cutscene.models.object.Sound;

/**
 * Stores data for the audio.play event
 * @param bus     The audio bus of the sound
 * @param sound   The sound object containing sound info (path & id)
 * @param volume  The render volume of the sound [0.0 .. 1.0]
 * @param pitch   The render pitch of the sound [>=0]
 * @param pan     The render pan of the sound (which ear it comes out of) [-1.0 .. 1.0]
 * @param loop    If the sound loops on completion (for background music)
 * @param await   Wait for audio completion before continuing
 */
public record AudioPlayData(
        AudioBus bus,
        Sound sound,
        Float volume,
        Float pitch,
        Float pan,
        boolean loop,
        boolean await
) implements ActionData { }
