package com.csse3200.game.components.minigame;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
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
class LaneRunnerObstacleComponentTest {
  @Mock private GameTime mockTimeSource;
  @Mock private MinigameService mockMinigameService;
  @Mock private EntityService mockEntityService;
  @Mock private Entity mockEntity;

  private LaneRunnerObstacleComponent obstacleComponent;
  private static final float BASE_SPEED = 200f;

  @BeforeEach
  void setUp() {

    // Setup service mocks
    ServiceLocator.registerTimeSource(mockTimeSource);
    ServiceLocator.registerMinigameService(mockMinigameService);
    ServiceLocator.registerEntityService(mockEntityService);

    // Setup time source mock
    when(mockTimeSource.getDeltaTime()).thenReturn(0.016f); // 60 FPS
    when(mockTimeSource.getTime()).thenReturn(1000L); // 1 second elapsed

    // Setup minigame service mock
    when(mockMinigameService.getScore()).thenReturn(0);

    // Create obstacle component
    obstacleComponent = new LaneRunnerObstacleComponent(BASE_SPEED);

    // Setup entity mock
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, 400f));
    when(mockEntity.getScale()).thenReturn(new Vector2(50f, 50f));
    doNothing().when(mockEntity).setPosition(anyFloat(), anyFloat());

    // Attach component to entity
    obstacleComponent.setEntity(mockEntity);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void testInitialization() {
    // Test initial values
    assertEquals(BASE_SPEED, obstacleComponent.getSpeed());
    assertTrue(obstacleComponent.isAlive());
  }

  @Test
  void testMovement() {
    // Test obstacle movement during update
    Vector2 initialPos = new Vector2(640f, 400f);
    when(mockEntity.getPosition()).thenReturn(initialPos);

    obstacleComponent.update();

    // Calculate expected speed: baseSpeed + (elapsedTime * SPEED_GROWTH)
    float expectedSpeed = BASE_SPEED + (1.0f * 100f); // 1 second * 100f growth
    float expectedY = 400f - (expectedSpeed * 0.016f);

    verify(mockEntity).setPosition(640f, expectedY);
  }

  @Test
  void testSpeedGrowth() {
    // Test that speed increases over time
    when(mockTimeSource.getTime()).thenReturn(2000L); // 2 seconds elapsed

    Vector2 initialPos = new Vector2(640f, 400f);
    when(mockEntity.getPosition()).thenReturn(initialPos);

    obstacleComponent.update();

    // Calculate expected speed: baseSpeed + (2.0f * 100f)
    float expectedSpeed = BASE_SPEED + (2.0f * 100f);
    float expectedY = 400f - (expectedSpeed * 0.016f);

    verify(mockEntity).setPosition(640f, expectedY);
  }

  @Test
  void testBoundsCheckOffScreen() {
    // Position obstacle off screen (below bottom)
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, -60f)); // Below screen
    when(mockEntity.getScale()).thenReturn(new Vector2(50f, 50f));

    obstacleComponent.update();

    // Should set alive to false, increment score, and unregister entity
    assertFalse(obstacleComponent.isAlive());
    verify(mockMinigameService).setScore(1);
    verify(mockEntityService).unregister(mockEntity);
  }

  @Test
  void testBoundsCheckOnScreen() {
    // Position obstacle on screen
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, 400f));
    when(mockEntity.getScale()).thenReturn(new Vector2(50f, 50f));

    obstacleComponent.update();

    // Should still be alive and not trigger score/removal
    assertTrue(obstacleComponent.isAlive());
    verify(mockMinigameService, never()).setScore(anyInt());
    verify(mockEntityService, never()).unregister(any());
  }

  @Test
  void testSetAlive() {
    // Test setAlive method
    obstacleComponent.setAlive(false);
    assertFalse(obstacleComponent.isAlive());

    obstacleComponent.setAlive(true);
    assertTrue(obstacleComponent.isAlive());
  }

  @Test
  void testUpdateWhenNotAlive() {
    // Test that update does nothing when not alive
    obstacleComponent.setAlive(false);

    Vector2 initialPos = new Vector2(640f, 400f);
    when(mockEntity.getPosition()).thenReturn(initialPos);

    obstacleComponent.update();

    // Should not move or check bounds
    verify(mockEntity, never()).setPosition(anyFloat(), anyFloat());
    verify(mockMinigameService, never()).setScore(anyInt());
    verify(mockEntityService, never()).unregister(any());
  }

  @Test
  void testMultipleUpdates() {
    // Test multiple update calls
    Vector2 pos = new Vector2(640f, 400f);
    when(mockEntity.getPosition()).thenReturn(pos);

    // Update multiple times
    obstacleComponent.update();
    obstacleComponent.update();
    obstacleComponent.update();

    // Should call setPosition multiple times
    verify(mockEntity, atLeast(3)).setPosition(anyFloat(), anyFloat());
  }

  @Test
  void testGetSpeed() {
    // Test getSpeed method
    assertEquals(BASE_SPEED, obstacleComponent.getSpeed());
  }

  @Test
  void testIsAlive() {
    // Test isAlive method
    assertTrue(obstacleComponent.isAlive());

    obstacleComponent.setAlive(false);
    assertFalse(obstacleComponent.isAlive());
  }

  @Test
  void testConstructorWithDifferentSpeed() {
    // Test constructor with different base speed
    float customSpeed = 150f;
    LaneRunnerObstacleComponent customObstacle = new LaneRunnerObstacleComponent(customSpeed);

    assertEquals(customSpeed, customObstacle.getSpeed());
    assertTrue(customObstacle.isAlive());
  }
}
