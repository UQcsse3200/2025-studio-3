package com.csse3200.game.progression;

import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.progression.wallet.Wallet;
import java.util.HashSet;
import java.util.Set;
import net.dermetfan.utils.Pair;

/**
 * Represents a user profile in the game. Allows customization of player attributes and tracking of
 * progress.
 */
public class Profile {
  private String name;
  private Wallet wallet; // The player's wallet (incl. coins & skill points)
  private Inventory inventory; // The player's inventory of items (not defences)
  private SkillSet skillset; // The player's skills / skill tree
  private Statistics statistics; // The player's statistics (includes achievements)
  private Arsenal arsenal; // The player's arsenal of unlocked defences
  private String currentLevel; // The player's current level
  private float worldMapX = -1f; // last saved X on world map; -1 means unset
  private float worldMapY = -1f; // last saved Y on world map; -1 means unset
  private int worldMapZoomIdx = -1; // last saved zoom step index; -1 means unset
  private boolean playedLevelTutorial; // Whether the player has played the level tutorial before
  private boolean playedMapTutorial; // Whether the player has played the map tutorial before
  private Set<String> completedLevels; // List of completed levels
  private Set<String> unlockedNodes; // List of unlocked nodes

  /** Creates a new profile with default values. */
  public Profile() {
    this.name = Savefile.createName();
    this.wallet = new Wallet();
    this.inventory = new Inventory();
    this.skillset = new SkillSet();
    this.statistics = new Statistics();
    this.arsenal = new Arsenal();
    this.worldMapX = -1f;
    this.worldMapY = -1f;
    this.worldMapZoomIdx = -1;
    this.unlockedNodes = new HashSet<>(Set.of("shop", "minigames", "skills"));
    this.completedLevels = new HashSet<>();
    this.currentLevel = "levelOne";
    this.playedLevelTutorial = false;
    this.playedMapTutorial = false;
  }

  /** 
   * Initialise a profile with the provided values. 
   * 
   * Using pairs to avoid a constructor with too many parameters. This is an antipattern and I
   * do not care. Why is this a thing that can't be turned off?!
   * 
   * @param nameAndLevel Pair containing the profile name and current level.
   * @param walletAndInventory Pair containing the wallet and inventory.
   * @param skillsetAndStatistics Pair containing the skillset and statistics.
   * @param nodes Pair containing the unlocked nodes and completed levels.
   * @param tutorialsPlayed Pair containing whether the level and map tutorials have been played.
   * @param worldMapCoords Pair containing the world map X and Y coordinates.
   * @param zoomAndArsenal Pair containing the world map zoom index and arsenal.
   */
  public Profile(
      Pair<String, String> nameAndLevel,
      Pair<Wallet, Inventory> walletAndInventory,
      Pair<SkillSet, Statistics> skillsetAndStatistics,
      Pair<Set<String>, Set<String>> nodes,
      Pair<Boolean, Boolean> tutorialsPlayed,
      Pair<Float, Float> worldMapCoords,
      Pair<Integer, Arsenal> zoomAndArsenal) {
    this.name = nameAndLevel.getKey();
    this.currentLevel = nameAndLevel.getValue();
    this.wallet = walletAndInventory.getKey();
    this.inventory = walletAndInventory.getValue();
    this.skillset = skillsetAndStatistics.getKey();
    this.statistics = skillsetAndStatistics.getValue();
    this.unlockedNodes = nodes.getKey();
    this.completedLevels = nodes.getValue();
    this.playedLevelTutorial = tutorialsPlayed.getKey();
    this.playedMapTutorial = tutorialsPlayed.getValue();
    this.worldMapX = worldMapCoords.getKey();
    this.worldMapY = worldMapCoords.getValue();
    this.worldMapZoomIdx = zoomAndArsenal.getKey();
    this.arsenal = zoomAndArsenal.getValue();
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
   * Gets the saved world map X coordinate.
   * 
   * @return the world map X coordinate.
   */
  public float getWorldMapX() {
    return worldMapX;
  }

  /** 
   * Gets the saved world map Y coordinate (-1 if unset).
   * 
   * @return the world map Y coordinate.
   */
  public float getWorldMapY() {
    return worldMapY;
  }

  /**
   * Sets the saved world map X coordinate (-1 if unset).
   * 
   * @param worldMapX the world map X coordinate.
   */
  public void setWorldMapX(float worldMapX) {
    this.worldMapX = worldMapX;
  }

  /** 
   * Gets the saved world map zoom step index (-1 if unset).
   *
   * @return the world map zoom step index.
   */
  public int getWorldMapZoomIdx() {
    return worldMapZoomIdx;
  }

  /** 
   * Sets the saved world map zoom step index.
   * 
   * @param worldMapZoomIdx the world map zoom step index.
   */
  public void setWorldMapZoomIdx(int worldMapZoomIdx) {
    this.worldMapZoomIdx = worldMapZoomIdx;
  }

  /**
   * Sets the saved world map Y coordinate.
   * 
   * @param worldMapY the world map Y coordinate.
   */
  public void setWorldMapY(float worldMapY) {
    this.worldMapY = worldMapY;
  }

  /**
   * Whether the player has played the level tutorial before.
   * 
   * @return true if the player has played the level tutorial before, false otherwise.
   */
  public boolean getPlayedLevelTutorial() {
    return this.playedLevelTutorial;
  }

  /**
   * Sets a flag to show that the player has played the level tutorial before.
   */
  public void setPlayedLevelTutorial() {
    this.playedLevelTutorial = true;
  }

  /**
   * Whether the player has played the map tutorial before.
   * 
   * @return true if the player has played the map tutorial before, false otherwise.
   */
  public boolean getPlayedMapTutorial() {
    return this.playedMapTutorial;
  }

  /** Sets a flag to show that the player has played the map tutorial before. */
  public void setPlayedMapTutorial() {
    this.playedMapTutorial = true;
  }

  /**
   * Get the set of completed levels.
   *
   * @return the set of completed levels.
   */
  public Set<String> getCompletedLevels() {
    return completedLevels;
  }

  /**
   * Get the set of unlocked nodes.
   *
   * @return the set of unlocked nodes.
   */
  public Set<String> getUnlockedNodes() {
    return unlockedNodes;
  }

  /**
   * Unlock a new node.
   *
   * @param nodeId the ID of the node to unlock.
   */
  public void unlockNode(String nodeId) {
    this.unlockedNodes.add(nodeId);
  }

  /**
   * Mark a level as completed.
   *
   * @param levelId the ID of the level to mark as completed.
   */
  public void completeLevel(String levelId) {
    this.completedLevels.add(levelId);
  }
}
