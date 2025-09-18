package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.mainmenu.MainMenuActions;
import com.csse3200.game.components.mainmenu.MainMenuDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The game screen containing the main menu. */
public class MainMenuScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(MainMenuScreen.class);

  private static final String[] MAIN_MENU_TEXTURES = {
    "images/bg.png",
    "images/bg-text.png",
    "images/btn-blue.png",
    "images/btn-blue.atlas",
    "images/settings_icon.png"
  };

  public MainMenuScreen(GdxGame game) {
    super(game, Optional.of("images/bg.png"), Optional.of(MAIN_MENU_TEXTURES));
  }

  /**
   * Creates the main menu's UI including components for rendering ui elements to the screen and
   * capturing and handling ui input.
   */
  @Override
  protected Entity constructEntity(Stage stage) {
    logger.debug("Main menu screen UI is created");
    return new Entity()
        .addComponent(new MainMenuDisplay())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new MainMenuActions(game));
  }
}
