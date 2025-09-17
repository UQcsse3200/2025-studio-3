package com.csse3200.game.cutscene.validators;

import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.exceptions.AuthoringError;
import java.util.List;

/** Interface to define action validators. */
public interface ActionValidator {
  /**
   * Validate an action against a set of rules defined for each type of action.
   *
   * @param beatId The id of the beat having one of its actions validated
   * @param action The {@link ActionDTO} being validated
   * @param context The {@link ValidationCtx} with the document context
   * @return A list of {@link AuthoringError} for each rule infraction
   */
  List<AuthoringError> validate(String beatId, ActionDTO action, ValidationCtx context);
}
