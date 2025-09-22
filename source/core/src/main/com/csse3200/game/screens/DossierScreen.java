package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.dossier.DossierDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** DossierScreen is a screen that displays the dossier of the game. */
public class DossierScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(DossierScreen.class);
  private final GdxGame game;
  private final Renderer renderer;
  private Map<String, BaseEnemyConfig> enemyConfigs;
  private Map<String, BaseDefenderConfig> defenderConfigs;
  private Map<String, BaseGeneratorConfig> generatorConfigs;

  /**
   * Constructor for the DossierScreen.
   *
   * @param gdxGame The GdxGame instance.
   */
  public DossierScreen(GdxGame gdxGame) {
    this.game = gdxGame;

    logger.debug("[DossierScreen] Initialising services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerTimeSource(new GameTime());

    renderer = RenderFactory.createRenderer();
    logger.debug("[DossierScreen] Renderer created");
    renderer.getCamera().getEntity().setPosition(5f, 5f);

    // Load configs first
    loadConfigs();

    // Load assets second
    loadAssets();

    // Create UI last
    createUI();
  }

  /** Loads the configs for the dossier screen. */
  private void loadConfigs() {
    ConfigService configService = ServiceLocator.getConfigService();
    logger.info(
        "[DossierScreen] ConfigService retrieved: {}", configService != null ? "not null" : "null");

    if (configService != null) {
      this.enemyConfigs = configService.getEnemyConfigs();
      logger.info(
          "[DossierScreen] Enemy configs loaded: {}",
          this.enemyConfigs != null ? this.enemyConfigs.size() : "null");

      this.defenderConfigs = configService.getDefenderConfigs();
      logger.info(
          "[DossierScreen] Defender configs loaded: {}",
          this.defenderConfigs != null ? this.defenderConfigs.size() : "null");

      this.generatorConfigs = configService.getGeneratorConfigs();
      logger.info(
          "[DossierScreen] Generator configs loaded: {}",
          this.generatorConfigs != null ? this.generatorConfigs.size() : "null");
    } else {
      logger.error("[DossierScreen] ConfigService is null! Cannot load configs.");
      this.enemyConfigs = new HashMap<>();
      this.defenderConfigs = new HashMap<>();
      this.generatorConfigs = new HashMap<>();
    }
  }

  /** Loads the assets for the dossier screen. */
  private void loadAssets() {
    logger.debug("[DossierScreen] Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();

    List<String> assets = new ArrayList<>();
    assets.add("images/backgrounds/bg.png");
    assets.add("images/entities/placeholder.png");

    for (Map.Entry<String, BaseEnemyConfig> entry : enemyConfigs.entrySet()) {
      BaseEnemyConfig config = entry.getValue();
      if (config != null && config.getAssetPath() != null) {
        assets.add(config.getAssetPath());
      }
    }

    for (Map.Entry<String, BaseDefenderConfig> entry : defenderConfigs.entrySet()) {
      BaseDefenderConfig config = entry.getValue();
      if (config != null && config.getAssetPath() != null) {
        assets.add(config.getAssetPath());
      }
    }

    for (Map.Entry<String, BaseGeneratorConfig> entry : generatorConfigs.entrySet()) {
      BaseGeneratorConfig config = entry.getValue();
      if (config != null && config.getAssetPath() != null) {
        assets.add(config.getAssetPath());
      }
    }

    logger.info("[DossierScreen] Loading {} assets for dossier screen", assets.size());
    if (!assets.isEmpty()) {
      resourceService.loadTextures(assets.toArray(new String[0]));
      resourceService.loadAll();
    }
  }

  /** Creates the UI for the dossier screen. */
  private void createUI() {
    logger.debug("[DossierScreen] Creating UI");
    Stage stage = ServiceLocator.getRenderService().getStage();

    // Add the background image
    Texture bgTexture =
        ServiceLocator.getResourceService().getAsset("images/backgrounds/bg.png", Texture.class);
    Image bg = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));
    bg.setFillParent(true);
    bg.setScaling(Scaling.fill);
    stage.addActor(bg);

    // Create the dossier display with configs
    Entity ui =
        new Entity()
            .addComponent(new InputDecorator(stage, 10))
            .addComponent(new DossierDisplay(game));

    ServiceLocator.getEntityService().register(ui);
    logger.debug("[DossierScreen] Registered and created");
  }

  @Override
  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    logger.debug("[DossierScreen] Rendering");
    renderer.render();
  }

  @Override
  public void resize(int width, int height) {
    logger.debug("[DossierScreen] Resized renderer: ({} x {})", width, height);
    renderer.resize(width, height);
  }

  @Override
  public void dispose() {
    logger.debug("[DossierScreen] Disposing");

    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();

    logger.debug("[DossierScreen] Services cleared");
    ServiceLocator.clear();
  }
}
