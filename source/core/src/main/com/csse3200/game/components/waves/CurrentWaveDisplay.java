package com.csse3200.game.components.waves;

import static com.csse3200.game.entities.WaveManager.getCurrentWave;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.ui.UIComponent;

/**
 * A UI component for displaying the current wave number. Shows the current wave and can be updated
 * when waves change. Now integrated with the WaveManager system.
 */
public class CurrentWaveDisplay extends UIComponent {

  private static final String NO_WAVE_TEXT = "No Wave Active";

  /** Creates a new current wave display component. */
  public CurrentWaveDisplay() {
    // Default constructor
  }

  private Label waveLabel;
  private Label waveNumberLabel;
  private int currentWave = 0;

  @Override
  public void create() {
    super.create();
    addActors();

    // Listen for wave change events - expects an integer wave number
    entity.getEvents().addListener("waveChanged", this::updateWaveDisplay);

    // Also listen for new wave start events
    entity.getEvents().addListener("newWaveStarted", this::updateWaveDisplay);

    // Initialize with current wave from WaveManager (starts at 0, shows "No Wave Active")
    updateWaveDisplay(getCurrentWave());
  }

  /**
   * Creates actors and positions them on the stage using a table.
   *
   * @see Table for positioning options
   */
  private void addActors() {
    Table table = new Table();
    table.top().right();
    table.setFillParent(true);
    table.padTop(65f).padRight(5f);

    // Wave text label
    waveLabel = new Label("Current Wave:", skin, "small");

    // Wave number label - start at 0 (no wave active)
    waveNumberLabel = new Label(NO_WAVE_TEXT, skin, "small");

    // Add labels to table with some spacing
    table.add(waveLabel).padRight(10f);
    table.add(waveNumberLabel);

    stage.addActor(table);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // draw is handled by the stage
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
      waveNumberLabel.setText("Wave " + waveNumber);
    } else {
      waveNumberLabel.setText(NO_WAVE_TEXT);
    }
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
