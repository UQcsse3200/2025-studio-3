package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.ui.TypographyFactory;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.services.GameTime;
import com.badlogic.gdx.utils.Align;

public class LevelMapTutorial extends UIComponent {
  private Table dialogTable;
  private Table skipTable;
  private Image overlay;
  private Label messageLabel;
  private TextButton skipButton;
  private final GameTime gameTime;

  private int step = 0;
  private boolean active = true;
  private boolean paused = true;
  private float alpha = 0.7f;

  private final String[] tutorialMessages = {
          "Welcome to Level 1!",
          "Drag defence units from the hotbar onto the grid.",
          "Furnaces produce scrap metal which can be used to recruit more human defenders.",
          "Human defenders attack incoming robot enemies."
  };

  public LevelMapTutorial(GameTime gameTime) {
      this.gameTime = gameTime;
  }

  @Override
  public void create() {
    super.create();

    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888); // create dark overlay
    pixmap.setColor(0, 0, 0, 1); // Solid black
    pixmap.fill();
    Texture blackTex = new Texture(pixmap);
    pixmap.dispose(); // clean up after creating texture

    overlay = new Image(new TextureRegionDrawable(blackTex));

    overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    overlay.setColor(0, 0, 0, alpha);
    overlay.setVisible(true);
    stage.addActor(overlay);

    Texture dialogTex = new Texture(Gdx.files.internal("images/ui/dialog.png"));
    TextureRegionDrawable dialogDrawable = new TextureRegionDrawable(dialogTex);

    dialogTable = new Table();
    dialogTable.setBackground(dialogDrawable);
    dialogTable.setSize(Math.floorDiv(Gdx.graphics.getWidth(), 3), Math.floorDiv(Gdx.graphics.getHeight(), 5));
    dialogTable.setPosition(
            (Gdx.graphics.getWidth() - dialogTable.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - dialogTable.getHeight()) / 5f
    );

    Table contentTable = new Table();
    contentTable.setFillParent(true);

    messageLabel = TypographyFactory.createSubtitle(tutorialMessages[step], Color.WHITE);
    messageLabel.setWrap(true);
    messageLabel.setAlignment(Align.center);

    contentTable.add(messageLabel).width(dialogTable.getWidth() - 100f).expand().center().row();
    dialogTable.add(contentTable).expand().fill();
    dialogTable.align(Align.center);

    // next button
    Texture nextTexture = new Texture(Gdx.files.internal("images/ui/skip-icon.png"));
    Drawable nextDrawable = new TextureRegionDrawable(nextTexture);
    ImageButton nextButton = new ImageButton(nextDrawable);
    nextButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        nextStep();
      }
    });

    dialogTable.add(nextButton).size(30f, 30f).expandX().right().bottom().padRight(60f).padBottom(50f);
    stage.addActor(dialogTable);

    // skip button to skip tutorial
    skipButton = new TextButton("Skip Tutorial", skin);
    skipButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        skipTutorial();
      }
    });

    skipTable = new Table();
    skipTable.bottom().right().pad(20f);
    skipTable.setFillParent(true);
    skipTable.add(skipButton);
    stage.addActor(skipTable);

    pauseGame();
  }

  @Override
  public void update() {
    if (!active) return;

    switch (step) {
      case 0, 1, 2 -> {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) nextStep(); // press space bar to move onto next message
      }
      case 3 -> {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) endTutorial(); // press space bar to end tutorial
      }
    }
  }

  private void nextStep() {
    step++;
    if (step < tutorialMessages.length) {
      messageLabel.setText(tutorialMessages[step]);
    } else {
      endTutorial();
    }
  }

  private void endTutorial() {
    active = false;
    paused = false;
    overlay.setVisible(false);
    dialogTable.setVisible(false);
    skipTable.setVisible(false);
    resumeGame();
  }

  private void skipTutorial() {
    active = false;
    paused = false;
    overlay.setVisible(false);
    dialogTable.setVisible(false);
    skipTable.setVisible(false);
    resumeGame();
  }

  private void pauseGame() {
    gameTime.setTimeScale(0f);
  }

  private void resumeGame() {
    gameTime.setTimeScale(1f);
  }

  @Override
  public void dispose() {
    super.dispose();
    overlay.remove();
    dialogTable.remove();
    skipTable.remove();
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // Scene2D actors handle their own drawing
  }
}
