package com.csse3200.game.ui.terminal;

import com.badlogic.gdx.Input;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.ui.DraggableCharacter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.csse3200.game.entities.Entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(GameExtension.class)
public class DraggableCharacterTest {

//    @Test
//    void shouldCreateAndDisposeDirectly() {
//        DraggableCharacter character = new DraggableCharacter();
//
//        // Call create and make sure it doesn't crash
//        character.create();
//        assertNotNull(character);
//
//        // Call dispose and verify no exceptions
//        character.dispose();
//    }
//
//    @Test
//    void shouldAttachToEntityAndDispose() {
//        Entity entity = spy(Entity.class);   // use spy so we can verify calls
//        DraggableCharacter character = new DraggableCharacter();
//
//        entity.addComponent(character);
//
//        // When entity is created, character.create() should be called
//        entity.create();
//        verify(entity).create();
//
//        // When entity is disposed, character.dispose() should be called
//        entity.dispose();
//        verify(entity).dispose();
//    }
}
