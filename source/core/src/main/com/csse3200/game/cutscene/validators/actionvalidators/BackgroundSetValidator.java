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
 * <p>Validates background set events against the following conditions:
 *
 * <ul>
 *   <li>{@code backgroundId} must be a string
 *   <li>{@code backgroundId} must correspond to a valid background
 *   <li>{@code transition} must be valid against {@link ValidatorUtils#validateTransition(String,
 *       ActionDTO)}
 *   <li>{@code await} is valid using {@link ValidatorUtils#validateAwait(String, ActionDTO)}
 * </ul>
 */
public class BackgroundSetValidator implements ActionValidator {
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

    if (!(action.getFields().get("backgroundId") instanceof String))
      errors.add(
          new AuthoringError(
              "ACTION_BACKGROUND_SET_INVALID",
              "doc.cutscene.beats." + beatId + ".actions.*",
              "Background ID must be a string"));
    else {
      String backgroundId = (String) action.getFields().get("backgroundId");
      if (!context.backgroundIds().contains(backgroundId))
        errors.add(
            new AuthoringError(
                "ACTION_BACKGROUND_SET_INVALID",
                "doc.cutscene.beats." + beatId + ".actions.*",
                "Background ID " + backgroundId + " does not exist"));
    }

    errors.addAll(ValidatorUtils.validateTransition(beatId, action));

    errors.addAll(ValidatorUtils.validateAwait(beatId, action));

    return errors;
  }
}
