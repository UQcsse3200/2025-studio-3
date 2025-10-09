package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.achievements.AchievementsDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AchievementsScreen is a game screen containing the player's achievements.
 *
 * <p>It sets up the rendering, input and services for the UI to function and manages an
 * AchievementsDisplay component that displays the actual achievements.
 */
public class AchievementsScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(AchievementsScreen.class);
  private AchievementsDisplay achievementsDisplay;

  /**
   * Creates a new AchievementsScreen and registers the services required, creates the renderer, and
   * initialises the Achievements UI.
   *
   * @param gdxGame current game instance
   */
  public AchievementsScreen(GdxGame gdxGame) {
    super(gdxGame, Optional.of("images/backgrounds/bg.png"), Optional.empty());
    ServiceLocator.registerResourceService(new ResourceService());
  }

  /**
   * Adjusts the screen when window is resized.
   *
   * @param width new screen width
   * @param height new screen height
   */
  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    // Notify DialogService to resize any active dialogs
    ServiceLocator.getDialogService().resize();
    // Update close button position
    if (achievementsDisplay != null) {
      achievementsDisplay.updateOnResize();
    }
  }

  /**
   * Creates the AchievementsScreen's UI including components for rendering UI elements to the
   * screen and capturing and handling UI input.
   */
  @Override
  protected Entity constructEntity(Stage stage) {
    logger.debug("Creating achievements screen UI");
    achievementsDisplay = new AchievementsDisplay(game);
    ServiceLocator.getMusicService().play("sounds/background-music/progression_background.mp3");
    return new Entity()
        .addComponent(achievementsDisplay)
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new Terminal())
        .addComponent(ServiceLocator.getInputService().getInputFactory().createForTerminal())
        .addComponent(new TerminalDisplay());
  }
}
