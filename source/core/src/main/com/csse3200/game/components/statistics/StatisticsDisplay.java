package com.csse3200.game.components.statistics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The StatisticsDisplay class is a UI component that renders a table of player Statistics on screen.
 */
public class StatisticsDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsDisplay.class);
    private final GdxGame game;

    // Root table that holds all UI elements for this screen
    private Table rootTable;

    /**
     * Creates a StatisticsDisplay for the game instance.
     *
     * @param game current game instance
     */
    public StatisticsDisplay(GdxGame game) {
        super();
        this.game = game;
    }

    @Override
    public void create() {
        super.create();
        addActors();
    }

    /**
     * Builds and adds the main UI actors for the Statistics screen.
     */
    private void addActors() {
        Label title = new Label("Statistics", skin, "title");
        Table statisticsTable = makeStatisticsTable();
        Table menuBtns = makeBackBtn();

        rootTable = new Table();
        rootTable.setFillParent(true);

        rootTable.add(title).expandX().top().padTop(20f);

        rootTable.row().padTop(30f);
        rootTable.add(statisticsTable).expandX().expandY();

        rootTable.row();
        rootTable.add(menuBtns).fillX();

        stage.addActor(rootTable);
    }

    /**
     * Builds a table displaying the player's Statistics
     *
     * @return a table containing formatted Statistics
     */
    private Table makeStatisticsTable() {
        // Get current values
        Statistics statistics = Persistence.profile().statistics();

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

    /**
     * Builds a table containing exit button.
     *
     * @return table with exit button
     */
    private Table makeBackBtn() {
        TextButton backBtn = new TextButton("Back", skin);

        // Add listener for the back button
        backBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Back button clicked");
                        backMenu();
                    }
                });

        // Place button in a table
        Table table = new Table();
        table.add(backBtn).expandX().left().pad(0f, 15f, 15f, 0f);
        return table;
    }

    /**
     * Handles navigation back to the Profile Screen.
     */
    private void backMenu() {
        game.setScreen(ScreenType.PROFILE);
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

    /**
     * Disposes of this UI component.
     */
    @Override
    public void dispose() {
        rootTable.clear();
        super.dispose();
    }
}

