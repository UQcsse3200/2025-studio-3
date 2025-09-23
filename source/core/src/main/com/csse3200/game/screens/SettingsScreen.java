package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.settingsmenu.SettingsMenuDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import java.util.Optional;

/** The game screen containing the settings. */
public class SettingsScreen extends BaseScreen {
  public SettingsScreen(GdxGame game) {
    super(game, Optional.of("images/backgrounds/bg.png"), Optional.empty());
  }

  /**
   * Creates the setting screen's ui including components for rendering ui elements to the screen
   * and capturing and handling ui input.
   */
  @Override
  protected Entity constructEntity(Stage stage) {
    return new Entity()
        .addComponent(new SettingsMenuDisplay(game))
        .addComponent(new InputDecorator(stage, 10));
  }
}
