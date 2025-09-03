package com.csse3200.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.csse3200.game.files.UserSettings;
import com.csse3200.game.screens.MainGameScreen;
import com.csse3200.game.screens.MainMenuScreen;
import com.csse3200.game.screens.SettingsScreen;
import com.csse3200.game.screens.WorldMapScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.badlogic.gdx.Gdx.app;

public class GdxGame extends Game {
    private static final Logger logger = LoggerFactory.getLogger(GdxGame.class);

    @Override
    public void create() {
        logger.info("Creating game");
        loadSettings();

        // Sets background to light yellow
        Gdx.gl.glClearColor(248f/255f, 249f/255f, 178f/255f, 1);

        setScreen(ScreenType.MAIN_MENU);
    }

    private void loadSettings() {
        logger.debug("Loading game settings");
        UserSettings.Settings settings = UserSettings.get();
        UserSettings.applySettings(settings);
    }

    public void setScreen(ScreenType screenType) {
        logger.info("Setting game screen to {}", screenType);
        Screen currentScreen = getScreen();
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        setScreen(newScreen(screenType));
    }

    @Override
    public void dispose() {
        logger.debug("Disposing of current screen");
        getScreen().dispose();
    }

    private Screen newScreen(ScreenType screenType) {
        switch (screenType) {
            case MAIN_MENU:
                return new MainMenuScreen(this);
            case MAIN_GAME:
                return new MainGameScreen(this);
            case SETTINGS:
                return new SettingsScreen(this);
            case WORLD_MAP:
                return new WorldMapScreen(this);
            default:
                return null;
        }
    }

    public enum ScreenType {
        MAIN_MENU, MAIN_GAME, SETTINGS, WORLD_MAP
    }

    public void exit() {
        app.exit();
    }
}
