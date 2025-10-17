package com.csse3200.game.components.minigame;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.MinigameService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BallComponentTest {
  @Mock private GameTime mockTimeSource;
  @Mock private MinigameService mockMinigameService;
  @Mock private Entity mockEntity;

  private BallComponent ballComponent;

  @BeforeEach
  void setUp() {
    // Setup service mocks
    ServiceLocator.registerTimeSource(mockTimeSource);
    ServiceLocator.registerMinigameService(mockMinigameService);
    
    // Setup time source mock
    when(mockTimeSource.getDeltaTime()).thenReturn(0.016f); // 60 FPS
    
    // Create ball component
    ballComponent = new BallComponent();
    
    // Setup entity mock
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, 400f));
    when(mockEntity.getScale()).thenReturn(new Vector2(20f, 20f));
    doNothing().when(mockEntity).setPosition(anyFloat(), anyFloat());
    
    // Attach component to entity
    ballComponent.setEntity(mockEntity);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void testInitialization() {
    // Test initial values
    assertEquals(300f, ballComponent.getVelocityY());
    assertEquals(0, ballComponent.getScore());
    assertEquals(0, ballComponent.getBallsHit());
  }

  @Test
  void testHitPaddle() {
    // Test reverseY method
    ballComponent.hitPaddle();
    
    assertEquals(-300f, ballComponent.getVelocityY());
    assertEquals(1, ballComponent.getScore());
    assertEquals(1, ballComponent.getBallsHit());
    
    // Test multiple reverses
    ballComponent.hitPaddle();
    assertEquals(300f, ballComponent.getVelocityY());
    assertEquals(2, ballComponent.getScore());
    assertEquals(2, ballComponent.getBallsHit());
  }

  @Test
  void testUpdateMovement() {
    // Test ball movement during update
    Vector2 initialPos = new Vector2(640f, 400f);
    when(mockEntity.getPosition()).thenReturn(initialPos);
    
    ballComponent.update();
    
    // Verify position was updated based on velocity and delta time
    verify(mockEntity).setPosition(
        640f + 300f * 0.016f, // x + velocity.x * delta
        400f + 300f * 0.016f  // y + velocity.y * delta
    );
  }

  @Test
  void testLeftWallCollision() {
    // Position ball at left wall
    when(mockEntity.getPosition()).thenReturn(new Vector2(0f, 400f));
    when(mockEntity.getScale()).thenReturn(new Vector2(20f, 20f));
    
    // Set negative X velocity to simulate moving left
    ballComponent = new BallComponent();
    ballComponent.setEntity(mockEntity);
    
    ballComponent.update();
    
    // Should reverse X velocity and clamp position to 0
    verify(mockEntity).setPosition(eq(0f), anyFloat());
  }

  @Test
  void testRightWallCollision() {
    // Position ball at right wall
    when(mockEntity.getPosition()).thenReturn(new Vector2(1260f, 400f)); // 1280 - 20
    when(mockEntity.getScale()).thenReturn(new Vector2(20f, 20f));
    
    ballComponent = new BallComponent();
    ballComponent.setEntity(mockEntity);
    
    ballComponent.update();
    
    // Should reverse X velocity and clamp position
    verify(mockEntity).setPosition(eq(1260f), anyFloat());
  }

  @Test
  void testTopWallCollision() {
    // Position ball at top wall
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, 700f)); // 720 - 20
    when(mockEntity.getScale()).thenReturn(new Vector2(20f, 20f));
    
    ballComponent = new BallComponent();
    ballComponent.setEntity(mockEntity);
    
    ballComponent.update();
    
    // Should reverse Y velocity and clamp position
    verify(mockEntity).setPosition(anyFloat(), eq(700f));
  }

  @Test
  void testBottomWallCollisionGameOver() {
    // Position ball at bottom wall
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, 0f));
    when(mockEntity.getScale()).thenReturn(new Vector2(20f, 20f));
    
    ballComponent = new BallComponent();
    ballComponent.setEntity(mockEntity);
    
    ballComponent.update();
    
    // Should set velocity to 0 and trigger game over
    verify(mockEntity).setPosition(anyFloat(), eq(0f));
    verify(mockMinigameService).setGameOver(true);
  }

  @Test
  void testMultipleUpdates() {
    // Test multiple update calls
    Vector2 pos = new Vector2(640f, 400f);
    when(mockEntity.getPosition()).thenReturn(pos);
    
    // Update multiple times
    ballComponent.update();
    ballComponent.update();
    ballComponent.update();
    
    // Should call setPosition multiple times
    verify(mockEntity, atLeast(3)).setPosition(anyFloat(), anyFloat());
  }

  @Test
  void testScoreIncrement() {
    // Test that reverseY increments score and ballsHit
    int initialScore = ballComponent.getScore();
    int initialBallsHit = ballComponent.getBallsHit();
    
    ballComponent.hitPaddle();
    
    assertEquals(initialScore + 1, ballComponent.getScore());
    assertEquals(initialBallsHit + 1, ballComponent.getBallsHit());
  }

  @Test
  void testVelocityYGetter() {
    // Test getVelocityY method
    assertEquals(300f, ballComponent.getVelocityY());
    
    ballComponent.hitPaddle();
    assertEquals(-300f, ballComponent.getVelocityY());
  }

  @Test
  void testGetters() {
    // Test all getter methods
    assertEquals(0, ballComponent.getScore());
    assertEquals(0, ballComponent.getBallsHit());
    assertEquals(300f, ballComponent.getVelocityY());
  }
}
