package com.csse3200.game.components.minigame;

import com.badlogic.gdx.Gdx;
import com.csse3200.game.components.Component;
import com.csse3200.game.screens.LaneRunnerScreen;
import com.csse3200.game.services.ServiceLocator;

/** Component for the Lane Runner player that handles input and lane tracking. */
public class LaneRunnerPlayerComponent extends Component {
  private int currentLane = 1;
  private static final float MOVE_SPEED = 10f;
  private boolean leftPressed = false;
  private boolean rightPressed = false;

  @Override
  public void update() {
    handleInput();
    updatePosition();
  }

  /** Handles the input for the player. */
  private void handleInput() {
    // Check for left movement
    int left = ServiceLocator.getSettingsService().getSettings().getLeftButton();
    boolean leftKeyPressed = Gdx.input.isKeyPressed(left);
    if (leftKeyPressed && !leftPressed) {
      moveLeft();
    }
    leftPressed = leftKeyPressed;

    // Check for right movement
    int right = ServiceLocator.getSettingsService().getSettings().getRightButton();
    boolean rightKeyPressed = Gdx.input.isKeyPressed(right);
    if (rightKeyPressed && !rightPressed) {
      moveRight();
    }
    rightPressed = rightKeyPressed;
  }

  /** Moves the player to the left. */
  private void moveLeft() {
    if (currentLane > 0) {
      currentLane--;
    }
  }

  /** Moves the player to the right. */
  private void moveRight() {
    if (currentLane < LaneRunnerScreen.NUM_LANES - 1) {
      currentLane++;
    }
  }

  /** Updates the position of the player. */
  private void updatePosition() {
    float targetX = LaneRunnerScreen.LANE_CENTER + currentLane * LaneRunnerScreen.LANE_WIDTH - 32f;
    float currentX = entity.getPosition().x;

    // Smooth movement towards target lane
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    float newX = currentX + (targetX - currentX) * MOVE_SPEED * delta;

    entity.setPosition(newX, entity.getPosition().y);
  }

  /**
   * Gets the current lane of the player.
   *
   * @return the current lane of the player
   */
  public int getCurrentLane() {
    return currentLane;
  }
}
