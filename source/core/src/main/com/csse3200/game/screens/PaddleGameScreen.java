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



public class PaddleGameScreen extends ScreenAdapter {
    private final GdxGame game;
    private Stage stage;
    private Image paddleImage;
    private Image ballImage;
    private Entity paddle;
    private Entity ball;

    private Texture paddleTex;
    private Texture ballTex;
    private Texture bgTex;

    private Label scoreLabel;
    private int score;
    private int ballsHit;
    private float totalTime = 0f;
    private Label timeLabel;

    public PaddleGameScreen(GdxGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        ballsHit = 0;

        loadAssests();
        addBackground();
        createPaddle();
        createBall();
        createScoreLabel();
    }

    private void loadAssests() {
        paddleTex = new Texture(Gdx.files.internal("images/paddle.png"));
        ballTex = new Texture(Gdx.files.internal("images/ball.png"));
        bgTex = new Texture(Gdx.files.internal("images/WallPongbg.png"));
    }

    private void addBackground() {
        Image bg = new Image(bgTex);
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        bg.setPosition(0, 0);
        stage.addActor(bg);
    }

    private void createPaddle() {
        paddleImage = new Image(paddleTex);
        paddleImage.setSize(300, 50);
        paddleImage.setPosition(Gdx.graphics.getWidth() / 2f - 150, 50);
        stage.addActor(paddleImage);

        paddle = new Entity();
        paddle.addComponent(new PaddleComponent(paddleImage));
        paddle.addComponent(new PaddleInputComponent(paddle));
        paddle.create();
    }

    private void createBall() {
        ballImage = new Image(ballTex);
        ballImage.setSize(50, 50);
        ballImage.setPosition(Gdx.graphics.getWidth() / 2f - 25, 100);
        stage.addActor(ballImage);

        ball = new Entity();
        ball.addComponent(new BallComponent(ballImage, 200f, 200f));
        ball.addComponent(new CollisionComponent(paddleImage));
        ball.create();
    }

    private void createScoreLabel() {
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        scoreLabel = new Label("Score: 0", style);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 40);
        timeLabel = new Label("Time: 0.00s", style);
        timeLabel.setPosition(20, Gdx.graphics.getHeight() - 60);
        stage.addActor(scoreLabel);
        stage.addActor(timeLabel);
    }

    @Override
    public void render(float delta) {
        totalTime += delta;
        float survivalTime = totalTime;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        BallComponent ballComp = ball.getComponent(BallComponent.class);
        CollisionComponent collisionComp = ball.getComponent(CollisionComponent.class);


        paddle.getComponent(PaddleComponent.class).update();
        paddle.getComponent(PaddleInputComponent.class).update();


        ballComp.update(delta, collisionComp);

        score = ballComp.getScore();
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