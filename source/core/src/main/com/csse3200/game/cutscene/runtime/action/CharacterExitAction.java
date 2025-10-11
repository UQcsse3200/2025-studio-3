package com.csse3200.game.cutscene.runtime.action;

import com.csse3200.game.cutscene.models.object.actiondata.CharacterExitData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.states.CharacterState;

public class CharacterExitAction implements ActionState {
  private CharacterState characterState;
  private CharacterExitData characterExitData;
  private int transitionMsLeft;
  private final int transitionDurationMs;
  private final boolean await;
  private boolean done;

  public CharacterExitAction(CharacterState characterState, CharacterExitData characterExitData) {
    this.characterState = characterState;
    this.characterExitData = characterExitData;
    this.transitionMsLeft = characterExitData.duration();
    this.transitionDurationMs = characterExitData.duration();
    this.await = characterExitData.await();
  }

  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  @Override
  public void tick(int dtMs) {
    if (transitionMsLeft >= 0) {
      transitionMsLeft -= dtMs;

      switch (characterExitData.transition()) {
        case SLIDE ->
            characterState.setxOffset(((float) transitionMsLeft / transitionDurationMs) - 1);
        case FADE -> characterState.setOpacity((float) transitionMsLeft / transitionDurationMs);
        default -> {
          // do nothing
        }
      }
    } else {
      characterState.setOnScreen(false);
      characterState.setxOffset(-1f);
      done = true;
    }
  }

  /** Triggered on skip, will fast track any logic to its final state */
  @Override
  public void skip() {
    characterState.setOnScreen(false);
    characterState.setxOffset(-1f);
    characterState.setOpacity(1f);
    done = true;
  }

  /**
   * @return True if the action is blocking till completion (false if async)
   */
  @Override
  public boolean blocking() {
    return await;
  }

  /**
   * @return True if the action is completed (can be disposed of)
   */
  @Override
  public boolean done() {
    return done;
  }
}
