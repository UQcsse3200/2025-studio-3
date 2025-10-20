package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.components.worldmap.WorldMapNodeRenderComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.ui.WorldMapNode;
import net.dermetfan.utils.Pair;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing world map nodes dynamically. Allows registration of screens/levels without
 * requiring a static JSON file.
 */
public class WorldMapService {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapService.class);
  private static final String LOCK_REASON =
    "You must complete the previous level to unlock this one.";
  private final Map<String, WorldMapNode> nodes;

  /** Constructor for the world map service. */
  public WorldMapService() {
    this.nodes = new HashMap<>();
    loadNodes();
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

  /** 
   * Registers the nodes on the world map. This is the default configuration for the nodes, 
   * and is updated when the profile is loaded.
   */
  private void loadNodes() {
    registerNode(
        new WorldMapNode(
            "Shop",
            new Pair<>(0.75f, 0.40f),
            false,
            true,
            ScreenType.SHOP,
            "images/nodes/shop.png",
            ""),
        "shop");
    registerNode(
        new WorldMapNode(
            "Town",
            new Pair<>(0.20f, 0.80f),
            false,
            true,
            ScreenType.SKILLTREE,
            "images/nodes/skills.png",
            ""),
        "skills");
    registerNode(
        new WorldMapNode(
            "Arcade",
            new Pair<>(0.55f, 0.395f),
            false,
            true,
            ScreenType.MINI_GAMES,
            "images/nodes/arcade.png",
            ""),
        "minigames");
    registerNode(
        new WorldMapNode(
            "Level 1",
            new Pair<>(0.18f, 0.27f),
            false,
            false,
            ScreenType.MAIN_GAME,
            "images/nodes/level1.png",
            LOCK_REASON),
        "levelOne");
    registerNode(
        new WorldMapNode(
            "Level 2",
            new Pair<>(0.32f, 0.24f),
            false,
            false,
            ScreenType.MAIN_GAME,
            "images/nodes/level2.png",
            LOCK_REASON),
        "levelTwo");
    registerNode(
        new WorldMapNode(
            "Level 3",
            new Pair<>(0.42f, 0.412f),
            false,
            false,
            ScreenType.MAIN_GAME,
            "images/nodes/level3.png",
            LOCK_REASON),
        "levelThree");
    registerNode(
        new WorldMapNode(
            "Level 4",
            new Pair<>(0.7f, 0.55f),
            false,
            false,
            ScreenType.MAIN_GAME,
            "images/nodes/level4.png",
            LOCK_REASON),
        "levelFour");
    registerNode(
        new WorldMapNode(
            "Level 5",
            new Pair<>(0.85f, 0.78f),
            false,
            false,
            ScreenType.MAIN_GAME,
            "images/nodes/level5.png",
            LOCK_REASON),
        "levelFive");
  }

  /**
   * Apply the profile state to the world map nodes.
   */
  public void applyState() {
    Profile profile = ServiceLocator.getProfileService().getProfile();
    Set<String> unlocked = profile.getUnlockedNodes();
    Set<String> completedLevels = profile.getCompletedLevels();

    // Unlock default nodes
    for (String key : unlocked) {
      WorldMapNode node = nodes.get(key);
      if (node != null) {
        node.setUnlocked(true);
        node.setLockReason(null);
      }
    }

    // Mark completed levels
    for (String levelKey : completedLevels) {
      WorldMapNode node = nodes.get(levelKey);
      if (node != null) {
        node.setCompleted(true);
      }
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

  /** Clear all registered nodes */
  public void clearNodes() {
    nodes.clear();
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
