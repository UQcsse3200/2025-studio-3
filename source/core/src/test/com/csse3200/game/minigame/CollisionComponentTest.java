package com.csse3200.game.minigame;

import static org.junit.Assert.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.entities.Entity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CollisionComponentTest {

  private float screenWidth = 800f;
  private float screenHeight = 600f;

  private Image ballImage;
  private Image paddleImage;
  private BallComponent ball;
  private CollisionComponent collision;
  private static final float COLLISION_WIDTH_RATIO = 0.25f;
  private static final float VISIBLE_PADDLE_HEIGHT = 10f;
  private static final float BALL_SIZE = 40f;

  @Before
  public void setUp() {
    Gdx.graphics = Mockito.mock(Graphics.class);
    Mockito.when(Gdx.graphics.getWidth()).thenReturn((int) screenWidth);
    Mockito.when(Gdx.graphics.getHeight()).thenReturn((int) screenHeight);

    ballImage = new Image();
    ballImage.setSize(40, 40);
    ballImage.setPosition(100, 100);

    paddleImage = new Image();
    paddleImage.setSize(200, 20);
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
    float yOffset = (imageHeight / 2f) - (effectiveHeight / 2f); // (20/2) - (10/2) = 5
    return paddleImage.getY() + yOffset + effectiveHeight; // 150 + 5 + 10 = 165
  }

  @Test
  public void checkCollisionDoesNothingIfNoEntity() {
    CollisionComponent collision = new CollisionComponent(paddleImage);
    try {
      collision.checkCollision(1f);
    } catch (Exception e) {
      fail("No exception if entity is null");
    }
  }

  @Test
  public void checkCollisionDoesNothingIfNoBallComponent() {
    Entity entity = new Entity();
    CollisionComponent collision = new CollisionComponent(paddleImage);
    entity.addComponent(collision);
    entity.create();
    try {
      collision.checkCollision(1f);
    } catch (Exception e) {
      fail("No exception if ballcomponent is null");
    }
  }

  @Test
  public void ballCollidesWithTarget() {
    float paddleX = paddleImage.getX();
    float paddleW = paddleImage.getWidth();

    float effectiveW = paddleW * COLLISION_WIDTH_RATIO;
    float xOffset = (paddleW - effectiveW) / 2f;
    float effectiveXStart = paddleX + xOffset;
    float effectiveXEnd = effectiveXStart + effectiveW;
    ballImage.setX(160);
    ballImage.setY(120);
    int initialScore = ball.getScore();
    int initialBallsHit = ball.getBallsHit();
    float initialVelocityY = ball.getVelocityY();

    collision.checkCollision(1f);
    float truePaddleTopY = getTruePaddleTopY();
    assertEquals(initialBallsHit + 1, ball.getBallsHit());
    assertEquals(initialScore + 1, ball.getScore());
    // assertEquals(paddleImage.getY() + paddleImage.getHeight(), ballImage.getY(), 0.01f);
    // assertTrue(ball.getVelocityY() < 0);

    assertEquals(truePaddleTopY + 2f, ballImage.getY(), 0.01f); // Velocity should be reversed
    assertEquals(-initialVelocityY, ball.getVelocityY(), 0.01f);
  }

  @Test
  public void ballDoesNotCollideWhenSeperated() {
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
