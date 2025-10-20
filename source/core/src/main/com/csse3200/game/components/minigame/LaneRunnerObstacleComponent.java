package com.csse3200.game.components.minigame;

import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;

/** Component for Lane Runner obstacles that handles movement and cleanup. */
public class LaneRunnerObstacleComponent extends Component {
  private float speed;
  private boolean isAlive = true;
  private static final float SPEED_GROWTH = 100f;

  /**
   * Creates a new LaneRunnerObstacleComponent.
   *
   * @param baseSpeed - The base speed of the obstacle.
   */
  public LaneRunnerObstacleComponent(float baseSpeed) {
    this.speed = baseSpeed;
  }

  @Override
  public void update() {
    if (!isAlive) {
      return;
    }

    moveDown();
    checkBounds();
  }

  /** Moves the obstacle down. */
  private void moveDown() {
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    float elapsedTime = ServiceLocator.getTimeSource().getTime() / 1000f;
    float currentSpeed = speed + (elapsedTime * SPEED_GROWTH);

    float newY = entity.getPosition().y - (currentSpeed * delta);
    entity.setPosition(entity.getPosition().x, newY);
  }

  /** Checks if the obstacle is off the screen and removes it if it is. */
  private void checkBounds() {
    // Remove obstacle if it goes off screen
    if (entity.getPosition().y < -entity.getScale().y) {
      isAlive = false;
      ServiceLocator.getMinigameService()
          .setScore(ServiceLocator.getMinigameService().getScore() + 1);
      ServiceLocator.getEntityService().unregister(entity);
    }
  }

  /**
   * Checks if the obstacle is alive.
   *
   * @return true if the obstacle is alive, false otherwise
   */
  public boolean isAlive() {
    return isAlive;
  }

  /**
   * Sets the alive status of the obstacle.
   *
   * @param alive - The new alive status of the obstacle.
   */
  public void setAlive(boolean alive) {
    this.isAlive = alive;
  }

  /**
   * Gets the speed of the obstacle.
   *
   * @return the speed of the obstacle
   */
  public float getSpeed() {
    return speed;
  }

  /**
   * Sets the speed of the obstacle.
   *
   * @param speed the new speed of the obstacle
   */
  public void setSpeed(float speed) {
    this.speed = speed;
  }
}
