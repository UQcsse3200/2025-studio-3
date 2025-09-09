package com.csse3200.game.cutscene.runtime.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.csse3200.game.components.Component;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.services.ServiceLocator;

public class CutsceneInputComponent extends InputComponent {
    private final CutsceneOrchestrator orchestrator;

    public CutsceneInputComponent(CutsceneOrchestrator orchestrator) {
        super(1000);
        this.orchestrator = orchestrator;
    }

    /**
     * @param keyCode
     * @return
     */
    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Input.Keys.SPACE) {
            orchestrator.advance();
            return true;
        }
        return false;
    }
}
