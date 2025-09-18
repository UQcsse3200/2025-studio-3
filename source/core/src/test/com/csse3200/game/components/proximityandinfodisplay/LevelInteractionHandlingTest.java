package com.csse3200.game.components.proximityandinfodisplay;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.components.proximityinfodisplay.LevelInteractionHandler;
import com.csse3200.game.data.MenuSpriteData;
import org.junit.jupiter.api.Test;

public class LevelInteractionHandlingTest {

  @Test
  public void testLockedLevel() {
    // Arrange
    MenuSpriteData spriteData =
        new MenuSpriteData(
            null, null, 0, 0, "Locked Level", "This level is locked", "locked.png", true // locked
            );

    // Act
    LevelInteractionHandler.handleInteraction(spriteData);

    // Assert → still locked
    assertTrue(spriteData.getLocked());
  }

  @Test
  public void testUnlockedLevel() {
    // Arrange
    MenuSpriteData spriteData =
        new MenuSpriteData(
            null,
            null,
            0,
            0,
            "Unlocked Level",
            "This level is open",
            "unlocked.png",
            false // unlocked
            );

    // Act
    LevelInteractionHandler.handleInteraction(spriteData);

    // Assert → still unlocked
    assertFalse(spriteData.getLocked());
  }
}
