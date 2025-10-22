package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.components.hud.PauseButton;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.events.EventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PauseButtonComponentTest {

  @Test
  void shouldSetPausedStateCorrectly() {
    // Setup
    PauseButton pauseButton = new PauseButton();
    Entity entity = mock(Entity.class);
    EventHandler events = mock(EventHandler.class);
    when(entity.getEvents()).thenReturn(events);

    pauseButton.setEntity(entity);

    // Test setting paused
    pauseButton.setPaused(true);

    // Test setting unpaused
    pauseButton.setPaused(false);
  }

  @Test
  void shouldDisposeResources() {
    PauseButton pauseButton = new PauseButton();

    // Should not throw when disposing without initialization
    assertDoesNotThrow(() -> pauseButton.dispose());

    // Test with proper initialization
    pauseButton.setEntity(mock(Entity.class));
    assertDoesNotThrow(() -> pauseButton.dispose());
  }
}
