package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.mainmenu.MainMenuActions;
import com.csse3200.game.components.mainmenu.MainMenuDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The game screen containing the main menu. */
public class MainMenuScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(MainMenuScreen.class);

  private static final String[] MAIN_MENU_TEXTURES = {
    "images/backgrounds/bg.png",
    "images/backgrounds/bg-text.png",
    "images/ui/settings-icon.png"
  };

  public MainMenuScreen(GdxGame game) {
    super(game, Optional.of("images/backgrounds/bg.png"), Optional.of(MAIN_MENU_TEXTURES));
  }

  /**
   * Creates the main menu's UI including components for rendering ui elements to the screen and
   * capturing and handling ui input.
   */
  @Override
  protected Entity constructEntity(Stage stage) {
    ServiceLocator.getMusicService().play("sounds/background-music/intro_music.mp3");
    logger.debug("Main menu screen UI is created");
    return new Entity()
        .addComponent(new MainMenuDisplay())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new MainMenuActions(game))
        .addComponent(new Terminal())
        .addComponent(ServiceLocator.getInputService().getInputFactory().createForTerminal())
        .addComponent(new TerminalDisplay());
  }
}
