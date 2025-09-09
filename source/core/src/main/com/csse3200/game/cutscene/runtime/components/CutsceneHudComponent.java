package com.csse3200.game.cutscene.runtime.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.cutscene.runtime.OrchestratorState;
import com.csse3200.game.ui.UIComponent;

public class CutsceneHudComponent extends UIComponent {
    private final CutsceneOrchestrator orchestrator;

    @Override
    public void create() {
        super.create();

        // create stage and everything for rendering
    }

    /**
     * Draw the renderable. Should be called only by the renderer, not manually.
     *
     * @param batch Batch to render to.
     */
    @Override
    protected void draw(SpriteBatch batch) {
        OrchestratorState state = orchestrator.state();

        // draw changes with state
    }
}
