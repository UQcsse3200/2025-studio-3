package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.shop.ShopDisplay;
import com.csse3200.game.data.MenuSpriteData;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.progression.inventory.ItemRegistry;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShopScreen extends ScreenAdapter implements MenuSpriteScreen {
  private static final Logger logger = LoggerFactory.getLogger(ShopScreen.class);
  private final GdxGame game;
  private final Renderer renderer;
  private static final String[] shopTextures = {
    "images/shopbackground.jpg", "images/coins.png", "images/dialog.png"
  };

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

  /** Renders the shop screen. */
  @Override
  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    renderer.render();
  }

  /** Handles resizing of the shop screen. */
  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
    logger.trace("Resized renderer: ({} x {})", width, height);
  }

  /** Pauses the shop screen. Overridden to do nothing. */
  @Override
  public void pause() {
    // Do nothing
  }

  /** Resumes the shop screen. Overridden to do nothing. */
  @Override
  public void resume() {
    // Do nothing
  }

  /** Disposes of the shop screen's resources. */
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
    logger.debug("Loading shop assets");
    ServiceLocator.getResourceService().loadTextures(shopTextures);
    String[] itemTextures = new String[ItemRegistry.ITEMS.length];
    for (int i = 0; i < ItemRegistry.ITEMS.length; i++) {
      itemTextures[i] = ItemRegistry.ITEMS[i].assetPath();
    }
    ServiceLocator.getResourceService().loadTextures(itemTextures);
    ServiceLocator.getResourceService().loadAll();
  }

  /** Unloads the shop screen's assets from the resource manager. */
  private void unloadAssets() {
    logger.debug("Unloading shop assets");
    ServiceLocator.getResourceService().unloadAssets(shopTextures);
  }

  @Override
  public void register(MenuSpriteData menuSpriteData) {
    menuSpriteData
        .edit(this)
        .position(50, 50)
        .name("Shop")
        .description("Shop")
        .sprite("images/shopsprite.png")
        .locked(false)
        .apply();
  }

  /**
   * Creates the shop screen's ui including components for rendering ui elements to the screen and
   * capturing and handling ui input.
   */
  private void createUI() {
    logger.debug("Creating shop ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity ui = new Entity();
    ui.addComponent(new ShopDisplay()).addComponent(new InputDecorator(stage, 10));
    // .addComponent(new ShopButtons())
    // .addComponent(new ShopActions(game));

    ServiceLocator.getEntityService().register(ui);
  }
}
