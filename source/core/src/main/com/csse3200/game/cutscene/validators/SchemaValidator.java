package com.csse3200.game.cutscene.validators;

import com.csse3200.game.cutscene.models.dto.CutsceneDocDTO;
import com.csse3200.game.exceptions.AuthoringError;
import java.util.List;

/**
 * Interface to define Schema validators. Schema validators are built up of {@link ActionValidator}s
 * which combine to validate a given schema. Schemas will change with time and will need new
 * validators, so this interface ensures they can be easily swapped.
 */
public interface SchemaValidator {
  /**
   * Validates a given {@link CutsceneDocDTO}
   *
   * @param cutsceneDocDTO The {@link CutsceneDocDTO} that needs to be validated
   * @return A list of {@link AuthoringError} detail each rule infraction
   */
  List<AuthoringError> validate(CutsceneDocDTO cutsceneDocDTO);
}
