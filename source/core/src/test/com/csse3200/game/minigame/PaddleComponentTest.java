package com.csse3200.game.minigame;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaddleComponentTest {
  private PaddleComponent paddleComponent;
  private Image paddleImage;

  @BeforeEach
  void setup() {
    paddleImage = new Image(mock(Texture.class));
    paddleImage.setSize(100, 20);
    paddleImage.setPosition(400, 50);
    paddleComponent = new PaddleComponent(paddleImage);
  }

  @Test
  void testMoveLeft() {
    float initialX = paddleImage.getX();
    paddleComponent.moveLeft(0.1f);
    assertTrue(paddleImage.getX() < initialX, "Paddle should move left");
  }

  @Test
  void testMoveRight() {
    float initialX = paddleImage.getX();
    paddleComponent.moveRight(0.1f);
    assertTrue(paddleImage.getX() > initialX, "Paddle should move right");
  }
}
