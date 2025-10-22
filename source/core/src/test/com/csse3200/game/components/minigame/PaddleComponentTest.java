package com.csse3200.game.components.minigame;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
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
class PaddleComponentTest {
  @Mock private GameTime mockTimeSource;
  @Mock private SettingsService mockSettingsService;
  @Mock private Entity mockEntity;

  private PaddleComponent paddleComponent;

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

    // Create paddle component
    paddleComponent = new PaddleComponent();

    // Setup entity mock
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, 50f));
    when(mockEntity.getScale()).thenReturn(new Vector2(100f, 30f));
    doNothing().when(mockEntity).setPosition(anyFloat(), anyFloat());

    // Attach component to entity
    paddleComponent.setEntity(mockEntity);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void testInitialization() {
    // Test basic component creation
    PaddleComponent component = new PaddleComponent();
    assertNotNull(component);
  }

  @Test
  void testMoveLeft() {
    // Test moving left
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(false); // Input.Keys.D

    paddleComponent.update();

    // Calculate expected position: currentX - speed * delta
    float expectedX = 640f - 500f * 0.016f;
    verify(mockEntity).setPosition(expectedX, 50f);
  }

  @Test
  void testMoveRight() {
    // Test moving right
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D

    paddleComponent.update();

    // Calculate expected position: currentX + speed * delta
    float expectedX = 640f + 500f * 0.016f;
    verify(mockEntity).setPosition(expectedX, 50f);
  }

  @Test
  void testMoveLeftWithBoundary() {
    // Test moving left with boundary constraint
    when(mockEntity.getPosition()).thenReturn(new Vector2(10f, 50f)); // Near left boundary
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(false); // Input.Keys.D

    paddleComponent.update();

    // Should move left by speed * delta, but clamp to 0
    float expectedX = Math.max(0f, 10f - 500f * 0.016f); // 10f - 8f = 2f
    verify(mockEntity).setPosition(expectedX, 50f);
  }

  @Test
  void testMoveRightWithBoundary() {
    // Test moving right with boundary constraint
    when(mockEntity.getPosition()).thenReturn(new Vector2(1200f, 50f)); // Near right boundary
    when(mockEntity.getScale()).thenReturn(new Vector2(100f, 30f));
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D

    paddleComponent.update();

    // Should clamp to worldWidth - paddleScale.x (1280 - 100 = 1180)
    verify(mockEntity).setPosition(1180f, 50f);
  }

  @Test
  void testNoInputMovement() {
    // Test that no input doesn't change position
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(false); // Input.Keys.D

    paddleComponent.update();

    // Should not call setPosition
    verify(mockEntity, never()).setPosition(anyFloat(), anyFloat());
  }

  @Test
  void testBothKeysPressed() {
    // Test when both keys are pressed
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D

    paddleComponent.update();

    // Should move left (A key takes precedence in the code)
    float expectedX = 640f - 500f * 0.016f;
    verify(mockEntity).setPosition(expectedX, 50f);
  }

  @Test
  void testMultipleUpdates() {
    // Test multiple update calls
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A

    paddleComponent.update();
    paddleComponent.update();
    paddleComponent.update();

    // Should call setPosition multiple times
    verify(mockEntity, atLeast(3)).setPosition(anyFloat(), anyFloat());
  }

  @Test
  void testDeltaTimeUsage() {
    // Test that delta time is used in movement calculation
    when(mockTimeSource.getDeltaTime()).thenReturn(0.1f); // Different delta time
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(false); // Input.Keys.D

    paddleComponent.update();

    // Should use the different delta time in calculation
    float expectedX = 640f - 500f * 0.1f;
    verify(mockEntity).setPosition(expectedX, 50f);
  }

  @Test
  void testInputKeyMapping() {
    // Test that correct input keys are used
    when(mockSettingsService.getSettings().getLeftButton()).thenReturn(Input.Keys.LEFT);
    when(mockSettingsService.getSettings().getRightButton()).thenReturn(Input.Keys.RIGHT);

    when(Gdx.input.isKeyPressed(Input.Keys.LEFT)).thenReturn(true);
    when(Gdx.input.isKeyPressed(Input.Keys.RIGHT)).thenReturn(false);

    paddleComponent.update();

    // Should move left using LEFT key
    float expectedX = 640f - 500f * 0.016f;
    verify(mockEntity).setPosition(expectedX, 50f);
  }

  @Test
  void testBoundaryConstraints() {
    // Test left boundary constraint
    when(mockEntity.getPosition()).thenReturn(new Vector2(0f, 50f));
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(false); // Input.Keys.D

    paddleComponent.update();

    // Should stay at 0
    verify(mockEntity).setPosition(0f, 50f);

    // Test right boundary constraint
    when(mockEntity.getPosition()).thenReturn(new Vector2(1180f, 50f)); // 1280 - 100
    when(mockEntity.getScale()).thenReturn(new Vector2(100f, 30f));
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D

    paddleComponent.update();

    // Should stay at 1180
    verify(mockEntity).setPosition(1180f, 50f);
  }

  @Test
  void testPositionCalculation() {
    // Test position calculation with different starting positions
    when(mockEntity.getPosition()).thenReturn(new Vector2(320f, 50f));
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A

    paddleComponent.update();

    // Should move right from 320f
    float expectedX = 320f + 500f * 0.016f;
    verify(mockEntity).setPosition(expectedX, 50f);
  }

  @Test
  void testYPositionPreservation() {
    // Test that Y position is preserved during movement
    when(mockEntity.getPosition()).thenReturn(new Vector2(640f, 100f));
    when(Gdx.input.isKeyPressed(29)).thenReturn(true); // Input.Keys.A
    when(Gdx.input.isKeyPressed(32)).thenReturn(false); // Input.Keys.D

    paddleComponent.update();

    // Y position should remain unchanged
    float expectedX = 640f - 500f * 0.016f;
    verify(mockEntity).setPosition(expectedX, 100f);
  }

  @Test
  void testSpeedConstant() {
    // Test that speed is constant (500f)
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A

    paddleComponent.update();

    // Should use speed of 500f
    float expectedX = 640f + 500f * 0.016f;
    verify(mockEntity).setPosition(expectedX, 50f);
  }

  @Test
  void testWorldWidthBoundary() {
    // Test right boundary with world width of 1280f
    when(mockEntity.getPosition()).thenReturn(new Vector2(1000f, 50f));
    when(mockEntity.getScale()).thenReturn(new Vector2(100f, 30f));
    when(Gdx.input.isKeyPressed(32)).thenReturn(true); // Input.Keys.D
    when(Gdx.input.isKeyPressed(29)).thenReturn(false); // Input.Keys.A

    paddleComponent.update();

    // Should move right by speed * delta
    float expectedX = 1000f + 500f * 0.016f; // 1000f + 8f = 1008f
    verify(mockEntity).setPosition(expectedX, 50f);
  }
}
