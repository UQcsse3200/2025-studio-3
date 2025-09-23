package com.csse3200.game.minigame;

import static org.junit.Assert.*;

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

  @BeforeEach
  void setUp() {
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
  void updateDoesNothingIfNoEntity() {
    CollisionComponent collision2 = new CollisionComponent(paddleImage);
    try {
      collision2.update();
    } catch (Exception e) {
      fail("No exception if entity is null");
    }
  }

  @Test
  void updateDoesNothingIfNoBallComponent() {
    Entity entity = new Entity();
    CollisionComponent collision2 = new CollisionComponent(paddleImage);
    entity.addComponent(collision2);
    entity.create();
    try {
      collision2.update(1f);
    } catch (Exception e) {
      fail("No exception if ballcomponent is null");
    }
  }

  @Test
  void ballCollidesWithTarget() {
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
  void ballDoesNotCollideWhenSeperated() {
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
