package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.persistence.DeserializedSettings;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.progression.Profile;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
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
  @Mock Monitor monitorMock;
  @Mock DisplayMode displayModeMock;
  @Mock Monitor primaryMonitorMock;
  @Mock DeserializedSettings deserializedSettingsMock;
  @Mock Settings settingsMock;

  @BeforeEach
  void setUp() {
    fileLoaderMock = mockStatic(FileLoader.class, withSettings().strictness(Strictness.LENIENT));
    fileLoaderMock
        .when(() -> FileLoader.readClass(DeserializedSettings.class, anyString(), any(FileLoader.Location.class)))
        .thenReturn(deserializedSettingsMock);
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

    settingsMock = mock(Settings.class);
    lenient().when(settingsMock.getFps()).thenReturn(60);
    lenient().when(settingsMock.isVsync()).thenReturn(true);
    lenient().when(settingsMock.getCurrentMode()).thenReturn(Settings.Mode.WINDOWED);
    lenient().when(settingsMock.getWindowedResolution()).thenReturn(new Pair<>(1920, 1080));
    lenient().when(settingsMock.getCurrentMonitor()).thenReturn(primaryMonitorMock);
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
  }
}