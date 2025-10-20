package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.csse3200.game.components.worldmap.WorldMapNodeRenderComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.ui.WorldMapNode;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing world map nodes dynamically. Allows registration of screens/levels without
 * requiring a static JSON file.
 */
public class WorldMapService {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapService.class);
  private final Map<String, WorldMapNode> nodes;
  private final List<WorldMapNodeRenderComponent> nodeRenderComponents;

  private Entity playerEntity;

  public void registerPlayer(Entity player) {
    this.playerEntity = player;
  }

  public Entity getPlayerEntity() {
    return this.playerEntity;
  }

  /** Constructor for the world map service. */
  public WorldMapService() {
    this.nodes = new HashMap<>();
    this.nodeRenderComponents = new ArrayList<>();
  }

  /**
   * Register a new node on the world map
   *
   * @param node The node to register
   * @param key The key to register the node with
   */
  public void registerNode(WorldMapNode node, String key) {
    node.setRegistrationKey(key);
    nodes.put(key, node);
    logger.debug("[WorldMapService] Registered world map node: {}", key);
  }

  /**
   * Retrieve a map node
   *
   * @param key The key to get the node with
   * @return The node, or null if not found
   */
  public WorldMapNode getNode(String key) {
    return nodes.get(key);
  }

  /**
   * Get all registered nodes
   *
   * @return List of all nodes
   */
  public List<WorldMapNode> getAllNodes() {
    return new ArrayList<>(nodes.values());
  }

  // Put these helpers near WorldMapService (or the class where the method lives)
  private static final String LOCK_MSG = "Locked until you reach this node.";
  private static final Map<String, Integer> LEVEL_INDEX =
      Map.of("levelOne", 1, "levelTwo", 2, "levelThree", 3, "levelFour", 4, "levelFive", 5);

  private enum State {
    UNLOCKED_DONE,
    UNLOCKED_TODO,
    LOCKED
  }

  /** Returns true for "level*" keys. */
  private static boolean isLevelKey(String key) {
    return key != null && key.startsWith("level");
  }

  /** Returns level index; unknown/non-level = MAX_VALUE. */
  private static int levelIndexOf(String key) {
    if (key == null) return Integer.MAX_VALUE;
    Integer idx = LEVEL_INDEX.get(key.trim());
    return idx != null ? idx : Integer.MAX_VALUE;
  }

  /** Decide node state with simple, flat rules. */
  private static State decideState(
      String key, Set<String> defaultUnlocked, int currentIdx, boolean finished) {

    // Special nodes (shop/minigames/skills) are always unlocked but not completed
    if (defaultUnlocked != null && defaultUnlocked.contains(key)) {
      return State.UNLOCKED_TODO;
    }

    // Non-level nodes (that are not special) are locked
    if (!isLevelKey(key)) {
      return State.LOCKED;
    }

    // Level nodes
    int idx = levelIndexOf(key);
    if (finished || idx < currentIdx) return State.UNLOCKED_DONE;
    if (idx == currentIdx) return State.UNLOCKED_TODO;
    return State.LOCKED;
  }

  /** Apply the decided state to a node. */
  private static void apply(WorldMapNode node, State s) {
    switch (s) {
      case UNLOCKED_DONE:
        node.setUnlocked(true);
        node.setCompleted(true);
        node.setLockReason(null);
        break;
      case UNLOCKED_TODO:
        node.setUnlocked(true);
        node.setCompleted(false);
        node.setLockReason(null);
        break;
      default: // LOCKED
        node.setUnlocked(false);
        node.setCompleted(false);
        node.setLockReason(LOCK_MSG);
    }
  }

  public void applyStatesFrom(Profile profile, Set<String> defaultUnlocked) {
    if (profile == null) return;

    String cur = profile.getCurrentLevel();
    boolean finished = "end".equals(cur);
    int currentIdx = finished ? Integer.MAX_VALUE - 1 : levelIndexOf(cur);

    for (WorldMapNode node : getAllNodes()) {
      String key = node.getRegistrationKey();
      State s = decideState(key, defaultUnlocked, currentIdx, finished);
      apply(node, s);
    }
  }

  /**
   * Lock a specific node
   *
   * @param key The key of the node to lock
   * @param lockReason The reason the node is locked
   */
  public void lockNode(String key, String lockReason) {
    WorldMapNode node = nodes.get(key);
    if (node == null) {
      logger.warn("[WorldMapService] Attempted to lock unknown node: {}", key);
      return;
    }
    node.setUnlocked(false);
    node.setLockReason(lockReason);
    logger.info("[WorldMapService] Locked node: {}", key);
  }

  /**
   * Register a node render component for proximity updates
   *
   * @param renderComponent the render component to register
   */
  public void registerNodeRenderComponent(WorldMapNodeRenderComponent renderComponent) {
    nodeRenderComponents.add(renderComponent);
  }

  /**
   * Update all node render components with the current nearby node
   *
   * @param nearbyNode the node that the player is currently near, or null if none
   */
  public void updateNodeProximity(WorldMapNode nearbyNode) {
    for (WorldMapNodeRenderComponent renderComponent : nodeRenderComponents) {
      renderComponent.updateProximityState(nearbyNode);
    }
  }

  /** Clear all registered nodes */
  public void clearNodes() {
    nodes.clear();
    nodeRenderComponents.clear();
    logger.debug("[WorldMapService] Cleared all world map nodes");
  }

  // ====== JSON path support ======
  private Map<String, Map<String, PathDef>> pathMap = new HashMap<>();

  public static class PathDef {
    private String next;
    private List<Vector2> waypoints;

    public String getNext() {
      return next;
    }

    public void setNext(String next) {
      this.next = next;
    }

    public List<Vector2> getWaypoints() {
      return waypoints;
    }

    public void setWaypoints(List<Vector2> waypoints) {
      this.waypoints = waypoints;
    }
  }

  public void loadPathConfig(String internalPath) {
    var file = Gdx.files.internal(internalPath);
    if (!file.exists()) {
      logger.warn("[WorldMapService] path config not found: {}", internalPath);
      return; // guard: no file
    }

    pathMap.clear();

    JsonValue root = new JsonReader().parse(file);
    JsonValue directions = (root != null) ? root.get("directions") : null;
    if (directions == null) {
      logger.warn("[WorldMapService] 'directions' node missing in {}", internalPath);
      return; // guard: no directions
    }

    // iterate top-level nodes; keep body tiny, push logic to helpers
    for (JsonValue node = directions.child(); node != null; node = node.next()) {
      parseNode(node);
    }

    logger.info("[WorldMapService] Loaded path config for {} nodes", pathMap.size());
  }

  /** Parse one node (e.g., "Town", "Level1", ...) and put into pathMap if non-empty. */
  private void parseNode(JsonValue nodeEntry) {
    String curNodeKey = (nodeEntry != null) ? nodeEntry.name() : null;
    if (curNodeKey == null || curNodeKey.isEmpty()) {
      return; // guard
    }

    Map<String, PathDef> keyMap = new HashMap<>();

    for (JsonValue keyEntry = nodeEntry.child(); keyEntry != null; keyEntry = keyEntry.next()) {
      String keyName = keyEntry.name();
      if (keyName == null || keyName.isEmpty()) {
        continue;
      }

      PathDef def = buildPathDef(keyEntry);
      if (def != null) {
        keyMap.put(keyName, def);
      }
    }

    if (!keyMap.isEmpty()) {
      pathMap.put(curNodeKey, keyMap);
    }
  }

  /** Build a PathDef from a direction entry; return null if it is effectively empty. */
  private PathDef buildPathDef(JsonValue keyEntry) {
    PathDef def = new PathDef();
    def.next = optString(keyEntry, "next");
    def.waypoints = readWaypoints(keyEntry != null ? keyEntry.get("path") : null);

    // Only keep entries that have "next" or at least one waypoint (same as original logic)
    boolean hasNext = (def.next != null && !def.next.isEmpty());
    boolean hasPath = (def.waypoints != null && !def.waypoints.isEmpty());
    return (hasNext || hasPath) ? def : null;
  }

  /** Read a list of waypoints [[x,y], ...]; tolerate malformed items gracefully. */
  private List<Vector2> readWaypoints(JsonValue pathArr) {
    if (pathArr == null) {
      return new ArrayList<>();
    }
    List<Vector2> list = new ArrayList<>();
    for (JsonValue p = pathArr.child(); p != null; p = p.next()) {
      // Each waypoint expected to be a 2-element array
      JsonValue xv = p.get(0);
      JsonValue yv = p.get(1);
      if (xv != null && yv != null) {
        list.add(new Vector2(xv.asFloat(), yv.asFloat()));
      }
    }
    return list;
  }

  /** Null-safe string getter from a JsonValue child. */
  private String optString(JsonValue parent, String name) {
    if (parent == null || name == null) return null;
    JsonValue v = parent.get(name);
    return (v != null) ? v.asString() : null;
  }

  public PathDef getPath(String currentNodeKey, String keyName) {
    Map<String, PathDef> m = pathMap.get(currentNodeKey);
    return (m == null) ? null : m.get(keyName);
  }

  public WorldMapNode findNodeAt(float x, float y) {
    for (WorldMapNodeRenderComponent c : nodeRenderComponents) {
      if (c.hit(x, y)) {
        return c.getNode();
      }
    }
    return null;
  }
}
