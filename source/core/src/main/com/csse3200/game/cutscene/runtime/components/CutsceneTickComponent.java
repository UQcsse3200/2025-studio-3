package com.csse3200.game.cutscene.runtime.components;

import com.csse3200.game.components.Component;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.services.ServiceLocator;

public class CutsceneTickComponent extends Component {
  private final CutsceneOrchestrator orchestrator;

  /**
   * Create a tick component with a cutscene orchestrator
   *
   * @param orchestrator The orchestrator that is being ticked
   */
  public CutsceneTickComponent(CutsceneOrchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Runs the orchestrator update function, disposes of entity if orchestrator is no longer
   * running
   */
  @Override
  public void update() {
    float df = ServiceLocator.getTimeSource().getDeltaTime();
    orchestrator.update(df);
  }
}
