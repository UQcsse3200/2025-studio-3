package com.csse3200.game.cutscene.validators;

import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.cutscene.validators.actionvalidators.*;
import com.csse3200.game.exceptions.AuthoringError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Contains a registry of {@link ActionValidator}s */
public class ActionValidatorRegistry {
  private final Map<String, ActionValidator> actionValidators;

  /** Initialise the {@code actionValidators} map. */
  public ActionValidatorRegistry() {
    actionValidators = new HashMap<>();

    actionValidators.put("audio.play", new AudioPlayValidator());
    actionValidators.put("audio.set", new AudioSetValidator());
    actionValidators.put("audio.stop", new AudioStopValidator());
    actionValidators.put("background.set", new BackgroundSetValidator());
    actionValidators.put("character.enter", new CharacterEnterValidator());
    actionValidators.put("character.exit", new CharacterExitValidator());
    actionValidators.put("dialogue.show", new DialogueShowValidator());
    actionValidators.put("dialogue.chorus", new DialogueChorusValidator());
    actionValidators.put("dialogue.hide", new DialogueHideValidator());
    actionValidators.put("goto", new GotoValidator());
    actionValidators.put("parallel", new ParallelValidator());
    actionValidators.put("choice", new ChoiceValidator());
  }

  /**
   * Validates a given {@link ActionDTO}.
   *
   * @param action The {@link ActionDTO} containing action data
   * @param beatId The {@code beatId} of the beat the {@code action} belongs to
   * @param context {@link ValidationCtx} object containing relevant information for performing
   *     validation
   * @return A list of {@link AuthoringError}s detailing any validation failures in the provided
   *     {@link ActionDTO}
   */
  public List<AuthoringError> validate(ActionDTO action, String beatId, ValidationCtx context) {
    if (action.getType() == null)
      return List.of(
          new AuthoringError(
              "ACTION_TYPE_NULL",
              "doc.cutscene.beats." + beatId + "actions.*",
              "Action type must be a string and not null"));
    else if (!actionValidators.containsKey(action.getType()))
      return List.of(
          new AuthoringError(
              "ACTION_TYPE_INVALID",
              "doc.cutscene.beats." + beatId + "actions.*",
              "Action " + action.getType() + " is not a valid action"));

    return actionValidators.get(action.getType()).validate(beatId, action, context);
  }
}
