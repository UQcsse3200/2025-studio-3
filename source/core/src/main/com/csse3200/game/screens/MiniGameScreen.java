package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.worldmap.AnimatedDropdownMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenuActions;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.minigame.MiniGameActions;
import com.csse3200.game.minigame.MiniGameDisplay;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The screen for the mini game. */
public class MiniGameScreen extends ScreenAdapter {
  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.screens.MiniGameScreen.class);
  private final GdxGame game;
  private final Renderer renderer;
  private static final String[] laneRunnerTextures = {
    "images/backgrounds/bg.png", "images/entities/character.png"
  };

  /**
   * Constructor for the mini game screen.
   *
   * @param game the game instance
   */
  public MiniGameScreen(GdxGame game) {
    this.game = game;

    logger.debug("Initialising mini game screen services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());

    renderer = RenderFactory.createRenderer();
    ServiceLocator.getMusicService().play("sounds/background-music/progression_background.mp3");
    loadAssets();
    createUI();
  }

  @Override
  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    renderer.render();
  }

  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(laneRunnerTextures);
    ServiceLocator.getResourceService().loadAll();
  }

  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(laneRunnerTextures);
  }

  @Override
  public void dispose() {
    logger.debug("Disposing mini game screen");

    renderer.dispose();
    unloadAssets();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();

    ServiceLocator.clear();
  }

  private void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();

    // Add the background image as a Stage actor
    Texture bgTex =
        ServiceLocator.getResourceService().getAsset("images/backgrounds/bg.png", Texture.class);
    logger.debug("loads mini game screen background texture asset");
    Image bg = new Image(new TextureRegionDrawable(new TextureRegion(bgTex)));
    bg.setFillParent(true);
    bg.setScaling(Scaling.fill);
    stage.addActor(bg);
    logger.debug("shows mini game screen background");

    Entity ui = new Entity();
    ui.addComponent(new MiniGameDisplay())
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new MiniGameActions(game))
        .addComponent(new WorldMapNavigationMenu())
        .addComponent(new WorldMapNavigationMenuActions(game))
        .addComponent(new AnimatedDropdownMenu())
        .addComponent(new Terminal())
        .addComponent(ServiceLocator.getInputService().getInputFactory().createForTerminal())
        .addComponent(new TerminalDisplay());
    ServiceLocator.getEntityService().register(ui);
    logger.debug("mini game screen ui is created and registered");
  }
}
