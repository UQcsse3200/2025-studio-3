package com.csse3200.game.cutscene.runtime;

import com.badlogic.gdx.graphics.Color;
import com.csse3200.game.cutscene.models.object.Character;
import com.csse3200.game.cutscene.runtime.components.CutsceneHudComponent;
import com.csse3200.game.cutscene.runtime.states.BackgroundState;
import com.csse3200.game.cutscene.runtime.states.CharacterState;
import com.csse3200.game.cutscene.runtime.states.ChoiceState;
import com.csse3200.game.cutscene.runtime.states.DialogueState;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrchestratorState {
  private DialogueState dialogueState;
  private BackgroundState backgroundState;
  private Map<Character, CharacterState> characterStates;
  private ChoiceState choiceState;

  public OrchestratorState() {
    this.dialogueState = new DialogueState();
    this.dialogueState.setVisible(false);
    this.backgroundState = new BackgroundState();
    this.backgroundState.setImage(CutsceneHudComponent.loadImage(Color.BLACK));
    this.characterStates = new HashMap<>();
    this.choiceState = null;
  }

  public DialogueState getDialogueState() {
    return dialogueState;
  }

  public BackgroundState getBackgroundState() {
    return backgroundState;
  }

  public Map<Character, CharacterState> getCharacterStates() {
    return characterStates;
  }

  public List<CharacterState> getCharacterStatesList() {
    return characterStates.values().stream().toList();
  }

  public ChoiceState getChoiceState() {
    return choiceState;
  }
}
