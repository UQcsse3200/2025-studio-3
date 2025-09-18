package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.minigame.MiniGameInputComponent;
import com.csse3200.game.minigame.WallPongGameOverActions;
import com.csse3200.game.minigame.WallPongGameOverDisplay;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WallPongGameOverScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(WallPongGameOverScreen.class);

  private final GdxGame game;
  private final Renderer renderer;
  private final int finalScore;
  private final float survivalTime;
  private final int ballsHit;

  private static final String[] wallPongGameOverTextures = {
    "images/GameOver.png",
  };

  public WallPongGameOverScreen(GdxGame game, int finalScore, float survivalTime, int ballsHit) {
    this.game = game;
    this.finalScore = finalScore;
    this.survivalTime = survivalTime;
    this.ballsHit = ballsHit;
    logger.info("Initialising Wall Pong Game Over Screen");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());

    renderer = RenderFactory.createRenderer();
    loadAssets();
    createUI();
  }

  private void loadAssets() {
    logger.debug("Loading assets...");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(wallPongGameOverTextures);
    ServiceLocator.getResourceService().loadAll();
  }

  public void unloadAssets() {
    logger.debug("Unloading assets...");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(wallPongGameOverTextures);
  }

  private void createUI() {
    logger.debug("Creating Wall Pong Game Over Screen");
    Stage stage = ServiceLocator.getRenderService().getStage();

    Entity uiEntity =
        new Entity()
            .addComponent(new WallPongGameOverDisplay(finalScore, survivalTime, ballsHit))
            .addComponent(new InputDecorator(stage, 10))
            .addComponent(new MiniGameInputComponent(true))
            .addComponent(new WallPongGameOverActions(game));
    uiEntity.create();
  }

  @Override
  public void dispose() {
    renderer.dispose();
    unloadAssets();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.clear();
  }

  @Override
  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    renderer.render();
  }
}
