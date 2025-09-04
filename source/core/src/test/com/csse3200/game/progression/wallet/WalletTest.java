package com.csse3200.game.progression.wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.csse3200.game.extensions.GameExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(GameExtension.class)
class WalletTest {

    @Test
    void testDefaultWalletCoins() {
        Wallet wallet = new Wallet();
        int coins = 100;
        assertEquals(coins, wallet.getCoins());
        wallet.setCoins(200);
        coins = 200;
        assertEquals(coins, wallet.getCoins());
        wallet.addCoins(10);
        coins = 210;
        assertEquals(coins, wallet.getCoins());
        wallet.purchaseShopItem(20);
        coins = 190;
        assertEquals(coins, wallet.getCoins());
    }

    @Test
    void testVariableWalletCoins() {
        Wallet wallet = new Wallet(1, 2);
        int coins = 1;
        assertEquals(coins, wallet.getCoins());
        wallet.setCoins(200);
        coins = 200;
        assertEquals(coins, wallet.getCoins());
        wallet.addCoins(10);
        coins = 210;
        assertEquals(coins, wallet.getCoins());
        wallet.purchaseShopItem(20);
        coins = 190;
        assertEquals(coins, wallet.getCoins());
    }

    @Test
    void testDefaultWalletSkillsPoints() {
        Wallet wallet = new Wallet();
        int skillsPoints = 10;
        assertEquals(skillsPoints, wallet.getSkillsPoints());
        wallet.setSkillsPoints(20);
        skillsPoints = 20;
        assertEquals(skillsPoints, wallet.getSkillsPoints());
        wallet.addSkillsPoints(10);
        skillsPoints = 30;
        assertEquals(skillsPoints, wallet.getSkillsPoints());
        wallet.unlockSkill(2);
        skillsPoints = 28;
        assertEquals(skillsPoints, wallet.getSkillsPoints());
    }

    @Test
    void testVariableWalletSkillsPoints() {
        Wallet wallet = new Wallet(1, 2);
        int skillsPoints = 2;
        assertEquals(skillsPoints, wallet.getSkillsPoints());
        wallet.setSkillsPoints(10);
        skillsPoints = 10;
        assertEquals(skillsPoints, wallet.getSkillsPoints());
        wallet.addSkillsPoints(10);
        skillsPoints = 20;
        assertEquals(skillsPoints, wallet.getSkillsPoints());
        wallet.unlockSkill(2);
        skillsPoints = 18;
        assertEquals(skillsPoints, wallet.getSkillsPoints());
    }
}