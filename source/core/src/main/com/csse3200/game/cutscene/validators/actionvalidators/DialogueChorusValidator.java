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
 * Validates dialogue chorus events against the following conditions:
 * <ul>
 *     <li>{@code characterIds} must be a list</li>
 *     <li>Each {@code characterIds} element that is a valid string must also correspond to a valid character (the string is a {@code characterId})</li>
 *     <li>{@code text} must be a valid string</li>
 *     <li>{@code await} is valid using {@link ValidatorUtils#validateAwait(String, ActionDTO)}</li>
 * </ul>
 */
public class DialogueChorusValidator implements ActionValidator {
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

        Object characterIdsObject = action.fields.get("characterIds");
        if (characterIdsObject instanceof List<?>) {
            for (Object item : (List<?>)characterIdsObject) {
                if (item instanceof String) {
                    if (!context.characterIds().contains((String)item)) {
                        errors.add(new AuthoringError("DIALOGUE_CHORUS_CHARACTERID_NONEXISTANT", path,
                                "The character ID " + (String)item + " does not exist."));
                    }
                }
            }
        } else {
            errors.add(new AuthoringError("DIALOGUE_CHORUS_CHARACTERIDS_INVALID", path,
                    "Character IDs are malformed or nonexistent"));
        }

        List<AuthoringError> textErrors = ValidatorUtils.validateString(action.fields.get("text"), "text", path);
        errors.addAll(textErrors);

        errors.addAll(ValidatorUtils.validateAwait(beatId, action));

        return errors;
    }
}
