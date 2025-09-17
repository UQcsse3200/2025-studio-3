package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.minigame.LaneRunnerGameOverActions;
import com.csse3200.game.minigame.LaneRunnerGameOverDisplay;
import com.csse3200.game.minigame.MiniGameInputComponent;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaneRunnerGameOverScreen extends ScreenAdapter {

  private static final Logger logger = LoggerFactory.getLogger(LaneRunnerGameOverScreen.class);
  private final GdxGame game;
  private final Renderer renderer;
  private final int finalScore;
  private final float survivalTime;
  private final int obstaclesDodged;
  private static final String[] laneRunnerGameOverTextures = {
    "images/GameOver.png", "images/Background.png",
  };

  public LaneRunnerGameOverScreen(
      GdxGame game, int finalScore, float survivalTime, int obstaclesDodged) {
    this.game = game;
    this.finalScore = finalScore;
    this.survivalTime = survivalTime;
    this.obstaclesDodged = obstaclesDodged;

    logger.debug("Initialising lane runner game over screen services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());

    renderer = RenderFactory.createRenderer();
    loadAssets();
    createUI();
  }

  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(laneRunnerGameOverTextures);
    ServiceLocator.getResourceService().loadAll();
  }

  public void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(laneRunnerGameOverTextures);
  }

  private void createUI() {
    logger.debug("Creating UI");
    Stage stage = ServiceLocator.getRenderService().getStage();

    Entity uiEntity = new Entity();
    uiEntity
        .addComponent(new LaneRunnerGameOverDisplay(finalScore, survivalTime, obstaclesDodged))
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new MiniGameInputComponent(true))
        .addComponent(new LaneRunnerGameOverActions(game));
    uiEntity.create();
  }

  public void dispose() {
    renderer.dispose();
    unloadAssets();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.clear();
  }

  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    renderer.render();
  }
}
