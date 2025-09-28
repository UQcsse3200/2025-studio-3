package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.csse3200.game.components.worldmap.WorldMapNodeRenderComponent;
import com.csse3200.game.ui.WorldMapNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  /**
   * Mark a node as completed
   *
   * @param key The key of the completed node
   */
  public void completeNode(String key) {
    WorldMapNode node = nodes.get(key);
    if (node == null) {
      logger.warn("[WorldMapService] Attempted to complete unknown node: {}", key);
      return;
    }
    node.setCompleted(true);
    logger.info("[WorldMapService] Completed node: {}", key);
  }

  /**
   * Unlock a specific node
   *
   * @param key The key of the node to unlock
   */
  public void unlockNode(String key) {
    WorldMapNode node = nodes.get(key);
    if (node == null) {
      logger.warn("[WorldMapService] Attempted to unlock unknown node: {}", key);
      return;
    }
    node.setUnlocked(true);
    logger.info("[WorldMapService] Unlocked node: {}", key);
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
    public String next;
    public List<Vector2> waypoints;
  }

  public void loadPathConfig(String internalPath) {
    var file = Gdx.files.internal(internalPath);
    if (!file.exists()) {
      logger.warn("[WorldMapService] path config not found: {}", internalPath);
      return;
    }
    pathMap.clear();
    JsonReader reader = new JsonReader();
    JsonValue root = reader.parse(file);

    for (JsonValue nodeEntry = root.child(); nodeEntry != null; nodeEntry = nodeEntry.next()) {
      String curNodeKey = nodeEntry.name();
      Map<String, PathDef> keyMap = new HashMap<>();
      for (JsonValue keyEntry = nodeEntry.child(); keyEntry != null; keyEntry = keyEntry.next()) {
        String keyName = keyEntry.name();
        PathDef def = new PathDef();
        def.next = keyEntry.getString("next");
        def.waypoints = new ArrayList<>();
        JsonValue pathArr = keyEntry.get("path");
        if (pathArr != null) {
          for (JsonValue p = pathArr.child(); p != null; p = p.next()) {
            float wx = p.get(0).asFloat();
            float wy = p.get(1).asFloat();
            def.waypoints.add(new Vector2(wx, wy));
          }
        }
        keyMap.put(keyName, def);
      }
      pathMap.put(curNodeKey, keyMap);
    }
    logger.info("[WorldMapService] Loaded path config for {} nodes", pathMap.size());
  }

  public PathDef getPath(String currentNodeKey, String keyName) {
    Map<String, PathDef> m = pathMap.get(currentNodeKey);
    return (m == null) ? null : m.get(keyName);
  }
}
