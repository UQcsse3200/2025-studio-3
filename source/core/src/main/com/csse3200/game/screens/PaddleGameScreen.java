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

/*import com.csse3200.game.minigame.BallComponent;
import com.csse3200.game.minigame.CollisionComponent;
import com.csse3200.game.minigame.PaddleComponent;
import com.csse3200.game.minigame.PaddleInputComponent;*/

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

    public PaddleGameScreen(GdxGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        loadAssests();
        addBackground();
        createPaddle();
        createBall();
        createScoreLabel();
    }

    private void loadAssests() {
        paddleTex=  new Texture(Gdx.files.internal("images/paddle.png"));
        ballTex = new Texture(Gdx.files.internal("images/ball.png"));
        bgTex = new Texture(Gdx.files.internal("images/bg.png"));
    }

    private void addBackground() {
        Image bg = new Image(bgTex);
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        bg.setPosition(0, 0);
        stage.addActor(bg);
    }

    private void createPaddle() {
        paddleImage = new Image(paddleTex);
        paddleImage.setSize(150, 25);
        paddleImage.setPosition(Gdx.graphics.getWidth() / 2f-75, 50);
        stage.addActor(paddleImage);

        paddle = new Entity();
        //paddle.addComponent(new PaddleComponent(paddleImage));
        //paddle.addComponent(new PaddleInputComponent(paddle));
        paddle.create();
    }

    private void createBall() {
        ballImage = new Image(ballTex);
        ballImage.setSize(30,30);
        ballImage.setPosition(Gdx.graphics.getWidth() / 2f-15, 100);
        stage.addActor(ballImage);

        ball = new Entity();
        //ball.addComponent(new BallComponent(ballImage, 300f, 300f));
        //ball.addComponent(new CollisionComponent(paddleImage));
        ball.create();
    }

    private void createScoreLabel() {
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        scoreLabel = new Label("Score: 0", style);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 40);
        stage.addActor(scoreLabel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /*paddle.getComponent(PaddleComponent.class).update();
        paddle.getComponent(PaddleInputComponent.class).update();
        ball.getComponent(BallComponent.class).update(delta);
        ball.getComponent(CollisionComponent.class).update(delta);

        score = ball.getComponent(BallComponent.class).getScore();*/
        scoreLabel.setText("Score: " + score);

        stage.act(delta);
        stage.draw();
    }
}
