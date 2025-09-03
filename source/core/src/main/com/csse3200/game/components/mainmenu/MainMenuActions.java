package com.csse3200.game.components.mainmenu;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMenuActions extends Component {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuActions.class);
    private GdxGame game;

    public MainMenuActions(GdxGame game) {
        this.game = game;
    }

    @Override
    public void create() {
        entity.getEvents().addListener("start", this::onStart);
        entity.getEvents().addListener("load", this::onLoad);
        entity.getEvents().addListener("exit", this::onExit);
        entity.getEvents().addListener("settings", this::onSettings);
        entity.getEvents().addListener("worldMap", this::onWorldMap);
    }

    private void onStart() {
        logger.info("Start game");
        game.setScreen(GdxGame.ScreenType.MAIN_GAME);
    }

    private void onWorldMap() {
        logger.info("Launching world map screen");
        game.setScreen(GdxGame.ScreenType.WORLD_MAP);
    }

    private void onLoad() {
        logger.info("Load game (not yet implemented)");
    }

    private void onExit() {
        logger.info("Exit game");
        game.exit();
    }

    private void onSettings() {
        logger.info("Launching settings screen");
        game.setScreen(GdxGame.ScreenType.SETTINGS);
    }
}
