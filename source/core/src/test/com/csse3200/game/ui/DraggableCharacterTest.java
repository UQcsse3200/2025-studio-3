package com.csse3200.game.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class DraggableCharacterTest {

  LevelGameArea mockArea = mock(LevelGameArea.class);

  @BeforeEach
  void setUp() {
    // Set up required services for UIComponent static initialization
    SettingsService mockSettingsService = mock(SettingsService.class);
    Settings mockSettings = mock(Settings.class);
    when(mockSettingsService.getSettings()).thenReturn(mockSettings);
    when(mockSettings.getCurrentUIScale()).thenReturn(Settings.UIScale.MEDIUM);
    ServiceLocator.registerSettingsService(mockSettingsService);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.deregisterSettingsService();
  }

  @Test
  void testCreateDraggableCharacter() {
    DraggableCharacter character = new DraggableCharacter(mockArea);
    assertNotNull(character);
  }

  @Test
  void testSetTexturePath() {
    DraggableCharacter character = new DraggableCharacter(mockArea);
    String testPath = "images/entities/character.png";
    character.setTexture(testPath);
    assertEquals(testPath, character.getTexturePath());
  }

  @Test
  void testSetPosition() {
    DraggableCharacter character = new DraggableCharacter(mockArea);
    float testX = 100f;
    float testY = 150f;
    character.setOffsets(testX, testY);
    assertEquals(testX, character.getOffsetX());
    assertEquals(testY, character.getOffsetY());
  }

  @Test
  void testSetScale() {
    DraggableCharacter character = new DraggableCharacter(mockArea);
    float testScale = 0.25f;
    character.setScale(testScale);
    assertEquals(testScale, character.getScale());
  }
}
