package com.csse3200.game.progression;

import com.csse3200.game.components.statistics.Statistics;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.progression.wallet.Wallet;

/**
 * Represents a user profile in the game. Allows customization of player
 * attributes and tracking of progress.
 */
public class Profile {
  private String name;
  private Wallet wallet; // The player's wallet (incl. coins & skill points)
  // private Inventory inventory; // The player's inventory of items (not defences)
  // private Skillset skillset; // The player's skills / skill tree
  // private Achievements achievements; // The player's achievements
  private Statistics statistics; // The player's statistics
  // private Progress progress; // The player's overall progress
  // private Arsenal arsenal; // The player's unlocked defences

  /**
   * Creates a new profile with default values.
   */
  public Profile() {
    this.name = Savefile.createName();
    this.wallet = new Wallet();
    // this.inventory = new Inventory();
    // this.skillset = new Skillset();
    // this.achievements = new Achievements();
    this.statistics = new Statistics();
  }

  /**
   * Initialise a profile with the provided values.
   */
  public Profile(String name, Wallet wallet /*, Inventory inventory, Skillset skillset, Achievements achievements*/, Statistics statistics) {
    this.name = name;
    this.wallet = wallet;
    // this.inventory = inventory;
    // this.skillset = skillset;
    // this.achievements = achievements;
    this.statistics = statistics;
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
  * Get the wallet associated with the profile.
  *
  * @return the wallet of the profile.
  */
  public Wallet wallet() {
    return wallet;
  }

  // /**
  // * Get the inventory associated with the profile.
  // *
  // * @return the inventory of the profile.
  // */
  // public Inventory inventory() {
  // return inventory;
  // }

  // /**
  // * Get the skillset associated with the profile.
  // *
  // * @return the skillset of the profile.
  // */
  // public Skillset skillset() {
  // return skillset;
  // }

  // /**
  // * Get the achievements associated with the profile.
  // *
  // * @return the achievements of the profile.
  // */
  // public Achievements achievements() {
  // return achievements;
  // }

   /**
   * Get the statistics associated with the profile.
   *
   * @return the statistics of the profile.
   */
   public Statistics statistics() {
        return statistics;
   }
}
