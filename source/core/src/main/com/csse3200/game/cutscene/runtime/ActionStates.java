package com.csse3200.game.cutscene.runtime;

import com.csse3200.game.cutscene.models.object.Advance;
import com.csse3200.game.cutscene.runtime.states.AdvanceAutoDelayState;
import com.csse3200.game.cutscene.runtime.states.AdvanceAutoState;
import com.csse3200.game.cutscene.runtime.states.AdvanceInputState;
import com.csse3200.game.cutscene.runtime.states.AdvanceSignalState;

public class ActionStates {
    static ActionState advance(Advance advance) {
        return switch (advance.getMode()) {
            case INPUT -> new AdvanceInputState();
            case AUTO -> new AdvanceAutoState();
            case AUTO_DELAY -> new AdvanceAutoDelayState(advance.getDelayMs());
            case SIGNAL -> new AdvanceSignalState(advance.getSignalKey());
        };
    }
}
