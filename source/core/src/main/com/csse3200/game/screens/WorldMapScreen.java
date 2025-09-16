package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import com.csse3200.game.GdxGame;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.components.hud.AnimatedDropdownMenu;
import com.csse3200.game.components.hud.MainMapNavigationMenu;
import com.csse3200.game.components.hud.MainMapNavigationMenuActions;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Scene2D imports
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
  private final Renderer renderer;
  private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);
  private static final String SHOP_NODE_ID = "shop";
  private static final String SKILLS_NODE_ID = "skills";
  private Texture worldMap;
  private Texture nodeCompleted, nodeUnlocked;
  private Texture lockedLevel1, lockedLevel2;
  private Texture playerTex, backButton;
    private final GdxGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);

    private Texture worldMap;
    private Texture nodeCompleted, nodeUnlocked;
    private Texture lockedLevel1, lockedLevel2;
    private Texture playerTex;
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
    private Node[] nodes;
    private Vector2 playerPos;
    private float playerSpeed = 200f;
    private Node nearbyNode = null;
  private Texture nodeCompleted;
  private Texture nodeUnlocked;
  private Texture lockedLevel1;
  private Texture lockedLevel2;
  private Texture shopTexture;
  private Texture skillsTexture;
  private Texture playerTex;
  private Node[] nodes;
  private Vector2 playerPos;
  private float playerSpeed = 200f;
  private Node nearbyNode = null;

    private Rectangle backBtnBounds;
    private BitmapFont font;
    private BitmapFont font;

    // NEW: Scene2D stage + UI
    private Stage stage;
    private Skin skin;

    public WorldMapScreen(GdxGame game) {
        this.game = game;
    }
    // UI
    private Stage stage;
    private Skin skin;

    // Logical map size (kept as in your current code)
    private final float worldWidth = 3000f;
    private final float worldHeight = 2000f;

    // Zoom steps: baseline at index 0; you can zoom OUT only beyond this
    private final float[] ZOOM_STEPS = {1.20f, 1.35f, 1.50f, 1.70f, 1.90f};
    private int zoomIdx = 0; // baseline

    public WorldMapScreen(GdxGame game) {
        this.game = game;
    }
  public WorldMapScreen(GdxGame game) {
    this.game = game;
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    renderer = RenderFactory.createRenderer();
    createUI();
  }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = ZOOM_STEPS[zoomIdx]; // start at baseline

        worldMap = new Texture(Gdx.files.internal("images/world_map.png"));
        nodeCompleted = new Texture(Gdx.files.internal("images/node_completed.png"));
        nodeUnlocked = new Texture(Gdx.files.internal("images/node_unlocked.png"));
        lockedLevel1 = new Texture(Gdx.files.internal("images/locked_level1.png"));
        lockedLevel2 = new Texture(Gdx.files.internal("images/locked_level2.png"));
        playerTex = new Texture(Gdx.files.internal("images/character.png"));
        backButton = new Texture(Gdx.files.internal("images/back_button.png"));
        // Load assets
        worldMap     = new Texture(Gdx.files.internal("images/world_map.png"));
        nodeCompleted= new Texture(Gdx.files.internal("images/node_completed.png"));
        nodeUnlocked = new Texture(Gdx.files.internal("images/node_unlocked.png"));
        lockedLevel1 = new Texture(Gdx.files.internal("images/locked_level1.png"));
        lockedLevel2 = new Texture(Gdx.files.internal("images/locked_level2.png"));
        playerTex    = new Texture(Gdx.files.internal("images/character.png"));
    worldMap = new Texture(Gdx.files.internal("images/world_map.png"));
    nodeCompleted = new Texture(Gdx.files.internal("images/node_completed.png"));
    nodeUnlocked = new Texture(Gdx.files.internal("images/node_unlocked.png"));
    lockedLevel1 = new Texture(Gdx.files.internal("images/locked_level1.png"));
    lockedLevel2 = new Texture(Gdx.files.internal("images/locked_level2.png"));
    shopTexture = new Texture(Gdx.files.internal("images/shopsprite.png"));
    skillsTexture = new Texture(Gdx.files.internal("images/skills.png"));
    playerTex = new Texture(Gdx.files.internal("images/character.png"));
    backButton = new Texture(Gdx.files.internal("images/back_button.png"));
    shapeRenderer = new ShapeRenderer();

        FileHandle file = Gdx.files.internal("data/nodes.json");
        Json json = new Json();
        nodes = json.fromJson(Node[].class, file);
        // Load nodes
        FileHandle file = Gdx.files.internal("data/nodes.json");
        Json json = new Json();
        nodes = json.fromJson(Node[].class, file);

        playerPos =
                new Vector2(nodes[0].px * Gdx.graphics.getWidth(), nodes[0].py * Gdx.graphics.getHeight());
        playerPos = new Vector2(worldWidth * nodes[0].px, worldHeight * nodes[0].py);
        playerPos =
                new Vector2(nodes[0].px * Gdx.graphics.getWidth(), nodes[0].py * Gdx.graphics.getHeight());

        backBtnBounds = new Rectangle(20, Gdx.graphics.getHeight() - 140, 120, 120);
    font = new BitmapFont();
    font.setColor(Color.WHITE);
    font.getData().setScale(2f);
  }
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
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        // === UI (fixed to screen) ===
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Back button (image)
        Texture backTex = new Texture(Gdx.files.internal("images/back_button.png"));
        ImageButton backBtn = new ImageButton(new TextureRegionDrawable(backTex));
        backBtn.setSize(150, 150);
        backBtn.setPosition(20, Gdx.graphics.getHeight() - 170);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.info("Back → MAIN_MENU");
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
        batch.begin();
        // Draw world map
        batch.draw(worldMap, 0, 0, worldWidth, worldHeight);

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
    batch.draw(worldMap, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    for (Node node : nodes) {
      Texture nodeTex;
      if (SHOP_NODE_ID.equals(node.id)) {
        nodeTex = shopTexture;
      } else if (SKILLS_NODE_ID.equals(node.id)) {
        nodeTex = skillsTexture;
      } else if (node.completed) {
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

            float x = node.px * worldWidth;
            float y = node.py * worldHeight;
            batch.draw(nodeTex, x, y, 80, 80);

            if (playerPos.dst(x, y) < 60) {
                nearbyNode = node;
                String prompt = (nearbyNode.level == 1) ? "Press E to Start" : "Press E to Checkpoint";
                font.draw(batch, prompt, x, y + 100);
            }
        }
            if (playerPos.dst(x, y) < 100) {
      if (playerPos.dst(x, y) < 60) {
        nearbyNode = node;
        String prompt;
        if (SHOP_NODE_ID.equals(nearbyNode.id)) {
          prompt = "Press E to Shop";
        } else if (SKILLS_NODE_ID.equals(nearbyNode.id)) {
          prompt = "Press E to view Skills";
        } else if (nearbyNode.level == 1) {
          prompt = "Press E to Start";
        } else {
          prompt = "Press E to Checkpoint";
        }
        font.draw(batch, prompt, x, y + 100);
      }
    }
            if (playerPos.dst(x, y) < 60) {
                nearbyNode = node;
                String prompt = (node.level == 1) ? "Press E to Start" : "Press E to Checkpoint";
                font.draw(batch, prompt, x, y + 100);
            }
        }

        // Player sprite
        batch.draw(playerTex, playerPos.x, playerPos.y, 96, 96);

        batch.end();
        // Draw player
        batch.draw(playerTex, playerPos.x, playerPos.y, 96, 96);
        batch.end();

        // Input handling
        handleInput(delta);

        // Draw Scene2D UI
        stage.act(delta);
        stage.draw();
    }
        // UI on top
        stage.act(delta);
        stage.draw();
    }
    // Update and render only the stage (UI components) without clearing the screen
    ServiceLocator.getEntityService().update();
    Stage stage = ServiceLocator.getRenderService().getStage();
    stage.act();
    stage.draw();

    handleInput(delta);
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
        // Zoom OUT only (Q). Zoom IN (K) only back toward baseline, never past it.
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
                logger.info("Zoom IN (toward baseline) → {}", camera.zoom);
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

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
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

    if (nearbyNode != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (SHOP_NODE_ID.equals(nearbyNode.id)) {
        logger.info("Opening Shop!");
        game.setScreen(GdxGame.ScreenType.SHOP);
      } else if (SKILLS_NODE_ID.equals(nearbyNode.id)) {
        logger.info("Opening Skills!");
        game.setScreen(GdxGame.ScreenType.SKILLTREE);
      } else if (nearbyNode.level == 1) {
        logger.info("Starting Level 1!");
        game.setScreen(GdxGame.ScreenType.MAIN_GAME);
      } else if (nearbyNode.level == 3) {
        logger.info("Starting Slot Machine Level!");
        game.setScreen(GdxGame.ScreenType.SLOT_MACHINE);
      } else {
        logger.info("Checkpoint reached at Level {}", nearbyNode.level);
        nearbyNode.unlocked = true;
      }
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
    renderer.dispose();
    if (batch != null) {
      batch.dispose();
    }
    if (worldMap != null) {
      worldMap.dispose();
    }
    if (nodeCompleted != null) {
      nodeCompleted.dispose();
    }
    if (nodeUnlocked != null) {
      nodeUnlocked.dispose();
    }
    if (lockedLevel1 != null) {
      lockedLevel1.dispose();
    }
    if (lockedLevel2 != null) {
      lockedLevel2.dispose();
    }
    if (shopTexture != null) {
      shopTexture.dispose();
    }
    if (skillsTexture != null) {
      skillsTexture.dispose();
    }
    if (playerTex != null) {
      playerTex.dispose();
    }
    if (font != null) {
      font.dispose();
    }
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.clear();
  }

  /**
   * Creates the StatisticsScreen's UI including components for rendering UI elements to the screen
   * and capturing and handling UI input.
   */
  private void createUI() {
    logger.debug("Creating ui");
    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity ui = new Entity();
    ui.addComponent(new InputDecorator(stage, 10))
        .addComponent(new MainMapNavigationMenu())
        .addComponent(new MainMapNavigationMenuActions(this.game))
        .addComponent(new AnimatedDropdownMenu());
    ServiceLocator.getEntityService().register(ui);
  }
}

