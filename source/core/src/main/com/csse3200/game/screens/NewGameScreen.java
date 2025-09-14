package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.persistence.NewGameMenuActions;
import com.csse3200.game.components.persistence.NewGameMenuDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.DialogService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The game screen containing the new game menu. */
public class NewGameScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(NewGameScreen.class);
  private final GdxGame game;
  private final Renderer renderer;
  private static final String[] textures = {"images/dialog.png"};

  /**
   * Constructor for the new game screen.
   *
   * @param game the game instance
   */
  public NewGameScreen(GdxGame game) {
    this.game = game;
    logger.debug("Initialising new game screen services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerDialogService(new DialogService());
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
    logger.debug("Disposing new game screen");
    renderer.dispose();
    unloadAssets();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.getDialogService().hideAllDialogs();
    ServiceLocator.clear();
  }

  /** Loads the new game screen's assets. */
  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(textures);
    ServiceLocator.getResourceService().loadAll();
  }

  /** Unloads the new game screen's assets. */
  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(textures);
  }

  /**
   * Creates the new game menu's ui including components for rendering ui elements to the screen and
   * capturing and handling ui input.
   */
  private void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity ui = new Entity();
    ui.addComponent(new NewGameMenuDisplay())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new NewGameMenuActions(game));
    ServiceLocator.getEntityService().register(ui);
  }
}
