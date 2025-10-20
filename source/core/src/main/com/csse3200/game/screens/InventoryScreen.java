package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.inventory.InventoryDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The InventoryScreen is a game screen containing the player's inventory.
 *
 * <p>It sets up the rendering, input and services for the UI to function and manages an
 * InventoryDisplay component that displays the actual inventory items.
 */
public class InventoryScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(InventoryScreen.class);
  private final GdxGame game;
  private final Renderer renderer;

  /**
   * Creates a new InventoryScreen and registers the services required, creates the renderer, and
   * initialises the Inventory UI.
   *
   * @param gdxGame current game instance
   */
  public InventoryScreen(GdxGame gdxGame) {
    this.game = gdxGame;
    logger.debug("Initialising inventory screen services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerTimeSource(new GameTime());
    renderer = RenderFactory.createRenderer();
    renderer.getCamera().getEntity().setPosition(5f, 5f);
    loadAssets();
    createUI();
    ServiceLocator.getMusicService().play("sounds/background-music/progression_background.mp3");
  }

  @Override
  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    renderer.render();
  }

  /** Loads the shop screen's assets. */
  private void loadAssets() {
    logger.debug("Loading inventory assets");
    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) {
      logger.warn("ConfigService is null");
      return;
    }
    String[] itemTextures = new String[configService.getItemConfigs().size()];
    for (int i = 0; i < configService.getItemConfigs().size(); i++) {
      itemTextures[i] = configService.getItemConfigValues()[i].getAssetPath();
    }
    ServiceLocator.getResourceService().loadTextures(itemTextures);
    String[] backgroundTextures = {"images/backgrounds/bg.png"};
    ServiceLocator.getResourceService().loadTextures(backgroundTextures);
    ServiceLocator.getResourceService().loadAll();
  }

  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
  }

  @Override
  public void dispose() {
    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.clear();
  }

  /**
   * Creates the InventoryScreen's UI including components for rendering UI elements to the screen
   * and capturing and handling UI input.
   */
  private void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    // Set background image
    Texture backgroundTexture =
        ServiceLocator.getResourceService().getAsset("images/backgrounds/bg.png", Texture.class);
    Image backgroundImage = new Image(backgroundTexture);
    backgroundImage.setSize(
        stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
    stage.addActor(backgroundImage);

    Entity ui = new Entity();
    ui.addComponent(new InventoryDisplay(game))
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new Terminal())
        .addComponent(ServiceLocator.getInputService().getInputFactory().createForTerminal())
        .addComponent(new TerminalDisplay());
    ServiceLocator.getEntityService().register(ui);
  }
}
