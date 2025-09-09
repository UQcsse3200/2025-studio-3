package com.csse3200.game.data;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.GdxGame;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class MenuSpriteDataTest {

  @Test
  void testSettersAndGetters() {
    MenuSpriteData msd = new MenuSpriteData(GdxGame.ScreenType.SETTINGS);

    msd.setX(5);
    msd.setY(20);
    msd.setName("Name");
    msd.setDescription("Description");
    msd.setSpriteResourcePath("spritePath");
    msd.setLocked(true);

    assertEquals(5, msd.getX());
    assertEquals(20, msd.getY());
    assertEquals("Name", msd.getName());
    assertEquals("Description", msd.getDescription());
    assertEquals("spritePath", msd.getSpriteResourcePath());
    assertTrue(msd.getLocked());
    assertNull(msd.getId());
  }
}
