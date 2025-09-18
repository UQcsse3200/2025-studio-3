package com.csse3200.game.cutscene.runtime;

/** Advance on choice */
public interface SupportsChoice {
  /**
   * Advance on id selection
   *
   * @param id The id of the choice selected
   */
  void select(String id);
}
