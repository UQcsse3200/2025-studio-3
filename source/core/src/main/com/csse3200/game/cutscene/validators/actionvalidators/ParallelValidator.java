package com.csse3200.game.cutscene.validators.actionvalidators;

import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.cutscene.validators.ActionValidator;
import com.csse3200.game.cutscene.validators.ActionValidatorRegistry;
import com.csse3200.game.cutscene.validators.ValidationCtx;
import com.csse3200.game.cutscene.validators.ValidatorUtils;
import com.csse3200.game.exceptions.AuthoringError;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the {@link ActionValidator} interface for concrete implementation of the {@code
 * validate} method.
 *
 * <p>Validates parallel events against the following conditions:
 *
 * <ul>
 *   <li>{@code action.actions} must be a non-null list of {@link ActionDTO}
 *   <li>Every child action must pass its own validator via {@link
 *       ActionValidatorRegistry#validate(ActionDTO, String, ValidationCtx)}
 *   <li>{@code await} is valid using {@link ValidatorUtils#validateAwait(String, ActionDTO)}
 * </ul>
 */
public class ParallelValidator implements ActionValidator {
  @Override
  public List<AuthoringError> validate(String beatId, ActionDTO action, ValidationCtx context) {
    List<AuthoringError> errors = new ArrayList<>();

    ActionValidatorRegistry actionValidatorRegistry = new ActionValidatorRegistry();

    for (ActionDTO childAction : action.getActions()) {
      errors.addAll(actionValidatorRegistry.validate(childAction, beatId, context));
    }

    errors.addAll(ValidatorUtils.validateAwait(beatId, action));

    return errors;
  }
}
