package com.csse3200.game.cutscene.runtime.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.cutscene.models.object.actiondata.ChoiceData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.cutscene.runtime.states.ChoiceState;
import com.csse3200.game.cutscene.runtime.states.DialogueState;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.ui.UIFactory;
import java.util.Objects;

public class ChoiceAction implements ActionState {
  private boolean done;
  private CutsceneOrchestrator orchestrator;
  private ChoiceState choiceState;
  private DialogueState dialogueState;
  private ChoiceData data;

  private Skin skin = new Skin(Gdx.files.internal("skin/tdwfb.json"));
  private UIFactory ui = new UIFactory(skin, Settings.UIScale.MEDIUM);

  public ChoiceAction(
      CutsceneOrchestrator orchestrator,
      ChoiceState choiceState,
      DialogueState dialogueState,
      ChoiceData data) {
    this.orchestrator = orchestrator;
    this.choiceState = choiceState;
    this.dialogueState = dialogueState;
    this.data = data;
    this.done = false;
  }

  /**
   * Runs on every game tick to progress logic
   *
   * @param dtMs The delta time in milliseconds
   */
  @Override
  public void tick(int dtMs) {
    if (!done) {
      if (!data.prompt().equals("")) {
        dialogueState.set(data.prompt(), "");
        dialogueState.setVisible(true);
      }

      choiceState.setActive(true);

      data.choices()
          .forEach(
              choice -> {
                if (Objects.equals(choice.getCutsceneId(), "current")) {
                  Button button = ui.primaryButton(choice.getLine(), 1f);
                  button.setFillParent(false);
                  button.addListener(
                      new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                          orchestrator.choose(choice.getEntryBeatId());
                          orchestrator.state().getChoiceState().setActive(false);
                          orchestrator.state().getChoiceState().clearChoices();
                        }
                      });
                  choiceState.addChoice(button);
                }
              });
      done = true;
    }
  }

  /** Triggered on skip, will fast track any logic to its final state */
  @Override
  public void skip() {}

  /**
   * @return True if the action is blocking till completion (false if async)
   */
  @Override
  public boolean blocking() {
    return true;
  }

  /**
   * @return True if the action is completed (can be disposed of)
   */
  @Override
  public boolean done() {
    return false;
  }
}
