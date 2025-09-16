package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.dossier.DefenceConfigs;
import com.csse3200.game.components.dossier.DossierDisplay;
import com.csse3200.game.components.dossier.EntityConfigs;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DossierScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(InventoryScreen.class);
  private final GdxGame game;
  private final Renderer renderer;
  private static final String[] dossierTextures = {"images/robot_placeholder.png"};

  public DossierScreen(GdxGame gdxGame) {
    this.game = gdxGame;
    logger.debug("Initialising dossier screen services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerTimeSource(new GameTime());
    renderer = RenderFactory.createRenderer();
    renderer.getCamera().getEntity().setPosition(5f, 5f);
    loadAssets();
    createUI();
  }
  /**
   * Updates entities and renders current frame.
   *
   * @param delta time elapsed since last frame
   */
  @Override
  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    renderer.render();
  }

  /**
   * Adjusts the screen when window is resized.
   *
   * @param width new screen width
   * @param height new screen height
   */
  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
  }

  private void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity ui = new Entity();
    // Change this later
    ui.addComponent(new DossierDisplay(game, FileLoader.readClass(EntityConfigs.class, "configs/Enemies.json")
            , FileLoader.readClass(DefenceConfigs.class, "configs/defences.json"),
                    new Texture[]{new Texture("images/default_enemy_image.png"),
                            new Texture("images/sling_shooter_1.png")}))
            .addComponent(new InputDecorator(stage, 10));
    ServiceLocator.getEntityService().register(ui);
  }

  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(dossierTextures);
    ServiceLocator.getResourceService().loadAll();
  }

  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(dossierTextures);
  }

  /** Disposes of this screen's resources. */
  @Override
  public void dispose() {
    renderer.dispose();
    unloadAssets();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.clear();
  }
}
