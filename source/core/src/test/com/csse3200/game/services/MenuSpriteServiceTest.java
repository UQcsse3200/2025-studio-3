package com.csse3200.game.services;

import static org.junit.Assert.*;

import com.csse3200.game.GdxGame;
import com.csse3200.game.data.MenuSpriteData;
import com.csse3200.game.exceptions.MenuSpriteNotFoundException;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.screens.MainMenuScreen;
import com.csse3200.game.screens.SettingsScreen;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

@ExtendWith(GameExtension.class)
public class MenuSpriteServiceTest {

  @Mock GdxGame gdxGame;

  enum anEnum {
    ITEM1,
    ITEM2
  }

  @Test
  public void menuSpriteFunctions() {
    MenuSpriteService menuSpriteService = new MenuSpriteService();

    MenuSpriteData msd1 = new MenuSpriteData(GdxGame.ScreenType.MAIN_MENU);
    msd1.edit(Mockito.mock(MainMenuScreen.class))
        .name("Name1")
        .description("Description 1")
        .position(10, 10)
        .locked(false)
        .apply();

    msd1.id = anEnum.ITEM1;

    menuSpriteService.register(GdxGame.ScreenType.MAIN_MENU, msd1);

    MenuSpriteData msd2 = new MenuSpriteData(GdxGame.ScreenType.SETTINGS);
    msd2.edit(Mockito.mock(SettingsScreen.class))
        .name("Settings")
        .description("Settings")
        .position(100, 100)
        .locked(false)
        .apply();

    menuSpriteService.register(GdxGame.ScreenType.SETTINGS, msd2);

    List<MenuSpriteData> msdList = new ArrayList<>();
    msdList.add(msd1);
    msdList.add(msd2);

    assertEquals(menuSpriteService.getMenuSprites().size(), 2);
    assertTrue(msdList.containsAll(menuSpriteService.getMenuSprites()));

    // test static sprite get by screen class
    try {
      MenuSpriteData msd = menuSpriteService.getMenuSprite(Mockito.mock(SettingsScreen.class));
      assertEquals(msd, msd2);
    } catch (MenuSpriteNotFoundException e) {
      assertNull(e);
    }
    // test invalid throws error
    assertThrows(
        MenuSpriteNotFoundException.class,
        () -> menuSpriteService.getMenuSprite(Mockito.mock(MainMenuScreen.class)));

    // Test static sprite get by ScreenType
    try {
      MenuSpriteData msd = menuSpriteService.getMenuSprite(GdxGame.ScreenType.SETTINGS);
      assertEquals(msd, msd2);
    } catch (MenuSpriteNotFoundException e) {
      assertNull(e);
    }
    // test invalid throws error
    assertThrows(
        MenuSpriteNotFoundException.class,
        () -> menuSpriteService.getMenuSprite(GdxGame.ScreenType.MAIN_MENU));

    // test dynamic sprite get by screen and enum
    try {
      MenuSpriteData msd =
          menuSpriteService.getMenuSprite(Mockito.mock(MainMenuScreen.class), anEnum.ITEM1);
      assertEquals(msd, msd1);
    } catch (MenuSpriteNotFoundException e) {
      assertNull(e);
    }
    // invalid ID to throw error
    assertThrows(
        MenuSpriteNotFoundException.class,
        () -> menuSpriteService.getMenuSprite(Mockito.mock(MainMenuScreen.class), anEnum.ITEM2));
    assertThrows(
        MenuSpriteNotFoundException.class,
        () -> menuSpriteService.getMenuSprite(Mockito.mock(SettingsScreen.class), anEnum.ITEM1));
    assertThrows(
        MenuSpriteNotFoundException.class,
        () -> menuSpriteService.getMenuSprite(Mockito.mock(SettingsScreen.class), anEnum.ITEM2));

    // test dynamic sprite get by screentype and enum
    try {
      MenuSpriteData msd =
          menuSpriteService.getMenuSprite(GdxGame.ScreenType.MAIN_MENU, anEnum.ITEM1);
      assertEquals(msd, msd1);
    } catch (MenuSpriteNotFoundException e) {
      assertNull(e);
    }
    // invalid ID to throw error
    assertThrows(
        MenuSpriteNotFoundException.class,
        () -> menuSpriteService.getMenuSprite(GdxGame.ScreenType.MAIN_MENU, anEnum.ITEM2));
    assertThrows(
        MenuSpriteNotFoundException.class,
        () -> menuSpriteService.getMenuSprite(GdxGame.ScreenType.SETTINGS, anEnum.ITEM1));
    assertThrows(
        MenuSpriteNotFoundException.class,
        () -> menuSpriteService.getMenuSprite(GdxGame.ScreenType.SETTINGS, anEnum.ITEM2));
  }
}
