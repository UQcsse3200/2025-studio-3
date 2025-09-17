package com.csse3200.game.cutscene.runtime.action;

import com.csse3200.game.cutscene.models.object.actiondata.DialogueHideData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.states.DialogueState;

public class DialogueHideAction implements ActionState {
  private final DialogueState dialogueState;
  private boolean await;
  private boolean done;

  public DialogueHideAction(DialogueState dialogueState, DialogueHideData dialogueHideData) {
    this.dialogueState = dialogueState;
    this.await = dialogueHideData.await();

    this.dialogueState.setVisible(false);
    this.done = true;
  }

  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  @Override
  public void tick(int dtMs) {
    // Has no animation yet
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
