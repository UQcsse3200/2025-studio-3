package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.components.hud.PauseButton;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.events.EventHandler;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PauseButtonTest {
  private PauseButton pauseButton;
  private Stage stage;
  private Entity entity;
  private EventHandler events;
  private ResourceService resourceService;
  private SettingsService settingsService;

  @BeforeEach
  void setup() {
    // Mock dependencies
    stage = mock(Stage.class);
    entity = mock(Entity.class);
    events = mock(EventHandler.class);
    resourceService = mock(ResourceService.class);
    settingsService = mock(SettingsService.class);

    ServiceLocator.registerGlobalResourceService(resourceService);
    ServiceLocator.registerSettingsService(settingsService);

    when(stage.getWidth()).thenReturn(800f);
    when(stage.getHeight()).thenReturn(600f);
    when(resourceService.getAsset(anyString(), eq(Texture.class))).thenReturn(mock(Texture.class));
    when(entity.getEvents()).thenReturn(events);

    pauseButton = new PauseButton();
    pauseButton.setEntity(entity);
    pauseButton.create();
  }

  @Test
  void shouldPauseGameWhenClicked() {
    pauseButton.setPaused(true);
    assertTrue(getIsPaused(), "PauseButton should be paused after setPaused(true)");
  }

  @Test
  void shouldTriggerPauseEventOnClick() {
    pauseButton.setPaused(false);
    pauseButton.entity.getEvents().trigger("pause");
    verify(events).trigger("pause");
  }

  /** Helper method to access private field */
  private boolean getIsPaused() {
    try {
      var field = PauseButton.class.getDeclaredField("isPaused");
      field.setAccessible(true);
      return (boolean) field.get(pauseButton);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
