package com.csse3200.game.cutscene.models.object.actiondata;

import com.csse3200.game.cutscene.models.object.Choice;

import java.util.List;

/**
 * Stores the data for the choice event
 * @param prompt   The text prompt for the question
 * @param choices  A list of choices
 */
public record ChoiceData(
        String prompt,
        List<Choice> choices
) implements ActionData { }
