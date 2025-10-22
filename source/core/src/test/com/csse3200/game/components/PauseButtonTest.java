package com.csse3200.game.components;

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
import org.mockito.Mockito;

public class PauseButtonTest {

  private PauseButton pauseButton;
  private Entity mockEntity;
  private EventHandler mockEvents;
  private ResourceService mockResourceService;
  private Stage mockStage;

  @BeforeEach
  void setUp() {
    // Mock dependencies
    mockEntity = Mockito.mock(Entity.class, RETURNS_DEEP_STUBS);
    mockEvents = Mockito.mock(EventHandler.class);
    mockStage = Mockito.mock(Stage.class);
    mockResourceService = Mockito.mock(ResourceService.class);
    SettingsService mockSettingsService = Mockito.mock(SettingsService.class);

    when(mockStage.getWidth()).thenReturn(800f);
    when(mockStage.getHeight()).thenReturn(600f);
    when(mockResourceService.getAsset("images/ui/pause-icon.png", Texture.class))
        .thenReturn(Mockito.mock(Texture.class));

    // Register mocks
    ServiceLocator.registerResourceService(mockResourceService);
    ServiceLocator.registerSettingsService(mockSettingsService);

    // Setup entity & component
    when(mockEntity.getEvents()).thenReturn(mockEvents);

    pauseButton = new PauseButton();
    pauseButton.setEntity(mockEntity);
    pauseButton.create();
  }

  @Test
  void shouldPauseGameWhenClicked() {
    pauseButton.setPaused(true);
    verify(mockEntity.getEvents(), never()).trigger("resumekeypressed");
  }

  @Test
  void shouldTriggerPauseEventOnClick() {
    pauseButton.setPaused(false);
    pauseButton.setPaused(true);
    verify(mockEntity.getEvents(), times(1)).trigger("pause");
  }
}
