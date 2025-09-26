package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.TypographyFactory;
import com.csse3200.game.ui.UIComponent;
import java.util.Arrays;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Display settings menu component. */
public class DisplaySettingsMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(DisplaySettingsMenu.class);
  private Table rootTable;
  private Table bottomRow;
  private int previousFps;
  private SelectBox<String> displayModeSelect;
  private SelectBox<String> resolutionSelect;
  private Label resolutionLabel;
  private TextField fpsText;
  private CheckBox vsyncCheck;
  private SelectBox<String> uiScaleSelect;
  private SelectBox<String> qualitySelect;

  /** Constructor for DisplaySettingsMenu. */
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
    bottomRow.setVisible(false);
    rootTable.setVisible(false);
  }

  /** Add actors to the UI. */
  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);
    rootTable.center();

    // Create title
    Label title = TypographyFactory.createTitle("Display Settings");
    rootTable.add(title).center().padTop(30f).colspan(2);
    rootTable.row().padTop(30f);

    // Get current settings
    Settings settings = ServiceLocator.getSettingsService().getSettings();

    // Create components
    Label displayModeLabel = new Label("Display Mode:", skin);
    displayModeSelect = new SelectBox<>(skin);
    String[] displayModeItems = settings.getAvailableModes().values().toArray(new String[0]);
    String[] items =
        Arrays.stream(displayModeItems).map(String::toUpperCase).toArray(String[]::new);
    displayModeSelect.setItems(items);
    displayModeSelect.setSelected(settings.getCurrentMode().toString());
    whiten(displayModeLabel);

    // Add change listener to show/hide resolution row based on display mode
    displayModeSelect.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            String selectedMode = displayModeSelect.getSelected();
            boolean isWindowed = "WINDOWED".equals(selectedMode);
            resolutionLabel.setVisible(isWindowed);
            resolutionSelect.setVisible(isWindowed);
            applyDisplayModeChange(selectedMode);
          }
        });

    resolutionLabel = new Label("Resolution:", skin);
    resolutionSelect = new SelectBox<>(skin);
    String[] resolutionStrings =
        settings.getAvailableResolutions().stream()
            .map(pair -> pair.getKey() + "x" + pair.getValue())
            .toArray(String[]::new);
    String currentResolution =
        settings.getWindowedResolution().getKey()
            + "x"
            + settings.getWindowedResolution().getValue();
    resolutionSelect.setItems(resolutionStrings);
    resolutionSelect.setSelected(currentResolution);
    whiten(resolutionLabel);

    boolean isWindowed = settings.getCurrentMode() == Settings.Mode.WINDOWED;
    resolutionLabel.setVisible(isWindowed);
    resolutionSelect.setVisible(isWindowed);

    // Add change listener to apply resolution change immediately
    resolutionSelect.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            applyResolutionChange();
          }
        });

    Label fpsLabel = new Label("Max FPS:", skin);
    fpsText = new TextField(Integer.toString(settings.getFps()), skin);
    previousFps = settings.getFps();
    whiten(fpsLabel);

    Label uiScaleLabel = new Label("UI Scale:", skin);
    uiScaleSelect = new SelectBox<>(skin);
    uiScaleSelect.setItems("SMALL", "MEDIUM", "LARGE");
    uiScaleSelect.setSelected(settings.getCurrentUIScale().toString());
    whiten(uiScaleLabel);

    Label qualityLabel = new Label("Quality:", skin);
    qualitySelect = new SelectBox<>(skin);
    qualitySelect.setItems("LOW", "HIGH");
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
          }
        });

    // Layout
    rootTable.add(displayModeLabel).left().padRight(25f);
    rootTable.add(displayModeSelect).center().width(200f);
    rootTable.row().padTop(10f);

    rootTable.add(resolutionLabel).left().padRight(25f);
    rootTable.add(resolutionSelect).center().width(200f);
    rootTable.row().padTop(10f);

    rootTable.add(fpsLabel).left().padRight(25f);
    rootTable.add(fpsText).center().width(200f);
    rootTable.row().padTop(10f);

    rootTable.add(uiScaleLabel).left().padRight(25f);
    rootTable.add(uiScaleSelect).center().width(200f);
    rootTable.row().padTop(10f);

    rootTable.add(qualityLabel).left().padRight(25f);
    rootTable.add(qualitySelect).center().width(200f);
    rootTable.row().padTop(10f);

    rootTable.add(vsyncLabel).left().padRight(25f);
    rootTable.add(vsyncCheck).center();
    rootTable.row().padTop(20f);

    // Apply button bottom center
    bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().padBottom(20f);
    bottomRow.add(applyBtn).size(150f, 50f).center();
    stage.addActor(bottomRow);

    stage.addActor(rootTable);
  }

  /** Apply display mode change immediately. */
  private void applyDisplayModeChange(String selectedMode) {
    logger.info("[DisplaySettingsMenu] Applying display mode change: {}", selectedMode);
    SettingsService settingsService = ServiceLocator.getSettingsService();
    switch (selectedMode) {
      case "WINDOWED":
        settingsService.changeDisplayMode(Settings.Mode.WINDOWED);
        break;
      case "FULLSCREEN":
        settingsService.changeDisplayMode(Settings.Mode.FULLSCREEN);
        break;
      case "BORDERLESS":
        settingsService.changeDisplayMode(Settings.Mode.BORDERLESS);
        break;
      default:
        break;
    }
    resolutionSelect.setSelected(
        settingsService.getSettings().getWindowedResolution().getKey()
            + "x"
            + settingsService.getSettings().getWindowedResolution().getValue());
    ServiceLocator.getSettingsService().saveSettings();
  }

  /** Apply resolution change immediately. */
  private void applyResolutionChange() {
    logger.info("[DisplaySettingsMenu] Applying resolution change");
    String selectedResolution = resolutionSelect.getSelected();
    if (selectedResolution != null) {
      // Parse resolution string (e.g., "1920x1080")
      String[] parts = selectedResolution.split("x");
      if (parts.length == 2) {
        try {
          int width = Integer.parseInt(parts[0]);
          int height = Integer.parseInt(parts[1]);
          ServiceLocator.getSettingsService().switchResolution(new Pair<>(width, height));
        } catch (NumberFormatException e) {
          logger.error("Invalid resolution format: {}", selectedResolution);
        }
      }
    }
    ServiceLocator.getSettingsService().saveSettings();
  }

  /** Apply the remaining display settings (FPS, VSync, etc.). */
  private void applyChanges() {
    logger.info("[DisplaySettingsMenu] Applying remaining display settings");
    Settings settings = ServiceLocator.getSettingsService().getSettings();
    Integer fpsVal = parseOrNull(fpsText.getText());

    // Validate FPS value
    if (fpsVal != null) {
      int maxRefreshRate = settings.getRefreshRate();
      if (fpsVal > maxRefreshRate) {
        ServiceLocator.getDialogService()
            .error(
                "Invalid FPS",
                "FPS cannot exceed monitor refresh rate (" + maxRefreshRate + " Hz).");
        fpsText.setText(Integer.toString(previousFps));
        return;
      }
    }

    // Apply remaining display settings
    if (fpsVal != null && vsyncCheck != null && uiScaleSelect != null && qualitySelect != null) {
      Settings.UIScale uiScale;
      try {
        uiScale = Settings.UIScale.valueOf(uiScaleSelect.getSelected());
      } catch (IllegalArgumentException e) {
        uiScale = Settings.UIScale.MEDIUM;
      }

      Settings.Quality quality;
      try {
        quality = Settings.Quality.valueOf(qualitySelect.getSelected());
      } catch (IllegalArgumentException e) {
        quality = Settings.Quality.HIGH;
      }

      ServiceLocator.getSettingsService()
          .changeDisplaySettings(fpsVal, vsyncCheck.isChecked(), uiScale, quality);
      previousFps = fpsVal;
    }

    ServiceLocator.getSettingsService().saveSettings();
    logger.info("[DisplaySettingsMenu] Remaining display settings applied");
    entity.getEvents().trigger("backtosettingsmenu");
  }

  /**
   * Parse the number or return null.
   *
   * @param num The number to parse.
   * @return The parsed number or null.
   */
  private Integer parseOrNull(String num) {
    try {
      return Integer.parseInt(num, 10);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /** Show the display settings menu. */
  private void showMenu() {
    rootTable.setVisible(true);
    bottomRow.setVisible(true);
  }

  /** Hide the display settings menu. */
  private void hideMenu() {
    rootTable.setVisible(false);
    bottomRow.setVisible(false);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void dispose() {
    rootTable.clear();
    bottomRow.clear();
    super.dispose();
  }

  /**
   * Whiten the label.
   *
   * @param label The label to whiten.
   */
  private static void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = Color.WHITE;
    label.setStyle(st);
    logger.debug("Labels are white");
  }
}
