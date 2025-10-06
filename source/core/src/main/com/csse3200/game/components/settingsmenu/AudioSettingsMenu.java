package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Audio settings menu component. */
public class AudioSettingsMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(AudioSettingsMenu.class);
  private static final String PERCENTAGE_FORMAT = "%.0f%%";
  private Table rootTable;
  private Table bottomRow;
  private Slider masterVolumeSlider;
  private Slider musicVolumeSlider;
  private Slider soundVolumeSlider;
  private Slider voiceVolumeSlider;

  /** Constructor for AudioSettingsMenu. */
  public AudioSettingsMenu() {
    super();
  }

  @Override
  public void create() {
    super.create();
    addActors();
    entity.getEvents().addListener("backtosettingsmenu", this::hideMenu);
    entity.getEvents().addListener("gamesettings", this::hideMenu);
    entity.getEvents().addListener("displaysettings", this::hideMenu);
    entity.getEvents().addListener("audiosettings", this::showMenu);
    bottomRow.setVisible(false);
    rootTable.setVisible(false);
  }

  /** Add actors to the UI. */
  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);
    rootTable.center(); // Center the entire table content

    // Create title with proper UI scaling
    Label title = ui.title("Audio Settings");
    float uiScale = ui.getUIScale();
    rootTable.add(title).center().padTop(30f * uiScale).colspan(3);
    rootTable.row().padTop(30f * uiScale);

    Settings settings = ServiceLocator.getSettingsService().getSettings();

    // Create components using UIFactory
    Label masterVolumeLabel = ui.text("Master Volume:");
    masterVolumeSlider = ui.createSlider(0f, 1f, 0.01f, false);
    masterVolumeSlider.setValue(settings.getMasterVolume());
    final Label masterVolumeValueLabel =
        ui.text(String.format(PERCENTAGE_FORMAT, settings.getMasterVolume() * 100));

    Label musicVolumeLabel = ui.text("Music Volume:");
    musicVolumeSlider = ui.createSlider(0f, 1f, 0.01f, false);
    musicVolumeSlider.setValue(settings.getMusicVolume());
    final Label musicVolumeValueLabel =
        ui.text(String.format(PERCENTAGE_FORMAT, settings.getMusicVolume() * 100));

    Label soundVolumeLabel = ui.text("Sound Volume:");
    soundVolumeSlider = ui.createSlider(0f, 1f, 0.01f, false);
    soundVolumeSlider.setValue(settings.getSoundVolume());
    final Label soundVolumeValueLabel =
        ui.text(String.format(PERCENTAGE_FORMAT, settings.getSoundVolume() * 100));

    Label voiceVolumeLabel = ui.text("Voice Volume:");
    voiceVolumeSlider = ui.createSlider(0f, 1f, 0.01f, false);
    voiceVolumeSlider.setValue(settings.getVoiceVolume());
    final Label voiceVolumeValueLabel =
        ui.text(String.format(PERCENTAGE_FORMAT, settings.getVoiceVolume() * 100));

    // Create apply button using UIFactory
    int buttonWidth = 150;
    TextButton applyBtn = ui.primaryButton("Apply", buttonWidth);
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(buttonWidth);
    applyBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Apply button clicked");
            applyChanges();
            entity.getEvents().trigger("backtosettingsmenu");
          }
        });

    // Slider listeners
    masterVolumeSlider.addListener(
        (Event event) -> {
          float value = masterVolumeSlider.getValue();
          masterVolumeValueLabel.setText(String.format(PERCENTAGE_FORMAT, value * 100));
          return true;
        });

    musicVolumeSlider.addListener(
        (Event event) -> {
          float value = musicVolumeSlider.getValue();
          musicVolumeValueLabel.setText(String.format(PERCENTAGE_FORMAT, value * 100));
          return true;
        });

    soundVolumeSlider.addListener(
        (Event event) -> {
          float value = soundVolumeSlider.getValue();
          soundVolumeValueLabel.setText(String.format(PERCENTAGE_FORMAT, value * 100));
          return true;
        });

    voiceVolumeSlider.addListener(
        (Event event) -> {
          float value = voiceVolumeSlider.getValue();
          voiceVolumeValueLabel.setText(String.format(PERCENTAGE_FORMAT, value * 100));
          return true;
        });

    // Layout with proper UI scaling
    rootTable.add(masterVolumeLabel).left().padRight(15f * uiScale);
    rootTable.add(masterVolumeSlider).width(200f * uiScale).center();
    rootTable.add(masterVolumeValueLabel).width(50f * uiScale).left().padLeft(10f * uiScale);
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(musicVolumeLabel).left().padRight(15f * uiScale);
    rootTable.add(musicVolumeSlider).width(200f * uiScale).center();
    rootTable.add(musicVolumeValueLabel).width(50f * uiScale).left().padLeft(10f * uiScale);
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(soundVolumeLabel).left().padRight(15f * uiScale);
    rootTable.add(soundVolumeSlider).width(200f * uiScale).center();
    rootTable.add(soundVolumeValueLabel).width(50f * uiScale).left().padLeft(10f * uiScale);
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(voiceVolumeLabel).left().padRight(15f * uiScale);
    rootTable.add(voiceVolumeSlider).width(200f * uiScale).center();
    rootTable.add(voiceVolumeValueLabel).width(50f * uiScale).left().padLeft(10f * uiScale);
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

  /** Apply changes to the audio settings. */
  private void applyChanges() {
    logger.info("[AudioSettingsMenu] Applying audio settings");
    Settings settings = ServiceLocator.getSettingsService().getSettings();
    settings.setMasterVolume(masterVolumeSlider.getValue());
    settings.setMusicVolume(musicVolumeSlider.getValue());
    settings.setSoundVolume(soundVolumeSlider.getValue());
    settings.setVoiceVolume(voiceVolumeSlider.getValue());
    ServiceLocator.getSettingsService().saveSettings();
    logger.info("[AudioSettingsMenu] Audio settings applied");
  }

  /** Show the audio settings menu. */
  private void showMenu() {
    rootTable.setVisible(true);
    bottomRow.setVisible(true);
  }

  /** Hide the audio settings menu. */
  private void hideMenu() {
    rootTable.setVisible(false);
    bottomRow.setVisible(false);
  }

  @Override
  public void dispose() {
    rootTable.clear();
    bottomRow.clear();
    super.dispose();
  }
}
