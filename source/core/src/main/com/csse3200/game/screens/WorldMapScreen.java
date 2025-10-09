package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.CameraComponent;
import com.csse3200.game.components.worldmap.AnimatedDropdownMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenuActions;
import com.csse3200.game.components.worldmap.WorldMapNodeRenderComponent;
import com.csse3200.game.components.worldmap.WorldMapPlayerComponent;
import com.csse3200.game.components.worldmap.WorldMapRenderComponent;
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

public class WorldMapScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);
  private static final String[] ADDITIONAL_TEXTURES = {
    "images/backgrounds/world_map.png",
    "images/entities/character.png",
    "images/nodes/completed.png",
    "images/nodes/locked.png"
  };

  private static final String DEV_CUTSCENE_PATH = "cutscene3";

  private static final float WORLD_WIDTH = 3000f;
  private static final float WORLD_HEIGHT = 2000f;
  private static final Vector2 WORLD_SIZE = new Vector2(WORLD_WIDTH, WORLD_HEIGHT);
  private static final float[] ZOOM_STEPS = {1.20f, 1.35f, 1.50f, 1.70f, 1.90f};
  private static final float CAMERA_LERP_SPEED = 8.0f;

  private int zoomIdx = 0;
  private Entity playerEntity;
  private final List<String> textures = new ArrayList<>();

  /**
   * Constructor for the world map screen.
   *
   * @param game the game instance
   */
  public WorldMapScreen(GdxGame game) {
    super(game, Optional.empty(), Optional.of(ADDITIONAL_TEXTURES));
    logger.debug("[WorldMapScreen] Initializing world map");
    loadTextures();
    createEntities();
    createNodes();
  }

  /**
   * Constructs the UI entity for the world map screen.
   *
   * @param stage the stage to create the UI screen on
   * @return the UI entity
   */
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
    // Create world map background entity
    Entity worldMapEntity = new Entity();
    worldMapEntity.addComponent(new WorldMapRenderComponent(WORLD_SIZE));
    ServiceLocator.getEntityService().register(worldMapEntity);

    // Create player entity
    playerEntity = new Entity();
    playerEntity.setPosition(
        new Vector2(WORLD_WIDTH * 0.1f, WORLD_HEIGHT * 0.25f)); // Start position
    playerEntity.addComponent(new WorldMapPlayerComponent(WORLD_SIZE));
    ServiceLocator.getEntityService().register(playerEntity);

    // Setup camera to follow player
    CameraComponent camera = renderer.getCamera();
    camera.getEntity().setPosition(WORLD_WIDTH * 0.1f, WORLD_HEIGHT * 0.25f);

    // Set initial zoom on the underlying camera
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

    // DEV hotkey: press N to play the cutscene
    handleDevHotkey();

    super.render(delta);
  }

  /** Updates the camera to follow the player with smooth interpolation. */
  private void updateCamera() {
    if (playerEntity != null) {
      Vector2 playerPos = playerEntity.getPosition();
      CameraComponent camera = renderer.getCamera();
      Vector2 currentCameraPos = camera.getEntity().getPosition();

      // Smoothly interpolate camera position towards player position
      float deltaTime = Gdx.graphics.getDeltaTime();
      float lerpFactor = 1.0f - (float) Math.pow(0.5, CAMERA_LERP_SPEED * deltaTime);

      float newX = MathUtils.lerp(currentCameraPos.x, playerPos.x, lerpFactor);
      float newY = MathUtils.lerp(currentCameraPos.y, playerPos.y, lerpFactor);

      camera.getEntity().setPosition(newX, newY);
      clampCamera(camera);
    }
  }

  /**
   * Clamps the camera to the world bounds.
   *
   * @param camera the camera to clamp
   */
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

    // FIX: use MathUtils.clamp (Math.clamp doesn't exist in Java)
    float newX = MathUtils.clamp(cameraPos.x, minX, maxX);
    float newY = MathUtils.clamp(cameraPos.y, minY, maxY);

    camera.getEntity().setPosition(newX, newY);
  }

  /** Handles the zoom input. */
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

  /** Dev-only: press N to play the Level 3 intro cutscene from anywhere on the map. */
  private void handleDevHotkey() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
      // Resolve what the loader will actually look for:
      String resolved = "cutscenes/" + DEV_CUTSCENE_PATH + ".json";

      // Check the file exists before trying to load/play
      if (!Gdx.files.internal(resolved).exists()) {
        logger.error("Cutscene JSON not found at {}", resolved);
        return; // don't crash the app
      }

      ServiceLocator.getCutsceneService()
          .playCutscene(
              DEV_CUTSCENE_PATH, // bare name
              name -> {
                /* callback fires immediately on start in your service */
              });
    }
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
}
