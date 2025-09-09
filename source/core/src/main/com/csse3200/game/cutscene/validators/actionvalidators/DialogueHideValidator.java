package com.csse3200.game.cutscene.validators.actionvalidators;

import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.cutscene.validators.ActionValidator;
import com.csse3200.game.cutscene.validators.ValidationCtx;
import com.csse3200.game.cutscene.validators.ValidatorUtils;
import com.csse3200.game.exceptions.AuthoringError;

import java.util.List;

/**
 * Implements the {@link ActionValidator} interface for concrete implementation of the {@code validate} method.
 *
 * Validates dialogue hide events be ensuring they have a valid await boolean.
 */
public class DialogueHideValidator implements ActionValidator {
    /**
     * {@inheritDoc}
     *
     * @param beatId  The id of the beat having one of its actions validated
     * @param action  The {@link ActionDTO} being validated
     * @param context The {@link ValidationCtx} with the document context
     * @return A list of {@link AuthoringError} for each rule infraction
     */
    @Override
    public List<AuthoringError> validate(String beatId, ActionDTO action, ValidationCtx context) {
        return ValidatorUtils.validateAwait(beatId, action);
    }
}
