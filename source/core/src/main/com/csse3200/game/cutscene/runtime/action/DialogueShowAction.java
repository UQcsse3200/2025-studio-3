package com.csse3200.game.cutscene.runtime.action;

import com.csse3200.game.cutscene.models.object.actiondata.DialogueShowData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.states.DialogueState;

public class DialogueShowAction implements ActionState {
  private final DialogueState dialogueState;
  private final String speaker;
  private final String text;
  private final boolean await;
  private int charsShown;
  private int nextCharMsCountdown;
  private boolean done;

  public DialogueShowAction(DialogueState dialogueState, DialogueShowData dialogueShowData) {
    this.dialogueState = dialogueState;
    this.speaker = dialogueShowData.character().getName();
    this.text = dialogueShowData.text();
    this.await = dialogueShowData.await();
    this.charsShown = 0;
    this.nextCharMsCountdown = 0;
    this.done = false;

    this.dialogueState.set(speaker, "");
  }

  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  @Override
  public void tick(int dtMs) {
    if (!dialogueState.isVisible()) {
      this.dialogueState.setVisible(true);
    }

    if (nextCharMsCountdown > 0) {
      nextCharMsCountdown -= dtMs;
    } else if (text.length() >= charsShown) {
      dialogueState.set(speaker, text.substring(0, charsShown));
      char nextCharIfExists;
      if (charsShown == text.length()) {
        nextCharIfExists = '\0';
      } else {
        nextCharIfExists = text.charAt(charsShown);
      }
      nextCharMsCountdown =
          switch (text.charAt(Math.max(charsShown - 1, 0))) {
            case ',' -> 150;
            case '.' -> {
              if (nextCharIfExists == '.') {
                yield 50;
              } else {
                yield 360;
              }
            }
            case '-' -> 200;
            case ':' -> 240;
            case '!' -> {
              if (text.charAt(charsShown - 2) == '?') {
                yield 500;
              } else {
                yield 340;
              }
            }
            case '?' -> {
              if (nextCharIfExists == '!') {
                yield 10;
              } else {
                yield 380;
              }
            }
            default -> 32;
          };
      charsShown++;
    } else {
      done = true;
    }
  }

  /** Triggered on skip, will fast track any logic to its final state */
  @Override
  public void skip() {
    dialogueState.setVisible(true);
    dialogueState.set(speaker, text);
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
