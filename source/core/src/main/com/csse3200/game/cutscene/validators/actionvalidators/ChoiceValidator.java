package com.csse3200.game.cutscene.validators.actionvalidators;

import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.cutscene.validators.ActionValidator;
import com.csse3200.game.cutscene.validators.ValidationCtx;
import com.csse3200.game.cutscene.validators.ValidatorUtils;
import com.csse3200.game.exceptions.AuthoringError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implements the {@link ActionValidator} interface for concrete implementation of the {@code validate} method.
 *
 * Validates choice events against the following conditions:
 * <ul>
 *     <li>{@code prompt} must be a string</li>
 *     <li>{@code choices} must be a list</li>
 *     <li>Each element of {@code choices} must be a map</li>
 *     <li>Each entry in each choice map must have a {@link String} key and {@link String} value</li>
 *     <li>No key or value can be empty or null</li>
 *     <li>The choice field {@code entryBeatId} must be a valid beat id (only checked for {@code "current"} {@code cutsceneId})</li>
 *     <li>Each choice map must contain the following keys:
 *         <ul>
 *             <li>{@code type}</li>
 *             <li>{@code line}</li>
 *             <li>{@code cutsceneId}</li>
 *             <li>{@code entryBeatId}</li>
 *         </ul>
 *     </li>
 * </ul>
 */
public class ChoiceValidator implements ActionValidator {
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

        List<AuthoringError> promptErrors = ValidatorUtils.validateString(
                action.fields.get("prompt"), "prompt", path);
        errors.addAll(promptErrors);

        Object choicesObject = action.fields.get("choices");
        if (choicesObject instanceof List<?>) {
            for (Object choice : (List<?>)choicesObject) {
                if (choice instanceof Map<?, ?>) {
                    errors.addAll(validateChoice((Map<?, ?>)choice, path, context));
                } else {
                    errors.add(new AuthoringError("ACTION_CHOICE_MALFORMED", path, "Choice is malformed"));
                }
            }
        } else {
            errors.add(new AuthoringError("ACTION_CHOICES_NOT_LIST", path, "Choices must be a list"));
        }

        return errors;
    }

    /**
     * Validates a choice by checking:
     *
     * <ul>
     *     <li>Each entry in each choice map must have a {@link String} key and {@link String} value</li>
     *     <li>No key or value can be empty or null</li>
     *     <li>The choice field {@code entryBeatId} must be a valid beat id (only checked for {@code "current"} {@code cutsceneId})</li>
     *     <li>Each choice map must contain the following keys:
     *         <ul>
     *             <li>{@code type}</li>
     *             <li>{@code line}</li>
     *             <li>{@code cutsceneId}</li>
     *             <li>{@code entryBeatId}</li>
     *         </ul>
     *     </li>
     * </ul>
     * @param choice  The {@link Map} to be verified
     * @param path    A string to the error path
     * @param context The {@link ValidationCtx} containing cutscene info
     * @return A list of {@link AuthoringError} for each rule infraction
     */
    private List<AuthoringError> validateChoice(Map<?, ?> choice, String path, ValidationCtx context) {
        List<AuthoringError> choiceErrors = new ArrayList<>();

        List<String> keys = new ArrayList<>();

        for (Map.Entry<?, ?> entry : choice.entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) {
                choiceErrors.add(new AuthoringError("ACTION_CHOICE_MALFORMED_LINE", path,
                        "A line in a choice is malformed (not String: String)"));
            } else {
                String key = (String)entry.getKey();
                String value = (String)entry.getValue();

                if (key.isEmpty() || value.isEmpty()) {
                    choiceErrors.add(new AuthoringError("ACTION_CHOICE_EMPTY_STRING", path,
                            "A key or value in the choice is empty"));
                } else if (key.equals("entryBeatId") && !context.beatIds().contains(value)) {
                    choiceErrors.add(new AuthoringError("ACTION_CHOICE_INVALID_BEAT_ID", path,
                            "The beat ID " + value + " does not exist"));
                }

                keys.add(key);
            }
        }

        if (!keys.containsAll(List.of("type", "line", "cutsceneId", "entryBeatId"))) {
            choiceErrors.add(new AuthoringError("ACTION_CHOICE_MISSING_PARAMETERS", path,
                    "A KV pair (parameter) is missing from a choice"));
        }

        return choiceErrors;
    }
}
