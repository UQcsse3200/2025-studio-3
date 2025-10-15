package com.csse3200.game.minigame;

import com.badlogic.gdx.Input;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.services.ServiceLocator;

public class MiniGameInputComponent extends InputComponent {
  private boolean isGameOverScreen = false;

  public MiniGameInputComponent(boolean isGameOverScreen) {
    super(5);
  }

  @Override
  public boolean keyDown(int key) {
    int left = ServiceLocator.getSettingsService().getSettings().getLeftButton();
    int right = ServiceLocator.getSettingsService().getSettings().getRightButton();
    int escape = Input.Keys.ESCAPE;
    int space = ServiceLocator.getSettingsService().getSettings().getSkipButton();

    if (key == left) {
      if (!isGameOverScreen) {
        entity.getEvents().trigger("moveLeft");
        return true;
      }
      return false;
    }

    if (key == right) {
      if (!isGameOverScreen) {
        entity.getEvents().trigger("moveRight");
        return true;
      }
      return false;
    }

    if (key == escape) {
      if (isGameOverScreen) {
        entity.getEvents().trigger("returnToArcade");
        return true;
      }
      return false;
    }

    if (key == space) {
      if (isGameOverScreen) {
        entity.getEvents().trigger("playAgain");
        return true;
      }
      return false;
    }

    return false;
  }
}
