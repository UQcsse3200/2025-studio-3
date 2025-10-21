package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.minigame.BackgroundRenderComponent;
import com.csse3200.game.components.minigame.BallComponent;
import com.csse3200.game.components.minigame.MinigameHUD;
import com.csse3200.game.components.minigame.PaddleCollisionComponent;
import com.csse3200.game.components.minigame.PaddleComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.MinigameService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The game screen containing the paddle game. */
public class PaddleGameScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(PaddleGameScreen.class);
  private final GdxGame game;
  private final Renderer renderer;
  private boolean gameOverDialogShown = false;

  /**
   * Creates a new PaddleGameScreen and initializes the stage, input processor, and assets.
   *
   * @param game the game instance
   */
  public PaddleGameScreen(GdxGame game) {
    this.game = game;
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerTimeSource(new GameTime());
    ServiceLocator.registerPhysicsService(new PhysicsService());
    ServiceLocator.registerMinigameService(new MinigameService());

    renderer = RenderFactory.createRenderer();
    logger.debug("[PaddleGameScreen] Renderer created");

    renderer.getCamera().getEntity().setPosition(640f, 360f);
    loadAssests();
    createUI();
    createGameElements();

    ServiceLocator.getMusicService().play("sounds/background-music/level2_music.mp3");
  }

  /** Loads the paddle game's assets. */
  private void loadAssests() {
    ResourceService resourceService = ServiceLocator.getResourceService();
    String[] textures = {
      "images/entities/minigames/paddle_new.png",
      "images/entities/minigames/ball_new.png",
      "images/backgrounds/WallPongbg.png"
    };
    resourceService.loadTextures(textures);
    
    // Load sounds
    String[] sounds = {"sounds/bounce.mp3"};
    resourceService.loadSounds(sounds);
    
    resourceService.loadAll();
  }

  private void createUI() {
    logger.debug("[PaddleGameScreen] Creating UI");
    Stage stage = ServiceLocator.getRenderService().getStage();

    // Create the paddle game UI
    Entity ui =
        new Entity().addComponent(new InputDecorator(stage, 10)).addComponent(new MinigameHUD());

    ServiceLocator.getEntityService().register(ui);
  }

  /** Creates the game elements. */
  private void createGameElements() {
    logger.info("[PaddleGameScreen] Creating game elements");

    // Create background entity with custom render component
    Entity backgroundEntity =
        new Entity()
            .addComponent(new BackgroundRenderComponent("images/backgrounds/WallPongbg.png"));
    ServiceLocator.getEntityService().register(backgroundEntity);

    // Create paddle entity
    ColliderComponent paddleCollider = new ColliderComponent();
    Entity paddle =
        new Entity()
            .addComponent(
                new TextureRenderComponent(
                    ServiceLocator.getResourceService()
                        .getAsset("images/entities/minigames/paddle_new.png", Texture.class)))
            .addComponent(new PaddleComponent())
            .addComponent(new PaddleCollisionComponent())
            .addComponent(new PhysicsComponent())
            .addComponent(paddleCollider);
    paddleCollider.setAsBox(new Vector2(100f, 30f)).setLayer(PhysicsLayer.PLAYER);

    // Set paddle scale and position
    paddle.setScale(100f, 30f);
    paddle.setPosition(590f, 50f);
    logger.info("[PaddleGameScreen] Paddle created at (590, 100) with scale (100, 50)");
    ServiceLocator.getEntityService().register(paddle);

    // Create ball entity
    ColliderComponent ballCollider = new ColliderComponent();
    Entity ball =
        new Entity()
            .addComponent(
                new TextureRenderComponent(
                    ServiceLocator.getResourceService()
                        .getAsset("images/entities/minigames/ball_new.png", Texture.class)))
            .addComponent(new BallComponent())
            .addComponent(new PhysicsComponent())
            .addComponent(ballCollider);
    ballCollider.setAsBox(new Vector2(20f, 20f)).setLayer(PhysicsLayer.PROJECTILE);

    // Set ball scale and position
    ball.setScale(20f, 20f);
    ball.setPosition(640f, 400f);
    logger.info("[PaddleGameScreen] Ball created at (640, 400) with scale (20, 20)");
    ServiceLocator.getEntityService().register(ball);
  }

  @Override
  public void render(float delta) {
    // Only update game logic if not game over
    if (!ServiceLocator.getMinigameService().isGameOver()) {
      ServiceLocator.getPhysicsService().getPhysics().update();
      ServiceLocator.getEntityService().update();
    }

    renderer.render();

    if (ServiceLocator.getMinigameService().isGameOver() && !gameOverDialogShown) {
      handleGameOver();
    }
  }

  /** Handles the game over state. */
  private void handleGameOver() {
    // Add achievement and coins
    int score = ServiceLocator.getMinigameService().getScore();
    if (score >= 10) {
      ServiceLocator.getProfileService()
          .getProfile()
          .getStatistics()
          .incrementStatistic("paddleGameCompleted");
    }
    ServiceLocator.getProfileService().getProfile().getWallet().addCoins(score);

    // Show game over dialog
    gameOverDialogShown = true;
    String title = "Game Over";
    float time = ServiceLocator.getTimeSource().getTime();
    String message = String.format("Final Score: %d%nSurvival Time: %.2fs", score, time / 1000f);
    ServiceLocator.getDialogService()
        .gameOver(
            title,
            message,
            d -> game.setScreen(GdxGame.ScreenType.PADDLE_GAME),
            d -> game.setScreen(GdxGame.ScreenType.MINI_GAMES));
  }

  @Override
  public void resize(int width, int height) {
    logger.debug("[PaddleGameScreen] Resized renderer: ({} x {})", width, height);
    renderer.resize(width, height);
  }

  @Override
  public void dispose() {
    logger.debug("[PaddleGameScreen] Disposing");

    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();

    logger.debug("[PaddleGameScreen] Services cleared");
    ServiceLocator.clear();
  }
}
