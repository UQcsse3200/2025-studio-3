package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;

/**
 * Component for the Lane Runner player that handles input and lane tracking.
 */
public class LaneRunnerPlayerComponent extends Component {
  private int currentLane = 1;
  private final LaneManager laneManager;
  private static final float MOVE_SPEED = 10f;
  private boolean leftPressed = false;
  private boolean rightPressed = false;

  public LaneRunnerPlayerComponent(LaneManager laneManager) {
    this.laneManager = laneManager;
  }

  @Override
  public void update() {
    handleInput();
    updatePosition();
  }

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

  private void moveLeft() {
    if (currentLane > 0) {
      currentLane--;
    }
  }

  private void moveRight() {
    if (currentLane < laneManager.getNumLanes() - 1) {
      currentLane++;
    }
  }

  private void updatePosition() {
    float targetX = laneManager.getLaneCenter(currentLane);
    float currentX = entity.getPosition().x;
    
    // Smooth movement towards target lane
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    float newX = currentX + (targetX - currentX) * MOVE_SPEED * delta;
    
    entity.setPosition(newX, entity.getPosition().y);
  }

  public int getCurrentLane() {
    return currentLane;
  }

  public float getLaneCenter() {
    return laneManager.getLaneCenter(currentLane);
  }
}
