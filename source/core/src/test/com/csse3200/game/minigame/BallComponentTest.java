package com.csse3200.game.minigame;

import static org.junit.Assert.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BallComponentTest {

  private float screenWidth = 800f;
  private float screenHeight = 600f;

  @Before
  public void setUp() {
    Gdx.graphics = Mockito.mock(Graphics.class);
    Mockito.when(Gdx.graphics.getWidth()).thenReturn((int) screenWidth);
    Mockito.when(Gdx.graphics.getHeight()).thenReturn((int) screenHeight);
  }

  @Test
  public void ballMovesCorrectly() {
    Image ballImage = new Image();
    ballImage.setPosition(100, 100);
    BallComponent ball = new BallComponent(ballImage, 50f, 50f);

    ball.update(1f);

    assertEquals(150f, ballImage.getX(), 0.01f);
    assertEquals(150f, ballImage.getY(), 0.01f);
  }

  @Test
  public void ballBouncesOffLeftWall() {
    Image ballImage = new Image();
    ballImage.setSize(40, 40);
    ballImage.setPosition(0, 100);
    BallComponent ball = new BallComponent(ballImage, -50f, 0f);
    int initialScore = ball.getScore();

    ball.update(1f);

    assertEquals(0f, ballImage.getX(), 0.01f);
    assertTrue("velocity x should be positive", ball.getVelocityX() >= 0);
    assertEquals(initialScore + 1, ball.getScore());
  }

  @Test
  public void ballBouncesOffRightWall() {
    Image ballImage = new Image();
    ballImage.setSize(40, 40);
    ballImage.setPosition(screenWidth - 40, 100);
    BallComponent ball = new BallComponent(ballImage, 50f, 0f);
    int initialScore = ball.getScore();

    ball.update(1f);

    assertEquals(screenWidth - 40, ballImage.getX(), 0.01f);
    assertTrue("velocity x should be negative", ball.getVelocityX() < 0);
    assertEquals(initialScore + 1, ball.getScore());
  }

  @Test
  public void ballBouncesOffTopWall() {
    Image ballImage = new Image();
    ballImage.setSize(40, 40);
    ballImage.setPosition(100, screenHeight - 40);
    BallComponent ball = new BallComponent(ballImage, 0f, 50f);
    int initialScore = ball.getScore();
    int initialBallsHit = ball.getBallsHit();

    ball.update(1f);

    assertEquals(screenHeight - 40, ballImage.getY(), 0.01f);
    assertEquals(initialScore + 1, ball.getScore());
    assertTrue("velocity y should be negative", ball.getVelocityY() < 0);
    assertEquals(initialBallsHit, ball.getBallsHit());
    ;
  }

  @Test
  public void ballWithZeroVelocity() {
    Image ballImage = new Image();
    ballImage.setSize(40, 40);
    ballImage.setPosition(100, 100);
    BallComponent ball = new BallComponent(ballImage, 0f, 0f);

    int initialScore = ball.getScore();
    int initialBallsHit = ball.getBallsHit();
    float initialX = ballImage.getX();
    float initialY = ballImage.getY();
    ball.update(1f);

    assertEquals(initialX, ballImage.getX(), 0.01f);
    assertEquals(initialY, ballImage.getY(), 0.01f);

    assertEquals(initialScore, ball.getScore());
    assertEquals(initialBallsHit, ball.getBallsHit());
  }

  @Test
  public void reverseY() {
    Image ballImage = new Image();
    ballImage.setSize(40, 40);
    ballImage.setPosition(100, 100);
    BallComponent ball = new BallComponent(ballImage, 50f, 50f);
    int initialScore = ball.getScore();
    int initialBallsHit = ball.getBallsHit();
    float initialVelocityY = ball.getVelocityY();

    ball.reverseY();

    assertEquals(-initialVelocityY, ball.getVelocityY(), 0.01f);
    assertEquals(initialBallsHit + 1, ball.getBallsHit());
    assertEquals(initialScore + 1, ball.getScore());
  }
}
