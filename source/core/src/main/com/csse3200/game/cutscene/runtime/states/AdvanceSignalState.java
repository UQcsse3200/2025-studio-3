package com.csse3200.game.cutscene.runtime.states;

import com.csse3200.game.cutscene.runtime.ActionState;

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
  public void tick(int dtMs) {}

  /**
   * Triggered on skip, will fast track any logic to its final state
   */
  @Override
  public void skip() {

  }

  /**
   * @return True if the action is blocking till completion (false if async)
   */
  @Override
  public boolean blocking() {
    return !open;
  }

  /**
   * @return True if the action is completed (can be disposed of)
   */
  @Override
  public boolean done() {
    return open;
  }

  public void signal(String signalKey) {
    if (signalKey != null && signalKey.equals(key)) {
      open = true;
    }
  }
}
