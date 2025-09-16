package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.currency.SunlightHudDisplay;
import com.csse3200.game.components.gamearea.PerformanceDisplay;
import com.csse3200.game.components.hud.PauseButton;
import com.csse3200.game.components.hud.PauseMenu;
import com.csse3200.game.components.hud.PauseMenuActions;
import com.csse3200.game.components.maingame.MainGameActions;
import com.csse3200.game.components.waves.CurrentWaveDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.*;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The game screen containing the main game.
 *
 * <p>Details on libGDX screens: https://happycoding.io/tutorials/libgdx/game-screens
 */
public class MainGameScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(MainGameScreen.class);
  private static final String[] mainGameTextures = {
    "images/normal_sunlight.png",
    "images/heart.png",
    "images/coins.png",
    "images/plaque.png",
    "images/skillpoints.png",
    "images/settings-icon.png",
    "images/menu-icon.png",
    "images/profile.png",
    "images/dialog.png",
    "images/achievement.png",
    "images/pause-icon.png"
  };
  private static final Vector2 CAMERA_POSITION = new Vector2(7.5f, 7.5f);

  protected final GdxGame game;
  protected final Renderer renderer;
  protected final PhysicsEngine physicsEngine;
  protected final WaveManager waveManager;
  protected LevelGameArea gameArea;
  protected boolean isPaused = false;
  protected com.badlogic.gdx.audio.Music backgroundMusic;

  public MainGameScreen(GdxGame game) {
    this.game = game;
    this.waveManager = new WaveManager();

   // if (Persistence.profile() == null) {
     // throw new IllegalStateException("No profile loaded, cannot start game");
   // }

    logger.debug("Initialising main game screen services");
    ServiceLocator.registerTimeSource(new GameTime());
    PhysicsService physicsService = new PhysicsService();
    ServiceLocator.registerPhysicsService(physicsService);
    physicsEngine = physicsService.getPhysics();
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerCurrencyService(new CurrencyService(50, Integer.MAX_VALUE));
    ServiceLocator.registerItemEffectsService(new ItemEffectsService());

    renderer = RenderFactory.createRenderer();
    renderer.getCamera().getEntity().setPosition(CAMERA_POSITION);
    renderer.getDebug().renderPhysicsWorld(physicsEngine.getWorld());

    loadAssets();
    createUI();

    Entity uiHud = new Entity().addComponent(new SunlightHudDisplay());
    ServiceLocator.getEntityService().register(uiHud);

    logger.debug("Initialising main game screen entities");
    TerrainFactory terrainFactory = new TerrainFactory(renderer.getCamera());
    gameArea = createGameArea(terrainFactory);
    waveManager.setGameArea(gameArea);
    gameArea.create();

    snapCameraBottomLeft();
    waveManager.initialiseNewWave();

    // Get reference to background music (this is a bit hacky but will be fixed later)
    backgroundMusic =
        ServiceLocator.getResourceService()
            .getAsset("sounds/BGM_03_mp3.mp3", com.badlogic.gdx.audio.Music.class);
  }

  @Override
  public void render(float delta) {
    if (!isPaused) {
      physicsEngine.update();
      ServiceLocator.getEntityService().update();
      waveManager.update();
    }
    renderer.render();
    waveManager.update();
    gameArea.checkGameOver(); // check game-over state
  }

  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
    snapCameraBottomLeft();
    logger.trace("Resized renderer: ({} x {})", width, height);
    if (gameArea != null) {
      gameArea.resize();
    }
  }

  @Override
  public void pause() {
    logger.info("Game paused");
    setPaused(true);
  }

  @Override
  public void resume() {
    logger.info("Game resumed");
    setPaused(false);
  }

  @Override
  public void dispose() {
    logger.debug("Disposing main game screen");
    renderer.dispose();
    unloadAssets();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getResourceService().dispose();
    ServiceLocator.clear();
  }

  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(mainGameTextures);
    resourceService.loadTextureAtlases(new String[] {"images/grenade.atlas"});
    ServiceLocator.getResourceService().loadAll();
  }

  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(mainGameTextures);
  }

  /**
   * Creates the main game's ui including components for rendering ui elements to* the screen and
   * capturing and handling ui input.
   */
  private void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    InputComponent inputComponent =
        ServiceLocator.getInputService().getInputFactory().createForTerminal();

    // Create pause components
    PauseButton pauseButton = new PauseButton();
    PauseMenu pauseMenu = new PauseMenu();
    PauseMenuActions pauseMenuActions = new PauseMenuActions(this.game);
    MainGameActions mainGameActions = new MainGameActions();

    Entity ui = new Entity();
    ui.addComponent(new InputDecorator(stage, 10))
        .addComponent(new PerformanceDisplay())
        .addComponent(mainGameActions)
        .addComponent(pauseButton)
        .addComponent(pauseMenu)
        .addComponent(pauseMenuActions)
        .addComponent(new Terminal())
        .addComponent(inputComponent)
        .addComponent(new TerminalDisplay())
        .addComponent(new CurrentWaveDisplay());

    // Connect the pause menu, pause button, and main game screen to the main game actions
    mainGameActions.setPauseMenu(pauseMenu);
    mainGameActions.setPauseButton(pauseButton);
    configureMainGameActions(mainGameActions);

    // Connect the UI entity to the WaveManager for event triggering
    WaveManager.setGameEntity(ui);

    ServiceLocator.getEntityService().register(ui);
  }

  /**
   * Factory method for creating the game area. Subclasses may override to provide a different area.
   */
  protected LevelGameArea createGameArea(TerrainFactory terrainFactory) {
    return new LevelGameArea(terrainFactory);
  }

  /** Hook for configuring main game actions. Subclasses can override to bind themselves instead. */
  protected void configureMainGameActions(MainGameActions mainGameActions) {
    mainGameActions.setMainGameScreen(this);
  }

  private void snapCameraBottomLeft() {
    var cam = renderer.getCamera();

    float viewportWidth = cam.getCamera().viewportWidth;
    float viewportHeight = cam.getCamera().viewportHeight;

    cam.getEntity().setPosition(viewportWidth / 2f, viewportHeight / 2f);
  }

  /** Sets the pause state of the game */
  public void setPaused(boolean paused) {
    this.isPaused = paused;
    logger.info("Game paused: {}", paused);

    // Pause/resume music
    if (backgroundMusic != null) {
      if (paused) {
        backgroundMusic.pause();
      } else {
        backgroundMusic.play();
      }
    }

    // Pause/resume sunlight generation
    if (gameArea != null) {
      gameArea
          .getEntities()
          .forEach(
              entity -> {
                com.csse3200.game.components.currency.CurrencyGeneratorComponent generator =
                    entity.getComponent(
                        com.csse3200.game.components.currency.CurrencyGeneratorComponent.class);
                if (generator != null) {
                  if (paused) {
                    generator.pause();
                  } else {
                    generator.resume();
                  }
                }
              });
    }
  }

  /** Returns whether the game is currently paused */
  public boolean isPaused() {
    return isPaused;
  }
}
