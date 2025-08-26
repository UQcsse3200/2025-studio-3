package com.csse3200.game.components.wallet;

public class Wallet {
    int coins;
    int skillsPoints;

    public Wallet() {
        this.coins = 100; //temp
        this.skillsPoints = 0;
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

    public void addSkillsPoints(int achievement) { //will be Achievement achievement
        this.skillsPoints += achievement; //will be something like achievement.getPoints()
    }

    public void unlockSkill(int skill) {//will be Skill skill
        if (skillsPoints >= skill) {//will be something like skill.getCost()
            this.skillsPoints -= skill; //will be something like skill.getCost()
        } else {
            //throw Exception;
        }
    }

    public void purchaseShopItem(int item) {//will be ShopItem item
        if (coins >= item) {//will be something like item.getCost()
            this.coins -= item;//will be something like item.getCost()
        } else {
            //throw Exception;
        }
    }
}
