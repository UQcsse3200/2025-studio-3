package com.csse3200.game.minigame;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MiniGameActions extends Component {
    private static final Logger logger = LoggerFactory.getLogger(com.csse3200.game.components.mainmenu.MainMenuActions.class);
    private GdxGame game;

    public MiniGameActions(GdxGame game) {
        this.game = game;
    }

    @Override
    public void create() {
        entity.getEvents().addListener("lanerunner", this::onLaneRunner);
        entity.getEvents().addListener("brickbreaker", this::onLaneRunner);
        entity.getEvents().addListener("back", this::onBack);
    }

    /**
     * Swaps to the Main Game screen.
     */
    private void onLaneRunner() {
        logger.info("Lane Runner Mini game");
        Persistence.load();
        game.loadMenus();
        game.setScreen(GdxGame.ScreenType.LANE_RUNNER);
    }
//    private void onBrickBreaker() {
//        logger.info("Brick Breaker Mini game");
//        Persistence.load();
//        game.loadMenus();
//        game.setScreen(GdxGame.ScreenType.BRICK_BREAKER);
//    }
    /**
     * Swaps to the Main Menu screen.
     */
    private void onBack() {
        logger.info("Launching Main Menu screen");
        game.setScreen(GdxGame.ScreenType.MAIN_MENU);
    }
}
