package com.csse3200.game.progression.wallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Wallet class enables a player to collect, store and spend coins and skills points which allow
 * purchase of Shop Items and unlocking of Skills to use in-level.
 *
 * <p>A Wallet is created within and assigned to a Profile.
 *
 * <p>A Wallet can be created with preset or variable coins and skillsPoints.
 */
public class Wallet {
  private static final Logger logger = LoggerFactory.getLogger(Wallet.class);
  int coins;
  int skillsPoints;

  /** Default constructor for Wallet. */
  public Wallet() {
    this.coins = 100; // Default starting coins
    this.skillsPoints = 10; // Default starting skillsPoints
  }

  /**
   * Creates a Wallet with the specified initial coin and skill point amounts.
   *
   * @param coins the initial amount of coins
   * @param skillsPoints the initial amount of skill points
   */
  public Wallet(int coins, int skillsPoints) {
    this.coins = coins;
    this.skillsPoints = skillsPoints;
  }

  /**
   * Gets the current amount of coins in the wallet.
   *
   * @return the current amount of coins
   */
  public int getCoins() {
    return coins;
  }

  /**
   * Gets the current amount of skill points in the wallet.
   *
   * @return the current amount of skill points
   */
  public int getSkillsPoints() {
    return skillsPoints;
  }

  /**
   * Sets the current amount of coins in the wallet.
   *
   * @param coins the new amount of coins
   */
  public void setCoins(int coins) {
    if (coins >= 0) {
      this.coins = coins;
    } else {
      logger.error("Cannot set coins to a negative value");
    }
  }

  /**
   * Sets the current amount of skills points in the wallet.
   *
   * @param skillsPoints the new amount of skills points
   */
  public void setSkillsPoints(int skillsPoints) {
    if (skillsPoints >= 0) {
      this.skillsPoints = skillsPoints;
    } else {
      logger.error("Cannot set skills points to a negative value");
    }
  }

  /**
   * Adds coins to the wallet.
   *
   * @param coins the amount of coins to add
   */
  public void addCoins(int coins) {
    this.coins += coins;
  }

  /**
   * Adds skills points to the wallet.
   *
   * @param achievementSkillsPoints the amount of skills points to add
   */
  public void addSkillsPoints(int achievementSkillsPoints) {
    this.skillsPoints += achievementSkillsPoints;
  }

  /**
   * Unlocks a skill by deducting the specified skill cost from the wallet.
   *
   * @param skillCost the cost of the skill to unlock
   */
  public void unlockSkill(int skillCost) {
    // Skills will now handle purchase functionality re checking have enough skillsPoints to unlock
    this.skillsPoints -= skillCost;
  }

  /**
   * Purchases Shop Item where Wallet has sufficient coins, and returns true, otherwise returns
   * false
   *
   * @param itemCost the cost of the item to purchase
   * @return true if the item was purchased successfully, false otherwise
   */
  public boolean purchaseShopItem(int itemCost) {
    if (coins >= itemCost) {
      this.coins -= itemCost;
      logger.info("Item purchased");
      return true;
    } else {
      logger.error("Not enough coins to purchase");
      return false;
    }
  }
}
