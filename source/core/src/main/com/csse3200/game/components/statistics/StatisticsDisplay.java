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
import com.csse3200.game.ui.TypographyFactory;
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
    Label title = TypographyFactory.createTitle("Statistics");
    Table statisticsTable = makeStatisticsTable();

    rootTable = new Table();
    rootTable.setFillParent(true);

    rootTable.add(title).expandX().top().padTop(20f);

    rootTable.row().padTop(30f);
    rootTable.add(statisticsTable).expandX().expandY();

    stage.addActor(rootTable);
    createCloseButton();
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
    Label killsLabel = TypographyFactory.createSubtitle("Total Kills:");
    Label kills =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("enemiesKilled")));

    Label shotsLabel = TypographyFactory.createSubtitle("Shots Fired:");
    Label shots =
        TypographyFactory.createSubtitle(Integer.toString(statistics.getStatistic("shotsFired")));

    Label levelsLabel = TypographyFactory.createSubtitle("Levels Passed:");
    Label levels =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("levelsCompleted")));

    Label lostLevelsLabel = TypographyFactory.createSubtitle("Levels Lost:");
    Label lostLevels =
        TypographyFactory.createSubtitle(Integer.toString(statistics.getStatistic("levelsLost")));

    Label defencesPlantedLabel = TypographyFactory.createSubtitle("Defences Planted:");
    Label defencesPlanted =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("defencesPlanted")));

    Label defencesUnlockedLabel = TypographyFactory.createSubtitle("Defences Unlocked:");
    Label defencesUnlocked =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("defencesUnlocked")));

    Label defencesLostLabel = TypographyFactory.createSubtitle("Defences Lost:");
    Label defencesLost =
        TypographyFactory.createSubtitle(Integer.toString(statistics.getStatistic("defencesLost")));

    Label coinsLabel = TypographyFactory.createSubtitle("Total Coins Earned:");
    Label coins =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("coinsCollected")));

    Label coinsSpentLabel = TypographyFactory.createSubtitle("Total Coins Spent:");
    Label coinsSpent =
        TypographyFactory.createSubtitle(Integer.toString(statistics.getStatistic("coinsSpent")));

    Label skillPointsLabel = TypographyFactory.createSubtitle("Skill Points Collected:");
    Label skillPoints =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("skillPointsCollected")));

    Label skillPointsSpentLabel = TypographyFactory.createSubtitle("Skill Points Spent:");
    Label skillPointsSpent =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("skillPointsSpent")));

    Label purchasesLabel = TypographyFactory.createSubtitle("Purchases Made:");
    Label purchases =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("purchasesMade")));

    Label wavesLabel = TypographyFactory.createSubtitle("Waves Completed:");
    Label waves =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("wavesCompleted")));

    Label itemsLabel = TypographyFactory.createSubtitle("Items Collected:");
    Label items =
        TypographyFactory.createSubtitle(
            Integer.toString(statistics.getStatistic("itemsCollected")));

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

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    // Create close button using close-icon.png
    ImageButton closeButton =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));

    // Position in top left with 20f padding
    closeButton.setSize(60f, 60f);
    closeButton.setPosition(
        20f, // 20f padding from left
        stage.getHeight() - 60f - 20f // 20f padding from top
        );

    // Add listener for the close button
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Close button clicked");
            backMenu();
          }
        });

    stage.addActor(closeButton);
  }

  /** Handles navigation back to the World Map. */
  private void backMenu() {
    game.setScreen(ScreenType.WORLD_MAP);
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
