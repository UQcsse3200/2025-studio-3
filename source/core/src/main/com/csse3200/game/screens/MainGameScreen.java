package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.SlotMachineArea;
import com.csse3200.game.components.currency.ScrapHudDisplay;
import com.csse3200.game.components.gamearea.PerformanceDisplay;
import com.csse3200.game.components.hud.PauseButton;
import com.csse3200.game.components.hud.PauseMenu;
import com.csse3200.game.components.hud.PauseMenuActions;
import com.csse3200.game.components.waves.CurrentWaveDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.*;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The game screen containing the main game.
 *
 * <p>Details on libGDX screens: https://happycoding.io/tutorials/libgdx/game-screens
 */
public class MainGameScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(MainGameScreen.class);
  private Music music;
  private List<String> textureAtlases = new ArrayList<>();
  private static final String[] MAIN_GAME_TEXTURES = {
    "images/backgrounds/level-1-map-v2.png",
    "images/backgrounds/level-2-map-v1.png",
    "images/entities/minigames/selected_star.png",
    "images/entities/defences/sling_shooter_1.png",
    "images/entities/defences/sling_shooter_front.png",
    "images/effects/grenade.png",
    "images/effects/coffee.png",
    "images/effects/emp.png",
    "images/effects/buff.png",
    "images/effects/nuke.png",
    "images/entities/defences/forge_1.png",
    "images/effects/sling_projectile.png",
    "images/effects/sling_projectile_pad.png",
    "images/entities/currency/scrap_metal.png",
    "images/entities/enemies/Scrap-titan.png",
          "images/entities/enemies/Scrap-titan2.png",
          "images/entities/enemies/Scrap-titan3.png",
          "images/entities/enemies/Scrap-titan4.png",
          "images/entities/enemies/Scrap-titan5.png",
          "images/entities/enemies/Scrap-titan6.png",


  };
  private static final String[] MAIN_GAME_TEXTURE_ATLASES = {
    "images/entities/defences/sling_shooter.atlas",
    "images/entities/enemies/robot_placeholder.atlas",
    "images/entities/enemies/basic_robot.atlas",
    "images/effects/grenade.atlas",
    "images/effects/coffee.atlas",
    "images/effects/emp.atlas",
    "images/effects/buff.atlas",
    "images/entities/defences/forge.atlas",
    "images/effects/nuke.atlas",
    "images/entities/enemies/blue_robot.atlas",
    "images/entities/enemies/red_robot.atlas",
    "images/entities/enemies/Scrap-titan.atlas"
  };
  private static final Vector2 CAMERA_POSITION = new Vector2(7.5f, 7.5f);
  protected final GdxGame game;
  protected final Renderer renderer;
  protected final PhysicsEngine physicsEngine;
  protected final WaveManager waveManager;
  protected LevelGameArea gameArea;
  protected boolean isPaused = false;
  private List<String> textures = new ArrayList<>();
  private String level;

  /**
   * Constructor for the main game screen.
   *
   * @param game the game instance
   */
  public MainGameScreen(GdxGame game) {
    this.game = game;
    logger.debug("[MainGameScreen] Initialising main game screen");
    level = ServiceLocator.getProfileService().getProfile().getCurrentLevel();
    logger.debug("[MainGameScreen] Profile current level: '{}'", level);
    logger.debug("[MainGameScreen] Converted to level key: '{}'", level);
    this.waveManager = new WaveManager(level);
    logger.debug("[MainGameScreen] Initialising main game screen services");
    ServiceLocator.registerTimeSource(new GameTime());
    PhysicsService physicsService = new PhysicsService();
    ServiceLocator.registerPhysicsService(physicsService);
    physicsEngine = physicsService.getPhysics();
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerCurrencyService(new CurrencyService(50, 10000));
    ServiceLocator.registerItemEffectsService(new ItemEffectsService());

    renderer = RenderFactory.createRenderer();
    renderer.getCamera().getEntity().setPosition(CAMERA_POSITION);
    renderer.getDebug().renderPhysicsWorld(physicsEngine.getWorld());

    loadAssets();
    createUI();

    logger.debug("Initialising main game screen entities");
    gameArea = createGameArea();
    // Wire WaveManager spawn callback to LevelGameArea.spawnRobot with enum
    // conversion
      waveManager.setEnemySpawnCallback(new WaveManager.EnemySpawnCallback() {
          @Override
          public void spawnEnemy(int col, int row, String robotType) {
              if (gameArea != null) {
                  gameArea.spawnRobot(col, row, RobotFactory.RobotType.valueOf(robotType.toUpperCase()));
              }
          }

          @Override
          public void spawnBoss(int row) {
              if (gameArea != null) {
                  gameArea.spawnBoss(row);
              }
          }
      });
    gameArea.create();

    snapCameraBottomLeft();
    waveManager.initialiseNewWave();
  }

  @Override
  public void render(float delta) {
    if (!isPaused) {
      physicsEngine.update();
      ServiceLocator.getEntityService().update();
      waveManager.update(delta);
    }

    renderer.render();
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

    // Load Item Textures
    for (BaseItemConfig item : ServiceLocator.getConfigService().getItemConfigValues()) {
      textures.add(item.getAssetPath());
    }

    // Load Enemy Textures and Atlases
    for (BaseEnemyConfig enemy : ServiceLocator.getConfigService().getEnemyConfigValues()) {
      textures.add(enemy.getAssetPath());
      textureAtlases.add(enemy.getAtlasPath());
    }

    // Load Defender Textures and Atlases
    for (BaseDefenderConfig defender :
        ServiceLocator.getConfigService().getDefenderConfigValues()) {
      textures.add(defender.getAssetPath());
      textureAtlases.add(defender.getAtlasPath());
    }

    // Load Generator Textures
    for (BaseGeneratorConfig generator :
        ServiceLocator.getConfigService().getGeneratorConfigValues()) {
      textures.add(generator.getAssetPath());
    }

    // Load Music & Sounds
    resourceService.loadMusic(new String[] {"sounds/BGM_03_mp3.mp3"});
    resourceService.loadSounds(new String[] {"sounds/Impact4.ogg"});

    // Load Textures
    resourceService.loadTextures(MAIN_GAME_TEXTURES);
    resourceService.loadTextures(textures.toArray(new String[0]));
    resourceService.loadTextureAtlases(MAIN_GAME_TEXTURE_ATLASES);
    resourceService.loadTextureAtlases(textureAtlases.toArray(new String[0]));
    ServiceLocator.getResourceService().loadAll();
    music = ServiceLocator.getResourceService().getAsset("sounds/BGM_03_mp3.mp3", Music.class);
    music.setLooping(true);
    music.setVolume(0.3f);
    music.play();
  }

  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(MAIN_GAME_TEXTURES);
  }

  /**
   * Creates the main game's ui including components for rendering ui elements to* the screen and
   * capturing and handling ui input.
   */
  protected void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();

    Entity ui = new Entity();
    ui.addComponent(new InputDecorator(stage, 10))
        .addComponent(new PerformanceDisplay())
        .addComponent(new PauseButton())
        .addComponent(new PauseMenu())
        .addComponent(new PauseMenuActions(this.game))
        .addComponent(new Terminal())
        .addComponent(ServiceLocator.getInputService().getInputFactory().createForTerminal())
        .addComponent(new TerminalDisplay())
        .addComponent(new CurrentWaveDisplay(waveManager))
        .addComponent(new ScrapHudDisplay());

    // Add event listeners for pause/resume to the UI entity
    ui.getEvents().addListener("pause", this::handlePause);
    ui.getEvents().addListener("resume", this::handleResume);

    // Connect the CurrentWaveDisplay to the WaveManager for event listening
    waveManager.setWaveEventListener(
        new WaveManager.WaveEventListener() {
          @Override
          public void onPreparationPhaseStarted(int waveNumber) {
            // CurrentWaveDisplay will handle this internally
          }

          @Override
          public void onWaveChanged(int waveNumber) {
            // CurrentWaveDisplay will handle this internally
          }

          @Override
          public void onWaveStarted(int waveNumber) {
            // CurrentWaveDisplay will handle this internally
          }
        });

    ServiceLocator.getEntityService().register(ui);
  }

  /**
   * Factory method for creating the game area. Automatically detects whether to create a regular
   * level or slot machine area based on the level configuration.
   */
  protected LevelGameArea createGameArea() {
    BaseLevelConfig cfg = ServiceLocator.getConfigService().getLevelConfig(level);
    if (cfg != null && cfg.isSlotMachine()) {
      var slot = new SlotMachineArea(level);
      slot.setWaveManager(this.waveManager);
      return slot;
    } else {
      var area = new LevelGameArea(level);
      area.setWaveManager(this.waveManager);
      return area;
    }
  }

  /** Snaps the camera to the bottom left of the screen */
  private void snapCameraBottomLeft() {
    var cam = renderer.getCamera();

    float viewportWidth = cam.getCamera().viewportWidth;
    float viewportHeight = cam.getCamera().viewportHeight;

    cam.getEntity().setPosition(viewportWidth / 2f, viewportHeight / 2f);
  }

  /** Event handler for pause events */
  private void handlePause() {
    logger.info("[MainGameScreen] Game paused");
    music.pause();
    // Pause currency generation, pause wave manager, pause generators.
  }

  /** Event handler for resume events */
  private void handleResume() {
    logger.info("[MainGameScreen] Game resumed");
    music.play();
    // Resume currency generation, resume wave manager, resume generators.
  }
}
