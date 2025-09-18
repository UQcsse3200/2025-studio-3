package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.shop.ShopActions;
import com.csse3200.game.components.shop.ShopDisplay;
import com.csse3200.game.components.worldmap.AnimatedDropdownMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenuActions;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShopScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(ShopScreen.class);
  private final Renderer renderer;
  private final GdxGame game;
  private String[] shopTextures = {
    "images/ui/shop-popup.png", "images/entities/currency/coins.png", "images/ui/dialog.png"
  };
  private String[] itemTextures;

  /**
   * Initialises the shop screen.
   *
   * @param game the game instance
   */
  public ShopScreen(GdxGame game) {
    this.game = game;
    logger.debug("Initialising shop screen services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    renderer = RenderFactory.createRenderer();
    loadAssets();
    createUI();
  }

  @Override
  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    renderer.render();
  }

  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
    logger.trace("Resized renderer: ({} x {})", width, height);
  }

  @Override
  public void pause() {
    // Do nothing
  }

  @Override
  public void resume() {
    // Do nothing
  }

  @Override
  public void dispose() {
    logger.debug("Disposing shop screen");
    renderer.dispose();
    unloadAssets();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.clear();
  }

  /** Loads the shop screen's assets. */
  private void loadAssets() {
    ServiceLocator.getResourceService().loadTextures(shopTextures);
    logger.debug("Loading shop assets");
    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) {
      logger.warn("ConfigService is null");
      return;
    }
    itemTextures = new String[configService.getItemConfigs().length];
    for (int i = 0; i < configService.getItemConfigs().length; i++) {
      itemTextures[i] = configService.getItemConfigs()[i].getAssetPath();
    }
    ServiceLocator.getResourceService().loadTextures(itemTextures);
    ServiceLocator.getResourceService().loadAll();
  }

  /** Unloads the shop screen's assets from the resource manager. */
  private void unloadAssets() {
    logger.debug("Unloading shop assets");
    ServiceLocator.getResourceService().unloadAssets(shopTextures);
    ServiceLocator.getResourceService().unloadAssets(itemTextures);
  }

  /**
   * Creates the shop screen's ui including components for rendering ui elements to the screen and
   * capturing and handling ui input.
   */
  private void createUI() {
    logger.debug("Creating shop ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity ui = new Entity();
    ui.addComponent(new ShopDisplay())
        .addComponent(new ShopActions(this.game))
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new WorldMapNavigationMenu())
        .addComponent(new WorldMapNavigationMenuActions(this.game))
        .addComponent(new AnimatedDropdownMenu());
    ServiceLocator.getEntityService().register(ui);
  }
}
