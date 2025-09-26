package com.csse3200.game.cutscene.runtime.states;

import com.csse3200.game.cutscene.runtime.ActionState;

/** Advance auto delay state. */
public class AdvanceAutoDelayState implements ActionState {
  private final int duration;
  private int elapsed;

  /**
   * Create an advance auto delay state with a given duration in Milliseconds
   *
   * @param durationMs The delay duration in milliseconds
   */
  public AdvanceAutoDelayState(int durationMs) {
    this.duration = durationMs;
  }

  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  @Override
  public void tick(int dtMs) {
    elapsed += dtMs;
  }

  /**
   * Triggered on skip, will fast track any logic to its final state
   */
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
    return elapsed < duration;
  }

  /**
   * Checks if the action is completed (can be disposed of)
   *
   * @return True if the action is completed (can be disposed of)
   */
  @Override
  public boolean done() {
    return elapsed >= duration;
  }
}
