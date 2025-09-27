package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.jcm.discordgamesdk.*;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityAssets;
import de.jcm.discordgamesdk.activity.ActivityTimestamps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.ConnectException;

@ExtendWith(MockitoExtension.class)
class DiscordRichPresenceServiceTest {
  @Mock Core mockCore;
  @Mock ActivityManager mockActivityManager;
  @Mock CreateParams mockCreateParams;  
  @Mock Activity mockActivity;
  private DiscordRichPresenceService service;

  @BeforeEach
  void setUp() {
    service = new DiscordRichPresenceService();
    service.createParamsSupplier = () -> mockCreateParams;
    service.coreFactory = p -> mockCore;
    service.activitySupplier = () -> mockActivity;
    
    lenient().when(mockActivity.assets()).thenReturn(mock(ActivityAssets.class));
    lenient().when(mockActivity.timestamps()).thenReturn(mock(ActivityTimestamps.class));
  }

  @Test
  void testInitializationSuccess() {
      when(mockCore.activityManager()).thenReturn(mockActivityManager);

      service.initialize();
      assertTrue(service.isInitialized());
      verify(mockCreateParams).setClientID(1421050206404739225L);
      verify(mockCreateParams).setFlags(CreateParams.getDefaultFlags());
      verify(mockCore).activityManager();
  }

  @Test
  void testInitializationFailure() {
    service.createParamsSupplier = () -> {
      throw new RuntimeException("Discord not available");
    };

    service.initialize();

    assertFalse(service.isInitialized());
  }

  @Test
  void testInitializationWithConnectException() {
    ConnectException connectException = new ConnectException("Connection refused");
    RuntimeException wrappedException = new RuntimeException(connectException);
    service.createParamsSupplier = () -> {
      throw wrappedException;
    };

    service.initialize();

    assertFalse(service.isInitialized());
  }

  @Test
  void testSetPresenceWhenNotInitialized() {
    service.setPresence("Test State");
    // Should not throw exception, just log error
    assertFalse(service.isInitialized());
  }

  @Test
  void testSetPresenceWithState() {
    // Initialize service with mocked dependencies
    service.isInitialized = true;
    service.activityManager = mockActivityManager;
    service.startTime = System.currentTimeMillis() / 1000;

    service.setPresence("Test State");

    verify(mockActivity).setDetails("Playing The Day We Fought Back");
    verify(mockActivity).setState("Test State");
    verify(mockActivityManager).updateActivity(eq(mockActivity), any());
  }

  @Test
  void testSetPresenceWithNullState() {
    service.isInitialized = true;
    service.activityManager = mockActivityManager;
    service.startTime = System.currentTimeMillis() / 1000;

    service.setPresence(null);

    verify(mockActivity).setState(null);
    verify(mockActivity).setDetails("Playing The Day We Fought Back");
  }

  @Test
  void testSetPresenceWithEmptyState() {
    service.isInitialized = true;
    service.activityManager = mockActivityManager;
    service.startTime = System.currentTimeMillis() / 1000;

    service.setPresence("");

    verify(mockActivity).setState("");
    verify(mockActivity).setDetails("Playing The Day We Fought Back");
  }

  @Test
  void testUpdateGamePresenceWithLevelAndWave() {
    service.isInitialized = true;
    service.activityManager = mockActivityManager;
    service.startTime = System.currentTimeMillis() / 1000;

    service.updateGamePresence("Level 1", 3);

    verify(mockActivity).setState("Level: Level 1 | Wave: 3");
    verify(mockActivity).setDetails("Playing The Day We Fought Back");
  }

