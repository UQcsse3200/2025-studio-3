package com.csse3200.game.data;

import com.badlogic.gdx.Screen;
import com.csse3200.game.GdxGame;
import com.csse3200.game.screens.DynamicMenuSpriteScreen;
import com.csse3200.game.screens.MenuSpriteScreen;

import java.lang.reflect.InvocationTargetException;

/**
 * Stores data for menu sprites
 * also facilitates activating a sprite
 */
public class MenuSpriteData {
    private GdxGame.ScreenType screenType;
    private Class<?> screenClass;
    public Enum<?> id;
    private int x;
    private int y;
    private String name;
    private String description;
    private String spriteResourcePath;
    private boolean locked;

    public MenuSpriteData(Class<? extends Screen> screenClass, Enum<?> id,
                          Integer x, Integer y, String name,
                          String description, String spriteResourcePath,
                          Boolean locked) {
        this.screenClass = screenClass;
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
        this.description = description;
        this.spriteResourcePath = spriteResourcePath;
        this.locked = locked;
    }

    /**
     * Initialises a new builder (for dynamic menu sprite creation).
     * @param screen - current game screen.
     * @param id - desired enum ID for the dynamic screen.
     * @return a new MenuSpriteBuilder.
     */
    public static MenuSpriteDataBuilder builder(Screen screen, Enum<?> id) {
        return new MenuSpriteDataBuilder(screen, id);
    }

    /**
     * Functions like "GdxGame.setScreen" but for dynamic menu sprites.
     * @param dynamicMenuSpriteScreen - the menu to enter.
     * @param id - the enum ID.
     */
    private static <E extends Enum<E>> void dynamicMenuEnter(DynamicMenuSpriteScreen<E> dynamicMenuSpriteScreen, Enum<?> id) {
        @SuppressWarnings("unchecked") E castId = (E) id;
        dynamicMenuSpriteScreen.onMenuEnter(castId);
    }

    /**
     * Used to "enter" a Menu/Map.
     * @param game GdxGame passed into main function for screen.
     * @throws NoSuchMethodException - when no class can be constructed.
     * @throws InvocationTargetException - when no new class can be instantiated.
     * @throws InstantiationException - when no new class can be instantiated.
     * @throws IllegalAccessException - when no new class can be instantiated.
     */
    public void enter(GdxGame game) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (MenuSpriteScreen.class.isAssignableFrom(screenClass)) {
            // static
            MenuSpriteScreen screen = (MenuSpriteScreen) screenClass.getDeclaredConstructor(GdxGame.class).newInstance(game);
            Screen currentScreen = game.getScreen();
            if (currentScreen != null) {
                currentScreen.dispose();
            }
            game.setScreen(screen);
        } else if (DynamicMenuSpriteScreen.class.isAssignableFrom(screenClass)) {
            // dynamic
            DynamicMenuSpriteScreen<?> screen = (DynamicMenuSpriteScreen<?>) screenClass.getDeclaredConstructor(GdxGame.class).newInstance(game);
            Screen currentScreen = game.getScreen();
            if (currentScreen != null) {
                currentScreen.dispose();
            }
            game.setScreen(screen);
            dynamicMenuEnter(screen, id);
        }
    }


    // single/nondynamic creation
    public MenuSpriteData(GdxGame.ScreenType screenType) {
        this.screenType = screenType;
    }

    /**
     * Creates a new MenuSprite editor.
     * @param screen - the current game screen.
     * @return MenuSpriteDataEditor for the MenuSpriteData.
     */
    public MenuSpriteDataEditor edit(Screen screen) {
        this.screenClass = screen.getClass();
        return new MenuSpriteDataEditor(this);
    }

    public Class<?> getScreenClass() {
        return this.screenClass;
    }

    public GdxGame.ScreenType getScreenType() {
        return this.screenType;
    }

    // data getters and setters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getSpriteResourcePath() {
        return spriteResourcePath;
    }
    public boolean getLocked() {
        return locked;
    }

    public Enum<?> getId() {
        return id;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setSpriteResourcePath(String spriteResourcePath) {
        this.spriteResourcePath = spriteResourcePath;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return screenType.toString() + " at (" + x + ", " + y + ") " + name + " : " + description;
    }
}
