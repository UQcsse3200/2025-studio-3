package com.csse3200.game.progression;

import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.progression.wallet.Wallet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.dermetfan.utils.Pair;

/**
 * Represents a user profile in the game. Allows customization of player attributes and tracking of
 * progress.
 */
public class Profile {

  public static final java.util.Set<String> DEFAULT_UNLOCKED =
      java.util.Set.of("levelOne", "shop", "minigames", "skills");
  private java.util.Set<String> unlockedNodes;
  private String name;
  private Wallet wallet; // The player's wallet (incl. coins & skill points)
  private Inventory inventory; // The player's inventory of items (not defences)
  private SkillSet skillset; // The player's skills / skill tree
  private Statistics statistics; // The player's statistics (includes achievements)
  private Arsenal arsenal; // The player's arsenal of unlocked defences
  private String currentLevel; // The player's current level
  private List<String> completedNodes; // List of completed nodes/levels
  private float worldMapX = -1f; // last saved X on world map; -1 means unset
  private float worldMapY = -1f; // last saved Y on world map; -1 means unset
  private int worldMapZoomIdx = -1; // last saved zoom step index; -1 means unset

  /** Creates a new profile with default values. */
  public Profile() {
    this.name = Savefile.createName();
    this.wallet = new Wallet();
    this.inventory = new Inventory();
    this.skillset = new SkillSet();
    this.statistics = new Statistics();
    this.arsenal = new Arsenal();
    this.completedNodes = new ArrayList<>();
    this.unlockedNodes = new HashSet<>();
    this.unlockedNodes.add("levelOne");
    this.worldMapX = -1f;
    this.worldMapY = -1f;
    this.worldMapZoomIdx = -1;
    this.unlockedNodes = new java.util.HashSet<>(DEFAULT_UNLOCKED); // include defaults
  }

  /** Initialise a profile with the provided values. */
  public Profile(
      Pair<String, String> nameAndLevel,
      Wallet wallet,
      Inventory inventory,
      SkillSet skillset,
      Statistics statistics,
      Arsenal arsenal,
      List<String> completedNodes) {
    this.name = nameAndLevel.getKey();
    this.currentLevel = nameAndLevel.getValue();
    this.wallet = wallet;
    this.inventory = inventory;
    this.skillset = skillset;
    this.statistics = statistics != null ? statistics : new Statistics();
    this.arsenal = arsenal;
    this.completedNodes = completedNodes != null ? completedNodes : new ArrayList<>();
    this.unlockedNodes = new java.util.HashSet<>(DEFAULT_UNLOCKED);
    if (this.currentLevel != null) {
      this.unlockedNodes.add(this.currentLevel);
    }
  }

  /**
   * Get the name of the profile.
   *
   * @return the name of the profile.
   */
  public String getName() {
    return name;
  }

  /**
   * Change the name of the profile.
   *
   * @param name the new name of the profile.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the current level of the profile.
   *
   * @return the current level of the profile.
   */
  public String getCurrentLevel() {
    return currentLevel;
  }

  /**
   * Set the current level of the profile.
   *
   * @param currentLevel the new current level of the profile.
   */
  public void setCurrentLevel(String currentLevel) {
    this.currentLevel = currentLevel;
  }

  /**
   * Get the wallet associated with the profile.
   *
   * @return the wallet of the profile.
   */
  public Wallet getWallet() {
    return wallet;
  }

  /**
   * Get the inventory associated with the profile.
   *
   * @return the inventory of the profile.
   */
  public Inventory getInventory() {
    return inventory;
  }

  /**
   * Get the arsenal associated with the profile.
   *
   * @return the arsenal of the profile.
   */
  public Arsenal getArsenal() {
    return arsenal;
  }

  /**
   * Get the skillset associated with the profile.
   *
   * @return the skillset of the profile.
   */
  public SkillSet getSkillset() {
    return skillset;
  }

  /**
   * Get the statistics associated with the profile.
   *
   * @return the statistics of the profile.
   */
  public Statistics getStatistics() {
    return statistics;
  }

  /**
   * Get the completed nodes associated with the profile.
   *
   * @return the completed nodes of the profile.
   */
  public List<String> getCompletedNodes() {
    return completedNodes;
  }

  /**
   * Set the completed nodes associated with the profile.
   *
   * @param completedNodes the new completed nodes of the profile.
   */
  public void setCompletedNodes(List<String> completedNodes) {
    this.completedNodes = completedNodes;
  }

  /**
   * Add a completed node to the profile.
   *
   * @param node the node to add to the profile.
   */
  public void addCompletedNode(String node) {
    this.completedNodes.add(node);
  }

  public float getWorldMapX() {
    return worldMapX;
  }

  public float getWorldMapY() {
    return worldMapY;
  }

  public void setWorldMapX(float worldMapX) {
    this.worldMapX = worldMapX;
  }

  /** Gets the saved world map zoom step index (-1 if unset). */
  public int getWorldMapZoomIdx() {
    return worldMapZoomIdx;
  }

  /** Sets the saved world map zoom step index. */
  public void setWorldMapZoomIdx(int worldMapZoomIdx) {
    this.worldMapZoomIdx = worldMapZoomIdx;
  }

  public void setWorldMapY(float worldMapY) {
    this.worldMapY = worldMapY;
  }

  /** Marks a node as completed and keeps it unlocked for replay. */
  public void completeNode(String key) {
    if (key == null || key.isEmpty()) return;
    if (!completedNodes.contains(key)) {
      completedNodes.add(key);
    }
    unlockedNodes.add(key);
  }

  /**
   * Unlocks a node so the player can access it, even if not completed yet.
   *
   * @param key the node identifier
   */
  public void unlockNode(String key) {
    if (key == null || key.isEmpty()) return;
    unlockedNodes.add(key);
  }

  /** Returns true if this node has been completed. */
  public boolean isNodeCompleted(String key) {
    return completedNodes.contains(key);
  }

  /** Returns true if this node is unlocked and can be entered. */
  public boolean isNodeUnlocked(String key) {
    return unlockedNodes.contains(key);
  }

  public Set<String> getUnlockedNodes() {
    return unlockedNodes;
  }
}
