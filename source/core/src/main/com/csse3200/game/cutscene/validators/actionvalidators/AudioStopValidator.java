package com.csse3200.game.cutscene.validators.actionvalidators;

import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.cutscene.validators.ActionValidator;
import com.csse3200.game.cutscene.validators.ValidationCtx;
import com.csse3200.game.cutscene.validators.ValidatorUtils;
import com.csse3200.game.exceptions.AuthoringError;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the {@link ActionValidator} interface for concrete implementation of the {@code
 * validate} method.
 *
 * <p>Validates audio stop event against the following conditions:
 *
 * <ul>
 *   <li>{@code bus} must be a string
 *   <li>{@code bus} must equal "music"
 *   <li>{@code fadeMs} must be an integer
 *   <li>{@code fadeMs} must be {@code >= 0}
 *   <li>{@code await} is valid using {@link ValidatorUtils#validateAwait(String, ActionDTO)}
 * </ul>
 */
public class AudioStopValidator implements ActionValidator {
  /**
   * {@inheritDoc}
   *
   * @param beatId The id of the beat having one of its actions validated
   * @param action The {@link ActionDTO} being validated
   * @param context The {@link ValidationCtx} with the document context
   * @return A list of {@link AuthoringError} for each rule infraction
   */
  @Override
  public List<AuthoringError> validate(String beatId, ActionDTO action, ValidationCtx context) {
    List<AuthoringError> errors = new ArrayList<>();

    // bus validation
    Object busObject = action.fields.get("bus");

    String path = "doc.cutscene.beats." + beatId + ".actions.*";

    errors.addAll(ValidatorUtils.validateString(busObject, "AUDIO_STOP_BUS", path));

    if (busObject instanceof String) {
      String bus = (String) busObject;
      if (!bus.equals("music")) {
        errors.add(
            new AuthoringError(
                "ACTION_AUDIO_STOP_BUS_INVALID", path, "Bus for audio set must be only \"music\""));
      }
    }

    errors.addAll(ValidatorUtils.validateInt(action.fields.get("fadeMs"), "AUDIO_STOP_FADE", path));

    if (action.fields.get("fadeMs") instanceof Long) {
      if ((Long) action.fields.get("fadeMs") < 0) {
        errors.add(
            new AuthoringError(
                "ACTION_AUDIO_STOP_FADE_INVALID", path, "FadeMs must be greater or equal to 0"));
      }
    }

    errors.addAll(ValidatorUtils.validateAwait(beatId, action));

    return errors;
  }
}
