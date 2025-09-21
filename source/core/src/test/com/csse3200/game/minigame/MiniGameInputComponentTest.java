package com.csse3200.game.minigame;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Input;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.events.EventHandler;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class MiniGameInputComponentTest {

  private Entity mockEntity;
  private EventHandler mockEventHandler;

  @BeforeEach
  void setup() {
    mockEntity = mock(Entity.class);
    mockEventHandler = mock(EventHandler.class);
    when(mockEntity.getEvents()).thenReturn(mockEventHandler);
  }

  @Test
  void testMoveLeftKeyDuringGameplay() {
    MiniGameInputComponent input = new MiniGameInputComponent(false);
    input.setEntity(mockEntity);

    boolean handled = input.keyDown(Input.Keys.LEFT);

    assertTrue(handled, "LEFT should be handled in gameplay mode");
    verify(mockEventHandler).trigger("moveLeft");
  }

  @Test
  void testMoveRightKeyDuringGameplay() {
    MiniGameInputComponent input = new MiniGameInputComponent(false);
    input.setEntity(mockEntity);

    boolean handled = input.keyDown(Input.Keys.D);

    assertTrue(handled, "D should be handled in gameplay mode");
    verify(mockEventHandler).trigger("moveRight");
  }

  @Test
  void testMoveKeysIgnoredInGameOverScreen() {
    MiniGameInputComponent input = new MiniGameInputComponent(true);
    input.setEntity(mockEntity);

    boolean leftHandled = input.keyDown(Input.Keys.A);
    boolean rightHandled = input.keyDown(Input.Keys.RIGHT);

    assertFalse(leftHandled, "LEFT/A should not be handled in game over mode");
    assertFalse(rightHandled, "RIGHT/D should not be handled in game over mode");

    verify(mockEventHandler, never()).trigger("moveRight");
    verify(mockEventHandler, never()).trigger("moveLeft");
  }

  @Test
  void testEscapeKeyTriggersReturnToArcadeInGameOverScreen() {
    MiniGameInputComponent input = new MiniGameInputComponent(true);
    input.setEntity(mockEntity);

    boolean handled = input.keyDown(Input.Keys.ESCAPE);

    assertTrue(handled, "ESCAPE should be handled in game over mode");
    verify(mockEventHandler).trigger("returnToArcade");
  }

  @Test
  void testSpaceKeyTriggersPlayAgainInGameOverScreen() {
    MiniGameInputComponent input = new MiniGameInputComponent(true);
    input.setEntity(mockEntity);

    boolean handled = input.keyDown(Input.Keys.SPACE);

    assertTrue(handled, "SPACE should be handled in game over mode");
    verify(mockEventHandler).trigger("playAgain");
  }

  @Test
  void testEscapeKeyIgnoredDuringGameplay() {
    MiniGameInputComponent input = new MiniGameInputComponent(false);
    input.setEntity(mockEntity);

    boolean handled = input.keyDown(Input.Keys.ESCAPE);

    assertFalse(handled, "ESCAPE should not be handled during gameplay");
    verify(mockEventHandler, never()).trigger("returnToArcade");
  }

  @Test
  void testSpaceKeyIgnoredDuringGameplay() {
    MiniGameInputComponent input = new MiniGameInputComponent(false);
    input.setEntity(mockEntity);

    boolean handled = input.keyDown(Input.Keys.SPACE);

    assertFalse(handled, "SPACE should not be handled during gameplay");
    verify(mockEventHandler, never()).trigger("playAgain");
  }

  @Test
  void testOtherKeyReturnFalse() {
    MiniGameInputComponent input = new MiniGameInputComponent(false);
    input.setEntity(mockEntity);

    boolean handled = input.keyDown(Input.Keys.NUM_1);

    assertFalse(handled, "Unmapped keys should return false");
    verifyNoInteractions(mockEventHandler);
  }
}
