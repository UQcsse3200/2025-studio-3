package com.csse3200.game.cutscene.runtime.action;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.csse3200.game.cutscene.models.object.Transition;
import com.csse3200.game.cutscene.models.object.actiondata.CharacterEnterData;
import com.csse3200.game.cutscene.runtime.ActionState;
import com.csse3200.game.cutscene.runtime.states.CharacterState;

public class CharacterEnterAction implements ActionState {
  private CharacterState characterState;
  private CharacterEnterData characterEnterData;
  private int transitionMsLeft;
  private final int transitionDurationMs;
  private final boolean await;
  private boolean done;

  public CharacterEnterAction(
      CharacterState characterState, CharacterEnterData characterEnterData) {
    this.characterState = characterState;
    this.characterEnterData = characterEnterData;
    this.transitionMsLeft = characterEnterData.duration();
    this.transitionDurationMs = characterEnterData.duration();
    this.await = characterEnterData.await();

    this.characterState.setTexture(
        new SpriteDrawable(
            new Sprite(
                new Texture(
                    characterEnterData.character().getPoses().get(characterEnterData.pose())))));

    if (!characterState.isOnScreen() && characterEnterData.transition() == Transition.SLIDE) {
      characterState.setxOffset(-1);
    }

    characterState.setPosition(characterEnterData.position());
    characterState.setOnScreen(true);
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

      switch (characterEnterData.transition()) {
        case SLIDE -> {
          characterState.setxOffset(-((float) transitionMsLeft / transitionDurationMs));
        }
        case FADE -> {
          characterState.setOpacity(1 - (float) transitionMsLeft / transitionDurationMs);
        }
        default -> {
          // do nothing
        }
      }
    } else {
      characterState.setxOffset(0);
      characterState.setOpacity(1);
      done = true;
    }
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
