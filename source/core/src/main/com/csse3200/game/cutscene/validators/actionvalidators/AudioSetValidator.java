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
 * Validatess audio set events against the following conditions:
 * <ul>
 *     <li>{@code bus} is a valid string</li>
 *     <li>{@code bus} must be equal to "music"</li>
 *     <li>{@code volume} must be a double or float</li>
 *     <li>{@code volume} &isin; [0.0, 1.0]</li>
 * </ul>
 */
public class AudioSetValidator implements ActionValidator {
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

        errors.addAll(ValidatorUtils.validateString(busObject, "AUDIO_SET_BUS", path));

        if (busObject instanceof String) {
            String bus = (String) busObject;
            if (bus.equals("music")) {
                errors.addAll(ValidatorUtils.validateDoubleWithRange(action.fields.get("volume"),
                        "AUDIO_PLAY_VOLUME", path, 0.0, 1.0));
            } else {
                errors.add(new AuthoringError("ACTION_AUDIO_SET_BUS_INVALID", path,
                        "Bus for audio set must be only \"music\""));
            }
        }

        return errors;
    }
}
