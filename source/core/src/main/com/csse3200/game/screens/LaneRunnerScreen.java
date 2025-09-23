package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputService;
import com.csse3200.game.minigame.*;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
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
  private Image playerImage;
  private int cureentLane = 1; // Start in the middle lane (0, 1, 2)
  private ObstacleManager obstacleManager;
  private Entity player;
  private boolean gameOver = false;
  private int score = 0;
  private float survivalTime = 0f;
  private float scoreTimer = 0f;
  private Label scoreLabel;
  private Label timeLabel;
  private static final String[] laneRunnerTextures = {
    "images/entities/minigames/Bomb.png",
    "images/backgrounds/Background.png",
    "images/backgrounds/GameOver.png",
    "images/backgrounds/lanes.png",
    "images/entities/character.png"
  };

  public LaneRunnerScreen(GdxGame game) {
    this.game = game;

    logger.debug("Initialising lane runner mini game screen services");
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerPhysicsService(new PhysicsService());

    renderer = RenderFactory.createRenderer();
    this.laneManager = new LaneManager(Gdx.graphics.getWidth());
    loadAssets();
    createUI();
    initializeObstacles();
  }

  private void loadAssets() {
    logger.debug("Loading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(laneRunnerTextures);
    ServiceLocator.getResourceService().loadAll();
  }

  private void addBackground(String texturePath, float width, float height) {
    Texture bgTex = ServiceLocator.getResourceService().getAsset(texturePath, Texture.class);
    Image background = new Image(bgTex);
    background.setSize(width, height);
    background.setPosition(0, 0);
    ServiceLocator.getRenderService().getStage().addActor(background);
  }

  private void createUI() {
    logger.debug("Creating UI");
    Stage stage = ServiceLocator.getRenderService().getStage();

    addBackground(
        "images/backgrounds/Background.png", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    // Create the background image
    Texture bgTex =
        ServiceLocator.getResourceService().getAsset("images/backgrounds/lanes.png", Texture.class);
    Image background = new Image(bgTex);

    // Calculate the correct width to avoid stretching, based on our lane logic
    float totalLaneWidth = laneManager.getLaneWidth() * laneManager.getNumLanes();
    float screenHeight = Gdx.graphics.getHeight();
    background.setSize(totalLaneWidth, screenHeight);

    // Center the background on the screen
    float leftMargin = (Gdx.graphics.getWidth() - totalLaneWidth) / 2;
    background.setPosition(leftMargin, 0);

    stage.addActor(background);

    Texture playerTex =
        ServiceLocator.getResourceService()
            .getAsset("images/entities/character.png", Texture.class);
    Image playerImg = new Image(playerTex);
    playerImg.setSize(64f, 64f);
    float playerX = laneManager.getLaneCenter(1) - 32f;
    float playerY = 2f;
    playerImg.setPosition(playerX, playerY);
    stage.addActor(playerImg);
    this.playerImage = playerImg;
    this.cureentLane = 1;

    createScoreUI(stage);

    Entity inputListener = new Entity().addComponent(new MiniGameInputComponent(false));
    ServiceLocator.getEntityService().register(inputListener);

    inputListener.getEvents().addListener("moveLeft", this::movePLayerLeft);
    inputListener.getEvents().addListener("moveRight", this::movePlayerRight);
  }

  private void createScoreUI(Stage stage) {
    com.badlogic.gdx.graphics.g2d.BitmapFont font = new BitmapFont();
    Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

    scoreLabel = new Label("Score: 0", labelStyle);
    scoreLabel.setFontScale(2f);
    scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 40f);
    scoreLabel.setAlignment(Align.left);
    stage.addActor(scoreLabel);

    timeLabel = new Label("Time: 0.0s", labelStyle);
    timeLabel.setFontScale(2f);
    timeLabel.setPosition(Gdx.graphics.getWidth() - 200f, Gdx.graphics.getHeight() - 40f);
    timeLabel.setAlignment(Align.right);
    stage.addActor(timeLabel);

    // Update score and time every second
    stage.addAction(
        com.badlogic.gdx.scenes.scene2d.actions.Actions.forever(
            com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
                com.badlogic.gdx.scenes.scene2d.actions.Actions.delay(1f),
                com.badlogic.gdx.scenes.scene2d.actions.Actions.run(
                    () -> {
                      if (!gameOver) {
                        score += 0.5; // Increment score
                        survivalTime += 0.1f; // Increment survival time
                        scoreLabel.setText("Score: " + score);
                        timeLabel.setText(String.format("Time: %.1fs", survivalTime));
                      }
                    }))));
  }

  private void initializeObstacles() {
    obstacleManager = new ObstacleManager(laneManager);
    player = LaneRunnerPlayerFactory.createPlayer(laneManager);
    updatePlayerEntityPosition();
  }

  private void movePlayerRight() {
    if (cureentLane < laneManager.getNumLanes() - 1) {
      cureentLane++;
      updatePlayerPosition();
      logger.info("Moved Right to lane: {}", cureentLane);
    }
  }

  private void updatePlayerEntityPosition() {
    float newX = laneManager.getLaneCenter(cureentLane);
    player.setPosition(newX, LaneConfig.PLAYER_Y);
  }

  private void movePLayerLeft() {
    if (cureentLane > 0) {
      cureentLane--;
      updatePlayerPosition();
      logger.info("Moved Left to lane: {}", cureentLane);
    }
  }

  private void updatePlayerPosition() {
    float newX = laneManager.getLaneCenter(cureentLane) - 32f; // Center the image
    playerImage.setPosition(newX, playerImage.getY());
  }

  @Override
  public void render(float delta) {
    // Only update game logic if not game over
    if (!gameOver) {
      survivalTime += delta;
      scoreTimer += delta;
      if (scoreTimer >= 1f) { // Every second
        score += 0.5; // Increment score
        scoreTimer = 0f;
      }
      int previousDodged = obstacleManager.getObstaclesDodged();
      ServiceLocator.getEntityService().update();
      obstacleManager.update(delta);
      int newDodged = obstacleManager.getObstaclesDodged();
      if (newDodged > previousDodged) {
        score += (newDodged - previousDodged) * 5; // Bonus for dodging
      }
      scoreLabel.setText("Score: " + score);
      timeLabel.setText(String.format("Time: %.1fs", survivalTime));
      if (obstacleManager.checkCollision(playerImage)) {
        logger.info("Player collided with an obstacle. Game Over!");
        gameOver = true;
        game.setScreen(
            new LaneRunnerGameOverScreen(
                game, score, survivalTime, obstacleManager.getObstaclesDodged()));
        return;
      }
    }
    // Always render the scene & stage so Game Over box shows
    renderer.render();
    Stage stage = ServiceLocator.getRenderService().getStage();
    stage.act(delta);
    stage.draw();
  }

  private void unloadAssets() {
    logger.debug("Unloading assets");
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.unloadAssets(laneRunnerTextures);
  }

  @Override
  public void dispose() {
    logger.debug("Disposing lane runner mini game screen");

    if (obstacleManager != null) {
      obstacleManager.clearObstacles();
    }
    renderer.dispose();
    unloadAssets();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();

    ServiceLocator.clear();
  }
}
