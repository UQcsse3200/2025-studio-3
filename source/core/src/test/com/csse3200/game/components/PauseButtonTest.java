package com.csse3200.game.components;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.hud.PauseMenuActions;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.DialogService;
import com.csse3200.game.services.DiscordRichPresenceService;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PauseMenuActionsTest {

  private GdxGame mockGame;
  private DialogService mockDialogService;
  private ProfileService mockProfileService;
  private DiscordRichPresenceService mockDiscordService;
  private Entity entity;
  private PauseMenuActions pauseMenuActions;

  @BeforeEach
  void setup() {
    mockGame = mock(GdxGame.class);
    mockDialogService = mock(DialogService.class);
    mockProfileService = mock(ProfileService.class);
    mockDiscordService = mock(DiscordRichPresenceService.class);

    // Register mocked services
    ServiceLocator.registerDialogService(mockDialogService);
    ServiceLocator.registerProfileService(mockProfileService);
    ServiceLocator.registerDiscordRichPresenceService(mockDiscordService);

    // Create entity and attach PauseMenuActions
    entity = new Entity();
    pauseMenuActions = new PauseMenuActions(mockGame);
    entity.addComponent(pauseMenuActions);
    entity.create(); // register listeners
  }

  @Test
  void testOnQuitLevelShowsDialog() {
    // Simulate triggering the event
    entity.getEvents().trigger("quit_level");

    // Verify dialog service is called
    verify(mockDialogService)
        .warning(
            org.mockito.ArgumentMatchers.eq("Quit Level"),
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.isNull());
  }

  @Test
  void testOnMainMenuShowsDialog() {
    entity.getEvents().trigger("open_main_menu");

    verify(mockDialogService)
        .warning(
            org.mockito.ArgumentMatchers.eq("Main Menu"),
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.isNull());
  }

  @Test
  void testOnExitGameShowsDialog() {
    entity.getEvents().trigger("exit_game");

    verify(mockDialogService)
        .warning(
            org.mockito.ArgumentMatchers.eq("Exit Game"),
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.isNull());
  }
}
