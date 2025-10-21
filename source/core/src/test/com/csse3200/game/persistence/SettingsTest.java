package com.csse3200.game.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.Input;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.utils.EnvironmentUtils;
import java.util.Arrays;
import net.dermetfan.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(GameExtension.class)
class SettingsTest {
  @Mock Monitor monitorMock;
  @Mock DisplayMode displayModeMock;
  @Mock Monitor primaryMonitorMock;
  @Mock DeserializedSettings deserializedSettingsMock;

  @BeforeEach
  void setUp() {
    Graphics graphicsMock = mock(Graphics.class);
    Gdx.graphics = graphicsMock;
    Monitor[] monitors = new Monitor[] {monitorMock, primaryMonitorMock};
    lenient().when(graphicsMock.getMonitors()).thenReturn(monitors);
    lenient().when(graphicsMock.getPrimaryMonitor()).thenReturn(primaryMonitorMock);
    lenient().when(graphicsMock.getDisplayMode(any(Monitor.class))).thenReturn(displayModeMock);
    lenient().when(graphicsMock.setFullscreenMode(any(DisplayMode.class))).thenReturn(true);
  }

  private void setupDeserializedSettings() {
    when(deserializedSettingsMock.getWindowedResolution()).thenReturn(new Pair<>(1920, 1080));
    when(deserializedSettingsMock.getFps()).thenReturn(60);
    when(deserializedSettingsMock.isVsync()).thenReturn(true);
    when(deserializedSettingsMock.getCurrentUIScale()).thenReturn(Settings.UIScale.MEDIUM);
    when(deserializedSettingsMock.getQuality()).thenReturn(Settings.Quality.HIGH);
    when(deserializedSettingsMock.getCurrentMode()).thenReturn(Settings.Mode.WINDOWED);
    when(deserializedSettingsMock.getMusicVolume()).thenReturn(0.8f);
    when(deserializedSettingsMock.getSoundVolume()).thenReturn(0.7f);
    when(deserializedSettingsMock.getVoiceVolume()).thenReturn(0.9f);
    when(deserializedSettingsMock.getMasterVolume()).thenReturn(0.75f);
    when(deserializedSettingsMock.getDifficulty()).thenReturn(Settings.Difficulty.NORMAL);
    when(deserializedSettingsMock.getPauseButton()).thenReturn(Input.Keys.ESCAPE);
    when(deserializedSettingsMock.getSkipButton()).thenReturn(Input.Keys.SPACE);
    when(deserializedSettingsMock.getInteractionButton()).thenReturn(Input.Keys.E);
    when(deserializedSettingsMock.getUpButton()).thenReturn(Input.Keys.W);
    when(deserializedSettingsMock.getDownButton()).thenReturn(Input.Keys.S);
    when(deserializedSettingsMock.getLeftButton()).thenReturn(Input.Keys.A);
    when(deserializedSettingsMock.getRightButton()).thenReturn(Input.Keys.D);
    when(deserializedSettingsMock.getZoomInButton()).thenReturn(Input.Keys.Z);
    when(deserializedSettingsMock.getZoomOutButton()).thenReturn(Input.Keys.X);
  }

  @Test
  void testDefaultConstructor() {
    try (MockedStatic<EnvironmentUtils> environmentUtilsMock =
        Mockito.mockStatic(EnvironmentUtils.class)) {
      environmentUtilsMock.when(EnvironmentUtils::getOperatingSystem).thenReturn("Windows");
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getResolutionFromDisplayMode(any(Monitor.class)))
          .thenReturn(new Pair<>(1920, 1080));
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getRefreshRateFromDisplayMode(any(Monitor.class)))
          .thenReturn(60);

      Settings settings = new Settings();

      // Test default volume settings
      assertEquals(1.0f, settings.getSoundVolume());
      assertEquals(1.0f, settings.getVoiceVolume());
      assertEquals(1.0f, settings.getMusicVolume());
      assertEquals(1.0f, settings.getMasterVolume());

      // Test default gameplay settings
      assertEquals(Settings.Difficulty.NORMAL, settings.getDifficulty());
      assertEquals(Input.Keys.ESCAPE, settings.getPauseButton());
      assertEquals(Input.Keys.SPACE, settings.getSkipButton());
      assertEquals(Input.Keys.E, settings.getInteractionButton());
      assertEquals(Input.Keys.W, settings.getUpButton());
      assertEquals(Input.Keys.S, settings.getDownButton());
      assertEquals(Input.Keys.A, settings.getLeftButton());
      assertEquals(Input.Keys.D, settings.getRightButton());
      assertEquals(Input.Keys.Z, settings.getZoomInButton());
      assertEquals(Input.Keys.X, settings.getZoomOutButton());

