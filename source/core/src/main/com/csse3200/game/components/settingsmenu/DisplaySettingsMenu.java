package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Display settings menu component. */
public class DisplaySettingsMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(DisplaySettingsMenu.class);
  private Table rootTable;

  // Display Settings Components
  private SelectBox<String> displayModeSelect;
  private TextField fpsText;
  private CheckBox vsyncCheck;

  public DisplaySettingsMenu() {
    super();
  }

  @Override
  public void create() {
    super.create();
    addActors();
    entity.getEvents().addListener("backtosettingsmenu", this::hideMenu);
    entity.getEvents().addListener("gamesettings", this::hideMenu);
    entity.getEvents().addListener("displaysettings", this::showMenu);
    entity.getEvents().addListener("audiosettings", this::hideMenu);
    rootTable.setVisible(false);
  }

  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);

    // Get current settings
    Settings settings = new Settings();

    // Create components
    Label displayModeLabel = new Label("Display Mode:", skin);
    displayModeSelect = new SelectBox<>(skin);
    displayModeSelect.setItems("Windowed", "Fullscreen", "Borderless");
    displayModeSelect.setSelected(settings.getCurrentMode().toString());
    whiten(displayModeLabel);

    Label resolutionLabel = new Label("Resolution:", skin);
    SelectBox<String> resolutionSelect = new SelectBox<>(skin);
    resolutionSelect.setItems("1920x1080", "1600x900", "1366x768", "1280x720");
    whiten(resolutionLabel);

    Label fpsLabel = new Label("Max FPS:", skin);
    fpsText = new TextField(Integer.toString(settings.getFps()), skin);
    whiten(fpsLabel);

    Label uiScaleLabel = new Label("UI Scale:", skin);
    SelectBox<String> uiScaleSelect = new SelectBox<>(skin);
    uiScaleSelect.setItems("Small", "Medium", "Large");
    uiScaleSelect.setSelected(settings.getCurrentUIScale().toString());
    whiten(uiScaleLabel);

    Label qualityLabel = new Label("Quality:", skin);
    SelectBox<String> qualitySelect = new SelectBox<>(skin);
    qualitySelect.setItems("Low", "High");
    qualitySelect.setSelected(settings.getQuality().toString());
    whiten(qualityLabel);

    Label vsyncLabel = new Label("VSync:", skin);
    vsyncCheck = new CheckBox("", skin);
    vsyncCheck.setChecked(settings.isVsync());
    whiten(vsyncLabel);

    // Apply button
    TextButton applyBtn = ButtonFactory.createButton("Apply");
    applyBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Apply button clicked");
            applyChanges();
            entity.getEvents().trigger("backtosettingsmenu");
          }
        });

    // Layout
    rootTable.add(displayModeLabel).right().padRight(15f);
    rootTable.add(displayModeSelect).left().width(150f);
    rootTable.row().padTop(10f);

    rootTable.add(resolutionLabel).right().padRight(15f);
    rootTable.add(resolutionSelect).left().width(150f);
    rootTable.row().padTop(10f);

    rootTable.add(fpsLabel).right().padRight(15f);
    rootTable.add(fpsText).left().width(100f);
    rootTable.row().padTop(10f);

    rootTable.add(uiScaleLabel).right().padRight(15f);
    rootTable.add(uiScaleSelect).left().width(150f);
    rootTable.row().padTop(10f);

    rootTable.add(qualityLabel).right().padRight(15f);
    rootTable.add(qualitySelect).left().width(150f);
    rootTable.row().padTop(10f);

    rootTable.add(vsyncLabel).right().padRight(15f);
    rootTable.add(vsyncCheck).left();
    rootTable.row().padTop(20f);

    // Apply button bottom center
    Table bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().center().pad(20f);
    bottomRow.add(applyBtn).size(150f, 50f);
    stage.addActor(bottomRow);

    stage.addActor(rootTable);
  }

  private void applyChanges() {
    // Apply display settings
    Settings settings = new Settings();
    Integer fpsVal = parseOrNull(fpsText.getText());
    if (fpsVal != null) {
      settings.setFps(fpsVal);
    }
    if (displayModeSelect != null) {
      String mode = displayModeSelect.getSelected();
      switch (mode) {
        case "Windowed":
          settings.setCurrentMode(Settings.Mode.WINDOWED);
          break;
        case "Fullscreen":
          settings.setCurrentMode(Settings.Mode.FULLSCREEN);
          break;
        case "Borderless":
          settings.setCurrentMode(Settings.Mode.BORDERLESS);
          break;
        default:
          settings.setCurrentMode(Settings.Mode.WINDOWED);
          break;
      }
    }
    if (vsyncCheck != null) {
      settings.setVsync(vsyncCheck.isChecked());
    }
    logger.debug("Display settings applied");
  }

  private Integer parseOrNull(String num) {
    try {
      return Integer.parseInt(num, 10);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private void showMenu() {
    rootTable.setVisible(true);
  }

  private void hideMenu() {
    rootTable.setVisible(false);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void update() {
    stage.act(ServiceLocator.getTimeSource().getDeltaTime());
  }

  @Override
  public void dispose() {
    rootTable.clear();
    super.dispose();
  }

  private static void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = Color.WHITE;
    label.setStyle(st);
    logger.debug("Labels are white");
  }
}
