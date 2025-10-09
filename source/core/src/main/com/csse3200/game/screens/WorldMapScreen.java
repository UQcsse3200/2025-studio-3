package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.CameraComponent;
import com.csse3200.game.components.worldmap.AnimatedDropdownMenu;
import com.csse3200.game.components.worldmap.WorldMapClickInputComponent;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenuActions;
import com.csse3200.game.components.worldmap.WorldMapNodeRenderComponent;
import com.csse3200.game.components.worldmap.WorldMapPanInputComponent;
import com.csse3200.game.components.worldmap.WorldMapPlayerComponent;
import com.csse3200.game.components.worldmap.WorldMapRenderComponent;
import com.csse3200.game.components.worldmap.WorldMapZoomInputComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WorldMapService;
import com.csse3200.game.ui.WorldMapNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** World map screen */
public class WorldMapScreen extends BaseScreen {
    private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);

    private static final String[] ADDITIONAL_TEXTURES = {
            "images/backgrounds/world_map.png",
            "images/entities/character.png",
            "images/nodes/completed.png",
            "images/nodes/locked.png"
    };

    // Dev hotkeys — pass *bare* names to CutsceneService, check file existence first.
    private static final String DEV_CUTSCENE_NAME_N = "cutscene3";
    private static final String DEV_CUTSCENE_NAME_V = "cutscene3_the_choice";

    private static final float WORLD_WIDTH = 3000f;
    private static final float WORLD_HEIGHT = 2000f;
    private static final Vector2 WORLD_SIZE = new Vector2(WORLD_WIDTH, WORLD_HEIGHT);
    private static final float[] ZOOM_STEPS = {1.20f, 1.35f, 1.50f, 1.70f, 1.90f};
    private static final float CAMERA_LERP_SPEED = 8.0f;

    private int zoomIdx = 0;
    private Entity playerEntity;
    private final List<String> textures = new ArrayList<>();

    /** When true, camera smoothly follows the player; manual pan disables this. */
    private boolean followCamera = true;

    public WorldMapScreen(GdxGame game) {
        super(game, Optional.empty(), Optional.of(ADDITIONAL_TEXTURES));
        logger.debug("[WorldMapScreen] Initializing world map");
        loadTextures();
        createEntities();
        createNodes();
    }

    /** Exposes the camera component to input components. */
    public CameraComponent getCameraComponent() {
        return renderer.getCamera();
    }

    @Override
    protected Entity constructEntity(Stage stage) {
        Entity ui = new Entity();
        ui.addComponent(new InputDecorator(stage, 10))
                .addComponent(new WorldMapNavigationMenu())
                .addComponent(new WorldMapNavigationMenuActions(game))
                .addComponent(new AnimatedDropdownMenu())
                // Register inputs (priorities: lower than Stage(10) so UI wins first)
                .addComponent(new WorldMapZoomInputComponent(this, 5))
                .addComponent(new WorldMapPanInputComponent(this, 6))
                .addComponent(new WorldMapClickInputComponent(this, playerEntity, 12));
        return ui;
    }

    /** Creates the entities for the world map screen. */
    private void createEntities() {
        // World map background entity
        Entity worldMapEntity = new Entity();
        worldMapEntity.addComponent(new WorldMapRenderComponent(WORLD_SIZE));
        ServiceLocator.getEntityService().register(worldMapEntity);

        // Player entity
        playerEntity = new Entity();
        playerEntity.setPosition(new Vector2(WORLD_WIDTH * 0.1f, WORLD_HEIGHT * 0.25f)); // Start pos
        playerEntity.addComponent(new WorldMapPlayerComponent(WORLD_SIZE));
        ServiceLocator.getEntityService().register(playerEntity);

        // Camera setup
        CameraComponent camera = renderer.getCamera();
        camera.getEntity().setPosition(WORLD_WIDTH * 0.1f, WORLD_HEIGHT * 0.25f);
        if (camera.getCamera()
                instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
            orthographicCamera.zoom = ZOOM_STEPS[zoomIdx];
        }

        // Listen for node events
        playerEntity.getEvents().addListener("enterNode", this::onNodeEnter);
    }

    /** Loads the textures for the nodes. */
    private void loadTextures() {
        for (WorldMapNode node : ServiceLocator.getWorldMapService().getAllNodes()) {
            textures.add(node.getNodeTexture());
        }
        ServiceLocator.getResourceService().loadTextures(textures.toArray(new String[0]));
        ServiceLocator.getResourceService().loadAll();
    }

    /** Creates the nodes for the world map. */
    private void createNodes() {
        WorldMapService worldMapService = ServiceLocator.getWorldMapService();

        // Get existing nodes and mark completed ones
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

        // Create render entities for each node
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

        // Dev hotkeys every frame
        handleDevHotkeys();

        super.render(delta);
    }

    /** Smooth follow camera that tracks the player when followCamera is enabled. */
    private void updateCamera() {
        if (!followCamera || playerEntity == null) {
            return;
        }
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

    /** Clamps the camera to the world bounds. */
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

        float newX = MathUtils.clamp(cameraPos.x, minX, maxX);
        float newY = MathUtils.clamp(cameraPos.y, minY, maxY);

        camera.getEntity().setPosition(newX, newY);
    }

    /** Handles the Q/K keyboard zoom input. */
    private void handleZoomInput() {
        CameraComponent camera = renderer.getCamera();

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && zoomIdx < ZOOM_STEPS.length - 1) {
            zoomIdx++;
            if (camera.getCamera()
                    instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
                orthographicCamera.zoom = ZOOM_STEPS[zoomIdx];
                logger.info("Zoom OUT → {}", ZOOM_STEPS[zoomIdx]);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.K) && zoomIdx > 0) {
            zoomIdx--;
            if (camera.getCamera()
                    instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
                orthographicCamera.zoom = ZOOM_STEPS[zoomIdx];
                logger.info("Zoom IN → {}", ZOOM_STEPS[zoomIdx]);
            }
        }
    }

    /**
     * Adjust zoom by a number of discrete steps (negative to zoom in, positive to zoom out).
     * Called by WorldMapZoomInputComponent (mouse wheel).
     */
    public void stepZoom(int steps) {
        int newIdx = MathUtils.clamp(zoomIdx + steps, 0, ZOOM_STEPS.length - 1);
        if (newIdx == zoomIdx) {
            return;
        }
        zoomIdx = newIdx;
        CameraComponent camera = renderer.getCamera();
        if (camera.getCamera() instanceof com.badlogic.gdx.graphics.OrthographicCamera oc) {
            oc.zoom = ZOOM_STEPS[zoomIdx];
            clampCamera(camera);
        }
    }

    /** Disables follow mode when a manual pan begins. */
    public void startManualPan() {
        followCamera = false;
    }

    /**
     * Pans the camera by the given screen-space delta (in pixels). Dragging moves the view with the
     * cursor (grab-and-drag behavior).
     */
    public void panByScreenDelta(float deltaScreenX, float deltaScreenY) {
        CameraComponent camera = renderer.getCamera();
        com.badlogic.gdx.graphics.Camera gdxCam = camera.getCamera();

        float zoom = 1.0f;
        if (gdxCam instanceof com.badlogic.gdx.graphics.OrthographicCamera oc) {
            zoom = oc.zoom;
        }

        float screenW = Math.max(Gdx.graphics.getWidth(), 1);
        float screenH = Math.max(Gdx.graphics.getHeight(), 1);

        float worldPerPixelX = (gdxCam.viewportWidth * zoom) / screenW;
        float worldPerPixelY = (gdxCam.viewportHeight * zoom) / screenH;

        // Move camera opposite on X (so content follows the cursor), same sign on Y (LibGDX coords)
        float dxWorld = -deltaScreenX * worldPerPixelX;
        float dyWorld = +deltaScreenY * worldPerPixelY;

        Vector2 pos = camera.getEntity().getPosition();
        camera.getEntity().setPosition(pos.x + dxWorld, pos.y + dyWorld);
        clampCamera(camera);
    }

    /** Returns true if the world-map player is currently moving between nodes. */
    public boolean isPlayerCurrentlyMoving() {
        if (playerEntity == null) return false;
        WorldMapPlayerComponent comp = playerEntity.getComponent(WorldMapPlayerComponent.class);
        return comp != null && comp.isCurrentlyMoving();
    }

    /**
     * Handles the event when the player enters a node.
     *
     * @param node the node the player entered
     */
    private void onNodeEnter(WorldMapNode node) {
        logger.info("[WorldMapScreen] Entering node: {}", node.getLabel());
        game.setScreen(node.getTargetScreen());
    }

    // -------------------------
    // Dev hotkeys (N & V)
    // -------------------------
    private void handleDevHotkeys() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            playDevCutsceneIfExists(DEV_CUTSCENE_NAME_N);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            playDevCutsceneIfExists(DEV_CUTSCENE_NAME_V);
        }
    }

    private void playDevCutsceneIfExists(String bareName) {
        // Our CutsceneService expects bare names; the loader resolves to "cutscenes/<name>.json".
        String resolved = "cutscenes/" + bareName + ".json";
        if (!Gdx.files.internal(resolved).exists()) {
            logger.error("Cutscene JSON not found at {}", resolved);
            return;
        }
        logger.info("🎬 Playing cutscene: {}", resolved);
        ServiceLocator.getCutsceneService()
                .playCutscene(
                        bareName,
                        id -> {
                            // callback is immediate in your service; no-op is fine
                        });
    }
}
