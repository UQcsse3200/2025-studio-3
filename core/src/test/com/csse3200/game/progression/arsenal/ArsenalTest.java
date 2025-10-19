package com.csse3200.game.progression.arsenal;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class ArsenalTest {
  private Arsenal arsenal;
  private final Integer numInitialDefences = 2;
  private final Integer numInitialGenerators = 1;

  @BeforeEach
  void setUp() {
    arsenal = new Arsenal();
  }

  @Test
  void shouldCreate() {
    List<String> defences = arsenal.getDefenders();
    List<String> generators = arsenal.getGenerators();
    assertEquals(numInitialDefences, defences.size());
    assertEquals(numInitialGenerators, generators.size());
    assertTrue(defences.contains("slingshooter"));
    assertTrue(defences.contains("shield"));
    assertTrue(generators.contains("furnace"));
  }

  @Test
  void shouldUnlockDefence() {
    arsenal.unlockDefence("turret");
    assertTrue(arsenal.contains("turret"));
    assertEquals(numInitialDefences + 1, arsenal.getDefenders().size());
  }

  @Test
  void shouldUnlockMultipleDefences() {
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("wall");
    arsenal.unlockDefence("cannon");

    assertEquals(numInitialDefences + 3, arsenal.getDefenders().size());
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
    assertEquals(numInitialDefences + 3, arsenal.getDefenders().size());
    assertTrue(arsenal.contains("turret"));
  }

  @Test
  void shouldLockDefence() {
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("wall");

    assertTrue(arsenal.contains("turret"));
    assertTrue(arsenal.contains("wall"));
    assertEquals(numInitialDefences + 2, arsenal.getDefenders().size());

    arsenal.lockDefence("turret");

    assertFalse(arsenal.contains("turret"));
    assertTrue(arsenal.contains("wall"));
    assertEquals(numInitialDefences + 1, arsenal.getDefenders().size());
  }

  @Test
  void shouldLockOnlyFirstOccurrenceOfDefence() {
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("turret");

    assertEquals(numInitialDefences + 3, arsenal.getDefenders().size());

    arsenal.lockDefence("turret");

    // Should still contain turret (but only 2 instances)
    assertTrue(arsenal.contains("turret"));
    assertEquals(numInitialDefences + 2, arsenal.getDefenders().size());
  }

  @Test
  void shouldHandleLockingNonExistentDefence() {
    arsenal.unlockDefence("turret");

    // Try to lock a defence that doesn't exist
    arsenal.lockDefence("cannon");

    // Original defence should still be there
    assertTrue(arsenal.contains("turret"));
    assertEquals(numInitialDefences + 1, arsenal.getDefenders().size());
  }

  @Test
  void shouldHandleLockingFromEmptyArsenal() {
    // Try to lock from empty arsenal
    arsenal.lockDefence("turret");

    // Should remain empty
    assertEquals(numInitialDefences, arsenal.getDefenders().size());
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
    assertEquals(numInitialDefences + 1, arsenal.getDefenders().size());

    // Test locking null
    arsenal.lockDefence(null);
    assertFalse(arsenal.contains(null));
    assertEquals(numInitialDefences, arsenal.getDefenders().size());
  }

  @Test
  void shouldHandleEmptyStringDefenceKey() {
    arsenal.unlockDefence("");

    assertTrue(arsenal.contains(""));
    assertEquals(numInitialDefences + 1, arsenal.getDefenders().size());

    arsenal.lockDefence("");
    assertFalse(arsenal.contains(""));
    assertEquals(numInitialDefences, arsenal.getDefenders().size());
  }

  @Test
  void shouldHandleCaseSensitiveDefenceKeys() {
    arsenal.unlockDefence("Turret");
    arsenal.unlockDefence("turret");
    arsenal.unlockDefence("TURRET");

    assertEquals(numInitialDefences + 3, arsenal.getDefenders().size());
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
    assertEquals(numInitialDefences + 1, arsenal.getDefenders().size());
  }

  @Test
  void shouldHandleVeryLongDefenceKeys() {
    String longKey = "a".repeat(1000); // 1000 character key
    arsenal.unlockDefence(longKey);

    assertTrue(arsenal.contains(longKey));
    assertEquals(numInitialDefences + 1, arsenal.getDefenders().size());
  }

  @Test
  void shouldAllowManyDefences() {
    // Add many defences
    for (int i = 0; i < 1000; i++) {
      arsenal.unlockDefence("defence" + i);
    }

    assertEquals(numInitialDefences + 1000, arsenal.getDefenders().size());
    assertTrue(arsenal.contains("defence0"));
    assertTrue(arsenal.contains("defence500"));
    assertTrue(arsenal.contains("defence999"));
    assertFalse(arsenal.contains("defence1000"));
  }
}
