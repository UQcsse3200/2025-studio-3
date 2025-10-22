package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.persistence.NewGameMenuActions;
import com.csse3200.game.components.persistence.NewGameMenuDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The game screen containing the new game menu. */
public class NewGameScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(NewGameScreen.class);

  /**
   * Constructor for the new game screen.
   *
   * @param game the game instance
   */
  public NewGameScreen(GdxGame game) {
    super(game, Optional.of("images/backgrounds/title.png"), Optional.empty());
  }

  /**
   * Creates the new game menu's ui including components for rendering ui elements to the screen and
   * capturing and handling ui input.
   */
  @Override
  protected Entity constructEntity(Stage stage) {
    logger.debug("Creating new game screen UI");
    return new Entity()
        .addComponent(new NewGameMenuDisplay())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new NewGameMenuActions(game))
        .addComponent(new Terminal())
        .addComponent(ServiceLocator.getInputService().getInputFactory().createForTerminal())
        .addComponent(new TerminalDisplay());
  }
}
