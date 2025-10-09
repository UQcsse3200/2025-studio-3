package com.csse3200.game.cutscene.runtime;

/** Interface for action states. */
public interface ActionState {
  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  void tick(int dtMs);

  /**
   * Triggered on skip, will fast track any logic to its final state
   */
  void skip();

  /**
   * Checks if the action is blocking till completion (false if async)
   *
   * @return True if the action is blocking till completion (false if async)
   */
  boolean blocking();

  /**
   * Checks if the action is completed (can be disposed of)
   *
   * @return True if the action is completed (can be disposed of)
   */
  boolean done();
}
