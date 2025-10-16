package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.minigame.*;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsComponent.AlignX;
import com.csse3200.game.physics.components.PhysicsComponent.AlignY;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.MinigameService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaneRunnerScreen extends ScreenAdapter {

  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.screens.LaneRunnerScreen.class);
  private final GdxGame game;
  private final Renderer renderer;
  private LaneManager laneManager;
  private boolean gameOverDialogShown = false;
  private float spawnTimer = 0f;
  private final java.util.Random random = new java.util.Random();
  private com.badlogic.gdx.physics.box2d.Box2DDebugRenderer debugRenderer;
  private boolean gameElementsCreated = false;
  private static final String[] laneRunnerTextures = {
    "images/entities/minigames/Bomb.png",
    "images/backgrounds/Background.png",
    "images/backgrounds/GameOver.png",
    "images/backgrounds/lanes.png",
    "images/entities/character.png"
  };

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
    
    // Initialize debug renderer for physics debugging
    debugRenderer = new com.badlogic.gdx.physics.box2d.Box2DDebugRenderer();
    
    renderer.getCamera().getEntity().setPosition(640f, 360f);
    this.laneManager = new LaneManager(Gdx.graphics.getWidth());
    ServiceLocator.getMusicService().play("sounds/background-music/level3_music.mp3");
    loadAssets();
    createUI();
    createGameElements();
  }

  /** Loads the lane runner game's assets. */
  private void loadAssets() {
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(laneRunnerTextures);
    resourceService.loadAll();
  }


  private void createUI() {
    logger.debug("[LaneRunnerScreen] Creating UI");
    Stage stage = ServiceLocator.getRenderService().getStage();
    
    // Create the lane runner game UI
    Entity ui = new Entity()
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new MinigameHUD());

    ServiceLocator.getEntityService().register(ui);

    // Background removed as requested
  }


  /** Creates the game elements. */
  private void createGameElements() {
    if (gameElementsCreated) {
      logger.warn("[LaneRunnerScreen] Game elements already created, skipping");
      return;
    }
    
    logger.info("[LaneRunnerScreen] Creating game elements");
    gameElementsCreated = true;
    
    // Create player entity with components
    ColliderComponent playerCollider = new ColliderComponent();
    Entity player = new Entity()
        .addComponent(new TextureRenderComponent(ServiceLocator.getResourceService()
            .getAsset("images/entities/character.png", Texture.class)))
        .addComponent(new LaneRunnerPlayerComponent(laneManager))
        .addComponent(new PhysicsComponent())
        .addComponent(playerCollider)
        .addComponent(new LaneRunnerPlayerCollisionComponent());
    
    // Enable continuous collision detection for player
    player.getComponent(PhysicsComponent.class).getBody().setBullet(true);
    
    playerCollider.setAsBox(new Vector2(16f, 16f))
        .setCollisionFilter(PhysicsLayer.PLAYER, PhysicsLayer.PROJECTILE);
    
    // Set player scale and position
    player.setScale(32f, 32f);
    player.setPosition(laneManager.getLaneCenter(1), 100f);
    logger.info("[LaneRunnerScreen] Player created at lane center with scale (32, 32)");
    ServiceLocator.getEntityService().register(player);
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
    
    // Render physics debug information
    if (debugRenderer != null) {
      debugRenderer.render(ServiceLocator.getPhysicsService().getPhysics().getWorld(), 
          renderer.getCamera().getCamera().combined);
    }
    
    if (ServiceLocator.getMinigameService().isGameOver() && !gameOverDialogShown) {
      handleGameOver();
    }
  }

  /**
   * Spawns a new obstacle in a random lane.
   */
  private void spawnObstacle() {
    int laneIndex = random.nextInt(laneManager.getNumLanes());
    float x = laneManager.getLaneCenter(laneIndex);
    float y = Gdx.graphics.getHeight();

    // Create obstacle entity with components
    ColliderComponent obstacleCollider = new ColliderComponent();
    Entity obstacle = new Entity()
        .addComponent(new TextureRenderComponent(ServiceLocator.getResourceService()
            .getAsset("images/entities/minigames/Bomb.png", Texture.class)))
        .addComponent(new LaneRunnerObstacleComponent(3f))
        .addComponent(new PhysicsComponent())
        .addComponent(obstacleCollider);
    
    // Enable continuous collision detection for obstacles
    obstacle.getComponent(PhysicsComponent.class).getBody().setBullet(true);
    
    obstacleCollider.setAsBox(new Vector2(32f, 32f))
        .setCollisionFilter(PhysicsLayer.PROJECTILE, PhysicsLayer.PLAYER);
    obstacleCollider.setAsBoxAligned(new Vector2(32f, 32f), AlignX.CENTER, AlignY.CENTER);

    obstacle.setScale(32f, 32f);
    obstacle.setPosition(x, y);
    
    ServiceLocator.getEntityService().register(obstacle);
  }

  /**
   * Handles the game over state.
   */
  private void handleGameOver() {
    // Add achievement and coins
    int score = ServiceLocator.getMinigameService().getScore();
    if (score >= 10) {
      ServiceLocator.getProfileService().getProfile().getStatistics().incrementStatistic("laneRunnerCompleted");
    }
    ServiceLocator.getProfileService().getProfile().getWallet().addCoins(Math.floorDiv(score, 3));

    // Show game over dialog
    gameOverDialogShown = true;
    String title = "Game Over";
    float time = ServiceLocator.getTimeSource().getTime();
    String message = String.format(
        "Final Score: %d%nSurvival Time: %.2fs",
        score, time / 1000f);
    ServiceLocator
        .getDialogService()
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

    if (debugRenderer != null) {
      debugRenderer.dispose();
    }
    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();

    logger.debug("[LaneRunnerScreen] Services cleared");
    ServiceLocator.clear();
  }

}
