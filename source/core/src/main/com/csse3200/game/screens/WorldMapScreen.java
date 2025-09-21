package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.GdxGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldMapScreen implements Screen {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);

  private final GdxGame game;

  private SpriteBatch batch;
  private OrthographicCamera camera;

  private Texture worldMap;
  private Texture nodeCompleted, nodeUnlocked;
  private Texture lockedLevel1, lockedLevel2;
  private Texture playerTex;

  private Node[] nodes;
  private Vector2 playerPos;
  private float playerSpeed = 200f;
  private Node nearbyNode = null;

  private BitmapFont font;

  // UI
  private Stage stage;
  private Skin skin;
  private ImageButton backBtn;
  private final ShapeRenderer uiShapes = new ShapeRenderer(); // for UI glow
  private final ShapeRenderer worldShapes = new ShapeRenderer(); // for node glow
  private boolean hoveringBack = false;

  // Logical map size
  private final float worldWidth = 3000f;
  private final float worldHeight = 2000f;

  // Zoom steps (index 0 = baseline)
  private final float[] ZOOM_STEPS = {1.20f, 1.35f, 1.50f, 1.70f, 1.90f};
  private int zoomIdx = 0;

  public WorldMapScreen(GdxGame game) {
    this.game = game;
  }

  @Override
  public void show() {
    batch = new SpriteBatch();

    camera = new OrthographicCamera();
    camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.zoom = ZOOM_STEPS[zoomIdx];

    // Assets
    worldMap = new Texture(Gdx.files.internal("images/world_map.png"));
    nodeCompleted = new Texture(Gdx.files.internal("images/node_completed.png"));
    nodeUnlocked = new Texture(Gdx.files.internal("images/node_unlocked.png"));
    lockedLevel1 = new Texture(Gdx.files.internal("images/locked_level1.png"));
    lockedLevel2 = new Texture(Gdx.files.internal("images/locked_level2.png"));
    playerTex = new Texture(Gdx.files.internal("images/character.png"));

    // Nodes (expects px/py in [0..1])
    FileHandle file = Gdx.files.internal("data/nodes.json");
    nodes = new Json().fromJson(Node[].class, file);

    // Start from node 0
    playerPos = new Vector2(worldWidth * nodes[0].px, worldHeight * nodes[0].py);

    // Text
    font = new BitmapFont();
    font.setColor(Color.WHITE);
    font.getData().setScale(2f);

    // === UI ===
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);
    skin = new Skin(Gdx.files.internal("uiskin.json"));

    // Back button
    Texture backTex = new Texture(Gdx.files.internal("images/back_button.png"));
    backBtn = new ImageButton(new TextureRegionDrawable(backTex));
    backBtn.setTransform(true);
    backBtn.setSize(150, 150);
    backBtn.setOrigin(backBtn.getWidth() / 2f, backBtn.getHeight() / 2f);
    backBtn.setPosition(20, Gdx.graphics.getHeight() - 170);
    backBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.info("Back → MAIN_MENU");
            game.setScreen(GdxGame.ScreenType.MAIN_MENU);
          }
        });
    backBtn.addListener(
        new InputListener() {
          @Override
          public void enter(InputEvent e, float x, float y, int pointer, Actor from) {
            hoveringBack = true;
            backBtn.setScale(1.07f);
          }

          @Override
          public void exit(InputEvent e, float x, float y, int pointer, Actor to) {
            hoveringBack = false;
            backBtn.setScale(1f);
          }
        });

    // Shop button
    TextButton shopBtn = new TextButton("Shop", skin);
    shopBtn.setPosition(Gdx.graphics.getWidth() - 240, 20);
    shopBtn.setSize(100, 40);
    shopBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            logger.info("Opening Shop");
            game.setScreen(GdxGame.ScreenType.SHOP);
          }
        });
    stage.addActor(shopBtn);

    // Inventory button
    TextButton invBtn = new TextButton("Inventory", skin);
    invBtn.setPosition(Gdx.graphics.getWidth() - 120, 20);
    invBtn.setSize(100, 40);
    invBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            logger.info("Opening Inventory");
            game.setScreen(GdxGame.ScreenType.INVENTORY);
          }
        });
    stage.addActor(invBtn);

    // Add actors
    stage.addActor(backBtn);
  }

  @Override
  public void render(float delta) {
    handleInput(delta);

    // Camera follows player
    camera.position.set(playerPos.x, playerPos.y, 0);
    clampCamera();
    camera.update();

    // WORLD: map background
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.draw(worldMap, 0, 0, worldWidth, worldHeight);
    batch.end();

    // WORLD: node glow
    Vector2 mouseWorld = getMouseWorld();
    Gdx.gl.glEnable(GL20.GL_BLEND);
    worldShapes.setProjectionMatrix(camera.combined);
    worldShapes.begin(ShapeRenderer.ShapeType.Filled);
    for (Node node : nodes) {
      float x = node.px * worldWidth;
      float y = node.py * worldHeight;
      float nodeSize = 80f;

      boolean hovering = isMouseOverNode(mouseWorld, x, y, nodeSize);
      boolean near = playerPos.dst(x + nodeSize / 2f, y + nodeSize / 2f) < 150f;

      if (hovering || near) {
        if (node.unlocked) {
          worldShapes.setColor(1f, 1f, 0f, 0.5f); // yellow
        } else {
          worldShapes.setColor(0.7f, 0.7f, 0.7f, 0.5f); // gray
        }
        worldShapes.circle(x + nodeSize / 2f, y + nodeSize / 2f, 50f);
      }
    }
    worldShapes.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);

    // WORLD: nodes + player
    batch.begin();
    nearbyNode = null;
    for (Node node : nodes) {
      Texture nodeTex;
      if (node.completed) nodeTex = nodeCompleted;
      else if (node.unlocked) nodeTex = nodeUnlocked;
      else if (node.level == 2) nodeTex = lockedLevel1;
      else nodeTex = lockedLevel2;

      float x = node.px * worldWidth;
      float y = node.py * worldHeight;
      float nodeSize = 80f;

      boolean hovering = isMouseOverNode(mouseWorld, x, y, nodeSize);
      boolean near = playerPos.dst(x + nodeSize / 2f, y + nodeSize / 2f) < 150f;

      if (hovering) {
        batch.draw(nodeTex, x - 4f, y - 4f, nodeSize + 8f, nodeSize + 8f);
      } else {
        batch.draw(nodeTex, x, y, nodeSize, nodeSize);
      }

      if (near) {
        nearbyNode = node;
        String prompt = (node.level == 1) ? "Press E to Start" : "Press E to Checkpoint";
        font.draw(batch, prompt, x, y + 100f);
      }
    }

    batch.draw(playerTex, playerPos.x, playerPos.y, 96, 96);
    batch.end();

    // UI: back button glow
    if (hoveringBack) {
      uiShapes.setProjectionMatrix(stage.getCamera().combined);
      Gdx.gl.glEnable(GL20.GL_BLEND);
      uiShapes.begin(ShapeRenderer.ShapeType.Filled);
      uiShapes.setColor(1f, 1f, 0f, 0.35f);
      float cx = backBtn.getX() + backBtn.getWidth() / 2f;
      float cy = backBtn.getY() + backBtn.getHeight() / 2f;
      float r = Math.max(backBtn.getWidth(), backBtn.getHeight()) / 1.5f;
      uiShapes.circle(cx, cy, r);
      uiShapes.end();
      Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    stage.act(delta);
    stage.draw();
  }

  private void handleInput(float delta) {
    float moveAmount = playerSpeed * delta;

    if (Gdx.input.isKeyPressed(Input.Keys.W)) playerPos.y += moveAmount;
    if (Gdx.input.isKeyPressed(Input.Keys.S)) playerPos.y -= moveAmount;
    if (Gdx.input.isKeyPressed(Input.Keys.A)) playerPos.x -= moveAmount;
    if (Gdx.input.isKeyPressed(Input.Keys.D)) playerPos.x += moveAmount;

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      if (zoomIdx < ZOOM_STEPS.length - 1) {
        zoomIdx++;
        camera.zoom = ZOOM_STEPS[zoomIdx];
        logger.info("Zoom OUT → {}", camera.zoom);
      }
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
      if (zoomIdx > 0) {
        zoomIdx--;
        camera.zoom = ZOOM_STEPS[zoomIdx];
        logger.info("Zoom IN → {}", camera.zoom);
      }
    }

    if (nearbyNode != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (nearbyNode.level == 1) {
        logger.info("🚀 Starting Level 1!");
        nearbyNode.completed = true;
        startMainGameNextFrame();
        for (Node node : nodes) if (node.level == 2) node.unlocked = true;
        return;

      } else if (nearbyNode.level == 2 && isLevelCompleted(1)) {
        logger.info("🚀 Starting Level 2!");
        nearbyNode.completed = true;
        startMainGameNextFrame();
        for (Node node : nodes) if (node.level == 3) node.unlocked = true;
        return;

      } else if (nearbyNode.level == 3 && isLevelCompleted(2)) {
        logger.info("🚀 Starting Level 3!");
        nearbyNode.completed = true;
        startSlotMachineGameNextFrame();
        return;

      } else {
        logger.info("❌ Level {} is locked. Finish the previous level first!", nearbyNode.level);
      }
    }
  }

  private Vector2 getMouseWorld() {
    Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
    camera.unproject(v);
    return new Vector2(v.x, v.y);
  }

  private void startMainGameNextFrame() {
    Gdx.app.postRunnable(
        () -> {
          try {
            game.setScreen(GdxGame.ScreenType.MAIN_GAME);
          } catch (Throwable t) {
            logger.error("MAIN_GAME crashed; staying on world map.", t);
          }
        });
  }

  private void startSlotMachineGameNextFrame() {
    Gdx.app.postRunnable(
        () -> {
          try {
            game.setScreen(GdxGame.ScreenType.SLOT_MACHINE);
          } catch (Throwable t) {
            logger.error("SLOT_MACHINE crashed; staying on world map.", t);
          }
        });
  }

  private boolean isMouseOverNode(Vector2 mouseWorld, float nodeX, float nodeY, float size) {
    return mouseWorld.x >= nodeX
        && mouseWorld.x <= nodeX + size
        && mouseWorld.y >= nodeY
        && mouseWorld.y <= nodeY + size;
  }

  private boolean isLevelCompleted(int level) {
    // Test Only
    if (level == 2) return true;
    for (Node n : nodes) if (n.level == level) return n.completed;
    return false;
  }

  private void clampCamera() {
    float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
    float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

    float minX = effectiveViewportWidth / 2f;
    float maxX = worldWidth - effectiveViewportWidth / 2f;
    float minY = effectiveViewportHeight / 2f;
    float maxY = worldHeight - effectiveViewportHeight / 2f;

    camera.position.x = Math.max(minX, Math.min(maxX, camera.position.x));
    camera.position.y = Math.max(minY, Math.min(maxY, camera.position.y));
  }

  @Override
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
    if (stage != null) stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  @Override
  public void dispose() {
    if (batch != null) batch.dispose();
    if (worldMap != null) worldMap.dispose();
    if (nodeCompleted != null) nodeCompleted.dispose();
    if (nodeUnlocked != null) nodeUnlocked.dispose();
    if (lockedLevel1 != null) lockedLevel1.dispose();
    if (lockedLevel2 != null) lockedLevel2.dispose();
    if (playerTex != null) playerTex.dispose();
    if (font != null) font.dispose();
    if (stage != null) stage.dispose();
    if (skin != null) skin.dispose();
    uiShapes.dispose();
    worldShapes.dispose();
  }
}
