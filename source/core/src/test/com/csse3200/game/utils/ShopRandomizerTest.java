package com.csse3200.game.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** Test class for ShopRandomizer utility class. */
class ShopRandomizerTest {

  @Test
  void testGetShopItemIndexes_ReturnsCorrectArraySize() {
    int[] result = ShopRandomizer.getShopItemIndexes("testSeed", 0, 10, LocalDateTime.now());
    assertEquals(3, result.length, "Should return exactly 3 indexes");
  }

  @Test
  void testGetShopItemIndexes_IndexesWithinRange() {
    int min = 5;
    int max = 15;
    int[] result = ShopRandomizer.getShopItemIndexes("testSeed", min, max, LocalDateTime.now());

    for (int index : result) {
      assertTrue(
          index >= min && index <= max,
          "Index " + index + " should be between " + min + " and " + max);
    }
  }

  @Test
  void testGetShopItemIndexes_SameSeedSameTimeReturnsSameResult() {
    String seed = "consistentSeed";
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);

    int[] result1 = ShopRandomizer.getShopItemIndexes(seed, 0, 10, dateTime);
    int[] result2 = ShopRandomizer.getShopItemIndexes(seed, 0, 10, dateTime);

    assertArrayEquals(result1, result2, "Same seed and time should return same indexes");
  }

  @Test
  void testGetShopItemIndexes_DifferentSeedsReturnDifferentResults() {
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);

    int[] result1 = ShopRandomizer.getShopItemIndexes("seed1", 0, 10, dateTime);
    int[] result2 = ShopRandomizer.getShopItemIndexes("seed2", 0, 10, dateTime);

    assertFalse(
        java.util.Arrays.equals(result1, result2),
        "Different seeds should return different indexes");
  }

  @Test
  void testGetShopItemIndexes_DifferentHoursReturnDifferentResults() {
    String seed = "sameSeed";
    LocalDateTime time1 = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
    LocalDateTime time2 = LocalDateTime.of(2024, 1, 15, 11, 30, 45);

    int[] result1 = ShopRandomizer.getShopItemIndexes(seed, 0, 10, time1);
    int[] result2 = ShopRandomizer.getShopItemIndexes(seed, 0, 10, time2);

    assertFalse(
        java.util.Arrays.equals(result1, result2),
        "Different hours should return different indexes");
  }

  @Test
  void testGetShopItemIndexes_Same15MinuteIntervalReturnsSameResult() {
    String seed = "sameSeed";
    // Both times are in the 10:30-10:44 interval (round to 10:30)
    LocalDateTime time1 = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
    LocalDateTime time2 = LocalDateTime.of(2024, 1, 15, 10, 44, 20);

    int[] result1 = ShopRandomizer.getShopItemIndexes(seed, 0, 10, time1);
    int[] result2 = ShopRandomizer.getShopItemIndexes(seed, 0, 10, time2);

    assertArrayEquals(
        result1, result2, "Times within same 15-minute interval should return same indexes");
  }

  @Test
  void testGetShopItemIndexes_EdgeCaseMinEqualsMax() {
    int[] result = ShopRandomizer.getShopItemIndexes("testSeed", 5, 5, LocalDateTime.now());

    assertEquals(3, result.length);
    for (int index : result) {
      assertEquals(5, index, "When min equals max, all indexes should be that value");
    }
  }

  @Test
  void testGetShopItemIndexes_ZeroRange() {
    int[] result = ShopRandomizer.getShopItemIndexes("testSeed", 0, 0, LocalDateTime.now());

    assertEquals(3, result.length);
    for (int index : result) {
      assertEquals(0, index, "With range 0-0, all indexes should be 0");
    }
  }

  @Test
  void testGetShopItemIndexes_LargeRange() {
    int min = 0;
    int max = 1000;
    int[] result = ShopRandomizer.getShopItemIndexes("testSeed", min, max, LocalDateTime.now());

    assertEquals(3, result.length);
    for (int index : result) {
      assertTrue(index >= min && index <= max, "Index should be within large range");
    }
  }

  @Test
  void testGetShopItemIndexes_EmptySeed() {
    int[] result = ShopRandomizer.getShopItemIndexes("", 0, 10, LocalDateTime.now());

    assertEquals(3, result.length);
    for (int index : result) {
      assertTrue(index >= 0 && index <= 10, "Index should be within range");
    }
  }

  @Test
  void testGetShopItemIndexes_NullSeed() {
    int[] result = ShopRandomizer.getShopItemIndexes(null, 0, 10, LocalDateTime.now());

    assertEquals(3, result.length);
    for (int index : result) {
      assertTrue(index >= 0 && index <= 10, "Index should be within range");
    }
  }

  @Test
  void testGetShopItemIndexes_ConsistencyAcrossMultipleCalls() {
    String seed = "consistencyTest";
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
    int min = 0;
    int max = 20;

    int[] firstResult = ShopRandomizer.getShopItemIndexes(seed, min, max, dateTime);

    for (int i = 0; i < 10; i++) {
      int[] result = ShopRandomizer.getShopItemIndexes(seed, min, max, dateTime);
      assertArrayEquals(
          firstResult, result, "Call " + i + " should return same result as first call");
    }
  }

  @Test
  void testGetShopItemIndexes_TimeZoneHandling() {
    String seed = "timezoneTest";
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0, 0);

    int[] result = ShopRandomizer.getShopItemIndexes(seed, 0, 10, dateTime);

    assertEquals(3, result.length);
    for (int index : result) {
      assertTrue(index >= 0 && index <= 10, "Index should be within range");
    }
  }

  @Test
  void testGetShopItemIndexes_DeterministicBehavior() {
    String seed = "deterministicTest";
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
    int min = 0;
    int max = 5;

    int[] result1 = ShopRandomizer.getShopItemIndexes(seed, min, max, dateTime);
    int[] result2 = ShopRandomizer.getShopItemIndexes(seed, min, max, dateTime);
    int[] result3 = ShopRandomizer.getShopItemIndexes(seed, min, max, dateTime);

    assertArrayEquals(result1, result2, "Results should be identical");
    assertArrayEquals(result2, result3, "Results should be identical");
  }
}
