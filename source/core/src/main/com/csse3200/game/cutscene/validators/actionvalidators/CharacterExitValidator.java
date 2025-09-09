package com.csse3200.game.cutscene.validators.actionvalidators;

import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.cutscene.validators.ActionValidator;
import com.csse3200.game.cutscene.validators.ValidationCtx;
import com.csse3200.game.cutscene.validators.ValidatorUtils;
import com.csse3200.game.exceptions.AuthoringError;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the {@link ActionValidator} interface for concrete implementation of the {@code validate} method.
 *
 * Validates character exit events against the following conditions:
 * <ul>
 *     <li>{@code characterId} must be a string</li>
 *     <li>{@code characterId} must correspond to a valid character</li>
 *     <li>{@code transition} must be valid against {@link ValidatorUtils#validateTransition(String, ActionDTO)}</li>
 *     <li>{@code await} is valid using {@link ValidatorUtils#validateAwait(String, ActionDTO)}</li>
 * </ul>
 */
public class CharacterExitValidator implements ActionValidator {
    /**
     * {@inheritDoc}
     *
     * @param beatId   The id of the beat having one of its actions validated
     * @param action   The {@link ActionDTO} being validated
     * @param context  The {@link ValidationCtx} with the document context
     * @return A list of {@link AuthoringError} for each rule infraction
     */
    @Override
    public List<AuthoringError> validate(String beatId, ActionDTO action, ValidationCtx context) {
        List<AuthoringError> errors = new ArrayList<>();

        String path = "doc.cutscene.beats." + beatId + ".action.*";

        List<AuthoringError> characterIdErrors = ValidatorUtils.validateString(action.fields.get("characterId"),
                "characterId", path);
        errors.addAll(characterIdErrors);

        if (characterIdErrors.isEmpty()) {
            String characterId = (String) action.fields.get("characterId");
            if (!context.characterIds().contains(characterId)) {
                errors.add(new AuthoringError("ACTION_CHARACTER_ENTER_INVALID_CID", path,
                        "Character ID " + characterId + " does not exist"));
            }
        }

        errors.addAll(ValidatorUtils.validateTransition(beatId, action));

        errors.addAll(ValidatorUtils.validateAwait(beatId, action));

        return errors;
    }
}
