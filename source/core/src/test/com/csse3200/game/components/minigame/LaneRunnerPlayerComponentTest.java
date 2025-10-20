package com.csse3200.game.components.minigame;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.screens.LaneRunnerScreen;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
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
class LaneRunnerPlayerComponentTest {
  @Mock private GameTime mockTimeSource;
  @Mock private SettingsService mockSettingsService;
  @Mock private Entity mockEntity;

  private LaneRunnerPlayerComponent playerComponent;

  @BeforeEach
  void setUp() {
    Gdx.input = mock(Input.class);

    // Setup service mocks
    ServiceLocator.registerTimeSource(mockTimeSource);
    ServiceLocator.registerSettingsService(mockSettingsService);

    // Setup time source mock
    when(mockTimeSource.getDeltaTime()).thenReturn(0.016f); // 60 FPS

    // Setup settings service mock
    com.csse3200.game.persistence.Settings mockSettings =
        mock(com.csse3200.game.persistence.Settings.class);
    when(mockSettingsService.getSettings()).thenReturn(mockSettings);
    when(mockSettings.getLeftButton()).thenReturn(Input.Keys.A);
    when(mockSettings.getRightButton()).thenReturn(Input.Keys.D);

    // Create player component
    playerComponent = new LaneRunnerPlayerComponent();

    // Setup entity mock
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, 100f));
    doNothing().when(mockEntity).setPosition(anyFloat(), anyFloat());

    // Attach component to entity
    playerComponent.setEntity(mockEntity);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void testInitialization() {
    // Test initial values
    assertEquals(1, playerComponent.getCurrentLane());
  }

  @Test
  void testMove() {
    // Test moving left from lane 1 to lane 0
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A

    playerComponent.update();

    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D

    playerComponent.update();

    assertEquals(1, playerComponent.getCurrentLane());
  }

  @Test
  void testMoveLeftFromLane0() {
    // Test that moving left from lane 0 doesn't go below 0
    playerComponent = new LaneRunnerPlayerComponent();
    playerComponent.setEntity(mockEntity);

    // Set to lane 0
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    playerComponent.update();
    assertEquals(0, playerComponent.getCurrentLane());

    // Try to move left again
    playerComponent.update();
    assertEquals(0, playerComponent.getCurrentLane()); // Should stay at 0
  }

  @Test
  void testMoveRightFromLane2() {
    // Test that moving right from lane 2 doesn't go above max
    playerComponent = new LaneRunnerPlayerComponent();
    playerComponent.setEntity(mockEntity);

    // Set to lane 2
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D
    playerComponent.update();
    assertEquals(2, playerComponent.getCurrentLane());

    // Try to move right again
    playerComponent.update();
    assertEquals(2, playerComponent.getCurrentLane()); // Should stay at 2
  }

  @Test
  void testPositionUpdate() {
    // Test position update based on current lane
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D

    playerComponent.update();

    // Should move to lane 2, verify that setPosition was called
    verify(mockEntity).setPosition(anyFloat(), anyFloat());
  }

  @Test
  void testSmoothMovement() {
    // Test smooth movement towards target lane
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D

    // Set initial position
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, 100f));

    playerComponent.update();

    // Should calculate smooth movement
    float targetX = LaneRunnerScreen.LANE_CENTER + 2 * LaneRunnerScreen.LANE_WIDTH - 32f;
    float currentX = 640f;
    float expectedX = currentX + (targetX - currentX) * 10f * 0.016f; // MOVE_SPEED * delta

    verify(mockEntity).setPosition(expectedX, 100f);
  }

  @Test
  void testNoInputMovement() {
    // Test that no input doesn't change lane
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(false); // Input.Keys.D

    int initialLane = playerComponent.getCurrentLane();
    playerComponent.update();

    assertEquals(initialLane, playerComponent.getCurrentLane());
  }

  @Test
  void testKeyPressHandling() {
    // Test that key press is handled correctly
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A

    playerComponent.update();

    assertEquals(0, playerComponent.getCurrentLane());
  }

  @Test
  void testMultipleUpdates() {
    // Test multiple update calls
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D

    playerComponent.update();
    playerComponent.update();
    playerComponent.update();

    // Should still be in lane 2 (can't go further right)
    assertEquals(2, playerComponent.getCurrentLane());
  }

  @Test
  void testGetCurrentLane() {
    // Test getCurrentLane method
    assertEquals(1, playerComponent.getCurrentLane());

    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    playerComponent.update();
    assertEquals(0, playerComponent.getCurrentLane());
  }

  @Test
  void testLaneBoundaries() {
    // Test lane boundaries
    assertEquals(1, playerComponent.getCurrentLane());

    // Move to leftmost lane
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    playerComponent.update();
    assertEquals(0, playerComponent.getCurrentLane());

    // Move to rightmost lane (lane 2)
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D
    playerComponent.update(); // Move to lane 1

    // Simulate key release and press again for lane 2
    when(Gdx.input.isKeyPressed(32)).thenReturn(false); // Release
    playerComponent.update(); // Process release
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Press again
    playerComponent.update(); // Move to lane 2

    assertEquals(2, playerComponent.getCurrentLane());
  }

  @Test
  void testPositionCalculation() {
    // Test position calculation for each lane
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    playerComponent.update();

    // Lane 0 position - verify that setPosition was called
    verify(mockEntity).setPosition(anyFloat(), anyFloat());

    // Move to lane 1
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D
    playerComponent.update();

    // Lane 1 position - verify that setPosition was called again
    verify(mockEntity, atLeast(2)).setPosition(anyFloat(), anyFloat());
  }

  @Test
  void testInputKeyMapping() {
    // Test that correct input keys are used
    when(mockSettingsService.getSettings().getLeftButton()).thenReturn(Input.Keys.LEFT);
    when(mockSettingsService.getSettings().getRightButton()).thenReturn(Input.Keys.RIGHT);

    when(Gdx.input.isKeyPressed(Input.Keys.LEFT)).thenReturn(true);
    when(Gdx.input.isKeyPressed(Input.Keys.RIGHT)).thenReturn(false);

    playerComponent.update();

    assertEquals(0, playerComponent.getCurrentLane());
  }

  @Test
  void testDeltaTimeUsage() {
    // Test that delta time is used in position calculation
    when(mockTimeSource.getDeltaTime()).thenReturn(0.1f); // Different delta time
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D

    playerComponent.update();

    // Should use the different delta time in calculation
    float targetX = LaneRunnerScreen.LANE_CENTER + 2 * LaneRunnerScreen.LANE_WIDTH - 32f;
    float currentX = 640f;
    float expectedX = currentX + (targetX - currentX) * 10f * 0.1f; // MOVE_SPEED * delta

    verify(mockEntity).setPosition(eq(expectedX), anyFloat());
  }
}
