package com.csse3200.game.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.screens.SettingsScreen;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(GameExtension.class)
public class MenuSpriteDataBuilderTest {

  enum anEnum {
    ITEM1
  }

  @Test
  void testBuilder() {
    MenuSpriteData msd =
        MenuSpriteData.builder(Mockito.mock(SettingsScreen.class), anEnum.ITEM1)
            .position(5, 20)
            .name("Name")
            .description("Description")
            .sprite("spritePath")
            .locked(true)
            .build();

    assertEquals(msd.getX(), 5);
    assertEquals(msd.getY(), 20);
    assertEquals(msd.getName(), "Name");
    assertEquals(msd.getDescription(), "Description");
    assertEquals(msd.getSpriteResourcePath(), "spritePath");
    assertTrue(msd.getLocked());
    assertEquals(msd.getId(), anEnum.ITEM1);
    assertEquals(msd.getId().getClass(), anEnum.class);
  }
}
