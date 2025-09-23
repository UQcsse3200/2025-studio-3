package com.csse3200.game.cutscene.models.object;

/** For JSON values {@code "input"|"auto"|"auto_delay"|"signal"}. */
public enum AdvanceMode {
  /** Moves to next beat on keyboard or mouse input */
  INPUT,

  /** Moves to next beat after all awaits have finished */
  AUTO,

  /** Moves to next beat after a set ms delay after all awaits have finished */
  AUTO_DELAY,

  /** Moves to next beat after an in game event is triggered */
  SIGNAL
}
