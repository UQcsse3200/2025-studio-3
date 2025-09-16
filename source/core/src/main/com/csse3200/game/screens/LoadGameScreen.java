package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.persistence.LoadMenuActions;
import com.csse3200.game.components.persistence.LoadMenuDisplay;
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

/** The game screen containing the load menu. */
public class LoadGameScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(LoadGameScreen.class);
  private final GdxGame game;
  private final Renderer renderer;

  /**
   * Constructor for the load game screen.
   *
   * @param game
   */
  public LoadGameScreen(GdxGame game) {
    this.game = game;
    logger.debug("Initialising load game screen services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    renderer = RenderFactory.createRenderer();
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
    logger.debug("Disposing load game screen");
    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.getDialogService().hideAllDialogs();
    ServiceLocator.clear();
  }
  /**
   * Creates the load menu's ui including components for rendering ui elements to the screen and
   * capturing and handling ui input.
   */
  private void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity ui = new Entity();
    ui.addComponent(new LoadMenuDisplay())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new LoadMenuActions(game));
    ServiceLocator.getEntityService().register(ui);
  }
}
