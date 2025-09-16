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
import com.csse3200.game.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Scene2D imports
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

    // NEW: Scene2D stage + UI
    private Stage stage;
    private Skin skin;

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
    shapeRenderer = new ShapeRenderer();

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
        FileHandle file = Gdx.files.internal("data/nodes.json");
        Json json = new Json();
        nodes = json.fromJson(Node[].class, file);

        playerPos =
                new Vector2(nodes[0].px * Gdx.graphics.getWidth(), nodes[0].py * Gdx.graphics.getHeight());

        backBtnBounds = new Rectangle(20, Gdx.graphics.getHeight() - 140, 120, 120);

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        // === Scene2D UI ===
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json")); // default LibGDX skin

        // Shop button
        TextButton shopBtn = new TextButton("Shop", skin);
        shopBtn.setPosition(Gdx.graphics.getWidth() - 220, 40);
        shopBtn.setSize(100, 50);
        shopBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        logger.info("🛒 Opening Shop");
                        if (Persistence.profile() == null) {
                            logger.info("Loading persistence before opening shop");
                            Persistence.load();
                        }
                        game.setScreen(GdxGame.ScreenType.SHOP);
                    }
                });

    @Override
    public void render(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        // Inventory button
        TextButton invBtn = new TextButton("Inventory", skin);
        invBtn.setPosition(Gdx.graphics.getWidth() - 110, 40);
        invBtn.setSize(100, 50);
        invBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        logger.info("🎒 Opening Inventory");
                        if (Persistence.profile() == null) {
                            logger.info("Loading persistence before opening inventory");
                            Persistence.load();
                        }
                        game.setScreen(GdxGame.ScreenType.INVENTORY);
                    }
                });

        // Add buttons to stage
        stage.addActor(shopBtn);
        stage.addActor(invBtn);
    }

    @Override
    public void render(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

// Draw background
        batch.draw(worldMap, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

// 🔹 Back button hover check
        boolean hoveringBack = isMouseOverBack();

// 🔹 Glow effect
        if (hoveringBack) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(1f, 1f, 0f, 0.3f)); // yellow glow
            shapeRenderer.circle(backBtnBounds.x + backBtnBounds.width / 2,
                    backBtnBounds.y + backBtnBounds.height / 2,
                    backBtnBounds.width / 1.5f);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

// 🔹 Draw back button (pop if hovering)
        batch.begin();
        float drawX = backBtnBounds.x - (hoveringBack ? 5 : 0);
        float drawY = backBtnBounds.y - (hoveringBack ? 5 : 0);
        float drawW = hoveringBack ? backBtnBounds.width + 10 : backBtnBounds.width;
        float drawH = hoveringBack ? backBtnBounds.height + 10 : backBtnBounds.height;
        // Background map
        batch.draw(worldMap, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Back button
        batch.draw(backButton, backBtnBounds.x, backBtnBounds.y,
                backBtnBounds.width, backBtnBounds.height);

        // Draw level nodes
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
        batch.draw(backButton, drawX, drawY, drawW, drawH);
        batch.end();

        // End batch before drawing shapes
        nearbyNode = null;
        // Node drawing loop
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
            float nodeSize = 80f;
            float glowSize = 100f;

            // Hover check
            boolean hovering = isMouseOverNode(x, y, nodeSize);

            // Player proximity check
            float distanceToPlayer = playerPos.dst(x + nodeSize / 2, y + nodeSize / 2);
            boolean near = distanceToPlayer < 150; // adjust as needed

            // Draw glow if hovering or near
            if (hovering || near) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                shapeRenderer.setProjectionMatrix(camera.combined);

                if (node.unlocked) {
                    shapeRenderer.setColor(new Color(1f, 1f, 0f, 0.5f)); // yellow
                } else {
                    shapeRenderer.setColor(new Color(0.7f, 0.7f, 0.7f, 0.5f)); // gray
                }

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.circle(x + nodeSize / 2, y + nodeSize / 2, glowSize / 2);
                shapeRenderer.end();

                Gdx.gl.glDisable(GL20.GL_BLEND);

                batch.begin();
            float x = node.px * Gdx.graphics.getWidth();
            float y = node.py * Gdx.graphics.getHeight();
            batch.draw(nodeTex, x, y, 80, 80);

            if (playerPos.dst(x, y) < 60) {
                nearbyNode = node;
                String prompt = (nearbyNode.level == 1) ? "Press E to Start" : "Press E to Checkpoint";
                font.draw(batch, prompt, x, y + 100);
            }
        }
                // Draw slightly bigger if hovering
                if (hovering) {
                    batch.draw(nodeTex, x - 4, y - 4, nodeSize + 8, nodeSize + 8);
                } else {
                    batch.draw(nodeTex, x, y, nodeSize, nodeSize);
                }
                batch.end(); // End batch before next node
            } else {
                batch.begin();
                batch.draw(nodeTex, x, y, nodeSize, nodeSize);
                batch.end();
            }

            // Show prompt if player is near
            if (near) {
                batch.begin();
                nearbyNode = node;
                String prompt = (nearbyNode.level == 1) ? "Press E to Start" : "Press E to Checkpoint";
                font.draw(batch, prompt, x, y + 100);
                batch.end();
            }
        }

        // Player sprite
        batch.draw(playerTex, playerPos.x, playerPos.y, 96, 96);

        batch.end();
        // Draw player character
        batch.begin();
        batch.draw(playerTex, playerPos.x, playerPos.y, 96, 96);
        batch.end();

        // Input handling
        handleInput(delta);

        // Draw Scene2D UI
        stage.act(delta);
        stage.draw();
    }
        // Handle player input
        handleInput(delta);
    }


    private void handleInput(float delta) {
    float moveAmount = playerSpeed * delta;
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
                logger.info(" Starting Level 1!");
                game.setScreen(GdxGame.ScreenType.MAIN_GAME);
            } else {
                logger.info("✅ Checkpoint reached at Level {}", nearbyNode.level);
                nearbyNode.unlocked = true;
            }
        }
    }
        if (nearbyNode != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (nearbyNode.level == 1) {
                logger.info(" Starting Level 1!");
                // Mark level 1 as completed when starting it (or do this after finishing)
                nearbyNode.completed = true;
                game.setScreen(GdxGame.ScreenType.MAIN_GAME);

                // 🔹 Unlock level 2 once level 1 is completed
                for (Node node : nodes) {
                    if (node.level == 2) {
                        node.unlocked = true;
                    }
                }
            } else if (nearbyNode.level == 2 && isLevelCompleted(1)) {
                logger.info(" Starting Level 2!");
                nearbyNode.completed = true;
                game.setScreen(GdxGame.ScreenType.MAIN_GAME);

                // 🔹 Unlock level 3 after level 2 is completed
                for (Node node : nodes) {
                    if (node.level == 3) {
                        node.unlocked = true;
                    }
                }
            } else if (nearbyNode.level == 3 && isLevelCompleted(2)) {
                logger.info(" Starting Level 3!");
                nearbyNode.completed = true;
                game.setScreen(GdxGame.ScreenType.MAIN_GAME);
            } else {
                logger.info(" Level {} is locked. Finish the previous level first!", nearbyNode.level);
            }
        }


    }
  private boolean isMouseOverNode(float nodeX, float nodeY, float size) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return (mx >= nodeX && mx <= nodeX + size && my >= nodeY && my <= nodeY + size);
  }
    private boolean isMouseOverBack() {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return backBtnBounds.contains(mx, my);
    }
    private boolean isLevelCompleted(int level) {
        for (Node node : nodes) {
            if (node.level == level) {
                return node.completed;
            }
        }
        return false;
    }


    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }
    @Override
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
  }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

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
        stage.dispose();
        skin.dispose();
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
    shapeRenderer.dispose();
    font.dispose();
  }
}
