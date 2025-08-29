package com.csse3200.game.components.waves;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/**
 * A UI component for displaying the current wave number.
 * Shows the current wave and can be updated when waves change.
 */
public class CurrentWaveDisplay extends UIComponent {
	private Table table;
	private Label waveLabel;
	private Label waveNumberLabel;
	private String currentWave = "Wave 1";

	@Override
	public void create() {
		super.create();
		addActors();

		// Listen for wave change events - expects an integer wave number
		entity.getEvents().addListener("waveChanged", this::updateWaveDisplay);
	}

	/**
	 * Creates actors and positions them on the stage using a table.
	 * @see Table for positioning options
	 */
	private void addActors() {
		table = new Table();
		table.top().right();
		table.setFillParent(true);
		table.padTop(45f).padRight(5f);

		// Wave text label
		waveLabel = new Label("Current Wave:", skin, "large");
		
		// Wave number label
		waveNumberLabel = new Label(currentWave, skin, "large");

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
	 * Updates the wave display on the UI.
	 * This method is called by the event system when "waveChanged" event is triggered.
	 * @param waveNumber the new wave number to display
	 */
	public void updateWaveDisplay(int waveNumber) {
		currentWave = "Wave " + waveNumber;
		waveNumberLabel.setText(currentWave);
	}

	/**
	 * Gets the current wave being displayed.
	 * @return current wave string
	 */
	public String getCurrentWave() {
		return currentWave;
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
