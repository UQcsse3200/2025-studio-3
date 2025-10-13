package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Interpolation;
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
import com.csse3200.game.components.hud.SpeedControlDisplay;
import com.csse3200.game.components.waves.CurrentWaveDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.factories.BossFactory;
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
 * <p>Details on libGDX screens: <a
 * href="https://happycoding.io/tutorials/libgdx/game-screens">...</a>
 */
public class MainGameScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(MainGameScreen.class);
  private List<String> textureAtlases = new ArrayList<>();
  private static final String[] MAIN_GAME_TEXTURES = {
    "images/backgrounds/level_map_grass.png",
    "images/backgrounds/level_map_town.png",
    "images/backgrounds/level_map_final.png",
    "images/entities/enemies/samurai_Bot.png",
    "images/entities/enemies/samurai_Bot2.png",
    "images/entities/enemies/samurai_Bot3.png",
    "images/entities/enemies/samurai_Bot4.png",
    "images/entities/enemies/samurai_Bot5.png",
    "images/entities/enemies/samurai_Bot6.png",
    "images/entities/enemies/samurai_Bot7.png",
    "images/entities/enemies/samurai_Bot8.png",
    "images/entities/enemies/samurai_Bot9.png",
    "images/entities/enemies/samurai_Bot10.png",
    "images/entities/enemies/samurai_Bot11.png",
    "images/entities/enemies/gun_Bot.png",
    "images/entities/enemies/gun_Bot2.png",
    "images/entities/enemies/gun_Bot3.png",
    "images/entities/enemies/gun_Bot4.png",
    "images/entities/enemies/gun_Bot5.png",
    "images/entities/enemies/gun_Bot6.png",
    "images/entities/enemies/gun_Bot7.png",
    "images/entities/enemies/gun_Bot8.png",
    "images/entities/enemies/gun_Bot9.png",
    "images/entities/minigames/selected_star.png",
    "images/entities/defences/sling_shooter_1.png",
    "images/entities/defences/shield_1.png",
    "images/entities/defences/healer_1.png",
    "images/entities/defences/shadow_idle1.png",
    "images/entities/defences/army_guy_1.png",
    "images/entities/defences/harpoon0.png",
    "images/entities/defences/sling_shooter_front.png",
    "images/effects/grenade.png",
    "images/effects/coffee.png",
    "images/effects/emp.png",
    "images/effects/buff.png",
    "images/effects/nuke.png",
    "images/entities/defences/forge_1.png",
    "images/effects/sling_projectile.png",
    "images/effects/bullet.png",
    "images/effects/shock.png",
    "images/effects/default_projectile.png",
    "images/effects/sling_projectile_pad.png",
    "images/effects/harpoon_projectile.png",
    "images/entities/currency/scrap_metal.png",
    "images/effects/shell.png",
    "images/entities/currency/scrap_metal.png",
    "images/entities/slotmachine/slot_reels_background.png",
    "images/entities/enemies/Scrap-titan.png",
    "images/entities/enemies/Scrap-titan2.png",
    "images/entities/enemies/Scrap-titan3.png",
    "images/entities/enemies/Scrap-titan4.png",
    "images/entities/enemies/Scrap-titan5.png",
    "images/entities/enemies/Scrap-titan6.png",
    "images/effects/gun_bot_fireball.png",
  };
  private static final String[] MAIN_GAME_TEXTURE_ATLASES = {
    "images/entities/defences/sling_shooter.atlas",
    "images/entities/defences/shield.atlas",
    "images/entities/defences/healer.atlas",
    "images/entities/enemies/robot_placeholder.atlas",
    "images/entities/enemies/standard_robot.atlas",
    "images/effects/grenade.atlas",
    "images/effects/coffee.atlas",
    "images/effects/emp.atlas",
    "images/effects/buff.atlas",
    "images/entities/defences/forge.atlas",
    "images/effects/nuke.atlas",
    "images/entities/enemies/Scrap-titan.atlas",
    "images/entities/enemies/samurai_Bot.atlas",
    "images/entities/enemies/gun_Bot.atlas",
    "images/entities/defences/mortar.atlas",
    "images/entities/slotmachine/slot_frame.atlas",
    "images/entities/slotmachine/slot_reels.atlas",
    "images/entities/slotmachine/pie_filled.atlas",
    "images/entities/enemies/fast_robot.atlas",
    "images/entities/enemies/tanky_robot.atlas"
  };
  private static final Vector2 CAMERA_POSITION = new Vector2(7.5f, 7.5f);
  protected final GdxGame game;
  protected final Renderer renderer;
  protected final PhysicsEngine physicsEngine;
  protected LevelGameArea gameArea;
  protected boolean isPaused = false;
  private final List<String> textures = new ArrayList<>();
  private final String level;

  private enum PanPhase {
    RIGHT,
    LEFT,
    DONE
  }

  private PanPhase panPhase;
  private float panElapsed;
  private static final float PAN_DURATION = 3f; // seconds
  private boolean doIntroPan = true;
  private final float panStartX;
  private final float panTargetX;

  /** Optional override for which level to load. If null, fall back to profile.currentLevel */
  private final String overrideLevelKey;

  private static final String[] SOUNDS = {
    "sounds/item_buff.mp3",
    "sounds/item_coffee.mp3",
    "sounds/item_emp.mp3",
    "sounds/item_grenade.mp3",
    "sounds/item_nuke.mp3",
    "sounds/damage.mp3",
    "sounds/robot-attack.mp3",
    "sounds/slingshooter-place.mp3",
    "sounds/forge-place.mp3",
    "sounds/human-death.mp3",
    "sounds/mortar-place.mp3",
    "sounds/shadow-place.mp3",
    "sounds/shield-place.mp3",
    "sounds/shooter-place.mp3",
    "sounds/robot-death.mp3",
    "sounds/generator-death.mp3",
    "sounds/game-over-voice.mp3"
  };

  /**
   * Constructor for the main game screen. Falls back to profile.currentLevel.
   *
   * @param game the game instance
   */
  public MainGameScreen(GdxGame game) {
    this(game, null);
  }

  /**
   * Constructor for the main game screen with an explicit level key. If {@code levelKey} is null or
   * blank, the screen will fall back to using the current profile's currentLevel.
   *
   * @param game the game instance
   * @param levelKey the explicit level key to load (e.g., "levelThree")
   */
  public MainGameScreen(GdxGame game, String levelKey) {
    this.game = game;
    logger.debug("[MainGameScreen] Initialising main game screen");
    this.overrideLevelKey = (levelKey != null && !levelKey.isBlank()) ? levelKey : null;

    // Resolve which level to load
    this.level = resolveLevelToLoad();
    logger.debug("[MainGameScreen] Effective level to load: '{}'", level);
    logger.debug("[MainGameScreen] Initialising main game screen services");

    ServiceLocator.registerTimeSource(new GameTime());
    PhysicsService physicsService = new PhysicsService();
    ServiceLocator.registerPhysicsService(physicsService);
    physicsEngine = physicsService.getPhysics();
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerCurrencyService(new CurrencyService(100, 10000));
    ServiceLocator.registerItemEffectsService(new ItemEffectsService());
    ServiceLocator.registerWaveService(new WaveService());
    renderer = RenderFactory.createRenderer();
    renderer.getCamera().getEntity().setPosition(CAMERA_POSITION);
    renderer.getDebug().renderPhysicsWorld(physicsEngine.getWorld());

    loadAssets();
    createUI();

    logger.debug("Initialising main game screen entities");
    gameArea = createGameArea();
    // Wire WaveService spawn callback to LevelGameArea.spawnRobot with enum conversion
    // Wire WaveService spawn callback to LevelGameArea.spawnRobot with enum conversion
    ServiceLocator.getWaveService()
        .setEnemySpawnCallback(
            new WaveService.EnemySpawnCallback() {
              @Override
              public void spawnEnemy(int col, int row, String robotType) {
                gameArea.spawnRobot(
                    col, row, RobotFactory.RobotType.valueOf(robotType.toUpperCase()));
              }

              @Override
              public void spawnBoss(int row, BossFactory.BossTypes bossType) {
                gameArea.spawnBoss(row, bossType);
              }
            });
    gameArea.create();
    snapCameraBottomLeft();
    ServiceLocator.getWaveService().initialiseNewWave();

    // Setup for camera pan
    var camComp = renderer.getCamera();
    float halfVW = camComp.getCamera().viewportWidth / 2f;
    float worldWidth = gameArea.getWorldWidth();
    panStartX = halfVW; // current
    panTargetX = Math.clamp(halfVW + (worldWidth - halfVW) * 0.35f, halfVW, worldWidth - halfVW);
    panElapsed = 0f;
    panPhase = PanPhase.RIGHT;
  }

  /**
   * Determine which level should be loaded. Preference order: 1) An explicit override supplied to
   * the constructor. 2) The current profile's currentLevel, if available. 3) Fallback to
   * "levelOne".
   */
  private String resolveLevelToLoad() {
    try {
      if (overrideLevelKey != null) {
        return overrideLevelKey; // manual selection takes precedence
      }
      var ps = ServiceLocator.getProfileService();
      if (ps != null && ps.getProfile() != null) {
        String cur = ps.getProfile().getCurrentLevel();
        if (cur != null && !cur.isBlank()) {
          return cur;
        }
      }
    } catch (Exception e) {
      logger.warn("[MainGameScreen] Failed to read currentLevel: {}", e.getMessage());
    }
    return "levelOne";
  }

  @Override
  public void render(float delta) {
    if (!isPaused) {
      // Use scaled delta for systems that accept it
      float scaledDelta = ServiceLocator.getTimeSource().getDeltaTime();
      physicsEngine.update();
      ServiceLocator.getEntityService().update();
      ServiceLocator.getWaveService().update(scaledDelta);
    }

    if (doIntroPan && panPhase == PanPhase.RIGHT && panElapsed == 0f) {
      gameArea.createWavePreview();
    }

    if (doIntroPan) {
      panElapsed += delta;
      float t = Math.min(1f, panElapsed / PAN_DURATION);
      var cam = renderer.getCamera().getCamera();

      if (panPhase == PanPhase.RIGHT) {
        cam.position.x = Interpolation.smoother.apply(panStartX, panTargetX, t);
        cam.update();
        if (t >= 1f) {
          // switch to left pan
          panPhase = PanPhase.LEFT;
          panElapsed = 0f;
        }
      } else if (panPhase == PanPhase.LEFT) {
        cam.position.x = Interpolation.smoother.apply(panTargetX, panStartX, t);
        cam.update();
        if (t >= 1f) {
          panPhase = PanPhase.DONE;
          doIntroPan = false;
          gameArea.clearWavePreview();
        }
      }
    }

    renderer.render();
    gameArea.checkGameOver(); // check game-over state
    gameArea.checkLevelComplete(); // check level-complete state
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
    resourceService.loadSounds(SOUNDS);
    resourceService.loadMusic(new String[] {"sounds/background-music/level1_music.mp3"});

    // Load Textures
    resourceService.loadTextures(MAIN_GAME_TEXTURES);
    resourceService.loadTextures(textures.toArray(new String[0]));
    resourceService.loadTextureAtlases(MAIN_GAME_TEXTURE_ATLASES);
    resourceService.loadTextureAtlases(textureAtlases.toArray(new String[0]));
    ServiceLocator.getResourceService().loadAll();
    ServiceLocator.getMusicService().play("sounds/background-music/level1_music.mp3");
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

    BaseLevelConfig cfgForUi = ServiceLocator.getConfigService().getLevelConfig(level);
    boolean isSlotLevel = cfgForUi != null && cfgForUi.isSlotMachine();

    Entity ui = new Entity();
    ui.addComponent(new InputDecorator(stage, 10))
        .addComponent(new PerformanceDisplay())
        .addComponent(new PauseButton())
        .addComponent(new PauseMenu())
        .addComponent(new SpeedControlDisplay())
        .addComponent(new PauseMenuActions(this.game))
        .addComponent(new Terminal())
        .addComponent(ServiceLocator.getInputService().getInputFactory().createForTerminal())
        .addComponent(new TerminalDisplay())
        .addComponent(new CurrentWaveDisplay());

    if (!isSlotLevel) {
      ui.addComponent(new ScrapHudDisplay());
    }

    // Add event listeners for pause/resume to the UI entity
    ui.getEvents().addListener("pause", this::handlePause);
    ui.getEvents().addListener("resume", this::handleResume);

    // Connect the CurrentWaveDisplay to the WaveService for event listening
    ServiceLocator.getWaveService()
        .setWaveEventListener(
            new WaveService.WaveEventListener() {
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
      return new SlotMachineArea(level);
    } else {
      return new LevelGameArea(level);
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
    ServiceLocator.getMusicService().pause();
    // Pause currency generation, pause wave manager, pause generators.
  }

  /** Event handler for resume events */
  private void handleResume() {
    logger.info("[MainGameScreen] Game resumed");
    ServiceLocator.getMusicService().resume();
    // Resume currency generation, resume wave manager, resume generators.
  }
}
