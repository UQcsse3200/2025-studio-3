package com.csse3200.game.cutscene.runtime.states;

import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.SupportsAdvance;

/**
 * Advance on key input
 */
public class AdvanceInputState implements ActionState, SupportsAdvance {
    private boolean open;

    /**
     * Runs on every game tick to progress logic
     *
     * @param dtMs The delta time in milliseconds
     */
    @Override
    public void tick(int dtMs) {

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

    /**
     * Advances the {@link ActionState} from an external code source
     */
    @Override
    public void advance() {
        open = true;
    }
}
