package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.minigame.BallComponent;
import com.csse3200.game.minigame.PaddleHUD;
import com.csse3200.game.minigame.PaddleComponent;
import com.csse3200.game.minigame.PaddleCollisionComponent;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.MinigameService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.physics.PhysicsService;
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
    String[] textures = {"images/entities/minigames/paddle.png", "images/entities/minigames/ball.png", "images/backgrounds/WallPongbg.png"};
    resourceService.loadTextures(textures);
    resourceService.loadAll();
  }

  private void createUI() {
    logger.debug("[PaddleGameScreen] Creating UI");
    Stage stage = ServiceLocator.getRenderService().getStage();

    // // Add the background image
    // Texture bgTex = ServiceLocator.getResourceService().getAsset("images/backgrounds/WallPongbg.png", Texture.class);
    // Image bg = new Image(new TextureRegionDrawable(new TextureRegion(bgTex)));
    // bg.setFillParent(true);
    // bg.setScaling(Scaling.fill);
    // stage.addActor(bg);

    // Create the paddle game UI
    Entity ui = new Entity()
        .addComponent(new InputDecorator(stage, 10))
        .addComponent(new PaddleHUD());

    ServiceLocator.getEntityService().register(ui);
  }

  /** Creates the game elements. */
  private void createGameElements() {
    logger.info("[PaddleGameScreen] Creating game elements");
    
    // Create paddle entity
    Entity paddle = new Entity()
      .addComponent(new TextureRenderComponent(ServiceLocator.getResourceService().getAsset("images/entities/minigames/paddle.png", Texture.class)))
      .addComponent(new PaddleComponent())
      .addComponent(new PaddleCollisionComponent())
      .addComponent(new PhysicsComponent())
      .addComponent(new ColliderComponent().setLayer(PhysicsLayer.PLAYER));
    
    // Set paddle scale and position
    paddle.setScale(100f, 50f);
    paddle.setPosition(590f, 100f);
    logger.info("[PaddleGameScreen] Paddle created at (590, 100) with scale (100, 50)");
    ServiceLocator.getEntityService().register(paddle);
    
    // Create ball entity
    Entity ball = new Entity()
      .addComponent(new TextureRenderComponent(ServiceLocator.getResourceService().getAsset("images/entities/minigames/ball.png", Texture.class)))
      .addComponent(new BallComponent())
      .addComponent(new PhysicsComponent())
      .addComponent(new ColliderComponent().setLayer(PhysicsLayer.PROJECTILE));
    
    // Set ball scale and position
    ball.setScale(50f, 50f);
    ball.setPosition(640f, 400f);
    logger.info("[PaddleGameScreen] Ball created at (640, 400) with scale (50, 50)");
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

  /**
   * Handles the game over state.
   */
  private void handleGameOver() {
    gameOverDialogShown = true;
    String title = "Game Over";
    float time = ServiceLocator.getTimeSource().getTime();
    int score = ServiceLocator.getMinigameService().getScore();
    String message = String.format(
        "Final Score: %d | Survival Time: %.2fs",
        score, time);
    ServiceLocator
        .getDialogService()
        .gameOver(
            title,
            message,
            d -> game.setScreen(GdxGame.ScreenType.PADDLE_GAME),
            d -> game.setScreen(GdxGame.ScreenType.MINI_GAMES));
    
    // TODO: Add coins + achievement unlock
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
