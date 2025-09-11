package com.csse3200.game.components.persistence;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.ui.UIComponent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A UI component for displaying the new game menu with save slot selection and name input. */
public class NewGameMenuDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(NewGameMenuDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;
  private List<Savefile> saveFiles;
  private TextField nameInput;
  private TextButton startButton;
  private Label nameLabel;

  @Override
  public void create() {
    super.create();
    loadSaveFiles();
    addActors();
  }

  private void loadSaveFiles() {
    saveFiles = Persistence.fetch();
    logger.debug("Loaded {} save files", saveFiles.size());
  }

  private void addActors() {
    table = new Table();
    table.setFillParent(true);

    // Back button positioned at top-left
    TextButton backBtn = new TextButton("Back", skin);
    backBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            entity.getEvents().trigger("back");
          }
        });

    // Title
    Label titleLabel = new Label("NEW GAME", skin, "large");

    // Name input field (initially hidden)
    nameLabel = new Label("Enter Save Name:", skin);
    nameInput = new TextField("", skin);
    nameInput.setMessageText("Enter a name for your save file");

    // Add listener to enable/disable start button based on text input
    nameInput.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            updateStartButtonState();
          }
        });

    // Create save slot buttons
    TextButton[] saveSlotButtons = new TextButton[3];
    for (int i = 0; i < 3; i++) {
      if (i < saveFiles.size()) {
        // Active save slot - show existing save info
        Savefile save = saveFiles.get(i);
        String buttonText =
            "Slot " + (i + 1) + ": " + save.getDisplayName() + "\n" + save.getDisplayDate();
        saveSlotButtons[i] = new TextButton(buttonText, skin);

        final int slotIndex = i;
        saveSlotButtons[i].addListener(
            new ChangeListener() {
              @Override
              public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug("Save slot {} selected for overwrite", slotIndex);
                showNameInput();
                entity.getEvents().trigger("selectSlot", slotIndex);
              }
            });
      } else {
        // Empty save slot
        String buttonText = "Slot " + (i + 1) + ": Empty";
        saveSlotButtons[i] = new TextButton(buttonText, skin);

        final int slotIndex = i;
        saveSlotButtons[i].addListener(
            new ChangeListener() {
              @Override
              public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug("Empty save slot {} selected", slotIndex);
                showNameInput();
                entity.getEvents().trigger("selectSlot", slotIndex);
              }
            });
      }
    }

    // Start game button (initially disabled)
    startButton = new TextButton("Start Game", skin);
    startButton.setDisabled(true);
    startButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            String saveName = nameInput.getText().trim();
            if (saveName.isEmpty()) {
              saveName = "New Game";
            }
            logger.debug("Start game button clicked with name: {}", saveName);
            entity.getEvents().trigger("startGame", saveName);
          }
        });

    // Layout: Back button in separate table for positioning
    Table backTable = new Table();
    backTable.setFillParent(true);
    backTable.top().left();
    backTable.add(backBtn).pad(30f);

    // Main content table
    Table contentTable = new Table();
    contentTable.setFillParent(true);
    contentTable.center();

    contentTable.add(titleLabel).padBottom(30f);
    contentTable.row();

    // Add save slots with consistent sizing and spacing
    for (int i = 0; i < saveSlotButtons.length; i++) {
      contentTable.add(saveSlotButtons[i]).width(400f).height(80f).padBottom(20f);
      contentTable.row();
    }

    // Name input section (initially hidden)
    contentTable.add(nameLabel).padBottom(10f).padTop(20f);
    contentTable.row();
    contentTable.add(nameInput).width(400f).height(40f).padBottom(20f);
    contentTable.row();

    // Start game button (initially hidden)
    contentTable.add(startButton).width(200f).height(60f);

    // Add both tables to stage
    stage.addActor(backTable);
    stage.addActor(contentTable);

    // Initially hide name input and start button
    hideNameInput();
  }

  /** Show the name input field and start button. */
  private void showNameInput() {
    nameLabel.setVisible(true);
    nameInput.setVisible(true);
    startButton.setVisible(true);
    updateStartButtonState();
  }

  /** Hide the name input field and start button. */
  private void hideNameInput() {
    nameLabel.setVisible(false);
    nameInput.setVisible(false);
    startButton.setVisible(false);
  }

  /** Update the start button state based on text input. */
  private void updateStartButtonState() {
    boolean hasText = !nameInput.getText().trim().isEmpty();
    startButton.setDisabled(!hasText);
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
    if (table != null) {
      table.clear();
    }
    super.dispose();
  }
}
