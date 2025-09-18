package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.CameraComponent;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenuActions;
import com.csse3200.game.components.worldmap.WorldMapNodeRenderComponent;
import com.csse3200.game.components.worldmap.WorldMapPlayerComponent;
import com.csse3200.game.components.worldmap.WorldMapRenderComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WorldMapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * World map screen
 */
public class WorldMapScreen extends BaseScreen {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapScreen.class);
  private static final String WORLD_MAP_TEXTURE = "images/world_map.png";
  private static final String[] ADDITIONAL_TEXTURES = {
    "images/character.png",
    "images/node_completed.png",
  };
  private static final float WORLD_WIDTH = 3000f;
  private static final float WORLD_HEIGHT = 2000f;
  private static final Vector2 WORLD_SIZE = new Vector2(WORLD_WIDTH, WORLD_HEIGHT);
  private static final float[] ZOOM_STEPS = {1.20f, 1.35f, 1.50f, 1.70f, 1.90f};
  private int zoomIdx = 0;
  private Entity playerEntity;

  /**
   * Constructor for the world map screen.
   * 
   * @param game the game instance
   */
  public WorldMapScreen(GdxGame game) {
    super(game, Optional.of(WORLD_MAP_TEXTURE), Optional.of(ADDITIONAL_TEXTURES));
    logger.debug("[WorldMapScreen] Initializing world map");
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
    ui.addComponent(new WorldMapNavigationMenu()).addComponent(new WorldMapNavigationMenuActions(game));
    return ui;
  }
  
  /**
   * Creates the entities for the world map screen.
   */
  private void createEntities() {
    // Create world map background entity
    Entity worldMapEntity = new Entity();
    worldMapEntity.addComponent(new WorldMapRenderComponent(WORLD_SIZE));
    ServiceLocator.getEntityService().register(worldMapEntity);
    
    // Create player entity
    playerEntity = new Entity();
    playerEntity.setPosition(new Vector2(WORLD_WIDTH * 0.1f, WORLD_HEIGHT * 0.25f)); // Start position
    playerEntity.addComponent(new WorldMapPlayerComponent(WORLD_SIZE));
    // Player rendering would be handled by a separate render component
    ServiceLocator.getEntityService().register(playerEntity);
    
    // Setup camera to follow player
    CameraComponent camera = renderer.getCamera();
    camera.getEntity().setPosition(5f, 5f);
    
    // Set initial zoom on the underlying camera
    if (camera.getCamera() instanceof com.badlogic.gdx.graphics.OrthographicCamera orthographicCamera) {
      orthographicCamera.zoom = ZOOM_STEPS[zoomIdx];
    }
    
    // Listen for node events
    playerEntity.getEvents().addListener("enterNode", this::onNodeEnter);
  }
  
  /**
   * Creates the nodes for the world map.
   */
  private void createNodes() {
    WorldMapService worldMapService = ServiceLocator.getWorldMapService();
  
    // Get existing nodes and mark completed ones
    List<WorldMapNode> nodes = worldMapService.getAllNodes();
    if (ServiceLocator.getProfileService() != null) {
      List<String> completedNodes = ServiceLocator.getProfileService().getProfile().getCompletedNodes();
      for (String nodeId : completedNodes) {
        worldMapService.completeNode(nodeId);
      }
    }

    // Load the textures for the nodes
    List<String> textures = new ArrayList<>();
    for (WorldMapNode node : nodes) {
      textures.add(node.getNodeTexture());
    }
    ServiceLocator.getResourceService().loadTextures(textures.toArray(new String[0]));
    ServiceLocator.getResourceService().loadAll();
      
    // Create render entities for each node
    for (WorldMapNode node : nodes) {
      Entity nodeEntity = new Entity();
      float worldX = node.getPositionX() * WORLD_WIDTH;
      float worldY = node.getPositionY() * WORLD_HEIGHT;
      nodeEntity.setPosition(worldX / 100f, worldY / 100f); // Scale for engine
      nodeEntity.addComponent(new WorldMapNodeRenderComponent(node, WORLD_SIZE, 80f));
      ServiceLocator.getEntityService().register(nodeEntity);
    }
  }

  @Override
  public void render(float delta) {
    handleZoomInput();
    updateCamera();
    super.render(delta);
  }
  
  /**
   * Updates the camera to follow the player.
   */
  private void updateCamera() {
    if (playerEntity != null) {
      Vector2 playerPos = playerEntity.getPosition();
      CameraComponent camera = renderer.getCamera();
      camera.getEntity().setPosition(playerPos.x / 100f, playerPos.y / 100f);
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
    float minX = effectiveViewportWidth / 200f; // Scale factor adjustment
    float maxX = WORLD_WIDTH / 100f - effectiveViewportWidth / 200f;
    float minY = effectiveViewportHeight / 200f;
    float maxY = WORLD_HEIGHT / 100f - effectiveViewportHeight / 200f;
    float newX = Math.clamp(cameraPos.x, minX, maxX);
    float newY = Math.clamp(cameraPos.y, minY, maxY);
    
    camera.getEntity().setPosition(newX, newY);
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
    
    Gdx.app.postRunnable(() -> {
      try {
        game.setScreen(node.getTargetScreen());
          } catch (Exception error) {
        logger.error("[WorldMapScreen] Failed to enter screen {}; staying on world map.", node.getTargetScreen(), error);
          }
        });
  }
}