      // Test default display settings
      assertEquals(Settings.Mode.FULLSCREEN, settings.getCurrentMode());
      assertEquals(Settings.UIScale.MEDIUM, settings.getCurrentUIScale());
      assertEquals(Settings.Quality.HIGH, settings.getQuality());
      assertFalse(settings.isVsync());

      // Test that available modes are set correctly
      assertTrue(settings.getAvailableModes().containsKey(Settings.Mode.WINDOWED));
      assertTrue(settings.getAvailableModes().containsKey(Settings.Mode.FULLSCREEN));
      assertTrue(settings.getAvailableModes().containsKey(Settings.Mode.BORDERLESS));
    }
  }

  @Test
  void testDeserializedConstructor() {
    try (MockedStatic<EnvironmentUtils> environmentUtilsMock =
        Mockito.mockStatic(EnvironmentUtils.class)) {
      environmentUtilsMock.when(EnvironmentUtils::getOperatingSystem).thenReturn("Windows");
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getResolutionFromDisplayMode(any(Monitor.class)))
          .thenReturn(new Pair<>(1920, 1080));
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getRefreshRateFromDisplayMode(any(Monitor.class)))
          .thenReturn(60);

      setupDeserializedSettings();

      Settings settings = new Settings(deserializedSettingsMock);

      // Test that deserialized values are loaded correctly
      assertEquals(0.7f, settings.getSoundVolume());
      assertEquals(0.9f, settings.getVoiceVolume());
      assertEquals(0.8f, settings.getMusicVolume());
      assertEquals(0.75f, settings.getMasterVolume());

      assertEquals(Settings.Difficulty.NORMAL, settings.getDifficulty());
      assertEquals(Input.Keys.ESCAPE, settings.getPauseButton());
      assertEquals(Input.Keys.SPACE, settings.getSkipButton());
      assertEquals(Input.Keys.E, settings.getInteractionButton());
      assertEquals(Input.Keys.W, settings.getUpButton());
      assertEquals(Input.Keys.S, settings.getDownButton());
      assertEquals(Input.Keys.A, settings.getLeftButton());
      assertEquals(Input.Keys.D, settings.getRightButton());
      assertEquals(Input.Keys.Z, settings.getZoomInButton());
      assertEquals(Input.Keys.X, settings.getZoomOutButton());

      assertEquals(Settings.Mode.WINDOWED, settings.getCurrentMode());
      assertEquals(Settings.UIScale.MEDIUM, settings.getCurrentUIScale());
      assertEquals(Settings.Quality.HIGH, settings.getQuality());
      assertTrue(settings.isVsync());
      assertEquals(60, settings.getFps());
    }
  }

  @Test
  void testVolumeSettersAndGetters() {
    Settings settings = new Settings();

    // Test sound volume
    settings.setSoundVolume(0.5f);
    assertEquals(0.5f, settings.getSoundVolume());

    // Test voice volume
    settings.setVoiceVolume(0.3f);
    assertEquals(0.3f, settings.getVoiceVolume());

    // Test music volume
    settings.setMusicVolume(0.8f);
    assertEquals(0.8f, settings.getMusicVolume());

    // Test master volume
    settings.setMasterVolume(0.6f);
    assertEquals(0.6f, settings.getMasterVolume());
  }

  @Test
  void testGameplaySettings() {
    Settings settings = new Settings();

    // Test difficulty
    settings.setDifficulty(Settings.Difficulty.EASY);
    assertEquals(Settings.Difficulty.EASY, settings.getDifficulty());

    settings.setDifficulty(Settings.Difficulty.HARD);
    assertEquals(Settings.Difficulty.HARD, settings.getDifficulty());

    // Test button assignments
    settings.setPauseButton(Input.Keys.P);
    assertEquals(Input.Keys.P, settings.getPauseButton());

    settings.setSkipButton(Input.Keys.ENTER);
    assertEquals(Input.Keys.ENTER, settings.getSkipButton());

    settings.setInteractionButton(Input.Keys.F);
    assertEquals(Input.Keys.F, settings.getInteractionButton());

    settings.setUpButton(Input.Keys.UP);
    assertEquals(Input.Keys.UP, settings.getUpButton());

    settings.setDownButton(Input.Keys.DOWN);
    assertEquals(Input.Keys.DOWN, settings.getDownButton());

    settings.setLeftButton(Input.Keys.LEFT);
    assertEquals(Input.Keys.LEFT, settings.getLeftButton());

    settings.setRightButton(Input.Keys.RIGHT);
    assertEquals(Input.Keys.RIGHT, settings.getRightButton());

    settings.setZoomInButton(Input.Keys.W);
    assertEquals(Input.Keys.W, settings.getZoomInButton());

    settings.setZoomOutButton(Input.Keys.Y);
    assertEquals(Input.Keys.Y, settings.getZoomOutButton());
  }

  @Test
  void testGamePlaySettingsResetKeyBindsResetsToDefaults() {
    Settings settings = new Settings();

    // Change everything away from defaults
    settings.setDifficulty(Settings.Difficulty.HARD);
    settings.setPauseButton(Input.Keys.P);
    settings.setSkipButton(Input.Keys.ENTER);
    settings.setInteractionButton(Input.Keys.F);
    settings.setUpButton(Input.Keys.UP);
    settings.setDownButton(Input.Keys.DOWN);
    settings.setLeftButton(Input.Keys.LEFT);
    settings.setRightButton(Input.Keys.RIGHT);
    settings.setZoomInButton(Input.Keys.W);
    settings.setZoomOutButton(Input.Keys.Y);

    // Reset and assert defaults
    settings.resetKeyBinds();

    // Difficulty should not reset
    assertEquals(Settings.Difficulty.HARD, settings.getDifficulty());

    // Keybinds should reset to defaults (ESCAPE, SPACE, E, W, S, A, D)
    assertEquals(Input.Keys.ESCAPE, settings.getPauseButton());
    assertEquals(Input.Keys.SPACE, settings.getSkipButton());
    assertEquals(Input.Keys.E, settings.getInteractionButton());
    assertEquals(Input.Keys.W, settings.getUpButton());
    assertEquals(Input.Keys.S, settings.getDownButton());
    assertEquals(Input.Keys.A, settings.getLeftButton());
    assertEquals(Input.Keys.D, settings.getRightButton());
    assertEquals(Input.Keys.Z, settings.getZoomInButton());
    assertEquals(Input.Keys.X, settings.getZoomOutButton());
  }

  @Test
  void testDisplaySettings() {
    try (MockedStatic<EnvironmentUtils> environmentUtilsMock =
        Mockito.mockStatic(EnvironmentUtils.class)) {
      environmentUtilsMock.when(EnvironmentUtils::getOperatingSystem).thenReturn("Windows");
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getResolutionFromDisplayMode(any(Monitor.class)))
          .thenReturn(new Pair<>(1920, 1080));
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getRefreshRateFromDisplayMode(any(Monitor.class)))
          .thenReturn(60);

      Settings settings = new Settings();

      // Test mode setting
      settings.setCurrentMode(Settings.Mode.WINDOWED);
      assertEquals(Settings.Mode.WINDOWED, settings.getCurrentMode());

      settings.setCurrentMode(Settings.Mode.BORDERLESS);
      assertEquals(Settings.Mode.BORDERLESS, settings.getCurrentMode());

      // Test UI scale
      settings.setCurrentUIScale(Settings.UIScale.SMALL);
      assertEquals(Settings.UIScale.SMALL, settings.getCurrentUIScale());

      settings.setCurrentUIScale(Settings.UIScale.LARGE);
      assertEquals(Settings.UIScale.LARGE, settings.getCurrentUIScale());

      // Test quality
      settings.setQuality(Settings.Quality.LOW);
      assertEquals(Settings.Quality.LOW, settings.getQuality());

      // Test vsync
      settings.setVsync(true);
      assertTrue(settings.isVsync());

      settings.setVsync(false);
      assertFalse(settings.isVsync());

      // Test FPS
      settings.setFps(55);
      assertEquals(55, settings.getFps());

      // Test refresh rate
      settings.setRefreshRate(144);
      assertEquals(144, settings.getRefreshRate());
    }
  }

  @Test
  void testResolutionSettings() {
    Settings settings = new Settings();

    // Test windowed resolution
    Pair<Integer, Integer> newResolution = new Pair<>(1600, 900);
    settings.setWindowedResolution(newResolution);
    assertEquals(newResolution, settings.getWindowedResolution());

    // Test current resolution
    Pair<Integer, Integer> currentResolution = new Pair<>(1920, 1080);
    settings.setCurrentResolution(currentResolution);
    assertEquals(currentResolution, settings.getCurrentResolution());
  }

  @Test
  void testMonitorSettings() {
    Settings settings = new Settings();

    // Test monitor setting
    settings.setCurrentMonitor(monitorMock);
    assertEquals(monitorMock, settings.getCurrentMonitor());

    // Test available monitors
    Monitor[] expectedMonitors = new Monitor[] {monitorMock, primaryMonitorMock};
    assertArrayEquals(expectedMonitors, settings.getAvailableMonitors());
  }

  @Test
  void testAvailableResolutions() {
    try (MockedStatic<EnvironmentUtils> environmentUtilsMock =
        Mockito.mockStatic(EnvironmentUtils.class)) {
      environmentUtilsMock.when(EnvironmentUtils::getOperatingSystem).thenReturn("Windows");
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getResolutionFromDisplayMode(any(Monitor.class)))
          .thenReturn(new Pair<>(1920, 1080));
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getRefreshRateFromDisplayMode(any(Monitor.class)))
          .thenReturn(60);

      Settings settings = new Settings();

      // Test that available resolutions are set
      assertNotNull(settings.getAvailableResolutions());
      assertFalse(settings.getAvailableResolutions().isEmpty());

      // Test setting available resolutions
      settings.setAvailableResolutions();
      assertNotNull(settings.getAvailableResolutions());
    }
  }

  @Test
  void testFpsValidation() {
    try (MockedStatic<EnvironmentUtils> environmentUtilsMock =
        Mockito.mockStatic(EnvironmentUtils.class)) {
      environmentUtilsMock.when(EnvironmentUtils::getOperatingSystem).thenReturn("Windows");
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getResolutionFromDisplayMode(any(Monitor.class)))
          .thenReturn(new Pair<>(1920, 1080));
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getRefreshRateFromDisplayMode(any(Monitor.class)))
          .thenReturn(60);

      Settings settings = new Settings();

      settings.setRefreshRate(60);
      settings.setFps(120);
      assertEquals(60, settings.getFps());
    }
  }

  @Test
  void testToString() {
    Settings settings = new Settings();
    String settingsString = settings.toString();

    assertTrue(settingsString.contains("Settings"));
    assertTrue(settingsString.contains("soundVolume"));
    assertTrue(settingsString.contains("voiceVolume"));
    assertTrue(settingsString.contains("musicVolume"));
    assertTrue(settingsString.contains("masterVolume"));
    assertTrue(settingsString.contains("difficulty"));
    assertTrue(settingsString.contains("currentResolution"));
    assertTrue(settingsString.contains("refreshRate"));
    assertTrue(settingsString.contains("fps"));
    assertTrue(settingsString.contains("vsync"));
    assertTrue(settingsString.contains("currentUIScale"));
    assertTrue(settingsString.contains("quality"));
    assertTrue(settingsString.contains("currentMode"));
  }

  @Test
  void testEnumValues() {
    // Test Mode enum
    assertEquals(3, Settings.Mode.values().length);
    assertTrue(Arrays.asList(Settings.Mode.values()).contains(Settings.Mode.WINDOWED));
    assertTrue(Arrays.asList(Settings.Mode.values()).contains(Settings.Mode.FULLSCREEN));
    assertTrue(Arrays.asList(Settings.Mode.values()).contains(Settings.Mode.BORDERLESS));

    // Test Difficulty enum
    assertEquals(3, Settings.Difficulty.values().length);
    assertTrue(Arrays.asList(Settings.Difficulty.values()).contains(Settings.Difficulty.EASY));
    assertTrue(Arrays.asList(Settings.Difficulty.values()).contains(Settings.Difficulty.NORMAL));
    assertTrue(Arrays.asList(Settings.Difficulty.values()).contains(Settings.Difficulty.HARD));

    // Test Quality enum
    assertEquals(2, Settings.Quality.values().length);
    assertTrue(Arrays.asList(Settings.Quality.values()).contains(Settings.Quality.LOW));
    assertTrue(Arrays.asList(Settings.Quality.values()).contains(Settings.Quality.HIGH));

    // Test UIScale enum
    assertEquals(3, Settings.UIScale.values().length);
    assertTrue(Arrays.asList(Settings.UIScale.values()).contains(Settings.UIScale.SMALL));
    assertTrue(Arrays.asList(Settings.UIScale.values()).contains(Settings.UIScale.MEDIUM));
    assertTrue(Arrays.asList(Settings.UIScale.values()).contains(Settings.UIScale.LARGE));
  }

  @Test
  void testEdgeCases() {
    try (MockedStatic<EnvironmentUtils> environmentUtilsMock =
        Mockito.mockStatic(EnvironmentUtils.class)) {
      environmentUtilsMock.when(EnvironmentUtils::getOperatingSystem).thenReturn("Windows");
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getResolutionFromDisplayMode(any(Monitor.class)))
          .thenReturn(new Pair<>(1920, 1080));
      environmentUtilsMock
          .when(() -> EnvironmentUtils.getRefreshRateFromDisplayMode(any(Monitor.class)))
          .thenReturn(60);

      Settings settings = new Settings();

      // Test negative volume values
      settings.setSoundVolume(-0.5f);
      assertEquals(-0.5f, settings.getSoundVolume());

      // Test volume values greater than 1
      settings.setSoundVolume(2.0f);
      assertEquals(2.0f, settings.getSoundVolume());

      // Test zero volume
      settings.setSoundVolume(0.0f);
      assertEquals(0.0f, settings.getSoundVolume());

      // Test FPS with zero refresh rate
      settings.setRefreshRate(50);
      settings.setFps(60);
      assertEquals(50, settings.getFps());
    }
  }
}
