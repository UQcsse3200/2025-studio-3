package com.csse3200.game.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.dossier.DossierDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.NPCConfigs;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.persistence.FileLoader;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DossierScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(DossierScreen.class);
  private static final String[] dossierTextures = {"images/entities/enemies/robot_placeholder.png"};

  public DossierScreen(GdxGame gdxGame) {
    super(gdxGame, Optional.of("images/backgrounds/bg.png"), Optional.of(dossierTextures));
  }

  @Override
  protected Entity constructEntity(Stage stage) {
    logger.debug("Creating dossier screen UI");
    return new Entity()
        .addComponent(
            new DossierDisplay(
                game,
                FileLoader.readClass(NPCConfigs.class, "configs/enemies.json"),
                FileLoader.readClass(NPCConfigs.class, "configs/defences.json"),
                new Texture[] {
                  new Texture("images/entities/enemies/basic_robot_default_sprite.png"),
                  new Texture("images/entities/enemies/red_robot_default_sprite.png"),
                  new Texture("images/entities/enemies/blue_robot_default_sprite.png"),
                  new Texture("images/entities/defences/sling_shooter_1.png")
                }))
        .addComponent(new InputDecorator(stage, 10));
  }
}
