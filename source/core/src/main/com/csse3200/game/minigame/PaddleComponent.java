package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;

/**
 * Handles the paddle's movement in the paddle game.
 */
public class PaddleComponent extends Component {
  private float speed = 500f;

  /**
   * Updates the paddle's movement.
   */
  @Override
  public void update() {
    int left = ServiceLocator.getSettingsService().getSettings().getLeftButton();
    if (Gdx.input.isKeyPressed(left)) {
      moveLeft();
    }
    int right = ServiceLocator.getSettingsService().getSettings().getRightButton();
    if (Gdx.input.isKeyPressed(right)) {
      moveRight();
    }
  }

  /**
   * Moves the paddle to the left.
   */
  private void moveLeft() {
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    Vector2 currentPos = entity.getPosition();
    float newX = currentPos.x - speed * delta;
    newX = Math.max(0, newX);
    entity.setPosition(newX, currentPos.y);
  }

  /**
   * Moves the paddle to the right.
   */
  private void moveRight() {
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    Vector2 currentPos = entity.getPosition();
    Vector2 paddleScale = entity.getScale();
    float newX = currentPos.x + speed * delta;
    
    float worldWidth = 1280f;
    
    newX = Math.min(worldWidth - paddleScale.x, newX);
    entity.setPosition(newX, currentPos.y);
  }
}
