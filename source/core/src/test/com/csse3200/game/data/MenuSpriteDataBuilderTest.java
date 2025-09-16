package com.csse3200.game.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.screens.SettingsScreen;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(GameExtension.class)
class MenuSpriteDataBuilderTest {

  enum TestEnum {
    ITEM1
  }

  @Test
  void testBuilder() {
    MenuSpriteData msd =
        MenuSpriteData.builder(Mockito.mock(SettingsScreen.class), TestEnum.ITEM1)
            .position(5, 20)
            .name("Name")
            .description("Description")
            .sprite("spritePath")
            .locked(true)
            .build();

    assertEquals(5, msd.getX());
    assertEquals(20, msd.getY());
    assertEquals("Name", msd.getName());
    assertEquals("Description", msd.getDescription());
    assertEquals("spritePath", msd.getSpriteResourcePath());
    assertTrue(msd.getLocked());
    assertEquals(TestEnum.ITEM1, msd.getId());
    assertEquals(TestEnum.class, msd.getId().getClass());
  }
}
