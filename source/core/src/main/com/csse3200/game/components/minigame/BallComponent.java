package com.csse3200.game.components.minigame;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Component for the ball in the minigame. */
public class BallComponent extends Component {
  private static final Logger logger = LoggerFactory.getLogger(BallComponent.class);
  private final Vector2 velocity;
  private float speedMultiplier = 1f;
  private int score;
  private int ballsHit;
  private static final float INITIAL_X_SPEED = 300f;
  private static final float INITIAL_Y_SPEED = 300f;
  private float mu = 1f;
  private float sigma = 0.1f;
  private float decayFactor = 1f;
  private final Random random = new Random();

  /** Creates a new BallComponent. */
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
   * Generates a random number from a Gaussian distribution that is truncated with repeated draws.
   *
   * @return a random number from a Gaussian distribution
   */
  private float randomizer() {
    float res = Float.MAX_VALUE;
    while (res > 1.5f * this.mu || res < 0.3f * this.mu) {
      res = (float) (this.mu + this.sigma * random.nextGaussian());
    }
    return res;
  }

  /** Reverses the ball's Y velocity and updates the score and balls hit. */
  public void hitPaddle() {
    velocity.y *= -1;
    score++;
    ballsHit++;
    this.mu += 0.1f * decayFactor;
    this.sigma += 0.1f * decayFactor;
    this.decayFactor *= 0.9f;
    speedMultiplier = randomizer();
  }

  @Override
  public void update() {
    float delta = ServiceLocator.getTimeSource().getDeltaTime();
    Vector2 currentPos = entity.getPosition();
    float newX = currentPos.x + velocity.x * delta * speedMultiplier;
    float newY = currentPos.y + velocity.y * delta * speedMultiplier;
    entity.setPosition(newX, newY);
    checkWallCollisions();
  }

  /** Checks for wall collisions and updates the ball's position and velocity. */
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