  @Test
  void testUpdateGamePresenceWithLevelOnly() {
    service.isInitialized = true;
    service.activityManager = mockActivityManager;
    service.startTime = System.currentTimeMillis() / 1000;

    service.updateGamePresence("Level 2", 0);

    verify(mockActivity).setState("Level: Level 2");

    service.updateGamePresence(null, 5);

    verify(mockActivity).setState("Wave: 5");

    service.updateGamePresence(null, 0);

    verify(mockActivity, atLeastOnce()).setState("");

    service.updateGamePresence("Level 3", 10);

    verify(mockActivity).setState("Level: Level 3 | Wave: 10");

    service.updateGamePresence(null, 0);

    verify(mockActivity, atLeastOnce()).setState("");
  }

  @Test
  void testUpdateGamePresenceWhenNotInitialized() {
    service.updateGamePresence("Level 1", 1);
    // Should not throw exception, just return early
    assertFalse(service.isInitialized());
  }

  @Test
  void testShutdownWhenNotInitialized() {
    service.shutdown();
    // Should not throw exception
    assertFalse(service.isInitialized());
  }

  @Test
  void testShutdownWhenInitialized() {
    service.isInitialized = true;
    service.core = mockCore;

    service.shutdown();

    verify(mockCore).close();
    assertFalse(service.isInitialized());
  }

  @Test
  void testShutdownWithException() {
    service.isInitialized = true;
    service.core = mockCore;
    
    doThrow(new RuntimeException("Close failed")).when(mockCore).close();

    // Should not throw exception, just log error
    assertDoesNotThrow(() -> service.shutdown());
    assertFalse(service.isInitialized());
  }

  @Test
  void testRunCallbacksWhenNotInitialized() {
    service.runCallbacks();
    // Should not throw exception
    assertFalse(service.isInitialized());
  }

  @Test
  void testRunCallbacksWhenInitialized() {
    service.isInitialized = true;
    service.core = mockCore;

    service.runCallbacks();

    verify(mockCore).runCallbacks();
  }

  @Test
  void testRunCallbacksWhenCoreIsNull() {
    service.isInitialized = true;
    service.core = null;

    // Should not throw exception
    assertDoesNotThrow(() -> service.runCallbacks());
  }

  @Test
  void testIsInitialized() {
    assertFalse(service.isInitialized());
    
    when(mockCore.activityManager()).thenReturn(mockActivityManager);
    service.createParamsSupplier = () -> mockCreateParams;
    service.coreFactory = p -> mockCore;

    service.initialize();
    assertTrue(service.isInitialized());
  }

  @Test
  void testSetPresenceWithException() {
    service.isInitialized = true;
    service.activityManager = mockActivityManager;
    
    // Mock the activity supplier to throw an exception
    service.activitySupplier = () -> {
      throw new RuntimeException("Activity creation failed");
    };

    // Should not throw exception, just log error
    assertDoesNotThrow(() -> service.setPresence("Test State"));
  }

  @Test
  void testUpdateGamePresenceWithSpecialCharacters() {
    service.isInitialized = true;
    service.activityManager = mockActivityManager;
    service.startTime = System.currentTimeMillis() / 1000;

    service.updateGamePresence("Level with special chars: !@#$%", 10);

    verify(mockActivity).setState("Level: Level with special chars: !@#$% | Wave: 10");
  }

  @Test
  void testUpdateGamePresenceWithLongStrings() {
    service.isInitialized = true;
    service.activityManager = mockActivityManager;
    service.startTime = System.currentTimeMillis() / 1000;

    String longLevel = "Very long level name that might exceed normal limits and should still be handled gracefully by the Discord Rich Presence service";
    service.updateGamePresence(longLevel, 999);

    verify(mockActivity).setState("Level: " + longLevel + " | Wave: 999");
  }

  @Test
  void testSetPresenceWithLongState() {
    service.isInitialized = true;
    service.activityManager = mockActivityManager;
    service.startTime = System.currentTimeMillis() / 1000;

    String longState = "Very long state string that might exceed normal limits and should still be handled gracefully by the Discord Rich Presence service";
    service.setPresence(longState);

    verify(mockActivity).setState(longState);
  }

}