package com.csse3200.game.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Input;
import net.dermetfan.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeserializedSettingsTest {

  private DeserializedSettings deserializedSettings;
  private Settings mockSettings;

  @BeforeEach
  void setUp() {
    deserializedSettings = new DeserializedSettings();
    mockSettings = mock(Settings.class);
  }

  @Test
  void testDefaultConstructor() {
    DeserializedSettings defaultSettings = new DeserializedSettings();
    assertNotNull(defaultSettings);
  }

  @Test
  void testConstructorWithSettings() {
    setupMockSettings();

    DeserializedSettings settingsFromMock = new DeserializedSettings(mockSettings);

    assertEquals(0.8f, settingsFromMock.getMusicVolume());
    assertEquals(0.7f, settingsFromMock.getSoundVolume());
    assertEquals(0.9f, settingsFromMock.getVoiceVolume());
    assertEquals(0.75f, settingsFromMock.getMasterVolume());
    assertEquals(Settings.Difficulty.NORMAL, settingsFromMock.getDifficulty());
    assertEquals(Input.Keys.ESCAPE, settingsFromMock.getPauseButton());
    assertEquals(Input.Keys.SPACE, settingsFromMock.getSkipButton());
    assertEquals(Input.Keys.E, settingsFromMock.getInteractionButton());
    assertEquals(Input.Keys.W, settingsFromMock.getUpButton());
    assertEquals(Input.Keys.S, settingsFromMock.getDownButton());
    assertEquals(Input.Keys.A, settingsFromMock.getLeftButton());
    assertEquals(Input.Keys.D, settingsFromMock.getRightButton());
    assertEquals(Settings.UIScale.MEDIUM, settingsFromMock.getCurrentUIScale());
    assertEquals(Settings.Quality.HIGH, settingsFromMock.getQuality());
    assertEquals(Settings.Mode.WINDOWED, settingsFromMock.getCurrentMode());
    assertEquals(60, settingsFromMock.getFps());
    assertTrue(settingsFromMock.isVsync());

    Pair<Integer, Integer> resolution = settingsFromMock.getWindowedResolution();
    assertEquals(1920, resolution.getKey());
    assertEquals(1080, resolution.getValue());
  }

  @Test
  void testVolumeGettersAndSetters() {
    deserializedSettings.setMusicVolume(0.5f);
    assertEquals(0.5f, deserializedSettings.getMusicVolume());

    deserializedSettings.setSoundVolume(0.3f);
    assertEquals(0.3f, deserializedSettings.getSoundVolume());

    deserializedSettings.setVoiceVolume(0.9f);
    assertEquals(0.9f, deserializedSettings.getVoiceVolume());

    deserializedSettings.setMasterVolume(0.1f);
    assertEquals(0.1f, deserializedSettings.getMasterVolume());
  }

  @Test
  void testVolumeBoundaryValues() {
    deserializedSettings.setMusicVolume(0.0f);
    assertEquals(0.0f, deserializedSettings.getMusicVolume());

    deserializedSettings.setSoundVolume(0.0f);
    assertEquals(0.0f, deserializedSettings.getSoundVolume());

    deserializedSettings.setVoiceVolume(1.0f);
    assertEquals(1.0f, deserializedSettings.getVoiceVolume());

    deserializedSettings.setMasterVolume(1.0f);
    assertEquals(1.0f, deserializedSettings.getMasterVolume());
  }

  @Test
  void testDifficultyGetterAndSetter() {
    deserializedSettings.setDifficulty(Settings.Difficulty.EASY);
    assertEquals(Settings.Difficulty.EASY, deserializedSettings.getDifficulty());

    deserializedSettings.setDifficulty(Settings.Difficulty.NORMAL);
    assertEquals(Settings.Difficulty.NORMAL, deserializedSettings.getDifficulty());

    deserializedSettings.setDifficulty(Settings.Difficulty.HARD);
    assertEquals(Settings.Difficulty.HARD, deserializedSettings.getDifficulty());
  }

  @Test
  void testButtonGettersAndSetters() {
    deserializedSettings.setPauseButton(Input.Keys.P);
    assertEquals(Input.Keys.P, deserializedSettings.getPauseButton());

    deserializedSettings.setSkipButton(Input.Keys.N);
    assertEquals(Input.Keys.N, deserializedSettings.getSkipButton());

    deserializedSettings.setInteractionButton(Input.Keys.ENTER);
    assertEquals(Input.Keys.ENTER, deserializedSettings.getInteractionButton());

    deserializedSettings.setUpButton(Input.Keys.UP);
    assertEquals(Input.Keys.UP, deserializedSettings.getUpButton());

    deserializedSettings.setDownButton(Input.Keys.DOWN);
    assertEquals(Input.Keys.DOWN, deserializedSettings.getDownButton());

    deserializedSettings.setLeftButton(Input.Keys.LEFT);
    assertEquals(Input.Keys.LEFT, deserializedSettings.getLeftButton());

    deserializedSettings.setRightButton(Input.Keys.RIGHT);
    assertEquals(Input.Keys.RIGHT, deserializedSettings.getRightButton());
  }

  @Test
  void testUIScaleGetterAndSetter() {
    deserializedSettings.setCurrentUIScale(Settings.UIScale.SMALL);
    assertEquals(Settings.UIScale.SMALL, deserializedSettings.getCurrentUIScale());

    deserializedSettings.setCurrentUIScale(Settings.UIScale.MEDIUM);
    assertEquals(Settings.UIScale.MEDIUM, deserializedSettings.getCurrentUIScale());

    deserializedSettings.setCurrentUIScale(Settings.UIScale.LARGE);
    assertEquals(Settings.UIScale.LARGE, deserializedSettings.getCurrentUIScale());
  }

  @Test
  void testQualityGetterAndSetter() {
    deserializedSettings.setQuality(Settings.Quality.LOW);
    assertEquals(Settings.Quality.LOW, deserializedSettings.getQuality());

    deserializedSettings.setQuality(Settings.Quality.HIGH);
    assertEquals(Settings.Quality.HIGH, deserializedSettings.getQuality());
  }

  @Test
  void testModeGetterAndSetter() {
    deserializedSettings.setCurrentMode(Settings.Mode.WINDOWED);
    assertEquals(Settings.Mode.WINDOWED, deserializedSettings.getCurrentMode());

    deserializedSettings.setCurrentMode(Settings.Mode.FULLSCREEN);
    assertEquals(Settings.Mode.FULLSCREEN, deserializedSettings.getCurrentMode());

    deserializedSettings.setCurrentMode(Settings.Mode.BORDERLESS);
    assertEquals(Settings.Mode.BORDERLESS, deserializedSettings.getCurrentMode());
  }

  @Test
  void testFpsGetterAndSetter() {
    deserializedSettings.setFps(120);
    assertEquals(120, deserializedSettings.getFps());

    deserializedSettings.setFps(30);
    assertEquals(30, deserializedSettings.getFps());

    deserializedSettings.setFps(60);
    assertEquals(60, deserializedSettings.getFps());
  }

  @Test
  void testVsyncGetterAndSetter() {
    deserializedSettings.setVsync(true);
    assertTrue(deserializedSettings.isVsync());

    deserializedSettings.setVsync(false);
    assertFalse(deserializedSettings.isVsync());
  }

  @Test
  void testWindowedResolutionGetterAndSetter() {
    Pair<Integer, Integer> resolution1 = new Pair<>(1920, 1080);
    deserializedSettings.setWindowedResolution(resolution1);
    Pair<Integer, Integer> result1 = deserializedSettings.getWindowedResolution();
    assertEquals(1920, result1.getKey());
    assertEquals(1080, result1.getValue());

    Pair<Integer, Integer> resolution2 = new Pair<>(1280, 720);
    deserializedSettings.setWindowedResolution(resolution2);
    Pair<Integer, Integer> result2 = deserializedSettings.getWindowedResolution();
    assertEquals(1280, result2.getKey());
    assertEquals(720, result2.getValue());
  }

  @Test
  void testWindowedResolutionEdgeCases() {
    Pair<Integer, Integer> minResolution = new Pair<>(640, 480);
    deserializedSettings.setWindowedResolution(minResolution);
    Pair<Integer, Integer> result = deserializedSettings.getWindowedResolution();
    assertEquals(640, result.getKey());
    assertEquals(480, result.getValue());

    Pair<Integer, Integer> highResolution = new Pair<>(3840, 2160);
    deserializedSettings.setWindowedResolution(highResolution);
    result = deserializedSettings.getWindowedResolution();
    assertEquals(3840, result.getKey());
    assertEquals(2160, result.getValue());
  }

  @Test
  void testMultipleFieldUpdates() {
    deserializedSettings.setMusicVolume(0.6f);
    deserializedSettings.setSoundVolume(0.4f);
    deserializedSettings.setDifficulty(Settings.Difficulty.HARD);
    deserializedSettings.setCurrentUIScale(Settings.UIScale.LARGE);
    deserializedSettings.setQuality(Settings.Quality.LOW);
    deserializedSettings.setCurrentMode(Settings.Mode.FULLSCREEN);
    deserializedSettings.setFps(144);
    deserializedSettings.setVsync(false);

    assertEquals(0.6f, deserializedSettings.getMusicVolume());
    assertEquals(0.4f, deserializedSettings.getSoundVolume());
    assertEquals(Settings.Difficulty.HARD, deserializedSettings.getDifficulty());
    assertEquals(Settings.UIScale.LARGE, deserializedSettings.getCurrentUIScale());
    assertEquals(Settings.Quality.LOW, deserializedSettings.getQuality());
    assertEquals(Settings.Mode.FULLSCREEN, deserializedSettings.getCurrentMode());
    assertEquals(144, deserializedSettings.getFps());
    assertFalse(deserializedSettings.isVsync());
  }

  @Test
  void testConstructorWithNullSettings() {
    assertThrows(
        NullPointerException.class,
        () -> {
          new DeserializedSettings(null);
        });
  }

  @Test
  void testVolumeNegativeValues() {
    deserializedSettings.setMusicVolume(-0.5f);
    assertEquals(-0.5f, deserializedSettings.getMusicVolume());

    deserializedSettings.setSoundVolume(-1.0f);
    assertEquals(-1.0f, deserializedSettings.getSoundVolume());
  }

  @Test
  void testVolumeValuesAboveOne() {
    deserializedSettings.setMusicVolume(1.5f);
    assertEquals(1.5f, deserializedSettings.getMusicVolume());

    deserializedSettings.setVoiceVolume(2.0f);
    assertEquals(2.0f, deserializedSettings.getVoiceVolume());
  }

  @Test
  void testFpsEdgeCases() {
    deserializedSettings.setFps(0);
    assertEquals(0, deserializedSettings.getFps());

    deserializedSettings.setFps(1);
    assertEquals(1, deserializedSettings.getFps());

    deserializedSettings.setFps(1000);
    assertEquals(1000, deserializedSettings.getFps());
  }

  @Test
  void testButtonKeyEdgeCases() {
    deserializedSettings.setPauseButton(0);
    assertEquals(0, deserializedSettings.getPauseButton());

    deserializedSettings.setSkipButton(-1);
    assertEquals(-1, deserializedSettings.getSkipButton());

    deserializedSettings.setInteractionButton(Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, deserializedSettings.getInteractionButton());
  }

  /** Helper method to setup mock Settings object with test values. */
  private void setupMockSettings() {
    when(mockSettings.getMusicVolume()).thenReturn(0.8f);
    when(mockSettings.getSoundVolume()).thenReturn(0.7f);
    when(mockSettings.getVoiceVolume()).thenReturn(0.9f);
    when(mockSettings.getMasterVolume()).thenReturn(0.75f);
    when(mockSettings.getDifficulty()).thenReturn(Settings.Difficulty.NORMAL);
    when(mockSettings.getPauseButton()).thenReturn(Input.Keys.ESCAPE);
    when(mockSettings.getSkipButton()).thenReturn(Input.Keys.SPACE);
    when(mockSettings.getInteractionButton()).thenReturn(Input.Keys.E);
    when(mockSettings.getUpButton()).thenReturn(Input.Keys.W);
    when(mockSettings.getDownButton()).thenReturn(Input.Keys.S);
    when(mockSettings.getLeftButton()).thenReturn(Input.Keys.A);
    when(mockSettings.getRightButton()).thenReturn(Input.Keys.D);
    when(mockSettings.getCurrentUIScale()).thenReturn(Settings.UIScale.MEDIUM);
    when(mockSettings.getQuality()).thenReturn(Settings.Quality.HIGH);
    when(mockSettings.getCurrentMode()).thenReturn(Settings.Mode.WINDOWED);
    when(mockSettings.getFps()).thenReturn(60);
    when(mockSettings.isVsync()).thenReturn(true);
    when(mockSettings.getWindowedResolution()).thenReturn(new Pair<>(1920, 1080));
  }
}
