package com.csse3200.game.components.hud;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;

/** Handles actions for the pause menu buttons. */
public class PauseMenuActions extends Component {
  private final GdxGame game;

  /**
   * Constructor for the PauseMenuActions class.
   *
   * @param game The game instance.
   */
  public PauseMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("quit_level", this::onQuitLevel);
    entity.getEvents().addListener("open_main_menu", this::onMainMenu);
    entity.getEvents().addListener("exit_game", this::onExitGame);
  }

  /** Shows warning dialog for quitting level */
  private void onQuitLevel() {
    ServiceLocator.getDialogService()
        .warning(
            "Quit Level",
            "Are you sure you want to quit this level? Your progress will not be saved.",
            dialog -> {
              game.setScreen(GdxGame.ScreenType.WORLD_MAP);
              ServiceLocator.getDiscordRichPresenceService().setPresence(null);
            },
            null);
  }

  /** Shows warning dialog for returning to main menu */
  private void onMainMenu() {
    ServiceLocator.getDialogService()
        .warning(
            "Main Menu",
            "Are you sure you want to return to the main menu? Your progress will not be saved.",
            dialog -> {
              game.setScreen(GdxGame.ScreenType.MAIN_MENU);
              ServiceLocator.getProfileService().clear();
              ServiceLocator.getDiscordRichPresenceService().setPresence(null);
            },
            null);
  }

  /** Shows warning dialog for exiting game */
  private void onExitGame() {
    ServiceLocator.getDialogService()
        .warning(
            "Exit Game",
            "Are you sure you want to exit the game? Your progress will not be saved.",
            dialog -> {
              game.exit();
              ServiceLocator.getDiscordRichPresenceService().setPresence(null);
            },
            null);
  }
}
