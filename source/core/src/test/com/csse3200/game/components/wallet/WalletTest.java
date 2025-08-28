package com.csse3200.game.components.wallet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WalletTest {

    @Test
    void testWalletCoins() {
        Wallet wallet = new Wallet();
        int coins = 100;
        assertEquals(coins, wallet.getCoins());
        wallet.setCoins(200);
        coins = 200;
        assertEquals(coins, wallet.getCoins());
        wallet.addCoins(10);
        coins = 210;
        assertEquals(coins, wallet.getCoins());
        wallet.purchaseShopItem(20);//will change to Item input
        coins = 190;//will change to Item get price function
        assertEquals(coins, wallet.getCoins());
    }

    @Test
    void testWallet2Coins() {
        Wallet wallet = new Wallet(1, 2);
        int coins = 1;
        assertEquals(coins, wallet.getCoins());
        wallet.setCoins(200);
        coins = 200;
        assertEquals(coins, wallet.getCoins());
        wallet.addCoins(10);
        coins = 210;
        assertEquals(coins, wallet.getCoins());
        wallet.purchaseShopItem(20);//will change to Item input
        coins = 190;//will change to Item get price function
        assertEquals(coins, wallet.getCoins());
    }

    @Test
    void testWalletSkillsPoints() {
        Wallet wallet = new Wallet();
        int skillPoints = 0;
        assertEquals(skillPoints, wallet.getSkillsPoints());
        wallet.setSkillsPoints(10);
        skillPoints = 10;
        assertEquals(skillPoints, wallet.getSkillsPoints());
        wallet.addSkillsPoints(10);
        skillPoints = 20;
        assertEquals(skillPoints, wallet.getSkillsPoints());
        wallet.unlockSkill(2);//will change to Skills input
        skillPoints = 18;//will change to Skill get cost function
        assertEquals(skillPoints, wallet.getSkillsPoints());
    }

    @Test
    void testWalletSkills2Points() {
        Wallet wallet = new Wallet(1, 2);
        int skillPoints = 2;
        assertEquals(skillPoints, wallet.getSkillsPoints());
        wallet.setSkillsPoints(10);
        skillPoints = 10;
        assertEquals(skillPoints, wallet.getSkillsPoints());
        wallet.addSkillsPoints(10);
        skillPoints = 20;
        assertEquals(skillPoints, wallet.getSkillsPoints());
        wallet.unlockSkill(2);//will change to Skills input
        skillPoints = 18;//will change to Skill get cost function
        assertEquals(skillPoints, wallet.getSkillsPoints());
    }
}