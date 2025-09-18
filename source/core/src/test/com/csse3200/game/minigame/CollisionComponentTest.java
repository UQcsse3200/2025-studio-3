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

    ball = new BallComponent(ballImage, 0f, 50f);

    collision = new CollisionComponent(paddleImage);

    Entity entity = new Entity();
    entity.addComponent(ball);
    entity.addComponent(collision);
    entity.create();
  }

  @Test
  public void updateDoesNothingIfNoEntity() {
    CollisionComponent collision = new CollisionComponent(paddleImage);
    try {
      collision.update();
    } catch (Exception e) {
      fail("No exception if entity is null");
    }
  }

  @Test
  public void updateDoesNothingIfNoBallComponent() {
    Entity entity = new Entity();
    CollisionComponent collision = new CollisionComponent(paddleImage);
    entity.addComponent(collision);
    entity.create();
    try {
      collision.update(1f);
    } catch (Exception e) {
      fail("No exception if ballcomponent is null");
    }
  }

  @Test
  public void ballCollidesWithTarget() {
    ballImage.setY(140);
    ballImage.setX(140);

    int initialScore = ball.getScore();
    int initialBallsHit = ball.getBallsHit();

    collision.update(1f);

    assertEquals(initialBallsHit + 1, ball.getBallsHit());
    assertEquals(initialScore + 1, ball.getScore());
    assertEquals(paddleImage.getY() + paddleImage.getHeight(), ballImage.getY(), 0.01f);
    assertTrue(ball.getVelocityY() < 0);
  }

  @Test
  public void ballDoesNotCollideWhenSeperated() {
    int initialScore = ball.getScore();
    int initialBallsHit = ball.getBallsHit();

    ballImage.setY(50);
    ballImage.setX(10);

    collision.update(1f);

    assertEquals(initialBallsHit, ball.getBallsHit());
    assertEquals(initialScore, ball.getScore());
    assertEquals(50, ballImage.getY(), 0.01f);
    assertEquals(10, ballImage.getX(), 0.01f);
  }
}
