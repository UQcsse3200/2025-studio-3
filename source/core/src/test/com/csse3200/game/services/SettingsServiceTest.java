package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.Input;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.persistence.DeserializedSettings;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.utils.EnvironmentUtils;
import net.dermetfan.utils.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {
  private MockedStatic<FileLoader> fileLoaderMock;
  private MockedStatic<EnvironmentUtils> environmentUtilsMock;
  @Mock Monitor monitorMock;
  @Mock DisplayMode displayModeMock;
  @Mock Monitor primaryMonitorMock;
  @Mock DeserializedSettings deserializedSettingsMock;
  @Mock Settings settingsMock;

  @BeforeEach
  void setUp() {
    fileLoaderMock = mockStatic(FileLoader.class, withSettings().strictness(Strictness.LENIENT));
    fileLoaderMock
        .when(
            () ->
                FileLoader.readClass(
                    eq(DeserializedSettings.class), anyString(), any(FileLoader.Location.class)))
        .thenReturn(deserializedSettingsMock);

    environmentUtilsMock =
        mockStatic(EnvironmentUtils.class, withSettings().strictness(Strictness.LENIENT));
    environmentUtilsMock.when(EnvironmentUtils::getOperatingSystem).thenReturn("Windows");
    environmentUtilsMock
        .when(() -> EnvironmentUtils.getResolutionFromDisplayMode(any(Monitor.class)))
        .thenReturn(new Pair<>(1920, 1080));
    environmentUtilsMock
        .when(() -> EnvironmentUtils.getRefreshRateFromDisplayMode(any(Monitor.class)))
        .thenReturn(144);
    Graphics graphicsMock = mock(Graphics.class);
    Gdx.graphics = graphicsMock;
    Monitor[] monitors = new Monitor[] {monitorMock, primaryMonitorMock};
    lenient().when(graphicsMock.getMonitors()).thenReturn(monitors);
    lenient().when(graphicsMock.getPrimaryMonitor()).thenReturn(primaryMonitorMock);
    lenient().when(graphicsMock.getDisplayMode(any(Monitor.class))).thenReturn(displayModeMock);
    lenient().when(graphicsMock.setFullscreenMode(any(DisplayMode.class))).thenReturn(true);
    lenient().when(graphicsMock.setWindowedMode(anyInt(), anyInt())).thenReturn(true);
    lenient().doNothing().when(graphicsMock).setUndecorated(anyBoolean());
    lenient().doNothing().when(graphicsMock).setResizable(anyBoolean());
    lenient().doNothing().when(graphicsMock).setForegroundFPS(anyInt());
    lenient().doNothing().when(graphicsMock).setVSync(anyBoolean());

    // Mock DeserializedSettings
    lenient().when(deserializedSettingsMock.getFps()).thenReturn(60);
    lenient().when(deserializedSettingsMock.isVsync()).thenReturn(true);
    lenient().when(deserializedSettingsMock.getCurrentMode()).thenReturn(Settings.Mode.WINDOWED);
    lenient()
        .when(deserializedSettingsMock.getWindowedResolution())
        .thenReturn(new Pair<>(1920, 1080));
    lenient().when(deserializedSettingsMock.getQuality()).thenReturn(Settings.Quality.HIGH);
    lenient()
        .when(deserializedSettingsMock.getCurrentUIScale())
        .thenReturn(Settings.UIScale.MEDIUM);
    lenient().when(deserializedSettingsMock.getDifficulty()).thenReturn(Settings.Difficulty.NORMAL);
    lenient().when(deserializedSettingsMock.getPauseButton()).thenReturn(Input.Keys.ESCAPE);
    lenient().when(deserializedSettingsMock.getSkipButton()).thenReturn(Input.Keys.SPACE);
    lenient().when(deserializedSettingsMock.getInteractionButton()).thenReturn(Input.Keys.E);
    lenient().when(deserializedSettingsMock.getUpButton()).thenReturn(Input.Keys.W);
    lenient().when(deserializedSettingsMock.getDownButton()).thenReturn(Input.Keys.S);
    lenient().when(deserializedSettingsMock.getLeftButton()).thenReturn(Input.Keys.A);
    lenient().when(deserializedSettingsMock.getRightButton()).thenReturn(Input.Keys.D);
    lenient().when(deserializedSettingsMock.getMusicVolume()).thenReturn(1.0f);
    lenient().when(deserializedSettingsMock.getSoundVolume()).thenReturn(1.0f);
    lenient().when(deserializedSettingsMock.getVoiceVolume()).thenReturn(1.0f);
    lenient().when(deserializedSettingsMock.getMasterVolume()).thenReturn(1.0f);

    settingsMock = mock(Settings.class);
    lenient().when(settingsMock.getFps()).thenReturn(60);
    lenient().when(settingsMock.isVsync()).thenReturn(true);
    lenient().when(settingsMock.getCurrentMode()).thenReturn(Settings.Mode.WINDOWED);
    lenient().when(settingsMock.getCurrentMonitor()).thenReturn(primaryMonitorMock);
    lenient().when(settingsMock.getWindowedResolution()).thenReturn(new Pair<>(1920, 1080));
    lenient().when(settingsMock.getCurrentResolution()).thenReturn(new Pair<>(1920, 1080));
    lenient().when(settingsMock.getQuality()).thenReturn(Settings.Quality.HIGH);
    lenient().when(settingsMock.getCurrentUIScale()).thenReturn(Settings.UIScale.MEDIUM);
    lenient().when(settingsMock.getDifficulty()).thenReturn(Settings.Difficulty.NORMAL);
    lenient().when(settingsMock.getPauseButton()).thenReturn(Input.Keys.ESCAPE);
    lenient().when(settingsMock.getSkipButton()).thenReturn(Input.Keys.SPACE);
    lenient().when(settingsMock.getInteractionButton()).thenReturn(Input.Keys.E);
    lenient().when(settingsMock.getUpButton()).thenReturn(Input.Keys.W);
    lenient().when(settingsMock.getDownButton()).thenReturn(Input.Keys.S);
    lenient().when(settingsMock.getLeftButton()).thenReturn(Input.Keys.A);
    lenient().when(settingsMock.getRightButton()).thenReturn(Input.Keys.D);
    lenient().when(settingsMock.getMusicVolume()).thenReturn(1.0f);
    lenient().when(settingsMock.getSoundVolume()).thenReturn(1.0f);
    lenient().when(settingsMock.getVoiceVolume()).thenReturn(1.0f);
    lenient().when(settingsMock.getMasterVolume()).thenReturn(1.0f);
  }

  @AfterEach
  void tearDown() {
    if (fileLoaderMock != null) {
      fileLoaderMock.close();
    }
    if (environmentUtilsMock != null) {
      environmentUtilsMock.close();
    }
  }

  @Test
  void testConstructorWithValidDeserializedSettings() {
    SettingsService service = new SettingsService();

    assertNotNull(service.getSettings());
    verify(Gdx.graphics).setForegroundFPS(60);
    verify(Gdx.graphics).setVSync(true);
    verify(Gdx.graphics).setWindowedMode(1920, 1080);
    verify(Gdx.graphics).setResizable(false);
    fileLoaderMock.verify(
        () ->
            FileLoader.writeClass(
                any(DeserializedSettings.class), anyString(), any(FileLoader.Location.class)));
  }

  @Test
  void testConstructorWithNullDeserializedSettings() {
    fileLoaderMock
        .when(
            () ->
                FileLoader.readClass(
                    eq(DeserializedSettings.class), anyString(), any(FileLoader.Location.class)))
        .thenReturn(null);

    SettingsService service = new SettingsService();

    assertNotNull(service.getSettings());
    verify(Gdx.graphics).setForegroundFPS(anyInt());
    verify(Gdx.graphics).setVSync(false);
  }

  @Test
  void testGetSettings() {
    SettingsService service = new SettingsService();
    Settings settings = service.getSettings();
    assertNotNull(settings);
  }

  @Test
  void testChangeDisplayModeFullscreen() {
    SettingsService service = new SettingsService();

    service.changeDisplayMode(Settings.Mode.FULLSCREEN);

    Settings settings = service.getSettings();
    assertEquals(Settings.Mode.FULLSCREEN, settings.getCurrentMode());

    verify(Gdx.graphics, atLeast(1)).setFullscreenMode(displayModeMock);
    verify(Gdx.graphics, atLeast(1)).setResizable(false);
  }

  @Test
  void testChangeDisplayModeBorderless() {
    SettingsService service = new SettingsService();

    service.changeDisplayMode(Settings.Mode.BORDERLESS);

    Settings settings = service.getSettings();
    assertEquals(Settings.Mode.BORDERLESS, settings.getCurrentMode());

    verify(Gdx.graphics, atLeast(1)).setUndecorated(true);
    verify(Gdx.graphics, atLeast(1)).setResizable(false);
    verify(Gdx.graphics, atLeast(1)).setWindowedMode(anyInt(), anyInt());
  }

  @Test
  void testChangeDisplayModeWindowed() {
    SettingsService service = new SettingsService();

    service.changeDisplayMode(Settings.Mode.WINDOWED);

    Settings settings = service.getSettings();
    assertEquals(Settings.Mode.WINDOWED, settings.getCurrentMode());

    verify(Gdx.graphics, atLeast(1)).setWindowedMode(1920, 1080);
  }

  @Test
  void testChangeDisplayModeInvalid() {
    SettingsService service = new SettingsService();

    assertThrows(NullPointerException.class, () -> service.changeDisplayMode(null));
  }

  @Test
  void testChangeDisplaySettingsValid() {
    SettingsService service = new SettingsService();

    service.changeDisplaySettings(120, false, Settings.UIScale.LARGE, Settings.Quality.LOW);

    Settings settings = service.getSettings();
    assertEquals(120, settings.getFps());
    assertEquals(false, settings.isVsync());
    assertEquals(Settings.UIScale.LARGE, settings.getCurrentUIScale());
    assertEquals(Settings.Quality.LOW, settings.getQuality());
    verify(Gdx.graphics).setForegroundFPS(120);
    verify(Gdx.graphics).setVSync(false);
  }

  @Test
  void testChangeDisplaySettingsInvalidFpsTooLow() {
    SettingsService service = new SettingsService();

    service.changeDisplaySettings(20, true, Settings.UIScale.MEDIUM, Settings.Quality.HIGH);

    Settings settings = service.getSettings();
    assertNotEquals(20, settings.getFps());
    verify(Gdx.graphics, never()).setForegroundFPS(20);
  }

  @Test
  void testChangeDisplaySettingsInvalidFpsTooHigh() {
    SettingsService service = new SettingsService();

    service.changeDisplaySettings(300, true, Settings.UIScale.MEDIUM, Settings.Quality.HIGH);

    Settings settings = service.getSettings();
    assertNotEquals(300, settings.getFps());
    verify(Gdx.graphics, never()).setForegroundFPS(300);
  }

  @Test
  void testSwitchResolutionInWindowedMode() {
    SettingsService service = new SettingsService();

    service.changeDisplayMode(Settings.Mode.WINDOWED);

    Pair<Integer, Integer> newResolution = new Pair<>(1600, 900);
    service.switchResolution(newResolution);

    Settings settings = service.getSettings();
    assertEquals(newResolution, settings.getCurrentResolution());
    verify(Gdx.graphics).setWindowedMode(1600, 900);
  }

  @Test
  void testSwitchResolutionInFullscreenMode() {
    SettingsService service = new SettingsService();

    service.changeDisplayMode(Settings.Mode.FULLSCREEN);

    Pair<Integer, Integer> newResolution = new Pair<>(1600, 900);
    service.switchResolution(newResolution);

    Settings settings = service.getSettings();
    assertNotEquals(newResolution, settings.getCurrentResolution());
    verify(Gdx.graphics, never()).setWindowedMode(1600, 900);
  }

  @Test
  void testChangeAudioSettingsValid() {
    SettingsService service = new SettingsService();

    service.changeAudioSettings(0.8f, 0.7f, 0.9f, 0.6f);

    Settings settings = service.getSettings();
    assertEquals(0.8f, settings.getMusicVolume(), 0.001f);
    assertEquals(0.7f, settings.getSoundVolume(), 0.001f);
    assertEquals(0.9f, settings.getVoiceVolume(), 0.001f);
    assertEquals(0.6f, settings.getMasterVolume(), 0.001f);
  }

  @Test
  void testChangeAudioSettingsInvalidMusicVolume() {
    SettingsService service = new SettingsService();

    service.changeAudioSettings(-0.1f, 0.5f, 0.5f, 0.5f);

    Settings settings = service.getSettings();
    assertNotEquals(-0.1f, settings.getMusicVolume(), 0.001f);
  }

  @Test
  void testChangeAudioSettingsInvalidSoundVolume() {
    SettingsService service = new SettingsService();

    service.changeAudioSettings(0.5f, 1.5f, 0.5f, 0.5f);

    Settings settings = service.getSettings();
    assertNotEquals(1.5f, settings.getSoundVolume(), 0.001f);
  }

  @Test
  void testChangeAudioSettingsInvalidVoiceVolume() {
    SettingsService service = new SettingsService();

    service.changeAudioSettings(0.5f, 0.5f, -0.1f, 0.5f);

    Settings settings = service.getSettings();
    assertNotEquals(-0.1f, settings.getVoiceVolume(), 0.001f);
  }

  @Test
  void testChangeAudioSettingsInvalidMasterVolume() {
    SettingsService service = new SettingsService();

    service.changeAudioSettings(0.5f, 0.5f, 0.5f, 2.0f);

    Settings settings = service.getSettings();
    assertNotEquals(2.0f, settings.getMasterVolume(), 0.001f);
  }

  @Test
  void testChangeKeybinds() {
    SettingsService service = new SettingsService();

    service.changeKeybinds(
        Input.Keys.P,
        Input.Keys.N,
        Input.Keys.F,
        Input.Keys.UP,
        Input.Keys.DOWN,
        Input.Keys.LEFT,
        Input.Keys.RIGHT);

    Settings settings = service.getSettings();
    assertEquals(Input.Keys.P, settings.getPauseButton());
    assertEquals(Input.Keys.N, settings.getSkipButton());
    assertEquals(Input.Keys.F, settings.getInteractionButton());
    assertEquals(Input.Keys.UP, settings.getUpButton());
    assertEquals(Input.Keys.DOWN, settings.getDownButton());
    assertEquals(Input.Keys.LEFT, settings.getLeftButton());
    assertEquals(Input.Keys.RIGHT, settings.getRightButton());
  }

  @Test
  void testGetSoundVolume() {
    SettingsService service = new SettingsService();
    Settings settings = service.getSettings();
    settings.setSoundVolume(0.8f);
    settings.setMasterVolume(0.5f);

    float result = service.getSoundVolume();

    assertEquals(0.4f, result, 0.001f);
  }

  @Test
  void testGetVoiceVolume() {
    SettingsService service = new SettingsService();
    Settings settings = service.getSettings();
    settings.setVoiceVolume(0.7f);
    settings.setMasterVolume(0.6f);

    float result = service.getVoiceVolume();

    assertEquals(0.42f, result, 0.001f);
  }

  @Test
  void testGetMusicVolume() {
    SettingsService service = new SettingsService();
    Settings settings = service.getSettings();
    settings.setMusicVolume(0.9f);
    settings.setMasterVolume(0.8f);

    float result = service.getMusicVolume();

    assertEquals(0.72f, result, 0.001f);
  }

  @Test
  void testSaveSettings() {
    SettingsService service = new SettingsService();

    service.saveSettings();

    fileLoaderMock.verify(
        () ->
            FileLoader.writeClass(
                any(DeserializedSettings.class), anyString(), any(FileLoader.Location.class)),
        times(2));
  }

  @Test
  void testChangeAudioSettingsBoundaryValues() {
    SettingsService service = new SettingsService();

    service.changeAudioSettings(0.0f, 0.0f, 0.0f, 0.0f);
    Settings settings = service.getSettings();
    assertEquals(0.0f, settings.getMusicVolume(), 0.001f);
    assertEquals(0.0f, settings.getSoundVolume(), 0.001f);
    assertEquals(0.0f, settings.getVoiceVolume(), 0.001f);
    assertEquals(0.0f, settings.getMasterVolume(), 0.001f);

    service.changeAudioSettings(1.0f, 1.0f, 1.0f, 1.0f);
    assertEquals(1.0f, settings.getMusicVolume(), 0.001f);
    assertEquals(1.0f, settings.getSoundVolume(), 0.001f);
    assertEquals(1.0f, settings.getVoiceVolume(), 0.001f);
    assertEquals(1.0f, settings.getMasterVolume(), 0.001f);
  }
}
