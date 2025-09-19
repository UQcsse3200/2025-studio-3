package com.csse3200.game.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.dossier.DossierDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** DossierScreen is a screen that displays the dossier of the game. */
public class DossierScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(DossierScreen.class);
  private Map<String, BaseEnemyConfig> enemyConfigs;
  private Map<String, BaseDefenderConfig> defenderConfigs;
  private Map<String, BaseGeneratorConfig> generatorConfigs;

  /**
   * Constructor for the DossierScreen.
   *
   * @param gdxGame The GdxGame instance.
   */
  public DossierScreen(GdxGame gdxGame) {
    super(gdxGame, Optional.of("images/backgrounds/bg.png"), Optional.empty());
    ConfigService configService = ServiceLocator.getConfigService();
    this.enemyConfigs = configService.getEnemyConfigs();
    this.defenderConfigs = configService.getDefenderConfigs();
    this.generatorConfigs = configService.getGeneratorConfigs();
    loadAssets();
  }

  /** Loads the assets for the dossier screen. */
  private void loadAssets() {
    List<String> assets = new ArrayList<>();
    for (String enemyKey : enemyConfigs.keySet()) {
      assets.add(enemyConfigs.get(enemyKey).getAssetPath());
    }
    for (String defenderKey : defenderConfigs.keySet()) {
      assets.add(defenderConfigs.get(defenderKey).getAssetPath());
    }
    for (String generatorKey : generatorConfigs.keySet()) {
      assets.add(generatorConfigs.get(generatorKey).getAssetPath());
    }
    ServiceLocator.getResourceService().loadTextures(assets.toArray(new String[0]));
    ServiceLocator.getResourceService().loadAll();
  }

  @Override
  protected Entity constructEntity(Stage stage) {
    logger.debug("Creating dossier screen UI");
    return new Entity()
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new DossierDisplay(game, enemyConfigs, defenderConfigs, generatorConfigs));
  }
}
