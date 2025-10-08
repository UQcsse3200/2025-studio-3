package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.persistence.LoadMenuActions;
import com.csse3200.game.components.persistence.LoadMenuDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The game screen containing the load menu. */
public class LoadGameScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(LoadGameScreen.class);

  /**
   * Constructor for the load game screen.
   *
   * @param game the game instance
   */
  public LoadGameScreen(GdxGame game) {
    super(game, Optional.of("images/backgrounds/bg.png"), Optional.empty());
  }

  /**
   * Creates the load menu's ui including components for rendering ui elements to the screen and
   * capturing and handling ui input.
   */
  @Override
  protected Entity constructEntity(Stage stage) {
    logger.debug("Creating load game screen UI");
    return new Entity()
        .addComponent(new LoadMenuDisplay())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new LoadMenuActions(game));
  }
}
