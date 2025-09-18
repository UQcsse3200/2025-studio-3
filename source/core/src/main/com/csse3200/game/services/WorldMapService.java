package com.csse3200.game.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csse3200.game.ui.WorldMapNode;

/**
 * Service for managing world map nodes dynamically. Allows registration of screens/levels without
 * requiring a static JSON file.
 */
public class WorldMapService {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapService.class);
  private final Map<String, WorldMapNode> nodes;

  /** Constructor for the world map service. */
  public WorldMapService() {
    this.nodes = new HashMap<>();
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

  /** Clear all registered nodes */
  public void clearNodes() {
    nodes.clear();
    logger.debug("[WorldMapService] Cleared all world map nodes");
  }
}
