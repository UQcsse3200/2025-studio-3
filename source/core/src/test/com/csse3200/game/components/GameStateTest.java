package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.services.GameStateService;
import com.csse3200.game.services.GameTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameStateServiceTest {

  private GameTime mockTime;
  private Timer mockTimer;
  private GameStateService service;

  @BeforeEach
  void setup() {
    mockTime = mock(GameTime.class);
    when(mockTime.getTimeScale()).thenReturn(1f);

    mockTimer = mock(Timer.class);

    service = new GameStateService(mockTime, mockTimer);
  }

  @Test
  void testInitialState() {
    assertFalse(service.isFrozen());
    assertFalse(service.isPlacementLocked());
    assertTrue(service.getActiveReasons().isEmpty());
  }

  @Test
  void testAddFreezeReasonFreezesGame() {
    service.addFreezeReason(GameStateService.FreezeReason.USER_PAUSE);

    assertTrue(service.isFrozen());
    assertEquals(Set.of(GameStateService.FreezeReason.USER_PAUSE), service.getActiveReasons());
    verify(mockTime).setTimeScale(0f);
    verify(mockTimer).stop();
  }

  @Test
  void testAddSameFreezeReasonTwiceDoesNothing() {
    service.addFreezeReason(GameStateService.FreezeReason.USER_PAUSE);
    service.addFreezeReason(GameStateService.FreezeReason.USER_PAUSE);

    assertEquals(1, service.getActiveReasons().size());
    verify(mockTime, times(1)).setTimeScale(0f);
  }

  @Test
  void testRemoveFreezeReasonUnfreezesGame() {
    service.addFreezeReason(GameStateService.FreezeReason.USER_PAUSE);
    service.removeFreezeReason(GameStateService.FreezeReason.USER_PAUSE);

    assertFalse(service.isFrozen());
    assertTrue(service.getActiveReasons().isEmpty());
    verify(mockTime).setTimeScale(0f);
    verify(mockTimer).stop();
    verify(mockTimer).start();
  }

  @Test
  void testRemoveInactiveReasonDoesNothing() {
    service.removeFreezeReason(GameStateService.FreezeReason.USER_PAUSE);

    assertFalse(service.isFrozen());
    verify(mockTime, never()).setTimeScale(0f);
    verify(mockTimer, never()).stop();
  }

  @Test
  void testPlacementLock() {
    service.lockPlacement();
    assertTrue(service.isPlacementLocked());

    service.unlockPlacement();
    assertFalse(service.isPlacementLocked());
  }

  @Test
  void testRegisterAndNotifyListener() {
    GameStateService.FreezeListener listener = mock(GameStateService.FreezeListener.class);
    service.registerFreezeListener(listener);

    service.addFreezeReason(GameStateService.FreezeReason.USER_PAUSE);
    verify(listener).onFreezeStateChanged(true);

    service.removeFreezeReason(GameStateService.FreezeReason.USER_PAUSE);
    verify(listener).onFreezeStateChanged(false);
  }

  @Test
  void testUnregisterListener() {
    GameStateService.FreezeListener listener = mock(GameStateService.FreezeListener.class);
    service.registerFreezeListener(listener);
    service.unregisterFreezeListener(listener);

    service.addFreezeReason(GameStateService.FreezeReason.USER_PAUSE);
    verify(listener, never()).onFreezeStateChanged(true);
  }

  @Test
  void testSetPreferredTimeScale() {
    service.setPreferredTimeScale(2f);
    verify(mockTime).setTimeScale(2f);

    service.addFreezeReason(GameStateService.FreezeReason.USER_PAUSE);
    service.setPreferredTimeScale(0.5f); // should only update cachedTimeScale
    verify(mockTime, times(1)).setTimeScale(0f); // time scale frozen
  }

  @Test
  void testSetPreferredTimeScaleClampsNegative() {
    service.setPreferredTimeScale(-5f);
    verify(mockTime).setTimeScale(0f);
  }
}
