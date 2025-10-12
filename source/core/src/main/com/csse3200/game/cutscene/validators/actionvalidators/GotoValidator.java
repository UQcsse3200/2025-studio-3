package com.csse3200.game.cutscene.validators.actionvalidators;

import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.cutscene.models.object.Beat;
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
 * <p>Validates goto events against the following conditions:
 *
 * <ul>
 *   <li>{@code cutsceneId} must be a string
 *   <li>{@code beatId} must be a string
 *   <li>If {@code cutsceneId == "current"} then {@code beatId} must reference a valid {@link Beat}
 * </ul>
 *
 * Cross cutscene beat validation is not performed.
 */
public class GotoValidator implements ActionValidator {
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

    String path = "doc.cutscene.beats." + beatId + ".action.*";

    errors.addAll(
        ValidatorUtils.validateString(action.getFields().get("cutsceneId"), "GOTO_CUTSCENE_ID", path));

    List<AuthoringError> beatIdErrors =
        ValidatorUtils.validateString(action.getFields().get("beatId"), "GOTO_CUTSCENE_BEAT_ID", path);

    if (errors.isEmpty() && action.getFields().get("cutsceneId").equals("current")) {
      if (beatIdErrors.isEmpty()) {
        String gotoBeatId = (String) action.getFields().get("beatId");
        if (!context.beatIds().contains(gotoBeatId)) {
          beatIdErrors.add(
              new AuthoringError(
                  "ACTION_GOTO_BEAT_ID_INVALID",
                  path,
                  "Beat ID " + gotoBeatId + " is not a valid beat ID"));
        }
      }

      errors.addAll(beatIdErrors);
    }

    return errors;
  }
}
