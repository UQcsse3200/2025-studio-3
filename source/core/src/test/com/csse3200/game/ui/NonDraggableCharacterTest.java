package com.csse3200.game.ui;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.extensions.UIExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
@ExtendWith(UIExtension.class)
class NonDraggableCharacterTest {

  @Test
  void testSetTexturePath() {
    NonDraggableCharacter character = new NonDraggableCharacter();
    String testPath = "images/entities/character.png";
    character.setTexture(testPath);
    assertEquals(testPath, character.getTexturePath());
  }

  @Test
  void testCreateDraggableCharacter() {
    NonDraggableCharacter character = new NonDraggableCharacter();
    assertNotNull(character);
  }

  @Test
  void testSetPosition() {
    NonDraggableCharacter character = new NonDraggableCharacter();
    float testX = 100f;
    float testY = 150f;
    character.setOffsets(testX, testY);
    assertEquals(testX, character.getOffsetX());
    assertEquals(testY, character.getOffsetY());
  }

  @Test
  void testSetScale() {
    NonDraggableCharacter character = new NonDraggableCharacter();
    float testScale = 0.25f;
    character.setScale(testScale);
    assertEquals(testScale, character.getScale());
  }
}
