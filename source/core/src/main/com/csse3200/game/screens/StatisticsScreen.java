package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.statistics.StatisticsDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.services.ServiceLocator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The StatisticsScreen is a game screen containing the player's Statistics.
 *
 * <p>It sets up the rendering, input and services for the UI to function and manages a
 * StatisticsDisplay component that displays the actual Statistics.
 */
public class StatisticsScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(StatisticsScreen.class);

  /**
   * Creates a new StatisticsScreen and registers the services required, creates the renderer, and
   * initialises the Statistics UI.
   *
   * @param gdxGame current game instance
   */
  public StatisticsScreen(GdxGame gdxGame) {
    super(gdxGame, Optional.of("images/backgrounds/bg.png"), Optional.empty());
  }

  /**
   * Creates the StatisticsScreen's UI including components for rendering UI elements to the screen
   * and capturing and handling UI input.
   */
  @Override
  protected Entity constructEntity(Stage stage) {
    logger.debug("Creating statistics screen UI");
    ServiceLocator.getMusicService().play("sounds/background-music/progression_background.mp3");
    return new Entity()
        .addComponent(new StatisticsDisplay(game))
        .addComponent(new InputDecorator(stage, 10));
  }
}
