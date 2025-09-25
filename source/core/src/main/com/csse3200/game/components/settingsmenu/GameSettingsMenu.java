package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Game settings menu component. */
public class GameSettingsMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(GameSettingsMenu.class);
  private Table rootTable;

  // Game Settings Components
  private SelectBox<String> difficultySelect;

  public GameSettingsMenu() {
    super();
  }

  @Override
  public void create() {
    super.create();
    addActors();
    entity.getEvents().addListener("backtosettingsmenu", this::hideMenu);
    entity.getEvents().addListener("gamesettings", this::showMenu);
    entity.getEvents().addListener("displaysettings", this::hideMenu);
    entity.getEvents().addListener("audiosettings", this::hideMenu);
    rootTable.setVisible(false);
  }

  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);

    // Get current settings
    Settings settings = new Settings();

    // Create components
    Label pauseLabel = new Label("Pause Key:", skin);
    TextField pauseKeyText = new TextField(Input.Keys.toString(settings.getPauseButton()), skin);
    whiten(pauseLabel);

    Label skipLabel = new Label("Skip Key:", skin);
    TextField skipKeyText = new TextField(Input.Keys.toString(settings.getSkipButton()), skin);
    whiten(skipLabel);

    Label interactionLabel = new Label("Interaction Key:", skin);
    TextField interactionKeyText =
        new TextField(Input.Keys.toString(settings.getInteractionButton()), skin);
    whiten(interactionLabel);

    Label upLabel = new Label("Up Key:", skin);
    TextField upKeyText = new TextField(Input.Keys.toString(settings.getUpButton()), skin);
    whiten(upLabel);

    Label downLabel = new Label("Down Key:", skin);
    TextField downKeyText = new TextField(Input.Keys.toString(settings.getDownButton()), skin);
    whiten(downLabel);

    Label leftLabel = new Label("Left Key:", skin);
    TextField leftKeyText = new TextField(Input.Keys.toString(settings.getLeftButton()), skin);
    whiten(leftLabel);

    Label rightLabel = new Label("Right Key:", skin);
    TextField rightKeyText = new TextField(Input.Keys.toString(settings.getRightButton()), skin);
    whiten(rightLabel);

    Label difficultyLabel = new Label("Difficulty:", skin);
    difficultySelect = new SelectBox<>(skin);
    difficultySelect.setItems("Easy", "Normal", "Hard");
    difficultySelect.setSelected(settings.getDifficulty().toString());
    whiten(difficultyLabel);

    // Apply button
    TextButton applyBtn = ButtonFactory.createButton("Apply");
    applyBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Apply button clicked");
            applyChanges();
            entity.getEvents().trigger("backtosettingsmenu");
          }
        });

    // Layout
    rootTable.add(pauseLabel).right().padRight(15f);
    rootTable.add(pauseKeyText).left().width(100f);
    rootTable.row().padTop(10f);

    rootTable.add(skipLabel).right().padRight(15f);
    rootTable.add(skipKeyText).left().width(100f);
    rootTable.row().padTop(10f);

    rootTable.add(interactionLabel).right().padRight(15f);
    rootTable.add(interactionKeyText).left().width(100f);
    rootTable.row().padTop(10f);

    rootTable.add(upLabel).right().padRight(15f);
    rootTable.add(upKeyText).left().width(100f);
    rootTable.row().padTop(10f);

    rootTable.add(downLabel).right().padRight(15f);
    rootTable.add(downKeyText).left().width(100f);
    rootTable.row().padTop(10f);

    rootTable.add(leftLabel).right().padRight(15f);
    rootTable.add(leftKeyText).left().width(100f);
    rootTable.row().padTop(10f);

    rootTable.add(rightLabel).right().padRight(15f);
    rootTable.add(rightKeyText).left().width(100f);
    rootTable.row().padTop(10f);

    rootTable.add(difficultyLabel).right().padRight(15f);
    rootTable.add(difficultySelect).left().width(150f);
    rootTable.row().padTop(20f);

    // Apply button bottom center
    Table bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().center().pad(20f);
    bottomRow.add(applyBtn).size(150f, 50f);
    stage.addActor(bottomRow);

    stage.addActor(rootTable);
  }

  private void applyChanges() {
    // Apply game settings
    if (difficultySelect != null) {
      Settings settings = new Settings();
      String difficulty = difficultySelect.getSelected();
      switch (difficulty) {
        case "Easy":
          settings.setDifficulty(Settings.Difficulty.EASY);
          break;
        case "Normal":
          settings.setDifficulty(Settings.Difficulty.NORMAL);
          break;
        case "Hard":
          settings.setDifficulty(Settings.Difficulty.HARD);
          break;
        default:
          settings.setDifficulty(Settings.Difficulty.NORMAL);
          break;
      }
      // Note: Key bindings would need to be implemented with proper key input handling
    }
    logger.debug("Game settings applied");
  }

  private void showMenu() {
    rootTable.setVisible(true);
  }

  private void hideMenu() {
    rootTable.setVisible(false);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void update() {
    stage.act(ServiceLocator.getTimeSource().getDeltaTime());
  }

  @Override
  public void dispose() {
    rootTable.clear();
    super.dispose();
  }

  private static void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = Color.WHITE;
    label.setStyle(st);
    logger.debug("Labels are white");
  }
}
