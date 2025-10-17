package com.csse3200.game.ui.terminal.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.entities.factories.RobotFactory.RobotType;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WaveService;
import java.util.ArrayList;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
public class SpawnRobotTest {
  private SpawnRobot spawnRobot;
  private WaveService mockWaveService;
  private ArrayList<String> args;
  private final int DEFAULT_LANE = 0;

  @BeforeEach
  void setUp() {
    spawnRobot = new SpawnRobot();
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

    boolean result = spawnRobot.action(args);

    assertFalse(result, "Expected command to fail when wave service is missing");
    verifyNoInteractions(mockWaveService);
  }

  @Test
  void shouldFailWhenNoArgs() {
    boolean result = spawnRobot.action(args);

    assertFalse(result, "Expected command to fail when no arguments were given");
    verifyNoInteractions(mockWaveService);
  }

  @Test
  void shouldSpawnDefaultLaneWhenOnlyTypeProvided() {
    ArrayList<String> args = new ArrayList<>();
    args.add("tanky");

    boolean result = spawnRobot.action(args);

    assertTrue(result, "Expected command to succeed, using default lane");
    verify(mockWaveService).spawnEnemyDebug(DEFAULT_LANE, RobotType.TANKY);
  }

  @Test
  void shouldSpawnSpecifiedLaneWhenValidLaneProvided() {
    ArrayList<String> args = new ArrayList<>();
    args.add("fast");
    args.add("3");

    boolean result = spawnRobot.action(args);

    assertTrue(result, "Expected command to succeed with valid args");
    verify(mockWaveService).spawnEnemyDebug(3, RobotType.FAST);
  }

  @Test
  void shouldSpawnDefaultLaneWhenInvalidLaneProvided() {
    ArrayList<String> args = new ArrayList<>();
    args.add("fast");
    args.add("this most certainly isn't a number");

    boolean result = spawnRobot.action(args);

    assertTrue(result, "Expected command to succeed, using default lane");
    verify(mockWaveService).spawnEnemyDebug(DEFAULT_LANE, RobotType.FAST);
  }

  @Test
  void shouldUseStandardRobotWhenTypeInvalid() {
    ArrayList<String> args = new ArrayList<>();
    args.add("this is not a valid robot type");
    args.add("this most certainly isn't a number");

    boolean result = spawnRobot.action(args);

    assertTrue(result, "Expected command to succeed, using default lane and robot type");
    verify(mockWaveService).spawnEnemyDebug(DEFAULT_LANE, RobotType.STANDARD);
  }

  @Test
  void shouldIgnoreExtraArgs() {
    ArrayList<String> args = new ArrayList<>();
    args.add("bungee");
    args.add("4");
    args.add("3");
    args.add("2");
    args.add("1");
    args.add("negative zero");
    args.add("tanky");

    boolean result = spawnRobot.action(args);

    assertTrue(result, "Expected command to succeed with valid args");
    verify(mockWaveService).spawnEnemyDebug(4, RobotType.BUNGEE);
  }
}
