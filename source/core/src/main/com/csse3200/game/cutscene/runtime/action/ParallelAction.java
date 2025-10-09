package com.csse3200.game.cutscene.runtime.action;

import com.csse3200.game.cutscene.models.object.actiondata.ActionData;
import com.csse3200.game.cutscene.models.object.actiondata.ParallelData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;

import java.util.ArrayList;
import java.util.List;

public class ParallelAction implements ActionState {
    private List<ActionState> actionStateList;
    private final boolean blocking;
    private boolean done;

    public ParallelAction(CutsceneOrchestrator orchestrator, ParallelData data) {
        actionStateList = new ArrayList<>();
        for (ActionData action : data.actions()) {
            ActionState actionState = orchestrator.getActionState(action);
            if (actionState != null) {
                actionStateList.add(actionState);
            }
        }
        this.blocking = data.await();
        this.done = false;
    }

    /**
     * Runs on every game tick to progress logic
     *
     * @param dtMs The delta time in milliseconds
     */
    @Override
    public void tick(int dtMs) {
        for (ActionState actionState : actionStateList) {
            actionState.tick(dtMs);
        }

        List<ActionState> completedActions = actionStateList.stream().filter(ActionState::done).toList();
        for (ActionState action : completedActions) {
            actionStateList.remove(action);
        }

        if (actionStateList.isEmpty()) {
            done = true;
        }
    }

    /**
     * Triggered on skip, will fast track any logic to its final state
     */
    @Override
    public void skip() {
        for (ActionState actionState : actionStateList) {
            actionState.skip();
        }
        done = true;
    }

    /**
     * @return True if the action is blocking till completion (false if async)
     */
    @Override
    public boolean blocking() {
        return blocking;
    }

    /**
     * @return True if the action is completed (can be disposed of)
     */
    @Override
    public boolean done() {
        return done;
    }
}
