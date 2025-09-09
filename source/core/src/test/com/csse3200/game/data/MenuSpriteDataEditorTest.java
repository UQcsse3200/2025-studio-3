package com.csse3200.game.data;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.GdxGame;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.screens.SettingsScreen;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(GameExtension.class)
class MenuSpriteDataEditorTest {

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

    assertEquals(5, msd.getX());
    assertEquals(20, msd.getY());
    assertEquals("Name", msd.getName());
    assertEquals("Description", msd.getDescription());
    assertEquals("spritePath", msd.getSpriteResourcePath());
    assertTrue(msd.getLocked());
    assertNull(msd.getId());
  }
}
