package com.csse3200.game.cutscene.runtime;

import com.csse3200.game.cutscene.models.object.Beat;
import com.csse3200.game.cutscene.models.object.Cutscene;
import com.csse3200.game.cutscene.models.object.actiondata.DialogueShowData;
import com.csse3200.game.cutscene.runtime.states.DialogueShowAction;
import com.csse3200.game.cutscene.runtime.states.DialogueState;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DefaultOrchestrator implements CutsceneOrchestrator {
    private OrchestratorState state;
    private Cutscene cutscene;
    private ListIterator<Beat> beats;
    private Beat beatIdx;
    private boolean running;
    private boolean paused;

    private List<ActionState> active;
    private boolean beatStarted;

    // Fixed states
    private DialogueState dialogueState;

    /**
     * Loads a cutscene from a {@link Cutscene} object
     *
     * @param cutscene The cutscene to load
     */
    @Override
    public void load(Cutscene cutscene) {
        this.state = new OrchestratorState();
        this.cutscene = cutscene;
        this.beats = cutscene.getBeats().listIterator();
        this.beatIdx = beats.next();
        this.running = true;
        this.paused = false;

        this.active = new ArrayList<>();
        this.beatStarted = false;

        this.dialogueState = state.getDialogueState();

        if (ServiceLocator.getTimeSource() == null) {
            ServiceLocator.registerTimeSource(new GameTime());
        }
    }

    /**
     * Updates the state of the cutscene by delta time (dt)
     *
     * @param dt Delta time between game frames
     */
    @Override
    public void update(float dt) {
        if (!running || paused) return;

        int dtMs = (int)(dt * 1000);

        if (!beatStarted) {
            beatIdx.getActions().forEach(action -> {
                // make switch to create states for each action
                active.add(switch(action) {
                    case DialogueShowData d -> new DialogueShowAction(state.getDialogueState(), d);
                    default -> null;
                });
            });

            active.add(ActionStates.advance(beatIdx.getAdvance()));

            beatStarted = true;
        }

        active.forEach(actionState -> {
            actionState.tick(dtMs);
        });

        active.removeIf(ActionState::done);

        // if there are no more blocking actions move on to next beat
        boolean blocking = active.stream().anyMatch(ActionState::blocking);
        if (!blocking) {
            active.clear();
            beatStarted = false;

            if (!beats.hasNext()) {
                this.running = false;
            } else {
                beatIdx = beats.next();
            }
        }
    }

    /**
     * Key or click to advance
     */
    @Override
    public void advance() {
        active.forEach(actionState -> {
            if (actionState instanceof SupportsAdvance) {
                ((SupportsAdvance) actionState).advance();
            }
        });
    }

    /**
     * Selects a choice (from choice events)
     *
     * @param id The id of the choice made
     */
    @Override
    public void choose(String id) {

    }

    /**
     * Get the current state of the orchestrator
     *
     * @return The current state of the orchestrator
     */
    @Override
    public OrchestratorState state() {
        return state;
    }

    /**
     * Check if the orchestrator is running
     *
     * @return true if paused, false if running
     */
    @Override
    public boolean paused() {
        return this.paused;
    }

    /**
     * Returns the running state of the orchestrator (to dispose)
     *
     * @return true if running, false otherwise
     */
    @Override
    public boolean isRuning() {
        return this.running;
    }

    /**
     * Set the pause state of the orchestrator
     *
     * @param pause True to pause execution, false to continue execution
     */
    @Override
    public void setPause(boolean pause) {
        this.paused = pause;
    }

    /**
     * Permanently stops execution, required load to restart cutscene
     */
    @Override
    public void stop() {
        this.running = false;
    }
}
