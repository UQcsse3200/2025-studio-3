package com.csse3200.game.cutscene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.csse3200.game.cutscene.models.dto.CutsceneDocDTO;
import com.csse3200.game.cutscene.models.object.Cutscene;
import com.csse3200.game.exceptions.AuthoringError;
import com.csse3200.game.exceptions.ValidationError;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CutscenePipeline {
  private static final Logger logger = LoggerFactory.getLogger(CutscenePipeline.class);

  private CutsceneLoader cutsceneLoader;
  private CutsceneValidator cutsceneValidator;
  private CutsceneCompiler cutsceneCompiler;

  public CutscenePipeline(
      CutsceneLoader cutsceneLoader,
      CutsceneValidator cutsceneValidator,
      CutsceneCompiler cutsceneCompiler) {
    this.cutsceneLoader = cutsceneLoader;
    this.cutsceneValidator = cutsceneValidator;
    this.cutsceneCompiler = cutsceneCompiler;
  }

  public Cutscene fromFile(String cutsceneName) throws ValidationError {
    FileHandle file = Gdx.files.internal("cutscenes/" + cutsceneName + ".json");

    CutsceneDocDTO cutsceneDocDTO = cutsceneLoader.load(file);
    List<AuthoringError> validationErrors = cutsceneValidator.validate(cutsceneDocDTO);

    if (!validationErrors.isEmpty()) {
      for (AuthoringError error : validationErrors) {
        String errorMessage = error.toString();
        logger.error(errorMessage);
      }
      throw new ValidationError("Failed with " + validationErrors.size() + " errors");
    } else {
      return cutsceneCompiler.compile(cutsceneDocDTO);
    }
  }
}
