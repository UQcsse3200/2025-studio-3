package com.csse3200.game.cutscene.runtime.action;

import com.csse3200.game.cutscene.models.object.actiondata.GotoData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.exceptions.InvalidGotoBeatId;

public class GotoAction implements ActionState {
  private CutsceneOrchestrator orchestrator;
  private GotoData gotoData;
  private boolean done = false;

  public GotoAction(
      CutsceneOrchestrator orchestrator, GotoData gotoData) {
    this.orchestrator = orchestrator;
    this.gotoData = gotoData;
  }

  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   * @throws InvalidGotoBeatId This should never throw because of the validation checker, but it's
   *     there just in case
   */
  @Override
  public void tick(int dtMs) {
    orchestrator.gotoBeat(gotoData.beatId());
    done = true;
  }

  /** Triggered on skip, will fast track any logic to its final state */
  @Override
  public void skip() {
    tick(1);
  }

  /**
   * @return True if the action is blocking till completion (false if async)
   */
  @Override
  public boolean blocking() {
    return true;
  }

  /**
   * @return True if the action is completed (can be disposed of)
   */
  @Override
  public boolean done() {
    return done;
  }
}
