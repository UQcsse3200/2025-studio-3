package com.csse3200.game.cutscene.runtime.action;

import com.csse3200.game.cutscene.models.object.actiondata.BackgroundSetData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.components.CutsceneHudComponent;
import com.csse3200.game.cutscene.runtime.states.BackgroundState;

public class BackgroundSetAction implements ActionState {
  private BackgroundState backgroundState;
  private int fadeLeftMs;
  private int totalFadeDurationMs;
  private final boolean await;
  private boolean done;

  public BackgroundSetAction(BackgroundState backgroundState, BackgroundSetData backgroundSetData) {
    this.backgroundState = backgroundState;
    this.fadeLeftMs = backgroundSetData.duration();
    this.totalFadeDurationMs = backgroundSetData.duration();
    this.await = backgroundSetData.await();

    this.backgroundState.setImage(
        CutsceneHudComponent.loadImage(backgroundSetData.background().getImage()));
  }

  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  @Override
  public void tick(int dtMs) {
    if (fadeLeftMs >= 0) {
      fadeLeftMs -= dtMs;

      float imageOpacity = (totalFadeDurationMs - (float) fadeLeftMs) / totalFadeDurationMs;
      backgroundState.setImageOpacity(imageOpacity);
      backgroundState.setOldImageOpacity(1 - imageOpacity);
    } else {
      done = true;
    }
  }

  /** Triggered on skip, will fast track any logic to its final state */
  @Override
  public void skip() {
    backgroundState.setImageOpacity(1f);
    backgroundState.setOldImageOpacity(0f);
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
