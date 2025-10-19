package com.csse3200.game.components.gamearea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/** Displays performance stats about the game for debugging purposes. */
public class PerformanceDisplay extends UIComponent {
  private static final float Z_INDEX = 5f;
  private Label profileLabel;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    profileLabel = ui.text(getStats());
    stage.addActor(profileLabel);
  }

  @Override
  public void draw(SpriteBatch batch) {
    if (ServiceLocator.getRenderService().getDebug().getActive()) {
      profileLabel.setVisible(true);
      profileLabel.setText(getStats());

      float offsetX = 25f;
      float offsetY = 100f;
      profileLabel.setPosition(offsetX, offsetY);
    } else {
      profileLabel.setVisible(false);
    }
  }

  private String getStats() {
    String message = "Debug\n";
    message =
        message
            .concat(String.format("FPS: %d fps%n", Gdx.graphics.getFramesPerSecond()))
            .concat(String.format("RAM: %d MB%n", Gdx.app.getJavaHeap() / 1000000));
    return message;
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    super.dispose();
    profileLabel.remove();
  }
}
