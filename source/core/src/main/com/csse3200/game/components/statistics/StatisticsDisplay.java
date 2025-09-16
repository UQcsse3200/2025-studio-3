package com.csse3200.game.components.statistics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The StatisticsDisplay class is a UI component that renders a table of player Statistics on
 * screen.
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

  /** Builds and adds the main UI actors for the Statistics screen. */
  private void addActors() {
    Label title = new Label("Statistics", skin, "title");
    Table statisticsTable = makeStatisticsTable();
    Table backBtn = makeBackBtn();

    rootTable = new Table();
    rootTable.setFillParent(true);

    rootTable.add(title).expandX().top().padTop(20f);

    rootTable.row().padTop(30f);
    rootTable.add(statisticsTable).expandX().expandY();

    stage.addActor(rootTable);
    stage.addActor(backBtn);
  }

  /**
   * Builds a table displaying the player's Statistics
   *
   * @return a table containing formatted Statistics
   */
  private Table makeStatisticsTable() {
    // Get current values
    Statistics statistics = ServiceLocator.getProfileService().getProfile().getStatistics();

    // Create components
    Label killsLabel = new Label("Total Kills:", skin);
    Label kills = new Label(Integer.toString(statistics.getStatistic("enemiesKilled")), skin);

    Label shotsLabel = new Label("Shots Fired:", skin);
    Label shots = new Label(Integer.toString(statistics.getStatistic("shotsFired")), skin);

    Label levelsLabel = new Label("Levels Passed:", skin);
    Label levels = new Label(Integer.toString(statistics.getStatistic("levelsCompleted")), skin);

    Label lostLevelsLabel = new Label("Levels Lost:", skin);
    Label lostLevels = new Label(Integer.toString(statistics.getStatistic("levelsLost")), skin);

    Label defencesPlantedLabel = new Label("Defences Planted:", skin);
    Label defencesPlanted =
        new Label(Integer.toString(statistics.getStatistic("defencesPlanted")), skin);

    Label defencesUnlockedLabel = new Label("Defences Unlocked:", skin);
    Label defencesUnlocked =
        new Label(Integer.toString(statistics.getStatistic("defencesUnlocked")), skin);

    Label defencesLostLabel = new Label("Defences Lost:", skin);
    Label defencesLost = new Label(Integer.toString(statistics.getStatistic("defencesLost")), skin);

    Label coinsLabel = new Label("Total Coins Earned:", skin);
    Label coins = new Label(Integer.toString(statistics.getStatistic("coinsCollected")), skin);

    Label coinsSpentLabel = new Label("Total Coins Spent:", skin);
    Label coinsSpent = new Label(Integer.toString(statistics.getStatistic("coinsSpent")), skin);

    Label skillPointsLabel = new Label("Skill Points Collected:", skin);
    Label skillPoints =
        new Label(Integer.toString(statistics.getStatistic("skillPointsCollected")), skin);

    Label skillPointsSpentLabel = new Label("Skill Points Spent:", skin);
    Label skillPointsSpent =
        new Label(Integer.toString(statistics.getStatistic("skillPointsSpent")), skin);

    Label purchasesLabel = new Label("Purchases Made:", skin);
    Label purchases = new Label(Integer.toString(statistics.getStatistic("purchasesMade")), skin);

    Label wavesLabel = new Label("Waves Completed:", skin);
    Label waves = new Label(Integer.toString(statistics.getStatistic("wavesCompleted")), skin);

    Label itemsLabel = new Label("Items Collected:", skin);
    Label items = new Label(Integer.toString(statistics.getStatistic("itemsCollected")), skin);

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
    table.add(lostLevelsLabel).right().padRight(15f);
    table.add(lostLevels).left();

    table.row().padTop(10f);
    table.add(defencesPlantedLabel).right().padRight(15f);
    table.add(defencesPlanted).left();

    table.row().padTop(10f);
    table.add(defencesUnlockedLabel).right().padRight(15f);
    table.add(defencesUnlocked).left();

    table.row().padTop(10f);
    table.add(defencesLostLabel).right().padRight(15f);
    table.add(defencesLost).left();

    table.row().padTop(10f);
    table.add(coinsLabel).right().padRight(15f);
    table.add(coins).left();

    table.row().padTop(10f);
    table.add(coinsSpentLabel).right().padRight(15f);
    table.add(coinsSpent).left();

    table.row().padTop(10f);
    table.add(skillPointsSpentLabel).right().padRight(15f);
    table.add(skillPointsSpent).left();

    table.row().padTop(10f);
    table.add(skillPointsLabel).right().padRight(15f);
    table.add(skillPoints).left();

    table.row().padTop(10f);
    table.add(purchasesLabel).right().padRight(15f);
    table.add(purchases).left();

    table.row().padTop(10f);
    table.add(wavesLabel).right().padRight(15f);
    table.add(waves).left();

    table.row().padTop(10f);
    table.add(itemsLabel).right().padRight(15f);
    table.add(items).left();

    return table;
  }

  /**
   * Builds a table containing exit button.
   *
   * @return table with exit button
   */
  private Table makeBackBtn() {
    // Create close button using close-icon.png
    ImageButton closeButton = new ImageButton(
        new TextureRegionDrawable(
            ServiceLocator.getGlobalResourceService().getAsset("images/close-icon.png", Texture.class)));
    
    // Position in top left with 20f padding
    closeButton.setSize(60f, 60f);
    closeButton.setPosition(
        20f,  // 20f padding from left
        stage.getHeight() - 60f - 20f  // 20f padding from top
    );

    // Add listener for the back button
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            backMenu();
          }
        });

    // Place button in a table
    Table table = new Table();
    table.setFillParent(true);
    table.add(closeButton).top().right().pad(20f);
    return table;
  }

  /** Handles navigation back to the Profile Screen. */
  private void backMenu() {
    game.setScreen(ScreenType.MAIN_GAME);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  /** Disposes of this UI component. */
  @Override
  public void dispose() {
    rootTable.clear();
    super.dispose();
  }
}
