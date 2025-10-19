package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;

public class BallComponent extends Component {
  private final Image image;
  private final Vector2 velocity;
  private int score;
  private int ballsHit;
  private final float initialSpeed;

  public BallComponent(Image image, float initialXSpeed, float initialYSpeed) {
    this.image = image;
    this.velocity = new Vector2(initialXSpeed, initialYSpeed);
    this.score = 0;
    this.ballsHit = 0;
    this.initialSpeed = initialXSpeed;
    if (ServiceLocator.getResourceService() != null) {
      ServiceLocator.getResourceService().loadSounds(new String[] {"sounds/bounce.mp3"});
    }
  }

  public Image getImage() {
    return image;
  }

  public float getVelocityY() {
    return velocity.y;
  }

  public int getScore() {
    return score;
  }

  public int getBallsHit() {
    return ballsHit;
  }

  public void reverseY() {
    velocity.y *= -1;
    score++;
    ballsHit++;
    // play sound when ball bounces off paddle
    if (ServiceLocator.getSettingsService() != null) {
      float volume = ServiceLocator.getSettingsService().getSoundVolume();

      Sound bounce = ServiceLocator.getResourceService().getAsset("sounds/bounce.mp3", Sound.class);
      bounce.play(0.7f * volume);
    }
  }

  public void update(float delta, CollisionComponent collisionComponent) {

    collisionComponent.checkCollision(delta);
    float newX = image.getX() + velocity.x * delta;
    float newY = image.getY() + velocity.y * delta;
    image.setPosition(newX, newY);
    checkWallCollisions();
  }

  private void checkWallCollisions() {
    float ballX = image.getX();
    float ballY = image.getY();
    float ballWidth = image.getWidth();
    float ballHeight = image.getHeight();
    int screenWidth = Gdx.graphics.getWidth();
    int screenHeight = Gdx.graphics.getHeight();

    if (ballX <= 0) {
      velocity.x *= -1;
      image.setX(0);
    } else if (ballX + ballWidth >= screenWidth) {
      velocity.x *= -1;
      image.setX(screenWidth - ballWidth);
    }

    if (ballY + ballHeight >= screenHeight) {
      velocity.y *= -1;
      image.setY(screenHeight - ballHeight);
    }

    if (ballY <= 0) {
      image.setY(0);
      velocity.y = 0;
      velocity.x = 0;
    }
  }

  public void draw(SpriteBatch batch) {}
}
