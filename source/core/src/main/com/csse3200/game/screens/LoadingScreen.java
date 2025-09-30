package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.GdxGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple loading screen that displays a background image and "Loading..." text. This screen is
 * shown while the game initializes and loads assets.
 */
public class LoadingScreen implements Screen {
  private static final Logger logger = LoggerFactory.getLogger(LoadingScreen.class);
  private final GdxGame game;
  private SpriteBatch batch;
  private OrthographicCamera camera;
  private Stage stage;
  private Skin skin;
  private Label loadingLabel;
  private Texture backgroundTexture;
  private float loadingTime = 0f;
  private static final float MIN_LOADING_TIME = 2f;

  public LoadingScreen(GdxGame game) {
    this.game = game;
    logger.debug("[LoadingScreen] Initializing loading screen");
    loadBackground();
    batch = new SpriteBatch();
    camera = new OrthographicCamera();
    camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    stage = new Stage(new ScreenViewport());
    skin = new Skin(Gdx.files.internal("skin/tdwfb.json"));
    createUI();
  }

  private void loadBackground() {
    try {
      backgroundTexture = new Texture(Gdx.files.internal("images/backgrounds/bg.png"));
    } catch (Exception e) {
      logger.warn("Could not load background image, using default.");
      backgroundTexture = null;
    }
  }

  private void createUI() {
    loadingLabel = new Label("Loading...", skin);
    Label.LabelStyle st = new Label.LabelStyle(loadingLabel.getStyle());
    loadingLabel.setStyle(st);
    loadingLabel.setFontScale(2f);

    float labelWidth = loadingLabel.getPrefWidth();
    float labelHeight = loadingLabel.getPrefHeight();
    loadingLabel.setPosition(
        (Gdx.graphics.getWidth() - labelWidth) / 2f, (Gdx.graphics.getHeight() - labelHeight) / 2f);

    stage.addActor(loadingLabel);
  }

  @Override
  public void show() {
    logger.debug("Showing loading screen");
  }

  @Override
  public void render(float delta) {
    loadingTime += delta;

    // Update camera
    camera.update();
    batch.setProjectionMatrix(camera.combined);

    Gdx.gl.glClearColor(0f, 0f, 0f, 1);

    batch.begin();
    if (backgroundTexture != null) {
      batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    batch.end();

    // Draw UI
    stage.act(delta);
    stage.draw();

    // Check if minimum loading time has passed
    if (loadingTime >= MIN_LOADING_TIME) {
      logger.debug("Loading complete, transitioning to main menu");
      completeLoading();
    }
  }

  private void completeLoading() {
    // Initialize the main game
    // Note: dispose() will be called automatically by GdxGame.setScreen()
    game.initializeGame();
  }

  @Override
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
    stage.getViewport().update(width, height, true);

    // Update label position
    if (loadingLabel != null) {
      float labelWidth = loadingLabel.getPrefWidth();
      float labelHeight = loadingLabel.getPrefHeight();
      loadingLabel.setPosition((width - labelWidth) / 2f, (height - labelHeight) / 2f);
    }
  }

  @Override
  public void pause() {
    // Do nothing
  }

  @Override
  public void resume() {
    // Do nothing
  }

  @Override
  public void hide() {
    // Do nothing
  }

  @Override
  public void dispose() {
    logger.debug("Disposing loading screen");

    if (batch != null) {
      batch.dispose();
    }
    if (stage != null) {
      stage.dispose();
    }
    if (skin != null) {
      skin.dispose();
    }
    if (backgroundTexture != null) {
      backgroundTexture.dispose();
    }
  }
}
