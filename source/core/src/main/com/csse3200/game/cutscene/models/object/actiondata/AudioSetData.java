package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.AudioBus;

/**
 * Stores data for the audio.set event
 *
 * @param bus The audio bus of the sound
 * @param volume The render volume of the sound [0.0 .. 1.0]
 */
public record AudioSetData(AudioBus bus, Float volume) implements ActionData {}
