package com.csse3200.game.cutscene;

import com.csse3200.game.cutscene.models.dto.CutsceneDocDTO;
import com.csse3200.game.cutscene.validators.V1SchemaValidator;
import com.csse3200.game.exceptions.AuthoringError;

import java.util.List;

public class CutsceneValidator {
    public List<AuthoringError> validate(CutsceneDocDTO cutscene) {
        switch (cutscene.schemaVersion) {
            case 1:
                return new V1SchemaValidator().validate(cutscene);
            default:
                return List.of(new AuthoringError("INVALID_SCHEMA", "doc",
                        "The schema version " + cutscene.schemaVersion + " does not exist"));
        }
    }
}
