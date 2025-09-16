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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.GdxGame;
import com.csse3200.game.persistence.Persistence;
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
    private Texture playerTex;

    private Node[] nodes;
    private Vector2 playerPos;
    private float playerSpeed = 200f;
    private Node nearbyNode = null;

    private BitmapFont font;

    // UI
    private Stage stage;
    private Skin skin;

    // World size (logical map size)
    private final float worldWidth = 3000f;
    private final float worldHeight = 2000f;

    // --- Zoom steps: start at current look (baseline), allow only further zoom-out ---
    // You can tweak these values if you want slightly more/less zoom-out granularity.
    private final float[] ZOOM_STEPS = { 1.20f, 1.35f, 1.50f, 1.70f, 1.90f };
    private int zoomIdx = 0; // 0 = baseline (same as before)

    public WorldMapScreen(GdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Baseline zoom (same as your old fixed zoom)
        camera.zoom = ZOOM_STEPS[zoomIdx];

        // Load assets
        worldMap = new Texture(Gdx.files.internal("images/world_map.png"));
        nodeCompleted = new Texture(Gdx.files.internal("images/node_completed.png"));
        nodeUnlocked = new Texture(Gdx.files.internal("images/node_unlocked.png"));
        lockedLevel1 = new Texture(Gdx.files.internal("images/locked_level1.png"));
        lockedLevel2 = new Texture(Gdx.files.internal("images/locked_level2.png"));
        playerTex = new Texture(Gdx.files.internal("images/character.png"));

        // Load nodes
        FileHandle file = Gdx.files.internal("data/nodes.json");
        Json json = new Json();
        nodes = json.fromJson(Node[].class, file);

        playerPos = new Vector2(worldWidth * nodes[0].px, worldHeight * nodes[0].py);

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        // === UI ===
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Back button (using image)
        Texture backTex = new Texture(Gdx.files.internal("images/back_button.png"));
        ImageButton backBtn = new ImageButton(new TextureRegionDrawable(backTex));
        backBtn.setSize(150, 150);
        backBtn.setPosition(20, Gdx.graphics.getHeight() - 170);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.info("Back button clicked → Main Menu");
                game.setScreen(GdxGame.ScreenType.MAIN_MENU);
            }
        });

        // Shop button
        TextButton shopBtn = new TextButton("Shop", skin);
        shopBtn.setPosition(Gdx.graphics.getWidth() - 240, 20);
        shopBtn.setSize(100, 40);
        shopBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Persistence.profile() == null) Persistence.load();
                game.setScreen(GdxGame.ScreenType.SHOP);
            }
        });

        // Inventory button
        TextButton invBtn = new TextButton("Inventory", skin);
        invBtn.setPosition(Gdx.graphics.getWidth() - 120, 20);
        invBtn.setSize(100, 40);
        invBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Persistence.profile() == null) Persistence.load();
                game.setScreen(GdxGame.ScreenType.INVENTORY);
            }
        });

        stage.addActor(backBtn);
        stage.addActor(shopBtn);
        stage.addActor(invBtn);
    }

    @Override
    public void render(float delta) {
        handleInput(delta);

        // Camera follows player
        camera.position.set(playerPos.x, playerPos.y, 0);
        clampCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(worldMap, 0, 0, worldWidth, worldHeight);

        // Draw nodes
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

            float x = node.px * worldWidth;
            float y = node.py * worldHeight;
            batch.draw(nodeTex, x, y, 80, 80);

            if (playerPos.dst(x, y) < 100) {
                nearbyNode = node;
                String prompt = (node.level == 1) ? "Press E to Start" : "Press E to Checkpoint";
                font.draw(batch, prompt, x, y + 100);
            }
        }

        // Draw player
        batch.draw(playerTex, playerPos.x, playerPos.y, 96, 96);
        batch.end();

        // Draw UI (always on top)
        stage.act(delta);
        stage.draw();
    }

    private void handleInput(float delta) {
        float moveAmount = playerSpeed * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) playerPos.y += moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) playerPos.y -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) playerPos.x -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) playerPos.x += moveAmount;

        // --- Zoom controls ---
        // Q → Zoom OUT one step (see more of the map)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (zoomIdx < ZOOM_STEPS.length - 1) {
                zoomIdx++;
                camera.zoom = ZOOM_STEPS[zoomIdx];
                logger.info("Zoom OUT → {}", camera.zoom);
            }
        }

        // K → Zoom IN one step (but NEVER closer than baseline index 0)
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            if (zoomIdx > 0) {
                zoomIdx--;
                camera.zoom = ZOOM_STEPS[zoomIdx];
                logger.info("Zoom IN (towards baseline) → {}", camera.zoom);
            }
        }

        if (nearbyNode != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (nearbyNode.level == 1) {
                logger.info("Starting Level 1!");
                game.setScreen(GdxGame.ScreenType.MAIN_GAME);
            } else {
                logger.info("Checkpoint reached at Level {}", nearbyNode.level);
                nearbyNode.unlocked = true;
            }
        }
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
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        worldMap.dispose();
        nodeCompleted.dispose();
        nodeUnlocked.dispose();
        lockedLevel1.dispose();
        lockedLevel2.dispose();
        playerTex.dispose();
        font.dispose();
        stage.dispose();
        skin.dispose();
    }
}
