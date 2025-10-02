package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.TypographyFactory;
import com.csse3200.game.ui.UIComponent;
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

    // Create title
    Label title = TypographyFactory.createTitle("Audio Settings");
    rootTable.add(title).center().padTop(30f).colspan(3);
    rootTable.row().padTop(30f);

    Settings settings = ServiceLocator.getSettingsService().getSettings();

    // Create components
    Label masterVolumeLabel = new Label("Master Volume:", skin);
    masterVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
    masterVolumeSlider.setValue(settings.getMasterVolume());
    final Label masterVolumeValueLabel =
        new Label(String.format(PERCENTAGE_FORMAT, settings.getMasterVolume() * 100), skin);
    whiten(masterVolumeLabel);
    whiten(masterVolumeValueLabel);

    Label musicVolumeLabel = new Label("Music Volume:", skin);
    musicVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
    musicVolumeSlider.setValue(settings.getMusicVolume());
    final Label musicVolumeValueLabel =
        new Label(String.format(PERCENTAGE_FORMAT, settings.getMusicVolume() * 100), skin);
    whiten(musicVolumeLabel);
    whiten(musicVolumeValueLabel);

    Label soundVolumeLabel = new Label("Sound Volume:", skin);
    soundVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
    soundVolumeSlider.setValue(settings.getSoundVolume());
    final Label soundVolumeValueLabel =
        new Label(String.format(PERCENTAGE_FORMAT, settings.getSoundVolume() * 100), skin);
    whiten(soundVolumeLabel);
    whiten(soundVolumeValueLabel);

    Label voiceVolumeLabel = new Label("Voice Volume:", skin);
    voiceVolumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
    voiceVolumeSlider.setValue(settings.getVoiceVolume());
    final Label voiceVolumeValueLabel =
        new Label(String.format(PERCENTAGE_FORMAT, settings.getVoiceVolume() * 100), skin);
    whiten(voiceVolumeLabel);
    whiten(voiceVolumeValueLabel);

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

    // Layout
    rootTable.add(masterVolumeLabel).left().padRight(15f);
    rootTable.add(masterVolumeSlider).width(200f).center();
    rootTable.add(masterVolumeValueLabel).left().padLeft(10f);
    rootTable.row().padTop(10f);

    rootTable.add(musicVolumeLabel).left().padRight(15f);
    rootTable.add(musicVolumeSlider).width(200f).center();
    rootTable.add(musicVolumeValueLabel).left().padLeft(10f);
    rootTable.row().padTop(10f);

    rootTable.add(soundVolumeLabel).left().padRight(15f);
    rootTable.add(soundVolumeSlider).width(200f).center();
    rootTable.add(soundVolumeValueLabel).left().padLeft(10f);
    rootTable.row().padTop(10f);

    rootTable.add(voiceVolumeLabel).left().padRight(15f);
    rootTable.add(voiceVolumeSlider).width(200f).center();
    rootTable.add(voiceVolumeValueLabel).left().padLeft(10f);
    rootTable.row().padTop(20f);

    // Apply button bottom center
    bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().padBottom(20f);
    bottomRow.add(applyBtn).size(150f, 50f).center();
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

    // Update the volume of currently playing music
    if (ServiceLocator.getMusicService() != null) {
      ServiceLocator.getMusicService()
          .updateVolume(masterVolumeSlider.getValue() * musicVolumeSlider.getValue());
    }

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
