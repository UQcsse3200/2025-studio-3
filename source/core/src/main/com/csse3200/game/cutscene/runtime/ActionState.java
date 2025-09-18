package com.csse3200.game.cutscene.runtime;

public interface ActionState {
  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  void tick(int dtMs);

  /**
   * @return True if the action is blocking till completion (false if async)
   */
  boolean blocking();

  /**
   * @return True if the action is completed (can be disposed of)
   */
  boolean done();
}
