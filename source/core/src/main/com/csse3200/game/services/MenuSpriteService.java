package com.csse3200.game.services;

import com.badlogic.gdx.Screen;
import com.csse3200.game.GdxGame;
import com.csse3200.game.data.MenuSpriteData;
import com.csse3200.game.exceptions.MenuSpriteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing the menu/map sprites.
 * Contains methods for registering, and getting a list and individual sprite data.
 */
public class MenuSpriteService {
    private static final Logger logger = LoggerFactory.getLogger(MenuSpriteService.class);
    private final Map<GdxGame.ScreenType, Map<Enum<?>, MenuSpriteData>> menuSprites;

    public MenuSpriteService() {
        this.menuSprites = new HashMap<>();
    }

    /**
     * Used to register a sprite with an ID to the hashmap.
     * @param screenType - the GdxGame ScreenType of the screen.
     * @param menuSpriteData - the MenuSpriteData to be registered.
     */
    public void register(GdxGame.ScreenType screenType, MenuSpriteData menuSpriteData) {
        menuSprites.computeIfAbsent(screenType, k -> new HashMap<>());

        menuSprites.get(screenType).put(menuSpriteData.id, menuSpriteData);
    }

    /**
     * Gets a list of immutable references to MenuSpriteData's
     * @return a list of MenuSpriteData
     */
    public List<MenuSpriteData> getMenuSprites() {
        List<MenuSpriteData> menuSpriteDataList = new ArrayList<>();
        for (GdxGame.ScreenType screen : menuSprites.keySet()) {
            menuSpriteDataList.addAll(menuSprites.get(screen).values());
        }

        return List.copyOf(menuSpriteDataList);
    }

    /**
     * **** FOR STATIC MENU SPRITES ****
     * Gets a specific MenuSpriteData from the appropriate screen.
     * @param screen - GameScreen to get data for.
     * @return MenuSpriteData for the screen.
     * @throws MenuSpriteNotFoundException when no MenuSpriteData can be found.
     */
    public MenuSpriteData getMenuSprite(Screen screen) throws MenuSpriteNotFoundException {
        for (MenuSpriteData menuSpriteData : getMenuSprites()) {
            if (menuSpriteData.getScreenClass().equals(screen.getClass()) && menuSpriteData.id == null) {
                return menuSpriteData;
            }
        }

        throw new MenuSpriteNotFoundException();
    }

    /**
     * **** FOR STATIC MENU SPRITES ****
     * @param screenType - GdxGame ScreenType of the sprite desired.
     * @return MenuSpriteData matching the screen type.
     * @throws MenuSpriteNotFoundException when no MenuSpriteData can be found.
     */
    public MenuSpriteData getMenuSprite(GdxGame.ScreenType screenType) throws MenuSpriteNotFoundException {
        if (menuSprites.get(screenType).size() == 1 && menuSprites.get(screenType).containsKey(null)) {
            return menuSprites.get(screenType).get(null);
        }

        throw new MenuSpriteNotFoundException();
    }

    /**
     * **** FOR DYNAMIC MENU SPRITES ONLY ****
     * Gets a specific MenuSpriteData for a given screen and enum ID.
     * @param screen - GameScreen to get data for.
     * @param id - Dyanmic ID for specific MenuSpriteData.
     * @return MenuSpriteData that matches both screen and ID.
     * @throws MenuSpriteNotFoundException when no MenuSpriteData can be found.
     */
    public MenuSpriteData getMenuSprite(Screen screen, Enum<?> id) throws MenuSpriteNotFoundException {
        for (MenuSpriteData menuSpriteData : getMenuSprites()) {
            if (menuSpriteData.getScreenClass().equals(screen.getClass()) && menuSpriteData.id == id) {
                return menuSpriteData;
            }
        }

        throw new MenuSpriteNotFoundException();
    }

    /**
     * **** FOR DYNAMIC MENU SPRITES ONLY ****
     * Gets a specific MenuSpriteData for a given ScreenType and enum ID.
     * @param screenType - GdxGame ScreenType of the sprite desired.
     * @param id - Enum ID of the sprite desired.
     * @return MenuSpriteData matching both screenType and id.
     * @throws MenuSpriteNotFoundException when no MenuSpriteData can be found.
     */
    public MenuSpriteData getMenuSprite(GdxGame.ScreenType screenType, Enum<?> id) throws MenuSpriteNotFoundException {
        MenuSpriteData menuSprite = menuSprites.get(screenType).get(id);
        if (menuSprite != null) {
            return menuSprite;
        }

        throw new MenuSpriteNotFoundException();
    }
}
