package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.ui.UIComponent;

public class WorldMapTutorial extends UIComponent {
  private Table table;
  private Label label;
  private float alpha = 1f;
  private boolean fadingOut = false;
  private boolean active = true;
  private int step = 0;

  @Override
  public void create() {
    super.create();

    Texture overlayTexture = new Texture(Gdx.files.internal("images/ui/dialog.png"));
    BitmapFont font = new BitmapFont();

    // Background dark overlay
    Image darkOverlay = new Image(new TextureRegionDrawable(overlayTexture));
    darkOverlay.setFillParent(true);
    darkOverlay.setColor(0, 0, 0, 0.6f); // darken background
    stage.addActor(darkOverlay);

    LabelStyle style = new LabelStyle();
    style.font = new BitmapFont();
    style.fontColor = new Color(1, 1, 1, alpha);

    label = new Label("Use W/A/S/D to move\nPress E to interact\nPress Q/K to zoom", style);

    table = new Table();
    table.top().left().pad(20f);
    table.setFillParent(true);
    table.add(label).left();

    stage.addActor(table);

    // Toggle button
    TextButton toggleButton = new TextButton("Tutorial", skin);
    toggleButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            boolean visible = table.isVisible();
            table.setVisible(!visible);
            darkOverlay.setVisible(!visible);
          }
        });

    Table buttonTable = new Table();
    buttonTable.top().left().pad(10f);
    buttonTable.setFillParent(true);
    buttonTable.add(toggleButton).left();
    stage.addActor(buttonTable);
  }

  @Override
  public void update() {
    if (!active) return;

    switch (step) {
      case 0 -> {
        if (Gdx.input.isKeyPressed(Input.Keys.W)
            || Gdx.input.isKeyPressed(Input.Keys.A)
            || Gdx.input.isKeyPressed(Input.Keys.S)
            || Gdx.input.isKeyPressed(Input.Keys.D)) {
          step++;
        }
      }
      case 1 -> {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
          step++;
        }
      }
      case 2 -> {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyJustPressed(Input.Keys.K)) {
          fadingOut = true;
        }
      }
    }

    if (fadingOut) {
      alpha -= Gdx.graphics.getDeltaTime() * 0.6f;
      if (alpha <= 0) {
        alpha = 0;
        active = false;
        table.remove(); // Hide overlay
      } else {
        label.getStyle().fontColor.a = alpha;
      }
    }
  }

  @Override
  public void dispose() {
    table.clear();
    super.dispose();
  }

  /**
   * Draw the renderable. Should be called only by the renderer, not manually.
   *
   * @param batch Batch to render to.
   */
  @Override
  protected void draw(SpriteBatch batch) {
    // don't require manual drawing as all visuals are handled by Scene2D actors
  }
}
