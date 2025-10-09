package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.minigame.BallComponent;
import com.csse3200.game.minigame.CollisionComponent;
import com.csse3200.game.minigame.PaddleComponent;
import com.csse3200.game.minigame.PaddleInputComponent;
import com.csse3200.game.services.ServiceLocator;

public class PaddleGameScreen extends ScreenAdapter {
  private final GdxGame game;
  private Stage stage;
  private Image paddleImage;
  private Entity paddle;
  private Entity ball;
  private Texture paddleTex;
  private Texture ballTex;
  private Texture bgTex;
  private Label scoreLabel;
  private int ballsHit;
  private float totalTime = 0f;
  private Label timeLabel;

  /**
   * Creates a new PaddleGameScreen and initializes the stage, input processor, and assets.
   *
   * @param game
   */
  public PaddleGameScreen(GdxGame game) {
    this.game = game;
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);
    ballsHit = 0;

    if (ServiceLocator.getResourceService() == null) {
      ServiceLocator.registerResourceService(new com.csse3200.game.services.ResourceService());
    }
    if (ServiceLocator.getSettingsService() == null) {
      ServiceLocator.registerSettingsService(new com.csse3200.game.services.SettingsService());
    }
    loadAssests();
    addBackground();
    createPaddle();
    createBall();
    createScoreLabel();
  }

  private void loadAssests() {
    paddleTex = new Texture(Gdx.files.internal("images/entities/minigames/paddle.png"));
    ballTex = new Texture(Gdx.files.internal("images/entities/minigames/ball.png"));
    bgTex = new Texture(Gdx.files.internal("images/backgrounds/WallPongbg.png"));
  }

  private void addBackground() {
    Image bg = new Image(bgTex);
    bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    bg.setPosition(0, 0);
    stage.addActor(bg);
  }

  private void createPaddle() {
    paddleImage = new Image(paddleTex);
    paddleImage.setSize(200, 35);
    paddleImage.setPosition(Gdx.graphics.getWidth() / 2f - 100, 50);
    stage.addActor(paddleImage);

    paddle = new Entity();
    paddle.addComponent(new PaddleComponent(paddleImage));
    paddle.addComponent(new PaddleInputComponent(paddle));
    paddle.create();
  }

  private void createBall() {
    Image ballImage = new Image(ballTex);
    ballImage.setSize(40, 40);
    ballImage.setPosition(Gdx.graphics.getWidth() / 2f - 20, 100);
    stage.addActor(ballImage);

    ball = new Entity();
    ball.addComponent(new BallComponent(ballImage, 300f, 300f));
    ball.addComponent(new CollisionComponent(paddleImage));
    ball.create();
  }

  private void createScoreLabel() {

    BitmapFont font = ServiceLocator.getGlobalResourceService().generateFreeTypeFont("Default", 20);
    Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
    scoreLabel = new Label("Score: 0", style);
    scoreLabel.setPosition(20, Gdx.graphics.getHeight() - (float) 40);
    stage.addActor(scoreLabel);
    timeLabel = new Label("Time: 0.00s", style);
    timeLabel.setPosition(20, Gdx.graphics.getHeight() - (float) 60);
    stage.addActor(timeLabel);
  }

  @Override
  public void render(float delta) {
    ServiceLocator.getMusicService().play("sounds/background-music/level2_music.mp3");
    totalTime += delta;
    float survivalTime = totalTime;
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    BallComponent ballComp = ball.getComponent(BallComponent.class);
    CollisionComponent collisionComp = ball.getComponent(CollisionComponent.class);

    paddle.getComponent(PaddleComponent.class).update();
    paddle.getComponent(PaddleInputComponent.class).update();

    ballComp.update(delta, collisionComp);

    int score = ballComp.getScore();
    scoreLabel.setText("Score: " + score);
    timeLabel.setText(String.format("Time: %.2f s", survivalTime));
    ballsHit = ballComp.getBallsHit();
    stage.act(delta);
    stage.draw();

    if (ballComp.getImage().getY() <= 0) {

      game.setScreen(new WallPongGameOverScreen(game, score, survivalTime, ballsHit));
      dispose();
    }
  }

  @Override
  public void dispose() {
    stage.dispose();
    if (paddleTex != null) paddleTex.dispose();
    if (ballTex != null) ballTex.dispose();
    if (bgTex != null) bgTex.dispose();
  }
}
