package com.csse3200.game.components.statistics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * */
public class StatisticsDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsDisplay.class);
    private final GdxGame game;

    private Table rootTable;

    public StatisticsDisplay(GdxGame game) {
        super();
        this.game = game;
    }

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        Label title = new Label("Statistics", skin, "title");
        Table statisticsTable = makeStatisticsTable();
        Table menuBtns = makeMenuBtns();

        rootTable = new Table();
        rootTable.setFillParent(true);

        rootTable.add(title).expandX().top().padTop(20f);

        rootTable.row().padTop(30f);
        rootTable.add(statisticsTable).expandX().expandY();

        rootTable.row();
        rootTable.add(menuBtns).fillX();

        stage.addActor(rootTable);
    }

    private Table makeStatisticsTable() {
        // Get current values
        Statistics statistics = new Statistics();
        //Statistics statistics = Profile.getStatistics();

        // Create components
        Label killsLabel = new Label("Total Kills:", skin);
        Label kills = new Label(Integer.toString(statistics.getKills()), skin);

        Label shotsLabel = new Label("Shots Fired:", skin);
        Label shots = new Label(Integer.toString(statistics.getShotsFired()), skin);

        Label levelsLabel = new Label("Levels Passed:", skin);
        Label levels = new Label(Integer.toString(statistics.getLevelsPassed()), skin);

        Label defencesLabel = new Label("Defences Unlocked:", skin);
        Label defences = new Label(Integer.toString(statistics.getNumDefencesUnlocked()), skin);

        Label coinsLabel = new Label("Total Coins Earned:", skin);
        Label coins = new Label(Integer.toString(statistics.getTotalCoinsEarned()), skin);

        // Position Components on table
        Table table = new Table();

        table.add(killsLabel).right().padRight(15f);
        table.add(kills).left();

        table.row().padTop(10f);
        table.add(shotsLabel).right().padRight(15f);
        table.add(shots).left();

        table.row().padTop(10f);
        table.add(levelsLabel).right().padRight(15f);
        table.add(levels).left();

        table.row().padTop(10f);
        table.add(defencesLabel).right().padRight(15f);
        table.add(defences).left();

        table.row().padTop(10f);
        table.add(coinsLabel).right().padRight(15f);
        table.add(coins).left();

        return table;
    }

    private Table makeMenuBtns() {
        TextButton exitBtn = new TextButton("Exit", skin);

        exitBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Exit button clicked");
                        exitMenu();
                    }
                });

        Table table = new Table();
        table.add(exitBtn).expandX().left().pad(0f, 15f, 15f, 0f);
        return table;
    }

    private void exitMenu() {
        game.setScreen(ScreenType.MAIN_MENU);
    }

    @Override
    protected void draw(SpriteBatch batch) {
        // draw is handled by the stage
    }

//    @Override
//    public void update() {
//        stage.act(ServiceLocator.getTimeSource().getDeltaTime());
//    }
//
    @Override
    public void dispose() {
        rootTable.clear();
        super.dispose();
    }
}

