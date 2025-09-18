package com.csse3200.game.minigame;

import com.badlogic.gdx.Input;
import com.csse3200.game.input.InputComponent;

public class MiniGameInputComponent extends InputComponent {
  private boolean isGameOverScreen = false;

  public MiniGameInputComponent(boolean isGameOverScreen) {
    super(5);
    this.isGameOverScreen = isGameOverScreen;
  }

  @Override
  public boolean keyDown(int key) {
    switch (key) {
      case Input.Keys.LEFT:
      case Input.Keys.A:
        if (!isGameOverScreen) {
          entity.getEvents().trigger("moveLeft");
          return true;
        }
        return false;
      case Input.Keys.RIGHT:
      case Input.Keys.D:
        if (!isGameOverScreen) {
          entity.getEvents().trigger("moveRight");
          return true;
        }
        return false;
      case Input.Keys.ESCAPE:
        if (isGameOverScreen) {
          entity.getEvents().trigger("mainMenu");
          return true;
        }
        return false;
      case Input.Keys.SPACE:
        if (isGameOverScreen) {
          entity.getEvents().trigger("playAgain");
          return true;
        }
        return false;
      default:
        return false;
    }
  }
}
