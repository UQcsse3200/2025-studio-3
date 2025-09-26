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
  private Table bottomRow;
  private SelectBox<String> difficultySelect;

  /**
   * Constructor for GameSettingsMenu.
   */
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
    bottomRow.setVisible(false);
    rootTable.setVisible(false);
  }

  /**
   * Add actors to the UI.
   */
  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);
    Settings settings = ServiceLocator.getSettingsService().getSettings();

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
    difficultySelect.setItems("EASY", "NORMAL", "HARD");
    difficultySelect.setSelected(settings.getDifficulty().toString());
    whiten(difficultyLabel);

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

    bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().padBottom(20f);
    bottomRow.add(applyBtn).size(150f, 50f);
    stage.addActor(bottomRow);

    stage.addActor(rootTable);
  }

  /**
   * Apply changes to the game settings.
   */
  private void applyChanges() {
    logger.info("[GameSettingsMenu] Applying game settings");
    Settings settings = ServiceLocator.getSettingsService().getSettings();
    if (difficultySelect != null) {
      String difficulty = difficultySelect.getSelected();
      switch (difficulty) {
        case "EASY":
          settings.setDifficulty(Settings.Difficulty.EASY);
          break;
        case "NORMAL":
          settings.setDifficulty(Settings.Difficulty.NORMAL);
          break;
        case "HARD":
          settings.setDifficulty(Settings.Difficulty.HARD);
          break;
        default:
          break;
      }
    }
    ServiceLocator.getSettingsService().saveSettings();
    logger.info("[GameSettingsMenu] Game settings applied");
  }

  /**
   * Show the game settings menu.
   */
  private void showMenu() {
    rootTable.setVisible(true);
    bottomRow.setVisible(true);
  }

  /**
   * Hide the game settings menu.
   */
  private void hideMenu() {
    rootTable.setVisible(false);
    bottomRow.setVisible(false);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void dispose() {
    rootTable.clear();
    bottomRow.clear();
    super.dispose();
  }

  /**
   * Whiten the label.
   * 
   * @param label The label to whiten.
   */
  private static void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = Color.WHITE;
    label.setStyle(st);
    logger.debug("Labels are white");
  }
}
