package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.concurrency.JobSystem;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WorldMapService;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.ui.WorldMapNode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles player movement and interaction on the world map */
public class WorldMapPlayerComponent extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapPlayerComponent.class);
  private static final float PLAYER_SPEED = 200f;
  private static final float INTERACTION_DISTANCE = 150f;
  private static final float PROXIMITY_CHECK_INTERVAL =
      0.1f; // Check every 100ms instead of every frame
  private final Vector2 worldSize;
  private WorldMapNode nearbyNode = null;
  private Texture playerTexture;
  // Node-path navigation state
  private boolean isMoving = false;
  private Vector2 targetPosition = null;
  private List<WorldMapNode> pathNodes;
  private int currentNodeIndex = -1;
  private static final float ARRIVAL_THRESHOLD = 6f;
  // Specific nodes for ordered path and town
  private WorldMapNode levelOneNode;
  private WorldMapNode levelTwoNode;
  private WorldMapNode levelThreeNode;
  private WorldMapNode townNode;

  private CompletableFuture<WorldMapNode> proximityCheckFuture = null;
  private float timeSinceLastProximityCheck = 0f;

  public WorldMapPlayerComponent(Vector2 worldSize) {
    this.worldSize = worldSize;
  }

  @Override
  public void create() {
    super.create();
    playerTexture =
        ServiceLocator.getResourceService()
            .getAsset("images/entities/character.png", Texture.class);
    buildOrderedPath();
  }

  @Override
  public void update() {
    handleMovement();
    updateAsyncProximityCheck();
    handleNodeInteraction();
  }

  /** Handles node-path navigation and smooth auto-movement. */
  private void handleMovement() {
    float delta = Gdx.graphics.getDeltaTime();
    Vector2 position = entity.getPosition();

    // If currently moving, continue towards the target
    if (isMoving && targetPosition != null) {
      Vector2 toTarget = targetPosition.cpy().sub(position);
      float dist = toTarget.len();
      if (dist <= ARRIVAL_THRESHOLD) {
        position.set(targetPosition);
        isMoving = false;
        targetPosition = null;
      } else {
        Vector2 step = toTarget.nor().scl(PLAYER_SPEED * delta);
        if (step.len() > dist) {
          step.setLength(dist);
        }
        position.add(step);
      }

      // Clamp to world bounds (account for new character height of 110)
      position.x = Math.clamp(position.x, 0, worldSize.x - 96);
      position.y = Math.clamp(position.y, 0, worldSize.y - 110);

      entity.setPosition(position);
      return;
    }

    // Not moving: directional navigation
    if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
      WorldMapNode right = findDirectionalNeighbor(position, true);
      if (right != null) {
        targetPosition = getWorldCoords(right);
        isMoving = true;
        currentNodeIndex = (pathNodes != null) ? pathNodes.indexOf(right) : -1;
      }
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
      WorldMapNode left = findDirectionalNeighbor(position, false);
      if (left != null) {
        targetPosition = getWorldCoords(left);
        isMoving = true;
        currentNodeIndex = (pathNodes != null) ? pathNodes.indexOf(left) : -1;
      }
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
      if (isAtNode(levelThreeNode, position) && townNode != null) {
        targetPosition = getWorldCoords(townNode);
        isMoving = true;
      }
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
      if (isAtNode(townNode, position) && levelThreeNode != null) {
        targetPosition = getWorldCoords(levelThreeNode);
        isMoving = true;
        int idx = (pathNodes != null) ? pathNodes.indexOf(levelThreeNode) : 2;
        currentNodeIndex = (idx >= 0) ? idx : 2;
      }
    }
  }

  /** Build ordered path (1 -> 2 -> 3) and cache Town as the highest Y node */
  private void buildOrderedPath() {
    WorldMapService worldMapService = ServiceLocator.getWorldMapService();

    // Resolve specific nodes by key if available
    levelOneNode = worldMapService.getNode("levelOne");
    levelTwoNode = worldMapService.getNode("levelTwo");
    levelThreeNode = worldMapService.getNode("levelThree");

    // Identify Town: prefer key "skills"; if absent, fall back to the highest Y node
    townNode = worldMapService.getNode("skills");

    // Main D/A path is only levels (exclude Town), in left-to-right order
    pathNodes = new ArrayList<>();
    if (levelOneNode != null) pathNodes.add(levelOneNode);
    if (levelTwoNode != null) pathNodes.add(levelTwoNode);
    if (levelThreeNode != null) pathNodes.add(levelThreeNode);

    currentNodeIndex = -1; // recalc on-demand
  }

  /** Find nearest neighbor strictly to the left/right (excluding Town). */
  private WorldMapNode findDirectionalNeighbor(Vector2 pos, boolean toRight) {
    WorldMapService worldMapService = ServiceLocator.getWorldMapService();
    List<WorldMapNode> all = new ArrayList<>(worldMapService.getAllNodes());
    float worldX = pos.x;

    WorldMapNode best = null;
    float bestPrimary = Float.MAX_VALUE; // minimal horizontal gap |dx|
    float bestSecondary = Float.MAX_VALUE; // tie-break by squared distance

    for (WorldMapNode n : all) {
      if (n == null || n == townNode) continue; // Town is only via W/S
      float nx = n.getPositionX() * worldSize.x;
      float ny = n.getPositionY() * worldSize.y;
      float dx = nx - worldX;

      if (toRight) {
        if (dx <= 0f) continue; // must be strictly to the right
      } else {
        if (dx >= 0f) continue; // must be strictly to the left
        dx = -dx; // use positive gap for comparison
      }

      float primary = Math.abs(dx);
      float secondary = pos.dst2(new Vector2(nx, ny));

      if (primary < bestPrimary - 1e-3f
          || (Math.abs(primary - bestPrimary) <= 1e-3f && secondary < bestSecondary)) {
        bestPrimary = primary;
        bestSecondary = secondary;
        best = n;
      }
    }
    return best;
  }

  /** Convert node's normalised coordinates to world coordinates used by rendering. */
  private Vector2 getWorldCoords(WorldMapNode node) {
    float x = node.getPositionX() * worldSize.x;
    float y = node.getPositionY() * worldSize.y;
    return new Vector2(x, y);
  }

  /** Check if current position is at a given node (within arrival threshold). */
  private boolean isAtNode(WorldMapNode node, Vector2 pos) {
    if (node == null) return false;
    Vector2 nodePos = getWorldCoords(node);
    return pos.dst2(nodePos) <= ARRIVAL_THRESHOLD * ARRIVAL_THRESHOLD;
  }

  /**
   * Updates proximity checking using the job system for better performance. Checks for nearby nodes
   * asynchronously at a reduced frequency.
   */
  private void updateAsyncProximityCheck() {
    timeSinceLastProximityCheck += Gdx.graphics.getDeltaTime();

    // Check if we should start a new proximity check
    if (shouldStartNewProximityCheck()) {
      processCompletedFutureIfExists();
      startNewProximityCheck();
    }

    // Process any completed futures for responsiveness
    processCompletedFutureIfExists();
  }

  /** Determines if a new proximity check should be started. */
  private boolean shouldStartNewProximityCheck() {
    return timeSinceLastProximityCheck >= PROXIMITY_CHECK_INTERVAL
        && (proximityCheckFuture == null || proximityCheckFuture.isDone());
  }

  /** Starts a new proximity check using the job system. */
  private void startNewProximityCheck() {
    Vector2 playerPos = new Vector2(entity.getPosition()); // Copy position for thread safety
    proximityCheckFuture = JobSystem.launch(() -> checkNodeProximityAsync(playerPos));
    timeSinceLastProximityCheck = 0f;
  }

  /** Processes a completed future if one exists, handling results and errors. */
  private void processCompletedFutureIfExists() {
    if (proximityCheckFuture != null && proximityCheckFuture.isDone()) {
      try {
        WorldMapNode newNearbyNode = proximityCheckFuture.get();
        updateNearbyNode(newNearbyNode);
        proximityCheckFuture = null; // Clear the completed future
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.warn(
            "[WorldMapPlayerComponent] Proximity check was interrupted: {}", e.getMessage());
        proximityCheckFuture = null;
      } catch (Exception e) {
        logger.warn(
            "[WorldMapPlayerComponent] Error getting proximity check result: {}", e.getMessage());
        proximityCheckFuture = null;
      }
    }
  }

  /**
   * Performs the actual proximity checking computation asynchronously. This method runs on a
   * background thread via the JobSystem.
   *
   * @param playerPos The player's position (copied for thread safety)
   * @return The nearby node, or null if none found
   */
  private WorldMapNode checkNodeProximityAsync(Vector2 playerPos) {
    WorldMapService worldMapService = ServiceLocator.getWorldMapService();
    List<WorldMapNode> nodes = worldMapService.getAllNodes();

    for (WorldMapNode node : nodes) {
      float nodeX = node.getPositionX() * worldSize.x;
      float nodeY = node.getPositionY() * worldSize.y;

      // Use center of taller character (96w x 110h) for distance calculation
      float distance = Vector2.dst(playerPos.x + 48, playerPos.y + 55, nodeX + 40, nodeY + 40);

      if (distance < INTERACTION_DISTANCE) {
        return node;
      }
    }

    return null; // No nearby node found
  }

  /**
   * Updates the nearby node and triggers UI updates if needed. This method runs on the main thread.
   *
   * @param newNearbyNode The new nearby node (or null)
   */
  private void updateNearbyNode(WorldMapNode newNearbyNode) {
    WorldMapNode previousNearby = nearbyNode;
    nearbyNode = newNearbyNode;

    // Update node render components for prompt display if the nearby node changed
    if (previousNearby != nearbyNode) {
      ServiceLocator.getWorldMapService().updateNodeProximity(nearbyNode);
    }
  }

  /** Handles the interaction with the nodes. */
  private void handleNodeInteraction() {
    if (nearbyNode != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (nearbyNode.isUnlocked() && !nearbyNode.isCompleted()) {
        String message = "Do you want to enter " + nearbyNode.getLabel() + "?";
        ServiceLocator.getDialogService()
            .warning(
                nearbyNode.getLabel(),
                message,
                dialog -> {
                  logger.info("[WorldMapPlayerComponent] Entering node: {}", nearbyNode.getLabel());
                  entity.getEvents().trigger("enterNode", nearbyNode);
                },
                null);
      } else {
        String message =
            nearbyNode.getLockReason() != null
                ? nearbyNode.getLockReason()
                : "This node is not available.";
        ServiceLocator.getDialogService().error(nearbyNode.getLabel(), message);
        logger.info(
            "[WorldMapPlayerComponent] Node '{}' is not accessible: {}",
            nearbyNode.getLabel(),
            message);
      }
    }
  }

  /**
   * Gets the nearby node.
   *
   * @return the nearby node
   */
  public WorldMapNode getNearbyNode() {
    return nearbyNode;
  }

  /**
   * Draws the player texture at the world coordinates with appropriate size.
   *
   * @param batch the sprite batch
   */
  @Override
  protected void draw(SpriteBatch batch) {
    if (playerTexture != null) {
      Vector2 position = entity.getPosition();
      // Make character slightly taller: 96 width x 110 height
      batch.draw(playerTexture, position.x, position.y, 96f, 110f);
    }
  }

  /** Clean up any running background tasks when the component is disposed. */
  @Override
  public void dispose() {
    if (proximityCheckFuture != null && !proximityCheckFuture.isDone()) {
      proximityCheckFuture.cancel(true);
    }
    super.dispose();
  }
}
