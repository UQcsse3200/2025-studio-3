package com.csse3200.game.cutscene.validators.actionvalidators;

import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.cutscene.validators.ActionValidator;
import com.csse3200.game.cutscene.validators.ValidationCtx;
import com.csse3200.game.cutscene.validators.ValidatorUtils;
import com.csse3200.game.exceptions.AuthoringError;

import java.util.ArrayList;
import java.util.List;

import static com.csse3200.game.cutscene.validators.ValidatorUtils.validateSoundId;

/**
 * Implements the {@link ActionValidator} interface for concrete implementation of the {@code validate} method.
 *
 * Validates audio play events against the following conditions:
 * <ul>
 *     <li>{@code bus} is a valid string</li>
 *     <li>{@code bus} is either "sfx" or "music"</li>
 *     <li>If {@code bus} is "sfx" it must have the following flags with conditions:
 *         <ul>
 *             <li>{@code volume}: [0.0, 1.0]</li>
 *             <li>{@code pitch}: [0.0, +inf]</li>
 *             <li>{@code pan}: [-1.0, 1.0]</li>
 *         </ul>
 *     </li>
 *     <li>If {@code bus} is "music" it must have the following flags with conditions:
 *         <ul>
 *             <li>{@code loop}: (boolean)</li>
 *             <li>{@code volume}: [0.0, 1.0]</li>
 *         </ul>
 *     </li>
 *     <li>{@code soundId} is a valid string and references a valid sound</li>
 *     <li>{@code await} is valid using {@link ValidatorUtils#validateAwait(String, ActionDTO)}</li>
 * </ul>
 */
public class AudioPlayValidator implements ActionValidator {
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

        // bus validation
        Object busObject = action.fields.get("bus");

        String path = "doc.cutscene.beats." + beatId + ".actions.*";

        errors.addAll(ValidatorUtils.validateString(busObject, "AUDIO_PLAY_BUS", path));

        if (busObject instanceof String) {
            String bus = (String) busObject;

            switch (bus) {
                case "sfx": {
                    errors.addAll(ValidatorUtils.validateDoubleWithRange(action.fields.get("volume"),
                            "AUDIO_PLAY_VOLUME", path, 0.0, 1.0));
                    errors.addAll(ValidatorUtils.validateDoubleWithRange(action.fields.get("pitch"),
                            "AUDIO_PLAY_PITCH", path, 0.0, Double.MAX_VALUE));
                    errors.addAll(ValidatorUtils.validateDoubleWithRange(action.fields.get("pan"),
                            "AUDIO_PLAY_PAN", path, -1.0, 1.0));
                    break;
                }
                case "music": {
                    errors.addAll(ValidatorUtils.validateBool(action.fields.get("loop"), "AUDIO_PLAY_LOOP", path));
                    errors.addAll(ValidatorUtils.validateDoubleWithRange(action.fields.get("volume"),
                            "AUDIO_PLAY_VOLUME", path, 0.0, 1.0));
                    break;
                }
                default: {
                    errors.add(new AuthoringError("ACTION_AUDIO_PLAY_INVALID_BUS", path,
                            "Bus value must be either \"sfx\" or \"music\""));
                    break;
                }
            }
        } else {
            errors.add(new AuthoringError("ACTION_AUDIO_PLAY_BUS_NOT_STRING", path,
                    "Bus value must be a string"));
        }


        // validate soundId
        errors.addAll(validateSoundId(action, context, path));

        errors.addAll(ValidatorUtils.validateAwait(beatId, action));

        return errors;
    }
}
