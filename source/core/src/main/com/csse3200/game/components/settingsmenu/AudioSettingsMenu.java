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
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Audio settings menu component. */
public class AudioSettingsMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(AudioSettingsMenu.class);
  private static final String PERCENTAGE_FORMAT = "%.0f%%";
  private Table rootTable;

  // Audio Settings Components
  private Slider masterVolumeSlider;
  private Slider musicVolumeSlider;
  private Slider soundVolumeSlider;
  private Slider voiceVolumeSlider;

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
    rootTable.setVisible(false);
  }

  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);

    // Get current settings
    Settings settings = new Settings();

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
    rootTable.add(masterVolumeLabel).right().padRight(15f);
    rootTable.add(masterVolumeSlider).width(200f).left();
    rootTable.add(masterVolumeValueLabel).left().padLeft(10f);
    rootTable.row().padTop(10f);

    rootTable.add(musicVolumeLabel).right().padRight(15f);
    rootTable.add(musicVolumeSlider).width(200f).left();
    rootTable.add(musicVolumeValueLabel).left().padLeft(10f);
    rootTable.row().padTop(10f);

    rootTable.add(soundVolumeLabel).right().padRight(15f);
    rootTable.add(soundVolumeSlider).width(200f).left();
    rootTable.add(soundVolumeValueLabel).left().padLeft(10f);
    rootTable.row().padTop(10f);

    rootTable.add(voiceVolumeLabel).right().padRight(15f);
    rootTable.add(voiceVolumeSlider).width(200f).left();
    rootTable.add(voiceVolumeValueLabel).left().padLeft(10f);
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
    // Apply audio settings
    Settings settings = new Settings();
    settings.setMasterVolume(masterVolumeSlider.getValue());
    settings.setMusicVolume(musicVolumeSlider.getValue());
    settings.setSoundVolume(soundVolumeSlider.getValue());
    settings.setVoiceVolume(voiceVolumeSlider.getValue());
    logger.debug("Audio settings applied");
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
