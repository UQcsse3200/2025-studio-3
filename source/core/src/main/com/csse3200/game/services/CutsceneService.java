package com.csse3200.game.services;

import com.csse3200.game.cutscene.*;
import com.csse3200.game.cutscene.models.object.Cutscene;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.cutscene.runtime.DefaultOrchestrator;
import com.csse3200.game.cutscene.runtime.components.CutsceneHudComponent;
import com.csse3200.game.cutscene.runtime.components.CutsceneInputComponent;
import com.csse3200.game.cutscene.runtime.components.CutsceneTickComponent;
import com.csse3200.game.entities.Entity;
import java.util.function.Consumer;

/** Service for managing cutscenes throughout the game. */
public class CutsceneService {
  private final CutsceneLoader cutsceneLoader = new CutsceneLoader();
  private final CutsceneValidator cutsceneValidator = new CutsceneValidator();
  private final CutsceneCompiler cutsceneCompiler = new CutsceneCompiler();
  private final CutscenePipeline cutscenePipeline;
  private final CutsceneOrchestrator orchestrator;
  private Entity cutsceneEntity;

  /** Constructor for the CutsceneService class. */
  public CutsceneService() {
    this.cutscenePipeline =
        new CutscenePipeline(cutsceneLoader, cutsceneValidator, cutsceneCompiler);
    orchestrator = new DefaultOrchestrator();
  }

  /**
   * runs a cutscene from its name
   *
   * @param name The name of the cutscene
   * @param callback The callback to be called when the cutscene is complete
   */
  public void playCutscene(String name, Consumer<String> callback) {
    Cutscene cutscene = this.cutscenePipeline.fromFile(name);

    orchestrator.load(cutscene);

    end();
    callback.accept(name);

    cutsceneEntity =
        new Entity()
            .addComponent(new CutsceneTickComponent(orchestrator))
            .addComponent(new CutsceneHudComponent(orchestrator))
            .addComponent(new CutsceneInputComponent(orchestrator));
    ServiceLocator.getEntityService().register(cutsceneEntity);
  }

  /** Clear the cutscene entity if it exists */
  public void end() {
    if (cutsceneEntity != null) {
      cutsceneEntity.setEnabled(false);
      cutsceneEntity.dispose();
    }
  }
}
