package com.csse3200.game.screens;

import com.badlogic.gdx.Screen;
import com.csse3200.game.GdxGame;
import com.csse3200.game.data.MenuSpriteData;

/**
 * Implements functions required for dynamic menu sprite operation.
 * @param <E> - Enum of levels/options
 */
public interface DynamicMenuSpriteScreen<E extends Enum<E>> extends Screen {
    /**
     * Ran at game launch to initialise screen in MapSpriteService
     */
    void register(GdxGame.ScreenType screenType);

    /**
     * Runs on screen activation.
     * Used to determine what version of the screen has been chosen.
     * @param menuId - the ID of the menu which has been selected.
     */
    void onMenuEnter(E menuId);
}
