package com.csse3200.game.components.wallet;

public class Wallet {
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

    //Skill will now handle purchase functionality re checking have enough skillPoints to unlock
    public void unlockSkill(int skillCost) {
        this.skillsPoints -= skillCost;
    }

    public void purchaseShopItem(int item) {//will be ShopItem item
        if (coins >= item) {//will be something like item.getCost()
            this.coins -= item;//will be something like item.getCost()
        } else {
            //throw Exception;
        }
    }
}
