package com.csse3200.game.components.waves;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WaveService;
import com.csse3200.game.ui.UIComponent;

/**
 * A UI component for displaying the current wave number. Shows the current wave and can be updated
 * when waves change. Now integrated with the WaveService system.
 */
public class CurrentWaveDisplay extends UIComponent {

  private static final String NO_WAVE_TEXT = "No Wave Active";

  /** Creates a new current wave display component. */
  public CurrentWaveDisplay() {
    // Default constructor
  }

  private Label waveLabel;
  private Label waveNumberLabel;
  private ProgressBar progressBar;
  private int currentWave = 0;

  /** Creates a new current wave display component. */
  @Override
  public void create() {
    super.create();
    addActors();

    // Listen directly to WaveService events
    ServiceLocator.getWaveService()
        .setWaveEventListener(
            new WaveService.WaveEventListener() {
              @Override
              public void onPreparationPhaseStarted(int waveNumber) {
                updateWaveDisplay(waveNumber);
              }

              @Override
              public void onWaveChanged(int waveNumber) {
                updateWaveDisplay(waveNumber);
                resetWaveProgressBar();
              }

              @Override
              public void onWaveStarted(int waveNumber) {
                updateWaveDisplay(waveNumber);
              }

              @Override
              public void onEnemyDisposed(int enemiesDisposed, int enemiesToSpawn) {
                updateWaveProgressBar(enemiesDisposed, enemiesToSpawn);
              }
            });

    // Initialize with current wave from WaveService (starts at 0, shows "No Wave Active")
    updateWaveDisplay(ServiceLocator.getWaveService().getCurrentWave());
  }

  /**
   * Creates actors and positions them on the stage using a table.
   *
   * @see Table for positioning options
   */
  private void addActors() {
    Table table = new Table();
    table.top().left();
    table.setFillParent(true);
    table.padTop(73f).padLeft(30f);

    // Wave text label
    waveLabel = ui.text("Wave: ");

    // Wave number label - start at 0 (no wave active)
    waveNumberLabel = ui.text(NO_WAVE_TEXT);

    // Creates progress bar
    progressBar = createWaveProgressBar();
    progressBar.setValue(0.09f);

    // Adds all elements to a table
    table.add(progressBar).padTop(10f).padRight(10f);
    table.add(waveLabel).padTop(10f).padRight(10f);
    table.add(waveNumberLabel).padTop(10f);

    stage.addActor(table);
  }

  /**
   * Creates a ProgressBar that displays the current progress through the current wave's enemies
   *
   * @return The created progress bar
   */
  private ProgressBar createWaveProgressBar() {
    // Initialise progress bar textures and nine patch textures
    Texture backgroundTex = new Texture("images/ui/progress_bar.png");
    Texture fillTex = new Texture("images/ui/progress_bar_fill.png");
    NinePatch backgroundNine = new NinePatch(new TextureRegion(backgroundTex), 0, 0, 8, 8);
    NinePatch fillNine = new NinePatch(new TextureRegion(fillTex), 8, 8, 8, 8);
    NinePatchDrawable backgroundDrawable = new NinePatchDrawable(backgroundNine);
    NinePatchDrawable fillDrawable = new NinePatchDrawable(fillNine);

    // Transparent drawable to avoid issues with progress bar
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(0, 0, 0, 0);
    pixmap.fill();
    Texture transparentTexture = new Texture(pixmap);
    pixmap.dispose();
    Drawable transparentKnob = new TextureRegionDrawable(new TextureRegion(transparentTexture));

    // Style for wave progress bar
    ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
    style.background = backgroundDrawable;
    style.knob = transparentKnob;
    style.knobBefore = fillDrawable;

    // Final progress bar setup and returning
    ProgressBar newProgressBar = new ProgressBar(0f, 1f, 0.001f, false, style);
    newProgressBar.setAnimateDuration(0.15f);
    return newProgressBar;
  }

  /**
   * Updates the wave display on the UI. This method is called by the event system when
   * "waveChanged" or "newWaveStarted" events are triggered.
   *
   * @param waveNumber the new wave number to display
   */
  public void updateWaveDisplay(int waveNumber) {
    currentWave = waveNumber;
    if (waveNumber > 0) {
      waveNumberLabel.setText("" + waveNumber);
    } else {
      waveNumberLabel.setText(NO_WAVE_TEXT);
    }
  }

  /**
   * Sets the progress value of the wave progress bar when an enemy is disposed
   *
   * @param enemiesDisposed the new wave number
   */
  public void updateWaveProgressBar(int enemiesDisposed, int enemiesToSpawn) {
    progressBar.setValue((float) enemiesDisposed / enemiesToSpawn);
  }

  /** Resets the wave progress bar when a new wave starts */
  public void resetWaveProgressBar() {
    progressBar.setValue(0.09f);
  }

  /**
   * Gets the current wave number being displayed.
   *
   * @return current wave number
   */
  public int getCurrentWaveNumber() {
    return currentWave;
  }

  /**
   * Gets the current wave being displayed as a formatted string. Returns "Wave X" for active waves
   * or "No Wave Active" for wave 0.
   *
   * @return formatted wave string for display
   */
  public String getCurrentWaveString() {
    if (currentWave > 0) {
      return "Wave " + currentWave;
    }
    return NO_WAVE_TEXT;
  }

  /**
   * Manually trigger a wave change for testing purposes.
   *
   * @param waveNumber the new wave number
   */
  public void setWave(int waveNumber) {
    updateWaveDisplay(waveNumber);
  }

  @Override
  public void dispose() {
    super.dispose();
    if (waveLabel != null) {
      waveLabel.remove();
    }
    if (waveNumberLabel != null) {
      waveNumberLabel.remove();
    }
  }
}
