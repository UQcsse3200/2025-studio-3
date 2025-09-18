package com.csse3200.game.cutscene.runtime.components;

import com.badlogic.gdx.Input;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.input.InputComponent;

public class CutsceneInputComponent extends InputComponent {
  private final CutsceneOrchestrator orchestrator;

  public CutsceneInputComponent(CutsceneOrchestrator orchestrator) {
    super(1000);
    this.orchestrator = orchestrator;
  }

  /**
   * @param keyCode
   * @return
   */
  @Override
  public boolean keyDown(int keyCode) {
    if (keyCode == Input.Keys.SPACE) {
      orchestrator.advance();
      return true;
    }
    return false;
  }
}
