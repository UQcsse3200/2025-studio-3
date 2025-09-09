package com.csse3200.game.cutscene.runtime.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.csse3200.game.components.Component;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;

public class CutsceneInputComponent extends Component implements InputProcessor {
    private final CutsceneOrchestrator orchestrator;

    public CutsceneInputComponent(CutsceneOrchestrator orchestrator) {
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

    /**
     * @param i
     * @return
     */
    @Override
    public boolean keyUp(int i) {
        return false;
    }

    /**
     * @param c
     * @return
     */
    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    /**
     * @param i
     * @param i1
     * @param i2
     * @param i3
     * @return
     */
    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    /**
     * @param i
     * @param i1
     * @param i2
     * @param i3
     * @return
     */
    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    /**
     * @param i
     * @param i1
     * @param i2
     * @return
     */
    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    /**
     * @param i
     * @param i1
     * @return
     */
    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    /**
     * @param v
     * @param v1
     * @return
     */
    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
