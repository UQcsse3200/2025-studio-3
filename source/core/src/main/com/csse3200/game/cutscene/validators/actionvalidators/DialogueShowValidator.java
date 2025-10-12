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
 * <p>Validates dialogue show events against the following conditions:
 *
 * <ul>
 *   <li>{@code characterId} must be a string
 *   <li>{@code characterId} must correspond to a valid character
 *   <li>{@code text} must be a valid string
 *   <li>{@code await} is valid using {@link ValidatorUtils#validateAwait(String, ActionDTO)}
 * </ul>
 */
public class DialogueShowValidator implements ActionValidator {
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
        ValidatorUtils.validateString(action.getFields().get("characterId"), "characterId", path);
    errors.addAll(characterIdErrors);

    if (characterIdErrors.isEmpty()) {
      String characterId = (String) action.getFields().get("characterId");
      if (!context.characterIds().contains(characterId)) {
        errors.add(
            new AuthoringError(
                "DIALOGUE_CHORUS_CHARACTERID_NONEXISTANT",
                path,
                "The character ID " + characterId + " does not exist."));
      }
    }

    List<AuthoringError> textErrors =
        ValidatorUtils.validateString(action.getFields().get("text"), "text", path);
    errors.addAll(textErrors);

    errors.addAll(ValidatorUtils.validateAwait(beatId, action));

    return errors;
  }
}
