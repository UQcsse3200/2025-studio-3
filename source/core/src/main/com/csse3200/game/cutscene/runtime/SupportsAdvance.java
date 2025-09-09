package com.csse3200.game.cutscene.runtime;

/**
 * Advanced from an external source (e.g. key input)
 */
public interface SupportsAdvance {
    /**
     * Advances the {@link ActionState} from an external code source
     */
    void advance();
}
