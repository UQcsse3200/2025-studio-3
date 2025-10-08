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
import com.csse3200.game.progression.Profile;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WorldMapService;
import com.csse3200.game.ui.WorldMapNode;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import com.csse3200.game.ui.tutorial.WorldMapTutorial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** World map screen */
public class WorldMapScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);
  private static final String[] ADDITIONAL_TEXTURES = {
    "images/backgrounds/world_map.png",
    "images/entities/character.png",
    "images/nodes/completed.png",
    "images/nodes/locked.png",
    "images/ui/glow.png",
    "images/ui/keycap_up.png",
    "images/ui/keycap_down.png",
    "images/ui/keycap_left.png",
    "images/ui/keycap_right.png",
    "images/ui/label_bg.png",
    "images/ui/key_e.png"
  };
  private static final float WORLD_WIDTH = 3000f;
  private static final float WORLD_HEIGHT = 2000f;
  private static final Vector2 WORLD_SIZE = new Vector2(WORLD_WIDTH, WORLD_HEIGHT);
  private static final float[] ZOOM_STEPS = {1.20f, 1.35f, 1.50f, 1.70f, 1.90f};
  private static final float CAMERA_LERP_SPEED = 8.0f;
  private static final String LOG_ZOOM_SAVE_FAIL = "[WorldMapScreen] Failed to save zoom idx: {}";

  private boolean followCamera = true; // When false, manual panning is active
  private int zoomIdx = 0;
  private Entity playerEntity;
  private final List<String> textures = new ArrayList<>();

  // One-shot smooth recenter flag and threshold (in world units)
  private boolean smoothRecentering = false;
  private static final float RECENTER_STOP_EPSILON = 2f;

  /** Prefix used to identify level nodes, e.g., "level1", "levelTwo". */
  private static final String LEVEL_PREFIX = "level";

  // Special non-level nodes that should always be unlocked but never auto-completed
  private static final java.util.Set<String> SPECIAL_NODES =
      new java.util.HashSet<>(java.util.List.of("skills", "shop", "minigames"));

  public WorldMapScreen(GdxGame game) {
    super(game, Optional.empty(), Optional.of(ADDITIONAL_TEXTURES));
    logger.debug("[WorldMapScreen] Initializing world map");
    loadTextures();
    createBackground();
    createNodes();
    createPlayer();
  }

  @Override
  protected Entity constructEntity(Stage stage) {
    Entity ui = new Entity();
    ui.addComponent(new InputDecorator(stage, 10))
        .addComponent(new WorldMapNavigationMenu())
        .addComponent(new WorldMapNavigationMenuActions(game))
        .addComponent(new AnimatedDropdownMenu())
        .addComponent(new Terminal())
        .addComponent(ServiceLocator.getInputService().getInputFactory().createForTerminal())
        .addComponent(new TerminalDisplay())
        .addComponent(new AnimatedDropdownMenu())
        .addComponent(new WorldMapTutorial())
        .addComponent(new AnimatedDropdownMenu())
        .addComponent(new WorldMapZoomInputComponent(this, 12))
        .addComponent(new WorldMapPanInputComponent(this, 12))
        .addComponent(new WorldMapClickInputComponent(this, playerEntity, 12));
    return ui;
  }

  public CameraComponent getCameraComponent() {
    return renderer.getCamera();
  }

  /** Creates the world map background entity (must be registered before nodes and player). */
  private void createBackground() {
    Entity worldMapEntity = new Entity();
    worldMapEntity.addComponent(new WorldMapRenderComponent(WORLD_SIZE));
    ServiceLocator.getEntityService().register(worldMapEntity);
  }

  /** Creates and registers the world-map player entity (rendered above nodes). */
  private void createPlayer() {
    playerEntity = new Entity();

    // Restore last saved world-map position if available; otherwise use default
    float defaultX = WORLD_WIDTH * 0.1f;
    float defaultY = WORLD_HEIGHT * 0.25f;
    float startX = defaultX;
    float startY = defaultY;

    var profileService = ServiceLocator.getProfileService();
    if (profileService != null) {
      float savedX = profileService.getProfile().getWorldMapX();
      float savedY = profileService.getProfile().getWorldMapY();
      if (savedX >= 0f && savedY >= 0f) {
        startX = savedX;
        startY = savedY;
      }
      int savedZoom = profileService.getProfile().getWorldMapZoomIdx();
      if (savedZoom >= 0 && savedZoom < ZOOM_STEPS.length) {
        zoomIdx = savedZoom;
      }
    }

    playerEntity.setPosition(new Vector2(startX, startY));
    playerEntity.addComponent(new WorldMapPlayerComponent(WORLD_SIZE));
    ServiceLocator.getEntityService().register(playerEntity);
    ServiceLocator.getWorldMapService().registerPlayer(playerEntity);

    // Setup camera to follow player and start at the same place
    CameraComponent camera = renderer.getCamera();
    camera.getEntity().setPosition(startX, startY);

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
    List<WorldMapNode> nodes = worldMapService.getAllNodes();
    var profileService = ServiceLocator.getProfileService();

    if (profileService != null && profileService.getProfile() != null) {
      handleProfileBasedNodeSetup(worldMapService, nodes, profileService.getProfile());
    } else {
      handleDefaultNodeSetup(worldMapService, nodes);
    }

    registerNodeEntities(worldMapService, nodes);
  }

  /**
   * Handles map setup when a profile exists (normal case). Unlocks all nodes up to the current
   * level and marks previous ones as completed. Special nodes are only unlocked, never marked
   * completed.
   */
  private void handleProfileBasedNodeSetup(
      WorldMapService wms, List<WorldMapNode> nodes, Profile profile) {
    String current = profile.getCurrentLevel();
    Set<String> toUnlock = new HashSet<>(SPECIAL_NODES);
    Set<String> toComplete = new HashSet<>();

    if (current != null && !current.isEmpty()) {
      List<String> chain = buildChainUpTo(wms, current);

      unlockAndCompleteChain(chain, toUnlock, toComplete);
      syncProfileCompletion(profile, toComplete);
      ensureSpecialNodesUnlocked(profile);
    }

    applyNodeStates(wms, nodes, toUnlock, toComplete);
  }

  /**
   * Handles map setup when there is no active profile (first-time run or error). Only unlocks the
   * default special nodes.
   */
  private void handleDefaultNodeSetup(WorldMapService wms, List<WorldMapNode> nodes) {
    for (WorldMapNode node : nodes) {
      String key = node.getRegistrationKey();
      if (SPECIAL_NODES.contains(key)) {
        wms.unlockNode(key);
      } else {
        wms.lockNode(key, "Locked until you reach this node.");
      }
    }
  }

  /**
   * Builds a linear chain of level keys from the first node to the given current key (inclusive).
   * Filters only level nodes, sorts by progression order, and includes up to currentKey. If
   * currentKey is not a valid level key, returns an empty list.
   */
  private List<String> buildChainUpTo(WorldMapService wms, String currentKey) {
    List<String> chain = new ArrayList<>();
    if (wms == null || currentKey == null || currentKey.isEmpty()) {
      return chain;
    }
    List<String> levelKeys = getSortedLevelKeys(wms);
    return collectChainUntil(levelKeys, currentKey);
  }

  /** Returns all level-like keys sorted deterministically by progression index. */
  private List<String> getSortedLevelKeys(WorldMapService wms) {
    List<String> levelKeys = new ArrayList<>();
    for (WorldMapNode node : wms.getAllNodes()) {
      String key = node.getRegistrationKey();
      if (key != null && key.startsWith(LEVEL_PREFIX)) {
        levelKeys.add(key);
      }
    }
    levelKeys.sort((a, b) -> Integer.compare(parseLevelIndex(a), parseLevelIndex(b)));
    return levelKeys;
  }

  /**
   * Builds a chain from the beginning until the currentKey (inclusive). Returns empty if not found.
   */
  private List<String> collectChainUntil(List<String> levelKeys, String currentKey) {
    List<String> chain = new ArrayList<>();
    boolean found = false;
    for (String key : levelKeys) {
      chain.add(key);
      if (currentKey.equals(key)) {
        found = true;
        break;
      }
    }
    if (!found) {
      chain.clear();
    }
    return chain;
  }

  /** Parses a level key into an integer progression index. */
  private int parseLevelIndex(String key) {
    if (key == null) return Integer.MAX_VALUE;
    String k = key.trim();
    switch (k) {
      case "levelOne":
        return 1;
      case "levelTwo":
        return 2;
      case "levelThree":
        return 3;
      case "levelFour":
        return 4;
      case "levelFive":
        return 5;
      default:
        return Integer.MAX_VALUE; // unknown -> push to end
    }
  }

  /**
   * Marks all previous nodes in the chain as completed and unlocks all nodes up to current. Special
   * nodes are only unlocked, not completed.
   */
  private void unlockAndCompleteChain(
      List<String> chain, Set<String> toUnlock, Set<String> toComplete) {
    for (int i = 0; i < chain.size(); i++) {
      String key = chain.get(i);
      toUnlock.add(key);
      // Mark previous ones as completed, exclude current and special nodes
      if (i < chain.size() - 1 && !SPECIAL_NODES.contains(key)) {
        toComplete.add(key);
      }
    }
  }

  /** Writes completed state back into the profile to keep persistence consistent. */
  private void syncProfileCompletion(Profile profile, Set<String> toComplete) {
    for (String key : toComplete) {
      if (!profile.isNodeCompleted(key)) {
        profile.completeNode(key);
      }
    }
  }

  /** Ensures the three special nodes are always unlocked (but never marked completed). */
  private void ensureSpecialNodesUnlocked(Profile profile) {
    for (String s : SPECIAL_NODES) {
      profile.unlockNode(s);
    }
  }

  /** Applies unlock/completion state to all map nodes visually and logically. */
  private void applyNodeStates(
      WorldMapService wms, List<WorldMapNode> nodes, Set<String> toUnlock, Set<String> toComplete) {
    for (WorldMapNode node : nodes) {
      String key = node.getRegistrationKey();
      if (toComplete.contains(key)) {
        wms.completeNode(key);
        wms.unlockNode(key);
      } else if (toUnlock.contains(key)) {
        wms.unlockNode(key);
      } else {
        wms.lockNode(key, "Locked until you reach this node.");
      }
    }
  }

  /** Registers node entities for rendering on the world map. */
  private void registerNodeEntities(WorldMapService wms, List<WorldMapNode> nodes) {
    for (WorldMapNode node : nodes) {
      Entity nodeEntity = new Entity();
      float worldX = node.getPositionX() * WORLD_WIDTH;
      float worldY = node.getPositionY() * WORLD_HEIGHT;
      nodeEntity.setPosition(worldX, worldY);
      WorldMapNodeRenderComponent comp = new WorldMapNodeRenderComponent(node, WORLD_SIZE, 80f);
      nodeEntity.addComponent(comp);
      wms.registerNodeRenderComponent(comp);
      ServiceLocator.getEntityService().register(nodeEntity);
    }
  }

  @Override
  public void render(float delta) {
    handleZoomInput();

    // 统一在每帧强制相机缩放到当前 step，避免不同步（提取为独立方法以降低嵌套/复杂度）
    enforceCameraZoomStep();

    // If WASD is pressed, smoothly recenter view to player when player is not moving
    if (playerEntity != null
        && (Gdx.input.isKeyJustPressed(Input.Keys.W)
            || Gdx.input.isKeyJustPressed(Input.Keys.A)
            || Gdx.input.isKeyJustPressed(Input.Keys.S)
            || Gdx.input.isKeyJustPressed(Input.Keys.D))) {
      WorldMapPlayerComponent comp = playerEntity.getComponent(WorldMapPlayerComponent.class);
      if (comp != null && !comp.isCurrentlyMoving()) {
        startSmoothRecenterToPlayer();
      }
    }

    // While moving, force auto-follow and ignore manual pan
    if (isPlayerCurrentlyMoving()) {
      followCamera = true;
    }

    // Run one-shot smooth recenter if requested (does not enable follow mode)
    updateSmoothRecentering();

    updateCamera();
    super.render(delta);
  }

  private void enforceCameraZoomStep() {
    CameraComponent cam = renderer.getCamera();
    if (!(cam.getCamera() instanceof com.badlogic.gdx.graphics.OrthographicCamera oc)) {
      return;
    }
    float target = ZOOM_STEPS[zoomIdx];
    if (oc.zoom == target) {
      return;
    }
    oc.zoom = target;
    // Clamp position after zoom change to ensure view never shows outside the world
    clampCamera(cam);
  }

  /** Updates the camera to follow the player with smooth interpolation when follow is enabled. */
  private void updateCamera() {
    if (!followCamera || playerEntity == null) {
      return;
    }
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

  /**
   * Performs a one-shot smooth recenter of the camera toward the player's current position without
   * enabling continuous follow mode. Stops automatically when close enough or when follow mode is
   * enabled due to player movement.
   */
  private void updateSmoothRecentering() {
    if (!smoothRecentering || playerEntity == null) {
      return;
    }
    // If follow mode takes over (e.g., player started moving), cancel the one-shot recenter
    if (followCamera) {
      smoothRecentering = false;
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

    float dx = playerPos.x - newX;
    float dy = playerPos.y - newY;
    if (Math.hypot(dx, dy) <= RECENTER_STOP_EPSILON) {
      smoothRecentering = false;
    }
  }

  /** Request a smooth, one-shot recenter to the player's current position. */
  public void startSmoothRecenterToPlayer() {
    if (playerEntity == null) return;
    smoothRecentering = true;
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
    float newX = Math.clamp(cameraPos.x, minX, maxX);
    float newY = Math.clamp(cameraPos.y, minY, maxY);

    camera.getEntity().setPosition(newX, newY);
  }

  /** Persist the current zoom step index to the active profile, if available. */
  private void persistZoomIdx() {
    var ps = ServiceLocator.getProfileService();
    if (ps != null && ps.getProfile() != null) {
      ps.getProfile().setWorldMapZoomIdx(zoomIdx);
      try {
        ps.saveCurrentProfile();
      } catch (Exception e) {
        logger.debug(LOG_ZOOM_SAVE_FAIL, e.getMessage());
      }
    }
  }

  /** Handles the zoom input. */
  private void handleZoomInput() {
    CameraComponent camera = renderer.getCamera();

    if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && zoomIdx < ZOOM_STEPS.length - 1) {
      zoomIdx++;
      if (camera.getCamera()
          instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
        orthographicCamera.zoom = ZOOM_STEPS[zoomIdx];
        clampCamera(camera);
        logger.info("Zoom OUT → {}", ZOOM_STEPS[zoomIdx]);
        persistZoomIdx();
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.K) && zoomIdx > 0) {
      zoomIdx--;
      if (camera.getCamera()
          instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
        orthographicCamera.zoom = ZOOM_STEPS[zoomIdx];
        clampCamera(camera);
        logger.info("Zoom IN → {}", ZOOM_STEPS[zoomIdx]);
        persistZoomIdx();
      }
    }
  }

  /** Adjust zoom by a number of discrete steps (negative to zoom in, positive to zoom out). */
  public void stepZoom(int steps) {
    int newIdx = MathUtils.clamp(zoomIdx + steps, 0, ZOOM_STEPS.length - 1);
    if (newIdx == zoomIdx) {
      return;
    }
    zoomIdx = newIdx;
    var camera = renderer.getCamera();
    if (camera.getCamera() instanceof com.badlogic.gdx.graphics.OrthographicCamera oc) {
      oc.zoom = ZOOM_STEPS[zoomIdx];
      clampCamera(camera);
      persistZoomIdx();
    }
  }

  /** Called by pan input to disable camera follow mode (manual panning). */
  public void startManualPan() {
    followCamera = false;
    smoothRecentering = false; // cancel any ongoing smooth recenter
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

  /** Immediately centers the camera on the player without enabling auto-follow. */
  public void centerCameraOnPlayer() {
    if (playerEntity == null) return;
    CameraComponent camera = renderer.getCamera();
    Vector2 playerPos = playerEntity.getPosition();
    camera.getEntity().setPosition(playerPos.x, playerPos.y);
    clampCamera(camera);
  }

  /** Player enters a node. */
  /**
   * Handles when player enters a node. Does NOT change currentLevel (it always points to last
   * unlocked level). Only saves current world map position to the profile.
   */
  private void onNodeEnter(WorldMapNode node) {
    var ps = ServiceLocator.getProfileService();
    if (ps != null && ps.getProfile() != null) {
      ps.saveCurrentProfile();
    }

    logger.info(
        "[WorldMapScreen] Entering node: {} (key={})", node.getLabel(), node.getRegistrationKey());

    // If this node targets the main game, pass the registration key as the level override.
    if (node.getTargetScreen() == GdxGame.ScreenType.MAIN_GAME) {
      String levelKey = node.getRegistrationKey();
      game.setScreen(GdxGame.ScreenType.MAIN_GAME, levelKey);
    } else {
      game.setScreen(node.getTargetScreen());
    }
  }

  /** Returns true if the world-map player is currently moving along a path or between nodes. */
  public boolean isPlayerCurrentlyMoving() {
    if (playerEntity == null) return false;
    WorldMapPlayerComponent comp = playerEntity.getComponent(WorldMapPlayerComponent.class);
    return comp != null && comp.isCurrentlyMoving();
  }
}
