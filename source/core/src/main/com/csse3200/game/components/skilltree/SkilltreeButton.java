package com.csse3200.game.components.skilltree;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Displays a button to exit the Main Game screen to the Main Menu screen. */
public class SkilltreeButton extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(SkilltreeButton.class);
  private static final float Z_INDEX = 2f;
  private Table table;
  private GdxGame game;

  public SkilltreeButton(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    // Create close button using close-icon.png
    ImageButton closeButton =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));

    // Position in top left with 20f padding
    closeButton.setSize(60f, 60f);
    closeButton.setPosition(20f, stage.getHeight() - 60f - 20f);

    // Triggers an event when the button is pressed.
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            game.setScreen(GdxGame.ScreenType.MAIN_GAME);
          }
        });

    stage.addActor(closeButton);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Do nothing, handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    table.clear();
    super.dispose();
  }
}
