package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.csse3200.game.GdxGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldMapScreen implements Screen {
  private final GdxGame game;
  private SpriteBatch batch;
  private OrthographicCamera camera;
  private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);
  private Texture worldMap;
  private Texture nodeCompleted, nodeUnlocked;
  private Texture lockedLevel1, lockedLevel2;
  private Texture playerTex, backButton;

  private Node[] nodes;
  private Vector2 playerPos;
  private float playerSpeed = 200f;
  private Node nearbyNode = null;

  private Rectangle backBtnBounds;
  private BitmapFont font;

  public WorldMapScreen(GdxGame game) {
    this.game = game;
  }

  @Override
  public void show() {
    batch = new SpriteBatch();

    camera = new OrthographicCamera();
    camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    worldMap = new Texture(Gdx.files.internal("images/world_map.png"));
    nodeCompleted = new Texture(Gdx.files.internal("images/node_completed.png"));
    nodeUnlocked = new Texture(Gdx.files.internal("images/node_unlocked.png"));
    lockedLevel1 = new Texture(Gdx.files.internal("images/locked_level1.png"));
    lockedLevel2 = new Texture(Gdx.files.internal("images/locked_level2.png"));
    playerTex = new Texture(Gdx.files.internal("images/character.png"));
    backButton = new Texture(Gdx.files.internal("images/back_button.png"));

    FileHandle file = Gdx.files.internal("data/nodes.json");
    Json json = new Json();
    nodes = json.fromJson(Node[].class, file);

    playerPos =
        new Vector2(nodes[0].px * Gdx.graphics.getWidth(), nodes[0].py * Gdx.graphics.getHeight());

    backBtnBounds = new Rectangle(20, Gdx.graphics.getHeight() - 140, 120, 120);

    font = new BitmapFont();
    font.setColor(Color.WHITE);
    font.getData().setScale(2f);
  }

  @Override
  public void render(float delta) {
    camera.update();
    batch.setProjectionMatrix(camera.combined);

    batch.begin();

    batch.draw(worldMap, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    batch.draw(
        backButton, backBtnBounds.x, backBtnBounds.y, backBtnBounds.width, backBtnBounds.height);

    for (Node node : nodes) {
      Texture nodeTex;
      if (node.completed) {
        nodeTex = nodeCompleted;
      } else if (node.unlocked) {
        nodeTex = nodeUnlocked;
      } else if (node.level == 2) {
        nodeTex = lockedLevel1;
      } else {
        nodeTex = lockedLevel2;
      }

      float x = node.px * Gdx.graphics.getWidth();
      float y = node.py * Gdx.graphics.getHeight();
      batch.draw(nodeTex, x, y, 80, 80);

      if (playerPos.dst(x, y) < 60) {
        nearbyNode = node;
        String prompt = (nearbyNode.level == 1) ? "Press E to Start" : "Press E to Checkpoint";
        font.draw(batch, prompt, x, y + 100);
      }
    }

    batch.draw(playerTex, playerPos.x, playerPos.y, 96, 96);

    batch.end();

    handleInput(delta);
  }

  private void handleInput(float delta) {
    float moveAmount = playerSpeed * delta;

    if (Gdx.input.isKeyPressed(Input.Keys.W)) playerPos.y += moveAmount;
    if (Gdx.input.isKeyPressed(Input.Keys.S)) playerPos.y -= moveAmount;
    if (Gdx.input.isKeyPressed(Input.Keys.A)) playerPos.x -= moveAmount;
    if (Gdx.input.isKeyPressed(Input.Keys.D)) playerPos.x += moveAmount;

    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
      float mx = Gdx.input.getX();
      float my = Gdx.graphics.getHeight() - Gdx.input.getY();

      if (backBtnBounds.contains(mx, my)) {
        game.setScreen(GdxGame.ScreenType.MAIN_MENU);
      }
    }

    if (nearbyNode != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (nearbyNode.level == 1) {
        logger.info("🚀 Starting Level 1!");
        game.setScreen(GdxGame.ScreenType.MAIN_GAME);
      } else {
        logger.info("✅ Checkpoint reached at Level {}", nearbyNode.level);
        nearbyNode.unlocked = true;
      }
    }
  }

  @Override
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
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
    batch.dispose();
    worldMap.dispose();
    nodeCompleted.dispose();
    nodeUnlocked.dispose();
    lockedLevel1.dispose();
    lockedLevel2.dispose();
    playerTex.dispose();
    backButton.dispose();
    font.dispose();
  }
}
