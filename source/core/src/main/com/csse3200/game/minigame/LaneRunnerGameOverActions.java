package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaneRunnerGameOverActions extends Component {
    private static final Logger logger = LoggerFactory.getLogger(com.csse3200.game.minigame.LaneRunnerGameOverActions.class);
    private GdxGame game;

    public LaneRunnerGameOverActions(GdxGame game) {
        this.game = game;
    }
    @Override
    public void create() {
        entity.getEvents().addListener("playAgain", this::onPlayAgain);
        entity.getEvents().addListener("mainMenu", this::onMainMenu);
    }
    @Override
    public void update() {
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {onMainMenu();}
        else if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {onPlayAgain();}
    }
    public void onPlayAgain() {
        logger.info("Restarting Lane Runner Mini game");
        game.setScreen(GdxGame.ScreenType.LANE_RUNNER);
    }
    public void onMainMenu() {
        logger.info("Launching Main Menu screen");
        game.setScreen(GdxGame.ScreenType.MAIN_MENU);
    }

}
