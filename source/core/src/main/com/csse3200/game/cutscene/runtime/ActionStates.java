package com.csse3200.game.cutscene.runtime;

import com.csse3200.game.cutscene.models.object.Advance;
import com.csse3200.game.cutscene.runtime.states.AdvanceAutoDelayState;
import com.csse3200.game.cutscene.runtime.states.AdvanceAutoState;
import com.csse3200.game.cutscene.runtime.states.AdvanceInputState;
import com.csse3200.game.cutscene.runtime.states.AdvanceSignalState;

/** Utility class for creating action states. */
public class ActionStates {
  /** Private constructor to prevent instantiation. */
  private ActionStates() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Creates an action state for an advance.
   *
   * @param advance the advance
   * @return the action state
   */
  static ActionState advance(Advance advance) {
    return switch (advance.getMode()) {
      case INPUT -> new AdvanceInputState();
      case AUTO -> new AdvanceAutoState();
      case AUTO_DELAY -> new AdvanceAutoDelayState(advance.getDelayMs());
      case SIGNAL -> new AdvanceSignalState(advance.getSignalKey());
    };
  }
}
