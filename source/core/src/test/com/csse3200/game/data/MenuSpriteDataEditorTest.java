package com.csse3200.game.data;

import com.csse3200.game.GdxGame;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.screens.SettingsScreen;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(GameExtension.class)
public class MenuSpriteDataEditorTest {

    @Test
    void testEditor() {
        MenuSpriteData msd = new MenuSpriteData(GdxGame.ScreenType.SETTINGS);
        msd.edit(Mockito.mock(SettingsScreen.class))
                .position(5, 20)
                .name("Name")
                .description("Description")
                .sprite("spritePath")
                .locked(true)
                .apply();

        assertEquals(msd.getX(), 5);
        assertEquals(msd.getY(), 20);
        assertEquals(msd.getName(), "Name");
        assertEquals(msd.getDescription(), "Description");
        assertEquals(msd.getSpriteResourcePath(), "spritePath");
        assertTrue(msd.getLocked());
        assertNull(msd.getId());
    }
}
