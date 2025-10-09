package com.csse3200.game.cutscene.runtime.states;

import com.csse3200.game.cutscene.runtime.ActionState;

/** Advance signal state. */
public class AdvanceSignalState implements ActionState {
  private final String key;
  private boolean open;

  /**
   * Creates an advance signal state with a specified key
   *
   * @param key The key to activate the signal
   */
  public AdvanceSignalState(String key) {
    this.key = key;
    this.open = false;
  }

  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  @Override
  public void tick(int dtMs) {
    // Not implemented
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
    return !open;
  }

  /**
   * Checks if the action is completed (can be disposed of)
   *
   * @return True if the action is completed (can be disposed of)
   */
  @Override
  public boolean done() {
    return open;
  }

  /**
   * Signals the action to advance
   *
   * @param signalKey The key to activate the signal
   */
  public void signal(String signalKey) {
    if (signalKey != null && signalKey.equals(key)) {
      open = true;
    }
  }
}
