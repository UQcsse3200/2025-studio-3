package com.csse3200.game.components.maingame;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.components.hud.PauseMenu;
import com.csse3200.game.screens.MainGameScreen;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class listens to events relevant to the Main Game Screen and does something when one of the
 * events is triggered.
 */
public class MainGameActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(MainGameActions.class);
  private final GdxGame game;
  private PauseMenu pauseMenu;
  private MainGameScreen mainGameScreen;
  private com.csse3200.game.components.hud.PauseButton pauseButton;

  /**
   * Constructor for the MainGameActions class.
   *
   * @param game The game instance.
   */
  public MainGameActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("pause_game", this::onPauseGame);
    entity.getEvents().addListener("resume_game", this::onResumeGame);
    entity.getEvents().addListener("hide_pause_menu", this::onHidePauseMenu);
  }

  /**
   * Sets the pause menu reference.
   *
   * @param pauseMenu The pause menu to set.
   */
  public void setPauseMenu(PauseMenu pauseMenu) {
    this.pauseMenu = pauseMenu;
  }
  
  /** Swaps to the Main Menu screen. */
  private void onExit() {
    logger.info("Exiting main game screen");
    ServiceLocator.deregisterConfigService();
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }

  /**
   * Sets the main game screen reference.
   *
   * @param mainGameScreen The main game screen to set.
   */
  public void setMainGameScreen(MainGameScreen mainGameScreen) {
    this.mainGameScreen = mainGameScreen;
  }

  /**
   * Gets the main game screen reference.
   *
   * @return The main game screen reference.
   */
  public MainGameScreen getMainGameScreen() {
    return mainGameScreen;
  }

  /**
   * Sets the pause button reference
   *
   * @param pauseButton The pause button to set.
   */
  public void setPauseButton(com.csse3200.game.components.hud.PauseButton pauseButton) {
    this.pauseButton = pauseButton;
  }

  /** Handles the pause game event */
  private void onPauseGame() {
    logger.info("Pause game event triggered");
    if (pauseMenu != null && mainGameScreen != null) {
      if (pauseMenu.isVisible()) {
        pauseMenu.hide();
        mainGameScreen.setPaused(false);
        if (pauseButton != null) {
          pauseButton.setPaused(false);
        }
      } else {
        pauseMenu.show();
        mainGameScreen.setPaused(true);
        if (pauseButton != null) {
          pauseButton.setPaused(true);
        }
      }
    } else {
      logger.warn("Pause menu or main game screen not set in MainGameActions");
    }
  }

  /** Handles the resume game event */
  private void onResumeGame() {
    logger.info("Resume game event triggered");
    if (pauseMenu != null && mainGameScreen != null) {
      pauseMenu.hide();
      mainGameScreen.setPaused(false);
      if (pauseButton != null) {
        pauseButton.setPaused(false);
      }
    } else {
      logger.warn("Pause menu or main game screen not set in MainGameActions");
    }
  }

  /** Handles hiding the pause menu (e.g., when opening settings) */
  private void onHidePauseMenu() {
    logger.info("Hide pause menu event triggered");
    if (pauseMenu != null) {
      pauseMenu.hide();
    }
    // Keep the game paused when hiding the menu for settings
  }
}
