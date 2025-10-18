package com.csse3200.game.ui.terminal.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WaveService;
import java.util.ArrayList;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class SpawnNextRobotTest {
  private SpawnNextRobot spawnNextRobot;
  private WaveService mockWaveService;
  private ArrayList<String> args;
  private static final int DEFAULT_LANE = 0;

  @BeforeEach
  void setUp() {
    spawnNextRobot = new SpawnNextRobot();
    args = new ArrayList<>();
    mockWaveService = mock(WaveService.class);
    ServiceLocator.registerWaveService(mockWaveService);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void shouldFailWhenWaveServiceNull() {
    // Un-registers the wave service to test it being null
    ServiceLocator.deregisterWaveService();

    args.add("standard");

    boolean result = spawnNextRobot.action(args);

    assertFalse(result, "Expected command to fail when wave service is missing");
    verifyNoInteractions(mockWaveService);
  }

  @Test
  void shouldSpawnDefaultLaneWhenNoArgs() {
    boolean result = spawnNextRobot.action(args);

    assertTrue(result, "Expected command to succeed, using default lane");
    verify(mockWaveService).spawnNextEnemy(DEFAULT_LANE);
  }

  @Test
  void shouldSpawnSpecifiedLaneWhenValidLaneProvided() {
    args.add("3");

    boolean result = spawnNextRobot.action(args);

    assertTrue(result, "Expected command to succeed with valid args");
    verify(mockWaveService).spawnNextEnemy(3);
  }

  @Test
  void shouldSpawnDefaultLaneWhenInvalidLaneProvided() {
    args.add("this most certainly isn't a number");

    boolean result = spawnNextRobot.action(args);

    assertTrue(result, "Expected command to succeed, using default lane");
    verify(mockWaveService).spawnNextEnemy(DEFAULT_LANE);
  }

  @Test
  void shouldIgnoreExtraArgs() {
    args.add("4");
    args.add("3");
    args.add("2");
    args.add("1");
    args.add("negative zero");
    args.add("tanky");

    boolean result = spawnNextRobot.action(args);

    assertTrue(result, "Expected command to succeed with valid args");
    verify(mockWaveService).spawnNextEnemy(4);
  }
}
