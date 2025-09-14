package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputService;
import com.csse3200.game.minigame.*;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaneRunnerScreen extends ScreenAdapter {

    private static final Logger logger = LoggerFactory.getLogger(com.csse3200.game.screens.LaneRunnerScreen.class);
    private final GdxGame game;
    private final Renderer renderer;
    private LaneManager laneManager;
    private Image playerImage;
    private int cureentLane = 1; // Start in the middle lane (0, 1, 2)
    private ObstacleManager obstacleManager;
    private Entity player;
    private boolean gameOver = false;
    private static final String[] laneRunnerTextures = {
            "images/box_boy.png",
            "images/LaneRunnerLanes.png",
            "images/heart.png"
    };

    public LaneRunnerScreen(GdxGame game) {
        this.game = game;

        logger.debug("Initialising lane runner mini game screen services");
        ServiceLocator.registerInputService(new InputService());
        ServiceLocator.registerResourceService(new ResourceService());
        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerRenderService(new RenderService());
        ServiceLocator.registerPhysicsService(new PhysicsService());

        renderer = RenderFactory.createRenderer();
        this.laneManager= new LaneManager(Gdx.graphics.getWidth());
        loadAssets();
        createUI();
        initializeObstacles();
    }

    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(laneRunnerTextures);
        ServiceLocator.getResourceService().loadAll();
    }
    private void createUI() {
        logger.debug("Creating UI");
        Stage stage = ServiceLocator.getRenderService().getStage();

        // Create the background image
        Texture bgTex = ServiceLocator.getResourceService().getAsset("images/LaneRunnerLanes.png", Texture.class);
        Image background = new Image(bgTex);

        // Calculate the correct width to avoid stretching, based on our lane logic
        float totalLaneWidth = laneManager.getLaneWidth() * laneManager.getNumLanes();
        float screenHeight = Gdx.graphics.getHeight();
        background.setSize(totalLaneWidth, screenHeight);

        // Center the background on the screen
        float leftMargin = (Gdx.graphics.getWidth() - totalLaneWidth) / 2;
        background.setPosition(leftMargin, 0);

        stage.addActor(background);


        Texture playerTex = ServiceLocator.getResourceService().getAsset("images/box_boy.png", Texture.class);
        Image playerImage = new Image(playerTex);
        playerImage.setSize(64f, 64f);
        float playerX = laneManager.getLaneCenter(1) - 32f;
        float playerY = 2f;
        playerImage.setPosition(playerX, playerY);
        stage.addActor(playerImage);
        this.playerImage = playerImage;
        this.cureentLane = 1;

        Entity inputListener = new Entity()
                .addComponent(new MiniGameInputComponent());
        ServiceLocator.getEntityService().register(inputListener);

        inputListener.getEvents().addListener("moveLeft",this::movePLayerLeft );
        inputListener.getEvents().addListener("moveRight",this::movePlayerRight );
    }
    private void initializeObstacles() {
        obstacleManager = new ObstacleManager(laneManager);
        player= LaneRunnerPlayerFactory.createPlayer(laneManager);
        updatePlayerEntityPosition();
    }
    private void movePlayerRight() {
        if (cureentLane < laneManager.getNumLanes() - 1) {
            cureentLane++;
            updatePlayerPosition();
            System.out.println("Moved Right to lane: " + cureentLane);
        }
    }
    private void updatePlayerEntityPosition() {
        float newX = laneManager.getLaneCenter(cureentLane);
        player.setPosition(newX, LaneConfig.PLAYER_Y);
    }
    private void movePLayerLeft() {
        if (cureentLane > 0) {
            cureentLane--;
            updatePlayerPosition();
            System.out.println("Moved Left to lane: " + cureentLane);
        }
    }
    private void updatePlayerPosition() {
        float newX = laneManager.getLaneCenter(cureentLane) - 32f; // Center the image
        playerImage.setPosition(newX, playerImage.getY());
    }

    public void render(float delta) {
        obstacleManager.update(delta);
        updatePlayerEntityPosition();
        if(obstacleManager.checkCollision(playerImage)){
            System.out.println("Game Over!");
            gameOver = true;
            game.setScreen(new MainMenuScreen(game));
            return;
        }
        ServiceLocator.getEntityService().update();
        renderer.render();
    }

   private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(laneRunnerTextures);
    }

    public void dispose() {
        logger.debug("Disposing lane runner mini game screen");

        if (obstacleManager != null) {
            obstacleManager.clearObstacles();
        }
        renderer.dispose();
       unloadAssets();
        ServiceLocator.getRenderService().dispose();
        ServiceLocator.getEntityService().dispose();

        ServiceLocator.clear();
    }
}

