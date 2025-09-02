package com.csse3200.game.data;

import com.csse3200.game.GdxGame;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(GameExtension.class)
public class MenuSpriteDataTest {

    @Test
    void testSettersAndGetters() {
        MenuSpriteData msd = new MenuSpriteData(GdxGame.ScreenType.SETTINGS);

        msd.setX(5);
        msd.setY(20);
        msd.setName("Name");
        msd.setDescription("Description");
        msd.setSpriteResourcePath("spritePath");
        msd.setLocked(true);

        assertEquals(msd.getX(), 5);
        assertEquals(msd.getY(), 20);
        assertEquals(msd.getName(), "Name");
        assertEquals(msd.getDescription(), "Description");
        assertEquals(msd.getSpriteResourcePath(), "spritePath");
        assertTrue(msd.getLocked());
        assertNull(msd.getId());
    }

}
