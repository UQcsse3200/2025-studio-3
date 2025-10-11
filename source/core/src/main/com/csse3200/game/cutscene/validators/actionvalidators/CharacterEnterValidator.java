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
 * <p>Validates character enter events against the following conditions:
 *
 * <ul>
 *   <li>{@code characterId} must be a string
 *   <li>{@code characterId} must correspond to a valid character
 *   <li>{@code pose} must be a string
 *   <li>{@code pose} must correspond to a valid pose in the {@code character} from the {@code
 *       characterId}
 *   <li>{@code position} must be a string
 *   <li>{@code position} must be either {@code "left"} or {@code "right"}
 *   <li>{@code transition} must be valid against {@link ValidatorUtils#validateTransition(String,
 *       ActionDTO)}
 *   <li>{@code await} is valid using {@link ValidatorUtils#validateAwait(String, ActionDTO)}
 * </ul>
 */
public class CharacterEnterValidator implements ActionValidator {
  private static final String CHARACTER_ID_FIELD = "characterId";
  private static final String POSITION_FIELD = "position";

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

    List<AuthoringError> characterIdErrors =
        ValidatorUtils.validateString(action.fields.get(CHARACTER_ID_FIELD), CHARACTER_ID_FIELD, path);
    errors.addAll(characterIdErrors);

    List<AuthoringError> poseErrors =
        ValidatorUtils.validateString(action.fields.get("pose"), "pose", path);
    errors.addAll(poseErrors);

    if (characterIdErrors.isEmpty()) {
      String characterId = (String) action.fields.get(CHARACTER_ID_FIELD);
      if (!context.characterIds().contains(characterId)) {
        errors.add(
            new AuthoringError(
                "ACTION_CHARACTER_ENTER_INVALID_CID",
                path,
                "Character ID " + characterId + " does not exist"));
      }

      if (poseErrors.isEmpty()) {
        String pose = (String) action.fields.get("pose");
        if (context.characterPoses().get(characterId) != null && !context.characterPoses().get(characterId).contains(pose)) {
            errors.add(
                new AuthoringError(
                    "ACTION_CHARACTER_POSE_INVALID",
                    path,
                    "Pose " + pose + " does not exist for the character " + characterId));
        }
      }
    }

    List<AuthoringError> positionErrors =
        ValidatorUtils.validateString(action.fields.get(POSITION_FIELD), POSITION_FIELD, path);
    errors.addAll(positionErrors);

    if (positionErrors.isEmpty()) {
      String position = (String) action.fields.get(POSITION_FIELD);

      if (!position.equals("left") && !position.equals("right")) {
        errors.add(
            new AuthoringError(
                "ACTION_CHARACTER_POSITION_INVALID",
                path,
                "Position must be either \"left\" or \"right\""));
      }
    }

    errors.addAll(ValidatorUtils.validateTransition(beatId, action));

    errors.addAll(ValidatorUtils.validateAwait(beatId, action));

    return errors;
  }
}
