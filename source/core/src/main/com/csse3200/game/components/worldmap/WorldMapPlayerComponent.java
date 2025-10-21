package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.concurrency.JobSystem;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import com.csse3200.game.services.WorldMapService;
import com.csse3200.game.services.WorldMapService.Direction;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.ui.WorldMapNode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles player logic on the World Map: - JSON-path based movement between nodes (W/A/S/D graph) -
 * Legacy directional navigation as a fallback - Nearby-node detection and E-to-enter prompt -
 * Drawing the player sprite
 *
 * <p>This version is refactored to reduce cognitive complexity and address common SonarQube
 * maintainability issues.
 */
@SuppressWarnings("java:S1854") // SonarQube is throwing false positives for useless variables.
public class WorldMapPlayerComponent extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapPlayerComponent.class);

  // Movement/interaction constants
  private static final float PLAYER_SPEED = 200f;
  private static final float INTERACTION_DISTANCE = 150f;
  private static final float ARRIVAL_THRESHOLD = 6f;
  private static final float NODE_SNAP_RADIUS = 36f;

  // Run proximity check every 100ms instead of each frame
  private static final float PROXIMITY_CHECK_INTERVAL = 0.1f;

  // BFS directions for JSON graph
  private static final Direction[] DIRS = {
    Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT
  };

  private final Vector2 worldSize;
  private Texture playerTexture;

  // Nearby-node UI status
  private WorldMapNode nearbyNode;

  // Free target movement (legacy path & fallback)
  private boolean isMoving;
  private Vector2 targetPosition;

  // Special nodes (used by legacy W/S transitions)
  private WorldMapNode levelThreeNode;
  private WorldMapNode townNode;

  // Screen-space rendering tweak so the sprite looks centred
  private float renderOffsetX = -15f;

  // Async proximity detection
  private CompletableFuture<WorldMapNode> proximityCheckFuture;
  private float timeSinceLastProximityCheck;

  // JSON-path movement state (queue of world-space waypoints)
  private final List<Vector2> waypointQueue = new ArrayList<>();
  private int waypointIndex = -1;
  private boolean pathMoving;

  // Reusable temp vectors to avoid allocations every frame
  private final Vector2 tmpPos = new Vector2();
  private final Vector2 toTarget = new Vector2();

  public WorldMapPlayerComponent(Vector2 worldSize) {
    this.worldSize = worldSize;
  }

  /** Creates a player component with an additional horizontal render offset. */
  public WorldMapPlayerComponent(Vector2 worldSize, float renderOffsetX) {
    this(worldSize);
    this.renderOffsetX = renderOffsetX;
  }

  @Override
  public void create() {
    super.create();
    playerTexture =
        ServiceLocator.getResourceService()
            .getAsset("images/entities/character.png", Texture.class);
    ServiceLocator.getResourceService().loadSounds(new String[] {"sounds/node_sound.mp3"});

    // Resolve a few special nodes used by legacy transitions
    resolveSpecialNodes();
  }

  @Override
  public void update() {
    handleMovement();
    updateAsyncProximityCheck();
    handleNodeInteraction();
  }

  private void persistWorldPos() {
    var ps = ServiceLocator.getProfileService();
    if (ps == null || ps.getProfile() == null) return;

    var pos = entity.getPosition();
    ps.getProfile().setWorldMapX(pos.x);
    ps.getProfile().setWorldMapY(pos.y);
  }

  // --------------------------------------------------------------------- //
  // Movement
  // --------------------------------------------------------------------- //

  /**
   * Top-level movement dispatch: 1) If following a JSON path → tick once. 2) Else if moving towards
   * a free target → tick once. 3) Else if a WASD key is pressed and on a node, only allow movement
   * if that direction exists in the JSON graph. Otherwise, block movement. 4) Else fallback to
   * legacy directional navigation.
   */
  private void handleMovement() {
    float delta = Gdx.graphics.getDeltaTime();

    if (advanceJsonPathIfMoving(delta)) return;
    if (advanceActiveTarget(delta)) return;

    String pressed = readWASDOnce();

    // If a WASD key was pressed, and the player is on a node, only allow movement
    // if that direction exists in the JSON graph. Otherwise, BLOCK movement.
    if (pressed != null && handleWASDFromNode(pressed, delta)) {
      // Either started a JSON path, or intentionally blocked movement (invalid direction on node)
      return;
    }

    // No valid JSON path started (or no key pressed) → allow legacy fallback
    handleLegacyDirectionalNavigation();
  }

  /**
   * If currently standing on a node, only allow WASD movement when that direction is defined in the
   * JSON graph for that node. If the direction is undefined, consume the key and block movement
   * (return true). If not on a node, return false so legacy navigation may proceed.
   *
   * @return true if movement was handled (either started JSON path or intentionally blocked); false
   *     if not on a node, so caller may run legacy fallback.
   */
  private boolean handleWASDFromNode(String pressed, float delta) {
    Vector2 cur = entity.getPosition();
    WorldMapNode at = getNearestNode(cur);
    if (at == null || !isOnNode(at, cur)) {
      // Not on a node → let caller proceed with legacy behavior
      return false;
    }

    WorldMapService svc = ServiceLocator.getWorldMapService();
    Direction direction = mapStringToDirection(pressed);
    if (direction == null) return true; // Invalid direction, block movement

    WorldMapService.Path def = svc.getPath(at.getRegistrationKey(), direction);

    if (def == null) {
      // On a node but this direction is NOT defined → block movement this frame
      logger.debug(
          "[WorldMap] Blocked movement: '{}' from '{}' not defined in JSON",
          pressed,
          at.getRegistrationKey());
      return true;
    }

    // Start JSON path from this node using the defined PathDef
    waypointQueue.clear();
    if (def.waypoints() != null) {
      for (Vector2 p : def.waypoints()) {
        waypointQueue.add(new Vector2(p));
      }
    }
    WorldMapNode nextNode = svc.getNode(def.destination());
    if (nextNode != null) {
      waypointQueue.add(getWorldCoords(nextNode));
    }
    if (waypointQueue.isEmpty()) {
      // Nothing to do, but still consume the key and block legacy movement for consistency
      return true;
    }

    waypointIndex = 0;
    pathMoving = true;

    // First tick for immediacy
    tickPathMovement(delta);

    // Clamp post-tick like elsewhere
    Vector2 posAfter = entity.getPosition();
    posAfter.x = MathUtils.clamp(posAfter.x, 0, worldSize.x - 96);
    posAfter.y = MathUtils.clamp(posAfter.y, 0, worldSize.y - 110);
    entity.setPosition(posAfter);

    return true;
  }

  /** If currently following a JSON-defined path, advance and clamp this frame. */
  private boolean advanceJsonPathIfMoving(float delta) {
    if (!pathMoving) return false;
    tickPathMovement(delta);

    // Clamp like legacy (character is 96x110)
    Vector2 pos = entity.getPosition();
    pos.x = MathUtils.clamp(pos.x, 0, worldSize.x - 96);
    pos.y = MathUtils.clamp(pos.y, 0, worldSize.y - 110);
    entity.setPosition(pos);
    return true;
  }

  /** If moving toward a free target position, advance one step this frame. */
  private boolean advanceActiveTarget(float delta) {
    if (!isMoving || targetPosition == null) return false;

    Vector2 pos = entity.getPosition();
    toTarget.set(targetPosition).sub(pos);
    float dist = toTarget.len();

    if (dist <= ARRIVAL_THRESHOLD) {
      pos.set(targetPosition);
      isMoving = false;
      targetPosition = null;
      persistWorldPos();
    } else {
      Vector2 step = toTarget.scl(PLAYER_SPEED * delta / Math.max(dist, 1e-4f));
      pos.add(step);
    }

    pos.x = MathUtils.clamp(pos.x, 0, worldSize.x - 96);
    pos.y = MathUtils.clamp(pos.y, 0, worldSize.y - 110);
    entity.setPosition(pos);
    return true;
  }

  /** Read a single movement key this frame; returns null if none. */
  private String readWASDOnce() {
    SettingsService settingsService = ServiceLocator.getSettingsService();
    if (Gdx.input.isKeyJustPressed(settingsService.getSettings().getUpButton())) return "W";
    if (Gdx.input.isKeyJustPressed(settingsService.getSettings().getLeftButton())) return "A";
    if (Gdx.input.isKeyJustPressed(settingsService.getSettings().getDownButton())) return "S";
    if (Gdx.input.isKeyJustPressed(settingsService.getSettings().getRightButton())) return "D";
    return null;
  }

  /**
   * Fallback: legacy directional navigation. Right → nearest node strictly to the right (levels
   * only) Left → nearest node strictly to the left (levels only) Up → Town if currently at Level 3
   * Down → Level 3 if currently at Town
   */
  private void handleLegacyDirectionalNavigation() {
    Vector2 position = entity.getPosition();
    SettingsService settingsService = ServiceLocator.getSettingsService();

    if (Gdx.input.isKeyJustPressed(settingsService.getSettings().getRightButton())) {
      WorldMapNode right = findDirectionalNeighbor(position, /* toRight= */ true);
      if (right != null) startFreeMoveTo(getWorldCoords(right));
      return;
    }

    if (Gdx.input.isKeyJustPressed(settingsService.getSettings().getLeftButton())) {
      WorldMapNode left = findDirectionalNeighbor(position, /* toRight= */ false);
      if (left != null) startFreeMoveTo(getWorldCoords(left));
      return;
    }

    if (Gdx.input.isKeyJustPressed(settingsService.getSettings().getUpButton())) {
      if (isAtNode(levelThreeNode, position) && townNode != null) {
        startFreeMoveTo(getWorldCoords(townNode));
      }
      return;
    }

    if (Gdx.input.isKeyJustPressed(settingsService.getSettings().getDownButton())
        && isAtNode(townNode, position)
        && levelThreeNode != null) {
      startFreeMoveTo(getWorldCoords(levelThreeNode));
    }
  }

  /** Begin free movement toward a world-space target. */
  private void startFreeMoveTo(Vector2 worldTarget) {
    targetPosition = worldTarget;
    isMoving = true;
    // Cancel JSON-path movement if any
    pathMoving = false;
    waypointIndex = -1;
    waypointQueue.clear();
  }

  // --------------------------------------------------------------------- //
  // Click-to-move along JSON path graph (BFS)
  // --------------------------------------------------------------------- //

  /**
   * Called by click-navigation to move to a target node along the JSON path graph. The waypoints
   * list will be rebuilt and followed next frames.
   */
  public boolean moveToNode(WorldMapNode target) {
    if (target == null) return false;

    WorldMapNode start = getNearestNode(entity.getPosition());
    if (start == null) return false;

    boolean snapToStart = !isOnNode(start, entity.getPosition());
    List<WorldMapService.Path> steps =
        findJsonPath(start.getRegistrationKey(), target.getRegistrationKey());

    if (!snapToStart && (steps == null || steps.isEmpty())) return false;

    waypointQueue.clear();
    if (snapToStart) {
      waypointQueue.add(getWorldCoords(start));
    }
    enqueueSteps(steps);

    if (waypointQueue.isEmpty()) return false;

    waypointIndex = 0;
    pathMoving = true;
    isMoving = false;
    targetPosition = null;
    return true;
  }

  private void enqueueSteps(List<WorldMapService.Path> steps) {
    if (steps == null) return;

    for (WorldMapService.Path pathDef : steps) {
      if (pathDef == null) continue;

      enqueueWaypoints(pathDef);
      enqueueEndNode(ServiceLocator.getWorldMapService(), pathDef);
    }
  }

  private void enqueueWaypoints(WorldMapService.Path pathDef) {
    if (pathDef.waypoints() != null) {
      for (Vector2 waypoint : pathDef.waypoints()) {
        waypointQueue.add(new Vector2(waypoint));
      }
    }
  }

  private void enqueueEndNode(WorldMapService worldMapService, WorldMapService.Path pathDef) {
    WorldMapNode endNode = worldMapService.getNode(pathDef.destination());
    if (endNode != null) {
      waypointQueue.add(getWorldCoords(endNode));
    }
  }

  /** BFS from startKey to targetKey using W/A/S/D edges defined in the JSON. */
  private List<WorldMapService.Path> findJsonPath(String startKey, String targetKey) {
    WorldMapService worldMapService = ServiceLocator.getWorldMapService();
    if (worldMapService == null
        || startKey == null
        || targetKey == null
        || startKey.equals(targetKey)) {
      return java.util.Collections.emptyList();
    }

    return performBreadthFirstSearch(worldMapService, startKey, targetKey);
  }

  private List<WorldMapService.Path> performBreadthFirstSearch(
      WorldMapService worldMapService, String startKey, String targetKey) {
    Deque<String> queue = new ArrayDeque<>();
    Map<String, Prev> previousNodes = new HashMap<>();

    queue.addLast(startKey);
    previousNodes.put(startKey, new Prev(null, null));

    while (!queue.isEmpty() && !previousNodes.containsKey(targetKey)) {
      exploreNeighbors(worldMapService, queue.removeFirst(), queue, previousNodes);
    }

    if (!previousNodes.containsKey(targetKey)) {
      return java.util.Collections.emptyList();
    }

    return reconstructPath(previousNodes, startKey, targetKey);
  }

  private void exploreNeighbors(
      WorldMapService worldMapService,
      String currentNode,
      Deque<String> queue,
      Map<String, Prev> previousNodes) {
    for (Direction direction : DIRS) {
      WorldMapService.Path pathDef = worldMapService.getPath(currentNode, direction);
      if (pathDef == null
          || pathDef.destination() == null
          || previousNodes.containsKey(pathDef.destination())) {
        continue;
      }
      previousNodes.put(pathDef.destination(), new Prev(currentNode, pathDef));
      queue.addLast(pathDef.destination());
    }
  }

  private List<WorldMapService.Path> reconstructPath(
      Map<String, Prev> previousNodes, String startKey, String targetKey) {
    LinkedList<WorldMapService.Path> path = new LinkedList<>();

    String currentNode = targetKey;
    while (!currentNode.equals(startKey)) {
      Prev previousNode = previousNodes.get(currentNode);
      if (previousNode == null) break;
      path.addFirst(previousNode.def);
      currentNode = previousNode.prevKey;
    }

    return path;
  }

  /** Small holder for BFS reconstruction. */
  private static final class Prev {
    final String prevKey;
    final WorldMapService.Path def;

    Prev(String prevKey, WorldMapService.Path def) {
      this.prevKey = prevKey;
      this.def = def;
    }
  }

  // --------------------------------------------------------------------- //
  // Utilities
  // --------------------------------------------------------------------- //

  /** Map WASD string to Direction enum. */
  private Direction mapStringToDirection(String pressed) {
    return switch (pressed) {
      case "W" -> Direction.UP;
      case "A" -> Direction.LEFT;
      case "S" -> Direction.DOWN;
      case "D" -> Direction.RIGHT;
      default -> null;
    };
  }

  /** Resolve special nodes used by legacy W/S transitions. */
  private void resolveSpecialNodes() {
    WorldMapService svc = ServiceLocator.getWorldMapService();
    levelThreeNode = svc.getNode("levelThree");
    // Prefer explicit "skills" key as Town; fall back to highest Y node if needed
    townNode = svc.getNode("skills");
    if (townNode == null) {
      float bestY = Float.NEGATIVE_INFINITY;
      for (WorldMapNode n : svc.getNodesList()) {
        float y = n.getPositionY();
        if (y > bestY) {
          bestY = y;
          townNode = n;
        }
      }
    }
  }

  /**
   * Finds the horizontally nearest neighbour strictly to the left/right (excluding Town). Rewritten
   * to: - keep at most ONE 'continue' in the loop (java:S135) - merge nested conditions
   * (java:S1066) - keep the control flow flat to lower complexity (java:S3776)
   */
  private WorldMapNode findDirectionalNeighbor(Vector2 pos, boolean toRight) {
    WorldMapService svc = ServiceLocator.getWorldMapService();
    float worldX = pos.x;

    WorldMapNode best = null;
    float bestPrimary = Float.MAX_VALUE; // |dx|
    float bestSecondary = Float.MAX_VALUE; // tie-break: squared distance

    for (WorldMapNode n : svc.getNodesList()) {
      // Single guard-continue: skip null/Town or wrong side in ONE place
      float nx = (n == null) ? 0f : n.getPositionX() * worldSize.x;
      float ny = (n == null) ? 0f : n.getPositionY() * worldSize.y;
      float dx = nx - worldX;

      boolean wrongSide = (toRight && dx <= 0f) || (!toRight && dx >= 0f);
      if (n == null || n == townNode || wrongSide) continue;

      float gap = Math.abs(dx); // primary metric: horizontal gap
      float d2 = pos.dst2(nx, ny); // secondary: squared distance

      boolean strictlyBetter = gap < bestPrimary - 1e-3f;
      boolean tieButCloser = Math.abs(gap - bestPrimary) <= 1e-3f && d2 < bestSecondary;
      if (strictlyBetter || tieButCloser) {
        bestPrimary = gap;
        bestSecondary = d2;
        best = n;
      }
    }
    return best;
  }

  /** Convert a node's normalised [0..1] coordinates to world-space pixels. */
  private Vector2 getWorldCoords(WorldMapNode node) {
    return new Vector2(node.getPositionX() * worldSize.x, node.getPositionY() * worldSize.y);
  }

  /** True if the position is within ARRIVAL_THRESHOLD of the node. */
  private boolean isAtNode(WorldMapNode node, Vector2 pos) {
    if (node == null) return false;
    Vector2 np = getWorldCoords(node);
    return pos.dst2(np) <= ARRIVAL_THRESHOLD * ARRIVAL_THRESHOLD;
  }

  /** True if the position is within NODE_SNAP_RADIUS of the node centre. */
  private boolean isOnNode(WorldMapNode node, Vector2 pos) {
    if (node == null) return false;
    Vector2 np = getWorldCoords(node);
    return pos.dst2(np) <= NODE_SNAP_RADIUS * NODE_SNAP_RADIUS;
  }

  /**
   * Finds the nearest world map node to a given position. Flat control flow, no redundant
   * conditions, and no nested 'if' chains.
   */
  private WorldMapNode getNearestNode(Vector2 pos) {
    WorldMapService svc = ServiceLocator.getWorldMapService();
    if (svc == null) return null;

    WorldMapNode bestNode = null;
    float bestDistSq = Float.MAX_VALUE;

    for (WorldMapNode node : svc.getNodesList()) {
      if (node == null) continue; // <= only single continue point

      Vector2 nodeWorldPos = getWorldCoords(node);
      float distSq = pos.dst2(nodeWorldPos);

      if (distSq < bestDistSq) {
        bestDistSq = distSq;
        bestNode = node;
      }
    }
    return bestNode;
  }

  // --------------------------------------------------------------------- //
  // Proximity detection (async, throttled)
  // --------------------------------------------------------------------- //

  private void updateAsyncProximityCheck() {
    timeSinceLastProximityCheck += Gdx.graphics.getDeltaTime();

    boolean readyToLaunch =
        timeSinceLastProximityCheck >= PROXIMITY_CHECK_INTERVAL
            && (proximityCheckFuture == null || proximityCheckFuture.isDone());

    if (readyToLaunch) {
      // Consume previous result if any
      processCompletedFutureIfExists();

      // Launch a new background job with a copy of the position
      Vector2 playerPos = new Vector2(entity.getPosition());
      proximityCheckFuture = JobSystem.launch(() -> checkNodeProximityAsync(playerPos));
      timeSinceLastProximityCheck = 0f;
    }

    // Consume completed result for responsiveness
    processCompletedFutureIfExists();
  }

  private void processCompletedFutureIfExists() {
    if (proximityCheckFuture == null || !proximityCheckFuture.isDone()) return;

    try {
      WorldMapNode result = proximityCheckFuture.get();
      setNearbyNode(result);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      logger.warn("[WorldMapPlayerComponent] Proximity check interrupted: {}", ie.getMessage());
    } catch (Exception e) {
      logger.warn("[WorldMapPlayerComponent] Proximity check failed: {}", e.getMessage());
    } finally {
      proximityCheckFuture = null;
    }
  }

  /** Background computation of the "nearby" node (if any). */
  private WorldMapNode checkNodeProximityAsync(Vector2 playerPos) {
    WorldMapService svc = ServiceLocator.getWorldMapService();
    for (WorldMapNode node : svc.getNodesList()) {
      float nodeX = node.getPositionX() * worldSize.x;
      float nodeY = node.getPositionY() * worldSize.y;

      // Character ~96x110 → measure from centre-ish points to feel natural
      float distance = Vector2.dst(playerPos.x + 48, playerPos.y + 55, nodeX + 40, nodeY + 40);
      if (distance < INTERACTION_DISTANCE) {
        return node; // <= single return/continue style
      }
    }
    return null;
  }

  private void setNearbyNode(WorldMapNode newNearby) {
    if (nearbyNode == newNearby) return;
    nearbyNode = newNearby;
    // Note: updateNodeProximity method removed as it doesn't exist in the new WorldMapService
  }

  // --------------------------------------------------------------------- //
  // Interaction (press E near a node)
  // --------------------------------------------------------------------- //

  private void handleNodeInteraction() {
    if (nearbyNode == null) return;
    SettingsService settingsService = ServiceLocator.getSettingsService();
    if (!Gdx.input.isKeyJustPressed(settingsService.getSettings().getInteractionButton())) return;
    if (nearbyNode.isUnlocked()) {
      String message =
          nearbyNode.isCompleted()
              ? ("You have completed this level.\nDo you want to re-enter "
                  + nearbyNode.getLabel()
                  + "?")
              : ("Do you want to enter " + nearbyNode.getLabel() + "?");
      ServiceLocator.getDialogService()
          .warning(
              nearbyNode.getLabel(),
              message,
              dialog -> {
                logger.info("[WorldMapPlayerComponent] Entering node: {}", nearbyNode.getLabel());
                entity.getEvents().trigger("enterNode", nearbyNode);
              },
              null);
      return;
    }

    String reason = nearbyNode.getLockReason();
    ServiceLocator.getDialogService()
        .error(nearbyNode.getLabel(), reason != null ? reason : "This node is not available.");
    logger.info(
        "[WorldMapPlayerComponent] Node '{}' not accessible: {}", nearbyNode.getLabel(), reason);
  }

  // --------------------------------------------------------------------- //
  // JSON-path movement ticking
  // --------------------------------------------------------------------- //

  private void tickPathMovement(float delta) {
    if (!pathMoving || waypointIndex < 0 || waypointIndex >= waypointQueue.size()) return;

    Vector2 curTarget = waypointQueue.get(waypointIndex);
    tmpPos.set(entity.getPosition());

    toTarget.set(curTarget).sub(tmpPos);
    float dist = toTarget.len();

    if (dist <= ARRIVAL_THRESHOLD) {
      // Reached current waypoint: go to next (or finish)
      waypointIndex++;
      if (waypointIndex >= waypointQueue.size()) {
        entity.setPosition(curTarget);
        float volume = ServiceLocator.getSettingsService().getSoundVolume();
        Sound nodeSound =
            ServiceLocator.getGlobalResourceService().getAsset("sounds/node_sound.mp3", Sound.class);
        nodeSound.play(0.2f * volume);
        persistWorldPos();

        pathMoving = false;
        waypointIndex = -1;
        waypointQueue.clear();
      }
      return;
    }

    // Advance towards the current waypoint
    Vector2 step = toTarget.scl(PLAYER_SPEED * delta / Math.max(dist, 1e-4f));
    tmpPos.add(step);

    // Clamp to world bounds (character 96x110)
    tmpPos.x = MathUtils.clamp(tmpPos.x, 0, worldSize.x - 96);
    tmpPos.y = MathUtils.clamp(tmpPos.y, 0, worldSize.y - 110);
    entity.setPosition(tmpPos);
  }

  // --------------------------------------------------------------------- //
  // Rendering & accessors
  // --------------------------------------------------------------------- //

  /** Draw the player sprite; slightly taller (96x110) for better proportions. */
  @Override
  protected void draw(SpriteBatch batch) {
    if (playerTexture == null) return;
    Vector2 pos = entity.getPosition();
    batch.draw(playerTexture, pos.x + renderOffsetX, pos.y, 96f, 110f);
  }

  /** True if the player is currently moving along a path or a free target. */
  public boolean isCurrentlyMoving() {
    return pathMoving || isMoving;
  }

  public WorldMapNode getNearbyNode() {
    return nearbyNode;
  }

  public Vector2 getWorldSize() {
    return worldSize;
  }

  public float getNodeSnapRadius() {
    return NODE_SNAP_RADIUS;
  }

  /** Set horizontal render offset in pixels (positive → shift right). */
  public void setRenderOffsetX(float offset) {
    this.renderOffsetX = offset;
  }

  @Override
  public void dispose() {
    if (proximityCheckFuture != null && !proximityCheckFuture.isDone()) {
      proximityCheckFuture.cancel(true);
    }
    super.dispose();
  }
}
