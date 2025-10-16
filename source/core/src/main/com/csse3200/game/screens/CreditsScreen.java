package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.credits.CreditsDisplay;
import com.csse3200.game.components.credits.CreditsInput;
import com.csse3200.game.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CreditsScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(CreditsScreen.class);
  private static final String CREDITS_LOG_PREFIX = "[Credits Screen] ";

  /**
   * Constructor for BaseScreen.
   *
   * @param game               the game instance
   */
  public CreditsScreen(GdxGame game) {
    super(game, Optional.empty(), Optional.of(List.of("images/backgrounds/bg-text.png").toArray(new String[0])));
  }

  /**
   * Constructs the UI entity for the base screen.
   *
   * @param stage the stage to create the UI screen on
   * @return the UI entity
   */
  @Override
  protected Entity constructEntity(Stage stage) {
    logger.debug("{}Constructing entity", CREDITS_LOG_PREFIX);
    return new Entity()
        .addComponent(new CreditsDisplay(stage, game))
        .addComponent(new CreditsInput(game));
  }
}
