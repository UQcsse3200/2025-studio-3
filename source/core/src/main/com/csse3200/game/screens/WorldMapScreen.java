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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import com.csse3200.game.GdxGame;
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

public class WorldMapScreen implements Screen {
    private final GdxGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private final Renderer renderer;
    private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);
    private static final String SHOP_NODE_ID = "shop";
    private static final String SKILLS_NODE_ID = "skills";
    private Texture worldMap;
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
    private BitmapFont font;

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

        worldMap = new Texture(Gdx.files.internal("images/world_map.png"));
        nodeCompleted = new Texture(Gdx.files.internal("images/node_completed.png"));
        nodeUnlocked = new Texture(Gdx.files.internal("images/node_unlocked.png"));
        lockedLevel1 = new Texture(Gdx.files.internal("images/locked_level1.png"));
        lockedLevel2 = new Texture(Gdx.files.internal("images/locked_level2.png"));
        shopTexture = new Texture(Gdx.files.internal("images/shopsprite.png"));
        skillsTexture = new Texture(Gdx.files.internal("images/skills.png"));
        playerTex = new Texture(Gdx.files.internal("images/character.png"));

        FileHandle file = Gdx.files.internal("data/nodes.json");
        Json json = new Json();
        nodes = json.fromJson(Node[].class, file);

        playerPos =
                new Vector2(nodes[0].px * Gdx.graphics.getWidth(), nodes[0].py * Gdx.graphics.getHeight());

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

        batch.draw(playerTex, playerPos.x, playerPos.y, 96, 96);

        batch.end();

        // Update and render only the stage (UI components) without clearing the screen
        ServiceLocator.getEntityService().update();
        Stage stage = ServiceLocator.getRenderService().getStage();
        stage.act();
        stage.draw();

        handleInput(delta);
    }

    private void handleInput(float delta) {
        float moveAmount = playerSpeed * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) playerPos.y += moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) playerPos.y -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) playerPos.x -= moveAmount;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) playerPos.x += moveAmount;

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
