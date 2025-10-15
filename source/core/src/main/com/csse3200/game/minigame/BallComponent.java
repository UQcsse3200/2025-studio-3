package com.csse3200.game.minigame;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BallComponent extends Component {
  private static final Logger logger = LoggerFactory.getLogger(BallComponent.class);
  private final Vector2 velocity;
  private int score;
  private int ballsHit;
  private static final float INITIAL_X_SPEED = 200f;
  private static final float INITIAL_Y_SPEED = 200f;

  /**
   * Creates a new BallComponent.
   */
  public BallComponent() {
    this.velocity = new Vector2(INITIAL_X_SPEED, INITIAL_Y_SPEED); // Start moving up and right
    this.score = 0;
    this.ballsHit = 0;
  }

  /**
   * Gets the ball's Y velocity.
   * 
   * @return the ball's Y velocity
   */
  public float getVelocityY() {
    return velocity.y;
  }

  /**
   * Gets the score.
   * 
   * @return the score
   */
  public int getScore() {
    return score;
  }

  /**
   * Gets the number of balls hit.
   * 
   * @return the number of balls hit
   */
  public int getBallsHit() {
    return ballsHit;
  }

  /**
   * Reverses the ball's Y velocity and updates the score and balls hit.
   */
  public void reverseY() {
    velocity.y *= -1;
    score++;
    ballsHit++;
  }

  @Override
  public void update() {
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    Vector2 currentPos = entity.getPosition();
    float newX = currentPos.x + velocity.x * delta;
    float newY = currentPos.y + velocity.y * delta;
    entity.setPosition(newX, newY);
    checkWallCollisions();
  }

  /**
   * Checks for wall collisions and updates the ball's position and velocity.
   */
  private void checkWallCollisions() {
    Vector2 pos = entity.getPosition();
    Vector2 scale = entity.getScale();
    float ballX = pos.x;
    float ballY = pos.y;
    float ballWidth = scale.x;
    float ballHeight = scale.y;
    
    float worldWidth = 1280f;
    float worldHeight = 720f; // Match camera viewport

    // Left wall collision
    if (ballX <= 0) {
      velocity.x *= -1;
      entity.setPosition(0, ballY);
    } 
    // Right wall collision
    else if (ballX + ballWidth >= worldWidth) {
      velocity.x *= -1;
      entity.setPosition(worldWidth - ballWidth, ballY);
    }

    // Top wall collision
    if (ballY + ballHeight >= worldHeight) {
      velocity.y *= -1;
      entity.setPosition(ballX, worldHeight - ballHeight);
    }

    // Bottom wall collision - game over
    if (ballY <= 0) {
      entity.setPosition(ballX, 0);
      velocity.y = 0;
      velocity.x = 0;
      logger.info("[BallComponent] Game over triggered - ball hit bottom at y={}", ballY);
      ServiceLocator.getMinigameService().setGameOver(true);
    }
  }
}
