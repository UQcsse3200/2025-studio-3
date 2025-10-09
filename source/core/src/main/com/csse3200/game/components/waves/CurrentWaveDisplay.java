package com.csse3200.game.components.waves;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
  private int currentWave = 0;

  /** Creates a new current wave display component. */
  @Override
  public void create() {
    super.create();
    entity.getEvents().addListener("pause", this::handlePause);
    entity.getEvents().addListener("resume", this::handleResume);
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
              }

              @Override
              public void onWaveStarted(int waveNumber) {
                updateWaveDisplay(waveNumber);
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
    waveLabel = ui.text("CURRENT WAVE: ");

    // Wave number label - start at 0 (no wave active)
    waveNumberLabel = ui.text(NO_WAVE_TEXT);

    // Add labels to table with some spacing
    table.add(waveLabel).padRight(10f);
    table.add(waveNumberLabel);

    stage.addActor(table);
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

  /** Handles pause events to dim the HUD */
  private void handlePause() {
    if (waveLabel != null) {
      waveLabel.getColor().a = 0.3f; // Dim to 30% opacity
    }
    if (waveNumberLabel != null) {
      waveNumberLabel.getColor().a = 0.3f; // Dim to 30% opacity
    }
  }

  /** Handles resume events to restore the HUD */
  private void handleResume() {
    if (waveLabel != null) {
      waveLabel.getColor().a = 1.0f; // Restore to 100% opacity
    }
    if (waveNumberLabel != null) {
      waveNumberLabel.getColor().a = 1.0f; // Restore to 100% opacity
    }
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
