package com.csse3200.game.cutscene.runtime.states;

import com.csse3200.game.cutscene.runtime.ActionState;

/** Advance automatically */
public class AdvanceAutoState implements ActionState {
  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  @Override
  public void tick(int dtMs) {
    // Not implemented
  }

  /** Triggered on skip, will fast track any logic to its final state */
  @Override
  public void skip() {
    // Not implemented
  }

  /**
   * Checks if the action is blocking till completion (false if async)
   *
   * @return True if the action is blocking till completion (false if async)
   */
  @Override
  public boolean blocking() {
    return false;
  }

  /**
   * Checks if the action is completed (can be disposed of)
   *
   * @return True if the action is completed (can be disposed of)
   */
  @Override
  public boolean done() {
    return true;
  }
}
