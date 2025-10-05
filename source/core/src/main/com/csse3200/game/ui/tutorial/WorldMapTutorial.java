package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.ui.TypographyFactory;
import com.csse3200.game.ui.UIComponent;

public class WorldMapTutorial extends UIComponent {
  private Table table;
  private Label currentLabel;
  private Label moveLabel;
  private Label interactLabel;
  private Label zoomLabel;
  private boolean displayAllLabels = false;

  private float alpha = 1f;
  private boolean fadingOut = false;
  private boolean active = true;
  private int step = 0;

  @Override
  public void create() {
    super.create();

    Texture overlayTexture = new Texture(Gdx.files.internal("images/ui/dialog.png"));
    TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(overlayTexture);

    // Create a table with the image as background
    table = new Table();
    table.setBackground(backgroundDrawable);
    table.setSize(Math.floorDiv(Gdx.graphics.getWidth(), 5), Math.floorDiv(Gdx.graphics.getHeight(), 5));
    table.setPosition(20f, Gdx.graphics.getHeight() - table.getHeight() - 100f); // top-left with padding
    table.pad(20f); // inner padding for the label

    this.moveLabel = TypographyFactory.createSubtitle("Use W/A/S/D to move", Color.WHITE);
    this.interactLabel = TypographyFactory.createSubtitle("Press E to interact", Color.WHITE);
    this.zoomLabel = TypographyFactory.createSubtitle("Press Q/K to zoom", Color.WHITE);
    currentLabel = moveLabel;

    table.add(currentLabel).left();
    moveLabel.setVisible(true); // set first label to visible and the rest invisible

    stage.addActor(table);

    // Toggle button
    TextButton toggleButton = new TextButton("Tutorial", skin);
    toggleButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        boolean visible = table.isVisible();

        if (!visible) {
          displayAllLabels = true;
          fadingOut = false;
          alpha = 1f;
          table.getColor().a = 1f;
          resetTutorial();
          table.setVisible(true);
        } else {
          table.setVisible(false);
        }
      }
    });

    Table buttonTable = new Table();
    buttonTable.top().left().pad(20f);
    buttonTable.setFillParent(true);
    buttonTable.add(toggleButton).left();
    stage.addActor(buttonTable);
  }

  private void resetTutorial() {
    step = 0;
    active = true;
    alpha = 1f;
    fadingOut = false;

    // Reset label visibilities
    moveLabel.setVisible(true);
    interactLabel.setVisible(true);
    zoomLabel.setVisible(true);

    moveLabel.getStyle().fontColor.a = 1f;
    interactLabel.getStyle().fontColor.a = 1f;
    zoomLabel.getStyle().fontColor.a = 1f;

    table.clearChildren();

    if (displayAllLabels) {
      moveLabel.setVisible(true);
      interactLabel.setVisible(true);
      zoomLabel.setVisible(true);
      table.add(moveLabel).left().padBottom(20f).row();
      table.add(interactLabel).left().padBottom(20f).row();
      table.add(zoomLabel).left();
    } else {
      moveLabel.setVisible(true);
      interactLabel.setVisible(false);
      zoomLabel.setVisible(false);
      currentLabel = moveLabel;
      table.add(currentLabel).left();
    }
  }

  @Override
  public void update() {
    if (currentLabel != null) {
      currentLabel.getStyle().fontColor.a = 1f;
    }

    if (!active) return;

    if (displayAllLabels) return;

    switch (step) {
      case 0 -> {
        if (Gdx.input.isKeyPressed(Input.Keys.W) ||
                Gdx.input.isKeyPressed(Input.Keys.A) ||
                Gdx.input.isKeyPressed(Input.Keys.S) ||
                Gdx.input.isKeyPressed(Input.Keys.D)) {
          currentLabel = interactLabel;
          currentLabel.getStyle().fontColor.a = 1f;
          currentLabel.setVisible(true);
          table.clearChildren();
          table.add(interactLabel).left();
          step++;
        }
      }
      case 1 -> {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
          currentLabel = zoomLabel;
          currentLabel.getStyle().fontColor.a = 1f;
          currentLabel.setVisible(true);
          table.clearChildren();
          table.add(currentLabel).left();
          step++;
        }
      }
      case 2 -> {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) ||
                Gdx.input.isKeyJustPressed(Input.Keys.K)) {
          fadingOut = true;
        }
      }
    }

    if (fadingOut) {
      alpha -= Gdx.graphics.getDeltaTime() * 0.6f;
      if (alpha <= 0) {
        alpha = 0;
        active = false;
        table.setVisible(false);
        table.clearChildren();
      } else {
        table.getColor().a = alpha;
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
