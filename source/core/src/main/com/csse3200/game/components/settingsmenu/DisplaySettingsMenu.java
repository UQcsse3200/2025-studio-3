package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
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

    // Create title with proper UI scaling
    Label title = ui.title("Display Settings");
    float uiScale = ui.getUIScale();
    rootTable.add(title).center().padTop(30f * uiScale).colspan(2);
    rootTable.row().padTop(30f * uiScale);

    // Get current settings
    Settings settings = ServiceLocator.getSettingsService().getSettings();

    // Create components using UIFactory
    Label displayModeLabel = ui.text("Display Mode:");
    String[] displayModeItems = settings.getAvailableModes().values().toArray(new String[0]);
    String[] items =
        Arrays.stream(displayModeItems).map(String::toUpperCase).toArray(String[]::new);
    displayModeSelect = ui.createSelectBox(items);
    displayModeSelect.setSelected(settings.getCurrentMode().toString());

    // Add change listener to show/hide resolution row based on display mode
    displayModeSelect.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            String selectedMode = displayModeSelect.getSelected();
            boolean isWindowed = "WINDOWED".equals(selectedMode);
            resolutionLabel.setVisible(isWindowed);
            resolutionSelect.setVisible(isWindowed);
            applyDisplayModeChange(selectedMode);
          }
        });

    resolutionLabel = ui.text("Resolution:");
    String[] resolutionStrings =
        settings.getAvailableResolutions().stream()
            .map(pair -> pair.getKey() + "x" + pair.getValue())
            .toArray(String[]::new);
    String currentResolution =
        settings.getWindowedResolution().getKey()
            + "x"
            + settings.getWindowedResolution().getValue();
    resolutionSelect = ui.createSelectBox(resolutionStrings);
    resolutionSelect.setSelected(currentResolution);

    boolean isWindowed = settings.getCurrentMode() == Settings.Mode.WINDOWED;
    resolutionLabel.setVisible(isWindowed);
    resolutionSelect.setVisible(isWindowed);

    // Add change listener to apply resolution change immediately
    resolutionSelect.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            applyResolutionChange();
          }
        });

    Label fpsLabel = ui.text("Max FPS:");
    fpsText = ui.createTextField(Integer.toString(settings.getFps()));
    previousFps = settings.getFps();

    Label uiScaleLabel = ui.text("UI Scale:");
    uiScaleSelect = ui.createSelectBox(new String[] {"SMALL", "MEDIUM", "LARGE"});
    uiScaleSelect.setSelected(settings.getCurrentUIScale().toString());

    Label qualityLabel = ui.text("Quality:");
    qualitySelect = ui.createSelectBox(new String[] {"LOW", "HIGH"});
    qualitySelect.setSelected(settings.getQuality().toString());

    Label vsyncLabel = ui.text("VSync:");
    vsyncCheck = ui.createCheckBox("");
    vsyncCheck.setChecked(settings.isVsync());

    // Create apply button using UIFactory
    int buttonWidth = 150;
    TextButton applyBtn = ui.primaryButton("Apply", buttonWidth);
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(buttonWidth);
    applyBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.debug("Apply button clicked");
            applyChanges();
          }
        });

    // Layout with proper UI scaling
    rootTable.add(displayModeLabel).left().padRight(25f * uiScale);
    rootTable.add(displayModeSelect).center().width(200f * uiScale);
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(resolutionLabel).left().padRight(25f * uiScale);
    rootTable.add(resolutionSelect).center().width(200f * uiScale);
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(fpsLabel).left().padRight(25f * uiScale);
    rootTable.add(fpsText).center().width(200f * uiScale);
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(uiScaleLabel).left().padRight(25f * uiScale);
    rootTable.add(uiScaleSelect).center().width(200f * uiScale);
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(qualityLabel).left().padRight(25f * uiScale);
    rootTable.add(qualitySelect).center().width(200f * uiScale);
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(vsyncLabel).left().padRight(25f * uiScale);
    rootTable.add(vsyncCheck).center();
    rootTable.row().padTop(20f * uiScale);

    // Apply button bottom center
    bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().padBottom(20f * uiScale);
    bottomRow
        .add(applyBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .center();
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
      case "WINDOWED BORDERLESS":
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
          Pair<Integer, Integer> resolution = new Pair<>(width, height);
          ServiceLocator.getSettingsService().switchResolution(resolution);
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
}
