package com.csse3200.game.cutscene.runtime;

import com.csse3200.game.cutscene.models.object.Beat;
import com.csse3200.game.cutscene.models.object.Cutscene;
import com.csse3200.game.cutscene.models.object.actiondata.*;
import com.csse3200.game.cutscene.runtime.action.*;
import com.csse3200.game.cutscene.runtime.states.*;
import com.csse3200.game.exceptions.InvalidGotoBeatId;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultOrchestrator implements CutsceneOrchestrator {
  private OrchestratorState state;
  private Cutscene cutscene;
  private List<Beat> beats;
  private Beat beatIdx;
  private boolean running;
  private boolean paused;

  private List<ActionState> queue = new ArrayList<>();
  private List<ActionState> active = new ArrayList<>();
  private boolean beatStarted;

  private Beat gotoBeat;

  /**
   * Loads a cutscene from a {@link Cutscene} object
   *
   * @param cutscene The cutscene to load
   */
  @Override
  public void load(Cutscene cutscene) {
    this.state = new OrchestratorState();
    this.cutscene = cutscene;
    this.beats = cutscene.getBeats();
    this.beatIdx = beats.getFirst();
    this.running = true;
    this.paused = false;

    this.queue = new ArrayList<>();
    this.active = new ArrayList<>();
    this.beatStarted = false;

    this.gotoBeat = null;

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

    int dtMs = (int) (dt * 1000);

    if (!beatStarted) {
      beatIdx
          .getActions()
          .forEach(
              action -> {
                // make switch to create states for each action
                ActionState actionState = getActionState(action);
                if (actionState != null) {
                  queue.add(actionState);
                }
              });

      queue.add(ActionStates.advance(beatIdx.getAdvance()));

      beatStarted = true;
    }

    if (!active.isEmpty() && active.stream().anyMatch(ActionState::done)) {
      List<ActionState> completedActions = active.stream().filter(ActionState::done).toList();
      for (ActionState action : completedActions) {
        active.remove(action);
      }
    }

    boolean activeBlocking = active.stream().anyMatch(ActionState::blocking);

    if (!activeBlocking && !queue.isEmpty()) {
      active.add(queue.getFirst());
      queue.removeFirst();
    }

    active.forEach(actionState -> actionState.tick(dtMs));

    // if there are no more blocking actions move on to next beat
    boolean blocking =
            active.stream().anyMatch(ActionState::blocking)
                    || queue.stream().anyMatch(ActionState::blocking);
    if (!blocking) {
      active.clear();
      queue.clear();
      beatStarted = false;

      if (gotoBeat != null) {
        beatIdx = gotoBeat;
        gotoBeat = null;
        return;
      }

      if (beats.getLast() == beatIdx) {
        ServiceLocator.getCutsceneService().end();
      } else {
        beatIdx = beats.get(beats.indexOf(beatIdx) + 1);
      }
    }
  }

  /**
   * Get the {@link ActionState} from the given {@link ActionData}
   * @param actionData the data to get the corresponding state for
   * @return an {@link ActionState} for the given actionData
   */
  public ActionState getActionState(ActionData actionData) {
    return switch (actionData) {
      case BackgroundSetData d ->
              new BackgroundSetAction(state.getBackgroundState(), d);
      case CharacterEnterData d -> {
        if (!state.getCharacterStates().containsKey(d.character())) {
          state
                  .getCharacterStates()
                  .put(d.character(), new CharacterState(d.character()));
        }
        yield new CharacterEnterAction(
                state.getCharacterStates().get(d.character()), d);
      }
      case CharacterExitData d -> {
        if (!state.getCharacterStates().containsKey(d.character())) {
          state
                  .getCharacterStates()
                  .put(d.character(), new CharacterState(d.character()));
        }
        yield new CharacterExitAction(
                state.getCharacterStates().get(d.character()), d);
      }
      case ChoiceData d ->
              new ChoiceAction(state.getChoiceState(), state().getDialogueState(), d);
      case DialogueShowData d ->
              new DialogueShowAction(state.getDialogueState(), d);
      case DialogueHideData d ->
              new DialogueHideAction(state.getDialogueState(), d);
      case GotoData d ->
              new GotoAction(this, beatIdx, beats, d);
      case ParallelData d ->
              new ParallelAction(this, d);
      default -> null;
    };
  }

  /** Key or click to advance */
  @Override
  public void advance() {
    if (active.size() == 1 && active.getFirst() instanceof SupportsAdvance) {
      ((SupportsAdvance) active.getFirst()).advance();
    } else {
      active.addAll(queue);
      queue.clear();
      active.forEach(ActionState::skip);
    }
  }

  /**
   * Selects a choice (from choice events)
   *
   * @param id The id of the choice made
   */
  @Override
  public void choose(String id) {
    gotoBeat(id);
    active.clear();
    queue.clear();
    beatStarted = false;
  }

  /**
   * Goto a specific beat from the beats ID
   *
   * @param id the ID of the beat to jump to
   */
  @Override
  public void gotoBeat(String id) {
    Beat beatToGoto = beats.stream().filter(beat -> Objects.equals(beat.getId(), id)).findFirst().orElse(null);
    if (beatToGoto == null) {
      throw new InvalidGotoBeatId("No valid beat could be found with the id " + id);
    }
    gotoBeat = beatToGoto;
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

  /** Permanently stops execution, required load to restart cutscene */
  @Override
  public void stop() {
    this.running = false;
    this.active.clear();
    this.queue.clear();
  }

  /**
   * Gets the cutscene
   *
   * @return the cutscene
   */
  public Cutscene getCutscene() {
    return cutscene;
  }
}
