package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.persistence.SaveGameMenuActions;
import com.csse3200.game.components.persistence.SaveGameMenuDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The game screen containing the save game menu. */
public class SaveGameScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(SaveGameScreen.class);
  private final GdxGame game;
  private final Renderer renderer;

  /**
   * Constructor for the save game screen.
   *
   * @param game the game instance
   */
  public SaveGameScreen(GdxGame game) {
    this.game = game;
    logger.debug("Initialising save game screen services");
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
    logger.debug("Disposing save game screen");
    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.getDialogService().hideAllDialogs();
    ServiceLocator.clear();
  }

  /**
   * Creates the save game menu's ui including components for rendering ui elements to the screen
   * and capturing and handling ui input.
   */
  private void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity ui = new Entity();
    ui.addComponent(new SaveGameMenuDisplay())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new SaveGameMenuActions(game));
    ServiceLocator.getEntityService().register(ui);
  }
}
