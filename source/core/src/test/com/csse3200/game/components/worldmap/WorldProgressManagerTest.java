package com.csse3200.game.components.worldmap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for world map progression focusing on markLevelcomplete().
 *
 * <p>NOTE: - Replace TODOs to match your concrete classes: - WorldMapProgress (service/controller
 * that holds save + level states) - LevelState enum (LOCKED/UNLOCKED/COMPLETED) - Method/field
 * names if different in your codebase.
 */
@ExtendWith(GameExtension.class)
class WorldMapProgressTest {

  private WorldMapProgress progress;
  private SaveGateway save;

  @BeforeEach
  void setUp() {
    GameTime time = mock(GameTime.class);
    ServiceLocator.registerTimeSource(time);
    save = new InMemorySave();
    progress =
        new WorldMapProgress(
            save, new String[] {"levelOne", "levelTwo", "levelThree", "levelFour", "levelFive"});
    save.writeCurrentLevel("levelThree");
    progress.rebuildStatesFromCurrentLevel();
  }

  @Test
  void shouldInitialiseStatesFromCurrentLevel_ruleOnlyCurrentLevel() {
    // Verify initial states based on current level "levelThree"
    assertEquals(LevelState.COMPLETED, progress.getState("levelOne"));
    assertEquals(LevelState.COMPLETED, progress.getState("levelTwo"));
    assertEquals(LevelState.UNLOCKED, progress.getState("levelThree"));
    assertEquals(LevelState.LOCKED, progress.getState("levelFour"));
    assertEquals(LevelState.LOCKED, progress.getState("levelFive"));
  }

  @Test
  void shouldMarkCurrentAsCompletedAndUnlockNext_onMarkLevelcomplete() throws Exception {
    // Use real ProfileService + Profile to verify behavior
    com.csse3200.game.services.ProfileService svc = new com.csse3200.game.services.ProfileService();
    com.csse3200.game.progression.Profile profile = new com.csse3200.game.progression.Profile();

    // Arrange: current level is levelThree
    profile.setCurrentLevel("levelThree");

    // Inject the profile into the service and activate it (avoid touching Persistence)
    java.lang.reflect.Field fProfile =
        com.csse3200.game.services.ProfileService.class.getDeclaredField("profile");
    fProfile.setAccessible(true);
    fProfile.set(svc, profile);
    java.lang.reflect.Field fActive =
        com.csse3200.game.services.ProfileService.class.getDeclaredField("isActive");
    fActive.setAccessible(true);
    fActive.setBoolean(svc, true);

    // Act
    svc.markLevelComplete("levelThree", "levelFour");

    // Assert
    assertTrue(
        profile.getCompletedNodes().contains("levelThree"),
        "Current level should be recorded as COMPLETED");
    assertTrue(profile.getUnlockedNodes().contains("levelFour"), "Next level should be UNLOCKED");
    assertEquals(
        "levelFour", profile.getCurrentLevel(), "currentLevel should advance to the next level");
  }

  @Test
  void shouldBeIdempotent_whenMarkingAlreadyCompleted() {
    // Mark a level as completed twice and verify no state change on second call
    progress.markLevelcomplete("levelThree");
    LevelSnapshot s1 = progress.snapshot();
    boolean changed = progress.markLevelcomplete("levelThree");
    LevelSnapshot s2 = progress.snapshot();
    assertFalse(changed, "Repeated marking should not cause changes");
    assertEquals(s1, s2, "Repeated calls should be idempotent");
  }

  @Test
  void shouldNotAdvancePastLastLevel() {
    // Set current level to last one and mark it completed, verify no advancement beyond last level
    save.writeCurrentLevel("levelFive");
    progress.rebuildStatesFromCurrentLevel();

    boolean changed = progress.markLevelcomplete("levelFive");
    assertTrue(changed, "Completing last level should cause a state change (mark as COMPLETED)");

    assertEquals(LevelState.COMPLETED, progress.getState("levelFive"));
    assertEquals("levelFive", save.readCurrentLevel(), "Should not advance beyond the last level");
  }

  @Test
  void shouldAllowReplayCompletedLevels() {
    // Completed levels should remain unlocked and be playable again
    progress.markLevelcomplete("levelThree");

    assertNotEquals(LevelState.LOCKED, progress.getState("levelThree"));

    // If there is a canEnter API, completed levels should be enterable
    assertTrue(progress.canEnter("levelThree"), "Completed levels should be re-enterable");
  }

  // ======= Simple in-memory save and snapshot helpers (replace with your implementations if
  // needed) =======

  private static class InMemorySave implements SaveGateway {
    private String current = "levelOne";

    @Override
    public String readCurrentLevel() {
      return current;
    }

    @Override
    public void writeCurrentLevel(String lv) {
      current = lv;
    }
  }

  interface SaveGateway {
    String readCurrentLevel();

    void writeCurrentLevel(String levelKey);
  }

  enum LevelState {
    LOCKED,
    UNLOCKED,
    COMPLETED
  }

  static final class LevelSnapshot {
    final java.util.Map<String, LevelState> states;

    LevelSnapshot(java.util.Map<String, LevelState> states) {
      this.states = new java.util.HashMap<>(states);
    }

    @Override
    public boolean equals(Object o) {
      return (o instanceof LevelSnapshot s) && s.states.equals(states);
    }

    @Override
    public int hashCode() {
      return states.hashCode();
    }
  }

  /**
   * Hypothetical world map progress class; replace with your real implementation if available.
   * Logic strictly follows the rule of using only currentLevel to determine states.
   */
  static class WorldMapProgress {
    private final SaveGateway save;
    private final String[] order; // Level order
    private final java.util.Map<String, LevelState> states = new java.util.HashMap<>();

    WorldMapProgress(SaveGateway save, String[] order) {
      this.save = save;
      this.order = order;
    }

    void rebuildStatesFromCurrentLevel() {
      String cur = save.readCurrentLevel();
      int idx = indexOf(cur);
      for (int i = 0; i < order.length; i++) {
        if (i < idx) states.put(order[i], LevelState.COMPLETED);
        else if (i == idx) states.put(order[i], LevelState.UNLOCKED);
        else states.put(order[i], LevelState.LOCKED);
      }
    }

    boolean markLevelcomplete(String levelKey) {
      int idx = indexOf(levelKey);
      if (idx < 0) return false;

      boolean changed = false;

      // Mark current level as completed
      if (states.get(levelKey) != LevelState.COMPLETED) {
        states.put(levelKey, LevelState.COMPLETED);
        changed = true;
      }

      // Unlock next level and advance currentLevel if possible
      if (idx + 1 < order.length) {
        String next = order[idx + 1];
        if (states.get(next) == LevelState.LOCKED) {
          states.put(next, LevelState.UNLOCKED);
          changed = true;
        }
        // Save currentLevel as next level if changed
        if (!next.equals(save.readCurrentLevel())) {
          save.writeCurrentLevel(next);
          changed = true;
        }
      }
      return changed;
    }

    boolean canEnter(String levelKey) {
      LevelState s = states.get(levelKey);
      return s == LevelState.UNLOCKED || s == LevelState.COMPLETED;
    }

    LevelState getState(String levelKey) {
      return states.get(levelKey);
    }

    LevelSnapshot snapshot() {
      return new LevelSnapshot(states);
    }

    private int indexOf(String key) {
      for (int i = 0; i < order.length; i++) if (order[i].equals(key)) return i;
      return -1;
    }
  }
}
