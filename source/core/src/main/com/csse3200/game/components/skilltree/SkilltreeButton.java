package com.csse3200.game.components.skilltree;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.GdxGame;
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
    table = new Table();
    table.top().right();
    table.setFillParent(true);

    TextButton backButton = new TextButton("Back", skin);

    // Triggers an event when the button is pressed.
    backButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            game.setScreen(GdxGame.ScreenType.MAIN_GAME);
          }
        });

    table.add(backButton).padTop(10f).padRight(10f);

    stage.addActor(table);
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
