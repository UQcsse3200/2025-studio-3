package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.configs.BaseAchievementConfig;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The game screen containing the achievements menu. Displays all achievements organized by tier
 * with unlock status and progress.
 */
public class AchievementsScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(AchievementsScreen.class);
  private final GdxGame game;
  private Stage stage;
  private Table rootTable;

  // Styles
  private Skin skin;
  private Label.LabelStyle headerStyle;
  private Label.LabelStyle textStyle;
  private Window.WindowStyle windowStyle;

  public AchievementsScreen(GdxGame game) {
    this.game = game;
    logger.debug("Created achievements screen");
  }

  @Override
  public void show() {
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);

    // Load skin
    skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));

    // Styles from skin
    headerStyle =
        skin.has("title", Label.LabelStyle.class)
            ? skin.get("title", Label.LabelStyle.class)
            : new Label.LabelStyle(new BitmapFont(), Color.DARK_GRAY);

    textStyle = skin.get(Label.LabelStyle.class);
    windowStyle = skin.get(Window.WindowStyle.class);

    // Root table
    rootTable = new Table();
    rootTable.setFillParent(true);
    stage.addActor(rootTable);

    // Header
    Label header = new Label("Achievements", headerStyle);
    header.setFontScale(2f);

    rootTable.top().padTop(20);
    rootTable.add(header).center().row();

    // Create achievement display
    createAchievementDisplay();

    // Back button
    TextButton backButton = new TextButton("Back", skin);
    backButton.getLabel().setFontScale(2f);
    backButton.pad(20, 60, 20, 60);

    backButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
            game.setScreen(GdxGame.ScreenType.MAIN_MENU);
          }
        });

    rootTable.row().padBottom(40);
    rootTable.add(backButton).center().expandX();
  }

  /** Creates the achievement display with achievements organized by tier. */
  private void createAchievementDisplay() {
    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) {
      Label errorLabel = new Label("Config service not available", textStyle);
      errorLabel.setColor(Color.RED);
      rootTable.row().padTop(20);
      rootTable.add(errorLabel).center().row();
      return;
    }

    Statistics statistics = Persistence.profile().statistics();
    Map<String, BaseAchievementConfig> achievementConfigs = configService.getAchievementConfigs();

    if (achievementConfigs == null || achievementConfigs.isEmpty()) {
      Label errorLabel = new Label("No achievements configured", textStyle);
      errorLabel.setColor(Color.RED);
      rootTable.row().padTop(20);
      rootTable.add(errorLabel).center().row();
      return;
    }

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

      TextButton achButton =
          createAchievementButton(
              config, isUnlocked, statistics.getStatistic(config.getStatistic()));

      // Place in correct tier table based on tier string
      String tier = config.getTier();
      if ("T1".equals(tier)) {
        t1Table.add(achButton).pad(5).width(150).height(60);
        colCountT1++;
        if (colCountT1 % 7 == 0) t1Table.row();
      } else if ("T2".equals(tier)) {
        t2Table.add(achButton).pad(5).width(150).height(60);
        colCountT2++;
        if (colCountT2 % 7 == 0) t2Table.row();
      } else if ("T3".equals(tier)) {
        t3Table.add(achButton).pad(5).width(150).height(60);
        colCountT3++;
        if (colCountT3 % 7 == 0) t3Table.row();
      }
    }

    // Stack tables vertically
    Table allTables = new Table();

    allTables.add(new Label("Tier 1", headerStyle)).center().row();
    allTables.add(t1Table).left().padBottom(20).row();

    allTables.add(new Label("Tier 2", headerStyle)).center().row();
    allTables.add(t2Table).left().padBottom(20).row();

    allTables.add(new Label("Tier 3", headerStyle)).center().row();
    allTables.add(t3Table).left().padBottom(20).row();

    ScrollPane scrollPane = new ScrollPane(allTables, skin);
    scrollPane.setFadeScrollBars(false);

    rootTable.row().padTop(20);
    rootTable.add(scrollPane).expand().fill().row();
  }

  /**
   * Creates a button for displaying an achievement.
   *
   * @param config the achievement config to create a button for
   * @param isUnlocked whether the achievement is unlocked
   * @param currentProgress current progress towards the achievement
   * @return the created button
   */
  private TextButton createAchievementButton(
      BaseAchievementConfig config, boolean isUnlocked, int currentProgress) {
    TextButton achButton = new TextButton(config.getName(), skin);

    // Set color based on unlock status
    if (isUnlocked) {
      achButton.getLabel().setColor(Color.GREEN);
    } else {
      achButton.getLabel().setColor(Color.RED);
    }

    achButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
            showAchievementPopup(config, isUnlocked, currentProgress);
          }
        });

    return achButton;
  }

  /**
   * Opens a popup showing achievement details.
   *
   * @param config the achievement config to show details for
   * @param isUnlocked whether the achievement is unlocked
   * @param currentProgress current progress towards the achievement
   */
  private void showAchievementPopup(
      BaseAchievementConfig config, boolean isUnlocked, int currentProgress) {
    Window popup = new Window("Achievement Details", windowStyle);

    Label name = new Label(config.getName(), headerStyle);
    if (isUnlocked) {
      name.setColor(Color.GREEN);
    } else {
      name.setColor(Color.RED);
    }

    Label description = new Label(config.getDescription(), textStyle);
    Label points = new Label("Points: " + config.getSkillPoints(), textStyle);
    Label progress = new Label("Progress: " + currentProgress + "/" + config.getQuota(), textStyle);
    Label tier = new Label("Tier: " + config.getTier(), textStyle);
    Label statistic = new Label("Tracks: " + config.getStatistic(), textStyle);

    name.setFontScale(1.5f);

    TextButton closeBtn = new TextButton("Close", skin);
    closeBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
            popup.remove();
          }
        });

    popup.add(name).pad(10).row();
    popup.add(description).pad(10).row();
    popup.add(tier).pad(10).row();
    popup.add(statistic).pad(10).row();
    popup.add(points).pad(10).row();
    popup.add(progress).pad(10).row();
    popup.add(closeBtn).pad(10).row();

    popup.pack();
    popup.setModal(true);
    popup.setMovable(false);
    popup.setPosition(
        (stage.getWidth() - popup.getWidth()) / 2, (stage.getHeight() - popup.getHeight()) / 2);

    stage.addActor(popup);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(248f / 255f, 249f / 255f, 178 / 255f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      game.setScreen(GdxGame.ScreenType.MAIN_MENU);
    }
  }

  @Override
  public void dispose() {
    if (stage != null) {
      stage.dispose();
    }
    if (skin != null) {
      skin.dispose();
    }
  }
}
