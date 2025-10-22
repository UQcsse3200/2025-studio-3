package com.csse3200.game.components.achievements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.configs.BaseAchievementConfig;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.Map;

/**
 * The AchievementsDisplay class is a UI component that renders a table of player achievements on
 * screen.
 */
public class AchievementsDisplay extends UIComponent {
  private final GdxGame game;
  private Table rootTable;
  float uiScale = ui.getUIScale();

  /**
   * Creates an AchievementsDisplay for the game instance.
   *
   * @param game current game instance
   */
  public AchievementsDisplay(GdxGame game) {
    super();
    this.game = game;
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Builds and adds the main UI actors for the Achievements screen. */
  private void addActors() {
    // Create background image
    Texture backgroundTexture =
        ServiceLocator.getGlobalResourceService()
            .getAsset("images/ui/menu_card.png", Texture.class);
    Image backgroundImage = new Image(backgroundTexture);
    backgroundImage.setSize(870f * uiScale, 610f * uiScale);

    // Create content table for achievements
    rootTable = new Table();
    rootTable.setSize(870f * uiScale, 610f * uiScale);
    rootTable.center();

    // Add title with 10f padding from top
    Label title = ui.title("Achievements");
    rootTable.add(title).expandX().center().padTop(25f * uiScale).row();

    createAchievementDisplay();

    // Create stack with background and content
    Stack stack = new Stack();
    stack.add(backgroundImage);
    stack.add(rootTable);
    stack.setSize(1044f * uiScale, 732f * uiScale);
    stack.setPosition(
        (stage.getWidth() - stack.getWidth()) / 2, (stage.getHeight() - stack.getHeight()) / 2);

    stage.addActor(stack);
    createCloseButton();
  }

  /** Creates the achievement display with achievements organized by tier. */
  private void createAchievementDisplay() {
    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) {
      Label errorLabel = ui.text("Config service not available");
      errorLabel.setColor(Color.RED);
      rootTable.row().padTop(20);
      rootTable.add(errorLabel).center().row();
      return;
    }

    Statistics statistics = ServiceLocator.getProfileService().getProfile().getStatistics();
    Map<String, BaseAchievementConfig> achievementConfigs = configService.getAchievementConfigs();

    if (achievementConfigs == null || achievementConfigs.isEmpty()) {
      Label errorLabel = ui.text("No achievements configured");
      errorLabel.setColor(Color.RED);
      rootTable.row().padTop(20);
      rootTable.add(errorLabel).center().row();
      return;
    }

    Table[] tables = populateTable(achievementConfigs, statistics);
    Table t1Table = tables[0];
    Table t2Table = tables[1];
    Table t3Table = tables[2];

    // Stack tables vertically
    Table allTables = new Table();

    allTables.add(ui.subheading("Tier 1")).center().row();
    allTables.add(t1Table).left().padBottom(20f * uiScale).row();

    allTables.add(ui.subheading("Tier 2")).center().row();
    allTables.add(t2Table).left().padBottom(20 * uiScale).row();

    allTables.add(ui.subheading("Tier 3")).center().row();
    allTables.add(t3Table).left().padBottom(20 * uiScale).row();

    ScrollPane scrollPane = new ScrollPane(allTables, skin);
    scrollPane.setFadeScrollBars(false);

    rootTable.row();
    rootTable.add(scrollPane).expand().fill().row();
  }

  /**
   * Populates the table with achievements organized by tier.
   *
   * @param achievementConfigs the achievement configs
   * @param statistics the statistics
   * @return the tables
   */
  private Table[] populateTable(
      Map<String, BaseAchievementConfig> achievementConfigs, Statistics statistics) {
    // Separate achievements by tier
    Table t1Table = new Table();
    Table t2Table = new Table();
    Table t3Table = new Table();

    int colCountT1 = 0;
    int colCountT2 = 0;
    int colCountT3 = 0;

    for (Map.Entry<String, BaseAchievementConfig> entry : achievementConfigs.entrySet()) {
      String achievementKey = entry.getKey();
      BaseAchievementConfig config = entry.getValue();
      boolean isUnlocked = statistics.isAchievementUnlocked(achievementKey);

      ImageButton achButton =
          createAchievementButton(
              config, isUnlocked, statistics.getStatistic(config.getStatistic()));

      // Place in correct tier table based on tier string
      String tier = config.getTier();
      if ("T1".equals(tier)) {
        t1Table.add(achButton).pad(5).size(200f * uiScale, 72f * uiScale);
        colCountT1++;
        if (colCountT1 % 6 == 0) t1Table.row();
      } else if ("T2".equals(tier)) {
        t2Table.add(achButton).pad(5).size(200f * uiScale, 72f * uiScale);
        colCountT2++;
        if (colCountT2 % 6 == 0) t2Table.row();
      } else if ("T3".equals(tier)) {
        t3Table.add(achButton).pad(5).size(200f * uiScale, 72f * uiScale);
        colCountT3++;
        if (colCountT3 % 6 == 0) t3Table.row();
      }
    }
    return new Table[] {t1Table, t2Table, t3Table};
  }

  /**
   * Creates a button for displaying an achievement.
   *
   * @param config the achievement config to create a button for
   * @param isUnlocked whether the achievement is unlocked
   * @param currentProgress current progress towards the achievement
   * @return the created button
   */
  private ImageButton createAchievementButton(
      BaseAchievementConfig config, boolean isUnlocked, int currentProgress) {
    ImageButton achButton =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/achievement.png", Texture.class)));

    achButton.setSize(200f * uiScale, 72f);
    Color labelColor = isUnlocked ? Color.GREEN : Color.RED;
    Label nameLabel = ui.text(config.getName());
    nameLabel.setColor(labelColor);
    nameLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
    Table centerTable = new Table();
    centerTable.setSize(200f * uiScale, 72f * uiScale);
    centerTable.center();
    centerTable.add(nameLabel).center().expand().fill();
    achButton.addActor(centerTable);

    achButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            showAchievementDialog(config, currentProgress);
          }
        });

    return achButton;
  }

  /**
   * Shows an info dialog with achievement details using DialogService.
   *
   * @param config the achievement config to show details for
   * @param currentProgress current progress towards the achievement
   */
  private void showAchievementDialog(BaseAchievementConfig config, int currentProgress) {
    String title = config.getName();

    // Build the message content
    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder
        .append(config.getDescription())
        .append("\n")
        .append("Progress: ")
        .append(currentProgress)
        .append("/")
        .append(config.getQuota());

    String message = messageBuilder.toString();

    // Use DialogService to create an info dialog
    ServiceLocator.getDialogService().info(title, message);
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    // Create close button using close-icon.png
    TextButton closeButton = ui.createBackExitButton(entity.getEvents(), stage.getHeight(), "Back");
    AchievementBackAction backAction = new AchievementBackAction(game);
    entity.getEvents().addListener("back", backAction::backMenu);
    stage.addActor(closeButton);
  }

  /** Disposes of this UI component. */
  @Override
  public void dispose() {
    rootTable.clear();
    super.dispose();
  }
}
