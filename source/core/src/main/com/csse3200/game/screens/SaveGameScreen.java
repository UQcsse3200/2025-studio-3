package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.persistence.SaveGameMenuActions;
import com.csse3200.game.components.persistence.SaveGameMenuDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The game screen containing the save game menu. */
public class SaveGameScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(SaveGameScreen.class);

  /**
   * Constructor for the save game screen.
   *
   * @param game the game instance
   */
  public SaveGameScreen(GdxGame game) {
    super(game, Optional.of("images/backgrounds/bg.png"), Optional.empty());
  }

  /**
   * Creates the save game menu's ui including components for rendering ui elements to the screen
   * and capturing and handling ui input.
   */
  @Override
  protected Entity constructEntity(Stage stage) {
    logger.debug("Creating save game screen UI");
    return new Entity()
        .addComponent(new SaveGameMenuDisplay())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new SaveGameMenuActions(game));
  }
}
