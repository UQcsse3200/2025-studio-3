package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.CameraComponent;
import com.csse3200.game.components.worldmap.*;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.services.*;
import com.csse3200.game.ui.WorldMapNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** World map screen */
public class WorldMapScreen extends BaseScreen {
    private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);

    private static final String[] ADDITIONAL_TEXTURES = {
            "images/backgrounds/world_map.png",
            "images/entities/character.png",
            "images/nodes/completed.png",
            "images/nodes/locked.png"
    };

    private static final float WORLD_WIDTH = 3000f;
    private static final float WORLD_HEIGHT = 2000f;
    private static final Vector2 WORLD_SIZE = new Vector2(WORLD_WIDTH, WORLD_HEIGHT);
    private static final float[] ZOOM_STEPS = {1.20f, 1.35f, 1.50f, 1.70f, 1.90f};
    private static final float CAMERA_LERP_SPEED = 8.0f;

    private int zoomIdx = 0;
    private Entity playerEntity;
    private final List<String> textures = new ArrayList<>();

    public WorldMapScreen(GdxGame game) {
        super(game, Optional.empty(), Optional.of(ADDITIONAL_TEXTURES));
        logger.debug("[WorldMapScreen] Initializing world map");

        // Ensure CutsceneService is registered
        if (ServiceLocator.getCutsceneService() == null) {
            logger.info("Registering CutsceneService for manual testing...");
            ServiceLocator.registerCutsceneService(new CutsceneService());
        }

        loadTextures();
        createEntities();
        createNodes();
    }

    @Override
    protected Entity constructEntity(Stage stage) {
        Entity ui = new Entity();
        ui.addComponent(new InputDecorator(stage, 10))
                .addComponent(new WorldMapNavigationMenu())
                .addComponent(new WorldMapNavigationMenuActions(game))
                .addComponent(new AnimatedDropdownMenu());
        return ui;
    }

    private void createEntities() {
        Entity worldMapEntity = new Entity();
        worldMapEntity.addComponent(new WorldMapRenderComponent(WORLD_SIZE));
        ServiceLocator.getEntityService().register(worldMapEntity);

        playerEntity = new Entity();
        playerEntity.setPosition(new Vector2(WORLD_WIDTH * 0.1f, WORLD_HEIGHT * 0.25f));
        playerEntity.addComponent(new WorldMapPlayerComponent(WORLD_SIZE));
        ServiceLocator.getEntityService().register(playerEntity);

        CameraComponent camera = renderer.getCamera();
        camera.getEntity().setPosition(WORLD_WIDTH * 0.1f, WORLD_HEIGHT * 0.25f);
        if (camera.getCamera() instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
            orthographicCamera.zoom = ZOOM_STEPS[zoomIdx];
        }

        playerEntity.getEvents().addListener("enterNode", this::onNodeEnter);
    }

    private void loadTextures() {
        for (WorldMapNode node : ServiceLocator.getWorldMapService().getAllNodes()) {
            textures.add(node.getNodeTexture());
        }
        ServiceLocator.getResourceService().loadTextures(textures.toArray(new String[0]));
        ServiceLocator.getResourceService().loadAll();
    }

    private void createNodes() {
        WorldMapService worldMapService = ServiceLocator.getWorldMapService();
        List<WorldMapNode> nodes = worldMapService.getAllNodes();
        ProfileService profileService = ServiceLocator.getProfileService();

        if (profileService != null) {
            List<String> completedNodes = profileService.getProfile().getCompletedNodes();
            for (String nodeId : completedNodes) {
                worldMapService.completeNode(nodeId);
                worldMapService.lockNode(nodeId, "This level has already been completed.");
            }
            String currentLevel = profileService.getProfile().getCurrentLevel();
            worldMapService.unlockNode(currentLevel);
        }

        for (WorldMapNode node : nodes) {
            Entity nodeEntity = new Entity();
            float worldX = node.getPositionX() * WORLD_WIDTH;
            float worldY = node.getPositionY() * WORLD_HEIGHT;
            nodeEntity.setPosition(worldX, worldY);
            nodeEntity.addComponent(new WorldMapNodeRenderComponent(node, WORLD_SIZE, 80f));
            ServiceLocator.getEntityService().register(nodeEntity);
        }
    }

    @Override
    public void render(float delta) {
        handleZoomInput();
        updateCamera();
        super.render(delta);

        // 🎬 Press V to safely play cutscene
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            String cutscenePath = "cutscene3_the_choice"; // include folder + .json
            File fileCheck = new File(Gdx.files.internal("cutscenes/" + cutscenePath + ".json").path());

            if (!fileCheck.exists()) {
                logger.error("❌ Cutscene file not found: {}", cutscenePath);
                return;
            }

            logger.info("🎬 Playing cutscene: {}", cutscenePath);
            ServiceLocator.getCutsceneService().playCutscene(cutscenePath, id -> {
                logger.info("✅ Cutscene finished — returning to world map");
                game.setScreen(GdxGame.ScreenType.WORLD_MAP);
            });
        }


    }

    private void updateCamera() {
        if (playerEntity != null) {
            Vector2 playerPos = playerEntity.getPosition();
            CameraComponent camera = renderer.getCamera();
            Vector2 currentCameraPos = camera.getEntity().getPosition();

            float deltaTime = Gdx.graphics.getDeltaTime();
            float lerpFactor = 1.0f - (float) Math.pow(0.5, CAMERA_LERP_SPEED * deltaTime);

            float newX = MathUtils.lerp(currentCameraPos.x, playerPos.x, lerpFactor);
            float newY = MathUtils.lerp(currentCameraPos.y, playerPos.y, lerpFactor);

            camera.getEntity().setPosition(newX, newY);
            clampCamera(camera);
        }
    }

    private void clampCamera(CameraComponent camera) {
        Vector2 cameraPos = camera.getEntity().getPosition();
        com.badlogic.gdx.graphics.Camera gdxCamera = camera.getCamera();

        float zoom = 1.0f;
        if (gdxCamera instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
            zoom = orthographicCamera.zoom;
        }

        float effectiveViewportWidth = gdxCamera.viewportWidth * zoom;
        float effectiveViewportHeight = gdxCamera.viewportHeight * zoom;
        float minX = effectiveViewportWidth / 2f;
        float maxX = WORLD_WIDTH - effectiveViewportWidth / 2f;
        float minY = effectiveViewportHeight / 2f;
        float maxY = WORLD_HEIGHT - effectiveViewportHeight / 2f;

        camera.getEntity().setPosition(
                MathUtils.clamp(cameraPos.x, minX, maxX),
                MathUtils.clamp(cameraPos.y, minY, maxY)
        );
    }

    private void handleZoomInput() {
        CameraComponent camera = renderer.getCamera();

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && zoomIdx < ZOOM_STEPS.length - 1) {
            zoomIdx++;
            if (camera.getCamera() instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
                orthographicCamera.zoom = ZOOM_STEPS[zoomIdx];
                logger.info("Zoom OUT → {}", ZOOM_STEPS[zoomIdx]);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.K) && zoomIdx > 0) {
            zoomIdx--;
            if (camera.getCamera() instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
                orthographicCamera.zoom = ZOOM_STEPS[zoomIdx];
                logger.info("Zoom IN → {}", ZOOM_STEPS[zoomIdx]);
            }
        }
    }

    private void onNodeEnter(WorldMapNode node) {
        logger.info("[WorldMapScreen] Entering node: {}", node.getLabel());
        game.setScreen(node.getTargetScreen());
    }
}
