package com.csse3200.game.progression.arsenal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArsenalTest {

  private Arsenal arsenal;

  @BeforeEach
  void setUp() {
    arsenal = new Arsenal();
  }

  @Test
  void shouldCreateEmptyArsenal() {
    List<String> defences = arsenal.getKeys();
    assertTrue(defences.isEmpty());
  }

  @Test
  void shouldUnlockDefence() {
    arsenal.unlockDefence("turret");

    assertTrue(arsenal.contains("turret"));
    assertEquals(1, arsenal.getKeys().size());
    assertEquals("turret", arsenal.getKeys().get(0));
  }

  @Test
  void shouldUnlockMultipleDefences() {
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("wall");
    arsenal.unlockDefence("cannon");

    assertEquals(3, arsenal.getKeys().size());
    assertTrue(arsenal.contains("turret"));
    assertTrue(arsenal.contains("wall"));
    assertTrue(arsenal.contains("cannon"));
  }

  @Test
  void shouldUnlockSameDefenceMultipleTimes() {
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("turret");

    // Should have 3 entries (List allows duplicates)
    assertEquals(3, arsenal.getKeys().size());
    assertTrue(arsenal.contains("turret"));

    // All entries should be the same defence
    List<String> defences = arsenal.getKeys();
    for (String defence : defences) {
      assertEquals("turret", defence);
    }
  }

  @Test
  void shouldLockDefence() {
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("wall");

    assertTrue(arsenal.contains("turret"));
    assertTrue(arsenal.contains("wall"));
    assertEquals(2, arsenal.getKeys().size());

    arsenal.lockDefence("turret");

    assertFalse(arsenal.contains("turret"));
    assertTrue(arsenal.contains("wall"));
    assertEquals(1, arsenal.getKeys().size());
  }

  @Test
  void shouldLockOnlyFirstOccurrenceOfDefence() {
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("turret");

    assertEquals(3, arsenal.getKeys().size());

    arsenal.lockDefence("turret");

    // Should still contain turret (but only 2 instances)
    assertTrue(arsenal.contains("turret"));
    assertEquals(2, arsenal.getKeys().size());
  }

  @Test
  void shouldHandleLockingNonExistentDefence() {
    arsenal.unlockDefence("turret");

    // Try to lock a defence that doesn't exist
    arsenal.lockDefence("cannon");

    // Original defence should still be there
    assertTrue(arsenal.contains("turret"));
    assertEquals(1, arsenal.getKeys().size());
  }

  @Test
  void shouldHandleLockingFromEmptyArsenal() {
    // Try to lock from empty arsenal
    arsenal.lockDefence("turret");

    // Should remain empty
    assertTrue(arsenal.getKeys().isEmpty());
    assertFalse(arsenal.contains("turret"));
  }

  @Test
  void shouldReturnCorrectContainsResult() {
    assertFalse(arsenal.contains("turret"));
    assertFalse(arsenal.contains("wall"));

    arsenal.unlockDefence("turret");

    assertTrue(arsenal.contains("turret"));
    assertFalse(arsenal.contains("wall"));

    arsenal.unlockDefence("wall");

    assertTrue(arsenal.contains("turret"));
    assertTrue(arsenal.contains("wall"));
  }

  @Test
  void shouldHandleNullDefenceKey() {
    // Test unlocking null
    arsenal.unlockDefence(null);
    assertTrue(arsenal.contains(null));
    assertEquals(1, arsenal.getKeys().size());

    // Test locking null
    arsenal.lockDefence(null);
    assertFalse(arsenal.contains(null));
    assertTrue(arsenal.getKeys().isEmpty());
  }

  @Test
  void shouldHandleEmptyStringDefenceKey() {
    arsenal.unlockDefence("");

    assertTrue(arsenal.contains(""));
    assertEquals(1, arsenal.getKeys().size());
    assertEquals("", arsenal.getKeys().get(0));

    arsenal.lockDefence("");

    assertFalse(arsenal.contains(""));
    assertTrue(arsenal.getKeys().isEmpty());
  }

  @Test
  void shouldPreserveOrderOfUnlockedDefences() {
    arsenal.unlockDefence("first");
    arsenal.unlockDefence("second");
    arsenal.unlockDefence("third");

    List<String> defences = arsenal.getKeys();
    assertEquals("first", defences.get(0));
    assertEquals("second", defences.get(1));
    assertEquals("third", defences.get(2));
  }

  @Test
  void shouldMaintainOrderAfterLocking() {
    arsenal.unlockDefence("first");
    arsenal.unlockDefence("second");
    arsenal.unlockDefence("third");
    arsenal.unlockDefence("fourth");

    // Remove the second element
    arsenal.lockDefence("second");

    List<String> defences = arsenal.getKeys();
    assertEquals(3, defences.size());
    assertEquals("first", defences.get(0));
    assertEquals("third", defences.get(1));
    assertEquals("fourth", defences.get(2));
  }

  @Test
  void shouldHandleCaseSensitiveDefenceKeys() {
    arsenal.unlockDefence("Turret");
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("TURRET");

    assertEquals(3, arsenal.getKeys().size());
    assertTrue(arsenal.contains("Turret"));
    assertTrue(arsenal.contains("turret"));
    assertTrue(arsenal.contains("TURRET"));

    // Should be case sensitive
    assertFalse(arsenal.contains("tUrReT"));
  }

  @Test
  void shouldAllowSpecialCharactersInDefenceKeys() {
    String specialKey = "turret-mk2_v1.0@advanced";
    arsenal.unlockDefence(specialKey);

    assertTrue(arsenal.contains(specialKey));
    assertEquals(1, arsenal.getKeys().size());
    assertEquals(specialKey, arsenal.getKeys().get(0));
  }

  @Test
  void shouldHandleVeryLongDefenceKeys() {
    String longKey = "a".repeat(1000); // 1000 character key
    arsenal.unlockDefence(longKey);

    assertTrue(arsenal.contains(longKey));
    assertEquals(1, arsenal.getKeys().size());
    assertEquals(longKey, arsenal.getKeys().get(0));
  }

  @Test
  void shouldAllowManyDefences() {
    // Add many defences
    for (int i = 0; i < 1000; i++) {
      arsenal.unlockDefence("defence" + i);
    }

    assertEquals(1000, arsenal.getKeys().size());
    assertTrue(arsenal.contains("defence0"));
    assertTrue(arsenal.contains("defence500"));
    assertTrue(arsenal.contains("defence999"));
    assertFalse(arsenal.contains("defence1000"));
  }
}
