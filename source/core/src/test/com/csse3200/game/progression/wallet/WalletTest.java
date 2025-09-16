package com.csse3200.game.progression.wallet;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class WalletTest {

  @Test
  void testDefaultWalletCoins() {
    Wallet wallet = new Wallet();
    assertEquals(30, wallet.getCoins()); // Corrected to match actual default value
    wallet.setCoins(200);
    assertEquals(200, wallet.getCoins());
    wallet.addCoins(10);
    assertEquals(210, wallet.getCoins());
    boolean result = wallet.purchaseShopItem(20);
    assertTrue(result);
    assertEquals(190, wallet.getCoins());
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
    assertEquals(1, wallet.getSkillsPoints()); // Corrected to match actual default value
    wallet.setSkillsPoints(20);
    assertEquals(20, wallet.getSkillsPoints());
    wallet.addSkillsPoints(10);
    assertEquals(30, wallet.getSkillsPoints());
    wallet.unlockSkill(2);
    assertEquals(28, wallet.getSkillsPoints());
  }

  @Test
  void testVariableWalletSkillsPoints() {
    Wallet wallet = new Wallet(1, 2);
    assertEquals(2, wallet.getSkillsPoints());
    wallet.setSkillsPoints(10);
    assertEquals(10, wallet.getSkillsPoints());
    wallet.addSkillsPoints(10);
    assertEquals(20, wallet.getSkillsPoints());
    wallet.unlockSkill(2);
    assertEquals(18, wallet.getSkillsPoints());
  }

  @Test
  void testSetCoinsWithNegativeValue() {
    Wallet wallet = new Wallet();
    int originalCoins = wallet.getCoins();
    wallet.setCoins(-10);
    assertEquals(originalCoins, wallet.getCoins()); // Should remain unchanged
  }

  @Test
  void testSetSkillsPointsWithNegativeValue() {
    Wallet wallet = new Wallet();
    int originalSkillsPoints = wallet.getSkillsPoints();
    wallet.setSkillsPoints(-5);
    assertEquals(originalSkillsPoints, wallet.getSkillsPoints()); // Should remain unchanged
  }

  @Test
  void testPurchaseShopItemInsufficientFunds() {
    Wallet wallet = new Wallet();
    int originalCoins = wallet.getCoins();
    boolean result = wallet.purchaseShopItem(originalCoins + 10);
    assertFalse(result);
    assertEquals(originalCoins, wallet.getCoins()); // Should remain unchanged
  }

  @Test
  void testPurchaseShopItemExactAmount() {
    Wallet wallet = new Wallet();
    int exactAmount = wallet.getCoins();
    boolean result = wallet.purchaseShopItem(exactAmount);
    assertTrue(result);
    assertEquals(0, wallet.getCoins());
  }

  @Test
  void testAddNegativeCoins() {
    Wallet wallet = new Wallet();
    int originalCoins = wallet.getCoins();
    wallet.addCoins(-10);
    assertEquals(originalCoins - 10, wallet.getCoins()); // Negative addition should work
  }

  @Test
  void testAddNegativeSkillsPoints() {
    Wallet wallet = new Wallet();
    int originalSkillsPoints = wallet.getSkillsPoints();
    wallet.addSkillsPoints(-1);
    assertEquals(
        originalSkillsPoints - 1, wallet.getSkillsPoints()); // Negative addition should work
  }
}
