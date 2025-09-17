package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.Character;
import com.csse3200.game.cutscene.models.object.Transition;

/**
 * Stores data for the character.exit event
 *
 * @param character The character object containing character info (id, name, and poses)
 * @param transition The transition that happens to the character sprite
 * @param duration How long the transition will take
 * @param await Wait for the transition to complete before continuing
 */
public record CharacterExitData(
    Character character, Transition transition, int duration, boolean await)
    implements ActionData {}
