package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.Character;
import com.csse3200.game.cutscene.models.object.Position;
import com.csse3200.game.cutscene.models.object.Transition;

/**
 * Stores data for the character.enter event
 * @param character   The character object containing character info (id, name, and poses)
 * @param pose        The pose the character will be rendered in
 * @param position    The position on screen the character will be rendered in
 * @param transition  The transition that happens to the character sprite
 * @param duration    How long the transition will take
 * @param await       Wait for the transition to complete before continuing
 */
public record CharacterEnterData(
        Character character,
        String pose,
        Position position,
        Transition transition,
        int duration,
        boolean await
) implements ActionData { }
