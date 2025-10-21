package com.csse3200.game.services;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.ui.WorldMapNode;
import com.csse3200.game.utils.LevelType;
import java.util.*;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing world map nodes and their navigation paths. Uses hardcoded path data instead
 * of JSON configuration.
 */
public class WorldMapService {
  private static final Logger logger = LoggerFactory.getLogger(WorldMapService.class);
  private static final String LOCK_REASON =
      "You must complete the previous level to unlock this one.";
  private final Map<String, WorldMapNode> nodes;
  private final Map<String, Directions> nodePaths;
  private Entity player;

  /** Enum for the directions on the world map. */
  public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;
  }

  /** Constructor for the world map service. */
  public WorldMapService() {
    this.nodes = new HashMap<>();
    this.nodePaths = new HashMap<>();
    loadNodes();
    loadPaths();
  }

  /**
   * Set the player entity
   *
   * @param player the player entity
   */
  public void setPlayer(Entity player) {
    this.player = player;
  }

  /**
   * Get the player entity
   *
   * @return the player entity
   */
  public Entity getPlayer() {
    return player;
  }

  /** Clear the player entity */
  public void clearPlayer() {
    this.player = null;
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
  public List<WorldMapNode> getNodesList() {
    return new ArrayList<>(nodes.values());
  }

  /**
   * Get all registered nodes as a map
   *
   * @return Map of all nodes
   */
  public Map<String, WorldMapNode> getNodesMap() {
    return nodes;
  }

  /**
   * Get all registered paths
   *
   * @return Map of all paths
   */
  public Map<String, Directions> getPathsMap() {
    return nodePaths;
  }

  /**
   * Registers the nodes on the world map. This is the default configuration for the nodes, and is
   * updated when the profile is loaded.
   */
  private void loadNodes() {
    WorldMapNode shopNode =
        new WorldMapNode(
            LevelType.SHOP.toString(),
            new Pair<>(0.75f, 0.40f),
            false,
            true,
            ScreenType.SHOP,
            "images/nodes/shop.png",
            "");
    shopNode.setRegistrationKey(LevelType.SHOP.toKey());
    nodes.put(LevelType.SHOP.toKey(), shopNode);
    WorldMapNode townNode =
        new WorldMapNode(
            LevelType.TOWN.toString(),
            new Pair<>(0.20f, 0.80f),
            false,
            true,
            ScreenType.SKILLTREE,
            "images/nodes/skills.png",
            "");
    townNode.setRegistrationKey(LevelType.TOWN.toKey());
    nodes.put(LevelType.TOWN.toKey(), townNode);
    WorldMapNode minigamesNode =
        new WorldMapNode(
            LevelType.MINIGAMES.toString(),
            new Pair<>(0.55f, 0.395f),
            false,
            true,
            ScreenType.MINI_GAMES,
            "images/nodes/arcade.png",
            "");
    minigamesNode.setRegistrationKey(LevelType.MINIGAMES.toKey());
    nodes.put(LevelType.MINIGAMES.toKey(), minigamesNode);
    WorldMapNode levelOneNode =
        new WorldMapNode(
            LevelType.LEVEL_ONE.toString(),
            new Pair<>(0.18f, 0.27f),
            false,
            true,
            ScreenType.MAIN_GAME,
            "images/nodes/level1.png",
            LOCK_REASON);
    levelOneNode.setRegistrationKey(LevelType.LEVEL_ONE.toKey());
    nodes.put(LevelType.LEVEL_ONE.toKey(), levelOneNode);
    WorldMapNode levelTwoNode =
        new WorldMapNode(
            LevelType.LEVEL_TWO.toString(),
            new Pair<>(0.32f, 0.24f),
            false,
            false,
            ScreenType.MAIN_GAME,
            "images/nodes/level2.png",
            LOCK_REASON);
    levelTwoNode.setRegistrationKey(LevelType.LEVEL_TWO.toKey());
    nodes.put(LevelType.LEVEL_TWO.toKey(), levelTwoNode);
    WorldMapNode levelThreeNode =
        new WorldMapNode(
            LevelType.LEVEL_THREE.toString(),
            new Pair<>(0.42f, 0.412f),
            false,
            false,
            ScreenType.MAIN_GAME,
            "images/nodes/level3.png",
            LOCK_REASON);
    levelThreeNode.setRegistrationKey(LevelType.LEVEL_THREE.toKey());
    nodes.put(LevelType.LEVEL_THREE.toKey(), levelThreeNode);
    WorldMapNode levelFourNode =
        new WorldMapNode(
            LevelType.LEVEL_FOUR.toString(),
            new Pair<>(0.7f, 0.55f),
            false,
            false,
            ScreenType.MAIN_GAME,
            "images/nodes/level4.png",
            LOCK_REASON);
    levelFourNode.setRegistrationKey(LevelType.LEVEL_FOUR.toKey());
    nodes.put(LevelType.LEVEL_FOUR.toKey(), levelFourNode);
    WorldMapNode levelFiveNode =
        new WorldMapNode(
            LevelType.LEVEL_FIVE.toString(),
            new Pair<>(0.85f, 0.78f),
            false,
            false,
            ScreenType.MAIN_GAME,
            "images/nodes/level5.png",
            LOCK_REASON);
    levelFiveNode.setRegistrationKey(LevelType.LEVEL_FIVE.toKey());
    nodes.put(LevelType.LEVEL_FIVE.toKey(), levelFiveNode);
  }

  /** Initializes the paths for the world map. */
  private void loadPaths() {
    nodePaths.put(
        LevelType.LEVEL_ONE.toKey(),
        new Directions(
            null,
            null,
            null,
            new Path(
                LevelType.LEVEL_TWO.toKey(),
                Arrays.asList(
                    new Vector2(593, 597), new Vector2(726, 507), new Vector2(960, 480)))));
    nodePaths.put(
        LevelType.LEVEL_TWO.toKey(),
        new Directions(
            null,
            null,
            new Path(
                LevelType.LEVEL_ONE.toKey(),
                Arrays.asList(new Vector2(726, 507), new Vector2(593, 597), new Vector2(540, 540))),
            new Path(
                LevelType.LEVEL_THREE.toKey(),
                Arrays.asList(
                    new Vector2(1096, 593),
                    new Vector2(1096, 693),
                    new Vector2(1207, 693),
                    new Vector2(1260, 824)))));
    nodePaths.put(
        LevelType.LEVEL_THREE.toKey(),
        new Directions(
            new Path(
                LevelType.TOWN.toKey(),
                Arrays.asList(
                    new Vector2(1246, 824),
                    new Vector2(1250, 977),
                    new Vector2(1200, 977),
                    new Vector2(600, 1600))),
            null,
            new Path(
                LevelType.LEVEL_TWO.toKey(),
                Arrays.asList(
                    new Vector2(1207, 693),
                    new Vector2(1096, 693),
                    new Vector2(1096, 593),
                    new Vector2(960, 480))),
            new Path(
                LevelType.MINIGAMES.toKey(),
                Arrays.asList(new Vector2(1425, 687), new Vector2(1650, 790)))));
    nodePaths.put(
        LevelType.LEVEL_FOUR.toKey(),
        new Directions(
            new Path(
                LevelType.LEVEL_FIVE.toKey(),
                Arrays.asList(new Vector2(2342, 1347), new Vector2(2550, 1560))),
            new Path(
                LevelType.SHOP.toKey(),
                Arrays.asList(
                    new Vector2(2092, 1024),
                    new Vector2(2135, 997),
                    new Vector2(2225, 967),
                    new Vector2(2220, 800))),
            new Path(
                LevelType.MINIGAMES.toKey(),
                Arrays.asList(
                    new Vector2(2012, 1133),
                    new Vector2(2012, 1050),
                    new Vector2(1909, 1000),
                    new Vector2(1650, 790))),
            null));
    nodePaths.put(
        LevelType.LEVEL_FIVE.toKey(),
        new Directions(
            null,
            new Path(
                LevelType.LEVEL_FOUR.toKey(),
                Arrays.asList(new Vector2(2342, 1347), new Vector2(2100, 1110))),
            null,
            null));
    nodePaths.put(
        LevelType.MINIGAMES.toKey(),
        new Directions(
            null,
            new Path(
                LevelType.SHOP.toKey(),
                Arrays.asList(
                    new Vector2(1650, 706),
                    new Vector2(1800, 620),
                    new Vector2(1855, 660),
                    new Vector2(2135, 577),
                    new Vector2(2294, 727),
                    new Vector2(2220, 800))),
            new Path(
                LevelType.LEVEL_THREE.toKey(),
                Arrays.asList(new Vector2(1425, 687), new Vector2(1350, 800))),
            new Path(
                LevelType.LEVEL_FOUR.toKey(),
                Arrays.asList(
                    new Vector2(1909, 1000),
                    new Vector2(2012, 1050),
                    new Vector2(2012, 1133),
                    new Vector2(2100, 1100)))));
    nodePaths.put(
        LevelType.SHOP.toKey(),
        new Directions(
            new Path(
                LevelType.LEVEL_FOUR.toKey(),
                Arrays.asList(
                    new Vector2(2225, 967),
                    new Vector2(2135, 997),
                    new Vector2(2092, 1024),
                    new Vector2(2092, 1110),
                    new Vector2(2100, 1100))),
            null,
            new Path(
                LevelType.MINIGAMES.toKey(),
                Arrays.asList(
                    new Vector2(2294, 727),
                    new Vector2(2135, 577),
                    new Vector2(1855, 660),
                    new Vector2(1800, 620),
                    new Vector2(1650, 706),
                    new Vector2(1650, 790))),
            null));
    nodePaths.put(
        LevelType.TOWN.toKey(),
        new Directions(
            null,
            new Path(
                LevelType.LEVEL_THREE.toKey(),
                Arrays.asList(
                    new Vector2(1200, 977), new Vector2(1250, 977), new Vector2(1260, 824))),
            null,
            null));
  }

  /** Apply the profile state to the world map nodes. */
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

  /** Clear all registered nodes */
  public void clearNodes() {
    nodes.clear();
    nodePaths.clear();
    logger.debug("[WorldMapService] Cleared all world map nodes and paths");
  }

  /**
   * Find a node at a given position with tolerance for click detection
   *
   * @param x the x position in world coordinates
   * @param y the y position in world coordinates
   * @return the node, or null if not found
   */
  public WorldMapNode findNodeAt(float x, float y) {
    // World size constants - should match WorldMapScreen
    final float WORLD_WIDTH = 3000f;
    final float WORLD_HEIGHT = 2000f;
    final float HIT_RADIUS = 120f;

    return nodes.values().stream()
        .filter(
            node -> {
              // Calculate node center position in world coordinates
              float nodeX = node.getPositionX() * WORLD_WIDTH;
              float nodeY = node.getPositionY() * WORLD_HEIGHT;

              // Calculate distance from click point to node center
              float dx = x - nodeX;
              float dy = y - nodeY;
              float distanceSquared = dx * dx + dy * dy;

              // Check if click is within hit radius
              return distanceSquared <= (HIT_RADIUS * HIT_RADIUS);
            })
        .findFirst()
        .orElse(null);
  }

  /**
   * Gets a path definition for a specific node and direction.
   *
   * @param currentNodeKey the current node's key
   * @param direction the direction
   * @return the path definition, or null if not found
   */
  public Path getPath(String currentNodeKey, Direction direction) {
    Directions node = nodePaths.get(currentNodeKey);
    if (node == null) return null;

    return switch (direction) {
      case UP -> node.up();
      case DOWN -> node.down();
      case LEFT -> node.left();
      case RIGHT -> node.right();
      default -> null;
    };
  }

  /**
   * Record type for a path.
   *
   * @param destination the destination node
   * @param waypoints the waypoints of the path
   */
  public record Path(String destination, List<Vector2> waypoints) {}

  /**
   * Record type for directions.
   *
   * @param up the path up
   * @param down the path down
   * @param left the path left
   * @param right the path right
   */
  public record Directions(Path up, Path down, Path left, Path right) {}
}
