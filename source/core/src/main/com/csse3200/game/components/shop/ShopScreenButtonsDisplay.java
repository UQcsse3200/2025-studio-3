package com.csse3200.game.components.shop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays a button to exit the Shop screen 
 */
public class ShopScreenButtonsDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(ShopScreenButtonsDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /**
   * Create the overlay UI elements for the shop screen.
   */
  private void addActors() {
    table = new Table();
    table.top().right();
    table.setFillParent(true);

    // TODO: Add coins icon and numerical quantity of coins.

    TextButton backButton = new TextButton("Back", skin);
    backButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            entity.getEvents().trigger("back");
          }
        });

    table.add(backButton).padTop(10f).padRight(10f);

    stage.addActor(table);
  }

  @Override
  public void draw(SpriteBatch batch) {
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
