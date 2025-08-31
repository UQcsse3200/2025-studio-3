package com.csse3200.game.components.wallet;

import com.csse3200.game.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The Wallet class enables a player to collect, store and spend coins and skills points which allow purchase
 * of Shop Items and unlocking of Skills to use in-level.
 *
 * A Wallet is created within and assigned to a Profile.
 *
 * A Wallet can be created with preset or variable coins and skillsPoints. *
 */
public class Wallet {
    private static final Logger logger = LoggerFactory.getLogger(Wallet.class);
    int coins;
    int skillsPoints;

    public Wallet() {
        this.coins = 100; //temp
        this.skillsPoints = 0;
    }

    public Wallet(int coins, int skillsPoints) {
        this.coins = coins;
        this.skillsPoints = skillsPoints;
    }

    public int getCoins() {
        return coins;
    }

    public int getSkillsPoints() {
        return skillsPoints;
    }

    public void setCoins(int coins) {
        if (coins >= 0) {
            this.coins = coins;
        }
    }

    public void setSkillsPoints(int skillsPoints){
        if (skillsPoints >= 0) {
            this.skillsPoints = skillsPoints;
        }
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void addSkillsPoints(int achievementSkillPoints) {
        this.skillsPoints += achievementSkillPoints;
    }

    // Skill will now handle purchase functionality re checking have enough skillPoints to unlock
    public void unlockSkill(int skillCost) {
        this.skillsPoints -= skillCost;
    }

    // Purchases Shop Item where Wallet has sufficient coins, and returns true, otherwise returns false
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
