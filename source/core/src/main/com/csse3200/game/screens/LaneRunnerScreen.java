package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.minigame.*;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsComponent.AlignX;
import com.csse3200.game.physics.components.PhysicsComponent.AlignY;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.MinigameService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The Lane Runner screen. */
public class LaneRunnerScreen extends ScreenAdapter {
  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.screens.LaneRunnerScreen.class);
  private final GdxGame game;
  private final Renderer renderer;
  public static final int NUM_LANES = 3;
  public static final float LANE_WIDTH = 1280f / NUM_LANES;
  public static final float LANE_CENTER = LANE_WIDTH / 2;
  private boolean gameOverDialogShown = false;
  private float spawnTimer = 0f;
  private final Random random = new java.util.Random();
  private static final String MOVESOUND = "sounds/lane_move.mp3";
  private static final String[] laneRunnerTextures = {
    "images/entities/minigames/Bomb.png",
    "images/backgrounds/lanes.png",
    "images/entities/character.png"
  };

  /**
   * Creates a new Lane Runner screen.
   *
   * @param game the game instance
   */
  public LaneRunnerScreen(GdxGame game) {
    this.game = game;
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerTimeSource(new GameTime());
    ServiceLocator.registerPhysicsService(new PhysicsService());
    ServiceLocator.registerMinigameService(new MinigameService());

    renderer = RenderFactory.createRenderer();
    logger.debug("[LaneRunnerScreen] Renderer created");

    renderer.getCamera().getEntity().setPosition(640f, 360f);
    loadAssets();
    createUI();
    createGameElements();

    ServiceLocator.getMusicService().play("sounds/background-music/level3_music.mp3");
  }

  /** Loads the lane runner game's assets. */
  private void loadAssets() {
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(laneRunnerTextures);
    resourceService.loadAll();
    ServiceLocator.getResourceService().loadSounds(new String[] {MOVESOUND});
    ServiceLocator.getResourceService().loadAll();
  }

  /** Creates the UI for the lane runner game. */
  private void createUI() {
    logger.debug("[LaneRunnerScreen] Creating UI");
    Stage stage = ServiceLocator.getRenderService().getStage();

    // Create the lane runner game UI
    Entity ui =
        new Entity().addComponent(new InputDecorator(stage, 10)).addComponent(new MinigameHUD());

    ServiceLocator.getEntityService().register(ui);
  }

  /** Creates the game elements. */
  private void createGameElements() {
    createBackground();

    // Create player entity with components
    ColliderComponent playerCollider = new ColliderComponent();
    Entity player =
        new Entity()
            .addComponent(
                new TextureRenderComponent(
                    ServiceLocator.getResourceService()
                        .getAsset("images/entities/character.png", Texture.class)))
            .addComponent(new LaneRunnerPlayerComponent())
            .addComponent(new PhysicsComponent())
            .addComponent(playerCollider)
            .addComponent(new LaneRunnerPlayerCollisionComponent());
    player.getComponent(PhysicsComponent.class).getBody().setBullet(true);
    playerCollider
        .setAsBoxAligned(new Vector2(64f, 64f), AlignX.LEFT, AlignY.BOTTOM)
        .setCollisionFilter(PhysicsLayer.PLAYER, PhysicsLayer.PROJECTILE);
    player.setScale(64f, 64f);
    player.setPosition(1 * LANE_WIDTH + LANE_CENTER - 32f, 50f);
    ServiceLocator.getEntityService().register(player);
  }

  /** Creates the background for the lane runner game. */
  private void createBackground() {
    var camera = renderer.getCamera();
    float worldWidth = camera.getCamera().viewportWidth;
    float worldHeight = camera.getCamera().viewportHeight;
    Entity background =
        new Entity().addComponent(new BackgroundRenderComponent("images/backgrounds/lanes.png"));

    // Scale the background to fill the world view
    background.setScale(worldWidth, worldHeight);
    background.setPosition(
        camera.getEntity().getPosition().x - worldWidth / 2f,
        camera.getEntity().getPosition().y - worldHeight / 2f);

    // Register background FIRST to ensure it's drawn behind everything
    ServiceLocator.getEntityService().register(background);
  }

  @Override
  public void render(float delta) {
    // Only update game logic if not game over
    if (!ServiceLocator.getMinigameService().isGameOver()) {
      ServiceLocator.getPhysicsService().getPhysics().update();
      ServiceLocator.getEntityService().update();

      // Spawn obstacles
      spawnTimer += delta;
      if (spawnTimer >= 1.5f) {
        spawnObstacle();
        spawnTimer = 0f;
      }
    }

    renderer.render();

    if (ServiceLocator.getMinigameService().isGameOver() && !gameOverDialogShown) {
      handleGameOver();
    }
  }

  /** Spawns a new obstacle in a random lane. */
  private void spawnObstacle() {
    int laneIndex = random.nextInt(NUM_LANES);
    float x = laneIndex * LANE_WIDTH + LANE_CENTER;
    float y = Gdx.graphics.getHeight();

    // Create obstacle entity with components
    ColliderComponent obstacleCollider = new ColliderComponent();
    Entity obstacle =
        new Entity()
            .addComponent(
                new TextureRenderComponent(
                    ServiceLocator.getResourceService()
                        .getAsset("images/entities/minigames/Bomb.png", Texture.class)))
            .addComponent(new LaneRunnerObstacleComponent(3f))
            .addComponent(new PhysicsComponent())
            .addComponent(obstacleCollider);
    obstacle.getComponent(PhysicsComponent.class).getBody().setBullet(true);
    obstacleCollider
        .setAsBoxAligned(new Vector2(32f, 32f), AlignX.LEFT, AlignY.BOTTOM)
        .setCollisionFilter(PhysicsLayer.PROJECTILE, PhysicsLayer.PLAYER);
    obstacle.setScale(32f, 32f);
    obstacle.setPosition(x, y);
    ServiceLocator.getEntityService().register(obstacle);
  }

  /** Handles the game over state. */
  private void handleGameOver() {
    // Add achievement and coins
    int score = ServiceLocator.getMinigameService().getScore();
    if (score >= 10) {
      ServiceLocator.getProfileService()
          .getProfile()
          .getStatistics()
          .incrementStatistic("laneRunnerCompleted");
    }
    ServiceLocator.getProfileService().getProfile().getWallet().addCoins(Math.floorDiv(score, 3));
    ServiceLocator.getProfileService()
        .getProfile()
        .getStatistics()
        .incrementStatistic("coinsCollected", Math.floorDiv(score, 3));

    // Show game over dialog
    gameOverDialogShown = true;
    String title = "Game Over";
    float time = ServiceLocator.getTimeSource().getTime();
    String message = String.format("Final Score: %d%nSurvival Time: %.2fs", score, time / 1000f);
    ServiceLocator.getDialogService()
        .gameOver(
            title,
            message,
            d -> game.setScreen(GdxGame.ScreenType.LANE_RUNNER),
            d -> game.setScreen(GdxGame.ScreenType.MINI_GAMES));
  }

  @Override
  public void resize(int width, int height) {
    logger.debug("[LaneRunnerScreen] Resized renderer: ({} x {})", width, height);
    renderer.resize(width, height);
  }

  @Override
  public void dispose() {
    logger.debug("[LaneRunnerScreen] Disposing");

    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();

    logger.debug("[LaneRunnerScreen] Services cleared");
    ServiceLocator.clear();
  }
}
