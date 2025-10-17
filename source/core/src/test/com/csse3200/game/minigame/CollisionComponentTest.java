package com.csse3200.game.minigame;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(GameExtension.class)
class CollisionComponentTest {

  private float screenWidth = 800f;
  private float screenHeight = 600f;

  private Image ballImage;
  private Image paddleImage;
  private BallComponent ball;
  private CollisionComponent collision;

  private static final float COLLISION_WIDTH_RATIO = 0.25f;
  private static final float VISIBLE_PADDLE_HEIGHT = 10f;
  private static final float BALL_SIZE = 40f;

  @BeforeEach
  void setUp() {
    Gdx.graphics = Mockito.mock(Graphics.class);
    Mockito.when(Gdx.graphics.getWidth()).thenReturn((int) screenWidth);
    Mockito.when(Gdx.graphics.getHeight()).thenReturn((int) screenHeight);

    ballImage = new Image();
    ballImage.setSize(BALL_SIZE, BALL_SIZE); // 40x40
    ballImage.setPosition(100, 100);

    paddleImage = new Image();
    paddleImage.setSize(200, 20); // 200x20
    paddleImage.setPosition(80, 150);

    ball = new BallComponent(ballImage, 0f, -50f);

    collision = new CollisionComponent(paddleImage);

    Entity entity = new Entity();
    entity.addComponent(ball);
    entity.addComponent(collision);
    entity.create();
  }

  private float getTruePaddleTopY() {
    float imageHeight = paddleImage.getHeight(); // 20
    float effectiveHeight = VISIBLE_PADDLE_HEIGHT; // 10
    float yOffset = (imageHeight / 2f) - (effectiveHeight / 2f); // 5

    return paddleImage.getY() + yOffset + effectiveHeight;
  }

  @Test
  void checkCollisionDoesNothingIfNoEntity() {
    CollisionComponent collision2 = new CollisionComponent(paddleImage);
    try {

      collision2.checkCollision(1f);
    } catch (Exception e) {
      fail("No exception should be thrown if entity is null");
    }
  }

  @Test
  void checkCollisionDoesNothingIfNoBallComponent() {
    Entity entity = new Entity();
    CollisionComponent collision2 = new CollisionComponent(paddleImage);
    entity.addComponent(collision2);
    entity.create();
    try {

      collision2.checkCollision(1f);
    } catch (Exception e) {
      fail("No exception should be thrown if ballcomponent is null");
    }
  }

  @Test
  void ballCollidesWithTarget() {

    float effectiveXStart = 80 + (200 - (200 * COLLISION_WIDTH_RATIO)) / 2f; // 80 + 75 = 155
    float truePaddleTopY = getTruePaddleTopY(); // 165

    ballImage.setX(effectiveXStart + 5); // X=160

    ballImage.setY(120);

    int initialScore = ball.getScore();
    int initialBallsHit = ball.getBallsHit();
    float initialVelocityY = ball.getVelocityY(); // -50f

    collision.checkCollision(1f);

    assertEquals(initialBallsHit + 1, ball.getBallsHit());
    assertEquals(initialScore + 1, ball.getScore());

    assertEquals(truePaddleTopY + 2f, ballImage.getY(), 0.01f);

    assertEquals(-initialVelocityY, ball.getVelocityY(), 0.01f);
  }

  @Test
  void ballDoesNotCollideWhenSeperated() {
    int initialScore = ball.getScore();
    int initialBallsHit = ball.getBallsHit();

    ballImage.setY(50);
    ballImage.setX(10);

    collision.checkCollision(1f);

    assertEquals(initialBallsHit, ball.getBallsHit());
    assertEquals(initialScore, ball.getScore());
    assertEquals(50, ballImage.getY(), 0.01f);
    assertEquals(10, ballImage.getX(), 0.01f);
  }
}
