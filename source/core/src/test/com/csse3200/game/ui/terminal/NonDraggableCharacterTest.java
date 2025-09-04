package com.csse3200.game.ui.terminal;

import com.badlogic.gdx.Input;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.ui.DraggableCharacter;
import com.csse3200.game.ui.NonDraggableCharacter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.csse3200.game.entities.Entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(GameExtension.class)
public class NonDraggableCharacterTest {

    @Test
    void testSetTexturePath() {
        NonDraggableCharacter character = new NonDraggableCharacter();
        String testPath = "images/box_boy_leaf.png";
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
