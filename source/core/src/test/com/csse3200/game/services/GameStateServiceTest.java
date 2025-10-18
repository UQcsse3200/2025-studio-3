package com.csse3200.game.services;

import static com.csse3200.game.services.GameStateService.FreezeReason.INTRO_PAN;
import static com.csse3200.game.services.GameStateService.FreezeReason.USER_PAUSE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(GameExtension.class)
class GameStateServiceTest {
  private Timer timer;
  private GameTime timeSource;
  private GameStateService service;

  @BeforeEach
  void setUp() {
    timer = Mockito.mock(Timer.class);
    timeSource = new GameTime();
    timeSource.setTimeScale(2f);
    service = new GameStateService(timeSource, timer);
  }

  @Test
  void freezesOnFirstReason() {
    service.addFreezeReason(USER_PAUSE);

    assertTrue(service.isFrozen());
    assertEquals(0f, timeSource.getTimeScale());
    verify(timer).stop();
  }

  @Test
  void repeatedAddDoesNotDoubleStopTimer() {
    service.addFreezeReason(USER_PAUSE);
    service.addFreezeReason(USER_PAUSE);

    verify(timer, times(1)).stop();
    assertEquals(1, service.getActiveReasons().size());
  }

  @Test
  void unfreezesAfterLastReasonRemoved() {
    service.addFreezeReason(USER_PAUSE);
    service.addFreezeReason(INTRO_PAN);
    service.removeFreezeReason(USER_PAUSE);

    // Still frozen because one reason remains
    assertTrue(service.isFrozen());
    verify(timer, never()).start();

    service.removeFreezeReason(INTRO_PAN);

    assertFalse(service.isFrozen());
    assertEquals(2f, timeSource.getTimeScale());
    verify(timer).start();
  }

  @Test
  void lockAndUnlockPlacement() {
    assertFalse(service.isPlacementLocked());
    service.lockPlacement();
    assertTrue(service.isPlacementLocked());
    service.unlockPlacement();
    assertFalse(service.isPlacementLocked());
  }

  @Test
  void removingMissingReasonDoesNothing() {
    service.removeFreezeReason(USER_PAUSE);
    assertFalse(service.isFrozen());
    verify(timer, never()).stop();
    verify(timer, never()).start();
  }

  @Test
  void nullReasonRejected() {
    assertThrows(NullPointerException.class, () -> service.addFreezeReason(null));
    assertThrows(NullPointerException.class, () -> service.removeFreezeReason(null));
  }
}
