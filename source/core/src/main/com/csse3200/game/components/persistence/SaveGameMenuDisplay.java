package com.csse3200.game.components.persistence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A UI component for displaying the save game menu with save slot selection and name input. */
public class SaveGameMenuDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(SaveGameMenuDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;
  private List<Savefile> saveFiles;
  private TextField nameInput;
  private TextButton saveButton;
  private Label nameLabel;
  private boolean overwrite = false;

  @Override
  public void create() {
    super.create();
    saveFiles = Persistence.fetch();
    addActors();
  }

  @Override
  public void update() {
    // Update label text based on overwrite flag
    if (overwrite) {
      nameLabel.setText("Enter Save Name (overwriting existing save):");
    } else {
      nameLabel.setText("Enter Save Name:");
    }
  }

  /** Add the actors to the table. */
  private void addActors() {
    table = new Table();
    table.setFillParent(true);

    // Back button positioned at top-left with close icon
    ImageButton backBtn = new ImageButton(
        new TextureRegionDrawable(
            ServiceLocator.getGlobalResourceService().getAsset("images/close-icon.png", Texture.class)));
    backBtn.setSize(60f, 60f);
    backBtn.setPosition(
        20f,  // 20f padding from left
        stage.getHeight() - 60f - 20f  // 20f padding from top
    );
    backBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            entity.getEvents().trigger("back");
          }
        });

    // Title
    Label titleLabel = new Label("SAVE GAME", skin, "large");

    // Name input field (initially hidden)
    nameLabel = new Label("Enter Save Name:", skin);
    nameInput = new TextField("", skin);
    nameInput.setMessageText("");

    // Create save slot buttons
    TextButton[] saveSlotButtons = new TextButton[3];
    for (int i = 0; i < 3; i++) {
      if (saveFiles.get(i) != null) {
        // Active save slot - show existing save info
        Savefile save = saveFiles.get(i);
        String buttonText = save.getDisplayName() + "\n" + save.getDisplayDate();
        saveSlotButtons[i] = new TextButton(buttonText, skin);

        final int slotIndex = i;
        saveSlotButtons[i].addListener(
            new ChangeListener() {
              @Override
              public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug("Save slot {} selected for overwrite", slotIndex);
                showNameInput();
                overwrite = true;
                entity.getEvents().trigger("selectSlot", slotIndex);
              }
            });
      } else {
        // Empty save slot
        String buttonText = "Empty";
        saveSlotButtons[i] = new TextButton(buttonText, skin);

        final int slotIndex = i;
        saveSlotButtons[i].addListener(
            new ChangeListener() {
              @Override
              public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug("Empty save slot {} selected", slotIndex);
                showNameInput();
                overwrite = false;
                entity.getEvents().trigger("selectSlot", slotIndex);
              }
            });
      }
    }

    // Save game button
    saveButton = new TextButton("Save Game", skin);
    saveButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            String saveName = nameInput.getText().trim();
            if (saveName.isEmpty()) {
              ServiceLocator.getDialogService().error("Error", "Please enter a save name");
            } else {
              logger.debug("Save game button clicked with name: {}", saveName);
              if (overwrite) {
                ServiceLocator.getDialogService()
                    .warning(
                        "Warning",
                        "Are you sure you want to overwrite an existing save? This action cannot be undone.",
                        dialog -> entity.getEvents().trigger("saveGame", saveName),
                        null);
              } else {
                entity.getEvents().trigger("saveGame", saveName);
              }
            }
          }
        });

    // Add back button directly to stage (no table needed for positioning)

    Table contentTable = new Table();
    contentTable.setFillParent(true);
    contentTable.center();
    contentTable.add(titleLabel).padBottom(30f);
    contentTable.row();
    for (int i = 0; i < saveSlotButtons.length; i++) {
      contentTable.add(saveSlotButtons[i]).width(400f).height(80f).padBottom(20f);
      contentTable.row();
    }
    contentTable.add(nameLabel).padBottom(10f).padTop(20f);
    contentTable.row();
    contentTable.add(nameInput).width(400f).height(40f).padBottom(20f);
    contentTable.row();
    contentTable.add(saveButton).width(200f).height(60f);

    stage.addActor(backBtn);
    stage.addActor(contentTable);
    hideNameInput();
  }

  /** Show the name input field and save button. */
  private void showNameInput() {
    nameLabel.setVisible(true);
    nameInput.setVisible(true);
    saveButton.setVisible(true);
  }

  /** Hide the name input field and save button. */
  private void hideNameInput() {
    nameLabel.setVisible(false);
    nameInput.setVisible(false);
    saveButton.setVisible(false);
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
